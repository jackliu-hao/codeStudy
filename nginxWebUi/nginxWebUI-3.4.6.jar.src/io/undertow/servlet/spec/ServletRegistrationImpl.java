/*     */ package io.undertow.servlet.spec;
/*     */ 
/*     */ import io.undertow.UndertowMessages;
/*     */ import io.undertow.servlet.api.Deployment;
/*     */ import io.undertow.servlet.api.DeploymentInfo;
/*     */ import io.undertow.servlet.api.HttpMethodSecurityInfo;
/*     */ import io.undertow.servlet.api.SecurityConstraint;
/*     */ import io.undertow.servlet.api.SecurityInfo;
/*     */ import io.undertow.servlet.api.ServletInfo;
/*     */ import io.undertow.servlet.api.ServletSecurityInfo;
/*     */ import io.undertow.servlet.api.TransportGuaranteeType;
/*     */ import io.undertow.servlet.api.WebResourceCollection;
/*     */ import io.undertow.servlet.core.ManagedServlet;
/*     */ import java.util.Collection;
/*     */ import java.util.HashSet;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import javax.servlet.HttpMethodConstraintElement;
/*     */ import javax.servlet.MultipartConfigElement;
/*     */ import javax.servlet.ServletRegistration;
/*     */ import javax.servlet.ServletSecurityElement;
/*     */ import javax.servlet.annotation.ServletSecurity;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ServletRegistrationImpl
/*     */   implements ServletRegistration, ServletRegistration.Dynamic
/*     */ {
/*     */   private final ServletInfo servletInfo;
/*     */   private final ManagedServlet managedServlet;
/*     */   private final Deployment deployment;
/*     */   
/*     */   public ServletRegistrationImpl(ServletInfo servletInfo, ManagedServlet managedServlet, Deployment deployment) {
/*  57 */     this.servletInfo = servletInfo;
/*  58 */     this.managedServlet = managedServlet;
/*  59 */     this.deployment = deployment;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setLoadOnStartup(int loadOnStartup) {
/*  64 */     this.servletInfo.setLoadOnStartup(Integer.valueOf(loadOnStartup));
/*     */   }
/*     */ 
/*     */   
/*     */   public Set<String> setServletSecurity(ServletSecurityElement constraint) {
/*  69 */     if (constraint == null) {
/*  70 */       throw UndertowMessages.MESSAGES.argumentCannotBeNull("constraint");
/*     */     }
/*  72 */     DeploymentInfo deploymentInfo = this.deployment.getDeploymentInfo();
/*     */ 
/*     */     
/*  75 */     Set<String> urlPatterns = new HashSet<>();
/*  76 */     for (SecurityConstraint sc : deploymentInfo.getSecurityConstraints()) {
/*  77 */       for (WebResourceCollection webResources : sc.getWebResourceCollections()) {
/*  78 */         urlPatterns.addAll(webResources.getUrlPatterns());
/*     */       }
/*     */     } 
/*  81 */     Set<String> ret = new HashSet<>();
/*  82 */     for (String url : this.servletInfo.getMappings()) {
/*  83 */       if (urlPatterns.contains(url)) {
/*  84 */         ret.add(url);
/*     */       }
/*     */     } 
/*  87 */     ServletSecurityInfo info = new ServletSecurityInfo();
/*  88 */     this.servletInfo.setServletSecurityInfo(info);
/*  89 */     ((ServletSecurityInfo)((ServletSecurityInfo)info.setTransportGuaranteeType((constraint.getTransportGuarantee() == ServletSecurity.TransportGuarantee.CONFIDENTIAL) ? TransportGuaranteeType.CONFIDENTIAL : TransportGuaranteeType.NONE))
/*  90 */       .setEmptyRoleSemantic(emptyRoleSemantic(constraint.getEmptyRoleSemantic())))
/*  91 */       .addRolesAllowed(constraint.getRolesAllowed());
/*     */     
/*  93 */     for (HttpMethodConstraintElement methodConstraint : constraint.getHttpMethodConstraints()) {
/*  94 */       info.addHttpMethodSecurityInfo((HttpMethodSecurityInfo)((HttpMethodSecurityInfo)((HttpMethodSecurityInfo)(new HttpMethodSecurityInfo())
/*  95 */           .setTransportGuaranteeType((methodConstraint.getTransportGuarantee() == ServletSecurity.TransportGuarantee.CONFIDENTIAL) ? TransportGuaranteeType.CONFIDENTIAL : TransportGuaranteeType.NONE))
/*  96 */           .setMethod(methodConstraint.getMethodName())
/*  97 */           .setEmptyRoleSemantic(emptyRoleSemantic(methodConstraint.getEmptyRoleSemantic())))
/*  98 */           .addRolesAllowed(methodConstraint.getRolesAllowed()));
/*     */     }
/* 100 */     return ret;
/*     */   }
/*     */   
/*     */   private SecurityInfo.EmptyRoleSemantic emptyRoleSemantic(ServletSecurity.EmptyRoleSemantic emptyRoleSemantic) {
/* 104 */     switch (emptyRoleSemantic) {
/*     */       case PERMIT:
/* 106 */         return SecurityInfo.EmptyRoleSemantic.PERMIT;
/*     */       case DENY:
/* 108 */         return SecurityInfo.EmptyRoleSemantic.DENY;
/*     */     } 
/* 110 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void setMultipartConfig(MultipartConfigElement multipartConfig) {
/* 116 */     this.servletInfo.setMultipartConfig(multipartConfig);
/* 117 */     this.managedServlet.setupMultipart(this.deployment.getServletContext());
/*     */   }
/*     */ 
/*     */   
/*     */   public void setRunAsRole(String roleName) {
/* 122 */     this.servletInfo.setRunAs(roleName);
/*     */   }
/*     */ 
/*     */   
/*     */   public void setAsyncSupported(boolean isAsyncSupported) {
/* 127 */     this.servletInfo.setAsyncSupported(isAsyncSupported);
/*     */   }
/*     */ 
/*     */   
/*     */   public Set<String> addMapping(String... urlPatterns) {
/* 132 */     return this.deployment.tryAddServletMappings(this.servletInfo, urlPatterns);
/*     */   }
/*     */ 
/*     */   
/*     */   public Collection<String> getMappings() {
/* 137 */     return this.servletInfo.getMappings();
/*     */   }
/*     */ 
/*     */   
/*     */   public String getRunAsRole() {
/* 142 */     return this.servletInfo.getRunAs();
/*     */   }
/*     */ 
/*     */   
/*     */   public String getName() {
/* 147 */     return this.servletInfo.getName();
/*     */   }
/*     */ 
/*     */   
/*     */   public String getClassName() {
/* 152 */     return this.servletInfo.getServletClass().getName();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean setInitParameter(String name, String value) {
/* 158 */     if (this.servletInfo.getInitParams().containsKey(name)) {
/* 159 */       return false;
/*     */     }
/* 161 */     this.servletInfo.addInitParam(name, value);
/* 162 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getInitParameter(String name) {
/* 167 */     return (String)this.servletInfo.getInitParams().get(name);
/*     */   }
/*     */ 
/*     */   
/*     */   public Set<String> setInitParameters(Map<String, String> initParameters) {
/* 172 */     Set<String> ret = new HashSet<>();
/* 173 */     for (Map.Entry<String, String> entry : initParameters.entrySet()) {
/* 174 */       if (!setInitParameter(entry.getKey(), entry.getValue())) {
/* 175 */         ret.add(entry.getKey());
/*     */       }
/*     */     } 
/* 178 */     return ret;
/*     */   }
/*     */ 
/*     */   
/*     */   public Map<String, String> getInitParameters() {
/* 183 */     return this.servletInfo.getInitParams();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\servlet\spec\ServletRegistrationImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */