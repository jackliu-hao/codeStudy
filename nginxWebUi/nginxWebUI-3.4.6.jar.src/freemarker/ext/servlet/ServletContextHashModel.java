/*    */ package freemarker.ext.servlet;
/*    */ 
/*    */ import freemarker.template.ObjectWrapper;
/*    */ import freemarker.template.TemplateHashModel;
/*    */ import freemarker.template.TemplateModel;
/*    */ import freemarker.template.TemplateModelException;
/*    */ import javax.servlet.GenericServlet;
/*    */ import javax.servlet.ServletContext;
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
/*    */ 
/*    */ 
/*    */ public final class ServletContextHashModel
/*    */   implements TemplateHashModel
/*    */ {
/*    */   private final GenericServlet servlet;
/*    */   private final ServletContext servletctx;
/*    */   private final ObjectWrapper wrapper;
/*    */   
/*    */   public ServletContextHashModel(GenericServlet servlet, ObjectWrapper wrapper) {
/* 40 */     this.servlet = servlet;
/* 41 */     this.servletctx = servlet.getServletContext();
/* 42 */     this.wrapper = wrapper;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   @Deprecated
/*    */   public ServletContextHashModel(ServletContext servletctx, ObjectWrapper wrapper) {
/* 52 */     this.servlet = null;
/* 53 */     this.servletctx = servletctx;
/* 54 */     this.wrapper = wrapper;
/*    */   }
/*    */ 
/*    */   
/*    */   public TemplateModel get(String key) throws TemplateModelException {
/* 59 */     return this.wrapper.wrap(this.servletctx.getAttribute(key));
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean isEmpty() {
/* 64 */     return !this.servletctx.getAttributeNames().hasMoreElements();
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public GenericServlet getServlet() {
/* 72 */     return this.servlet;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\ext\servlet\ServletContextHashModel.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */