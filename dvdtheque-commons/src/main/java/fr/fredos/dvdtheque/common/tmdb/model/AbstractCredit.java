package fr.fredos.dvdtheque.common.tmdb.model;

public abstract class AbstractCredit {
	protected String credit_id;
	protected String name;
	public String getCredit_id() {
		return credit_id;
	}
	public void setCredit_id(String credit_id) {
		this.credit_id = credit_id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	@Override
	public String toString() {
		return "AbstractCredit [credit_id=" + credit_id + ", name=" + name + "]";
	}
}
