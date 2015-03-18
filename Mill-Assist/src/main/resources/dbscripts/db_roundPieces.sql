RENAME TABLE RECTANGULAR_DIMENSIONS TO WP_RECT_DIM;

CREATE TABLE
    WP_RECT_DIM
    (
        WORKPIECE_ID INTEGER NOT NULL,
        LENGTH DOUBLE NOT NULL,
        WIDTH DOUBLE NOT NULL,
        HEIGHT DOUBLE NOT NULL,
        CONSTRAINT PKEY_RECT_DIM PRIMARY KEY (WORKPIECE_ID),
        CONSTRAINT FKEY_WORKPIECE_ID FOREIGN KEY (WORKPIECE_ID) REFERENCES WORKPIECE
        (ID)
    ON
    DELETE
        CASCADE
    );
    
INSERT INTO WP_RECT_DIM (
        SELECT ID, LENGTH, WIDTH, HEIGHT FROM WORKPIECE
        );
        
CREATE TABLE
    WP_CYL_DIM
    (
        WORKPIECE_ID INTEGER NOT NULL,
        LENGTH DOUBLE NOT NULL,
        WIDTH DOUBLE NOT NULL,
        HEIGHT DOUBLE NOT NULL,
        CONSTRAINT PKEY_RECT_DIM PRIMARY KEY (WORKPIECE_ID),
        CONSTRAINT FKEY_WORKPIECE_ID FOREIGN KEY (WORKPIECE_ID) REFERENCES WORKPIECE
        (ID)
    ON
    DELETE
        CASCADE
    );
        
ALTER TABLE WORKPIECE DROP COLUMN LENGTH;
ALTER TABLE WORKPIECE DROP COLUMN WIDTH;
ALTER TABLE WORKPIECE DROP COLUMN HEIGHT;

INSERT INTO WORKPIECESHAPE (NAME) VALUES ('ROUND');

ALTER TABLE STACKPLATE ADD COLUMN MAX_UNDERFLOW DOUBLE DEFAULT 10 NOT NULL;

ALTER TABLE CNCMILLINGMACHINE ADD COLUMN R_ROUND_PIECES DOUBLE DEFAULT 90 NOT NULL;