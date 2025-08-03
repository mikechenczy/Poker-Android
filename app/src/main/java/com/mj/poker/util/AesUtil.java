package com.mj.poker.util;

import com.mj.poker.Const;

import java.nio.charset.StandardCharsets;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class AesUtil {

    public static String encrypt(String str) {
        return encrypt(Const.aes, str);
    }

    public static String decrypt(String str) {
        return decrypt(Const.aes, str);
    }

    public static String encrypt(String str, String str2) {
        try {
            return byteToHex(encrypt(str, str2.getBytes()));
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String decrypt(String str, String str2) {
        if (str2 == null) {
            return null;
        }
        byte[] hexToByte = hexToByte(str2);
        if (hexToByte == null) {
            return "";
        }
        try {
            return new String(decrypt(str, hexToByte));
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    private static byte[] encrypt(String str, byte[] bArr) throws Exception {
        SecretKeySpec secretKeySpec = new SecretKeySpec(str.getBytes(StandardCharsets.UTF_8), "AES");
        Cipher instance = Cipher.getInstance("AES/ECB/PKCS5Padding");
        instance.init(1, secretKeySpec);
        return instance.doFinal(bArr);
    }

    private static byte[] decrypt(String str, byte[] bArr) throws Exception {
        if (bArr == null) {
            return null;
        }
        SecretKeySpec secretKeySpec = new SecretKeySpec(str.getBytes(), "AES");
        Cipher instance = Cipher.getInstance("AES/ECB/NoPadding");
        instance.init(2, secretKeySpec);
        return instance.doFinal(bArr);
    }

    private static final String HEX = "0123456789ABCDEF";

    public static byte[] hexToByte(String str) {
        byte[] bArr = null;
        if (str == null) {
            return null;
        }
        int length = str.length();
        if (length != 0 && length % 2 == 0) {
            int i = length / 2;
            bArr = new byte[i];
            for (int i2 = 0; i2 < i; i2++) {
                int i3 = i2 * 2;
                bArr[i2] = Integer.valueOf(str.substring(i3, i3 + 2), 16).byteValue();
            }
        }
        return bArr;
    }

    public static String byteToHex(byte[] bArr) {
        if (bArr == null) {
            return "";
        }
        StringBuffer stringBuffer = new StringBuffer(bArr.length * 2);
        for (byte appendHex : bArr) {
            appendHex(stringBuffer, appendHex);
        }
        return stringBuffer.toString();
    }

    private static void appendHex(StringBuffer stringBuffer, byte b) {
        stringBuffer.append(HEX.charAt((b >> 4) & 15));
        stringBuffer.append(HEX.charAt(b & 15));
    }
}