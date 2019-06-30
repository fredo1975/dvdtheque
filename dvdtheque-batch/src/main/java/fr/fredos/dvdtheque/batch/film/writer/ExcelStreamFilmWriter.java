package fr.fredos.dvdtheque.batch.film.writer;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemStreamWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.WritableResource;

import fr.fredos.dvdtheque.dao.model.object.Film;
import fr.fredos.dvdtheque.service.IPersonneService;

@Configuration
public class ExcelStreamFilmWriter implements ItemStreamWriter<Film> {
	protected Logger logger = LoggerFactory.getLogger(ExcelStreamFilmWriter.class);
	@Autowired
	protected IPersonneService personneService;
	@Autowired
    protected Environment environment;
    public static final String EXCEL_DVD_FILE_NAME_EXPORT = "excel.dvd.file.name.export";
    public static final String EXCEL_DVD_FILE_PATH_EXPORT = "excel.dvd.file.path.export";
	private SXSSFWorkbook workBook;
    private WritableResource resource;
    private SXSSFRow row;
	private SXSSFSheet sheet;
    private Integer currentRowNumber;
    private Integer currentColumnNumber;
    private String[] headerTab = new String[]{"Realisateur", "Titre", "Zonedvd","Annee","Acteurs","Rippé","RIP Date"};
    
	public ExcelStreamFilmWriter() {
		
	}
	@Bean
    protected SXSSFWorkbook getWorkBook() {
    	return new SXSSFWorkbook(1);
    }
	@Override
	public void open(ExecutionContext executionContext) throws ItemStreamException {
		String fileName = environment.getRequiredProperty(EXCEL_DVD_FILE_PATH_EXPORT)+environment.getRequiredProperty(EXCEL_DVD_FILE_NAME_EXPORT);
    	this.resource = new FileSystemResource(fileName);
    	this.row = null;
    	this.workBook = getWorkBook();
		this.sheet = this.workBook.createSheet("Films");
        this.currentRowNumber = 0;
        this.currentColumnNumber = 0;
        createHeaderRow();
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
	    row = null;
	}
	private void createHeaderRow() {
    	addRow();
    	for(int i=0;i<headerTab.length;i++) {
    		addCell(headerTab[i]);
    	}
    }
	private void addRow() {
        row = this.sheet.createRow(currentRowNumber);
        currentRowNumber++;
        currentColumnNumber = 0;
    }

    private void addCell(String value) {
        SXSSFCell cell = row.createCell(currentColumnNumber);
        cell.setCellValue(value);
        currentColumnNumber++;
    }
	private void writeBook(Film film) {
        addRow();
        addCell(personneService.printPersonnes(film.getRealisateurs(),","));
        addCell(film.getTitre());
        addCell(film.getDvd().getZone().toString());
        addCell(film.getAnnee().toString());
        addCell(personneService.printPersonnes(film.getActeurs(),","));
        addCell(film.isRipped()?"oui":"non");
        if(film.isRipped() && film.getDvd().getDateRip() != null) {
        	DateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            addCell(sdf.format(film.getDvd().getDateRip()));
        }else {
        	addCell("");
        }
    }
	@Override
	public void write(List<? extends Film> items) throws Exception {
		items.forEach(this::writeBook);
	}

}
