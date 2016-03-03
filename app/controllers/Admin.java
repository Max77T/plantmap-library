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

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Inject;

import controllers.auth.Secured;
import models.users.Role;
import models.users.UserCreationRequest;
import models.users.UserRepository;
import play.libs.Json;
import play.mvc.Result;
import play.mvc.Security.Authenticated;
import viewmodels.AdminVM;
import views.html.admin;

/**
 * Contains actions for the admin page.
 */
public class Admin extends Base {	
	private final UserRepository userRepository;

	@Inject
	public Admin(UserRepository userRepository){
		this.userRepository = userRepository;
	}
	
	@Authenticated(Secured.class)
	@controllers.actions.Role(requiredRole = Role.Admin)
	public Result index() {
		return ok(admin.render(AdminVM.usersToAdminVM(userRepository)));
	}

	/**
	 * Delete the user with id "id" from DB.
	 * This actions can be called only by user with the Admin role.
	 */
	@Authenticated(Secured.class)
	@controllers.actions.Role(requiredRole = Role.Admin)
	public Result delete(int id){
		userRepository.deleteUser(id);
		return ok(admin.render(AdminVM.usersToAdminVM(userRepository)));
	}
	
	/**
	 * Create a new user in DB.
	 * This actions can be called only by user with the Admin role.
	 */
	@Authenticated(Secured.class)
	@controllers.actions.Role(requiredRole = Role.Admin)
	public Result add(){
		JsonNode json = request().body().asJson();
		UserCreationRequest user = Json.fromJson(json, UserCreationRequest.class);
		
		List<Role> roles = new ArrayList<>();
		switch (user.getRole()) {
			case "Admin":
				roles.add(Role.Admin);
				roles.add(Role.Geomatician);
				roles.add(Role.User);
				break;
			case "Geomatician":
				roles.add(Role.Geomatician);
				roles.add(Role.User);
				break;
			case "User":
				roles.add(Role.User);
				break;
		}
		
		userRepository.create(user.getLogin(), user.getPassword(), user.getEmail(), roles);
		return ok(admin.render(AdminVM.usersToAdminVM(userRepository)));
	}
}
