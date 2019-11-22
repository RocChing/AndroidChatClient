package com.roc.chatclient.socket.structures;

/**
 * author       :   long
 * created on   :   2017/11/30
 * description  :   Server地址
 */

public class TcpAddress {
    public String ip;
    public int     port;

    public TcpAddress(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }
}