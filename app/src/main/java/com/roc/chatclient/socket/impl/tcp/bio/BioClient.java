package com.roc.chatclient.socket.impl.tcp.bio;

import android.util.Log;

import com.roc.chatclient.socket.GClient;
import com.roc.chatclient.socket.impl.tcp.IReceiveData;
import com.roc.chatclient.socket.structures.BaseClient;
import com.roc.chatclient.socket.structures.BaseMessageProcessor;
import com.roc.chatclient.socket.structures.IConnectListener;
import com.roc.chatclient.socket.structures.TcpAddress;
import com.roc.chatclient.socket.structures.message.Message;
import com.roc.chatclient.util.CommonUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketException;

/**
 * author       :   long
 * created on   :   2017/11/30
 * description  :   BioClient
 */
public class BioClient extends BaseClient {

    static {
        GClient.init();
    }

    //-------------------------------------------------------------------------------------------
    private BioConnector mConnector;
    private IReceiveData receiveData;

    public BioClient(BaseMessageProcessor mMessageProcessor, IConnectListener mConnectListener, IReceiveData iReceiveData) {
        super(mMessageProcessor);
        mConnector = new BioConnector(this, mConnectListener);
        receiveData = iReceiveData;
    }

    //-------------------------------------------------------------------------------------------
    public void setConnectAddress(TcpAddress[] tcpArray) {
        mConnector.setConnectAddress(tcpArray);
    }

    public void setConnectTimeout(long connect_timeout) {
        mConnector.setConnectTimeout(connect_timeout);
    }

    public void connect() {
        mConnector.connect();
    }

    public void disconnect() {
        mConnector.disconnect();
    }

    public void reconnect() {
        mConnector.reconnect();
    }

    public boolean isConnected() {
        return mConnector.isConnected();
    }

    //-------------------------------------------------------------------------------------------
    private OutputStream mOutputStream = null;
    private InputStream mInputStream = null;

    public void init(OutputStream mOutputStream, InputStream mInputStream) throws IOException {
        this.mOutputStream = mOutputStream;
        this.mInputStream = mInputStream;
    }

//    @Override
//    public void onReceiveData(byte[] src, int offset, int length) {
//        Message msg = mReadMessageQueen.build(src, offset, length);
//        mReadMessageQueen.add(msg);
//        if (receiveData != null) {
//            receiveData.HandleMsg(msg);
//        }
//    }

    @Override
    public void onCheckConnect() {
        mConnector.checkConnect();
    }

    public void onClose() {
        mOutputStream = null;
        mInputStream = null;
    }

    public boolean onRead() {
        boolean readRet = false;
        boolean flag = null != mMessageProcessor;
        try {
            //int maximum_length = 64 * 1024;
            byte[] bodyBytes = new byte[maxSize];

            while (true) {
                int numRead = 0;
                boolean newMsg = false;
                int msgAllLength = 0;
//                Log.d(Tag, "begin receive....");
                if (!newMsg) {
                    int firstChar = mInputStream.read();
                    if (firstChar == newMsgFlag) {
                        byte[] bytesLength = new byte[4];
                        mInputStream.read(bytesLength, 0, 4);
                        msgAllLength = CommonUtils.byteArrayToInt2(bytesLength);
                        newMsg = true;
                    }
                }
//                Log.d(Tag, "the msgAllLength value is:" + msgAllLength);
                if (!newMsg || msgAllLength < 1) {
                    newMsg = false;
                    continue;
                }

                int read = 0;
                while ((read = mInputStream.read(bodyBytes, 0, maxSize)) > 0) {
//                    Log.d(Tag, "the read value is:" + read);
                    if (read > 0) {
                        numRead += read;
                        mMessageProcessor.onReceiveData(this, bodyBytes, 0, read);
                        if (msgAllLength > numRead) {
                            continue;
                        }
//                        Log.d(Tag, "the numRead value is:" + numRead);
                        mMessageProcessor.onReceiveDataCompleted(this);
                        break;
                    }
                    continue;
                }
                Thread.sleep(100);
            }
        } catch (SocketException e) {
            e.printStackTrace();//客户端主动socket.stopConnect()会调用这里 java.net.SocketException: Socket closed
            readRet = false;
        } catch (IOException e1) {
            e1.printStackTrace();
            readRet = false;
        } catch (Exception e2) {
            e2.printStackTrace();
            readRet = false;
        }

        if (flag) {
            mMessageProcessor.onReceiveDataCompleted(this);
        }

        Log.d(Tag, "end");
        //退出客户端的时候需要把要该客户端要写出去的数据清空
        if (!readRet) {
            Message msg = pollWriteMessage();
            while (null != msg) {
                removeWriteMessage(msg);
                msg = pollWriteMessage();
            }
        }
        return false;
    }

    public boolean onWrite() {
        boolean writeRet = true;
        Message msg = pollWriteMessage();
        try {
            while (null != msg) {
                mOutputStream.write(msg.data, msg.offset, msg.length);
                mOutputStream.flush();
                removeWriteMessage(msg);
                msg = pollWriteMessage();
            }
        } catch (SocketException e) {
            e.printStackTrace();//客户端主动socket.stopConnect()会调用这里 java.net.SocketException: Socket closed
            writeRet = false;
        } catch (IOException e1) {
            e1.printStackTrace();//发送的时候出现异常，说明socket被关闭了(服务器关闭)java.net.SocketException: sendto failed: EPIPE (Broken pipe)
            writeRet = false;
        } catch (Exception e2) {
            e2.printStackTrace();
            writeRet = false;
        }

        //退出客户端的时候需要把该客户端要写出去的数据清空
        if (!writeRet) {
            if (null != msg) {
                removeWriteMessage(msg);
            }
            msg = pollWriteMessage();
            while (null != msg) {
                removeWriteMessage(msg);
                msg = pollWriteMessage();
            }
        }

        return writeRet;
    }
}
