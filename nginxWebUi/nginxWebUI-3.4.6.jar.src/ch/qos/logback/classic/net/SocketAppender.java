/*    */ package ch.qos.logback.classic.net;
/*    */ 
/*    */ import ch.qos.logback.classic.spi.ILoggingEvent;
/*    */ import ch.qos.logback.core.net.AbstractSocketAppender;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class SocketAppender
/*    */   extends AbstractSocketAppender<ILoggingEvent>
/*    */ {
/* 34 */   private static final PreSerializationTransformer<ILoggingEvent> pst = new LoggingEventPreSerializationTransformer();
/*    */ 
/*    */ 
/*    */   
/*    */   private boolean includeCallerData = false;
/*    */ 
/*    */ 
/*    */   
/*    */   protected void postProcessEvent(ILoggingEvent event) {
/* 43 */     if (this.includeCallerData) {
/* 44 */       event.getCallerData();
/*    */     }
/*    */   }
/*    */   
/*    */   public void setIncludeCallerData(boolean includeCallerData) {
/* 49 */     this.includeCallerData = includeCallerData;
/*    */   }
/*    */   
/*    */   public PreSerializationTransformer<ILoggingEvent> getPST() {
/* 53 */     return pst;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\classic\net\SocketAppender.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */