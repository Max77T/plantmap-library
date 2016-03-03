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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import play.Logger;

class PathLink{
	static final String metadataDirectoryName = "metadata";
	static final String metadataFileExtension = ".json";
	
	private final Path map;
	private final Path metadata;

	private PathLink(Path mapPath, Path metadataPath) {
		this.map = mapPath;
		this.metadata = metadataPath;
	}
	
	public static PathLink create(Path mapPath){
		Objects.requireNonNull(mapPath);
		Path metadataPath = mapPath.getParent().resolve(metadataDirectoryName)
				.resolve(mapPath.getFileName() + metadataFileExtension);
		return new PathLink(mapPath, metadataPath);
	}
	
	/**
	 * Move all maps and relative metadata to 'dstPath'
	 * Return a new list with the PathLinks to the moved files.
	 */
	public static List<PathLink> moveFiles(List<PathLink> pathLinks, Path dstPath) throws IOException {
		Logger.debug("Move " + pathLinks + " into " + dstPath);
		Objects.requireNonNull(pathLinks);
		Objects.requireNonNull(dstPath);
		
		ArrayList<PathLink> updatedLinks = new ArrayList<>();
		
		for(PathLink pathLink: pathLinks){
			Objects.requireNonNull(pathLink);
			
			Path mapDstPath = dstPath.resolve(pathLink.getMap().getFileName());
			PathLink dstPathLink = PathLink.create(mapDstPath);
			
			dstPathLink.getMap().getParent().toFile().mkdirs();
			dstPathLink.getMetadata().getParent().toFile().mkdirs();

			Files.move(pathLink.getMap(), dstPathLink.getMap());
			Files.move(pathLink.getMetadata(), dstPathLink.getMetadata());
			
			updatedLinks.add(dstPathLink);
		}
		return updatedLinks;
	}

	public Path getMap() {
		return map;
	}

	public Path getMetadata() {
		return metadata;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + map.hashCode();
		result = prime * result + metadata.hashCode();
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj){
			return true;
		}
		if (obj == null || getClass() != obj.getClass()){
			return false;
		}
		
		PathLink other = (PathLink) obj;
		return this.map.equals(other.map) && 
				this.metadata.equals(other.metadata);
	}

	@Override
	public String toString() {
		return "PathLink [map=" + map + ", metadata=" + metadata + "]";
	}
}