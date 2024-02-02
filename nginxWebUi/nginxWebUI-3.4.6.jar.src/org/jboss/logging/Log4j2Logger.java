/*    */ package org.jboss.logging;
/*    */ 
/*    */ import org.apache.logging.log4j.Level;
/*    */ import org.apache.logging.log4j.LogManager;
/*    */ import org.apache.logging.log4j.Logger;
/*    */ import org.apache.logging.log4j.LoggingException;
/*    */ import org.apache.logging.log4j.message.Message;
/*    */ import org.apache.logging.log4j.message.MessageFormatMessageFactory;
/*    */ import org.apache.logging.log4j.message.StringFormattedMessage;
/*    */ import org.apache.logging.log4j.spi.AbstractLogger;
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
/*    */ final class Log4j2Logger
/*    */   extends Logger
/*    */ {
/*    */   private static final long serialVersionUID = -2507841068232627725L;
/*    */   private final AbstractLogger logger;
/*    */   private final MessageFormatMessageFactory messageFactory;
/*    */   
/*    */   Log4j2Logger(String name) {
/* 35 */     super(name);
/* 36 */     Logger logger = LogManager.getLogger(name);
/* 37 */     if (!(logger instanceof AbstractLogger)) {
/* 38 */       throw new LoggingException("The logger for [" + name + "] does not extend AbstractLogger. Actual logger: " + logger.getClass().getName());
/*    */     }
/* 40 */     this.logger = (AbstractLogger)logger;
/* 41 */     this.messageFactory = new MessageFormatMessageFactory();
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean isEnabled(Logger.Level level) {
/* 46 */     return this.logger.isEnabled(translate(level));
/*    */   }
/*    */ 
/*    */   
/*    */   protected void doLog(Logger.Level level, String loggerClassName, Object message, Object[] parameters, Throwable thrown) {
/* 51 */     Level translatedLevel = translate(level);
/* 52 */     if (this.logger.isEnabled(translatedLevel)) {
/*    */       try {
/* 54 */         this.logger.logMessage(loggerClassName, translatedLevel, null, (parameters == null || parameters.length == 0) ? this.messageFactory
/* 55 */             .newMessage(message) : this.messageFactory.newMessage(String.valueOf(message), parameters), thrown);
/*    */       }
/* 57 */       catch (Throwable throwable) {}
/*    */     }
/*    */   }
/*    */ 
/*    */   
/*    */   protected void doLogf(Logger.Level level, String loggerClassName, String format, Object[] parameters, Throwable thrown) {
/* 63 */     Level translatedLevel = translate(level);
/* 64 */     if (this.logger.isEnabled(translatedLevel)) {
/*    */       try {
/* 66 */         this.logger.logMessage(loggerClassName, translatedLevel, null, (Message)new StringFormattedMessage(format, parameters), thrown);
/* 67 */       } catch (Throwable throwable) {}
/*    */     }
/*    */   }
/*    */   
/*    */   private static Level translate(Logger.Level level) {
/* 72 */     if (level == Logger.Level.TRACE)
/* 73 */       return Level.TRACE; 
/* 74 */     if (level == Logger.Level.DEBUG) {
/* 75 */       return Level.DEBUG;
/*    */     }
/* 77 */     return infoOrHigher(level);
/*    */   }
/*    */   
/*    */   private static Level infoOrHigher(Logger.Level level) {
/* 81 */     if (level == Logger.Level.INFO)
/* 82 */       return Level.INFO; 
/* 83 */     if (level == Logger.Level.WARN)
/* 84 */       return Level.WARN; 
/* 85 */     if (level == Logger.Level.ERROR)
/* 86 */       return Level.ERROR; 
/* 87 */     if (level == Logger.Level.FATAL) {
/* 88 */       return Level.FATAL;
/*    */     }
/* 90 */     return Level.ALL;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\jboss\logging\Log4j2Logger.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */