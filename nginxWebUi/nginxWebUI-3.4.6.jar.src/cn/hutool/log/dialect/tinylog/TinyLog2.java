/*     */ package cn.hutool.log.dialect.tinylog;
/*     */ 
/*     */ import cn.hutool.core.util.ArrayUtil;
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import cn.hutool.log.AbstractLog;
/*     */ import cn.hutool.log.level.Level;
/*     */ import org.tinylog.Level;
/*     */ import org.tinylog.configuration.Configuration;
/*     */ import org.tinylog.format.AdvancedMessageFormatter;
/*     */ import org.tinylog.format.MessageFormatter;
/*     */ import org.tinylog.provider.LoggingProvider;
/*     */ import org.tinylog.provider.ProviderRegistry;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class TinyLog2
/*     */   extends AbstractLog
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   private static final int DEPTH = 5;
/*     */   private final int level;
/*     */   private final String name;
/*  27 */   private static final LoggingProvider provider = ProviderRegistry.getLoggingProvider();
/*     */ 
/*     */   
/*  30 */   private static final MessageFormatter formatter = (MessageFormatter)new AdvancedMessageFormatter(
/*  31 */       Configuration.getLocale(), 
/*  32 */       Configuration.isEscapingEnabled());
/*     */   
/*     */   public TinyLog2(Class<?> clazz) {
/*  35 */     this((null == clazz) ? "null" : clazz.getName());
/*     */   }
/*     */   
/*     */   public TinyLog2(String name) {
/*  39 */     this.name = name;
/*  40 */     this.level = provider.getMinimumLevel().ordinal();
/*     */   }
/*     */ 
/*     */   
/*     */   public String getName() {
/*  45 */     return this.name;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isTraceEnabled() {
/*  51 */     return (this.level <= Level.TRACE.ordinal());
/*     */   }
/*     */ 
/*     */   
/*     */   public void trace(String fqcn, Throwable t, String format, Object... arguments) {
/*  56 */     logIfEnabled(fqcn, Level.TRACE, t, format, arguments);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isDebugEnabled() {
/*  62 */     return (this.level <= Level.DEBUG.ordinal());
/*     */   }
/*     */ 
/*     */   
/*     */   public void debug(String fqcn, Throwable t, String format, Object... arguments) {
/*  67 */     logIfEnabled(fqcn, Level.DEBUG, t, format, arguments);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isInfoEnabled() {
/*  72 */     return (this.level <= Level.INFO.ordinal());
/*     */   }
/*     */ 
/*     */   
/*     */   public void info(String fqcn, Throwable t, String format, Object... arguments) {
/*  77 */     logIfEnabled(fqcn, Level.INFO, t, format, arguments);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isWarnEnabled() {
/*  83 */     return (this.level <= Level.WARN.ordinal());
/*     */   }
/*     */ 
/*     */   
/*     */   public void warn(String fqcn, Throwable t, String format, Object... arguments) {
/*  88 */     logIfEnabled(fqcn, Level.WARN, t, format, arguments);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isErrorEnabled() {
/*  94 */     return (this.level <= Level.ERROR.ordinal());
/*     */   }
/*     */ 
/*     */   
/*     */   public void error(String fqcn, Throwable t, String format, Object... arguments) {
/*  99 */     logIfEnabled(fqcn, Level.ERROR, t, format, arguments);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void log(String fqcn, Level level, Throwable t, String format, Object... arguments) {
/* 105 */     logIfEnabled(fqcn, toTinyLevel(level), t, format, arguments);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isEnabled(Level level) {
/* 110 */     return (this.level <= toTinyLevel(level).ordinal());
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
/* 123 */     if (null == t) {
/* 124 */       t = getLastArgumentIfThrowable(arguments);
/*     */     }
/* 126 */     provider.log(5, null, level, t, formatter, StrUtil.toString(format), arguments);
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
/* 138 */     switch (level) {
/*     */       case TRACE:
/* 140 */         tinyLevel = Level.TRACE;
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
/* 160 */         return tinyLevel;case DEBUG: tinyLevel = Level.DEBUG; return tinyLevel;case INFO: tinyLevel = Level.INFO; return tinyLevel;case WARN: tinyLevel = Level.WARN; return tinyLevel;case ERROR: tinyLevel = Level.ERROR; return tinyLevel;case OFF: tinyLevel = Level.OFF; return tinyLevel;
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
/* 171 */     if (ArrayUtil.isNotEmpty(arguments) && arguments[arguments.length - 1] instanceof Throwable) {
/* 172 */       return (Throwable)arguments[arguments.length - 1];
/*     */     }
/* 174 */     return null;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\log\dialect\tinylog\TinyLog2.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */