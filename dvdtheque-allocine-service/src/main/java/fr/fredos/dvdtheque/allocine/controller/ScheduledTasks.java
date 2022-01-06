package fr.fredos.dvdtheque.allocine.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import fr.fredos.dvdtheque.allocine.service.AllocineService;

@Component
public class ScheduledTasks {
	@Autowired
    AllocineService allocineService;
	
	
	@Scheduled(cron = "${fichefilm.parsing.cron}", zone = "Europe/Paris")
	public void retrieveAllocineScrapingFicheFilm() {
		allocineService.scrapAllAllocineFicheFilm();
	}
	
}
