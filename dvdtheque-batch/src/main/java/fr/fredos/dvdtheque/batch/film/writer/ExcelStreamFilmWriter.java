package fr.fredos.dvdtheque.batch.film.writer;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
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
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemStreamWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.WritableResource;

import fr.fredos.dvdtheque.batch.model.Film;
import fr.fredos.dvdtheque.batch.model.Personne;
import fr.fredos.dvdtheque.common.enums.FilmOrigine;

@Configuration
public class ExcelStreamFilmWriter implements ItemStreamWriter<Film> {
	protected Logger logger = LoggerFactory.getLogger(ExcelStreamFilmWriter.class);
	private static final String NEW_LINE_CHARACTER="\r\n";
	private SXSSFRow row;
	private SXSSFSheet sheet;
    private Integer currentRowNumber;
    private Integer currentColumnNumber;
    public static final String[] EXCEL_HEADER_TAB = new String[]{"Realisateur", "Titre", "Annee","Acteurs","Origine Film", "TMDB ID", "Vu","Date insertion", "Zonedvd","Rippé","RIP Date","Dvd Format","Date Sortie DVD"};
    
	@Autowired
    protected Environment environment;
    public static final String EXCEL_DVD_FILE_NAME_EXPORT = "excel.dvd.file.name.export";
    public static final String EXCEL_DVD_FILE_PATH_EXPORT = "excel.dvd.file.path.export";
	private SXSSFWorkbook workBook;
    private WritableResource resource;
    
	public ExcelStreamFilmWriter() {
		
	}
	
	@Override
	public void open(ExecutionContext executionContext) throws ItemStreamException {
		String fileName = environment.getRequiredProperty(EXCEL_DVD_FILE_PATH_EXPORT)+"ListeDvdExport-"+new Date().getTime()+"-TOUS.xlsx";
    	this.resource = new FileSystemResource(fileName);
    	this.workBook = this.getWorkBook();
		this.initSheet(this.workBook);
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
	    this.setRow(null);
	}
	
	@Override
	public void write(List<? extends Film> items) throws Exception {
		for(Film film : items) {
			this.writeBook(film);
		}
	}
	
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

    private String printPersonnes(final Set<Personne> personnes, final String separator) {
		if (CollectionUtils.isNotEmpty(personnes)) {
			StringBuilder sb = new StringBuilder();
			personnes.forEach(real -> {
				sb.append(real.getNom()).append(separator);
			});
			return StringUtils.chomp(sb.toString(), separator);
		}
		return StringUtils.EMPTY;
	}
    public void writeBook(Film film) {
        addRow();
        // 0
        addCell(printPersonnes(film.getRealisateur(),","));
        // 1
        addCell(film.getTitre());
        // 2
        addCell(film.getAnnee().toString());
        // 3
        addCell(printPersonnes(film.getActeur(),","));
        // 4
        addCell(film.getOrigine()!=null?film.getOrigine().name():StringUtils.EMPTY);
        // 5
        addCell(film.getTmdbId().toString());
        // 6
        addCell(film.isVu()?"oui":"non");
        // 7
        if(film.getDateInsertion() != null) {
        	DateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        	addCell(sdf.format(film.getDateInsertion()));
        }else {
        	addCell("");
        }
        if(film.getDvd() != null) {
        	// 8
        	if(film.getDvd().getZone() != null && FilmOrigine.DVD.equals(film.getOrigine())) {
        		addCell(film.getDvd().getZone().toString());
        	}else {
        		addCell("");
        	}
        	
        	// 9
        	if(FilmOrigine.DVD.equals(film.getOrigine())) {
        		addCell(film.getDvd().isRipped()?"oui":"non");
        	}else {
            	addCell("");
            }
            
            // 10
            if(film.getDvd().isRipped() && film.getDvd().getDateRip() != null) {
            	DateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                addCell(sdf.format(film.getDvd().getDateRip()));
            }else {
            	addCell("");
            }
            // 11
            if(film.getDvd().getFormat() != null && FilmOrigine.DVD.equals(film.getOrigine())) {
            	addCell(film.getDvd().getFormat().name());
            }else {
            	addCell("");
            }
            // 12
            if(film.getDvd() != null && film.getDvd().getDateSortie() != null) {
            	DateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                addCell(sdf.format(film.getDvd().getDateSortie()));
            }else {
            	addCell("");
            }
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
