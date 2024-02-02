/*    */ package freemarker.ext.jsp;
/*    */ 
/*    */ import freemarker.log.Logger;
/*    */ import java.io.IOException;
/*    */ import java.io.Writer;
/*    */ import java.util.Enumeration;
/*    */ import javax.servlet.Servlet;
/*    */ import javax.servlet.ServletConfig;
/*    */ import javax.servlet.ServletContext;
/*    */ import javax.servlet.ServletException;
/*    */ import javax.servlet.ServletRequest;
/*    */ import javax.servlet.ServletResponse;
/*    */ import javax.servlet.http.HttpSession;
/*    */ import javax.servlet.jsp.JspFactory;
/*    */ import javax.servlet.jsp.JspWriter;
/*    */ import javax.servlet.jsp.PageContext;
/*    */ import javax.servlet.jsp.el.ELException;
/*    */ import javax.servlet.jsp.el.ExpressionEvaluator;
/*    */ import javax.servlet.jsp.el.VariableResolver;
/*    */ import javax.servlet.jsp.tagext.BodyContent;
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
/*    */ public class _FreeMarkerPageContext2
/*    */   extends FreeMarkerPageContext
/*    */ {
/* 41 */   private static final Logger LOG = Logger.getLogger("freemarker.jsp");
/*    */   
/*    */   static {
/* 44 */     if (JspFactory.getDefaultFactory() == null) {
/* 45 */       JspFactory.setDefaultFactory(new FreeMarkerJspFactory2());
/*    */     }
/* 47 */     LOG.debug("Using JspFactory implementation class " + 
/* 48 */         JspFactory.getDefaultFactory().getClass().getName());
/*    */   }
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
/*    */   public ExpressionEvaluator getExpressionEvaluator() {
/*    */     try {
/* 64 */       Class<?> type = Thread.currentThread().getContextClassLoader().loadClass("org.apache.commons.el.ExpressionEvaluatorImpl");
/* 65 */       return (ExpressionEvaluator)type.newInstance();
/* 66 */     } catch (Exception e) {
/* 67 */       throw new UnsupportedOperationException("In order for the getExpressionEvaluator() method to work, you must have downloaded the apache commons-el jar and made it available in the classpath.");
/*    */     } 
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public VariableResolver getVariableResolver() {
/* 80 */     final PageContext ctx = this;
/*    */     
/* 82 */     return new VariableResolver()
/*    */       {
/*    */         public Object resolveVariable(String name) throws ELException {
/* 85 */           return ctx.findAttribute(name);
/*    */         }
/*    */       };
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void include(String path, boolean flush) throws IOException, ServletException {
/* 95 */     super.include(path);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\ext\jsp\_FreeMarkerPageContext2.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */