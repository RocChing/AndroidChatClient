package com.roc.chatclient.model;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.File;
import java.math.BigDecimal;

public class FileInfo {
    private String name;

    private double size;

    private String path;

    private String thumbPath;

    private int type;

    public FileInfo() {

    }

    public FileInfo(File file, String thumbPath) {
        name = file.getName();
        size = file.length() / 1024d;
        path = file.getAbsolutePath();
        this.thumbPath = thumbPath;
    }

    @JSONField(serialize = false)
    public String getStringSize() {
        BigDecimal bd = new BigDecimal(size);
        double f1 = bd.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        return f1 + "";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getSize() {
        return size;
    }

    public void setSize(double size) {
        this.size = size;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getThumbPath() {
        return thumbPath;
    }

    public void setThumbPath(String thumbPath) {
        this.thumbPath = thumbPath;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
