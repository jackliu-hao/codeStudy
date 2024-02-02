/*     */ package cn.hutool.log;
/*     */ 
/*     */ import cn.hutool.core.exceptions.ExceptionUtil;
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import cn.hutool.log.level.Level;
/*     */ import java.io.Serializable;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class AbstractLog
/*     */   implements Log, Serializable
/*     */ {
/*     */   private static final long serialVersionUID = -3211115409504005616L;
/*  19 */   private static final String FQCN = AbstractLog.class.getName();
/*     */ 
/*     */   
/*     */   public boolean isEnabled(Level level) {
/*  23 */     switch (level) {
/*     */       case TRACE:
/*  25 */         return isTraceEnabled();
/*     */       case DEBUG:
/*  27 */         return isDebugEnabled();
/*     */       case INFO:
/*  29 */         return isInfoEnabled();
/*     */       case WARN:
/*  31 */         return isWarnEnabled();
/*     */       case ERROR:
/*  33 */         return isErrorEnabled();
/*     */     } 
/*  35 */     throw new Error(StrUtil.format("Can not identify level: {}", new Object[] { level }));
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void trace(Throwable t) {
/*  41 */     trace(t, ExceptionUtil.getSimpleMessage(t), new Object[0]);
/*     */   }
/*     */ 
/*     */   
/*     */   public void trace(String format, Object... arguments) {
/*  46 */     trace((Throwable)null, format, arguments);
/*     */   }
/*     */ 
/*     */   
/*     */   public void trace(Throwable t, String format, Object... arguments) {
/*  51 */     trace(FQCN, t, format, arguments);
/*     */   }
/*     */ 
/*     */   
/*     */   public void debug(Throwable t) {
/*  56 */     debug(t, ExceptionUtil.getSimpleMessage(t), new Object[0]);
/*     */   }
/*     */ 
/*     */   
/*     */   public void debug(String format, Object... arguments) {
/*  61 */     if (null != arguments && 1 == arguments.length && arguments[0] instanceof Throwable) {
/*     */       
/*  63 */       debug((Throwable)arguments[0], format, new Object[0]);
/*     */     } else {
/*  65 */       debug((Throwable)null, format, arguments);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void debug(Throwable t, String format, Object... arguments) {
/*  71 */     debug(FQCN, t, format, arguments);
/*     */   }
/*     */ 
/*     */   
/*     */   public void info(Throwable t) {
/*  76 */     info(t, ExceptionUtil.getSimpleMessage(t), new Object[0]);
/*     */   }
/*     */ 
/*     */   
/*     */   public void info(String format, Object... arguments) {
/*  81 */     if (null != arguments && 1 == arguments.length && arguments[0] instanceof Throwable) {
/*     */       
/*  83 */       info((Throwable)arguments[0], format, new Object[0]);
/*     */     } else {
/*  85 */       info((Throwable)null, format, arguments);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void info(Throwable t, String format, Object... arguments) {
/*  91 */     info(FQCN, t, format, arguments);
/*     */   }
/*     */ 
/*     */   
/*     */   public void warn(Throwable t) {
/*  96 */     warn(t, ExceptionUtil.getSimpleMessage(t), new Object[0]);
/*     */   }
/*     */ 
/*     */   
/*     */   public void warn(String format, Object... arguments) {
/* 101 */     if (null != arguments && 1 == arguments.length && arguments[0] instanceof Throwable) {
/*     */       
/* 103 */       warn((Throwable)arguments[0], format, new Object[0]);
/*     */     } else {
/* 105 */       warn((Throwable)null, format, arguments);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void warn(Throwable t, String format, Object... arguments) {
/* 111 */     warn(FQCN, t, format, arguments);
/*     */   }
/*     */ 
/*     */   
/*     */   public void error(Throwable t) {
/* 116 */     error(t, ExceptionUtil.getSimpleMessage(t), new Object[0]);
/*     */   }
/*     */ 
/*     */   
/*     */   public void error(String format, Object... arguments) {
/* 121 */     if (null != arguments && 1 == arguments.length && arguments[0] instanceof Throwable) {
/*     */       
/* 123 */       error((Throwable)arguments[0], format, new Object[0]);
/*     */     } else {
/* 125 */       error((Throwable)null, format, arguments);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void error(Throwable t, String format, Object... arguments) {
/* 131 */     error(FQCN, t, format, arguments);
/*     */   }
/*     */ 
/*     */   
/*     */   public void log(Level level, String format, Object... arguments) {
/* 136 */     if (null != arguments && 1 == arguments.length && arguments[0] instanceof Throwable) {
/*     */       
/* 138 */       log(level, (Throwable)arguments[0], format, new Object[0]);
/*     */     } else {
/* 140 */       log(level, (Throwable)null, format, arguments);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void log(Level level, Throwable t, String format, Object... arguments) {
/* 146 */     log(FQCN, level, t, format, arguments);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\log\AbstractLog.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */