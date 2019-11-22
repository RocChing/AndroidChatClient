package com.roc.chatclient.util;

import android.text.TextUtils;

import java.security.MessageDigest;
import java.util.Locale;

public final class StringUtils {

    /**
     * 是否为空字符串
     *
     * @param str
     * @return
     */
    public static boolean isEmpty(CharSequence str) {
        return TextUtils.isEmpty(str);
    }

    /**
     * 获得md5 字符串
     *
     * @param input 字符串
     * @return
     */
    public static String getMd5String(String input) {
        String result = "";
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] bs = md5.digest(input.getBytes("utf-8"));
            result = byteArrayToHexString(bs);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * byte[]数组转换为16进制的字符串
     *
     * @param data 要转换的字节数组
     * @return 转换后的结果
     */
    public static final String byteArrayToHexString(byte[] data) {
        StringBuilder sb = new StringBuilder(data.length * 2);
        for (byte b : data) {
            int v = b & 0xff;
            if (v < 16) {
                sb.append('0');
            }
            sb.append(Integer.toHexString(v));
        }
        return sb.toString().toUpperCase(Locale.getDefault());
    }

    /**
     * 16进制表示的字符串转换为字节数组
     *
     * @param s 16进制表示的字符串
     * @return byte[] 字节数组
     */
    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] d = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            // 两位一组，表示一个字节,把这样表示的16进制字符串，还原成一个进制字节
            d[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character
                    .digit(s.charAt(i + 1), 16));
        }
        return d;
    }


    /**
     * 16进制直接转换成为字符串(无需Unicode解码)
     *
     * @param hexStr
     * @return
     */
    public static String hexStr2Str(String hexStr) throws Exception {
        String str = "0123456789ABCDEF";
        char[] hexs = hexStr.toCharArray();
        byte[] bytes = new byte[hexStr.length() / 2];
        int n;
        for (int i = 0; i < bytes.length; i++) {
            n = str.indexOf(hexs[2 * i]) * 16;
            n += str.indexOf(hexs[2 * i + 1]);
            bytes[i] = (byte) (n & 0xff);
        }
        return new String(bytes, "UTF-8");
    }
}
