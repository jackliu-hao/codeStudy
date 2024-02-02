/*      */ package io.undertow.servlet.api;
/*      */ 
/*      */ import io.undertow.security.api.AuthenticationMechanism;
/*      */ import io.undertow.security.api.AuthenticationMechanismFactory;
/*      */ import io.undertow.security.api.AuthenticationMode;
/*      */ import io.undertow.security.api.NotificationReceiver;
/*      */ import io.undertow.security.api.SecurityContextFactory;
/*      */ import io.undertow.security.idm.IdentityManager;
/*      */ import io.undertow.server.HandlerWrapper;
/*      */ import io.undertow.server.handlers.resource.ResourceManager;
/*      */ import io.undertow.server.session.SecureRandomSessionIdGenerator;
/*      */ import io.undertow.server.session.SessionIdGenerator;
/*      */ import io.undertow.server.session.SessionListener;
/*      */ import io.undertow.servlet.ServletExtension;
/*      */ import io.undertow.servlet.UndertowServletMessages;
/*      */ import io.undertow.servlet.core.DefaultAuthorizationManager;
/*      */ import io.undertow.servlet.core.InMemorySessionManagerFactory;
/*      */ import io.undertow.servlet.util.DefaultClassIntrospector;
/*      */ import io.undertow.util.ImmediateAuthenticationMechanismFactory;
/*      */ import java.io.File;
/*      */ import java.nio.file.Path;
/*      */ import java.nio.file.Paths;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Arrays;
/*      */ import java.util.Collection;
/*      */ import java.util.HashMap;
/*      */ import java.util.HashSet;
/*      */ import java.util.List;
/*      */ import java.util.Locale;
/*      */ import java.util.Map;
/*      */ import java.util.Set;
/*      */ import java.util.concurrent.ConcurrentMap;
/*      */ import java.util.concurrent.Executor;
/*      */ import javax.servlet.DispatcherType;
/*      */ import javax.servlet.MultipartConfigElement;
/*      */ import javax.servlet.ServletContextListener;
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class DeploymentInfo
/*      */   implements Cloneable
/*      */ {
/*      */   private static final int DEFAULT_MAJOR_VERSION;
/*      */   private String deploymentName;
/*      */   private String displayName;
/*      */   private String contextPath;
/*      */   private ClassLoader classLoader;
/*      */   
/*      */   static {
/*   74 */     Package servletPackage = ServletContextListener.class.getPackage();
/*   75 */     DEFAULT_MAJOR_VERSION = servletPackage.getName().startsWith("jakarta.") ? 5 : 4;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*   82 */   private ResourceManager resourceManager = ResourceManager.EMPTY_RESOURCE_MANAGER;
/*   83 */   private ClassIntrospecter classIntrospecter = (ClassIntrospecter)DefaultClassIntrospector.INSTANCE;
/*   84 */   private int majorVersion = DEFAULT_MAJOR_VERSION;
/*   85 */   private int minorVersion = 0;
/*   86 */   private int containerMajorVersion = DEFAULT_MAJOR_VERSION;
/*   87 */   private int containerMinorVersion = 0;
/*      */   private Executor executor;
/*      */   private Executor asyncExecutor;
/*      */   private Path tempDir;
/*      */   private JspConfigDescriptor jspConfigDescriptor;
/*      */   private DefaultServletConfig defaultServletConfig;
/*   93 */   private SessionManagerFactory sessionManagerFactory = (SessionManagerFactory)new InMemorySessionManagerFactory();
/*      */   private LoginConfig loginConfig;
/*      */   private IdentityManager identityManager;
/*      */   private ConfidentialPortManager confidentialPortManager;
/*      */   private boolean allowNonStandardWrappers = false;
/*   98 */   private int defaultSessionTimeout = 1800;
/*      */   private ConcurrentMap<String, Object> servletContextAttributeBackingMap;
/*      */   private ServletSessionConfig servletSessionConfig;
/*  101 */   private String hostName = "localhost";
/*      */   private boolean denyUncoveredHttpMethods = false;
/*  103 */   private ServletStackTraces servletStackTraces = ServletStackTraces.LOCAL_ONLY;
/*      */   private boolean invalidateSessionOnLogout = false;
/*  105 */   private int defaultCookieVersion = 0;
/*      */   private SessionPersistenceManager sessionPersistenceManager;
/*      */   private String defaultEncoding;
/*      */   private String defaultRequestEncoding;
/*      */   private String defaultResponseEncoding;
/*  110 */   private String urlEncoding = null;
/*      */   private boolean ignoreFlush = false;
/*  112 */   private AuthorizationManager authorizationManager = (AuthorizationManager)DefaultAuthorizationManager.INSTANCE;
/*      */   private AuthenticationMechanism jaspiAuthenticationMechanism;
/*      */   private SecurityContextFactory securityContextFactory;
/*  115 */   private String serverName = "Undertow";
/*  116 */   private MetricsCollector metricsCollector = null;
/*  117 */   private SessionConfigWrapper sessionConfigWrapper = null;
/*      */   private boolean eagerFilterInit = false;
/*      */   private boolean disableCachingForSecuredPages = true;
/*      */   private boolean escapeErrorMessage = true;
/*      */   private boolean sendCustomReasonPhraseOnError = false;
/*      */   private boolean useCachedAuthenticationMechanism = true;
/*      */   private boolean preservePathOnForward = true;
/*  124 */   private AuthenticationMode authenticationMode = AuthenticationMode.PRO_ACTIVE;
/*      */   private ExceptionHandler exceptionHandler;
/*  126 */   private final Map<String, ServletInfo> servlets = new HashMap<>();
/*  127 */   private final Map<String, FilterInfo> filters = new HashMap<>();
/*  128 */   private final List<FilterMappingInfo> filterServletNameMappings = new ArrayList<>();
/*  129 */   private final List<FilterMappingInfo> filterUrlMappings = new ArrayList<>();
/*  130 */   private final List<ListenerInfo> listeners = new ArrayList<>();
/*  131 */   private final List<ServletContainerInitializerInfo> servletContainerInitializers = new ArrayList<>();
/*  132 */   private final List<ThreadSetupHandler> threadSetupActions = new ArrayList<>();
/*  133 */   private final Map<String, String> initParameters = new HashMap<>();
/*  134 */   private final Map<String, Object> servletContextAttributes = new HashMap<>();
/*  135 */   private final Map<String, String> localeCharsetMapping = new HashMap<>();
/*  136 */   private final List<String> welcomePages = new ArrayList<>();
/*  137 */   private final List<ErrorPage> errorPages = new ArrayList<>();
/*  138 */   private final List<MimeMapping> mimeMappings = new ArrayList<>();
/*  139 */   private final List<SecurityConstraint> securityConstraints = new ArrayList<>();
/*  140 */   private final Set<String> securityRoles = new HashSet<>();
/*  141 */   private final List<NotificationReceiver> notificationReceivers = new ArrayList<>();
/*  142 */   private final Map<String, AuthenticationMechanismFactory> authenticationMechanisms = new HashMap<>();
/*  143 */   private final List<LifecycleInterceptor> lifecycleInterceptors = new ArrayList<>();
/*  144 */   private final List<SessionListener> sessionListeners = new ArrayList<>();
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  149 */   private final List<ServletExtension> servletExtensions = new ArrayList<>();
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  154 */   private final Map<String, Set<String>> principalVersusRolesMap = new HashMap<>();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  160 */   private final List<HandlerWrapper> initialHandlerChainWrappers = new ArrayList<>();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  166 */   private final List<HandlerWrapper> outerHandlerChainWrappers = new ArrayList<>();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  172 */   private final List<HandlerWrapper> innerHandlerChainWrappers = new ArrayList<>();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  179 */   private HandlerWrapper initialSecurityWrapper = null;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  185 */   private final List<HandlerWrapper> securityWrappers = new ArrayList<>();
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private MultipartConfigElement defaultMultipartConfig;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  195 */   private int contentTypeCacheSize = 100;
/*      */   
/*      */   private boolean changeSessionIdOnLogin = true;
/*      */   
/*  199 */   private SessionIdGenerator sessionIdGenerator = (SessionIdGenerator)new SecureRandomSessionIdGenerator();
/*      */ 
/*      */   
/*      */   private CrawlerSessionManagerConfig crawlerSessionManagerConfig;
/*      */ 
/*      */   
/*      */   private boolean securityDisabled;
/*      */ 
/*      */   
/*      */   private boolean checkOtherSessionManagers = true;
/*      */   
/*  210 */   private final List<ServletContextListener> deploymentCompleteListeners = new ArrayList<>();
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  215 */   private final Map<String, String> preCompressedResources = new HashMap<>();
/*      */   
/*      */   public void validate() {
/*  218 */     if (this.deploymentName == null) {
/*  219 */       throw UndertowServletMessages.MESSAGES.paramCannotBeNull("deploymentName");
/*      */     }
/*  221 */     if (this.contextPath == null) {
/*  222 */       throw UndertowServletMessages.MESSAGES.paramCannotBeNull("contextName");
/*      */     }
/*  224 */     if (this.classLoader == null) {
/*  225 */       throw UndertowServletMessages.MESSAGES.paramCannotBeNull("classLoader");
/*      */     }
/*  227 */     if (this.resourceManager == null) {
/*  228 */       throw UndertowServletMessages.MESSAGES.paramCannotBeNull("resourceManager");
/*      */     }
/*  230 */     if (this.classIntrospecter == null) {
/*  231 */       throw UndertowServletMessages.MESSAGES.paramCannotBeNull("classIntrospecter");
/*      */     }
/*      */     
/*  234 */     for (ServletInfo servlet : this.servlets.values()) {
/*  235 */       servlet.validate();
/*      */     }
/*  237 */     for (FilterInfo filter : this.filters.values()) {
/*  238 */       filter.validate();
/*      */     }
/*  240 */     for (FilterMappingInfo mapping : this.filterServletNameMappings) {
/*  241 */       if (!this.filters.containsKey(mapping.getFilterName())) {
/*  242 */         throw UndertowServletMessages.MESSAGES.filterNotFound(mapping.getFilterName(), mapping.getMappingType() + " - " + mapping.getMapping());
/*      */       }
/*      */     } 
/*  245 */     for (FilterMappingInfo mapping : this.filterUrlMappings) {
/*  246 */       if (!this.filters.containsKey(mapping.getFilterName())) {
/*  247 */         throw UndertowServletMessages.MESSAGES.filterNotFound(mapping.getFilterName(), mapping.getMappingType() + " - " + mapping.getMapping());
/*      */       }
/*      */     } 
/*      */   }
/*      */   
/*      */   public String getDeploymentName() {
/*  253 */     return this.deploymentName;
/*      */   }
/*      */   
/*      */   public DeploymentInfo setDeploymentName(String deploymentName) {
/*  257 */     this.deploymentName = deploymentName;
/*  258 */     return this;
/*      */   }
/*      */   
/*      */   public String getDisplayName() {
/*  262 */     return this.displayName;
/*      */   }
/*      */   
/*      */   public DeploymentInfo setDisplayName(String displayName) {
/*  266 */     this.displayName = displayName;
/*  267 */     return this;
/*      */   }
/*      */   
/*      */   public String getContextPath() {
/*  271 */     return this.contextPath;
/*      */   }
/*      */   
/*      */   public DeploymentInfo setContextPath(String contextPath) {
/*  275 */     if (contextPath != null && contextPath.isEmpty()) {
/*  276 */       this.contextPath = "/";
/*      */     } else {
/*  278 */       this.contextPath = contextPath;
/*      */     } 
/*  280 */     return this;
/*      */   }
/*      */   
/*      */   public ClassLoader getClassLoader() {
/*  284 */     return this.classLoader;
/*      */   }
/*      */   
/*      */   public DeploymentInfo setClassLoader(ClassLoader classLoader) {
/*  288 */     this.classLoader = classLoader;
/*  289 */     return this;
/*      */   }
/*      */   
/*      */   public ResourceManager getResourceManager() {
/*  293 */     return this.resourceManager;
/*      */   }
/*      */   
/*      */   public DeploymentInfo setResourceManager(ResourceManager resourceManager) {
/*  297 */     this.resourceManager = resourceManager;
/*  298 */     return this;
/*      */   }
/*      */   
/*      */   public ClassIntrospecter getClassIntrospecter() {
/*  302 */     return this.classIntrospecter;
/*      */   }
/*      */   
/*      */   public DeploymentInfo setClassIntrospecter(ClassIntrospecter classIntrospecter) {
/*  306 */     this.classIntrospecter = classIntrospecter;
/*  307 */     return this;
/*      */   }
/*      */   
/*      */   public boolean isAllowNonStandardWrappers() {
/*  311 */     return this.allowNonStandardWrappers;
/*      */   }
/*      */   
/*      */   public DeploymentInfo setAllowNonStandardWrappers(boolean allowNonStandardWrappers) {
/*  315 */     this.allowNonStandardWrappers = allowNonStandardWrappers;
/*  316 */     return this;
/*      */   }
/*      */   
/*      */   public int getDefaultSessionTimeout() {
/*  320 */     return this.defaultSessionTimeout;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public DeploymentInfo setDefaultSessionTimeout(int defaultSessionTimeout) {
/*  327 */     this.defaultSessionTimeout = defaultSessionTimeout;
/*  328 */     return this;
/*      */   }
/*      */   
/*      */   public String getDefaultEncoding() {
/*  332 */     return this.defaultEncoding;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public DeploymentInfo setDefaultEncoding(String defaultEncoding) {
/*  341 */     this.defaultEncoding = defaultEncoding;
/*  342 */     return this;
/*      */   }
/*      */   
/*      */   public String getUrlEncoding() {
/*  346 */     return this.urlEncoding;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public DeploymentInfo setUrlEncoding(String urlEncoding) {
/*  356 */     this.urlEncoding = urlEncoding;
/*  357 */     return this;
/*      */   }
/*      */   
/*      */   public DeploymentInfo addServlet(ServletInfo servlet) {
/*  361 */     this.servlets.put(servlet.getName(), servlet);
/*  362 */     return this;
/*      */   }
/*      */   
/*      */   public DeploymentInfo addServlets(ServletInfo... servlets) {
/*  366 */     for (ServletInfo servlet : servlets) {
/*  367 */       addServlet(servlet);
/*      */     }
/*  369 */     return this;
/*      */   }
/*      */   
/*      */   public DeploymentInfo addServlets(Collection<ServletInfo> servlets) {
/*  373 */     for (ServletInfo servlet : servlets) {
/*  374 */       addServlet(servlet);
/*      */     }
/*  376 */     return this;
/*      */   }
/*      */   
/*      */   public Map<String, ServletInfo> getServlets() {
/*  380 */     return this.servlets;
/*      */   }
/*      */ 
/*      */   
/*      */   public DeploymentInfo addFilter(FilterInfo filter) {
/*  385 */     this.filters.put(filter.getName(), filter);
/*  386 */     return this;
/*      */   }
/*      */   
/*      */   public DeploymentInfo addFilters(FilterInfo... filters) {
/*  390 */     for (FilterInfo filter : filters) {
/*  391 */       addFilter(filter);
/*      */     }
/*  393 */     return this;
/*      */   }
/*      */   
/*      */   public DeploymentInfo addFilters(Collection<FilterInfo> filters) {
/*  397 */     for (FilterInfo filter : filters) {
/*  398 */       addFilter(filter);
/*      */     }
/*  400 */     return this;
/*      */   }
/*      */   
/*      */   public Map<String, FilterInfo> getFilters() {
/*  404 */     return this.filters;
/*      */   }
/*      */   
/*      */   public DeploymentInfo addFilterUrlMapping(String filterName, String mapping, DispatcherType dispatcher) {
/*  408 */     this.filterUrlMappings.add(new FilterMappingInfo(filterName, FilterMappingInfo.MappingType.URL, mapping, dispatcher));
/*  409 */     return this;
/*      */   }
/*      */   
/*      */   public DeploymentInfo addFilterServletNameMapping(String filterName, String mapping, DispatcherType dispatcher) {
/*  413 */     this.filterServletNameMappings.add(new FilterMappingInfo(filterName, FilterMappingInfo.MappingType.SERVLET, mapping, dispatcher));
/*  414 */     return this;
/*      */   }
/*      */   
/*      */   public DeploymentInfo insertFilterUrlMapping(int pos, String filterName, String mapping, DispatcherType dispatcher) {
/*  418 */     this.filterUrlMappings.add(pos, new FilterMappingInfo(filterName, FilterMappingInfo.MappingType.URL, mapping, dispatcher));
/*  419 */     return this;
/*      */   }
/*      */   
/*      */   public DeploymentInfo insertFilterServletNameMapping(int pos, String filterName, String mapping, DispatcherType dispatcher) {
/*  423 */     this.filterServletNameMappings.add(pos, new FilterMappingInfo(filterName, FilterMappingInfo.MappingType.SERVLET, mapping, dispatcher));
/*  424 */     return this;
/*      */   }
/*      */   
/*      */   public List<FilterMappingInfo> getFilterMappings() {
/*  428 */     ArrayList<FilterMappingInfo> ret = new ArrayList<>(this.filterUrlMappings);
/*  429 */     ret.addAll(this.filterServletNameMappings);
/*  430 */     return ret;
/*      */   }
/*      */ 
/*      */   
/*      */   public DeploymentInfo addListener(ListenerInfo listener) {
/*  435 */     this.listeners.add(listener);
/*  436 */     return this;
/*      */   }
/*      */   
/*      */   public DeploymentInfo addListeners(ListenerInfo... listeners) {
/*  440 */     this.listeners.addAll(Arrays.asList(listeners));
/*  441 */     return this;
/*      */   }
/*      */   
/*      */   public DeploymentInfo addListeners(Collection<ListenerInfo> listeners) {
/*  445 */     this.listeners.addAll(listeners);
/*  446 */     return this;
/*      */   }
/*      */   
/*      */   public List<ListenerInfo> getListeners() {
/*  450 */     return this.listeners;
/*      */   }
/*      */   
/*      */   public int getMajorVersion() {
/*  454 */     return this.majorVersion;
/*      */   }
/*      */   
/*      */   public DeploymentInfo setMajorVersion(int majorVersion) {
/*  458 */     this.majorVersion = majorVersion;
/*  459 */     return this;
/*      */   }
/*      */   
/*      */   public int getMinorVersion() {
/*  463 */     return this.minorVersion;
/*      */   }
/*      */   
/*      */   public DeploymentInfo setMinorVersion(int minorVersion) {
/*  467 */     this.minorVersion = minorVersion;
/*  468 */     return this;
/*      */   }
/*      */   
/*      */   public DeploymentInfo addServletContainerInitializer(ServletContainerInitializerInfo servletContainerInitializer) {
/*  472 */     this.servletContainerInitializers.add(servletContainerInitializer);
/*  473 */     return this;
/*      */   }
/*      */   
/*      */   @Deprecated
/*      */   public DeploymentInfo addServletContainerInitalizer(ServletContainerInitializerInfo servletContainerInitializer) {
/*  478 */     return addServletContainerInitializer(servletContainerInitializer);
/*      */   }
/*      */   
/*      */   public DeploymentInfo addServletContainerInitializers(ServletContainerInitializerInfo... servletContainerInitializer) {
/*  482 */     this.servletContainerInitializers.addAll(Arrays.asList(servletContainerInitializer));
/*  483 */     return this;
/*      */   }
/*      */   
/*      */   @Deprecated
/*      */   public DeploymentInfo addServletContainerInitalizers(ServletContainerInitializerInfo... servletContainerInitializer) {
/*  488 */     return addServletContainerInitializers(servletContainerInitializer);
/*      */   }
/*      */   
/*      */   public DeploymentInfo addServletContainerInitializers(List<ServletContainerInitializerInfo> servletContainerInitializer) {
/*  492 */     this.servletContainerInitializers.addAll(servletContainerInitializer);
/*  493 */     return this;
/*      */   }
/*      */   
/*      */   @Deprecated
/*      */   public DeploymentInfo addServletContainerInitalizers(List<ServletContainerInitializerInfo> servletContainerInitializers) {
/*  498 */     return addServletContainerInitializers(servletContainerInitializers);
/*      */   }
/*      */   
/*      */   public List<ServletContainerInitializerInfo> getServletContainerInitializers() {
/*  502 */     return this.servletContainerInitializers;
/*      */   }
/*      */   
/*      */   @Deprecated
/*      */   public DeploymentInfo addThreadSetupAction(ThreadSetupAction action) {
/*  507 */     this.threadSetupActions.add(new LegacyThreadSetupActionWrapper(action));
/*  508 */     return this;
/*      */   }
/*      */   
/*      */   public DeploymentInfo addThreadSetupAction(ThreadSetupHandler action) {
/*  512 */     this.threadSetupActions.add(action);
/*  513 */     return this;
/*      */   }
/*      */   
/*      */   public List<ThreadSetupHandler> getThreadSetupActions() {
/*  517 */     return this.threadSetupActions;
/*      */   }
/*      */   
/*      */   public boolean isEagerFilterInit() {
/*  521 */     return this.eagerFilterInit;
/*      */   }
/*      */   
/*      */   public DeploymentInfo setEagerFilterInit(boolean eagerFilterInit) {
/*  525 */     this.eagerFilterInit = eagerFilterInit;
/*  526 */     return this;
/*      */   }
/*      */   
/*      */   public DeploymentInfo addInitParameter(String name, String value) {
/*  530 */     this.initParameters.put(name, value);
/*  531 */     return this;
/*      */   }
/*      */   
/*      */   public Map<String, String> getInitParameters() {
/*  535 */     return this.initParameters;
/*      */   }
/*      */   
/*      */   public DeploymentInfo addServletContextAttribute(String name, Object value) {
/*  539 */     this.servletContextAttributes.put(name, value);
/*  540 */     return this;
/*      */   }
/*      */   
/*      */   public Map<String, Object> getServletContextAttributes() {
/*  544 */     return this.servletContextAttributes;
/*      */   }
/*      */   
/*      */   public DeploymentInfo addWelcomePage(String welcomePage) {
/*  548 */     this.welcomePages.add(welcomePage);
/*  549 */     return this;
/*      */   }
/*      */   
/*      */   public DeploymentInfo addWelcomePages(String... welcomePages) {
/*  553 */     this.welcomePages.addAll(Arrays.asList(welcomePages));
/*  554 */     return this;
/*      */   }
/*      */   
/*      */   public DeploymentInfo addWelcomePages(Collection<String> welcomePages) {
/*  558 */     this.welcomePages.addAll(welcomePages);
/*  559 */     return this;
/*      */   }
/*      */   
/*      */   public List<String> getWelcomePages() {
/*  563 */     return this.welcomePages;
/*      */   }
/*      */   
/*      */   public DeploymentInfo addErrorPage(ErrorPage errorPage) {
/*  567 */     this.errorPages.add(errorPage);
/*  568 */     return this;
/*      */   }
/*      */   
/*      */   public DeploymentInfo addErrorPages(ErrorPage... errorPages) {
/*  572 */     this.errorPages.addAll(Arrays.asList(errorPages));
/*  573 */     return this;
/*      */   }
/*      */   
/*      */   public DeploymentInfo addErrorPages(Collection<ErrorPage> errorPages) {
/*  577 */     this.errorPages.addAll(errorPages);
/*  578 */     return this;
/*      */   }
/*      */   
/*      */   public List<ErrorPage> getErrorPages() {
/*  582 */     return this.errorPages;
/*      */   }
/*      */   
/*      */   public DeploymentInfo addMimeMapping(MimeMapping mimeMappings) {
/*  586 */     this.mimeMappings.add(mimeMappings);
/*  587 */     return this;
/*      */   }
/*      */   
/*      */   public DeploymentInfo addMimeMappings(MimeMapping... mimeMappings) {
/*  591 */     this.mimeMappings.addAll(Arrays.asList(mimeMappings));
/*  592 */     return this;
/*      */   }
/*      */   
/*      */   public DeploymentInfo addMimeMappings(Collection<MimeMapping> mimeMappings) {
/*  596 */     this.mimeMappings.addAll(mimeMappings);
/*  597 */     return this;
/*      */   }
/*      */   
/*      */   public List<MimeMapping> getMimeMappings() {
/*  601 */     return this.mimeMappings;
/*      */   }
/*      */ 
/*      */   
/*      */   public DeploymentInfo addSecurityConstraint(SecurityConstraint securityConstraint) {
/*  606 */     this.securityConstraints.add(securityConstraint);
/*  607 */     return this;
/*      */   }
/*      */   
/*      */   public DeploymentInfo addSecurityConstraints(SecurityConstraint... securityConstraints) {
/*  611 */     this.securityConstraints.addAll(Arrays.asList(securityConstraints));
/*  612 */     return this;
/*      */   }
/*      */   
/*      */   public DeploymentInfo addSecurityConstraints(Collection<SecurityConstraint> securityConstraints) {
/*  616 */     this.securityConstraints.addAll(securityConstraints);
/*  617 */     return this;
/*      */   }
/*      */   
/*      */   public List<SecurityConstraint> getSecurityConstraints() {
/*  621 */     return this.securityConstraints;
/*      */   }
/*      */   
/*      */   public Executor getExecutor() {
/*  625 */     return this.executor;
/*      */   }
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
/*      */   public DeploymentInfo setExecutor(Executor executor) {
/*  640 */     this.executor = executor;
/*  641 */     return this;
/*      */   }
/*      */   
/*      */   public Executor getAsyncExecutor() {
/*  645 */     return this.asyncExecutor;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public DeploymentInfo setAsyncExecutor(Executor asyncExecutor) {
/*  656 */     this.asyncExecutor = asyncExecutor;
/*  657 */     return this;
/*      */   }
/*      */   
/*      */   public File getTempDir() {
/*  661 */     if (this.tempDir == null) {
/*  662 */       return null;
/*      */     }
/*  664 */     return this.tempDir.toFile();
/*      */   }
/*      */   
/*      */   public Path getTempPath() {
/*  668 */     return this.tempDir;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Path requireTempPath() {
/*  677 */     if (this.tempDir != null) {
/*  678 */       return this.tempDir;
/*      */     }
/*  680 */     return Paths.get(SecurityActions.getSystemProperty("java.io.tmpdir"), new String[0]);
/*      */   }
/*      */   
/*      */   public DeploymentInfo setTempDir(File tempDir) {
/*  684 */     this.tempDir = (tempDir != null) ? tempDir.toPath() : null;
/*  685 */     return this;
/*      */   }
/*      */   
/*      */   public DeploymentInfo setTempDir(Path tempDir) {
/*  689 */     this.tempDir = tempDir;
/*  690 */     return this;
/*      */   }
/*      */   
/*      */   public boolean isIgnoreFlush() {
/*  694 */     return this.ignoreFlush;
/*      */   }
/*      */   
/*      */   public DeploymentInfo setIgnoreFlush(boolean ignoreFlush) {
/*  698 */     this.ignoreFlush = ignoreFlush;
/*  699 */     return this;
/*      */   }
/*      */   
/*      */   public JspConfigDescriptor getJspConfigDescriptor() {
/*  703 */     return this.jspConfigDescriptor;
/*      */   }
/*      */   
/*      */   public DeploymentInfo setJspConfigDescriptor(JspConfigDescriptor jspConfigDescriptor) {
/*  707 */     this.jspConfigDescriptor = jspConfigDescriptor;
/*  708 */     return this;
/*      */   }
/*      */   
/*      */   public DefaultServletConfig getDefaultServletConfig() {
/*  712 */     return this.defaultServletConfig;
/*      */   }
/*      */   
/*      */   public DeploymentInfo setDefaultServletConfig(DefaultServletConfig defaultServletConfig) {
/*  716 */     this.defaultServletConfig = defaultServletConfig;
/*  717 */     return this;
/*      */   }
/*      */   
/*      */   public DeploymentInfo addLocaleCharsetMapping(String locale, String charset) {
/*  721 */     this.localeCharsetMapping.put(locale, charset);
/*  722 */     return this;
/*      */   }
/*      */   
/*      */   public Map<String, String> getLocaleCharsetMapping() {
/*  726 */     return this.localeCharsetMapping;
/*      */   }
/*      */   
/*      */   public SessionManagerFactory getSessionManagerFactory() {
/*  730 */     return this.sessionManagerFactory;
/*      */   }
/*      */   
/*      */   public DeploymentInfo setSessionManagerFactory(SessionManagerFactory sessionManagerFactory) {
/*  734 */     this.sessionManagerFactory = sessionManagerFactory;
/*  735 */     return this;
/*      */   }
/*      */   
/*      */   public LoginConfig getLoginConfig() {
/*  739 */     return this.loginConfig;
/*      */   }
/*      */   
/*      */   public DeploymentInfo setLoginConfig(LoginConfig loginConfig) {
/*  743 */     this.loginConfig = loginConfig;
/*  744 */     return this;
/*      */   }
/*      */   
/*      */   public IdentityManager getIdentityManager() {
/*  748 */     return this.identityManager;
/*      */   }
/*      */   
/*      */   public DeploymentInfo setIdentityManager(IdentityManager identityManager) {
/*  752 */     this.identityManager = identityManager;
/*  753 */     return this;
/*      */   }
/*      */   
/*      */   public ConfidentialPortManager getConfidentialPortManager() {
/*  757 */     return this.confidentialPortManager;
/*      */   }
/*      */   
/*      */   public DeploymentInfo setConfidentialPortManager(ConfidentialPortManager confidentialPortManager) {
/*  761 */     this.confidentialPortManager = confidentialPortManager;
/*  762 */     return this;
/*      */   }
/*      */   
/*      */   public DeploymentInfo addSecurityRole(String role) {
/*  766 */     this.securityRoles.add(role);
/*  767 */     return this;
/*      */   }
/*      */   
/*      */   public DeploymentInfo addSecurityRoles(String... roles) {
/*  771 */     this.securityRoles.addAll(Arrays.asList(roles));
/*  772 */     return this;
/*      */   }
/*      */   
/*      */   public DeploymentInfo addSecurityRoles(Collection<String> roles) {
/*  776 */     this.securityRoles.addAll(roles);
/*  777 */     return this;
/*      */   }
/*      */   
/*      */   public Set<String> getSecurityRoles() {
/*  781 */     return this.securityRoles;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public DeploymentInfo addOuterHandlerChainWrapper(HandlerWrapper wrapper) {
/*  792 */     this.outerHandlerChainWrappers.add(wrapper);
/*  793 */     return this;
/*      */   }
/*      */   
/*      */   public List<HandlerWrapper> getOuterHandlerChainWrappers() {
/*  797 */     return this.outerHandlerChainWrappers;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public DeploymentInfo addInnerHandlerChainWrapper(HandlerWrapper wrapper) {
/*  807 */     this.innerHandlerChainWrappers.add(wrapper);
/*  808 */     return this;
/*      */   }
/*      */   
/*      */   public List<HandlerWrapper> getInnerHandlerChainWrappers() {
/*  812 */     return this.innerHandlerChainWrappers;
/*      */   }
/*      */   
/*      */   public DeploymentInfo addInitialHandlerChainWrapper(HandlerWrapper wrapper) {
/*  816 */     this.initialHandlerChainWrappers.add(wrapper);
/*  817 */     return this;
/*      */   }
/*      */   
/*      */   public List<HandlerWrapper> getInitialHandlerChainWrappers() {
/*  821 */     return this.initialHandlerChainWrappers;
/*      */   }
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
/*      */   public DeploymentInfo setInitialSecurityWrapper(HandlerWrapper wrapper) {
/*  837 */     this.initialSecurityWrapper = wrapper;
/*      */     
/*  839 */     return this;
/*      */   }
/*      */   
/*      */   public HandlerWrapper getInitialSecurityWrapper() {
/*  843 */     return this.initialSecurityWrapper;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public DeploymentInfo addSecurityWrapper(HandlerWrapper wrapper) {
/*  853 */     this.securityWrappers.add(wrapper);
/*  854 */     return this;
/*      */   }
/*      */   
/*      */   public List<HandlerWrapper> getSecurityWrappers() {
/*  858 */     return this.securityWrappers;
/*      */   }
/*      */   
/*      */   public DeploymentInfo addNotificationReceiver(NotificationReceiver notificationReceiver) {
/*  862 */     this.notificationReceivers.add(notificationReceiver);
/*  863 */     return this;
/*      */   }
/*      */   
/*      */   public DeploymentInfo addNotificactionReceivers(NotificationReceiver... notificationReceivers) {
/*  867 */     this.notificationReceivers.addAll(Arrays.asList(notificationReceivers));
/*  868 */     return this;
/*      */   }
/*      */   
/*      */   public DeploymentInfo addNotificationReceivers(Collection<NotificationReceiver> notificationReceivers) {
/*  872 */     this.notificationReceivers.addAll(notificationReceivers);
/*  873 */     return this;
/*      */   }
/*      */   
/*      */   public List<NotificationReceiver> getNotificationReceivers() {
/*  877 */     return this.notificationReceivers;
/*      */   }
/*      */   
/*      */   public ConcurrentMap<String, Object> getServletContextAttributeBackingMap() {
/*  881 */     return this.servletContextAttributeBackingMap;
/*      */   }
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
/*      */   public DeploymentInfo setServletContextAttributeBackingMap(ConcurrentMap<String, Object> servletContextAttributeBackingMap) {
/*  895 */     this.servletContextAttributeBackingMap = servletContextAttributeBackingMap;
/*  896 */     return this;
/*      */   }
/*      */   
/*      */   public ServletSessionConfig getServletSessionConfig() {
/*  900 */     return this.servletSessionConfig;
/*      */   }
/*      */   
/*      */   public DeploymentInfo setServletSessionConfig(ServletSessionConfig servletSessionConfig) {
/*  904 */     this.servletSessionConfig = servletSessionConfig;
/*  905 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getHostName() {
/*  912 */     return this.hostName;
/*      */   }
/*      */   
/*      */   public DeploymentInfo setHostName(String hostName) {
/*  916 */     this.hostName = hostName;
/*  917 */     return this;
/*      */   }
/*      */   
/*      */   public boolean isDenyUncoveredHttpMethods() {
/*  921 */     return this.denyUncoveredHttpMethods;
/*      */   }
/*      */   
/*      */   public DeploymentInfo setDenyUncoveredHttpMethods(boolean denyUncoveredHttpMethods) {
/*  925 */     this.denyUncoveredHttpMethods = denyUncoveredHttpMethods;
/*  926 */     return this;
/*      */   }
/*      */   
/*      */   public ServletStackTraces getServletStackTraces() {
/*  930 */     return this.servletStackTraces;
/*      */   }
/*      */   
/*      */   public DeploymentInfo setServletStackTraces(ServletStackTraces servletStackTraces) {
/*  934 */     this.servletStackTraces = servletStackTraces;
/*  935 */     return this;
/*      */   }
/*      */   
/*      */   public boolean isInvalidateSessionOnLogout() {
/*  939 */     return this.invalidateSessionOnLogout;
/*      */   }
/*      */   
/*      */   public DeploymentInfo setInvalidateSessionOnLogout(boolean invalidateSessionOnLogout) {
/*  943 */     this.invalidateSessionOnLogout = invalidateSessionOnLogout;
/*  944 */     return this;
/*      */   }
/*      */   
/*      */   public int getDefaultCookieVersion() {
/*  948 */     return this.defaultCookieVersion;
/*      */   }
/*      */   
/*      */   public DeploymentInfo setDefaultCookieVersion(int defaultCookieVersion) {
/*  952 */     this.defaultCookieVersion = defaultCookieVersion;
/*  953 */     return this;
/*      */   }
/*      */   
/*      */   public SessionPersistenceManager getSessionPersistenceManager() {
/*  957 */     return this.sessionPersistenceManager;
/*      */   }
/*      */   
/*      */   public DeploymentInfo setSessionPersistenceManager(SessionPersistenceManager sessionPersistenceManager) {
/*  961 */     this.sessionPersistenceManager = sessionPersistenceManager;
/*  962 */     return this;
/*      */   }
/*      */   
/*      */   public AuthorizationManager getAuthorizationManager() {
/*  966 */     return this.authorizationManager;
/*      */   }
/*      */   
/*      */   public DeploymentInfo setAuthorizationManager(AuthorizationManager authorizationManager) {
/*  970 */     this.authorizationManager = authorizationManager;
/*  971 */     return this;
/*      */   }
/*      */   
/*      */   public DeploymentInfo addPrincipalVsRoleMapping(String principal, String mapping) {
/*  975 */     Set<String> set = this.principalVersusRolesMap.get(principal);
/*  976 */     if (set == null) {
/*  977 */       this.principalVersusRolesMap.put(principal, set = new HashSet<>());
/*      */     }
/*  979 */     set.add(mapping);
/*  980 */     return this;
/*      */   }
/*      */   
/*      */   public DeploymentInfo addPrincipalVsRoleMappings(String principal, String... mappings) {
/*  984 */     Set<String> set = this.principalVersusRolesMap.get(principal);
/*  985 */     if (set == null) {
/*  986 */       this.principalVersusRolesMap.put(principal, set = new HashSet<>());
/*      */     }
/*  988 */     set.addAll(Arrays.asList(mappings));
/*  989 */     return this;
/*      */   }
/*      */   
/*      */   public DeploymentInfo addPrincipalVsRoleMappings(String principal, Collection<String> mappings) {
/*  993 */     Set<String> set = this.principalVersusRolesMap.get(principal);
/*  994 */     if (set == null) {
/*  995 */       this.principalVersusRolesMap.put(principal, set = new HashSet<>());
/*      */     }
/*  997 */     set.addAll(mappings);
/*  998 */     return this;
/*      */   }
/*      */   
/*      */   public Map<String, Set<String>> getPrincipalVersusRolesMap() {
/* 1002 */     return this.principalVersusRolesMap;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public DeploymentInfo clearLoginMethods() {
/* 1011 */     if (this.loginConfig != null) {
/* 1012 */       this.loginConfig.getAuthMethods().clear();
/*      */     }
/* 1014 */     return this;
/*      */   }
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
/*      */   public DeploymentInfo addFirstAuthenticationMechanism(String name, AuthenticationMechanism mechanism) {
/* 1033 */     this.authenticationMechanisms.put(name, new ImmediateAuthenticationMechanismFactory(mechanism));
/* 1034 */     if (this.loginConfig == null) {
/* 1035 */       this.loginConfig = new LoginConfig(null);
/*      */     }
/* 1037 */     this.loginConfig.addFirstAuthMethod(new AuthMethodConfig(name));
/* 1038 */     return this;
/*      */   }
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
/*      */   public DeploymentInfo addLastAuthenticationMechanism(String name, AuthenticationMechanism mechanism) {
/* 1057 */     this.authenticationMechanisms.put(name, new ImmediateAuthenticationMechanismFactory(mechanism));
/* 1058 */     if (this.loginConfig == null) {
/* 1059 */       this.loginConfig = new LoginConfig(null);
/*      */     }
/* 1061 */     this.loginConfig.addLastAuthMethod(new AuthMethodConfig(name));
/* 1062 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public DeploymentInfo addAuthenticationMechanism(String name, AuthenticationMechanismFactory factory) {
/* 1073 */     this.authenticationMechanisms.put(name.toUpperCase(Locale.US), factory);
/* 1074 */     return this;
/*      */   }
/*      */   
/*      */   public Map<String, AuthenticationMechanismFactory> getAuthenticationMechanisms() {
/* 1078 */     return this.authenticationMechanisms;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isAuthenticationMechanismPresent(String mechanismName) {
/* 1087 */     if (this.loginConfig != null) {
/* 1088 */       for (AuthMethodConfig method : this.loginConfig.getAuthMethods()) {
/* 1089 */         if (method.getName().equalsIgnoreCase(mechanismName)) {
/* 1090 */           return true;
/*      */         }
/*      */       } 
/*      */     }
/* 1094 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public DeploymentInfo addServletExtension(ServletExtension servletExtension) {
/* 1104 */     this.servletExtensions.add(servletExtension);
/* 1105 */     return this;
/*      */   }
/*      */   
/*      */   public List<ServletExtension> getServletExtensions() {
/* 1109 */     return this.servletExtensions;
/*      */   }
/*      */   
/*      */   public AuthenticationMechanism getJaspiAuthenticationMechanism() {
/* 1113 */     return this.jaspiAuthenticationMechanism;
/*      */   }
/*      */   
/*      */   public DeploymentInfo setJaspiAuthenticationMechanism(AuthenticationMechanism jaspiAuthenticationMechanism) {
/* 1117 */     this.jaspiAuthenticationMechanism = jaspiAuthenticationMechanism;
/* 1118 */     return this;
/*      */   }
/*      */   
/*      */   public SecurityContextFactory getSecurityContextFactory() {
/* 1122 */     return this.securityContextFactory;
/*      */   }
/*      */   
/*      */   public DeploymentInfo setSecurityContextFactory(SecurityContextFactory securityContextFactory) {
/* 1126 */     this.securityContextFactory = securityContextFactory;
/* 1127 */     return this;
/*      */   }
/*      */   
/*      */   public String getServerName() {
/* 1131 */     return this.serverName;
/*      */   }
/*      */   
/*      */   public DeploymentInfo setServerName(String serverName) {
/* 1135 */     this.serverName = serverName;
/* 1136 */     return this;
/*      */   }
/*      */   
/*      */   public DeploymentInfo setMetricsCollector(MetricsCollector metricsCollector) {
/* 1140 */     this.metricsCollector = metricsCollector;
/* 1141 */     return this;
/*      */   }
/*      */   
/*      */   public MetricsCollector getMetricsCollector() {
/* 1145 */     return this.metricsCollector;
/*      */   }
/*      */   
/*      */   public SessionConfigWrapper getSessionConfigWrapper() {
/* 1149 */     return this.sessionConfigWrapper;
/*      */   }
/*      */   
/*      */   public DeploymentInfo setSessionConfigWrapper(SessionConfigWrapper sessionConfigWrapper) {
/* 1153 */     this.sessionConfigWrapper = sessionConfigWrapper;
/* 1154 */     return this;
/*      */   }
/*      */   
/*      */   public boolean isDisableCachingForSecuredPages() {
/* 1158 */     return this.disableCachingForSecuredPages;
/*      */   }
/*      */   
/*      */   public DeploymentInfo setDisableCachingForSecuredPages(boolean disableCachingForSecuredPages) {
/* 1162 */     this.disableCachingForSecuredPages = disableCachingForSecuredPages;
/* 1163 */     return this;
/*      */   }
/*      */   
/*      */   public DeploymentInfo addLifecycleInterceptor(LifecycleInterceptor interceptor) {
/* 1167 */     this.lifecycleInterceptors.add(interceptor);
/* 1168 */     return this;
/*      */   }
/*      */   
/*      */   public List<LifecycleInterceptor> getLifecycleInterceptors() {
/* 1172 */     return this.lifecycleInterceptors;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ExceptionHandler getExceptionHandler() {
/* 1180 */     return this.exceptionHandler;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public DeploymentInfo setExceptionHandler(ExceptionHandler exceptionHandler) {
/* 1189 */     this.exceptionHandler = exceptionHandler;
/* 1190 */     return this;
/*      */   }
/*      */   
/*      */   public boolean isEscapeErrorMessage() {
/* 1194 */     return this.escapeErrorMessage;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public DeploymentInfo setEscapeErrorMessage(boolean escapeErrorMessage) {
/* 1205 */     this.escapeErrorMessage = escapeErrorMessage;
/* 1206 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public DeploymentInfo addSessionListener(SessionListener sessionListener) {
/* 1211 */     this.sessionListeners.add(sessionListener);
/* 1212 */     return this;
/*      */   }
/*      */   
/*      */   public List<SessionListener> getSessionListeners() {
/* 1216 */     return this.sessionListeners;
/*      */   }
/*      */   
/*      */   public AuthenticationMode getAuthenticationMode() {
/* 1220 */     return this.authenticationMode;
/*      */   }
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
/*      */   public DeploymentInfo setAuthenticationMode(AuthenticationMode authenticationMode) {
/* 1235 */     this.authenticationMode = authenticationMode;
/* 1236 */     return this;
/*      */   }
/*      */   
/*      */   public MultipartConfigElement getDefaultMultipartConfig() {
/* 1240 */     return this.defaultMultipartConfig;
/*      */   }
/*      */   
/*      */   public DeploymentInfo setDefaultMultipartConfig(MultipartConfigElement defaultMultipartConfig) {
/* 1244 */     this.defaultMultipartConfig = defaultMultipartConfig;
/* 1245 */     return this;
/*      */   }
/*      */   
/*      */   public int getContentTypeCacheSize() {
/* 1249 */     return this.contentTypeCacheSize;
/*      */   }
/*      */   
/*      */   public DeploymentInfo setContentTypeCacheSize(int contentTypeCacheSize) {
/* 1253 */     this.contentTypeCacheSize = contentTypeCacheSize;
/* 1254 */     return this;
/*      */   }
/*      */   
/*      */   public SessionIdGenerator getSessionIdGenerator() {
/* 1258 */     return this.sessionIdGenerator;
/*      */   }
/*      */   
/*      */   public DeploymentInfo setSessionIdGenerator(SessionIdGenerator sessionIdGenerator) {
/* 1262 */     this.sessionIdGenerator = sessionIdGenerator;
/* 1263 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean isSendCustomReasonPhraseOnError() {
/* 1268 */     return this.sendCustomReasonPhraseOnError;
/*      */   }
/*      */   
/*      */   public CrawlerSessionManagerConfig getCrawlerSessionManagerConfig() {
/* 1272 */     return this.crawlerSessionManagerConfig;
/*      */   }
/*      */   
/*      */   public DeploymentInfo setCrawlerSessionManagerConfig(CrawlerSessionManagerConfig crawlerSessionManagerConfig) {
/* 1276 */     this.crawlerSessionManagerConfig = crawlerSessionManagerConfig;
/* 1277 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public DeploymentInfo setSendCustomReasonPhraseOnError(boolean sendCustomReasonPhraseOnError) {
/* 1289 */     this.sendCustomReasonPhraseOnError = sendCustomReasonPhraseOnError;
/* 1290 */     return this;
/*      */   }
/*      */   
/*      */   public boolean isChangeSessionIdOnLogin() {
/* 1294 */     return this.changeSessionIdOnLogin;
/*      */   }
/*      */   
/*      */   public DeploymentInfo setChangeSessionIdOnLogin(boolean changeSessionIdOnLogin) {
/* 1298 */     this.changeSessionIdOnLogin = changeSessionIdOnLogin;
/* 1299 */     return this;
/*      */   }
/*      */   
/*      */   public boolean isUseCachedAuthenticationMechanism() {
/* 1303 */     return this.useCachedAuthenticationMechanism;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public DeploymentInfo setUseCachedAuthenticationMechanism(boolean useCachedAuthenticationMechanism) {
/* 1314 */     this.useCachedAuthenticationMechanism = useCachedAuthenticationMechanism;
/* 1315 */     return this;
/*      */   }
/*      */   
/*      */   public boolean isSecurityDisabled() {
/* 1319 */     return this.securityDisabled;
/*      */   }
/*      */   
/*      */   public DeploymentInfo setSecurityDisabled(boolean securityDisabled) {
/* 1323 */     this.securityDisabled = securityDisabled;
/* 1324 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean isCheckOtherSessionManagers() {
/* 1329 */     return this.checkOtherSessionManagers;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public DeploymentInfo setCheckOtherSessionManagers(boolean checkOtherSessionManagers) {
/* 1337 */     this.checkOtherSessionManagers = checkOtherSessionManagers;
/* 1338 */     return this;
/*      */   }
/*      */   
/*      */   public String getDefaultRequestEncoding() {
/* 1342 */     return this.defaultRequestEncoding;
/*      */   }
/*      */   
/*      */   public DeploymentInfo setDefaultRequestEncoding(String defaultRequestEncoding) {
/* 1346 */     this.defaultRequestEncoding = defaultRequestEncoding;
/* 1347 */     return this;
/*      */   }
/*      */   
/*      */   public String getDefaultResponseEncoding() {
/* 1351 */     return this.defaultResponseEncoding;
/*      */   }
/*      */   
/*      */   public DeploymentInfo setDefaultResponseEncoding(String defaultResponseEncoding) {
/* 1355 */     this.defaultResponseEncoding = defaultResponseEncoding;
/* 1356 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public DeploymentInfo addPreCompressedResourceEncoding(String encoding, String extension) {
/* 1368 */     this.preCompressedResources.put(encoding, extension);
/* 1369 */     return this;
/*      */   }
/*      */   
/*      */   public Map<String, String> getPreCompressedResources() {
/* 1373 */     return this.preCompressedResources;
/*      */   }
/*      */   
/*      */   public int getContainerMajorVersion() {
/* 1377 */     return this.containerMajorVersion;
/*      */   }
/*      */   
/*      */   public DeploymentInfo setContainerMajorVersion(int containerMajorVersion) {
/* 1381 */     this.containerMajorVersion = containerMajorVersion;
/* 1382 */     return this;
/*      */   }
/*      */   
/*      */   public int getContainerMinorVersion() {
/* 1386 */     return this.containerMinorVersion;
/*      */   }
/*      */   
/*      */   public DeploymentInfo setContainerMinorVersion(int containerMinorVersion) {
/* 1390 */     this.containerMinorVersion = containerMinorVersion;
/* 1391 */     return this;
/*      */   }
/*      */   
/*      */   public boolean isPreservePathOnForward() {
/* 1395 */     return this.preservePathOnForward;
/*      */   }
/*      */   
/*      */   public void setPreservePathOnForward(boolean preservePathOnForward) {
/* 1399 */     this.preservePathOnForward = preservePathOnForward;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public DeploymentInfo addDeploymentCompleteListener(ServletContextListener servletContextListener) {
/* 1411 */     this.deploymentCompleteListeners.add(servletContextListener);
/* 1412 */     return this;
/*      */   }
/*      */   
/*      */   public List<ServletContextListener> getDeploymentCompleteListeners() {
/* 1416 */     return this.deploymentCompleteListeners;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public DeploymentInfo clone() {
/* 1428 */     DeploymentInfo info = (new DeploymentInfo()).setClassLoader(this.classLoader).setContextPath(this.contextPath).setResourceManager(this.resourceManager).setMajorVersion(this.majorVersion).setMinorVersion(this.minorVersion).setDeploymentName(this.deploymentName).setClassIntrospecter(this.classIntrospecter);
/*      */     
/* 1430 */     for (Map.Entry<String, ServletInfo> e : this.servlets.entrySet()) {
/* 1431 */       info.addServlet(((ServletInfo)e.getValue()).clone());
/*      */     }
/*      */     
/* 1434 */     for (Map.Entry<String, FilterInfo> e : this.filters.entrySet()) {
/* 1435 */       info.addFilter(((FilterInfo)e.getValue()).clone());
/*      */     }
/* 1437 */     info.displayName = this.displayName;
/* 1438 */     info.filterUrlMappings.addAll(this.filterUrlMappings);
/* 1439 */     info.filterServletNameMappings.addAll(this.filterServletNameMappings);
/* 1440 */     info.listeners.addAll(this.listeners);
/* 1441 */     info.servletContainerInitializers.addAll(this.servletContainerInitializers);
/* 1442 */     info.threadSetupActions.addAll(this.threadSetupActions);
/* 1443 */     info.initParameters.putAll(this.initParameters);
/* 1444 */     info.servletContextAttributes.putAll(this.servletContextAttributes);
/* 1445 */     info.welcomePages.addAll(this.welcomePages);
/* 1446 */     info.errorPages.addAll(this.errorPages);
/* 1447 */     info.mimeMappings.addAll(this.mimeMappings);
/* 1448 */     info.executor = this.executor;
/* 1449 */     info.asyncExecutor = this.asyncExecutor;
/* 1450 */     info.tempDir = this.tempDir;
/* 1451 */     info.jspConfigDescriptor = this.jspConfigDescriptor;
/* 1452 */     info.defaultServletConfig = this.defaultServletConfig;
/* 1453 */     info.localeCharsetMapping.putAll(this.localeCharsetMapping);
/* 1454 */     info.sessionManagerFactory = this.sessionManagerFactory;
/* 1455 */     if (this.loginConfig != null) {
/* 1456 */       info.loginConfig = this.loginConfig.clone();
/*      */     }
/* 1458 */     info.identityManager = this.identityManager;
/* 1459 */     info.confidentialPortManager = this.confidentialPortManager;
/* 1460 */     info.defaultEncoding = this.defaultEncoding;
/* 1461 */     info.urlEncoding = this.urlEncoding;
/* 1462 */     info.securityConstraints.addAll(this.securityConstraints);
/* 1463 */     info.outerHandlerChainWrappers.addAll(this.outerHandlerChainWrappers);
/* 1464 */     info.innerHandlerChainWrappers.addAll(this.innerHandlerChainWrappers);
/* 1465 */     info.initialSecurityWrapper = this.initialSecurityWrapper;
/* 1466 */     info.securityWrappers.addAll(this.securityWrappers);
/* 1467 */     info.initialHandlerChainWrappers.addAll(this.initialHandlerChainWrappers);
/* 1468 */     info.securityRoles.addAll(this.securityRoles);
/* 1469 */     info.notificationReceivers.addAll(this.notificationReceivers);
/* 1470 */     info.allowNonStandardWrappers = this.allowNonStandardWrappers;
/* 1471 */     info.defaultSessionTimeout = this.defaultSessionTimeout;
/* 1472 */     info.servletContextAttributeBackingMap = this.servletContextAttributeBackingMap;
/* 1473 */     info.servletSessionConfig = this.servletSessionConfig;
/* 1474 */     info.hostName = this.hostName;
/* 1475 */     info.denyUncoveredHttpMethods = this.denyUncoveredHttpMethods;
/* 1476 */     info.servletStackTraces = this.servletStackTraces;
/* 1477 */     info.invalidateSessionOnLogout = this.invalidateSessionOnLogout;
/* 1478 */     info.defaultCookieVersion = this.defaultCookieVersion;
/* 1479 */     info.sessionPersistenceManager = this.sessionPersistenceManager;
/* 1480 */     for (Map.Entry<String, Set<String>> e : this.principalVersusRolesMap.entrySet()) {
/* 1481 */       info.principalVersusRolesMap.put(e.getKey(), new HashSet<>(e.getValue()));
/*      */     }
/* 1483 */     info.ignoreFlush = this.ignoreFlush;
/* 1484 */     info.authorizationManager = this.authorizationManager;
/* 1485 */     info.authenticationMechanisms.putAll(this.authenticationMechanisms);
/* 1486 */     info.servletExtensions.addAll(this.servletExtensions);
/* 1487 */     info.jaspiAuthenticationMechanism = this.jaspiAuthenticationMechanism;
/* 1488 */     info.securityContextFactory = this.securityContextFactory;
/* 1489 */     info.serverName = this.serverName;
/* 1490 */     info.metricsCollector = this.metricsCollector;
/* 1491 */     info.sessionConfigWrapper = this.sessionConfigWrapper;
/* 1492 */     info.eagerFilterInit = this.eagerFilterInit;
/* 1493 */     info.disableCachingForSecuredPages = this.disableCachingForSecuredPages;
/* 1494 */     info.exceptionHandler = this.exceptionHandler;
/* 1495 */     info.escapeErrorMessage = this.escapeErrorMessage;
/* 1496 */     info.sessionListeners.addAll(this.sessionListeners);
/* 1497 */     info.lifecycleInterceptors.addAll(this.lifecycleInterceptors);
/* 1498 */     info.authenticationMode = this.authenticationMode;
/* 1499 */     info.defaultMultipartConfig = this.defaultMultipartConfig;
/* 1500 */     info.contentTypeCacheSize = this.contentTypeCacheSize;
/* 1501 */     info.sessionIdGenerator = this.sessionIdGenerator;
/* 1502 */     info.sendCustomReasonPhraseOnError = this.sendCustomReasonPhraseOnError;
/* 1503 */     info.changeSessionIdOnLogin = this.changeSessionIdOnLogin;
/* 1504 */     info.crawlerSessionManagerConfig = this.crawlerSessionManagerConfig;
/* 1505 */     info.securityDisabled = this.securityDisabled;
/* 1506 */     info.useCachedAuthenticationMechanism = this.useCachedAuthenticationMechanism;
/* 1507 */     info.checkOtherSessionManagers = this.checkOtherSessionManagers;
/* 1508 */     info.defaultRequestEncoding = this.defaultRequestEncoding;
/* 1509 */     info.defaultResponseEncoding = this.defaultResponseEncoding;
/* 1510 */     info.preCompressedResources.putAll(this.preCompressedResources);
/* 1511 */     info.containerMajorVersion = this.containerMajorVersion;
/* 1512 */     info.containerMinorVersion = this.containerMinorVersion;
/* 1513 */     info.deploymentCompleteListeners.addAll(this.deploymentCompleteListeners);
/* 1514 */     info.preservePathOnForward = this.preservePathOnForward;
/* 1515 */     return info;
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\servlet\api\DeploymentInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */