package com.mj.poker.ui;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.alibaba.fastjson.JSONArray;
import com.mj.poker.Const;
import com.mj.poker.client.ClientHandler;
import com.mj.poker.client.ConnectionHandler;
import com.mj.poker.service.HttpService;
import com.mj.poker.ui.adapter.PlayerListViewAdapter;
import com.mj.poker.R;
import com.mj.poker.ui.compomnets.HorizontalListView;
import com.mj.poker.util.BitmapUtil;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static MainActivity INSTANCE;

    public HorizontalListView playerListView;
    public PlayerListViewAdapter playerListViewAdapter;
    public Button readyButton;
    public TextView totalMoneyTextView;
    public Button lookButton;
    public Button throwButton;
    public Button haveButton;
    public ImageView card1ImageView;
    public ImageView card2ImageView;
    public ImageView card3ImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        INSTANCE = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        totalMoneyTextView = findViewById(R.id.totalMoneyText);
        readyButton = findViewById(R.id.readyButton);
        lookButton = findViewById(R.id.lookButton);
        throwButton = findViewById(R.id.throwButton);
        haveButton = findViewById(R.id.haveButton);
        card1ImageView = findViewById(R.id.card1ImageView);
        card2ImageView = findViewById(R.id.card2ImageView);
        card3ImageView = findViewById(R.id.card3ImageView);

        playerListView = findViewById(R.id.horizon_listview);
        playerListViewAdapter = new PlayerListViewAdapter(getApplicationContext(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
        playerListView.setAdapter(playerListViewAdapter);
        readyButton.setOnClickListener((v) -> {
            readyButton.setVisibility(View.INVISIBLE);
            new Thread(HttpService::ready).start();
        });
        lookButton.setOnClickListener((v) -> {
            lookButton.setVisibility(View.INVISIBLE);
            new Thread(HttpService::lookCards).start();
            new Thread(() -> showCards(ConnectionHandler.nettyClient.clientHandler.cards)).start();
        });
        throwButton.setOnClickListener((v) -> {
            hideButtons();
            new Thread(HttpService::throwCards).start();
        });
        haveButton.setOnClickListener((v) -> {
            showHave();
        });
        card1ImageView.setImageBitmap(BitmapUtil.getBitmapByName(this, "back"));
        card2ImageView.setImageBitmap(BitmapUtil.getBitmapByName(this, "back"));
        card3ImageView.setImageBitmap(BitmapUtil.getBitmapByName(this, "back"));
        ((Toolbar)findViewById(R.id.id_toolbar)).setNavigationOnClickListener(v -> {
            finish();
            new Thread(HttpService::exitRoom).start();
        });
        ((TextView)findViewById(R.id.room_title_text)).setText(getString(R.string.room_title)+"："+ Const.roomTitle);
        ((TextView)findViewById(R.id.room_description_text)).setText(getString(R.string.room_description)+"："+ Const.roomDescription);
    }

    @Override
    protected void onDestroy() {
        INSTANCE = null;
        new Thread(HttpService::exitRoom).start();
        super.onDestroy();
    }

    public void showText(String msg) {
        runOnUiThread(() -> Toast.makeText(this, msg, Toast.LENGTH_SHORT).show());
    }

    public void showCards() {
        runOnUiThread(() -> card1ImageView.setVisibility(View.VISIBLE));
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        runOnUiThread(() -> card2ImageView.setVisibility(View.VISIBLE));
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        runOnUiThread(() -> card3ImageView.setVisibility(View.VISIBLE));
    }

    public void showCards(String cards) {
        String[] cs = cards.replace("[", "").replace("]", "").split(",");
        for(int i=0;i<3;i++) {
            String r = cs[i].replaceAll(" ", "")
                    .replaceAll("♠", "s")
                    .replaceAll("♥", "h")
                    .replaceAll("♣", "c")
                    .replaceAll("♦", "d")
                    .replaceAll("大王", "j2")
                    .replaceAll("小王", "j1")
                    .toLowerCase();
            int finalI = i;
            runOnUiThread(() -> {
                switch (finalI) {
                    case 0:
                        card1ImageView.setImageBitmap(BitmapUtil.getBitmapByName(this, r));
                        break;
                    case 1:
                        card2ImageView.setImageBitmap(BitmapUtil.getBitmapByName(this, r));
                        break;
                    case 2:
                        card3ImageView.setImageBitmap(BitmapUtil.getBitmapByName(this, r));
                        break;
                }
            });
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void showHave() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("请选择要牌类型");
        String[] items = generateHaveTypes(View.INVISIBLE==lookButton.getVisibility(), ConnectionHandler.nettyClient.clientHandler.playerCount==2, ConnectionHandler.nettyClient.clientHandler.currentMoney);
        builder.setSingleChoiceItems(items, -1, (dialog, which) -> {
            String item = items[which];
            if(item.contains("看")) {
                int money = Integer.parseInt(item.replaceAll("个看别人牌", ""));
                choosePlayer(ConnectionHandler.nettyClient.clientHandler.players, money);
            } else if (item.contains("开牌")) {
                int money = Integer.parseInt(item.replaceAll("个开牌", ""));
                new Thread(() -> HttpService.have(money, 1)).start();
            } else {
                int money = Integer.parseInt(item.replaceAll("个", ""));
                new Thread(() -> HttpService.have(money, 0)).start();
            }
            dialog.dismiss();
        });
        builder.show();
    }

    private String[] generateHaveTypes(boolean looked, boolean open, int baseMoney) {
        List<String> result = new ArrayList<>();
        result.add((looked?2:1)*baseMoney+"个");
        result.add((looked?2:1)*(baseMoney+2)+"个");
        result.add((looked?2:1)*baseMoney*2+"个");
        if(open) {
            result.add((looked?2:1)*baseMoney+"个开牌");
        }
        result.add((looked?2:1)*baseMoney*3+"个看别人牌");
        return result.toArray(new String[]{});
    }

    public void choosePlayer(JSONArray names, int money) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("请选择要看的玩家");
        List<String> is = new ArrayList<>();
        for(int i=0;i<names.size();i++) {
            if(!names.getString(i).equals(Const.user.getUsername()))
                is.add(names.getString(i));
        }
        String[] items = is.toArray(new String[]{});
        builder.setSingleChoiceItems(items, -1, (dialog, which) -> {
            String item = items[which];
            new Thread(() -> HttpService.have(money, 2, item)).start();
            lookButton.setEnabled(false);
            throwButton.setEnabled(false);
            haveButton.setEnabled(false);
            dialog.dismiss();
        });
        builder.show();
    }

    public void hideButtons() {
        runOnUiThread(() -> {
            playerListViewAdapter.setCurrentName(null);
            playerListViewAdapter.notifyDataSetChanged();
            //currentNameTextView.setText("当前发话:");
            totalMoneyTextView.setText("桌面上的豆子:");
            readyButton.setVisibility(View.VISIBLE);
            lookButton.setVisibility(View.INVISIBLE);
            throwButton.setVisibility(View.INVISIBLE);
            haveButton.setVisibility(View.INVISIBLE);
            card1ImageView.setVisibility(View.INVISIBLE);
            card2ImageView.setVisibility(View.INVISIBLE);
            card3ImageView.setVisibility(View.INVISIBLE);
            card1ImageView.setImageBitmap(BitmapUtil.getBitmapByName(this, "back"));
            card2ImageView.setImageBitmap(BitmapUtil.getBitmapByName(this, "back"));
            card3ImageView.setImageBitmap(BitmapUtil.getBitmapByName(this, "back"));
        });
    }

    public void resetUI() {
        hideButtons();
        runOnUiThread(() -> {
            playerListViewAdapter.setData(new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
            playerListViewAdapter.notifyDataSetChanged();
        });
    }

    public void startGame(String current, String totalMoney) {
        runOnUiThread(() -> {
            lookButton.setVisibility(View.VISIBLE);
            if (Const.user.getUsername().equals(current)) {
                throwButton.setVisibility(View.VISIBLE);
                haveButton.setVisibility(View.VISIBLE);
            }
            //currentNameTextView.setText("当前发话:" + current);
            totalMoneyTextView.setText("桌面上的豆子:" + totalMoney);
        });
    }

    public void gameState(String current, String totalMoney) {
        runOnUiThread(() -> {
            if (Const.user.getUsername().equals(current)) {
                throwButton.setVisibility(View.VISIBLE);
                haveButton.setVisibility(View.VISIBLE);
            } else {
                throwButton.setVisibility(View.INVISIBLE);
                haveButton.setVisibility(View.INVISIBLE);
            }
            //currentNameTextView.setText("当前发话:" + current);
            totalMoneyTextView.setText("桌面上的豆子:" + totalMoney);
        });
    }

    public void showMessage(String message, String title) {
        runOnUiThread(() -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(title);
            builder.setMessage(message);
            builder.setPositiveButton("确定", (dialog, which) -> { });
            builder.show();
        });
    }

    public void lookCardsMessage(String message, String title) {
        runOnUiThread(() ->
                new AlertDialog.Builder(this)
                        .setTitle(title)
                        .setMessage(message)
                        .setCancelable(false)
                        .setPositiveButton("是", (dialog, which) -> new Thread(HttpService::haveCardsAfterLook).start())
                        .setNegativeButton("否", (dialog, which) -> {
                            hideButtons();
                            new Thread(HttpService::throwCards).start();
                        })
                        .show()
        );
    }
}