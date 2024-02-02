/*    */ package io.undertow.io;
/*    */ 
/*    */ import io.undertow.UndertowLogger;
/*    */ import io.undertow.server.HttpServerExchange;
/*    */ import java.io.Closeable;
/*    */ import java.io.IOException;
/*    */ import org.xnio.IoUtils;
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
/*    */ public class DefaultIoCallback
/*    */   implements IoCallback
/*    */ {
/* 35 */   private static final IoCallback CALLBACK = new IoCallback()
/*    */     {
/*    */       public void onComplete(HttpServerExchange exchange, Sender sender) {
/* 38 */         exchange.endExchange();
/*    */       }
/*    */ 
/*    */       
/*    */       public void onException(HttpServerExchange exchange, Sender sender, IOException exception) {
/* 43 */         UndertowLogger.REQUEST_IO_LOGGER.ioException(exception);
/* 44 */         exchange.endExchange();
/*    */       }
/*    */     };
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void onComplete(HttpServerExchange exchange, Sender sender) {
/* 54 */     sender.close(CALLBACK);
/*    */   }
/*    */ 
/*    */   
/*    */   public void onException(HttpServerExchange exchange, Sender sender, IOException exception) {
/* 59 */     UndertowLogger.REQUEST_IO_LOGGER.ioException(exception);
/*    */     try {
/* 61 */       exchange.endExchange();
/*    */     } finally {
/* 63 */       IoUtils.safeClose((Closeable)exchange.getConnection());
/*    */     } 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\io\DefaultIoCallback.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */