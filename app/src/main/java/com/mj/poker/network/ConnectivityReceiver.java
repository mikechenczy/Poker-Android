package com.mj.poker.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import java.io.IOException;

public class ConnectivityReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        if(info == null){
            if(Client.readThread!=null) {
                Client.readThread.interrupt();
                try {
                    if (Client.dis != null)
                        Client.dis.close();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
                try {
                    if (Client.dos != null)
                        Client.dos.close();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
                try {
                    if (Client.socket != null) {
                        Client.socket.shutdownInput();
                        Client.socket.shutdownOutput();
                        Client.socket.close();
                    }
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
                new Thread(Client::connect).start();
            }
            Toast.makeText(context, "no network", Toast.LENGTH_SHORT).show();
        }else{
            switch (info.getType()){
                case ConnectivityManager.TYPE_WIFI:
                    if(Client.readThread!=null) {
                        Client.readThread.interrupt();
                        try {
                            if (Client.dis != null)
                                Client.dis.close();
                        } catch (IOException ioException) {
                            ioException.printStackTrace();
                        }
                        try {
                            if (Client.dos != null)
                                Client.dos.close();
                        } catch (IOException ioException) {
                            ioException.printStackTrace();
                        }
                        try {
                            if (Client.socket != null) {
                                Client.socket.shutdownInput();
                                Client.socket.shutdownOutput();
                                Client.socket.close();
                            }
                        } catch (IOException ioException) {
                            ioException.printStackTrace();
                        }
                        new Thread(Client::connect).start();
                    }
                    Toast.makeText(context, "wifi", Toast.LENGTH_SHORT).show();
                    break;
                case ConnectivityManager.TYPE_MOBILE:
                    if(Client.readThread!=null) {
                        Client.readThread.interrupt();
                        try {
                            if (Client.dis != null)
                                Client.dis.close();
                        } catch (IOException ioException) {
                            ioException.printStackTrace();
                        }
                        try {
                            if (Client.dos != null)
                                Client.dos.close();
                        } catch (IOException ioException) {
                            ioException.printStackTrace();
                        }
                        try {
                            if (Client.socket != null) {
                                Client.socket.shutdownInput();
                                Client.socket.shutdownOutput();
                                Client.socket.close();
                            }
                        } catch (IOException ioException) {
                            ioException.printStackTrace();
                        }
                        new Thread(Client::connect).start();
                    }
                    Toast.makeText(context, "mobile", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }
}