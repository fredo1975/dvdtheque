package fr.fredos.dvdtheque.batch.csv.format.launcher;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.integration.file.FileReadingMessageSource;

public class BackupLauncher {

	@Autowired
	FileReadingMessageSource source;
	public static void main(String[] args) {
		ClassPathXmlApplicationContext cpt = new ClassPathXmlApplicationContext("classpath*:spring-int-copy-files.xml",
				"classpath*:applicationContext-batch.xml");
        cpt.registerShutdownHook();
        
	}
}
