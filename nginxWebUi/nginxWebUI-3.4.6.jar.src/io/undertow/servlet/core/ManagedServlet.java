/*     */ package io.undertow.servlet.core;
/*     */ 
/*     */ import io.undertow.server.handlers.form.FormEncodedDataDefinition;
/*     */ import io.undertow.server.handlers.form.FormParserFactory;
/*     */ import io.undertow.server.handlers.form.MultiPartParserDefinition;
/*     */ import io.undertow.server.handlers.resource.ResourceChangeListener;
/*     */ import io.undertow.server.handlers.resource.ResourceManager;
/*     */ import io.undertow.servlet.UndertowServletLogger;
/*     */ import io.undertow.servlet.UndertowServletMessages;
/*     */ import io.undertow.servlet.api.DeploymentInfo;
/*     */ import io.undertow.servlet.api.DeploymentManager;
/*     */ import io.undertow.servlet.api.InstanceFactory;
/*     */ import io.undertow.servlet.api.InstanceHandle;
/*     */ import io.undertow.servlet.api.LifecycleInterceptor;
/*     */ import io.undertow.servlet.api.ServletInfo;
/*     */ import io.undertow.servlet.spec.ServletConfigImpl;
/*     */ import io.undertow.servlet.spec.ServletContextImpl;
/*     */ import java.nio.file.Path;
/*     */ import java.nio.file.Paths;
/*     */ import java.util.Date;
/*     */ import java.util.List;
/*     */ import java.util.concurrent.atomic.AtomicLongFieldUpdater;
/*     */ import javax.servlet.MultipartConfigElement;
/*     */ import javax.servlet.Servlet;
/*     */ import javax.servlet.ServletConfig;
/*     */ import javax.servlet.ServletContext;
/*     */ import javax.servlet.ServletException;
/*     */ import javax.servlet.SingleThreadModel;
/*     */ import javax.servlet.UnavailableException;
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
/*     */ public class ManagedServlet
/*     */   implements Lifecycle
/*     */ {
/*     */   private final ServletInfo servletInfo;
/*     */   private final ServletContextImpl servletContext;
/*     */   private volatile boolean started = false;
/*     */   private final InstanceStrategy instanceStrategy;
/*     */   private volatile boolean permanentlyUnavailable = false;
/*     */   private long maxMultipartRequestSize;
/*     */   private FormParserFactory formParserFactory;
/*     */   private MultipartConfigElement multipartConfig;
/*  67 */   private static final AtomicLongFieldUpdater<ManagedServlet> unavailableUntilUpdater = AtomicLongFieldUpdater.newUpdater(ManagedServlet.class, "unavailableUntil");
/*     */   
/*  69 */   private volatile long unavailableUntil = 0L;
/*     */ 
/*     */   
/*     */   public ManagedServlet(ServletInfo servletInfo, ServletContextImpl servletContext) {
/*  73 */     this.servletInfo = servletInfo;
/*  74 */     this.servletContext = servletContext;
/*  75 */     if (SingleThreadModel.class.isAssignableFrom(servletInfo.getServletClass())) {
/*  76 */       this.instanceStrategy = new SingleThreadModelPoolStrategy(servletInfo.getInstanceFactory(), servletInfo, servletContext);
/*     */     } else {
/*  78 */       this.instanceStrategy = new DefaultInstanceStrategy(servletInfo.getInstanceFactory(), servletInfo, servletContext);
/*     */     } 
/*  80 */     setupMultipart(servletContext);
/*     */   }
/*     */ 
/*     */   
/*     */   public void setupMultipart(ServletContextImpl servletContext) {
/*  85 */     FormEncodedDataDefinition formDataParser = (new FormEncodedDataDefinition()).setDefaultEncoding(servletContext.getDeployment().getDefaultRequestCharset().name());
/*  86 */     MultipartConfigElement multipartConfig = this.servletInfo.getMultipartConfig();
/*  87 */     if (multipartConfig == null) {
/*  88 */       multipartConfig = servletContext.getDeployment().getDeploymentInfo().getDefaultMultipartConfig();
/*     */     }
/*  90 */     this.multipartConfig = multipartConfig;
/*  91 */     if (multipartConfig != null) {
/*     */       Path tempDir;
/*  93 */       MultipartConfigElement config = multipartConfig;
/*  94 */       if (config.getMaxRequestSize() != -1L) {
/*  95 */         this.maxMultipartRequestSize = config.getMaxRequestSize();
/*     */       } else {
/*  97 */         this.maxMultipartRequestSize = -1L;
/*     */       } 
/*     */       
/* 100 */       if (config.getLocation() == null || config.getLocation().isEmpty()) {
/* 101 */         tempDir = servletContext.getDeployment().getDeploymentInfo().getTempPath();
/*     */       } else {
/* 103 */         String location = config.getLocation();
/* 104 */         Path locFile = Paths.get(location, new String[0]);
/* 105 */         if (locFile.isAbsolute()) {
/* 106 */           tempDir = locFile;
/*     */         } else {
/* 108 */           DeploymentInfo deploymentInfo = servletContext.getDeployment().getDeploymentInfo();
/* 109 */           tempDir = deploymentInfo.requireTempPath().resolve(location);
/*     */         } 
/*     */       } 
/*     */       
/* 113 */       MultiPartParserDefinition multiPartParserDefinition = new MultiPartParserDefinition(tempDir);
/* 114 */       if (config.getMaxFileSize() > 0L) {
/* 115 */         multiPartParserDefinition.setMaxIndividualFileSize(config.getMaxFileSize());
/*     */       }
/* 117 */       if (config.getFileSizeThreshold() > 0) {
/* 118 */         multiPartParserDefinition.setFileSizeThreshold(config.getFileSizeThreshold());
/*     */       }
/* 120 */       multiPartParserDefinition.setDefaultEncoding(servletContext.getDeployment().getDefaultRequestCharset().name());
/*     */       
/* 122 */       this
/*     */ 
/*     */         
/* 125 */         .formParserFactory = FormParserFactory.builder(false).addParser((FormParserFactory.ParserDefinition)formDataParser).addParser((FormParserFactory.ParserDefinition)multiPartParserDefinition).build();
/*     */     }
/*     */     else {
/*     */       
/* 129 */       this.formParserFactory = FormParserFactory.builder(false).addParser((FormParserFactory.ParserDefinition)formDataParser).build();
/* 130 */       this.maxMultipartRequestSize = -1L;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized void start() throws ServletException {}
/*     */ 
/*     */   
/*     */   public void createServlet() throws ServletException {
/* 140 */     if (this.permanentlyUnavailable) {
/*     */       return;
/*     */     }
/*     */     try {
/* 144 */       if (!this.started && this.servletInfo.getLoadOnStartup() != null && this.servletInfo.getLoadOnStartup().intValue() >= 0) {
/* 145 */         this.instanceStrategy.start();
/* 146 */         this.started = true;
/*     */       } 
/* 148 */     } catch (UnavailableException e) {
/* 149 */       if (e.isPermanent()) {
/* 150 */         this.permanentlyUnavailable = true;
/* 151 */         stop();
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   public synchronized void stop() {
/* 157 */     if (this.started) {
/* 158 */       this.instanceStrategy.stop();
/*     */     }
/* 160 */     this.started = false;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isStarted() {
/* 165 */     return this.started;
/*     */   }
/*     */   
/*     */   public boolean isPermanentlyUnavailable() {
/* 169 */     return this.permanentlyUnavailable;
/*     */   }
/*     */   
/*     */   public boolean isTemporarilyUnavailable() {
/* 173 */     long until = this.unavailableUntil;
/* 174 */     if (until != 0L) {
/* 175 */       if (System.currentTimeMillis() < until) {
/* 176 */         return true;
/*     */       }
/* 178 */       unavailableUntilUpdater.compareAndSet(this, until, 0L);
/*     */     } 
/*     */     
/* 181 */     return false;
/*     */   }
/*     */   
/*     */   public void setPermanentlyUnavailable(boolean permanentlyUnavailable) {
/* 185 */     this.permanentlyUnavailable = permanentlyUnavailable;
/*     */   }
/*     */   
/*     */   public InstanceHandle<? extends Servlet> getServlet() throws ServletException {
/* 189 */     if (this.servletContext.getDeployment().getDeploymentState() != DeploymentManager.State.STARTED) {
/* 190 */       throw UndertowServletMessages.MESSAGES.deploymentStopped(this.servletContext.getDeployment().getDeploymentInfo().getDeploymentName());
/*     */     }
/* 192 */     if (!this.started) {
/* 193 */       synchronized (this) {
/* 194 */         if (!this.started) {
/* 195 */           this.instanceStrategy.start();
/* 196 */           this.started = true;
/*     */         } 
/*     */       } 
/*     */     }
/* 200 */     return this.instanceStrategy.getServlet();
/*     */   }
/*     */ 
/*     */   
/*     */   public void forceInit() throws ServletException {
/* 205 */     if (!this.started) {
/* 206 */       if (this.servletContext.getDeployment().getDeploymentState() != DeploymentManager.State.STARTED) {
/* 207 */         throw UndertowServletMessages.MESSAGES.deploymentStopped(this.servletContext.getDeployment().getDeploymentInfo().getDeploymentName());
/*     */       }
/* 209 */       synchronized (this) {
/* 210 */         if (!this.started) {
/*     */           try {
/* 212 */             this.instanceStrategy.start();
/* 213 */           } catch (UnavailableException e) {
/* 214 */             handleUnavailableException(e);
/*     */           } 
/* 216 */           this.started = true;
/*     */         } 
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   public void handleUnavailableException(UnavailableException e) {
/* 223 */     if (e.isPermanent()) {
/* 224 */       UndertowServletLogger.REQUEST_LOGGER.stoppingServletDueToPermanentUnavailability(getServletInfo().getName(), e);
/* 225 */       stop();
/* 226 */       setPermanentlyUnavailable(true);
/*     */     } else {
/* 228 */       long until = System.currentTimeMillis() + (e.getUnavailableSeconds() * 1000);
/* 229 */       unavailableUntilUpdater.set(this, until);
/* 230 */       UndertowServletLogger.REQUEST_LOGGER.stoppingServletUntilDueToTemporaryUnavailability(getServletInfo().getName(), new Date(until), e);
/*     */     } 
/*     */   }
/*     */   
/*     */   public ServletInfo getServletInfo() {
/* 235 */     return this.servletInfo;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long getMaxRequestSize() {
/* 243 */     return this.maxMultipartRequestSize;
/*     */   }
/*     */   
/*     */   public FormParserFactory getFormParserFactory() {
/* 247 */     return this.formParserFactory;
/*     */   }
/*     */   
/*     */   public MultipartConfigElement getMultipartConfig() {
/* 251 */     return this.multipartConfig;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 256 */     return "ManagedServlet{servletInfo=" + this.servletInfo + '}';
/*     */   }
/*     */ 
/*     */   
/*     */   static interface InstanceStrategy
/*     */   {
/*     */     void start() throws ServletException;
/*     */ 
/*     */     
/*     */     void stop();
/*     */ 
/*     */     
/*     */     InstanceHandle<? extends Servlet> getServlet() throws ServletException;
/*     */   }
/*     */ 
/*     */   
/*     */   private static class DefaultInstanceStrategy
/*     */     implements InstanceStrategy
/*     */   {
/*     */     private final InstanceFactory<? extends Servlet> factory;
/*     */     
/*     */     private final ServletInfo servletInfo;
/*     */     
/*     */     private final ServletContextImpl servletContext;
/*     */     private volatile InstanceHandle<? extends Servlet> handle;
/*     */     private volatile Servlet instance;
/*     */     private ResourceChangeListener changeListener;
/*     */     
/* 284 */     private final InstanceHandle<Servlet> instanceHandle = new InstanceHandle<Servlet>()
/*     */       {
/*     */         public Servlet getInstance() {
/* 287 */           return ManagedServlet.DefaultInstanceStrategy.this.instance;
/*     */         }
/*     */ 
/*     */ 
/*     */         
/*     */         public void release() {}
/*     */       };
/*     */ 
/*     */     
/*     */     DefaultInstanceStrategy(InstanceFactory<? extends Servlet> factory, ServletInfo servletInfo, ServletContextImpl servletContext) {
/* 297 */       this.factory = factory;
/* 298 */       this.servletInfo = servletInfo;
/* 299 */       this.servletContext = servletContext;
/*     */     }
/*     */     
/*     */     public synchronized void start() throws ServletException {
/*     */       try {
/* 304 */         this.handle = this.factory.createInstance();
/* 305 */       } catch (Exception e) {
/* 306 */         throw UndertowServletMessages.MESSAGES.couldNotInstantiateComponent(this.servletInfo.getName(), e);
/*     */       } 
/* 308 */       this.instance = (Servlet)this.handle.getInstance();
/* 309 */       (new LifecyleInterceptorInvocation(this.servletContext.getDeployment().getDeploymentInfo().getLifecycleInterceptors(), this.servletInfo, this.instance, (ServletConfig)new ServletConfigImpl(this.servletInfo, (ServletContext)this.servletContext))).proceed();
/*     */ 
/*     */       
/* 312 */       ResourceManager resourceManager = this.servletContext.getDeployment().getDeploymentInfo().getResourceManager();
/* 313 */       if (this.instance instanceof ResourceChangeListener && resourceManager.isResourceChangeListenerSupported()) {
/* 314 */         resourceManager.registerResourceChangeListener(this.changeListener = (ResourceChangeListener)this.instance);
/*     */       }
/*     */     }
/*     */     
/*     */     public synchronized void stop() {
/* 319 */       if (this.handle != null) {
/* 320 */         ResourceManager resourceManager = this.servletContext.getDeployment().getDeploymentInfo().getResourceManager();
/* 321 */         if (this.changeListener != null) {
/* 322 */           resourceManager.removeResourceChangeListener(this.changeListener);
/*     */         }
/* 324 */         invokeDestroy();
/* 325 */         this.handle.release();
/*     */       } 
/*     */     }
/*     */     
/*     */     private void invokeDestroy() {
/* 330 */       List<LifecycleInterceptor> interceptors = this.servletContext.getDeployment().getDeploymentInfo().getLifecycleInterceptors();
/*     */       try {
/* 332 */         (new LifecyleInterceptorInvocation(interceptors, this.servletInfo, this.instance)).proceed();
/* 333 */       } catch (Exception e) {
/* 334 */         UndertowServletLogger.ROOT_LOGGER.failedToDestroy(this.servletInfo, e);
/*     */       } 
/*     */     }
/*     */     
/*     */     public InstanceHandle<? extends Servlet> getServlet() {
/* 339 */       return this.instanceHandle;
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   private static class SingleThreadModelPoolStrategy
/*     */     implements InstanceStrategy
/*     */   {
/*     */     private final InstanceFactory<? extends Servlet> factory;
/*     */     
/*     */     private final ServletInfo servletInfo;
/*     */     
/*     */     private final ServletContextImpl servletContext;
/*     */     
/*     */     private SingleThreadModelPoolStrategy(InstanceFactory<? extends Servlet> factory, ServletInfo servletInfo, ServletContextImpl servletContext) {
/* 354 */       this.factory = factory;
/* 355 */       this.servletInfo = servletInfo;
/* 356 */       this.servletContext = servletContext;
/*     */     }
/*     */ 
/*     */     
/*     */     public void start() throws ServletException {
/* 361 */       if (this.servletInfo.getLoadOnStartup() != null)
/*     */       {
/* 363 */         getServlet().release();
/*     */       }
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public void stop() {}
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public InstanceHandle<? extends Servlet> getServlet() throws ServletException {
/*     */       final InstanceHandle<? extends Servlet> instanceHandle;
/*     */       try {
/* 378 */         instanceHandle = this.factory.createInstance();
/* 379 */       } catch (Exception e) {
/* 380 */         throw UndertowServletMessages.MESSAGES.couldNotInstantiateComponent(this.servletInfo.getName(), e);
/*     */       } 
/* 382 */       final Servlet instance = (Servlet)instanceHandle.getInstance();
/* 383 */       (new LifecyleInterceptorInvocation(this.servletContext.getDeployment().getDeploymentInfo().getLifecycleInterceptors(), this.servletInfo, instance, (ServletConfig)new ServletConfigImpl(this.servletInfo, (ServletContext)this.servletContext))).proceed();
/*     */       
/* 385 */       return new InstanceHandle<Servlet>()
/*     */         {
/*     */           public Servlet getInstance() {
/* 388 */             return instance;
/*     */           }
/*     */ 
/*     */           
/*     */           public void release() {
/*     */             try {
/* 394 */               instance.destroy();
/* 395 */             } catch (Throwable t) {
/* 396 */               UndertowServletLogger.REQUEST_LOGGER.failedToDestroy(instance, t);
/*     */             } 
/* 398 */             instanceHandle.release();
/*     */           }
/*     */         };
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\servlet\core\ManagedServlet.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */