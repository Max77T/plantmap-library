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
package modules;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;

import conf.PlantMapConfig;
import conf.SearchConfig;
import conf.SecurityConfig;
import conf.UploadConfig;
import models.maps.ESMapRepository;
import models.maps.MapRepository;
import models.upload.EbeanUploadHistoryRepository;
import models.upload.UploadHistoryRepository;
import models.users.EbeanUserRepository;
import models.users.UserRepository;
import play.Logger;
import services.ImageService;
import services.ImageServiceImpl;
import services.filetransfer.DeleteService;
import services.filetransfer.DeleteServiceImpl;
import services.filetransfer.DownloadService;
import services.filetransfer.DownloadServiceImpl;
import services.filetransfer.UploadService;
import services.filetransfer.UploadServiceImpl;

/**
 * Module used to bind injected component in PlantMap controllers.
 * @see <a href="https://playframework.com/documentation/2.4.x/JavaDependencyInjection">Play documentation</a> for how binding works
 * @see <a href="http://stackoverflow.com/a/4792416">StackOverflow</a> for the binding of one instance to multiple interfaces with Guice
 */
public class PlantMapModule extends AbstractModule {
	
	@Override
	protected void configure() {
		Logger.debug("Configure guice binding...");
		// Configuration
		bind(PlantMapConfig.class).in(Singleton.class); // Use only one instance of configuration
		bind(UploadConfig.class).to(PlantMapConfig.class).asEagerSingleton();
		bind(SearchConfig.class).to(PlantMapConfig.class).asEagerSingleton();
		bind(SecurityConfig.class).to(PlantMapConfig.class).asEagerSingleton();
		
		// Repository
		bind(MapRepository.class).to(ESMapRepository.class);
		bind(UserRepository.class).to(EbeanUserRepository.class);
		bind(UploadHistoryRepository.class).to(EbeanUploadHistoryRepository.class);
		
		// Services
		bind(UploadService.class).to(UploadServiceImpl.class);
		bind(ImageService.class).to(ImageServiceImpl.class);
		bind(DownloadService.class).to(DownloadServiceImpl.class);
		bind(DeleteService.class).to(DeleteServiceImpl.class);
		Logger.debug("Configure guice binding done.");
	}
}
