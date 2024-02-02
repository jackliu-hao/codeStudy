/*     */ package freemarker.ext.jsp;
/*     */ 
/*     */ import freemarker.log.Logger;
/*     */ import freemarker.template.utility.ClassUtil;
/*     */ import java.util.Iterator;
/*     */ import java.util.LinkedList;
/*     */ import javax.el.ArrayELResolver;
/*     */ import javax.el.BeanELResolver;
/*     */ import javax.el.CompositeELResolver;
/*     */ import javax.el.ELContext;
/*     */ import javax.el.ELContextEvent;
/*     */ import javax.el.ELContextListener;
/*     */ import javax.el.ELResolver;
/*     */ import javax.el.ExpressionFactory;
/*     */ import javax.el.FunctionMapper;
/*     */ import javax.el.ListELResolver;
/*     */ import javax.el.MapELResolver;
/*     */ import javax.el.ResourceBundleELResolver;
/*     */ import javax.el.ValueExpression;
/*     */ import javax.el.VariableMapper;
/*     */ import javax.servlet.jsp.JspApplicationContext;
/*     */ import javax.servlet.jsp.el.ImplicitObjectELResolver;
/*     */ import javax.servlet.jsp.el.ScopedAttributeELResolver;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ class FreeMarkerJspApplicationContext
/*     */   implements JspApplicationContext
/*     */ {
/*     */   FreeMarkerJspApplicationContext() {
/*  52 */     this.listeners = new LinkedList();
/*  53 */     this.elResolver = new CompositeELResolver();
/*  54 */     this.additionalResolvers = new CompositeELResolver();
/*     */     
/*  56 */     this.elResolver.add((ELResolver)new ImplicitObjectELResolver());
/*  57 */     this.elResolver.add((ELResolver)this.additionalResolvers);
/*  58 */     this.elResolver.add((ELResolver)new MapELResolver());
/*  59 */     this.elResolver.add((ELResolver)new ResourceBundleELResolver());
/*  60 */     this.elResolver.add((ELResolver)new ListELResolver());
/*  61 */     this.elResolver.add((ELResolver)new ArrayELResolver());
/*  62 */     this.elResolver.add((ELResolver)new BeanELResolver());
/*  63 */     this.elResolver.add((ELResolver)new ScopedAttributeELResolver());
/*     */   }
/*     */   private static final Logger LOG = Logger.getLogger("freemarker.jsp"); private static final ExpressionFactory expressionFactoryImpl = findExpressionFactoryImplementation();
/*     */   
/*     */   public void addELContextListener(ELContextListener listener) {
/*  68 */     synchronized (this.listeners) {
/*  69 */       this.listeners.addLast(listener);
/*     */     } 
/*     */   }
/*     */   private final LinkedList listeners; private final CompositeELResolver elResolver; private final CompositeELResolver additionalResolvers;
/*     */   private static ExpressionFactory findExpressionFactoryImplementation() {
/*  74 */     ExpressionFactory ef = tryExpressionFactoryImplementation("com.sun");
/*  75 */     if (ef == null) {
/*  76 */       ef = tryExpressionFactoryImplementation("org.apache");
/*  77 */       if (ef == null) {
/*  78 */         LOG.warn("Could not find any implementation for " + ExpressionFactory.class
/*  79 */             .getName());
/*     */       }
/*     */     } 
/*  82 */     return ef;
/*     */   }
/*     */   
/*     */   private static ExpressionFactory tryExpressionFactoryImplementation(String packagePrefix) {
/*  86 */     String className = packagePrefix + ".el.ExpressionFactoryImpl";
/*     */     
/*  88 */     try { Class<?> cl = ClassUtil.forName(className);
/*  89 */       if (ExpressionFactory.class.isAssignableFrom(cl)) {
/*  90 */         LOG.info("Using " + className + " as implementation of " + ExpressionFactory.class
/*  91 */             .getName());
/*  92 */         return (ExpressionFactory)cl.newInstance();
/*     */       } 
/*  94 */       LOG.warn("Class " + className + " does not implement " + ExpressionFactory.class
/*  95 */           .getName()); }
/*  96 */     catch (ClassNotFoundException classNotFoundException) {  }
/*  97 */     catch (Exception e)
/*  98 */     { LOG.error("Failed to instantiate " + className, e); }
/*     */     
/* 100 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public void addELResolver(ELResolver resolver) {
/* 105 */     this.additionalResolvers.add(resolver);
/*     */   }
/*     */ 
/*     */   
/*     */   public ExpressionFactory getExpressionFactory() {
/* 110 */     return expressionFactoryImpl;
/*     */   }
/*     */   
/*     */   ELContext createNewELContext(FreeMarkerPageContext pageCtx) {
/* 114 */     ELContext ctx = new FreeMarkerELContext(pageCtx);
/* 115 */     ELContextEvent event = new ELContextEvent(ctx);
/* 116 */     synchronized (this.listeners) {
/* 117 */       for (Iterator<ELContextListener> iter = this.listeners.iterator(); iter.hasNext(); ) {
/* 118 */         ELContextListener l = iter.next();
/* 119 */         l.contextCreated(event);
/*     */       } 
/*     */     } 
/* 122 */     return ctx;
/*     */   }
/*     */   
/*     */   private class FreeMarkerELContext extends ELContext {
/*     */     private final FreeMarkerPageContext pageCtx;
/*     */     
/*     */     FreeMarkerELContext(FreeMarkerPageContext pageCtx) {
/* 129 */       this.pageCtx = pageCtx;
/*     */     }
/*     */ 
/*     */     
/*     */     public ELResolver getELResolver() {
/* 134 */       return (ELResolver)FreeMarkerJspApplicationContext.this.elResolver;
/*     */     }
/*     */ 
/*     */     
/*     */     public FunctionMapper getFunctionMapper() {
/* 139 */       return null;
/*     */     }
/*     */ 
/*     */     
/*     */     public VariableMapper getVariableMapper() {
/* 144 */       return new VariableMapper()
/*     */         {
/*     */           public ValueExpression resolveVariable(String name) {
/* 147 */             Object obj = FreeMarkerJspApplicationContext.FreeMarkerELContext.this.pageCtx.findAttribute(name);
/* 148 */             if (obj == null) {
/* 149 */               return null;
/*     */             }
/* 151 */             return FreeMarkerJspApplicationContext.expressionFactoryImpl.createValueExpression(obj, obj
/* 152 */                 .getClass());
/*     */           }
/*     */ 
/*     */ 
/*     */           
/*     */           public ValueExpression setVariable(String name, ValueExpression value) {
/* 158 */             ValueExpression prev = resolveVariable(name);
/* 159 */             FreeMarkerJspApplicationContext.FreeMarkerELContext.this.pageCtx.setAttribute(name, value.getValue(FreeMarkerJspApplicationContext.FreeMarkerELContext.this));
/*     */             
/* 161 */             return prev;
/*     */           }
/*     */         };
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\ext\jsp\FreeMarkerJspApplicationContext.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */