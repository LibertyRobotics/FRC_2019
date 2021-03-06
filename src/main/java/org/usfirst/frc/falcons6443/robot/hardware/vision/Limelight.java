package org.usfirst.frc.falcons6443.robot.hardware.vision;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;

/**
 * Class created to manage limelight data for FRC 2019
 * 
 * @author Will Richards, Goirick Saha
 */

public class Limelight{

    //Creates uninitilized variables to hold limelight table, x offset, y offset and object area
    private NetworkTable limelightTable;
    private NetworkTableEntry tx;
    private NetworkTableEntry ty;
    private NetworkTableEntry ta;
    private NetworkTableEntry tv;

    //Variables for calculating distance
    private double fAngle;
    private double sAngle;
    private double fHeight;
    private double sHeight;
    private double Distance;


    public Limelight(){
        limelightTable = NetworkTableInstance.getDefault().getTable("limelight");

        //Initilizes entries for limelight values
        tx = limelightTable.getEntry("tx"); //X degrees
        ty = limelightTable.getEntry("ty"); //Y degrees
        ta = limelightTable.getEntry("ta"); //Target area
        tv = limelightTable.getEntry("tv"); //Target vlaid
    }

    /**
     *  Will get the current X offset value if no object is detected it will default to 0
     *  @return the X offset in degrees to the target
     */
    public double getX(){
        return tx.getDouble(0.0);
    }

    /**
     * Gets the number 1 or 0 (true or false) if the target is visible or not
     * @return the visibility of the target
     */
    public double getValidTarget(){
        return tv.getDouble(0.0);
    }

    /**
     * Will get the current Y offset value if no object is detected it will default to 0
     * @return Y offset to target in degrees
     */
    public double getY(){
        return ty.getDouble(0.0);
    }

    /**
     * Will get the current area value
     * @return returns area, if no objects are detected it returns 0
     */
    public double getArea(){
        return ta.getDouble(0.0);
    }

    /**
     * Unused Method to calculate the distance to an object, Math needs work
     * @return returns distance in inches to target
     */
    public double getDistance(){
        Distance = (sHeight - fHeight)/Math.tan(fAngle + sAngle); 
        return Distance; 
    }

    /**
     * Will turn on the LED so the camera can track the refelctive tape
     */
    public void turnOnLED(){
        limelightTable.getEntry("ledMode").setNumber(3);
    }

    /**
     * Will turn off the LED so we dont blind everyone as the LED is very bright
     */
    public void turnOffLED(){
        limelightTable.getEntry("ledMode").setNumber(1);
    }
    
    /**
     * Allows for getting the current camera mode
     * @return an int (1 or 0) that specifies what mode it is in
     */
    public double getCamMode(){
       return limelightTable.getEntry("camMode").getDouble(0.0);
    }

    /**
     * Allows for manual camera mode setting
     * @param mode pass in 1 or 0 to switch
     */
    public void setCamMode(double mode){
        limelightTable.getEntry("camMode").setNumber(mode);
    }
}
