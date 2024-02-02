/*     */ package cn.hutool.log.dialect.log4j2;
/*     */ 
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import cn.hutool.log.AbstractLog;
/*     */ import cn.hutool.log.level.Level;
/*     */ import org.apache.logging.log4j.Level;
/*     */ import org.apache.logging.log4j.LogManager;
/*     */ import org.apache.logging.log4j.Logger;
/*     */ import org.apache.logging.log4j.spi.AbstractLogger;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Log4j2Log
/*     */   extends AbstractLog
/*     */ {
/*     */   private static final long serialVersionUID = -6843151523380063975L;
/*     */   private final transient Logger logger;
/*     */   
/*     */   public Log4j2Log(Logger logger) {
/*  24 */     this.logger = logger;
/*     */   }
/*     */   
/*     */   public Log4j2Log(Class<?> clazz) {
/*  28 */     this(LogManager.getLogger(clazz));
/*     */   }
/*     */   
/*     */   public Log4j2Log(String name) {
/*  32 */     this(LogManager.getLogger(name));
/*     */   }
/*     */ 
/*     */   
/*     */   public String getName() {
/*  37 */     return this.logger.getName();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isTraceEnabled() {
/*  43 */     return this.logger.isTraceEnabled();
/*     */   }
/*     */ 
/*     */   
/*     */   public void trace(String fqcn, Throwable t, String format, Object... arguments) {
/*  48 */     logIfEnabled(fqcn, Level.TRACE, t, format, arguments);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isDebugEnabled() {
/*  54 */     return this.logger.isDebugEnabled();
/*     */   }
/*     */ 
/*     */   
/*     */   public void debug(String fqcn, Throwable t, String format, Object... arguments) {
/*  59 */     logIfEnabled(fqcn, Level.DEBUG, t, format, arguments);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isInfoEnabled() {
/*  65 */     return this.logger.isInfoEnabled();
/*     */   }
/*     */ 
/*     */   
/*     */   public void info(String fqcn, Throwable t, String format, Object... arguments) {
/*  70 */     logIfEnabled(fqcn, Level.INFO, t, format, arguments);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isWarnEnabled() {
/*  76 */     return this.logger.isWarnEnabled();
/*     */   }
/*     */ 
/*     */   
/*     */   public void warn(String fqcn, Throwable t, String format, Object... arguments) {
/*  81 */     logIfEnabled(fqcn, Level.WARN, t, format, arguments);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isErrorEnabled() {
/*  87 */     return this.logger.isErrorEnabled();
/*     */   }
/*     */ 
/*     */   
/*     */   public void error(String fqcn, Throwable t, String format, Object... arguments) {
/*  92 */     logIfEnabled(fqcn, Level.ERROR, t, format, arguments);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void log(String fqcn, Level level, Throwable t, String format, Object... arguments) {
/*     */     Level log4j2Level;
/*  99 */     switch (level) {
/*     */       case TRACE:
/* 101 */         log4j2Level = Level.TRACE;
/*     */         break;
/*     */       case DEBUG:
/* 104 */         log4j2Level = Level.DEBUG;
/*     */         break;
/*     */       case INFO:
/* 107 */         log4j2Level = Level.INFO;
/*     */         break;
/*     */       case WARN:
/* 110 */         log4j2Level = Level.WARN;
/*     */         break;
/*     */       case ERROR:
/* 113 */         log4j2Level = Level.ERROR;
/*     */         break;
/*     */       default:
/* 116 */         throw new Error(StrUtil.format("Can not identify level: {}", new Object[] { level }));
/*     */     } 
/* 118 */     logIfEnabled(fqcn, log4j2Level, t, format, arguments);
/*     */   }
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
/*     */   private void logIfEnabled(String fqcn, Level level, Throwable t, String msgTemplate, Object... arguments) {
/* 133 */     if (this.logger.isEnabled(level))
/* 134 */       if (this.logger instanceof AbstractLogger) {
/* 135 */         ((AbstractLogger)this.logger).logIfEnabled(fqcn, level, null, StrUtil.format(msgTemplate, arguments), t);
/*     */       } else {
/*     */         
/* 138 */         this.logger.log(level, StrUtil.format(msgTemplate, arguments), t);
/*     */       }  
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\log\dialect\log4j2\Log4j2Log.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */