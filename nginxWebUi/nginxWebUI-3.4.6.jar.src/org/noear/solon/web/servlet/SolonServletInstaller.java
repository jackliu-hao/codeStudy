/*     */ package org.noear.solon.web.servlet;
/*     */ import java.util.EnumSet;
/*     */ import java.util.EventListener;
/*     */ import java.util.LinkedHashSet;
/*     */ import java.util.Set;
/*     */ import javax.servlet.DispatcherType;
/*     */ import javax.servlet.FilterRegistration;
/*     */ import javax.servlet.ServletContainerInitializer;
/*     */ import javax.servlet.ServletContext;
/*     */ import javax.servlet.ServletRegistration;
/*     */ import javax.servlet.annotation.WebFilter;
/*     */ import javax.servlet.annotation.WebInitParam;
/*     */ import javax.servlet.annotation.WebListener;
/*     */ import javax.servlet.annotation.WebServlet;
/*     */ import org.noear.solon.core.BeanWrap;
/*     */ import org.noear.solon.web.servlet.holder.FilterHodler;
/*     */ import org.noear.solon.web.servlet.holder.ServletHolder;
/*     */ 
/*     */ public class SolonServletInstaller {
/*  20 */   Set<ServletContainerInitializer> initializers = new LinkedHashSet<>();
/*  21 */   Set<FilterHodler> filters = new LinkedHashSet<>();
/*  22 */   Set<EventListener> listeners = new LinkedHashSet<>();
/*  23 */   Set<ServletHolder> servlets = new LinkedHashSet<>();
/*     */   
/*     */   public SolonServletInstaller() {
/*  26 */     Aop.context().beanForeach(bw -> {
/*     */           if (bw.raw() instanceof ServletContainerInitializer) {
/*     */             this.initializers.add(bw.raw());
/*     */           }
/*     */           if (bw.raw() instanceof EventListener) {
/*     */             WebListener anno = (WebListener)bw.clz().getAnnotation(WebListener.class);
/*     */             if (anno != null) {
/*     */               this.listeners.add(bw.raw());
/*     */             }
/*     */           } 
/*     */           if (bw.raw() instanceof Filter) {
/*     */             WebFilter anno = (WebFilter)bw.clz().getAnnotation(WebFilter.class);
/*     */             if (anno != null) {
/*     */               this.filters.add(new FilterHodler(anno, (Filter)bw.raw()));
/*     */             }
/*     */           } 
/*     */           if (bw.raw() instanceof Servlet) {
/*     */             WebServlet anno = (WebServlet)bw.clz().getAnnotation(WebServlet.class);
/*     */             if (anno != null) {
/*     */               this.servlets.add(new ServletHolder(anno, (Servlet)bw.raw()));
/*     */             }
/*     */           } 
/*     */         });
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void startup(Set<Class<?>> set, ServletContext sc) throws ServletException {
/*  55 */     for (ServletContainerInitializer si : this.initializers) {
/*  56 */       si.onStartup(set, sc);
/*     */     }
/*     */     
/*  59 */     for (EventListener l : this.listeners) {
/*  60 */       sc.addListener(l);
/*     */     }
/*     */     
/*  63 */     for (FilterHodler f : this.filters) {
/*  64 */       String[] urlPatterns = f.anno.value();
/*  65 */       if (urlPatterns.length == 0) {
/*  66 */         urlPatterns = f.anno.urlPatterns();
/*     */       }
/*     */ 
/*     */       
/*  70 */       String name = f.anno.filterName();
/*  71 */       if (Utils.isEmpty(name)) {
/*  72 */         name = f.filter.getClass().getSimpleName();
/*     */       }
/*     */ 
/*     */       
/*  76 */       EnumSet<DispatcherType> enumSet = EnumSet.copyOf(Arrays.asList(f.anno.dispatcherTypes()));
/*     */       
/*  78 */       FilterRegistration.Dynamic dy = sc.addFilter(name, f.filter);
/*     */       
/*  80 */       for (WebInitParam ip : f.anno.initParams()) {
/*  81 */         dy.setInitParameter(ip.name(), ip.value());
/*     */       }
/*     */ 
/*     */       
/*  85 */       if (urlPatterns.length > 0) {
/*  86 */         dy.addMappingForUrlPatterns(enumSet, false, urlPatterns);
/*     */       }
/*     */       
/*  89 */       if ((f.anno.servletNames()).length > 0) {
/*  90 */         dy.addMappingForServletNames(enumSet, false, f.anno.servletNames());
/*     */       }
/*     */     } 
/*     */     
/*  94 */     for (ServletHolder s : this.servlets) {
/*  95 */       String[] urlPatterns = s.anno.value();
/*  96 */       if (urlPatterns.length == 0) {
/*  97 */         urlPatterns = s.anno.urlPatterns();
/*     */       }
/*     */       
/* 100 */       String name = s.anno.name();
/* 101 */       if (Utils.isEmpty(name)) {
/* 102 */         name = s.servlet.getClass().getSimpleName();
/*     */       }
/*     */       
/* 105 */       ServletRegistration.Dynamic dy = sc.addServlet(name, s.servlet);
/*     */       
/* 107 */       for (WebInitParam ip : s.anno.initParams()) {
/* 108 */         dy.setInitParameter(ip.name(), ip.value());
/*     */       }
/*     */       
/* 111 */       dy.addMapping(urlPatterns);
/* 112 */       dy.setLoadOnStartup(s.anno.loadOnStartup());
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\web\servlet\SolonServletInstaller.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */