/*    */ package ch.qos.logback.classic.jul;
/*    */ 
/*    */ import ch.qos.logback.classic.Level;
/*    */ import ch.qos.logback.classic.Logger;
/*    */ import java.util.logging.Level;
/*    */ import java.util.logging.Logger;
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
/*    */ public class JULHelper
/*    */ {
/*    */   public static final boolean isRegularNonRootLogger(Logger julLogger) {
/* 22 */     if (julLogger == null)
/* 23 */       return false; 
/* 24 */     return !julLogger.getName().equals("");
/*    */   }
/*    */   
/*    */   public static final boolean isRoot(Logger julLogger) {
/* 28 */     if (julLogger == null)
/* 29 */       return false; 
/* 30 */     return julLogger.getName().equals("");
/*    */   }
/*    */   
/*    */   public static Level asJULLevel(Level lbLevel) {
/* 34 */     if (lbLevel == null) {
/* 35 */       throw new IllegalArgumentException("Unexpected level [null]");
/*    */     }
/* 37 */     switch (lbLevel.levelInt) {
/*    */       case -2147483648:
/* 39 */         return Level.ALL;
/*    */       case 5000:
/* 41 */         return Level.FINEST;
/*    */       case 10000:
/* 43 */         return Level.FINE;
/*    */       case 20000:
/* 45 */         return Level.INFO;
/*    */       case 30000:
/* 47 */         return Level.WARNING;
/*    */       case 40000:
/* 49 */         return Level.SEVERE;
/*    */       case 2147483647:
/* 51 */         return Level.OFF;
/*    */     } 
/* 53 */     throw new IllegalArgumentException("Unexpected level [" + lbLevel + "]");
/*    */   }
/*    */ 
/*    */   
/*    */   public static String asJULLoggerName(String loggerName) {
/* 58 */     if ("ROOT".equals(loggerName)) {
/* 59 */       return "";
/*    */     }
/* 61 */     return loggerName;
/*    */   }
/*    */   
/*    */   public static Logger asJULLogger(String loggerName) {
/* 65 */     String julLoggerName = asJULLoggerName(loggerName);
/* 66 */     return Logger.getLogger(julLoggerName);
/*    */   }
/*    */   
/*    */   public static Logger asJULLogger(Logger logger) {
/* 70 */     return asJULLogger(logger.getName());
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\classic\jul\JULHelper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */