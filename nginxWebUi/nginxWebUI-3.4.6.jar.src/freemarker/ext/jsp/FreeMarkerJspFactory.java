/*    */ package freemarker.ext.jsp;
/*    */ 
/*    */ import javax.servlet.Servlet;
/*    */ import javax.servlet.ServletRequest;
/*    */ import javax.servlet.ServletResponse;
/*    */ import javax.servlet.jsp.JspEngineInfo;
/*    */ import javax.servlet.jsp.JspFactory;
/*    */ import javax.servlet.jsp.PageContext;
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
/*    */ 
/*    */ abstract class FreeMarkerJspFactory
/*    */   extends JspFactory
/*    */ {
/*    */   protected abstract String getSpecificationVersion();
/*    */   
/*    */   public JspEngineInfo getEngineInfo() {
/* 36 */     return new JspEngineInfo()
/*    */       {
/*    */         public String getSpecificationVersion() {
/* 39 */           return FreeMarkerJspFactory.this.getSpecificationVersion();
/*    */         }
/*    */       };
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public PageContext getPageContext(Servlet servlet, ServletRequest request, ServletResponse response, String errorPageURL, boolean needsSession, int bufferSize, boolean autoFlush) {
/* 52 */     throw new UnsupportedOperationException();
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void releasePageContext(PageContext ctx) {
/* 61 */     throw new UnsupportedOperationException();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\ext\jsp\FreeMarkerJspFactory.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */