/*     */ package io.undertow.servlet.api;
/*     */ 
/*     */ import io.undertow.servlet.UndertowServletMessages;
/*     */ import io.undertow.servlet.util.ConstructorInstanceFactory;
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import javax.servlet.Filter;
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
/*     */ public class FilterInfo
/*     */   implements Cloneable
/*     */ {
/*     */   private final Class<? extends Filter> filterClass;
/*     */   private final String name;
/*     */   private volatile InstanceFactory<? extends Filter> instanceFactory;
/*  40 */   private final Map<String, String> initParams = new HashMap<>();
/*     */   
/*     */   private volatile boolean asyncSupported;
/*     */   
/*     */   public FilterInfo(String name, Class<? extends Filter> filterClass) {
/*  45 */     if (name == null) {
/*  46 */       throw UndertowServletMessages.MESSAGES.paramCannotBeNull("name");
/*     */     }
/*  48 */     if (filterClass == null) {
/*  49 */       throw UndertowServletMessages.MESSAGES.paramCannotBeNull("filterClass", "Filter", name);
/*     */     }
/*  51 */     if (!Filter.class.isAssignableFrom(filterClass)) {
/*  52 */       throw UndertowServletMessages.MESSAGES.filterMustImplementFilter(name, filterClass);
/*     */     }
/*     */     try {
/*  55 */       Constructor<Filter> ctor = (Constructor)filterClass.getDeclaredConstructor(new Class[0]);
/*  56 */       ctor.setAccessible(true);
/*  57 */       this.instanceFactory = (InstanceFactory<? extends Filter>)new ConstructorInstanceFactory(ctor);
/*  58 */       this.name = name;
/*  59 */       this.filterClass = filterClass;
/*  60 */     } catch (NoSuchMethodException e) {
/*  61 */       throw UndertowServletMessages.MESSAGES.componentMustHaveDefaultConstructor("Filter", filterClass);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public FilterInfo(String name, Class<? extends Filter> filterClass, InstanceFactory<? extends Filter> instanceFactory) {
/*  67 */     if (name == null) {
/*  68 */       throw UndertowServletMessages.MESSAGES.paramCannotBeNull("name");
/*     */     }
/*  70 */     if (filterClass == null) {
/*  71 */       throw UndertowServletMessages.MESSAGES.paramCannotBeNull("filterClass", "Filter", name);
/*     */     }
/*  73 */     if (!Filter.class.isAssignableFrom(filterClass)) {
/*  74 */       throw UndertowServletMessages.MESSAGES.filterMustImplementFilter(name, filterClass);
/*     */     }
/*  76 */     this.instanceFactory = instanceFactory;
/*  77 */     this.name = name;
/*  78 */     this.filterClass = filterClass;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void validate() {}
/*     */ 
/*     */ 
/*     */   
/*     */   public FilterInfo clone() {
/*  88 */     FilterInfo info = (new FilterInfo(this.name, this.filterClass, this.instanceFactory)).setAsyncSupported(this.asyncSupported);
/*  89 */     info.initParams.putAll(this.initParams);
/*  90 */     return info;
/*     */   }
/*     */   
/*     */   public Class<? extends Filter> getFilterClass() {
/*  94 */     return this.filterClass;
/*     */   }
/*     */   
/*     */   public String getName() {
/*  98 */     return this.name;
/*     */   }
/*     */   public InstanceFactory<? extends Filter> getInstanceFactory() {
/* 101 */     return this.instanceFactory;
/*     */   }
/*     */   
/*     */   public void setInstanceFactory(InstanceFactory<? extends Filter> instanceFactory) {
/* 105 */     this.instanceFactory = instanceFactory;
/*     */   }
/*     */   
/*     */   public FilterInfo addInitParam(String name, String value) {
/* 109 */     this.initParams.put(name, value);
/* 110 */     return this;
/*     */   }
/*     */   
/*     */   public Map<String, String> getInitParams() {
/* 114 */     return Collections.unmodifiableMap(this.initParams);
/*     */   }
/*     */   
/*     */   public boolean isAsyncSupported() {
/* 118 */     return this.asyncSupported;
/*     */   }
/*     */   
/*     */   public FilterInfo setAsyncSupported(boolean asyncSupported) {
/* 122 */     this.asyncSupported = asyncSupported;
/* 123 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 128 */     return "FilterInfo{filterClass=" + this.filterClass + ", name='" + this.name + '\'' + '}';
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\servlet\api\FilterInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */