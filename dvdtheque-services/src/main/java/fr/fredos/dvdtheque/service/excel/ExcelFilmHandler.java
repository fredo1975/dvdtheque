package fr.fredos.dvdtheque.service.excel;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import fr.fredos.dvdtheque.dao.model.object.Film;
import fr.fredos.dvdtheque.service.IPersonneService;
@Configuration
public class ExcelFilmHandler {
	protected Logger logger = LoggerFactory.getLogger(ExcelFilmHandler.class);
    private static final String NEW_LINE_CHARACTER="\r\n";
	private SXSSFRow row;
	private SXSSFSheet sheet;
    private Integer currentRowNumber;
    private Integer currentColumnNumber;
    private String[] headerTab = new String[]{"Realisateur", "Titre", "Zonedvd","Annee","Acteurs","Rippé","RIP Date","Dvd Format", "TMDB ID"};
    @Autowired
	protected IPersonneService personneService;
    @Bean
    @Scope("prototype")
    public SXSSFWorkbook getWorkBook() {
    	return new SXSSFWorkbook(1);
    }
    public void initSheet(SXSSFWorkbook workBook) {
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
    public Workbook createSheetFromFile(File f) throws EncryptedDocumentException, IOException {
    	return WorkbookFactory.create(f);
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
    public SXSSFWorkbook createSXSSFWorkbookFromFilmList(List<Film> list) throws IOException {
    	SXSSFWorkbook workBook = new SXSSFWorkbook(1);
	    try{
	    	initSheet(workBook);
	    	setRow(null);
	    	for(Film film : list) {
	    		writeBook(film);
	    	}
		}finally {
            if (null != workBook) {
                try {
                	workBook.close();
                } catch (IOException eio) {
                    logger.error("Error Occurred while exporting to XLS ", eio);
                    throw eio;
                }
            }
        }
	    return workBook;
    }
    public byte[] createByteContentFromFilmList(List<Film> list) throws IOException {
    	byte[] excelContent = null;
	    SXSSFWorkbook workBook = new SXSSFWorkbook(1);
	    try{
	    	initSheet(workBook);
	    	setRow(null);
	    	for(Film film : list) {
	    		writeBook(film);
	    	}
	    	for(Iterator<Row> rowIt = sheet.iterator();rowIt.hasNext();) {
	        	Row row = rowIt.next();
	        	for(Iterator<Cell> cellIt = row.iterator();cellIt.hasNext();) {
	        		Cell cell = cellIt.next();
	        	}
	        }
	    	ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    	workBook.write(baos);
	    	excelContent = baos.toByteArray();
	    }finally {
            if (null != workBook) {
                try {
                	workBook.close();
                } catch (IOException eio) {
                    logger.error("Error Occurred while exporting to XLS ", eio);
                    throw eio;
                }
            }
        }
	    return excelContent;
    }
    
    public String createCsvFromExcel(Workbook workBook) throws IOException {
    	Sheet selSheet = workBook.getSheetAt(0);
    	StringBuffer sb = new StringBuffer();
        Iterator<Row> rowIterator = selSheet.iterator();
        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            Iterator<Cell> cellIterator = row.cellIterator();
            boolean newLine = true;
            while (cellIterator.hasNext()) {
                Cell cell = cellIterator.next();
                if(newLine) {
                	newLine = !newLine;
                }else {
                	sb.append(";");
                }
                switch (cell.getCellType()) {
                case STRING:
                    sb.append(cell.getStringCellValue());
                    break;
                case NUMERIC:
                    sb.append(cell.getNumericCellValue());
                    break;
                case BOOLEAN:
                    sb.append(cell.getBooleanCellValue());
                    break;
                default:
                }
            }
            sb.append(NEW_LINE_CHARACTER);
        }
        return sb.toString();
    }
}
