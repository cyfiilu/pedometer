package com.iilu.fendou.modules.sport.entity;

public class DayData {

    private String userid;
    private String dayDate;
    private int dayStepCount = 0;
    private int dayGoalNum;
    private float km = 0f;
    private float kcal = 0f;
    private int zzStartTime;
    private int zzEndTime;
    private int zzStepCount = 0;
    private int zzGoalNum;
    private int mmStartTime;
    private int mmEndTime;
    private int mmGoalNum;
    private int mmStepCount = 0;
    private int[] hours;

    public DayData() {
        hours = new int[24];
        for (int i = 0; i < 24; i++) {
            hours[i] = 0;
        }
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getDayDate() {
        return dayDate;
    }

    public void setDayDate(String dayDate) {
        this.dayDate = dayDate;
    }

    public int getDayStepCount() {
        return dayStepCount;
    }

    public void setDayStepCount(int dayStepCount) {
        this.dayStepCount = dayStepCount;
    }

    public int getDayGoalNum() {
        return dayGoalNum;
    }

    public void setDayGoalNum(int dayGoalNum) {
        this.dayGoalNum = dayGoalNum;
    }

    public float getKm() {
        return km;
    }

    public void setKm(float km) {
        this.km = km;
    }

    public float getKcal() {
        return kcal;
    }

    public void setKcal(float kcal) {
        this.kcal = kcal;
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

    public int getZzStepCount() {
        return zzStepCount;
    }

    public void setZzStepCount(int zzStepCount) {
        this.zzStepCount = zzStepCount;
    }

    public int getZzGoalNum() {
        return zzGoalNum;
    }

    public void setZzGoalNum(int zzGoalNum) {
        this.zzGoalNum = zzGoalNum;
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

    public int getMmGoalNum() {
        return mmGoalNum;
    }

    public void setMmGoalNum(int mmGoalNum) {
        this.mmGoalNum = mmGoalNum;
    }

    public int getMmStepCount() {
        return mmStepCount;
    }

    public void setMmStepCount(int mmStepCount) {
        this.mmStepCount = mmStepCount;
    }

    public int[] getHours() {
        return hours;
    }

    public void setHours(int[] hours) {
        this.hours = hours;
    }
}
