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
package models.upload;

import java.text.SimpleDateFormat;
import java.util.Date;


public class UploadHistoryItem {
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	private final String name;
	private final String login;
	private final String date;
	private final Long size;
	
	public UploadHistoryItem(String name, String login, Date date, Long size) {
		this.name = name;
		this.login = login;
		this.date = dateFormat.format(date);
		this.size = size;
	}
	
	public String getName() {
		return name;
	}
	
	public String getLogin() {
		return login;
	}

	public String getDate() {
		return  date;
	}
	
	public Long getSizeInMo() {
		return size / (1024 * 1024);
	}
}
