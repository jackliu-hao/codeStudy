package io.undertow.servlet.spec;

import io.undertow.Version;
import io.undertow.server.HandlerWrapper;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.cache.LRUCache;
import io.undertow.server.handlers.resource.Resource;
import io.undertow.server.session.PathParameterSessionConfig;
import io.undertow.server.session.Session;
import io.undertow.server.session.SessionConfig;
import io.undertow.server.session.SessionManager;
import io.undertow.server.session.SslSessionConfig;
import io.undertow.servlet.UndertowServletLogger;
import io.undertow.servlet.UndertowServletMessages;
import io.undertow.servlet.api.Deployment;
import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.api.DeploymentManager;
import io.undertow.servlet.api.FilterInfo;
import io.undertow.servlet.api.HttpMethodSecurityInfo;
import io.undertow.servlet.api.InstanceFactory;
import io.undertow.servlet.api.ListenerInfo;
import io.undertow.servlet.api.SecurityInfo;
import io.undertow.servlet.api.ServletContainer;
import io.undertow.servlet.api.ServletInfo;
import io.undertow.servlet.api.ServletSecurityInfo;
import io.undertow.servlet.api.SessionConfigWrapper;
import io.undertow.servlet.api.ThreadSetupHandler;
import io.undertow.servlet.api.TransportGuaranteeType;
import io.undertow.servlet.core.ApplicationListeners;
import io.undertow.servlet.core.ManagedListener;
import io.undertow.servlet.core.ManagedServlet;
import io.undertow.servlet.handlers.ServletChain;
import io.undertow.servlet.handlers.ServletHandler;
import io.undertow.servlet.handlers.ServletRequestContext;
import io.undertow.servlet.util.EmptyEnumeration;
import io.undertow.servlet.util.ImmediateInstanceFactory;
import io.undertow.servlet.util.IteratorEnumeration;
import io.undertow.util.AttachmentKey;
import io.undertow.util.CanonicalPathUtils;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Enumeration;
import java.util.EventListener;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import javax.annotation.security.DeclareRoles;
import javax.annotation.security.RunAs;
import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import javax.servlet.FilterRegistration;
import javax.servlet.MultipartConfigElement;
import javax.servlet.ReadListener;
import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import javax.servlet.ServletRequest;
import javax.servlet.SessionTrackingMode;
import javax.servlet.WriteListener;
import javax.servlet.annotation.HttpMethodConstraint;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.ServletSecurity;
import javax.servlet.descriptor.JspConfigDescriptor;

public class ServletContextImpl implements ServletContext {
   private final ServletContainer servletContainer;
   private final Deployment deployment;
   private volatile DeploymentInfo deploymentInfo;
   private final ConcurrentMap<String, Object> attributes;
   private final SessionCookieConfigImpl sessionCookieConfig;
   private final AttachmentKey<HttpSessionImpl> sessionAttachmentKey = AttachmentKey.create(HttpSessionImpl.class);
   private volatile Set<SessionTrackingMode> sessionTrackingModes;
   private volatile Set<SessionTrackingMode> defaultSessionTrackingModes;
   private volatile SessionConfig sessionConfig;
   private volatile boolean initialized;
   private int filterMappingUrlPatternInsertPosition;
   private int filterMappingServletNameInsertPosition;
   private final LRUCache<String, ContentTypeInfo> contentTypeCache;
   private volatile ThreadSetupHandler.Action<Void, WriteListener> onWritePossibleTask;
   private volatile ThreadSetupHandler.Action<Void, Runnable> runnableTask;
   private volatile ThreadSetupHandler.Action<Void, ReadListener> onDataAvailableTask;
   private volatile ThreadSetupHandler.Action<Void, ReadListener> onAllDataReadTask;
   private volatile ThreadSetupHandler.Action<Void, ThreadSetupHandler.Action<Void, Object>> invokeActionTask;
   private volatile int defaultSessionTimeout;

   public ServletContextImpl(ServletContainer servletContainer, Deployment deployment) {
      this.sessionTrackingModes = new HashSet(Arrays.asList(SessionTrackingMode.COOKIE, SessionTrackingMode.URL));
      this.defaultSessionTrackingModes = new HashSet(Arrays.asList(SessionTrackingMode.COOKIE, SessionTrackingMode.URL));
      this.initialized = false;
      this.filterMappingUrlPatternInsertPosition = 0;
      this.filterMappingServletNameInsertPosition = 0;
      this.servletContainer = servletContainer;
      this.deployment = deployment;
      this.deploymentInfo = deployment.getDeploymentInfo();
      this.sessionCookieConfig = new SessionCookieConfigImpl(this);
      this.sessionCookieConfig.setPath(this.deploymentInfo.getContextPath());
      if (this.deploymentInfo.getServletContextAttributeBackingMap() == null) {
         this.attributes = new ConcurrentHashMap();
      } else {
         this.attributes = this.deploymentInfo.getServletContextAttributeBackingMap();
      }

      this.attributes.putAll(deployment.getDeploymentInfo().getServletContextAttributes());
      this.contentTypeCache = new LRUCache(deployment.getDeploymentInfo().getContentTypeCacheSize(), -1, true);
      this.defaultSessionTimeout = this.deploymentInfo.getDefaultSessionTimeout() / 60;
   }

   public void initDone() {
      this.initialized = true;
      Set<SessionTrackingMode> trackingMethods = this.sessionTrackingModes;
      SessionConfig sessionConfig = this.sessionCookieConfig;
      if (trackingMethods != null && !trackingMethods.isEmpty()) {
         if (this.sessionTrackingModes.contains(SessionTrackingMode.SSL)) {
            sessionConfig = new SslSessionConfig(this.deployment.getSessionManager());
         } else if (this.sessionTrackingModes.contains(SessionTrackingMode.COOKIE) && this.sessionTrackingModes.contains(SessionTrackingMode.URL)) {
            this.sessionCookieConfig.setFallback(new PathParameterSessionConfig(this.sessionCookieConfig.getName().toLowerCase(Locale.ENGLISH)));
         } else if (this.sessionTrackingModes.contains(SessionTrackingMode.URL)) {
            sessionConfig = new PathParameterSessionConfig(this.sessionCookieConfig.getName().toLowerCase(Locale.ENGLISH));
         }
      }

      SessionConfigWrapper wrapper = this.deploymentInfo.getSessionConfigWrapper();
      if (wrapper != null) {
         sessionConfig = wrapper.wrap((SessionConfig)sessionConfig, this.deployment);
      }

      this.sessionConfig = new ServletContextSessionConfig((SessionConfig)sessionConfig);
      this.onWritePossibleTask = this.deployment.createThreadSetupAction(new ThreadSetupHandler.Action<Void, WriteListener>() {
         public Void call(HttpServerExchange exchange, WriteListener context) throws Exception {
            context.onWritePossible();
            return null;
         }
      });
      this.runnableTask = this.deployment.createThreadSetupAction(new ThreadSetupHandler.Action<Void, Runnable>() {
         public Void call(HttpServerExchange exchange, Runnable runnable) throws Exception {
            runnable.run();
            return null;
         }
      });
      this.onDataAvailableTask = this.deployment.createThreadSetupAction(new ThreadSetupHandler.Action<Void, ReadListener>() {
         public Void call(HttpServerExchange exchange, ReadListener context) throws Exception {
            context.onDataAvailable();
            return null;
         }
      });
      this.onAllDataReadTask = this.deployment.createThreadSetupAction(new ThreadSetupHandler.Action<Void, ReadListener>() {
         public Void call(HttpServerExchange exchange, ReadListener context) throws Exception {
            context.onAllDataRead();
            return null;
         }
      });
      this.invokeActionTask = this.deployment.createThreadSetupAction(new ThreadSetupHandler.Action<Void, ThreadSetupHandler.Action<Void, Object>>() {
         public Void call(HttpServerExchange exchange, ThreadSetupHandler.Action<Void, Object> context) throws Exception {
            context.call(exchange, (Object)null);
            return null;
         }
      });
   }

   private DeploymentInfo getDeploymentInfo() {
      DeploymentInfo deploymentInfo = this.deploymentInfo;
      if (deploymentInfo == null) {
         throw UndertowServletLogger.ROOT_LOGGER.contextDestroyed();
      } else {
         return deploymentInfo;
      }
   }

   public String getContextPath() {
      String contextPath = this.getDeploymentInfo().getContextPath();
      return contextPath.equals("/") ? "" : contextPath;
   }

   public ServletContext getContext(String uripath) {
      DeploymentManager deploymentByPath = this.servletContainer.getDeploymentByPath(uripath);
      return deploymentByPath == null ? null : deploymentByPath.getDeployment().getServletContext();
   }

   public int getMajorVersion() {
      return this.getDeploymentInfo().getContainerMajorVersion();
   }

   public int getMinorVersion() {
      return this.getDeploymentInfo().getContainerMinorVersion();
   }

   public int getEffectiveMajorVersion() {
      return this.getDeploymentInfo().getMajorVersion();
   }

   public int getEffectiveMinorVersion() {
      return this.getDeploymentInfo().getMinorVersion();
   }

   public String getMimeType(String file) {
      if (file == null) {
         return null;
      } else {
         String lower = file.toLowerCase(Locale.ENGLISH);
         int pos = lower.lastIndexOf(46);
         return pos == -1 ? null : (String)this.deployment.getMimeExtensionMappings().get(lower.substring(pos + 1));
      }
   }

   public Set<String> getResourcePaths(String path) {
      Resource resource;
      try {
         resource = this.getDeploymentInfo().getResourceManager().getResource(path);
      } catch (IOException var9) {
         return null;
      }

      if (resource != null && resource.isDirectory()) {
         Set<String> resources = new HashSet();
         Iterator var4 = resource.list().iterator();

         while(var4.hasNext()) {
            Resource res = (Resource)var4.next();
            Path file = res.getFilePath();
            if (file != null) {
               Path base = res.getResourceManagerRootPath();
               if (base == null) {
                  resources.add(file.toString());
               } else {
                  String filePath = file.toAbsolutePath().toString().substring(base.toAbsolutePath().toString().length());
                  filePath = filePath.replace('\\', '/');
                  if (Files.isDirectory(file, new LinkOption[0])) {
                     filePath = filePath + "/";
                  }

                  resources.add(filePath);
               }
            }
         }

         return resources;
      } else {
         return null;
      }
   }

   public URL getResource(String path) throws MalformedURLException {
      if (path != null && path.startsWith("/")) {
         Resource resource = null;

         try {
            resource = this.getDeploymentInfo().getResourceManager().getResource(path);
         } catch (IOException var4) {
            return null;
         }

         return resource == null ? null : resource.getUrl();
      } else {
         throw UndertowServletMessages.MESSAGES.pathMustStartWithSlash(path);
      }
   }

   public InputStream getResourceAsStream(String path) {
      Resource resource = null;

      try {
         resource = this.getDeploymentInfo().getResourceManager().getResource(path);
      } catch (IOException var6) {
         return null;
      }

      if (resource == null) {
         return null;
      } else {
         try {
            return resource.getFile() != null ? new BufferedInputStream(new FileInputStream(resource.getFile())) : new BufferedInputStream(resource.getUrl().openStream());
         } catch (FileNotFoundException var4) {
            return null;
         } catch (IOException var5) {
            return null;
         }
      }
   }

   public RequestDispatcher getRequestDispatcher(String path) {
      if (path == null) {
         return null;
      } else if (!path.startsWith("/")) {
         throw UndertowServletMessages.MESSAGES.pathMustStartWithSlashForRequestDispatcher(path);
      } else {
         String realPath = CanonicalPathUtils.canonicalize(path, true);
         return realPath == null ? null : new RequestDispatcherImpl(realPath, this);
      }
   }

   public RequestDispatcher getNamedDispatcher(String name) {
      ServletChain chain = this.deployment.getServletPaths().getServletHandlerByName(name);
      return chain != null ? new RequestDispatcherImpl(chain, this) : null;
   }

   public Servlet getServlet(String name) throws ServletException {
      return (Servlet)this.deployment.getServletPaths().getServletHandlerByName(name).getManagedServlet().getServlet().getInstance();
   }

   public Enumeration<Servlet> getServlets() {
      return EmptyEnumeration.instance();
   }

   public Enumeration<String> getServletNames() {
      return EmptyEnumeration.instance();
   }

   public void log(String msg) {
      UndertowServletLogger.ROOT_LOGGER.info(msg);
   }

   public void log(Exception exception, String msg) {
      UndertowServletLogger.ROOT_LOGGER.error(msg, exception);
   }

   public void log(String message, Throwable throwable) {
      UndertowServletLogger.ROOT_LOGGER.error(message, throwable);
   }

   public String getRealPath(String path) {
      if (path == null) {
         return null;
      } else {
         DeploymentInfo deploymentInfo = this.getDeploymentInfo();
         String canonicalPath = CanonicalPathUtils.canonicalize(path);

         Resource resource;
         try {
            resource = deploymentInfo.getResourceManager().getResource(canonicalPath);
            if (resource == null) {
               Resource deploymentRoot = deploymentInfo.getResourceManager().getResource("/");
               if (deploymentRoot == null) {
                  return null;
               }

               Path root = deploymentRoot.getFilePath();
               if (root == null) {
                  return null;
               }

               if (!canonicalPath.startsWith("/")) {
                  canonicalPath = "/" + canonicalPath;
               }

               if (File.separatorChar != '/') {
                  canonicalPath = canonicalPath.replace('/', File.separatorChar);
               }

               return root.toAbsolutePath().toString() + canonicalPath;
            }
         } catch (IOException var7) {
            return null;
         }

         Path file = resource.getFilePath();
         return file == null ? null : file.toAbsolutePath().toString();
      }
   }

   public String getServerInfo() {
      return this.getDeploymentInfo().getServerName() + " - " + Version.getVersionString();
   }

   public String getInitParameter(String name) {
      if (name == null) {
         throw UndertowServletMessages.MESSAGES.nullName();
      } else {
         return (String)this.getDeploymentInfo().getInitParameters().get(name);
      }
   }

   public Enumeration<String> getInitParameterNames() {
      return new IteratorEnumeration(this.getDeploymentInfo().getInitParameters().keySet().iterator());
   }

   public boolean setInitParameter(String name, String value) {
      if (name == null) {
         throw UndertowServletMessages.MESSAGES.paramCannotBeNullNPE("name");
      } else if (this.getDeploymentInfo().getInitParameters().containsKey(name)) {
         return false;
      } else {
         this.getDeploymentInfo().addInitParameter(name, value);
         return true;
      }
   }

   public Object getAttribute(String name) {
      if (name == null) {
         throw UndertowServletMessages.MESSAGES.nullName();
      } else {
         return this.attributes.get(name);
      }
   }

   public Enumeration<String> getAttributeNames() {
      return new IteratorEnumeration(this.attributes.keySet().iterator());
   }

   public void setAttribute(String name, Object object) {
      if (name == null) {
         throw UndertowServletMessages.MESSAGES.nullName();
      } else {
         Object existing;
         if (object == null) {
            existing = this.attributes.remove(name);
            if (this.deployment.getApplicationListeners() != null && existing != null) {
               this.deployment.getApplicationListeners().servletContextAttributeRemoved(name, existing);
            }
         } else {
            existing = this.attributes.put(name, object);
            if (this.deployment.getApplicationListeners() != null) {
               if (existing != null) {
                  this.deployment.getApplicationListeners().servletContextAttributeReplaced(name, existing);
               } else {
                  this.deployment.getApplicationListeners().servletContextAttributeAdded(name, object);
               }
            }
         }

      }
   }

   public void removeAttribute(String name) {
      Object exiting = this.attributes.remove(name);
      this.deployment.getApplicationListeners().servletContextAttributeRemoved(name, exiting);
   }

   public String getServletContextName() {
      return this.getDeploymentInfo().getDisplayName();
   }

   public ServletRegistration.Dynamic addServlet(String servletName, String className) {
      return this.addServlet(servletName, className, Collections.emptyList());
   }

   public ServletRegistration.Dynamic addServlet(String servletName, String className, List<HandlerWrapper> wrappers) {
      this.ensureNotProgramaticListener();
      this.ensureNotInitialized();
      this.ensureServletNameNotNull(servletName);

      try {
         DeploymentInfo deploymentInfo = this.getDeploymentInfo();
         if (deploymentInfo.getServlets().containsKey(servletName)) {
            return null;
         } else {
            Class<? extends Servlet> servletClass = deploymentInfo.getClassLoader().loadClass(className);
            ServletInfo servlet = new ServletInfo(servletName, servletClass, deploymentInfo.getClassIntrospecter().createInstanceFactory(servletClass));
            Iterator var7 = wrappers.iterator();

            while(var7.hasNext()) {
               HandlerWrapper i = (HandlerWrapper)var7.next();
               servlet.addHandlerChainWrapper(i);
            }

            this.readServletAnnotations(servlet, deploymentInfo);
            deploymentInfo.addServlet(servlet);
            ServletHandler handler = this.deployment.getServlets().addServlet(servlet);
            return new ServletRegistrationImpl(servlet, handler.getManagedServlet(), this.deployment);
         }
      } catch (ClassNotFoundException var9) {
         throw UndertowServletMessages.MESSAGES.cannotLoadClass(className, var9);
      } catch (NoSuchMethodException var10) {
         throw UndertowServletMessages.MESSAGES.couldNotCreateFactory(className, var10);
      }
   }

   public ServletRegistration.Dynamic addServlet(String servletName, Servlet servlet) {
      this.ensureNotProgramaticListener();
      this.ensureNotInitialized();
      this.ensureServletNameNotNull(servletName);
      DeploymentInfo deploymentInfo = this.getDeploymentInfo();
      if (deploymentInfo.getServlets().containsKey(servletName)) {
         return null;
      } else {
         ServletInfo s = new ServletInfo(servletName, servlet.getClass(), new ImmediateInstanceFactory(servlet));
         this.readServletAnnotations(s, deploymentInfo);
         deploymentInfo.addServlet(s);
         ServletHandler handler = this.deployment.getServlets().addServlet(s);
         return new ServletRegistrationImpl(s, handler.getManagedServlet(), this.deployment);
      }
   }

   public ServletRegistration.Dynamic addServlet(String servletName, Class<? extends Servlet> servletClass) {
      this.ensureNotProgramaticListener();
      this.ensureNotInitialized();
      this.ensureServletNameNotNull(servletName);
      DeploymentInfo deploymentInfo = this.getDeploymentInfo();
      if (deploymentInfo.getServlets().containsKey(servletName)) {
         return null;
      } else {
         try {
            ServletInfo servlet = new ServletInfo(servletName, servletClass, deploymentInfo.getClassIntrospecter().createInstanceFactory(servletClass));
            this.readServletAnnotations(servlet, deploymentInfo);
            deploymentInfo.addServlet(servlet);
            ServletHandler handler = this.deployment.getServlets().addServlet(servlet);
            return new ServletRegistrationImpl(servlet, handler.getManagedServlet(), this.deployment);
         } catch (NoSuchMethodException var6) {
            throw UndertowServletMessages.MESSAGES.couldNotCreateFactory(servletClass.getName(), var6);
         }
      }
   }

   private void ensureServletNameNotNull(String servletName) {
      if (servletName == null) {
         throw UndertowServletMessages.MESSAGES.servletNameNull();
      }
   }

   public <T extends Servlet> T createServlet(Class<T> clazz) throws ServletException {
      this.ensureNotProgramaticListener();

      try {
         return (Servlet)this.getDeploymentInfo().getClassIntrospecter().createInstanceFactory(clazz).createInstance().getInstance();
      } catch (Exception var3) {
         throw UndertowServletMessages.MESSAGES.couldNotInstantiateComponent(clazz.getName(), var3);
      }
   }

   public ServletRegistration getServletRegistration(String servletName) {
      this.ensureNotProgramaticListener();
      ManagedServlet servlet = this.deployment.getServlets().getManagedServlet(servletName);
      return servlet == null ? null : new ServletRegistrationImpl(servlet.getServletInfo(), servlet, this.deployment);
   }

   public Map<String, ? extends ServletRegistration> getServletRegistrations() {
      this.ensureNotProgramaticListener();
      Map<String, ServletRegistration> ret = new HashMap();
      Iterator var2 = this.deployment.getServlets().getServletHandlers().entrySet().iterator();

      while(var2.hasNext()) {
         Map.Entry<String, ServletHandler> entry = (Map.Entry)var2.next();
         ret.put(entry.getKey(), new ServletRegistrationImpl(((ServletHandler)entry.getValue()).getManagedServlet().getServletInfo(), ((ServletHandler)entry.getValue()).getManagedServlet(), this.deployment));
      }

      return ret;
   }

   public FilterRegistration.Dynamic addFilter(String filterName, String className) {
      this.ensureNotProgramaticListener();
      this.ensureNotInitialized();
      DeploymentInfo deploymentInfo = this.getDeploymentInfo();
      if (deploymentInfo.getFilters().containsKey(filterName)) {
         return null;
      } else {
         try {
            Class<? extends Filter> filterClass = deploymentInfo.getClassLoader().loadClass(className);
            FilterInfo filter = new FilterInfo(filterName, filterClass, deploymentInfo.getClassIntrospecter().createInstanceFactory(filterClass));
            deploymentInfo.addFilter(filter);
            this.deployment.getFilters().addFilter(filter);
            return new FilterRegistrationImpl(filter, this.deployment, this);
         } catch (ClassNotFoundException var6) {
            throw UndertowServletMessages.MESSAGES.cannotLoadClass(className, var6);
         } catch (NoSuchMethodException var7) {
            throw UndertowServletMessages.MESSAGES.couldNotCreateFactory(className, var7);
         }
      }
   }

   public FilterRegistration.Dynamic addFilter(String filterName, Filter filter) {
      this.ensureNotProgramaticListener();
      this.ensureNotInitialized();
      DeploymentInfo deploymentInfo = this.getDeploymentInfo();
      if (deploymentInfo.getFilters().containsKey(filterName)) {
         return null;
      } else {
         FilterInfo f = new FilterInfo(filterName, filter.getClass(), new ImmediateInstanceFactory(filter));
         deploymentInfo.addFilter(f);
         this.deployment.getFilters().addFilter(f);
         return new FilterRegistrationImpl(f, this.deployment, this);
      }
   }

   public FilterRegistration.Dynamic addFilter(String filterName, Class<? extends Filter> filterClass) {
      this.ensureNotProgramaticListener();
      this.ensureNotInitialized();
      DeploymentInfo deploymentInfo = this.getDeploymentInfo();
      if (deploymentInfo.getFilters().containsKey(filterName)) {
         return null;
      } else {
         try {
            FilterInfo filter = new FilterInfo(filterName, filterClass, deploymentInfo.getClassIntrospecter().createInstanceFactory(filterClass));
            deploymentInfo.addFilter(filter);
            this.deployment.getFilters().addFilter(filter);
            return new FilterRegistrationImpl(filter, this.deployment, this);
         } catch (NoSuchMethodException var5) {
            throw UndertowServletMessages.MESSAGES.couldNotCreateFactory(filterClass.getName(), var5);
         }
      }
   }

   public <T extends Filter> T createFilter(Class<T> clazz) throws ServletException {
      this.ensureNotProgramaticListener();

      try {
         return (Filter)this.getDeploymentInfo().getClassIntrospecter().createInstanceFactory(clazz).createInstance().getInstance();
      } catch (Exception var3) {
         throw UndertowServletMessages.MESSAGES.couldNotInstantiateComponent(clazz.getName(), var3);
      }
   }

   public FilterRegistration getFilterRegistration(String filterName) {
      this.ensureNotProgramaticListener();
      FilterInfo filterInfo = (FilterInfo)this.getDeploymentInfo().getFilters().get(filterName);
      return filterInfo == null ? null : new FilterRegistrationImpl(filterInfo, this.deployment, this);
   }

   public Map<String, ? extends FilterRegistration> getFilterRegistrations() {
      this.ensureNotProgramaticListener();
      Map<String, FilterRegistration> ret = new HashMap();
      Iterator var2 = this.getDeploymentInfo().getFilters().entrySet().iterator();

      while(var2.hasNext()) {
         Map.Entry<String, FilterInfo> entry = (Map.Entry)var2.next();
         ret.put(entry.getKey(), new FilterRegistrationImpl((FilterInfo)entry.getValue(), this.deployment, this));
      }

      return ret;
   }

   public SessionCookieConfigImpl getSessionCookieConfig() {
      this.ensureNotProgramaticListener();
      return this.sessionCookieConfig;
   }

   public void setSessionTrackingModes(Set<SessionTrackingMode> sessionTrackingModes) {
      this.ensureNotProgramaticListener();
      this.ensureNotInitialized();
      if (sessionTrackingModes.size() > 1 && sessionTrackingModes.contains(SessionTrackingMode.SSL)) {
         throw UndertowServletMessages.MESSAGES.sslCannotBeCombinedWithAnyOtherMethod();
      } else {
         this.sessionTrackingModes = new HashSet(sessionTrackingModes);
      }
   }

   public Set<SessionTrackingMode> getDefaultSessionTrackingModes() {
      this.ensureNotProgramaticListener();
      return this.defaultSessionTrackingModes;
   }

   public Set<SessionTrackingMode> getEffectiveSessionTrackingModes() {
      this.ensureNotProgramaticListener();
      return Collections.unmodifiableSet(this.sessionTrackingModes);
   }

   public void addListener(String className) {
      try {
         Class<? extends EventListener> clazz = this.getDeploymentInfo().getClassLoader().loadClass(className);
         this.addListener(clazz);
      } catch (ClassNotFoundException var3) {
         throw new IllegalArgumentException(var3);
      }
   }

   public <T extends EventListener> void addListener(T t) {
      this.ensureNotInitialized();
      this.ensureNotProgramaticListener();
      if (ApplicationListeners.listenerState() != ApplicationListeners.ListenerState.NO_LISTENER && ServletContextListener.class.isAssignableFrom(t.getClass())) {
         throw UndertowServletMessages.MESSAGES.cannotAddServletContextListener();
      } else {
         ListenerInfo listener = new ListenerInfo(t.getClass(), new ImmediateInstanceFactory(t));
         this.getDeploymentInfo().addListener(listener);
         this.deployment.getApplicationListeners().addListener(new ManagedListener(listener, true));
      }
   }

   public void addListener(Class<? extends EventListener> listenerClass) {
      this.ensureNotInitialized();
      this.ensureNotProgramaticListener();
      if (ApplicationListeners.listenerState() != ApplicationListeners.ListenerState.NO_LISTENER && ServletContextListener.class.isAssignableFrom(listenerClass)) {
         throw UndertowServletMessages.MESSAGES.cannotAddServletContextListener();
      } else {
         DeploymentInfo deploymentInfo = this.getDeploymentInfo();
         InstanceFactory<? extends EventListener> factory = null;

         try {
            factory = deploymentInfo.getClassIntrospecter().createInstanceFactory(listenerClass);
         } catch (Exception var5) {
            throw new IllegalArgumentException(var5);
         }

         ListenerInfo listener = new ListenerInfo(listenerClass, factory);
         deploymentInfo.addListener(listener);
         this.deployment.getApplicationListeners().addListener(new ManagedListener(listener, true));
      }
   }

   public <T extends EventListener> T createListener(Class<T> clazz) throws ServletException {
      this.ensureNotProgramaticListener();
      if (!ApplicationListeners.isListenerClass(clazz)) {
         throw UndertowServletMessages.MESSAGES.listenerMustImplementListenerClass(clazz);
      } else {
         try {
            return (EventListener)this.getDeploymentInfo().getClassIntrospecter().createInstanceFactory(clazz).createInstance().getInstance();
         } catch (Exception var3) {
            throw UndertowServletMessages.MESSAGES.couldNotInstantiateComponent(clazz.getName(), var3);
         }
      }
   }

   public JspConfigDescriptor getJspConfigDescriptor() {
      return this.getDeploymentInfo().getJspConfigDescriptor();
   }

   public ClassLoader getClassLoader() {
      return this.getDeploymentInfo().getClassLoader();
   }

   public void declareRoles(String... roleNames) {
      DeploymentInfo di = this.getDeploymentInfo();
      if (this.isInitialized()) {
         throw UndertowServletMessages.MESSAGES.servletAlreadyInitialize(di.getDeploymentName(), di.getContextPath());
      } else {
         String[] var3 = roleNames;
         int var4 = roleNames.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            String role = var3[var5];
            if (role == null || role.isEmpty()) {
               throw UndertowServletMessages.MESSAGES.roleMustNotBeEmpty(di.getDeploymentName(), di.getContextPath());
            }
         }

         if (ApplicationListeners.listenerState() == ApplicationListeners.ListenerState.PROGRAMATIC_LISTENER) {
            throw UndertowServletMessages.MESSAGES.cantCallFromDynamicListener(di.getDeploymentName(), di.getContextPath());
         } else {
            this.deploymentInfo.addSecurityRoles(roleNames);
         }
      }
   }

   public ServletRegistration.Dynamic addJspFile(String servletName, String jspFile) {
      if (servletName != null && !servletName.isEmpty()) {
         return this.addServlet(servletName, "org.apache.jasper.servlet.JspServlet", Collections.singletonList((handler) -> {
            return (exchange) -> {
               ServletRequest request = ((ServletRequestContext)exchange.getAttachment(ServletRequestContext.ATTACHMENT_KEY)).getServletRequest();
               request.setAttribute(System.getProperty("org.apache.jasper.Constants.JSP_FILE", "org.apache.catalina.jsp_file"), jspFile);
               handler.handleRequest(exchange);
            };
         }));
      } else {
         throw UndertowServletMessages.MESSAGES.paramCannotBeNull("servletName");
      }
   }

   public int getSessionTimeout() {
      return this.defaultSessionTimeout;
   }

   public void setSessionTimeout(int sessionTimeout) {
      this.ensureNotInitialized();
      this.ensureNotProgramaticListener();
      this.defaultSessionTimeout = sessionTimeout;
      this.deployment.getSessionManager().setDefaultSessionTimeout(sessionTimeout * 60);
   }

   public String getRequestCharacterEncoding() {
      DeploymentInfo deploymentInfo = this.getDeploymentInfo();
      String enconding = deploymentInfo.getDefaultRequestEncoding();
      return enconding != null ? enconding : deploymentInfo.getDefaultEncoding();
   }

   public void setRequestCharacterEncoding(String encoding) {
      this.ensureNotInitialized();
      this.ensureNotProgramaticListener();
      this.getDeploymentInfo().setDefaultRequestEncoding(encoding);
   }

   public String getResponseCharacterEncoding() {
      DeploymentInfo deploymentInfo = this.getDeploymentInfo();
      String enconding = deploymentInfo.getDefaultResponseEncoding();
      return enconding != null ? enconding : deploymentInfo.getDefaultEncoding();
   }

   public void setResponseCharacterEncoding(String encoding) {
      this.ensureNotInitialized();
      this.ensureNotProgramaticListener();
      this.getDeploymentInfo().setDefaultResponseEncoding(encoding);
   }

   public String getVirtualServerName() {
      return this.deployment.getDeploymentInfo().getHostName();
   }

   public HttpSessionImpl getSession(String sessionId) {
      SessionManager sessionManager = this.deployment.getSessionManager();
      Session session = sessionManager.getSession(sessionId);
      return session != null ? SecurityActions.forSession(session, this, false) : null;
   }

   public HttpSessionImpl getSession(ServletContextImpl originalServletContext, HttpServerExchange exchange, boolean create) {
      SessionConfig c = originalServletContext.getSessionConfig();
      HttpSessionImpl httpSession = (HttpSessionImpl)exchange.getAttachment(this.sessionAttachmentKey);
      if (httpSession != null && httpSession.isInvalid()) {
         exchange.removeAttachment(this.sessionAttachmentKey);
         httpSession = null;
      }

      if (httpSession == null) {
         SessionManager sessionManager = this.deployment.getSessionManager();
         Session session = sessionManager.getSession(exchange, c);
         if (session != null) {
            httpSession = SecurityActions.forSession(session, this, false);
            exchange.putAttachment(this.sessionAttachmentKey, httpSession);
         } else if (create) {
            String existing = c.findSessionId(exchange);
            Boolean isRequestedSessionIdSaved = (Boolean)exchange.getAttachment(HttpServletRequestImpl.REQUESTED_SESSION_ID_SET);
            if (isRequestedSessionIdSaved == null || !isRequestedSessionIdSaved) {
               exchange.putAttachment(HttpServletRequestImpl.REQUESTED_SESSION_ID_SET, Boolean.TRUE);
               exchange.putAttachment(HttpServletRequestImpl.REQUESTED_SESSION_ID, existing);
            }

            if (originalServletContext != this) {
               final HttpSessionImpl topLevel = originalServletContext.getSession(originalServletContext, exchange, true);
               c = new SessionConfig() {
                  public void setSessionId(HttpServerExchange exchange, String sessionId) {
                  }

                  public void clearSession(HttpServerExchange exchange, String sessionId) {
                  }

                  public String findSessionId(HttpServerExchange exchange) {
                     return topLevel.getId();
                  }

                  public SessionConfig.SessionCookieSource sessionCookieSource(HttpServerExchange exchange) {
                     return SessionConfig.SessionCookieSource.NONE;
                  }

                  public String rewriteUrl(String originalUrl, String sessionId) {
                     return null;
                  }
               };
               session = sessionManager.getSession(exchange, c);
               if (session != null) {
                  httpSession = SecurityActions.forSession(session, this, false);
                  exchange.putAttachment(this.sessionAttachmentKey, httpSession);
               }
            } else if (existing != null) {
               if (!this.getDeploymentInfo().isCheckOtherSessionManagers()) {
                  c.clearSession(exchange, existing);
               } else {
                  boolean found = false;
                  Iterator var11 = this.deployment.getServletContainer().listDeployments().iterator();

                  while(var11.hasNext()) {
                     String deploymentName = (String)var11.next();
                     DeploymentManager deployment = this.deployment.getServletContainer().getDeployment(deploymentName);
                     if (deployment != null && deployment.getDeployment().getSessionManager().getSession(existing) != null) {
                        found = true;
                        break;
                     }
                  }

                  if (!found) {
                     c.clearSession(exchange, existing);
                  }
               }
            }

            if (httpSession == null) {
               Session newSession = sessionManager.createSession(exchange, c);
               httpSession = SecurityActions.forSession(newSession, this, true);
               exchange.putAttachment(this.sessionAttachmentKey, httpSession);
            }
         }
      }

      return httpSession;
   }

   public HttpSessionImpl getSession(HttpServerExchange exchange, boolean create) {
      return this.getSession(this, exchange, create);
   }

   public void updateSessionAccessTime(HttpServerExchange exchange) {
      HttpSessionImpl httpSession = this.getSession(exchange, false);
      if (httpSession != null) {
         Session underlyingSession;
         if (System.getSecurityManager() == null) {
            underlyingSession = httpSession.getSession();
         } else {
            underlyingSession = (Session)AccessController.doPrivileged(new HttpSessionImpl.UnwrapSessionAction(httpSession));
         }

         underlyingSession.requestDone(exchange);
      }

   }

   public Deployment getDeployment() {
      return this.deployment;
   }

   private void ensureNotInitialized() {
      if (this.initialized) {
         throw UndertowServletMessages.MESSAGES.servletContextAlreadyInitialized();
      }
   }

   private void ensureNotProgramaticListener() {
      if (ApplicationListeners.listenerState() == ApplicationListeners.ListenerState.PROGRAMATIC_LISTENER) {
         throw UndertowServletMessages.MESSAGES.cannotCallFromProgramaticListener();
      }
   }

   boolean isInitialized() {
      return this.initialized;
   }

   public SessionConfig getSessionConfig() {
      return this.sessionConfig;
   }

   public void destroy() {
      this.attributes.clear();
      this.deploymentInfo = null;
   }

   private void readServletAnnotations(ServletInfo servlet, DeploymentInfo deploymentInfo) {
      if (System.getSecurityManager() == null) {
         (new ReadServletAnnotationsTask(servlet, deploymentInfo)).run();
      } else {
         AccessController.doPrivileged(new ReadServletAnnotationsTask(servlet, deploymentInfo));
      }

   }

   public void setDefaultSessionTrackingModes(HashSet<SessionTrackingMode> sessionTrackingModes) {
      this.defaultSessionTrackingModes = sessionTrackingModes;
      this.sessionTrackingModes = sessionTrackingModes;
   }

   void invokeOnWritePossible(HttpServerExchange exchange, WriteListener listener) {
      try {
         this.onWritePossibleTask.call(exchange, listener);
      } catch (Exception var4) {
         throw new RuntimeException(var4);
      }
   }

   void invokeOnAllDataRead(HttpServerExchange exchange, ReadListener listener) {
      try {
         this.onAllDataReadTask.call(exchange, listener);
      } catch (Exception var4) {
         throw new RuntimeException(var4);
      }
   }

   void invokeOnDataAvailable(HttpServerExchange exchange, ReadListener listener) {
      try {
         this.onDataAvailableTask.call(exchange, listener);
      } catch (Exception var4) {
         throw new RuntimeException(var4);
      }
   }

   void invokeAction(HttpServerExchange exchange, ThreadSetupHandler.Action<Void, Object> listener) {
      try {
         this.invokeActionTask.call(exchange, listener);
      } catch (Exception var4) {
         throw new RuntimeException(var4);
      }
   }

   void invokeRunnable(HttpServerExchange exchange, Runnable runnable) {
      boolean setupRequired = SecurityActions.currentServletRequestContext() == null;
      if (setupRequired) {
         try {
            this.runnableTask.call(exchange, runnable);
         } catch (Exception var5) {
            throw new RuntimeException(var5);
         }
      } else {
         runnable.run();
      }

   }

   void addMappingForServletNames(FilterInfo filterInfo, EnumSet<DispatcherType> dispatcherTypes, boolean isMatchAfter, String... servletNames) {
      DeploymentInfo deploymentInfo = this.deployment.getDeploymentInfo();
      String[] var6 = servletNames;
      int var7 = servletNames.length;

      for(int var8 = 0; var8 < var7; ++var8) {
         String servlet = var6[var8];
         Iterator var10;
         DispatcherType dispatcher;
         if (isMatchAfter) {
            if (dispatcherTypes != null && !dispatcherTypes.isEmpty()) {
               var10 = dispatcherTypes.iterator();

               while(var10.hasNext()) {
                  dispatcher = (DispatcherType)var10.next();
                  deploymentInfo.addFilterServletNameMapping(filterInfo.getName(), servlet, dispatcher);
               }
            } else {
               deploymentInfo.addFilterServletNameMapping(filterInfo.getName(), servlet, DispatcherType.REQUEST);
            }
         } else if (dispatcherTypes != null && !dispatcherTypes.isEmpty()) {
            var10 = dispatcherTypes.iterator();

            while(var10.hasNext()) {
               dispatcher = (DispatcherType)var10.next();
               deploymentInfo.insertFilterServletNameMapping(this.filterMappingServletNameInsertPosition++, filterInfo.getName(), servlet, dispatcher);
            }
         } else {
            deploymentInfo.insertFilterServletNameMapping(this.filterMappingServletNameInsertPosition++, filterInfo.getName(), servlet, DispatcherType.REQUEST);
         }
      }

      this.deployment.getServletPaths().invalidate();
   }

   void addMappingForUrlPatterns(FilterInfo filterInfo, EnumSet<DispatcherType> dispatcherTypes, boolean isMatchAfter, String... urlPatterns) {
      DeploymentInfo deploymentInfo = this.deployment.getDeploymentInfo();
      String[] var6 = urlPatterns;
      int var7 = urlPatterns.length;

      for(int var8 = 0; var8 < var7; ++var8) {
         String url = var6[var8];
         Iterator var10;
         DispatcherType dispatcher;
         if (isMatchAfter) {
            if (dispatcherTypes != null && !dispatcherTypes.isEmpty()) {
               var10 = dispatcherTypes.iterator();

               while(var10.hasNext()) {
                  dispatcher = (DispatcherType)var10.next();
                  deploymentInfo.addFilterUrlMapping(filterInfo.getName(), url, dispatcher);
               }
            } else {
               deploymentInfo.addFilterUrlMapping(filterInfo.getName(), url, DispatcherType.REQUEST);
            }
         } else if (dispatcherTypes != null && !dispatcherTypes.isEmpty()) {
            var10 = dispatcherTypes.iterator();

            while(var10.hasNext()) {
               dispatcher = (DispatcherType)var10.next();
               deploymentInfo.insertFilterUrlMapping(this.filterMappingUrlPatternInsertPosition++, filterInfo.getName(), url, dispatcher);
            }
         } else {
            deploymentInfo.insertFilterUrlMapping(this.filterMappingUrlPatternInsertPosition++, filterInfo.getName(), url, DispatcherType.REQUEST);
         }
      }

      this.deployment.getServletPaths().invalidate();
   }

   ContentTypeInfo parseContentType(String type) {
      ContentTypeInfo existing = (ContentTypeInfo)this.contentTypeCache.get(type);
      if (existing != null) {
         return existing;
      } else {
         String contentType = type;
         String charset = null;
         int split = type.indexOf(";");
         if (split != -1) {
            int pos = type.indexOf("charset=");
            int i;
            if (pos != -1) {
               i = pos + "charset=".length();

               int charsetStart;
               do {
                  charsetStart = type.charAt(i);
                  if (charsetStart == 32 || charsetStart == 9 || charsetStart == 59) {
                     break;
                  }

                  ++i;
               } while(i < type.length());

               charset = type.substring(pos + "charset=".length(), i);
               if (charset.startsWith("\"") && charset.endsWith("\"") && charset.length() > 1) {
                  charset = charset.substring(1, charset.length() - 1);
               }

               charsetStart = pos;

               do {
                  --charsetStart;
               } while(type.charAt(charsetStart) != ';' && charsetStart > 0);

               StringBuilder contentTypeBuilder = new StringBuilder();
               contentTypeBuilder.append(type.substring(0, charsetStart));
               if (i != type.length()) {
                  contentTypeBuilder.append(type.substring(i));
               }

               contentType = contentTypeBuilder.toString();
            }

            for(i = contentType.length() - 1; i >= 0; --i) {
               char c = contentType.charAt(i);
               if (c != ' ' && c != '\t') {
                  if (c == ';') {
                     contentType = contentType.substring(0, i);
                  }
                  break;
               }
            }
         }

         if (charset == null) {
            existing = new ContentTypeInfo(contentType, (String)null, contentType);
         } else {
            existing = new ContentTypeInfo(contentType + ";charset=" + charset, charset, contentType);
         }

         this.contentTypeCache.add(type, existing);
         return existing;
      }
   }

   static final class ServletContextSessionConfig implements SessionConfig {
      private final AttachmentKey<String> INVALIDATED;
      private final SessionConfig delegate;

      private ServletContextSessionConfig(SessionConfig delegate) {
         this.INVALIDATED = AttachmentKey.create(String.class);
         this.delegate = delegate;
      }

      public void setSessionId(HttpServerExchange exchange, String sessionId) {
         this.delegate.setSessionId(exchange, sessionId);
      }

      public void clearSession(HttpServerExchange exchange, String sessionId) {
         exchange.putAttachment(this.INVALIDATED, sessionId);
         Boolean isRequestedSessionIdSaved = (Boolean)exchange.getAttachment(HttpServletRequestImpl.REQUESTED_SESSION_ID_SET);
         if (isRequestedSessionIdSaved == null || !isRequestedSessionIdSaved) {
            exchange.putAttachment(HttpServletRequestImpl.REQUESTED_SESSION_ID_SET, Boolean.TRUE);
            exchange.putAttachment(HttpServletRequestImpl.REQUESTED_SESSION_ID, sessionId);
         }

         this.delegate.clearSession(exchange, sessionId);
      }

      public String findSessionId(HttpServerExchange exchange) {
         String invalidated = (String)exchange.getAttachment(this.INVALIDATED);
         ServletRequestContext src = (ServletRequestContext)exchange.getAttachment(ServletRequestContext.ATTACHMENT_KEY);
         String current;
         if (src.getOverridenSessionId() == null) {
            current = this.delegate.findSessionId(exchange);
         } else {
            current = src.getOverridenSessionId();
         }

         if (invalidated == null) {
            return current;
         } else {
            return invalidated.equals(current) ? null : current;
         }
      }

      public SessionConfig.SessionCookieSource sessionCookieSource(HttpServerExchange exchange) {
         return this.delegate.sessionCookieSource(exchange);
      }

      public String rewriteUrl(String originalUrl, String sessionId) {
         return this.delegate.rewriteUrl(originalUrl, sessionId);
      }

      public SessionConfig getDelegate() {
         return this.delegate;
      }

      // $FF: synthetic method
      ServletContextSessionConfig(SessionConfig x0, Object x1) {
         this(x0);
      }
   }

   private static final class ReadServletAnnotationsTask implements PrivilegedAction<Void> {
      private final ServletInfo servletInfo;
      private final DeploymentInfo deploymentInfo;

      private ReadServletAnnotationsTask(ServletInfo servletInfo, DeploymentInfo deploymentInfo) {
         this.servletInfo = servletInfo;
         this.deploymentInfo = deploymentInfo;
      }

      public Void run() {
         ServletSecurity security = (ServletSecurity)this.servletInfo.getServletClass().getAnnotation(ServletSecurity.class);
         if (security != null) {
            ServletSecurityInfo servletSecurityInfo = (ServletSecurityInfo)((ServletSecurityInfo)((ServletSecurityInfo)(new ServletSecurityInfo()).setEmptyRoleSemantic(security.value().value() == ServletSecurity.EmptyRoleSemantic.DENY ? SecurityInfo.EmptyRoleSemantic.DENY : SecurityInfo.EmptyRoleSemantic.PERMIT)).setTransportGuaranteeType(security.value().transportGuarantee() == ServletSecurity.TransportGuarantee.CONFIDENTIAL ? TransportGuaranteeType.CONFIDENTIAL : TransportGuaranteeType.NONE)).addRolesAllowed(security.value().rolesAllowed());
            HttpMethodConstraint[] var3 = security.httpMethodConstraints();
            int var4 = var3.length;

            for(int var5 = 0; var5 < var4; ++var5) {
               HttpMethodConstraint constraint = var3[var5];
               ((ServletSecurityInfo)((ServletSecurityInfo)servletSecurityInfo.addHttpMethodSecurityInfo((new HttpMethodSecurityInfo()).setMethod(constraint.value())).setEmptyRoleSemantic(constraint.emptyRoleSemantic() == ServletSecurity.EmptyRoleSemantic.DENY ? SecurityInfo.EmptyRoleSemantic.DENY : SecurityInfo.EmptyRoleSemantic.PERMIT)).setTransportGuaranteeType(constraint.transportGuarantee() == ServletSecurity.TransportGuarantee.CONFIDENTIAL ? TransportGuaranteeType.CONFIDENTIAL : TransportGuaranteeType.NONE)).addRolesAllowed(constraint.rolesAllowed());
            }

            this.servletInfo.setServletSecurityInfo(servletSecurityInfo);
         }

         MultipartConfig multipartConfig = (MultipartConfig)this.servletInfo.getServletClass().getAnnotation(MultipartConfig.class);
         if (multipartConfig != null) {
            this.servletInfo.setMultipartConfig(new MultipartConfigElement(multipartConfig.location(), multipartConfig.maxFileSize(), multipartConfig.maxRequestSize(), multipartConfig.fileSizeThreshold()));
         }

         RunAs runAs = (RunAs)this.servletInfo.getServletClass().getAnnotation(RunAs.class);
         if (runAs != null) {
            this.servletInfo.setRunAs(runAs.value());
         }

         DeclareRoles declareRoles = (DeclareRoles)this.servletInfo.getServletClass().getAnnotation(DeclareRoles.class);
         if (declareRoles != null) {
            this.deploymentInfo.addSecurityRoles(declareRoles.value());
         }

         return null;
      }

      // $FF: synthetic method
      ReadServletAnnotationsTask(ServletInfo x0, DeploymentInfo x1, Object x2) {
         this(x0, x1);
      }
   }
}
