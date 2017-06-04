package com.iilu.fendou.modules.entity;

public class UserInfo extends BaseModel {

    private String sex;
    private String city;
    private int height;
    private int weight;
    private int stepWidth;
    private int dayGoalNum;
    private int zzGoalNum;
    private int mmGoalNum;
    private int zzStartTime;
    private int zzEndTime;
    private int mmStartTime;
    private int mmEndTime;

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public int getStepWidth() {
        return stepWidth;
    }

    public void setStepWidth(int stepWidth) {
        this.stepWidth = stepWidth;
    }

    public int getDayGoalNum() {
        return dayGoalNum;
    }

    public void setDayGoalNum(int dayGoalNum) {
        this.dayGoalNum = dayGoalNum;
    }

    public int getZzGoalNum() {
        return zzGoalNum;
    }

    public void setZzGoalNum(int zzGoalNum) {
        this.zzGoalNum = zzGoalNum;
    }

    public int getMmGoalNum() {
        return mmGoalNum;
    }

    public void setMmGoalNum(int mmGoalNum) {
        this.mmGoalNum = mmGoalNum;
    }

    public int getZzStartTime() {
        return zzStartTime;
    }

    public void setZzStartTime(int zzStartTime) {
        this.zzStartTime = zzStartTime;
    }

    public int getZzEndTime() {
        return zzEndTime;
    }

    public void setZzEndTime(int zzEndTime) {
        this.zzEndTime = zzEndTime;
    }

    public int getMmStartTime() {
        return mmStartTime;
    }

    public void setMmStartTime(int mmStartTime) {
        this.mmStartTime = mmStartTime;
    }

    public int getMmEndTime() {
        return mmEndTime;
    }

    public void setMmEndTime(int mmEndTime) {
        this.mmEndTime = mmEndTime;
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "height='" + height + '\'' +
                ", weight='" + weight + '\'' +
                ", stepWidth='" + stepWidth + '\'' +
                ", dayGoalNum='" + dayGoalNum + '\'' +
                ", zzGoalNum='" + zzGoalNum + '\'' +
                ", mmGoalNum='" + mmGoalNum + '\'' +
                ", zzStartTime=" + zzStartTime +
                ", zzEndTime=" + zzEndTime +
                ", mmStartTime=" + mmStartTime +
                ", mmEndTime=" + mmEndTime +
                '}';
    }
}
