# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table activity (
  id                        bigint auto_increment not null,
  name                      varchar(255),
  description               longtext,
  start_time                datetime,
  end_time                  datetime,
  image_url                 varchar(255),
  active                    tinyint(1) default 0,
  constraint uq_activity_name unique (name),
  constraint pk_activity primary key (id))
;

create table category (
  id                        bigint auto_increment not null,
  name                      varchar(255),
  type_id                   bigint,
  constraint uq_category_name unique (name),
  constraint pk_category primary key (id))
;

create table category_type (
  id                        bigint auto_increment not null,
  name                      varchar(255),
  constraint uq_category_type_name unique (name),
  constraint pk_category_type primary key (id))
;

create table tag (
  id                        bigint auto_increment not null,
  name                      varchar(255),
  constraint uq_tag_name unique (name),
  constraint pk_tag primary key (id))
;


create table activity_tag (
  activity_id                    bigint not null,
  tag_id                         bigint not null,
  constraint pk_activity_tag primary key (activity_id, tag_id))
;

create table category_activity (
  category_id                    bigint not null,
  activity_id                    bigint not null,
  constraint pk_category_activity primary key (category_id, activity_id))
;
alter table category add constraint fk_category_type_1 foreign key (type_id) references category_type (id) on delete restrict on update restrict;
create index ix_category_type_1 on category (type_id);



alter table activity_tag add constraint fk_activity_tag_activity_01 foreign key (activity_id) references activity (id) on delete restrict on update restrict;

alter table activity_tag add constraint fk_activity_tag_tag_02 foreign key (tag_id) references tag (id) on delete restrict on update restrict;

alter table category_activity add constraint fk_category_activity_category_01 foreign key (category_id) references category (id) on delete restrict on update restrict;

alter table category_activity add constraint fk_category_activity_activity_02 foreign key (activity_id) references activity (id) on delete restrict on update restrict;

# --- !Downs

SET FOREIGN_KEY_CHECKS=0;

drop table activity;

drop table activity_tag;

drop table category;

drop table category_activity;

drop table category_type;

drop table tag;

SET FOREIGN_KEY_CHECKS=1;

