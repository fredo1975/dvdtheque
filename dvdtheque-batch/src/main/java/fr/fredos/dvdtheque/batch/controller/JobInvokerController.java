package fr.fredos.dvdtheque.batch.controller;

import javax.annotation.security.RolesAllowed;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dvdtheque-batch-service/invokejob")
public class JobInvokerController {
	@Autowired
	JobLauncher jobLauncher;

	@Autowired
	@Qualifier("runExportFilmsJob")
	Job runExportFilmsJob;

	@Autowired
	@Qualifier("importFilmsJob")
	Job importFilmsJob;

	@RolesAllowed("user")
	@RequestMapping("/exportFilmsJob")
	public String handleExportFilmsJob() throws Exception {
		JobParameters jobParameters = new JobParametersBuilder().addLong("time", System.currentTimeMillis())
				.toJobParameters();
		jobLauncher.run(runExportFilmsJob, jobParameters);
		return "Batch exportFilmsJob has been invoked";
	}

	@RolesAllowed("user")
	@RequestMapping("/importFilmsJob")
	@PostMapping("/importFilmsJob")
	public String handleImportFilmsJob(@RequestBody String filePath) throws Exception {
		JobParameters jobParameters = new JobParametersBuilder().addLong("time", System.currentTimeMillis())
				.addString("INPUT_FILE_PATH", filePath).toJobParameters();
		jobLauncher.run(importFilmsJob, jobParameters);
		return "Batch importFilmsJob has been invoked";
	}
}
