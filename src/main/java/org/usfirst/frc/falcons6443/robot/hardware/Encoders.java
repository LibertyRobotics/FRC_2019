package org.usfirst.frc.falcons6443.robot.hardware;

import edu.wpi.first.wpilibj.Encoder;

/**
 * This class was imported from Simbotics code.
 * Includes more encoder options. Some added functions to fit code needs
 *
 * NOTE: The GreyHill encoders are 256 ticks per rev. Make sure to calculate in if you have
 * a gearbox or not (eg: GreyHill on a 4:1 gearbox is 1024 ticks per rev, 256 times 4)
 *
 * @author Simbotics 2017
 */
public class Encoders extends Encoder{
    private int prev;
    private int speed;
    private int offset;
    private int ticksPerRev = 256;
    private double diameter;

    public Encoders(int channelA, int channelB) {
        super(channelA, channelB);
    }

    public Encoders(int channelA, int channelB, int offset) {
        super(channelA, channelB);
        this.offset = offset;
    }

    //look into counterbase and encoding types
    public Encoders(int aChannel, int bChannel, EncodingType encodingType, int offset) {
        super(aChannel, bChannel, false, encodingType);
        this.offset = offset;
        this.setDistancePerPulse(1);
    }

    public Encoders(int aChannel, int bChannel, EncodingType encodingType) {
        this(aChannel, bChannel, encodingType, 0);
    }

    public void set(int val) {
        super.reset();
        this.offset = val;
    }

    @Override
    public int get() {
        return super.get() + this.offset;
    }

    @Override
    public void reset() {
        super.reset();
        this.offset = 0;
        this.prev = 0;
    }

    public int speed(){
        return this.speed;
    }

    //periodic function (TEST)
    public void updateSpeed(){
        int curr = this.get();
        this.speed = curr - this.prev;
        this.prev = curr;
    }

    public double rawSpeed(){
        return this.getRate();
    }

    public double getDistanceWithDiameter(){
        return this.get() * this.diameter * Math.PI / this.ticksPerRev;
    }

    public void setDiameter(double wheelDiameter){
        diameter = wheelDiameter;
    }

    public void setTicksPerRev(int ticksPerRev){
        this.ticksPerRev = ticksPerRev;
    }

    public double getRevs(){
        return (double) this.get() / (double) this.ticksPerRev;
    }
}