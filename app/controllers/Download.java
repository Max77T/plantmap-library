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

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Inject;

import controllers.auth.Secured;
import models.DownloadRequest;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Result;
import play.mvc.Security.Authenticated;
import services.filetransfer.DownloadService;

/**
 * Controller which handles map download actions.
 * Maps download is done in two steps:
 * - A first action prepares an archive with required maps and send back a unique URL.
 * - A second action sends back the archive.
 */
public class Download extends Base {
	/**
	 * Class used as serializer for JSON response for first download step.
	 */
	public static class PrepareDownloadResponse {
		private final String fileUrl;

		public PrepareDownloadResponse(String fileUrl) {
			this.fileUrl = fileUrl;
		}

		public String getFileUrl() {
			return fileUrl;
		}
	}
	
	private final DownloadService service;

	@Inject
	public Download(DownloadService service){
		this.service = service;
	}
	
	/**
	 * First step of the download process.
	 * Create an archive with maps and send back an URL for the archive.
	 */
	@Authenticated(Secured.class)
	@BodyParser.Of(BodyParser.Json.class)
	public Result prepareDownload() throws IOException{
		JsonNode json = request().body().asJson();			
		DownloadRequest dr = Json.fromJson(json, DownloadRequest.class);
			
		if(dr == null){
			return badRequest();
		}

		Optional<String> zipUrlOpt = service.createDownloadZip(dr);
		if(zipUrlOpt.isPresent()){
			String fullUrl = routes.Download.download(zipUrlOpt.get()).absoluteURL(request());
			return ok(Json.toJson(new PrepareDownloadResponse(fullUrl)));
		}
		return notFound();
	}
	
	/**
	 * Second step of the download process.
	 * Send the archive prepared in the first step.
	 */
	@Authenticated(Secured.class)
	public Result download(String file) throws IOException{
		if(file == null || file.isEmpty()){
			return badRequest();
		}

		Optional<InputStream> inputStreamOpt = service.getZip(file);
		if(inputStreamOpt.isPresent()){
			return ok(inputStreamOpt.get());
		}
		return notFound();
	}
}
