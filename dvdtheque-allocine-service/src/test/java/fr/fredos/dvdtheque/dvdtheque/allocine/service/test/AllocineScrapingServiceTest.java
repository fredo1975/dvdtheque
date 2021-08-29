package fr.fredos.dvdtheque.dvdtheque.allocine.service.test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import org.jsoup.nodes.Element;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import fr.fredos.dvdtheque.allocine.service.AllocineScrapingService;
import fr.fredos.dvdtheque.service.IFilmService;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {fr.fredos.dvdtheque.dao.Application.class,
		fr.fredos.dvdtheque.service.ServiceApplication.class,
		fr.fredos.dvdtheque.allocine.service.AllocineServiceApplication.class})
@ActiveProfiles("test")
public class AllocineScrapingServiceTest {
	protected Logger logger = LoggerFactory.getLogger(AllocineScrapingServiceTest.class);
	@Autowired
    private AllocineScrapingService client;
    @Autowired
	protected IFilmService filmService;
    
    @Before()
	public void setUp() throws Exception {
    	filmService.cleanAllFilms();
	}
    
    
    private void writeDoc(Element e) throws IOException {
    	String saveLocation = "D:\\tmp\\tmp.txt";
    	File file = new File(saveLocation);
        /*if(file.exists()) {
            file.delete();
        }*/
        //System.out.println("\nCreating file...");
        file.createNewFile();
        //System.out.println("Writing results to file...");
        FileOutputStream fos = new FileOutputStream(file, true);
        String encoding = System.getProperty("file.encoding");
        try (Writer bw = new BufferedWriter(new OutputStreamWriter(fos, encoding))) {
        	bw.append(e.toString() + " ");
        }
        //System.out.println("Success. Exiting...");
    }
    
    
    @Test
    public void retrieveAllocineMovieFeedTest() throws IOException {
    	client.retrieveAllocineMovieFeed("2046");
    	//String url = "https://www.allocine.fr/film/fichefilm-247271/critiques/presse/";
    	
    	//Document document = Jsoup.connect(url).get();
        //String html = document.html();
    }
}
