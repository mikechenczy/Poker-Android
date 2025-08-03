package com.mj.poker.service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import androidx.annotation.RequiresApi;

import com.mj.poker.R;

public class ConnectionService extends Service {
    private ConnectionHandler handler;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        handler = new ConnectionHandler();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, final int startId) {
        /*int type = intent.getIntExtra("type",1);
        if(type == 1){
            createNotificationChannel();
        }else{
            createErrorNotification();
        }*/
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel();
        } else {
            createNotificationChannelOldSdk();
        }
        new Thread(() -> {
            com.mj.poker.client.ConnectionHandler.connect();
            handler.sendEmptyMessage(startId);
        }).start();
        return super.onStartCommand(intent, flags, startId);
    }

    private void createErrorNotification() {
        Notification notification = new Notification.Builder(this).build();
        startForeground(0, notification);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createNotificationChannel() {
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // 通知渠道的id
        String id = "connect_server_channel";
        // 用户可以看到的通知渠道的名字.
        CharSequence name = getString(R.string.app_name);
//         用户可以看到的通知渠道的描述
        String description = getString(R.string.app_name);
        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel mChannel = new NotificationChannel(id, name, importance);
//         配置通知渠道的属性
        mChannel.setDescription(description);
//         设置通知出现时的闪灯（如果 android 设备支持的话）
        mChannel.enableLights(true); mChannel.setLightColor(Color.RED);
//         设置通知出现时的震动（如果 android 设备支持的话）
        mChannel.enableVibration(true);
        mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
//         最后在notificationmanager中创建该通知渠道 //
        mNotificationManager.createNotificationChannel(mChannel);
        // 通知渠道的id
        // Create a notification and set the notification channel.
        Notification notification = new Notification.Builder(this)
                .setContentTitle(getString(R.string.toast_services_start_title)) .setContentText(getString(R.string.toast_services_start_content))
                .setSmallIcon(R.drawable.ic_launcher)
                .setChannelId(id)
                .build();
        System.out.println("START");
        startForeground(1, notification);
    }

    private void createNotificationChannelOldSdk() {
        Notification notification = new Notification.Builder(this)
                .setContentTitle(getString(R.string.toast_services_start_title)) .setContentText(getString(R.string.toast_services_start_content))
                .setSmallIcon(R.drawable.ic_launcher)
                .build();
        startForeground(1, notification);
    }

    private class ConnectionHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            stopSelf(msg.what);
        }
    }

    @SuppressLint("WrongConstant")
    @Override
    public void onDestroy() {
        super.onDestroy();
        //Log.d(TAG, "5s onDestroy");
        //Toast.makeText(this, "this service destroy", 1).show();
        stopForeground(true);
    }
}