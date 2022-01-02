-- Database: dvdthequedb

DROP DATABASE IF EXISTS dvdthequedb;

CREATE DATABASE dvdthequedb
    WITH 
    OWNER = postgres
    ENCODING = 'UTF8'
    LC_COLLATE = 'French_France.1252'
    LC_CTYPE = 'French_France.1252'
    TABLESPACE = pg_default
    CONNECTION LIMIT = -1;

DROP TABLE IF EXISTS "dvdtheque-service".film_realisateur;

DROP TABLE IF EXISTS "dvdtheque-service".film_genre;

DROP TABLE IF EXISTS "dvdtheque-service".film_critiques_presse;

DROP TABLE IF EXISTS "dvdtheque-service".film_acteur;

DROP TABLE IF EXISTS "dvdtheque-service".critiques_presse;

DROP TABLE IF EXISTS "dvdtheque-service".genre;

DROP TABLE IF EXISTS "dvdtheque-service".personne;

DROP TABLE IF EXISTS "dvdtheque-service".film;

DROP TABLE IF EXISTS "dvdtheque-service".dvd;

CREATE TABLE IF NOT EXISTS "dvdtheque-service".dvd (
  id serial PRIMARY KEY,
  annee INT DEFAULT NULL,
  zone INT NOT NULL,
  edition varchar(255) DEFAULT NULL,
  date_rip TIMESTAMP DEFAULT NULL,
  format varchar(7) DEFAULT NULL,
  ripped BOOL NOT NULL,
  date_sortie TIMESTAMP DEFAULT NULL
);

CREATE TABLE IF NOT EXISTS "dvdtheque-service".film (
  id serial PRIMARY KEY,
  vu BOOL NOT NULL,
  homepage varchar(255) DEFAULT 1,
  annee INT NOT NULL,
  titre varchar(255) NOT NULL ,
  titre_O varchar(255) DEFAULT NULL ,
  dvd_id INT DEFAULT NULL,
  origine INT NOT NULL,
  poster_path varchar(255) DEFAULT NULL,
  tmdb_id INT DEFAULT NULL,
  overview text,
  runtime INT DEFAULT NULL,
  date_sortie TIMESTAMP NOT NULL,
  date_insertion TIMESTAMP NOT NULL,
  FOREIGN KEY (dvd_id)
      REFERENCES "dvdtheque-service".dvd (id)
);

CREATE TABLE IF NOT EXISTS "dvdtheque-service".personne (
  id serial PRIMARY KEY,
  nom varchar(255) NOT NULL,
  prenom varchar(255) DEFAULT NULL,
  date_n TIMESTAMP DEFAULT NULL,
  id_pays INT DEFAULT NULL,
  profile_path varchar(255) DEFAULT NULL
);

CREATE TABLE IF NOT EXISTS "dvdtheque-service".genre (
  id serial PRIMARY KEY,
  name varchar(2500) DEFAULT NULL,
  tmdb_id INT NOT NULL
);

CREATE TABLE IF NOT EXISTS "dvdtheque-service".critiques_presse (
  id serial PRIMARY KEY,
  code INT NOT NULL,
  nom_source varchar(50) NOT NULL ,
  auteur varchar(50) DEFAULT NULL ,
  critique varchar(500),
  note float NOT NULL
);

CREATE TABLE IF NOT EXISTS "dvdtheque-service".film_acteur (
  film_id INT NOT NULL,
  acteur_id INT NOT NULL,
  PRIMARY KEY (film_id, acteur_id),
  FOREIGN KEY (film_id)
      REFERENCES "dvdtheque-service".film (id),
  FOREIGN KEY (acteur_id)
      REFERENCES "dvdtheque-service".personne (id)
);

CREATE TABLE IF NOT EXISTS "dvdtheque-service".film_critiques_presse (
  film_id INT NOT NULL,
  critiques_presse_id INT NOT NULL,
  PRIMARY KEY (film_id, critiques_presse_id),
  FOREIGN KEY (film_id)
      REFERENCES "dvdtheque-service".film (id),
  FOREIGN KEY (critiques_presse_id)
      REFERENCES "dvdtheque-service".critiques_presse (id)
);

CREATE TABLE IF NOT EXISTS "dvdtheque-service".film_genre (
  film_id INT NOT NULL,
  genre_id INT NOT NULL,
  PRIMARY KEY (film_id, genre_id),
  FOREIGN KEY (film_id)
      REFERENCES "dvdtheque-service".film (id),
  FOREIGN KEY (genre_id)
      REFERENCES "dvdtheque-service".genre (id)
);

CREATE TABLE IF NOT EXISTS "dvdtheque-service".film_realisateur (
  film_id INT NOT NULL,
  realisateur_id INT NOT NULL,
  PRIMARY KEY (film_id, realisateur_id),
  FOREIGN KEY (film_id)
      REFERENCES "dvdtheque-service".film (id),
  FOREIGN KEY (realisateur_id)
      REFERENCES "dvdtheque-service".personne (id)
);
