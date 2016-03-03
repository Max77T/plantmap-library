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

import play.mvc.Result;
import views.html.about;
import views.html.error;

/**
 * Common actions for Plantmap application.
 */
public class Application extends Base {
    public Result index() {
    	return redirect(controllers.routes.Search.index());
    }

    public Result about() {
        return ok(about.render());
    }
    
    public Result error() {
        return ok(error.render(""));
    }
}
