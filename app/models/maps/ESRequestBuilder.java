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

import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;

import play.Logger;

/**
 * Transform a SearchRequest into an ElasticSearch request.
 */
class ESRequestBuilder {
	private static final String[] fieldsToSearchIn = new String[]{
			"keywords", "taxon.cdref", "taxon.name.analyzed", "extension",
			"dimension", "projection", "organizationCbn.analyzed", "email",
			"projectDescription", "projectName.analyzed", "versionNumber",
			"genealogyData", "thesaurusISO.analyzed", "thesaurusINSPIRE.analyzed",
			"thesaurusCBN.analyzed", "dataStatus", "usageLimit", "updateFrequency",
			"dataOwner.analyzed", "contactName.analyzed", "contactInspire.analyzed", "cbnManager.analyzed"
			};

	static QueryBuilder createRequest(SearchRequest sr, boolean includePrivateMaps){
		BoolQueryBuilder qb = QueryBuilders.boolQuery();

		if(isStringFieldSet(sr.getSearchTerms())){
			MultiMatchQueryBuilder mmqb = QueryBuilders.multiMatchQuery(sr.getSearchTerms(), fieldsToSearchIn).fuzziness(Fuzziness.AUTO);
			qb.must(mmqb);
		}

		if(!includePrivateMaps){
			qb.filter(QueryBuilders.termQuery("isPrivate", false));
		}

		if(isStringFieldSet(sr.getKeywords())){
			for(String keyword: sr.getKeywords().split(",")){
				qb.filter(QueryBuilders.matchQuery("keywords", keyword));
			}
		}

		String taxon = sr.getTaxon();
		if(isStringFieldSet(taxon)){
			if(isNumber(taxon)){
				Logger.debug("Taxon is a number, search in CDREF");
				qb.filter(QueryBuilders.termQuery("taxon.cdref", taxon));
			} else {
				Logger.debug("Taxon is not a number, search in name");
				qb.filter(QueryBuilders.termQuery("taxon.name.original", taxon));
			}
		}

		if(isStringFieldSet(sr.getProjectName())){
			qb.filter(QueryBuilders.termQuery("projectName.original", sr.getProjectName()));
		}

		if(isStringFieldSet(sr.getContactName())){
			qb.filter(QueryBuilders.termQuery("contactName.original", sr.getContactName()));
		}

		if(isStringFieldSet(sr.getOrganizationCbn())){
			qb.filter(QueryBuilders.termQuery("organizationCbn.original", sr.getOrganizationCbn()));
		}

		if(isStringFieldSet(sr.getRangeGenerationDateStart()) && isStringFieldSet(sr.getRangeGenerationDateEnd())){
			qb.filter(new RangeQueryBuilder("generationDate")
					.from(sr.getRangeGenerationDateStart())
					.to(sr.getRangeGenerationDateEnd()));
		}

		if(isStringFieldSet(sr.getVisibility())){
			switch(sr.getVisibility()){
			case "private":
				qb.filter(QueryBuilders.termQuery("isPrivate", true));
				break;
			case "public":
				qb.filter(QueryBuilders.termQuery("isPrivate", false));
				break;
			}
		}

		Logger.debug(qb.toString());
		return qb;
	}

	private static boolean isStringFieldSet(String value){
		return value != null &&
				!value.isEmpty() &&
				!value.equals("undefined");
	}

	private static boolean isNumber(String value){
        return value.chars().allMatch(Character::isDigit);
    }
}
