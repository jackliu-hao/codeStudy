/*    */ package org.noear.solon.core.handle;
/*    */ 
/*    */ import org.noear.solon.core.SignalType;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public enum MethodType
/*    */ {
/* 13 */   GET("GET", SignalType.HTTP),
/* 14 */   POST("POST", SignalType.HTTP),
/*    */   
/* 16 */   PUT("PUT", SignalType.HTTP),
/* 17 */   DELETE("DELETE", SignalType.HTTP),
/* 18 */   PATCH("PATCH", SignalType.HTTP),
/*    */   
/* 20 */   HEAD("HEAD", SignalType.HTTP),
/*    */   
/* 22 */   OPTIONS("OPTIONS", SignalType.HTTP),
/*    */   
/* 24 */   TRACE("TRACE", SignalType.HTTP),
/* 25 */   CONNECT("CONNECT", SignalType.HTTP),
/*    */ 
/*    */   
/* 28 */   HTTP("HTTP", SignalType.HTTP),
/*    */ 
/*    */   
/* 31 */   WEBSOCKET("WEBSOCKET", SignalType.WEBSOCKET),
/*    */ 
/*    */   
/* 34 */   SOCKET("SOCKET", SignalType.SOCKET),
/*    */ 
/*    */   
/* 37 */   ALL("ALL", SignalType.ALL);
/*    */   
/*    */   public final String name;
/*    */   public final SignalType signal;
/*    */   
/*    */   MethodType(String name, SignalType signal) {
/* 43 */     this.name = name;
/* 44 */     this.signal = signal;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\core\handle\MethodType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */