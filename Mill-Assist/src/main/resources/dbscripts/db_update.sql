ALTER TABLE IRSCW.CNCMILLINGMACHINE ADD COLUMN NEW_DEV_INT BOOLEAN;
ALTER TABLE IRSCW.CNCMILLINGMACHINE ADD COLUMN NB_FIXTURES INTEGER DEFAULT 1;
ALTER TABLE IRSCW.CNCMILLINGMACHINE ADD COLUMN TIM_ALLOWED BOOLEAN DEFAULT FALSE;
ALTER TABLE IRSCW.ZONE ADD COLUMN ZONE_NR INT NOT NULL DEFAULT 0;
ALTER TABLE IRSCW.DEVICESETTINGS_WORKAREA_CLAMPING ADD COLUMN ACTIVE_FL BOOLEAN;

UPDATE IRSCW.ZONE 
   SET ZONE_NR = 1 
 WHERE
       IRSCW.ZONE.DEVICE IN
       ( 
          SELECT ID
            FROM IRSCW.DEVICE
           WHERE TYPE = 1
       )
;

UPDATE IRSCW.CNCMILLINGMACHINE SET NEW_DEV_INT = false;

INSERT INTO DEVICE VALUES (7,'REVERSAL UNIT',7);
INSERT INTO ZONE VALUES (7,7,'REVERSAL UNIT MAIN ZONE', 0);

CREATE TABLE
    REVERSALUNIT
    (
        ID INTEGER NOT NULL,
        STATION_HEIGHT FLOAT DEFAULT 0 NOT NULL,
        CONSTRAINT REVERSALUNIT_DEVICE FOREIGN KEY (ID) REFERENCES DEVICE (ID)
    );
    
INSERT INTO REVERSALUNIT (ID, STATION_HEIGHT) VALUES (7, 0.0);    
 
INSERT INTO COORDINATES (X, Y, Z, W, P, R) VALUES (0,0,0,0,0,0);

INSERT INTO USERFRAME (NUMBER, ZSAFE, LOCATION, NAME) 
        SELECT 11, 20.0, MAX(ID), 'REVERSAL UNIT' 
        FROM COORDINATES;
    
INSERT INTO WORKAREA (ZONE, USERFRAME, NAME) 
        (SELECT ZONE.ID, USERFRAME.ID, 'REVERSAL UNIT'
          FROM ZONE, USERFRAME
         WHERE ZONE.DEVICE = 7 
           AND USERFRAME.NUMBER = 11
        );

INSERT INTO COORDINATES (X, Y, Z, W, P, R) VALUES (0,0,0,0,0,0);
INSERT INTO COORDINATES (X, Y, Z, W, P, R) VALUES (0,0,0,0,0,0);
INSERT INTO COORDINATES (X, Y, Z, W, P, R) VALUES (0,0,0,0,0,0);        
        
INSERT INTO CLAMPING (TYPE, RELATIVE_POSITION, SMOOTH_TO, SMOOTH_FROM, HEIGHT, NAME, FIXTURE_TYPE)
          SELECT 3, MAX(ID), (MAX(ID) - 1), (MAX(ID) -2),  0, 'Reversal Unit', 0       
           FROM COORDINATES;        

INSERT INTO WORKAREA_CLAMPING (WORKAREA, CLAMPING)
        (SELECT WORKAREA.ID,  CLAMPING.ID 
           FROM WORKAREA, CLAMPING
          WHERE WORKAREA.NAME = 'REVERSAL UNIT'
            AND CLAMPING.NAME = 'Reversal Unit'
        );

INSERT INTO MCODE VALUES (3,2, 1, 'loadR', true, false, false, false, false, false);
INSERT INTO MCODE VALUES (4,3, 1, 'unloadR', true, false, false, false, false, false);

INSERT INTO WORKAREA (ZONE, USERFRAME, NAME)
         (
                SELECT WORKAREA.ZONE
                      ,WORKAREA.USERFRAME
                      ,'MAIN REV'
                  from workarea, userframe 
                 where userframe.number = 3
                   and userframe.id = workarea.userframe
                );

insert into workarea_clamping (workarea, clamping ) 
(
       select workarea.id, workarea_clamping.clamping
         from workarea_clamping, workarea
        where workarea.name = 'MAIN REV WA1'
          and workarea_clamping.workarea in 
                (select workarea.id
                   from workarea, userframe
                  where userframe.number = 3
                    and userframe.id = workarea.userframe
                )
);  

INSERT INTO WORKAREA (ZONE, USERFRAME, NAME)
         (
                SELECT WORKAREA.ZONE
                      ,WORKAREA.USERFRAME
                      ,'MAIN REV WA2'
                  from workarea, userframe 
                 where userframe.number = 4
                   and userframe.id = workarea.userframe
                );

insert into workarea_clamping (workarea, clamping ) 
(
       select workarea.id, workarea_clamping.clamping
         from workarea_clamping, workarea
        where workarea.name = 'MAIN REV WA2'
          and workarea_clamping.workarea in 
                (select workarea.id
                   from workarea, userframe
                  where userframe.number = 4
                    and userframe.id = workarea.userframe
                )
);     

UPDATE WORKPIECETYPE SET ID = 3 WHERE NAME = 'FINISHED';
INSERT INTO WORKPIECETYPE VALUES (2, 'HALF_FINISHED');

ALTER TABLE DEVICEACTIONSETTINGS ADD COLUMN WORKPIECETYPE INTEGER;
ALTER TABLE DEVICEACTIONSETTINGS ADD CONSTRAINT DEVICEACTIONSETTINGS_WORKPICETYPE FOREIGN KEY (WORKPIECETYPE) REFERENCES WORKPIECETYPE (ID);      
        
 
CREATE TABLE
    REVERSALUNITSETTINGS
    (
        ID INTEGER NOT NULL,
        CONFIGWIDTH FLOAT DEFAULT 0.0,
        CONSTRAINT PKEY_REVERSALUNITSETTINGS PRIMARY KEY (ID),
        CONSTRAINT REVERSALUNITSETTINGS_ID FOREIGN KEY (ID) REFERENCES DEVICESETTINGS (ID)
    ON
    DELETE
        CASCADE
    ON
    UPDATE
        RESTRICT
    ); 

UPDATE DEVICEACTIONSETTINGS SET WORKPIECETYPE = 1 WHERE DEVICEACTIONSETTINGS.ID IN (
        SELECT DEVICEACTIONSETTINGS.ID
          FROM DEVICEACTIONSETTINGS 
          JOIN STEP 
            ON DEVICEACTIONSETTINGS.STEP = STEP.ID 
          JOIN DEVICE
            ON DEVICEACTIONSETTINGS.DEVICE = DEVICE.ID
          JOIN STEPTYPE
            ON STEPTYPE.ID = STEP.TYPE
          JOIN DEVICETYPE
            ON DEVICE.TYPE = DEVICETYPE.ID
         WHERE (STEPTYPE.ID IN (1,5) AND DEVICETYPE.ID IN (2, 3, 4, 6)) OR
               (STEPTYPE.ID = 2 AND DEVICETYPE.ID IN (1, 3))  
       )
;

UPDATE DEVICEACTIONSETTINGS SET WORKPIECETYPE = 3 WHERE WORKPIECETYPE is null;
INSERT INTO DEVICETYPE VALUES (7, 'REVERSAL UNIT');


ALTER TABLE ROBOTPUTSETTINGS ADD COLUMN APPROACHTYPE INTEGER NOT NULL DEFAULT 1;
ALTER TABLE ROBOTPICKSETTINGS ADD COLUMN APPROACHTYPE INTEGER NOT NULL DEFAULT 1;

ALTER TABLE ROBOTPUTSETTINGS ADD COLUMN TURN_IN_MACHINE BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE ROBOTPICKSETTINGS ADD COLUMN TURN_IN_MACHINE BOOLEAN NOT NULL DEFAULT FALSE;