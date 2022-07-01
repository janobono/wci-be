-- EXTENSION
create
extension if not exists "uuid-ossp";
create
extension if not exists unaccent;

-- TABLE
create table wci_application_property
(
    property_key   varchar(255) not null,
    property_lang  varchar(255) not null,
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
    gdpr         bool                               not null,
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
    add primary key (property_key, property_lang);

alter table wci_user
    add primary key (id);

-- UNIQUE
alter table wci_application_property
    add constraint u_wci_application_property unique (property_key, property_lang, property_group);

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
create index idx_wci_application_property on wci_application_property (property_lang, property_group);

create index idx_wci_user on wci_user (username);

create index idx_wci_user_authority on wci_user_authority (user_id);

-- DATA
insert into wci_user (id, username, password, first_name, last_name, email, gdpr, confirmed, enabled)
values ('954ade48-a7ba-4ab7-889a-110929504766', 'wci-admin',
        '$2a$12$ylzODaJ9Iv82eas9tWECdOH7Py78bBlmLoiA.gBF7J6XxMyntEgxq', 'wci', 'admin', 'wci.admin@wci.sk', true, true,
        true);
insert into wci_user_authority (user_id, authority)
values ('954ade48-a7ba-4ab7-889a-110929504766', 'WCI_ADMIN');
insert into wci_user (id, username, password, first_name, last_name, email, gdpr, confirmed, enabled)
values ('fdce1500-2f72-48b4-8a22-af09a044193d', 'wci-manager',
        '$2a$12$ZaapiFVtn0rTQuB4yu6UJ.3XtzrRy5Pf2AVfIwFjJ/Z1SQaqnaEGu', 'wci', 'manager', 'wci.manager@wci.sk', true,
        true, true);
insert into wci_user_authority (user_id, authority)
values ('fdce1500-2f72-48b4-8a22-af09a044193d', 'WCI_MANAGER');
insert into wci_user (id, username, password, first_name, last_name, email, gdpr, confirmed, enabled)
values ('a9b935e9-151f-49a5-be04-1dfcda5c56b6', 'wci-employee',
        '$2a$12$AeS9MD7evnXB0kY5oe0RruUIZG2uuvArzLRWUt6TqE1ZwmsbLs2PS', 'wci', 'employee', 'wci.employee@wci.sk', true,
        true, true);
insert into wci_user_authority (user_id, authority)
values ('a9b935e9-151f-49a5-be04-1dfcda5c56b6', 'WCI_EMPLOYEE');
insert into wci_user (id, username, password, first_name, last_name, email, gdpr, confirmed, enabled)
values ('c65f8205-7969-4b47-bf13-329624b4c57f', 'wci-customer',
        '$2a$12$2hViAbWWIcXm4wr6jUsb7OWE6MPWxzsrZ432Pfr9NkjZtPtIv3jra', 'wci', 'customer', 'wci.customer@wci.sk', true,
        true, true);
insert into wci_user_authority (user_id, authority)
values ('c65f8205-7969-4b47-bf13-329624b4c57f', 'WCI_CUSTOMER');

insert into wci_application_property (property_key, property_lang, property_group, property_value)
values ('GLOBAL_WEB_URL', 'sk', 'GLOBAL', 'http://change-me.sk');
insert into wci_application_property (property_key, property_lang, property_group, property_value)
values ('GLOBAL_WEB_URL', 'en', 'GLOBAL', 'http://change-me.sk');
insert into wci_application_property (property_key, property_lang, property_group, property_value)
values ('GLOBAL_MAIL', 'sk', 'GLOBAL', 'mail@change-me.sk');
insert into wci_application_property (property_key, property_lang, property_group, property_value)
values ('GLOBAL_MAIL', 'en', 'GLOBAL', 'mail@change-me.sk');
insert into wci_application_property (property_key, property_lang, property_group, property_value)
values ('GLOBAL_SIGN_UP_TOKEN_EXPIRATION', 'sk', 'GLOBAL', '86400000');
insert into wci_application_property (property_key, property_lang, property_group, property_value)
values ('GLOBAL_SIGN_UP_TOKEN_EXPIRATION', 'en', 'GLOBAL', '86400000');
insert into wci_application_property (property_key, property_lang, property_group, property_value)
values ('GLOBAL_RESET_PASSWORD_TOKEN_EXPIRATION', 'sk', 'GLOBAL', '86400000');
insert into wci_application_property (property_key, property_lang, property_group, property_value)
values ('GLOBAL_RESET_PASSWORD_TOKEN_EXPIRATION', 'en', 'GLOBAL', '86400000');

insert into wci_application_property (property_key, property_lang, property_group, property_value)
values ('RESET_PASSWORD_MAIL_SUBJECT', 'sk', 'RESET_PASSWORD_MAIL', 'Aktivácia hesla');
insert into wci_application_property (property_key, property_lang, property_group, property_value)
values ('RESET_PASSWORD_MAIL_SUBJECT', 'en', 'RESET_PASSWORD_MAIL', 'Password activation');
insert into wci_application_property (property_key, property_lang, property_group, property_value)
values ('RESET_PASSWORD_MAIL_TITLE', 'sk', 'RESET_PASSWORD_MAIL', 'Aktivácia hesla');
insert into wci_application_property (property_key, property_lang, property_group, property_value)
values ('RESET_PASSWORD_MAIL_TITLE', 'en', 'RESET_PASSWORD_MAIL', 'Password activation');
insert into wci_application_property (property_key, property_lang, property_group, property_value)
values ('RESET_PASSWORD_MAIL_MESSAGE', 'sk', 'RESET_PASSWORD_MAIL', 'Vygenerovali sme pre Vás nové heslo.');
insert into wci_application_property (property_key, property_lang, property_group, property_value)
values ('RESET_PASSWORD_MAIL_MESSAGE', 'en', 'RESET_PASSWORD_MAIL', 'We have generated a new password for you.');
insert into wci_application_property (property_key, property_lang, property_group, property_value)
values ('RESET_PASSWORD_MAIL_PASSWORD_MESSAGE', 'sk', 'RESET_PASSWORD_MAIL', 'Nové heslo: {0}');
insert into wci_application_property (property_key, property_lang, property_group, property_value)
values ('RESET_PASSWORD_MAIL_PASSWORD_MESSAGE', 'en', 'RESET_PASSWORD_MAIL', 'New password: {0}');
insert into wci_application_property (property_key, property_lang, property_group, property_value)
values ('RESET_PASSWORD_MAIL_LINK', 'sk', 'RESET_PASSWORD_MAIL', 'Kliknutím aktivujete heslo.');
insert into wci_application_property (property_key, property_lang, property_group, property_value)
values ('RESET_PASSWORD_MAIL_LINK', 'en', 'RESET_PASSWORD_MAIL', 'Please click to activate password.');

insert into wci_application_property (property_key, property_lang, property_group, property_value)
values ('SIGN_UP_MAIL_SUBJECT', 'sk', 'SIGN_UP_MAIL', 'Aktivácia účtu');
insert into wci_application_property (property_key, property_lang, property_group, property_value)
values ('SIGN_UP_MAIL_SUBJECT', 'en', 'SIGN_UP_MAIL', 'Account activation');
insert into wci_application_property (property_key, property_lang, property_group, property_value)
values ('SIGN_UP_MAIL_TITLE', 'sk', 'SIGN_UP_MAIL', 'Aktivácia účtu');
insert into wci_application_property (property_key, property_lang, property_group, property_value)
values ('SIGN_UP_MAIL_TITLE', 'en', 'SIGN_UP_MAIL', 'Account activation');
insert into wci_application_property (property_key, property_lang, property_group, property_value)
values ('SIGN_UP_MAIL_MESSAGE', 'sk', 'SIGN_UP_MAIL', 'Váš účet bol vytvorený.');
insert into wci_application_property (property_key, property_lang, property_group, property_value)
values ('SIGN_UP_MAIL_MESSAGE', 'en', 'SIGN_UP_MAIL', 'Your account was created.');
insert into wci_application_property (property_key, property_lang, property_group, property_value)
values ('SIGN_UP_MAIL_LINK', 'sk', 'SIGN_UP_MAIL', 'Kliknutím aktivujete svoj účet.');
insert into wci_application_property (property_key, property_lang, property_group, property_value)
values ('SIGN_UP_MAIL_LINK', 'en', 'SIGN_UP_MAIL', 'Please click to activate your account.');

-- "cookies-info": {
--     "website-instructions": "This website makes use of cookies to enhance the browsing experience and provide additional functionality.",
--     "personal-instructions": "None of this data can or will be used to identify or contact you.",
--     "agree-instructions": "By clicking Allow cookies you give your permission to this website to store small bits of data on your device.",
--     "learn-instructions": "To learn more about cookies and localStorage, visit",
--     "disable-instructions": "To disable all cookies through the browser, click on the corresponding link and follow the instructions:"
--   },
--
--       "cookies-info": {
--     "website-instructions": "Táto webová stránka používa súbory cookie na zlepšenie zážitku z prehliadania a poskytovanie dodatočných funkcií.",
--     "personal-instructions": "Žiadne z týchto údajov nemožno a ani nebudú použité na vašu identifikáciu alebo kontaktovanie.",
--     "agree-instructions": "Kliknutím na položku Povoliť cookies dávate tejto webovej lokalite povolenie na ukladanie malých kúskov údajov na vašom zariadení.",
--     "learn-instructions": "Ak sa chcete dozvedieť viac o cookies a localStorage, navštívte",
--     "disable-instructions": "Ak chcete zakázať všetky súbory cookie prostredníctvom prehliadača, kliknite na príslušný odkaz a postupujte podľa pokynov:"
--   },
