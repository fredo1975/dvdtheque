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
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import fr.fredos.dvdtheque.dao.model.object.Film;
import fr.fredos.dvdtheque.service.IPersonneService;
@Component
public class ExcelFilmHandler {
	protected Logger logger = LoggerFactory.getLogger(ExcelFilmHandler.class);
    private static final String NEW_LINE_CHARACTER="\r\n";
	private SXSSFRow row;
	private SXSSFSheet sheet;
    private Integer currentRowNumber;
    private Integer currentColumnNumber;
    public static final String[] EXCEL_HEADER_TAB = new String[]{"Realisateur", "Titre", "Annee","Acteurs","Origine Film", "TMDB ID", "Vu", "Zonedvd","Rippé","RIP Date","Dvd Format"};
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
    	for(int i=0;i<EXCEL_HEADER_TAB.length;i++) {
    		addCell(EXCEL_HEADER_TAB[i]);
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
        // 0
        addCell(personneService.printPersonnes(film.getRealisateurs(),","));
        // 1
        addCell(film.getTitre());
        // 2
        addCell(film.getAnnee().toString());
        // 3
        addCell(personneService.printPersonnes(film.getActeurs(),","));
        // 4
        addCell(film.getOrigine().name());
        // 5
        addCell(film.getTmdbId().toString());
        // 6
        addCell(film.isVu()?"oui":"non");
        
        if(film.getDvd() != null) {
        	// 7
        	addCell(film.getDvd().getZone().toString());
        	// 8
            addCell(film.getDvd().isRipped()?"oui":"non");
            // 9
            if(film.getDvd().isRipped() && film.getDvd().getDateRip() != null) {
            	DateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                addCell(sdf.format(film.getDvd().getDateRip()));
            }else {
            	addCell("");
            }
            // 10
            addCell(film.getDvd().getFormat().name());
        }
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
