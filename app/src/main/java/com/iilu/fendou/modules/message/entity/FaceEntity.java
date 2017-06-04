package com.iilu.fendou.modules.message.entity;

import android.graphics.drawable.Drawable;

public class FaceEntity {

    public String flag = null; // 说明文本
    public Drawable icon = null; // 图标
    public boolean isSelected = false;//是否被选中

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
