package org.noear.solon.core.handle;

import org.noear.solon.core.SignalType;

public enum MethodType {
   GET("GET", SignalType.HTTP),
   POST("POST", SignalType.HTTP),
   PUT("PUT", SignalType.HTTP),
   DELETE("DELETE", SignalType.HTTP),
   PATCH("PATCH", SignalType.HTTP),
   HEAD("HEAD", SignalType.HTTP),
   OPTIONS("OPTIONS", SignalType.HTTP),
   TRACE("TRACE", SignalType.HTTP),
   CONNECT("CONNECT", SignalType.HTTP),
   HTTP("HTTP", SignalType.HTTP),
   WEBSOCKET("WEBSOCKET", SignalType.WEBSOCKET),
   SOCKET("SOCKET", SignalType.SOCKET),
   ALL("ALL", SignalType.ALL);

   public final String name;
   public final SignalType signal;

   private MethodType(String name, SignalType signal) {
      this.name = name;
      this.signal = signal;
   }
}
