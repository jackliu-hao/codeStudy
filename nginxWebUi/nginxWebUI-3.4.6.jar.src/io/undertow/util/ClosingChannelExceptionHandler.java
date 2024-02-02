/*    */ package io.undertow.util;
/*    */ 
/*    */ import io.undertow.UndertowLogger;
/*    */ import java.io.Closeable;
/*    */ import java.io.IOException;
/*    */ import java.nio.channels.Channel;
/*    */ import org.xnio.ChannelExceptionHandler;
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
/*    */ 
/*    */ 
/*    */ public class ClosingChannelExceptionHandler<T extends Channel>
/*    */   implements ChannelExceptionHandler<T>
/*    */ {
/*    */   private final Closeable[] closable;
/*    */   
/*    */   public ClosingChannelExceptionHandler(Closeable... closable) {
/* 41 */     this.closable = closable;
/*    */   }
/*    */ 
/*    */   
/*    */   public void handleException(T t, IOException e) {
/* 46 */     UndertowLogger.REQUEST_IO_LOGGER.ioException(e);
/* 47 */     IoUtils.safeClose((Closeable)t);
/* 48 */     IoUtils.safeClose(this.closable);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\underto\\util\ClosingChannelExceptionHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */