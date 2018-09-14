package fr.fredos.dvdtheque.web.enums;

public enum NAVLOGIN {
	LOGIN("login.xhtml"),NULL("#");
	
	private String nav;
	
	private NAVLOGIN(String nav) {
		this.nav = nav;
	}
}
