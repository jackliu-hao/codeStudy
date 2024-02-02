package io.undertow.servlet.core;

import io.undertow.servlet.api.FilterInfo;
import io.undertow.servlet.handlers.ServletPathMatches;
import io.undertow.util.CopyOnWriteMap;
import java.util.HashMap;
import java.util.Map;

public class ManagedFilters {
   private final Map<String, ManagedFilter> managedFilterMap = new CopyOnWriteMap();
   private final DeploymentImpl deployment;
   private final ServletPathMatches servletPathMatches;

   public ManagedFilters(DeploymentImpl deployment, ServletPathMatches servletPathMatches) {
      this.deployment = deployment;
      this.servletPathMatches = servletPathMatches;
   }

   public ManagedFilter addFilter(FilterInfo filterInfo) {
      ManagedFilter managedFilter = new ManagedFilter(filterInfo, this.deployment.getServletContext());
      this.managedFilterMap.put(filterInfo.getName(), managedFilter);
      this.deployment.addLifecycleObjects(managedFilter);
      this.servletPathMatches.invalidate();
      return managedFilter;
   }

   public ManagedFilter getManagedFilter(String name) {
      return (ManagedFilter)this.managedFilterMap.get(name);
   }

   public Map<String, ManagedFilter> getFilters() {
      return new HashMap(this.managedFilterMap);
   }
}
