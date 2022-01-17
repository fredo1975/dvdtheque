package fr.fredos.dvdtheque.common.tmdb.model;

public class Crew extends AbstractCredit{
	private String job;
	private String department;
	public String getJob() {
		return job;
	}
	public void setJob(String job) {
		this.job = job;
	}
	public String getDepartment() {
		return department;
	}
	public void setDepartment(String department) {
		this.department = department;
	}
	@Override
	public String toString() {
		return "Crew [credit_id=" + credit_id + ", name=" + name + ", job=" + job + ", department=" + department + "]";
	}
}
