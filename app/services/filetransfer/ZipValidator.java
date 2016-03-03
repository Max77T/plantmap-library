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
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import conf.UploadConfig;
import play.Logger;

class ZipValidator {
	private static final String filenameAcceptancePattern = "[a-zA-Z0-9_\\.\\-]{5,200}";
	private final UploadConfig config;
	
	public ZipValidator(UploadConfig config){
		Objects.requireNonNull(config);
		this.config = config;
	}
	
	/**
	 * Return the list of all files (PathLink) which respects the PlantMap content format.
	 * @param dirPath The directory to validate
	 * @return The list of PathLinks corresponding to valid maps in the dir at dirPath
	 * @throws IOException
	 */
	public List<PathLink> getValidZipContent(Path dirPath) throws IOException{
		Objects.requireNonNull(dirPath);

		return Files.walk(dirPath, 1)
				.filter(path -> path.toFile().isFile()) //ignore directories
				.filter(this::isValidMap)
				.map(PathLink::create)
				.filter(pathLink -> pathLink.getMetadata().toFile().exists()) // Check that map has corresponding metadata
				.filter(pathLink -> isValidMetadata(pathLink.getMetadata()))
				.collect(Collectors.toList());
	}
	
	public boolean isValidZipName(String zipName){
		return Pattern.matches(filenameAcceptancePattern, zipName.toString());
	}

	/**
	 * @return true if the image at imgPath is ok to be stored
	 * 			false otherwise
	 */
	private boolean isValidMap(Path mapPath){
		// Check that extension is allowed
		String imgString = mapPath.toString().toLowerCase();
		boolean result = config.getAllowedImgExtensions().stream().anyMatch(imgString::endsWith);

		// Check filename
		result &= Pattern.matches(filenameAcceptancePattern, mapPath.getFileName().toString());

		if(!result){
			Logger.info("Ignoring file " + mapPath);
		}
		
		return result;
	}

	/**
	 * @return true if the file at metadataPath is a correct metadata file
	 * 			false otherwise
	 */
	private boolean isValidMetadata(Path path){
		// TODO: Check that the metadata is ok
		return true;
	}
}
