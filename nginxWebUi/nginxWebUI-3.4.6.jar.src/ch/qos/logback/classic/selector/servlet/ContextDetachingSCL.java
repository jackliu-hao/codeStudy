/*    */ package ch.qos.logback.classic.selector.servlet;
/*    */ 
/*    */ import ch.qos.logback.classic.Logger;
/*    */ import ch.qos.logback.classic.LoggerContext;
/*    */ import ch.qos.logback.classic.selector.ContextSelector;
/*    */ import ch.qos.logback.classic.util.ContextSelectorStaticBinder;
/*    */ import ch.qos.logback.core.util.JNDIUtil;
/*    */ import ch.qos.logback.core.util.OptionHelper;
/*    */ import javax.naming.Context;
/*    */ import javax.naming.NamingException;
/*    */ import javax.servlet.ServletContextEvent;
/*    */ import javax.servlet.ServletContextListener;
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
/*    */ public class ContextDetachingSCL
/*    */   implements ServletContextListener
/*    */ {
/*    */   public void contextInitialized(ServletContextEvent arg0) {}
/*    */   
/*    */   public void contextDestroyed(ServletContextEvent servletContextEvent) {
/* 39 */     String loggerContextName = null;
/*    */     
/*    */     try {
/* 42 */       Context ctx = JNDIUtil.getInitialContext();
/* 43 */       loggerContextName = JNDIUtil.lookupString(ctx, "java:comp/env/logback/context-name");
/* 44 */     } catch (NamingException namingException) {}
/*    */ 
/*    */     
/* 47 */     if (!OptionHelper.isEmpty(loggerContextName)) {
/* 48 */       System.out.println("About to detach context named " + loggerContextName);
/*    */       
/* 50 */       ContextSelector selector = ContextSelectorStaticBinder.getSingleton().getContextSelector();
/* 51 */       if (selector == null) {
/* 52 */         System.out.println("Selector is null, cannot detach context. Skipping.");
/*    */         return;
/*    */       } 
/* 55 */       LoggerContext context = selector.getLoggerContext(loggerContextName);
/* 56 */       if (context != null) {
/* 57 */         Logger logger = context.getLogger("ROOT");
/* 58 */         logger.warn("Stopping logger context " + loggerContextName);
/* 59 */         selector.detachLoggerContext(loggerContextName);
/*    */         
/* 61 */         context.stop();
/*    */       } else {
/* 63 */         System.out.println("No context named " + loggerContextName + " was found.");
/*    */       } 
/*    */     } 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\classic\selector\servlet\ContextDetachingSCL.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */