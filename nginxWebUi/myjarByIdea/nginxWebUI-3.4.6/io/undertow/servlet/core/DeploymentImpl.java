package io.undertow.servlet.core;

import io.undertow.security.api.AuthenticationMechanism;
import io.undertow.server.HttpHandler;
import io.undertow.server.session.SessionManager;
import io.undertow.servlet.api.Deployment;
import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.api.DeploymentManager;
import io.undertow.servlet.api.ServletContainer;
import io.undertow.servlet.api.ServletDispatcher;
import io.undertow.servlet.api.ServletInfo;
import io.undertow.servlet.api.ThreadSetupHandler;
import io.undertow.servlet.handlers.ServletInitialHandler;
import io.undertow.servlet.handlers.ServletPathMatches;
import io.undertow.servlet.spec.ServletContextImpl;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;

public class DeploymentImpl implements Deployment {
   private final DeploymentManager deploymentManager;
   private final DeploymentInfo deploymentInfo;
   private final ServletContainer servletContainer;
   private final List<Lifecycle> lifecycleObjects = new ArrayList();
   private final ServletPathMatches servletPaths;
   private final ManagedServlets servlets;
   private final ManagedFilters filters;
   private volatile ApplicationListeners applicationListeners;
   private volatile ServletContextImpl servletContext;
   private volatile ServletInitialHandler servletHandler;
   private volatile HttpHandler initialHandler;
   private volatile ErrorPages errorPages;
   private volatile Map<String, String> mimeExtensionMappings;
   private volatile SessionManager sessionManager;
   /** @deprecated */
   @Deprecated
   private volatile Charset defaultCharset;
   private volatile Charset defaultRequestCharset;
   private volatile Charset defaultResponseCharset;
   private volatile List<AuthenticationMechanism> authenticationMechanisms;
   private volatile List<ThreadSetupHandler> threadSetupActions;
   private Set<String> existingUrlPatterns;

   public DeploymentImpl(DeploymentManager deploymentManager, DeploymentInfo deploymentInfo, ServletContainer servletContainer) {
      this.defaultCharset = StandardCharsets.ISO_8859_1;
      this.defaultRequestCharset = StandardCharsets.ISO_8859_1;
      this.defaultResponseCharset = StandardCharsets.ISO_8859_1;
      this.deploymentManager = deploymentManager;
      this.deploymentInfo = deploymentInfo;
      this.servletContainer = servletContainer;
      this.servletPaths = new ServletPathMatches(this);
      this.servlets = new ManagedServlets(this, this.servletPaths);
      this.filters = new ManagedFilters(this, this.servletPaths);
   }

   public ServletContainer getServletContainer() {
      return this.servletContainer;
   }

   public ManagedServlets getServlets() {
      return this.servlets;
   }

   public ManagedFilters getFilters() {
      return this.filters;
   }

   void setApplicationListeners(ApplicationListeners applicationListeners) {
      this.applicationListeners = applicationListeners;
   }

   void setServletContext(ServletContextImpl servletContext) {
      this.servletContext = servletContext;
   }

   public DeploymentInfo getDeploymentInfo() {
      return this.deploymentInfo;
   }

   public ApplicationListeners getApplicationListeners() {
      return this.applicationListeners;
   }

   public ServletContextImpl getServletContext() {
      return this.servletContext;
   }

   public HttpHandler getHandler() {
      return this.initialHandler;
   }

   public void setInitialHandler(HttpHandler initialHandler) {
      this.initialHandler = initialHandler;
   }

   void setServletHandler(ServletInitialHandler servletHandler) {
      this.servletHandler = servletHandler;
   }

   void addLifecycleObjects(Collection<Lifecycle> objects) {
      this.lifecycleObjects.addAll(objects);
   }

   void addLifecycleObjects(Lifecycle... objects) {
      this.lifecycleObjects.addAll(Arrays.asList(objects));
   }

   void setSessionManager(SessionManager sessionManager) {
      this.sessionManager = sessionManager;
   }

   public List<Lifecycle> getLifecycleObjects() {
      return Collections.unmodifiableList(this.lifecycleObjects);
   }

   public ServletPathMatches getServletPaths() {
      return this.servletPaths;
   }

   void setThreadSetupActions(List<ThreadSetupHandler> threadSetupActions) {
      this.threadSetupActions = threadSetupActions;
   }

   public <C, T> ThreadSetupHandler.Action<C, T> createThreadSetupAction(ThreadSetupHandler.Action<C, T> target) {
      ThreadSetupHandler.Action<C, T> ret = target;

      ThreadSetupHandler wrapper;
      for(Iterator var3 = this.threadSetupActions.iterator(); var3.hasNext(); ret = wrapper.create(ret)) {
         wrapper = (ThreadSetupHandler)var3.next();
      }

      return ret;
   }

   public ErrorPages getErrorPages() {
      return this.errorPages;
   }

   public void setErrorPages(ErrorPages errorPages) {
      this.errorPages = errorPages;
   }

   public Map<String, String> getMimeExtensionMappings() {
      return this.mimeExtensionMappings;
   }

   public void setMimeExtensionMappings(Map<String, String> mimeExtensionMappings) {
      this.mimeExtensionMappings = Collections.unmodifiableMap(new HashMap(mimeExtensionMappings));
   }

   public ServletDispatcher getServletDispatcher() {
      return this.servletHandler;
   }

   public SessionManager getSessionManager() {
      return this.sessionManager;
   }

   public Executor getExecutor() {
      return this.deploymentInfo.getExecutor();
   }

   public Executor getAsyncExecutor() {
      return this.deploymentInfo.getAsyncExecutor();
   }

   /** @deprecated */
   @Deprecated
   public Charset getDefaultCharset() {
      return this.defaultCharset;
   }

   public Charset getDefaultRequestCharset() {
      return this.defaultRequestCharset;
   }

   public Charset getDefaultResponseCharset() {
      return this.defaultResponseCharset;
   }

   public void setAuthenticationMechanisms(List<AuthenticationMechanism> authenticationMechanisms) {
      this.authenticationMechanisms = authenticationMechanisms;
   }

   public List<AuthenticationMechanism> getAuthenticationMechanisms() {
      return this.authenticationMechanisms;
   }

   public DeploymentManager.State getDeploymentState() {
      return this.deploymentManager.getState();
   }

   public Set<String> tryAddServletMappings(ServletInfo servletInfo, String... urlPatterns) {
      Set<String> ret = new HashSet();
      if (this.existingUrlPatterns == null) {
         this.existingUrlPatterns = new HashSet();
         Iterator var4 = this.deploymentInfo.getServlets().values().iterator();

         while(var4.hasNext()) {
            ServletInfo s = (ServletInfo)var4.next();
            if (!s.getName().equals(servletInfo.getName())) {
               this.existingUrlPatterns.addAll(s.getMappings());
            }
         }
      }

      String[] var8 = urlPatterns;
      int var9 = urlPatterns.length;

      int var6;
      String pattern;
      for(var6 = 0; var6 < var9; ++var6) {
         pattern = var8[var6];
         if (this.existingUrlPatterns.contains(pattern)) {
            ret.add(pattern);
         }
      }

      if (ret.isEmpty()) {
         var8 = urlPatterns;
         var9 = urlPatterns.length;

         for(var6 = 0; var6 < var9; ++var6) {
            pattern = var8[var6];
            this.existingUrlPatterns.add(pattern);
            if (!servletInfo.getMappings().contains(pattern)) {
               servletInfo.addMapping(pattern);
            }
         }
      }

      this.getServletPaths().invalidate();
      return ret;
   }

   /** @deprecated */
   @Deprecated
   public void setDefaultCharset(Charset defaultCharset) {
      this.defaultCharset = defaultCharset;
   }

   public void setDefaultRequestCharset(Charset defaultRequestCharset) {
      this.defaultRequestCharset = defaultRequestCharset;
   }

   public void setDefaultResponseCharset(Charset defaultResponseCharset) {
      this.defaultResponseCharset = defaultResponseCharset;
   }

   void destroy() {
      this.getApplicationListeners().contextDestroyed();
      this.getApplicationListeners().stop();
      if (this.servletContext != null) {
         this.servletContext.destroy();
      }

      this.servletContext = null;
   }
}
