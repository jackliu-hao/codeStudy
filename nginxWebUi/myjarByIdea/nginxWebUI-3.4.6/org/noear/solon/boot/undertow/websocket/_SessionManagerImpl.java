package org.noear.solon.boot.undertow.websocket;

import io.undertow.websockets.core.WebSocketChannel;
import java.util.Collection;
import java.util.Collections;
import org.noear.solon.core.SignalType;
import org.noear.solon.core.message.Session;
import org.noear.solon.socketd.SessionManager;

public class _SessionManagerImpl extends SessionManager {
   protected SignalType signalType() {
      return SignalType.WEBSOCKET;
   }

   public Session getSession(Object conn) {
      if (conn instanceof WebSocketChannel) {
         return _SocketServerSession.get((WebSocketChannel)conn);
      } else {
         throw new IllegalArgumentException("This conn requires a undertow WebSocketChannel type");
      }
   }

   public Collection<Session> getOpenSessions() {
      return Collections.unmodifiableCollection(_SocketServerSession.sessions.values());
   }

   public void removeSession(Object conn) {
      if (conn instanceof WebSocketChannel) {
         _SocketServerSession.remove((WebSocketChannel)conn);
      } else {
         throw new IllegalArgumentException("This conn requires a undertow WebSocketChannel type");
      }
   }
}
