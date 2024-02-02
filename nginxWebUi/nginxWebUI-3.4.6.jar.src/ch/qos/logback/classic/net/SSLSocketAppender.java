/*    */ package ch.qos.logback.classic.net;
/*    */ 
/*    */ import ch.qos.logback.classic.spi.ILoggingEvent;
/*    */ import ch.qos.logback.core.net.AbstractSSLSocketAppender;
/*    */ import ch.qos.logback.core.spi.PreSerializationTransformer;
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
/*    */ public class SSLSocketAppender
/*    */   extends AbstractSSLSocketAppender<ILoggingEvent>
/*    */ {
/* 30 */   private final PreSerializationTransformer<ILoggingEvent> pst = new LoggingEventPreSerializationTransformer();
/*    */ 
/*    */ 
/*    */   
/*    */   private boolean includeCallerData;
/*    */ 
/*    */ 
/*    */   
/*    */   protected void postProcessEvent(ILoggingEvent event) {
/* 39 */     if (this.includeCallerData) {
/* 40 */       event.getCallerData();
/*    */     }
/*    */   }
/*    */   
/*    */   public void setIncludeCallerData(boolean includeCallerData) {
/* 45 */     this.includeCallerData = includeCallerData;
/*    */   }
/*    */   
/*    */   public PreSerializationTransformer<ILoggingEvent> getPST() {
/* 49 */     return this.pst;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\classic\net\SSLSocketAppender.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */