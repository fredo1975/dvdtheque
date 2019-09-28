package fr.fredos.dvdtheque.rest.file.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Calendar;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import fr.fredos.dvdtheque.common.exceptions.DvdthequeCommonsException;
import fr.fredos.dvdtheque.service.excel.ExcelFilmHandler;
@Component
public class MultipartFileUtil {
	protected Logger logger = LoggerFactory.getLogger(MultipartFileUtil.class);
	@Autowired
    private ExcelFilmHandler excelFilmHandler;
	/**
	 * 
	 * @param file
	 * @return
	 * @throws Exception
	 */
	public File createFileToImport(MultipartFile file) throws Exception {
		File resFile = null;
		Calendar cal = Calendar.getInstance();
		File tempFile = new File(System.getProperty("java.io.tmpdir")+"/"+ cal.getTimeInMillis() + "_"+file.getOriginalFilename());
		File convFile = new File(System.getProperty("java.io.tmpdir")+"/"+file.getOriginalFilename());
		try {
			file.transferTo(convFile);
		} catch (IllegalStateException | IOException e) {
			logger.error(e.getMessage());
			throw e;
		}
		if(StringUtils.equalsIgnoreCase(FilenameUtils.getExtension(file.getOriginalFilename()),"csv")) {
			resFile = convFile;
		} else if(StringUtils.equalsIgnoreCase(FilenameUtils.getExtension(file.getOriginalFilename()),"xls") 
				|| StringUtils.equalsIgnoreCase(FilenameUtils.getExtension(file.getOriginalFilename()),"xlsx")) {
			Workbook workBook;
			try {
				workBook = this.excelFilmHandler.createSheetFromFile(convFile);
				String csv = this.excelFilmHandler.createCsvFromExcel(workBook);
				FileOutputStream outputStream = new FileOutputStream(tempFile);
			    byte[] strToBytes = csv.getBytes();
			    outputStream.write(strToBytes);
			    outputStream.close();
			    resFile = tempFile;
			} catch (EncryptedDocumentException | IOException e) {
				logger.error(e.getMessage());
				throw e;
			}
		}else {
			String msg = "File not recognized";
			logger.error(msg);
			throw new DvdthequeCommonsException(msg);
		}
		return resFile;
	}
}
