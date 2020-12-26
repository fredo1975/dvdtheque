package fr.fredos.dvdtheque.allocine.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import fr.fredos.dvdtheque.allocine.scrapinig.model.CritiquePresse;

@Service
public class AllocineScrapingService {
	protected Logger logger = LoggerFactory.getLogger(AllocineScrapingService.class);
	
	@Autowired
    Environment environment;
	private final static String AHREF = "a[href]";
	private final static String HREF = "href";
	private final static String NAV = "nav.pagination";
	private final static String SPAN = "span";
	private final static String P = "p";
	private final static String H2 = "h2";
	private final static String DIV = "div";
	private final static String DIV_EVAL_HOLDER = "div.eval-holder";
	private final static String DIV_REVIEWS_PRESS_COMMENT = "div.reviews-press-comment";
	private final static String SECTION = "section";
	private final static String FILM_DELIMITER = "/film/fichefilm_gen_cfilm=";
	private final static String SEARCH_PAGE_DELIMITER = "?page=";
	private final static String PRESS_DELIMITER = "/presse-";
	private final static String BASE_URL = "https://www.allocine.fr";
	private final static String LIST_FILM_URL = "/films/";
	private final static String CRITIQUE_PRESSE_FILM_BASE_URL ="/film/fichefilm-";
	private final static String CRITIQUE_PRESSE_FILM_END_URL ="/critiques/presse/";
	
	
	public void retrieveAllocineMovieFeed(final String title) {
		Document filmdocument = retrieveCritiquePresseFilm(title);
		if(filmdocument != null) {
			Map<Integer,CritiquePresse> map = retrieveCritiquePresseMap(filmdocument);
			logger.info("######### map="+map);
		}
	}
	
	private Map<Integer,CritiquePresse> retrieveCritiquePresseMap(Document document){
		Map<Integer,CritiquePresse> map = new HashMap<>();
		Elements es = document.select(SECTION);
    	for(Element e : es){
    	    //logger.info("### e.getAllElements()="+e.getAllElements());
    	    Elements es2 = e.select(DIV);
    	    for(Element e2 : es2){
    	    	Elements es3 = e2.select(SECTION);
    	    	for(Element e4 : es3){
    	    		Elements masthead = e4.select(DIV_REVIEWS_PRESS_COMMENT);
    	    		for(Element ec : masthead){
    	    			//logger.info("### ec.getAllElements()="+ec.getAllElements());
    	    			
    	    			Elements esH2 = ec.select(H2);
    	    			int index=0;
	    				for(Element e8 : esH2){
	    					//logger.info("### e8.getAllElements()="+e8.getAllElements());
	    					Elements esSpan = e8.select(SPAN);
		    				for(Element e9 : esSpan){
		    					if(StringUtils.isNotEmpty(e9.text())) {
		    						CritiquePresse cp = new CritiquePresse();
		    						cp.setNewsSource(e9.text());
		    						map.put(Integer.valueOf(index++), cp);
		    					}
		    				}
	    				}
	    				Elements esP = ec.select(P);
	    				index=0;
	    				for(Element e8 : esP){
	    					if(StringUtils.isNotEmpty(e8.text())) {
	    						CritiquePresse cp = map.get(index++);
	    						cp.setBody(e8.text());
	    					}
	    				}
	    				
	    				Elements es4 = ec.select(DIV_EVAL_HOLDER);
	    				index=0;
	    				for(Element e5 : es4){
	    					//logger.info("######### e5.getAllElements()="+e5.getAllElements());
	    					Document doc = Jsoup.parse(e5.getAllElements().text(), "utf-8"); 
	    					Elements a = doc.select(DIV_EVAL_HOLDER);
	    					for(Element a1 : a){
	    						//logger.info("######### a1.getAllElements()="+a1.getAllElements());
	    					}
	    				}
    	    		}
    	    	}
    	    }
    	}
    	return map;
	}
	
	private Document retrieveFilmIdOnFilmsPage(final String title) {
		try {
			Document listFilmdocument = Jsoup.connect(BASE_URL+LIST_FILM_URL).get();
	        Elements links = listFilmdocument.select(AHREF);
	        for (Element link : links) {
	            if(StringUtils.contains(link.attr(HREF), FILM_DELIMITER)) {
	            	if(link.text().equalsIgnoreCase(title)) {
	            		logger.info("### link : " + link.attr(HREF));
	            		logger.info("### text : " + link.text());
	            		final String filmTempId = StringUtils.substringAfter(link.attr(HREF), FILM_DELIMITER);
	            		final String filmId = StringUtils.substringBefore(filmTempId, ".html");
		            	logger.info("### filmtempId : " + filmTempId);
		            	logger.info("### filmId : " + filmId);
		            	String url = BASE_URL+CRITIQUE_PRESSE_FILM_BASE_URL+filmId+CRITIQUE_PRESSE_FILM_END_URL;
		            	logger.info("### url : " + url);
	                    return Jsoup.connect(url).get();
	            	}
	            }
	        }
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		logger.info("### not found 1");
		return null;
	}
	private Document retrievePage(final String title, final String url,final String page) {
		logger.info("### url : " + url);
		try {
			
			Document filmdocument = retrieveFilmIdOnFilmsPage(title);
			if(filmdocument == null) {
				Document listFilmdocument = Jsoup.connect(url).get();
				logger.info("### not found 2");
				Elements es = listFilmdocument.select(NAV);
		    	for(Element e : es){
		    		Elements es2 = e.select(DIV);
		    	    for(Element e2 : es2){
		    	    	Elements links = e2.select(AHREF);
						int index=2;
				        for (Element link : links) {
				        	logger.info("### link.getAllElements() : " + link.getAllElements());
				        	if(StringUtils.contains(link.attr(HREF), SEARCH_PAGE_DELIMITER)) {
				        		logger.info("### link : " + link.attr(HREF));
			            		logger.info("### text : " + link.text());
			            		String url2 = BASE_URL+LIST_FILM_URL+SEARCH_PAGE_DELIMITER+link.text();
				            	logger.info("### url : " + url);
				            	
				            	return retrievePage(title, url2, link.text());
				        	}
				        }
		    	    }
		    	}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	private Document retrieveCritiquePresseFilm(final String title) {
		try {
			Document listFilmdocument = retrieveFilmIdOnFilmsPage(title);
			if(listFilmdocument == null) {
				Document listFilmdocument2 = Jsoup.connect(BASE_URL+LIST_FILM_URL).get();
				Elements links = listFilmdocument2.select(AHREF);
				int index=2;
		        for (Element link : links) {
		        	if(StringUtils.contains(link.attr(HREF), SEARCH_PAGE_DELIMITER)) {
		            	logger.info("### link : " + link.attr(HREF));
	            		logger.info("### text : " + link.text());
	            		int page = Integer.valueOf(link.text()).intValue();
		            	String url = BASE_URL+LIST_FILM_URL+SEARCH_PAGE_DELIMITER+link.text();
		            	logger.info("### url : " + url);
		            	return retrievePage(title, url, link.text());
	                    //return Jsoup.connect(url).get();
		            }
		        }
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
