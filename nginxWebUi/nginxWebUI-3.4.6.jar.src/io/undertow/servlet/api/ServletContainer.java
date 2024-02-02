/*    */ package io.undertow.servlet.api;
/*    */ 
/*    */ import io.undertow.servlet.core.ServletContainerImpl;
/*    */ import java.util.Collection;
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
/*    */ public interface ServletContainer
/*    */ {
/*    */   Collection<String> listDeployments();
/*    */   
/*    */   DeploymentManager addDeployment(DeploymentInfo paramDeploymentInfo);
/*    */   
/*    */   DeploymentManager getDeployment(String paramString);
/*    */   
/*    */   void removeDeployment(DeploymentInfo paramDeploymentInfo);
/*    */   
/*    */   DeploymentManager getDeploymentByPath(String paramString);
/*    */   
/*    */   public static class Factory
/*    */   {
/*    */     public static ServletContainer newInstance() {
/* 47 */       return (ServletContainer)new ServletContainerImpl();
/*    */     }
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\servlet\api\ServletContainer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */