/*    */ package org.noear.solon.boot.undertow.websocket;
/*    */ import io.undertow.websockets.WebSocketConnectionCallback;
/*    */ import io.undertow.websockets.core.WebSocketChannel;
/*    */ import io.undertow.websockets.spi.WebSocketHttpExchange;
/*    */ import org.xnio.ChannelListener;
/*    */ 
/*    */ public class UtWsConnectionCallback implements WebSocketConnectionCallback {
/*  8 */   UtWsChannelListener listener = new UtWsChannelListener();
/*    */ 
/*    */   
/*    */   public void onConnect(WebSocketHttpExchange exchange, WebSocketChannel channel) {
/* 12 */     this.listener.onOpen(exchange, channel);
/*    */     
/* 14 */     channel.getReceiveSetter().set((ChannelListener)this.listener);
/* 15 */     channel.resumeReceives();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\boo\\undertow\websocket\UtWsConnectionCallback.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */