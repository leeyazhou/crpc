/**
 * 
 */
package com.github.leeyazhou.crpc.transport.netty;

import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;
import com.github.leeyazhou.crpc.core.Constants;
import com.github.leeyazhou.crpc.core.URL;
import com.github.leeyazhou.crpc.core.concurrent.NamedThreadFactory;
import com.github.leeyazhou.crpc.protocol.message.RequestMessage;
import com.github.leeyazhou.crpc.protocol.message.ResponseMessage;
import com.github.leeyazhou.crpc.protocol.netty.NettyProtocolDecoder;
import com.github.leeyazhou.crpc.protocol.netty.NettyProtocolEncoder;
import com.github.leeyazhou.crpc.transport.ChannelManager;
import com.github.leeyazhou.crpc.transport.netty.handler.NettyClientHandler;
import com.github.leeyazhou.crpc.transport.netty.handler.NettyClientHeartBeatHandler;
import com.github.leeyazhou.crpc.transport.service.InternalEchoService;
import com.github.leeyazhou.crpc.transport.service.impl.InternalEchoServiceImpl;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * @author leeyazhou
 *
 */
public class NettyClientTest extends NettyServerTest {
  private static final EventLoopGroup bussinessGroup = new NioEventLoopGroup(0, new NamedThreadFactory("crpc-client"));

  @Test
  public void testSendRequest() {
    final URL url = URL.valueOf("crpc://127.0.0.1:25001").addParameter(Constants.SERVICE_INTERFACE,
        InternalEchoService.class.getName());
    final ChannelManager channelManager = new ChannelManager();
    Bootstrap bootStrap = new Bootstrap();

    final NettyClient client = new NettyClient(bootStrap, url);
    int connectTimeout = url.getParameter(Constants.REQUEST_TIMEOUT, 3000);

    bootStrap.group(bussinessGroup).channel(NioSocketChannel.class);
    bootStrap.option(ChannelOption.TCP_NODELAY, true);
    bootStrap.option(ChannelOption.SO_REUSEADDR, true);
    bootStrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectTimeout < 1000 ? 1000 : connectTimeout);
    bootStrap.handler(new ChannelInitializer<SocketChannel>() {
      @Override
      public void initChannel(SocketChannel channel) throws Exception {
        channel.pipeline().addLast("decoder", new NettyProtocolDecoder());
        channel.pipeline().addLast("encoder", new NettyProtocolEncoder());
        channel.pipeline().addLast("heartbeat", new NettyClientHeartBeatHandler(client, 0, 0, 3, TimeUnit.SECONDS));
        channel.pipeline().addLast("handler", new NettyClientHandler(url, client, channelManager));
      }
    });

    client.connect();

    RequestMessage message =
        new RequestMessage().setTargetClassName(InternalEchoServiceImpl.class.getName()).setMethodName("echo")
            .setArgs(new Object[] {"PING"}).setArgTypes(new String[] {String.class.getName()}).setTimeout(3000);

    ResponseMessage responseMessage = client.request(message);
    Assert.assertEquals("PING", responseMessage.getResponse());
  }

}