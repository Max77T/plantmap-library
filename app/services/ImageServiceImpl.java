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
package services;

import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.Objects;

import javax.imageio.ImageIO;

import org.imgscalr.Scalr;
import org.imgscalr.Scalr.Method;
import org.imgscalr.Scalr.Mode;

import com.google.inject.Inject;

import conf.UploadConfig;
import play.Logger;

public class ImageServiceImpl implements ImageService {
	private final UploadConfig uploadConfig;
	
	@Inject
	public ImageServiceImpl(UploadConfig uploadConfig){
		Objects.requireNonNull(uploadConfig);
		this.uploadConfig = uploadConfig;
	}
	
	@Override
	public InputStream getThumbnail(ThumbnailSize size, String pattern) throws IOException {
		Objects.requireNonNull(pattern);
		Logger.debug("getting " + size + " thumbnail file " + pattern);
    	
    	Path thumbnailPath = uploadConfig.getLibraryThumbnailDir()
    			.resolve(size.toString())
    			.resolve(pattern);
    	try{
    		InputStream thumbStream = Files.newInputStream(thumbnailPath);
    		Logger.debug("found " + size + " thumbnail " + thumbnailPath);
    		return thumbStream;
    	}catch(FileNotFoundException | NoSuchFileException ex){
    		// Thumbnail not found, create it
    		Path mapPath = uploadConfig.getLibraryStoreDir().resolve(pattern);
    		createThumbnailForMap(size, mapPath, thumbnailPath);
    	}
    	return Files.newInputStream(thumbnailPath);
	}
	
    private void createThumbnailForMap(ThumbnailSize size, Path mapPath, Path thumbnailPath) throws IOException{
    	Logger.info("Creating " + size + " thumbnail for " + mapPath);
    	try(InputStream mapInputStream = Files.newInputStream(mapPath)){
	    	BufferedImage image = ImageIO.read(mapInputStream);
	        BufferedImage small = Scalr.resize(image,
	                                           Method.QUALITY,
	                                           Mode.AUTOMATIC,
	                                           size.getWidth(), size.getHeight(),
	                                           Scalr.OP_ANTIALIAS);
	     
	        thumbnailPath.getParent().toFile().mkdirs();
	        try(OutputStream smallOutputStream = Files.newOutputStream(thumbnailPath)){
	        	String extension = getExtension(thumbnailPath.toFile().getName());
	        	ImageIO.write(small, extension, smallOutputStream);
	        }
    	}
    }
    
    private String getExtension(String fileName){
    	int i = fileName.lastIndexOf('.');
    	if (i > 0) {
    	    return fileName.substring(i+1);
    	}
    	throw new IllegalArgumentException("No extension on this filename: " + fileName);
    }
}
