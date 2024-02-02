package io.undertow.servlet.handlers;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.servlet.core.ManagedFilter;
import io.undertow.servlet.core.ManagedServlet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import javax.servlet.DispatcherType;
import javax.servlet.ServletException;
import javax.servlet.http.MappingMatch;

public class ServletChain {
   private final HttpHandler handler;
   private final ManagedServlet managedServlet;
   private final String servletPath;
   private final Executor executor;
   private final boolean defaultServletMapping;
   private final MappingMatch mappingMatch;
   private final String pattern;
   private final Map<DispatcherType, List<ManagedFilter>> filters;

   public ServletChain(HttpHandler handler, ManagedServlet managedServlet, String servletPath, boolean defaultServletMapping, MappingMatch mappingMatch, String pattern, Map<DispatcherType, List<ManagedFilter>> filters) {
      this(handler, managedServlet, servletPath, defaultServletMapping, mappingMatch, pattern, filters, true);
   }

   private ServletChain(final HttpHandler originalHandler, ManagedServlet managedServlet, String servletPath, boolean defaultServletMapping, MappingMatch mappingMatch, String pattern, Map<DispatcherType, List<ManagedFilter>> filters, boolean wrapHandler) {
      if (wrapHandler) {
         this.handler = new HttpHandler() {
            private volatile boolean initDone = false;

            public void handleRequest(HttpServerExchange exchange) throws Exception {
               if (!this.initDone) {
                  synchronized(this) {
                     if (!this.initDone) {
                        ServletRequestContext src = (ServletRequestContext)exchange.getAttachment(ServletRequestContext.ATTACHMENT_KEY);
                        ServletChain.this.forceInit(src.getDispatcherType());
                        this.initDone = true;
                     }
                  }
               }

               originalHandler.handleRequest(exchange);
            }
         };
      } else {
         this.handler = originalHandler;
      }

      this.managedServlet = managedServlet;
      this.servletPath = servletPath;
      this.defaultServletMapping = defaultServletMapping;
      this.mappingMatch = mappingMatch;
      this.pattern = pattern;
      this.executor = managedServlet.getServletInfo().getExecutor();
      this.filters = filters;
   }

   public ServletChain(ServletChain other, String pattern, MappingMatch mappingMatch) {
      this(other.getHandler(), other.getManagedServlet(), other.getServletPath(), other.isDefaultServletMapping(), mappingMatch, pattern, other.filters, false);
   }

   public HttpHandler getHandler() {
      return this.handler;
   }

   public ManagedServlet getManagedServlet() {
      return this.managedServlet;
   }

   public String getServletPath() {
      return this.servletPath;
   }

   public Executor getExecutor() {
      return this.executor;
   }

   public boolean isDefaultServletMapping() {
      return this.defaultServletMapping;
   }

   public MappingMatch getMappingMatch() {
      return this.mappingMatch;
   }

   public String getPattern() {
      return this.pattern;
   }

   void forceInit(DispatcherType dispatcherType) throws ServletException {
      if (this.filters != null) {
         List<ManagedFilter> list = (List)this.filters.get(dispatcherType);
         if (list != null && !list.isEmpty()) {
            for(int i = 0; i < list.size(); ++i) {
               ManagedFilter filter = (ManagedFilter)list.get(i);
               filter.forceInit();
            }
         }
      }

      this.managedServlet.forceInit();
   }
}
