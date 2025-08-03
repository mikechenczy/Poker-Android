package com.mj.poker.client;
public class ConnectionHandler {
    public static NettyClient nettyClient;
    public static void connect() {
        nettyClient = new NettyClient();
        nettyClient.doConnect();
    }
}
