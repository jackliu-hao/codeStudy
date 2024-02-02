/*    */ package org.noear.solon.logging.event;
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
/*    */ public interface Appender
/*    */ {
/*    */   default Level getDefaultLevel() {
/* 15 */     return Level.TRACE;
/*    */   }
/*    */   
/*    */   default void start() {}
/*    */   
/*    */   default void stop() {}
/*    */   
/*    */   String getName();
/*    */   
/*    */   void setName(String paramString);
/*    */   
/*    */   void append(LogEvent paramLogEvent);
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\logging\event\Appender.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */