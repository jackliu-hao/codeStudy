/*    */ package com.mysql.cj.log;
/*    */ 
/*    */ import com.mysql.cj.Constants;
/*    */ import com.mysql.cj.Query;
/*    */ import com.mysql.cj.Session;
/*    */ import com.mysql.cj.protocol.Resultset;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class LoggingProfilerEventHandler
/*    */   implements ProfilerEventHandler
/*    */ {
/*    */   private Log logger;
/*    */   
/*    */   public void consumeEvent(ProfilerEvent evt) {
/* 47 */     switch (evt.getEventType()) {
/*    */       case 0:
/* 49 */         this.logger.logWarn(evt);
/*    */         return;
/*    */     } 
/*    */     
/* 53 */     this.logger.logInfo(evt);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public void destroy() {
/* 59 */     this.logger = null;
/*    */   }
/*    */   
/*    */   public void init(Log log) {
/* 63 */     this.logger = log;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void processEvent(byte eventType, Session session, Query query, Resultset resultSet, long eventDuration, Throwable eventCreationPoint, String message) {
/* 70 */     consumeEvent(new ProfilerEventImpl(eventType, (session == null) ? "" : session
/* 71 */           .getHostInfo().getHost(), (session == null) ? "" : session
/* 72 */           .getHostInfo().getDatabase(), (session == null) ? -1L : session
/* 73 */           .getThreadId(), (query == null) ? -1 : query
/* 74 */           .getId(), (resultSet == null) ? -1 : resultSet
/* 75 */           .getResultId(), eventDuration, (session == null) ? Constants.MILLIS_I18N : session
/*    */           
/* 77 */           .getQueryTimingUnits(), eventCreationPoint, message));
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\log\LoggingProfilerEventHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */