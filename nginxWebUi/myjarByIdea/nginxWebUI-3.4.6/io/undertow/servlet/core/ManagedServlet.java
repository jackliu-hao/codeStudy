package io.undertow.servlet.core;

import io.undertow.server.handlers.form.FormEncodedDataDefinition;
import io.undertow.server.handlers.form.FormParserFactory;
import io.undertow.server.handlers.form.MultiPartParserDefinition;
import io.undertow.server.handlers.resource.ResourceChangeListener;
import io.undertow.server.handlers.resource.ResourceManager;
import io.undertow.servlet.UndertowServletLogger;
import io.undertow.servlet.UndertowServletMessages;
import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.api.DeploymentManager;
import io.undertow.servlet.api.InstanceFactory;
import io.undertow.servlet.api.InstanceHandle;
import io.undertow.servlet.api.LifecycleInterceptor;
import io.undertow.servlet.api.ServletInfo;
import io.undertow.servlet.spec.ServletConfigImpl;
import io.undertow.servlet.spec.ServletContextImpl;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicLongFieldUpdater;
import javax.servlet.MultipartConfigElement;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.SingleThreadModel;
import javax.servlet.UnavailableException;

public class ManagedServlet implements Lifecycle {
   private final ServletInfo servletInfo;
   private final ServletContextImpl servletContext;
   private volatile boolean started = false;
   private final InstanceStrategy instanceStrategy;
   private volatile boolean permanentlyUnavailable = false;
   private long maxMultipartRequestSize;
   private FormParserFactory formParserFactory;
   private MultipartConfigElement multipartConfig;
   private static final AtomicLongFieldUpdater<ManagedServlet> unavailableUntilUpdater = AtomicLongFieldUpdater.newUpdater(ManagedServlet.class, "unavailableUntil");
   private volatile long unavailableUntil = 0L;

   public ManagedServlet(ServletInfo servletInfo, ServletContextImpl servletContext) {
      this.servletInfo = servletInfo;
      this.servletContext = servletContext;
      if (SingleThreadModel.class.isAssignableFrom(servletInfo.getServletClass())) {
         this.instanceStrategy = new SingleThreadModelPoolStrategy(servletInfo.getInstanceFactory(), servletInfo, servletContext);
      } else {
         this.instanceStrategy = new DefaultInstanceStrategy(servletInfo.getInstanceFactory(), servletInfo, servletContext);
      }

      this.setupMultipart(servletContext);
   }

   public void setupMultipart(ServletContextImpl servletContext) {
      FormEncodedDataDefinition formDataParser = (new FormEncodedDataDefinition()).setDefaultEncoding(servletContext.getDeployment().getDefaultRequestCharset().name());
      MultipartConfigElement multipartConfig = this.servletInfo.getMultipartConfig();
      if (multipartConfig == null) {
         multipartConfig = servletContext.getDeployment().getDeploymentInfo().getDefaultMultipartConfig();
      }

      this.multipartConfig = multipartConfig;
      if (multipartConfig != null) {
         if (multipartConfig.getMaxRequestSize() != -1L) {
            this.maxMultipartRequestSize = multipartConfig.getMaxRequestSize();
         } else {
            this.maxMultipartRequestSize = -1L;
         }

         Path tempDir;
         if (multipartConfig.getLocation() != null && !multipartConfig.getLocation().isEmpty()) {
            String location = multipartConfig.getLocation();
            Path locFile = Paths.get(location);
            if (locFile.isAbsolute()) {
               tempDir = locFile;
            } else {
               DeploymentInfo deploymentInfo = servletContext.getDeployment().getDeploymentInfo();
               tempDir = deploymentInfo.requireTempPath().resolve(location);
            }
         } else {
            tempDir = servletContext.getDeployment().getDeploymentInfo().getTempPath();
         }

         MultiPartParserDefinition multiPartParserDefinition = new MultiPartParserDefinition(tempDir);
         if (multipartConfig.getMaxFileSize() > 0L) {
            multiPartParserDefinition.setMaxIndividualFileSize(multipartConfig.getMaxFileSize());
         }

         if (multipartConfig.getFileSizeThreshold() > 0) {
            multiPartParserDefinition.setFileSizeThreshold((long)multipartConfig.getFileSizeThreshold());
         }

         multiPartParserDefinition.setDefaultEncoding(servletContext.getDeployment().getDefaultRequestCharset().name());
         this.formParserFactory = FormParserFactory.builder(false).addParser(formDataParser).addParser(multiPartParserDefinition).build();
      } else {
         this.formParserFactory = FormParserFactory.builder(false).addParser(formDataParser).build();
         this.maxMultipartRequestSize = -1L;
      }

   }

   public synchronized void start() throws ServletException {
   }

   public void createServlet() throws ServletException {
      if (!this.permanentlyUnavailable) {
         try {
            if (!this.started && this.servletInfo.getLoadOnStartup() != null && this.servletInfo.getLoadOnStartup() >= 0) {
               this.instanceStrategy.start();
               this.started = true;
            }
         } catch (UnavailableException var2) {
            if (var2.isPermanent()) {
               this.permanentlyUnavailable = true;
               this.stop();
            }
         }

      }
   }

   public synchronized void stop() {
      if (this.started) {
         this.instanceStrategy.stop();
      }

      this.started = false;
   }

   public boolean isStarted() {
      return this.started;
   }

   public boolean isPermanentlyUnavailable() {
      return this.permanentlyUnavailable;
   }

   public boolean isTemporarilyUnavailable() {
      long until = this.unavailableUntil;
      if (until != 0L) {
         if (System.currentTimeMillis() < until) {
            return true;
         }

         unavailableUntilUpdater.compareAndSet(this, until, 0L);
      }

      return false;
   }

   public void setPermanentlyUnavailable(boolean permanentlyUnavailable) {
      this.permanentlyUnavailable = permanentlyUnavailable;
   }

   public InstanceHandle<? extends Servlet> getServlet() throws ServletException {
      if (this.servletContext.getDeployment().getDeploymentState() != DeploymentManager.State.STARTED) {
         throw UndertowServletMessages.MESSAGES.deploymentStopped(this.servletContext.getDeployment().getDeploymentInfo().getDeploymentName());
      } else {
         if (!this.started) {
            synchronized(this) {
               if (!this.started) {
                  this.instanceStrategy.start();
                  this.started = true;
               }
            }
         }

         return this.instanceStrategy.getServlet();
      }
   }

   public void forceInit() throws ServletException {
      if (!this.started) {
         if (this.servletContext.getDeployment().getDeploymentState() != DeploymentManager.State.STARTED) {
            throw UndertowServletMessages.MESSAGES.deploymentStopped(this.servletContext.getDeployment().getDeploymentInfo().getDeploymentName());
         }

         synchronized(this) {
            if (!this.started) {
               try {
                  this.instanceStrategy.start();
               } catch (UnavailableException var4) {
                  this.handleUnavailableException(var4);
               }

               this.started = true;
            }
         }
      }

   }

   public void handleUnavailableException(UnavailableException e) {
      if (e.isPermanent()) {
         UndertowServletLogger.REQUEST_LOGGER.stoppingServletDueToPermanentUnavailability(this.getServletInfo().getName(), e);
         this.stop();
         this.setPermanentlyUnavailable(true);
      } else {
         long until = System.currentTimeMillis() + (long)(e.getUnavailableSeconds() * 1000);
         unavailableUntilUpdater.set(this, until);
         UndertowServletLogger.REQUEST_LOGGER.stoppingServletUntilDueToTemporaryUnavailability(this.getServletInfo().getName(), new Date(until), e);
      }

   }

   public ServletInfo getServletInfo() {
      return this.servletInfo;
   }

   public long getMaxRequestSize() {
      return this.maxMultipartRequestSize;
   }

   public FormParserFactory getFormParserFactory() {
      return this.formParserFactory;
   }

   public MultipartConfigElement getMultipartConfig() {
      return this.multipartConfig;
   }

   public String toString() {
      return "ManagedServlet{servletInfo=" + this.servletInfo + '}';
   }

   private static class SingleThreadModelPoolStrategy implements InstanceStrategy {
      private final InstanceFactory<? extends Servlet> factory;
      private final ServletInfo servletInfo;
      private final ServletContextImpl servletContext;

      private SingleThreadModelPoolStrategy(InstanceFactory<? extends Servlet> factory, ServletInfo servletInfo, ServletContextImpl servletContext) {
         this.factory = factory;
         this.servletInfo = servletInfo;
         this.servletContext = servletContext;
      }

      public void start() throws ServletException {
         if (this.servletInfo.getLoadOnStartup() != null) {
            this.getServlet().release();
         }

      }

      public void stop() {
      }

      public InstanceHandle<? extends Servlet> getServlet() throws ServletException {
         final InstanceHandle instanceHandle;
         try {
            instanceHandle = this.factory.createInstance();
         } catch (Exception var4) {
            throw UndertowServletMessages.MESSAGES.couldNotInstantiateComponent(this.servletInfo.getName(), var4);
         }

         final Servlet instance = (Servlet)instanceHandle.getInstance();
         (new LifecyleInterceptorInvocation(this.servletContext.getDeployment().getDeploymentInfo().getLifecycleInterceptors(), this.servletInfo, instance, new ServletConfigImpl(this.servletInfo, this.servletContext))).proceed();
         return new InstanceHandle<Servlet>() {
            public Servlet getInstance() {
               return instance;
            }

            public void release() {
               try {
                  instance.destroy();
               } catch (Throwable var2) {
                  UndertowServletLogger.REQUEST_LOGGER.failedToDestroy(instance, var2);
               }

               instanceHandle.release();
            }
         };
      }

      // $FF: synthetic method
      SingleThreadModelPoolStrategy(InstanceFactory x0, ServletInfo x1, ServletContextImpl x2, Object x3) {
         this(x0, x1, x2);
      }
   }

   private static class DefaultInstanceStrategy implements InstanceStrategy {
      private final InstanceFactory<? extends Servlet> factory;
      private final ServletInfo servletInfo;
      private final ServletContextImpl servletContext;
      private volatile InstanceHandle<? extends Servlet> handle;
      private volatile Servlet instance;
      private ResourceChangeListener changeListener;
      private final InstanceHandle<Servlet> instanceHandle = new InstanceHandle<Servlet>() {
         public Servlet getInstance() {
            return DefaultInstanceStrategy.this.instance;
         }

         public void release() {
         }
      };

      DefaultInstanceStrategy(InstanceFactory<? extends Servlet> factory, ServletInfo servletInfo, ServletContextImpl servletContext) {
         this.factory = factory;
         this.servletInfo = servletInfo;
         this.servletContext = servletContext;
      }

      public synchronized void start() throws ServletException {
         try {
            this.handle = this.factory.createInstance();
         } catch (Exception var2) {
            throw UndertowServletMessages.MESSAGES.couldNotInstantiateComponent(this.servletInfo.getName(), var2);
         }

         this.instance = (Servlet)this.handle.getInstance();
         (new LifecyleInterceptorInvocation(this.servletContext.getDeployment().getDeploymentInfo().getLifecycleInterceptors(), this.servletInfo, this.instance, new ServletConfigImpl(this.servletInfo, this.servletContext))).proceed();
         ResourceManager resourceManager = this.servletContext.getDeployment().getDeploymentInfo().getResourceManager();
         if (this.instance instanceof ResourceChangeListener && resourceManager.isResourceChangeListenerSupported()) {
            resourceManager.registerResourceChangeListener(this.changeListener = (ResourceChangeListener)this.instance);
         }

      }

      public synchronized void stop() {
         if (this.handle != null) {
            ResourceManager resourceManager = this.servletContext.getDeployment().getDeploymentInfo().getResourceManager();
            if (this.changeListener != null) {
               resourceManager.removeResourceChangeListener(this.changeListener);
            }

            this.invokeDestroy();
            this.handle.release();
         }

      }

      private void invokeDestroy() {
         List<LifecycleInterceptor> interceptors = this.servletContext.getDeployment().getDeploymentInfo().getLifecycleInterceptors();

         try {
            (new LifecyleInterceptorInvocation(interceptors, this.servletInfo, this.instance)).proceed();
         } catch (Exception var3) {
            UndertowServletLogger.ROOT_LOGGER.failedToDestroy(this.servletInfo, var3);
         }

      }

      public InstanceHandle<? extends Servlet> getServlet() {
         return this.instanceHandle;
      }
   }

   interface InstanceStrategy {
      void start() throws ServletException;

      void stop();

      InstanceHandle<? extends Servlet> getServlet() throws ServletException;
   }
}
