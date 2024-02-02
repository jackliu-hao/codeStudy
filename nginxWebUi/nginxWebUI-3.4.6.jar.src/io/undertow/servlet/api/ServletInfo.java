/*     */ package io.undertow.servlet.api;
/*     */ 
/*     */ import io.undertow.server.HandlerWrapper;
/*     */ import io.undertow.servlet.UndertowServletMessages;
/*     */ import io.undertow.servlet.util.ConstructorInstanceFactory;
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.concurrent.Executor;
/*     */ import javax.servlet.MultipartConfigElement;
/*     */ import javax.servlet.Servlet;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ServletInfo
/*     */   implements Cloneable
/*     */ {
/*     */   private final Class<? extends Servlet> servletClass;
/*     */   private final String name;
/*  45 */   private final List<String> mappings = new ArrayList<>();
/*  46 */   private final Map<String, String> initParams = new HashMap<>();
/*  47 */   private final List<SecurityRoleRef> securityRoleRefs = new ArrayList<>();
/*  48 */   private final List<HandlerWrapper> handlerChainWrappers = new ArrayList<>();
/*     */   
/*     */   private InstanceFactory<? extends Servlet> instanceFactory;
/*     */   
/*     */   private String jspFile;
/*     */   
/*     */   private Integer loadOnStartup;
/*     */   
/*     */   private boolean enabled;
/*     */   
/*     */   private boolean asyncSupported;
/*     */   
/*     */   private String runAs;
/*     */   
/*     */   private MultipartConfigElement multipartConfig;
/*     */   
/*     */   private ServletSecurityInfo servletSecurityInfo;
/*     */   private Executor executor;
/*     */   private boolean requireWelcomeFileMapping;
/*     */   
/*     */   public ServletInfo(String name, Class<? extends Servlet> servletClass) {
/*  69 */     if (name == null) {
/*  70 */       throw UndertowServletMessages.MESSAGES.paramCannotBeNull("name");
/*     */     }
/*  72 */     if (servletClass == null) {
/*  73 */       throw UndertowServletMessages.MESSAGES.paramCannotBeNull("servletClass", "Servlet", name);
/*     */     }
/*  75 */     if (!Servlet.class.isAssignableFrom(servletClass)) {
/*  76 */       throw UndertowServletMessages.MESSAGES.servletMustImplementServlet(name, servletClass);
/*     */     }
/*     */     try {
/*  79 */       Constructor<? extends Servlet> ctor = servletClass.getDeclaredConstructor(new Class[0]);
/*  80 */       ctor.setAccessible(true);
/*  81 */       this.instanceFactory = (InstanceFactory<? extends Servlet>)new ConstructorInstanceFactory(ctor);
/*  82 */       this.name = name;
/*  83 */       this.servletClass = servletClass;
/*  84 */     } catch (NoSuchMethodException e) {
/*  85 */       throw UndertowServletMessages.MESSAGES.componentMustHaveDefaultConstructor("Servlet", servletClass);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public ServletInfo(String name, Class<? extends Servlet> servletClass, InstanceFactory<? extends Servlet> instanceFactory) {
/*  91 */     if (name == null) {
/*  92 */       throw UndertowServletMessages.MESSAGES.paramCannotBeNull("name");
/*     */     }
/*  94 */     if (servletClass == null) {
/*  95 */       throw UndertowServletMessages.MESSAGES.paramCannotBeNull("servletClass", "Servlet", name);
/*     */     }
/*  97 */     if (!Servlet.class.isAssignableFrom(servletClass)) {
/*  98 */       throw UndertowServletMessages.MESSAGES.servletMustImplementServlet(name, servletClass);
/*     */     }
/* 100 */     this.instanceFactory = instanceFactory;
/* 101 */     this.name = name;
/* 102 */     this.servletClass = servletClass;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void validate() {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ServletInfo clone() {
/* 119 */     ServletInfo info = (new ServletInfo(this.name, this.servletClass, this.instanceFactory)).setJspFile(this.jspFile).setLoadOnStartup(this.loadOnStartup).setEnabled(this.enabled).setAsyncSupported(this.asyncSupported).setRunAs(this.runAs).setMultipartConfig(this.multipartConfig).setExecutor(this.executor).setRequireWelcomeFileMapping(this.requireWelcomeFileMapping);
/* 120 */     info.mappings.addAll(this.mappings);
/* 121 */     info.initParams.putAll(this.initParams);
/* 122 */     info.securityRoleRefs.addAll(this.securityRoleRefs);
/* 123 */     info.handlerChainWrappers.addAll(this.handlerChainWrappers);
/* 124 */     if (this.servletSecurityInfo != null) {
/* 125 */       info.servletSecurityInfo = this.servletSecurityInfo.clone();
/*     */     }
/* 127 */     return info;
/*     */   }
/*     */   
/*     */   public Class<? extends Servlet> getServletClass() {
/* 131 */     return this.servletClass;
/*     */   }
/*     */   
/*     */   public String getName() {
/* 135 */     return this.name;
/*     */   }
/*     */   
/*     */   public void setInstanceFactory(InstanceFactory<? extends Servlet> instanceFactory) {
/* 139 */     if (instanceFactory == null) {
/* 140 */       throw UndertowServletMessages.MESSAGES.paramCannotBeNull("instanceFactory");
/*     */     }
/* 142 */     this.instanceFactory = instanceFactory;
/*     */   }
/*     */   
/*     */   public InstanceFactory<? extends Servlet> getInstanceFactory() {
/* 146 */     return this.instanceFactory;
/*     */   }
/*     */   
/*     */   public List<String> getMappings() {
/* 150 */     return Collections.unmodifiableList(this.mappings);
/*     */   }
/*     */   
/*     */   public ServletInfo addMapping(String mapping) {
/* 154 */     if (!mapping.startsWith("/") && !mapping.startsWith("*") && !mapping.isEmpty()) {
/*     */       
/* 156 */       this.mappings.add("/" + mapping);
/*     */     } else {
/* 158 */       this.mappings.add(mapping);
/*     */     } 
/* 160 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public ServletInfo addMappings(Collection<String> mappings) {
/* 165 */     for (String m : mappings) {
/* 166 */       addMapping(m);
/*     */     }
/* 168 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public ServletInfo addMappings(String... mappings) {
/* 173 */     for (String m : mappings) {
/* 174 */       addMapping(m);
/*     */     }
/* 176 */     return this;
/*     */   }
/*     */   
/*     */   public ServletInfo addInitParam(String name, String value) {
/* 180 */     this.initParams.put(name, value);
/* 181 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public Map<String, String> getInitParams() {
/* 186 */     return Collections.unmodifiableMap(this.initParams);
/*     */   }
/*     */   
/*     */   public String getJspFile() {
/* 190 */     return this.jspFile;
/*     */   }
/*     */   
/*     */   public ServletInfo setJspFile(String jspFile) {
/* 194 */     this.jspFile = jspFile;
/* 195 */     return this;
/*     */   }
/*     */   
/*     */   public Integer getLoadOnStartup() {
/* 199 */     return this.loadOnStartup;
/*     */   }
/*     */   
/*     */   public ServletInfo setLoadOnStartup(Integer loadOnStartup) {
/* 203 */     this.loadOnStartup = loadOnStartup;
/* 204 */     return this;
/*     */   }
/*     */   
/*     */   public boolean isAsyncSupported() {
/* 208 */     return this.asyncSupported;
/*     */   }
/*     */   
/*     */   public ServletInfo setAsyncSupported(boolean asyncSupported) {
/* 212 */     this.asyncSupported = asyncSupported;
/* 213 */     return this;
/*     */   }
/*     */   
/*     */   public boolean isEnabled() {
/* 217 */     return this.enabled;
/*     */   }
/*     */   
/*     */   public ServletInfo setEnabled(boolean enabled) {
/* 221 */     this.enabled = enabled;
/* 222 */     return this;
/*     */   }
/*     */   
/*     */   public String getRunAs() {
/* 226 */     return this.runAs;
/*     */   }
/*     */   
/*     */   public ServletInfo setRunAs(String runAs) {
/* 230 */     this.runAs = runAs;
/* 231 */     return this;
/*     */   }
/*     */   
/*     */   public MultipartConfigElement getMultipartConfig() {
/* 235 */     return this.multipartConfig;
/*     */   }
/*     */   
/*     */   public ServletInfo setMultipartConfig(MultipartConfigElement multipartConfig) {
/* 239 */     this.multipartConfig = multipartConfig;
/* 240 */     return this;
/*     */   }
/*     */   
/*     */   public void addSecurityRoleRef(String role, String linkedRole) {
/* 244 */     this.securityRoleRefs.add(new SecurityRoleRef(role, linkedRole));
/*     */   }
/*     */   
/*     */   public List<SecurityRoleRef> getSecurityRoleRefs() {
/* 248 */     return Collections.unmodifiableList(this.securityRoleRefs);
/*     */   }
/*     */   
/*     */   public ServletInfo addHandlerChainWrapper(HandlerWrapper wrapper) {
/* 252 */     this.handlerChainWrappers.add(wrapper);
/* 253 */     return this;
/*     */   }
/*     */   
/*     */   public List<HandlerWrapper> getHandlerChainWrappers() {
/* 257 */     return Collections.unmodifiableList(this.handlerChainWrappers);
/*     */   }
/*     */   
/*     */   public ServletSecurityInfo getServletSecurityInfo() {
/* 261 */     return this.servletSecurityInfo;
/*     */   }
/*     */   
/*     */   public ServletInfo setServletSecurityInfo(ServletSecurityInfo servletSecurityInfo) {
/* 265 */     this.servletSecurityInfo = servletSecurityInfo;
/* 266 */     return this;
/*     */   }
/*     */   
/*     */   public Executor getExecutor() {
/* 270 */     return this.executor;
/*     */   }
/*     */   
/*     */   public ServletInfo setExecutor(Executor executor) {
/* 274 */     this.executor = executor;
/* 275 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isRequireWelcomeFileMapping() {
/* 283 */     return this.requireWelcomeFileMapping;
/*     */   }
/*     */   
/*     */   public ServletInfo setRequireWelcomeFileMapping(boolean requireWelcomeFileMapping) {
/* 287 */     this.requireWelcomeFileMapping = requireWelcomeFileMapping;
/* 288 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 293 */     return "ServletInfo{mappings=" + this.mappings + ", servletClass=" + this.servletClass + ", name='" + this.name + '\'' + '}';
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\servlet\api\ServletInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */