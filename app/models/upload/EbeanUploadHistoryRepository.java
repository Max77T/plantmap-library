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
import java.util.stream.Collectors;

/**
 * Ebean implementation of the UploadHistoryRepository interface.
 */
public class EbeanUploadHistoryRepository implements UploadHistoryRepository {

	@Override
	public List<UploadHistoryItem> getRecentHistory() {		
		return UploadHistoryEntity.getRecentHistory()
				.stream()
				.map(h -> new UploadHistoryItem(h.name, h.login, h.date, h.size))
				.collect(Collectors.toList());
	}

	@Override
	public void addUploadHistory(String login, String name, Long size, Date date) {
		UploadHistoryEntity.create(login, name, size, date);
	}
}