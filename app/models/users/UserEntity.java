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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import org.mindrot.jbcrypt.BCrypt;

import com.avaje.ebean.Model;

import play.Logger;
import play.data.validation.Constraints;


@Entity
@Table(name="plantmap_user")
public class UserEntity extends Model {
	@Id
	public Integer id;

	@Column(unique=true)
	@Constraints.Required
	public String login;

	/**
	 * Passwords are encrypted with BCrypt and the jBCrypt library.
	 * @see <a href="http://www.mindrot.org/projects/jBCrypt/">mindrot.org</a> for more information.
	 */
	@Constraints.Required
	public String password;

	@Constraints.Required
	public String email;

	@ManyToMany(cascade=CascadeType.ALL)
	@JoinTable(name="plantmap_user_role")
	public List<RoleEntity> roles = new ArrayList<>();

	public static final Finder<Integer, UserEntity> find = new Finder<Integer,UserEntity>(UserEntity.class);

	/**
	 * Authenticate a user against the data in BDD.
	 * @return The User object if login / password match, nothing otherwise.
	 */
	public static Optional<UserEntity> authenticate(String login, String password) {
		Logger.debug("Authenticate user with login " + login);

		UserEntity user = UserEntity.find.where().eq("login", login).findUnique();
		if (user != null && BCrypt.checkpw(password, user.password)) {
			return Optional.of(user);
		}
		return Optional.empty();
	}

	static boolean isInRole(String login, String role) {
		UserEntity user = UserEntity.find.where().eq("login", login).findUnique();
		return user != null && 
				user.roles.stream().anyMatch(r -> r.name.equals(role));
	}

	/**
	 * Create a user in DB.
	 * @return The User object created.
	 */
	static UserEntity create(String login, String password, String email, List<Role> roles) {
		Objects.requireNonNull(login);
		Objects.requireNonNull(password);
		Objects.requireNonNull(email);
		Objects.requireNonNull(roles);

		if(login.isEmpty()){
			throw new IllegalArgumentException("Login must not be empty");
		}
		
		List<RoleEntity> roleEntities = roles.stream()
				.map(r -> r.toString())
				.map(RoleEntity::getByName)
				.map(Optional::get)
				.collect(Collectors.toList());

		UserEntity user = new UserEntity();

		user.login = login;
		user.password = BCrypt.hashpw(password, BCrypt.gensalt());
		user.email = email;
		user.roles = roleEntities;
		
		user.save();
		return user;
	}

	public static Optional<UserEntity> getUser(String login) {
		if(login == null){
			return Optional.empty();
		}

		UserEntity user = UserEntity.find.where().eq("login", login).findUnique();
		return Optional.ofNullable(user);
	}

	public static List<UserEntity> getAllUser(){
		return UserEntity.find.all();
	}

	public static boolean deleteUser(int id){
		UserEntity us = UserEntity.find.byId(id);
		if(us == null){
			return false;
		}
		
		us.delete();
		return true;
	}
	
	User toUser(){
		return new User(
				id, 
				login, 
				email, 
				roles.stream().map(r -> Role.valueOf(r.name)).collect(Collectors.toSet())
			);
	}
}