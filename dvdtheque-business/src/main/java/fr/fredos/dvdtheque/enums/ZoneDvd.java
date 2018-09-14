package fr.fredos.dvdtheque.enums;

public enum ZoneDvd {
	Zone1(1),Zone2(2);
	
	ZoneDvd(Integer zone){
		id=zone;
	}
	private final Integer id;
	public Integer getId() {
		return id;
	}
	public static ZoneDvd getById(Integer id) {
	    for(ZoneDvd e : values()) {
	        if(e.id.equals(id)) return e;
	    }
	    return null;
	}
}
