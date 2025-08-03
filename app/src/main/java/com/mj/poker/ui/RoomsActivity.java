package com.mj.poker.ui;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.mj.poker.Const;
import com.mj.poker.service.ConnectionService;
import com.mj.poker.R;
import com.mj.poker.service.HttpService;
import com.mj.poker.ui.adapter.RoomListAdapter;
import com.mj.poker.util.DataUtil;

public class RoomsActivity extends AppCompatActivity {
    public static RoomsActivity INSTANCE;

    public TextView moneyTextView;
    public Button createRoomButton;
    public ProgressDialog progressDialog;
    public ListView roomList;
    public AlertDialog privacyDialog;

    public RoomListAdapter roomListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        INSTANCE = this;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(new Intent(this, ConnectionService.class));
        } else {
            startService(new Intent(this, ConnectionService.class));
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rooms);
        Toolbar toolbar = findViewById(R.id.id_toolbar);
        toolbar.setOnMenuItemClickListener(item -> {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return false;
        });
        roomList = findViewById(R.id.room_list);
        roomList.setAdapter(roomListAdapter = new RoomListAdapter(this, null));
        createRoomButton = findViewById(R.id.create_room_button);
        createRoomButton.setOnClickListener(v -> {
            startActivity(new Intent(this, CreateRoomActivity.class));
        });
        moneyTextView = findViewById(R.id.money_text_view);
        moneyTextView.setText(getString(R.string.money)+"："+ Const.user.getMoney());
        if (Const.agree)
            initialization();
        else
            showPrivacy();
    }

    private void initialization() {
        new Thread(() -> {
            String s = HttpService.getPcUrl();
            if (s == null)
                showText(R.string.cannot_connect_to_server);
            else {
                runOnUiThread(() -> ((TextView) findViewById(R.id.pcUrl)).setText(getString(R.string.downloadUrl) + s));

            }
        }).start();
    }

    public void showPrivacy() {
        final View inflate = LayoutInflater.from(this).inflate(R.layout.dialog_privacy_show, null);
        TextView tv_title = inflate.findViewById(R.id.tv_title);
        tv_title.setText(R.string.privacy_title);
        TextView tv_content = inflate.findViewById(R.id.tv_content);
        tv_content.setText(R.string.privacy);
        privacyDialog = new AlertDialog
                .Builder(this)
                .setView(inflate)
                .setOnDismissListener(dialog -> finish())
                .show();
        // 通过WindowManager获取
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        final WindowManager.LayoutParams params = privacyDialog.getWindow().getAttributes();
        params.width = dm.widthPixels*4/5;
        params.height = dm.heightPixels*3/5;
        privacyDialog.getWindow().setAttributes(params);
        privacyDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }

    public void onClickDisagree(View v) {
        finish();
    }

    public void onClickAgree(View v) {
        privacyDialog.setOnDismissListener(null);
        privacyDialog.dismiss();
        Const.agree = true;
        new Thread(DataUtil::save).start();
        initialization();
    }

    public void showText(String msg) {
        runOnUiThread(() -> Toast.makeText(this, msg, Toast.LENGTH_SHORT).show());
    }

    public void showText(@StringRes int id) {
        runOnUiThread(() -> Toast.makeText(this, id, Toast.LENGTH_SHORT).show());
    }

    public void setMoney() {
        runOnUiThread(() -> moneyTextView.setText(getString(R.string.money)+"："+ Const.user.getMoney()));
    }

    public void showMessage(String message, String title) {
        runOnUiThread(() -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(title);
            builder.setMessage(message);
            builder.setPositiveButton(R.string.btn_ok, (dialog, which) -> { });
            builder.show();
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            Intent i = new Intent(Intent.ACTION_MAIN);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.addCategory(Intent.CATEGORY_HOME);
            startActivity(i);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void startMainActivity() {
        startActivity(new Intent(this, MainActivity.class));
    }
}