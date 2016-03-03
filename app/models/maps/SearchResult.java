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
package models.maps;

import java.util.List;
import java.util.Objects;

public class SearchResult {
	private final List<Map> maps;
	private final long nbHits;
	private final long timeTaken;
	
	public SearchResult(List<Map> maps, long nbHits, long timeTaken) {
		Objects.requireNonNull(maps);
		
		this.maps = maps;
		this.nbHits = nbHits;
		this.timeTaken = timeTaken;
	}

	public List<Map> getMaps() {
		if(maps.isEmpty()){
		}
		return maps;
	}

	public long getNbHits() {
		return nbHits;
	}

	public long getTimeTaken() {
		return timeTaken;
	}
}
