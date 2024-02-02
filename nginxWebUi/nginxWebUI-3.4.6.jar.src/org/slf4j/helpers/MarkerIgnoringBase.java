/*     */ package org.slf4j.helpers;
/*     */ 
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.Marker;
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
/*     */ public abstract class MarkerIgnoringBase
/*     */   extends NamedLoggerBase
/*     */   implements Logger
/*     */ {
/*     */   private static final long serialVersionUID = 9044267456635152283L;
/*     */   
/*     */   public boolean isTraceEnabled(Marker marker) {
/*  43 */     return isTraceEnabled();
/*     */   }
/*     */   
/*     */   public void trace(Marker marker, String msg) {
/*  47 */     trace(msg);
/*     */   }
/*     */   
/*     */   public void trace(Marker marker, String format, Object arg) {
/*  51 */     trace(format, arg);
/*     */   }
/*     */   
/*     */   public void trace(Marker marker, String format, Object arg1, Object arg2) {
/*  55 */     trace(format, arg1, arg2);
/*     */   }
/*     */   
/*     */   public void trace(Marker marker, String format, Object... arguments) {
/*  59 */     trace(format, arguments);
/*     */   }
/*     */   
/*     */   public void trace(Marker marker, String msg, Throwable t) {
/*  63 */     trace(msg, t);
/*     */   }
/*     */   
/*     */   public boolean isDebugEnabled(Marker marker) {
/*  67 */     return isDebugEnabled();
/*     */   }
/*     */   
/*     */   public void debug(Marker marker, String msg) {
/*  71 */     debug(msg);
/*     */   }
/*     */   
/*     */   public void debug(Marker marker, String format, Object arg) {
/*  75 */     debug(format, arg);
/*     */   }
/*     */   
/*     */   public void debug(Marker marker, String format, Object arg1, Object arg2) {
/*  79 */     debug(format, arg1, arg2);
/*     */   }
/*     */   
/*     */   public void debug(Marker marker, String format, Object... arguments) {
/*  83 */     debug(format, arguments);
/*     */   }
/*     */   
/*     */   public void debug(Marker marker, String msg, Throwable t) {
/*  87 */     debug(msg, t);
/*     */   }
/*     */   
/*     */   public boolean isInfoEnabled(Marker marker) {
/*  91 */     return isInfoEnabled();
/*     */   }
/*     */   
/*     */   public void info(Marker marker, String msg) {
/*  95 */     info(msg);
/*     */   }
/*     */   
/*     */   public void info(Marker marker, String format, Object arg) {
/*  99 */     info(format, arg);
/*     */   }
/*     */   
/*     */   public void info(Marker marker, String format, Object arg1, Object arg2) {
/* 103 */     info(format, arg1, arg2);
/*     */   }
/*     */   
/*     */   public void info(Marker marker, String format, Object... arguments) {
/* 107 */     info(format, arguments);
/*     */   }
/*     */   
/*     */   public void info(Marker marker, String msg, Throwable t) {
/* 111 */     info(msg, t);
/*     */   }
/*     */   
/*     */   public boolean isWarnEnabled(Marker marker) {
/* 115 */     return isWarnEnabled();
/*     */   }
/*     */   
/*     */   public void warn(Marker marker, String msg) {
/* 119 */     warn(msg);
/*     */   }
/*     */   
/*     */   public void warn(Marker marker, String format, Object arg) {
/* 123 */     warn(format, arg);
/*     */   }
/*     */   
/*     */   public void warn(Marker marker, String format, Object arg1, Object arg2) {
/* 127 */     warn(format, arg1, arg2);
/*     */   }
/*     */   
/*     */   public void warn(Marker marker, String format, Object... arguments) {
/* 131 */     warn(format, arguments);
/*     */   }
/*     */   
/*     */   public void warn(Marker marker, String msg, Throwable t) {
/* 135 */     warn(msg, t);
/*     */   }
/*     */   
/*     */   public boolean isErrorEnabled(Marker marker) {
/* 139 */     return isErrorEnabled();
/*     */   }
/*     */   
/*     */   public void error(Marker marker, String msg) {
/* 143 */     error(msg);
/*     */   }
/*     */   
/*     */   public void error(Marker marker, String format, Object arg) {
/* 147 */     error(format, arg);
/*     */   }
/*     */   
/*     */   public void error(Marker marker, String format, Object arg1, Object arg2) {
/* 151 */     error(format, arg1, arg2);
/*     */   }
/*     */   
/*     */   public void error(Marker marker, String format, Object... arguments) {
/* 155 */     error(format, arguments);
/*     */   }
/*     */   
/*     */   public void error(Marker marker, String msg, Throwable t) {
/* 159 */     error(msg, t);
/*     */   }
/*     */   
/*     */   public String toString() {
/* 163 */     return getClass().getName() + "(" + getName() + ")";
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\slf4j\helpers\MarkerIgnoringBase.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */