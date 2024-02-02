/*     */ package cn.hutool.log.dialect.commons;
/*     */ 
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import cn.hutool.log.AbstractLog;
/*     */ import cn.hutool.log.level.Level;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.apache.commons.logging.LogFactory;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ApacheCommonsLog
/*     */   extends AbstractLog
/*     */ {
/*     */   private static final long serialVersionUID = -6843151523380063975L;
/*     */   private final transient Log logger;
/*     */   private final String name;
/*     */   
/*     */   public ApacheCommonsLog(Log logger, String name) {
/*  23 */     this.logger = logger;
/*  24 */     this.name = name;
/*     */   }
/*     */   
/*     */   public ApacheCommonsLog(Class<?> clazz) {
/*  28 */     this(LogFactory.getLog(clazz), (null == clazz) ? "null" : clazz.getName());
/*     */   }
/*     */   
/*     */   public ApacheCommonsLog(String name) {
/*  32 */     this(LogFactory.getLog(name), name);
/*     */   }
/*     */ 
/*     */   
/*     */   public String getName() {
/*  37 */     return this.name;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isTraceEnabled() {
/*  43 */     return this.logger.isTraceEnabled();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void trace(String fqcn, Throwable t, String format, Object... arguments) {
/*  49 */     if (isTraceEnabled()) {
/*  50 */       this.logger.trace(StrUtil.format(format, arguments), t);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isDebugEnabled() {
/*  57 */     return this.logger.isDebugEnabled();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void debug(String fqcn, Throwable t, String format, Object... arguments) {
/*  63 */     if (isDebugEnabled()) {
/*  64 */       this.logger.debug(StrUtil.format(format, arguments), t);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isInfoEnabled() {
/*  71 */     return this.logger.isInfoEnabled();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void info(String fqcn, Throwable t, String format, Object... arguments) {
/*  77 */     if (isInfoEnabled()) {
/*  78 */       this.logger.info(StrUtil.format(format, arguments), t);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isWarnEnabled() {
/*  85 */     return this.logger.isWarnEnabled();
/*     */   }
/*     */ 
/*     */   
/*     */   public void warn(String format, Object... arguments) {
/*  90 */     if (isWarnEnabled()) {
/*  91 */       this.logger.warn(StrUtil.format(format, arguments));
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void warn(Throwable t, String format, Object... arguments) {}
/*     */ 
/*     */ 
/*     */   
/*     */   public void warn(String fqcn, Throwable t, String format, Object... arguments) {
/* 102 */     if (isWarnEnabled()) {
/* 103 */       this.logger.warn(StrUtil.format(format, arguments), t);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isErrorEnabled() {
/* 110 */     return this.logger.isErrorEnabled();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void error(String fqcn, Throwable t, String format, Object... arguments) {
/* 116 */     if (isErrorEnabled()) {
/* 117 */       this.logger.warn(StrUtil.format(format, arguments), t);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void log(String fqcn, Level level, Throwable t, String format, Object... arguments) {
/* 125 */     switch (level) {
/*     */       case TRACE:
/* 127 */         trace(t, format, arguments);
/*     */         return;
/*     */       case DEBUG:
/* 130 */         debug(t, format, arguments);
/*     */         return;
/*     */       case INFO:
/* 133 */         info(t, format, arguments);
/*     */         return;
/*     */       case WARN:
/* 136 */         warn(t, format, arguments);
/*     */         return;
/*     */       case ERROR:
/* 139 */         error(t, format, arguments);
/*     */         return;
/*     */     } 
/* 142 */     throw new Error(StrUtil.format("Can not identify level: {}", new Object[] { level }));
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\log\dialect\commons\ApacheCommonsLog.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */