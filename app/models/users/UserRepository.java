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

import java.util.List;
import java.util.Optional;

public interface UserRepository {
	/**
	 * Authenticate a user against the data in DB.
	 * @return The User object if login / password match, null otherwise.
	 */
	public Optional<User> authenticate(String login, String password);
	
	/**
	 * Check that the user corresponding to login has the role. 
	 * @return The User object if login / password match, null otherwise.
	 */
	public boolean isUserInRole(String login, String role);

	/**
	 * Create a user in the DB.
	 */
	public User create(String login, String password, String email, List<Role> roles);
	
	/**
	 * Get user from DB
	 */
	public Optional<User> getUser(String login);
	
	/**
	 * Get all users from DB
	 */
	public List<User> getAllUser();
	
	/**
	 * Delete User by Id
	 */
	public boolean deleteUser(int id);
}
