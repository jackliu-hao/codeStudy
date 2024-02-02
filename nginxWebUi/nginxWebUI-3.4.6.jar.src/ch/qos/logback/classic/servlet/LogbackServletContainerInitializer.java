/*    */ package ch.qos.logback.classic.servlet;
/*    */ 
/*    */ import ch.qos.logback.classic.util.StatusViaSLF4JLoggerFactory;
/*    */ import ch.qos.logback.core.util.OptionHelper;
/*    */ import java.util.EventListener;
/*    */ import java.util.Set;
/*    */ import javax.servlet.ServletContainerInitializer;
/*    */ import javax.servlet.ServletContext;
/*    */ import javax.servlet.ServletException;
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
/*    */ public class LogbackServletContainerInitializer
/*    */   implements ServletContainerInitializer
/*    */ {
/*    */   public void onStartup(Set<Class<?>> c, ServletContext ctx) throws ServletException {
/* 25 */     if (isDisabledByConfiguration(ctx)) {
/* 26 */       StatusViaSLF4JLoggerFactory.addInfo("Due to deployment instructions will NOT register an instance of " + LogbackServletContextListener.class + " to the current web-app", this);
/*    */ 
/*    */       
/*    */       return;
/*    */     } 
/*    */     
/* 32 */     StatusViaSLF4JLoggerFactory.addInfo("Adding an instance of  " + LogbackServletContextListener.class + " to the current web-app", this);
/* 33 */     LogbackServletContextListener lscl = new LogbackServletContextListener();
/* 34 */     ctx.addListener((EventListener)lscl);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   boolean isDisabledByConfiguration(ServletContext ctx) {
/* 45 */     String disableAttributeStr = null;
/* 46 */     Object disableAttribute = ctx.getInitParameter("logbackDisableServletContainerInitializer");
/* 47 */     if (disableAttribute instanceof String) {
/* 48 */       disableAttributeStr = (String)disableAttribute;
/*    */     }
/*    */     
/* 51 */     if (OptionHelper.isEmpty(disableAttributeStr)) {
/* 52 */       disableAttributeStr = OptionHelper.getSystemProperty("logbackDisableServletContainerInitializer");
/*    */     }
/*    */     
/* 55 */     if (OptionHelper.isEmpty(disableAttributeStr)) {
/* 56 */       disableAttributeStr = OptionHelper.getEnv("logbackDisableServletContainerInitializer");
/*    */     }
/*    */     
/* 59 */     if (OptionHelper.isEmpty(disableAttributeStr)) {
/* 60 */       return false;
/*    */     }
/* 62 */     return disableAttributeStr.equalsIgnoreCase("true");
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\classic\servlet\LogbackServletContainerInitializer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */