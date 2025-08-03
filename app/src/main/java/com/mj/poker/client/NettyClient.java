package com.mj.poker.client;

import com.mj.poker.Const;

import java.net.URI;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshakerFactory;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;
import io.netty.util.concurrent.GenericFutureListener;

/**
 * Netty Java客户端
 *
 * @author lucher
 */
public class NettyClient extends Thread {

    // Server端IP地址
    public ClientHandler clientHandler = new ClientHandler();
    @Override
    public void run() {
        doConnect();
    }

    public boolean connect(String url){
        EventLoopGroup client = new NioEventLoopGroup();
        try{
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(client);
            bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
            bootstrap.option(ChannelOption.TCP_NODELAY, true);
            bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, Const.connectTimeout);
            bootstrap.channel(NioSocketChannel.class);
            final NettyClientHandler[] handler = {new NettyClientHandler(this)};
            bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel channel) {
                    ChannelPipeline pipeline = channel.pipeline();
                    pipeline.addLast(new HttpClientCodec(), new HttpObjectAggregator(2155380*10));
                    //pipeline.addLast("frameDecoder", new LengthFieldBasedFrameDecoder(2155380*10, 0, 4, 0, 4));
                    pipeline.addLast("handler", handler[0]);
                }
            });
            URI uri = new URI(url);
            System.out.println("connect: "+uri);
            ChannelFuture cf = bootstrap.connect(uri.getHost(), uri.getPort()).sync();
            cf.addListener((GenericFutureListener<ChannelFuture>) channelFuture -> {
                String log = String.format("连接websocket服务器: %s isSuccess=%s", url, channelFuture.isSuccess());
                System.out.println(log);
                System.out.println(channelFuture.isSuccess());
                if(channelFuture.isSuccess()){
                    //进行握手
                    Channel channel = channelFuture.channel();
                    handler[0] = (NettyClientHandler) channel.pipeline().get("handler");
                    WebSocketClientHandshaker handshaker =
                            WebSocketClientHandshakerFactory.newHandshaker(uri, WebSocketVersion.V13, null, true, new DefaultHttpHeaders(), 2155380 * 10);
                    handler[0].setHandshaker(handshaker);
                    handshaker.handshake(channel);
                    // 阻塞等待是否握手成功?
                    //handler[0].handshakeFuture().sync();
                    handler[0].handshakeFuture();
                }
            });
            cf.channel().closeFuture().sync();
            return !handler[0].exceptionCaught;
        } catch (Exception ex){
            ex.printStackTrace();
        } finally {
            client.shutdownGracefully();
        }
        return false;
    }


    public void doConnect() {
        do {
            long start = System.currentTimeMillis();
            boolean connected = connect(Const.wsAddr + Const.user.getUserId());
            long time = System.currentTimeMillis() - start;
            if (time < Const.connectTimeout) {
                try {
                    Thread.sleep(Const.connectTimeout - time);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    return;
                }
            }
        } while (true);
    }
}