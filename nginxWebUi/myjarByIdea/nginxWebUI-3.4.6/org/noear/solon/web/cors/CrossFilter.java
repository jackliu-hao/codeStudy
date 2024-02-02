package org.noear.solon.web.cors;

import org.noear.solon.Utils;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Filter;
import org.noear.solon.core.handle.FilterChain;
import org.noear.solon.core.handle.MethodType;

public class CrossFilter implements Filter {
   protected int maxAge = 3600;
   protected String allowedOrigins = "*";
   protected String allowedMethods = "*";
   protected String allowedHeaders = "*";
   protected boolean allowCredentials = true;
   protected String exposedHeaders;

   public CrossFilter maxAge(int maxAge) {
      if (maxAge >= 0) {
         this.maxAge = maxAge;
      }

      return this;
   }

   public CrossFilter allowedOrigins(String allowOrigin) {
      if (allowOrigin != null) {
         this.allowedOrigins = allowOrigin;
      }

      return this;
   }

   public CrossFilter allowedMethods(String allowMethods) {
      this.allowedMethods = allowMethods;
      return this;
   }

   public CrossFilter allowedHeaders(String allowHeaders) {
      this.allowedHeaders = allowHeaders;
      return this;
   }

   public CrossFilter allowCredentials(boolean allowCredentials) {
      this.allowCredentials = allowCredentials;
      return this;
   }

   public CrossFilter exposedHeaders(String exposeHeaders) {
      this.exposedHeaders = exposeHeaders;
      return this;
   }

   public void doFilter(Context ctx, FilterChain chain) throws Throwable {
      this.doFilter0(ctx, chain);
      if (!ctx.getHandled()) {
         chain.doFilter(ctx);
      }

   }

   protected void doFilter0(Context ctx, FilterChain chain) throws Throwable {
      if (!ctx.getHandled()) {
         String origin = ctx.header("Origin");
         if (!Utils.isEmpty(origin)) {
            ctx.headerSet("Access-Control-Max-Age", String.valueOf(this.maxAge));
            String requestMethod;
            if (Utils.isNotEmpty(this.allowedHeaders)) {
               if ("*".equals(this.allowedHeaders)) {
                  requestMethod = ctx.header("Access-Control-Request-Headers");
                  if (Utils.isNotEmpty(requestMethod)) {
                     ctx.headerSet("Access-Control-Allow-Headers", requestMethod);
                  }
               } else {
                  ctx.headerSet("Access-Control-Allow-Headers", this.allowedHeaders);
               }
            }

            if (Utils.isNotEmpty(this.allowedMethods)) {
               if ("*".equals(this.allowedMethods)) {
                  requestMethod = ctx.header("Access-Control-Request-Method");
                  if (Utils.isEmpty(requestMethod)) {
                     requestMethod = ctx.method();
                  }

                  if (Utils.isNotEmpty(requestMethod)) {
                     ctx.headerSet("Access-Control-Allow-Methods", requestMethod);
                  }
               } else {
                  ctx.headerSet("Access-Control-Allow-Methods", this.allowedMethods);
               }
            }

            if (Utils.isNotEmpty(this.allowedOrigins) && ("*".equals(this.allowedOrigins) || this.allowedOrigins.contains(origin))) {
               ctx.headerSet("Access-Control-Allow-Origin", origin);
            }

            if (this.allowCredentials) {
               ctx.headerSet("Access-Control-Allow-Credentials", "true");
            }

            if (Utils.isNotEmpty(this.exposedHeaders)) {
               ctx.headerSet("Access-Control-Expose-Headers", this.exposedHeaders);
            }

            if (MethodType.OPTIONS.name.equalsIgnoreCase(ctx.method())) {
               ctx.setHandled(true);
            }

         }
      }
   }
}
