package fr.fredos.dvdtheque.batch.configuration;

import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import fr.fredos.dvdtheque.batch.film.listener.ExportFilmsJobListener;
import fr.fredos.dvdtheque.batch.film.writer.ExcelFilmWriter;
import fr.fredos.dvdtheque.dao.model.object.Film;
import fr.fredos.dvdtheque.service.IFilmService;

@Configuration
@EnableBatchProcessing
public class BatchExportFilmsConfiguration {
	protected Logger logger = LoggerFactory.getLogger(BatchExportFilmsConfiguration.class);
	@Autowired
	protected IFilmService filmService;
	@Autowired
	protected JobBuilderFactory jobBuilderFactory;
    @Autowired
    protected StepBuilderFactory stepBuilderFactory;
    @Autowired
    protected Environment environment;
    public static final String EXCEL_DVD_FILE_NAME_EXPORT = "excel.dvd.file.name.export";
    @Bean
	public Job exportFilmsJob() throws IOException {
    	SXSSFWorkbook workBook = new SXSSFWorkbook(1);
    	FileOutputStream outputStream = new FileOutputStream(environment.getRequiredProperty(EXCEL_DVD_FILE_NAME_EXPORT));
		return jobBuilderFactory.get("exportFilms")
				.incrementer(new RunIdIncrementer())
				.start(exportFilmsStep(workBook))
				.listener(new ExportFilmsJobListener(workBook,outputStream))
				.build();
	}
    
    @Bean
    protected ListItemReader<Film> dbFilmReader() {
    	return new ListItemReader<>(filmService.findAllFilms());
    }
    @Bean
    protected ExcelFilmWriter excelFilmWriter(SXSSFWorkbook workBook) {
    	return new ExcelFilmWriter(workBook);
    }
    @Bean
    protected Step exportFilmsStep(SXSSFWorkbook workBook) {
        return stepBuilderFactory.get("exportFilms")
                .<Film, Film>chunk(500).reader(dbFilmReader())
                .writer(excelFilmWriter(workBook))
                .build();
    }
}
