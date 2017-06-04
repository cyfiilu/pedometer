package com.iilu.fendou.modules.message.entity;

import com.hyphenate.chat.EMConversation;

public class EasemobMessage extends MsgBase {

    private EMConversation emConversation;

    public EMConversation getEmConversation() {
        return emConversation;
    }

    public void setEmConversation(EMConversation emConversation) {
        this.emConversation = emConversation;
    }
}
