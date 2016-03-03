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
package models.users;

import java.util.List;
import java.util.Optional;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import com.avaje.ebean.Model;

import play.data.validation.Constraints;

@Entity
@Table(name="plantmap_role")
public class RoleEntity extends Model {
    @Id
    public Integer id;

    @Constraints.Required
    public String name;
    
    @ManyToMany(mappedBy = "roles")
    public List<UserEntity> users;
    
    static final Finder<Integer, RoleEntity> find = new Finder<Integer,RoleEntity>(RoleEntity.class);

    public RoleEntity(){}
    
    public RoleEntity(String roleName){
    	this.name = roleName;
    }
    
    static Optional<RoleEntity> getByName(String name){
    	return Optional.ofNullable(find.where().eq("name", name).findUnique());
    }
    
    @Override
    public boolean equals(Object obj) {
    	if(obj == this){
    		return true;
    	}
    	if(obj == null){
    		return false;
    	}
    	if(getClass() != obj.getClass()){
    		return false;
    	}
    	
    	RoleEntity other = (RoleEntity) obj;
		return name.equals(other.name);
    }
}