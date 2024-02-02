package io.undertow.servlet.core;

import io.undertow.servlet.UndertowServletLogger;
import io.undertow.servlet.UndertowServletMessages;
import io.undertow.servlet.api.DeploymentManager;
import io.undertow.servlet.api.FilterInfo;
import io.undertow.servlet.api.InstanceHandle;
import io.undertow.servlet.spec.FilterConfigImpl;
import io.undertow.servlet.spec.ServletContextImpl;
import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public class ManagedFilter implements Lifecycle {
   private final FilterInfo filterInfo;
   private final ServletContextImpl servletContext;
   private volatile boolean started = false;
   private volatile Filter filter;
   private volatile InstanceHandle<? extends Filter> handle;

   public ManagedFilter(FilterInfo filterInfo, ServletContextImpl servletContext) {
      this.filterInfo = filterInfo;
      this.servletContext = servletContext;
   }

   public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
      if (this.servletContext.getDeployment().getDeploymentState() != DeploymentManager.State.STARTED) {
         throw UndertowServletMessages.MESSAGES.deploymentStopped(this.servletContext.getDeployment().getDeploymentInfo().getDeploymentName());
      } else {
         if (!this.started) {
            this.start();
         }

         this.getFilter().doFilter(request, response, chain);
      }
   }

   private Filter getFilter() throws ServletException {
      if (this.filter == null) {
         this.createFilter();
      }

      return this.filter;
   }

   public void createFilter() throws ServletException {
      synchronized(this) {
         if (this.filter == null) {
            try {
               this.handle = this.filterInfo.getInstanceFactory().createInstance();
            } catch (Exception var4) {
               throw UndertowServletMessages.MESSAGES.couldNotInstantiateComponent(this.filterInfo.getName(), var4);
            }

            Filter filter = (Filter)this.handle.getInstance();
            (new LifecyleInterceptorInvocation(this.servletContext.getDeployment().getDeploymentInfo().getLifecycleInterceptors(), this.filterInfo, filter, new FilterConfigImpl(this.filterInfo, this.servletContext))).proceed();
            this.filter = filter;
         }

      }
   }

   public synchronized void start() throws ServletException {
      if (!this.started) {
         this.started = true;
      }

   }

   public synchronized void stop() {
      this.started = false;
      if (this.handle != null) {
         try {
            (new LifecyleInterceptorInvocation(this.servletContext.getDeployment().getDeploymentInfo().getLifecycleInterceptors(), this.filterInfo, this.filter)).proceed();
         } catch (Exception var2) {
            UndertowServletLogger.ROOT_LOGGER.failedToDestroy(this.filterInfo, var2);
         }

         this.handle.release();
      }

      this.filter = null;
      this.handle = null;
   }

   public boolean isStarted() {
      return this.started;
   }

   public FilterInfo getFilterInfo() {
      return this.filterInfo;
   }

   public String toString() {
      return "ManagedFilter{filterInfo=" + this.filterInfo + '}';
   }

   public void forceInit() throws ServletException {
      if (this.filter == null) {
         this.createFilter();
      }

   }
}
