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

import java.io.IOException;
import java.util.Optional;

import models.maps.MapRepository;
import models.maps.Map;

import com.google.inject.Inject;

import play.i18n.Messages;
import play.mvc.Result;
import viewmodels.PermalinkVM;
import views.html.permalink;
import views.html.error;

/**
 * Controller which handles permalink actions.
 * Permalink page provides details about a map (big size + all metadata).
 */
public class Permalink extends Base {
	private final MapRepository mapRepository;
	
	@Inject
	public Permalink(MapRepository mapRepository){
		this.mapRepository = mapRepository;
	}
	
	public Result getId(String id) throws IOException {
		Optional<Map> result = mapRepository.getMap(id);
		if(!result.isPresent()) {
			return badRequest(error.render(Messages.get("controllers.error.illegalAccess")));
		}
		
		Map map = result.get();
		boolean hasRight = getUserContext().isAuthenticated() || !map.getIsPrivate();
		
		if(!hasRight){
			return notFound(error.render(Messages.get("controllers.error.illegalAccess")));
		}
		return ok(permalink.render(new PermalinkVM(map)));
	}
}
