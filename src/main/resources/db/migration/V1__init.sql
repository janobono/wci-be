-- EXTENSION
create extension if not exists "uuid-ossp";
create extension if not exists unaccent;

-- TABLE
create table wci_application_property
(
    property_key   varchar(255) not null,
    property_group varchar(255) not null,
    property_value text         not null
);

create table wci_user
(
    id           uuid default uuid_generate_v4()    not null,
    username     varchar(255)                       not null,
    password     varchar(255)                       not null,
    title_before varchar(255),
    first_name   varchar(255) collate "sk-SK-x-icu" not null,
    mid_name     varchar(255) collate "sk-SK-x-icu",
    last_name    varchar(255) collate "sk-SK-x-icu" not null,
    title_after  varchar(255),
    email        varchar(255)                       not null,
    confirmed    bool                               not null,
    enabled      bool                               not null
);

create table wci_user_authority
(
    user_id   uuid         not null,
    authority varchar(255) not null
);

-- PK
alter table wci_application_property
    add primary key (property_key);

alter table wci_user
    add primary key (id);

-- UNIQUE
alter table wci_application_property
    add constraint u_wci_application_property unique (property_key, property_group);

alter table wci_user
    add constraint u_wci_user01 unique (username);

alter table wci_user
    add constraint u_wci_user02 unique (email);

alter table wci_user_authority
    add constraint u_wci_user_authority unique (user_id, authority);

-- FK
alter table wci_user_authority
    add foreign key (user_id) references wci_user (id) on delete cascade;

-- INDEX
create index idx_wci_application_property on wci_application_property (property_group);

create index idx_wci_user on wci_user (username);

create index idx_wci_user_authority on wci_user_authority (user_id);
