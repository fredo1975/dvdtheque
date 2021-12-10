package fr.fredos.dvdtheque.allocine.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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

import fr.fredos.dvdtheque.allocine.scraping.model.CritiquePresse;

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
	private final static String DIV_RATING_MDL_1_HOLDER = "div.rating-mdl.n10.stareval-stars";
	private final static String DIV_RATING_MDL_2_HOLDER = "div.rating-mdl.n20.stareval-stars";
	private final static String DIV_RATING_MDL_3_HOLDER = "div.rating-mdl.n30.stareval-stars";
	private final static String DIV_RATING_MDL_4_HOLDER = "div.rating-mdl.n40.stareval-stars";
	private final static String DIV_RATING_MDL_5_HOLDER = "div.rating-mdl.n50.stareval-stars";
	private final static String DIV_REVIEWS_PRESS_COMMENT = "div.reviews-press-comment";
	private final static String SECTION = "section";
	private final static String FILM_DELIMITER = "/film/fichefilm_gen_cfilm=";
	private final static String SEARCH_PAGE_DELIMITER = "?page=";
	private final static String BASE_URL = "https://www.allocine.fr";
	private final static String LIST_FILM_URL = "/films/";
	private final static String CRITIQUE_PRESSE_FILM_BASE_URL ="/film/fichefilm-";
	private final static String CRITIQUE_PRESSE_FILM_END_URL ="/critiques/presse/";
	
	
	public void retrieveAllocineScrapingMovieFeed(final String title) {
		FicheFilm ficheFilm = findFilm(title);
		if(ficheFilm != null) {
			Map<Integer,CritiquePresse> map = retrieveCritiquePresseMap(ficheFilm);
			logger.info("######### map="+map);
		}
	}
	
	private Map<Integer,CritiquePresse> retrieveCritiquePresseMap(FicheFilm ficheFilm){
		Map<Integer,CritiquePresse> map = new HashMap<>();
		Document document;
		try {
			document = Jsoup.connect(ficheFilm.getUrl())
					.userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/96.0.4664.45 Safari/537.36")
					.get();
			
			Elements es = document.select(SECTION);
	    	for(Element e : es){
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
			    						//logger.info("### cp="+cp.toString());
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
		    						//logger.info("### cp="+cp.toString());
		    					}
		    				}
		    				
		    				Elements es4 = ec.select(DIV_EVAL_HOLDER);
		    				index=0;
		    				for(Element e5 : es4){
		    					//logger.info("######### e5.getAllElements()="+e5.getAllElements());
		    					Elements a = e5.select(DIV_EVAL_HOLDER);
	    						//logger.info("######### a="+a.text());
	    						CritiquePresse cp = map.get(index++);
	    						cp.setAuthor(a.text());
	    						
	    						Double rating = null;
	    						if(e5.select(DIV_RATING_MDL_1_HOLDER).size()>0) {
	    							//logger.info("######### 1**");
	    							rating = 1.0d;
	    						}
	    						if(e5.select(DIV_RATING_MDL_2_HOLDER).size()>0) {
	    							//logger.info("######### 2**");
	    							rating = 2.0d;
	    						}
	    						if(e5.select(DIV_RATING_MDL_3_HOLDER).size()>0) {
	    							//logger.info("######### 3**");
	    							rating = 3.0d;
	    						}
	    						if(e5.select(DIV_RATING_MDL_4_HOLDER).size()>0) {
	    							//logger.info("######### 4**");
	    							rating = 4.0d;
	    						}
	    						if(e5.select(DIV_RATING_MDL_5_HOLDER).size()>0) {
	    							//logger.info("######### 5**");
	    							rating = 5.0d;
	    						}
	    						cp.setRating(rating);
	    						logger.info("### cp="+cp.toString());
		    				}
	    	    		}
	    	    	}
	    	    }
	    	    
	    	}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
    	return map;
	}
	private class FicheFilm {
		private final String ficheFilm;
		private final String url;
		
		public FicheFilm(String ficheFilm, String url) {
			super();
			this.ficheFilm = ficheFilm;
			this.url = url;
		}
		public String getFicheFilm() {
			return ficheFilm;
		}
		public String getUrl() {
			return url;
		}
		@Override
		public String toString() {
			return "FicheFilm [ficheFilm=" + ficheFilm + ", url=" + url + "]";
		}
		
	}
	private class Page {
		private Integer numPage = 1;

		public Page(Integer numPage) {
			super();
			this.numPage = numPage;
		}

		public Integer getNumPage() {
			return numPage;
		}

		public void setNumPage(Integer numPage) {
			this.numPage = numPage;
		}
		
	}
	private FicheFilm retrieveFilmIdOnFilmsPage(final String title, Page page) {
		Document listFilmdocument = null;
		FicheFilm ficheFilm = null;
		try {
			logger.info("### page.getNumPage() {}",page.getNumPage());
			if(page.getNumPage() == 1) {
				listFilmdocument = Jsoup.connect(BASE_URL+LIST_FILM_URL)
						.userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/96.0.4664.45 Safari/537.36")
						.get();
			}else {
				listFilmdocument = Jsoup.connect(BASE_URL+LIST_FILM_URL+SEARCH_PAGE_DELIMITER+page.getNumPage())
						.userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/96.0.4664.45 Safari/537.36")
						.get();
			}
			ficheFilm = parsePage(title, listFilmdocument);
			if(ficheFilm != null) {
				logger.info("### found {}",ficheFilm.toString());
			}
			/*
			if(page.getNumPage() == 1) {
				listFilmdocument = Jsoup.connect(BASE_URL+LIST_FILM_URL)
						.userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/96.0.4664.45 Safari/537.36")
						.get();
				ficheFilm = parsePage(title, listFilmdocument);
				if(ficheFilm != null) {
					logger.info("### found {}",ficheFilm.toString());
				}else {
					page.setNumPage(page.getNumPage()+1);
					return retrieveFilmIdOnFilmsPage(title,page);
					//parsePage(title, listFilmdocument);
				}
			}else {
				listFilmdocument = Jsoup.connect(BASE_URL+LIST_FILM_URL+SEARCH_PAGE_DELIMITER+page.getNumPage())
						.userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/96.0.4664.45 Safari/537.36")
						.get();
				ficheFilm = parsePage(title, listFilmdocument);
				if(ficheFilm != null) {
					logger.info("### found {}",ficheFilm.toString());
				}else {
					page.setNumPage(page.getNumPage()+1);
					return retrieveFilmIdOnFilmsPage(title,page);
					//parsePage(title, listFilmdocument);
				}
			}*/
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ficheFilm;
	}
	
	private FicheFilm parsePage(final String title, final Document listFilmdocument) {
		Elements links = listFilmdocument.select(AHREF);
        for (Element link : links) {
            if(StringUtils.contains(link.attr(HREF), FILM_DELIMITER)) {
            	logger.info("### link : " + link.text());
            	if(link.text().equalsIgnoreCase(title)) {
            		logger.info("### link : " + link.attr(HREF));
            		logger.info("### text : " + link.text());
            		final String filmTempId = StringUtils.substringAfter(link.attr(HREF), FILM_DELIMITER);
            		final String filmId = StringUtils.substringBefore(filmTempId, ".html");
	            	logger.info("### filmtempId : " + filmTempId);
	            	logger.info("### filmId : " + filmId);
	            	String url = BASE_URL+CRITIQUE_PRESSE_FILM_BASE_URL+filmId+CRITIQUE_PRESSE_FILM_END_URL;
	            	logger.info("### url : " + url);
	            	return new FicheFilm(filmId, url);
            	}
            }
        }
        return null;
	}
	
	private FicheFilm findFilm(final String title) {
		Integer _page = Integer.valueOf(1);
		Page page = new Page(_page);
		FicheFilm ficheFilm = retrieveFilmIdOnFilmsPage(title,page);
		while(ficheFilm == null) {
			page.setNumPage(page.getNumPage()+1);
			ficheFilm = retrieveFilmIdOnFilmsPage(title,page);
		}
		return ficheFilm;
	}
}
