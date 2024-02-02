package org.noear.solon.core.handle;

public class ContextPathFilter implements Filter {
   private String path;
   private boolean forced;

   public ContextPathFilter(String path, boolean forced) {
      this.path = path;
      this.forced = forced;
   }

   public ContextPathFilter(String path) {
      this(path, false);
   }

   public void doFilter(Context ctx, FilterChain chain) throws Throwable {
      if (ctx.pathNew().startsWith(this.path)) {
         ctx.pathNew(ctx.path().substring(this.path.length() - 1));
      } else if (this.forced) {
         return;
      }

      chain.doFilter(ctx);
   }
}
