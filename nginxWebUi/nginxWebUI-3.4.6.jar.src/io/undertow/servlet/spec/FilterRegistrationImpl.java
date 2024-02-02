/*     */ package io.undertow.servlet.spec;
/*     */ 
/*     */ import io.undertow.servlet.api.Deployment;
/*     */ import io.undertow.servlet.api.DeploymentInfo;
/*     */ import io.undertow.servlet.api.FilterInfo;
/*     */ import io.undertow.servlet.api.FilterMappingInfo;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.EnumSet;
/*     */ import java.util.HashSet;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import javax.servlet.DispatcherType;
/*     */ import javax.servlet.FilterRegistration;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class FilterRegistrationImpl
/*     */   implements FilterRegistration, FilterRegistration.Dynamic
/*     */ {
/*     */   private final FilterInfo filterInfo;
/*     */   private final Deployment deployment;
/*     */   private final ServletContextImpl servletContext;
/*     */   
/*     */   public FilterRegistrationImpl(FilterInfo filterInfo, Deployment deployment, ServletContextImpl servletContext) {
/*  47 */     this.filterInfo = filterInfo;
/*  48 */     this.deployment = deployment;
/*  49 */     this.servletContext = servletContext;
/*     */   }
/*     */ 
/*     */   
/*     */   public void addMappingForServletNames(EnumSet<DispatcherType> dispatcherTypes, boolean isMatchAfter, String... servletNames) {
/*  54 */     this.servletContext.addMappingForServletNames(this.filterInfo, dispatcherTypes, isMatchAfter, servletNames);
/*     */   }
/*     */ 
/*     */   
/*     */   public Collection<String> getServletNameMappings() {
/*  59 */     DeploymentInfo deploymentInfo = this.deployment.getDeploymentInfo();
/*  60 */     List<String> ret = new ArrayList<>();
/*  61 */     for (FilterMappingInfo mapping : deploymentInfo.getFilterMappings()) {
/*  62 */       if (mapping.getMappingType() == FilterMappingInfo.MappingType.SERVLET && 
/*  63 */         mapping.getFilterName().equals(this.filterInfo.getName())) {
/*  64 */         ret.add(mapping.getMapping());
/*     */       }
/*     */     } 
/*     */     
/*  68 */     return ret;
/*     */   }
/*     */ 
/*     */   
/*     */   public void addMappingForUrlPatterns(EnumSet<DispatcherType> dispatcherTypes, boolean isMatchAfter, String... urlPatterns) {
/*  73 */     this.servletContext.addMappingForUrlPatterns(this.filterInfo, dispatcherTypes, isMatchAfter, urlPatterns);
/*     */   }
/*     */ 
/*     */   
/*     */   public Collection<String> getUrlPatternMappings() {
/*  78 */     DeploymentInfo deploymentInfo = this.deployment.getDeploymentInfo();
/*  79 */     List<String> ret = new ArrayList<>();
/*  80 */     for (FilterMappingInfo mapping : deploymentInfo.getFilterMappings()) {
/*  81 */       if (mapping.getMappingType() == FilterMappingInfo.MappingType.URL && 
/*  82 */         mapping.getFilterName().equals(this.filterInfo.getName())) {
/*  83 */         ret.add(mapping.getMapping());
/*     */       }
/*     */     } 
/*     */     
/*  87 */     return ret;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getName() {
/*  92 */     return this.filterInfo.getName();
/*     */   }
/*     */ 
/*     */   
/*     */   public String getClassName() {
/*  97 */     return this.filterInfo.getFilterClass().getName();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean setInitParameter(String name, String value) {
/* 102 */     if (this.filterInfo.getInitParams().containsKey(name)) {
/* 103 */       return false;
/*     */     }
/* 105 */     this.filterInfo.addInitParam(name, value);
/* 106 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getInitParameter(String name) {
/* 111 */     return (String)this.filterInfo.getInitParams().get(name);
/*     */   }
/*     */ 
/*     */   
/*     */   public Set<String> setInitParameters(Map<String, String> initParameters) {
/* 116 */     Set<String> ret = new HashSet<>();
/* 117 */     for (Map.Entry<String, String> entry : initParameters.entrySet()) {
/* 118 */       if (!setInitParameter(entry.getKey(), entry.getValue())) {
/* 119 */         ret.add(entry.getKey());
/*     */       }
/*     */     } 
/* 122 */     return ret;
/*     */   }
/*     */ 
/*     */   
/*     */   public Map<String, String> getInitParameters() {
/* 127 */     return this.filterInfo.getInitParams();
/*     */   }
/*     */ 
/*     */   
/*     */   public void setAsyncSupported(boolean isAsyncSupported) {
/* 132 */     this.filterInfo.setAsyncSupported(isAsyncSupported);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\servlet\spec\FilterRegistrationImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */