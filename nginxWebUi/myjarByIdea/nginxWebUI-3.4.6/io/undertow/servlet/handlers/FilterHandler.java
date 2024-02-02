package io.undertow.servlet.handlers;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.servlet.UndertowServletMessages;
import io.undertow.servlet.core.ManagedFilter;
import java.io.IOException;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.servlet.DispatcherType;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletRequestWrapper;
import javax.servlet.ServletResponse;
import javax.servlet.ServletResponseWrapper;

public class FilterHandler implements HttpHandler {
   private final Map<DispatcherType, List<ManagedFilter>> filters;
   private final Map<DispatcherType, Boolean> asyncSupported;
   private final boolean allowNonStandardWrappers;
   private final HttpHandler next;

   public FilterHandler(Map<DispatcherType, List<ManagedFilter>> filters, boolean allowNonStandardWrappers, HttpHandler next) {
      this.allowNonStandardWrappers = allowNonStandardWrappers;
      this.next = next;
      this.filters = new EnumMap(filters);
      Map<DispatcherType, Boolean> asyncSupported = new EnumMap(DispatcherType.class);

      Map.Entry entry;
      boolean supported;
      for(Iterator var5 = filters.entrySet().iterator(); var5.hasNext(); asyncSupported.put(entry.getKey(), supported)) {
         entry = (Map.Entry)var5.next();
         supported = true;
         Iterator var8 = ((List)entry.getValue()).iterator();

         while(var8.hasNext()) {
            ManagedFilter i = (ManagedFilter)var8.next();
            if (!i.getFilterInfo().isAsyncSupported()) {
               supported = false;
               break;
            }
         }
      }

      this.asyncSupported = asyncSupported;
   }

   public void handleRequest(HttpServerExchange exchange) throws Exception {
      ServletRequestContext servletRequestContext = (ServletRequestContext)exchange.getAttachment(ServletRequestContext.ATTACHMENT_KEY);
      ServletRequest request = servletRequestContext.getServletRequest();
      ServletResponse response = servletRequestContext.getServletResponse();
      DispatcherType dispatcher = servletRequestContext.getDispatcherType();
      Boolean supported = (Boolean)this.asyncSupported.get(dispatcher);
      if (supported != null && !supported) {
         servletRequestContext.setAsyncSupported(false);
      }

      List<ManagedFilter> filters = (List)this.filters.get(dispatcher);
      if (filters == null) {
         this.next.handleRequest(exchange);
      } else {
         FilterChainImpl filterChain = new FilterChainImpl(exchange, filters, this.next, this.allowNonStandardWrappers);
         filterChain.doFilter(request, response);
      }

   }

   private static class FilterChainImpl implements FilterChain {
      int location;
      final HttpServerExchange exchange;
      final List<ManagedFilter> filters;
      final HttpHandler next;
      final boolean allowNonStandardWrappers;

      private FilterChainImpl(HttpServerExchange exchange, List<ManagedFilter> filters, HttpHandler next, boolean allowNonStandardWrappers) {
         this.location = 0;
         this.exchange = exchange;
         this.filters = filters;
         this.next = next;
         this.allowNonStandardWrappers = allowNonStandardWrappers;
      }

      public void doFilter(ServletRequest request, ServletResponse response) throws IOException, ServletException {
         ServletRequestContext servletRequestContext = (ServletRequestContext)this.exchange.getAttachment(ServletRequestContext.ATTACHMENT_KEY);
         ServletRequest oldReq = servletRequestContext.getServletRequest();
         ServletResponse oldResp = servletRequestContext.getServletResponse();

         try {
            if (!this.allowNonStandardWrappers) {
               if (oldReq != request && !(request instanceof ServletRequestWrapper)) {
                  throw UndertowServletMessages.MESSAGES.requestWasNotOriginalOrWrapper(request);
               }

               if (oldResp != response && !(response instanceof ServletResponseWrapper)) {
                  throw UndertowServletMessages.MESSAGES.responseWasNotOriginalOrWrapper(response);
               }
            }

            servletRequestContext.setServletRequest(request);
            servletRequestContext.setServletResponse(response);
            int index = this.location++;
            if (index >= this.filters.size()) {
               this.next.handleRequest(this.exchange);
            } else {
               ((ManagedFilter)this.filters.get(index)).doFilter(request, response, this);
            }
         } catch (IOException var13) {
            throw var13;
         } catch (ServletException var14) {
            throw var14;
         } catch (RuntimeException var15) {
            throw var15;
         } catch (Exception var16) {
            throw new RuntimeException(var16);
         } finally {
            --this.location;
            servletRequestContext.setServletRequest(oldReq);
            servletRequestContext.setServletResponse(oldResp);
         }

      }

      // $FF: synthetic method
      FilterChainImpl(HttpServerExchange x0, List x1, HttpHandler x2, boolean x3, Object x4) {
         this(x0, x1, x2, x3);
      }
   }
}
