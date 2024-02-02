/*    */ package com.sun.activation.registries;
/*    */ 
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
/*    */ public class LogSupport
/*    */ {
/*    */   private static boolean debug = false;
/*    */   private static Logger logger;
/* 39 */   private static final Level level = Level.FINE;
/*    */   
/*    */   static {
/*    */     try {
/* 43 */       debug = Boolean.getBoolean("javax.activation.debug");
/* 44 */     } catch (Throwable t) {}
/*    */ 
/*    */     
/* 47 */     logger = Logger.getLogger("javax.activation");
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static void log(String msg) {
/* 58 */     if (debug)
/* 59 */       System.out.println(msg); 
/* 60 */     logger.log(level, msg);
/*    */   }
/*    */   
/*    */   public static void log(String msg, Throwable t) {
/* 64 */     if (debug)
/* 65 */       System.out.println(msg + "; Exception: " + t); 
/* 66 */     logger.log(level, msg, t);
/*    */   }
/*    */   
/*    */   public static boolean isLoggable() {
/* 70 */     return (debug || logger.isLoggable(level));
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\activation\registries\LogSupport.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */