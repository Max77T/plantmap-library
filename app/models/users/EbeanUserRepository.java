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
import java.util.stream.Collectors;


/**
 * Ebean implementation of the UserRepository interface.
 */
public class EbeanUserRepository implements UserRepository {
	@Override
	public Optional<User> authenticate(String login, String password) {
		return UserEntity.authenticate(login, password)
				.map(UserEntity::toUser);
	}

	@Override
	public boolean isUserInRole(String login, String role) {
		return UserEntity.isInRole(login, role);
	}

	@Override
	public User create(String login, String password, String email, List<Role> roles) {
		return UserEntity.create(login, password, email, roles).toUser();
	}

	@Override
	public Optional<User> getUser(String login) {
		return UserEntity.getUser(login).map(UserEntity::toUser);
	}

	@Override
	public List<User> getAllUser() {
		return UserEntity.getAllUser().stream()
				.map(UserEntity::toUser)
				.collect(Collectors.toList());
	}

	@Override
	public boolean deleteUser(int id) {
		return UserEntity.deleteUser(id);
	}
}
