/*    */ package io.undertow.servlet.core;
/*    */ 
/*    */ import io.undertow.servlet.UndertowServletMessages;
/*    */ import io.undertow.servlet.api.DeploymentInfo;
/*    */ import io.undertow.servlet.api.DeploymentManager;
/*    */ import io.undertow.servlet.api.ServletContainer;
/*    */ import java.util.Collection;
/*    */ import java.util.Collections;
/*    */ import java.util.HashMap;
/*    */ import java.util.HashSet;
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
/*    */ public class ServletContainerImpl
/*    */   implements ServletContainer
/*    */ {
/* 40 */   private final Map<String, DeploymentManager> deployments = Collections.synchronizedMap(new HashMap<>());
/* 41 */   private final Map<String, DeploymentManager> deploymentsByPath = Collections.synchronizedMap(new HashMap<>());
/*    */ 
/*    */   
/*    */   public Collection<String> listDeployments() {
/* 45 */     return new HashSet<>(this.deployments.keySet());
/*    */   }
/*    */ 
/*    */   
/*    */   public DeploymentManager addDeployment(DeploymentInfo deployment) {
/* 50 */     DeploymentInfo dep = deployment.clone();
/* 51 */     DeploymentManager deploymentManager = new DeploymentManagerImpl(dep, this);
/* 52 */     this.deployments.put(dep.getDeploymentName(), deploymentManager);
/* 53 */     this.deploymentsByPath.put(dep.getContextPath(), deploymentManager);
/* 54 */     return deploymentManager;
/*    */   }
/*    */ 
/*    */   
/*    */   public DeploymentManager getDeployment(String deploymentName) {
/* 59 */     return this.deployments.get(deploymentName);
/*    */   }
/*    */ 
/*    */   
/*    */   public void removeDeployment(DeploymentInfo deploymentInfo) {
/* 64 */     DeploymentManager deploymentManager = this.deployments.get(deploymentInfo.getDeploymentName());
/* 65 */     if (deploymentManager.getState() != DeploymentManager.State.UNDEPLOYED) {
/* 66 */       throw UndertowServletMessages.MESSAGES.canOnlyRemoveDeploymentsWhenUndeployed(deploymentManager.getState());
/*    */     }
/* 68 */     this.deployments.remove(deploymentInfo.getDeploymentName());
/* 69 */     this.deploymentsByPath.remove(deploymentInfo.getContextPath());
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public DeploymentManager getDeploymentByPath(String path) {
/* 75 */     DeploymentManager exact = this.deploymentsByPath.get(path.isEmpty() ? "/" : path);
/* 76 */     if (exact != null) {
/* 77 */       return exact;
/*    */     }
/* 79 */     int length = path.length();
/* 80 */     int pos = length;
/*    */     
/* 82 */     while (pos > 1) {
/* 83 */       pos--;
/* 84 */       if (path.charAt(pos) == '/') {
/* 85 */         String part = path.substring(0, pos);
/* 86 */         DeploymentManager deployment = this.deploymentsByPath.get(part);
/* 87 */         if (deployment != null) {
/* 88 */           return deployment;
/*    */         }
/*    */       } 
/*    */     } 
/* 92 */     return this.deploymentsByPath.get("/");
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\servlet\core\ServletContainerImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */