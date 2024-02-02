/*    */ package io.undertow.servlet.core;
/*    */ 
/*    */ import io.undertow.servlet.api.ServletInfo;
/*    */ import io.undertow.servlet.handlers.ServletHandler;
/*    */ import io.undertow.servlet.handlers.ServletPathMatches;
/*    */ import io.undertow.util.CopyOnWriteMap;
/*    */ import java.util.HashMap;
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
/*    */ public class ManagedServlets
/*    */ {
/* 36 */   private final Map<String, ServletHandler> managedServletMap = (Map<String, ServletHandler>)new CopyOnWriteMap();
/*    */   private final DeploymentImpl deployment;
/*    */   private final ServletPathMatches servletPaths;
/*    */   
/*    */   public ManagedServlets(DeploymentImpl deployment, ServletPathMatches servletPaths) {
/* 41 */     this.deployment = deployment;
/* 42 */     this.servletPaths = servletPaths;
/*    */   }
/*    */   
/*    */   public ServletHandler addServlet(ServletInfo servletInfo) {
/* 46 */     ManagedServlet managedServlet = new ManagedServlet(servletInfo, this.deployment.getServletContext());
/* 47 */     ServletHandler servletHandler = new ServletHandler(managedServlet);
/* 48 */     this.managedServletMap.put(servletInfo.getName(), servletHandler);
/* 49 */     this.deployment.addLifecycleObjects(new Lifecycle[] { managedServlet });
/* 50 */     this.servletPaths.invalidate();
/*    */     
/* 52 */     return servletHandler;
/*    */   }
/*    */   
/*    */   public ManagedServlet getManagedServlet(String name) {
/* 56 */     ServletHandler servletHandler = this.managedServletMap.get(name);
/* 57 */     if (servletHandler == null) {
/* 58 */       return null;
/*    */     }
/* 60 */     return servletHandler.getManagedServlet();
/*    */   }
/*    */   
/*    */   public ServletHandler getServletHandler(String name) {
/* 64 */     return this.managedServletMap.get(name);
/*    */   }
/*    */   
/*    */   public Map<String, ServletHandler> getServletHandlers() {
/* 68 */     return new HashMap<>(this.managedServletMap);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\servlet\core\ManagedServlets.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */