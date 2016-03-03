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
package viewmodels;

import java.util.List;
import java.util.LinkedHashMap;

import play.i18n.Messages;
import models.maps.Map;

public class PermalinkVM {
	private final LinkedHashMap<String, LinkedHashMap<String, String>> metadata;
	private final String title;
	private final String path;
	private final String id;
	private final boolean isPrivate;	
	
	public PermalinkVM(Map map) {
		this.metadata = build(map);
		title = map.getProjectName() + " - " + map.getTaxon().getCdref();
		this.path = map.getPath();
		this.id = map.getId();
		this.isPrivate = map.getIsPrivate();
	}
	
	public LinkedHashMap<String, LinkedHashMap<String, String>> getMetadata() {
		return metadata;
	}

	public String getTitle() {
		return title;
	}

	public String getPath() {
		return path;
	}
	
	public String getId() {
		return id;
	}
	
	public boolean getIsPrivate() {
		return isPrivate;
	}

	private LinkedHashMap<String, LinkedHashMap<String, String>> build(Map map) {
		LinkedHashMap<String, String> taxon = new LinkedHashMap<String, String>();
		taxon.put(Messages.get("dtos.permalinkVM.metaData.taxonName"), map.getTaxon().getName());
		taxon.put(Messages.get("dtos.permalinkVM.metaData.taxonCdref"), map.getTaxon().getCdref());

		LinkedHashMap<String, String> project = new LinkedHashMap<String, String>();
		project.put(Messages.get("dtos.permalinkVM.metaData.projectName"), map.getProjectName());
		project.put(Messages.get("dtos.permalinkVM.metaData.projectDescription"), map.getProjectDescription());
		project.put(Messages.get("dtos.permalinkVM.metaData.projectVersion"), map.getVersionNumber());
		project.put(Messages.get("dtos.permalinkVM.metaData.projectModificationDate"), map.getProjectModificationDate());
		project.put(Messages.get("dtos.permalinkVM.metaData.rangeObservation"), map.getRangeObservationStart() + " - " + map.getRangeObservationEnd());

		LinkedHashMap<String, String> creator = new LinkedHashMap<String, String>();
		creator.put(Messages.get("dtos.permalinkVM.metaData.organizationCbn"), map.getOrganizationCbn());
		creator.put(Messages.get("dtos.permalinkVM.metaData.contactName"), map.getContactName());
		creator.put(Messages.get("dtos.permalinkVM.metaData.email"), map.getEmail());

		LinkedHashMap<String, String> geographicalArea = new LinkedHashMap<String, String>();
		geographicalArea.put(Messages.get("dtos.permalinkVM.metaData.projection"), map.getProjection());
		geographicalArea.put(Messages.get("dtos.permalinkVM.metaData.bBox"), commaReducer(map.getbBox()));
		
		LinkedHashMap<String, String> image = new LinkedHashMap<String, String>();
		image.put(Messages.get("dtos.permalinkVM.metaData.pictureExtension"), map.getExtension());
		image.put(Messages.get("dtos.permalinkVM.metaData.pictureDimension"), map.getDimension());
		image.put(Messages.get("dtos.permalinkVM.metaData.pictureGenerationDate"), map.getGenerationDate());
		image.put(Messages.get("dtos.permalinkVM.metaData.pictureWeight"), Integer.toString(map.getWeight()) + "Ko");
		image.put(Messages.get("dtos.permalinkVM.metaData.pictureDpi"), Integer.toString(map.getDpi()));
		
		LinkedHashMap<String, String> inspireField = new LinkedHashMap<String, String>();
		inspireField.put(Messages.get("dtos.permalinkVM.metaData.genealogyData"), map.getGenealogyData());
		inspireField.put(Messages.get("dtos.permalinkVM.metaData.thesaurusISO"), map.getThesaurusISO());
		inspireField.put(Messages.get("dtos.permalinkVM.metaData.thesaurusINSPIRE"), map.getThesaurusINSPIRE());
		inspireField.put(Messages.get("dtos.permalinkVM.metaData.thesaurusCBN"), map.getThesaurusCBN());
		inspireField.put(Messages.get("dtos.permalinkVM.metaData.keywords"), commaReducer(map.getKeywords()));
		inspireField.put(Messages.get("dtos.permalinkVM.metaData.dataState"), map.getDataState());
		inspireField.put(Messages.get("dtos.permalinkVM.metaData.usageLimit"), map.getUsageLimit());
		inspireField.put(Messages.get("dtos.permalinkVM.metaData.updateFrequency"), map.getUpdateFrequency());
		inspireField.put(Messages.get("dtos.permalinkVM.metaData.cbnManager"), map.getCbnManager());
		inspireField.put(Messages.get("dtos.permalinkVM.metaData.dataOwner"), map.getDataOwner());
		inspireField.put(Messages.get("dtos.permalinkVM.metaData.contactInspire"), map.getContactInspire());
		
		LinkedHashMap<String, LinkedHashMap<String, String>> metaData = new LinkedHashMap<>();
		metaData.put(Messages.get("dtos.permalinkVM.metaData.title.taxon"), taxon);
		metaData.put(Messages.get("dtos.permalinkVM.metaData.title.project"), project);
		metaData.put(Messages.get("dtos.permalinkVM.metaData.title.creator"), creator);
		metaData.put(Messages.get("dtos.permalinkVM.metaData.title.geographicalArea"), geographicalArea);
		metaData.put(Messages.get("dtos.permalinkVM.metaData.title.inspireField"), inspireField);
		metaData.put(Messages.get("dtos.permalinkVM.metaData.title.picture"), image);
		
		return metaData;
	}
	
	private String commaReducer(List<String> strings){
		if(strings == null){
			return "";
		}
		return strings.stream().reduce((t, u) -> t + ", " + u).orElse("");
	}
}