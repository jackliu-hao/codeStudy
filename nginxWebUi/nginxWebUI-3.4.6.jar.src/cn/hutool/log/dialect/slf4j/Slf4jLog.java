/*     */ package cn.hutool.log.dialect.slf4j;
/*     */ 
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import cn.hutool.log.AbstractLog;
/*     */ import cn.hutool.log.level.Level;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ import org.slf4j.spi.LocationAwareLogger;
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
/*     */ public class Slf4jLog
/*     */   extends AbstractLog
/*     */ {
/*     */   private static final long serialVersionUID = -6843151523380063975L;
/*     */   private final transient Logger logger;
/*     */   private final boolean isLocationAwareLogger;
/*     */   
/*     */   public Slf4jLog(Logger logger) {
/*  27 */     this.logger = logger;
/*  28 */     this.isLocationAwareLogger = logger instanceof LocationAwareLogger;
/*     */   }
/*     */   
/*     */   public Slf4jLog(Class<?> clazz) {
/*  32 */     this(getSlf4jLogger(clazz));
/*     */   }
/*     */   
/*     */   public Slf4jLog(String name) {
/*  36 */     this(LoggerFactory.getLogger(name));
/*     */   }
/*     */ 
/*     */   
/*     */   public String getName() {
/*  41 */     return this.logger.getName();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isTraceEnabled() {
/*  47 */     return this.logger.isTraceEnabled();
/*     */   }
/*     */ 
/*     */   
/*     */   public void trace(String fqcn, Throwable t, String format, Object... arguments) {
/*  52 */     if (isTraceEnabled()) {
/*  53 */       if (this.isLocationAwareLogger) {
/*  54 */         locationAwareLog((LocationAwareLogger)this.logger, fqcn, 0, t, format, arguments);
/*     */       } else {
/*  56 */         this.logger.trace(StrUtil.format(format, arguments), t);
/*     */       } 
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isDebugEnabled() {
/*  64 */     return this.logger.isDebugEnabled();
/*     */   }
/*     */ 
/*     */   
/*     */   public void debug(String fqcn, Throwable t, String format, Object... arguments) {
/*  69 */     if (isDebugEnabled()) {
/*  70 */       if (this.isLocationAwareLogger) {
/*  71 */         locationAwareLog((LocationAwareLogger)this.logger, fqcn, 10, t, format, arguments);
/*     */       } else {
/*  73 */         this.logger.debug(StrUtil.format(format, arguments), t);
/*     */       } 
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isInfoEnabled() {
/*  81 */     return this.logger.isInfoEnabled();
/*     */   }
/*     */ 
/*     */   
/*     */   public void info(String fqcn, Throwable t, String format, Object... arguments) {
/*  86 */     if (isInfoEnabled()) {
/*  87 */       if (this.isLocationAwareLogger) {
/*  88 */         locationAwareLog((LocationAwareLogger)this.logger, fqcn, 20, t, format, arguments);
/*     */       } else {
/*  90 */         this.logger.info(StrUtil.format(format, arguments), t);
/*     */       } 
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isWarnEnabled() {
/*  98 */     return this.logger.isWarnEnabled();
/*     */   }
/*     */ 
/*     */   
/*     */   public void warn(String fqcn, Throwable t, String format, Object... arguments) {
/* 103 */     if (isWarnEnabled()) {
/* 104 */       if (this.isLocationAwareLogger) {
/* 105 */         locationAwareLog((LocationAwareLogger)this.logger, fqcn, 30, t, format, arguments);
/*     */       } else {
/* 107 */         this.logger.warn(StrUtil.format(format, arguments), t);
/*     */       } 
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isErrorEnabled() {
/* 115 */     return this.logger.isErrorEnabled();
/*     */   }
/*     */ 
/*     */   
/*     */   public void error(String fqcn, Throwable t, String format, Object... arguments) {
/* 120 */     if (isErrorEnabled()) {
/* 121 */       if (this.isLocationAwareLogger) {
/* 122 */         locationAwareLog((LocationAwareLogger)this.logger, fqcn, 40, t, format, arguments);
/*     */       } else {
/* 124 */         this.logger.error(StrUtil.format(format, arguments), t);
/*     */       } 
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void log(String fqcn, Level level, Throwable t, String format, Object... arguments) {
/* 132 */     switch (level) {
/*     */       case TRACE:
/* 134 */         trace(fqcn, t, format, arguments);
/*     */         return;
/*     */       case DEBUG:
/* 137 */         debug(fqcn, t, format, arguments);
/*     */         return;
/*     */       case INFO:
/* 140 */         info(fqcn, t, format, arguments);
/*     */         return;
/*     */       case WARN:
/* 143 */         warn(fqcn, t, format, arguments);
/*     */         return;
/*     */       case ERROR:
/* 146 */         error(fqcn, t, format, arguments);
/*     */         return;
/*     */     } 
/* 149 */     throw new Error(StrUtil.format("Can not identify level: {}", new Object[] { level }));
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
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void locationAwareLog(LocationAwareLogger logger, String fqcn, int level_int, Throwable t, String msgTemplate, Object[] arguments) {
/* 168 */     logger.log(null, fqcn, level_int, StrUtil.format(msgTemplate, arguments), null, t);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static Logger getSlf4jLogger(Class<?> clazz) {
/* 178 */     return (null == clazz) ? LoggerFactory.getLogger("") : LoggerFactory.getLogger(clazz);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\log\dialect\slf4j\Slf4jLog.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */