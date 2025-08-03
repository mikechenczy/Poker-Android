package com.mj.poker;

import com.alibaba.fastjson.JSONObject;
import com.mj.poker.model.PayType;
import com.mj.poker.model.User;
import com.mj.poker.util.DataUtil;
import com.mj.poker.util.ServerUtil;
import com.mj.poker.util.Utils;

import java.util.Arrays;
import java.util.List;

public class Const {
    public static final boolean isDebug = false;
    public static List<String> baseDomains = Arrays.asList("mjczy.top", "mjczy.us.kg", "mjczy.life", "mjczy.xyz", "mjczy.club", "mjczy.info", "mjczy.org", "mjczy.net");
    public static List<String> dnsServers = Arrays.asList("", "223.5.5.5", "2400:3200::1", "114.114.114.114", "2001:dc7:1000::1", "1.1.1.1", "8.8.8.8");
    public static String baseDomain = baseDomains.get(0);
    public static JSONObject serverAddrs;
    public static String serverAddr;
    public static String wsAddr;
    public static boolean ipGot;
    public static boolean ipv6Support;
    public static String domain80 = "http://home.mjczy.top/updateFiles/";
    public static final int socketTimeout = 3000;
    public static final int connectTimeout = 3000;
    public static User user;
    public static boolean autoLogin = true;
    public static String version;
    public static String autoLoginUsername;
    public static String autoLoginPassword;
    public static PayType payType;
    public static String payNo;
    public static boolean agree;
    public static String neverShowMessageDialog;
    public static String neverShowVersionDialog;
    public static String roomTitle;
    public static String roomDescription;
    public static int roomId;

    static {
        serverAddrs = new JSONObject();
        serverAddrs.put("ipv4", "http://home.mjczy.top/lrp/");
        serverAddrs.put("ipv6", "http://ipv6.mjczy.top:2095/");
        serverAddrs.put("ipv4ws", "ws://home.mjczy.top/lrp/websocket/");
        serverAddrs.put("ipv6ws", "ws://ipv6.mjczy.top:2095/websocket/");
        serverAddrs.put("host", "");
        serverAddr = serverAddrs.getString("ipv4");
        wsAddr = serverAddrs.getString("ipv4ws");
        DataUtil.load();
        DataUtil.save();//In order to first save generated DEVICE ID
        getIp();
    }

    public static void getIp() {
        if(isDebug) {
            serverAddrs = new JSONObject();
            //serverInfo.put("ipv4", "http://192.168.1.99:2095/");
            //serverInfo.put("ipv6", "http://192.168.1.99:2095/");
            //serverInfo.put("ipv4", "http://home.mjczy.top/leapremote/");
            //serverInfo.put("ipv6", "http://home.mjczy.top/leapremote/");
            //serverInfo.put("ipv6-new", "http://ipv6.mjczy.top:2086/");
            //serverInfo.put("ipv4ws", "ws://192.168.1.99:2095/websocket/");
            //serverInfo.put("ipv6ws", "ws://192.168.1.99:2095/websocket/");
            //serverInfo.put("ipv4ws", "ws://home.mjczy.top/leapremote/websocket/");
            //serverInfo.put("ipv6ws", "ws://home.mjczy.top/leapremote/websocket/");
            //serverInfo.put("ipv6Url-new", "ws://ipv6.mjczy.top:2086/websocket/");

            serverAddrs.put("ipv4", "http://home.mjczy.top/lrp/");
            serverAddrs.put("ipv6", "http://home.mjczy.top/lrp/");
            serverAddrs.put("ipv4ws", "ws://home.mjczy.top/lrp/websocket/");
            serverAddrs.put("ipv6ws", "ws://home.mjczy.top/lrp/websocket/");

            //serverInfo.put("ipv4", "http://192.168.1.99:2095/");
            //serverInfo.put("ipv6", "http://192.168.1.99:2095/");
            //serverInfo.put("ipv4ws", "ws://192.168.1.99:2095/websocket/");
            //serverInfo.put("ipv6ws", "ws://192.168.1.99:2095/websocket/");
            serverAddrs.put("host", "");
            serverAddr = serverAddrs.getString("ipv4");
            wsAddr = serverAddrs.getString("ipv4ws");
            System.out.println(serverAddrs);
            ipGot = true;
            return;
        }
        new Thread(() -> {
            ipGot = false;
            try {
                serverAddrs = ServerUtil.getAddress("poker");
                serverAddr = ipv6Support ? serverAddrs.getString("ipv6") : serverAddrs.getString("ipv4");
                wsAddr = ipv6Support ? serverAddrs.getString("ipv6ws") : serverAddrs.getString("ipv4ws");
                System.out.println(serverAddrs);
                ipGot = true;
                return;
            } catch (Exception e) {
                e.printStackTrace();
            }
            getDomainAndIp();
        }).start();
    }

    public static void getDomainAndIp() {
        new Thread(() -> {
            ipGot = false;
            while (true) {
                try {
                    Thread.sleep(0);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                for (String domain : baseDomains) {
                    baseDomain = domain;
                    new Thread(() -> ipv6Support = ServerUtil.ipv6Test()).start();
                    try {
                        serverAddrs = ServerUtil.getAddress("poker");
                        ipv6Support = ServerUtil.ipv6Test();
                        serverAddr = ipv6Support ? serverAddrs.getString("ipv6") : serverAddrs.getString("ipv4");
                        wsAddr = ipv6Support ? serverAddrs.getString("ipv6ws") : serverAddrs.getString("ipv4ws");
                        ipGot = true;
                        return;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }
}
