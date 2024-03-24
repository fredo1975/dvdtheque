package fr.fredos.dvdtheque.batch.csv.format.launcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Qualifier;
//@Component
public class ImportFilmsLauncher {
	private static final Logger LOGGER = LoggerFactory.getLogger(ImportFilmsLauncher.class);
	private final Job job;
    private final JobLauncher jobLauncher;
    
    //@Autowired
    ImportFilmsLauncher(@Qualifier("importFilmsJob") Job job, JobLauncher jobLauncher) {
        this.job = job;
        this.jobLauncher = jobLauncher;
    }

    void launchCsvFileToDatabaseJob() throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
        LOGGER.info("Starting importFilms job");
        //jobLauncher.run(job, newExecution());
        LOGGER.info("Stopping importFilms job");
    }

    private JobParameters newExecution() {
        
        return new JobParameters();
    }
}
