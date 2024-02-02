/*      */ package io.undertow.servlet.spec;
/*      */ 
/*      */ import io.undertow.Version;
/*      */ import io.undertow.server.HandlerWrapper;
/*      */ import io.undertow.server.HttpHandler;
/*      */ import io.undertow.server.HttpServerExchange;
/*      */ import io.undertow.server.handlers.cache.LRUCache;
/*      */ import io.undertow.server.handlers.resource.Resource;
/*      */ import io.undertow.server.session.PathParameterSessionConfig;
/*      */ import io.undertow.server.session.Session;
/*      */ import io.undertow.server.session.SessionConfig;
/*      */ import io.undertow.server.session.SessionManager;
/*      */ import io.undertow.server.session.SslSessionConfig;
/*      */ import io.undertow.servlet.UndertowServletLogger;
/*      */ import io.undertow.servlet.UndertowServletMessages;
/*      */ import io.undertow.servlet.api.Deployment;
/*      */ import io.undertow.servlet.api.DeploymentInfo;
/*      */ import io.undertow.servlet.api.DeploymentManager;
/*      */ import io.undertow.servlet.api.FilterInfo;
/*      */ import io.undertow.servlet.api.HttpMethodSecurityInfo;
/*      */ import io.undertow.servlet.api.InstanceFactory;
/*      */ import io.undertow.servlet.api.ListenerInfo;
/*      */ import io.undertow.servlet.api.SecurityInfo;
/*      */ import io.undertow.servlet.api.ServletContainer;
/*      */ import io.undertow.servlet.api.ServletInfo;
/*      */ import io.undertow.servlet.api.ServletSecurityInfo;
/*      */ import io.undertow.servlet.api.SessionConfigWrapper;
/*      */ import io.undertow.servlet.api.ThreadSetupHandler;
/*      */ import io.undertow.servlet.api.TransportGuaranteeType;
/*      */ import io.undertow.servlet.core.ApplicationListeners;
/*      */ import io.undertow.servlet.core.ManagedListener;
/*      */ import io.undertow.servlet.core.ManagedServlet;
/*      */ import io.undertow.servlet.handlers.ServletChain;
/*      */ import io.undertow.servlet.handlers.ServletHandler;
/*      */ import io.undertow.servlet.handlers.ServletRequestContext;
/*      */ import io.undertow.servlet.util.EmptyEnumeration;
/*      */ import io.undertow.servlet.util.ImmediateInstanceFactory;
/*      */ import io.undertow.servlet.util.IteratorEnumeration;
/*      */ import io.undertow.util.AttachmentKey;
/*      */ import io.undertow.util.CanonicalPathUtils;
/*      */ import java.io.BufferedInputStream;
/*      */ import java.io.File;
/*      */ import java.io.FileInputStream;
/*      */ import java.io.FileNotFoundException;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.net.MalformedURLException;
/*      */ import java.net.URL;
/*      */ import java.nio.file.Files;
/*      */ import java.nio.file.Path;
/*      */ import java.security.AccessController;
/*      */ import java.security.PrivilegedAction;
/*      */ import java.util.Arrays;
/*      */ import java.util.Collections;
/*      */ import java.util.EnumSet;
/*      */ import java.util.Enumeration;
/*      */ import java.util.EventListener;
/*      */ import java.util.HashMap;
/*      */ import java.util.HashSet;
/*      */ import java.util.List;
/*      */ import java.util.Locale;
/*      */ import java.util.Map;
/*      */ import java.util.Set;
/*      */ import java.util.concurrent.ConcurrentHashMap;
/*      */ import java.util.concurrent.ConcurrentMap;
/*      */ import javax.annotation.security.DeclareRoles;
/*      */ import javax.annotation.security.RunAs;
/*      */ import javax.servlet.DispatcherType;
/*      */ import javax.servlet.Filter;
/*      */ import javax.servlet.FilterRegistration;
/*      */ import javax.servlet.MultipartConfigElement;
/*      */ import javax.servlet.ReadListener;
/*      */ import javax.servlet.RequestDispatcher;
/*      */ import javax.servlet.Servlet;
/*      */ import javax.servlet.ServletContext;
/*      */ import javax.servlet.ServletContextListener;
/*      */ import javax.servlet.ServletException;
/*      */ import javax.servlet.ServletRegistration;
/*      */ import javax.servlet.ServletRequest;
/*      */ import javax.servlet.SessionCookieConfig;
/*      */ import javax.servlet.SessionTrackingMode;
/*      */ import javax.servlet.WriteListener;
/*      */ import javax.servlet.annotation.HttpMethodConstraint;
/*      */ import javax.servlet.annotation.MultipartConfig;
/*      */ import javax.servlet.annotation.ServletSecurity;
/*      */ import javax.servlet.descriptor.JspConfigDescriptor;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class ServletContextImpl
/*      */   implements ServletContext
/*      */ {
/*      */   private final ServletContainer servletContainer;
/*      */   private final Deployment deployment;
/*      */   private volatile DeploymentInfo deploymentInfo;
/*      */   private final ConcurrentMap<String, Object> attributes;
/*      */   private final SessionCookieConfigImpl sessionCookieConfig;
/*  117 */   private final AttachmentKey<HttpSessionImpl> sessionAttachmentKey = AttachmentKey.create(HttpSessionImpl.class);
/*  118 */   private volatile Set<SessionTrackingMode> sessionTrackingModes = new HashSet<>(Arrays.asList(new SessionTrackingMode[] { SessionTrackingMode.COOKIE, SessionTrackingMode.URL }));
/*  119 */   private volatile Set<SessionTrackingMode> defaultSessionTrackingModes = new HashSet<>(Arrays.asList(new SessionTrackingMode[] { SessionTrackingMode.COOKIE, SessionTrackingMode.URL }));
/*      */   private volatile SessionConfig sessionConfig;
/*      */   private volatile boolean initialized = false;
/*  122 */   private int filterMappingUrlPatternInsertPosition = 0;
/*  123 */   private int filterMappingServletNameInsertPosition = 0;
/*      */   
/*      */   private final LRUCache<String, ContentTypeInfo> contentTypeCache;
/*      */   
/*      */   private volatile ThreadSetupHandler.Action<Void, WriteListener> onWritePossibleTask;
/*      */   
/*      */   private volatile ThreadSetupHandler.Action<Void, Runnable> runnableTask;
/*      */   private volatile ThreadSetupHandler.Action<Void, ReadListener> onDataAvailableTask;
/*      */   private volatile ThreadSetupHandler.Action<Void, ReadListener> onAllDataReadTask;
/*      */   private volatile ThreadSetupHandler.Action<Void, ThreadSetupHandler.Action<Void, Object>> invokeActionTask;
/*      */   private volatile int defaultSessionTimeout;
/*      */   
/*      */   public ServletContextImpl(ServletContainer servletContainer, Deployment deployment) {
/*  136 */     this.servletContainer = servletContainer;
/*  137 */     this.deployment = deployment;
/*  138 */     this.deploymentInfo = deployment.getDeploymentInfo();
/*  139 */     this.sessionCookieConfig = new SessionCookieConfigImpl(this);
/*  140 */     this.sessionCookieConfig.setPath(this.deploymentInfo.getContextPath());
/*  141 */     if (this.deploymentInfo.getServletContextAttributeBackingMap() == null) {
/*  142 */       this.attributes = new ConcurrentHashMap<>();
/*      */     } else {
/*  144 */       this.attributes = this.deploymentInfo.getServletContextAttributeBackingMap();
/*      */     } 
/*  146 */     this.attributes.putAll(deployment.getDeploymentInfo().getServletContextAttributes());
/*  147 */     this.contentTypeCache = new LRUCache(deployment.getDeploymentInfo().getContentTypeCacheSize(), -1, true);
/*  148 */     this.defaultSessionTimeout = this.deploymentInfo.getDefaultSessionTimeout() / 60;
/*      */   } public void initDone() {
/*      */     PathParameterSessionConfig pathParameterSessionConfig;
/*      */     SessionConfig sessionConfig1;
/*  152 */     this.initialized = true;
/*  153 */     Set<SessionTrackingMode> trackingMethods = this.sessionTrackingModes;
/*  154 */     SessionConfig sessionConfig = this.sessionCookieConfig;
/*  155 */     if (trackingMethods != null && !trackingMethods.isEmpty()) {
/*  156 */       if (this.sessionTrackingModes.contains(SessionTrackingMode.SSL)) {
/*  157 */         SslSessionConfig sslSessionConfig = new SslSessionConfig(this.deployment.getSessionManager());
/*      */       }
/*  159 */       else if (this.sessionTrackingModes.contains(SessionTrackingMode.COOKIE) && this.sessionTrackingModes.contains(SessionTrackingMode.URL)) {
/*  160 */         this.sessionCookieConfig.setFallback((SessionConfig)new PathParameterSessionConfig(this.sessionCookieConfig.getName().toLowerCase(Locale.ENGLISH)));
/*  161 */       } else if (this.sessionTrackingModes.contains(SessionTrackingMode.URL)) {
/*  162 */         pathParameterSessionConfig = new PathParameterSessionConfig(this.sessionCookieConfig.getName().toLowerCase(Locale.ENGLISH));
/*      */       } 
/*      */     }
/*      */     
/*  166 */     SessionConfigWrapper wrapper = this.deploymentInfo.getSessionConfigWrapper();
/*  167 */     if (wrapper != null) {
/*  168 */       sessionConfig1 = wrapper.wrap((SessionConfig)pathParameterSessionConfig, this.deployment);
/*      */     }
/*  170 */     this.sessionConfig = new ServletContextSessionConfig(sessionConfig1);
/*  171 */     this.onWritePossibleTask = this.deployment.createThreadSetupAction(new ThreadSetupHandler.Action<Void, WriteListener>()
/*      */         {
/*      */           public Void call(HttpServerExchange exchange, WriteListener context) throws Exception {
/*  174 */             context.onWritePossible();
/*  175 */             return null;
/*      */           }
/*      */         });
/*  178 */     this.runnableTask = this.deployment.createThreadSetupAction(new ThreadSetupHandler.Action<Void, Runnable>()
/*      */         {
/*      */           public Void call(HttpServerExchange exchange, Runnable runnable) throws Exception {
/*  181 */             runnable.run();
/*  182 */             return null;
/*      */           }
/*      */         });
/*  185 */     this.onDataAvailableTask = this.deployment.createThreadSetupAction(new ThreadSetupHandler.Action<Void, ReadListener>()
/*      */         {
/*      */           public Void call(HttpServerExchange exchange, ReadListener context) throws Exception {
/*  188 */             context.onDataAvailable();
/*  189 */             return null;
/*      */           }
/*      */         });
/*  192 */     this.onAllDataReadTask = this.deployment.createThreadSetupAction(new ThreadSetupHandler.Action<Void, ReadListener>()
/*      */         {
/*      */           public Void call(HttpServerExchange exchange, ReadListener context) throws Exception {
/*  195 */             context.onAllDataRead();
/*  196 */             return null;
/*      */           }
/*      */         });
/*  199 */     this.invokeActionTask = this.deployment.createThreadSetupAction(new ThreadSetupHandler.Action<Void, ThreadSetupHandler.Action<Void, Object>>()
/*      */         {
/*      */           public Void call(HttpServerExchange exchange, ThreadSetupHandler.Action<Void, Object> context) throws Exception {
/*  202 */             context.call(exchange, null);
/*  203 */             return null;
/*      */           }
/*      */         });
/*      */   }
/*      */   
/*      */   private DeploymentInfo getDeploymentInfo() {
/*  209 */     DeploymentInfo deploymentInfo = this.deploymentInfo;
/*  210 */     if (deploymentInfo == null)
/*  211 */       throw UndertowServletLogger.ROOT_LOGGER.contextDestroyed(); 
/*  212 */     return deploymentInfo;
/*      */   }
/*      */ 
/*      */   
/*      */   public String getContextPath() {
/*  217 */     String contextPath = getDeploymentInfo().getContextPath();
/*  218 */     if (contextPath.equals("/")) {
/*  219 */       return "";
/*      */     }
/*  221 */     return contextPath;
/*      */   }
/*      */ 
/*      */   
/*      */   public ServletContext getContext(String uripath) {
/*  226 */     DeploymentManager deploymentByPath = this.servletContainer.getDeploymentByPath(uripath);
/*  227 */     if (deploymentByPath == null) {
/*  228 */       return null;
/*      */     }
/*  230 */     return deploymentByPath.getDeployment().getServletContext();
/*      */   }
/*      */ 
/*      */   
/*      */   public int getMajorVersion() {
/*  235 */     return getDeploymentInfo().getContainerMajorVersion();
/*      */   }
/*      */ 
/*      */   
/*      */   public int getMinorVersion() {
/*  240 */     return getDeploymentInfo().getContainerMinorVersion();
/*      */   }
/*      */ 
/*      */   
/*      */   public int getEffectiveMajorVersion() {
/*  245 */     return getDeploymentInfo().getMajorVersion();
/*      */   }
/*      */ 
/*      */   
/*      */   public int getEffectiveMinorVersion() {
/*  250 */     return getDeploymentInfo().getMinorVersion();
/*      */   }
/*      */ 
/*      */   
/*      */   public String getMimeType(String file) {
/*  255 */     if (file == null) {
/*  256 */       return null;
/*      */     }
/*  258 */     String lower = file.toLowerCase(Locale.ENGLISH);
/*  259 */     int pos = lower.lastIndexOf('.');
/*  260 */     if (pos == -1) {
/*  261 */       return null;
/*      */     }
/*  263 */     return (String)this.deployment.getMimeExtensionMappings().get(lower.substring(pos + 1));
/*      */   }
/*      */ 
/*      */   
/*      */   public Set<String> getResourcePaths(String path) {
/*      */     Resource resource;
/*      */     try {
/*  270 */       resource = getDeploymentInfo().getResourceManager().getResource(path);
/*  271 */     } catch (IOException e) {
/*  272 */       return null;
/*      */     } 
/*  274 */     if (resource == null || !resource.isDirectory()) {
/*  275 */       return null;
/*      */     }
/*  277 */     Set<String> resources = new HashSet<>();
/*  278 */     for (Resource res : resource.list()) {
/*  279 */       Path file = res.getFilePath();
/*  280 */       if (file != null) {
/*  281 */         Path base = res.getResourceManagerRootPath();
/*  282 */         if (base == null) {
/*  283 */           resources.add(file.toString()); continue;
/*      */         } 
/*  285 */         String filePath = file.toAbsolutePath().toString().substring(base.toAbsolutePath().toString().length());
/*  286 */         filePath = filePath.replace('\\', '/');
/*  287 */         if (Files.isDirectory(file, new java.nio.file.LinkOption[0])) {
/*  288 */           filePath = filePath + "/";
/*      */         }
/*  290 */         resources.add(filePath);
/*      */       } 
/*      */     } 
/*      */     
/*  294 */     return resources;
/*      */   }
/*      */ 
/*      */   
/*      */   public URL getResource(String path) throws MalformedURLException {
/*  299 */     if (path == null || !path.startsWith("/")) {
/*  300 */       throw UndertowServletMessages.MESSAGES.pathMustStartWithSlash(path);
/*      */     }
/*  302 */     Resource resource = null;
/*      */     try {
/*  304 */       resource = getDeploymentInfo().getResourceManager().getResource(path);
/*  305 */     } catch (IOException e) {
/*  306 */       return null;
/*      */     } 
/*  308 */     if (resource == null) {
/*  309 */       return null;
/*      */     }
/*  311 */     return resource.getUrl();
/*      */   }
/*      */ 
/*      */   
/*      */   public InputStream getResourceAsStream(String path) {
/*  316 */     Resource resource = null;
/*      */     try {
/*  318 */       resource = getDeploymentInfo().getResourceManager().getResource(path);
/*  319 */     } catch (IOException e) {
/*  320 */       return null;
/*      */     } 
/*  322 */     if (resource == null) {
/*  323 */       return null;
/*      */     }
/*      */     try {
/*  326 */       if (resource.getFile() != null) {
/*  327 */         return new BufferedInputStream(new FileInputStream(resource.getFile()));
/*      */       }
/*  329 */       return new BufferedInputStream(resource.getUrl().openStream());
/*      */     }
/*  331 */     catch (FileNotFoundException e) {
/*      */       
/*  333 */       return null;
/*  334 */     } catch (IOException e) {
/*  335 */       return null;
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public RequestDispatcher getRequestDispatcher(String path) {
/*  341 */     if (path == null) {
/*  342 */       return null;
/*      */     }
/*  344 */     if (!path.startsWith("/")) {
/*  345 */       throw UndertowServletMessages.MESSAGES.pathMustStartWithSlashForRequestDispatcher(path);
/*      */     }
/*  347 */     String realPath = CanonicalPathUtils.canonicalize(path, true);
/*  348 */     if (realPath == null)
/*      */     {
/*  350 */       return null;
/*      */     }
/*  352 */     return new RequestDispatcherImpl(realPath, this);
/*      */   }
/*      */ 
/*      */   
/*      */   public RequestDispatcher getNamedDispatcher(String name) {
/*  357 */     ServletChain chain = this.deployment.getServletPaths().getServletHandlerByName(name);
/*  358 */     if (chain != null) {
/*  359 */       return new RequestDispatcherImpl(chain, this);
/*      */     }
/*  361 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public Servlet getServlet(String name) throws ServletException {
/*  367 */     return (Servlet)this.deployment.getServletPaths().getServletHandlerByName(name).getManagedServlet().getServlet().getInstance();
/*      */   }
/*      */ 
/*      */   
/*      */   public Enumeration<Servlet> getServlets() {
/*  372 */     return EmptyEnumeration.instance();
/*      */   }
/*      */ 
/*      */   
/*      */   public Enumeration<String> getServletNames() {
/*  377 */     return EmptyEnumeration.instance();
/*      */   }
/*      */ 
/*      */   
/*      */   public void log(String msg) {
/*  382 */     UndertowServletLogger.ROOT_LOGGER.info(msg);
/*      */   }
/*      */ 
/*      */   
/*      */   public void log(Exception exception, String msg) {
/*  387 */     UndertowServletLogger.ROOT_LOGGER.error(msg, exception);
/*      */   }
/*      */ 
/*      */   
/*      */   public void log(String message, Throwable throwable) {
/*  392 */     UndertowServletLogger.ROOT_LOGGER.error(message, throwable);
/*      */   }
/*      */   
/*      */   public String getRealPath(String path) {
/*      */     Resource resource;
/*  397 */     if (path == null) {
/*  398 */       return null;
/*      */     }
/*  400 */     DeploymentInfo deploymentInfo = getDeploymentInfo();
/*  401 */     String canonicalPath = CanonicalPathUtils.canonicalize(path);
/*      */     
/*      */     try {
/*  404 */       resource = deploymentInfo.getResourceManager().getResource(canonicalPath);
/*      */       
/*  406 */       if (resource == null) {
/*      */         
/*  408 */         Resource deploymentRoot = deploymentInfo.getResourceManager().getResource("/");
/*  409 */         if (deploymentRoot == null) {
/*  410 */           return null;
/*      */         }
/*  412 */         Path root = deploymentRoot.getFilePath();
/*  413 */         if (root == null) {
/*  414 */           return null;
/*      */         }
/*  416 */         if (!canonicalPath.startsWith("/")) {
/*  417 */           canonicalPath = "/" + canonicalPath;
/*      */         }
/*  419 */         if (File.separatorChar != '/') {
/*  420 */           canonicalPath = canonicalPath.replace('/', File.separatorChar);
/*      */         }
/*  422 */         return root.toAbsolutePath().toString() + canonicalPath;
/*      */       } 
/*  424 */     } catch (IOException e) {
/*  425 */       return null;
/*      */     } 
/*  427 */     Path file = resource.getFilePath();
/*  428 */     if (file == null) {
/*  429 */       return null;
/*      */     }
/*  431 */     return file.toAbsolutePath().toString();
/*      */   }
/*      */ 
/*      */   
/*      */   public String getServerInfo() {
/*  436 */     return getDeploymentInfo().getServerName() + " - " + Version.getVersionString();
/*      */   }
/*      */ 
/*      */   
/*      */   public String getInitParameter(String name) {
/*  441 */     if (name == null) {
/*  442 */       throw UndertowServletMessages.MESSAGES.nullName();
/*      */     }
/*  444 */     return (String)getDeploymentInfo().getInitParameters().get(name);
/*      */   }
/*      */ 
/*      */   
/*      */   public Enumeration<String> getInitParameterNames() {
/*  449 */     return (Enumeration<String>)new IteratorEnumeration(getDeploymentInfo().getInitParameters().keySet().iterator());
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean setInitParameter(String name, String value) {
/*  454 */     if (name == null) {
/*  455 */       throw UndertowServletMessages.MESSAGES.paramCannotBeNullNPE("name");
/*      */     }
/*  457 */     if (getDeploymentInfo().getInitParameters().containsKey(name)) {
/*  458 */       return false;
/*      */     }
/*  460 */     getDeploymentInfo().addInitParameter(name, value);
/*  461 */     return true;
/*      */   }
/*      */ 
/*      */   
/*      */   public Object getAttribute(String name) {
/*  466 */     if (name == null) {
/*  467 */       throw UndertowServletMessages.MESSAGES.nullName();
/*      */     }
/*  469 */     return this.attributes.get(name);
/*      */   }
/*      */ 
/*      */   
/*      */   public Enumeration<String> getAttributeNames() {
/*  474 */     return (Enumeration<String>)new IteratorEnumeration(this.attributes.keySet().iterator());
/*      */   }
/*      */ 
/*      */   
/*      */   public void setAttribute(String name, Object object) {
/*  479 */     if (name == null) {
/*  480 */       throw UndertowServletMessages.MESSAGES.nullName();
/*      */     }
/*  482 */     if (object == null) {
/*  483 */       Object existing = this.attributes.remove(name);
/*  484 */       if (this.deployment.getApplicationListeners() != null && 
/*  485 */         existing != null) {
/*  486 */         this.deployment.getApplicationListeners().servletContextAttributeRemoved(name, existing);
/*      */       }
/*      */     } else {
/*      */       
/*  490 */       Object existing = this.attributes.put(name, object);
/*  491 */       if (this.deployment.getApplicationListeners() != null) {
/*  492 */         if (existing != null) {
/*  493 */           this.deployment.getApplicationListeners().servletContextAttributeReplaced(name, existing);
/*      */         } else {
/*  495 */           this.deployment.getApplicationListeners().servletContextAttributeAdded(name, object);
/*      */         } 
/*      */       }
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public void removeAttribute(String name) {
/*  503 */     Object exiting = this.attributes.remove(name);
/*  504 */     this.deployment.getApplicationListeners().servletContextAttributeRemoved(name, exiting);
/*      */   }
/*      */ 
/*      */   
/*      */   public String getServletContextName() {
/*  509 */     return getDeploymentInfo().getDisplayName();
/*      */   }
/*      */ 
/*      */   
/*      */   public ServletRegistration.Dynamic addServlet(String servletName, String className) {
/*  514 */     return addServlet(servletName, className, Collections.emptyList());
/*      */   }
/*      */   
/*      */   public ServletRegistration.Dynamic addServlet(String servletName, String className, List<HandlerWrapper> wrappers) {
/*  518 */     ensureNotProgramaticListener();
/*  519 */     ensureNotInitialized();
/*  520 */     ensureServletNameNotNull(servletName);
/*      */     try {
/*  522 */       DeploymentInfo deploymentInfo = getDeploymentInfo();
/*  523 */       if (deploymentInfo.getServlets().containsKey(servletName)) {
/*  524 */         return null;
/*      */       }
/*  526 */       Class<? extends Servlet> servletClass = (Class)deploymentInfo.getClassLoader().loadClass(className);
/*  527 */       ServletInfo servlet = new ServletInfo(servletName, servletClass, deploymentInfo.getClassIntrospecter().createInstanceFactory(servletClass));
/*  528 */       for (HandlerWrapper i : wrappers) {
/*  529 */         servlet.addHandlerChainWrapper(i);
/*      */       }
/*  531 */       readServletAnnotations(servlet, deploymentInfo);
/*  532 */       deploymentInfo.addServlet(servlet);
/*  533 */       ServletHandler handler = this.deployment.getServlets().addServlet(servlet);
/*  534 */       return new ServletRegistrationImpl(servlet, handler.getManagedServlet(), this.deployment);
/*  535 */     } catch (ClassNotFoundException e) {
/*  536 */       throw UndertowServletMessages.MESSAGES.cannotLoadClass(className, e);
/*  537 */     } catch (NoSuchMethodException e) {
/*  538 */       throw UndertowServletMessages.MESSAGES.couldNotCreateFactory(className, e);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public ServletRegistration.Dynamic addServlet(String servletName, Servlet servlet) {
/*  544 */     ensureNotProgramaticListener();
/*  545 */     ensureNotInitialized();
/*  546 */     ensureServletNameNotNull(servletName);
/*  547 */     DeploymentInfo deploymentInfo = getDeploymentInfo();
/*  548 */     if (deploymentInfo.getServlets().containsKey(servletName)) {
/*  549 */       return null;
/*      */     }
/*  551 */     ServletInfo s = new ServletInfo(servletName, servlet.getClass(), (InstanceFactory)new ImmediateInstanceFactory(servlet));
/*  552 */     readServletAnnotations(s, deploymentInfo);
/*  553 */     deploymentInfo.addServlet(s);
/*  554 */     ServletHandler handler = this.deployment.getServlets().addServlet(s);
/*  555 */     return new ServletRegistrationImpl(s, handler.getManagedServlet(), this.deployment);
/*      */   }
/*      */ 
/*      */   
/*      */   public ServletRegistration.Dynamic addServlet(String servletName, Class<? extends Servlet> servletClass) {
/*  560 */     ensureNotProgramaticListener();
/*  561 */     ensureNotInitialized();
/*  562 */     ensureServletNameNotNull(servletName);
/*  563 */     DeploymentInfo deploymentInfo = getDeploymentInfo();
/*  564 */     if (deploymentInfo.getServlets().containsKey(servletName)) {
/*  565 */       return null;
/*      */     }
/*      */     try {
/*  568 */       ServletInfo servlet = new ServletInfo(servletName, servletClass, deploymentInfo.getClassIntrospecter().createInstanceFactory(servletClass));
/*  569 */       readServletAnnotations(servlet, deploymentInfo);
/*  570 */       deploymentInfo.addServlet(servlet);
/*  571 */       ServletHandler handler = this.deployment.getServlets().addServlet(servlet);
/*  572 */       return new ServletRegistrationImpl(servlet, handler.getManagedServlet(), this.deployment);
/*  573 */     } catch (NoSuchMethodException e) {
/*  574 */       throw UndertowServletMessages.MESSAGES.couldNotCreateFactory(servletClass.getName(), e);
/*      */     } 
/*      */   }
/*      */   
/*      */   private void ensureServletNameNotNull(String servletName) {
/*  579 */     if (servletName == null) {
/*  580 */       throw UndertowServletMessages.MESSAGES.servletNameNull();
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   public <T extends Servlet> T createServlet(Class<T> clazz) throws ServletException {
/*  586 */     ensureNotProgramaticListener();
/*      */     try {
/*  588 */       return (T)getDeploymentInfo().getClassIntrospecter().createInstanceFactory(clazz).createInstance().getInstance();
/*  589 */     } catch (Exception e) {
/*  590 */       throw UndertowServletMessages.MESSAGES.couldNotInstantiateComponent(clazz.getName(), e);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public ServletRegistration getServletRegistration(String servletName) {
/*  596 */     ensureNotProgramaticListener();
/*  597 */     ManagedServlet servlet = this.deployment.getServlets().getManagedServlet(servletName);
/*  598 */     if (servlet == null) {
/*  599 */       return null;
/*      */     }
/*  601 */     return new ServletRegistrationImpl(servlet.getServletInfo(), servlet, this.deployment);
/*      */   }
/*      */ 
/*      */   
/*      */   public Map<String, ? extends ServletRegistration> getServletRegistrations() {
/*  606 */     ensureNotProgramaticListener();
/*  607 */     Map<String, ServletRegistration> ret = new HashMap<>();
/*  608 */     for (Map.Entry<String, ServletHandler> entry : (Iterable<Map.Entry<String, ServletHandler>>)this.deployment.getServlets().getServletHandlers().entrySet()) {
/*  609 */       ret.put(entry.getKey(), new ServletRegistrationImpl(((ServletHandler)entry.getValue()).getManagedServlet().getServletInfo(), ((ServletHandler)entry.getValue()).getManagedServlet(), this.deployment));
/*      */     }
/*  611 */     return ret;
/*      */   }
/*      */ 
/*      */   
/*      */   public FilterRegistration.Dynamic addFilter(String filterName, String className) {
/*  616 */     ensureNotProgramaticListener();
/*  617 */     ensureNotInitialized();
/*  618 */     DeploymentInfo deploymentInfo = getDeploymentInfo();
/*  619 */     if (deploymentInfo.getFilters().containsKey(filterName)) {
/*  620 */       return null;
/*      */     }
/*      */     try {
/*  623 */       Class<? extends Filter> filterClass = (Class)deploymentInfo.getClassLoader().loadClass(className);
/*  624 */       FilterInfo filter = new FilterInfo(filterName, filterClass, deploymentInfo.getClassIntrospecter().createInstanceFactory(filterClass));
/*  625 */       deploymentInfo.addFilter(filter);
/*  626 */       this.deployment.getFilters().addFilter(filter);
/*  627 */       return new FilterRegistrationImpl(filter, this.deployment, this);
/*  628 */     } catch (ClassNotFoundException e) {
/*  629 */       throw UndertowServletMessages.MESSAGES.cannotLoadClass(className, e);
/*  630 */     } catch (NoSuchMethodException e) {
/*  631 */       throw UndertowServletMessages.MESSAGES.couldNotCreateFactory(className, e);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public FilterRegistration.Dynamic addFilter(String filterName, Filter filter) {
/*  637 */     ensureNotProgramaticListener();
/*  638 */     ensureNotInitialized();
/*  639 */     DeploymentInfo deploymentInfo = getDeploymentInfo();
/*  640 */     if (deploymentInfo.getFilters().containsKey(filterName)) {
/*  641 */       return null;
/*      */     }
/*  643 */     FilterInfo f = new FilterInfo(filterName, filter.getClass(), (InstanceFactory)new ImmediateInstanceFactory(filter));
/*  644 */     deploymentInfo.addFilter(f);
/*  645 */     this.deployment.getFilters().addFilter(f);
/*  646 */     return new FilterRegistrationImpl(f, this.deployment, this);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public FilterRegistration.Dynamic addFilter(String filterName, Class<? extends Filter> filterClass) {
/*  652 */     ensureNotProgramaticListener();
/*  653 */     ensureNotInitialized();
/*  654 */     DeploymentInfo deploymentInfo = getDeploymentInfo();
/*  655 */     if (deploymentInfo.getFilters().containsKey(filterName)) {
/*  656 */       return null;
/*      */     }
/*      */     try {
/*  659 */       FilterInfo filter = new FilterInfo(filterName, filterClass, deploymentInfo.getClassIntrospecter().createInstanceFactory(filterClass));
/*  660 */       deploymentInfo.addFilter(filter);
/*  661 */       this.deployment.getFilters().addFilter(filter);
/*  662 */       return new FilterRegistrationImpl(filter, this.deployment, this);
/*  663 */     } catch (NoSuchMethodException e) {
/*  664 */       throw UndertowServletMessages.MESSAGES.couldNotCreateFactory(filterClass.getName(), e);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public <T extends Filter> T createFilter(Class<T> clazz) throws ServletException {
/*  670 */     ensureNotProgramaticListener();
/*      */     try {
/*  672 */       return (T)getDeploymentInfo().getClassIntrospecter().createInstanceFactory(clazz).createInstance().getInstance();
/*  673 */     } catch (Exception e) {
/*  674 */       throw UndertowServletMessages.MESSAGES.couldNotInstantiateComponent(clazz.getName(), e);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public FilterRegistration getFilterRegistration(String filterName) {
/*  680 */     ensureNotProgramaticListener();
/*  681 */     FilterInfo filterInfo = (FilterInfo)getDeploymentInfo().getFilters().get(filterName);
/*  682 */     if (filterInfo == null) {
/*  683 */       return null;
/*      */     }
/*  685 */     return new FilterRegistrationImpl(filterInfo, this.deployment, this);
/*      */   }
/*      */ 
/*      */   
/*      */   public Map<String, ? extends FilterRegistration> getFilterRegistrations() {
/*  690 */     ensureNotProgramaticListener();
/*  691 */     Map<String, FilterRegistration> ret = new HashMap<>();
/*  692 */     for (Map.Entry<String, FilterInfo> entry : (Iterable<Map.Entry<String, FilterInfo>>)getDeploymentInfo().getFilters().entrySet()) {
/*  693 */       ret.put(entry.getKey(), new FilterRegistrationImpl(entry.getValue(), this.deployment, this));
/*      */     }
/*  695 */     return ret;
/*      */   }
/*      */ 
/*      */   
/*      */   public SessionCookieConfigImpl getSessionCookieConfig() {
/*  700 */     ensureNotProgramaticListener();
/*  701 */     return this.sessionCookieConfig;
/*      */   }
/*      */ 
/*      */   
/*      */   public void setSessionTrackingModes(Set<SessionTrackingMode> sessionTrackingModes) {
/*  706 */     ensureNotProgramaticListener();
/*  707 */     ensureNotInitialized();
/*  708 */     if (sessionTrackingModes.size() > 1 && sessionTrackingModes.contains(SessionTrackingMode.SSL)) {
/*  709 */       throw UndertowServletMessages.MESSAGES.sslCannotBeCombinedWithAnyOtherMethod();
/*      */     }
/*  711 */     this.sessionTrackingModes = new HashSet<>(sessionTrackingModes);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public Set<SessionTrackingMode> getDefaultSessionTrackingModes() {
/*  717 */     ensureNotProgramaticListener();
/*  718 */     return this.defaultSessionTrackingModes;
/*      */   }
/*      */ 
/*      */   
/*      */   public Set<SessionTrackingMode> getEffectiveSessionTrackingModes() {
/*  723 */     ensureNotProgramaticListener();
/*  724 */     return Collections.unmodifiableSet(this.sessionTrackingModes);
/*      */   }
/*      */ 
/*      */   
/*      */   public void addListener(String className) {
/*      */     try {
/*  730 */       Class<? extends EventListener> clazz = (Class)getDeploymentInfo().getClassLoader().loadClass(className);
/*  731 */       addListener(clazz);
/*  732 */     } catch (ClassNotFoundException e) {
/*  733 */       throw new IllegalArgumentException(e);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public <T extends EventListener> void addListener(T t) {
/*  739 */     ensureNotInitialized();
/*  740 */     ensureNotProgramaticListener();
/*  741 */     if (ApplicationListeners.listenerState() != ApplicationListeners.ListenerState.NO_LISTENER && ServletContextListener.class
/*  742 */       .isAssignableFrom(t.getClass())) {
/*  743 */       throw UndertowServletMessages.MESSAGES.cannotAddServletContextListener();
/*      */     }
/*  745 */     ListenerInfo listener = new ListenerInfo(t.getClass(), (InstanceFactory)new ImmediateInstanceFactory(t));
/*  746 */     getDeploymentInfo().addListener(listener);
/*  747 */     this.deployment.getApplicationListeners().addListener(new ManagedListener(listener, true));
/*      */   }
/*      */ 
/*      */   
/*      */   public void addListener(Class<? extends EventListener> listenerClass) {
/*  752 */     ensureNotInitialized();
/*  753 */     ensureNotProgramaticListener();
/*  754 */     if (ApplicationListeners.listenerState() != ApplicationListeners.ListenerState.NO_LISTENER && ServletContextListener.class
/*  755 */       .isAssignableFrom(listenerClass)) {
/*  756 */       throw UndertowServletMessages.MESSAGES.cannotAddServletContextListener();
/*      */     }
/*  758 */     DeploymentInfo deploymentInfo = getDeploymentInfo();
/*  759 */     InstanceFactory<? extends EventListener> factory = null;
/*      */     try {
/*  761 */       factory = deploymentInfo.getClassIntrospecter().createInstanceFactory(listenerClass);
/*  762 */     } catch (Exception e) {
/*  763 */       throw new IllegalArgumentException(e);
/*      */     } 
/*  765 */     ListenerInfo listener = new ListenerInfo(listenerClass, factory);
/*  766 */     deploymentInfo.addListener(listener);
/*  767 */     this.deployment.getApplicationListeners().addListener(new ManagedListener(listener, true));
/*      */   }
/*      */ 
/*      */   
/*      */   public <T extends EventListener> T createListener(Class<T> clazz) throws ServletException {
/*  772 */     ensureNotProgramaticListener();
/*  773 */     if (!ApplicationListeners.isListenerClass(clazz)) {
/*  774 */       throw UndertowServletMessages.MESSAGES.listenerMustImplementListenerClass(clazz);
/*      */     }
/*      */     try {
/*  777 */       return (T)getDeploymentInfo().getClassIntrospecter().createInstanceFactory(clazz).createInstance().getInstance();
/*  778 */     } catch (Exception e) {
/*  779 */       throw UndertowServletMessages.MESSAGES.couldNotInstantiateComponent(clazz.getName(), e);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public JspConfigDescriptor getJspConfigDescriptor() {
/*  785 */     return getDeploymentInfo().getJspConfigDescriptor();
/*      */   }
/*      */ 
/*      */   
/*      */   public ClassLoader getClassLoader() {
/*  790 */     return getDeploymentInfo().getClassLoader();
/*      */   }
/*      */ 
/*      */   
/*      */   public void declareRoles(String... roleNames) {
/*  795 */     DeploymentInfo di = getDeploymentInfo();
/*  796 */     if (isInitialized()) {
/*  797 */       throw UndertowServletMessages.MESSAGES.servletAlreadyInitialize(di.getDeploymentName(), di.getContextPath());
/*      */     }
/*      */     
/*  800 */     for (String role : roleNames) {
/*  801 */       if (role == null || role.isEmpty()) {
/*  802 */         throw UndertowServletMessages.MESSAGES.roleMustNotBeEmpty(di.getDeploymentName(), di.getContextPath());
/*      */       }
/*      */     } 
/*      */     
/*  806 */     if (ApplicationListeners.listenerState() == ApplicationListeners.ListenerState.PROGRAMATIC_LISTENER)
/*      */     {
/*  808 */       throw UndertowServletMessages.MESSAGES.cantCallFromDynamicListener(di.getDeploymentName(), di.getContextPath());
/*      */     }
/*      */     
/*  811 */     this.deploymentInfo.addSecurityRoles(roleNames);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public ServletRegistration.Dynamic addJspFile(String servletName, String jspFile) {
/*  817 */     if (servletName == null || servletName.isEmpty()) {
/*  818 */       throw UndertowServletMessages.MESSAGES.paramCannotBeNull("servletName");
/*      */     }
/*  820 */     return addServlet(servletName, "org.apache.jasper.servlet.JspServlet", Collections.singletonList(handler -> ()));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getSessionTimeout() {
/*  829 */     return this.defaultSessionTimeout;
/*      */   }
/*      */ 
/*      */   
/*      */   public void setSessionTimeout(int sessionTimeout) {
/*  834 */     ensureNotInitialized();
/*  835 */     ensureNotProgramaticListener();
/*  836 */     this.defaultSessionTimeout = sessionTimeout;
/*  837 */     this.deployment.getSessionManager().setDefaultSessionTimeout(sessionTimeout * 60);
/*      */   }
/*      */ 
/*      */   
/*      */   public String getRequestCharacterEncoding() {
/*  842 */     DeploymentInfo deploymentInfo = getDeploymentInfo();
/*  843 */     String enconding = deploymentInfo.getDefaultRequestEncoding();
/*  844 */     if (enconding != null) {
/*  845 */       return enconding;
/*      */     }
/*  847 */     return deploymentInfo.getDefaultEncoding();
/*      */   }
/*      */ 
/*      */   
/*      */   public void setRequestCharacterEncoding(String encoding) {
/*  852 */     ensureNotInitialized();
/*  853 */     ensureNotProgramaticListener();
/*  854 */     getDeploymentInfo().setDefaultRequestEncoding(encoding);
/*      */   }
/*      */ 
/*      */   
/*      */   public String getResponseCharacterEncoding() {
/*  859 */     DeploymentInfo deploymentInfo = getDeploymentInfo();
/*  860 */     String enconding = deploymentInfo.getDefaultResponseEncoding();
/*  861 */     if (enconding != null) {
/*  862 */       return enconding;
/*      */     }
/*  864 */     return deploymentInfo.getDefaultEncoding();
/*      */   }
/*      */ 
/*      */   
/*      */   public void setResponseCharacterEncoding(String encoding) {
/*  869 */     ensureNotInitialized();
/*  870 */     ensureNotProgramaticListener();
/*  871 */     getDeploymentInfo().setDefaultResponseEncoding(encoding);
/*      */   }
/*      */ 
/*      */   
/*      */   public String getVirtualServerName() {
/*  876 */     return this.deployment.getDeploymentInfo().getHostName();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public HttpSessionImpl getSession(String sessionId) {
/*  886 */     SessionManager sessionManager = this.deployment.getSessionManager();
/*  887 */     Session session = sessionManager.getSession(sessionId);
/*  888 */     if (session != null) {
/*  889 */       return SecurityActions.forSession(session, this, false);
/*      */     }
/*  891 */     return null;
/*      */   }
/*      */   
/*      */   public HttpSessionImpl getSession(ServletContextImpl originalServletContext, HttpServerExchange exchange, boolean create) {
/*  895 */     SessionConfig c = originalServletContext.getSessionConfig();
/*  896 */     HttpSessionImpl httpSession = (HttpSessionImpl)exchange.getAttachment(this.sessionAttachmentKey);
/*  897 */     if (httpSession != null && httpSession.isInvalid()) {
/*  898 */       exchange.removeAttachment(this.sessionAttachmentKey);
/*  899 */       httpSession = null;
/*      */     } 
/*  901 */     if (httpSession == null) {
/*  902 */       SessionManager sessionManager = this.deployment.getSessionManager();
/*  903 */       Session session = sessionManager.getSession(exchange, c);
/*  904 */       if (session != null) {
/*  905 */         httpSession = SecurityActions.forSession(session, this, false);
/*  906 */         exchange.putAttachment(this.sessionAttachmentKey, httpSession);
/*  907 */       } else if (create) {
/*      */         
/*  909 */         String existing = c.findSessionId(exchange);
/*  910 */         Boolean isRequestedSessionIdSaved = (Boolean)exchange.getAttachment(HttpServletRequestImpl.REQUESTED_SESSION_ID_SET);
/*  911 */         if (isRequestedSessionIdSaved == null || !isRequestedSessionIdSaved.booleanValue()) {
/*  912 */           exchange.putAttachment(HttpServletRequestImpl.REQUESTED_SESSION_ID_SET, Boolean.TRUE);
/*  913 */           exchange.putAttachment(HttpServletRequestImpl.REQUESTED_SESSION_ID, existing);
/*      */         } 
/*      */         
/*  916 */         if (originalServletContext != this) {
/*      */ 
/*      */           
/*  919 */           final HttpSessionImpl topLevel = originalServletContext.getSession(originalServletContext, exchange, true);
/*      */ 
/*      */           
/*  922 */           c = new SessionConfig()
/*      */             {
/*      */               public void setSessionId(HttpServerExchange exchange, String sessionId) {}
/*      */ 
/*      */ 
/*      */ 
/*      */               
/*      */               public void clearSession(HttpServerExchange exchange, String sessionId) {}
/*      */ 
/*      */ 
/*      */ 
/*      */               
/*      */               public String findSessionId(HttpServerExchange exchange) {
/*  935 */                 return topLevel.getId();
/*      */               }
/*      */ 
/*      */               
/*      */               public SessionConfig.SessionCookieSource sessionCookieSource(HttpServerExchange exchange) {
/*  940 */                 return SessionConfig.SessionCookieSource.NONE;
/*      */               }
/*      */ 
/*      */               
/*      */               public String rewriteUrl(String originalUrl, String sessionId) {
/*  945 */                 return null;
/*      */               }
/*      */             };
/*      */ 
/*      */ 
/*      */           
/*  951 */           session = sessionManager.getSession(exchange, c);
/*  952 */           if (session != null) {
/*  953 */             httpSession = SecurityActions.forSession(session, this, false);
/*  954 */             exchange.putAttachment(this.sessionAttachmentKey, httpSession);
/*      */           } 
/*  956 */         } else if (existing != null) {
/*  957 */           if (getDeploymentInfo().isCheckOtherSessionManagers()) {
/*  958 */             boolean found = false;
/*  959 */             for (String deploymentName : this.deployment.getServletContainer().listDeployments()) {
/*  960 */               DeploymentManager deployment = this.deployment.getServletContainer().getDeployment(deploymentName);
/*  961 */               if (deployment != null && 
/*  962 */                 deployment.getDeployment().getSessionManager().getSession(existing) != null) {
/*  963 */                 found = true;
/*      */                 
/*      */                 break;
/*      */               } 
/*      */             } 
/*  968 */             if (!found) {
/*  969 */               c.clearSession(exchange, existing);
/*      */             }
/*      */           } else {
/*  972 */             c.clearSession(exchange, existing);
/*      */           } 
/*      */         } 
/*      */         
/*  976 */         if (httpSession == null) {
/*  977 */           Session newSession = sessionManager.createSession(exchange, c);
/*  978 */           httpSession = SecurityActions.forSession(newSession, this, true);
/*  979 */           exchange.putAttachment(this.sessionAttachmentKey, httpSession);
/*      */         } 
/*      */       } 
/*      */     } 
/*  983 */     return httpSession;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public HttpSessionImpl getSession(HttpServerExchange exchange, boolean create) {
/*  993 */     return getSession(this, exchange, create);
/*      */   }
/*      */   
/*      */   public void updateSessionAccessTime(HttpServerExchange exchange) {
/*  997 */     HttpSessionImpl httpSession = getSession(exchange, false);
/*  998 */     if (httpSession != null) {
/*      */       Session underlyingSession;
/* 1000 */       if (System.getSecurityManager() == null) {
/* 1001 */         underlyingSession = httpSession.getSession();
/*      */       } else {
/* 1003 */         underlyingSession = AccessController.<Session>doPrivileged(new HttpSessionImpl.UnwrapSessionAction(httpSession));
/*      */       } 
/* 1005 */       underlyingSession.requestDone(exchange);
/*      */     } 
/*      */   }
/*      */   
/*      */   public Deployment getDeployment() {
/* 1010 */     return this.deployment;
/*      */   }
/*      */   
/*      */   private void ensureNotInitialized() {
/* 1014 */     if (this.initialized) {
/* 1015 */       throw UndertowServletMessages.MESSAGES.servletContextAlreadyInitialized();
/*      */     }
/*      */   }
/*      */   
/*      */   private void ensureNotProgramaticListener() {
/* 1020 */     if (ApplicationListeners.listenerState() == ApplicationListeners.ListenerState.PROGRAMATIC_LISTENER) {
/* 1021 */       throw UndertowServletMessages.MESSAGES.cannotCallFromProgramaticListener();
/*      */     }
/*      */   }
/*      */   
/*      */   boolean isInitialized() {
/* 1026 */     return this.initialized;
/*      */   }
/*      */   
/*      */   public SessionConfig getSessionConfig() {
/* 1030 */     return this.sessionConfig;
/*      */   }
/*      */   
/*      */   public void destroy() {
/* 1034 */     this.attributes.clear();
/* 1035 */     this.deploymentInfo = null;
/*      */   }
/*      */   
/*      */   private void readServletAnnotations(ServletInfo servlet, DeploymentInfo deploymentInfo) {
/* 1039 */     if (System.getSecurityManager() == null) {
/* 1040 */       (new ReadServletAnnotationsTask(servlet, deploymentInfo)).run();
/*      */     } else {
/* 1042 */       AccessController.doPrivileged(new ReadServletAnnotationsTask(servlet, deploymentInfo));
/*      */     } 
/*      */   }
/*      */   
/*      */   public void setDefaultSessionTrackingModes(HashSet<SessionTrackingMode> sessionTrackingModes) {
/* 1047 */     this.defaultSessionTrackingModes = sessionTrackingModes;
/* 1048 */     this.sessionTrackingModes = sessionTrackingModes;
/*      */   }
/*      */   
/*      */   void invokeOnWritePossible(HttpServerExchange exchange, WriteListener listener) {
/*      */     try {
/* 1053 */       this.onWritePossibleTask.call(exchange, listener);
/* 1054 */     } catch (Exception e) {
/* 1055 */       throw new RuntimeException(e);
/*      */     } 
/*      */   }
/*      */   
/*      */   void invokeOnAllDataRead(HttpServerExchange exchange, ReadListener listener) {
/*      */     try {
/* 1061 */       this.onAllDataReadTask.call(exchange, listener);
/* 1062 */     } catch (Exception e) {
/* 1063 */       throw new RuntimeException(e);
/*      */     } 
/*      */   }
/*      */   
/*      */   void invokeOnDataAvailable(HttpServerExchange exchange, ReadListener listener) {
/*      */     try {
/* 1069 */       this.onDataAvailableTask.call(exchange, listener);
/* 1070 */     } catch (Exception e) {
/* 1071 */       throw new RuntimeException(e);
/*      */     } 
/*      */   }
/*      */   
/*      */   void invokeAction(HttpServerExchange exchange, ThreadSetupHandler.Action<Void, Object> listener) {
/*      */     try {
/* 1077 */       this.invokeActionTask.call(exchange, listener);
/* 1078 */     } catch (Exception e) {
/* 1079 */       throw new RuntimeException(e);
/*      */     } 
/*      */   }
/*      */   
/*      */   void invokeRunnable(HttpServerExchange exchange, Runnable runnable) {
/* 1084 */     boolean setupRequired = (SecurityActions.currentServletRequestContext() == null);
/* 1085 */     if (setupRequired) {
/*      */       try {
/* 1087 */         this.runnableTask.call(exchange, runnable);
/* 1088 */       } catch (Exception e) {
/* 1089 */         throw new RuntimeException(e);
/*      */       } 
/*      */     } else {
/* 1092 */       runnable.run();
/*      */     } 
/*      */   }
/*      */   
/*      */   private static final class ReadServletAnnotationsTask implements PrivilegedAction<Void> {
/*      */     private final ServletInfo servletInfo;
/*      */     private final DeploymentInfo deploymentInfo;
/*      */     
/*      */     private ReadServletAnnotationsTask(ServletInfo servletInfo, DeploymentInfo deploymentInfo) {
/* 1101 */       this.servletInfo = servletInfo;
/* 1102 */       this.deploymentInfo = deploymentInfo;
/*      */     }
/*      */ 
/*      */     
/*      */     public Void run() {
/* 1107 */       ServletSecurity security = (ServletSecurity)this.servletInfo.getServletClass().getAnnotation(ServletSecurity.class);
/* 1108 */       if (security != null) {
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 1113 */         ServletSecurityInfo servletSecurityInfo = (ServletSecurityInfo)((ServletSecurityInfo)((ServletSecurityInfo)(new ServletSecurityInfo()).setEmptyRoleSemantic((security.value().value() == ServletSecurity.EmptyRoleSemantic.DENY) ? SecurityInfo.EmptyRoleSemantic.DENY : SecurityInfo.EmptyRoleSemantic.PERMIT)).setTransportGuaranteeType((security.value().transportGuarantee() == ServletSecurity.TransportGuarantee.CONFIDENTIAL) ? TransportGuaranteeType.CONFIDENTIAL : TransportGuaranteeType.NONE)).addRolesAllowed(security.value().rolesAllowed());
/* 1114 */         for (HttpMethodConstraint constraint : security.httpMethodConstraints()) {
/* 1115 */           ((ServletSecurityInfo)((ServletSecurityInfo)servletSecurityInfo.addHttpMethodSecurityInfo((new HttpMethodSecurityInfo())
/* 1116 */               .setMethod(constraint.value()))
/* 1117 */             .setEmptyRoleSemantic((constraint.emptyRoleSemantic() == ServletSecurity.EmptyRoleSemantic.DENY) ? SecurityInfo.EmptyRoleSemantic.DENY : SecurityInfo.EmptyRoleSemantic.PERMIT))
/* 1118 */             .setTransportGuaranteeType((constraint.transportGuarantee() == ServletSecurity.TransportGuarantee.CONFIDENTIAL) ? TransportGuaranteeType.CONFIDENTIAL : TransportGuaranteeType.NONE))
/* 1119 */             .addRolesAllowed(constraint.rolesAllowed());
/*      */         }
/* 1121 */         this.servletInfo.setServletSecurityInfo(servletSecurityInfo);
/*      */       } 
/* 1123 */       MultipartConfig multipartConfig = (MultipartConfig)this.servletInfo.getServletClass().getAnnotation(MultipartConfig.class);
/* 1124 */       if (multipartConfig != null) {
/* 1125 */         this.servletInfo.setMultipartConfig(new MultipartConfigElement(multipartConfig.location(), multipartConfig.maxFileSize(), multipartConfig.maxRequestSize(), multipartConfig.fileSizeThreshold()));
/*      */       }
/* 1127 */       RunAs runAs = (RunAs)this.servletInfo.getServletClass().getAnnotation(RunAs.class);
/* 1128 */       if (runAs != null) {
/* 1129 */         this.servletInfo.setRunAs(runAs.value());
/*      */       }
/* 1131 */       DeclareRoles declareRoles = (DeclareRoles)this.servletInfo.getServletClass().getAnnotation(DeclareRoles.class);
/* 1132 */       if (declareRoles != null) {
/* 1133 */         this.deploymentInfo.addSecurityRoles(declareRoles.value());
/*      */       }
/* 1135 */       return null;
/*      */     }
/*      */   }
/*      */   
/*      */   void addMappingForServletNames(FilterInfo filterInfo, EnumSet<DispatcherType> dispatcherTypes, boolean isMatchAfter, String... servletNames) {
/* 1140 */     DeploymentInfo deploymentInfo = this.deployment.getDeploymentInfo();
/*      */     
/* 1142 */     for (String servlet : servletNames) {
/* 1143 */       if (isMatchAfter) {
/* 1144 */         if (dispatcherTypes == null || dispatcherTypes.isEmpty()) {
/* 1145 */           deploymentInfo.addFilterServletNameMapping(filterInfo.getName(), servlet, DispatcherType.REQUEST);
/*      */         } else {
/* 1147 */           for (DispatcherType dispatcher : dispatcherTypes) {
/* 1148 */             deploymentInfo.addFilterServletNameMapping(filterInfo.getName(), servlet, dispatcher);
/*      */           }
/*      */         }
/*      */       
/* 1152 */       } else if (dispatcherTypes == null || dispatcherTypes.isEmpty()) {
/* 1153 */         deploymentInfo.insertFilterServletNameMapping(this.filterMappingServletNameInsertPosition++, filterInfo.getName(), servlet, DispatcherType.REQUEST);
/*      */       } else {
/* 1155 */         for (DispatcherType dispatcher : dispatcherTypes) {
/* 1156 */           deploymentInfo.insertFilterServletNameMapping(this.filterMappingServletNameInsertPosition++, filterInfo.getName(), servlet, dispatcher);
/*      */         }
/*      */       } 
/*      */     } 
/*      */     
/* 1161 */     this.deployment.getServletPaths().invalidate();
/*      */   }
/*      */   
/*      */   void addMappingForUrlPatterns(FilterInfo filterInfo, EnumSet<DispatcherType> dispatcherTypes, boolean isMatchAfter, String... urlPatterns) {
/* 1165 */     DeploymentInfo deploymentInfo = this.deployment.getDeploymentInfo();
/* 1166 */     for (String url : urlPatterns) {
/* 1167 */       if (isMatchAfter) {
/* 1168 */         if (dispatcherTypes == null || dispatcherTypes.isEmpty()) {
/* 1169 */           deploymentInfo.addFilterUrlMapping(filterInfo.getName(), url, DispatcherType.REQUEST);
/*      */         } else {
/* 1171 */           for (DispatcherType dispatcher : dispatcherTypes) {
/* 1172 */             deploymentInfo.addFilterUrlMapping(filterInfo.getName(), url, dispatcher);
/*      */           }
/*      */         }
/*      */       
/* 1176 */       } else if (dispatcherTypes == null || dispatcherTypes.isEmpty()) {
/* 1177 */         deploymentInfo.insertFilterUrlMapping(this.filterMappingUrlPatternInsertPosition++, filterInfo.getName(), url, DispatcherType.REQUEST);
/*      */       } else {
/* 1179 */         for (DispatcherType dispatcher : dispatcherTypes) {
/* 1180 */           deploymentInfo.insertFilterUrlMapping(this.filterMappingUrlPatternInsertPosition++, filterInfo.getName(), url, dispatcher);
/*      */         }
/*      */       } 
/*      */     } 
/*      */     
/* 1185 */     this.deployment.getServletPaths().invalidate();
/*      */   }
/*      */   
/*      */   ContentTypeInfo parseContentType(String type) {
/* 1189 */     ContentTypeInfo existing = (ContentTypeInfo)this.contentTypeCache.get(type);
/* 1190 */     if (existing != null) {
/* 1191 */       return existing;
/*      */     }
/* 1193 */     String contentType = type;
/* 1194 */     String charset = null;
/*      */     
/* 1196 */     int split = type.indexOf(";");
/* 1197 */     if (split != -1) {
/* 1198 */       int pos = type.indexOf("charset=");
/* 1199 */       if (pos != -1) {
/* 1200 */         int j = pos + "charset=".length();
/*      */         do {
/* 1202 */           char c = type.charAt(j);
/* 1203 */           if (c == ' ' || c == '\t' || c == ';') {
/*      */             break;
/*      */           }
/* 1206 */           ++j;
/* 1207 */         } while (j < type.length());
/* 1208 */         charset = type.substring(pos + "charset=".length(), j);
/*      */         
/* 1210 */         if (charset.startsWith("\"") && charset.endsWith("\"") && charset.length() > 1) {
/* 1211 */           charset = charset.substring(1, charset.length() - 1);
/*      */         }
/*      */         
/* 1214 */         int charsetStart = pos;
/* 1215 */         while (type.charAt(--charsetStart) != ';' && charsetStart > 0);
/*      */         
/* 1217 */         StringBuilder contentTypeBuilder = new StringBuilder();
/* 1218 */         contentTypeBuilder.append(type.substring(0, charsetStart));
/* 1219 */         if (j != type.length()) {
/* 1220 */           contentTypeBuilder.append(type.substring(j));
/*      */         }
/* 1222 */         contentType = contentTypeBuilder.toString();
/*      */       } 
/*      */       
/* 1225 */       for (int i = contentType.length() - 1; i >= 0; ) {
/* 1226 */         char c = contentType.charAt(i);
/* 1227 */         if (c == ' ' || c == '\t') {
/*      */           i--; continue;
/*      */         } 
/* 1230 */         if (c == ';') {
/* 1231 */           contentType = contentType.substring(0, i);
/*      */         }
/*      */       } 
/*      */     } 
/*      */     
/* 1236 */     if (charset == null) {
/* 1237 */       existing = new ContentTypeInfo(contentType, null, contentType);
/*      */     } else {
/* 1239 */       existing = new ContentTypeInfo(contentType + ";charset=" + charset, charset, contentType);
/*      */     } 
/* 1241 */     this.contentTypeCache.add(type, existing);
/* 1242 */     return existing;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   static final class ServletContextSessionConfig
/*      */     implements SessionConfig
/*      */   {
/* 1250 */     private final AttachmentKey<String> INVALIDATED = AttachmentKey.create(String.class);
/*      */     
/*      */     private final SessionConfig delegate;
/*      */     
/*      */     private ServletContextSessionConfig(SessionConfig delegate) {
/* 1255 */       this.delegate = delegate;
/*      */     }
/*      */ 
/*      */     
/*      */     public void setSessionId(HttpServerExchange exchange, String sessionId) {
/* 1260 */       this.delegate.setSessionId(exchange, sessionId);
/*      */     }
/*      */ 
/*      */     
/*      */     public void clearSession(HttpServerExchange exchange, String sessionId) {
/* 1265 */       exchange.putAttachment(this.INVALIDATED, sessionId);
/* 1266 */       Boolean isRequestedSessionIdSaved = (Boolean)exchange.getAttachment(HttpServletRequestImpl.REQUESTED_SESSION_ID_SET);
/* 1267 */       if (isRequestedSessionIdSaved == null || !isRequestedSessionIdSaved.booleanValue()) {
/* 1268 */         exchange.putAttachment(HttpServletRequestImpl.REQUESTED_SESSION_ID_SET, Boolean.TRUE);
/* 1269 */         exchange.putAttachment(HttpServletRequestImpl.REQUESTED_SESSION_ID, sessionId);
/*      */       } 
/* 1271 */       this.delegate.clearSession(exchange, sessionId);
/*      */     }
/*      */ 
/*      */     
/*      */     public String findSessionId(HttpServerExchange exchange) {
/* 1276 */       String current, invalidated = (String)exchange.getAttachment(this.INVALIDATED);
/* 1277 */       ServletRequestContext src = (ServletRequestContext)exchange.getAttachment(ServletRequestContext.ATTACHMENT_KEY);
/*      */       
/* 1279 */       if (src.getOverridenSessionId() == null) {
/* 1280 */         current = this.delegate.findSessionId(exchange);
/*      */       } else {
/* 1282 */         current = src.getOverridenSessionId();
/*      */       } 
/* 1284 */       if (invalidated == null) {
/* 1285 */         return current;
/*      */       }
/* 1287 */       if (invalidated.equals(current)) {
/* 1288 */         return null;
/*      */       }
/* 1290 */       return current;
/*      */     }
/*      */ 
/*      */     
/*      */     public SessionConfig.SessionCookieSource sessionCookieSource(HttpServerExchange exchange) {
/* 1295 */       return this.delegate.sessionCookieSource(exchange);
/*      */     }
/*      */ 
/*      */     
/*      */     public String rewriteUrl(String originalUrl, String sessionId) {
/* 1300 */       return this.delegate.rewriteUrl(originalUrl, sessionId);
/*      */     }
/*      */     
/*      */     public SessionConfig getDelegate() {
/* 1304 */       return this.delegate;
/*      */     }
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\servlet\spec\ServletContextImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */