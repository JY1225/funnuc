package eu.robojob.millassist.external.device.processing.cnc.mcode;

import java.util.Set;

public class GenericMCode {

    private int id;
    private int index;
    private String name;
    private Set<Integer> robotServiceInputsRequired;
    private Set<Integer> robotServiceOutputsUsed;

    public GenericMCode(final int id, final int index, final String name, final Set<Integer> robotServiceInputsRequired,
            final Set<Integer> robotServiceOutputsUsed) {
        this.id = id;
        this.index = index;
        this.name = name;
        this.robotServiceInputsRequired = robotServiceInputsRequired;
        this.robotServiceOutputsUsed = robotServiceOutputsUsed;
    }

    public int getId() {
        return id;
    }

    public void setId(final int id) {
        this.id = id;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(final int index) {
        this.index = index;
    }

    public boolean isActive(final Set<Integer> activeRobotInputs) {
        if (activeRobotInputs.containsAll(robotServiceInputsRequired)
                && robotServiceInputsRequired.containsAll(activeRobotInputs)) {
            return true;
        }
        return false;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public Set<Integer> getRobotServiceInputsRequired() {
        return robotServiceInputsRequired;
    }

    public void setRobotServiceInputsRequired(final Set<Integer> robotServiceInputsRequired) {
        this.robotServiceInputsRequired = robotServiceInputsRequired;
    }

    public Set<Integer> getRobotServiceOutputsUsed() {
        return robotServiceOutputsUsed;
    }

    public void setRobotServiceOutputsUsed(final Set<Integer> robotServiceOutputsUsed) {
        this.robotServiceOutputsUsed = robotServiceOutputsUsed;
    }

}
