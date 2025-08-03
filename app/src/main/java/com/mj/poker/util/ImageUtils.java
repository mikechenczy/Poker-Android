package com.mj.poker.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class ImageUtils {

    public static String byteArrayToString(byte[] bytes){
        return com.mj.poker.util.Base64.getEncoder().encodeToString(bytes);
    }

    public static byte[] stringToByteArray(String str){
        return com.mj.poker.util.Base64.getDecoder().decode(str);
    }

    public static Bitmap byteArrayToBitmap(byte[] b) {
        return BitmapFactory.decodeByteArray(b, 0, b.length);
    }
}
