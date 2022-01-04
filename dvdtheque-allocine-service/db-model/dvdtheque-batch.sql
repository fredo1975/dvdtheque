CREATE SCHEMA IF NOT EXISTS "dvdtheque-allocine-service"
    AUTHORIZATION postgres;


DROP TABLE IF EXISTS "dvdtheque-allocine-service".critiquepresse;

DROP TABLE IF EXISTS "dvdtheque-allocine-service".fichefilm;

CREATE TABLE IF NOT EXISTS "dvdtheque-allocine-service".fichefilm (
  id serial PRIMARY KEY,
  allocine_film_id INT NOT NULL,
  url varchar(255) NOT NULL,
  page_number INT NOT NULL,
  title varchar(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS "dvdtheque-allocine-service".critiquepresse (
  id serial PRIMARY KEY,
  news_source varchar(255) NOT NULL,
  rating float NOT NULL,
  body TEXT NOT NULL,
  author varchar(255) NOT NULL,
  fiche_film_id INT NOT NULL,
  FOREIGN KEY (fiche_film_id)
      REFERENCES "dvdtheque-allocine-service".fichefilm (id)
);
