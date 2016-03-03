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

/**
 * Store search form values.
 */
public class SearchRequest {
	private String searchTerms;
	private String keywords;
	private String projectName;
	private String rangeGenerationDateStart;
	private String rangeGenerationDateEnd;
	private String contactName;
	private String organizationCbn;
	private String taxon;
	private String visibility;
	
	private int pageNumber;
	private int pageSize;
	
	public String getSearchTerms() {
		return searchTerms;
	}
	
	public void setSearchTerms(String searchTerms) {
		this.searchTerms = searchTerms;
	}
	
	public String getKeywords() {
		return keywords;
	}
	
	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}
	
	public String getProjectName() {
		return projectName;
	}
	
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
	
	public String getRangeGenerationDateStart() {
		return rangeGenerationDateStart;
	}
	
	public void setRangeGenerationDateStart(String rangeGenerationDateStart) {
		this.rangeGenerationDateStart = rangeGenerationDateStart;
	}
	
	public String getRangeGenerationDateEnd() {
		return rangeGenerationDateEnd;
	}
	
	public void setRangeGenerationDateEnd(String rangeGenerationDateEnd) {
		this.rangeGenerationDateEnd = rangeGenerationDateEnd;
	}
	
	public String getContactName() {
		return contactName;
	}
	
	public void setContactName(String contactName) {
		this.contactName = contactName;
	}
	
	public String getOrganizationCbn() {
		return organizationCbn;
	}
	
	public void setOrganizationCbn(String organizationCbn) {
		this.organizationCbn = organizationCbn;
	}
	
	public String getTaxon() {
		return taxon;
	}
	
	public void setTaxon(String taxon) {
		this.taxon = taxon;
	}

	public String getVisibility() {
		return visibility;
	}
	
	public void setVisibility(String visibility) {
		this.visibility = visibility;
	}
	
	public int getPageNumber() {
		return pageNumber;
	}
	
	public void setPageNumber(int pageNumber) {
		this.pageNumber = pageNumber;
	}
	
	public int getPageSize() {
		return pageSize;
	}
	
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	@Override
	public String toString() {
		return "SearchRequest [searchTerms=" + searchTerms + ", keywords=" + keywords + ", projectName=" + projectName
				+ ", rangeGenerationDateStart=" + rangeGenerationDateStart + ", rangeGenerationDateEnd=" + rangeGenerationDateEnd
				+ ", contact=" + contactName + ", organizationCbn=" + organizationCbn
				+ ", taxon=" + taxon + ", pageNumber=" + pageNumber + ", pageSize=" + pageSize + "]";
	}
}
