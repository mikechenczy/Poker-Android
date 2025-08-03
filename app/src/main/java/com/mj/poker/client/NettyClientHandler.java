package com.mj.poker.client;

import android.content.Intent;
import android.os.Build;

import com.mj.poker.Const;
import com.mj.poker.R;
import com.mj.poker.service.ConnectionService;
import com.mj.poker.ui.MainActivity;
import com.mj.poker.ui.RoomsActivity;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshaker;

public class NettyClientHandler extends SimpleChannelInboundHandler<Object> {
    private WebSocketClientHandshaker handshaker = null;
    private ChannelPromise handshakeFuture = null;
    public NettyClient webSocket;
    public boolean exceptionCaught;

    public NettyClientHandler(NettyClient webSocket) {
        this.webSocket = webSocket;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        webSocket.clientHandler.setCtx(ctx);
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        this.handshakeFuture = ctx.newPromise();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) {
        /*if(msg instanceof FullHttpResponse) {
            FullHttpResponse response = (FullHttpResponse) msg;
            int statusCode = response.status().code();
            if (statusCode >= 300 && statusCode < 400) {
                String location = response.headers().get(HttpHeaderNames.LOCATION);
                if (location != null) {
                    if (location.startsWith("http://")) {
                        webSocket.tempRedirectUrl = "ws://" + location.substring(7);  // http://example.com -> ws://example.com
                    } else if (location.startsWith("https://")) {
                        webSocket.tempRedirectUrl = "wss://" + location.substring(8); // https://example.com -> wss://example.com
                    } else {
                        webSocket.tempRedirectUrl = location;
                    }
                    System.out.println("Redirecting to: " + webSocket.tempRedirectUrl);
                    ctx.close();
                } else {
                    System.err.println("Received redirect but no Location header");
                }
                return;
            }
        }*/
        //System.out.println("WebSocketClientHandler::channelRead0: ");
        // 握手协议返回，设置结束握手
        if (!this.handshaker.isHandshakeComplete()){
            FullHttpResponse response = (FullHttpResponse)msg;
            this.handshaker.finishHandshake(ctx.channel(), response);
            this.handshakeFuture.setSuccess();
            //System.out.println("WebSocketClientHandler::channelRead0 HandshakeComplete...");
            return;
        }

        if (msg instanceof TextWebSocketFrame) {
            //System.out.println(msg);
            TextWebSocketFrame textFrame = (TextWebSocketFrame)msg;
            //System.out.println("WebSocketClientHandler::channelRead0 textFrame: " + textFrame.text());
            webSocket.clientHandler.handleMessage(textFrame.text());
        }

        if (msg instanceof CloseWebSocketFrame){
            //System.out.println("WebSocketClientHandler::channelRead0 CloseWebSocketFrame");
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        System.out.println("Inactive");
        System.out.println("Client channelInactive:" + ctx);
        if(MainActivity.INSTANCE!=null) {
            MainActivity.INSTANCE.finish();
            MainActivity.INSTANCE = null;
            Const.roomId = 0;
            RoomsActivity.INSTANCE.showText(R.string.cannot_connect_to_server);
        }
        RoomsActivity.INSTANCE.runOnUiThread(() -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                RoomsActivity.INSTANCE.startForegroundService(new Intent(RoomsActivity.INSTANCE, ConnectionService.class));
            } else {
                RoomsActivity.INSTANCE.startService(new Intent(RoomsActivity.INSTANCE, ConnectionService.class));
            }
        });
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        exceptionCaught = true;
        System.out.println("WebSocketClientHandler::exceptionCaught");
        cause.printStackTrace();
        ctx.channel().close();
    }

    public void setHandshaker(WebSocketClientHandshaker handshaker) {
        this.handshaker = handshaker;
    }

    public ChannelFuture handshakeFuture() {
        return this.handshakeFuture;
    }

    public ChannelPromise getHandshakeFuture() {
        return handshakeFuture;
    }

    public void setHandshakeFuture(ChannelPromise handshakeFuture) {
        this.handshakeFuture = handshakeFuture;
    }

}