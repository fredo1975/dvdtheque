package fr.fredos.dvdtheque.batch.film.listener;

import java.io.FileOutputStream;
import java.io.IOException;

import javax.batch.runtime.BatchStatus;

import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;

public class ExportFilmsJobListener implements JobExecutionListener {
	protected Logger logger = LoggerFactory.getLogger(ExportFilmsJobListener.class);
	private final SXSSFWorkbook workbook;
    private final FileOutputStream outputStream;
    
    public ExportFilmsJobListener(SXSSFWorkbook workbook,FileOutputStream outputStream) throws IOException {
        this.workbook = workbook;
        this.outputStream = outputStream;
    }
	@Override
	public void beforeJob(JobExecution jobExecution) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void afterJob(JobExecution jobExecution) {
		BatchStatus batchStatus = jobExecution.getStatus().getBatchStatus();
        if (batchStatus.equals(BatchStatus.COMPLETED)) {
            try {
                this.workbook.write(outputStream);
                outputStream.close();
                workbook.dispose();
            } catch (IOException e) {
            	logger.error(e.getMessage(), e);
            }
        }
	}

}
