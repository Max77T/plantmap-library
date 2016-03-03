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
package models.maps;

import java.nio.file.Paths;


/** 
 * Summary of map, used to display in search results
 */
public class MapSummary {
	private final String id;
	private final Taxon taxon;
	private final String pictureName;
	private final String projectName;
	private final String projectDescription;
	private final String organizationCbn;
	private final String path;
	private final boolean isPrivate;

	public MapSummary(String id, 
			Taxon taxon, String path, 
			String projectName, boolean isPrivate,
			String organizationCbn, String projectDescription) {
		this.id = id;
		this.taxon = taxon;
		this.path = path;
		this.projectName = projectName;
		this.pictureName = Paths.get(path).getFileName().toString();
		this.isPrivate = isPrivate;
		this.organizationCbn = organizationCbn;
		this.projectDescription = projectDescription;
	}

	public static MapSummary fromMap(Map map){
		return new MapSummary(
				map.getId(),
				map.getTaxon(),
				map.getPath(),
				map.getProjectName(),
				map.getIsPrivate(),
				map.getOrganizationCbn(),
				map.getProjectDescription()
				);
	}

	public String getId() {
		return id;
	}

	public Taxon getTaxon() {
		return taxon;
	}
	
	public String getPictureName() {
		return pictureName;
	}
	
	public String getProjectName() {
		return projectName;
	}
	
	public String getUrl() {
		return path;
	}
	
	public boolean getIsPrivate() {
		return isPrivate;
	}

	public String getProjectDescription() {
		return projectDescription;
	}
	
	public String getOrganizationCbn() {
		return organizationCbn;
	}
}


