CREATE TABLE authority (
  id integer,
  authority varchar(255),
  primary key (id)
);

CREATE TABLE credentials (
  id integer,
  enabled boolean not null,
  name varchar(255) not null,
  password varchar(255) not null,
  version integer,
  primary key (id)
);

CREATE TABLE credentials_authorities (
  credentials_id bigint not null,
  authorities_id bigint not null
);

INSERT INTO authority VALUES(0,'ROLE_OAUTH_ADMIN');
INSERT INTO authority VALUES(1,'ROLE_ADMIN');
INSERT INTO authority VALUES(2,'ROLE_USER');
INSERT INTO credentials VALUES(0,1,'oauth_admin','admin',0);
INSERT INTO credentials VALUES(1,1,'resource_admin','admin',0);
INSERT INTO credentials VALUES(2,1,'user','user',0);
INSERT INTO credentials_authorities VALUES(0,0);
INSERT INTO credentials_authorities VALUES(1,1);
INSERT INTO credentials_authorities VALUES(2,2);