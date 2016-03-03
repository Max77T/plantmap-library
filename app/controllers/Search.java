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
package controllers;

import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Inject;

import conf.SearchConfig;
import controllers.actions.UserContext;
import models.maps.MapRepository;
import models.maps.MapSummary;
import models.maps.SearchRequest;
import models.maps.SearchResult;
import play.Logger;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Result;
import viewmodels.SearchVM;
import views.html.search;

/**
 * Controller which handles search actions.
 */
public class Search extends Base {
	private final MapRepository mapRepository;
	private final SearchConfig searchConfig;
	
	@Inject
	public Search(MapRepository mapRepository, SearchConfig searchConfig){
		this.mapRepository = mapRepository;
		this.searchConfig = searchConfig;
	}
	
	public  Result index() {
		SearchVM svm = new SearchVM(searchConfig.getCbnList());
		return ok(search.render(svm));
	}

	/**
	 * Return hits from elastic search corresponding to SearchRequest.
	 */
	@BodyParser.Of(BodyParser.Json.class)
	public Result search(){
		JsonNode json = request().body().asJson();
		SearchRequest sr = Json.fromJson(json, SearchRequest.class);
		
		UserContext userCtx = getUserContext();
		SearchResult result = mapRepository.searchMaps(sr, userCtx.isAuthenticated());
		
		Logger.info("Found " + result.getNbHits() + " maps, return only " + result.getMaps().size());
		List<MapSummary> summaries = result.getMaps().stream()
				.map(MapSummary::fromMap)
				.collect(Collectors.toList());
		
		return ok(Json.toJson(new SearchResponse(summaries, result.getNbHits(), result.getTimeTaken())));
	}
	
	/**
	 * Used for serialization
	 */
	static class SearchResponse {
		private final List<MapSummary> results;
		private final long nbHits;
		private final long timeTaken;
		
		public SearchResponse(List<MapSummary> results, long nbHits, long timeTaken) {
			this.results = results;
			this.nbHits = nbHits;
			this.timeTaken = timeTaken;
		}

		public List<MapSummary> getResults() {
			return results;
		}

		public long getNbHits() {
			return nbHits;
		}

		public long getTimeTaken() {
			return timeTaken;
		}
	}
}
