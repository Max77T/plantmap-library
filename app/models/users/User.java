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
package models.users;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class User {
	private final int id;
	private final String login;
	private final String email;
	private final HashSet<Role> roles;

	public User(int id, String login, String email, Collection<Role> roles) {
		this.id = id;
		this.login = login;
		this.email = email;
		this.roles = new HashSet<>(roles);
	}
	
	public int getId() {
		return id;
	}
	
	public String getLogin() {
		return login;
	}
	
	public String getEmail() {
		return email;
	}
	
	public Set<Role> getRoles() {
		return roles;
	}
	
	public boolean hasRole(Role role){
		return roles.contains(role);
	}
}
