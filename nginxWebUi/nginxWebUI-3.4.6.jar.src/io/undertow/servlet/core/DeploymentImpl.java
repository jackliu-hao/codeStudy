/*     */ package io.undertow.servlet.core;
/*     */ 
/*     */ import io.undertow.security.api.AuthenticationMechanism;
/*     */ import io.undertow.server.HttpHandler;
/*     */ import io.undertow.server.session.SessionManager;
/*     */ import io.undertow.servlet.api.Deployment;
/*     */ import io.undertow.servlet.api.DeploymentInfo;
/*     */ import io.undertow.servlet.api.DeploymentManager;
/*     */ import io.undertow.servlet.api.ServletContainer;
/*     */ import io.undertow.servlet.api.ServletDispatcher;
/*     */ import io.undertow.servlet.api.ServletInfo;
/*     */ import io.undertow.servlet.api.ThreadSetupHandler;
/*     */ import io.undertow.servlet.handlers.ServletInitialHandler;
/*     */ import io.undertow.servlet.handlers.ServletPathMatches;
/*     */ import io.undertow.servlet.spec.ServletContextImpl;
/*     */ import java.nio.charset.Charset;
/*     */ import java.nio.charset.StandardCharsets;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.Executor;
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
/*     */ 
/*     */ public class DeploymentImpl
/*     */   implements Deployment
/*     */ {
/*     */   private final DeploymentManager deploymentManager;
/*     */   private final DeploymentInfo deploymentInfo;
/*     */   private final ServletContainer servletContainer;
/*  61 */   private final List<Lifecycle> lifecycleObjects = new ArrayList<>();
/*     */   
/*     */   private final ServletPathMatches servletPaths;
/*     */   private final ManagedServlets servlets;
/*     */   private final ManagedFilters filters;
/*     */   private volatile ApplicationListeners applicationListeners;
/*     */   private volatile ServletContextImpl servletContext;
/*     */   private volatile ServletInitialHandler servletHandler;
/*     */   private volatile HttpHandler initialHandler;
/*     */   private volatile ErrorPages errorPages;
/*     */   private volatile Map<String, String> mimeExtensionMappings;
/*     */   private volatile SessionManager sessionManager;
/*     */   @Deprecated
/*  74 */   private volatile Charset defaultCharset = StandardCharsets.ISO_8859_1;
/*     */   
/*  76 */   private volatile Charset defaultRequestCharset = StandardCharsets.ISO_8859_1;
/*  77 */   private volatile Charset defaultResponseCharset = StandardCharsets.ISO_8859_1;
/*     */ 
/*     */ 
/*     */   
/*     */   private volatile List<AuthenticationMechanism> authenticationMechanisms;
/*     */ 
/*     */   
/*     */   private volatile List<ThreadSetupHandler> threadSetupActions;
/*     */ 
/*     */   
/*     */   private Set<String> existingUrlPatterns;
/*     */ 
/*     */ 
/*     */   
/*     */   public DeploymentImpl(DeploymentManager deploymentManager, DeploymentInfo deploymentInfo, ServletContainer servletContainer) {
/*  92 */     this.deploymentManager = deploymentManager;
/*  93 */     this.deploymentInfo = deploymentInfo;
/*  94 */     this.servletContainer = servletContainer;
/*  95 */     this.servletPaths = new ServletPathMatches(this);
/*  96 */     this.servlets = new ManagedServlets(this, this.servletPaths);
/*  97 */     this.filters = new ManagedFilters(this, this.servletPaths);
/*     */   }
/*     */ 
/*     */   
/*     */   public ServletContainer getServletContainer() {
/* 102 */     return this.servletContainer;
/*     */   }
/*     */   
/*     */   public ManagedServlets getServlets() {
/* 106 */     return this.servlets;
/*     */   }
/*     */   
/*     */   public ManagedFilters getFilters() {
/* 110 */     return this.filters;
/*     */   }
/*     */   
/*     */   void setApplicationListeners(ApplicationListeners applicationListeners) {
/* 114 */     this.applicationListeners = applicationListeners;
/*     */   }
/*     */   
/*     */   void setServletContext(ServletContextImpl servletContext) {
/* 118 */     this.servletContext = servletContext;
/*     */   }
/*     */ 
/*     */   
/*     */   public DeploymentInfo getDeploymentInfo() {
/* 123 */     return this.deploymentInfo;
/*     */   }
/*     */ 
/*     */   
/*     */   public ApplicationListeners getApplicationListeners() {
/* 128 */     return this.applicationListeners;
/*     */   }
/*     */ 
/*     */   
/*     */   public ServletContextImpl getServletContext() {
/* 133 */     return this.servletContext;
/*     */   }
/*     */ 
/*     */   
/*     */   public HttpHandler getHandler() {
/* 138 */     return this.initialHandler;
/*     */   }
/*     */   
/*     */   public void setInitialHandler(HttpHandler initialHandler) {
/* 142 */     this.initialHandler = initialHandler;
/*     */   }
/*     */   
/*     */   void setServletHandler(ServletInitialHandler servletHandler) {
/* 146 */     this.servletHandler = servletHandler;
/*     */   }
/*     */   
/*     */   void addLifecycleObjects(Collection<Lifecycle> objects) {
/* 150 */     this.lifecycleObjects.addAll(objects);
/*     */   }
/*     */   
/*     */   void addLifecycleObjects(Lifecycle... objects) {
/* 154 */     this.lifecycleObjects.addAll(Arrays.asList(objects));
/*     */   }
/*     */   
/*     */   void setSessionManager(SessionManager sessionManager) {
/* 158 */     this.sessionManager = sessionManager;
/*     */   }
/*     */   
/*     */   public List<Lifecycle> getLifecycleObjects() {
/* 162 */     return Collections.unmodifiableList(this.lifecycleObjects);
/*     */   }
/*     */ 
/*     */   
/*     */   public ServletPathMatches getServletPaths() {
/* 167 */     return this.servletPaths;
/*     */   }
/*     */   
/*     */   void setThreadSetupActions(List<ThreadSetupHandler> threadSetupActions) {
/* 171 */     this.threadSetupActions = threadSetupActions;
/*     */   }
/*     */   
/*     */   public <C, T> ThreadSetupHandler.Action<C, T> createThreadSetupAction(ThreadSetupHandler.Action<C, T> target) {
/* 175 */     ThreadSetupHandler.Action<C, T> ret = target;
/* 176 */     for (ThreadSetupHandler wrapper : this.threadSetupActions) {
/* 177 */       ret = wrapper.create(ret);
/*     */     }
/* 179 */     return ret;
/*     */   }
/*     */   
/*     */   public ErrorPages getErrorPages() {
/* 183 */     return this.errorPages;
/*     */   }
/*     */   
/*     */   public void setErrorPages(ErrorPages errorPages) {
/* 187 */     this.errorPages = errorPages;
/*     */   }
/*     */ 
/*     */   
/*     */   public Map<String, String> getMimeExtensionMappings() {
/* 192 */     return this.mimeExtensionMappings;
/*     */   }
/*     */   
/*     */   public void setMimeExtensionMappings(Map<String, String> mimeExtensionMappings) {
/* 196 */     this.mimeExtensionMappings = Collections.unmodifiableMap(new HashMap<>(mimeExtensionMappings));
/*     */   }
/*     */ 
/*     */   
/*     */   public ServletDispatcher getServletDispatcher() {
/* 201 */     return (ServletDispatcher)this.servletHandler;
/*     */   }
/*     */ 
/*     */   
/*     */   public SessionManager getSessionManager() {
/* 206 */     return this.sessionManager;
/*     */   }
/*     */ 
/*     */   
/*     */   public Executor getExecutor() {
/* 211 */     return this.deploymentInfo.getExecutor();
/*     */   }
/*     */ 
/*     */   
/*     */   public Executor getAsyncExecutor() {
/* 216 */     return this.deploymentInfo.getAsyncExecutor();
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   public Charset getDefaultCharset() {
/* 221 */     return this.defaultCharset;
/*     */   }
/*     */ 
/*     */   
/*     */   public Charset getDefaultRequestCharset() {
/* 226 */     return this.defaultRequestCharset;
/*     */   }
/*     */ 
/*     */   
/*     */   public Charset getDefaultResponseCharset() {
/* 231 */     return this.defaultResponseCharset;
/*     */   }
/*     */   
/*     */   public void setAuthenticationMechanisms(List<AuthenticationMechanism> authenticationMechanisms) {
/* 235 */     this.authenticationMechanisms = authenticationMechanisms;
/*     */   }
/*     */ 
/*     */   
/*     */   public List<AuthenticationMechanism> getAuthenticationMechanisms() {
/* 240 */     return this.authenticationMechanisms;
/*     */   }
/*     */ 
/*     */   
/*     */   public DeploymentManager.State getDeploymentState() {
/* 245 */     return this.deploymentManager.getState();
/*     */   }
/*     */ 
/*     */   
/*     */   public Set<String> tryAddServletMappings(ServletInfo servletInfo, String... urlPatterns) {
/* 250 */     Set<String> ret = new HashSet<>();
/* 251 */     if (this.existingUrlPatterns == null) {
/* 252 */       this.existingUrlPatterns = new HashSet<>();
/* 253 */       for (ServletInfo s : this.deploymentInfo.getServlets().values()) {
/* 254 */         if (!s.getName().equals(servletInfo.getName())) {
/* 255 */           this.existingUrlPatterns.addAll(s.getMappings());
/*     */         }
/*     */       } 
/*     */     } 
/* 259 */     for (String pattern : urlPatterns) {
/* 260 */       if (this.existingUrlPatterns.contains(pattern)) {
/* 261 */         ret.add(pattern);
/*     */       }
/*     */     } 
/*     */     
/* 265 */     if (ret.isEmpty()) {
/* 266 */       for (String pattern : urlPatterns) {
/* 267 */         this.existingUrlPatterns.add(pattern);
/* 268 */         if (!servletInfo.getMappings().contains(pattern)) {
/* 269 */           servletInfo.addMapping(pattern);
/*     */         }
/*     */       } 
/*     */     }
/* 273 */     getServletPaths().invalidate();
/* 274 */     return ret;
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   public void setDefaultCharset(Charset defaultCharset) {
/* 279 */     this.defaultCharset = defaultCharset;
/*     */   }
/*     */   
/*     */   public void setDefaultRequestCharset(Charset defaultRequestCharset) {
/* 283 */     this.defaultRequestCharset = defaultRequestCharset;
/*     */   }
/*     */   
/*     */   public void setDefaultResponseCharset(Charset defaultResponseCharset) {
/* 287 */     this.defaultResponseCharset = defaultResponseCharset;
/*     */   }
/*     */   
/*     */   void destroy() {
/* 291 */     getApplicationListeners().contextDestroyed();
/* 292 */     getApplicationListeners().stop();
/* 293 */     if (this.servletContext != null) {
/* 294 */       this.servletContext.destroy();
/*     */     }
/* 296 */     this.servletContext = null;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\servlet\core\DeploymentImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */