package com.iilu.fendou.modules.sport.entity;

public class StepSensor {

    private String userid;
    private int sensorCount;
    private long sensorTime;
    private long bootTime;

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public int getSensorCount() {
        return sensorCount;
    }

    public void setSensorCount(int sensorCount) {
        this.sensorCount = sensorCount;
    }

    public long getSensorTime() {
        return sensorTime;
    }

    public void setSensorTime(long sensorTime) {
        this.sensorTime = sensorTime;
    }

    public long getBootTime() {
        return bootTime;
    }

    public void setBootTime(long bootTime) {
        this.bootTime = bootTime;
    }
}
