package fr.fredos.dvdtheque.service.excel;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import fr.fredos.dvdtheque.dao.model.object.Film;
import fr.fredos.dvdtheque.service.IPersonneService;
@Configuration
public class ExcelFilmHandler {
	private SXSSFRow row;
	private SXSSFSheet sheet;
    private Integer currentRowNumber;
    private Integer currentColumnNumber;
    private String[] headerTab = new String[]{"Realisateur", "Titre", "Zonedvd","Annee","Acteurs","Rippé","RIP Date","Dvd Format", "TMDB ID"};
    //private SXSSFWorkbook workBook;
    @Autowired
	protected IPersonneService personneService;
    @Bean
    @Scope("prototype")
    public SXSSFWorkbook getWorkBook() {
    	return new SXSSFWorkbook(1);
    }
    public void createSheet(SXSSFWorkbook workBook) {
    	//this.workBook = workBook;
    	this.sheet = workBook.createSheet("Films");
        this.currentRowNumber = 0;
        this.currentColumnNumber = 0;
        this.createHeaderRow();
    }
    public Workbook createSheetFromByteArray(byte[] b) throws EncryptedDocumentException, IOException {
    	InputStream is = new ByteArrayInputStream(b);
    	return WorkbookFactory.create(is);
    }
    public SXSSFRow getRow() {
		return this.row;
	}

	public void setRow(SXSSFRow row) {
		this.row = row;
	}

	public void createHeaderRow() {
    	addRow();
    	for(int i=0;i<headerTab.length;i++) {
    		addCell(headerTab[i]);
    	}
    }
	private void addRow() {
		this.row = this.sheet.createRow(currentRowNumber);
		this.currentRowNumber++;
		this.currentColumnNumber = 0;
    }

    private void addCell(String value) {
        SXSSFCell cell = this.row.createCell(currentColumnNumber);
        cell.setCellValue(value);
        this.currentColumnNumber++;
    }
    public void writeBook(Film film) {
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
        addCell(film.getDvd().getFormat().name());
        addCell(film.getTmdbId().toString());
    }
}
