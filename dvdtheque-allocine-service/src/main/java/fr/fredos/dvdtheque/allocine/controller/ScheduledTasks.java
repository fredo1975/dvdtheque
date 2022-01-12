package fr.fredos.dvdtheque.allocine.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import fr.fredos.dvdtheque.allocine.service.AllocineService;

@Component
public class ScheduledTasks {
	protected Logger logger = LoggerFactory.getLogger(ScheduledTasks.class);
	@Autowired
    AllocineService allocineService;
	
	
	@Scheduled(cron = "${fichefilm.parsing.cron}", zone = "Europe/Paris")
	public void retrieveAllocineScrapingFicheFilm() {
		logger.info("retrieveAllocineScrapingFicheFilm");
		allocineService.scrapAllAllocineFicheFilm();
	}
	
}
