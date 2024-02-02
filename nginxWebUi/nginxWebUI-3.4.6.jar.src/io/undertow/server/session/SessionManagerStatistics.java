/*    */ package io.undertow.server.session;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ public interface SessionManagerStatistics
/*    */ {
/*    */   long getCreatedSessionCount();
/*    */   
/*    */   long getMaxActiveSessions();
/*    */   
/*    */   default long getHighestSessionCount() {
/* 46 */     return -1L;
/*    */   }
/*    */   
/*    */   long getActiveSessionCount();
/*    */   
/*    */   long getExpiredSessionCount();
/*    */   
/*    */   long getRejectedSessions();
/*    */   
/*    */   long getMaxSessionAliveTime();
/*    */   
/*    */   long getAverageSessionAliveTime();
/*    */   
/*    */   long getStartTime();
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\session\SessionManagerStatistics.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */