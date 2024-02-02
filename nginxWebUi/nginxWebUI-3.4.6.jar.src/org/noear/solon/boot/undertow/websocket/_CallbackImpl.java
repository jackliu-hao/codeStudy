/*    */ package org.noear.solon.boot.undertow.websocket;
/*    */ 
/*    */ import io.undertow.websockets.core.WebSocketCallback;
/*    */ import io.undertow.websockets.core.WebSocketChannel;
/*    */ import org.noear.solon.core.event.EventBus;
/*    */ 
/*    */ 
/*    */ 
/*    */ class _CallbackImpl
/*    */   implements WebSocketCallback<Void>
/*    */ {
/* 12 */   public static final WebSocketCallback<Void> instance = new _CallbackImpl();
/*    */ 
/*    */ 
/*    */   
/*    */   public void complete(WebSocketChannel webSocketChannel, Void unused) {}
/*    */ 
/*    */ 
/*    */   
/*    */   public void onError(WebSocketChannel webSocketChannel, Void unused, Throwable e) {
/* 21 */     EventBus.push(e);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\boo\\undertow\websocket\_CallbackImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */