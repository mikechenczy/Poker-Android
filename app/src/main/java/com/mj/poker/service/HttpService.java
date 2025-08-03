package com.mj.poker.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.mj.poker.Const;
import com.mj.poker.model.PayType;
import com.mj.poker.model.User;
import com.mj.poker.util.HttpUtils;
import com.mj.poker.util.ImageUtils;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HttpService {
    public static HttpClient httpClient;
    public static String url;

    static {
        BasicHttpParams httpParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParams, Const.connectTimeout);
        HttpConnectionParams.setSoTimeout(httpParams, Const.socketTimeout);
        SchemeRegistry schemeRegistry = new SchemeRegistry();
        schemeRegistry.register(new Scheme("http",
                new PlainSocketFactory(), 80));
        ClientConnectionManager connManager = new ThreadSafeClientConnManager(httpParams, schemeRegistry);
        httpClient = new DefaultHttpClient(connManager, httpParams);
    }

    public static String GetRequest(String url) {
        try {
            HttpResponse res = httpClient.execute(new HttpGet(url));
            return EntityUtils.toString(res.getEntity(), "utf-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static JSONObject needUpdate() {
        AtomicBoolean stop = new AtomicBoolean(false);
        if (!Const.ipGot)
            Const.getIp();
        new Thread(() -> {
            try {
                Thread.sleep(Const.socketTimeout);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (!Const.ipGot)
                stop.set(true);
        }).start();
        while (!Const.ipGot && !stop.get()) {
            try {
                Thread.sleep(0);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (!Const.ipGot)
            return null;
        Map<String, String> map = new HashMap<>();
        map.put("version", Const.version);
        url = Const.serverAddr + "core/checkVersionAndroidNew";
        url = HttpUtils.setParamToUrl(url, map);
        String content = GetRequest(url);
        if(content==null)
            return null;
        JSONObject json = null;
        try {
            json = JSONObject.parseObject(content);
        } catch (Exception e){
            e.printStackTrace();
        }
        return json;
    }

    public static User loginWithoutData(String usernameOrEmail, String password) {
        if (!Const.ipGot)
            return null;
        Map<String, String> map = new HashMap<>();
        map.put("username", usernameOrEmail);
        map.put("password", password);
        url = Const.serverAddr + "user/loginWithoutData";
        url = HttpUtils.setParamToUrl(url, map);
        String content = GetRequest(url);
        if(content==null)
            return null;
        JSONObject json = JSONObject.parseObject(content);
        User user = new User();
        if (json.getInteger("errno") == 0) {
            user = JSON.parseObject(json.getString("user"), new TypeReference<User>() {
            });
            return user;
        }
        user.setUserId(json.getInteger("errno"));
        return user;
    }

    public static User login(String usernameOrEmail, String password) {
        AtomicBoolean stop = new AtomicBoolean(false);
        if (!Const.ipGot)
            Const.getIp();
        new Thread(() -> {
            try {
                Thread.sleep(Const.socketTimeout);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (!Const.ipGot)
                stop.set(true);
        }).start();
        while (!Const.ipGot && !stop.get()) {
            try {
                Thread.sleep(0);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (!Const.ipGot)
            return null;
        Map<String, String> map = new HashMap<>();
        map.put("username", usernameOrEmail);
        map.put("password", password);
        map.put("device", "Android");
        map.put("ipAddress", getPublicIp());
        map.put("version", Const.version);
        url = Const.serverAddr + "user/login";
        url = HttpUtils.setParamToUrl(url, map);
        HttpGet get = new HttpGet(url);
        try {
            HttpResponse res = httpClient.execute(get);
            JSONObject json = JSONObject.parseObject(EntityUtils.toString(res.getEntity(), "utf-8"));
            int errno = json.getInteger("errno");
            User user = new User();
            switch (errno) {
                case 0:
                    user = JSON.parseObject(json.getString("user"), new TypeReference<User>(){});
                    return user;
                default:
                    user.setUserId(errno);
                    return user;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static User register(Map<String, String> map) {
        AtomicBoolean stop = new AtomicBoolean(false);
        if (!Const.ipGot)
            Const.getIp();
        new Thread(() -> {
            try {
                Thread.sleep(Const.socketTimeout);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (!Const.ipGot)
                stop.set(true);
        }).start();
        while (!Const.ipGot && !stop.get()) {
            try {
                Thread.sleep(0);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (!Const.ipGot)
            return null;
        url = Const.serverAddr + "user/register";
        url = HttpUtils.setParamToUrl(url, map);
        HttpGet get = new HttpGet(url);
        try {
            HttpResponse res = httpClient.execute(get);
            String content = EntityUtils.toString(res.getEntity(), "utf-8");
            JSONObject json;
            try {
                json = JSONObject.parseObject(content);
            }catch (Exception e){
                e.printStackTrace();
                return null;
            }
            int errno = json.getInteger("errno");
            User user = new User();
            switch (errno) {
                case 0:
                    user = JSON.parseObject(json.getString("user"), new TypeReference<User>(){});
                    return user;
                default:
                    user.setUserId(errno);
                    return user;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static int signIn() {
        url = Const.serverAddr + "core/signIn";
        HttpGet get = new HttpGet(url);
        try {
            HttpResponse res = httpClient.execute(get);
            String content = EntityUtils.toString(res.getEntity(), "utf-8");
            JSONObject json;
            try {
                json = JSONObject.parseObject(content);
            }catch (Exception e){
                e.printStackTrace();
                return -1;
            }
            return json.getInteger("errno");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static int adSignIn() {
        url = Const.serverAddr + "core/adSignIn";
        HttpGet get = new HttpGet(url);
        try {
            HttpResponse res = httpClient.execute(get);
            String content = EntityUtils.toString(res.getEntity(), "utf-8");
            JSONObject json;
            try {
                json = JSONObject.parseObject(content);
            }catch (Exception e){
                e.printStackTrace();
                return -1;
            }
            return json.getInteger("errno");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static PayType[] getPayTypes() {
        url = Const.serverAddr + "pay/getPayTypes";
        HttpGet get = new HttpGet(url);
        try {
            HttpResponse res = httpClient.execute(get);
            String content = EntityUtils.toString(res.getEntity(), "utf-8");
            JSONObject json;
            try {
                json = JSONObject.parseObject(content);
            }catch (Exception e){
                e.printStackTrace();
                return null;
            }
            return JSON.parseObject(json.getString("payTypes"), new TypeReference<PayType[]>(){});
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Object[] pay(int payType) {
        url = Const.serverAddr + "pay/pay";
        Map<String, String> map = new HashMap<>();
        map.put("payType", String.valueOf(payType));
        url = HttpUtils.setParamToUrl(url, map);
        HttpGet get = new HttpGet(url);
        try {
            String content = EntityUtils.toString(httpClient.execute(get).getEntity(), "utf-8");
            if(content.equals(""))
                return null;
            Object[] result = new Object[2];
            result[0] = content.substring(content.length()-40);
            result[1] = ImageUtils.byteArrayToBitmap(ImageUtils.stringToByteArray(content.substring(0, content.length()-40)));
            return result;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Object[] query(String payNo) {
        url = Const.serverAddr + "pay/query";
        Map<String, String> map = new HashMap<>();
        map.put("payNo", String.valueOf(payNo));
        url = HttpUtils.setParamToUrl(url, map);
        HttpGet get = new HttpGet(url);
        try {
            String content = EntityUtils.toString(httpClient.execute(get).getEntity(), "utf-8");
            JSONObject json;
            try {
                json = JSONObject.parseObject(content);
            } catch (Exception e) {
                e.printStackTrace();
                Object[] result = new Object[1];
                result[0] = -1;
                return result;
            }
            Object[] result = new Object[2];
            result[0] = json.getInteger("errno");
            if(json.getInteger("errno")==0) {
                result[1] = JSON.parseObject(json.getString("result"), new TypeReference<Boolean>(){});
            }
            return result;
        } catch (IOException e) {
            e.printStackTrace();
        }
        Object[] result = new Object[1];
        result[0] = -1;
        return result;
    }

    public static boolean logout() {
        url = Const.serverAddr + "user/logout";
        HttpGet get = new HttpGet(url);
        try {
            String content = EntityUtils.toString(httpClient.execute(get).getEntity(), "utf-8");
            return content.equals("true");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static String getPcUrl() {
        url = Const.serverAddr + "core/getPcUrl";
        HttpGet get = new HttpGet(url);
        try {
            return EntityUtils.toString(httpClient.execute(get).getEntity(), "utf-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getMessageContent() {
        return getMessageContent(Const.serverAddr);
    }

    public static String getMessageContent(String baseurl) {
        url = baseurl + "core/getMessageContent?version="+ Const.version+"&device=android";
        HttpGet get = new HttpGet(url);
        try {
            return EntityUtils.toString(httpClient.execute(get).getEntity(), "utf-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Object[] getOrderInfo(int payType) {
        url = Const.serverAddr + "pay/getOrderInfo";
        Map<String, String> map = new HashMap<>();
        map.put("payType", String.valueOf(payType));
        url = HttpUtils.setParamToUrl(url, map);
        HttpGet get = new HttpGet(url);
        try {
            String content = EntityUtils.toString(httpClient.execute(get).getEntity(), "utf-8");
            if(content.equals(""))
                return null;
            Object[] result = new Object[2];
            result[0] = content.substring(content.length()-40);
            result[1] = content.substring(0, content.length()-40);
            return result;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getSignInInfo() {
        url = Const.serverAddr + "core/getSignInInfo";
        HttpGet get = new HttpGet(url);
        try {
            HttpResponse res = httpClient.execute(get);
            String content = EntityUtils.toString(res.getEntity(), "utf-8");
            JSONObject json;
            try {
                json = JSONObject.parseObject(content);
            } catch (Exception e){
                e.printStackTrace();
                return null;
            }
            return json.getString("signInInfo");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static int forgetPasswordEmail(String username) {
        AtomicBoolean stop = new AtomicBoolean(false);
        if (!Const.ipGot)
            Const.getIp();
        new Thread(() -> {
            try {
                Thread.sleep(Const.socketTimeout);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (!Const.ipGot)
                stop.set(true);
        }).start();
        while (!Const.ipGot && !stop.get()) {
            try {
                Thread.sleep(0);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (!Const.ipGot)
            return -1;
        url = Const.serverAddr + "user/forgetPasswordEmail";
        Map<String, String> map = new HashMap<>();
        map.put("username", username);
        url = HttpUtils.setParamToUrl(url, map);
        HttpGet get = new HttpGet(url);
        try {
            HttpResponse res = httpClient.execute(get);
            String content = EntityUtils.toString(res.getEntity(), "utf-8");
            JSONObject json;
            try {
                json = JSONObject.parseObject(content);
            } catch (Exception e){
                e.printStackTrace();
                return -1;
            }
            return json.getInteger("errno");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static int forgetPassword(Map<String, String> map) {
        AtomicBoolean stop = new AtomicBoolean(false);
        if (!Const.ipGot)
            Const.getIp();
        new Thread(() -> {
            try {
                Thread.sleep(Const.socketTimeout);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (!Const.ipGot)
                stop.set(true);
        }).start();
        while (!Const.ipGot && !stop.get()) {
            try {
                Thread.sleep(0);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (!Const.ipGot)
            return -1;
        url = Const.serverAddr + "user/forgetPassword";
        url = HttpUtils.setParamToUrl(url, map);
        HttpGet get = new HttpGet(url);
        try {
            HttpResponse res = httpClient.execute(get);
            String content = EntityUtils.toString(res.getEntity(), "utf-8");
            JSONObject json;
            try {
                json = JSONObject.parseObject(content);
            } catch (Exception e){
                e.printStackTrace();
                return -1;
            }
            return json.getInteger("errno");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static JSONObject createRoom(String title, String description, String password) {
        Map<String, String> map = new HashMap<>();
        map.put("userId", ""+ Const.user.getUserId());
        map.put("title", title);
        map.put("description", description);
        map.put("password", password);
        url = Const.serverAddr + "room/createRoom";
        url = HttpUtils.setParamToUrl(url, map);
        String content = GetRequest(url);
        if (content == null) {
            JSONObject o = new JSONObject();
            o.put("errno", 1);
            return o;
        }
        return JSONObject.parseObject(content);
    }

    public static JSONObject enterRoom(int roomId, String password) {
        Map<String, String> map = new HashMap<>();
        map.put("userId", "" + Const.user.getUserId());
        map.put("roomId", "" + roomId);
        if(password!=null) {
            map.put("password", password);
        }
        url = Const.serverAddr + "room/enterRoom";
        url = HttpUtils.setParamToUrl(url, map);
        String content = GetRequest(url);
        if (content == null) {
            JSONObject o = new JSONObject();
            o.put("errno", 1);
            return o;
        }
        return JSONObject.parseObject(content);
    }

    public static JSONObject exitRoom() {
        Map<String, String> map = new HashMap<>();
        map.put("userId", "" + Const.user.getUserId());
        url = Const.serverAddr + "room/exitRoom";
        url = HttpUtils.setParamToUrl(url, map);
        String content = GetRequest(url);
        if (content == null) {
            JSONObject o = new JSONObject();
            o.put("errno", 1);
            return o;
        }
        return JSONObject.parseObject(content);
    }

    public static JSONObject ready() {
        Map<String, String> map = new HashMap<>();
        map.put("userId", "" + Const.user.getUserId());
        url = Const.serverAddr + "room/ready";
        url = HttpUtils.setParamToUrl(url, map);
        String content = GetRequest(url);
        if (content == null) {
            JSONObject o = new JSONObject();
            o.put("errno", 1);
            return o;
        }
        return JSONObject.parseObject(content);
    }
    public static JSONObject throwCards() {
        Map<String, String> map = new HashMap<>();
        map.put("userId", "" + Const.user.getUserId());
        url = Const.serverAddr + "room/throwCards";
        url = HttpUtils.setParamToUrl(url, map);
        String content = GetRequest(url);
        if (content == null) {
            JSONObject o = new JSONObject();
            o.put("errno", 1);
            return o;
        }
        return JSONObject.parseObject(content);
    }

    public static JSONObject haveCardsAfterLook() {
        Map<String, String> map = new HashMap<>();
        map.put("userId", "" + Const.user.getUserId());
        url = Const.serverAddr + "room/haveCardsAfterLook";
        url = HttpUtils.setParamToUrl(url, map);
        String content = GetRequest(url);
        if (content == null) {
            JSONObject o = new JSONObject();
            o.put("errno", 1);
            return o;
        }
        return JSONObject.parseObject(content);
    }

    public static JSONObject lookCards() {
        Map<String, String> map = new HashMap<>();
        map.put("userId", "" + Const.user.getUserId());
        url = Const.serverAddr + "room/lookCards";
        url = HttpUtils.setParamToUrl(url, map);
        String content = GetRequest(url);
        if (content == null) {
            JSONObject o = new JSONObject();
            o.put("errno", 1);
            return o;
        }
        return JSONObject.parseObject(content);
    }

    public static void have(int baseMoney, int type) {
        have(baseMoney, type, null);
    }

    public static JSONObject have(int baseMoney, int type, String name) {
        Map<String, String> map = new HashMap<>();
        map.put("userId", "" + Const.user.getUserId());
        map.put("money", "" + baseMoney);
        map.put("haveType", "" + type);
        if (name != null) {
            map.put("name", name);
        }
        url = Const.serverAddr + "room/have";
        url = HttpUtils.setParamToUrl(url, map);
        String content = GetRequest(url);
        if (content == null) {
            JSONObject o = new JSONObject();
            o.put("errno", 1);
            return o;
        }
        return JSONObject.parseObject(content);
    }

    public static String getPublicIp() {
        try {
            String path = "http://www.net.cn/static/customercare/yourip.asp";// 要获得html页面内容的地址
            URL url = new URL(path);// 创建url对象
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();// 打开连接
            conn.setRequestProperty("contentType", "GBK"); // 设置url中文参数编码
            conn.setConnectTimeout(5 * 1000);// 请求的时间
            conn.setRequestMethod("GET");// 请求方式
            InputStream inStream = conn.getInputStream();
            // readLesoSysXML(inStream);
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    inStream, "GBK"));
            StringBuffer buffer = new StringBuffer();
            String line = "";
            // 读取获取到内容的最后一行,写入
            while ((line = in.readLine()) != null) {
                buffer.append(line);
            }
            List<String> ips = new ArrayList<String>();
            //用正则表达式提取String字符串中的IP地址
            String regEx="((2[0-4]\\d|25[0-5]|[01]?\\d\\d?)\\.){3}(2[0-4]\\d|25[0-5]|[01]?\\d\\d?)";
            String str = buffer.toString();
            Pattern p = Pattern.compile(regEx);
            Matcher m = p.matcher(str);
            while (m.find()) {
                String result = m.group();
                ips.add(result);
            }
            String PublicIp = ips.get(0);
            // 返回公网IP值
            return PublicIp;
        } catch (Exception e) {
            System.out.println("获取公网IP连接超时");
            return "";
        }
    }
}
