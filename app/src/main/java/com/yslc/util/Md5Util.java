package com.yslc.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 使用MD5对字符串进行加密
 *
 * @author HH
 */
public class Md5Util {
    static public String getMD5(byte[] source) {
        String s = null;
        //讲字符转换成16进制表示字符
        char[] hexDigits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'a', 'b', 'c', 'd', 'e', 'f'};
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(source);
            byte[] tmp = md.digest();
            char[] str = new char[16 * 2];
            int k = 0;
            byte byte0;
            // 分别对字节高4位和低4位进行16进制转换
            for (int i = 0; i < 16; i++) {
                byte0 = tmp[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            s = new String(str);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return s;
    }
}
