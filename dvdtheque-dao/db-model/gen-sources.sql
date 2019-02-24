
-- 
-- Structure de la table `DVD`
-- 

CREATE TABLE `DVD` (
  `ID` int(11) NOT NULL auto_increment,
  `ANNEE` int(11) default NULL,
  `ZONE` int(11) NOT NULL,
  `EDITION` varchar(255) default NULL,
  PRIMARY KEY  (`ID`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

-- 
-- Structure de la table `FILM`
-- 

CREATE TABLE `FILM` (
  `ID` int(11) NOT NULL auto_increment,
  `ANNEE` int(11) NOT NULL,
  `TITRE` varchar(255) NOT NULL,
  `TITRE_O` varchar(255) default NULL,
  `ID_DVD` int(11) NOT NULL,
  `RIPPED` int(1) DEFAULT NULL,
  'POSTER_PATH' varchar(255) default NULL,
  'TMDB_ID' BIGINT default NULL,
  PRIMARY KEY  (`ID`),
  KEY `FK20ED8438B0040A` (`ID`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;

-- 
-- Structure de la table `PAYS`
-- 

CREATE TABLE `PAYS` (
  `ID` int(11) NOT NULL auto_increment,
  `LIB` varchar(255) NOT NULL,
  `I18N` varchar(4) NOT NULL,
  PRIMARY KEY  (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

-- 
-- Structure de la table `PERSONNE`
-- 

CREATE TABLE `PERSONNE` (
  `ID` int(11) NOT NULL auto_increment,
  `NOM` varchar(255) NOT NULL,
  `PRENOM` varchar(255) default NULL,
  `DATE_N` date default NULL,
  `ID_PAYS` int(11) default NULL,
  PRIMARY KEY  (`ID`),
  KEY `FK491017CC9B32005E` (`ID_PAYS`),
  KEY `FK491017CC38B0040A` (`ID`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;

-- 
-- Structure de la table `ROLES`
-- 

CREATE TABLE `ROLES` (
  `ID` int(11) NOT NULL,
  `NAME` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- 
-- Contenu de la table `ROLES`
-- 

INSERT INTO `ROLES` (`ID`, `NAME`) VALUES 
(1, 'admin'),
(2, 'user');

-- --------------------------------------------------------

CREATE TABLE `USERROLES` (
  `ID` int(11) NOT NULL,
  `USER_ID` int(11) NOT NULL,
  `ROLE_ID` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- 
-- Contenu de la table `USERROLES`
-- 

INSERT INTO `USERROLES` (`ID`, `USER_ID`, `ROLE_ID`) VALUES 
(1, 1, 1),
(2, 2, 1);
-- 
-- Structure de la table `TYPE`
-- 

CREATE TABLE `TYPE` (
  `ID` int(11) NOT NULL auto_increment,
  `LIB` varchar(255) NOT NULL,
  PRIMARY KEY  (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;

-- 
-- Contenu de la table `TYPE`
-- 

insert into TYPE (`LIB`) values ('Acteur');

insert into TYPE (`LIB`) values ('Realisateur');

-- --------------------------------------------------------

-- 
-- Structure de la table `TYPE_PERSONNE_FILM`
-- 

CREATE TABLE `TYPE_PERSONNE_FILM` (
  `ID` int(11) NOT NULL auto_increment,
  `ID_TYPE` int(11) NOT NULL,
  `ID_PERSONNE` int(11) NOT NULL,
  `ID_FILM` int(11) NOT NULL,
  PRIMARY KEY  (`ID`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

-- 
-- Structure de la table `USER`
-- 

CREATE TABLE `USER` (
  `ID` int(11) NOT NULL,
  `USERNAME` varchar(255) NOT NULL,
  `PASSWORD` varchar(255) NOT NULL,
  `EMAIL` varchar(255) default NULL,
  `FIRSTNAME` varchar(255) default NULL,
  `LASTNAME` varchar(255) default NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- 
-- Contenu de la table `USER`
-- 

INSERT INTO `USER` (`ID`, `USERNAME`, `PASSWORD`, `EMAIL`, `FIRSTNAME`, `LASTNAME`) VALUES 
(1, 'fredo', 'fredo', 'fredo', 'fredo', 'fredo'),
(2, 'asap', 'asap', 'asap', 'asap', 'asap');

-- --------------------------------------------------------

-- 
-- Structure de la table `USERROLES`
-- 

CREATE TABLE `USERROLES` (
  `ID` int(11) NOT NULL,
  `USER_ID` int(11) NOT NULL,
  `ROLE_ID` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- 
-- Contenu de la table `USERROLES`
-- 

INSERT INTO `USERROLES` (`ID`, `USER_ID`, `ROLE_ID`) VALUES 
(1, 1, 1),
(2, 2, 1);

