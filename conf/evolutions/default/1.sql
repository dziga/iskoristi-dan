# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table activity (
  id                        bigint not null,
  name                      varchar(255),
  description               clob,
  type_id                   bigint,
  active                    boolean,
  constraint uq_activity_name unique (name),
  constraint pk_activity primary key (id))
;

create table activity_type (
  id                        bigint not null,
  name                      varchar(255),
  constraint uq_activity_type_name unique (name),
  constraint pk_activity_type primary key (id))
;

create table image (
  id                        bigint not null,
  url                       varchar(255),
  constraint uq_image_url unique (url),
  constraint pk_image primary key (id))
;

create table recommendation (
  id                        bigint not null,
  name                      varchar(255),
  description               clob,
  start_time                timestamp,
  end_time                  timestamp,
  location                  varchar(255),
  active                    boolean,
  constraint uq_recommendation_name unique (name),
  constraint pk_recommendation primary key (id))
;

create table tag (
  id                        bigint not null,
  name                      varchar(255),
  constraint uq_tag_name unique (name),
  constraint pk_tag primary key (id))
;


create table activity_recommendation (
  activity_id                    bigint not null,
  recommendation_id              bigint not null,
  constraint pk_activity_recommendation primary key (activity_id, recommendation_id))
;

create table activity_tag (
  activity_id                    bigint not null,
  tag_id                         bigint not null,
  constraint pk_activity_tag primary key (activity_id, tag_id))
;

create table activity_image (
  activity_id                    bigint not null,
  image_id                       bigint not null,
  constraint pk_activity_image primary key (activity_id, image_id))
;

create table recommendation_image (
  recommendation_id              bigint not null,
  image_id                       bigint not null,
  constraint pk_recommendation_image primary key (recommendation_id, image_id))
;
create sequence activity_seq;

create sequence activity_type_seq;

create sequence image_seq;

create sequence recommendation_seq;

create sequence tag_seq;

alter table activity add constraint fk_activity_type_1 foreign key (type_id) references activity_type (id) on delete restrict on update restrict;
create index ix_activity_type_1 on activity (type_id);



alter table activity_recommendation add constraint fk_activity_recommendation_ac_01 foreign key (activity_id) references activity (id) on delete restrict on update restrict;

alter table activity_recommendation add constraint fk_activity_recommendation_re_02 foreign key (recommendation_id) references recommendation (id) on delete restrict on update restrict;

alter table activity_tag add constraint fk_activity_tag_activity_01 foreign key (activity_id) references activity (id) on delete restrict on update restrict;

alter table activity_tag add constraint fk_activity_tag_tag_02 foreign key (tag_id) references tag (id) on delete restrict on update restrict;

alter table activity_image add constraint fk_activity_image_activity_01 foreign key (activity_id) references activity (id) on delete restrict on update restrict;

alter table activity_image add constraint fk_activity_image_image_02 foreign key (image_id) references image (id) on delete restrict on update restrict;

alter table recommendation_image add constraint fk_recommendation_image_recom_01 foreign key (recommendation_id) references recommendation (id) on delete restrict on update restrict;

alter table recommendation_image add constraint fk_recommendation_image_image_02 foreign key (image_id) references image (id) on delete restrict on update restrict;

# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists activity;

drop table if exists activity_recommendation;

drop table if exists activity_tag;

drop table if exists activity_image;

drop table if exists activity_type;

drop table if exists image;

drop table if exists recommendation;

drop table if exists recommendation_image;

drop table if exists tag;

SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists activity_seq;

drop sequence if exists activity_type_seq;

drop sequence if exists image_seq;

drop sequence if exists recommendation_seq;

drop sequence if exists tag_seq;

