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

import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Result;
import play.mvc.Security.Authenticated;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Inject;

import controllers.auth.Secured;
import models.DeleteRequest;
import models.users.Role;
import services.filetransfer.DeleteService;

/**
 * Controller which handles map deletion actions.
 */
public class Delete extends Base {
	private final DeleteService service;
	
	@Inject
	public Delete(DeleteService service){
		this.service = service;
	}
	
	/**
	 * Delete the maps from the library.
	 * This actions can be called only by user with the Admin role.
	 */
	@Authenticated(Secured.class)
	@controllers.actions.Role(requiredRole = Role.Admin)
	@BodyParser.Of(BodyParser.Json.class)
	public Result delete() throws IOException{
		JsonNode json = request().body().asJson();
		DeleteRequest dr = Json.fromJson(json, DeleteRequest.class);
		if(dr == null){
			return badRequest();
		}
		boolean result = service.deleteMaps(dr);
		if(result){
			return noContent();
		}
		return notFound();
	}
}
