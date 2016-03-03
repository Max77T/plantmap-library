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

import com.google.inject.Inject;

import play.mvc.Result;
import services.ImageService;
import services.ThumbnailSize;


public class Image extends Base {
	private final ImageService imageService;
	
	@Inject
	public Image(ImageService imageService){
		this.imageService = imageService;
	}
	
	/**
	 * Send a reduced version of the file.
	 */
    public Result getThumbnail(String size, String file) throws IOException {
    	ThumbnailSize thumbSize = ThumbnailSize.valueOf(size.toUpperCase());
    	if(file == null || file.isEmpty()){
			return badRequest();
		}
    	
    	//TODO: Check file pattern
    	return ok(imageService.getThumbnail(thumbSize, file));
    }
}
