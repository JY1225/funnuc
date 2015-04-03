package eu.robojob.millassist.positioning;

public class RobotPosition {
    
    private int id;
    private Coordinates position;
    private Config configuration;
    
    public RobotPosition(final Coordinates position, final Config configuration) {
        this.position = position;
        this.configuration = configuration;
    }
    
    public RobotPosition() {
        this(new Coordinates(), new Config());
    } 
    
    public void setId(int id) {
        this.id = id;
    }
    
    public int getId() {
        return this.id;
    }
    
    public Coordinates getPosition() {
        return position;
    }

    public void setPosition(Coordinates position) {
        this.position = position;
    }

    public Config getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Config configuration) {
        this.configuration = configuration;
    }
    
    @Override
    public String toString() {
        return position.toString() + ";" + configuration.toString();
    }
}
