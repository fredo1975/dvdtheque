INSERT INTO user(username, password) VALUES ('fredo', '$2y$10$kzJcS22.xtPJGCLwO3PWIObWS9Rh.dOe7SmUGxSyvTqwiQ2YPZJEW');

INSERT INTO roles (ID, NAME) VALUES (1, 'ROLE_USER');
INSERT INTO roles (ID, NAME) VALUES (2, 'ROLE_ADMIN');

INSERT INTO userroles (ID, USER_ID,ROLE_ID) VALUES (1,1,1);
INSERT INTO userroles (ID, USER_ID,ROLE_ID) VALUES (2,1,2);
