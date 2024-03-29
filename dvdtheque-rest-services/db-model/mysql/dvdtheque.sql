
DROP DATABASE IF EXISTS `dvdtheque`;
CREATE DATABASE IF NOT EXISTS `dvdtheque`;
USE `dvdtheque`;

DROP TABLE IF EXISTS `FILM_acteurs`;
DROP TABLE IF EXISTS `FILM_critiquesPresse`;
DROP TABLE IF EXISTS `FILM_genres`;
DROP TABLE IF EXISTS `FILM_realisateurs`;
DROP TABLE IF EXISTS `CRITIQUES_PRESSE`;
DROP TABLE IF EXISTS `DVD`;
DROP TABLE IF EXISTS `FILM`;
DROP TABLE IF EXISTS `PERSONNE`;
DROP TABLE IF EXISTS `GENRE`;
DROP TABLE IF EXISTS `ROLES`;
DROP TABLE IF EXISTS `USER`;
DROP TABLE IF EXISTS `USERROLES`;

CREATE TABLE IF NOT EXISTS `CRITIQUES_PRESSE` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `CODE` bigint(20) NOT NULL,
  `NOM_SOURCE` varchar(50) NOT NULL ,
  `AUTEUR` varchar(50) DEFAULT NULL COLLATE 'utf8mb4_general_ci',
  `CRITIQUE` longtext COLLATE 'utf8mb4_general_ci',
  `NOTE` double NOT NULL,
  PRIMARY KEY (`ID`)
) COLLATE='utf8mb4_general_ci' ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS `DVD` (
  `ID` bigint(11) NOT NULL AUTO_INCREMENT,
  `ANNEE` int(11) DEFAULT NULL,
  `ZONE` int(11) NOT NULL,
  `EDITION` varchar(255) DEFAULT NULL,
  `DATE_RIP` date DEFAULT NULL,
  `FORMAT` varchar(7) DEFAULT NULL,
  `RIPPED` tinyint(1) NOT NULL,
  `DATE_SORTIE` date DEFAULT NULL,
  PRIMARY KEY (`ID`)
) COLLATE='utf8mb4_general_ci' ENGINE=InnoDB ;

CREATE TABLE IF NOT EXISTS `FILM` (
  `ID` bigint(11) NOT NULL AUTO_INCREMENT,
  `VU` tinyint(4) NOT NULL DEFAULT '1',
  `HOMEPAGE` varchar(255) DEFAULT '1',
  `ANNEE` int(11) NOT NULL,
  `TITRE` varchar(255) NOT NULL COLLATE 'utf8mb4_general_ci',
  `TITRE_O` varchar(255) DEFAULT NULL COLLATE 'utf8mb4_general_ci',
  `ID_DVD` int(11) DEFAULT NULL,
  `ORIGINE` int(11) NOT NULL,
  `POSTER_PATH` varchar(255) DEFAULT NULL,
  `TMDB_ID` bigint(20) DEFAULT NULL,
  `OVERVIEW` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci,
  `RUNTIME` int(11) DEFAULT NULL,
  `DATE_SORTIE` date NOT NULL,
  `DATE_INSERTION` date NOT NULL,
  PRIMARY KEY (`ID`)
) COLLATE='utf8mb4_general_ci' ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS `PERSONNE` (
  `ID` bigint(11) NOT NULL AUTO_INCREMENT,
  `NOM` varchar(255) NOT NULL,
  `PRENOM` varchar(255) DEFAULT NULL,
  `DATE_N` date DEFAULT NULL,
  `ID_PAYS` int(11) DEFAULT NULL,
  `PROFILE_PATH` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) COLLATE='utf8mb4_general_ci' ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS `GENRE` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `NAME` varchar(2500) DEFAULT NULL,
  `TMDB_ID` int(11) NOT NULL,
  PRIMARY KEY (`ID`)
) COLLATE='utf8mb4_general_ci' ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS `FILM_acteurs` (
  `FILM_ID` bigint(20) NOT NULL,
  `ACTEURS_ID` bigint(20) NOT NULL,
  PRIMARY KEY (`FILM_ID`,`ACTEURS_ID`),
  KEY `FK_ACTEURS_ID` (`ACTEURS_ID`),
  CONSTRAINT `FK_ACTEURS_ID` FOREIGN KEY (`ACTEURS_ID`) REFERENCES `PERSONNE` (`ID`),
  CONSTRAINT `FK_FILM_ID` FOREIGN KEY (`FILM_ID`) REFERENCES `FILM` (`ID`)
) COLLATE='utf8mb4_general_ci' ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS `FILM_critiquesPresse` (
  `FILM_ID` bigint(20) NOT NULL,
  `CRITIQUESPRESSE_ID` bigint(20) NOT NULL,
  PRIMARY KEY (`FILM_ID`,`CRITIQUESPRESSE_ID`),
  KEY `FK_CRITIQUESPRESSE_ID` (`CRITIQUESPRESSE_ID`)
) COLLATE='utf8mb4_general_ci' ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS `FILM_genres` (
  `FILM_ID` bigint(20) NOT NULL,
  `GENRES_ID` bigint(20) NOT NULL,
  PRIMARY KEY (`FILM_ID`,`GENRES_ID`),
  KEY `FK_GENRES_ID` (`GENRES_ID`),
  CONSTRAINT `FK_GENRES_ID` FOREIGN KEY (`GENRES_ID`) REFERENCES `GENRE` (`ID`)
) COLLATE='utf8mb4_general_ci' ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS `FILM_realisateurs` (
  `FILM_ID` bigint(20) NOT NULL,
  `REALISATEURS_ID` bigint(20) NOT NULL,
  PRIMARY KEY (`FILM_ID`,`REALISATEURS_ID`),
  KEY `FK_REALISATEURS_ID` (`REALISATEURS_ID`),
  CONSTRAINT `FK_REALISATEURS_ID` FOREIGN KEY (`REALISATEURS_ID`) REFERENCES `PERSONNE` (`ID`)
) COLLATE='utf8mb4_general_ci' ENGINE=InnoDB;
