package fr.fredos.dvdtheque.common.tmdb.model;

public class Cast extends AbstractCredit{
	private String cast_id;
	private String character;
	private String profile_path;
	public String getCast_id() {
		return cast_id;
	}
	public void setCast_id(String cast_id) {
		this.cast_id = cast_id;
	}
	public String getCharacter() {
		return character;
	}
	public void setCharacter(String character) {
		this.character = character;
	}
	public String getProfile_path() {
		return profile_path;
	}
	public void setProfile_path(String profile_path) {
		this.profile_path = profile_path;
	}
	@Override
	public String toString() {
		return "Cast [cast_id=" + cast_id + ", character=" + character + ", profile_path=" + profile_path + "]";
	}
}
