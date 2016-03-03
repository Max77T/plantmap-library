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

import controllers.actions.UserContext;
import controllers.actions.UserContextAction;
import play.mvc.Controller;
import play.mvc.With;

/**
 * Base controller for Plantmap ones. Provides a userContext
 * for all actions in child classes.
 */
@With(UserContextAction.class)
public abstract class Base extends Controller {
	/**
	 * Return the current user context.
	 */
	final protected UserContext getUserContext(){
		return (UserContext) ctx().args.get("userContext");
	}
}
