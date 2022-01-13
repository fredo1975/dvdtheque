package fr.fredos.dvdtheque.allocine.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import fr.fredos.dvdtheque.allocine.domain.CritiquePresse;
import fr.fredos.dvdtheque.allocine.domain.FicheFilm;
import fr.fredos.dvdtheque.allocine.domain.Page;
import fr.fredos.dvdtheque.allocine.repository.FicheFilmRepository;

@Service
public class AllocineServiceImpl implements AllocineService {
	protected Logger logger = LoggerFactory.getLogger(AllocineServiceImpl.class);
	FicheFilmRepository ficheFilmRepository;
	@Autowired
	AllocineServiceImpl(FicheFilmRepository ficheFilmRepository) {
		this.ficheFilmRepository = ficheFilmRepository;
	}
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
	private final static String CRITIQUE_PRESSE_FILM_BASE_URL = "/film/fichefilm-";
	private final static String CRITIQUE_PRESSE_FILM_END_URL = "/critiques/presse/";

	@Value("${fichefilm.parsing.page}")
	private int nbParsedPage;
	
	/**
	 * 
	 */
	@Override
	public void scrapAllAllocineFicheFilm() {
		Integer _page = Integer.valueOf(1);
		Page page = new Page(_page);
		Set<FicheFilm> allFicheFilmFromPage = retrieveAllFicheFilmFromPage(page);
		if (CollectionUtils.isNotEmpty(allFicheFilmFromPage)) {
			processCritiquePress(allFicheFilmFromPage);
		}

		while (page.getNumPage() < nbParsedPage) {
			if (CollectionUtils.isNotEmpty(allFicheFilmFromPage)) {
				allFicheFilmFromPage.clear();
			}
			page.setNumPage(page.getNumPage() + 1);
			allFicheFilmFromPage = retrieveAllFicheFilmFromPage(page);
			if (CollectionUtils.isNotEmpty(allFicheFilmFromPage)) {
				processCritiquePress(allFicheFilmFromPage);
			}
		}
	}
	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW,readOnly = false)
	public void removeAllFilmWithoutCritique() {
		List<FicheFilm> l = findAllFilmWithoutCritique();
		ficheFilmRepository.deleteAll(l);
	}
	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW,readOnly = false)
	public FicheFilm saveFicheFilm(FicheFilm ficheFilm) {
		return ficheFilmRepository.save(ficheFilm);
	}

	/**
	 * 
	 * @param allFicheFilmFromPage
	 */
	private void processCritiquePress(final Set<FicheFilm> allFicheFilmFromPage) {
		if (CollectionUtils.isNotEmpty(allFicheFilmFromPage)) {
			for (FicheFilm ficheFilm : allFicheFilmFromPage) {
				Map<Integer, CritiquePresse> map = retrieveCritiquePresseMap(ficheFilm);
				Optional<FicheFilm> op = retrievefindByFicheFilmId(ficheFilm.getAllocineFilmId());
				if(MapUtils.isNotEmpty(map) && op.isEmpty()) {
					saveFicheFilm(ficheFilm);
				}
			}
		}
	}

	/**
	 * 
	 * @param ficheFilm
	 * @return
	 */
	private Map<Integer, CritiquePresse> retrieveCritiquePresseMap(FicheFilm ficheFilm) {
		Map<Integer, CritiquePresse> map = new HashMap<>();
		Document document;
		try {
			document = Jsoup.connect(ficheFilm.getUrl()).userAgent(
					"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/96.0.4664.45 Safari/537.36")
					.get();

			Elements es = document.select(SECTION);
			for (Element e : es) {
				Elements es2 = e.select(DIV);
				for (Element e2 : es2) {
					Elements es3 = e2.select(SECTION);
					for (Element e4 : es3) {
						Elements masthead = e4.select(DIV_REVIEWS_PRESS_COMMENT);
						for (Element ec : masthead) {
							// logger.debug("### ec.getAllElements()="+ec.getAllElements());
							Elements esH2 = ec.select(H2);
							int index = 0;
							for (Element e8 : esH2) {
								// logger.debug("### e8.getAllElements()="+e8.getAllElements());
								Elements esSpan = e8.select(SPAN);
								for (Element e9 : esSpan) {
									if (StringUtils.isNotEmpty(e9.text())) {
										CritiquePresse cp = new CritiquePresse();
										cp.setNewsSource(e9.text());
										// logger.debug("### cp="+cp.toString());
										map.put(Integer.valueOf(index++), cp);
										cp.setFicheFilm(ficheFilm);
										ficheFilm.addCritiquePresse(cp);
									}
								}
							}
							Elements esP = ec.select(P);
							index = 0;
							for (Element e8 : esP) {
								if (StringUtils.isNotEmpty(e8.text())) {
									CritiquePresse cp = map.get(index++);
									cp.setBody(e8.text());
									// logger.debug("### cp="+cp.toString());
								}
							}

							Elements es4 = ec.select(DIV_EVAL_HOLDER);
							index = 0;
							for (Element e5 : es4) {
								// logger.info("######### e5.getAllElements()="+e5.getAllElements());
								Elements a = e5.select(DIV_EVAL_HOLDER);
								// logger.debug("######### a="+a.text());
								CritiquePresse cp = map.get(index++);
								cp.setAuthor(a.text());

								Double rating = null;
								if (e5.select(DIV_RATING_MDL_1_HOLDER).size() > 0) {
									// logger.debug("######### 1**");
									rating = 1.0d;
								}
								if (e5.select(DIV_RATING_MDL_2_HOLDER).size() > 0) {
									// logger.debug("######### 2**");
									rating = 2.0d;
								}
								if (e5.select(DIV_RATING_MDL_3_HOLDER).size() > 0) {
									// logger.debug("######### 3**");
									rating = 3.0d;
								}
								if (e5.select(DIV_RATING_MDL_4_HOLDER).size() > 0) {
									// logger.debug("######### 4**");
									rating = 4.0d;
								}
								if (e5.select(DIV_RATING_MDL_5_HOLDER).size() > 0) {
									// logger.debug("######### 5**");
									rating = 5.0d;
								}if (rating == null) {
									rating = 0.0d;
								}
								cp.setRating(rating);
								logger.debug("### cp=" + cp.toString());
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

	/**
	 * 
	 * @param page
	 * @return
	 */
	private Set<FicheFilm> retrieveAllFicheFilmFromPage(Page page) {
		Document listFilmdocument = null;
		Set<FicheFilm> allFicheFilmFromPage = null;
		try {
			logger.debug("### page.getNumPage() {}", page.getNumPage());
			if (page.getNumPage() == 1) {
				listFilmdocument = Jsoup.connect(BASE_URL + LIST_FILM_URL).userAgent(
						"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/96.0.4664.45 Safari/537.36")
						.get();
			} else {
				listFilmdocument = Jsoup.connect(BASE_URL + LIST_FILM_URL + SEARCH_PAGE_DELIMITER + page.getNumPage())
						.userAgent(
								"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/96.0.4664.45 Safari/537.36")
						.get();
			}
			allFicheFilmFromPage = retrieveAllFicheFilmFromPage(listFilmdocument, page.getNumPage());

			if (CollectionUtils.isNotEmpty(allFicheFilmFromPage)) {
				logger.debug("### found {} films {}", allFicheFilmFromPage.size(), allFicheFilmFromPage.toString());
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return allFicheFilmFromPage;
	}

	/**
	 * 
	 * @param listFilmdocument
	 * @param numPage
	 * @return
	 */
	private Set<FicheFilm> retrieveAllFicheFilmFromPage(final Document listFilmdocument, final int numPage) {
		Set<FicheFilm> set = new HashSet<>();
		Elements links = listFilmdocument.select(AHREF);
		for (Element link : links) {
			if (StringUtils.contains(link.attr(HREF), FILM_DELIMITER)) {
				logger.debug("### link : " + link.text());
				logger.debug("### link : " + link.attr(HREF));
				final String filmTempId = StringUtils.substringAfter(link.attr(HREF), FILM_DELIMITER);
				final String filmId = StringUtils.substringBefore(filmTempId, ".html");
				logger.debug("### filmId : " + filmId);
				String url = BASE_URL + CRITIQUE_PRESSE_FILM_BASE_URL + filmId + CRITIQUE_PRESSE_FILM_END_URL;
				logger.debug("### url : " + url);
				set.add(new FicheFilm(link.text(), Integer.valueOf(filmId), url, numPage));
			}
		}
		return set;
	}

	@Override
	public List<FicheFilm> retrieveAllFicheFilm() {
		return ficheFilmRepository.findAll();
	}

	@Override
	public Optional<FicheFilm> retrieveFicheFilm(int id) {
		return ficheFilmRepository.findById(id);
	}
	@Override
	public FicheFilm retrieveFicheFilmByTitle(String title) {
		return ficheFilmRepository.findByTitle(title);
	}
	
	@Override
	public Optional<FicheFilm> retrievefindByFicheFilmId(Integer ficheFilmId) {
		return Optional.ofNullable(ficheFilmRepository.findByFicheFilmId(ficheFilmId));
	}
	@Override
	public List<FicheFilm> findAllFilmWithoutCritique() {
		return ficheFilmRepository.findAllFilmWithoutCritique();
	}
}
