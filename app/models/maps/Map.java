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

public class Map {
	private final String id;
	private final String path;
	private final String generationDate;
	private final int dpi;
	private final String extension;
	private final String dimension;
	private final int weight;
	private final List<String> bBox;
	private final String projection;
	private final Taxon taxon;
	private final String organizationCbn;  
	private final String nameAuthor;
	private final String email;
	private final String projectDescription;
	private final String projectName;
	private final String projectModificationDate;
	private final String rangeObservationStart;
	private final String rangeObservationEnd;
	private final String versionNumber;
	private final String genealogyData;
	private final String thesaurusISO;
	private final String thesaurusINSPIRE;
	private final String thesaurusCBN;
	private final List<String> keywords;
	private final String dataState;
	private final String usageLimit;
	private final String updateFrequency;
	private final String dataOwner;
	private final String contactName;
	private final String contactInspire;
	private final boolean isPrivate;
	private final String cbnManager;

	public Map(MapDto map, String id) {
		this.id = id;
		this.path = map.path;
		this.generationDate = map.generationDate;
		this.dpi = map.dpi;
		this.extension = map.extension;
		this.dimension = map.dimension;
		this.weight = map.weight;
		this.bBox = map.bBox;
		this.projection = map.projection;
		this.taxon = map.taxon;
		this.organizationCbn = map.organizationCbn;  
		this.nameAuthor = map.nameAuthor;
		this.email = map.email;
		this.projectDescription = map.projectDescription;
		this.projectName = map.projectName;
		this.projectModificationDate = map.projectModificationDate;
		this.rangeObservationStart = map.rangeObservationStart;
		this.rangeObservationEnd = map.rangeObservationEnd;
		this.versionNumber = map.versionNumber;
		this.genealogyData = map.genealogyData;
		this.thesaurusISO = map.thesaurusISO;
		this.thesaurusINSPIRE = map.thesaurusINSPIRE;
		this.thesaurusCBN = map.thesaurusCBN;
		this.keywords = map.keywords;
		this.dataState = map.dataState;
		this.usageLimit = map.usageLimit;
		this.updateFrequency = map.updateFrequency;
		this.dataOwner = map.dataOwner;
		this.contactName = map.contactName;
		this.contactInspire = map.contactInspire;
		this.isPrivate = map.isPrivate;
		this.cbnManager = map.cbnManager;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj){
			return true;
		}
		if (obj == null || getClass() != obj.getClass()){
			return false;
		}

		Map map = (Map) obj;
		return this.id != null && this.id.equals(map.id);
	}
	
	@Override
	public int hashCode() {
		return id.hashCode();
	}
	
	public String getId() {
		return id;
	}

	public String getPath() {
		return path;
	}

	public String getGenerationDate() {
		return generationDate;
	}

	public int getDpi() {
		return dpi;
	}

	public String getExtension() {
		return extension;
	}

	public String getDimension() {
		return dimension;
	}

	public int getWeight() {
		return weight;
	}

	public String getProjection() {
		return projection;
	}

	public Taxon getTaxon() {
		return taxon;
	}

	public String getOrganizationCbn() {
		return organizationCbn;
	}

	public String getNameAuthor() {
		return nameAuthor;
	}

	public String getEmail() {
		return email;
	}

	public String getProjectDescription() {
		return projectDescription;
	}

	public String getProjectName() {
		return projectName;
	}

	public String getProjectModificationDate() {
		return projectModificationDate;
	}

	public String getRangeObservationStart() {
		return rangeObservationStart;
	}

	public String getRangeObservationEnd() {
		return rangeObservationEnd;
	}

	public String getVersionNumber() {
		return versionNumber;
	}

	public String getGenealogyData() {
		return genealogyData;
	}

	public String getThesaurusISO() {
		return thesaurusISO;
	}

	public String getThesaurusINSPIRE() {
		return thesaurusINSPIRE;
	}

	public String getThesaurusCBN() {
		return thesaurusCBN;
	}

	public List<String> getKeywords() {
		return keywords;
	}

	public String getDataState() {
		return dataState;
	}

	public String getUsageLimit() {
		return usageLimit;
	}
	
	public String getDataOwner() {
		return dataOwner;
	}

	public String getContactName() {
		return contactName;
	}

	public String getContactInspire() {
		return contactInspire;
	}

	public List<String> getbBox() {
		return bBox;
	}

	public String getUpdateFrequency() {
		return updateFrequency;
	}
	
	public boolean getIsPrivate() {
		return isPrivate;
	}

	public String getCbnManager() {
		return cbnManager;
	}
}
