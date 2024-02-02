package io.undertow.servlet.core;

import io.undertow.servlet.api.FilterInfo;
import io.undertow.servlet.api.LifecycleInterceptor;
import io.undertow.servlet.api.ServletInfo;
import java.util.List;
import javax.servlet.Filter;
import javax.servlet.FilterConfig;
import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

class LifecyleInterceptorInvocation implements LifecycleInterceptor.LifecycleContext {
   private final List<LifecycleInterceptor> list;
   private final ServletInfo servletInfo;
   private final FilterInfo filterInfo;
   private final Servlet servlet;
   private final Filter filter;
   private int i;
   private final ServletConfig servletConfig;
   private final FilterConfig filterConfig;

   LifecyleInterceptorInvocation(List<LifecycleInterceptor> list, ServletInfo servletInfo, Servlet servlet, ServletConfig servletConfig) {
      this.list = list;
      this.servletInfo = servletInfo;
      this.servlet = servlet;
      this.servletConfig = servletConfig;
      this.filter = null;
      this.filterConfig = null;
      this.filterInfo = null;
      this.i = list.size();
   }

   LifecyleInterceptorInvocation(List<LifecycleInterceptor> list, ServletInfo servletInfo, Servlet servlet) {
      this.list = list;
      this.servlet = servlet;
      this.servletInfo = servletInfo;
      this.filterInfo = null;
      this.servletConfig = null;
      this.filter = null;
      this.filterConfig = null;
      this.i = list.size();
   }

   LifecyleInterceptorInvocation(List<LifecycleInterceptor> list, FilterInfo filterInfo, Filter filter, FilterConfig filterConfig) {
      this.list = list;
      this.servlet = null;
      this.servletConfig = null;
      this.filter = filter;
      this.filterConfig = filterConfig;
      this.filterInfo = filterInfo;
      this.servletInfo = null;
      this.i = list.size();
   }

   LifecyleInterceptorInvocation(List<LifecycleInterceptor> list, FilterInfo filterInfo, Filter filter) {
      this.list = list;
      this.servlet = null;
      this.servletConfig = null;
      this.filter = filter;
      this.filterConfig = null;
      this.filterInfo = filterInfo;
      this.servletInfo = null;
      this.i = list.size();
   }

   public void proceed() throws ServletException {
      if (--this.i >= 0) {
         LifecycleInterceptor next = (LifecycleInterceptor)this.list.get(this.i);
         if (this.filter != null) {
            if (this.filterConfig == null) {
               next.destroy((FilterInfo)this.filterInfo, (Filter)this.filter, this);
            } else {
               next.init((FilterInfo)this.filterInfo, (Filter)this.filter, this);
            }
         } else if (this.servletConfig == null) {
            next.destroy((ServletInfo)this.servletInfo, (Servlet)this.servlet, this);
         } else {
            next.init((ServletInfo)this.servletInfo, (Servlet)this.servlet, this);
         }
      } else if (this.i == -1) {
         if (this.filter != null) {
            if (this.filterConfig == null) {
               this.filter.destroy();
            } else {
               this.filter.init(this.filterConfig);
            }
         } else if (this.servletConfig == null) {
            this.servlet.destroy();
         } else {
            this.servlet.init(this.servletConfig);
         }
      }

   }
}
