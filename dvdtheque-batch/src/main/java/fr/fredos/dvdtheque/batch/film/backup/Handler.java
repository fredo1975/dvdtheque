package fr.fredos.dvdtheque.batch.film.backup;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Handler {
	protected Logger logger = LoggerFactory.getLogger(Handler.class);
	public File handleFile(File input) {
		logger.info("Copying file: " + input.getAbsolutePath());
		return input;
	}
}
