/*    */ package io.undertow.servlet.api;
/*    */ 
/*    */ import io.undertow.server.HttpHandler;
/*    */ import javax.servlet.ServletException;
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
/*    */ 
/*    */ 
/*    */ public interface DeploymentManager
/*    */ {
/*    */   void deploy();
/*    */   
/*    */   HttpHandler start() throws ServletException;
/*    */   
/*    */   void stop() throws ServletException;
/*    */   
/*    */   void undeploy();
/*    */   
/*    */   State getState();
/*    */   
/*    */   Deployment getDeployment();
/*    */   
/*    */   public enum State
/*    */   {
/* 60 */     UNDEPLOYED,
/* 61 */     DEPLOYED,
/* 62 */     STARTED;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\servlet\api\DeploymentManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */