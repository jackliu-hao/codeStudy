package io.undertow.servlet.spec;

import io.undertow.servlet.api.Deployment;
import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.api.FilterInfo;
import io.undertow.servlet.api.FilterMappingInfo;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;

public class FilterRegistrationImpl implements FilterRegistration, FilterRegistration.Dynamic {
   private final FilterInfo filterInfo;
   private final Deployment deployment;
   private final ServletContextImpl servletContext;

   public FilterRegistrationImpl(FilterInfo filterInfo, Deployment deployment, ServletContextImpl servletContext) {
      this.filterInfo = filterInfo;
      this.deployment = deployment;
      this.servletContext = servletContext;
   }

   public void addMappingForServletNames(EnumSet<DispatcherType> dispatcherTypes, boolean isMatchAfter, String... servletNames) {
      this.servletContext.addMappingForServletNames(this.filterInfo, dispatcherTypes, isMatchAfter, servletNames);
   }

   public Collection<String> getServletNameMappings() {
      DeploymentInfo deploymentInfo = this.deployment.getDeploymentInfo();
      List<String> ret = new ArrayList();
      Iterator var3 = deploymentInfo.getFilterMappings().iterator();

      while(var3.hasNext()) {
         FilterMappingInfo mapping = (FilterMappingInfo)var3.next();
         if (mapping.getMappingType() == FilterMappingInfo.MappingType.SERVLET && mapping.getFilterName().equals(this.filterInfo.getName())) {
            ret.add(mapping.getMapping());
         }
      }

      return ret;
   }

   public void addMappingForUrlPatterns(EnumSet<DispatcherType> dispatcherTypes, boolean isMatchAfter, String... urlPatterns) {
      this.servletContext.addMappingForUrlPatterns(this.filterInfo, dispatcherTypes, isMatchAfter, urlPatterns);
   }

   public Collection<String> getUrlPatternMappings() {
      DeploymentInfo deploymentInfo = this.deployment.getDeploymentInfo();
      List<String> ret = new ArrayList();
      Iterator var3 = deploymentInfo.getFilterMappings().iterator();

      while(var3.hasNext()) {
         FilterMappingInfo mapping = (FilterMappingInfo)var3.next();
         if (mapping.getMappingType() == FilterMappingInfo.MappingType.URL && mapping.getFilterName().equals(this.filterInfo.getName())) {
            ret.add(mapping.getMapping());
         }
      }

      return ret;
   }

   public String getName() {
      return this.filterInfo.getName();
   }

   public String getClassName() {
      return this.filterInfo.getFilterClass().getName();
   }

   public boolean setInitParameter(String name, String value) {
      if (this.filterInfo.getInitParams().containsKey(name)) {
         return false;
      } else {
         this.filterInfo.addInitParam(name, value);
         return true;
      }
   }

   public String getInitParameter(String name) {
      return (String)this.filterInfo.getInitParams().get(name);
   }

   public Set<String> setInitParameters(Map<String, String> initParameters) {
      Set<String> ret = new HashSet();
      Iterator var3 = initParameters.entrySet().iterator();

      while(var3.hasNext()) {
         Map.Entry<String, String> entry = (Map.Entry)var3.next();
         if (!this.setInitParameter((String)entry.getKey(), (String)entry.getValue())) {
            ret.add(entry.getKey());
         }
      }

      return ret;
   }

   public Map<String, String> getInitParameters() {
      return this.filterInfo.getInitParams();
   }

   public void setAsyncSupported(boolean isAsyncSupported) {
      this.filterInfo.setAsyncSupported(isAsyncSupported);
   }
}
