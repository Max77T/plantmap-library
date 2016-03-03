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
package controllers.actions;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;

import com.google.inject.Inject;

import conf.SecurityConfig;
import models.users.User;
import models.users.UserRepository;
import play.Logger;
import play.cache.CacheApi;
import play.cache.NamedCache;
import play.libs.F.Promise;
import play.mvc.Action;
import play.mvc.Http.Context;
import play.mvc.Http.Session;
import play.mvc.Result;

/**
 * This action provides information on the current logged user to next actions.
 * If no user is connected, an anonymous context is provided with no login and no rights.
 */
public class UserContextAction extends Action.Simple {
	private static final int USER_CACHE_DURATION = 10 * 60; // 10 minutes
	
	private static final UserContext anonymousContext = new UserContext(){
		@Override public String getLogin() { return "";	}
		@Override public String getEmail() { return ""; }
		@Override public boolean isAuthenticated() { return false; }
		@Override public boolean hasRole(models.users.Role role) { return false; }
	};
	
	private final UserRepository userRepository;
	private final CacheApi userCache;
	private final SecurityConfig securityConfig;
	
	@Inject
	public UserContextAction(UserRepository userRepository, 
			@NamedCache("user-cache") CacheApi userCache,
			SecurityConfig securityConfig){
		this.userRepository = userRepository;
		this.userCache = userCache;
		this.securityConfig = securityConfig;
	}

	@Override
	public Promise<Result> call(Context ctx) throws Throwable {
		Session session = ctx.session();
		String login = session.get("login");
		long currentT = new Date().getTime();
		if(login == null || isSessionExpired(session, currentT)){
			session.clear();
			ctx.args.put("userContext", anonymousContext);
			return delegate.call(ctx);
		}

        // update time in session
        session.put("userTime", Long.toString(currentT));
		
		UserContext userCtx = getUserContext(login);
		ctx.args.put("userContext", userCtx);
		return delegate.call(ctx);
	}

	/**
	 * Try to found user information associated to login.
	 * Search in the cache first, then in database.
	 * If nothing is found, an anonymous context is returned.
	 * @return The userContext associated to login.
	 */
	private UserContext getUserContext(String login) {
		UserContext userCtx = userCache.getOrElse(login, () -> {
			Logger.debug("User " + login + " not found in cache, getting it from DB");
			Optional<User> userOpt = userRepository.getUser(login);
			if(!userOpt.isPresent()){
				return anonymousContext;
			}
			User user = userOpt.get();
			return new UserContextImpl(user.getLogin(), user.getEmail(), user.getRoles());
		}, USER_CACHE_DURATION);
		return userCtx;
	}
	
	private boolean isSessionExpired(Session session, long currentTime){
        String previousTick = session.get("userTime");
        long previousT = Long.valueOf(previousTick);
        
        return currentTime - previousT > securityConfig.getSessionTimeout();
	}
	
	private static class UserContextImpl implements UserContext {
		private final String login;
		private final String email;
		private final HashSet<models.users.Role> roles;
		
		UserContextImpl(String login, String email, Collection<models.users.Role> set) {
			Objects.requireNonNull(login);
			Objects.requireNonNull(email);
			Objects.requireNonNull(set);
			
			this.login = login;
			this.email = email;
			this.roles = new HashSet<>(set);
		}

		@Override
		public String getLogin() {
			return login;
		}

		@Override
		public String getEmail() {
			return email;
		}

		@Override
		public boolean isAuthenticated() {
			return true;
		}

		@Override
		public boolean hasRole(models.users.Role role) {
			return roles.contains(role);
		}
	}
}
