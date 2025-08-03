package com.mj.poker.util;

import android.app.Activity;

import com.alibaba.fastjson.JSONObject;
import com.mj.poker.Const;
import com.mj.poker.service.HttpService;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class ServerUtil {

    public static JSONObject getAddress(String name) {
        JSONObject answer = new JSONObject();
        answer.put("ipv4", new String(Base64.getDecoder().decode(GetTextRecord.getATxt("ipv4."+name+"."+ Const.baseDomain))));
        answer.put("ipv6", new String(Base64.getDecoder().decode(GetTextRecord.getATxt("ipv6."+name+"."+ Const.baseDomain))));
        answer.put("ipv4ws", new String(Base64.getDecoder().decode(GetTextRecord.getATxt("ipv4ws."+name+"."+ Const.baseDomain))));
        answer.put("ipv6ws", new String(Base64.getDecoder().decode(GetTextRecord.getATxt("ipv6ws."+name+"."+ Const.baseDomain))));
        return answer;
    }

    public static boolean ipv6Test() {
        return HttpService.getMessageContent(Const.serverAddrs.getString("ipv6"))!=null;
    }

    public static boolean isReachable(String addr) {
        return isReachable(addr, 80);
    }

    public static boolean isReachable(String addr, int port) {
        return isReachable(addr, port, Const.connectTimeout);
    }

    public static boolean isReachable(String addr, int port, int timeout) {
        // Any Open port on other machine
        // openPort =  22 - ssh, 80 or 443 - webserver, 25 - mailserver etc.
        try {
            new Socket().connect(new InetSocketAddress(addr, port), timeout);
            return true;
        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public static boolean checkNetwork() {
        return isReachable("www.baidu.com");
    }


    public static void refreshIpv4AndIpv6() {
        new Thread(() -> {
            //if (ServerUtil.checkNetwork()) {
                boolean ipv6Support = ServerUtil.ipv6Test();
                if(Const.ipv6Support==ipv6Support)
                    return;
                Const.ipv6Support = ipv6Support;
                Const.serverAddr = Const.serverAddrs.getString(Const.ipv6Support ? "ipv6" : "ipv4");
                Const.wsAddr = Const.serverAddrs.getString(Const.ipv6Support ? "ipv6ws" : "ipv4ws");
            //}
        }).start();
    }
    public static void refreshWebSocketIpv4AndIpv6(Activity activity) {
        new Thread(() -> {
            //if (ServerUtil.checkNetwork()) {
                boolean ipv6Support = ServerUtil.ipv6Test();
                if(Const.ipv6Support==ipv6Support)
                    return;
                Const.ipv6Support = ipv6Support;
                Const.serverAddr = Const.serverAddrs.getString(Const.ipv6Support ? "ipv6" : "ipv4");
                Const.wsAddr = Const.serverAddrs.getString(Const.ipv6Support ? "ipv6ws" : "ipv4ws");
                //activity.runOnUiThread(() -> ClientHelper.reconnect(activity));
            //}
        }).start();
    }
}
