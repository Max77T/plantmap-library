package Search;

import static org.junit.Assert.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.elasticsearch.action.index.IndexResponse;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import conf.SearchConfig;
import io.searchbox.client.JestClient;
import io.searchbox.client.JestClientFactory;
import io.searchbox.client.config.HttpClientConfig;
import io.searchbox.indices.CreateIndex;
import io.searchbox.indices.DeleteIndex;
import io.searchbox.indices.IndicesExists;
import io.searchbox.indices.mapping.PutMapping;
import models.maps.ESMapRepository;
import models.maps.Map;
import models.maps.MapDto;
import models.maps.SearchRequest;
import models.maps.SearchResult;
import models.maps.Taxon;

public class Search {

	private static SearchConfig config;
	private static ESMapRepository esMap;
	private static JestClient client;

	@BeforeClass
	public static void start() throws IOException {
		config = new SearchConfig() {
			@Override public int getESPort() { return 9300; }
			@Override public String getESClusterName() { return "plantmap_cluster"; }
			@Override public String getESAddress() { return "localhost"; }
			@Override public String getESIndexName() { return "totos"; }
			@Override public String getESTypeName() { return "map"; }
			@Override public List<String> getCbnList() { return new ArrayList<>(); }
		};
		esMap = new ESMapRepository(config);

		JestClientFactory factory = new JestClientFactory();
		factory.setHttpClientConfig(new HttpClientConfig.Builder("http://localhost:9200")
				.multiThreaded(true)
				.build());

		client = factory.getObject();

		boolean indexExists = client.execute(new IndicesExists.Builder(config.getESIndexName()).build()).isSucceeded();
		if (indexExists) {
			client.execute(new DeleteIndex.Builder(config.getESIndexName()).build());
		}
		client.execute(new CreateIndex.Builder(config.getESIndexName()).build());

		String json = new String(Files.readAllBytes(Paths.get("conf/mapping.es")));
		System.out.println(json);

		PutMapping putMapping = new PutMapping.Builder(
				config.getESIndexName(),
				config.getESTypeName(),
				json).build();
		System.out.println(putMapping);
		client.execute(putMapping);

	}

	@AfterClass
	public static void stop() throws IOException {
		client.execute(new DeleteIndex.Builder(config.getESIndexName()).build());
		client.shutdownClient();
	}
	
	@Test
	public void TestSearch_DoesntShowPrivate() throws IOException, InterruptedException {
		MapDto mapdo = new MapDto();
		mapdo.keywords = new ArrayList<>();
		mapdo.keywords.add("test");
		mapdo.isPrivate = true;

		String mapId = esMap.putMaps(Arrays.asList(mapdo)).get(0);

		SearchRequest searchRequest = new SearchRequest();
		searchRequest.setKeywords("test");
		searchRequest.setPageSize(10);
		searchRequest.setPageNumber(1);
		Thread.sleep(1000);
		SearchResult sr = esMap.searchMaps(searchRequest, false);
		assertTrue(sr.getMaps().isEmpty());
		esMap.delete(mapId);
	}

	@Test
	public void TestSearch_ShowPublic() throws IOException, InterruptedException {
		MapDto mapdo = new MapDto();
		mapdo.keywords = new ArrayList<>();
		mapdo.keywords.add("test");
		mapdo.isPrivate = false;

		String mapId = esMap.putMaps(Arrays.asList(mapdo)).get(0);
		Thread.sleep(1000);
		SearchRequest searchRequest = new SearchRequest();
		searchRequest.setKeywords("test");
		searchRequest.setPageSize(10);
		searchRequest.setPageNumber(1);

		SearchResult sr = esMap.searchMaps(searchRequest, false);	
		assertFalse(sr.getMaps().isEmpty());
		esMap.delete(mapId);
	}

	@Test
	public void TestSearch_ShowPublic2() throws IOException, InterruptedException {
		MapDto mapdo = new MapDto();
		mapdo.keywords = new ArrayList<>();
		mapdo.keywords.add("toto");
		mapdo.isPrivate = false;

		String mapId = esMap.putMaps(Arrays.asList(mapdo)).get(0);
		Thread.sleep(1000);
		SearchRequest searchRequest = new SearchRequest();
		searchRequest.setKeywords("toto");
		searchRequest.setPageSize(10);
		searchRequest.setPageNumber(1);

		SearchResult sr = esMap.searchMaps(searchRequest, true);
		assertFalse(sr.getMaps().isEmpty());
		esMap.delete(mapId);
	}

	@Test
	public void TestSearch_Keyword() throws IOException, InterruptedException {
		MapDto mapdo = new MapDto();
		mapdo.keywords = new ArrayList<>();
		mapdo.keywords.add("test");
		mapdo.isPrivate = false;

		String mapId = esMap.putMaps(Arrays.asList(mapdo)).get(0);
		Thread.sleep(1000);
		SearchRequest searchRequest = new SearchRequest();
		searchRequest.setKeywords("test");
		searchRequest.setPageSize(10);
		searchRequest.setPageNumber(1);

		SearchResult sr = esMap.searchMaps(searchRequest, true);
		assertFalse(sr.getMaps().isEmpty());
		esMap.delete(mapId);
	}

	@Test
	public void TestSearch_Keyword_ManyOne() throws IOException, InterruptedException {
		MapDto mapdo = new MapDto();
		mapdo.keywords = new ArrayList<>();
		mapdo.keywords.add("voiture");
		mapdo.keywords.add("bateau");
		mapdo.isPrivate = false;

		String mapId = esMap.putMaps(Arrays.asList(mapdo)).get(0);
		Thread.sleep(1000);
		SearchRequest searchRequest = new SearchRequest();
		searchRequest.setKeywords("bateau");
		searchRequest.setPageSize(10);
		searchRequest.setPageNumber(1);

		SearchResult sr = esMap.searchMaps(searchRequest, true);
		assertFalse(sr.getMaps().isEmpty());
		esMap.delete(mapId);
	}

	@Test
	public void TestSearch_Keyword_OneMany() throws IOException, InterruptedException {
		MapDto mapdo = new MapDto();
		mapdo.keywords = new ArrayList<>();
		mapdo.keywords.add("bateau");
		mapdo.isPrivate = false;

		String mapId = esMap.putMaps(Arrays.asList(mapdo)).get(0);
		Thread.sleep(1000);
		SearchRequest searchRequest = new SearchRequest();
		searchRequest.setKeywords("bateau,voiture");
		searchRequest.setPageSize(10);
		searchRequest.setPageNumber(1);

		SearchResult sr = esMap.searchMaps(searchRequest, true);
		assertTrue(sr.getMaps().isEmpty());
		esMap.delete(mapId);
	}

	@Test
	public void TestSearch_Keyword_ManyMany() throws IOException, InterruptedException {
		MapDto mapdo = new MapDto();
		mapdo.keywords = new ArrayList<>();
		mapdo.keywords.add("bateau");
		mapdo.keywords.add("moto");
		mapdo.keywords.add("voiture");
		mapdo.isPrivate = false;

		String mapId = esMap.putMaps(Arrays.asList(mapdo)).get(0);
		Thread.sleep(1000);
		SearchRequest searchRequest = new SearchRequest();
		searchRequest.setKeywords("bateau voiture");
		searchRequest.setPageSize(10);
		searchRequest.setPageNumber(1);

		SearchResult sr = esMap.searchMaps(searchRequest, true);
		assertFalse(sr.getMaps().isEmpty());
		esMap.delete(mapId);
	}



	@Test
	public void TestPutSearchKeywordInformationwithPrediction() throws IOException, InterruptedException {
		MapDto mapdo = new MapDto();
		mapdo.keywords = new ArrayList<>();
		mapdo.keywords.add("rose");
		String mapId = esMap.putMaps(Arrays.asList(mapdo)).get(0);
		Thread.sleep(1000);
		SearchRequest searchRequest = new SearchRequest();
		searchRequest.setKeywords("ros");
		searchRequest.setPageSize(10);
		searchRequest.setPageNumber(1);	
		for (Map map : esMap.searchMaps(searchRequest, true).getMaps()) {
			assertTrue(map.getKeywords().contains("rose"));	
		}
		esMap.delete(mapId);
	}

	@Test
	public void TestPutSearchKeywordInformationwithPredictionWithAccent() throws IOException, InterruptedException {
		MapDto mapdo = new MapDto();
		mapdo.projectName="rose";
		String mapId = esMap.putMaps(Arrays.asList(mapdo)).get(0);
		Thread.sleep(1000);
		SearchRequest searchRequest = new SearchRequest();
		searchRequest.setProjectName("rosé");
		searchRequest.setPageSize(10);
		searchRequest.setPageNumber(1);
		for (Map map : esMap.searchMaps(searchRequest, true).getMaps()) {
			assertTrue(map.getProjectName().equals("rose")); 		
		}
		esMap.delete(mapId);
	}

	@Test
	public void TestPutSearchKeywordInformationwithPredictionSuffixe() throws IOException, InterruptedException {
		MapDto mapdo = new MapDto();
		mapdo.projectName="rose";
		String mapId = esMap.putMaps(Arrays.asList(mapdo)).get(0);
		Thread.sleep(1000);
		SearchRequest searchRequest = new SearchRequest();
		searchRequest.setProjectName("ose");
		searchRequest.setPageSize(10);
		searchRequest.setPageNumber(1);
		for (Map map : esMap.searchMaps(searchRequest, true).getMaps()) {
			assertTrue(map.getProjectName().equals("rose"));
		}
		esMap.delete(mapId);
	}

	@Test
	public void TestPutSearchKeywordInformationwithPredictionSuffixeAndDate() throws IOException, InterruptedException {
		MapDto mapdo = new MapDto();
		mapdo.projectName="rose";
		mapdo.generationDate = "2015-12-25";
		String mapId = esMap.putMaps(Arrays.asList(mapdo)).get(0);
		Thread.sleep(1000);
		SearchRequest searchRequest = new SearchRequest();
		searchRequest.setProjectName("ose");
		searchRequest.setRangeGenerationDateStart("2015-12-23");
		searchRequest.setRangeGenerationDateEnd("2015-12-29");
		searchRequest.setPageSize(10);
		searchRequest.setPageNumber(1);
		for (Map map : esMap.searchMaps(searchRequest, true).getMaps()) {
			assertTrue(map.getProjectName().equals("rose")); 
		}
		esMap.delete(mapId);
	}

	@Test
	public void TestPutProjectNameSearchInformationwithPrediction() throws IOException, InterruptedException {
		//TODO: fix and rename
		MapDto mapdo = new MapDto();
		mapdo.projectName="rose";
		String mapId = esMap.putMaps(Arrays.asList(mapdo)).get(0);
		Thread.sleep(1000);
		SearchRequest searchRequest = new SearchRequest();
		searchRequest.setProjectName("ros");
		searchRequest.setPageSize(10);
		searchRequest.setPageNumber(1);
		for (Map map : esMap.searchMaps(searchRequest, true).getMaps()) {
			assertTrue(map.getProjectName().equals("rose"));
			esMap.delete(mapId);
		}

	}

	@Test
	public void TestPutSearchProjectNameInformationwithPredictionWithAccent() throws IOException, InterruptedException {
		MapDto mapdo = new MapDto();
		mapdo.projectName="rose";
		String mapId = esMap.putMaps(Arrays.asList(mapdo)).get(0);
		Thread.sleep(1000);
		SearchRequest searchRequest = new SearchRequest();
		searchRequest.setProjectName("rosé");
		searchRequest.setPageSize(10);
		searchRequest.setPageNumber(1);
		for (Map map : esMap.searchMaps(searchRequest, true).getMaps()) {
			assertTrue(map.getProjectName().equals("rose"));
		}
		esMap.delete(mapId);
	}

	@Test
	public void TestPutSearchProjectNameInformationwithPredictionSuffixe() throws IOException, InterruptedException {
		MapDto mapdo = new MapDto();
		mapdo.projectName="rose";
		String mapId = esMap.putMaps(Arrays.asList(mapdo)).get(0);
		Thread.sleep(1000);
		SearchRequest searchRequest = new SearchRequest();
		searchRequest.setProjectName("ose");
		searchRequest.setPageSize(10);
		searchRequest.setPageNumber(1);
		for (Map map : esMap.searchMaps(searchRequest, true).getMaps()) {
			assertTrue(map.getProjectName().equals("rose"));
		}

		esMap.delete(mapId);
	}

	@Test
	public void TestPutSearchProjectNameInformationwithPredictionSuffixeAndDate() throws IOException, InterruptedException {
		MapDto mapdo = new MapDto();
		mapdo.projectName="rose";
		mapdo.generationDate = "2015-12-25";
		String mapId = esMap.putMaps(Arrays.asList(mapdo)).get(0);
		Thread.sleep(1000);
		SearchRequest searchRequest = new SearchRequest();
		searchRequest.setProjectName("ose");
		searchRequest.setRangeGenerationDateStart("2015-12-23");
		searchRequest.setRangeGenerationDateEnd("2015-12-29");
		searchRequest.setPageSize(10);
		searchRequest.setPageNumber(1);
		for (Map map : esMap.searchMaps(searchRequest, true).getMaps()) {
			assertTrue(map.getProjectName().equals("rose")); 
		}
		
		esMap.delete(mapId);
	}


	/*Test Of Project Owner */

	@Test
	public void TestPutProjectOwnerSearchInformationwithPrediction() throws IOException, InterruptedException {
		MapDto mapdo = new MapDto();
		mapdo.contactName="Romain";
		String mapId = esMap.putMaps(Arrays.asList(mapdo)).get(0);
		Thread.sleep(1000);
		SearchRequest searchRequest = new SearchRequest();
		searchRequest.setContactName("Romain");
		searchRequest.setPageSize(10);
		searchRequest.setPageNumber(1);
		for (Map map : esMap.searchMaps(searchRequest, true).getMaps()) {
			assertTrue(map.getContactName().equals("Romain"));
		}
		esMap.delete(mapId);
	}

	@Test
	public void TestPutSearchProjectOwnerInformationwithPredictionWithAccent() throws IOException, InterruptedException {

		MapDto mapdo = new MapDto();
		mapdo.contactName="Romain";
		String mapId = esMap.putMaps(Arrays.asList(mapdo)).get(0);
		Thread.sleep(1000);
		SearchRequest searchRequest = new SearchRequest();
		searchRequest.setContactName("Romaïn");
		searchRequest.setPageSize(10);
		searchRequest.setPageNumber(1);
		for (Map map : esMap.searchMaps(searchRequest, true).getMaps()) {
			assertTrue(map.getContactName().equals("Romain"));		
		}
		esMap.delete(mapId);
	}

	@Test
	public void TestPutSearchProjectOwnerInformationwithPredictionSuffixe() throws IOException, InterruptedException {
		MapDto mapdo = new MapDto();
		mapdo.contactName="Romain";
		String mapId = esMap.putMaps(Arrays.asList(mapdo)).get(0);
		Thread.sleep(1000);
		SearchRequest searchRequest = new SearchRequest();
		searchRequest.setContactName("main");
		searchRequest.setPageSize(10);
		searchRequest.setPageNumber(1);
		for (Map map : esMap.searchMaps(searchRequest, true).getMaps()) {
			assertTrue(map.getContactName().equals("Romain")); 
		}
		esMap.delete(mapId);
	}

	@Test
	public void TestPutSearchProjectOwnerInformationwithPredictionSuffixeAndDate() throws IOException, InterruptedException {
		MapDto mapdo = new MapDto();
		mapdo.contactName="Romain";
		mapdo.generationDate = "2015-12-25";
		String mapId = esMap.putMaps(Arrays.asList(mapdo)).get(0);
		Thread.sleep(1000);
		SearchRequest searchRequest = new SearchRequest();
		searchRequest.setContactName("main");
		searchRequest.setRangeGenerationDateStart("2015-12-23");
		searchRequest.setRangeGenerationDateEnd("2015-12-29");
		searchRequest.setPageSize(10);
		searchRequest.setPageNumber(1);
		for (Map map : esMap.searchMaps(searchRequest, true).getMaps()) {

			assertTrue(map.getContactName().equals("Romain"));
		}
		esMap.delete(mapId);
	}

	/* Test Taxon */	
	@Test
	public void TestPutTaxonNameSearchInformationwithPrediction() throws IOException, InterruptedException {

		MapDto mapdo = new MapDto();
		mapdo.taxon=new Taxon();
		mapdo.taxon.setCdref("Corse");
		mapdo.taxon.setName("rose");
		String mapId = esMap.putMaps(Arrays.asList(mapdo)).get(0);
		Thread.sleep(1000);
		SearchRequest searchRequest = new SearchRequest();
		searchRequest.setTaxon("rose");
		searchRequest.setPageSize(10);
		searchRequest.setPageNumber(1);
		for (Map map : esMap.searchMaps(searchRequest, true).getMaps()) {
			assertTrue((map.getTaxon().getCdref().equals("rose")) || (map.getTaxon().getName().equals("rose")));
		}
		esMap.delete(mapId);
	}

	@Test
	public void TestPutSearchTaxonNameInformationwithPredictionWithAccent() throws IOException, InterruptedException {
		MapDto mapdo = new MapDto();
		mapdo.taxon=new Taxon();
		mapdo.taxon.setCdref("Corse");
		mapdo.taxon.setName("rose");
		String mapId = esMap.putMaps(Arrays.asList(mapdo)).get(0);
		Thread.sleep(1000);
		SearchRequest searchRequest = new SearchRequest();
		searchRequest.setTaxon("rosé");;
		searchRequest.setPageSize(10);
		searchRequest.setPageNumber(1);
		for (Map map : esMap.searchMaps(searchRequest, true).getMaps()) {
			assertTrue((map.getTaxon().getCdref().equals("rose")) || (map.getTaxon().getName().equals("rose"))); 
		}
		esMap.delete(mapId);
	}

	@Test
	public void TestPutSearchTaxonNameInformationwithPredictionSuffixe() throws IOException, InterruptedException {
		MapDto mapdo = new MapDto();
		mapdo.taxon=new Taxon();
		mapdo.taxon.setCdref("Corse");
		mapdo.taxon.setName("rose");
		String mapId = esMap.putMaps(Arrays.asList(mapdo)).get(0);
		Thread.sleep(1000);
		SearchRequest searchRequest = new SearchRequest();
		searchRequest.setTaxon("ose");
		searchRequest.setPageSize(10);
		searchRequest.setPageNumber(1);
		for (Map map : esMap.searchMaps(searchRequest, true).getMaps()) {
			assertTrue((map.getTaxon().getCdref().equals("rose")) || (map.getTaxon().getName().equals("rose")));
		}
		esMap.delete(mapId);
	}

	@Test
	public void TestPutSearchTaxonNameInformationwithPredictionSuffixeAndDate() throws IOException, InterruptedException {
		MapDto mapdo = new MapDto();
		mapdo.taxon=new Taxon();
		mapdo.taxon.setCdref("Corse");
		mapdo.taxon.setName("rose");
		mapdo.generationDate = "2015-12-25";
		String mapId = esMap.putMaps(Arrays.asList(mapdo)).get(0);
		Thread.sleep(1000);
		SearchRequest searchRequest = new SearchRequest();
		searchRequest.setTaxon("ose");
		searchRequest.setRangeGenerationDateStart("2015-12-23");
		searchRequest.setRangeGenerationDateEnd("2015-12-29");
		searchRequest.setPageSize(10);
		searchRequest.setPageNumber(1);
		for (Map map : esMap.searchMaps(searchRequest, true).getMaps()) {
			assertTrue((map.getTaxon().getCdref().equals("rose")) || (map.getTaxon().getName().equals("rose")));		
		}

		esMap.delete(mapId);
	}

	/* Test taxon 2 */ 

	@Test
	public void TestPutTaxonCDREFSearchInformationwithPrediction() throws IOException, InterruptedException {

		MapDto mapdo = new MapDto();
		mapdo.taxon=new Taxon();
		mapdo.taxon.setCdref("Corse");
		mapdo.taxon.setName("rose");
		String mapId = esMap.putMaps(Arrays.asList(mapdo)).get(0);
		Thread.sleep(1000);
		SearchRequest searchRequest = new SearchRequest();
		searchRequest.setTaxon("Corse");;
		searchRequest.setPageSize(10);
		searchRequest.setPageNumber(1);
		for (Map map : esMap.searchMaps(searchRequest, true).getMaps()) {
			assertTrue((map.getTaxon().getCdref().equals("Corse")) || (map.getTaxon().getName().equals("Corse")) );
		}
		esMap.delete(mapId);
	}

	@Test
	public void TestPutSearchTaxonCDREFInformationwithPredictionWithAccent() throws IOException, InterruptedException {
		MapDto mapdo = new MapDto();
		mapdo.taxon=new Taxon();
		mapdo.taxon.setCdref("Corse");
		mapdo.taxon.setName("rose");
		String mapId = esMap.putMaps(Arrays.asList(mapdo)).get(0);
		Thread.sleep(1000);
		SearchRequest searchRequest = new SearchRequest();
		searchRequest.setTaxon("Corsé");;
		searchRequest.setPageSize(10);
		searchRequest.setPageNumber(1);
		for (Map map : esMap.searchMaps(searchRequest, true).getMaps()) {
			assertTrue((map.getTaxon().getCdref().equals("Corse")) || (map.getTaxon().getName().equals("Corse")));
		}
		esMap.delete(mapId);

	}

	@Test
	public void TestPutSearchTaxonCDREFInformationwithPredictionSuffixe() throws IOException, InterruptedException {
		MapDto mapdo = new MapDto();
		mapdo.taxon=new Taxon();
		mapdo.taxon.setCdref("Corse");
		mapdo.taxon.setName("rose");
		String mapId = esMap.putMaps(Arrays.asList(mapdo)).get(0);
		Thread.sleep(1000);
		SearchRequest searchRequest = new SearchRequest();
		searchRequest.setTaxon("orse");
		searchRequest.setPageSize(10);
		searchRequest.setPageNumber(1);
		for (Map map : esMap.searchMaps(searchRequest, true).getMaps()) {
		assertTrue((map.getTaxon().getCdref().equals("Corse")) || (map.getTaxon().getName().equals("Corse")));
		}
		esMap.delete(mapId);
	}

	@Test
	public void TestPutSearchTaxonCDREFInformationwithPredictionSuffixeAndDate() throws IOException, InterruptedException {
		MapDto mapdo = new MapDto();
		mapdo.taxon=new Taxon();
		mapdo.taxon.setCdref("Corse");
		mapdo.taxon.setName("rose");
		mapdo.generationDate = "2015-12-25";
		String mapId = esMap.putMaps(Arrays.asList(mapdo)).get(0);
		Thread.sleep(1000);
		SearchRequest searchRequest = new SearchRequest();
		searchRequest.setTaxon("cors");
		searchRequest.setRangeGenerationDateStart("2015-12-23");
		searchRequest.setRangeGenerationDateEnd("2015-12-29");
		searchRequest.setPageSize(10);
		searchRequest.setPageNumber(1);
		for (Map map : esMap.searchMaps(searchRequest, true).getMaps()) {
			assertTrue((map.getTaxon().getCdref().equals("Corse")) || (map.getTaxon().getName().equals("Corse")));			
		}
		esMap.delete(mapId);
	}
}
