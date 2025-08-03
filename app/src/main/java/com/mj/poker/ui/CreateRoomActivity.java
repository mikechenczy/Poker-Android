package com.mj.poker.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.alibaba.fastjson.JSONObject;
import com.mj.poker.Const;
import com.mj.poker.R;
import com.mj.poker.client.ClientHandler;
import com.mj.poker.client.ConnectionHandler;
import com.mj.poker.service.HttpService;

import io.netty.util.internal.StringUtil;

public class CreateRoomActivity extends AppCompatActivity {
    public static CreateRoomActivity INSTANCE;

    public EditText nameText;
    public EditText descriptionText;
    public EditText passwordText;
    public Button commitButton;
    public Button cancelButton;
    public TextView errorMessage;

    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        INSTANCE = this;
        setContentView(R.layout.activity_create_room);
        ((Toolbar)findViewById(R.id.id_toolbar)).setNavigationOnClickListener(v -> finish());
        cancelButton = findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(v -> finish());
        commitButton = findViewById(R.id.commitButton);
        errorMessage = findViewById(R.id.errnoMessage);
        nameText = findViewById(R.id.nameText);
        descriptionText = findViewById(R.id.descriptionText);
        passwordText = findViewById(R.id.passwordText);
        commitButton.setOnClickListener(v -> {
            if(commitButton.isEnabled()) {
                commitButton.setEnabled(false);
                errorMessage.setVisibility(View.INVISIBLE);
                if(StringUtil.isNullOrEmpty(nameText.getText().toString()) || StringUtil.isNullOrEmpty(descriptionText.getText().toString())) {
                    showErrorMessage(getString(R.string.enter_properly));
                    commitButton.setEnabled(true);
                    return;
                }
                new Thread(() -> {
                    String password = passwordText.getText().toString();
                    ConnectionHandler.nettyClient.clientHandler.createRoom = true;
                    JSONObject object = HttpService.createRoom(nameText.getText().toString(), descriptionText.getText().toString(), password);
                    switch(object.getInteger("errno")) {
                        case 0:
                            Const.roomTitle = nameText.getText().toString();
                            Const.roomDescription = descriptionText.getText().toString();
                            Const.roomId = object.getInteger("roomId");
                            startMainActivity();
                            new Thread(() -> HttpService.enterRoom(Const.roomId, password)).start();
                            break;
                        case -1:
                            runOnUiThread(() -> {
                                Toast.makeText(this, R.string.cannot_connect_to_server, Toast.LENGTH_SHORT).show();
                                commitButton.setEnabled(true);
                            });
                            break;
                        default:
                            runOnUiThread(() -> {
                                Toast.makeText(this, object.getString("errMsg"), Toast.LENGTH_SHORT).show();
                                commitButton.setEnabled(true);
                            });
                            break;
                    }
                }).start();
            }
        });
    }

    private void startMainActivity() {
        runOnUiThread(() -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });
    }

    public void showErrorMessage(String message){
        errorMessage.setText(message);
        errorMessage.setVisibility(View.VISIBLE);
    }

}
