/*     */ package cn.hutool.log.dialect.logtube;
/*     */ 
/*     */ import cn.hutool.core.exceptions.ExceptionUtil;
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import cn.hutool.log.AbstractLog;
/*     */ import cn.hutool.log.level.Level;
/*     */ import io.github.logtube.Logtube;
/*     */ import io.github.logtube.core.IEventLogger;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class LogTubeLog
/*     */   extends AbstractLog
/*     */ {
/*     */   private final IEventLogger logger;
/*     */   
/*     */   public LogTubeLog(IEventLogger logger) {
/*  22 */     this.logger = logger;
/*     */   }
/*     */   
/*     */   public LogTubeLog(Class<?> clazz) {
/*  26 */     this((null == clazz) ? "null" : clazz.getName());
/*     */   }
/*     */   
/*     */   public LogTubeLog(String name) {
/*  30 */     this(Logtube.getLogger(name));
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public String getName() {
/*  36 */     return this.logger.getName();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isTraceEnabled() {
/*  42 */     return this.logger.isTraceEnabled();
/*     */   }
/*     */ 
/*     */   
/*     */   public void trace(String fqcn, Throwable t, String format, Object... arguments) {
/*  47 */     log(fqcn, Level.TRACE, t, format, arguments);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isDebugEnabled() {
/*  53 */     return this.logger.isDebugEnabled();
/*     */   }
/*     */ 
/*     */   
/*     */   public void debug(String fqcn, Throwable t, String format, Object... arguments) {
/*  58 */     log(fqcn, Level.DEBUG, t, format, arguments);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isInfoEnabled() {
/*  64 */     return this.logger.isInfoEnabled();
/*     */   }
/*     */ 
/*     */   
/*     */   public void info(String fqcn, Throwable t, String format, Object... arguments) {
/*  69 */     log(fqcn, Level.INFO, t, format, arguments);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isWarnEnabled() {
/*  75 */     return this.logger.isWarnEnabled();
/*     */   }
/*     */ 
/*     */   
/*     */   public void warn(String fqcn, Throwable t, String format, Object... arguments) {
/*  80 */     log(fqcn, Level.WARN, t, format, arguments);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isErrorEnabled() {
/*  86 */     return this.logger.isErrorEnabled();
/*     */   }
/*     */ 
/*     */   
/*     */   public void error(String fqcn, Throwable t, String format, Object... arguments) {
/*  91 */     log(fqcn, Level.ERROR, t, format, arguments);
/*     */   }
/*     */ 
/*     */   
/*     */   public void log(String fqcn, Level level, Throwable t, String format, Object... arguments) {
/*  96 */     String topic = level.name().toLowerCase();
/*  97 */     this.logger.topic(topic)
/*  98 */       .xStackTraceElement(ExceptionUtil.getStackElement(6), null)
/*  99 */       .message(StrUtil.format(format, arguments))
/* 100 */       .xException(t)
/* 101 */       .commit();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\log\dialect\logtube\LogTubeLog.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */