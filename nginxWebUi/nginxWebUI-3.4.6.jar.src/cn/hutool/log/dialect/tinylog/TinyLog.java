/*     */ package cn.hutool.log.dialect.tinylog;
/*     */ 
/*     */ import cn.hutool.core.util.ArrayUtil;
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import cn.hutool.log.AbstractLog;
/*     */ import cn.hutool.log.level.Level;
/*     */ import org.pmw.tinylog.Level;
/*     */ import org.pmw.tinylog.LogEntryForwarder;
/*     */ import org.pmw.tinylog.Logger;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class TinyLog
/*     */   extends AbstractLog
/*     */ {
/*     */   private static final long serialVersionUID = -4848042277045993735L;
/*     */   private static final int DEPTH = 4;
/*     */   private final int level;
/*     */   private final String name;
/*     */   
/*     */   public TinyLog(Class<?> clazz) {
/*  28 */     this((null == clazz) ? "null" : clazz.getName());
/*     */   }
/*     */   
/*     */   public TinyLog(String name) {
/*  32 */     this.name = name;
/*  33 */     this.level = Logger.getLevel(name).ordinal();
/*     */   }
/*     */ 
/*     */   
/*     */   public String getName() {
/*  38 */     return this.name;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isTraceEnabled() {
/*  44 */     return (this.level <= Level.TRACE.ordinal());
/*     */   }
/*     */ 
/*     */   
/*     */   public void trace(String fqcn, Throwable t, String format, Object... arguments) {
/*  49 */     logIfEnabled(fqcn, Level.TRACE, t, format, arguments);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isDebugEnabled() {
/*  55 */     return (this.level <= Level.DEBUG.ordinal());
/*     */   }
/*     */ 
/*     */   
/*     */   public void debug(String fqcn, Throwable t, String format, Object... arguments) {
/*  60 */     logIfEnabled(fqcn, Level.DEBUG, t, format, arguments);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isInfoEnabled() {
/*  65 */     return (this.level <= Level.INFO.ordinal());
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
/*  76 */     return (this.level <= Level.WARNING.ordinal());
/*     */   }
/*     */ 
/*     */   
/*     */   public void warn(String fqcn, Throwable t, String format, Object... arguments) {
/*  81 */     logIfEnabled(fqcn, Level.WARNING, t, format, arguments);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isErrorEnabled() {
/*  87 */     return (this.level <= Level.ERROR.ordinal());
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
/*  98 */     logIfEnabled(fqcn, toTinyLevel(level), t, format, arguments);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isEnabled(Level level) {
/* 103 */     return (this.level <= toTinyLevel(level).ordinal());
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
/*     */   private void logIfEnabled(String fqcn, Level level, Throwable t, String format, Object... arguments) {
/* 116 */     if (null == t) {
/* 117 */       t = getLastArgumentIfThrowable(arguments);
/*     */     }
/* 119 */     LogEntryForwarder.forward(4, level, t, StrUtil.toString(format), arguments);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private Level toTinyLevel(Level level) {
/*     */     Level tinyLevel;
/* 131 */     switch (level) {
/*     */       case TRACE:
/* 133 */         tinyLevel = Level.TRACE;
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
/*     */ 
/*     */ 
/*     */         
/* 153 */         return tinyLevel;case DEBUG: tinyLevel = Level.DEBUG; return tinyLevel;case INFO: tinyLevel = Level.INFO; return tinyLevel;case WARN: tinyLevel = Level.WARNING; return tinyLevel;case ERROR: tinyLevel = Level.ERROR; return tinyLevel;case OFF: tinyLevel = Level.OFF; return tinyLevel;
/*     */     } 
/*     */     throw new Error(StrUtil.format("Can not identify level: {}", new Object[] { level }));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static Throwable getLastArgumentIfThrowable(Object... arguments) {
/* 164 */     if (ArrayUtil.isNotEmpty(arguments) && arguments[arguments.length - 1] instanceof Throwable) {
/* 165 */       return (Throwable)arguments[arguments.length - 1];
/*     */     }
/* 167 */     return null;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\log\dialect\tinylog\TinyLog.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */