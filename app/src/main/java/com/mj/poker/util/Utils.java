package com.mj.poker.util;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;

import com.mj.poker.Const;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.DecimalFormat;

public class Utils {
    public static boolean stringIsEmpty(String s) {
        return s==null || s.isEmpty();
    }

    public static boolean check(String username, String password) {
        /*return !(username.contains(" ") || password.contains(" ") || username.equals("") || password.equals("") || username.contains(",") || username.contains("&")
                || username.contains("?") || password.contains("&") || password.contains("?") || username.contains("=") || password.contains("=") || username.contains("/")
                || password.contains("/") || username.contains("\\") || password.contains("\\"));*/
        return !(username.contains(" ") || password.contains(" ") || username.equals("") || password.equals("") || username.length()>=15 || password.length()>=20);
    }

    public static boolean check(String password) {
        return !(password.contains(" ") || password.equals("") || password.length()>=20);
    }

    public static boolean checkUsername(String username) {
        return !(username.contains(" ") || username.equals("") || username.length()>=15);
    }
    public static boolean isEmail(String str) {
        boolean isEmail = false;
        String expr = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})$";

        if (str.matches(expr)) {
            isEmail = true;
        }
        if(isEmail) {
            return str.endsWith("@126.com") || str.endsWith("@163.com") || str.endsWith("@qq.com") || str.endsWith("@sina.com") ||
                    str.endsWith("@sina.cn") || str.endsWith("@outlook.com") || str.endsWith("@gmail.com") || str.endsWith("@mjczy.top");
        }
        return false;
    }

    public static String string(String s) {
        return s==null?"":s;
    }

    public static void sleep(){
        try {
            Thread.sleep(0);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static String decodeString(String s) {
        try {
            return URLDecoder.decode(s, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return s;
        }
    }

    public static boolean checkFileName(String fileName) {
        return fileName != null && (!(fileName.contains("\\") || fileName.contains("/") || fileName.contains(":") || fileName.contains("*") || fileName.contains("?")
                || fileName.contains("\"") || fileName.contains("<") || fileName.contains(">") || fileName.contains("|") || fileName.equals("")));
    }

    public static String getHumanFileSize(File file){
        if(file==null)
            return null;
        long blockSize=0;
        try {
            if(file.isDirectory()){
                blockSize = getFileSizes(file);
            }else{
                blockSize = getFileSize(file);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return FormatFileSize(blockSize);
    }

    private static long getFileSize(File file)
    {
        return file.length();
    }

    private static long getFileSizes(File f)
    {
        long size = 0;
        File files[] = f.listFiles();
        for (int i = 0; i < files.length; i++)
            size = size + (files[i].isDirectory()?getFileSizes(files[i]):getFileSize(files[i]));
        return size;
    }

    public static String FormatFileSize(long fileS)
    {
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = "";
        String wrongSize="0B";
        if(fileS==0){
            return wrongSize;
        }
        if (fileS < 1024){
            fileSizeString = df.format((double) fileS) + "B";
        }
        else if (fileS < 1048576){
            fileSizeString = df.format((double) fileS / 1024) + "KB";
        }
        else if (fileS < 1073741824){
            fileSizeString = df.format((double) fileS / 1048576) + "MB";
        }
        else{
            fileSizeString = df.format((double) fileS / 1073741824) + "GB";
        }
        return fileSizeString;
    }

    public static String byteArrayToString(byte[] bytes) {
        if(bytes==null)
            return null;
        return new com.mj.poker.util.BASE64Encoder().encode(bytes);
    }

    public static byte[] stringToByteArray(String str) {
        if(str==null)
            return null;
        try {
            return new com.mj.poker.util.BASE64Decoder().decodeBuffer(str);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Serializable byteArrayToObject(byte[] bytes) {
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        try {
            Serializable result;
            ObjectInputStream oos = new ObjectInputStream(bais);
            result = (Serializable) oos.readObject();
            oos.close();
            bais.close();
            return result;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static byte[] objectToByteArray(Serializable obj) {
        byte[] result;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(obj);
            oos.flush();
            result = baos.toByteArray();
            return result;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Serializable stringToObject(String str) {
        return byteArrayToObject(stringToByteArray(str));
    }

    public static String objectToString(Serializable obj) {
        return byteArrayToString(objectToByteArray(obj));
    }

    public static String getHumanTime(long ms) {
        if(ms<=0)
            return "已到期";
        long day = ms / 1000 / 60 / 60 / 24;
        long hour = ms / 1000 / 60 / 60 % 24;
        long min = ms / 1000 / 60 % 60;
        long sec = ms / 1000 % 60;
        String result;
        if(day>0)
            result = day+"天"+hour+"时"+min+"分";
        else if(hour>0)
            result = hour+"时"+min+"分";
        else if(min>0)
            result = min+"分"+sec+"秒";
        else
            result = sec+"秒";
        return result;
    }

    public static int[] sortIntArrayFromBiggest(int[] arr){
        for (int i = 1; i < arr.length; i++) {
            for (int j=i;j>0;j--){
                if (arr[j]<=arr[j-1]){
                    break;
                }else{
                    int temp = arr[j];
                    arr[j] = arr[j-1];
                    arr[j-1] = temp;
                }
            }
        }
        return arr;
    }

    public static int[] integerArrayToIntArray(Integer[] array) {
        if(array==null)
            return null;
        if(array.length==0)
            return new int[]{};
        int[] result = new int[array.length];
        for(int i=0;i<array.length;i++)
            result[i] = array[i];
        return result;
    }

    public static boolean isVip() {
        return Const.user != null && Const.user.getVip() - System.currentTimeMillis() > 0;
    }

    public static String getVersion(Activity activity) {
        PackageInfo pkg;
        String versionName = "";
        try {
            pkg = activity.getPackageManager().getPackageInfo(activity.getApplication().getPackageName(), 0);
            versionName = pkg.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionName;
    }

    public static void verifyStoragePermissions(Activity activity) {
        try {
            //检测是否有写的权限
            int permission = ActivityCompat.checkSelfPermission(activity,
                    "android.permission.WRITE_EXTERNAL_STORAGE");
            if (permission != PackageManager.PERMISSION_GRANTED) {
                // 没有写的权限，去申请写的权限，会弹出对话框
                ActivityCompat.requestPermissions(activity, new String[]{
                        "android.permission.READ_EXTERNAL_STORAGE",
                        "android.permission.WRITE_EXTERNAL_STORAGE" },1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
