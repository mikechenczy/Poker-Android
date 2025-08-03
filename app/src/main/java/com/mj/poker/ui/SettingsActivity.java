package com.mj.poker.ui;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;

import com.alibaba.fastjson.JSONObject;
import com.leon.lib.settingview.LSettingItem;
import com.mj.poker.Const;
import com.mj.poker.R;
import com.mj.poker.service.HttpService;
import com.mj.poker.util.DataUtil;
import com.mj.poker.util.UpdateManager;
import com.mj.poker.util.Utils;

import java.lang.reflect.Field;

public class SettingsActivity extends AppCompatActivity {
    public static SettingsActivity INSTANCE;
    public LSettingItem autoLoginSwift;
    //public LSettingItem choosePayType;
    public LSettingItem logout;
    public LSettingItem share;
    public LSettingItem changePassword;
    public LSettingItem checkVersion;
    public ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        INSTANCE = this;
        reload();
        ((Toolbar)findViewById(R.id.id_toolbar)).setNavigationOnClickListener(v -> finish());
        ((TextView) findViewById(R.id.versionText)).setText(getString(R.string.versionText) + Utils.getVersion(this));
        /*choosePayType = findViewById(R.id.choosePayType);
        choosePayType.setmOnLSettingItemClick(() -> {
            Intent intent = new Intent(MainActivity.INSTANCE, ChoosePayActivity.class);
            startActivity(intent);
        });*/
        logout = findViewById(R.id.logout);
        logout.setmOnLSettingItemClick(() -> new Thread(() -> {
            if (HttpService.logout()) {
                Const.autoLogin = false;
                DataUtil.save();
                RoomsActivity.INSTANCE.runOnUiThread(() -> {
                    Intent intent = new Intent(RoomsActivity.INSTANCE, LoginActivity.class);
                    startActivity(intent);
                    RoomsActivity.INSTANCE.finish();
                    RoomsActivity.INSTANCE = null;
                });
            } else {
                RoomsActivity.INSTANCE.showText(R.string.cannot_connect_to_server);
            }
        }).start());
        share = findViewById(R.id.share);
        share.setmOnLSettingItemClick(() -> shareText(this, getString(R.string.shareContent) +
                ((TextView) RoomsActivity.INSTANCE.findViewById(R.id.pcUrl)).getText().toString().replace(getString(R.string.downloadUrl), "")));
        changePassword = findViewById(R.id.changePassword);
        changePassword.setmOnLSettingItemClick(() -> {
            Intent intent = new Intent(RoomsActivity.INSTANCE, ForgetPasswordActivity.class);
            startActivity(intent);
        });
        checkVersion = findViewById(R.id.checkVersion);
        checkVersion.setmOnLSettingItemClick(() -> new Thread(() -> {
        /*if(!TcpService.getRealIp()) {
            runOnUiThread(() -> updateText.setText(R.string.can_not_connect_to_server));
            return;
        }*/
            JSONObject s = HttpService.needUpdate();
            if(s==null)
                runOnUiThread(() -> Toast.makeText(this, getString(R.string.cannot_connect_to_server), Toast.LENGTH_SHORT).show());
            else {
                runOnUiThread(() -> {
                    if (s.get("force")==null) {
                        new AlertDialog.Builder(this).setTitle(R.string.upToDate).setMessage(R.string.upToDate).setPositiveButton(R.string.admit, null).show();
                    } else {
                        new AlertDialog.Builder(this).setTitle(R.string.newVersion).setMessage(getString(R.string.version)+":" + s.getString("version") + "\n"+getString(R.string.content)+":" + s.getString("description") + "\n"+(s.getBoolean("force")?getString(R.string.forceUpdate):"")+getString(R.string.toUpdate))
                                .setPositiveButton(R.string.yes, (dialog, which) -> {
                                    progressDialog = new ProgressDialog(this);
                                    progressDialog.setTitle(R.string.downloadingUpdate);
                                    //设置水平进度条
                                    progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                                    //设置进度条最大值为100
                                    progressDialog.setMax(100);
                                    //设置进度条当前值为0
                                    progressDialog.setProgress(0);
                                    progressDialog.setCancelable(!s.getBoolean("force"));// 设置是否可以通过点击Back键取消
                                    progressDialog.setCanceledOnTouchOutside(false);
                                    progressDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.btn_cancel), (dialog1, which1) -> {
                                        if(s.getBoolean("force")) {
                                            RoomsActivity.INSTANCE.finish();
                                            System.exit(0);
                                        }
                                    });
                                    UpdateManager updateManager = new UpdateManager(this);
                                    updateManager.update();
                                    progressDialog.setOnDismissListener((dialog1 -> updateManager.downloadApkThread.interrupt()));
                                    progressDialog.show();
                                })
                                .setCancelable(!s.getBoolean("force"))
                                .setNegativeButton(R.string.no, (dialog, which) -> {
                                    if(s.getBoolean("force")) {
                                        RoomsActivity.INSTANCE.finish();
                                        System.exit(0);
                                    }
                                }).show();
                    }
                });
            }
        }).start());
        autoLoginSwift = findViewById(R.id.autoLoginSwift);
        if (Const.autoLogin)
            autoLoginSwift.clickOn();
        try {
            Field switchItem = autoLoginSwift.getClass().getDeclaredField("mRightIcon_switch");
            switchItem.setAccessible(true);
            SwitchCompat switchCompat = (SwitchCompat) switchItem.get(autoLoginSwift);
            switchCompat.setOnClickListener(v -> {
                Const.autoLogin = !Const.autoLogin;
                new Thread(DataUtil::save).start();
            });
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        autoLoginSwift.setmOnLSettingItemClick(() -> {
            Const.autoLogin = !Const.autoLogin;
            new Thread(DataUtil::save).start();
        });
    }

    public void clickOn(LSettingItem settingItem) {
        try {
            Field switchItem = autoLoginSwift.getClass().getDeclaredField("mRightStyle");
            switchItem.setAccessible(true);
            int mRightStyle = (int) switchItem.get(settingItem);
            switchItem = autoLoginSwift.getClass().getDeclaredField("mRightIcon_check");
            switchItem.setAccessible(true);
            AppCompatCheckBox mRightIcon_check = (AppCompatCheckBox) switchItem.get(settingItem);
            switchItem = autoLoginSwift.getClass().getDeclaredField("mRightIcon_switch");
            switchItem.setAccessible(true);
            SwitchCompat mRightIcon_switch = (SwitchCompat) switchItem.get(settingItem);
            switch (mRightStyle) {
                case 2:
                    //选择框切换选中状态
                    mRightIcon_check.setChecked(!mRightIcon_check.isChecked());
                    break;
                case 3:
                    //开关切换状态
                    mRightIcon_switch.setChecked(!mRightIcon_switch.isChecked());
                    break;
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    public void reload() {
        ((TextView)findViewById(R.id.usernameText)).setText(Const.user.getUsername());
        //+"\n"+getString(R.string.vipTime)+(Utils.isVip()?(Locale.getDefault().getLanguage().contains("zh")?new SimpleDateFormat("yyyy年MM月dd日HH时mm分ss秒").format(new Date(Define.user.getVip())):new SimpleDateFormat("yyyy:MM:dd:HH:mm:ss").format(new Date(Define.user.getVip()))):getString(R.string.overTime)));
    }

    public void shareText(Context context, String text) {
        if(context == null || text == null)
            return;
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, text);
        context.startActivity(Intent.createChooser(intent, getString(R.string.shareTitle)));
    }
}
