package fr.fredos.dvdtheque.batch.film.writer;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;

import fr.fredos.dvdtheque.dao.model.object.Film;
import fr.fredos.dvdtheque.service.IPersonneService;

public class ExcelFilmWriter implements ItemWriter<Film>{
	protected Logger logger = LoggerFactory.getLogger(ExcelFilmWriter.class);
	@Autowired
	protected IPersonneService personneService;
	private final SXSSFSheet sheet;
    private Integer currentRowNumber;
    private Integer currentColumnNumber;
    private SXSSFRow row;
    String[] headerTab = new String[]{"Realisateur", "Titre", "Zonedvd","Annee","Acteurs","Rippé","RIP Date"};
    public ExcelFilmWriter(SXSSFWorkbook workbook) {
        this.sheet = workbook.createSheet("Films");
        this.currentRowNumber = 0;
        this.currentColumnNumber = 0;
        createHeaderRow();
    }
    
    private void createHeaderRow() {
    	addRow();
    	for(int i=0;i<headerTab.length;i++) {
    		addCell(headerTab[i]);
    	}
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
	@Override
	public void write(List<? extends Film> items) throws Exception {
		items.forEach(this::writeBook);
	}

}
