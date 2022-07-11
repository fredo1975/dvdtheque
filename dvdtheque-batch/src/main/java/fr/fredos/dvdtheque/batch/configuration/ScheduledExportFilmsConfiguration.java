package fr.fredos.dvdtheque.batch.configuration;

import java.util.HashMap;
import java.util.Map;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
public class ScheduledExportFilmsConfiguration {

	@Autowired
	JobLauncher jobLauncher;
	@Autowired
	@Qualifier(value = "runExportFilmsJob")
	Job						job;
	
	@Scheduled(cron = "${batch.export.cron}")
	public void exportFilmsJob() {
    	Map<String, JobParameter> jobConfigMap = new HashMap<>();
        jobConfigMap.put("time", new JobParameter(System.currentTimeMillis()));
        JobParameters parameters = new JobParameters(jobConfigMap);
        try {
            jobLauncher.run(job, parameters);
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
        }
	}
	
}
