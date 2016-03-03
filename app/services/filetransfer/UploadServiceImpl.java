/*
 	This file is part of Plantmap-Library.

	Plantmap-Library is free software: you can redistribute it and/or modify
	it under the terms of the GNU General Public License as published by
	the Free Software Foundation, either version 3 of the License, or
	(at your option) any later version.
	
	Plantmap-Library is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.
	
	You should have received a copy of the GNU General Public License
	along with Plantmap-Library.  If not, see <http://www.gnu.org/licenses/>.
*/
package services.filetransfer;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Inject;

import conf.UploadConfig;
import models.maps.MapDto;
import models.maps.MapRepository;
import models.upload.UploadHistoryRepository;
import models.upload.UploadResult;
import play.Logger;
import play.libs.Json;
import play.mvc.Http.MultipartFormData.FilePart;

public class UploadServiceImpl implements UploadService {
	
	private final UploadConfig config;
	private final MapRepository mapRepository;
	private final UploadHistoryRepository uploadRepository;

	@Inject
	public UploadServiceImpl(UploadConfig config, 
			MapRepository mapRepository, 
			UploadHistoryRepository uploadRepository){
		this.config = config;
		this.mapRepository = mapRepository;
		this.uploadRepository = uploadRepository;
	}

	/**
	 * Execute all the process that has to be applied to incoming
	 * zip file from plantmap plugin
	 */
	@Override
	public UploadResult processZipFile(FilePart zip, String login, boolean isPrivate) throws IOException {
		Objects.requireNonNull(zip);
		
		String filename = zip.getFilename();
		ZipValidator zipValidator = new ZipValidator(config);
		Logger.info("Uploading " + zip.getFilename() + " from user " + login);
		if(!zipValidator.isValidZipName(filename)){
			Logger.warn("invalid filename " + zip.getFilename());
			return new UploadResult(0);
		}
		
		Long zipSize = zip.getFile().length();

		// Move file in tmp directory
		Path zipFile = zip.getFile().toPath();
		Path tmpDirPath = config.getTmpDirectory().resolve(UUID.randomUUID().toString());
		tmpDirPath.toFile().mkdirs();

		Path tmpZipFilePath = tmpDirPath.resolve(filename);
		Files.move(zipFile, tmpZipFilePath);

		// Extract zip in tmp directory
		ZipUtils.extractZip(tmpZipFilePath, tmpDirPath);

		// Get useful content
		List<PathLink> maps = zipValidator.getValidZipContent(tmpDirPath);
		if(maps.isEmpty()){
			Logger.warn("No map found with corresponding metadata in " + filename);
			return new UploadResult(0);
		}

		// Move maps in store
		List<PathLink> updatedMaps = 
				PathLink.moveFiles(maps, getLibraryPathForZip(filename));

		// Index metadata in ElasticSearch
		indexMaps(updatedMaps, isPrivate);

		FileUtils.deleteDirectory(tmpDirPath.toFile());
		
		// Add history entry in BDD
		uploadRepository.addUploadHistory(login, filename, zipSize, new Date());
		return new UploadResult(updatedMaps.size());
	}
	
	private Path getLibraryPathForZip(String fileName){
		String storeFolderName = fileName.substring(0, fileName.length() - 4);
		return config.getLibraryStoreDir().resolve(storeFolderName);
	}

	private void indexMaps(List<PathLink> pathLinks, boolean isPrivate) throws IOException {
		List<MapDto> maps = pathLinks.stream()
				.map(pathLink -> {
					try{
						return getMapDto(pathLink, isPrivate);
					} catch(IOException ex){
						Logger.warn(ex.getMessage());
						return null;
					}
				})
				.filter(m -> m != null)
				.collect(Collectors.toList());
		
		Logger.debug("indexing maps: " + maps);
		mapRepository.putMaps(maps);
	}

	/**
	 * Create and populate a MapDto based on the metadata in PathLink.getMetadata().
	 */
	private MapDto getMapDto(PathLink pathLink, boolean isPrivate) throws IOException {
		try(InputStream in = Files.newInputStream(pathLink.getMetadata())){
			JsonNode json = Json.parse(in);
			MapDto map = Json.fromJson(json, MapDto.class);
			Logger.info(map.generationDate.toString());

			map.path = config.getLibraryStoreDir().relativize(pathLink.getMap()).toString();
			map.isPrivate = isPrivate;
			
			return map;
		}
	}
}
