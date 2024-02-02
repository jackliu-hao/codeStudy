/*    */ package org.jboss.logging;
/*    */ 
/*    */ import java.text.MessageFormat;
/*    */ import org.apache.log4j.Level;
/*    */ import org.apache.log4j.Logger;
/*    */ import org.apache.log4j.Priority;
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
/*    */ final class Log4jLogger
/*    */   extends Logger
/*    */ {
/*    */   private static final long serialVersionUID = -5446154366955151335L;
/*    */   private final Logger logger;
/*    */   
/*    */   Log4jLogger(String name) {
/* 30 */     super(name);
/* 31 */     this.logger = Logger.getLogger(name);
/*    */   }
/*    */   
/*    */   public boolean isEnabled(Logger.Level level) {
/* 35 */     Level l = translate(level);
/* 36 */     return (this.logger.isEnabledFor((Priority)l) && l.isGreaterOrEqual((Priority)this.logger.getEffectiveLevel()));
/*    */   }
/*    */   
/*    */   protected void doLog(Logger.Level level, String loggerClassName, Object message, Object[] parameters, Throwable thrown) {
/* 40 */     Level translatedLevel = translate(level);
/* 41 */     if (this.logger.isEnabledFor((Priority)translatedLevel))
/* 42 */       try { this.logger.log(loggerClassName, (Priority)translatedLevel, (parameters == null || parameters.length == 0) ? message : MessageFormat.format(String.valueOf(message), parameters), thrown); }
/* 43 */       catch (Throwable throwable) {} 
/*    */   }
/*    */   
/*    */   protected void doLogf(Logger.Level level, String loggerClassName, String format, Object[] parameters, Throwable thrown) {
/* 47 */     Level translatedLevel = translate(level);
/* 48 */     if (this.logger.isEnabledFor((Priority)translatedLevel))
/* 49 */       try { this.logger.log(loggerClassName, (Priority)translatedLevel, (parameters == null) ? String.format(format, new Object[0]) : String.format(format, parameters), thrown); }
/* 50 */       catch (Throwable throwable) {} 
/*    */   }
/*    */   
/*    */   private static Level translate(Logger.Level level) {
/* 54 */     if (level == Logger.Level.TRACE)
/* 55 */       return Level.TRACE; 
/* 56 */     if (level == Logger.Level.DEBUG) {
/* 57 */       return Level.DEBUG;
/*    */     }
/* 59 */     return infoOrHigher(level);
/*    */   }
/*    */   
/*    */   private static Level infoOrHigher(Logger.Level level) {
/* 63 */     if (level == Logger.Level.INFO)
/* 64 */       return Level.INFO; 
/* 65 */     if (level == Logger.Level.WARN)
/* 66 */       return Level.WARN; 
/* 67 */     if (level == Logger.Level.ERROR)
/* 68 */       return Level.ERROR; 
/* 69 */     if (level == Logger.Level.FATAL) {
/* 70 */       return Level.FATAL;
/*    */     }
/* 72 */     return Level.ALL;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\jboss\logging\Log4jLogger.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */