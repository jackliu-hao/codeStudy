package org.noear.solon.core.message;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.Collection;
import java.util.Map;
import java.util.function.BiConsumer;
import org.noear.solon.core.NvMap;
import org.noear.solon.core.handle.MethodType;
import org.noear.solon.core.util.PathUtil;

public interface Session {
   Object real();

   String sessionId();

   MethodType method();

   URI uri();

   String path();

   void pathNew(String pathNew);

   String pathNew();

   default NvMap pathMap(String expr) {
      return PathUtil.pathVarMap(this.pathNew(), expr);
   }

   String header(String name);

   void headerSet(String name, String value);

   NvMap headerMap();

   String param(String name);

   void paramSet(String name, String value);

   NvMap paramMap();

   default Object attr(String name) {
      return this.attrMap().get(name);
   }

   default void attrSet(String name, Object value) {
      this.attrMap().put(name, value);
   }

   Map<String, Object> attrMap();

   int flag();

   void flagSet(int flag);

   void sendAsync(String message);

   void sendAsync(Message message);

   void send(String message);

   void send(Message message);

   String sendAndResponse(String message);

   String sendAndResponse(String message, int timeout);

   Message sendAndResponse(Message message);

   Message sendAndResponse(Message message, int timeout);

   void sendAndCallback(String message, BiConsumer<String, Throwable> callback);

   void sendAndCallback(Message message, BiConsumer<Message, Throwable> callback);

   default void listener(Listener listener) {
   }

   default Listener listener() {
      return null;
   }

   void close() throws IOException;

   boolean isValid();

   boolean isSecure();

   void setHandshaked(boolean handshaked);

   boolean getHandshaked();

   InetSocketAddress getRemoteAddress();

   InetSocketAddress getLocalAddress();

   void setAttachment(Object obj);

   <T> T getAttachment();

   Collection<Session> getOpenSessions();

   void sendHeartbeat();

   void sendHeartbeatAuto(int intervalSeconds);

   void sendHandshake(Message message);

   Message sendHandshakeAndResponse(Message message);
}
