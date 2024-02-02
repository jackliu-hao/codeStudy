/*    */ package ch.qos.logback.classic;
/*    */ 
/*    */ import ch.qos.logback.classic.spi.ILoggingEvent;
/*    */ import ch.qos.logback.core.AsyncAppenderBase;
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
/*    */ public class AsyncAppender
/*    */   extends AsyncAppenderBase<ILoggingEvent>
/*    */ {
/*    */   boolean includeCallerData = false;
/*    */   
/*    */   protected boolean isDiscardable(ILoggingEvent event) {
/* 38 */     Level level = event.getLevel();
/* 39 */     return (level.toInt() <= 20000);
/*    */   }
/*    */   
/*    */   protected void preprocess(ILoggingEvent eventObject) {
/* 43 */     eventObject.prepareForDeferredProcessing();
/* 44 */     if (this.includeCallerData)
/* 45 */       eventObject.getCallerData(); 
/*    */   }
/*    */   
/*    */   public boolean isIncludeCallerData() {
/* 49 */     return this.includeCallerData;
/*    */   }
/*    */   
/*    */   public void setIncludeCallerData(boolean includeCallerData) {
/* 53 */     this.includeCallerData = includeCallerData;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\classic\AsyncAppender.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */