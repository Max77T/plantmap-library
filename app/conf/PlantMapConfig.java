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
package conf;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.google.inject.Inject;

import play.Configuration;
import play.Logger;

/**
 * Store runtime configuration for plantmap application. 
 * This class read configuration through Play configuration object.
 * It should be loaded only one time.
 */
public class PlantMapConfig implements UploadConfig, SearchConfig, SecurityConfig {
	// SearchConfig
	private final String elasticSearchAddress;
	private final int elasticSearchPort;
	private final String elasticSearchClusterName;
	private final String esTypeName;
	private final String esIndexName;
	private final List<String> cbnList;
	
	// UploadConfig
	private final List<String> allowedImgExtensions;
	private final Path tmpDirectory;
	private final Path libraryStoreDir;
	private final Path libraryThumbnailDir;
	
	// SecurityConfig
	private final long sessionTimeout;
	
	@Inject
	public PlantMapConfig(Configuration config) throws IOException {
		// SearchConfig
		elasticSearchAddress = getConfigString(config, "plantmap.es.address", "localhost");
		elasticSearchPort = getConfigInt(config, "plantmap.es.port", 9300);
		elasticSearchClusterName = getConfigString(config, "plantmap.es.clustername");
		esTypeName = getConfigString(config, "plantmap.es.typename", "map");
		esIndexName = getConfigString(config, "plantmap.es.indexname", "maps");
		cbnList = Files.lines(Paths.get("conf/cbn-list.txt"))
				.sorted()
				.collect(Collectors.toList());
		
		// UploadConfig
		allowedImgExtensions = Arrays.asList(
				getConfigString(config, "plantmap.img.allowed", "jpeg;jpg;tiff;png").split(";"));
		
		tmpDirectory = Paths.get(getConfigString(config, "plantmap.tmpdir"));
		
		libraryStoreDir = Paths.get(getConfigString(config, "plantmap.librarydir"));
		libraryStoreDir.toFile().mkdirs();
		
		libraryThumbnailDir = Paths.get(getConfigString(config, 
				"plantmap.thumbnaildir",
				libraryStoreDir.resolve("thumbnails").toString()));
		libraryThumbnailDir.toFile().mkdirs();
		
		// SecurityConfig
		sessionTimeout = Long.valueOf(
				getConfigInt(config, "plantmap.session.timeout", 30) * 1000 * 60);
	}
	
	private String getConfigString(Configuration config, String key){
		return getConfigString(config, key, null);
	}
	
	private String getConfigString(Configuration config, String key, String defaultValue){
		String value = config.getString(key);
		if(value == null || value.isEmpty()){
			logNotFound(key, defaultValue);
		}
		return value != null ? value : defaultValue;
	}
	
	private int getConfigInt(Configuration config, String key, Integer defaultValue){
		Integer value = config.getInt(key);
		if(value == null){
			logNotFound(key, defaultValue);
		}
		return value != null ? value : defaultValue;
	}
	
	private void logNotFound(String key, Object defaultValue){
		Logger.warn("Configuration not found for key: " + key + ", use default value: " + defaultValue);
	}

	@Override
	public Path getTmpDirectory() {
		return tmpDirectory;
	}

	@Override
	public String getESAddress() {
		return elasticSearchAddress;
	}

	@Override
	public int getESPort() {
		return elasticSearchPort;
	}

	@Override
	public String getESClusterName() {
		return elasticSearchClusterName;
	}

	@Override
	public List<String> getAllowedImgExtensions() {
		return allowedImgExtensions;
	}

	@Override
	public Path getLibraryStoreDir() {
		return libraryStoreDir;
	}

	@Override
	public Path getLibraryThumbnailDir() {
		return libraryThumbnailDir;
	}

	@Override
	public String getESIndexName() {
		return esIndexName;
	}

	@Override
	public String getESTypeName() {
		return esTypeName;
	}

	@Override
	public long getSessionTimeout() {
		return sessionTimeout;
	}

	@Override
	public List<String> getCbnList() {
		return cbnList;
	}
}
