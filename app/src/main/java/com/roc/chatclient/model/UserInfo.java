package com.roc.chatclient.model;

import com.roc.chatclient.util.StringUtils;

public class UserInfo {
    public int Id;

    public String Name;

    public String Avatar;

    public UserInfo() {

    }

    public UserInfo(int id, String name, String avatar) {
        Id = id;
        Name = name;
        Avatar = avatar;
    }
}
