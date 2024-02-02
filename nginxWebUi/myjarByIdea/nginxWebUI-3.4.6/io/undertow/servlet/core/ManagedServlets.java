package io.undertow.servlet.core;

import io.undertow.servlet.api.ServletInfo;
import io.undertow.servlet.handlers.ServletHandler;
import io.undertow.servlet.handlers.ServletPathMatches;
import io.undertow.util.CopyOnWriteMap;
import java.util.HashMap;
import java.util.Map;

public class ManagedServlets {
   private final Map<String, ServletHandler> managedServletMap = new CopyOnWriteMap();
   private final DeploymentImpl deployment;
   private final ServletPathMatches servletPaths;

   public ManagedServlets(DeploymentImpl deployment, ServletPathMatches servletPaths) {
      this.deployment = deployment;
      this.servletPaths = servletPaths;
   }

   public ServletHandler addServlet(ServletInfo servletInfo) {
      ManagedServlet managedServlet = new ManagedServlet(servletInfo, this.deployment.getServletContext());
      ServletHandler servletHandler = new ServletHandler(managedServlet);
      this.managedServletMap.put(servletInfo.getName(), servletHandler);
      this.deployment.addLifecycleObjects(managedServlet);
      this.servletPaths.invalidate();
      return servletHandler;
   }

   public ManagedServlet getManagedServlet(String name) {
      ServletHandler servletHandler = (ServletHandler)this.managedServletMap.get(name);
      return servletHandler == null ? null : servletHandler.getManagedServlet();
   }

   public ServletHandler getServletHandler(String name) {
      return (ServletHandler)this.managedServletMap.get(name);
   }

   public Map<String, ServletHandler> getServletHandlers() {
      return new HashMap(this.managedServletMap);
   }
}
