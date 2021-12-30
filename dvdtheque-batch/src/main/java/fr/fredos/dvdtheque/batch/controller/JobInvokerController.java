package fr.fredos.dvdtheque.batch.controller;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class JobInvokerController {
	@Autowired
    JobLauncher jobLauncher;
 
    @Autowired
    Job exportFilmsJob;
 
    @RequestMapping("/invokejob")
    public String handle() throws Exception {
 
            JobParameters jobParameters = new JobParametersBuilder().addLong("time", System.currentTimeMillis())
                    .toJobParameters();
            jobLauncher.run(exportFilmsJob, jobParameters);
 
        return "Batch job has been invoked";
    }
}
