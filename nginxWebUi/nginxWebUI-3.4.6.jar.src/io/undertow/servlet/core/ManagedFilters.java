/*    */ package io.undertow.servlet.core;
/*    */ 
/*    */ import io.undertow.servlet.api.FilterInfo;
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
/*    */ public class ManagedFilters
/*    */ {
/* 35 */   private final Map<String, ManagedFilter> managedFilterMap = (Map<String, ManagedFilter>)new CopyOnWriteMap();
/*    */   private final DeploymentImpl deployment;
/*    */   private final ServletPathMatches servletPathMatches;
/*    */   
/*    */   public ManagedFilters(DeploymentImpl deployment, ServletPathMatches servletPathMatches) {
/* 40 */     this.deployment = deployment;
/* 41 */     this.servletPathMatches = servletPathMatches;
/*    */   }
/*    */   
/*    */   public ManagedFilter addFilter(FilterInfo filterInfo) {
/* 45 */     ManagedFilter managedFilter = new ManagedFilter(filterInfo, this.deployment.getServletContext());
/* 46 */     this.managedFilterMap.put(filterInfo.getName(), managedFilter);
/* 47 */     this.deployment.addLifecycleObjects(new Lifecycle[] { managedFilter });
/* 48 */     this.servletPathMatches.invalidate();
/* 49 */     return managedFilter;
/*    */   }
/*    */   
/*    */   public ManagedFilter getManagedFilter(String name) {
/* 53 */     return this.managedFilterMap.get(name);
/*    */   }
/*    */   
/*    */   public Map<String, ManagedFilter> getFilters() {
/* 57 */     return new HashMap<>(this.managedFilterMap);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\servlet\core\ManagedFilters.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */