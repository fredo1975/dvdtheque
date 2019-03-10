package fr.fredos.dvdtheque.service.enums;

public enum TypePersonne {
	REALISATEUR("Realisateur"),ACTEUR("Acteur");
	
	TypePersonne(String type){
		this.type = type;
	}
	private final String type;
	public String getType() {
		return type;
	}
	public static TypePersonne getByType(String type) {
	    for(TypePersonne e : values()) {
	        if(e.type.equalsIgnoreCase(type)) 
	        	return e;
	    }
	    return null;
	}
}
