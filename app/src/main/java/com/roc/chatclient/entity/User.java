package com.roc.chatclient.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.roc.chatclient.util.CommonUtils;

public class User extends BaseEntity {
    public String Name;

    public String Password;

    public int Gender;

    public String Phone;

    public String Email;

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
