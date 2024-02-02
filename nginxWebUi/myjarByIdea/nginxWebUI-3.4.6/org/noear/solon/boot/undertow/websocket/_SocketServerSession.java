package org.noear.solon.boot.undertow.websocket;

import io.undertow.websockets.core.WebSocketChannel;
import io.undertow.websockets.core.WebSockets;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.core.handle.MethodType;
import org.noear.solon.core.message.Message;
import org.noear.solon.core.message.Session;
import org.noear.solon.socketd.ProtocolManager;
import org.noear.solon.socketd.SessionBase;

public class _SocketServerSession extends SessionBase {
   public static final Map<WebSocketChannel, Session> sessions = new HashMap();
   private final WebSocketChannel real;
   private final String _sessionId = Utils.guid();
   private URI _uri;
   private String _path;

   public static Session get(WebSocketChannel real) {
      Session tmp = (Session)sessions.get(real);
      if (tmp == null) {
         synchronized(real) {
            tmp = (Session)sessions.get(real);
            if (tmp == null) {
               tmp = new _SocketServerSession(real);
               sessions.put(real, tmp);
            }
         }
      }

      return (Session)tmp;
   }

   public static void remove(WebSocketChannel real) {
      sessions.remove(real);
   }

   public _SocketServerSession(WebSocketChannel real) {
      this.real = real;
   }

   public Object real() {
      return this.real;
   }

   public String sessionId() {
      return this._sessionId;
   }

   public MethodType method() {
      return MethodType.WEBSOCKET;
   }

   public URI uri() {
      if (this._uri == null) {
         this._uri = URI.create(this.real.getUrl());
      }

      return this._uri;
   }

   public String path() {
      if (this._path == null) {
         this._path = this.uri().getPath();
      }

      return this._path;
   }

   public void sendAsync(String message) {
      if (Solon.app().enableWebSocketD()) {
         ByteBuffer buf = ProtocolManager.encode(Message.wrap(message));
         WebSockets.sendBinary(buf, this.real, _CallbackImpl.instance);
      } else {
         WebSockets.sendText(message, this.real, _CallbackImpl.instance);
      }

   }

   public void sendAsync(Message message) {
      super.send(message);
      ByteBuffer buf;
      if (Solon.app().enableWebSocketD()) {
         buf = ProtocolManager.encode(message);
         WebSockets.sendBinary(buf, this.real, _CallbackImpl.instance);
      } else if (message.isString()) {
         WebSockets.sendText(message.bodyAsString(), this.real, _CallbackImpl.instance);
      } else {
         buf = ByteBuffer.wrap(message.body());
         WebSockets.sendBinary(buf, this.real, _CallbackImpl.instance);
      }

   }

   public void send(String message) {
      try {
         if (Solon.app().enableWebSocketD()) {
            ByteBuffer buf = ProtocolManager.encode(Message.wrap(message));
            WebSockets.sendBinaryBlocking(buf, this.real);
         } else {
            WebSockets.sendTextBlocking(message, this.real);
         }

      } catch (RuntimeException var3) {
         throw var3;
      } catch (Throwable var4) {
         throw new RuntimeException(var4);
      }
   }

   public void send(Message message) {
      super.send(message);

      try {
         ByteBuffer buf;
         if (Solon.app().enableWebSocketD()) {
            buf = ProtocolManager.encode(message);
            WebSockets.sendBinaryBlocking(buf, this.real);
         } else if (message.isString()) {
            WebSockets.sendTextBlocking(message.bodyAsString(), this.real);
         } else {
            buf = ByteBuffer.wrap(message.body());
            WebSockets.sendBinaryBlocking(buf, this.real);
         }

      } catch (RuntimeException var3) {
         throw var3;
      } catch (Throwable var4) {
         throw new RuntimeException(var4);
      }
   }

   public void close() throws IOException {
      if (this.real != null) {
         this.real.close();
         sessions.remove(this.real);
      }
   }

   public boolean isValid() {
      return this.real == null ? false : this.real.isOpen();
   }

   public boolean isSecure() {
      return this.real.isSecure();
   }

   public InetSocketAddress getRemoteAddress() {
      return this.real.getSourceAddress();
   }

   public InetSocketAddress getLocalAddress() {
      return this.real.getDestinationAddress();
   }

   public void setAttachment(Object obj) {
      this.real.setAttribute("attachment", obj);
   }

   public <T> T getAttachment() {
      return this.real.getAttribute("attachment");
   }

   public Collection<Session> getOpenSessions() {
      return Collections.unmodifiableCollection(sessions.values());
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         _SocketServerSession that = (_SocketServerSession)o;
         return Objects.equals(this.real, that.real);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.real});
   }
}
