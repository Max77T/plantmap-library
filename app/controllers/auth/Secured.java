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
package controllers.auth;

import controllers.routes;
import play.mvc.Http.Context;
import play.mvc.Result;
import play.mvc.Security.Authenticator;

/**
 * Action annotated with this class require authenticated users.
 */
public class Secured extends Authenticator {
    @Override
    public String getUsername(Context ctx) {
    	String login = ctx.session().get("login");
    	if (login == null){
    		return null;
    	}
        return login;
    }

    @Override
    public Result onUnauthorized(Context ctx) {
        return redirect(routes.Authentication.login());
    }
}