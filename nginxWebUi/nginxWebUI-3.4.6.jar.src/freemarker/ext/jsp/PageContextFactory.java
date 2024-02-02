/*    */ package freemarker.ext.jsp;
/*    */ 
/*    */ import freemarker.core.Environment;
/*    */ import freemarker.template.TemplateModel;
/*    */ import freemarker.template.TemplateModelException;
/*    */ import freemarker.template.utility.UndeclaredThrowableException;
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
/*    */ 
/*    */ class PageContextFactory
/*    */ {
/* 32 */   private static final Class pageContextImpl = getPageContextImpl();
/*    */ 
/*    */   
/*    */   private static Class getPageContextImpl() {
/*    */     try {
/* 37 */       PageContext.class.getMethod("getELContext", (Class[])null);
/* 38 */       return Class.forName("freemarker.ext.jsp._FreeMarkerPageContext21");
/* 39 */     } catch (NoSuchMethodException e1) {
/*    */       try {
/* 41 */         PageContext.class.getMethod("getExpressionEvaluator", (Class[])null);
/* 42 */         return Class.forName("freemarker.ext.jsp._FreeMarkerPageContext2");
/* 43 */       } catch (NoSuchMethodException e2) {
/* 44 */         throw new IllegalStateException("Since FreeMarker 2.3.24, JSP support requires at least JSP 2.0.");
/*    */       }
/*    */     
/*    */     }
/* 48 */     catch (ClassNotFoundException e) {
/* 49 */       throw new NoClassDefFoundError(e.getMessage());
/*    */     } 
/*    */   }
/*    */   
/*    */   static FreeMarkerPageContext getCurrentPageContext() throws TemplateModelException {
/* 54 */     Environment env = Environment.getCurrentEnvironment();
/* 55 */     TemplateModel pageContextModel = env.getGlobalVariable("javax.servlet.jsp.jspPageContext");
/* 56 */     if (pageContextModel instanceof FreeMarkerPageContext) {
/* 57 */       return (FreeMarkerPageContext)pageContextModel;
/*    */     }
/*    */     
/*    */     try {
/* 61 */       FreeMarkerPageContext pageContext = pageContextImpl.newInstance();
/* 62 */       env.setGlobalVariable("javax.servlet.jsp.jspPageContext", pageContext);
/* 63 */       return pageContext;
/* 64 */     } catch (IllegalAccessException e) {
/* 65 */       throw new IllegalAccessError(e.getMessage());
/* 66 */     } catch (InstantiationException e) {
/* 67 */       throw new UndeclaredThrowableException(e);
/*    */     } 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\ext\jsp\PageContextFactory.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */