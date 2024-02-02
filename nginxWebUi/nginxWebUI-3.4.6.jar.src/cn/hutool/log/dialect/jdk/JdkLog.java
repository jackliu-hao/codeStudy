/*     */ package cn.hutool.log.dialect.jdk;
/*     */ 
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import cn.hutool.log.AbstractLog;
/*     */ import cn.hutool.log.level.Level;
/*     */ import java.util.logging.Level;
/*     */ import java.util.logging.LogRecord;
/*     */ import java.util.logging.Logger;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class JdkLog
/*     */   extends AbstractLog
/*     */ {
/*     */   private static final long serialVersionUID = -6843151523380063975L;
/*     */   private final transient Logger logger;
/*     */   
/*     */   public JdkLog(Logger logger) {
/*  23 */     this.logger = logger;
/*     */   }
/*     */   
/*     */   public JdkLog(Class<?> clazz) {
/*  27 */     this((null == clazz) ? "null" : clazz.getName());
/*     */   }
/*     */   
/*     */   public JdkLog(String name) {
/*  31 */     this(Logger.getLogger(name));
/*     */   }
/*     */ 
/*     */   
/*     */   public String getName() {
/*  36 */     return this.logger.getName();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isTraceEnabled() {
/*  42 */     return this.logger.isLoggable(Level.FINEST);
/*     */   }
/*     */ 
/*     */   
/*     */   public void trace(String fqcn, Throwable t, String format, Object... arguments) {
/*  47 */     logIfEnabled(fqcn, Level.FINEST, t, format, arguments);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isDebugEnabled() {
/*  53 */     return this.logger.isLoggable(Level.FINE);
/*     */   }
/*     */ 
/*     */   
/*     */   public void debug(String fqcn, Throwable t, String format, Object... arguments) {
/*  58 */     logIfEnabled(fqcn, Level.FINE, t, format, arguments);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isInfoEnabled() {
/*  64 */     return this.logger.isLoggable(Level.INFO);
/*     */   }
/*     */ 
/*     */   
/*     */   public void info(String fqcn, Throwable t, String format, Object... arguments) {
/*  69 */     logIfEnabled(fqcn, Level.INFO, t, format, arguments);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isWarnEnabled() {
/*  75 */     return this.logger.isLoggable(Level.WARNING);
/*     */   }
/*     */ 
/*     */   
/*     */   public void warn(String fqcn, Throwable t, String format, Object... arguments) {
/*  80 */     logIfEnabled(fqcn, Level.WARNING, t, format, arguments);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isErrorEnabled() {
/*  86 */     return this.logger.isLoggable(Level.SEVERE);
/*     */   }
/*     */ 
/*     */   
/*     */   public void error(String fqcn, Throwable t, String format, Object... arguments) {
/*  91 */     logIfEnabled(fqcn, Level.SEVERE, t, format, arguments);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void log(String fqcn, Level level, Throwable t, String format, Object... arguments) {
/*     */     Level jdkLevel;
/*  98 */     switch (level) {
/*     */       case TRACE:
/* 100 */         jdkLevel = Level.FINEST;
/*     */         break;
/*     */       case DEBUG:
/* 103 */         jdkLevel = Level.FINE;
/*     */         break;
/*     */       case INFO:
/* 106 */         jdkLevel = Level.INFO;
/*     */         break;
/*     */       case WARN:
/* 109 */         jdkLevel = Level.WARNING;
/*     */         break;
/*     */       case ERROR:
/* 112 */         jdkLevel = Level.SEVERE;
/*     */         break;
/*     */       default:
/* 115 */         throw new Error(StrUtil.format("Can not identify level: {}", new Object[] { level }));
/*     */     } 
/* 117 */     logIfEnabled(fqcn, jdkLevel, t, format, arguments);
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
/*     */   private void logIfEnabled(String callerFQCN, Level level, Throwable throwable, String format, Object[] arguments) {
/* 131 */     if (this.logger.isLoggable(level)) {
/* 132 */       LogRecord record = new LogRecord(level, StrUtil.format(format, arguments));
/* 133 */       record.setLoggerName(getName());
/* 134 */       record.setThrown(throwable);
/* 135 */       fillCallerData(callerFQCN, record);
/* 136 */       this.logger.log(record);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static void fillCallerData(String callerFQCN, LogRecord record) {
/* 146 */     StackTraceElement[] steArray = Thread.currentThread().getStackTrace();
/*     */     
/* 148 */     int found = -1;
/*     */     
/* 150 */     for (int i = steArray.length - 2; i > -1; i--) {
/*     */       
/* 152 */       String className = steArray[i].getClassName();
/* 153 */       if (callerFQCN.equals(className)) {
/* 154 */         found = i;
/*     */         
/*     */         break;
/*     */       } 
/*     */     } 
/* 159 */     if (found > -1) {
/* 160 */       StackTraceElement ste = steArray[found + 1];
/* 161 */       record.setSourceClassName(ste.getClassName());
/* 162 */       record.setSourceMethodName(ste.getMethodName());
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\log\dialect\jdk\JdkLog.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */