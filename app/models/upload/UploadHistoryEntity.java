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

import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.avaje.ebean.Model;

import play.data.validation.Constraints;

@Entity
@Table(name="plantmap_upload_history")
public class UploadHistoryEntity extends Model {
	@Id
    public Integer id;

	@Constraints.Required
    public String name;
	
	@ManyToOne
    public String login;
	
	@Constraints.Required
    public Long size;
	
	@Constraints.Required
    public Date date;
    
	public static Finder<Integer, UploadHistoryEntity> find = new Finder<Integer,UploadHistoryEntity>(UploadHistoryEntity.class);
    
	public static List<UploadHistoryEntity> getRecentHistory() {
		return find.setMaxRows(10).orderBy().desc("date").findList();
    }
	
	/**
	 * Create an entry in DB.
	 * @return The UploadHistory object created.
	 */
	public static void create(String login, String name, Long size, Date date) {
		UploadHistoryEntity uploadHitory = new UploadHistoryEntity();

		uploadHitory.name = name;
		uploadHitory.login = login;
		uploadHitory.size = size;
		uploadHitory.date = date;

		uploadHitory.save();
	}
}