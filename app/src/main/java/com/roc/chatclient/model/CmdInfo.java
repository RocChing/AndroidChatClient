package com.roc.chatclient.model;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import com.roc.chatclient.util.StringUtils;

import java.io.Serializable;

public class CmdInfo {
    public int Type;

    public Object Data;

    public String Token;

    public CmdInfo() {

    }

    private String validString;

    public CmdInfo(String validString, CmdType type, Object data) {
        Type = type.getValue();
        Data = data;
        this.validString = validString;
        setToken();
    }

    @JSONField(serialize = false)
    public String getDataJson() {
        return JSON.toJSONString(Data);
    }

    public <T> T of(Class<T> tClass) {
        String json = getDataJson();
        return JSON.parseObject(json, tClass);
    }

    public boolean isValid(String validString) {
        String json = getDataJson();
        String str = validString + "-" + json;
        String token = StringUtils.getMd5String(str);
        return token.equalsIgnoreCase(Token);
    }

    private void setToken() {
        String json = getDataJson();
        String str = validString + "-" + json;
        Token = StringUtils.getMd5String(str);
    }

    public CmdInfo clone(Object data) {
        return new CmdInfo(validString, CmdType.getType(Type), data);
    }
}
