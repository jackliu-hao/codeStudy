/*     */ package cn.hutool.log.dialect.console;
/*     */ 
/*     */ import cn.hutool.core.date.DateUtil;
/*     */ import cn.hutool.core.lang.Assert;
/*     */ import cn.hutool.core.lang.Console;
/*     */ import cn.hutool.core.lang.Dict;
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import cn.hutool.log.AbstractLog;
/*     */ import cn.hutool.log.level.Level;
/*     */ import java.util.Map;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ConsoleLog
/*     */   extends AbstractLog
/*     */ {
/*     */   private static final long serialVersionUID = -6843151523380063975L;
/*     */   private static final String logFormat = "[{date}] [{level}] {name}: {msg}";
/*  20 */   private static Level currentLevel = Level.DEBUG;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final String name;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ConsoleLog(Class<?> clazz) {
/*  32 */     this.name = (null == clazz) ? "null" : clazz.getName();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ConsoleLog(String name) {
/*  41 */     this.name = name;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getName() {
/*  46 */     return this.name;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void setLevel(Level customLevel) {
/*  56 */     Assert.notNull(customLevel);
/*  57 */     currentLevel = customLevel;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isTraceEnabled() {
/*  63 */     return isEnabled(Level.TRACE);
/*     */   }
/*     */ 
/*     */   
/*     */   public void trace(String fqcn, Throwable t, String format, Object... arguments) {
/*  68 */     log(fqcn, Level.TRACE, t, format, arguments);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isDebugEnabled() {
/*  74 */     return isEnabled(Level.DEBUG);
/*     */   }
/*     */ 
/*     */   
/*     */   public void debug(String fqcn, Throwable t, String format, Object... arguments) {
/*  79 */     log(fqcn, Level.DEBUG, t, format, arguments);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isInfoEnabled() {
/*  85 */     return isEnabled(Level.INFO);
/*     */   }
/*     */ 
/*     */   
/*     */   public void info(String fqcn, Throwable t, String format, Object... arguments) {
/*  90 */     log(fqcn, Level.INFO, t, format, arguments);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isWarnEnabled() {
/*  96 */     return isEnabled(Level.WARN);
/*     */   }
/*     */ 
/*     */   
/*     */   public void warn(String fqcn, Throwable t, String format, Object... arguments) {
/* 101 */     log(fqcn, Level.WARN, t, format, arguments);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isErrorEnabled() {
/* 107 */     return isEnabled(Level.ERROR);
/*     */   }
/*     */ 
/*     */   
/*     */   public void error(String fqcn, Throwable t, String format, Object... arguments) {
/* 112 */     log(fqcn, Level.ERROR, t, format, arguments);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void log(String fqcn, Level level, Throwable t, String format, Object... arguments) {
/* 119 */     if (false == isEnabled(level)) {
/*     */       return;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 128 */     Dict dict = Dict.create().set("date", DateUtil.now()).set("level", level.toString()).set("name", this.name).set("msg", StrUtil.format(format, arguments));
/*     */     
/* 130 */     String logMsg = StrUtil.format("[{date}] [{level}] {name}: {msg}", (Map)dict);
/*     */ 
/*     */     
/* 133 */     if (level.ordinal() >= Level.WARN.ordinal()) {
/* 134 */       Console.error(t, logMsg, new Object[0]);
/*     */     } else {
/* 136 */       Console.log(t, logMsg, new Object[0]);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isEnabled(Level level) {
/* 142 */     return (currentLevel.compareTo((Enum)level) <= 0);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\log\dialect\console\ConsoleLog.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */