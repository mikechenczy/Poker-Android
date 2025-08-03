package com.mj.poker.ui;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson.JSONObject;
import com.mj.poker.Const;
import com.mj.poker.R;
import com.mj.poker.model.User;
import com.mj.poker.service.HttpService;
import com.mj.poker.util.DataUtil;
import com.mj.poker.util.UpdateManager;
import com.mj.poker.util.Utils;

public class LoginActivity extends AppCompatActivity {
    public static LoginActivity INSTANCE;
    private EditText usernameText;
    private EditText passwordText;
    private Button loginButton;
    private Button registerButton;
    public Button forgetButton;
    private CheckBox checkbox;
    private TextView errorMessage;
    public ProgressDialog progressDialog;
    public boolean checkedUpdate;
    public boolean readMessage;
    public AlertDialog messageDialog;
    public String messageContent;

    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        INSTANCE = this;
        //Utils.verifyStoragePermissions(this); //获取储存权限
        DataUtil.sharedPreferences = getSharedPreferences("yan", MODE_PRIVATE);
        Const.version = Utils.getVersion(this);
        ((TextView) findViewById(R.id.versionText)).setText(getString(R.string.versionText) + Utils.getVersion(this));
        usernameText = findViewById(R.id.usernameText);
        passwordText = findViewById(R.id.passwordText);
        checkbox = findViewById(R.id.checkBox);
        errorMessage = findViewById(R.id.errnoMessage);
        loginButton = findViewById(R.id.loginButton);
        if(!Const.autoLogin)
            checkbox.setChecked(false);
        loginButton.setOnClickListener(v -> login());
        registerButton = findViewById(R.id.registerButton);
        registerButton.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
        forgetButton = findViewById(R.id.forgetButton);
        forgetButton.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, ForgetPasswordActivity.class);
            startActivity(intent);
        });
        if(!Utils.stringIsEmpty(Const.autoLoginUsername))
            usernameText.setText(Const.autoLoginUsername);
        if(!Utils.stringIsEmpty(Const.autoLoginUsername) && Const.autoLogin)
            passwordText.setText(Const.autoLoginPassword);
        new UpdateThread().start();
    }

    public void showMessage(Context context) {
        final View inflate = LayoutInflater.from(context).inflate(R.layout.dialog_message, null);
        TextView tv_title = inflate.findViewById(R.id.tv_title);
        tv_title.setText(R.string.message);
        TextView tv_content = inflate.findViewById(R.id.tv_content);
        tv_content.setText(messageContent);
        messageDialog = new AlertDialog
                .Builder(context)
                .setView(inflate)
                .show();
        // 通过WindowManager获取
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        final WindowManager.LayoutParams params = messageDialog.getWindow().getAttributes();
        params.width = dm.widthPixels*4/5;
        params.height = dm.heightPixels*3/5;
        messageDialog.getWindow().setAttributes(params);
        messageDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }

    public void onClickNeverShow(View view) {
        Const.neverShowMessageDialog = messageContent;
        new Thread(DataUtil::save).start();
        onClickAdmit(view);
    }

    public void onClickAdmit(View view) {
        messageDialog.dismiss();
        if(Const.autoLogin)
            login();
    }

    public class MessageThread extends Thread {
        @Override
        public void run() {
            messageContent = HttpService.getMessageContent();
            if(messageContent==null) {
                Toast.makeText(LoginActivity.this, R.string.cannot_connect_to_server, Toast.LENGTH_SHORT).show();
            } else {
                readMessage = true;
                if (!(messageContent.equals(Const.neverShowMessageDialog) || messageContent.equals("No message")))
                    runOnUiThread(() -> showMessage(LoginActivity.this));
                else
                    runOnUiThread(() -> {
                        if(Const.autoLogin)
                            login();
                    });
            }
        }
    }

    public class UpdateThread extends Thread {
        @Override
        public void run() {
               /*if(!TcpService.getRealIp()) {
                runOnUiThread(() -> updateText.setText(R.string.can_not_connect_to_server));
                return;
            }*/
            runOnUiThread(() -> {
                loginButton.setEnabled(false);
                loginButton.setBackgroundColor(getResources().getColor(R.color.darkSlateGray));
                loginButton.setText(getString(R.string.loggingIn));
            });
            JSONObject s = HttpService.needUpdate();
            if(s==null) {
                runOnUiThread(() -> {
                    loginButton.setBackgroundColor(getResources().getColor(R.color.darkCyan));
                    loginButton.setEnabled(true);
                    loginButton.setText(R.string.login);
                    showErrorMessage(getResources().getText(R.string.cannot_connect_to_server).toString());
                });
                return;
            }
            runOnUiThread(() -> {
                if (s.get("force")==null || (!s.getBoolean("force") && s.getString("version").equals(Const.neverShowVersionDialog) && !Utils.stringIsEmpty(Const.autoLoginUsername) && Const.autoLogin)) {
                    checkedUpdate = true;
                    loginButton.setBackgroundColor(getResources().getColor(R.color.darkCyan));
                    loginButton.setEnabled(true);
                    loginButton.setText(R.string.login);
                    if(Const.autoLogin)
                        login();
                } else {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(LoginActivity.this).setTitle(R.string.newVersion).setMessage(getString(R.string.version) + ":" + s.getString("version") + "\n" + getString(R.string.content) + ":" + s.getString("description") + "\n" + (s.getBoolean("force") ? getString(R.string.forceUpdate) : "") + getString(R.string.toUpdate))
                            .setPositiveButton(R.string.yes, (dialog, which) -> {
                                progressDialog = new ProgressDialog(LoginActivity.this);
                                progressDialog.setTitle(R.string.downloadingUpdate);
                                //设置水平进度条
                                progressDialog.setProgressStyle(progressDialog.STYLE_HORIZONTAL);
                                //设置进度条最大值为100
                                progressDialog.setMax(100);
                                //设置进度条当前值为0
                                progressDialog.setProgress(0);
                                progressDialog.setCancelable(!s.getBoolean("force"));// 设置是否可以通过点击Back键取消
                                progressDialog.setCanceledOnTouchOutside(false);
                                progressDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.btn_cancel), (dialog1, which1) -> {
                                    if (s.getBoolean("force")) {
                                        System.exit(0);
                                    }
                                });
                                UpdateManager updateManager = new UpdateManager(LoginActivity.this);
                                updateManager.update();
                                progressDialog.setOnDismissListener((dialog1 -> {
                                    if (s.getBoolean("force")) {
                                        System.exit(0);
                                    }
                                    updateManager.downloadApkThread.interrupt();
                                    checkedUpdate = true;
                                    loginButton.setBackgroundColor(getResources().getColor(R.color.darkCyan));
                                    loginButton.setEnabled(true);
                                    loginButton.setText(R.string.login);
                                    if (!Utils.stringIsEmpty(Const.autoLoginUsername) && Const.autoLogin) {
                                        passwordText.setText(Const.autoLoginPassword);
                                        login();
                                    }
                                }));
                                progressDialog.show();
                            })
                            .setNegativeButton(R.string.no, ((dialog, which) -> {
                                if (s.getBoolean("force")) {
                                    System.exit(0);
                                }
                                checkedUpdate = true;
                                loginButton.setBackgroundColor(getResources().getColor(R.color.darkCyan));
                                loginButton.setEnabled(true);
                                loginButton.setText(R.string.login);
                                if (!Utils.stringIsEmpty(Const.autoLoginUsername) && Const.autoLogin) {
                                    passwordText.setText(Const.autoLoginPassword);
                                    login();
                                }
                            }));
                    if (!s.getBoolean("force")) {
                        alertDialogBuilder.setNeutralButton(R.string.neverShowVersionDialog, ((dialog, which) -> {
                            checkedUpdate = true;
                            loginButton.setBackgroundColor(getResources().getColor(R.color.darkCyan));
                            loginButton.setEnabled(true);
                            loginButton.setText(R.string.login);
                            new Thread(() -> {
                                Const.neverShowVersionDialog = s.getString("version");
                                DataUtil.save();
                            }).start();
                            if (!Utils.stringIsEmpty(Const.autoLoginUsername) && Const.autoLogin) {
                                passwordText.setText(Const.autoLoginPassword);
                                login();
                            }
                        }));
                    } else {
                        alertDialogBuilder.setCancelable(false);
                    }
                    alertDialogBuilder.show();
                }
            });
        }
    }

    public void login() {
        if(loginButton.isEnabled()) {
            loginButton.setEnabled(false);
            loginButton.setBackgroundColor(getResources().getColor(R.color.darkSlateGray));
            if(!checkedUpdate) {
                new UpdateThread().start();
                loginButton.setEnabled(true);
                loginButton.setBackgroundColor(getResources().getColor(R.color.darkCyan));
                return;
            }
            if(!readMessage) {
                new MessageThread().start();
                loginButton.setEnabled(true);
                loginButton.setBackgroundColor(getResources().getColor(R.color.darkCyan));
                return;
            }
            loginButton.setBackgroundColor(getResources().getColor(R.color.darkSlateGray));
            loginButton.setText(getString(R.string.loggingIn));
            String username = usernameText.getText().toString();
            String password = passwordText.getText().toString();
            if(Utils.check(username,password)) {
                new Thread(() -> {
                    runOnUiThread(() -> errorMessage.setVisibility(View.INVISIBLE));
                    User user = HttpService.login(username, password);
                    if (user == null)
                        LoginActivity.this.runOnUiThread(() -> showErrorMessage(getResources().getText(R.string.cannot_connect_to_server).toString()));
                    else if (Utils.stringIsEmpty(user.getUsername()))
                        LoginActivity.this.runOnUiThread(() -> showErrorMessage(getString(user.getUserId() == 1?R.string.usernameOrPasswordWrongInFormat:R.string.usernameOrPasswordWrong)));
                    else {
                        Const.user = user;
                        Const.autoLogin = checkbox.isChecked();
                        new Thread(DataUtil::save).start();
                        Intent intent = new Intent(LoginActivity.this, RoomsActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    LoginActivity.this.runOnUiThread(() -> {
                        loginButton.setBackgroundColor(getResources().getColor(R.color.darkCyan));
                        loginButton.setEnabled(true);
                        loginButton.setText(getString(R.string.login));
                    });
                }).start();
            } else {
                showErrorMessage(getString(R.string.usernameOrPasswordWrongInFormat));
                loginButton.setBackgroundColor(getResources().getColor(R.color.darkCyan));
                loginButton.setEnabled(true);
                loginButton.setText(R.string.login);
            }
        }
    }

    public void showErrorMessage(String message){
        errorMessage.setText(message);
        errorMessage.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
}
