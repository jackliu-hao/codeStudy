/*    */ package org.noear.solon.logging.event;
/*    */ 
/*    */ import java.util.Map;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class LogEvent
/*    */ {
/*    */   private String loggerName;
/*    */   private Level level;
/*    */   private Map<String, String> metainfo;
/*    */   private Object content;
/*    */   private long timeStamp;
/*    */   private String threadName;
/*    */   private Throwable throwable;
/*    */   
/*    */   public LogEvent(String loggerName, Level level, Map<String, String> metainfo, Object content, long timeStamp, String threadName, Throwable throwable) {
/* 22 */     this.loggerName = loggerName;
/* 23 */     this.level = level;
/* 24 */     this.metainfo = metainfo;
/* 25 */     this.content = content;
/* 26 */     this.timeStamp = timeStamp;
/* 27 */     this.threadName = threadName;
/* 28 */     this.throwable = throwable;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String getLoggerName() {
/* 35 */     return this.loggerName;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public Level getLevel() {
/* 42 */     return this.level;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public Map<String, String> getMetainfo() {
/* 49 */     return this.metainfo;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public Object getContent() {
/* 56 */     return this.content;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public long getTimeStamp() {
/* 63 */     return this.timeStamp;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String getThreadName() {
/* 70 */     return this.threadName;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public Throwable getThrowable() {
/* 77 */     return this.throwable;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\logging\event\LogEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */