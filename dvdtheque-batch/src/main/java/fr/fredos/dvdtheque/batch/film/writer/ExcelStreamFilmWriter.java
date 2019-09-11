package fr.fredos.dvdtheque.batch.film.writer;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemStreamWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.WritableResource;

import fr.fredos.dvdtheque.dao.model.object.Film;
import fr.fredos.dvdtheque.service.excel.ExcelFilmHandler;

@Configuration
public class ExcelStreamFilmWriter implements ItemStreamWriter<Film> {
	protected Logger logger = LoggerFactory.getLogger(ExcelStreamFilmWriter.class);
	@Autowired
    protected Environment environment;
	@Autowired
    private ExcelFilmHandler excelFilmHandler;
    public static final String EXCEL_DVD_FILE_NAME_EXPORT = "excel.dvd.file.name.export";
    public static final String EXCEL_DVD_FILE_PATH_EXPORT = "excel.dvd.file.path.export";
	private SXSSFWorkbook workBook;
    private WritableResource resource;
    
	public ExcelStreamFilmWriter() {
		
	}
	
	@Override
	public void open(ExecutionContext executionContext) throws ItemStreamException {
		String fileName = environment.getRequiredProperty(EXCEL_DVD_FILE_PATH_EXPORT)+environment.getRequiredProperty(EXCEL_DVD_FILE_NAME_EXPORT);
    	this.resource = new FileSystemResource(fileName);
    	this.workBook = this.excelFilmHandler.getWorkBook();
		this.excelFilmHandler.initSheet(this.workBook);
	}

	@Override
	public void update(ExecutionContext executionContext) throws ItemStreamException {
		
	}

	@Override
	public void close() throws ItemStreamException {
		if (this.workBook == null) {
	        return;
	    }
	    try (BufferedOutputStream bos = new BufferedOutputStream(resource.getOutputStream())) {
	    	this.workBook.write(bos);
	        bos.flush();
	        this.workBook.close();
	    } catch (IOException ex) {
	        throw new ItemStreamException("Error writing to output file", ex);
	    }
	    this.excelFilmHandler.setRow(null);
	}
	
	@Override
	public void write(List<? extends Film> items) throws Exception {
		for(Film film : items) {
			this.excelFilmHandler.writeBook(film);
		}
	}
}
