/*    */ package io.undertow.servlet.api;
/*    */ 
/*    */ import java.util.Collections;
/*    */ import java.util.Date;
/*    */ import java.util.Map;
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
/*    */ public interface SessionPersistenceManager
/*    */ {
/*    */   void persistSessions(String paramString, Map<String, PersistentSession> paramMap);
/*    */   
/*    */   Map<String, PersistentSession> loadSessionAttributes(String paramString, ClassLoader paramClassLoader);
/*    */   
/*    */   void clear(String paramString);
/*    */   
/*    */   public static class PersistentSession
/*    */   {
/*    */     private final Date expiration;
/*    */     private final Map<String, Object> sessionData;
/*    */     
/*    */     public PersistentSession(Date expiration, Map<String, Object> sessionData) {
/* 45 */       this.expiration = expiration;
/* 46 */       this.sessionData = sessionData;
/*    */     }
/*    */     
/*    */     public Date getExpiration() {
/* 50 */       return this.expiration;
/*    */     }
/*    */     
/*    */     public Map<String, Object> getSessionData() {
/* 54 */       return Collections.unmodifiableMap(this.sessionData);
/*    */     }
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\servlet\api\SessionPersistenceManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */