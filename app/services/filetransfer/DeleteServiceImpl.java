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
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import com.google.inject.Inject;

import conf.UploadConfig;
import models.maps.MapRepository;
import models.DeleteRequest;
import models.maps.Map;
import play.Logger;

public class DeleteServiceImpl implements DeleteService {
	private final UploadConfig config;
	private final MapRepository mapRepository;

	@Inject
	public DeleteServiceImpl(UploadConfig config, MapRepository mapRepository){
		this.config = config;
		this.mapRepository = mapRepository;
	}

	/**
	 * Delete all the Map in ES and in file
	 */
	@Override
	public boolean deleteMaps(DeleteRequest dr) throws IOException {
		Objects.requireNonNull(dr);	
		if(dr.idList.length <= 0){
			Logger.warn("Empty list");
			return false;
		}

		//Collect the list of ElasticSearch ID for delete it
		List<Map> mapToDelete = Arrays.stream(dr.idList)
				.map(mapRepository::getMap)
				.filter(Optional::isPresent) // Ignore non existing documents in ES
				.map(Optional::get)
				.collect(Collectors.toList());

		//Collect the list of pathLinks for delete the file
		List<PathLink> pathLinks = mapToDelete.stream()
				.map(map -> map.getPath())
				.map(config.getLibraryStoreDir()::resolve) // Get absolute Path of the MAP
				.map(PathLink::create)
				.collect(Collectors.toList());

		//Delete in ES
		mapToDelete.stream().forEach(map -> mapRepository.delete(map.getId()));

		//Delete the Map and Metadata
		for (PathLink path : pathLinks) {
			deleteFile(path.getMap());
			deleteFile(path.getMetadata());
			
			deleteDirectoryIfEmpty(path.getMetadata().getParent());
			deleteDirectoryIfEmpty(path.getMap().getParent());
		}
		return true;
	}

	private void deleteFile(Path path) throws IOException{
		try{
			Files.delete(path);
		} catch (NoSuchFileException x) {
			Logger.info("No such file to delete: " + path);
		}
	}
	
	private void deleteDirectoryIfEmpty(Path path) throws IOException{
		if(path.toFile().exists() && isDirectoryEmpty(path)){
			Files.delete(path);
		}
	}

	private boolean isDirectoryEmpty(Path directory) throws IOException {
		try(DirectoryStream<Path> dirStream = Files.newDirectoryStream(directory)) {
			return !dirStream.iterator().hasNext();
		}
	}
}
