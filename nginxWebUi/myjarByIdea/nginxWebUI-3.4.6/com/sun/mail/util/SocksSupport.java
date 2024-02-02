package com.sun.mail.util;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Socket;
import java.net.Proxy.Type;

class SocksSupport {
   public static Socket getSocket(String host, int port) {
      return host != null && host.length() != 0 ? new Socket(new Proxy(Type.SOCKS, new InetSocketAddress(host, port))) : new Socket(Proxy.NO_PROXY);
   }
}
