/*    */ package io.undertow.servlet.core;
/*    */ 
/*    */ import io.undertow.server.session.InMemorySessionManager;
/*    */ import io.undertow.server.session.SessionManager;
/*    */ import io.undertow.servlet.api.Deployment;
/*    */ import io.undertow.servlet.api.SessionManagerFactory;
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
/*    */ public class InMemorySessionManagerFactory
/*    */   implements SessionManagerFactory
/*    */ {
/*    */   private final int maxSessions;
/*    */   private final boolean expireOldestUnusedSessionOnMax;
/*    */   
/*    */   public InMemorySessionManagerFactory() {
/* 36 */     this(-1, false);
/*    */   }
/*    */   
/*    */   public InMemorySessionManagerFactory(int maxSessions) {
/* 40 */     this(maxSessions, false);
/*    */   }
/*    */   
/*    */   public InMemorySessionManagerFactory(int maxSessions, boolean expireOldestUnusedSessionOnMax) {
/* 44 */     this.maxSessions = maxSessions;
/* 45 */     this.expireOldestUnusedSessionOnMax = expireOldestUnusedSessionOnMax;
/*    */   }
/*    */ 
/*    */   
/*    */   public SessionManager createSessionManager(Deployment deployment) {
/* 50 */     return (SessionManager)new InMemorySessionManager(deployment.getDeploymentInfo().getSessionIdGenerator(), deployment.getDeploymentInfo().getDeploymentName(), this.maxSessions, this.expireOldestUnusedSessionOnMax, (deployment.getDeploymentInfo().getMetricsCollector() != null));
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\servlet\core\InMemorySessionManagerFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */