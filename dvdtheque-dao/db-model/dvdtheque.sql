-- --------------------------------------------------------
-- Hôte :                        192.168.1.104
-- Version du serveur:           5.7.28-0ubuntu0.18.04.4 - (Ubuntu)
-- SE du serveur:                Linux
-- HeidiSQL Version:             10.1.0.5464
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;


-- Listage de la structure de la base pour dvdtheque
DROP DATABASE IF EXISTS `dvdtheque`;
CREATE DATABASE IF NOT EXISTS `dvdtheque` /*!40100 DEFAULT CHARACTER SET latin1 */;
USE `dvdtheque`;

-- Listage de la structure de la table dvdtheque. BATCH_JOB_EXECUTION
DROP TABLE IF EXISTS `BATCH_JOB_EXECUTION`;
CREATE TABLE IF NOT EXISTS `BATCH_JOB_EXECUTION` (
  `JOB_EXECUTION_ID` bigint(20) NOT NULL,
  `VERSION` bigint(20) DEFAULT NULL,
  `JOB_INSTANCE_ID` bigint(20) NOT NULL,
  `CREATE_TIME` datetime NOT NULL,
  `START_TIME` datetime DEFAULT NULL,
  `END_TIME` datetime DEFAULT NULL,
  `STATUS` varchar(10) DEFAULT NULL,
  `EXIT_CODE` varchar(2500) DEFAULT NULL,
  `EXIT_MESSAGE` varchar(2500) DEFAULT NULL,
  `LAST_UPDATED` datetime DEFAULT NULL,
  `JOB_CONFIGURATION_LOCATION` varchar(2500) DEFAULT NULL,
  PRIMARY KEY (`JOB_EXECUTION_ID`),
  KEY `JOB_INST_EXEC_FK` (`JOB_INSTANCE_ID`),
  CONSTRAINT `JOB_INST_EXEC_FK` FOREIGN KEY (`JOB_INSTANCE_ID`) REFERENCES `BATCH_JOB_INSTANCE` (`JOB_INSTANCE_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Les données exportées n'étaient pas sélectionnées.
-- Listage de la structure de la table dvdtheque. BATCH_JOB_EXECUTION_CONTEXT
DROP TABLE IF EXISTS `BATCH_JOB_EXECUTION_CONTEXT`;
CREATE TABLE IF NOT EXISTS `BATCH_JOB_EXECUTION_CONTEXT` (
  `JOB_EXECUTION_ID` bigint(20) NOT NULL,
  `SHORT_CONTEXT` varchar(2500) NOT NULL,
  `SERIALIZED_CONTEXT` text,
  PRIMARY KEY (`JOB_EXECUTION_ID`),
  CONSTRAINT `JOB_EXEC_CTX_FK` FOREIGN KEY (`JOB_EXECUTION_ID`) REFERENCES `BATCH_JOB_EXECUTION` (`JOB_EXECUTION_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Les données exportées n'étaient pas sélectionnées.
-- Listage de la structure de la table dvdtheque. BATCH_JOB_EXECUTION_PARAMS
DROP TABLE IF EXISTS `BATCH_JOB_EXECUTION_PARAMS`;
CREATE TABLE IF NOT EXISTS `BATCH_JOB_EXECUTION_PARAMS` (
  `JOB_EXECUTION_ID` bigint(20) NOT NULL,
  `TYPE_CD` varchar(6) NOT NULL,
  `KEY_NAME` varchar(100) NOT NULL,
  `STRING_VAL` varchar(250) DEFAULT NULL,
  `DATE_VAL` datetime DEFAULT NULL,
  `LONG_VAL` bigint(20) DEFAULT NULL,
  `DOUBLE_VAL` double DEFAULT NULL,
  `IDENTIFYING` char(1) NOT NULL,
  KEY `JOB_EXEC_PARAMS_FK` (`JOB_EXECUTION_ID`),
  CONSTRAINT `JOB_EXEC_PARAMS_FK` FOREIGN KEY (`JOB_EXECUTION_ID`) REFERENCES `BATCH_JOB_EXECUTION` (`JOB_EXECUTION_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Les données exportées n'étaient pas sélectionnées.
-- Listage de la structure de la table dvdtheque. BATCH_JOB_EXECUTION_SEQ
DROP TABLE IF EXISTS `BATCH_JOB_EXECUTION_SEQ`;
CREATE TABLE IF NOT EXISTS `BATCH_JOB_EXECUTION_SEQ` (
  `ID` bigint(20) NOT NULL,
  `UNIQUE_KEY` char(1) NOT NULL,
  UNIQUE KEY `UNIQUE_KEY_UN` (`UNIQUE_KEY`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Les données exportées n'étaient pas sélectionnées.
-- Listage de la structure de la table dvdtheque. BATCH_JOB_INSTANCE
DROP TABLE IF EXISTS `BATCH_JOB_INSTANCE`;
CREATE TABLE IF NOT EXISTS `BATCH_JOB_INSTANCE` (
  `JOB_INSTANCE_ID` bigint(20) NOT NULL,
  `VERSION` bigint(20) DEFAULT NULL,
  `JOB_NAME` varchar(100) NOT NULL,
  `JOB_KEY` varchar(32) NOT NULL,
  PRIMARY KEY (`JOB_INSTANCE_ID`),
  UNIQUE KEY `JOB_INST_UN` (`JOB_NAME`,`JOB_KEY`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Les données exportées n'étaient pas sélectionnées.
-- Listage de la structure de la table dvdtheque. BATCH_JOB_SEQ
DROP TABLE IF EXISTS `BATCH_JOB_SEQ`;
CREATE TABLE IF NOT EXISTS `BATCH_JOB_SEQ` (
  `ID` bigint(20) NOT NULL,
  `UNIQUE_KEY` char(1) NOT NULL,
  UNIQUE KEY `UNIQUE_KEY_UN` (`UNIQUE_KEY`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Les données exportées n'étaient pas sélectionnées.
-- Listage de la structure de la table dvdtheque. BATCH_STEP_EXECUTION
DROP TABLE IF EXISTS `BATCH_STEP_EXECUTION`;
CREATE TABLE IF NOT EXISTS `BATCH_STEP_EXECUTION` (
  `STEP_EXECUTION_ID` bigint(20) NOT NULL,
  `VERSION` bigint(20) NOT NULL,
  `STEP_NAME` varchar(100) NOT NULL,
  `JOB_EXECUTION_ID` bigint(20) NOT NULL,
  `START_TIME` datetime NOT NULL,
  `END_TIME` datetime DEFAULT NULL,
  `STATUS` varchar(10) DEFAULT NULL,
  `COMMIT_COUNT` bigint(20) DEFAULT NULL,
  `READ_COUNT` bigint(20) DEFAULT NULL,
  `FILTER_COUNT` bigint(20) DEFAULT NULL,
  `WRITE_COUNT` bigint(20) DEFAULT NULL,
  `READ_SKIP_COUNT` bigint(20) DEFAULT NULL,
  `WRITE_SKIP_COUNT` bigint(20) DEFAULT NULL,
  `PROCESS_SKIP_COUNT` bigint(20) DEFAULT NULL,
  `ROLLBACK_COUNT` bigint(20) DEFAULT NULL,
  `EXIT_CODE` varchar(2500) DEFAULT NULL,
  `EXIT_MESSAGE` varchar(2500) DEFAULT NULL,
  `LAST_UPDATED` datetime DEFAULT NULL,
  PRIMARY KEY (`STEP_EXECUTION_ID`),
  KEY `JOB_EXEC_STEP_FK` (`JOB_EXECUTION_ID`),
  CONSTRAINT `JOB_EXEC_STEP_FK` FOREIGN KEY (`JOB_EXECUTION_ID`) REFERENCES `BATCH_JOB_EXECUTION` (`JOB_EXECUTION_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Les données exportées n'étaient pas sélectionnées.
-- Listage de la structure de la table dvdtheque. BATCH_STEP_EXECUTION_CONTEXT
DROP TABLE IF EXISTS `BATCH_STEP_EXECUTION_CONTEXT`;
CREATE TABLE IF NOT EXISTS `BATCH_STEP_EXECUTION_CONTEXT` (
  `STEP_EXECUTION_ID` bigint(20) NOT NULL,
  `SHORT_CONTEXT` varchar(2500) NOT NULL,
  `SERIALIZED_CONTEXT` text,
  PRIMARY KEY (`STEP_EXECUTION_ID`),
  CONSTRAINT `STEP_EXEC_CTX_FK` FOREIGN KEY (`STEP_EXECUTION_ID`) REFERENCES `BATCH_STEP_EXECUTION` (`STEP_EXECUTION_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Les données exportées n'étaient pas sélectionnées.
-- Listage de la structure de la table dvdtheque. BATCH_STEP_EXECUTION_SEQ
DROP TABLE IF EXISTS `BATCH_STEP_EXECUTION_SEQ`;
CREATE TABLE IF NOT EXISTS `BATCH_STEP_EXECUTION_SEQ` (
  `ID` bigint(20) NOT NULL,
  `UNIQUE_KEY` char(1) NOT NULL,
  UNIQUE KEY `UNIQUE_KEY_UN` (`UNIQUE_KEY`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Les données exportées n'étaient pas sélectionnées.
-- Listage de la structure de la table dvdtheque. DVD
DROP TABLE IF EXISTS `DVD`;
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
) ENGINE=InnoDB AUTO_INCREMENT=72609 DEFAULT CHARSET=latin1;

-- Les données exportées n'étaient pas sélectionnées.
-- Listage de la structure de la table dvdtheque. FILM
DROP TABLE IF EXISTS `FILM`;
CREATE TABLE IF NOT EXISTS `FILM` (
  `ID` bigint(11) NOT NULL AUTO_INCREMENT,
  `VU` tinyint(4) NOT NULL DEFAULT '1',
  `HOMEPAGE` varchar(255) DEFAULT '1',
  `ANNEE` int(11) NOT NULL,
  `TITRE` varchar(255) NOT NULL,
  `TITRE_O` varchar(255) DEFAULT NULL,
  `ID_DVD` int(11) DEFAULT NULL,
  `ORIGINE` int(11) NOT NULL,
  `POSTER_PATH` varchar(255) DEFAULT NULL,
  `TMDB_ID` bigint(20) DEFAULT NULL,
  `OVERVIEW` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  `RUNTIME` int(11) DEFAULT NULL,
  `DATE_SORTIE` date NOT NULL,
  `DATE_INSERTION` date NOT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=73999 DEFAULT CHARSET=latin1;

-- Les données exportées n'étaient pas sélectionnées.
-- Listage de la structure de la table dvdtheque. FILM_acteurs
DROP TABLE IF EXISTS `FILM_acteurs`;
CREATE TABLE IF NOT EXISTS `FILM_acteurs` (
  `FILM_ID` bigint(20) NOT NULL,
  `ACTEURS_ID` bigint(20) NOT NULL,
  PRIMARY KEY (`FILM_ID`,`ACTEURS_ID`),
  KEY `FK_ACTEURS_ID` (`ACTEURS_ID`),
  CONSTRAINT `FK_ACTEURS_ID` FOREIGN KEY (`ACTEURS_ID`) REFERENCES `PERSONNE` (`ID`),
  CONSTRAINT `FK_FILM_ID` FOREIGN KEY (`FILM_ID`) REFERENCES `FILM` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Les données exportées n'étaient pas sélectionnées.
-- Listage de la structure de la table dvdtheque. FILM_genres
DROP TABLE IF EXISTS `FILM_genres`;
CREATE TABLE IF NOT EXISTS `FILM_genres` (
  `FILM_ID` bigint(20) NOT NULL,
  `GENRES_ID` bigint(20) NOT NULL,
  PRIMARY KEY (`FILM_ID`,`GENRES_ID`),
  KEY `FK_GENRES_ID` (`GENRES_ID`),
  CONSTRAINT `FK_GENRES_ID` FOREIGN KEY (`GENRES_ID`) REFERENCES `GENRE` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Les données exportées n'étaient pas sélectionnées.
-- Listage de la structure de la table dvdtheque. FILM_realisateurs
DROP TABLE IF EXISTS `FILM_realisateurs`;
CREATE TABLE IF NOT EXISTS `FILM_realisateurs` (
  `FILM_ID` bigint(20) NOT NULL,
  `REALISATEURS_ID` bigint(20) NOT NULL,
  PRIMARY KEY (`FILM_ID`,`REALISATEURS_ID`),
  KEY `FK_REALISATEURS_ID` (`REALISATEURS_ID`),
  CONSTRAINT `FK_REALISATEURS_ID` FOREIGN KEY (`REALISATEURS_ID`) REFERENCES `PERSONNE` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Les données exportées n'étaient pas sélectionnées.
-- Listage de la structure de la table dvdtheque. GENRE
DROP TABLE IF EXISTS `GENRE`;
CREATE TABLE IF NOT EXISTS `GENRE` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `NAME` varchar(2500) DEFAULT NULL,
  `TMDB_ID` int(11) NOT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=13462 DEFAULT CHARSET=latin1;

-- Les données exportées n'étaient pas sélectionnées.
-- Listage de la structure de la table dvdtheque. PERSONNE
DROP TABLE IF EXISTS `PERSONNE`;
CREATE TABLE IF NOT EXISTS `PERSONNE` (
  `ID` bigint(11) NOT NULL AUTO_INCREMENT,
  `NOM` varchar(255) NOT NULL,
  `PRENOM` varchar(255) DEFAULT NULL,
  `DATE_N` date DEFAULT NULL,
  `ID_PAYS` int(11) DEFAULT NULL,
  `PROFILE_PATH` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=450979 DEFAULT CHARSET=latin1;

-- Les données exportées n'étaient pas sélectionnées.
-- Listage de la structure de la table dvdtheque. ROLES
DROP TABLE IF EXISTS `ROLES`;
CREATE TABLE IF NOT EXISTS `ROLES` (
  `ID` int(11) NOT NULL,
  `NAME` varchar(255) NOT NULL,
  UNIQUE KEY `ID` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Les données exportées n'étaient pas sélectionnées.
-- Listage de la structure de la table dvdtheque. USER
DROP TABLE IF EXISTS `USER`;
CREATE TABLE IF NOT EXISTS `USER` (
  `ID` int(11) NOT NULL,
  `USERNAME` varchar(255) NOT NULL,
  `PASSWORD` varchar(255) NOT NULL,
  `EMAIL` varchar(255) DEFAULT NULL,
  `FIRSTNAME` varchar(255) DEFAULT NULL,
  `LASTNAME` varchar(255) DEFAULT NULL,
  UNIQUE KEY `ID` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Les données exportées n'étaient pas sélectionnées.
-- Listage de la structure de la table dvdtheque. USERROLES
DROP TABLE IF EXISTS `USERROLES`;
CREATE TABLE IF NOT EXISTS `USERROLES` (
  `ID` int(11) NOT NULL,
  `USER_ID` int(11) NOT NULL,
  `ROLE_ID` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Les données exportées n'étaient pas sélectionnées.
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IF(@OLD_FOREIGN_KEY_CHECKS IS NULL, 1, @OLD_FOREIGN_KEY_CHECKS) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
