--drop table if exists plantmap_role cascade;
--drop table if exists plantmap_user_role cascade;
--drop table if exists plantmap_upload_history cascade;
--drop table if exists plantmap_user cascade;

create table plantmap_role (
  id                        serial not null,
  name                      varchar(255) not null,
  constraint pk_plantmap_role primary key (id)
);

create table plantmap_upload_history (
  id                        serial not null,
  name                      varchar(255) not null,
  login                     varchar(255) not null,
  size                      bigint not null,
  date                      timestamp not null,
  constraint pk_plantmap_upload_history primary key (id)
);

create table plantmap_user (
  id                        serial not null,
  login                     varchar(255) not null,
  password                  varchar(255) not null,
  email                     varchar(255) not null,
  constraint uq_plantmap_user_login unique (login),
  constraint pk_plantmap_user primary key (id)
);

create table plantmap_user_role (
  plantmap_user_id               integer not null,
  plantmap_role_id               integer not null,
  constraint pk_plantmap_user_role primary key (plantmap_user_id, plantmap_role_id)
);

alter table plantmap_user_role add constraint fk_plantmap_user_role_plantma_01 foreign key (plantmap_user_id) references plantmap_user (id);
alter table plantmap_user_role add constraint fk_plantmap_user_role_plantma_02 foreign key (plantmap_role_id) references plantmap_role (id);

-- roles
INSERT INTO plantmap_role (id, name) VALUES (1, 'Admin');
INSERT INTO plantmap_role (id, name) VALUES (2, 'Geomatician');
INSERT INTO plantmap_role (id, name) VALUES (3, 'User');

-- user (1234 as password)
INSERT INTO plantmap_user (id, login, password, email) VALUES (1, 'admin', '$2a$10$wvYTkzUA4uHoLriW0gg9v.af0F1XHeTaRatBg6Huy/FLH8zLvKIYW', 'admin@fcbn.fr');

-- Admin
INSERT INTO plantmap_user_role (plantmap_user_id, plantmap_role_id) VALUES (1, 1);
INSERT INTO plantmap_user_role (plantmap_user_id, plantmap_role_id) VALUES (1, 2);
INSERT INTO plantmap_user_role (plantmap_user_id, plantmap_role_id) VALUES (1, 3);
