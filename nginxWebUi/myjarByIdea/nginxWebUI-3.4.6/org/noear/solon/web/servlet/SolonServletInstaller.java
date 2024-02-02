package org.noear.solon.web.servlet;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.EventListener;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import javax.servlet.FilterRegistration;
import javax.servlet.Servlet;
import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebListener;
import javax.servlet.annotation.WebServlet;
import org.noear.solon.Utils;
import org.noear.solon.core.Aop;
import org.noear.solon.web.servlet.holder.FilterHodler;
import org.noear.solon.web.servlet.holder.ServletHolder;

public class SolonServletInstaller {
   Set<ServletContainerInitializer> initializers = new LinkedHashSet();
   Set<FilterHodler> filters = new LinkedHashSet();
   Set<EventListener> listeners = new LinkedHashSet();
   Set<ServletHolder> servlets = new LinkedHashSet();

   public SolonServletInstaller() {
      Aop.context().beanForeach((bw) -> {
         if (bw.raw() instanceof ServletContainerInitializer) {
            this.initializers.add(bw.raw());
         }

         if (bw.raw() instanceof EventListener) {
            WebListener annoxx = (WebListener)bw.clz().getAnnotation(WebListener.class);
            if (annoxx != null) {
               this.listeners.add(bw.raw());
            }
         }

         if (bw.raw() instanceof Filter) {
            WebFilter anno = (WebFilter)bw.clz().getAnnotation(WebFilter.class);
            if (anno != null) {
               this.filters.add(new FilterHodler(anno, (Filter)bw.raw()));
            }
         }

         if (bw.raw() instanceof Servlet) {
            WebServlet annox = (WebServlet)bw.clz().getAnnotation(WebServlet.class);
            if (annox != null) {
               this.servlets.add(new ServletHolder(annox, (Servlet)bw.raw()));
            }
         }

      });
   }

   public void startup(Set<Class<?>> set, ServletContext sc) throws ServletException {
      Iterator var3 = this.initializers.iterator();

      while(var3.hasNext()) {
         ServletContainerInitializer si = (ServletContainerInitializer)var3.next();
         si.onStartup(set, sc);
      }

      var3 = this.listeners.iterator();

      while(var3.hasNext()) {
         EventListener l = (EventListener)var3.next();
         sc.addListener(l);
      }

      var3 = this.filters.iterator();

      String[] urlPatterns;
      String name;
      int var10;
      while(var3.hasNext()) {
         FilterHodler f = (FilterHodler)var3.next();
         urlPatterns = f.anno.value();
         if (urlPatterns.length == 0) {
            urlPatterns = f.anno.urlPatterns();
         }

         name = f.anno.filterName();
         if (Utils.isEmpty(name)) {
            name = f.filter.getClass().getSimpleName();
         }

         EnumSet<DispatcherType> enumSet = EnumSet.copyOf(Arrays.asList(f.anno.dispatcherTypes()));
         FilterRegistration.Dynamic dy = sc.addFilter(name, f.filter);
         WebInitParam[] var9 = f.anno.initParams();
         var10 = var9.length;

         for(int var11 = 0; var11 < var10; ++var11) {
            WebInitParam ip = var9[var11];
            dy.setInitParameter(ip.name(), ip.value());
         }

         if (urlPatterns.length > 0) {
            dy.addMappingForUrlPatterns(enumSet, false, urlPatterns);
         }

         if (f.anno.servletNames().length > 0) {
            dy.addMappingForServletNames(enumSet, false, f.anno.servletNames());
         }
      }

      var3 = this.servlets.iterator();

      while(var3.hasNext()) {
         ServletHolder s = (ServletHolder)var3.next();
         urlPatterns = s.anno.value();
         if (urlPatterns.length == 0) {
            urlPatterns = s.anno.urlPatterns();
         }

         name = s.anno.name();
         if (Utils.isEmpty(name)) {
            name = s.servlet.getClass().getSimpleName();
         }

         ServletRegistration.Dynamic dy = sc.addServlet(name, s.servlet);
         WebInitParam[] var17 = s.anno.initParams();
         int var18 = var17.length;

         for(var10 = 0; var10 < var18; ++var10) {
            WebInitParam ip = var17[var10];
            dy.setInitParameter(ip.name(), ip.value());
         }

         dy.addMapping(urlPatterns);
         dy.setLoadOnStartup(s.anno.loadOnStartup());
      }

   }
}
