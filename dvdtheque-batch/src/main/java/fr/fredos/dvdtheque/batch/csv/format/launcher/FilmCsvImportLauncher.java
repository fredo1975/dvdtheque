package fr.fredos.dvdtheque.batch.csv.format.launcher;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class FilmCsvImportLauncher {

	public static void main(String[] args) throws JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException, JobParametersInvalidException {
		ClassPathXmlApplicationContext cpt = new ClassPathXmlApplicationContext("classpath:spring-batch-job.xml");
        cpt.start();
        JobLauncher jobLauncher = (JobLauncher) cpt.getBean("jobLauncher");
        Job job = (Job) cpt.getBean("importFilm");
        JobParameters parameter = new JobParametersBuilder()/*.addDate("date", new Date()).addString("input.file", "E:/dev/liste_dvd/ListeDVD.csv")*/.toJobParameters();
        jobLauncher.run(job, parameter);
        cpt.close();
	}

}
