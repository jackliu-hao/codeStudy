/*    */ package org.noear.solon.boot.undertow.websocket;
/*    */ import io.undertow.websockets.core.BufferedBinaryMessage;
/*    */ import io.undertow.websockets.core.BufferedTextMessage;
/*    */ import io.undertow.websockets.core.StreamSourceFrameChannel;
/*    */ import io.undertow.websockets.core.WebSocketChannel;
/*    */ import io.undertow.websockets.spi.WebSocketHttpExchange;
/*    */ import java.io.IOException;
/*    */ import java.nio.ByteBuffer;
/*    */ import java.util.List;
/*    */ import org.noear.solon.Solon;
/*    */ import org.noear.solon.core.event.EventBus;
/*    */ import org.noear.solon.core.message.Message;
/*    */ import org.noear.solon.core.message.Session;
/*    */ import org.xnio.Pooled;
/*    */ 
/*    */ public class UtWsChannelListener extends AbstractReceiveListener {
/*    */   public void onOpen(WebSocketHttpExchange exchange, WebSocketChannel channel) {
/* 18 */     Session session = _SocketServerSession.get(channel);
/* 19 */     exchange.getRequestHeaders().forEach((k, v) -> {
/*    */           if (v.size() > 0) {
/*    */             session.headerSet(k, v.get(0));
/*    */           }
/*    */         });
/*    */     
/* 25 */     Solon.app().listener().onOpen(session);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   protected void onFullBinaryMessage(WebSocketChannel channel, BufferedBinaryMessage msg) throws IOException {
/*    */     try {
/* 32 */       Pooled<ByteBuffer[]> pulledData = msg.getData();
/*    */       
/*    */       try {
/* 35 */         ByteBuffer[] resource = (ByteBuffer[])pulledData.getResource();
/* 36 */         ByteBuffer byteBuffer = WebSockets.mergeBuffers(resource);
/*    */         
/* 38 */         Session session = _SocketServerSession.get(channel);
/* 39 */         Message message = null;
/*    */         
/* 41 */         if (Solon.app().enableWebSocketD()) {
/* 42 */           message = ProtocolManager.decode(byteBuffer);
/*    */         } else {
/* 44 */           message = Message.wrap(channel.getUrl(), null, byteBuffer.array());
/*    */         } 
/*    */         
/* 47 */         Solon.app().listener().onMessage(session, message);
/*    */       } finally {
/*    */         
/* 50 */         pulledData.discard();
/*    */       }
/*    */     
/* 53 */     } catch (Throwable ex) {
/* 54 */       EventBus.push(ex);
/*    */     } 
/*    */   }
/*    */ 
/*    */   
/*    */   protected void onFullTextMessage(WebSocketChannel channel, BufferedTextMessage msg) throws IOException {
/*    */     try {
/* 61 */       Session session = _SocketServerSession.get(channel);
/* 62 */       Message message = Message.wrap(channel.getUrl(), null, msg.getData());
/*    */       
/* 64 */       Solon.app().listener().onMessage(session, message.isString(true));
/* 65 */     } catch (Throwable ex) {
/* 66 */       EventBus.push(ex);
/*    */     } 
/*    */   }
/*    */ 
/*    */   
/*    */   protected void onClose(WebSocketChannel channel, StreamSourceFrameChannel frameChannel) throws IOException {
/* 72 */     Solon.app().listener().onClose(_SocketServerSession.get(channel));
/*    */     
/* 74 */     _SocketServerSession.remove(channel);
/*    */   }
/*    */ 
/*    */   
/*    */   protected void onError(WebSocketChannel channel, Throwable error) {
/* 79 */     Solon.app().listener().onError(_SocketServerSession.get(channel), error);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\boo\\undertow\websocket\UtWsChannelListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */