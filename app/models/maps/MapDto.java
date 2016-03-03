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

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Used only for communication (serialization) with ElasticSearch through Jackson
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class MapDto {
	public String path;
	public String generationDate;
	public int dpi;
	public String extension;
	public String dimension;
	public int weight;
	public List<String> bBox;
	public String projection;
	public Taxon taxon;
	public String organizationCbn;  
	public String nameAuthor;
	public String email;
	public String projectDescription;
	public String projectName;
	public String projectModificationDate;
	public String rangeObservationStart;
	public String rangeObservationEnd;
	public String versionNumber;
	public String genealogyData;
	public String thesaurusISO;
	public String thesaurusINSPIRE;
	public String thesaurusCBN;
	public List<String> keywords;
	public String dataState;
	public String usageLimit;
	public String updateFrequency;
	public String dataOwner;
	public String contactName;
	public String contactInspire;
	public boolean isPrivate;
	public String cbnManager;
}
