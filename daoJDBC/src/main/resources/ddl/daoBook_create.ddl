create sequence DAO_BOOK_SEQ as int start with 100 increment by 2;

create table DAO_BOOK_UID (
    ID bigint
);
insert into DAO_BOOK_UID (ID) VALUES ( NEXT VALUE FOR DAO_BOOK_SEQ );

create table DAO_BOOK (
    ID          bigint not null identity,
    VERSION     bigint not null,
    TITLE       varchar(64),
    AUTHOR      varchar(64),
    DESCRIPTION varchar(2000),
    PAGES       int,

    CONSTRAINT dao_BookPK PRIMARY KEY(ID)
);
