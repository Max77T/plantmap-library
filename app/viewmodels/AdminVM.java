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
package viewmodels;

import java.util.List;
import java.util.stream.Collectors;

import models.users.Role;
import models.users.UserRepository;

public class AdminVM {
	private final String login;
	private final String email;
	private final String role;
	private final int level;
	private final int id;

	public AdminVM(String login, String email, String role, int level, int id){
		this.login = login;
		this.email = email;
		this.role = role;
		this.level = level;
		this.id = id;
	}

	public static List<AdminVM> usersToAdminVM(UserRepository userRepo){
		return userRepo.getAllUser().stream()
				.map(user -> {
					String role = "User";
					int level = 30;
					
					if(user.hasRole(Role.Admin)){
						role = "Admin";
						level = 10;
					}else if(user.hasRole(Role.Geomatician)){
						role = "Geomatician";
						level = 20;
					}
					
					return new AdminVM(user.getLogin(), user.getEmail(), role, level, user.getId());
				})
				.sorted((a1, a2) -> a1.level - a2.level)
				.collect(Collectors.toList());
	}

	public String getLogin() {
		return login;
	}

	public String getEmail() {
		return email;
	}

	public String getRole() {
		return role;
	}

	public int getLevel() {
		return level;
	}

	public int getId() {
		return id;
	}
}
