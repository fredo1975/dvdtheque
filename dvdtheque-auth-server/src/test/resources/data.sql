create table oauth_client_details (
  client_id VARCHAR(256) PRIMARY KEY,
  resource_ids VARCHAR(256),
  client_secret VARCHAR(256),
  scope VARCHAR(256),
  authorized_grant_types VARCHAR(256),
  web_server_redirect_uri VARCHAR(256),
  authorities VARCHAR(256),
  access_token_validity INTEGER,
  refresh_token_validity INTEGER,
  additional_information VARCHAR(4096),
  autoapprove VARCHAR(256)
);

create table oauth_client_token (
  token_id VARCHAR(256),
  token VARBINARY(1024),
  authentication_id VARCHAR(256),
  user_name VARCHAR(256),
  client_id VARCHAR(256)
);

create table oauth_access_token (
  token_id VARCHAR(256),
  token VARBINARY(1024),
  authentication_id VARCHAR(256),
  user_name VARCHAR(256),
  client_id VARCHAR(256),
  authentication BLOB,
  refresh_token VARCHAR(256)
);

create table oauth_refresh_token (
  token_id VARCHAR(256),
  token VARBINARY(1024),
  authentication BLOB
);

create table oauth_code (
  code VARCHAR(256), authentication BLOB
);

create table oauth_approvals (
  userId VARCHAR(256),
  clientId VARCHAR(256),
  scope VARCHAR(256),
  status VARCHAR(10),
  expiresAt TIMESTAMP,
  lastModifiedAt TIMESTAMP
);

INSERT INTO user(username, password) VALUES ('fredo', '{bcrypt}$2y$10$kzJcS22.xtPJGCLwO3PWIObWS9Rh.dOe7SmUGxSyvTqwiQ2YPZJEW');

INSERT INTO roles (ID, NAME) VALUES (1, 'ROLE_USER');

INSERT INTO userroles (ID, USER_ID,ROLE_ID) VALUES (1,1,1);

INSERT INTO oauth_client_details (client_id, client_secret, scope, authorized_grant_types, authorities, access_token_validity) VALUES ('dvdtheque-clientId', '$2y$10$8AMGjnTCfq2uuFcns5TgxuodWUN8hgHZWk7Qp8pNS.m8TjES1KZVu', 'read,write', 'password,refresh_token,client_credentials', 'ROLE_CLIENT', 300);

INSERT INTO oauth_client_details (client_id, client_secret, scope, authorized_grant_types, authorities, access_token_validity) VALUES ('gateway', '$2y$10$kcfZdxhX5hFMKReerEMZ1u1cG1udXLnQrheQ7vz2SWIaGfc.CnsFq', 'read', 'authorization_code', 'ROLE_CLIENT', 300);

