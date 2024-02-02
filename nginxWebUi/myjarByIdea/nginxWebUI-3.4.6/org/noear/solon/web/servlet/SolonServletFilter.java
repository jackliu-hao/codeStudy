package org.noear.solon.web.servlet;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.noear.solon.Solon;
import org.noear.solon.core.event.EventBus;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.ContextUtil;
import org.noear.solon.core.handle.Handler;

public class SolonServletFilter implements Filter {
   public static Handler onFilterStart;
   public static Handler onFilterError;
   public static Handler onFilterEnd;

   public void init(FilterConfig filterConfig) throws ServletException {
   }

   public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
      if (request instanceof HttpServletRequest) {
         Context ctx = new SolonServletContext((HttpServletRequest)request, (HttpServletResponse)response);

         try {
            ContextUtil.currentSet(ctx);
            this.doFilterStart(ctx);
            Solon.app().tryHandle(ctx);
            ContextUtil.currentSet(ctx);
            if (!ctx.getHandled()) {
               filterChain.doFilter(request, response);
            }
         } catch (Throwable var9) {
            ctx.errors = var9;
            this.doFilterError(ctx);
            throw var9;
         } finally {
            this.doFilterEnd(ctx);
            ContextUtil.currentRemove();
         }
      } else {
         filterChain.doFilter(request, response);
      }

   }

   protected void doFilterStart(Context ctx) {
      this.doHandler(onFilterStart, ctx);
   }

   protected void doFilterError(Context ctx) {
      this.doHandler(onFilterError, ctx);
   }

   protected void doFilterEnd(Context ctx) {
      this.doHandler(onFilterEnd, ctx);
   }

   protected void doHandler(Handler h, Context ctx) {
      if (h != null) {
         try {
            h.handle(ctx);
         } catch (Throwable var4) {
            EventBus.push(var4);
         }
      }

   }

   public void destroy() {
   }
}
