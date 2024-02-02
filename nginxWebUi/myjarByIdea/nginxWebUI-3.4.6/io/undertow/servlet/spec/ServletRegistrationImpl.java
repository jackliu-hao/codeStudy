package io.undertow.servlet.spec;

import io.undertow.UndertowMessages;
import io.undertow.servlet.api.Deployment;
import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.api.HttpMethodSecurityInfo;
import io.undertow.servlet.api.SecurityConstraint;
import io.undertow.servlet.api.SecurityInfo;
import io.undertow.servlet.api.ServletInfo;
import io.undertow.servlet.api.ServletSecurityInfo;
import io.undertow.servlet.api.TransportGuaranteeType;
import io.undertow.servlet.api.WebResourceCollection;
import io.undertow.servlet.core.ManagedServlet;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.servlet.HttpMethodConstraintElement;
import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletRegistration;
import javax.servlet.ServletSecurityElement;
import javax.servlet.annotation.ServletSecurity;

public class ServletRegistrationImpl implements ServletRegistration, ServletRegistration.Dynamic {
   private final ServletInfo servletInfo;
   private final ManagedServlet managedServlet;
   private final Deployment deployment;

   public ServletRegistrationImpl(ServletInfo servletInfo, ManagedServlet managedServlet, Deployment deployment) {
      this.servletInfo = servletInfo;
      this.managedServlet = managedServlet;
      this.deployment = deployment;
   }

   public void setLoadOnStartup(int loadOnStartup) {
      this.servletInfo.setLoadOnStartup(loadOnStartup);
   }

   public Set<String> setServletSecurity(ServletSecurityElement constraint) {
      if (constraint == null) {
         throw UndertowMessages.MESSAGES.argumentCannotBeNull("constraint");
      } else {
         DeploymentInfo deploymentInfo = this.deployment.getDeploymentInfo();
         Set<String> urlPatterns = new HashSet();
         Iterator var4 = deploymentInfo.getSecurityConstraints().iterator();

         Iterator var6;
         while(var4.hasNext()) {
            SecurityConstraint sc = (SecurityConstraint)var4.next();
            var6 = sc.getWebResourceCollections().iterator();

            while(var6.hasNext()) {
               WebResourceCollection webResources = (WebResourceCollection)var6.next();
               urlPatterns.addAll(webResources.getUrlPatterns());
            }
         }

         Set<String> ret = new HashSet();
         Iterator var9 = this.servletInfo.getMappings().iterator();

         while(var9.hasNext()) {
            String url = (String)var9.next();
            if (urlPatterns.contains(url)) {
               ret.add(url);
            }
         }

         ServletSecurityInfo info = new ServletSecurityInfo();
         this.servletInfo.setServletSecurityInfo(info);
         ((ServletSecurityInfo)((ServletSecurityInfo)info.setTransportGuaranteeType(constraint.getTransportGuarantee() == ServletSecurity.TransportGuarantee.CONFIDENTIAL ? TransportGuaranteeType.CONFIDENTIAL : TransportGuaranteeType.NONE)).setEmptyRoleSemantic(this.emptyRoleSemantic(constraint.getEmptyRoleSemantic()))).addRolesAllowed(constraint.getRolesAllowed());
         var6 = constraint.getHttpMethodConstraints().iterator();

         while(var6.hasNext()) {
            HttpMethodConstraintElement methodConstraint = (HttpMethodConstraintElement)var6.next();
            info.addHttpMethodSecurityInfo((HttpMethodSecurityInfo)((HttpMethodSecurityInfo)((HttpMethodSecurityInfo)(new HttpMethodSecurityInfo()).setTransportGuaranteeType(methodConstraint.getTransportGuarantee() == ServletSecurity.TransportGuarantee.CONFIDENTIAL ? TransportGuaranteeType.CONFIDENTIAL : TransportGuaranteeType.NONE)).setMethod(methodConstraint.getMethodName()).setEmptyRoleSemantic(this.emptyRoleSemantic(methodConstraint.getEmptyRoleSemantic()))).addRolesAllowed(methodConstraint.getRolesAllowed()));
         }

         return ret;
      }
   }

   private SecurityInfo.EmptyRoleSemantic emptyRoleSemantic(ServletSecurity.EmptyRoleSemantic emptyRoleSemantic) {
      switch (emptyRoleSemantic) {
         case PERMIT:
            return SecurityInfo.EmptyRoleSemantic.PERMIT;
         case DENY:
            return SecurityInfo.EmptyRoleSemantic.DENY;
         default:
            return null;
      }
   }

   public void setMultipartConfig(MultipartConfigElement multipartConfig) {
      this.servletInfo.setMultipartConfig(multipartConfig);
      this.managedServlet.setupMultipart(this.deployment.getServletContext());
   }

   public void setRunAsRole(String roleName) {
      this.servletInfo.setRunAs(roleName);
   }

   public void setAsyncSupported(boolean isAsyncSupported) {
      this.servletInfo.setAsyncSupported(isAsyncSupported);
   }

   public Set<String> addMapping(String... urlPatterns) {
      return this.deployment.tryAddServletMappings(this.servletInfo, urlPatterns);
   }

   public Collection<String> getMappings() {
      return this.servletInfo.getMappings();
   }

   public String getRunAsRole() {
      return this.servletInfo.getRunAs();
   }

   public String getName() {
      return this.servletInfo.getName();
   }

   public String getClassName() {
      return this.servletInfo.getServletClass().getName();
   }

   public boolean setInitParameter(String name, String value) {
      if (this.servletInfo.getInitParams().containsKey(name)) {
         return false;
      } else {
         this.servletInfo.addInitParam(name, value);
         return true;
      }
   }

   public String getInitParameter(String name) {
      return (String)this.servletInfo.getInitParams().get(name);
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
      return this.servletInfo.getInitParams();
   }
}
