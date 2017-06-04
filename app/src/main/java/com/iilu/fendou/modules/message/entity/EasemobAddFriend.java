package com.iilu.fendou.modules.message.entity;

public class EasemobAddFriend extends MsgBase {

    private String userid;
    private String friendName;
    private String reason;
    private String accepte;
    private String readTag;
    private String receiveTime;
    private String headUrl;
    private int unreadCount;

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getFriendName() {
        return friendName;
    }

    public void setFriendName(String friendName) {
        this.friendName = friendName;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getAccepte() {
        return accepte;
    }

    public void setAccepte(String accepte) {
        this.accepte = accepte;
    }

    public String getReadTag() {
        return readTag;
    }

    public void setReadTag(String readTag) {
        this.readTag = readTag;
    }

    public String getReceiveTime() {
        return receiveTime;
    }

    public void setReceiveTime(String receiveTime) {
        this.receiveTime = receiveTime;
    }

    public String getHeadUrl() {
        return headUrl;
    }

    public void setHeadUrl(String headUrl) {
        this.headUrl = headUrl;
    }

    public int getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(int unreadCount) {
        this.unreadCount = unreadCount;
    }
}
