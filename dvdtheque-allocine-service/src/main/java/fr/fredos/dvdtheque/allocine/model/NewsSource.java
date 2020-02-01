package fr.fredos.dvdtheque.allocine.model;

public class NewsSource extends AlloCineBase{
	private String name;
	private String href;
	public Integer getCode() {
		return code;
	}
	public String getName() {
		return name;
	}
	public String getHref() {
		return href;
	}
	@Override
	public String toString() {
		return "NewsSource [code=" + code + ", name=" + name + ", href=" + href + "]";
	}
}
