package io.undertow.servlet.core;

import io.undertow.Handlers;
import io.undertow.predicate.Predicates;
import io.undertow.security.api.AuthenticationMechanism;
import io.undertow.security.api.AuthenticationMechanismFactory;
import io.undertow.security.api.NotificationReceiver;
import io.undertow.security.api.SecurityContextFactory;
import io.undertow.security.handlers.AuthenticationMechanismsHandler;
import io.undertow.security.handlers.NotificationReceiverHandler;
import io.undertow.security.handlers.SecurityInitialHandler;
import io.undertow.security.idm.IdentityManager;
import io.undertow.security.impl.BasicAuthenticationMechanism;
import io.undertow.security.impl.CachedAuthenticatedSessionMechanism;
import io.undertow.security.impl.ClientCertAuthenticationMechanism;
import io.undertow.security.impl.DigestAuthenticationMechanism;
import io.undertow.security.impl.ExternalAuthenticationMechanism;
import io.undertow.security.impl.GenericHeaderAuthenticationMechanism;
import io.undertow.security.impl.SecurityContextFactoryImpl;
import io.undertow.server.HandlerWrapper;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.HttpContinueReadHandler;
import io.undertow.server.handlers.PredicateHandler;
import io.undertow.server.handlers.form.FormEncodedDataDefinition;
import io.undertow.server.handlers.form.FormParserFactory;
import io.undertow.server.session.SessionListener;
import io.undertow.server.session.SessionManager;
import io.undertow.servlet.ServletExtension;
import io.undertow.servlet.UndertowServletLogger;
import io.undertow.servlet.UndertowServletMessages;
import io.undertow.servlet.api.AuthMethodConfig;
import io.undertow.servlet.api.Deployment;
import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.api.DeploymentManager;
import io.undertow.servlet.api.ErrorPage;
import io.undertow.servlet.api.FilterInfo;
import io.undertow.servlet.api.HttpMethodSecurityInfo;
import io.undertow.servlet.api.InstanceHandle;
import io.undertow.servlet.api.ListenerInfo;
import io.undertow.servlet.api.LoginConfig;
import io.undertow.servlet.api.MetricsCollector;
import io.undertow.servlet.api.MimeMapping;
import io.undertow.servlet.api.SecurityConstraint;
import io.undertow.servlet.api.SecurityInfo;
import io.undertow.servlet.api.ServletContainer;
import io.undertow.servlet.api.ServletContainerInitializerInfo;
import io.undertow.servlet.api.ServletInfo;
import io.undertow.servlet.api.ServletSecurityInfo;
import io.undertow.servlet.api.ServletSessionConfig;
import io.undertow.servlet.api.ServletStackTraces;
import io.undertow.servlet.api.SessionPersistenceManager;
import io.undertow.servlet.api.ThreadSetupHandler;
import io.undertow.servlet.api.WebResourceCollection;
import io.undertow.servlet.handlers.CrawlerSessionManagerHandler;
import io.undertow.servlet.handlers.RedirectDirHandler;
import io.undertow.servlet.handlers.SendErrorPageHandler;
import io.undertow.servlet.handlers.ServletDispatchingHandler;
import io.undertow.servlet.handlers.ServletHandler;
import io.undertow.servlet.handlers.ServletInitialHandler;
import io.undertow.servlet.handlers.SessionRestoringHandler;
import io.undertow.servlet.handlers.security.CachedAuthenticatedSessionHandler;
import io.undertow.servlet.handlers.security.SSLInformationAssociationHandler;
import io.undertow.servlet.handlers.security.SecurityPathMatches;
import io.undertow.servlet.handlers.security.ServletAuthenticationCallHandler;
import io.undertow.servlet.handlers.security.ServletAuthenticationConstraintHandler;
import io.undertow.servlet.handlers.security.ServletConfidentialityConstraintHandler;
import io.undertow.servlet.handlers.security.ServletFormAuthenticationMechanism;
import io.undertow.servlet.handlers.security.ServletSecurityConstraintHandler;
import io.undertow.servlet.predicate.DispatcherTypePredicate;
import io.undertow.servlet.spec.ServletContextImpl;
import io.undertow.servlet.spec.SessionCookieConfigImpl;
import io.undertow.util.MimeMappings;
import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.TreeMap;
import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletException;

public class DeploymentManagerImpl implements DeploymentManager {
   private final DeploymentInfo originalDeployment;
   private final ServletContainer servletContainer;
   private volatile DeploymentImpl deployment;
   private volatile DeploymentManager.State state;

   public DeploymentManagerImpl(DeploymentInfo deployment, ServletContainer servletContainer) {
      this.state = DeploymentManager.State.UNDEPLOYED;
      this.originalDeployment = deployment;
      this.servletContainer = servletContainer;
   }

   public void deploy() {
      final DeploymentInfo deploymentInfo = this.originalDeployment.clone();
      if (deploymentInfo.getServletStackTraces() == ServletStackTraces.ALL) {
         UndertowServletLogger.REQUEST_LOGGER.servletStackTracesAll(deploymentInfo.getDeploymentName());
      }

      deploymentInfo.validate();
      final DeploymentImpl deployment = new DeploymentImpl(this, deploymentInfo, this.servletContainer);
      this.deployment = deployment;
      final ServletContextImpl servletContext = new ServletContextImpl(this.servletContainer, deployment);
      deployment.setServletContext(servletContext);
      this.handleExtensions(deploymentInfo, servletContext);
      List<ThreadSetupHandler> setup = new ArrayList();
      setup.add(ServletRequestContextThreadSetupAction.INSTANCE);
      setup.add(new ContextClassLoaderSetupAction(deploymentInfo.getClassLoader()));
      setup.addAll(deploymentInfo.getThreadSetupActions());
      deployment.setThreadSetupActions(setup);
      deployment.getServletPaths().setWelcomePages(deploymentInfo.getWelcomePages());
      if (deploymentInfo.getDefaultEncoding() != null) {
         deployment.setDefaultCharset(Charset.forName(deploymentInfo.getDefaultEncoding()));
      }

      if (deploymentInfo.getDefaultRequestEncoding() != null) {
         deployment.setDefaultRequestCharset(Charset.forName(deploymentInfo.getDefaultRequestEncoding()));
      } else if (deploymentInfo.getDefaultEncoding() != null) {
         deployment.setDefaultRequestCharset(Charset.forName(deploymentInfo.getDefaultEncoding()));
      }

      if (deploymentInfo.getDefaultResponseEncoding() != null) {
         deployment.setDefaultResponseCharset(Charset.forName(deploymentInfo.getDefaultResponseEncoding()));
      } else if (deploymentInfo.getDefaultEncoding() != null) {
         deployment.setDefaultResponseCharset(Charset.forName(deploymentInfo.getDefaultEncoding()));
      }

      this.handleDeploymentSessionConfig(deploymentInfo, servletContext);
      deployment.setSessionManager(deploymentInfo.getSessionManagerFactory().createSessionManager(deployment));
      deployment.getSessionManager().setDefaultSessionTimeout(deploymentInfo.getDefaultSessionTimeout());

      try {
         deployment.createThreadSetupAction(new ThreadSetupHandler.Action<Void, Object>() {
            public Void call(HttpServerExchange exchange, Object ignore) throws Exception {
               ApplicationListeners listeners = DeploymentManagerImpl.this.createListeners();
               deployment.setApplicationListeners(listeners);
               DeploymentManagerImpl.this.createServletsAndFilters(deployment, deploymentInfo);
               DeploymentManagerImpl.this.initializeTempDir(servletContext, deploymentInfo);
               Iterator var4 = deploymentInfo.getServletContainerInitializers().iterator();

               while(var4.hasNext()) {
                  ServletContainerInitializerInfo sci = (ServletContainerInitializerInfo)var4.next();
                  InstanceHandle<? extends ServletContainerInitializer> instance = sci.getInstanceFactory().createInstance();

                  try {
                     ((ServletContainerInitializer)instance.getInstance()).onStartup(sci.getHandlesTypes(), servletContext);
                  } finally {
                     instance.release();
                  }
               }

               listeners.start();
               deployment.getSessionManager().registerSessionListener(new SessionListenerBridge(deployment, listeners, servletContext));
               var4 = deploymentInfo.getSessionListeners().iterator();

               while(var4.hasNext()) {
                  SessionListener listener = (SessionListener)var4.next();
                  deployment.getSessionManager().registerSessionListener(listener);
               }

               DeploymentManagerImpl.this.initializeErrorPages(deployment, deploymentInfo);
               DeploymentManagerImpl.this.initializeMimeMappings(deployment, deploymentInfo);
               listeners.contextInitialized();
               HttpHandler wrappedHandlers = ServletDispatchingHandler.INSTANCE;
               HttpHandler wrappedHandlersx = DeploymentManagerImpl.wrapHandlers(wrappedHandlers, deploymentInfo.getInnerHandlerChainWrappers());
               HttpHandler wrappedHandlersxx = new RedirectDirHandler(wrappedHandlersx, deployment.getServletPaths());
               HttpHandler outerHandlers;
               if (!deploymentInfo.isSecurityDisabled()) {
                  outerHandlers = DeploymentManagerImpl.this.setupSecurityHandlers((HttpHandler)wrappedHandlersxx);
                  wrappedHandlersxx = new PredicateHandler(DispatcherTypePredicate.REQUEST, outerHandlers, (HttpHandler)wrappedHandlersxx);
               }

               outerHandlers = DeploymentManagerImpl.wrapHandlers((HttpHandler)wrappedHandlersxx, deploymentInfo.getOuterHandlerChainWrappers());
               HttpHandler outerHandlersx = new SendErrorPageHandler(outerHandlers);
               HttpHandler wrappedHandlersxxx = new PredicateHandler(DispatcherTypePredicate.REQUEST, outerHandlersx, (HttpHandler)wrappedHandlersxx);
               wrappedHandlersxx = DeploymentManagerImpl.this.handleDevelopmentModePersistentSessions(wrappedHandlersxxx, deploymentInfo, deployment.getSessionManager(), servletContext);
               MetricsCollector metrics = deploymentInfo.getMetricsCollector();
               if (metrics != null) {
                  wrappedHandlersxx = new MetricsChainHandler((HttpHandler)wrappedHandlersxx, metrics, deployment);
               }

               if (deploymentInfo.getCrawlerSessionManagerConfig() != null) {
                  wrappedHandlersxx = new CrawlerSessionManagerHandler(deploymentInfo.getCrawlerSessionManagerConfig(), (HttpHandler)wrappedHandlersxx);
               }

               ServletInitialHandler servletInitialHandler = SecurityActions.createServletInitialHandler(deployment.getServletPaths(), (HttpHandler)wrappedHandlersxx, deployment, servletContext);
               HttpHandler initialHandler = DeploymentManagerImpl.wrapHandlers(servletInitialHandler, deployment.getDeploymentInfo().getInitialHandlerChainWrappers());
               HttpHandler initialHandlerx = new HttpContinueReadHandler(initialHandler);
               if (deploymentInfo.getUrlEncoding() != null) {
                  initialHandlerx = Handlers.urlDecodingHandler(deploymentInfo.getUrlEncoding(), (HttpHandler)initialHandlerx);
               }

               deployment.setInitialHandler((HttpHandler)initialHandlerx);
               deployment.setServletHandler(servletInitialHandler);
               deployment.getServletPaths().invalidate();
               servletContext.initDone();
               return null;
            }
         }).call((HttpServerExchange)null, (Object)null);
      } catch (Exception var7) {
         throw new RuntimeException(var7);
      }

      deployment.getServletPaths().initData();
      Iterator var5 = deploymentInfo.getDeploymentCompleteListeners().iterator();

      while(var5.hasNext()) {
         ServletContextListener listener = (ServletContextListener)var5.next();
         listener.contextInitialized(new ServletContextEvent(servletContext));
      }

      this.state = DeploymentManager.State.DEPLOYED;
   }

   private void createServletsAndFilters(DeploymentImpl deployment, DeploymentInfo deploymentInfo) {
      Iterator var3 = deploymentInfo.getServlets().entrySet().iterator();

      Map.Entry filter;
      while(var3.hasNext()) {
         filter = (Map.Entry)var3.next();
         deployment.getServlets().addServlet((ServletInfo)filter.getValue());
      }

      var3 = deploymentInfo.getFilters().entrySet().iterator();

      while(var3.hasNext()) {
         filter = (Map.Entry)var3.next();
         deployment.getFilters().addFilter((FilterInfo)filter.getValue());
      }

   }

   private void handleExtensions(DeploymentInfo deploymentInfo, ServletContextImpl servletContext) {
      Set<String> loadedExtensions = new HashSet();
      Iterator var4 = ServiceLoader.load(ServletExtension.class, deploymentInfo.getClassLoader()).iterator();

      ServletExtension extension;
      while(var4.hasNext()) {
         extension = (ServletExtension)var4.next();
         loadedExtensions.add(extension.getClass().getName());
         extension.handleDeployment(deploymentInfo, servletContext);
      }

      if (ServletExtension.class.getClassLoader() != null && !ServletExtension.class.getClassLoader().equals(deploymentInfo.getClassLoader())) {
         var4 = ServiceLoader.load(ServletExtension.class).iterator();

         while(var4.hasNext()) {
            extension = (ServletExtension)var4.next();
            if (!loadedExtensions.contains(extension.getClass().getName())) {
               extension.handleDeployment(deploymentInfo, servletContext);
            }
         }
      }

      var4 = ServletExtensionHolder.getServletExtensions().iterator();

      while(var4.hasNext()) {
         extension = (ServletExtension)var4.next();
         if (!loadedExtensions.contains(extension.getClass().getName())) {
            extension.handleDeployment(deploymentInfo, servletContext);
         }
      }

      var4 = deploymentInfo.getServletExtensions().iterator();

      while(var4.hasNext()) {
         extension = (ServletExtension)var4.next();
         extension.handleDeployment(deploymentInfo, servletContext);
      }

   }

   private HttpHandler setupSecurityHandlers(HttpHandler initialHandler) {
      DeploymentInfo deploymentInfo = this.deployment.getDeploymentInfo();
      LoginConfig loginConfig = deploymentInfo.getLoginConfig();
      HttpHandler current = new SSLInformationAssociationHandler(initialHandler);
      SecurityPathMatches securityPathMatches = this.buildSecurityConstraints();
      securityPathMatches.logWarningsAboutUncoveredMethods();
      HttpHandler current = new ServletAuthenticationCallHandler(current);

      HandlerWrapper wrapper;
      for(Iterator var6 = deploymentInfo.getSecurityWrappers().iterator(); var6.hasNext(); current = wrapper.wrap((HttpHandler)current)) {
         wrapper = (HandlerWrapper)var6.next();
      }

      if (deploymentInfo.isDisableCachingForSecuredPages()) {
         current = Handlers.predicate(Predicates.authRequired(), Handlers.disableCache((HttpHandler)current), (HttpHandler)current);
      }

      if (!securityPathMatches.isEmpty()) {
         current = new ServletAuthenticationConstraintHandler((HttpHandler)current);
      }

      current = new ServletConfidentialityConstraintHandler(deploymentInfo.getConfidentialPortManager(), (HttpHandler)current);
      if (!securityPathMatches.isEmpty()) {
         current = new ServletSecurityConstraintHandler(securityPathMatches, (HttpHandler)current);
      }

      HandlerWrapper initialSecurityWrapper = deploymentInfo.getInitialSecurityWrapper();
      String mechName = null;
      if (initialSecurityWrapper == null) {
         Map<String, AuthenticationMechanismFactory> factoryMap = new HashMap(deploymentInfo.getAuthenticationMechanisms());
         IdentityManager identityManager = deploymentInfo.getIdentityManager();
         if (!factoryMap.containsKey("BASIC")) {
            factoryMap.put("BASIC", BasicAuthenticationMechanism.FACTORY);
         }

         if (!factoryMap.containsKey("FORM")) {
            factoryMap.put("FORM", ServletFormAuthenticationMechanism.FACTORY);
         }

         if (!factoryMap.containsKey("DIGEST")) {
            factoryMap.put("DIGEST", DigestAuthenticationMechanism.FACTORY);
         }

         if (!factoryMap.containsKey("CLIENT_CERT")) {
            factoryMap.put("CLIENT_CERT", ClientCertAuthenticationMechanism.FACTORY);
         }

         if (!factoryMap.containsKey("EXTERNAL")) {
            factoryMap.put("EXTERNAL", ExternalAuthenticationMechanism.FACTORY);
         }

         if (!factoryMap.containsKey("GENERIC_HEADER")) {
            factoryMap.put("GENERIC_HEADER", GenericHeaderAuthenticationMechanism.FACTORY);
         }

         List<AuthenticationMechanism> authenticationMechanisms = new LinkedList();
         if (deploymentInfo.isUseCachedAuthenticationMechanism()) {
            authenticationMechanisms.add(new CachedAuthenticatedSessionMechanism(identityManager));
         }

         if (loginConfig != null || deploymentInfo.getJaspiAuthenticationMechanism() != null) {
            FormEncodedDataDefinition formEncodedDataDefinition = new FormEncodedDataDefinition();
            String reqEncoding = deploymentInfo.getDefaultRequestEncoding();
            if (reqEncoding == null) {
               reqEncoding = deploymentInfo.getDefaultEncoding();
            }

            if (reqEncoding != null) {
               formEncodedDataDefinition.setDefaultEncoding(reqEncoding);
            }

            FormParserFactory parser = FormParserFactory.builder(false).addParser(formEncodedDataDefinition).build();
            List<AuthMethodConfig> authMethods = Collections.emptyList();
            if (loginConfig != null) {
               authMethods = loginConfig.getAuthMethods();
            }

            Iterator var15 = authMethods.iterator();

            while(var15.hasNext()) {
               AuthMethodConfig method = (AuthMethodConfig)var15.next();
               AuthenticationMechanismFactory factory = (AuthenticationMechanismFactory)factoryMap.get(method.getName());
               if (factory == null) {
                  throw UndertowServletMessages.MESSAGES.unknownAuthenticationMechanism(method.getName());
               }

               if (mechName == null) {
                  mechName = method.getName();
               }

               Map<String, String> properties = new HashMap();
               properties.put("context_path", deploymentInfo.getContextPath());
               properties.put("realm", loginConfig.getRealmName());
               properties.put("error_page", loginConfig.getErrorPage());
               properties.put("login_page", loginConfig.getLoginPage());
               properties.putAll(method.getProperties());
               String name = method.getName().toUpperCase(Locale.US);
               name = name.equals("FORM") ? "FORM" : name;
               name = name.equals("BASIC") ? "BASIC" : name;
               name = name.equals("DIGEST") ? "DIGEST" : name;
               name = name.equals("CLIENT_CERT") ? "CLIENT_CERT" : name;
               authenticationMechanisms.add(factory.create(name, identityManager, parser, properties));
            }
         }

         this.deployment.setAuthenticationMechanisms(authenticationMechanisms);
         AuthenticationMechanismsHandler current;
         if (deploymentInfo.getJaspiAuthenticationMechanism() == null) {
            current = new AuthenticationMechanismsHandler((HttpHandler)current, authenticationMechanisms);
         } else {
            current = new AuthenticationMechanismsHandler((HttpHandler)current, Collections.singletonList(deploymentInfo.getJaspiAuthenticationMechanism()));
         }

         current = new CachedAuthenticatedSessionHandler(current, this.deployment.getServletContext());
      }

      List<NotificationReceiver> notificationReceivers = deploymentInfo.getNotificationReceivers();
      if (!notificationReceivers.isEmpty()) {
         current = new NotificationReceiverHandler((HttpHandler)current, notificationReceivers);
      }

      if (initialSecurityWrapper == null) {
         SecurityContextFactory contextFactory = deploymentInfo.getSecurityContextFactory();
         if (contextFactory == null) {
            contextFactory = SecurityContextFactoryImpl.INSTANCE;
         }

         current = new SecurityInitialHandler(deploymentInfo.getAuthenticationMode(), deploymentInfo.getIdentityManager(), mechName, contextFactory, (HttpHandler)current);
      } else {
         current = initialSecurityWrapper.wrap((HttpHandler)current);
      }

      return (HttpHandler)current;
   }

   private SecurityPathMatches buildSecurityConstraints() {
      SecurityPathMatches.Builder builder = SecurityPathMatches.builder(this.deployment.getDeploymentInfo());
      Set<String> urlPatterns = new HashSet();
      Iterator var3 = this.deployment.getDeploymentInfo().getSecurityConstraints().iterator();

      while(var3.hasNext()) {
         SecurityConstraint constraint = (SecurityConstraint)var3.next();
         builder.addSecurityConstraint(constraint);
         Iterator var5 = constraint.getWebResourceCollections().iterator();

         while(var5.hasNext()) {
            WebResourceCollection webResources = (WebResourceCollection)var5.next();
            urlPatterns.addAll(webResources.getUrlPatterns());
         }
      }

      var3 = this.deployment.getDeploymentInfo().getServlets().values().iterator();

      while(true) {
         HashSet methods;
         ServletSecurityInfo securityInfo;
         HashSet mappings;
         label54:
         do {
            do {
               ServletInfo servlet;
               do {
                  if (!var3.hasNext()) {
                     return builder.build();
                  }

                  servlet = (ServletInfo)var3.next();
                  securityInfo = servlet.getServletSecurityInfo();
               } while(securityInfo == null);

               mappings = new HashSet(servlet.getMappings());
               mappings.removeAll(urlPatterns);
            } while(mappings.isEmpty());

            methods = new HashSet();
            Iterator var8 = securityInfo.getHttpMethodSecurityInfo().iterator();

            while(true) {
               HttpMethodSecurityInfo method;
               do {
                  if (!var8.hasNext()) {
                     continue label54;
                  }

                  method = (HttpMethodSecurityInfo)var8.next();
                  methods.add(method.getMethod());
               } while(method.getRolesAllowed().isEmpty() && method.getEmptyRoleSemantic() == SecurityInfo.EmptyRoleSemantic.PERMIT);

               SecurityConstraint newConstraint = ((SecurityConstraint)((SecurityConstraint)(new SecurityConstraint()).addRolesAllowed(method.getRolesAllowed())).setTransportGuaranteeType(method.getTransportGuaranteeType())).addWebResourceCollection((new WebResourceCollection()).addUrlPatterns((Collection)mappings).addHttpMethod(method.getMethod()));
               builder.addSecurityConstraint(newConstraint);
            }
         } while(securityInfo.getRolesAllowed().isEmpty() && securityInfo.getEmptyRoleSemantic() == SecurityInfo.EmptyRoleSemantic.PERMIT && !methods.isEmpty());

         SecurityConstraint newConstraint = ((SecurityConstraint)((SecurityConstraint)((SecurityConstraint)(new SecurityConstraint()).setEmptyRoleSemantic(securityInfo.getEmptyRoleSemantic())).addRolesAllowed(securityInfo.getRolesAllowed())).setTransportGuaranteeType(securityInfo.getTransportGuaranteeType())).addWebResourceCollection((new WebResourceCollection()).addUrlPatterns((Collection)mappings).addHttpMethodOmissions((Collection)methods));
         builder.addSecurityConstraint(newConstraint);
      }
   }

   private void initializeTempDir(ServletContextImpl servletContext, DeploymentInfo deploymentInfo) {
      if (deploymentInfo.getTempDir() != null) {
         servletContext.setAttribute("javax.servlet.context.tempdir", deploymentInfo.getTempDir());
      } else {
         servletContext.setAttribute("javax.servlet.context.tempdir", new File(SecurityActions.getSystemProperty("java.io.tmpdir")));
      }

   }

   private void initializeMimeMappings(DeploymentImpl deployment, DeploymentInfo deploymentInfo) {
      Map<String, String> mappings = new HashMap(MimeMappings.DEFAULT_MIME_MAPPINGS);
      Iterator var4 = deploymentInfo.getMimeMappings().iterator();

      while(var4.hasNext()) {
         MimeMapping mapping = (MimeMapping)var4.next();
         mappings.put(mapping.getExtension().toLowerCase(Locale.ENGLISH), mapping.getMimeType());
      }

      deployment.setMimeExtensionMappings(mappings);
   }

   private void initializeErrorPages(DeploymentImpl deployment, DeploymentInfo deploymentInfo) {
      Map<Integer, String> codes = new HashMap();
      Map<Class<? extends Throwable>, String> exceptions = new HashMap();
      String defaultErrorPage = null;
      Iterator var6 = deploymentInfo.getErrorPages().iterator();

      while(var6.hasNext()) {
         ErrorPage page = (ErrorPage)var6.next();
         if (page.getExceptionType() != null) {
            exceptions.put(page.getExceptionType(), page.getLocation());
         } else if (page.getErrorCode() != null) {
            codes.put(page.getErrorCode(), page.getLocation());
         } else {
            if (defaultErrorPage != null) {
               throw UndertowServletMessages.MESSAGES.moreThanOneDefaultErrorPage(defaultErrorPage, page.getLocation());
            }

            defaultErrorPage = page.getLocation();
         }
      }

      deployment.setErrorPages(new ErrorPages(codes, exceptions, defaultErrorPage));
   }

   private ApplicationListeners createListeners() {
      List<ManagedListener> managedListeners = new ArrayList();
      Iterator var2 = this.deployment.getDeploymentInfo().getListeners().iterator();

      while(var2.hasNext()) {
         ListenerInfo listener = (ListenerInfo)var2.next();
         managedListeners.add(new ManagedListener(listener, listener.isProgramatic()));
      }

      return new ApplicationListeners(managedListeners, this.deployment.getServletContext());
   }

   private static HttpHandler wrapHandlers(HttpHandler wrapee, List<HandlerWrapper> wrappers) {
      HttpHandler current = wrapee;

      HandlerWrapper wrapper;
      for(Iterator var3 = wrappers.iterator(); var3.hasNext(); current = wrapper.wrap(current)) {
         wrapper = (HandlerWrapper)var3.next();
      }

      return current;
   }

   public HttpHandler start() throws ServletException {
      try {
         return (HttpHandler)this.deployment.createThreadSetupAction(new ThreadSetupHandler.Action<HttpHandler, Object>() {
            public HttpHandler call(HttpServerExchange exchange, Object ignore) throws ServletException {
               DeploymentManagerImpl.this.deployment.getSessionManager().start();
               ArrayList<Lifecycle> lifecycles = new ArrayList(DeploymentManagerImpl.this.deployment.getLifecycleObjects());
               Iterator var4 = lifecycles.iterator();

               while(var4.hasNext()) {
                  Lifecycle object = (Lifecycle)var4.next();
                  object.start();
               }

               HttpHandler root = DeploymentManagerImpl.this.deployment.getHandler();
               TreeMap<Integer, List<ManagedServlet>> loadOnStartup = new TreeMap();
               Iterator var6 = DeploymentManagerImpl.this.deployment.getServlets().getServletHandlers().entrySet().iterator();

               Map.Entry load;
               while(var6.hasNext()) {
                  load = (Map.Entry)var6.next();
                  ManagedServlet servletx = ((ServletHandler)load.getValue()).getManagedServlet();
                  Integer loadOnStartupNumber = servletx.getServletInfo().getLoadOnStartup();
                  if (loadOnStartupNumber != null && loadOnStartupNumber >= 0) {
                     List<ManagedServlet> list = (List)loadOnStartup.get(loadOnStartupNumber);
                     if (list == null) {
                        loadOnStartup.put(loadOnStartupNumber, list = new ArrayList());
                     }

                     ((List)list).add(servletx);
                  }
               }

               var6 = loadOnStartup.entrySet().iterator();

               while(var6.hasNext()) {
                  load = (Map.Entry)var6.next();
                  Iterator var14 = ((List)load.getValue()).iterator();

                  while(var14.hasNext()) {
                     ManagedServlet servlet = (ManagedServlet)var14.next();
                     servlet.createServlet();
                  }
               }

               if (DeploymentManagerImpl.this.deployment.getDeploymentInfo().isEagerFilterInit()) {
                  var6 = DeploymentManagerImpl.this.deployment.getFilters().getFilters().values().iterator();

                  while(var6.hasNext()) {
                     ManagedFilter filter = (ManagedFilter)var6.next();
                     filter.createFilter();
                  }
               }

               DeploymentManagerImpl.this.state = DeploymentManager.State.STARTED;
               return root;
            }
         }).call((HttpServerExchange)null, (Object)null);
      } catch (RuntimeException | ServletException var2) {
         throw var2;
      } catch (Exception var3) {
         throw new RuntimeException(var3);
      }
   }

   public void stop() throws ServletException {
      try {
         this.deployment.createThreadSetupAction(new ThreadSetupHandler.Action<Void, Object>() {
            public Void call(HttpServerExchange exchange, Object ignore) throws ServletException {
               Iterator var3 = DeploymentManagerImpl.this.deployment.getLifecycleObjects().iterator();

               while(var3.hasNext()) {
                  Lifecycle object = (Lifecycle)var3.next();

                  try {
                     object.stop();
                  } catch (Throwable var6) {
                     UndertowServletLogger.ROOT_LOGGER.failedToDestroy(object, var6);
                  }
               }

               DeploymentManagerImpl.this.deployment.getSessionManager().stop();
               DeploymentManagerImpl.this.state = DeploymentManager.State.DEPLOYED;
               return null;
            }
         }).call((HttpServerExchange)null, (Object)null);
      } catch (RuntimeException | ServletException var2) {
         throw var2;
      } catch (Exception var3) {
         throw new RuntimeException(var3);
      }
   }

   private HttpHandler handleDevelopmentModePersistentSessions(HttpHandler next, DeploymentInfo deploymentInfo, SessionManager sessionManager, ServletContextImpl servletContext) {
      SessionPersistenceManager sessionPersistenceManager = deploymentInfo.getSessionPersistenceManager();
      if (sessionPersistenceManager != null) {
         SessionRestoringHandler handler = new SessionRestoringHandler(this.deployment.getDeploymentInfo().getDeploymentName(), sessionManager, servletContext, next, sessionPersistenceManager);
         this.deployment.addLifecycleObjects(handler);
         return handler;
      } else {
         return next;
      }
   }

   public void handleDeploymentSessionConfig(DeploymentInfo deploymentInfo, ServletContextImpl servletContext) {
      SessionCookieConfigImpl sessionCookieConfig = servletContext.getSessionCookieConfig();
      ServletSessionConfig sc = deploymentInfo.getServletSessionConfig();
      if (sc != null) {
         sessionCookieConfig.setName(sc.getName());
         sessionCookieConfig.setComment(sc.getComment());
         sessionCookieConfig.setDomain(sc.getDomain());
         sessionCookieConfig.setHttpOnly(sc.isHttpOnly());
         sessionCookieConfig.setMaxAge(sc.getMaxAge());
         if (sc.getPath() != null) {
            sessionCookieConfig.setPath(sc.getPath());
         } else {
            sessionCookieConfig.setPath(deploymentInfo.getContextPath());
         }

         sessionCookieConfig.setSecure(sc.isSecure());
         if (sc.getSessionTrackingModes() != null) {
            servletContext.setDefaultSessionTrackingModes(new HashSet(sc.getSessionTrackingModes()));
         }
      }

   }

   public void undeploy() {
      try {
         this.deployment.createThreadSetupAction(new ThreadSetupHandler.Action<Void, Object>() {
            public Void call(HttpServerExchange exchange, Object ignore) throws ServletException {
               Iterator var3 = DeploymentManagerImpl.this.deployment.getDeploymentInfo().getDeploymentCompleteListeners().iterator();

               while(var3.hasNext()) {
                  ServletContextListener listener = (ServletContextListener)var3.next();

                  try {
                     listener.contextDestroyed(new ServletContextEvent(DeploymentManagerImpl.this.deployment.getServletContext()));
                  } catch (Throwable var6) {
                     UndertowServletLogger.REQUEST_LOGGER.failedToDestroy(listener, var6);
                  }
               }

               DeploymentManagerImpl.this.deployment.destroy();
               DeploymentManagerImpl.this.deployment = null;
               DeploymentManagerImpl.this.state = DeploymentManager.State.UNDEPLOYED;
               return null;
            }
         }).call((HttpServerExchange)null, (Object)null);
      } catch (Exception var2) {
         throw new RuntimeException(var2);
      }
   }

   public DeploymentManager.State getState() {
      return this.state;
   }

   public Deployment getDeployment() {
      return this.deployment;
   }
}
