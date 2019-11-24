package com.roc.chatclient.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.roc.chatclient.util.CommonUtils;

public class UserExtInfo extends UserInfo {
    public String Name;

    public int Gender;

    public String Phone;

    public String NickName;

    public String Avatar;

    /**
     * initial letter for nickname
     */
    private String initialLetter;

    public void setInitialLetter(String initialLetter) {
        this.initialLetter = initialLetter;
    }

    @JSONField(serialize = false)
    public String getInitialLetter() {
        if(initialLetter == null){
            CommonUtils.setUserInitialLetter(this);
        }
        return initialLetter;
    }
}
