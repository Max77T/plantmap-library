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

import java.util.Date;

import models.users.UserRepository;
import play.Play;
import play.data.Form;
import play.data.validation.Constraints.Required;
import play.mvc.Result;
import views.html.login;
import play.i18n.Messages;

/**
 * Controller which handles authentication actions.
 */
public class Authentication extends Base {
    public Result login() {
        return ok(login.render(Form.form(Login.class)));
    }
    
    /**
     * Invalidate the session of the user.
     */
    public Result logout() {
    	session().clear();
        flash("success", Messages.get("controllers.authentication.logout.success"));
        return redirect(routes.Search.index());
    }
    
    /**
     * POST destination of the login form.
	 * Create a session if the user provides correct login / password. 
	 * Return badRequest otherwise.
     */
    public Result authenticate() {
    	Form<Login> loginForm = Form.form(Login.class).bindFromRequest();
        if (loginForm.hasErrors()) {
            return badRequest(login.render(loginForm));
        } else {
            session().clear();
            session("login", loginForm.get().login);
            session().put("userTime", Long.toString(new Date().getTime()));
            return redirect(routes.Application.index());
        }
    }
    
    public static class Login {
    	@Required
    	public String login;
    	
    	@Required
        public String password;

    	/**
    	 * This method return an error message if the user is not authenticated.
    	 * This behavior is expected by play framework to validate forms.
    	 * @return An error message if the user is not authenticated, null otherwise.
    	 */
        public String validate() {
        	// Required because play forms doesn't support standard injection.
        	UserRepository userRepository = Play.application().injector().instanceOf(UserRepository.class);
            
        	if(userRepository.authenticate(login, password).isPresent()) {
              return null;
            }
            return Messages.get("controllers.authentication.login.error");
        }
    }
}
