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

CREATE TABLE `user` (
	`ID` INT NOT NULL AUTO_INCREMENT,
	`USERNAME` VARCHAR(255) NOT NULL COLLATE 'utf8mb4_general_ci',
	`PASSWORD` VARCHAR(255) NOT NULL COLLATE 'utf8mb4_general_ci',
	`EMAIL` VARCHAR(255) NULL DEFAULT NULL COLLATE 'utf8mb4_general_ci',
	`FIRSTNAME` VARCHAR(255) NULL DEFAULT NULL COLLATE 'utf8mb4_general_ci',
	`LASTNAME` VARCHAR(255) NULL DEFAULT NULL COLLATE 'utf8mb4_general_ci',
	UNIQUE INDEX `ID` (`ID`)
)
COLLATE='utf8mb4_general_ci'
ENGINE=InnoDB
AUTO_INCREMENT=2
;