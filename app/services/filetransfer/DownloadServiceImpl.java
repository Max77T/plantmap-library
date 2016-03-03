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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import com.google.inject.Inject;

import conf.UploadConfig;
import models.DownloadRequest;
import models.maps.MapRepository;
import play.Logger;

/**
 * This class allow the user to download the zip file for a list of maps.
 */
public class DownloadServiceImpl implements DownloadService {
	private final UploadConfig config;
	private final MapRepository mapRepository;
	
	@Inject
	public DownloadServiceImpl(UploadConfig config, MapRepository mapRepository){
		this.config = config;
		this.mapRepository = mapRepository;
	}
	
	/**
	 * Get the Jsonlist with id images and zip all the file images and metadata
	 */
	@Override
	public Optional<String> createDownloadZip(DownloadRequest dr) throws IOException {
		Objects.requireNonNull(dr);	
		
		if(dr.idList.length <= 0){
			Logger.warn("Empty list");
			return Optional.empty();
		}
		
		// Get a directory in tmp folder
		Path tmpDirPath = config.getTmpDirectory().resolve(UUID.randomUUID().toString());
		tmpDirPath.toFile().mkdir();
		
		// Create a List of PathLink of all ID in dr.idList
		List<PathLink> pathLinks = Arrays.stream(dr.idList)
			  .map(mapRepository::getMap)
			  .filter(Optional::isPresent) // Ignore non existing documents in ES
			  .map(Optional::get)
			  .map(map -> map.getPath())
			  .map(config.getLibraryStoreDir()::resolve) // Get absolute Path of the MAP
			  .map(PathLink::create)
			  .collect(Collectors.toList());

		if(pathLinks.isEmpty()) {
			return Optional.empty();
		}
		String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyymmddhhmmss"));
		Path dst = tmpDirPath.resolve("export-" + date + ".zip");
		Logger.debug("Path extract zip : " + dst.toString());
		
		// Create zip
		ZipUtils zipUtils = new ZipUtils();
		zipUtils.zipFolder(pathLinks, dst);
		
		String mapUrl = config.getTmpDirectory().relativize(dst).toString();
		return Optional.of(mapUrl);
	}

	@Override
	public Optional<InputStream> getZip(String file) {
		Path zipPath = config.getTmpDirectory().resolve(file);
		try{
			return Optional.of(Files.newInputStream(zipPath));
		} catch (IOException ex){
			Logger.warn("Error while loading file " + file + ": " + ex.getMessage());
			return Optional.empty();
		}
	}
}
