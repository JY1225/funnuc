CREATE TABLE CONFIG
(
    ID INTEGER NOT NULL GENERATED BY DEFAULT AS IDENTITY,
    FLIP INTEGER NOT NULL,
    UP INTEGER NOT NULL,
    FRONT INTEGER NOT NULL,
    TURN1 INTEGER NOT NULL,
    TURN2 INTEGER NOT NULL,
    TURN3 INTEGER NOT NULL,
    CONSTRAINT PK_CONFIG_ID PRIMARY KEY (ID)
);

CREATE TABLE ROB_IP_POINT 
(
    UF_NR INTEGER NOT NULL,
    TF_NR INTEGER NOT NULL,
    POS_TYPE INTEGER NOT NULL,
    LOCATION INTEGER NOT NULL,
    CONFIG INTEGER NOT NULL,
    CONSTRAINT PKEY_IP_POINT PRIMARY KEY (UF_NR, TF_NR, POS_TYPE),
    CONSTRAINT FK_IP_CONFIG FOREIGN KEY (CONFIG) REFERENCES CONFIG(ID) ON DELETE CASCADE ON UPDATE RESTRICT,
    CONSTRAINT FK_IP_COORD FOREIGN KEY (LOCATION) REFERENCES COORDINATES(ID) ON DELETE CASCADE ON UPDATE RESTRICT
);

CREATE TABLE ROB_RP_POINT 
(
    UF_NR INTEGER NOT NULL,
    TF_NR INTEGER NOT NULL,
    ORIGINAL_TF_NR INTEGER NOT NULL,
    LOCATION INTEGER NOT NULL,
    CONFIG INTEGER NOT NULL,
    CONSTRAINT PKEY_RP_POINT PRIMARY KEY (UF_NR, TF_NR, ORIGINAL_TF_NR),
    CONSTRAINT FK_RP_CONFIG FOREIGN KEY (CONFIG) REFERENCES CONFIG(ID)  ON DELETE CASCADE ON UPDATE RESTRICT,
    CONSTRAINT FK_RP_COORD FOREIGN KEY (LOCATION) REFERENCES COORDINATES(ID)  ON DELETE CASCADE ON UPDATE RESTRICT
);

-- special ID is a sort of enum value - 0 (homeAB) - 1 (jaw change) - 2 (jaw change approach)
CREATE TABLE ROB_SPECIAL_POINT 
(
    SPEC_ID INTEGER NOT NULL,
    LOCATION INTEGER NOT NULL,
    CONFIG INTEGER NOT NULL,
    CONSTRAINT PKEY_SPECIAL_POINT PRIMARY KEY (SPEC_ID),
    CONSTRAINT FK_SPECIAL_CONFIG FOREIGN KEY (CONFIG) REFERENCES CONFIG(ID)  ON DELETE CASCADE ON UPDATE RESTRICT,
    CONSTRAINT FK_SPECIAL_COORD FOREIGN KEY (LOCATION) REFERENCES COORDINATES(ID)  ON DELETE CASCADE ON UPDATE RESTRICT
);

CREATE TABLE ROB_USERFRAME
(
    UF_NR INTEGER NOT NULL,
    LOCATION INTEGER NOT NULL,
    CONFIG INTEGER NOT NULL,
    CONSTRAINT PKEY_ROB_UF PRIMARY KEY (UF_NR),
    CONSTRAINT FK_UF_CONFIG FOREIGN KEY (CONFIG) REFERENCES CONFIG(ID)  ON DELETE CASCADE ON UPDATE RESTRICT,
    CONSTRAINT FK_UF_COORD FOREIGN KEY (LOCATION) REFERENCES COORDINATES(ID)  ON DELETE CASCADE ON UPDATE RESTRICT
);

CREATE TABLE ROB_TOOLFRAME
(
    TF_NR INTEGER NOT NULL,
    LOCATION INTEGER NOT NULL,
    CONFIG INTEGER NOT NULL,
    CONSTRAINT PKEY_ROB_TF PRIMARY KEY (TF_NR),
    CONSTRAINT FK_TF_CONFIG FOREIGN KEY (CONFIG) REFERENCES CONFIG(ID)  ON DELETE CASCADE ON UPDATE RESTRICT,
    CONSTRAINT FK_TF_COORD FOREIGN KEY (LOCATION) REFERENCES COORDINATES(ID)  ON DELETE CASCADE ON UPDATE RESTRICT
);

CREATE TABLE ROB_REGISTER
(
    REG_ID INTEGER NOT NULL,
    VALUE INTEGER NOT NULL,
    CONSTRAINT PKEY_ROB_REG PRIMARY KEY (REG_ID)
);

ALTER TABLE ROBOT ADD COLUMN ACCEPT_DATA BOOLEAN DEFAULT FALSE;
