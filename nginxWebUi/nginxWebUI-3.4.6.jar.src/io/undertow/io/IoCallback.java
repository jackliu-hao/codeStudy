/*    */ package io.undertow.io;
/*    */ 
/*    */ import io.undertow.server.HttpServerExchange;
/*    */ import java.io.IOException;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public interface IoCallback
/*    */ {
/* 37 */   public static final IoCallback END_EXCHANGE = new DefaultIoCallback();
/*    */   
/*    */   void onComplete(HttpServerExchange paramHttpServerExchange, Sender paramSender);
/*    */   
/*    */   void onException(HttpServerExchange paramHttpServerExchange, Sender paramSender, IOException paramIOException);
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\io\IoCallback.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */