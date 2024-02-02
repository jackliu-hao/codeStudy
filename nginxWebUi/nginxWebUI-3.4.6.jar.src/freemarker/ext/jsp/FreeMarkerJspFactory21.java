/*    */ package freemarker.ext.jsp;
/*    */ 
/*    */ import javax.servlet.ServletContext;
/*    */ import javax.servlet.jsp.JspApplicationContext;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ class FreeMarkerJspFactory21
/*    */   extends FreeMarkerJspFactory
/*    */ {
/* 28 */   private static final String JSPCTX_KEY = FreeMarkerJspFactory21.class
/* 29 */     .getName() + "#jspAppContext";
/*    */ 
/*    */   
/*    */   protected String getSpecificationVersion() {
/* 33 */     return "2.1";
/*    */   }
/*    */ 
/*    */   
/*    */   public JspApplicationContext getJspApplicationContext(ServletContext ctx) {
/* 38 */     JspApplicationContext jspctx = (JspApplicationContext)ctx.getAttribute(JSPCTX_KEY);
/*    */     
/* 40 */     if (jspctx == null) {
/* 41 */       synchronized (ctx) {
/* 42 */         jspctx = (JspApplicationContext)ctx.getAttribute(JSPCTX_KEY);
/* 43 */         if (jspctx == null) {
/* 44 */           jspctx = new FreeMarkerJspApplicationContext();
/* 45 */           ctx.setAttribute(JSPCTX_KEY, jspctx);
/*    */         } 
/*    */       } 
/*    */     }
/* 49 */     return jspctx;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\ext\jsp\FreeMarkerJspFactory21.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */