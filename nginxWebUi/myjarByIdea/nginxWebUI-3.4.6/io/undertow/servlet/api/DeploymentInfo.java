package io.undertow.servlet.api;

import io.undertow.security.api.AuthenticationMechanism;
import io.undertow.security.api.AuthenticationMechanismFactory;
import io.undertow.security.api.AuthenticationMode;
import io.undertow.security.api.NotificationReceiver;
import io.undertow.security.api.SecurityContextFactory;
import io.undertow.security.idm.IdentityManager;
import io.undertow.server.HandlerWrapper;
import io.undertow.server.handlers.resource.ResourceManager;
import io.undertow.server.session.SecureRandomSessionIdGenerator;
import io.undertow.server.session.SessionIdGenerator;
import io.undertow.server.session.SessionListener;
import io.undertow.servlet.ServletExtension;
import io.undertow.servlet.UndertowServletMessages;
import io.undertow.servlet.core.DefaultAuthorizationManager;
import io.undertow.servlet.core.InMemorySessionManagerFactory;
import io.undertow.servlet.util.DefaultClassIntrospector;
import io.undertow.util.ImmediateAuthenticationMechanismFactory;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executor;
import javax.servlet.DispatcherType;
import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletContextListener;
import javax.servlet.descriptor.JspConfigDescriptor;

public class DeploymentInfo implements Cloneable {
   private static final int DEFAULT_MAJOR_VERSION;
   private String deploymentName;
   private String displayName;
   private String contextPath;
   private ClassLoader classLoader;
   private ResourceManager resourceManager;
   private ClassIntrospecter classIntrospecter;
   private int majorVersion;
   private int minorVersion;
   private int containerMajorVersion;
   private int containerMinorVersion;
   private Executor executor;
   private Executor asyncExecutor;
   private Path tempDir;
   private JspConfigDescriptor jspConfigDescriptor;
   private DefaultServletConfig defaultServletConfig;
   private SessionManagerFactory sessionManagerFactory;
   private LoginConfig loginConfig;
   private IdentityManager identityManager;
   private ConfidentialPortManager confidentialPortManager;
   private boolean allowNonStandardWrappers;
   private int defaultSessionTimeout;
   private ConcurrentMap<String, Object> servletContextAttributeBackingMap;
   private ServletSessionConfig servletSessionConfig;
   private String hostName;
   private boolean denyUncoveredHttpMethods;
   private ServletStackTraces servletStackTraces;
   private boolean invalidateSessionOnLogout;
   private int defaultCookieVersion;
   private SessionPersistenceManager sessionPersistenceManager;
   private String defaultEncoding;
   private String defaultRequestEncoding;
   private String defaultResponseEncoding;
   private String urlEncoding;
   private boolean ignoreFlush;
   private AuthorizationManager authorizationManager;
   private AuthenticationMechanism jaspiAuthenticationMechanism;
   private SecurityContextFactory securityContextFactory;
   private String serverName;
   private MetricsCollector metricsCollector;
   private SessionConfigWrapper sessionConfigWrapper;
   private boolean eagerFilterInit;
   private boolean disableCachingForSecuredPages;
   private boolean escapeErrorMessage;
   private boolean sendCustomReasonPhraseOnError;
   private boolean useCachedAuthenticationMechanism;
   private boolean preservePathOnForward;
   private AuthenticationMode authenticationMode;
   private ExceptionHandler exceptionHandler;
   private final Map<String, ServletInfo> servlets;
   private final Map<String, FilterInfo> filters;
   private final List<FilterMappingInfo> filterServletNameMappings;
   private final List<FilterMappingInfo> filterUrlMappings;
   private final List<ListenerInfo> listeners;
   private final List<ServletContainerInitializerInfo> servletContainerInitializers;
   private final List<ThreadSetupHandler> threadSetupActions;
   private final Map<String, String> initParameters;
   private final Map<String, Object> servletContextAttributes;
   private final Map<String, String> localeCharsetMapping;
   private final List<String> welcomePages;
   private final List<ErrorPage> errorPages;
   private final List<MimeMapping> mimeMappings;
   private final List<SecurityConstraint> securityConstraints;
   private final Set<String> securityRoles;
   private final List<NotificationReceiver> notificationReceivers;
   private final Map<String, AuthenticationMechanismFactory> authenticationMechanisms;
   private final List<LifecycleInterceptor> lifecycleInterceptors;
   private final List<SessionListener> sessionListeners;
   private final List<ServletExtension> servletExtensions;
   private final Map<String, Set<String>> principalVersusRolesMap;
   private final List<HandlerWrapper> initialHandlerChainWrappers;
   private final List<HandlerWrapper> outerHandlerChainWrappers;
   private final List<HandlerWrapper> innerHandlerChainWrappers;
   private HandlerWrapper initialSecurityWrapper;
   private final List<HandlerWrapper> securityWrappers;
   private MultipartConfigElement defaultMultipartConfig;
   private int contentTypeCacheSize;
   private boolean changeSessionIdOnLogin;
   private SessionIdGenerator sessionIdGenerator;
   private CrawlerSessionManagerConfig crawlerSessionManagerConfig;
   private boolean securityDisabled;
   private boolean checkOtherSessionManagers;
   private final List<ServletContextListener> deploymentCompleteListeners;
   private final Map<String, String> preCompressedResources;

   public DeploymentInfo() {
      this.resourceManager = ResourceManager.EMPTY_RESOURCE_MANAGER;
      this.classIntrospecter = DefaultClassIntrospector.INSTANCE;
      this.majorVersion = DEFAULT_MAJOR_VERSION;
      this.minorVersion = 0;
      this.containerMajorVersion = DEFAULT_MAJOR_VERSION;
      this.containerMinorVersion = 0;
      this.sessionManagerFactory = new InMemorySessionManagerFactory();
      this.allowNonStandardWrappers = false;
      this.defaultSessionTimeout = 1800;
      this.hostName = "localhost";
      this.denyUncoveredHttpMethods = false;
      this.servletStackTraces = ServletStackTraces.LOCAL_ONLY;
      this.invalidateSessionOnLogout = false;
      this.defaultCookieVersion = 0;
      this.urlEncoding = null;
      this.ignoreFlush = false;
      this.authorizationManager = DefaultAuthorizationManager.INSTANCE;
      this.serverName = "Undertow";
      this.metricsCollector = null;
      this.sessionConfigWrapper = null;
      this.eagerFilterInit = false;
      this.disableCachingForSecuredPages = true;
      this.escapeErrorMessage = true;
      this.sendCustomReasonPhraseOnError = false;
      this.useCachedAuthenticationMechanism = true;
      this.preservePathOnForward = true;
      this.authenticationMode = AuthenticationMode.PRO_ACTIVE;
      this.servlets = new HashMap();
      this.filters = new HashMap();
      this.filterServletNameMappings = new ArrayList();
      this.filterUrlMappings = new ArrayList();
      this.listeners = new ArrayList();
      this.servletContainerInitializers = new ArrayList();
      this.threadSetupActions = new ArrayList();
      this.initParameters = new HashMap();
      this.servletContextAttributes = new HashMap();
      this.localeCharsetMapping = new HashMap();
      this.welcomePages = new ArrayList();
      this.errorPages = new ArrayList();
      this.mimeMappings = new ArrayList();
      this.securityConstraints = new ArrayList();
      this.securityRoles = new HashSet();
      this.notificationReceivers = new ArrayList();
      this.authenticationMechanisms = new HashMap();
      this.lifecycleInterceptors = new ArrayList();
      this.sessionListeners = new ArrayList();
      this.servletExtensions = new ArrayList();
      this.principalVersusRolesMap = new HashMap();
      this.initialHandlerChainWrappers = new ArrayList();
      this.outerHandlerChainWrappers = new ArrayList();
      this.innerHandlerChainWrappers = new ArrayList();
      this.initialSecurityWrapper = null;
      this.securityWrappers = new ArrayList();
      this.contentTypeCacheSize = 100;
      this.changeSessionIdOnLogin = true;
      this.sessionIdGenerator = new SecureRandomSessionIdGenerator();
      this.checkOtherSessionManagers = true;
      this.deploymentCompleteListeners = new ArrayList();
      this.preCompressedResources = new HashMap();
   }

   public void validate() {
      if (this.deploymentName == null) {
         throw UndertowServletMessages.MESSAGES.paramCannotBeNull("deploymentName");
      } else if (this.contextPath == null) {
         throw UndertowServletMessages.MESSAGES.paramCannotBeNull("contextName");
      } else if (this.classLoader == null) {
         throw UndertowServletMessages.MESSAGES.paramCannotBeNull("classLoader");
      } else if (this.resourceManager == null) {
         throw UndertowServletMessages.MESSAGES.paramCannotBeNull("resourceManager");
      } else if (this.classIntrospecter == null) {
         throw UndertowServletMessages.MESSAGES.paramCannotBeNull("classIntrospecter");
      } else {
         Iterator var1 = this.servlets.values().iterator();

         while(var1.hasNext()) {
            ServletInfo servlet = (ServletInfo)var1.next();
            servlet.validate();
         }

         var1 = this.filters.values().iterator();

         while(var1.hasNext()) {
            FilterInfo filter = (FilterInfo)var1.next();
            filter.validate();
         }

         var1 = this.filterServletNameMappings.iterator();

         FilterMappingInfo mapping;
         do {
            if (!var1.hasNext()) {
               var1 = this.filterUrlMappings.iterator();

               do {
                  if (!var1.hasNext()) {
                     return;
                  }

                  mapping = (FilterMappingInfo)var1.next();
               } while(this.filters.containsKey(mapping.getFilterName()));

               throw UndertowServletMessages.MESSAGES.filterNotFound(mapping.getFilterName(), mapping.getMappingType() + " - " + mapping.getMapping());
            }

            mapping = (FilterMappingInfo)var1.next();
         } while(this.filters.containsKey(mapping.getFilterName()));

         throw UndertowServletMessages.MESSAGES.filterNotFound(mapping.getFilterName(), mapping.getMappingType() + " - " + mapping.getMapping());
      }
   }

   public String getDeploymentName() {
      return this.deploymentName;
   }

   public DeploymentInfo setDeploymentName(String deploymentName) {
      this.deploymentName = deploymentName;
      return this;
   }

   public String getDisplayName() {
      return this.displayName;
   }

   public DeploymentInfo setDisplayName(String displayName) {
      this.displayName = displayName;
      return this;
   }

   public String getContextPath() {
      return this.contextPath;
   }

   public DeploymentInfo setContextPath(String contextPath) {
      if (contextPath != null && contextPath.isEmpty()) {
         this.contextPath = "/";
      } else {
         this.contextPath = contextPath;
      }

      return this;
   }

   public ClassLoader getClassLoader() {
      return this.classLoader;
   }

   public DeploymentInfo setClassLoader(ClassLoader classLoader) {
      this.classLoader = classLoader;
      return this;
   }

   public ResourceManager getResourceManager() {
      return this.resourceManager;
   }

   public DeploymentInfo setResourceManager(ResourceManager resourceManager) {
      this.resourceManager = resourceManager;
      return this;
   }

   public ClassIntrospecter getClassIntrospecter() {
      return this.classIntrospecter;
   }

   public DeploymentInfo setClassIntrospecter(ClassIntrospecter classIntrospecter) {
      this.classIntrospecter = classIntrospecter;
      return this;
   }

   public boolean isAllowNonStandardWrappers() {
      return this.allowNonStandardWrappers;
   }

   public DeploymentInfo setAllowNonStandardWrappers(boolean allowNonStandardWrappers) {
      this.allowNonStandardWrappers = allowNonStandardWrappers;
      return this;
   }

   public int getDefaultSessionTimeout() {
      return this.defaultSessionTimeout;
   }

   public DeploymentInfo setDefaultSessionTimeout(int defaultSessionTimeout) {
      this.defaultSessionTimeout = defaultSessionTimeout;
      return this;
   }

   public String getDefaultEncoding() {
      return this.defaultEncoding;
   }

   public DeploymentInfo setDefaultEncoding(String defaultEncoding) {
      this.defaultEncoding = defaultEncoding;
      return this;
   }

   public String getUrlEncoding() {
      return this.urlEncoding;
   }

   public DeploymentInfo setUrlEncoding(String urlEncoding) {
      this.urlEncoding = urlEncoding;
      return this;
   }

   public DeploymentInfo addServlet(ServletInfo servlet) {
      this.servlets.put(servlet.getName(), servlet);
      return this;
   }

   public DeploymentInfo addServlets(ServletInfo... servlets) {
      ServletInfo[] var2 = servlets;
      int var3 = servlets.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         ServletInfo servlet = var2[var4];
         this.addServlet(servlet);
      }

      return this;
   }

   public DeploymentInfo addServlets(Collection<ServletInfo> servlets) {
      Iterator var2 = servlets.iterator();

      while(var2.hasNext()) {
         ServletInfo servlet = (ServletInfo)var2.next();
         this.addServlet(servlet);
      }

      return this;
   }

   public Map<String, ServletInfo> getServlets() {
      return this.servlets;
   }

   public DeploymentInfo addFilter(FilterInfo filter) {
      this.filters.put(filter.getName(), filter);
      return this;
   }

   public DeploymentInfo addFilters(FilterInfo... filters) {
      FilterInfo[] var2 = filters;
      int var3 = filters.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         FilterInfo filter = var2[var4];
         this.addFilter(filter);
      }

      return this;
   }

   public DeploymentInfo addFilters(Collection<FilterInfo> filters) {
      Iterator var2 = filters.iterator();

      while(var2.hasNext()) {
         FilterInfo filter = (FilterInfo)var2.next();
         this.addFilter(filter);
      }

      return this;
   }

   public Map<String, FilterInfo> getFilters() {
      return this.filters;
   }

   public DeploymentInfo addFilterUrlMapping(String filterName, String mapping, DispatcherType dispatcher) {
      this.filterUrlMappings.add(new FilterMappingInfo(filterName, FilterMappingInfo.MappingType.URL, mapping, dispatcher));
      return this;
   }

   public DeploymentInfo addFilterServletNameMapping(String filterName, String mapping, DispatcherType dispatcher) {
      this.filterServletNameMappings.add(new FilterMappingInfo(filterName, FilterMappingInfo.MappingType.SERVLET, mapping, dispatcher));
      return this;
   }

   public DeploymentInfo insertFilterUrlMapping(int pos, String filterName, String mapping, DispatcherType dispatcher) {
      this.filterUrlMappings.add(pos, new FilterMappingInfo(filterName, FilterMappingInfo.MappingType.URL, mapping, dispatcher));
      return this;
   }

   public DeploymentInfo insertFilterServletNameMapping(int pos, String filterName, String mapping, DispatcherType dispatcher) {
      this.filterServletNameMappings.add(pos, new FilterMappingInfo(filterName, FilterMappingInfo.MappingType.SERVLET, mapping, dispatcher));
      return this;
   }

   public List<FilterMappingInfo> getFilterMappings() {
      ArrayList<FilterMappingInfo> ret = new ArrayList(this.filterUrlMappings);
      ret.addAll(this.filterServletNameMappings);
      return ret;
   }

   public DeploymentInfo addListener(ListenerInfo listener) {
      this.listeners.add(listener);
      return this;
   }

   public DeploymentInfo addListeners(ListenerInfo... listeners) {
      this.listeners.addAll(Arrays.asList(listeners));
      return this;
   }

   public DeploymentInfo addListeners(Collection<ListenerInfo> listeners) {
      this.listeners.addAll(listeners);
      return this;
   }

   public List<ListenerInfo> getListeners() {
      return this.listeners;
   }

   public int getMajorVersion() {
      return this.majorVersion;
   }

   public DeploymentInfo setMajorVersion(int majorVersion) {
      this.majorVersion = majorVersion;
      return this;
   }

   public int getMinorVersion() {
      return this.minorVersion;
   }

   public DeploymentInfo setMinorVersion(int minorVersion) {
      this.minorVersion = minorVersion;
      return this;
   }

   public DeploymentInfo addServletContainerInitializer(ServletContainerInitializerInfo servletContainerInitializer) {
      this.servletContainerInitializers.add(servletContainerInitializer);
      return this;
   }

   /** @deprecated */
   @Deprecated
   public DeploymentInfo addServletContainerInitalizer(ServletContainerInitializerInfo servletContainerInitializer) {
      return this.addServletContainerInitializer(servletContainerInitializer);
   }

   public DeploymentInfo addServletContainerInitializers(ServletContainerInitializerInfo... servletContainerInitializer) {
      this.servletContainerInitializers.addAll(Arrays.asList(servletContainerInitializer));
      return this;
   }

   /** @deprecated */
   @Deprecated
   public DeploymentInfo addServletContainerInitalizers(ServletContainerInitializerInfo... servletContainerInitializer) {
      return this.addServletContainerInitializers(servletContainerInitializer);
   }

   public DeploymentInfo addServletContainerInitializers(List<ServletContainerInitializerInfo> servletContainerInitializer) {
      this.servletContainerInitializers.addAll(servletContainerInitializer);
      return this;
   }

   /** @deprecated */
   @Deprecated
   public DeploymentInfo addServletContainerInitalizers(List<ServletContainerInitializerInfo> servletContainerInitializers) {
      return this.addServletContainerInitializers(servletContainerInitializers);
   }

   public List<ServletContainerInitializerInfo> getServletContainerInitializers() {
      return this.servletContainerInitializers;
   }

   /** @deprecated */
   @Deprecated
   public DeploymentInfo addThreadSetupAction(ThreadSetupAction action) {
      this.threadSetupActions.add(new LegacyThreadSetupActionWrapper(action));
      return this;
   }

   public DeploymentInfo addThreadSetupAction(ThreadSetupHandler action) {
      this.threadSetupActions.add(action);
      return this;
   }

   public List<ThreadSetupHandler> getThreadSetupActions() {
      return this.threadSetupActions;
   }

   public boolean isEagerFilterInit() {
      return this.eagerFilterInit;
   }

   public DeploymentInfo setEagerFilterInit(boolean eagerFilterInit) {
      this.eagerFilterInit = eagerFilterInit;
      return this;
   }

   public DeploymentInfo addInitParameter(String name, String value) {
      this.initParameters.put(name, value);
      return this;
   }

   public Map<String, String> getInitParameters() {
      return this.initParameters;
   }

   public DeploymentInfo addServletContextAttribute(String name, Object value) {
      this.servletContextAttributes.put(name, value);
      return this;
   }

   public Map<String, Object> getServletContextAttributes() {
      return this.servletContextAttributes;
   }

   public DeploymentInfo addWelcomePage(String welcomePage) {
      this.welcomePages.add(welcomePage);
      return this;
   }

   public DeploymentInfo addWelcomePages(String... welcomePages) {
      this.welcomePages.addAll(Arrays.asList(welcomePages));
      return this;
   }

   public DeploymentInfo addWelcomePages(Collection<String> welcomePages) {
      this.welcomePages.addAll(welcomePages);
      return this;
   }

   public List<String> getWelcomePages() {
      return this.welcomePages;
   }

   public DeploymentInfo addErrorPage(ErrorPage errorPage) {
      this.errorPages.add(errorPage);
      return this;
   }

   public DeploymentInfo addErrorPages(ErrorPage... errorPages) {
      this.errorPages.addAll(Arrays.asList(errorPages));
      return this;
   }

   public DeploymentInfo addErrorPages(Collection<ErrorPage> errorPages) {
      this.errorPages.addAll(errorPages);
      return this;
   }

   public List<ErrorPage> getErrorPages() {
      return this.errorPages;
   }

   public DeploymentInfo addMimeMapping(MimeMapping mimeMappings) {
      this.mimeMappings.add(mimeMappings);
      return this;
   }

   public DeploymentInfo addMimeMappings(MimeMapping... mimeMappings) {
      this.mimeMappings.addAll(Arrays.asList(mimeMappings));
      return this;
   }

   public DeploymentInfo addMimeMappings(Collection<MimeMapping> mimeMappings) {
      this.mimeMappings.addAll(mimeMappings);
      return this;
   }

   public List<MimeMapping> getMimeMappings() {
      return this.mimeMappings;
   }

   public DeploymentInfo addSecurityConstraint(SecurityConstraint securityConstraint) {
      this.securityConstraints.add(securityConstraint);
      return this;
   }

   public DeploymentInfo addSecurityConstraints(SecurityConstraint... securityConstraints) {
      this.securityConstraints.addAll(Arrays.asList(securityConstraints));
      return this;
   }

   public DeploymentInfo addSecurityConstraints(Collection<SecurityConstraint> securityConstraints) {
      this.securityConstraints.addAll(securityConstraints);
      return this;
   }

   public List<SecurityConstraint> getSecurityConstraints() {
      return this.securityConstraints;
   }

   public Executor getExecutor() {
      return this.executor;
   }

   public DeploymentInfo setExecutor(Executor executor) {
      this.executor = executor;
      return this;
   }

   public Executor getAsyncExecutor() {
      return this.asyncExecutor;
   }

   public DeploymentInfo setAsyncExecutor(Executor asyncExecutor) {
      this.asyncExecutor = asyncExecutor;
      return this;
   }

   public File getTempDir() {
      return this.tempDir == null ? null : this.tempDir.toFile();
   }

   public Path getTempPath() {
      return this.tempDir;
   }

   public Path requireTempPath() {
      return this.tempDir != null ? this.tempDir : Paths.get(SecurityActions.getSystemProperty("java.io.tmpdir"));
   }

   public DeploymentInfo setTempDir(File tempDir) {
      this.tempDir = tempDir != null ? tempDir.toPath() : null;
      return this;
   }

   public DeploymentInfo setTempDir(Path tempDir) {
      this.tempDir = tempDir;
      return this;
   }

   public boolean isIgnoreFlush() {
      return this.ignoreFlush;
   }

   public DeploymentInfo setIgnoreFlush(boolean ignoreFlush) {
      this.ignoreFlush = ignoreFlush;
      return this;
   }

   public JspConfigDescriptor getJspConfigDescriptor() {
      return this.jspConfigDescriptor;
   }

   public DeploymentInfo setJspConfigDescriptor(JspConfigDescriptor jspConfigDescriptor) {
      this.jspConfigDescriptor = jspConfigDescriptor;
      return this;
   }

   public DefaultServletConfig getDefaultServletConfig() {
      return this.defaultServletConfig;
   }

   public DeploymentInfo setDefaultServletConfig(DefaultServletConfig defaultServletConfig) {
      this.defaultServletConfig = defaultServletConfig;
      return this;
   }

   public DeploymentInfo addLocaleCharsetMapping(String locale, String charset) {
      this.localeCharsetMapping.put(locale, charset);
      return this;
   }

   public Map<String, String> getLocaleCharsetMapping() {
      return this.localeCharsetMapping;
   }

   public SessionManagerFactory getSessionManagerFactory() {
      return this.sessionManagerFactory;
   }

   public DeploymentInfo setSessionManagerFactory(SessionManagerFactory sessionManagerFactory) {
      this.sessionManagerFactory = sessionManagerFactory;
      return this;
   }

   public LoginConfig getLoginConfig() {
      return this.loginConfig;
   }

   public DeploymentInfo setLoginConfig(LoginConfig loginConfig) {
      this.loginConfig = loginConfig;
      return this;
   }

   public IdentityManager getIdentityManager() {
      return this.identityManager;
   }

   public DeploymentInfo setIdentityManager(IdentityManager identityManager) {
      this.identityManager = identityManager;
      return this;
   }

   public ConfidentialPortManager getConfidentialPortManager() {
      return this.confidentialPortManager;
   }

   public DeploymentInfo setConfidentialPortManager(ConfidentialPortManager confidentialPortManager) {
      this.confidentialPortManager = confidentialPortManager;
      return this;
   }

   public DeploymentInfo addSecurityRole(String role) {
      this.securityRoles.add(role);
      return this;
   }

   public DeploymentInfo addSecurityRoles(String... roles) {
      this.securityRoles.addAll(Arrays.asList(roles));
      return this;
   }

   public DeploymentInfo addSecurityRoles(Collection<String> roles) {
      this.securityRoles.addAll(roles);
      return this;
   }

   public Set<String> getSecurityRoles() {
      return this.securityRoles;
   }

   public DeploymentInfo addOuterHandlerChainWrapper(HandlerWrapper wrapper) {
      this.outerHandlerChainWrappers.add(wrapper);
      return this;
   }

   public List<HandlerWrapper> getOuterHandlerChainWrappers() {
      return this.outerHandlerChainWrappers;
   }

   public DeploymentInfo addInnerHandlerChainWrapper(HandlerWrapper wrapper) {
      this.innerHandlerChainWrappers.add(wrapper);
      return this;
   }

   public List<HandlerWrapper> getInnerHandlerChainWrappers() {
      return this.innerHandlerChainWrappers;
   }

   public DeploymentInfo addInitialHandlerChainWrapper(HandlerWrapper wrapper) {
      this.initialHandlerChainWrappers.add(wrapper);
      return this;
   }

   public List<HandlerWrapper> getInitialHandlerChainWrappers() {
      return this.initialHandlerChainWrappers;
   }

   public DeploymentInfo setInitialSecurityWrapper(HandlerWrapper wrapper) {
      this.initialSecurityWrapper = wrapper;
      return this;
   }

   public HandlerWrapper getInitialSecurityWrapper() {
      return this.initialSecurityWrapper;
   }

   public DeploymentInfo addSecurityWrapper(HandlerWrapper wrapper) {
      this.securityWrappers.add(wrapper);
      return this;
   }

   public List<HandlerWrapper> getSecurityWrappers() {
      return this.securityWrappers;
   }

   public DeploymentInfo addNotificationReceiver(NotificationReceiver notificationReceiver) {
      this.notificationReceivers.add(notificationReceiver);
      return this;
   }

   public DeploymentInfo addNotificactionReceivers(NotificationReceiver... notificationReceivers) {
      this.notificationReceivers.addAll(Arrays.asList(notificationReceivers));
      return this;
   }

   public DeploymentInfo addNotificationReceivers(Collection<NotificationReceiver> notificationReceivers) {
      this.notificationReceivers.addAll(notificationReceivers);
      return this;
   }

   public List<NotificationReceiver> getNotificationReceivers() {
      return this.notificationReceivers;
   }

   public ConcurrentMap<String, Object> getServletContextAttributeBackingMap() {
      return this.servletContextAttributeBackingMap;
   }

   public DeploymentInfo setServletContextAttributeBackingMap(ConcurrentMap<String, Object> servletContextAttributeBackingMap) {
      this.servletContextAttributeBackingMap = servletContextAttributeBackingMap;
      return this;
   }

   public ServletSessionConfig getServletSessionConfig() {
      return this.servletSessionConfig;
   }

   public DeploymentInfo setServletSessionConfig(ServletSessionConfig servletSessionConfig) {
      this.servletSessionConfig = servletSessionConfig;
      return this;
   }

   public String getHostName() {
      return this.hostName;
   }

   public DeploymentInfo setHostName(String hostName) {
      this.hostName = hostName;
      return this;
   }

   public boolean isDenyUncoveredHttpMethods() {
      return this.denyUncoveredHttpMethods;
   }

   public DeploymentInfo setDenyUncoveredHttpMethods(boolean denyUncoveredHttpMethods) {
      this.denyUncoveredHttpMethods = denyUncoveredHttpMethods;
      return this;
   }

   public ServletStackTraces getServletStackTraces() {
      return this.servletStackTraces;
   }

   public DeploymentInfo setServletStackTraces(ServletStackTraces servletStackTraces) {
      this.servletStackTraces = servletStackTraces;
      return this;
   }

   public boolean isInvalidateSessionOnLogout() {
      return this.invalidateSessionOnLogout;
   }

   public DeploymentInfo setInvalidateSessionOnLogout(boolean invalidateSessionOnLogout) {
      this.invalidateSessionOnLogout = invalidateSessionOnLogout;
      return this;
   }

   public int getDefaultCookieVersion() {
      return this.defaultCookieVersion;
   }

   public DeploymentInfo setDefaultCookieVersion(int defaultCookieVersion) {
      this.defaultCookieVersion = defaultCookieVersion;
      return this;
   }

   public SessionPersistenceManager getSessionPersistenceManager() {
      return this.sessionPersistenceManager;
   }

   public DeploymentInfo setSessionPersistenceManager(SessionPersistenceManager sessionPersistenceManager) {
      this.sessionPersistenceManager = sessionPersistenceManager;
      return this;
   }

   public AuthorizationManager getAuthorizationManager() {
      return this.authorizationManager;
   }

   public DeploymentInfo setAuthorizationManager(AuthorizationManager authorizationManager) {
      this.authorizationManager = authorizationManager;
      return this;
   }

   public DeploymentInfo addPrincipalVsRoleMapping(String principal, String mapping) {
      Set<String> set = (Set)this.principalVersusRolesMap.get(principal);
      if (set == null) {
         this.principalVersusRolesMap.put(principal, set = new HashSet());
      }

      ((Set)set).add(mapping);
      return this;
   }

   public DeploymentInfo addPrincipalVsRoleMappings(String principal, String... mappings) {
      Set<String> set = (Set)this.principalVersusRolesMap.get(principal);
      if (set == null) {
         this.principalVersusRolesMap.put(principal, set = new HashSet());
      }

      ((Set)set).addAll(Arrays.asList(mappings));
      return this;
   }

   public DeploymentInfo addPrincipalVsRoleMappings(String principal, Collection<String> mappings) {
      Set<String> set = (Set)this.principalVersusRolesMap.get(principal);
      if (set == null) {
         this.principalVersusRolesMap.put(principal, set = new HashSet());
      }

      ((Set)set).addAll(mappings);
      return this;
   }

   public Map<String, Set<String>> getPrincipalVersusRolesMap() {
      return this.principalVersusRolesMap;
   }

   public DeploymentInfo clearLoginMethods() {
      if (this.loginConfig != null) {
         this.loginConfig.getAuthMethods().clear();
      }

      return this;
   }

   public DeploymentInfo addFirstAuthenticationMechanism(String name, AuthenticationMechanism mechanism) {
      this.authenticationMechanisms.put(name, new ImmediateAuthenticationMechanismFactory(mechanism));
      if (this.loginConfig == null) {
         this.loginConfig = new LoginConfig((String)null);
      }

      this.loginConfig.addFirstAuthMethod(new AuthMethodConfig(name));
      return this;
   }

   public DeploymentInfo addLastAuthenticationMechanism(String name, AuthenticationMechanism mechanism) {
      this.authenticationMechanisms.put(name, new ImmediateAuthenticationMechanismFactory(mechanism));
      if (this.loginConfig == null) {
         this.loginConfig = new LoginConfig((String)null);
      }

      this.loginConfig.addLastAuthMethod(new AuthMethodConfig(name));
      return this;
   }

   public DeploymentInfo addAuthenticationMechanism(String name, AuthenticationMechanismFactory factory) {
      this.authenticationMechanisms.put(name.toUpperCase(Locale.US), factory);
      return this;
   }

   public Map<String, AuthenticationMechanismFactory> getAuthenticationMechanisms() {
      return this.authenticationMechanisms;
   }

   public boolean isAuthenticationMechanismPresent(String mechanismName) {
      if (this.loginConfig != null) {
         Iterator var2 = this.loginConfig.getAuthMethods().iterator();

         while(var2.hasNext()) {
            AuthMethodConfig method = (AuthMethodConfig)var2.next();
            if (method.getName().equalsIgnoreCase(mechanismName)) {
               return true;
            }
         }
      }

      return false;
   }

   public DeploymentInfo addServletExtension(ServletExtension servletExtension) {
      this.servletExtensions.add(servletExtension);
      return this;
   }

   public List<ServletExtension> getServletExtensions() {
      return this.servletExtensions;
   }

   public AuthenticationMechanism getJaspiAuthenticationMechanism() {
      return this.jaspiAuthenticationMechanism;
   }

   public DeploymentInfo setJaspiAuthenticationMechanism(AuthenticationMechanism jaspiAuthenticationMechanism) {
      this.jaspiAuthenticationMechanism = jaspiAuthenticationMechanism;
      return this;
   }

   public SecurityContextFactory getSecurityContextFactory() {
      return this.securityContextFactory;
   }

   public DeploymentInfo setSecurityContextFactory(SecurityContextFactory securityContextFactory) {
      this.securityContextFactory = securityContextFactory;
      return this;
   }

   public String getServerName() {
      return this.serverName;
   }

   public DeploymentInfo setServerName(String serverName) {
      this.serverName = serverName;
      return this;
   }

   public DeploymentInfo setMetricsCollector(MetricsCollector metricsCollector) {
      this.metricsCollector = metricsCollector;
      return this;
   }

   public MetricsCollector getMetricsCollector() {
      return this.metricsCollector;
   }

   public SessionConfigWrapper getSessionConfigWrapper() {
      return this.sessionConfigWrapper;
   }

   public DeploymentInfo setSessionConfigWrapper(SessionConfigWrapper sessionConfigWrapper) {
      this.sessionConfigWrapper = sessionConfigWrapper;
      return this;
   }

   public boolean isDisableCachingForSecuredPages() {
      return this.disableCachingForSecuredPages;
   }

   public DeploymentInfo setDisableCachingForSecuredPages(boolean disableCachingForSecuredPages) {
      this.disableCachingForSecuredPages = disableCachingForSecuredPages;
      return this;
   }

   public DeploymentInfo addLifecycleInterceptor(LifecycleInterceptor interceptor) {
      this.lifecycleInterceptors.add(interceptor);
      return this;
   }

   public List<LifecycleInterceptor> getLifecycleInterceptors() {
      return this.lifecycleInterceptors;
   }

   public ExceptionHandler getExceptionHandler() {
      return this.exceptionHandler;
   }

   public DeploymentInfo setExceptionHandler(ExceptionHandler exceptionHandler) {
      this.exceptionHandler = exceptionHandler;
      return this;
   }

   public boolean isEscapeErrorMessage() {
      return this.escapeErrorMessage;
   }

   public DeploymentInfo setEscapeErrorMessage(boolean escapeErrorMessage) {
      this.escapeErrorMessage = escapeErrorMessage;
      return this;
   }

   public DeploymentInfo addSessionListener(SessionListener sessionListener) {
      this.sessionListeners.add(sessionListener);
      return this;
   }

   public List<SessionListener> getSessionListeners() {
      return this.sessionListeners;
   }

   public AuthenticationMode getAuthenticationMode() {
      return this.authenticationMode;
   }

   public DeploymentInfo setAuthenticationMode(AuthenticationMode authenticationMode) {
      this.authenticationMode = authenticationMode;
      return this;
   }

   public MultipartConfigElement getDefaultMultipartConfig() {
      return this.defaultMultipartConfig;
   }

   public DeploymentInfo setDefaultMultipartConfig(MultipartConfigElement defaultMultipartConfig) {
      this.defaultMultipartConfig = defaultMultipartConfig;
      return this;
   }

   public int getContentTypeCacheSize() {
      return this.contentTypeCacheSize;
   }

   public DeploymentInfo setContentTypeCacheSize(int contentTypeCacheSize) {
      this.contentTypeCacheSize = contentTypeCacheSize;
      return this;
   }

   public SessionIdGenerator getSessionIdGenerator() {
      return this.sessionIdGenerator;
   }

   public DeploymentInfo setSessionIdGenerator(SessionIdGenerator sessionIdGenerator) {
      this.sessionIdGenerator = sessionIdGenerator;
      return this;
   }

   public boolean isSendCustomReasonPhraseOnError() {
      return this.sendCustomReasonPhraseOnError;
   }

   public CrawlerSessionManagerConfig getCrawlerSessionManagerConfig() {
      return this.crawlerSessionManagerConfig;
   }

   public DeploymentInfo setCrawlerSessionManagerConfig(CrawlerSessionManagerConfig crawlerSessionManagerConfig) {
      this.crawlerSessionManagerConfig = crawlerSessionManagerConfig;
      return this;
   }

   public DeploymentInfo setSendCustomReasonPhraseOnError(boolean sendCustomReasonPhraseOnError) {
      this.sendCustomReasonPhraseOnError = sendCustomReasonPhraseOnError;
      return this;
   }

   public boolean isChangeSessionIdOnLogin() {
      return this.changeSessionIdOnLogin;
   }

   public DeploymentInfo setChangeSessionIdOnLogin(boolean changeSessionIdOnLogin) {
      this.changeSessionIdOnLogin = changeSessionIdOnLogin;
      return this;
   }

   public boolean isUseCachedAuthenticationMechanism() {
      return this.useCachedAuthenticationMechanism;
   }

   public DeploymentInfo setUseCachedAuthenticationMechanism(boolean useCachedAuthenticationMechanism) {
      this.useCachedAuthenticationMechanism = useCachedAuthenticationMechanism;
      return this;
   }

   public boolean isSecurityDisabled() {
      return this.securityDisabled;
   }

   public DeploymentInfo setSecurityDisabled(boolean securityDisabled) {
      this.securityDisabled = securityDisabled;
      return this;
   }

   public boolean isCheckOtherSessionManagers() {
      return this.checkOtherSessionManagers;
   }

   public DeploymentInfo setCheckOtherSessionManagers(boolean checkOtherSessionManagers) {
      this.checkOtherSessionManagers = checkOtherSessionManagers;
      return this;
   }

   public String getDefaultRequestEncoding() {
      return this.defaultRequestEncoding;
   }

   public DeploymentInfo setDefaultRequestEncoding(String defaultRequestEncoding) {
      this.defaultRequestEncoding = defaultRequestEncoding;
      return this;
   }

   public String getDefaultResponseEncoding() {
      return this.defaultResponseEncoding;
   }

   public DeploymentInfo setDefaultResponseEncoding(String defaultResponseEncoding) {
      this.defaultResponseEncoding = defaultResponseEncoding;
      return this;
   }

   public DeploymentInfo addPreCompressedResourceEncoding(String encoding, String extension) {
      this.preCompressedResources.put(encoding, extension);
      return this;
   }

   public Map<String, String> getPreCompressedResources() {
      return this.preCompressedResources;
   }

   public int getContainerMajorVersion() {
      return this.containerMajorVersion;
   }

   public DeploymentInfo setContainerMajorVersion(int containerMajorVersion) {
      this.containerMajorVersion = containerMajorVersion;
      return this;
   }

   public int getContainerMinorVersion() {
      return this.containerMinorVersion;
   }

   public DeploymentInfo setContainerMinorVersion(int containerMinorVersion) {
      this.containerMinorVersion = containerMinorVersion;
      return this;
   }

   public boolean isPreservePathOnForward() {
      return this.preservePathOnForward;
   }

   public void setPreservePathOnForward(boolean preservePathOnForward) {
      this.preservePathOnForward = preservePathOnForward;
   }

   public DeploymentInfo addDeploymentCompleteListener(ServletContextListener servletContextListener) {
      this.deploymentCompleteListeners.add(servletContextListener);
      return this;
   }

   public List<ServletContextListener> getDeploymentCompleteListeners() {
      return this.deploymentCompleteListeners;
   }

   public DeploymentInfo clone() {
      DeploymentInfo info = (new DeploymentInfo()).setClassLoader(this.classLoader).setContextPath(this.contextPath).setResourceManager(this.resourceManager).setMajorVersion(this.majorVersion).setMinorVersion(this.minorVersion).setDeploymentName(this.deploymentName).setClassIntrospecter(this.classIntrospecter);
      Iterator var2 = this.servlets.entrySet().iterator();

      Map.Entry e;
      while(var2.hasNext()) {
         e = (Map.Entry)var2.next();
         info.addServlet(((ServletInfo)e.getValue()).clone());
      }

      var2 = this.filters.entrySet().iterator();

      while(var2.hasNext()) {
         e = (Map.Entry)var2.next();
         info.addFilter(((FilterInfo)e.getValue()).clone());
      }

      info.displayName = this.displayName;
      info.filterUrlMappings.addAll(this.filterUrlMappings);
      info.filterServletNameMappings.addAll(this.filterServletNameMappings);
      info.listeners.addAll(this.listeners);
      info.servletContainerInitializers.addAll(this.servletContainerInitializers);
      info.threadSetupActions.addAll(this.threadSetupActions);
      info.initParameters.putAll(this.initParameters);
      info.servletContextAttributes.putAll(this.servletContextAttributes);
      info.welcomePages.addAll(this.welcomePages);
      info.errorPages.addAll(this.errorPages);
      info.mimeMappings.addAll(this.mimeMappings);
      info.executor = this.executor;
      info.asyncExecutor = this.asyncExecutor;
      info.tempDir = this.tempDir;
      info.jspConfigDescriptor = this.jspConfigDescriptor;
      info.defaultServletConfig = this.defaultServletConfig;
      info.localeCharsetMapping.putAll(this.localeCharsetMapping);
      info.sessionManagerFactory = this.sessionManagerFactory;
      if (this.loginConfig != null) {
         info.loginConfig = this.loginConfig.clone();
      }

      info.identityManager = this.identityManager;
      info.confidentialPortManager = this.confidentialPortManager;
      info.defaultEncoding = this.defaultEncoding;
      info.urlEncoding = this.urlEncoding;
      info.securityConstraints.addAll(this.securityConstraints);
      info.outerHandlerChainWrappers.addAll(this.outerHandlerChainWrappers);
      info.innerHandlerChainWrappers.addAll(this.innerHandlerChainWrappers);
      info.initialSecurityWrapper = this.initialSecurityWrapper;
      info.securityWrappers.addAll(this.securityWrappers);
      info.initialHandlerChainWrappers.addAll(this.initialHandlerChainWrappers);
      info.securityRoles.addAll(this.securityRoles);
      info.notificationReceivers.addAll(this.notificationReceivers);
      info.allowNonStandardWrappers = this.allowNonStandardWrappers;
      info.defaultSessionTimeout = this.defaultSessionTimeout;
      info.servletContextAttributeBackingMap = this.servletContextAttributeBackingMap;
      info.servletSessionConfig = this.servletSessionConfig;
      info.hostName = this.hostName;
      info.denyUncoveredHttpMethods = this.denyUncoveredHttpMethods;
      info.servletStackTraces = this.servletStackTraces;
      info.invalidateSessionOnLogout = this.invalidateSessionOnLogout;
      info.defaultCookieVersion = this.defaultCookieVersion;
      info.sessionPersistenceManager = this.sessionPersistenceManager;
      var2 = this.principalVersusRolesMap.entrySet().iterator();

      while(var2.hasNext()) {
         e = (Map.Entry)var2.next();
         info.principalVersusRolesMap.put(e.getKey(), new HashSet((Collection)e.getValue()));
      }

      info.ignoreFlush = this.ignoreFlush;
      info.authorizationManager = this.authorizationManager;
      info.authenticationMechanisms.putAll(this.authenticationMechanisms);
      info.servletExtensions.addAll(this.servletExtensions);
      info.jaspiAuthenticationMechanism = this.jaspiAuthenticationMechanism;
      info.securityContextFactory = this.securityContextFactory;
      info.serverName = this.serverName;
      info.metricsCollector = this.metricsCollector;
      info.sessionConfigWrapper = this.sessionConfigWrapper;
      info.eagerFilterInit = this.eagerFilterInit;
      info.disableCachingForSecuredPages = this.disableCachingForSecuredPages;
      info.exceptionHandler = this.exceptionHandler;
      info.escapeErrorMessage = this.escapeErrorMessage;
      info.sessionListeners.addAll(this.sessionListeners);
      info.lifecycleInterceptors.addAll(this.lifecycleInterceptors);
      info.authenticationMode = this.authenticationMode;
      info.defaultMultipartConfig = this.defaultMultipartConfig;
      info.contentTypeCacheSize = this.contentTypeCacheSize;
      info.sessionIdGenerator = this.sessionIdGenerator;
      info.sendCustomReasonPhraseOnError = this.sendCustomReasonPhraseOnError;
      info.changeSessionIdOnLogin = this.changeSessionIdOnLogin;
      info.crawlerSessionManagerConfig = this.crawlerSessionManagerConfig;
      info.securityDisabled = this.securityDisabled;
      info.useCachedAuthenticationMechanism = this.useCachedAuthenticationMechanism;
      info.checkOtherSessionManagers = this.checkOtherSessionManagers;
      info.defaultRequestEncoding = this.defaultRequestEncoding;
      info.defaultResponseEncoding = this.defaultResponseEncoding;
      info.preCompressedResources.putAll(this.preCompressedResources);
      info.containerMajorVersion = this.containerMajorVersion;
      info.containerMinorVersion = this.containerMinorVersion;
      info.deploymentCompleteListeners.addAll(this.deploymentCompleteListeners);
      info.preservePathOnForward = this.preservePathOnForward;
      return info;
   }

   static {
      Package servletPackage = ServletContextListener.class.getPackage();
      DEFAULT_MAJOR_VERSION = servletPackage.getName().startsWith("jakarta.") ? 5 : 4;
   }
}
