/*     */ package cn.hutool.log.dialect.jboss;
/*     */ 
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import cn.hutool.log.AbstractLog;
/*     */ import cn.hutool.log.level.Level;
/*     */ import org.jboss.logging.Logger;
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
/*     */ public class JbossLog
/*     */   extends AbstractLog
/*     */ {
/*     */   private static final long serialVersionUID = -6843151523380063975L;
/*     */   private final transient Logger logger;
/*     */   
/*     */   public JbossLog(Logger logger) {
/*  27 */     this.logger = logger;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JbossLog(Class<?> clazz) {
/*  36 */     this((null == clazz) ? "null" : clazz.getName());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JbossLog(String name) {
/*  45 */     this(Logger.getLogger(name));
/*     */   }
/*     */ 
/*     */   
/*     */   public String getName() {
/*  50 */     return this.logger.getName();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isTraceEnabled() {
/*  56 */     return this.logger.isTraceEnabled();
/*     */   }
/*     */ 
/*     */   
/*     */   public void trace(String fqcn, Throwable t, String format, Object... arguments) {
/*  61 */     if (isTraceEnabled()) {
/*  62 */       this.logger.trace(fqcn, StrUtil.format(format, arguments), t);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isDebugEnabled() {
/*  69 */     return this.logger.isDebugEnabled();
/*     */   }
/*     */ 
/*     */   
/*     */   public void debug(String fqcn, Throwable t, String format, Object... arguments) {
/*  74 */     if (isDebugEnabled()) {
/*  75 */       this.logger.debug(fqcn, StrUtil.format(format, arguments), t);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isInfoEnabled() {
/*  82 */     return this.logger.isInfoEnabled();
/*     */   }
/*     */ 
/*     */   
/*     */   public void info(String fqcn, Throwable t, String format, Object... arguments) {
/*  87 */     if (isInfoEnabled()) {
/*  88 */       this.logger.info(fqcn, StrUtil.format(format, arguments), t);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isWarnEnabled() {
/*  95 */     return this.logger.isEnabled(Logger.Level.WARN);
/*     */   }
/*     */ 
/*     */   
/*     */   public void warn(String fqcn, Throwable t, String format, Object... arguments) {
/* 100 */     if (isWarnEnabled()) {
/* 101 */       this.logger.warn(fqcn, StrUtil.format(format, arguments), t);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isErrorEnabled() {
/* 108 */     return this.logger.isEnabled(Logger.Level.ERROR);
/*     */   }
/*     */ 
/*     */   
/*     */   public void error(String fqcn, Throwable t, String format, Object... arguments) {
/* 113 */     if (isErrorEnabled()) {
/* 114 */       this.logger.error(fqcn, StrUtil.format(format, arguments), t);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void log(String fqcn, Level level, Throwable t, String format, Object... arguments) {
/* 121 */     switch (level) {
/*     */       case TRACE:
/* 123 */         trace(fqcn, t, format, arguments);
/*     */         return;
/*     */       case DEBUG:
/* 126 */         debug(fqcn, t, format, arguments);
/*     */         return;
/*     */       case INFO:
/* 129 */         info(fqcn, t, format, arguments);
/*     */         return;
/*     */       case WARN:
/* 132 */         warn(fqcn, t, format, arguments);
/*     */         return;
/*     */       case ERROR:
/* 135 */         error(fqcn, t, format, arguments);
/*     */         return;
/*     */     } 
/* 138 */     throw new Error(StrUtil.format("Can not identify level: {}", new Object[] { level }));
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\log\dialect\jboss\JbossLog.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */