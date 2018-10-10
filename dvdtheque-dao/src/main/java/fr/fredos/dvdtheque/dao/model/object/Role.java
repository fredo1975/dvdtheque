package fr.fredos.dvdtheque.dao.model.object;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@Entity
@Table(name = "ROLES")
public class Role implements Serializable {
	@Transient
	protected Logger logger = LoggerFactory.getLogger(Role.class);
	@Transient
	private int hashCode = Integer.MIN_VALUE;
	private static final long serialVersionUID = 1L;
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	@Column(name = "NAME")
	@NotNull
	private String name;
	@OneToMany(cascade = CascadeType.ALL, targetEntity=User.class, fetch = FetchType.LAZY)
	private Set<User> users;
	
	public Role() {
		super();
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Set<User> getUsers() {
		return users;
	}
	public void setUsers(Set<User> users) {
		this.users = users;
	}
	@Override
	public boolean equals(Object other) {
        if ( (this == other ) ) return true;
		 if ( (other == null ) ) return false;
		 if ( !(other instanceof Role) ) return false;
		 Role castOther = ( Role ) other; 
		 return ( (this.getId()==castOther.getId()) || ( this.getId()!=null && castOther.getId()!=null && this.getId().equals(castOther.getId()) ) );
    }
	@Override
    public int hashCode() {
        int result = 17;
        result = 37 * result + ( getId() == null ? 0 : this.getId().hashCode() );
        return result;
    }
    @Override
    public String toString(){
    	return "Role [id=" + id + ", name=" + name + ", hashCode=" + hashCode + "]";
    }
}
