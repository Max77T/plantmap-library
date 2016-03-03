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
package services.filetransfer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;



import play.Logger;

/**
 * Provide functions to manipulate zip files
 */
class ZipUtils {
	/**
	 * @param src Path to the zip file to extract.
	 * @param dst Path to the folder to extract in.
	 * @throws IOException
	 */
	public static void extractZip(Path src, Path dst) throws IOException {
		Objects.requireNonNull(src);
		Objects.requireNonNull(dst);

		try(ZipFile zipFile = new ZipFile(src.toString())){
			Enumeration<? extends ZipEntry> enu = zipFile.entries();
			while (enu.hasMoreElements()) {
				ZipEntry zipEntry = enu.nextElement();
				processZipEntry(zipEntry, dst, zipFile);
			}
		}
	}

	/**
	 * Create a zip file in path 'dst' who contains all file in 'src'
	 * @param src	List of file to zip
	 * @param dst	Path to zip file
	 * @throws IOException
	 */
	public void zipFolder(List<PathLink> src, Path dst) throws IOException {
		Objects.requireNonNull(src);
		Objects.requireNonNull(dst);

		Logger.debug("Destination path : " + dst.toString());
		ZipOutputStream out = new ZipOutputStream(Files.newOutputStream(dst));
		
		String projectFolder = null;
		String fileToZip = null;
		for (PathLink pathLink : src) {			
			InputStream fis = Files.newInputStream(pathLink.getMap());
			projectFolder = pathLink.getMap().getParent().getFileName().toString();
			fileToZip = projectFolder + "/" + pathLink.getMap().getFileName().toString();
			processZipFile(fileToZip, out, fis);
			fis.close();
			fis = Files.newInputStream(pathLink.getMetadata());
			fileToZip = projectFolder + "/" + PathLink.metadataDirectoryName + "/" + pathLink.getMetadata().getFileName().toString();
			processZipFile(fileToZip, out, fis);
			fis.close();
		}
		out.close();
	}
	
	/**
	 * Add each file 'fis' in zip file 'out' which name 'zipFileName'
	 * @param zipFileName 	Name of the file in the zip file
	 * @param out			Zip file
	 * @param fis			file to zip
	 * @throws IOException
	 */
	private void processZipFile(String zipFileName, ZipOutputStream out, InputStream fis) throws IOException{
		ZipEntry zipEntry = new ZipEntry(zipFileName); 	
		out.putNextEntry(zipEntry);						
		byte[] bytes = new byte[1024];
		int length;
		while ((length = fis.read(bytes)) >= 0) {
			out.write(bytes, 0, length);
		}
	}

	private static void processZipEntry(ZipEntry zipEntry, Path dst, ZipFile zipFile) throws IOException{
		String name = zipEntry.getName();
		File file = dst.resolve(name).toFile();
		if (name.endsWith("/")) {
			file.mkdirs();
			return;
		}

		File parent = file.getParentFile();
		if (parent != null) {
			parent.mkdirs();
		}

		try(InputStream is = zipFile.getInputStream(zipEntry);
				FileOutputStream fos = new FileOutputStream(file)){

			byte[] bytes = new byte[1024];
			int length;
			while ((length = is.read(bytes)) >= 0) {
				fos.write(bytes, 0, length);
			}
			fos.flush();
		}
	}
}