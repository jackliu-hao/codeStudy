/*    */ package org.noear.solon.core.handle;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ContextPathFilter
/*    */   implements Filter
/*    */ {
/*    */   private String path;
/*    */   private boolean forced;
/*    */   
/*    */   public ContextPathFilter(String path, boolean forced) {
/* 17 */     this.path = path;
/* 18 */     this.forced = forced;
/*    */   }
/*    */   
/*    */   public ContextPathFilter(String path) {
/* 22 */     this(path, false);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public void doFilter(Context ctx, FilterChain chain) throws Throwable {
/* 28 */     if (ctx.pathNew().startsWith(this.path)) {
/* 29 */       ctx.pathNew(ctx.path().substring(this.path.length() - 1));
/*    */     }
/* 31 */     else if (this.forced) {
/*    */       return;
/*    */     } 
/*    */ 
/*    */     
/* 36 */     chain.doFilter(ctx);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\core\handle\ContextPathFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */