/*    */ package ch.qos.logback.classic.util;
/*    */ 
/*    */ import ch.qos.logback.classic.LoggerContext;
/*    */ import ch.qos.logback.classic.selector.ContextJNDISelector;
/*    */ import ch.qos.logback.classic.selector.ContextSelector;
/*    */ import ch.qos.logback.classic.selector.DefaultContextSelector;
/*    */ import ch.qos.logback.core.util.Loader;
/*    */ import ch.qos.logback.core.util.OptionHelper;
/*    */ import java.lang.reflect.Constructor;
/*    */ import java.lang.reflect.InvocationTargetException;
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
/*    */ public class ContextSelectorStaticBinder
/*    */ {
/* 35 */   static ContextSelectorStaticBinder singleton = new ContextSelectorStaticBinder();
/*    */   
/*    */   ContextSelector contextSelector;
/*    */   Object key;
/*    */   
/*    */   public static ContextSelectorStaticBinder getSingleton() {
/* 41 */     return singleton;
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
/*    */   public void init(LoggerContext defaultLoggerContext, Object key) throws ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
/* 56 */     if (this.key == null) {
/* 57 */       this.key = key;
/* 58 */     } else if (this.key != key) {
/* 59 */       throw new IllegalAccessException("Only certain classes can access this method.");
/*    */     } 
/*    */     
/* 62 */     String contextSelectorStr = OptionHelper.getSystemProperty("logback.ContextSelector");
/* 63 */     if (contextSelectorStr == null) {
/* 64 */       this.contextSelector = (ContextSelector)new DefaultContextSelector(defaultLoggerContext);
/* 65 */     } else if (contextSelectorStr.equals("JNDI")) {
/*    */       
/* 67 */       this.contextSelector = (ContextSelector)new ContextJNDISelector(defaultLoggerContext);
/*    */     } else {
/* 69 */       this.contextSelector = dynamicalContextSelector(defaultLoggerContext, contextSelectorStr);
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   static ContextSelector dynamicalContextSelector(LoggerContext defaultLoggerContext, String contextSelectorStr) throws ClassNotFoundException, SecurityException, NoSuchMethodException, IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException {
/* 91 */     Class<?> contextSelectorClass = Loader.loadClass(contextSelectorStr);
/* 92 */     Constructor<?> cons = contextSelectorClass.getConstructor(new Class[] { LoggerContext.class });
/* 93 */     return (ContextSelector)cons.newInstance(new Object[] { defaultLoggerContext });
/*    */   }
/*    */   
/*    */   public ContextSelector getContextSelector() {
/* 97 */     return this.contextSelector;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\classi\\util\ContextSelectorStaticBinder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */