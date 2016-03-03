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
import java.nio.file.FileAlreadyExistsException;

import com.google.inject.Inject;

import controllers.auth.Secured;
import models.upload.UploadHistoryRepository;
import models.upload.UploadResult;
import models.users.Role;
import play.i18n.Messages;
import play.mvc.BodyParser;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import play.mvc.Result;
import play.mvc.Security.Authenticated;
import services.filetransfer.UploadService;
import viewmodels.UploadVM;
import views.html.upload;

/**
 * Controller which handles upload actions.
 * Only authenticated users with role Geomatician
 * can upload files to library.
 */
public class Upload extends Base {
	private final UploadService service;
	private final UploadHistoryRepository repository;

	@Inject
	public Upload(UploadService service, UploadHistoryRepository repository){
		this.service = service;
		this.repository = repository;
	}

	@Authenticated(Secured.class)
	@controllers.actions.Role(requiredRole = Role.Geomatician)
	public Result index() {
		return ok(upload.render(new UploadVM(repository.getRecentHistory())));
	}

	@Authenticated(Secured.class)
	@controllers.actions.Role(requiredRole = Role.Geomatician)
	@BodyParser.Of(value = BodyParser.MultipartFormData.class,
		maxLength = 300 * 1024 * 1024)
	public Result upload() throws IOException {
		MultipartFormData body = request().body().asMultipartFormData();
		
		FilePart zip = body.getFile("zipFile");
		boolean isPrivate = body.asFormUrlEncoded().get("isPrivate") != null;
		
		// Validate
		if (zip == null) {
			flash("error", Messages.get("controllers.upload.error.missingFile"));
			return redirect(routes.Upload.index());
		}

		String fileName = zip.getFilename();
		if(!fileName.endsWith(".zip")){
			flash("error", Messages.get("controllers.upload.error.zipFile"));
			return redirect(routes.Upload.index());
		}
		
		try{
			// Process
			UploadResult result = service.processZipFile(zip, session().get("login"), isPrivate);
			flash("success", Messages.get("controllers.upload.success.uploadFile", result.getNbUploaded(), zip.getFilename()));
			return redirect(routes.Upload.index());
		} catch(FileAlreadyExistsException ex) {
			flash("error", Messages.get("controllers.upload.error.alreadyExists", zip.getFilename()));
			return redirect(routes.Upload.index());
		}
	}
}
