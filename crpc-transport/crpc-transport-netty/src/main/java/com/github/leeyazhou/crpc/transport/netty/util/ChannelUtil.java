/**
 * 
 */
package com.github.leeyazhou.crpc.transport.netty.util;

import java.net.InetSocketAddress;
import com.github.leeyazhou.crpc.core.URL;
import io.netty.channel.ChannelHandlerContext;

/**
 * @author leeyazhou
 *
 */
public class ChannelUtil {

  public static URL toUrl(ChannelHandlerContext ctx) {
    InetSocketAddress ipSocket = (InetSocketAddress) ctx.channel().remoteAddress();
    URL url = new URL("crpc", ipSocket.getHostString(), ipSocket.getPort());
    return url;
  }
}
