package com.roc.chatclient.model;

import java.io.File;

public class VoiceInfo extends FileInfo {
    public int Length;

    public VoiceInfo() {

    }

    public VoiceInfo(File file, int length) {
        super(file, "");
        Length = length;
    }
}
