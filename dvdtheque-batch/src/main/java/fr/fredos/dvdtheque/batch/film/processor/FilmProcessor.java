package fr.fredos.dvdtheque.batch.film.processor;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.springframework.batch.item.ItemProcessor;

import fr.fredos.dvdtheque.batch.csv.format.FilmCsvImportFormat;
import fr.fredos.dvdtheque.service.dto.ActeurDto;
import fr.fredos.dvdtheque.service.dto.DvdDto;
import fr.fredos.dvdtheque.service.dto.FilmDto;
import fr.fredos.dvdtheque.service.dto.PersonneDto;
import fr.fredos.dvdtheque.service.dto.PersonnesFilm;
import fr.fredos.dvdtheque.service.dto.RealisateurDto;
import fr.fredos.dvdtheque.service.enums.TypePersonne;

public class FilmProcessor implements ItemProcessor<FilmCsvImportFormat,FilmDto> {
	
	private PersonneDto currentRealisateur;

	@Override
	public FilmDto process(FilmCsvImportFormat item) throws Exception {
		FilmDto filmDto=new FilmDto();
		filmDto.setAnnee(item.getAnnee());
		filmDto.setTitre(StringUtils.upperCase(item.getTitre()));
		//filmDto.setTitreO(item.getTitreO());
		DvdDto dvd = new DvdDto();
		dvd.setZone(item.getZonedvd());
		filmDto.setDvd(dvd);
		PersonnesFilm pf = new PersonnesFilm();
		String acteurs = item.getActeurs();
		Set<ActeurDto> acteursDto = new HashSet<ActeurDto>();
		if(!StringUtils.isEmpty(acteurs)){
			Set<PersonneDto> set = parsePersonneItem(acteurs, TypePersonne.ACTEUR);
			for(PersonneDto acteur : set){
				ActeurDto acteurDto = new ActeurDto();
				acteurDto.setPersonne(acteur);
				acteursDto.add(acteurDto);
			}
		}
		Set<RealisateurDto> realisateur = new HashSet<RealisateurDto>(1);
		if(StringUtils.isEmpty(item.getRealisateur())){
			RealisateurDto realisateurDto = new RealisateurDto();
			realisateurDto.setPersonne(currentRealisateur);
			realisateur.add(realisateurDto);
		}else{
			Set<PersonneDto> set = parsePersonneItem(item.getRealisateur(), TypePersonne.REALISATEUR);
			for(PersonneDto real : set){
				RealisateurDto realisateurDto = new RealisateurDto();
				realisateurDto.setPersonne(real);
				realisateur.add(realisateurDto);
			}
		}
		pf.setRealisateur(realisateur.iterator().next());
		pf.setActeur(acteursDto);
		filmDto.setPersonnesFilm(pf);
		filmDto.setRipped(false);
		return filmDto;
	}

	private Set<PersonneDto> parsePersonneItem(String acteurs, TypePersonne typePersonne){
		String[] tabPersonne = StringUtils.split(acteurs, ',');
		Set<PersonneDto> personnesDto = new HashSet<PersonneDto>(tabPersonne.length);
		for(int i=0;i<tabPersonne.length;i++){
			PersonneDto personne = new PersonneDto();
			//ActeurDto acteur = new ActeurDto();
			String[] tab = StringUtils.split(tabPersonne[i], ' ');
			if(tab.length>1){
				if(tab.length==2){
					String nom = StringUtils.trim(tab[1]);
					String prenom = StringUtils.trim(tab[0]);
					personne.setNom(StringUtils.upperCase(nom));
					personne.setPrenom(StringUtils.upperCase(prenom));
				}
				if(tab.length==3){
					String nom1 = StringUtils.trim(tab[1]);
					String nom2 = StringUtils.trim(tab[2]);
					String prenom = StringUtils.trim(tab[0]);
					personne.setNom(StringUtils.upperCase(nom1)+" "+StringUtils.upperCase(nom2));
					personne.setPrenom(StringUtils.upperCase(prenom));
				}
				if(tab.length==4){
					String nom1 = StringUtils.trim(tab[1]);
					String nom2 = StringUtils.trim(tab[2]);
					String nom3 = StringUtils.trim(tab[3]);
					String prenom = StringUtils.trim(tab[0]);
					personne.setNom(StringUtils.upperCase(nom1)+" "+StringUtils.upperCase(nom2)+" "+StringUtils.upperCase(nom3));
					personne.setPrenom(StringUtils.upperCase(prenom));
				}
				if(tab.length>4){
					String[] tab2 = StringUtils.split(tabPersonne[i], '&');
					String[] tab3 = StringUtils.split(tab2[0], ' ');
					String[] tab4 = StringUtils.split(tab2[1], ' ');
					String nom1 = StringUtils.trim(tab3[1]);
					String nom2 = StringUtils.trim(tab4[1]);
					String prenom1 = StringUtils.trim(tab3[0]);
					String prenom2 = StringUtils.trim(tab4[0]);
					personne.setNom(StringUtils.upperCase(nom1));
					personne.setPrenom(StringUtils.upperCase(prenom1));
					PersonneDto personne2 = new PersonneDto();
					personne2.setNom(StringUtils.upperCase(nom2));
					personne2.setPrenom(StringUtils.upperCase(prenom2));
					personnesDto.add(personne2);
				}
			}else{
				String nom = StringUtils.trim(tab[0]);
				personne.setNom(StringUtils.upperCase(nom));
				personne.setPrenom("");
			}
			personnesDto.add(personne);
			if(typePersonne.equals(TypePersonne.REALISATEUR)){
				this.setCurrentRealisateur(personne);
			}
		}
		return personnesDto;
	}

	public PersonneDto getCurrentRealisateur() {
		return currentRealisateur;
	}

	public void setCurrentRealisateur(PersonneDto currentRealisateur) {
		this.currentRealisateur = currentRealisateur;
	}

	
}
