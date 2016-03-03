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

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;

import conf.SearchConfig;
import play.Logger;
import play.libs.Json;

/**
 * Implementation of the MapRepository interface using Elastic Search as storage.
 */
public class ESMapRepository implements MapRepository {
	private final SearchConfig config;

	@Inject
	public ESMapRepository(SearchConfig config){
		this.config = config;
	}

	@Override
	public Optional<Map> getMap(String id){
		try(Client client = getClient()){
			GetResponse response = client.prepareGet(
					config.getESIndexName(), config.getESTypeName(), id).get();
			Logger.debug("getMap found id: " + response.getId());
			return fromSource(response.getId(), response.getSourceAsString());
		}
	}

	@Override
	public void delete(String id){
		try (Client client = getClient()){
			client.prepareDelete(config.getESIndexName(), config.getESTypeName(), id)
			.setRefresh(true)
			.execute()
			.actionGet();
		}
	}

	@Override
	public SearchResult searchMaps(SearchRequest searchRequest, boolean includePrivateMaps){
		QueryBuilder qb = ESRequestBuilder.createRequest(searchRequest, includePrivateMaps);
		int page = searchRequest.getPageNumber();
		int size = searchRequest.getPageSize();
		int from = (page - 1) * size;

		Logger.debug("Fetching maps from " + from + " to " + (from + size) + "(size = " + size + ")");

		try(Client client = getClient()){
			SearchResponse response = client.prepareSearch(config.getESIndexName())
					.setTypes(config.getESTypeName())
					.setQuery(qb)
					.setFrom(from)
					.setSize(size)
					.execute()
					.actionGet();

			SearchHits hits = response.getHits();
			ArrayList<Map> maps = new ArrayList<>();
			for(SearchHit hit: hits){
				Optional<Map> map = fromSource(hit.getId(), hit.getSourceAsString());
				map.ifPresent(maps::add);
			}
			return new SearchResult(maps, hits.getTotalHits(), response.getTookInMillis());
		}
	}

	@Override
	public List<String> putMaps(List<MapDto> mapdtos) throws IOException{
		ArrayList<String> ids = new ArrayList<>();
		for (MapDto mapDto : mapdtos) { //TODO: bulk indexing 
			ids.add(putMap(mapDto).getId());
		}
		return ids;
	}

	private IndexResponse putMap(MapDto mapdto) throws IOException{
		ObjectMapper mapper = new ObjectMapper(); //TODO: We should not use a new Object mapper each time
		byte[] json = mapper.writeValueAsBytes(mapdto);
		Client client = getClient();
		IndexResponse response = client.prepareIndex(config.getESIndexName(), config.getESTypeName())
				.setSource(json)
				.get();
		return response;
	}

	private static Optional<Map> fromSource(String id, String source){
		if(source == null){
			return Optional.empty();
		}
		MapDto map = Json.fromJson(Json.parse(source), MapDto.class);
		return Optional.of(new Map(map, id)); 
	}

	private Client getClient(){
		Settings settings = Settings.settingsBuilder()
				.put("cluster.name", config.getESClusterName()).build();

		try {
			return TransportClient.builder().settings(settings).build()
					.addTransportAddress(new InetSocketTransportAddress(
							InetAddress.getByName(config.getESAddress()), 
							config.getESPort()
							));
		} catch (UnknownHostException ex) {
			throw new UncheckedIOException(ex);
		}
	}
}
