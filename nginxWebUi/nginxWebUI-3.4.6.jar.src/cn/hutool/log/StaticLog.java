/*     */ package cn.hutool.log;
/*     */ 
/*     */ import cn.hutool.core.lang.caller.CallerUtil;
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import cn.hutool.log.level.Level;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class StaticLog
/*     */ {
/*  14 */   private static final String FQCN = StaticLog.class.getName();
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
/*     */   public static void trace(String format, Object... arguments) {
/*  29 */     trace(LogFactory.get(CallerUtil.getCallerCaller()), format, arguments);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void trace(Log log, String format, Object... arguments) {
/*  40 */     log.trace(FQCN, null, format, arguments);
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
/*     */   public static void debug(String format, Object... arguments) {
/*  52 */     debug(LogFactory.get(CallerUtil.getCallerCaller()), format, arguments);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void debug(Log log, String format, Object... arguments) {
/*  63 */     log.debug(FQCN, null, format, arguments);
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
/*     */   public static void info(String format, Object... arguments) {
/*  75 */     info(LogFactory.get(CallerUtil.getCallerCaller()), format, arguments);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void info(Log log, String format, Object... arguments) {
/*  86 */     log.info(FQCN, null, format, arguments);
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
/*     */   public static void warn(String format, Object... arguments) {
/*  98 */     warn(LogFactory.get(CallerUtil.getCallerCaller()), format, arguments);
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
/*     */   public static void warn(Throwable e, String format, Object... arguments) {
/* 110 */     warn(LogFactory.get(CallerUtil.getCallerCaller()), e, StrUtil.format(format, arguments), new Object[0]);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void warn(Log log, String format, Object... arguments) {
/* 121 */     warn(log, null, format, arguments);
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
/*     */   public static void warn(Log log, Throwable e, String format, Object... arguments) {
/* 133 */     log.warn(FQCN, e, format, arguments);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void error(Throwable e) {
/* 144 */     error(LogFactory.get(CallerUtil.getCallerCaller()), e);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void error(String format, Object... arguments) {
/* 155 */     error(LogFactory.get(CallerUtil.getCallerCaller()), format, arguments);
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
/*     */   public static void error(Throwable e, String format, Object... arguments) {
/* 167 */     error(LogFactory.get(CallerUtil.getCallerCaller()), e, format, arguments);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void error(Log log, Throwable e) {
/* 177 */     error(log, e, e.getMessage(), new Object[0]);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void error(Log log, String format, Object... arguments) {
/* 188 */     error(log, null, format, arguments);
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
/*     */   public static void error(Log log, Throwable e, String format, Object... arguments) {
/* 200 */     log.error(FQCN, e, format, arguments);
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
/*     */   public static void log(Level level, Throwable t, String format, Object... arguments) {
/* 213 */     LogFactory.get(CallerUtil.getCallerCaller()).log(FQCN, level, t, format, arguments);
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
/*     */   @Deprecated
/*     */   public static Log get(Class<?> clazz) {
/* 227 */     return LogFactory.get(clazz);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public static Log get(String name) {
/* 239 */     return LogFactory.get(name);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public static Log get() {
/* 248 */     return LogFactory.get(CallerUtil.getCallerCaller());
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\log\StaticLog.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */