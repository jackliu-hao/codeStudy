package io.undertow.servlet.api;

import javax.servlet.Filter;
import javax.servlet.Servlet;
import javax.servlet.ServletException;

public interface LifecycleInterceptor {
   void init(ServletInfo var1, Servlet var2, LifecycleContext var3) throws ServletException;

   void init(FilterInfo var1, Filter var2, LifecycleContext var3) throws ServletException;

   void destroy(ServletInfo var1, Servlet var2, LifecycleContext var3) throws ServletException;

   void destroy(FilterInfo var1, Filter var2, LifecycleContext var3) throws ServletException;

   public interface LifecycleContext {
      void proceed() throws ServletException;
   }
}
