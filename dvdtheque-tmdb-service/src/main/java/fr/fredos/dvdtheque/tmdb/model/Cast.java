package fr.fredos.dvdtheque.tmdb.model;

public class Cast extends AbstractCredit{
	private String cast_id;
	private String character;
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
	@Override
	public String toString() {
		return "Cast [credit_id=" + credit_id + ", name=" + name + ", cast_id=" + cast_id + ", character=" + character + "]";
	}
}
