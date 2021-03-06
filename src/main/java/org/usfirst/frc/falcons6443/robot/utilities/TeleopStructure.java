package org.usfirst.frc.falcons6443.robot.utilities;

import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.function.Consumer;

/*
 * A class containing all of the logic for teleop, including pairing a button with an action
 * running continuously or once per press), manual controls with and without backup buttons,
 * and functions that run when a set of buttons are not pressed.
 *
 * Use this class in Robot.java, function TeleopInit() and TeleopPeriodic()
 */
public class TeleopStructure {

    private int[] IDs = new int[2]; //0 is unpressed, 1 is toggle, 2 is toggle periodic
    private boolean first = true;

    private List<Boolean> runOnceSavedData = new ArrayList<>();
    private List<Boolean> toggleData = new ArrayList<>();
    private List<Boolean> isManualLessThanBuffer = new ArrayList<>();
    private List<Callable<Boolean>> isManualGetter = new ArrayList<>(); //add control manual getters
    private List<Consumer<Boolean>> isManualSetter = new ArrayList<>(); //add control manual setters

    public TeleopStructure(){
        //While loops to fill Lists with nulls (allows us to change them later)
        while(isManualGetter.size() < ManualControls.values().length) isManualGetter.add(null);
        while(isManualSetter.size() < ManualControls.values().length) isManualSetter.add(null);
        while(isManualLessThanBuffer.size() < ManualControls.values().length) isManualLessThanBuffer.add(null);
    }

    //A list of all manual controls of the robot, excluding drive
    //Used for manual controls. Can only have one ManualControls per manual axis (NOT per subsystem!)
    public enum ManualControls {
        VACUUM
    }

    //adding manual getters and setters to Lists using params:
    // ManualControls.manualEnum, () -> function(), (Boolean set) -> function(set)
    //Example: addIsManualGetter(TeleopStructure.ManualControls.Elevator, () -> elevator.getManual(),
    //                      (Boolean set) -> elevator.setManual(set));
    public void addIsManualGetterSetter(ManualControls manual, Callable<Boolean> callable,
                                        Consumer<Boolean> consumer) {
        isManualGetter.add(manual.ordinal(), callable);
        isManualSetter.add(manual.ordinal(), consumer);
        isManualLessThanBuffer.add(manual.ordinal(), true);  //included to ensure equal numbers of getters/setters to buffer checkers
    }

    //Pairs an action with a button
    public void press(boolean button, Runnable action){
        if(button) action.run();
    }

    //Pairs an action with a button, compatible with manual()
    // ie: this function can be used with manual() to control the same component
    // eg: button control and (backup) manual control of the same component
    public void press(ManualControls manual, boolean button, Runnable action){
        if(button) {
            isManualSetter.get(manual.ordinal()).accept(false); //turn manual off if nonmanual button pressed
            action.run();
        }
    }

    //Pairs an action with a manual input (joystick, trigger, etc)
    public void manual(ManualControls manualNumber, double input, Runnable action){
        if(Math.abs(input) > 0.2){
            isManualSetter.get(manualNumber.ordinal()).accept(true); //keeps update()s and off()s in check
            isManualLessThanBuffer.set(manualNumber.ordinal(), false); //does not run the off runnable in off()
            action.run();
        } else {
            isManualLessThanBuffer.set(manualNumber.ordinal(), true); //runs the off runnable in off()
        }
    }

    //Pairs an action with a manual input (joystick, trigger, etc)
    public void manual(ManualControls manualNumber, double input, Consumer<Double> action){
        if(Math.abs(input) > 0.2){
            isManualSetter.get(manualNumber.ordinal()).accept(true);
            isManualLessThanBuffer.set(manualNumber.ordinal(), false);
            action.accept(input);
        } else {
            isManualLessThanBuffer.set(manualNumber.ordinal(), true);
        }
    }

    //Runs an action when a set of buttons is not pressed
    public void off(Runnable off, boolean ... button){
        if(areAllFalse(button)) off.run();
    }

    //Runs an action when manual is less than buffer
    public void off(Runnable off, ManualControls manualNumber) {
        if(isManualLessThanBuffer.get(manualNumber.ordinal())) off.run();
    }

    //Runs an action when a set of buttons is not pressed and manual is less than buffer
    public void off(Runnable off, ManualControls manualNumber, boolean ... button){
        try {
            if(areAllFalse(button) && !isManualGetter.get(manualNumber.ordinal()).call()) off.run();
            else if((areAllFalse(button) && isManualGetter.get(manualNumber.ordinal()).call()
                    && isManualLessThanBuffer.get(manualNumber.ordinal()))) off.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Pairs an action with a button, activated only once unpressed (true) or once pressed (false)
    //This action will only run once, unlike press() which runs periodically until unpressed
    public void runOncePerPress(boolean button, Runnable function, boolean unpressedMode){
        if(first) runOnceSavedData.add(IDs[0], false);
        runOnceSavedData.add(IDs[0], false);
        if(button){
            if(!unpressedMode && !runOnceSavedData.get(IDs[0])){
                function.run();
            }
            runOnceSavedData.set(IDs[0], true);
        } else {
            if(unpressedMode && runOnceSavedData.get(IDs[0])){
                function.run();
            }
            runOnceSavedData.set(IDs[0], false);
        }
        IDs[0]++;
    }

    public void toggle(boolean button, Runnable function){
        if(first) toggleData.add(IDs[1], false);
        if(button){
            if(toggleData.get(IDs[1])){
                function.run();
            }
            toggleData.set(IDs[1], !toggleData.get(IDs[1]));
        }
        IDs[1]++;
    }

    public void togglePeriodic(boolean button, Runnable function){
        if(first) toggleData.add(IDs[2], false);
        if(button)
            toggleData.set(IDs[2], !toggleData.get(IDs[2]));
        if(toggleData.get(IDs[2]))
            function.run();
        IDs[2]++;
    }

    //clears the unpressedID
    public void periodicEnd(){
        first = false;
        for(int i = 0; i < IDs.length; i++)
            IDs[i] = 0;
    }

    private boolean areAllFalse(boolean[] array) {
        for(boolean b : array) if(b) return false;
        return true;
    }

    private static boolean areAllZero(double buffer, double[] array) {
        for(double d : array) if(d > buffer) return false;
        return true;
    }
}
