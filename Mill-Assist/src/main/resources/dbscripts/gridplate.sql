CREATE TABLE
	GRIDPLATE
	(
		ID INTEGER NOT NULL GENERATED BY DEFAULT AS IDENTITY,
		NAME VARCHAR(100) NOT NULL,
		LENGTH DOUBLE NOT NULL,
		WIDTH DOUBLE NOT NULL,
		HEIGHT DOUBLE NOT NULL,
		HOLELENGTH DOUBLE NOT NULL,
		HOLEWIDTH DOUBLE NOT NULL,
		NBHORIZONTAL INTEGER NOT NULL,
		NBVERTICAL INTEGER NOT NULL,
		HORIZONTALPADDING DOUBLE NOT NULL,
                VERTICALPADDINGTOP DOUBLE NOT NULL,
                VERTICALPADDINGBOTTOM DOUBLE NOT NULL,
		HOLE_X DOUBLE NOT NULL,
		HOLE_Y DOUBLE NOT NULL,
		OFFSET_X DOUBLE NOT NULL,
		OFFSET_Y DOUBLE NOT NULL,
		ORIENTATION INTEGER NOT NULL,
		HORIZONTAL_R DOUBLE DEFAULT 90.0 NOT NULL,
                TILTED_R DOUBLE DEFAULT 135.0 NOT NULL,
		SMOOTH_TO INTEGER NOT NULL,
		SMOOTH_FROM INTEGER NOT NULL,
		CONSTRAINT PKEY_GRIDPLATE PRIMARY KEY (ID),
		CONSTRAINT GRIDPLATE_SMOOTH_FROM FOREIGN KEY (SMOOTH_FROM) REFERENCES COORDINATES (ID),
                CONSTRAINT GRIDPLATE_SMOOTH_TO FOREIGN KEY (SMOOTH_TO) REFERENCES COORDINATES (ID)
	);
ALTER TABLE IRSCW.STACKPLATESETTINGS ADD COLUMN GRID_ID INTEGER;
ALTER TABLE IRSCW.STACKPLATESETTINGS ADD CONSTRAINT GRIDPLATE_ID FOREIGN KEY (GRID_ID) REFERENCES GRIDPLATE (ID);