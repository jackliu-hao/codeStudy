package io.undertow.servlet.api;

import io.undertow.server.HandlerWrapper;
import io.undertow.servlet.UndertowServletMessages;
import io.undertow.servlet.util.ConstructorInstanceFactory;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import javax.servlet.MultipartConfigElement;
import javax.servlet.Servlet;

public class ServletInfo implements Cloneable {
   private final Class<? extends Servlet> servletClass;
   private final String name;
   private final List<String> mappings = new ArrayList();
   private final Map<String, String> initParams = new HashMap();
   private final List<SecurityRoleRef> securityRoleRefs = new ArrayList();
   private final List<HandlerWrapper> handlerChainWrappers = new ArrayList();
   private InstanceFactory<? extends Servlet> instanceFactory;
   private String jspFile;
   private Integer loadOnStartup;
   private boolean enabled;
   private boolean asyncSupported;
   private String runAs;
   private MultipartConfigElement multipartConfig;
   private ServletSecurityInfo servletSecurityInfo;
   private Executor executor;
   private boolean requireWelcomeFileMapping;

   public ServletInfo(String name, Class<? extends Servlet> servletClass) {
      if (name == null) {
         throw UndertowServletMessages.MESSAGES.paramCannotBeNull("name");
      } else if (servletClass == null) {
         throw UndertowServletMessages.MESSAGES.paramCannotBeNull("servletClass", "Servlet", name);
      } else if (!Servlet.class.isAssignableFrom(servletClass)) {
         throw UndertowServletMessages.MESSAGES.servletMustImplementServlet(name, servletClass);
      } else {
         try {
            Constructor<? extends Servlet> ctor = servletClass.getDeclaredConstructor();
            ctor.setAccessible(true);
            this.instanceFactory = new ConstructorInstanceFactory(ctor);
            this.name = name;
            this.servletClass = servletClass;
         } catch (NoSuchMethodException var4) {
            throw UndertowServletMessages.MESSAGES.componentMustHaveDefaultConstructor("Servlet", servletClass);
         }
      }
   }

   public ServletInfo(String name, Class<? extends Servlet> servletClass, InstanceFactory<? extends Servlet> instanceFactory) {
      if (name == null) {
         throw UndertowServletMessages.MESSAGES.paramCannotBeNull("name");
      } else if (servletClass == null) {
         throw UndertowServletMessages.MESSAGES.paramCannotBeNull("servletClass", "Servlet", name);
      } else if (!Servlet.class.isAssignableFrom(servletClass)) {
         throw UndertowServletMessages.MESSAGES.servletMustImplementServlet(name, servletClass);
      } else {
         this.instanceFactory = instanceFactory;
         this.name = name;
         this.servletClass = servletClass;
      }
   }

   public void validate() {
   }

   public ServletInfo clone() {
      ServletInfo info = (new ServletInfo(this.name, this.servletClass, this.instanceFactory)).setJspFile(this.jspFile).setLoadOnStartup(this.loadOnStartup).setEnabled(this.enabled).setAsyncSupported(this.asyncSupported).setRunAs(this.runAs).setMultipartConfig(this.multipartConfig).setExecutor(this.executor).setRequireWelcomeFileMapping(this.requireWelcomeFileMapping);
      info.mappings.addAll(this.mappings);
      info.initParams.putAll(this.initParams);
      info.securityRoleRefs.addAll(this.securityRoleRefs);
      info.handlerChainWrappers.addAll(this.handlerChainWrappers);
      if (this.servletSecurityInfo != null) {
         info.servletSecurityInfo = this.servletSecurityInfo.clone();
      }

      return info;
   }

   public Class<? extends Servlet> getServletClass() {
      return this.servletClass;
   }

   public String getName() {
      return this.name;
   }

   public void setInstanceFactory(InstanceFactory<? extends Servlet> instanceFactory) {
      if (instanceFactory == null) {
         throw UndertowServletMessages.MESSAGES.paramCannotBeNull("instanceFactory");
      } else {
         this.instanceFactory = instanceFactory;
      }
   }

   public InstanceFactory<? extends Servlet> getInstanceFactory() {
      return this.instanceFactory;
   }

   public List<String> getMappings() {
      return Collections.unmodifiableList(this.mappings);
   }

   public ServletInfo addMapping(String mapping) {
      if (!mapping.startsWith("/") && !mapping.startsWith("*") && !mapping.isEmpty()) {
         this.mappings.add("/" + mapping);
      } else {
         this.mappings.add(mapping);
      }

      return this;
   }

   public ServletInfo addMappings(Collection<String> mappings) {
      Iterator var2 = mappings.iterator();

      while(var2.hasNext()) {
         String m = (String)var2.next();
         this.addMapping(m);
      }

      return this;
   }

   public ServletInfo addMappings(String... mappings) {
      String[] var2 = mappings;
      int var3 = mappings.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         String m = var2[var4];
         this.addMapping(m);
      }

      return this;
   }

   public ServletInfo addInitParam(String name, String value) {
      this.initParams.put(name, value);
      return this;
   }

   public Map<String, String> getInitParams() {
      return Collections.unmodifiableMap(this.initParams);
   }

   public String getJspFile() {
      return this.jspFile;
   }

   public ServletInfo setJspFile(String jspFile) {
      this.jspFile = jspFile;
      return this;
   }

   public Integer getLoadOnStartup() {
      return this.loadOnStartup;
   }

   public ServletInfo setLoadOnStartup(Integer loadOnStartup) {
      this.loadOnStartup = loadOnStartup;
      return this;
   }

   public boolean isAsyncSupported() {
      return this.asyncSupported;
   }

   public ServletInfo setAsyncSupported(boolean asyncSupported) {
      this.asyncSupported = asyncSupported;
      return this;
   }

   public boolean isEnabled() {
      return this.enabled;
   }

   public ServletInfo setEnabled(boolean enabled) {
      this.enabled = enabled;
      return this;
   }

   public String getRunAs() {
      return this.runAs;
   }

   public ServletInfo setRunAs(String runAs) {
      this.runAs = runAs;
      return this;
   }

   public MultipartConfigElement getMultipartConfig() {
      return this.multipartConfig;
   }

   public ServletInfo setMultipartConfig(MultipartConfigElement multipartConfig) {
      this.multipartConfig = multipartConfig;
      return this;
   }

   public void addSecurityRoleRef(String role, String linkedRole) {
      this.securityRoleRefs.add(new SecurityRoleRef(role, linkedRole));
   }

   public List<SecurityRoleRef> getSecurityRoleRefs() {
      return Collections.unmodifiableList(this.securityRoleRefs);
   }

   public ServletInfo addHandlerChainWrapper(HandlerWrapper wrapper) {
      this.handlerChainWrappers.add(wrapper);
      return this;
   }

   public List<HandlerWrapper> getHandlerChainWrappers() {
      return Collections.unmodifiableList(this.handlerChainWrappers);
   }

   public ServletSecurityInfo getServletSecurityInfo() {
      return this.servletSecurityInfo;
   }

   public ServletInfo setServletSecurityInfo(ServletSecurityInfo servletSecurityInfo) {
      this.servletSecurityInfo = servletSecurityInfo;
      return this;
   }

   public Executor getExecutor() {
      return this.executor;
   }

   public ServletInfo setExecutor(Executor executor) {
      this.executor = executor;
      return this;
   }

   public boolean isRequireWelcomeFileMapping() {
      return this.requireWelcomeFileMapping;
   }

   public ServletInfo setRequireWelcomeFileMapping(boolean requireWelcomeFileMapping) {
      this.requireWelcomeFileMapping = requireWelcomeFileMapping;
      return this;
   }

   public String toString() {
      return "ServletInfo{mappings=" + this.mappings + ", servletClass=" + this.servletClass + ", name='" + this.name + '\'' + '}';
   }
}
