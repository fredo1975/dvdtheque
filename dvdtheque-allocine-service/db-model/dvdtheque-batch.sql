CREATE SCHEMA IF NOT EXISTS "dvdtheque-allocine-service"
    AUTHORIZATION postgres;

DROP TABLE IF EXISTS "dvdtheque-allocine-service".fiche_film_critiquepresse;

DROP TABLE IF EXISTS "dvdtheque-allocine-service".critique_presse;

DROP TABLE IF EXISTS "dvdtheque-allocine-service".fiche_film;

CREATE TABLE IF NOT EXISTS "dvdtheque-allocine-service".fiche_film (
  id serial PRIMARY KEY,
  fiche_film INT NOT NULL,
  url varchar(255) NOT NULL,
  page_number INT NOT NULL,
  title varchar(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS "dvdtheque-allocine-service".critique_presse (
  id serial PRIMARY KEY,
  news_source varchar(255) NOT NULL,
  rating float NOT NULL,
  body TEXT NOT NULL,
  author varchar(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS "dvdtheque-allocine-service".fiche_film_critiquepresse (
  fichefilm_id INT NOT NULL,
  critiquepresse_id INT NOT NULL,
  PRIMARY KEY (fichefilm_id,critiquepresse_id),
  FOREIGN KEY (fichefilm_id)
      REFERENCES "dvdtheque-allocine-service".fiche_film (id),
  FOREIGN KEY (critiquepresse_id)
      REFERENCES "dvdtheque-allocine-service".critique_presse (id)
);