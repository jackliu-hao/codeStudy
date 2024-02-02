package org.noear.solon.web.cors;

import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Handler;
import org.noear.solon.core.handle.MethodType;
import org.noear.solon.web.cors.annotation.CrossOrigin;

public class CrossHandler implements Handler {
   protected int maxAge = 3600;
   protected String allowedOrigins = "*";
   protected String allowedMethods = "*";
   protected String allowedHeaders = "*";
   protected boolean allowCredentials = true;
   protected String exposedHeaders;

   public CrossHandler() {
   }

   public CrossHandler(CrossOrigin anno) {
      this.maxAge(anno.maxAge());
      this.allowedOrigins(Solon.cfg().getByParse(anno.origins()));
      this.allowCredentials(anno.credentials());
   }

   public CrossHandler maxAge(int maxAge) {
      if (maxAge >= 0) {
         this.maxAge = maxAge;
      }

      return this;
   }

   public CrossHandler allowedOrigins(String allowOrigin) {
      if (allowOrigin != null) {
         this.allowedOrigins = allowOrigin;
      }

      return this;
   }

   public CrossHandler allowedMethods(String allowMethods) {
      this.allowedMethods = allowMethods;
      return this;
   }

   public CrossHandler allowedHeaders(String allowHeaders) {
      this.allowedHeaders = allowHeaders;
      return this;
   }

   public CrossHandler allowCredentials(boolean allowCredentials) {
      this.allowCredentials = allowCredentials;
      return this;
   }

   public CrossHandler exposedHeaders(String exposeHeaders) {
      this.exposedHeaders = exposeHeaders;
      return this;
   }

   public void handle(Context ctx) throws Throwable {
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
