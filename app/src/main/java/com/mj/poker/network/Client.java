package com.mj.poker.network;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mj.poker.Const;
import com.mj.poker.ui.MainActivity;
import com.mj.poker.ui.RoomsActivity;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Client {
    public static Socket socket;
    public static DataInputStream dis;
    public static DataOutputStream dos;
    public static int lastMoney;
    public static int currentMoney;
    public static int playerCount;
    public static JSONArray players;
    public static String cards;

    public static Thread readThread;

    public static void connect() {
        int i=0;
        while (true) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            try {
                Socket socket = new Socket("81.68.185.86", 9080);
                if(Client.socket!=null ) {
                    try {
                        Client.socket.shutdownInput();
                        Client.socket.shutdownOutput();
                        Client.socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                Client.socket = socket;
                dis = new DataInputStream(socket.getInputStream());
                dos = new DataOutputStream(socket.getOutputStream());
                dos.writeUTF(Const.user.getUsername());
                Const.user.setMoney(dis.readInt());
                RoomsActivity.INSTANCE.setMoney();
                readThread = new Thread(Client::read);
                readThread.start();
                return;
            } catch (IOException e) {
                e.printStackTrace();
                try {
                    if (dis != null)
                        dis.close();
                    if (dos != null)
                        dos.close();
                    if (socket != null)
                        socket.close();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
                if(i++==5) {
                    MainActivity.INSTANCE.showText("无法连接服务器");
                }
            }
        }
    }

    public static void read() {
        try {
            while (true) {
                String m = dis.readUTF();
                System.out.println(m);
                JSONObject msg = JSON.parseObject(m);
                switch (msg.getString("type")) {
                    case "players": {
                        JSONArray players = msg.getJSONArray("players");
                        MainActivity.INSTANCE.runOnUiThread(() -> {
                            MainActivity.INSTANCE.playerListViewAdapter.setData(players);
                            MainActivity.INSTANCE.playerListViewAdapter.setCurrentName(msg.getString("currentPlayer"));
                            MainActivity.INSTANCE.playerListViewAdapter.notifyDataSetChanged();
                        });
                        break;
                    }
                    case "GameStart": {
                        lastMoney = Const.user.getMoney();
                        System.out.println(lastMoney);
                        cards = msg.getString("cards");
                        currentMoney = msg.getInteger("currentMoney");
                        playerCount = msg.getInteger("playerCount");
                        players = msg.getJSONArray("players");
                        MainActivity.INSTANCE.showCards();
                        MainActivity.INSTANCE.startGame(msg.getString("current"), msg.getString("totalMoney"));
                        break;
                    }
                    case "gameState": {
                        currentMoney = msg.getInteger("currentMoney");
                        playerCount = msg.getInteger("playerCount");
                        players = msg.getJSONArray("players");
                        MainActivity.INSTANCE.gameState(msg.getString("current"), msg.getString("totalMoney"));
                        break;
                    }
                    case "Money": {
                        Const.user.setMoney(msg.getInteger("money"));
                        RoomsActivity.INSTANCE.setMoney();
                        break;
                    }
                    case "win": {
                        MainActivity.INSTANCE.hideButtons();
                        MainActivity.INSTANCE.runOnUiThread(() -> MainActivity.INSTANCE.showMessage("你赢得了" + (Const.user.getMoney() - lastMoney) + "个豆子\n"+(msg.getString("cards")!=null?"对手的牌是:\n"+msg.getString("cards"):""), "你赢了"));
                        break;
                    }
                    case "lose": {
                        MainActivity.INSTANCE.hideButtons();
                        MainActivity.INSTANCE.runOnUiThread(() -> MainActivity.INSTANCE.showMessage("你输了" + (lastMoney - Const.user.getMoney()) + "个豆子\n" + (msg.getString("playerCards")!=null?"对手的牌是:\n"+msg.getString("playerCards"):""), "开牌"));
                        break;
                    }
                    case "lookPlayerCards": {
                        MainActivity.INSTANCE.runOnUiThread(() -> {
                            MainActivity.INSTANCE.lookButton.setEnabled(true);
                            MainActivity.INSTANCE.throwButton.setEnabled(true);
                            MainActivity.INSTANCE.haveButton.setEnabled(true);
                        });
                        MainActivity.INSTANCE.lookCardsMessage("你查看了玩家" + msg.getString("name") + "的牌\n" + msg.getString("cards")+"\n是否继续要牌", "看牌");
                        break;
                    }
                    case "lookedBy": {
                        MainActivity.INSTANCE.showMessage(msg.getString("name")+"看了你的牌", "看牌");
                        break;
                    }
                    case "GameStop": {
                        MainActivity.INSTANCE.hideButtons();
                        backMoney(lastMoney);
                        MainActivity.INSTANCE.showMessage("因为有玩家中途推出，游戏终止，豆子返还", "游戏结束");
                        break;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            //if(e.toString().contains("Software caused connection abort")) {

            //} else {
            MainActivity.INSTANCE.showText("已断开服务器");
            MainActivity.INSTANCE.resetUI();
            //}
        }
        new Thread(Client::connect).start();
    }

    public static void send(String str) {
        if(socket==null || dis==null || dos==null) {
            MainActivity.INSTANCE.resetUI();
            return;
        }
        try {
            dos.writeUTF(str);
            return;
        } catch(IOException e) {
            e.printStackTrace();
        }
        try {
            if (dis != null)
                dis.close();
            if (dos != null)
                dos.close();
            if (socket != null)
                socket.close();
        } catch (IOException ioException) {
        }
        MainActivity.INSTANCE.resetUI();
    }

    public static void ready() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", "ready");
        send(jsonObject.toString());
    }

    public static void lookCards() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", "lookCards");
        send(jsonObject.toString());
    }

    public static void throwCards() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", "throwCards");
        send(jsonObject.toString());
    }

    public static void haveCardsAfterLook() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", "haveCardsAfterLook");
        send(jsonObject.toString());
    }

    public static void backMoney(int money) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", "backMoney");
        jsonObject.put("money", money);
        send(jsonObject.toString());
    }

    public static void have(int baseMoney, int type) {
        have(baseMoney, type, null);
    }
    public static void have(int money, int type, String name) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", "have");
        jsonObject.put("money", money);
        jsonObject.put("haveType", type);
        if (name!=null) {
            jsonObject.put("name", name);
        }
        send(jsonObject.toString());
    }
}
