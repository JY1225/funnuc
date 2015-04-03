package eu.robojob.millassist.positioning;

/**
 * This class represents the CONFIG datatype of the FANUC robot. This is part of the XYZWPR datatype
 * of the FANUC robot.
 * 
 *@version v2.7.0 - initial version
 */
public class Config {
    
    private int id;
    private int cfgFlip, cfgUp, cfgFront;
    private int cfgTurn1, cfgTurn2, cfgTurn3;
    
    public Config(final int cfgFlip, final int cfgUp, final int cfgFront, 
            final int cfgTurn1, final int cfgTurn2, final int cfgTurn3) {
        this.cfgFlip = cfgFlip;
        this.cfgUp = cfgUp;
        this.cfgFront = cfgFront;
        this.cfgTurn1 = cfgTurn1;
        this.cfgTurn2 = cfgTurn2;
        this.cfgTurn3 = cfgTurn3;
    }
    
    public Config() {
        this(0,0,0,99,99,99);
    }
    
    public int getId() {
        return this.id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public int getCfgFlip() {
        return cfgFlip;
    }

    public void setCfgFlip(int cfgFlip) {
        this.cfgFlip = cfgFlip;
    }

    public int getCfgUp() {
        return cfgUp;
    }

    public void setCfgUp(int cfgUp) {
        this.cfgUp = cfgUp;
    }

    public int getCfgFront() {
        return cfgFront;
    }

    public void setCfgFront(int cfgFront) {
        this.cfgFront = cfgFront;
    }

    public int getCfgTurn1() {
        return cfgTurn1;
    }

    public void setCfgTurn1(int cfgTurn1) {
        this.cfgTurn1 = cfgTurn1;
    }

    public int getCfgTurn2() {
        return cfgTurn2;
    }

    public void setCfgTurn2(int cfgTurn2) {
        this.cfgTurn2 = cfgTurn2;
    }

    public int getCfgTurn3() {
        return cfgTurn3;
    }

    public void setCfgTurn3(int cfgTurn3) {
        this.cfgTurn3 = cfgTurn3;
    }
    
    @Override
    public String toString() {
        return "(" + cfgFlip + ";" + cfgUp + ";" + cfgFront + ";" +cfgTurn1 + ";" + cfgTurn2 + ";" + cfgTurn3 + ")";
    }

}
