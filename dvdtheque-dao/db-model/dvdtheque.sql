-- phpMyAdmin SQL Dump
-- version 3.4.11.1deb2+deb7u8
-- http://www.phpmyadmin.net
--
-- Client: localhost
-- Généré le: Dim 10 Mars 2019 à 14:32
-- Version du serveur: 5.5.60
-- Version de PHP: 5.4.45-0+deb7u14

SET SQL_MODE="NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";

--
-- Base de données: `dvdtheque`
--

-- --------------------------------------------------------

--
-- Structure de la table `BATCH_JOB_EXECUTION`
--

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
  KEY `JOB_INST_EXEC_FK` (`JOB_INSTANCE_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Structure de la table `BATCH_JOB_EXECUTION_CONTEXT`
--

CREATE TABLE IF NOT EXISTS `BATCH_JOB_EXECUTION_CONTEXT` (
  `JOB_EXECUTION_ID` bigint(20) NOT NULL,
  `SHORT_CONTEXT` varchar(2500) NOT NULL,
  `SERIALIZED_CONTEXT` text,
  PRIMARY KEY (`JOB_EXECUTION_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Structure de la table `BATCH_JOB_EXECUTION_PARAMS`
--

CREATE TABLE IF NOT EXISTS `BATCH_JOB_EXECUTION_PARAMS` (
  `JOB_EXECUTION_ID` bigint(20) NOT NULL,
  `TYPE_CD` varchar(6) NOT NULL,
  `KEY_NAME` varchar(100) NOT NULL,
  `STRING_VAL` varchar(250) DEFAULT NULL,
  `DATE_VAL` datetime DEFAULT NULL,
  `LONG_VAL` bigint(20) DEFAULT NULL,
  `DOUBLE_VAL` double DEFAULT NULL,
  `IDENTIFYING` char(1) NOT NULL,
  KEY `JOB_EXEC_PARAMS_FK` (`JOB_EXECUTION_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Structure de la table `BATCH_JOB_EXECUTION_SEQ`
--

CREATE TABLE IF NOT EXISTS `BATCH_JOB_EXECUTION_SEQ` (
  `ID` bigint(20) NOT NULL,
  `UNIQUE_KEY` char(1) NOT NULL,
  UNIQUE KEY `UNIQUE_KEY_UN` (`UNIQUE_KEY`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Structure de la table `BATCH_JOB_INSTANCE`
--

CREATE TABLE IF NOT EXISTS `BATCH_JOB_INSTANCE` (
  `JOB_INSTANCE_ID` bigint(20) NOT NULL,
  `VERSION` bigint(20) DEFAULT NULL,
  `JOB_NAME` varchar(100) NOT NULL,
  `JOB_KEY` varchar(32) NOT NULL,
  PRIMARY KEY (`JOB_INSTANCE_ID`),
  UNIQUE KEY `JOB_INST_UN` (`JOB_NAME`,`JOB_KEY`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Structure de la table `BATCH_JOB_SEQ`
--

CREATE TABLE IF NOT EXISTS `BATCH_JOB_SEQ` (
  `ID` bigint(20) NOT NULL,
  `UNIQUE_KEY` char(1) NOT NULL,
  UNIQUE KEY `UNIQUE_KEY_UN` (`UNIQUE_KEY`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Structure de la table `BATCH_STEP_EXECUTION`
--

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
  KEY `JOB_EXEC_STEP_FK` (`JOB_EXECUTION_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Structure de la table `BATCH_STEP_EXECUTION_CONTEXT`
--

CREATE TABLE IF NOT EXISTS `BATCH_STEP_EXECUTION_CONTEXT` (
  `STEP_EXECUTION_ID` bigint(20) NOT NULL,
  `SHORT_CONTEXT` varchar(2500) NOT NULL,
  `SERIALIZED_CONTEXT` text,
  PRIMARY KEY (`STEP_EXECUTION_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Structure de la table `BATCH_STEP_EXECUTION_SEQ`
--

CREATE TABLE IF NOT EXISTS `BATCH_STEP_EXECUTION_SEQ` (
  `ID` bigint(20) NOT NULL,
  `UNIQUE_KEY` char(1) NOT NULL,
  UNIQUE KEY `UNIQUE_KEY_UN` (`UNIQUE_KEY`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Structure de la table `DVD`
--

CREATE TABLE IF NOT EXISTS `DVD` (
  `ID` bigint(11) NOT NULL AUTO_INCREMENT,
  `ANNEE` int(11) DEFAULT NULL,
  `ZONE` int(11) NOT NULL,
  `EDITION` varchar(255) DEFAULT NULL,
  DATE_RIP date DEFAULT NULL,
  FORMAT varchar(7),
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=22460 ;

-- --------------------------------------------------------

--
-- Structure de la table `FILM`
--

CREATE TABLE IF NOT EXISTS `FILM` (
  `ID` bigint(11) NOT NULL AUTO_INCREMENT,
  `ANNEE` int(11) NOT NULL,
  `TITRE` varchar(255) NOT NULL,
  `TITRE_O` varchar(255) DEFAULT NULL,
  `ID_DVD` int(11) NOT NULL,
  `RIPPED` tinyint(1) NOT NULL,
  `POSTER_PATH` varchar(255) DEFAULT NULL,
  `TMDB_ID` bigint(20) DEFAULT NULL,
  `OVERVIEW` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=22456 ;

-- --------------------------------------------------------

--
-- Structure de la table `FILM_acteurs`
--

CREATE TABLE IF NOT EXISTS `FILM_acteurs` (
  `FILM_ID` bigint(20) NOT NULL,
  `ACTEURS_ID` bigint(20) NOT NULL,
  PRIMARY KEY (`FILM_ID`,`ACTEURS_ID`),
  KEY `FK_ACTEURS_ID` (`ACTEURS_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Structure de la table `FILM_realisateurs`
--

CREATE TABLE IF NOT EXISTS `FILM_realisateurs` (
  `FILM_ID` bigint(20) NOT NULL,
  `REALISATEURS_ID` bigint(20) NOT NULL,
  PRIMARY KEY (`FILM_ID`,`REALISATEURS_ID`),
  KEY `FK_REALISATEURS_ID` (`REALISATEURS_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Structure de la table `PERSONNE`
--

CREATE TABLE IF NOT EXISTS `PERSONNE` (
  `ID` bigint(11) NOT NULL AUTO_INCREMENT,
  `NOM` varchar(255) NOT NULL,
  `PRENOM` varchar(255) DEFAULT NULL,
  `DATE_N` date DEFAULT NULL,
  `ID_PAYS` int(11) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=148289 ;

-- --------------------------------------------------------

--
-- Structure de la table `ROLES`
--

CREATE TABLE IF NOT EXISTS `ROLES` (
  `ID` int(11) NOT NULL,
  `NAME` varchar(255) NOT NULL,
  UNIQUE KEY `ID` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Structure de la table `USER`
--

CREATE TABLE IF NOT EXISTS `USER` (
  `ID` int(11) NOT NULL,
  `USERNAME` varchar(255) NOT NULL,
  `PASSWORD` varchar(255) NOT NULL,
  `EMAIL` varchar(255) DEFAULT NULL,
  `FIRSTNAME` varchar(255) DEFAULT NULL,
  `LASTNAME` varchar(255) DEFAULT NULL,
  UNIQUE KEY `ID` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Structure de la table `USERROLES`
--

CREATE TABLE IF NOT EXISTS `USERROLES` (
  `ID` int(11) NOT NULL,
  `USER_ID` int(11) NOT NULL,
  `ROLE_ID` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Contraintes pour les tables exportées
--

--
-- Contraintes pour la table `BATCH_JOB_EXECUTION`
--
ALTER TABLE `BATCH_JOB_EXECUTION`
  ADD CONSTRAINT `JOB_INST_EXEC_FK` FOREIGN KEY (`JOB_INSTANCE_ID`) REFERENCES `BATCH_JOB_INSTANCE` (`JOB_INSTANCE_ID`);

--
-- Contraintes pour la table `BATCH_JOB_EXECUTION_CONTEXT`
--
ALTER TABLE `BATCH_JOB_EXECUTION_CONTEXT`
  ADD CONSTRAINT `JOB_EXEC_CTX_FK` FOREIGN KEY (`JOB_EXECUTION_ID`) REFERENCES `BATCH_JOB_EXECUTION` (`JOB_EXECUTION_ID`);

--
-- Contraintes pour la table `BATCH_JOB_EXECUTION_PARAMS`
--
ALTER TABLE `BATCH_JOB_EXECUTION_PARAMS`
  ADD CONSTRAINT `JOB_EXEC_PARAMS_FK` FOREIGN KEY (`JOB_EXECUTION_ID`) REFERENCES `BATCH_JOB_EXECUTION` (`JOB_EXECUTION_ID`);

--
-- Contraintes pour la table `BATCH_STEP_EXECUTION`
--
ALTER TABLE `BATCH_STEP_EXECUTION`
  ADD CONSTRAINT `JOB_EXEC_STEP_FK` FOREIGN KEY (`JOB_EXECUTION_ID`) REFERENCES `BATCH_JOB_EXECUTION` (`JOB_EXECUTION_ID`);

--
-- Contraintes pour la table `BATCH_STEP_EXECUTION_CONTEXT`
--
ALTER TABLE `BATCH_STEP_EXECUTION_CONTEXT`
  ADD CONSTRAINT `STEP_EXEC_CTX_FK` FOREIGN KEY (`STEP_EXECUTION_ID`) REFERENCES `BATCH_STEP_EXECUTION` (`STEP_EXECUTION_ID`);

--
-- Contraintes pour la table `FILM_acteurs`
--
ALTER TABLE `FILM_acteurs`
  ADD CONSTRAINT `FK_FILM_ID` FOREIGN KEY (`FILM_ID`) REFERENCES `FILM` (`ID`),
  ADD CONSTRAINT `FK_ACTEURS_ID` FOREIGN KEY (`ACTEURS_ID`) REFERENCES `PERSONNE` (`ID`);

--
-- Contraintes pour la table `FILM_realisateurs`
--
ALTER TABLE `FILM_realisateurs`
  ADD CONSTRAINT `FK_REALISATEURS_ID` FOREIGN KEY (`REALISATEURS_ID`) REFERENCES `PERSONNE` (`ID`);
