/*     */ package io.undertow.servlet.core;
/*     */ 
/*     */ import io.undertow.Handlers;
/*     */ import io.undertow.predicate.Predicate;
/*     */ import io.undertow.predicate.Predicates;
/*     */ import io.undertow.security.api.AuthenticationMechanism;
/*     */ import io.undertow.security.api.AuthenticationMechanismFactory;
/*     */ import io.undertow.security.api.NotificationReceiver;
/*     */ import io.undertow.security.api.SecurityContextFactory;
/*     */ import io.undertow.security.handlers.AuthenticationMechanismsHandler;
/*     */ import io.undertow.security.handlers.NotificationReceiverHandler;
/*     */ import io.undertow.security.handlers.SecurityInitialHandler;
/*     */ import io.undertow.security.idm.IdentityManager;
/*     */ import io.undertow.security.impl.BasicAuthenticationMechanism;
/*     */ import io.undertow.security.impl.CachedAuthenticatedSessionMechanism;
/*     */ import io.undertow.security.impl.ClientCertAuthenticationMechanism;
/*     */ import io.undertow.security.impl.DigestAuthenticationMechanism;
/*     */ import io.undertow.security.impl.ExternalAuthenticationMechanism;
/*     */ import io.undertow.security.impl.GenericHeaderAuthenticationMechanism;
/*     */ import io.undertow.security.impl.SecurityContextFactoryImpl;
/*     */ import io.undertow.server.HandlerWrapper;
/*     */ import io.undertow.server.HttpHandler;
/*     */ import io.undertow.server.HttpServerExchange;
/*     */ import io.undertow.server.handlers.HttpContinueReadHandler;
/*     */ import io.undertow.server.handlers.PredicateHandler;
/*     */ import io.undertow.server.handlers.form.FormEncodedDataDefinition;
/*     */ import io.undertow.server.handlers.form.FormParserFactory;
/*     */ import io.undertow.server.session.SessionListener;
/*     */ import io.undertow.server.session.SessionManager;
/*     */ import io.undertow.servlet.ServletExtension;
/*     */ import io.undertow.servlet.UndertowServletLogger;
/*     */ import io.undertow.servlet.UndertowServletMessages;
/*     */ import io.undertow.servlet.api.AuthMethodConfig;
/*     */ import io.undertow.servlet.api.Deployment;
/*     */ import io.undertow.servlet.api.DeploymentInfo;
/*     */ import io.undertow.servlet.api.DeploymentManager;
/*     */ import io.undertow.servlet.api.ErrorPage;
/*     */ import io.undertow.servlet.api.FilterInfo;
/*     */ import io.undertow.servlet.api.HttpMethodSecurityInfo;
/*     */ import io.undertow.servlet.api.InstanceHandle;
/*     */ import io.undertow.servlet.api.ListenerInfo;
/*     */ import io.undertow.servlet.api.LoginConfig;
/*     */ import io.undertow.servlet.api.MetricsCollector;
/*     */ import io.undertow.servlet.api.MimeMapping;
/*     */ import io.undertow.servlet.api.SecurityConstraint;
/*     */ import io.undertow.servlet.api.SecurityInfo;
/*     */ import io.undertow.servlet.api.ServletContainer;
/*     */ import io.undertow.servlet.api.ServletContainerInitializerInfo;
/*     */ import io.undertow.servlet.api.ServletInfo;
/*     */ import io.undertow.servlet.api.ServletSecurityInfo;
/*     */ import io.undertow.servlet.api.ServletSessionConfig;
/*     */ import io.undertow.servlet.api.ServletStackTraces;
/*     */ import io.undertow.servlet.api.SessionPersistenceManager;
/*     */ import io.undertow.servlet.api.ThreadSetupHandler;
/*     */ import io.undertow.servlet.api.WebResourceCollection;
/*     */ import io.undertow.servlet.handlers.CrawlerSessionManagerHandler;
/*     */ import io.undertow.servlet.handlers.RedirectDirHandler;
/*     */ import io.undertow.servlet.handlers.SendErrorPageHandler;
/*     */ import io.undertow.servlet.handlers.ServletDispatchingHandler;
/*     */ import io.undertow.servlet.handlers.ServletHandler;
/*     */ import io.undertow.servlet.handlers.ServletInitialHandler;
/*     */ import io.undertow.servlet.handlers.SessionRestoringHandler;
/*     */ import io.undertow.servlet.handlers.security.CachedAuthenticatedSessionHandler;
/*     */ import io.undertow.servlet.handlers.security.SSLInformationAssociationHandler;
/*     */ import io.undertow.servlet.handlers.security.SecurityPathMatches;
/*     */ import io.undertow.servlet.handlers.security.ServletAuthenticationCallHandler;
/*     */ import io.undertow.servlet.handlers.security.ServletAuthenticationConstraintHandler;
/*     */ import io.undertow.servlet.handlers.security.ServletConfidentialityConstraintHandler;
/*     */ import io.undertow.servlet.handlers.security.ServletFormAuthenticationMechanism;
/*     */ import io.undertow.servlet.handlers.security.ServletSecurityConstraintHandler;
/*     */ import io.undertow.servlet.predicate.DispatcherTypePredicate;
/*     */ import io.undertow.servlet.spec.ServletContextImpl;
/*     */ import io.undertow.servlet.spec.SessionCookieConfigImpl;
/*     */ import io.undertow.util.MimeMappings;
/*     */ import java.io.File;
/*     */ import java.nio.charset.Charset;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.LinkedList;
/*     */ import java.util.List;
/*     */ import java.util.Locale;
/*     */ import java.util.Map;
/*     */ import java.util.ServiceLoader;
/*     */ import java.util.Set;
/*     */ import java.util.TreeMap;
/*     */ import javax.servlet.ServletContainerInitializer;
/*     */ import javax.servlet.ServletContext;
/*     */ import javax.servlet.ServletContextEvent;
/*     */ import javax.servlet.ServletContextListener;
/*     */ import javax.servlet.ServletException;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class DeploymentManagerImpl
/*     */   implements DeploymentManager
/*     */ {
/*     */   private final DeploymentInfo originalDeployment;
/*     */   private final ServletContainer servletContainer;
/*     */   private volatile DeploymentImpl deployment;
/* 135 */   private volatile DeploymentManager.State state = DeploymentManager.State.UNDEPLOYED;
/*     */   
/*     */   public DeploymentManagerImpl(DeploymentInfo deployment, ServletContainer servletContainer) {
/* 138 */     this.originalDeployment = deployment;
/* 139 */     this.servletContainer = servletContainer;
/*     */   }
/*     */ 
/*     */   
/*     */   public void deploy() {
/* 144 */     final DeploymentInfo deploymentInfo = this.originalDeployment.clone();
/*     */     
/* 146 */     if (deploymentInfo.getServletStackTraces() == ServletStackTraces.ALL) {
/* 147 */       UndertowServletLogger.REQUEST_LOGGER.servletStackTracesAll(deploymentInfo.getDeploymentName());
/*     */     }
/*     */     
/* 150 */     deploymentInfo.validate();
/* 151 */     final DeploymentImpl deployment = new DeploymentImpl(this, deploymentInfo, this.servletContainer);
/* 152 */     this.deployment = deployment;
/*     */     
/* 154 */     final ServletContextImpl servletContext = new ServletContextImpl(this.servletContainer, deployment);
/* 155 */     deployment.setServletContext(servletContext);
/* 156 */     handleExtensions(deploymentInfo, servletContext);
/*     */     
/* 158 */     List<ThreadSetupHandler> setup = new ArrayList<>();
/* 159 */     setup.add(ServletRequestContextThreadSetupAction.INSTANCE);
/* 160 */     setup.add(new ContextClassLoaderSetupAction(deploymentInfo.getClassLoader()));
/* 161 */     setup.addAll(deploymentInfo.getThreadSetupActions());
/* 162 */     deployment.setThreadSetupActions(setup);
/*     */     
/* 164 */     deployment.getServletPaths().setWelcomePages(deploymentInfo.getWelcomePages());
/*     */     
/* 166 */     if (deploymentInfo.getDefaultEncoding() != null) {
/* 167 */       deployment.setDefaultCharset(Charset.forName(deploymentInfo.getDefaultEncoding()));
/*     */     }
/* 169 */     if (deploymentInfo.getDefaultRequestEncoding() != null) {
/* 170 */       deployment.setDefaultRequestCharset(Charset.forName(deploymentInfo.getDefaultRequestEncoding()));
/* 171 */     } else if (deploymentInfo.getDefaultEncoding() != null) {
/* 172 */       deployment.setDefaultRequestCharset(Charset.forName(deploymentInfo.getDefaultEncoding()));
/*     */     } 
/* 174 */     if (deploymentInfo.getDefaultResponseEncoding() != null) {
/* 175 */       deployment.setDefaultResponseCharset(Charset.forName(deploymentInfo.getDefaultResponseEncoding()));
/* 176 */     } else if (deploymentInfo.getDefaultEncoding() != null) {
/* 177 */       deployment.setDefaultResponseCharset(Charset.forName(deploymentInfo.getDefaultEncoding()));
/*     */     } 
/*     */     
/* 180 */     handleDeploymentSessionConfig(deploymentInfo, servletContext);
/*     */     
/* 182 */     deployment.setSessionManager(deploymentInfo.getSessionManagerFactory().createSessionManager(deployment));
/* 183 */     deployment.getSessionManager().setDefaultSessionTimeout(deploymentInfo.getDefaultSessionTimeout());
/*     */ 
/*     */     
/*     */     try {
/* 187 */       deployment.<C, T>createThreadSetupAction((ThreadSetupHandler.Action)new ThreadSetupHandler.Action<Void, Object>() { public Void call(HttpServerExchange exchange, Object ignore) throws Exception {
/*     */               CrawlerSessionManagerHandler crawlerSessionManagerHandler;
/*     */               HttpHandler httpHandler3;
/* 190 */               ApplicationListeners listeners = DeploymentManagerImpl.this.createListeners();
/*     */               
/* 192 */               deployment.setApplicationListeners(listeners);
/*     */ 
/*     */               
/* 195 */               DeploymentManagerImpl.this.createServletsAndFilters(deployment, deploymentInfo);
/*     */ 
/*     */               
/* 198 */               DeploymentManagerImpl.this.initializeTempDir(servletContext, deploymentInfo);
/*     */ 
/*     */               
/* 201 */               for (ServletContainerInitializerInfo sci : deploymentInfo.getServletContainerInitializers()) {
/* 202 */                 InstanceHandle<? extends ServletContainerInitializer> instance = sci.getInstanceFactory().createInstance();
/*     */                 try {
/* 204 */                   ((ServletContainerInitializer)instance.getInstance()).onStartup(sci.getHandlesTypes(), (ServletContext)servletContext);
/*     */                 } finally {
/* 206 */                   instance.release();
/*     */                 } 
/*     */               } 
/*     */               
/* 210 */               listeners.start();
/*     */               
/* 212 */               deployment.getSessionManager().registerSessionListener(new SessionListenerBridge(deployment, listeners, (ServletContext)servletContext));
/* 213 */               for (SessionListener listener : deploymentInfo.getSessionListeners()) {
/* 214 */                 deployment.getSessionManager().registerSessionListener(listener);
/*     */               }
/*     */               
/* 217 */               DeploymentManagerImpl.this.initializeErrorPages(deployment, deploymentInfo);
/* 218 */               DeploymentManagerImpl.this.initializeMimeMappings(deployment, deploymentInfo);
/* 219 */               listeners.contextInitialized();
/*     */ 
/*     */               
/* 222 */               ServletDispatchingHandler servletDispatchingHandler = ServletDispatchingHandler.INSTANCE;
/* 223 */               HttpHandler httpHandler2 = DeploymentManagerImpl.wrapHandlers((HttpHandler)servletDispatchingHandler, deploymentInfo.getInnerHandlerChainWrappers());
/* 224 */               RedirectDirHandler redirectDirHandler = new RedirectDirHandler(httpHandler2, deployment.getServletPaths());
/* 225 */               if (!deploymentInfo.isSecurityDisabled()) {
/* 226 */                 HttpHandler securityHandler = DeploymentManagerImpl.this.setupSecurityHandlers((HttpHandler)redirectDirHandler);
/* 227 */                 predicateHandler = new PredicateHandler((Predicate)DispatcherTypePredicate.REQUEST, securityHandler, (HttpHandler)redirectDirHandler);
/*     */               } 
/* 229 */               HttpHandler outerHandlers = DeploymentManagerImpl.wrapHandlers((HttpHandler)predicateHandler, deploymentInfo.getOuterHandlerChainWrappers());
/* 230 */               SendErrorPageHandler sendErrorPageHandler = new SendErrorPageHandler(outerHandlers);
/* 231 */               PredicateHandler predicateHandler = new PredicateHandler((Predicate)DispatcherTypePredicate.REQUEST, (HttpHandler)sendErrorPageHandler, (HttpHandler)predicateHandler);
/* 232 */               HttpHandler httpHandler1 = DeploymentManagerImpl.this.handleDevelopmentModePersistentSessions((HttpHandler)predicateHandler, deploymentInfo, deployment.getSessionManager(), servletContext);
/*     */               
/* 234 */               MetricsCollector metrics = deploymentInfo.getMetricsCollector();
/* 235 */               if (metrics != null) {
/* 236 */                 httpHandler1 = new MetricsChainHandler(httpHandler1, metrics, deployment);
/*     */               }
/* 238 */               if (deploymentInfo.getCrawlerSessionManagerConfig() != null) {
/* 239 */                 crawlerSessionManagerHandler = new CrawlerSessionManagerHandler(deploymentInfo.getCrawlerSessionManagerConfig(), httpHandler1);
/*     */               }
/*     */               
/* 242 */               ServletInitialHandler servletInitialHandler = SecurityActions.createServletInitialHandler(deployment.getServletPaths(), (HttpHandler)crawlerSessionManagerHandler, deployment, servletContext);
/*     */               
/* 244 */               HttpHandler initialHandler = DeploymentManagerImpl.wrapHandlers((HttpHandler)servletInitialHandler, deployment.getDeploymentInfo().getInitialHandlerChainWrappers());
/* 245 */               HttpContinueReadHandler httpContinueReadHandler = new HttpContinueReadHandler(initialHandler);
/* 246 */               if (deploymentInfo.getUrlEncoding() != null) {
/* 247 */                 httpHandler3 = Handlers.urlDecodingHandler(deploymentInfo.getUrlEncoding(), (HttpHandler)httpContinueReadHandler);
/*     */               }
/* 249 */               deployment.setInitialHandler(httpHandler3);
/* 250 */               deployment.setServletHandler(servletInitialHandler);
/* 251 */               deployment.getServletPaths().invalidate();
/* 252 */               servletContext.initDone();
/* 253 */               return null;
/*     */             } }
/* 255 */         ).call(null, null);
/* 256 */     } catch (Exception e) {
/* 257 */       throw new RuntimeException(e);
/*     */     } 
/*     */ 
/*     */     
/* 261 */     deployment.getServletPaths().initData();
/* 262 */     for (ServletContextListener listener : deploymentInfo.getDeploymentCompleteListeners()) {
/* 263 */       listener.contextInitialized(new ServletContextEvent((ServletContext)servletContext));
/*     */     }
/* 265 */     this.state = DeploymentManager.State.DEPLOYED;
/*     */   }
/*     */   
/*     */   private void createServletsAndFilters(DeploymentImpl deployment, DeploymentInfo deploymentInfo) {
/* 269 */     for (Map.Entry<String, ServletInfo> servlet : (Iterable<Map.Entry<String, ServletInfo>>)deploymentInfo.getServlets().entrySet()) {
/* 270 */       deployment.getServlets().addServlet(servlet.getValue());
/*     */     }
/* 272 */     for (Map.Entry<String, FilterInfo> filter : (Iterable<Map.Entry<String, FilterInfo>>)deploymentInfo.getFilters().entrySet()) {
/* 273 */       deployment.getFilters().addFilter(filter.getValue());
/*     */     }
/*     */   }
/*     */   
/*     */   private void handleExtensions(DeploymentInfo deploymentInfo, ServletContextImpl servletContext) {
/* 278 */     Set<String> loadedExtensions = new HashSet<>();
/*     */     
/* 280 */     for (ServletExtension extension : ServiceLoader.<ServletExtension>load(ServletExtension.class, deploymentInfo.getClassLoader())) {
/* 281 */       loadedExtensions.add(extension.getClass().getName());
/* 282 */       extension.handleDeployment(deploymentInfo, (ServletContext)servletContext);
/*     */     } 
/*     */     
/* 285 */     if (ServletExtension.class.getClassLoader() != null && !ServletExtension.class.getClassLoader().equals(deploymentInfo.getClassLoader())) {
/* 286 */       for (ServletExtension extension : ServiceLoader.<ServletExtension>load(ServletExtension.class)) {
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 291 */         if (!loadedExtensions.contains(extension.getClass().getName())) {
/* 292 */           extension.handleDeployment(deploymentInfo, (ServletContext)servletContext);
/*     */         }
/*     */       } 
/*     */     }
/*     */     
/* 297 */     for (ServletExtension extension : ServletExtensionHolder.getServletExtensions()) {
/* 298 */       if (!loadedExtensions.contains(extension.getClass().getName())) {
/* 299 */         extension.handleDeployment(deploymentInfo, (ServletContext)servletContext);
/*     */       }
/*     */     } 
/*     */     
/* 303 */     for (ServletExtension extension : deploymentInfo.getServletExtensions())
/* 304 */       extension.handleDeployment(deploymentInfo, (ServletContext)servletContext); 
/*     */   }
/*     */   
/*     */   private HttpHandler setupSecurityHandlers(HttpHandler initialHandler) {
/*     */     HttpHandler httpHandler2;
/*     */     PredicateHandler predicateHandler;
/*     */     ServletAuthenticationConstraintHandler servletAuthenticationConstraintHandler;
/*     */     ServletSecurityConstraintHandler servletSecurityConstraintHandler;
/*     */     CachedAuthenticatedSessionHandler cachedAuthenticatedSessionHandler;
/*     */     NotificationReceiverHandler notificationReceiverHandler;
/*     */     SecurityInitialHandler securityInitialHandler;
/*     */     HttpHandler httpHandler1;
/* 316 */     DeploymentInfo deploymentInfo = this.deployment.getDeploymentInfo();
/* 317 */     LoginConfig loginConfig = deploymentInfo.getLoginConfig();
/*     */     
/* 319 */     HttpHandler current = initialHandler;
/* 320 */     SSLInformationAssociationHandler sSLInformationAssociationHandler = new SSLInformationAssociationHandler(current);
/*     */     
/* 322 */     SecurityPathMatches securityPathMatches = buildSecurityConstraints();
/* 323 */     securityPathMatches.logWarningsAboutUncoveredMethods();
/* 324 */     ServletAuthenticationCallHandler servletAuthenticationCallHandler = new ServletAuthenticationCallHandler((HttpHandler)sSLInformationAssociationHandler);
/*     */     
/* 326 */     for (HandlerWrapper wrapper : deploymentInfo.getSecurityWrappers()) {
/* 327 */       httpHandler2 = wrapper.wrap((HttpHandler)servletAuthenticationCallHandler);
/*     */     }
/*     */     
/* 330 */     if (deploymentInfo.isDisableCachingForSecuredPages()) {
/* 331 */       predicateHandler = Handlers.predicate(Predicates.authRequired(), Handlers.disableCache(httpHandler2), httpHandler2);
/*     */     }
/* 333 */     if (!securityPathMatches.isEmpty()) {
/* 334 */       servletAuthenticationConstraintHandler = new ServletAuthenticationConstraintHandler((HttpHandler)predicateHandler);
/*     */     }
/* 336 */     ServletConfidentialityConstraintHandler servletConfidentialityConstraintHandler = new ServletConfidentialityConstraintHandler(deploymentInfo.getConfidentialPortManager(), (HttpHandler)servletAuthenticationConstraintHandler);
/* 337 */     if (!securityPathMatches.isEmpty()) {
/* 338 */       servletSecurityConstraintHandler = new ServletSecurityConstraintHandler(securityPathMatches, (HttpHandler)servletConfidentialityConstraintHandler);
/*     */     }
/*     */     
/* 341 */     HandlerWrapper initialSecurityWrapper = deploymentInfo.getInitialSecurityWrapper();
/*     */     
/* 343 */     String mechName = null;
/* 344 */     if (initialSecurityWrapper == null) {
/* 345 */       AuthenticationMechanismsHandler authenticationMechanismsHandler; Map<String, AuthenticationMechanismFactory> factoryMap = new HashMap<>(deploymentInfo.getAuthenticationMechanisms());
/* 346 */       IdentityManager identityManager = deploymentInfo.getIdentityManager();
/* 347 */       if (!factoryMap.containsKey("BASIC")) {
/* 348 */         factoryMap.put("BASIC", BasicAuthenticationMechanism.FACTORY);
/*     */       }
/* 350 */       if (!factoryMap.containsKey("FORM")) {
/* 351 */         factoryMap.put("FORM", ServletFormAuthenticationMechanism.FACTORY);
/*     */       }
/* 353 */       if (!factoryMap.containsKey("DIGEST")) {
/* 354 */         factoryMap.put("DIGEST", DigestAuthenticationMechanism.FACTORY);
/*     */       }
/* 356 */       if (!factoryMap.containsKey("CLIENT_CERT")) {
/* 357 */         factoryMap.put("CLIENT_CERT", ClientCertAuthenticationMechanism.FACTORY);
/*     */       }
/* 359 */       if (!factoryMap.containsKey("EXTERNAL")) {
/* 360 */         factoryMap.put("EXTERNAL", ExternalAuthenticationMechanism.FACTORY);
/*     */       }
/* 362 */       if (!factoryMap.containsKey("GENERIC_HEADER")) {
/* 363 */         factoryMap.put("GENERIC_HEADER", GenericHeaderAuthenticationMechanism.FACTORY);
/*     */       }
/* 365 */       List<AuthenticationMechanism> authenticationMechanisms = new LinkedList<>();
/*     */       
/* 367 */       if (deploymentInfo.isUseCachedAuthenticationMechanism()) {
/* 368 */         authenticationMechanisms.add(new CachedAuthenticatedSessionMechanism(identityManager));
/*     */       }
/* 370 */       if (loginConfig != null || deploymentInfo.getJaspiAuthenticationMechanism() != null) {
/*     */ 
/*     */         
/* 373 */         FormEncodedDataDefinition formEncodedDataDefinition = new FormEncodedDataDefinition();
/* 374 */         String reqEncoding = deploymentInfo.getDefaultRequestEncoding();
/* 375 */         if (reqEncoding == null) {
/* 376 */           reqEncoding = deploymentInfo.getDefaultEncoding();
/*     */         }
/* 378 */         if (reqEncoding != null) {
/* 379 */           formEncodedDataDefinition.setDefaultEncoding(reqEncoding);
/*     */         }
/*     */ 
/*     */         
/* 383 */         FormParserFactory parser = FormParserFactory.builder(false).addParser((FormParserFactory.ParserDefinition)formEncodedDataDefinition).build();
/*     */         
/* 385 */         List<AuthMethodConfig> authMethods = Collections.emptyList();
/* 386 */         if (loginConfig != null) {
/* 387 */           authMethods = loginConfig.getAuthMethods();
/*     */         }
/*     */         
/* 390 */         for (AuthMethodConfig method : authMethods) {
/* 391 */           AuthenticationMechanismFactory factory = factoryMap.get(method.getName());
/* 392 */           if (factory == null) {
/* 393 */             throw UndertowServletMessages.MESSAGES.unknownAuthenticationMechanism(method.getName());
/*     */           }
/* 395 */           if (mechName == null) {
/* 396 */             mechName = method.getName();
/*     */           }
/*     */           
/* 399 */           Map<String, String> properties = new HashMap<>();
/* 400 */           properties.put("context_path", deploymentInfo.getContextPath());
/* 401 */           properties.put("realm", loginConfig.getRealmName());
/* 402 */           properties.put("error_page", loginConfig.getErrorPage());
/* 403 */           properties.put("login_page", loginConfig.getLoginPage());
/* 404 */           properties.putAll(method.getProperties());
/*     */           
/* 406 */           String name = method.getName().toUpperCase(Locale.US);
/*     */ 
/*     */           
/* 409 */           name = name.equals("FORM") ? "FORM" : name;
/* 410 */           name = name.equals("BASIC") ? "BASIC" : name;
/* 411 */           name = name.equals("DIGEST") ? "DIGEST" : name;
/* 412 */           name = name.equals("CLIENT_CERT") ? "CLIENT_CERT" : name;
/*     */           
/* 414 */           authenticationMechanisms.add(factory.create(name, identityManager, parser, properties));
/*     */         } 
/*     */       } 
/*     */ 
/*     */       
/* 419 */       this.deployment.setAuthenticationMechanisms(authenticationMechanisms);
/*     */       
/* 421 */       if (deploymentInfo.getJaspiAuthenticationMechanism() == null) {
/* 422 */         authenticationMechanismsHandler = new AuthenticationMechanismsHandler((HttpHandler)servletSecurityConstraintHandler, authenticationMechanisms);
/*     */       } else {
/* 424 */         authenticationMechanismsHandler = new AuthenticationMechanismsHandler((HttpHandler)authenticationMechanismsHandler, Collections.singletonList(deploymentInfo.getJaspiAuthenticationMechanism()));
/*     */       } 
/*     */       
/* 427 */       cachedAuthenticatedSessionHandler = new CachedAuthenticatedSessionHandler((HttpHandler)authenticationMechanismsHandler, this.deployment.getServletContext());
/*     */     } 
/*     */     
/* 430 */     List<NotificationReceiver> notificationReceivers = deploymentInfo.getNotificationReceivers();
/* 431 */     if (!notificationReceivers.isEmpty()) {
/* 432 */       notificationReceiverHandler = new NotificationReceiverHandler((HttpHandler)cachedAuthenticatedSessionHandler, notificationReceivers);
/*     */     }
/*     */     
/* 435 */     if (initialSecurityWrapper == null) {
/*     */ 
/*     */       
/* 438 */       SecurityContextFactory contextFactory = deploymentInfo.getSecurityContextFactory();
/* 439 */       if (contextFactory == null) {
/* 440 */         contextFactory = SecurityContextFactoryImpl.INSTANCE;
/*     */       }
/* 442 */       securityInitialHandler = new SecurityInitialHandler(deploymentInfo.getAuthenticationMode(), deploymentInfo.getIdentityManager(), mechName, contextFactory, (HttpHandler)notificationReceiverHandler);
/*     */     } else {
/*     */       
/* 445 */       httpHandler1 = initialSecurityWrapper.wrap((HttpHandler)securityInitialHandler);
/*     */     } 
/*     */     
/* 448 */     return httpHandler1;
/*     */   }
/*     */   
/*     */   private SecurityPathMatches buildSecurityConstraints() {
/* 452 */     SecurityPathMatches.Builder builder = SecurityPathMatches.builder(this.deployment.getDeploymentInfo());
/* 453 */     Set<String> urlPatterns = new HashSet<>();
/* 454 */     for (SecurityConstraint constraint : this.deployment.getDeploymentInfo().getSecurityConstraints()) {
/* 455 */       builder.addSecurityConstraint(constraint);
/* 456 */       for (WebResourceCollection webResources : constraint.getWebResourceCollections()) {
/* 457 */         urlPatterns.addAll(webResources.getUrlPatterns());
/*     */       }
/*     */     } 
/*     */     
/* 461 */     for (ServletInfo servlet : this.deployment.getDeploymentInfo().getServlets().values()) {
/* 462 */       ServletSecurityInfo securityInfo = servlet.getServletSecurityInfo();
/* 463 */       if (securityInfo != null) {
/* 464 */         Set<String> mappings = new HashSet<>(servlet.getMappings());
/* 465 */         mappings.removeAll(urlPatterns);
/* 466 */         if (!mappings.isEmpty()) {
/* 467 */           Set<String> methods = new HashSet<>();
/*     */           
/* 469 */           for (HttpMethodSecurityInfo method : securityInfo.getHttpMethodSecurityInfo()) {
/* 470 */             methods.add(method.getMethod());
/* 471 */             if (method.getRolesAllowed().isEmpty() && method.getEmptyRoleSemantic() == SecurityInfo.EmptyRoleSemantic.PERMIT) {
/*     */               continue;
/*     */             }
/*     */ 
/*     */ 
/*     */ 
/*     */             
/* 478 */             SecurityConstraint newConstraint = ((SecurityConstraint)((SecurityConstraint)(new SecurityConstraint()).addRolesAllowed(method.getRolesAllowed())).setTransportGuaranteeType(method.getTransportGuaranteeType())).addWebResourceCollection((new WebResourceCollection()).addUrlPatterns(mappings)
/* 479 */                 .addHttpMethod(method.getMethod()));
/* 480 */             builder.addSecurityConstraint(newConstraint);
/*     */           } 
/*     */           
/* 483 */           if (!securityInfo.getRolesAllowed().isEmpty() || securityInfo
/* 484 */             .getEmptyRoleSemantic() != SecurityInfo.EmptyRoleSemantic.PERMIT || methods
/* 485 */             .isEmpty()) {
/*     */ 
/*     */ 
/*     */ 
/*     */             
/* 490 */             SecurityConstraint newConstraint = ((SecurityConstraint)((SecurityConstraint)((SecurityConstraint)(new SecurityConstraint()).setEmptyRoleSemantic(securityInfo.getEmptyRoleSemantic())).addRolesAllowed(securityInfo.getRolesAllowed())).setTransportGuaranteeType(securityInfo.getTransportGuaranteeType())).addWebResourceCollection((new WebResourceCollection()).addUrlPatterns(mappings)
/* 491 */                 .addHttpMethodOmissions(methods));
/* 492 */             builder.addSecurityConstraint(newConstraint);
/*     */           } 
/*     */         } 
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/* 499 */     return builder.build();
/*     */   }
/*     */   
/*     */   private void initializeTempDir(ServletContextImpl servletContext, DeploymentInfo deploymentInfo) {
/* 503 */     if (deploymentInfo.getTempDir() != null) {
/* 504 */       servletContext.setAttribute("javax.servlet.context.tempdir", deploymentInfo.getTempDir());
/*     */     } else {
/* 506 */       servletContext.setAttribute("javax.servlet.context.tempdir", new File(SecurityActions.getSystemProperty("java.io.tmpdir")));
/*     */     } 
/*     */   }
/*     */   
/*     */   private void initializeMimeMappings(DeploymentImpl deployment, DeploymentInfo deploymentInfo) {
/* 511 */     Map<String, String> mappings = new HashMap<>(MimeMappings.DEFAULT_MIME_MAPPINGS);
/* 512 */     for (MimeMapping mapping : deploymentInfo.getMimeMappings()) {
/* 513 */       mappings.put(mapping.getExtension().toLowerCase(Locale.ENGLISH), mapping.getMimeType());
/*     */     }
/* 515 */     deployment.setMimeExtensionMappings(mappings);
/*     */   }
/*     */   
/*     */   private void initializeErrorPages(DeploymentImpl deployment, DeploymentInfo deploymentInfo) {
/* 519 */     Map<Integer, String> codes = new HashMap<>();
/* 520 */     Map<Class<? extends Throwable>, String> exceptions = new HashMap<>();
/* 521 */     String defaultErrorPage = null;
/* 522 */     for (ErrorPage page : deploymentInfo.getErrorPages()) {
/* 523 */       if (page.getExceptionType() != null) {
/* 524 */         exceptions.put(page.getExceptionType(), page.getLocation()); continue;
/* 525 */       }  if (page.getErrorCode() != null) {
/* 526 */         codes.put(page.getErrorCode(), page.getLocation()); continue;
/*     */       } 
/* 528 */       if (defaultErrorPage != null) {
/* 529 */         throw UndertowServletMessages.MESSAGES.moreThanOneDefaultErrorPage(defaultErrorPage, page.getLocation());
/*     */       }
/* 531 */       defaultErrorPage = page.getLocation();
/*     */     } 
/*     */ 
/*     */     
/* 535 */     deployment.setErrorPages(new ErrorPages(codes, exceptions, defaultErrorPage));
/*     */   }
/*     */ 
/*     */   
/*     */   private ApplicationListeners createListeners() {
/* 540 */     List<ManagedListener> managedListeners = new ArrayList<>();
/* 541 */     for (ListenerInfo listener : this.deployment.getDeploymentInfo().getListeners()) {
/* 542 */       managedListeners.add(new ManagedListener(listener, listener.isProgramatic()));
/*     */     }
/* 544 */     return new ApplicationListeners(managedListeners, (ServletContext)this.deployment.getServletContext());
/*     */   }
/*     */ 
/*     */   
/*     */   private static HttpHandler wrapHandlers(HttpHandler wrapee, List<HandlerWrapper> wrappers) {
/* 549 */     HttpHandler current = wrapee;
/* 550 */     for (HandlerWrapper wrapper : wrappers) {
/* 551 */       current = wrapper.wrap(current);
/*     */     }
/* 553 */     return current;
/*     */   }
/*     */ 
/*     */   
/*     */   public HttpHandler start() throws ServletException {
/*     */     try {
/* 559 */       return (HttpHandler)this.deployment.<C, T>createThreadSetupAction((ThreadSetupHandler.Action)new ThreadSetupHandler.Action<HttpHandler, Object>()
/*     */           {
/*     */             public HttpHandler call(HttpServerExchange exchange, Object ignore) throws ServletException {
/* 562 */               DeploymentManagerImpl.this.deployment.getSessionManager().start();
/*     */ 
/*     */ 
/*     */               
/* 566 */               ArrayList<Lifecycle> lifecycles = new ArrayList<>(DeploymentManagerImpl.this.deployment.getLifecycleObjects());
/* 567 */               for (Lifecycle object : lifecycles) {
/* 568 */                 object.start();
/*     */               }
/* 570 */               HttpHandler root = DeploymentManagerImpl.this.deployment.getHandler();
/* 571 */               TreeMap<Integer, List<ManagedServlet>> loadOnStartup = new TreeMap<>();
/* 572 */               for (Map.Entry<String, ServletHandler> entry : DeploymentManagerImpl.this.deployment.getServlets().getServletHandlers().entrySet()) {
/* 573 */                 ManagedServlet servlet = ((ServletHandler)entry.getValue()).getManagedServlet();
/* 574 */                 Integer loadOnStartupNumber = servlet.getServletInfo().getLoadOnStartup();
/* 575 */                 if (loadOnStartupNumber == null || 
/* 576 */                   loadOnStartupNumber.intValue() < 0) {
/*     */                   continue;
/*     */                 }
/* 579 */                 List<ManagedServlet> list = loadOnStartup.get(loadOnStartupNumber);
/* 580 */                 if (list == null) {
/* 581 */                   loadOnStartup.put(loadOnStartupNumber, list = new ArrayList<>());
/*     */                 }
/* 583 */                 list.add(servlet);
/*     */               } 
/*     */               
/* 586 */               for (Map.Entry<Integer, List<ManagedServlet>> load : loadOnStartup.entrySet()) {
/* 587 */                 for (ManagedServlet servlet : load.getValue()) {
/* 588 */                   servlet.createServlet();
/*     */                 }
/*     */               } 
/*     */               
/* 592 */               if (DeploymentManagerImpl.this.deployment.getDeploymentInfo().isEagerFilterInit()) {
/* 593 */                 for (ManagedFilter filter : DeploymentManagerImpl.this.deployment.getFilters().getFilters().values()) {
/* 594 */                   filter.createFilter();
/*     */                 }
/*     */               }
/*     */               
/* 598 */               DeploymentManagerImpl.this.state = DeploymentManager.State.STARTED;
/* 599 */               return root;
/*     */             }
/* 601 */           }).call(null, null);
/* 602 */     } catch (ServletException|RuntimeException e) {
/* 603 */       throw e;
/* 604 */     } catch (Exception e) {
/* 605 */       throw new RuntimeException(e);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void stop() throws ServletException {
/*     */     try {
/* 612 */       this.deployment.<C, T>createThreadSetupAction((ThreadSetupHandler.Action)new ThreadSetupHandler.Action<Void, Object>()
/*     */           {
/*     */             public Void call(HttpServerExchange exchange, Object ignore) throws ServletException {
/* 615 */               for (Lifecycle object : DeploymentManagerImpl.this.deployment.getLifecycleObjects()) {
/*     */                 try {
/* 617 */                   object.stop();
/* 618 */                 } catch (Throwable t) {
/* 619 */                   UndertowServletLogger.ROOT_LOGGER.failedToDestroy(object, t);
/*     */                 } 
/*     */               } 
/* 622 */               DeploymentManagerImpl.this.deployment.getSessionManager().stop();
/* 623 */               DeploymentManagerImpl.this.state = DeploymentManager.State.DEPLOYED;
/* 624 */               return null;
/*     */             }
/* 626 */           }).call(null, null);
/* 627 */     } catch (ServletException|RuntimeException e) {
/* 628 */       throw e;
/* 629 */     } catch (Exception e) {
/* 630 */       throw new RuntimeException(e);
/*     */     } 
/*     */   }
/*     */   
/*     */   private HttpHandler handleDevelopmentModePersistentSessions(HttpHandler next, DeploymentInfo deploymentInfo, SessionManager sessionManager, ServletContextImpl servletContext) {
/* 635 */     SessionPersistenceManager sessionPersistenceManager = deploymentInfo.getSessionPersistenceManager();
/* 636 */     if (sessionPersistenceManager != null) {
/* 637 */       SessionRestoringHandler handler = new SessionRestoringHandler(this.deployment.getDeploymentInfo().getDeploymentName(), sessionManager, servletContext, next, sessionPersistenceManager);
/* 638 */       this.deployment.addLifecycleObjects(new Lifecycle[] { (Lifecycle)handler });
/* 639 */       return (HttpHandler)handler;
/*     */     } 
/* 641 */     return next;
/*     */   }
/*     */   
/*     */   public void handleDeploymentSessionConfig(DeploymentInfo deploymentInfo, ServletContextImpl servletContext) {
/* 645 */     SessionCookieConfigImpl sessionCookieConfig = servletContext.getSessionCookieConfig();
/* 646 */     ServletSessionConfig sc = deploymentInfo.getServletSessionConfig();
/* 647 */     if (sc != null) {
/* 648 */       sessionCookieConfig.setName(sc.getName());
/* 649 */       sessionCookieConfig.setComment(sc.getComment());
/* 650 */       sessionCookieConfig.setDomain(sc.getDomain());
/* 651 */       sessionCookieConfig.setHttpOnly(sc.isHttpOnly());
/* 652 */       sessionCookieConfig.setMaxAge(sc.getMaxAge());
/* 653 */       if (sc.getPath() != null) {
/* 654 */         sessionCookieConfig.setPath(sc.getPath());
/*     */       } else {
/* 656 */         sessionCookieConfig.setPath(deploymentInfo.getContextPath());
/*     */       } 
/* 658 */       sessionCookieConfig.setSecure(sc.isSecure());
/* 659 */       if (sc.getSessionTrackingModes() != null) {
/* 660 */         servletContext.setDefaultSessionTrackingModes(new HashSet(sc.getSessionTrackingModes()));
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void undeploy() {
/*     */     try {
/* 669 */       this.deployment.<C, T>createThreadSetupAction((ThreadSetupHandler.Action)new ThreadSetupHandler.Action<Void, Object>()
/*     */           {
/*     */             public Void call(HttpServerExchange exchange, Object ignore) throws ServletException {
/* 672 */               for (ServletContextListener listener : DeploymentManagerImpl.this.deployment.getDeploymentInfo().getDeploymentCompleteListeners()) {
/*     */                 try {
/* 674 */                   listener.contextDestroyed(new ServletContextEvent((ServletContext)DeploymentManagerImpl.this.deployment.getServletContext()));
/* 675 */                 } catch (Throwable t) {
/* 676 */                   UndertowServletLogger.REQUEST_LOGGER.failedToDestroy(listener, t);
/*     */                 } 
/*     */               } 
/* 679 */               DeploymentManagerImpl.this.deployment.destroy();
/* 680 */               DeploymentManagerImpl.this.deployment = null;
/* 681 */               DeploymentManagerImpl.this.state = DeploymentManager.State.UNDEPLOYED;
/* 682 */               return null;
/*     */             }
/* 684 */           }).call(null, null);
/* 685 */     } catch (Exception e) {
/* 686 */       throw new RuntimeException(e);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public DeploymentManager.State getState() {
/* 693 */     return this.state;
/*     */   }
/*     */ 
/*     */   
/*     */   public Deployment getDeployment() {
/* 698 */     return this.deployment;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\servlet\core\DeploymentManagerImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */