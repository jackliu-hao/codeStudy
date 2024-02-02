package org.noear.solon.boot.undertow.websocket;

import io.undertow.websockets.core.AbstractReceiveListener;
import io.undertow.websockets.core.BufferedBinaryMessage;
import io.undertow.websockets.core.BufferedTextMessage;
import io.undertow.websockets.core.StreamSourceFrameChannel;
import io.undertow.websockets.core.WebSocketChannel;
import io.undertow.websockets.core.WebSockets;
import io.undertow.websockets.spi.WebSocketHttpExchange;
import java.io.IOException;
import java.nio.ByteBuffer;
import org.noear.solon.Solon;
import org.noear.solon.core.event.EventBus;
import org.noear.solon.core.message.Message;
import org.noear.solon.core.message.Session;
import org.noear.solon.socketd.ProtocolManager;
import org.xnio.Pooled;

public class UtWsChannelListener extends AbstractReceiveListener {
   public void onOpen(WebSocketHttpExchange exchange, WebSocketChannel channel) {
      Session session = _SocketServerSession.get(channel);
      exchange.getRequestHeaders().forEach((k, v) -> {
         if (v.size() > 0) {
            session.headerSet(k, (String)v.get(0));
         }

      });
      Solon.app().listener().onOpen(session);
   }

   protected void onFullBinaryMessage(WebSocketChannel channel, BufferedBinaryMessage msg) throws IOException {
      try {
         Pooled<ByteBuffer[]> pulledData = msg.getData();

         try {
            ByteBuffer[] resource = (ByteBuffer[])pulledData.getResource();
            ByteBuffer byteBuffer = WebSockets.mergeBuffers(resource);
            Session session = _SocketServerSession.get(channel);
            Message message = null;
            if (Solon.app().enableWebSocketD()) {
               message = ProtocolManager.decode(byteBuffer);
            } else {
               message = Message.wrap(channel.getUrl(), (String)null, (byte[])byteBuffer.array());
            }

            Solon.app().listener().onMessage(session, message);
         } finally {
            pulledData.discard();
         }
      } catch (Throwable var12) {
         EventBus.push(var12);
      }

   }

   protected void onFullTextMessage(WebSocketChannel channel, BufferedTextMessage msg) throws IOException {
      try {
         Session session = _SocketServerSession.get(channel);
         Message message = Message.wrap(channel.getUrl(), (String)null, (String)msg.getData());
         Solon.app().listener().onMessage(session, message.isString(true));
      } catch (Throwable var5) {
         EventBus.push(var5);
      }

   }

   protected void onClose(WebSocketChannel channel, StreamSourceFrameChannel frameChannel) throws IOException {
      Solon.app().listener().onClose(_SocketServerSession.get(channel));
      _SocketServerSession.remove(channel);
   }

   protected void onError(WebSocketChannel channel, Throwable error) {
      Solon.app().listener().onError(_SocketServerSession.get(channel), error);
   }
}
