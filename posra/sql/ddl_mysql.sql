"/cygdrive/c/Program Files/MySQL/MySQL Server 5.6/bin/"mysql --user=root --password=GingerRoot#13 -D posra -n -N <<ESQL

    drop table if exists POSRA.ExternalID;

    drop table if exists POSRA.Polymer;

    drop table if exists POSRA.PolymerRepeatUnitSegmentAssociation;

    drop table if exists POSRA.RepeatUnit;

    drop table if exists POSRA.SMILES;

    drop table if exists POSRA.Segment;

    drop table if exists POSRA.SegmentAssociation;

    create table POSRA.ExternalID (
        value varchar(255) not null,
        polymerID integer not null,
        primary key (value)
    );

    create table POSRA.Polymer (
        polymerID integer not null auto_increment,
        name varchar(255) not null,
        ExternalID varchar(45),
        primary key (polymerID)
    );

    create table POSRA.PolymerRepeatUnitSegmentAssociation (
        prsID integer not null,
        polymerID integer not null,
        segmentID integer not null,
        repeatUnitID integer not null,
        segmentType char(1) not null,
        primary key (prsID, polymerID, segmentID, repeatUnitID)
    );

    create table POSRA.RepeatUnit (
        repeatUnitID integer not null auto_increment,
        primary key (repeatUnitID)
    );

    create table POSRA.SMILES (
        SMILESID integer not null auto_increment,
        SMILESString longtext not null,
        primary key (SMILESID)
    );

    create table POSRA.Segment (
        segmentID integer not null auto_increment,
        SMILESID integer,
        repeatUnitID integer,
        bondCount integer not null,
        degree varchar(255) not null,
        type char(1) not null,
        primary key (segmentID)
    );

    create table POSRA.SegmentAssociation (
        SegmentAssociationID integer not null,
        polymerID integer not null,
        segmentID1 integer not null,
        segmentID2 integer not null,
        relationshipType varchar(2) not null,
        primary key (SegmentAssociationID, polymerID, segmentID1, segmentID2)
    );

    alter table POSRA.Polymer 
        add constraint UK_t99ycg6hug2311570cgxwviqy  unique (name);

    alter table POSRA.ExternalID 
        add constraint FK_il8kly9hl6s6ktlv3dfsa8bwp 
        foreign key (polymerID) 
        references POSRA.Polymer (polymerID);

    alter table POSRA.PolymerRepeatUnitSegmentAssociation 
        add constraint FK_4a43riywmwykt062kuuyu62t4 
        foreign key (repeatUnitID) 
        references POSRA.RepeatUnit (repeatUnitID);

    alter table POSRA.PolymerRepeatUnitSegmentAssociation 
        add constraint FK_dvja1do8hskvtxub3mb7jdxsr 
        foreign key (segmentID) 
        references POSRA.Segment (segmentID);

    alter table POSRA.PolymerRepeatUnitSegmentAssociation 
        add constraint FK_ma5j6qb25oyvaym6knp88o9hf 
        foreign key (polymerID) 
        references POSRA.Polymer (polymerID);

    alter table POSRA.Segment 
        add constraint FK_padfwj8n3axt4wg54r94qcex2 
        foreign key (SMILESID) 
        references POSRA.SMILES (SMILESID);

    alter table POSRA.Segment 
        add constraint FK_8ugtwii3115xdlgqiv0b6krbr 
        foreign key (repeatUnitID) 
        references POSRA.RepeatUnit (repeatUnitID);

    alter table POSRA.SegmentAssociation 
        add constraint FK_9c0uyquw5o1otw0nwqnoce5at 
        foreign key (segmentID2) 
        references POSRA.Segment (segmentID);

    alter table POSRA.SegmentAssociation 
        add constraint FK_gwjrfa0cqxnsjoxf8vj4d87gj 
        foreign key (segmentID1) 
        references POSRA.Segment (segmentID);

    alter table POSRA.SegmentAssociation 
        add constraint FK_so7ln72fmn2rdyytt9x429ign 
        foreign key (polymerID) 
        references POSRA.Polymer (polymerID);

  \q
ESQL
