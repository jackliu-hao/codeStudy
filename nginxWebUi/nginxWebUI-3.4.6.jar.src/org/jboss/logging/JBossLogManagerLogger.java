/*    */ package org.jboss.logging;
/*    */ 
/*    */ import java.util.logging.Level;
/*    */ import org.jboss.logmanager.ExtLogRecord;
/*    */ import org.jboss.logmanager.Level;
/*    */ import org.jboss.logmanager.Logger;
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
/*    */ final class JBossLogManagerLogger
/*    */   extends Logger
/*    */ {
/*    */   private static final long serialVersionUID = 7429618317727584742L;
/*    */   private final Logger logger;
/*    */   
/*    */   JBossLogManagerLogger(String name, Logger logger) {
/* 30 */     super(name);
/* 31 */     this.logger = logger;
/*    */   }
/*    */   
/*    */   public boolean isEnabled(Logger.Level level) {
/* 35 */     return this.logger.isLoggable(translate(level));
/*    */   }
/*    */   
/*    */   protected void doLog(Logger.Level level, String loggerClassName, Object message, Object[] parameters, Throwable thrown) {
/* 39 */     Level translatedLevel = translate(level);
/* 40 */     if (this.logger.isLoggable(translatedLevel)) {
/* 41 */       if (parameters == null) {
/* 42 */         this.logger.log(loggerClassName, translatedLevel, String.valueOf(message), thrown);
/*    */       } else {
/* 44 */         this.logger.log(loggerClassName, translatedLevel, String.valueOf(message), ExtLogRecord.FormatStyle.MESSAGE_FORMAT, parameters, thrown);
/*    */       } 
/*    */     }
/*    */   }
/*    */   
/*    */   protected void doLogf(Logger.Level level, String loggerClassName, String format, Object[] parameters, Throwable thrown) {
/* 50 */     if (parameters == null) {
/* 51 */       this.logger.log(loggerClassName, translate(level), format, thrown);
/*    */     } else {
/* 53 */       this.logger.log(loggerClassName, translate(level), format, ExtLogRecord.FormatStyle.PRINTF, parameters, thrown);
/*    */     } 
/*    */   }
/*    */   
/*    */   private static Level translate(Logger.Level level) {
/* 58 */     if (level == Logger.Level.TRACE)
/* 59 */       return (Level)Level.TRACE; 
/* 60 */     if (level == Logger.Level.DEBUG) {
/* 61 */       return (Level)Level.DEBUG;
/*    */     }
/* 63 */     return infoOrHigher(level);
/*    */   }
/*    */   
/*    */   private static Level infoOrHigher(Logger.Level level) {
/* 67 */     if (level == Logger.Level.INFO)
/* 68 */       return (Level)Level.INFO; 
/* 69 */     if (level == Logger.Level.WARN)
/* 70 */       return (Level)Level.WARN; 
/* 71 */     if (level == Logger.Level.ERROR)
/* 72 */       return (Level)Level.ERROR; 
/* 73 */     if (level == Logger.Level.FATAL) {
/* 74 */       return (Level)Level.FATAL;
/*    */     }
/* 76 */     return Level.ALL;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\jboss\logging\JBossLogManagerLogger.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */