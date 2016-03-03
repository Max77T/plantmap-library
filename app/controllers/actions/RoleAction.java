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

import play.Logger;
import play.i18n.Messages;
import play.libs.F.Promise;
import play.mvc.Action;
import play.mvc.Http.Context;
import play.mvc.Result;
import play.mvc.With;

/**
 * Authorize actions for users based on their roles.
 */
@With(UserContextAction.class)
public class RoleAction extends Action<Role> {
	@Override
	public Promise<Result> call(Context ctx) throws Throwable {
		UserContext userCtx = (UserContext) ctx.args.get("userContext");
        if(userCtx == null){
        	return onError(ctx);
        }
        
        models.users.Role role = configuration.requiredRole();
        Logger.info("Checking that user " + userCtx.getLogin() + " has role " + role + " for page " + ctx.request().path());
        if(!userCtx.hasRole(role)){
        	return onError(ctx);
        }
        return delegate.call(ctx);
	}

	private Promise<Result> onError(Context ctx) {
		ctx.flash().put("error", Messages.get("controllers.error.illegalAccess"));
		return Promise.promise(() -> redirect(controllers.routes.Application.error()));
	}
}
