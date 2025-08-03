package com.mj.poker.client;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mj.poker.Const;
import com.mj.poker.ui.MainActivity;
import com.mj.poker.ui.RoomsActivity;

import io.netty.channel.ChannelHandlerContext;

/**
 * 处理客户端的channel
 *
 * @author lucher
 */
public class ClientHandler {


    public ChannelHandlerContext ctx;
    public int lastMoney;
    public int currentMoney;
    public int playerCount;
    public JSONArray players;
    public String cards;
    public boolean createRoom;


    public void handleMessage(String m) {
        JSONObject msg = JSON.parseObject(m);
        switch (msg.getString("type")) {
            case "isOnline":
                return;
            case "rooms": {
                if (RoomsActivity.INSTANCE != null) {
                    RoomsActivity.INSTANCE.runOnUiThread(() -> {
                        RoomsActivity.INSTANCE.roomListAdapter.setData(msg.getJSONArray("rooms"));
                        RoomsActivity.INSTANCE.roomListAdapter.notifyDataSetChanged();
                    });
                }
                break;
            }
            case "roomPlayersChanged": {
                JSONArray players = msg.getJSONArray("players");
                if (MainActivity.INSTANCE == null && !createRoom) {
                    RoomsActivity.INSTANCE.runOnUiThread(() -> RoomsActivity.INSTANCE.startMainActivity());
                    while (MainActivity.INSTANCE==null) {
                        try {
                            Thread.sleep(0);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    while (MainActivity.INSTANCE==null) {
                        try {
                            Thread.sleep(0);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
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
                MainActivity.INSTANCE.showMessage("因为有玩家中途推出，游戏终止，豆子返还", "游戏结束");
                break;
            }
        }
    }

    public void setCtx(ChannelHandlerContext ctx) {
        this.ctx = ctx;
    }
}