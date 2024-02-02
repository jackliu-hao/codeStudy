/*    */ package ch.qos.logback.classic.util;
/*    */ 
/*    */ import ch.qos.logback.classic.LoggerContext;
/*    */ import ch.qos.logback.core.Context;
/*    */ import ch.qos.logback.core.spi.ContextAwareBase;
/*    */ import ch.qos.logback.core.status.ErrorStatus;
/*    */ import ch.qos.logback.core.status.InfoStatus;
/*    */ import ch.qos.logback.core.status.Status;
/*    */ import org.slf4j.ILoggerFactory;
/*    */ import org.slf4j.LoggerFactory;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class StatusViaSLF4JLoggerFactory
/*    */ {
/*    */   public static void addInfo(String msg, Object o) {
/* 20 */     addStatus((Status)new InfoStatus(msg, o));
/*    */   }
/*    */   
/*    */   public static void addError(String msg, Object o) {
/* 24 */     addStatus((Status)new ErrorStatus(msg, o));
/*    */   }
/*    */   
/*    */   public static void addError(String msg, Object o, Throwable t) {
/* 28 */     addStatus((Status)new ErrorStatus(msg, o, t));
/*    */   }
/*    */   
/*    */   public static void addStatus(Status status) {
/* 32 */     ILoggerFactory iLoggerFactory = LoggerFactory.getILoggerFactory();
/* 33 */     if (iLoggerFactory instanceof LoggerContext) {
/* 34 */       ContextAwareBase contextAwareBase = new ContextAwareBase();
/* 35 */       LoggerContext loggerContext = (LoggerContext)iLoggerFactory;
/* 36 */       contextAwareBase.setContext((Context)loggerContext);
/* 37 */       contextAwareBase.addStatus(status);
/*    */     } 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\classi\\util\StatusViaSLF4JLoggerFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */