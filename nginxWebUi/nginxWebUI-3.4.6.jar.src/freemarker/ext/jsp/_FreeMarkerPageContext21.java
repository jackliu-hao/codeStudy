/*     */ package freemarker.ext.jsp;
/*     */ 
/*     */ import freemarker.log.Logger;
/*     */ import freemarker.template.utility.ClassUtil;
/*     */ import java.io.IOException;
/*     */ import java.io.Writer;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedAction;
/*     */ import java.util.Enumeration;
/*     */ import javax.el.ELContext;
/*     */ import javax.servlet.Servlet;
/*     */ import javax.servlet.ServletConfig;
/*     */ import javax.servlet.ServletContext;
/*     */ import javax.servlet.ServletException;
/*     */ import javax.servlet.ServletRequest;
/*     */ import javax.servlet.ServletResponse;
/*     */ import javax.servlet.http.HttpSession;
/*     */ import javax.servlet.jsp.JspApplicationContext;
/*     */ import javax.servlet.jsp.JspContext;
/*     */ import javax.servlet.jsp.JspFactory;
/*     */ import javax.servlet.jsp.JspWriter;
/*     */ import javax.servlet.jsp.PageContext;
/*     */ import javax.servlet.jsp.el.ELException;
/*     */ import javax.servlet.jsp.el.ExpressionEvaluator;
/*     */ import javax.servlet.jsp.el.VariableResolver;
/*     */ import javax.servlet.jsp.tagext.BodyContent;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class _FreeMarkerPageContext21
/*     */   extends FreeMarkerPageContext
/*     */ {
/*  46 */   private static final Logger LOG = Logger.getLogger("freemarker.jsp"); private ELContext elContext;
/*     */   
/*     */   static {
/*  49 */     if (JspFactory.getDefaultFactory() == null) {
/*  50 */       JspFactory.setDefaultFactory(new FreeMarkerJspFactory21());
/*     */     }
/*  52 */     LOG.debug("Using JspFactory implementation class " + 
/*  53 */         JspFactory.getDefaultFactory().getClass().getName());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ExpressionEvaluator getExpressionEvaluator() {
/*     */     try {
/*  75 */       Class<?> type = ((ClassLoader)AccessController.<ClassLoader>doPrivileged(new PrivilegedAction<ClassLoader>() { public Object run() { return Thread.currentThread().getContextClassLoader(); } })).loadClass("org.apache.commons.el.ExpressionEvaluatorImpl");
/*  76 */       return (ExpressionEvaluator)type.newInstance();
/*  77 */     } catch (Exception e) {
/*  78 */       throw new UnsupportedOperationException("In order for the getExpressionEvaluator() method to work, you must have downloaded the apache commons-el jar and made it available in the classpath.");
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public VariableResolver getVariableResolver() {
/*  91 */     final PageContext ctx = this;
/*     */     
/*  93 */     return new VariableResolver()
/*     */       {
/*     */         public Object resolveVariable(String name) throws ELException {
/*  96 */           return ctx.findAttribute(name);
/*     */         }
/*     */       };
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ELContext getELContext() {
/* 105 */     if (this.elContext == null) {
/* 106 */       JspApplicationContext jspctx = JspFactory.getDefaultFactory().getJspApplicationContext(getServletContext());
/* 107 */       if (jspctx instanceof FreeMarkerJspApplicationContext) {
/* 108 */         this.elContext = ((FreeMarkerJspApplicationContext)jspctx).createNewELContext(this);
/* 109 */         this.elContext.putContext(JspContext.class, this);
/*     */       } else {
/* 111 */         throw new UnsupportedOperationException("Can not create an ELContext using a foreign JspApplicationContext (of class " + 
/*     */             
/* 113 */             ClassUtil.getShortClassNameOfObject(jspctx) + ").\nHint: The cause of this is often that you are trying to use JSTL tags/functions in FTL. In that case, know that that's not really suppored, and you are supposed to use FTL constrcuts instead, like #list instead of JSTL's forEach, etc.");
/*     */       } 
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 119 */     return this.elContext;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\ext\jsp\_FreeMarkerPageContext21.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */