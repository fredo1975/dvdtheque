package fr.fredos.dvdtheque.dao.model.object;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
@Entity
@Table(name = "USERROLES")
public class UserRoles implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
	@JoinColumn(name = "USER_ID")
	@ManyToOne
    private User user;
	@JoinColumn(name = "ROLE_ID")
	@ManyToOne
    private Role role;
	public UserRoles() {
		super();
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public Role getRole() {
		return role;
	}
	public void setRole(Role role) {
		this.role = role;
	}
	@Override
    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
		 if ( (other == null ) ) return false;
		 if ( !(other instanceof User) ) return false;
		 UserRoles castOther = ( UserRoles ) other; 
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
    	return "UserRoles --> user="+user.getEmail()+" role="+role.getName();
    }
}
