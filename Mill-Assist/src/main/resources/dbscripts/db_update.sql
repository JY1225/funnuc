// add stud height
ALTER TABLE IRSCW.STACKPLATESETTINGS ADD COLUMN STUDHEIGHT FLOAT DEFAULT 25 NOT NULL;
// update default clamp height Mill-Assist E to 25
UPDATE IRSCW.CLAMPING SET HEIGHT = 25 WHERE ID IN (
        SELECT CLAMPING FROM WORKAREA_CLAMPING WHERE WORKAREA IN (
                SELECT ID FROM WORKAREA WHERE ZONE IN (
                        SELECT ID FROM ZONE WHERE DEVICE IN (
                                SELECT ID FROM DEVICE WHERE TYPE = 2
                        )
                )
        )
);