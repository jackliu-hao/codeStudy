/*     */ package cn.hutool.log.dialect.log4j;
/*     */ 
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import cn.hutool.log.AbstractLog;
/*     */ import cn.hutool.log.level.Level;
/*     */ import org.apache.log4j.Level;
/*     */ import org.apache.log4j.Logger;
/*     */ import org.apache.log4j.Priority;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Log4jLog
/*     */   extends AbstractLog
/*     */ {
/*     */   private static final long serialVersionUID = -6843151523380063975L;
/*     */   private final Logger logger;
/*     */   
/*     */   public Log4jLog(Logger logger) {
/*  22 */     this.logger = logger;
/*     */   }
/*     */   
/*     */   public Log4jLog(Class<?> clazz) {
/*  26 */     this((null == clazz) ? "null" : clazz.getName());
/*     */   }
/*     */   
/*     */   public Log4jLog(String name) {
/*  30 */     this(Logger.getLogger(name));
/*     */   }
/*     */ 
/*     */   
/*     */   public String getName() {
/*  35 */     return this.logger.getName();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isTraceEnabled() {
/*  41 */     return this.logger.isTraceEnabled();
/*     */   }
/*     */ 
/*     */   
/*     */   public void trace(String fqcn, Throwable t, String format, Object... arguments) {
/*  46 */     log(fqcn, Level.TRACE, t, format, arguments);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isDebugEnabled() {
/*  52 */     return this.logger.isDebugEnabled();
/*     */   }
/*     */ 
/*     */   
/*     */   public void debug(String fqcn, Throwable t, String format, Object... arguments) {
/*  57 */     log(fqcn, Level.DEBUG, t, format, arguments);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isInfoEnabled() {
/*  62 */     return this.logger.isInfoEnabled();
/*     */   }
/*     */ 
/*     */   
/*     */   public void info(String fqcn, Throwable t, String format, Object... arguments) {
/*  67 */     log(fqcn, Level.INFO, t, format, arguments);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isWarnEnabled() {
/*  73 */     return this.logger.isEnabledFor((Priority)Level.WARN);
/*     */   }
/*     */ 
/*     */   
/*     */   public void warn(String fqcn, Throwable t, String format, Object... arguments) {
/*  78 */     log(fqcn, Level.WARN, t, format, arguments);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isErrorEnabled() {
/*  84 */     return this.logger.isEnabledFor((Priority)Level.ERROR);
/*     */   }
/*     */ 
/*     */   
/*     */   public void error(String fqcn, Throwable t, String format, Object... arguments) {
/*  89 */     log(fqcn, Level.ERROR, t, format, arguments);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void log(String fqcn, Level level, Throwable t, String format, Object... arguments) {
/*     */     Level log4jLevel;
/*  96 */     switch (level) {
/*     */       case TRACE:
/*  98 */         log4jLevel = Level.TRACE;
/*     */         break;
/*     */       case DEBUG:
/* 101 */         log4jLevel = Level.DEBUG;
/*     */         break;
/*     */       case INFO:
/* 104 */         log4jLevel = Level.INFO;
/*     */         break;
/*     */       case WARN:
/* 107 */         log4jLevel = Level.WARN;
/*     */         break;
/*     */       case ERROR:
/* 110 */         log4jLevel = Level.ERROR;
/*     */         break;
/*     */       default:
/* 113 */         throw new Error(StrUtil.format("Can not identify level: {}", new Object[] { level }));
/*     */     } 
/*     */     
/* 116 */     if (this.logger.isEnabledFor((Priority)log4jLevel))
/* 117 */       this.logger.log(fqcn, (Priority)log4jLevel, StrUtil.format(format, arguments), t); 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\log\dialect\log4j\Log4jLog.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */