/*      */ package org.jboss.logging;
/*      */ 
/*      */ import java.io.Serializable;
/*      */ import java.lang.reflect.Constructor;
/*      */ import java.lang.reflect.InvocationTargetException;
/*      */ import java.security.AccessController;
/*      */ import java.security.PrivilegedAction;
/*      */ import java.util.Locale;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public abstract class Logger
/*      */   implements Serializable, BasicLogger
/*      */ {
/*      */   private static final long serialVersionUID = 4232175575988879434L;
/*   36 */   private static final String FQCN = Logger.class.getName();
/*      */   
/*      */   private final String name;
/*      */   
/*      */   public enum Level
/*      */   {
/*   42 */     FATAL,
/*   43 */     ERROR,
/*   44 */     WARN,
/*   45 */     INFO,
/*   46 */     DEBUG,
/*   47 */     TRACE;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected Logger(String name) {
/*   58 */     this.name = name;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getName() {
/*   67 */     return this.name;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isTraceEnabled() {
/*   98 */     return isEnabled(Level.TRACE);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void trace(Object message) {
/*  107 */     doLog(Level.TRACE, FQCN, message, (Object[])null, (Throwable)null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void trace(Object message, Throwable t) {
/*  117 */     doLog(Level.TRACE, FQCN, message, (Object[])null, t);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void trace(String loggerFqcn, Object message, Throwable t) {
/*  128 */     doLog(Level.TRACE, loggerFqcn, message, (Object[])null, t);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public void trace(Object message, Object[] params) {
/*  140 */     doLog(Level.TRACE, FQCN, message, params, (Throwable)null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public void trace(Object message, Object[] params, Throwable t) {
/*  153 */     doLog(Level.TRACE, FQCN, message, params, t);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void trace(String loggerFqcn, Object message, Object[] params, Throwable t) {
/*  165 */     doLog(Level.TRACE, loggerFqcn, message, params, t);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void tracev(String format, Object... params) {
/*  175 */     doLog(Level.TRACE, FQCN, format, params, (Throwable)null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void tracev(String format, Object param1) {
/*  185 */     if (isEnabled(Level.TRACE)) {
/*  186 */       doLog(Level.TRACE, FQCN, format, new Object[] { param1 }, (Throwable)null);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void tracev(String format, Object param1, Object param2) {
/*  198 */     if (isEnabled(Level.TRACE)) {
/*  199 */       doLog(Level.TRACE, FQCN, format, new Object[] { param1, param2 }, (Throwable)null);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void tracev(String format, Object param1, Object param2, Object param3) {
/*  212 */     if (isEnabled(Level.TRACE)) {
/*  213 */       doLog(Level.TRACE, FQCN, format, new Object[] { param1, param2, param3 }, (Throwable)null);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void tracev(Throwable t, String format, Object... params) {
/*  225 */     doLog(Level.TRACE, FQCN, format, params, t);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void tracev(Throwable t, String format, Object param1) {
/*  236 */     if (isEnabled(Level.TRACE)) {
/*  237 */       doLog(Level.TRACE, FQCN, format, new Object[] { param1 }, t);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void tracev(Throwable t, String format, Object param1, Object param2) {
/*  250 */     if (isEnabled(Level.TRACE)) {
/*  251 */       doLog(Level.TRACE, FQCN, format, new Object[] { param1, param2 }, t);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void tracev(Throwable t, String format, Object param1, Object param2, Object param3) {
/*  265 */     if (isEnabled(Level.TRACE)) {
/*  266 */       doLog(Level.TRACE, FQCN, format, new Object[] { param1, param2, param3 }, t);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void tracef(String format, Object... params) {
/*  277 */     doLogf(Level.TRACE, FQCN, format, params, (Throwable)null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void tracef(String format, Object param1) {
/*  287 */     if (isEnabled(Level.TRACE)) {
/*  288 */       doLogf(Level.TRACE, FQCN, format, new Object[] { param1 }, (Throwable)null);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void tracef(String format, Object param1, Object param2) {
/*  300 */     if (isEnabled(Level.TRACE)) {
/*  301 */       doLogf(Level.TRACE, FQCN, format, new Object[] { param1, param2 }, (Throwable)null);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void tracef(String format, Object param1, Object param2, Object param3) {
/*  314 */     if (isEnabled(Level.TRACE)) {
/*  315 */       doLogf(Level.TRACE, FQCN, format, new Object[] { param1, param2, param3 }, (Throwable)null);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void tracef(Throwable t, String format, Object... params) {
/*  327 */     doLogf(Level.TRACE, FQCN, format, params, t);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void tracef(Throwable t, String format, Object param1) {
/*  338 */     if (isEnabled(Level.TRACE)) {
/*  339 */       doLogf(Level.TRACE, FQCN, format, new Object[] { param1 }, t);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void tracef(Throwable t, String format, Object param1, Object param2) {
/*  352 */     if (isEnabled(Level.TRACE)) {
/*  353 */       doLogf(Level.TRACE, FQCN, format, new Object[] { param1, param2 }, t);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void tracef(Throwable t, String format, Object param1, Object param2, Object param3) {
/*  367 */     if (isEnabled(Level.TRACE)) {
/*  368 */       doLogf(Level.TRACE, FQCN, format, new Object[] { param1, param2, param3 }, t);
/*      */     }
/*      */   }
/*      */   
/*      */   public void tracef(String format, int arg) {
/*  373 */     if (isEnabled(Level.TRACE)) {
/*  374 */       doLogf(Level.TRACE, FQCN, format, new Object[] { Integer.valueOf(arg) }, (Throwable)null);
/*      */     }
/*      */   }
/*      */   
/*      */   public void tracef(String format, int arg1, int arg2) {
/*  379 */     if (isEnabled(Level.TRACE)) {
/*  380 */       doLogf(Level.TRACE, FQCN, format, new Object[] { Integer.valueOf(arg1), Integer.valueOf(arg2) }, (Throwable)null);
/*      */     }
/*      */   }
/*      */   
/*      */   public void tracef(String format, int arg1, Object arg2) {
/*  385 */     if (isEnabled(Level.TRACE)) {
/*  386 */       doLogf(Level.TRACE, FQCN, format, new Object[] { Integer.valueOf(arg1), arg2 }, (Throwable)null);
/*      */     }
/*      */   }
/*      */   
/*      */   public void tracef(String format, int arg1, int arg2, int arg3) {
/*  391 */     if (isEnabled(Level.TRACE)) {
/*  392 */       doLogf(Level.TRACE, FQCN, format, new Object[] { Integer.valueOf(arg1), Integer.valueOf(arg2), Integer.valueOf(arg3) }, (Throwable)null);
/*      */     }
/*      */   }
/*      */   
/*      */   public void tracef(String format, int arg1, int arg2, Object arg3) {
/*  397 */     if (isEnabled(Level.TRACE)) {
/*  398 */       doLogf(Level.TRACE, FQCN, format, new Object[] { Integer.valueOf(arg1), Integer.valueOf(arg2), arg3 }, (Throwable)null);
/*      */     }
/*      */   }
/*      */   
/*      */   public void tracef(String format, int arg1, Object arg2, Object arg3) {
/*  403 */     if (isEnabled(Level.TRACE)) {
/*  404 */       doLogf(Level.TRACE, FQCN, format, new Object[] { Integer.valueOf(arg1), arg2, arg3 }, (Throwable)null);
/*      */     }
/*      */   }
/*      */   
/*      */   public void tracef(Throwable t, String format, int arg) {
/*  409 */     if (isEnabled(Level.TRACE)) {
/*  410 */       doLogf(Level.TRACE, FQCN, format, new Object[] { Integer.valueOf(arg) }, t);
/*      */     }
/*      */   }
/*      */   
/*      */   public void tracef(Throwable t, String format, int arg1, int arg2) {
/*  415 */     if (isEnabled(Level.TRACE)) {
/*  416 */       doLogf(Level.TRACE, FQCN, format, new Object[] { Integer.valueOf(arg1), Integer.valueOf(arg2) }, t);
/*      */     }
/*      */   }
/*      */   
/*      */   public void tracef(Throwable t, String format, int arg1, Object arg2) {
/*  421 */     if (isEnabled(Level.TRACE)) {
/*  422 */       doLogf(Level.TRACE, FQCN, format, new Object[] { Integer.valueOf(arg1), arg2 }, t);
/*      */     }
/*      */   }
/*      */   
/*      */   public void tracef(Throwable t, String format, int arg1, int arg2, int arg3) {
/*  427 */     if (isEnabled(Level.TRACE)) {
/*  428 */       doLogf(Level.TRACE, FQCN, format, new Object[] { Integer.valueOf(arg1), Integer.valueOf(arg2), Integer.valueOf(arg3) }, t);
/*      */     }
/*      */   }
/*      */   
/*      */   public void tracef(Throwable t, String format, int arg1, int arg2, Object arg3) {
/*  433 */     if (isEnabled(Level.TRACE)) {
/*  434 */       doLogf(Level.TRACE, FQCN, format, new Object[] { Integer.valueOf(arg1), Integer.valueOf(arg2), arg3 }, t);
/*      */     }
/*      */   }
/*      */   
/*      */   public void tracef(Throwable t, String format, int arg1, Object arg2, Object arg3) {
/*  439 */     if (isEnabled(Level.TRACE)) {
/*  440 */       doLogf(Level.TRACE, FQCN, format, new Object[] { Integer.valueOf(arg1), arg2, arg3 }, t);
/*      */     }
/*      */   }
/*      */   
/*      */   public void tracef(String format, long arg) {
/*  445 */     if (isEnabled(Level.TRACE)) {
/*  446 */       doLogf(Level.TRACE, FQCN, format, new Object[] { Long.valueOf(arg) }, (Throwable)null);
/*      */     }
/*      */   }
/*      */   
/*      */   public void tracef(String format, long arg1, long arg2) {
/*  451 */     if (isEnabled(Level.TRACE)) {
/*  452 */       doLogf(Level.TRACE, FQCN, format, new Object[] { Long.valueOf(arg1), Long.valueOf(arg2) }, (Throwable)null);
/*      */     }
/*      */   }
/*      */   
/*      */   public void tracef(String format, long arg1, Object arg2) {
/*  457 */     if (isEnabled(Level.TRACE)) {
/*  458 */       doLogf(Level.TRACE, FQCN, format, new Object[] { Long.valueOf(arg1), arg2 }, (Throwable)null);
/*      */     }
/*      */   }
/*      */   
/*      */   public void tracef(String format, long arg1, long arg2, long arg3) {
/*  463 */     if (isEnabled(Level.TRACE)) {
/*  464 */       doLogf(Level.TRACE, FQCN, format, new Object[] { Long.valueOf(arg1), Long.valueOf(arg2), Long.valueOf(arg3) }, (Throwable)null);
/*      */     }
/*      */   }
/*      */   
/*      */   public void tracef(String format, long arg1, long arg2, Object arg3) {
/*  469 */     if (isEnabled(Level.TRACE)) {
/*  470 */       doLogf(Level.TRACE, FQCN, format, new Object[] { Long.valueOf(arg1), Long.valueOf(arg2), arg3 }, (Throwable)null);
/*      */     }
/*      */   }
/*      */   
/*      */   public void tracef(String format, long arg1, Object arg2, Object arg3) {
/*  475 */     if (isEnabled(Level.TRACE)) {
/*  476 */       doLogf(Level.TRACE, FQCN, format, new Object[] { Long.valueOf(arg1), arg2, arg3 }, (Throwable)null);
/*      */     }
/*      */   }
/*      */   
/*      */   public void tracef(Throwable t, String format, long arg) {
/*  481 */     if (isEnabled(Level.TRACE)) {
/*  482 */       doLogf(Level.TRACE, FQCN, format, new Object[] { Long.valueOf(arg) }, t);
/*      */     }
/*      */   }
/*      */   
/*      */   public void tracef(Throwable t, String format, long arg1, long arg2) {
/*  487 */     if (isEnabled(Level.TRACE)) {
/*  488 */       doLogf(Level.TRACE, FQCN, format, new Object[] { Long.valueOf(arg1), Long.valueOf(arg2) }, t);
/*      */     }
/*      */   }
/*      */   
/*      */   public void tracef(Throwable t, String format, long arg1, Object arg2) {
/*  493 */     if (isEnabled(Level.TRACE)) {
/*  494 */       doLogf(Level.TRACE, FQCN, format, new Object[] { Long.valueOf(arg1), arg2 }, t);
/*      */     }
/*      */   }
/*      */   
/*      */   public void tracef(Throwable t, String format, long arg1, long arg2, long arg3) {
/*  499 */     if (isEnabled(Level.TRACE)) {
/*  500 */       doLogf(Level.TRACE, FQCN, format, new Object[] { Long.valueOf(arg1), Long.valueOf(arg2), Long.valueOf(arg3) }, t);
/*      */     }
/*      */   }
/*      */   
/*      */   public void tracef(Throwable t, String format, long arg1, long arg2, Object arg3) {
/*  505 */     if (isEnabled(Level.TRACE)) {
/*  506 */       doLogf(Level.TRACE, FQCN, format, new Object[] { Long.valueOf(arg1), Long.valueOf(arg2), arg3 }, t);
/*      */     }
/*      */   }
/*      */   
/*      */   public void tracef(Throwable t, String format, long arg1, Object arg2, Object arg3) {
/*  511 */     if (isEnabled(Level.TRACE)) {
/*  512 */       doLogf(Level.TRACE, FQCN, format, new Object[] { Long.valueOf(arg1), arg2, arg3 }, t);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isDebugEnabled() {
/*  522 */     return isEnabled(Level.DEBUG);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void debug(Object message) {
/*  531 */     doLog(Level.DEBUG, FQCN, message, (Object[])null, (Throwable)null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void debug(Object message, Throwable t) {
/*  541 */     doLog(Level.DEBUG, FQCN, message, (Object[])null, t);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void debug(String loggerFqcn, Object message, Throwable t) {
/*  552 */     doLog(Level.DEBUG, loggerFqcn, message, (Object[])null, t);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public void debug(Object message, Object[] params) {
/*  564 */     doLog(Level.DEBUG, FQCN, message, params, (Throwable)null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public void debug(Object message, Object[] params, Throwable t) {
/*  577 */     doLog(Level.DEBUG, FQCN, message, params, t);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void debug(String loggerFqcn, Object message, Object[] params, Throwable t) {
/*  589 */     doLog(Level.DEBUG, loggerFqcn, message, params, t);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void debugv(String format, Object... params) {
/*  599 */     doLog(Level.DEBUG, FQCN, format, params, (Throwable)null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void debugv(String format, Object param1) {
/*  609 */     if (isEnabled(Level.DEBUG)) {
/*  610 */       doLog(Level.DEBUG, FQCN, format, new Object[] { param1 }, (Throwable)null);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void debugv(String format, Object param1, Object param2) {
/*  622 */     if (isEnabled(Level.DEBUG)) {
/*  623 */       doLog(Level.DEBUG, FQCN, format, new Object[] { param1, param2 }, (Throwable)null);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void debugv(String format, Object param1, Object param2, Object param3) {
/*  636 */     if (isEnabled(Level.DEBUG)) {
/*  637 */       doLog(Level.DEBUG, FQCN, format, new Object[] { param1, param2, param3 }, (Throwable)null);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void debugv(Throwable t, String format, Object... params) {
/*  649 */     doLog(Level.DEBUG, FQCN, format, params, t);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void debugv(Throwable t, String format, Object param1) {
/*  660 */     if (isEnabled(Level.DEBUG)) {
/*  661 */       doLog(Level.DEBUG, FQCN, format, new Object[] { param1 }, t);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void debugv(Throwable t, String format, Object param1, Object param2) {
/*  674 */     if (isEnabled(Level.DEBUG)) {
/*  675 */       doLog(Level.DEBUG, FQCN, format, new Object[] { param1, param2 }, t);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void debugv(Throwable t, String format, Object param1, Object param2, Object param3) {
/*  689 */     if (isEnabled(Level.DEBUG)) {
/*  690 */       doLog(Level.DEBUG, FQCN, format, new Object[] { param1, param2, param3 }, t);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void debugf(String format, Object... params) {
/*  701 */     doLogf(Level.DEBUG, FQCN, format, params, (Throwable)null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void debugf(String format, Object param1) {
/*  711 */     if (isEnabled(Level.DEBUG)) {
/*  712 */       doLogf(Level.DEBUG, FQCN, format, new Object[] { param1 }, (Throwable)null);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void debugf(String format, Object param1, Object param2) {
/*  724 */     if (isEnabled(Level.DEBUG)) {
/*  725 */       doLogf(Level.DEBUG, FQCN, format, new Object[] { param1, param2 }, (Throwable)null);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void debugf(String format, Object param1, Object param2, Object param3) {
/*  738 */     if (isEnabled(Level.DEBUG)) {
/*  739 */       doLogf(Level.DEBUG, FQCN, format, new Object[] { param1, param2, param3 }, (Throwable)null);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void debugf(Throwable t, String format, Object... params) {
/*  751 */     doLogf(Level.DEBUG, FQCN, format, params, t);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void debugf(Throwable t, String format, Object param1) {
/*  762 */     if (isEnabled(Level.DEBUG)) {
/*  763 */       doLogf(Level.DEBUG, FQCN, format, new Object[] { param1 }, t);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void debugf(Throwable t, String format, Object param1, Object param2) {
/*  776 */     if (isEnabled(Level.DEBUG)) {
/*  777 */       doLogf(Level.DEBUG, FQCN, format, new Object[] { param1, param2 }, t);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void debugf(Throwable t, String format, Object param1, Object param2, Object param3) {
/*  791 */     if (isEnabled(Level.DEBUG)) {
/*  792 */       doLogf(Level.DEBUG, FQCN, format, new Object[] { param1, param2, param3 }, t);
/*      */     }
/*      */   }
/*      */   
/*      */   public void debugf(String format, int arg) {
/*  797 */     if (isEnabled(Level.DEBUG)) {
/*  798 */       doLogf(Level.DEBUG, FQCN, format, new Object[] { Integer.valueOf(arg) }, (Throwable)null);
/*      */     }
/*      */   }
/*      */   
/*      */   public void debugf(String format, int arg1, int arg2) {
/*  803 */     if (isEnabled(Level.DEBUG)) {
/*  804 */       doLogf(Level.DEBUG, FQCN, format, new Object[] { Integer.valueOf(arg1), Integer.valueOf(arg2) }, (Throwable)null);
/*      */     }
/*      */   }
/*      */   
/*      */   public void debugf(String format, int arg1, Object arg2) {
/*  809 */     if (isEnabled(Level.DEBUG)) {
/*  810 */       doLogf(Level.DEBUG, FQCN, format, new Object[] { Integer.valueOf(arg1), arg2 }, (Throwable)null);
/*      */     }
/*      */   }
/*      */   
/*      */   public void debugf(String format, int arg1, int arg2, int arg3) {
/*  815 */     if (isEnabled(Level.DEBUG)) {
/*  816 */       doLogf(Level.DEBUG, FQCN, format, new Object[] { Integer.valueOf(arg1), Integer.valueOf(arg2), Integer.valueOf(arg3) }, (Throwable)null);
/*      */     }
/*      */   }
/*      */   
/*      */   public void debugf(String format, int arg1, int arg2, Object arg3) {
/*  821 */     if (isEnabled(Level.DEBUG)) {
/*  822 */       doLogf(Level.DEBUG, FQCN, format, new Object[] { Integer.valueOf(arg1), Integer.valueOf(arg2), arg3 }, (Throwable)null);
/*      */     }
/*      */   }
/*      */   
/*      */   public void debugf(String format, int arg1, Object arg2, Object arg3) {
/*  827 */     if (isEnabled(Level.DEBUG)) {
/*  828 */       doLogf(Level.DEBUG, FQCN, format, new Object[] { Integer.valueOf(arg1), arg2, arg3 }, (Throwable)null);
/*      */     }
/*      */   }
/*      */   
/*      */   public void debugf(Throwable t, String format, int arg) {
/*  833 */     if (isEnabled(Level.DEBUG)) {
/*  834 */       doLogf(Level.DEBUG, FQCN, format, new Object[] { Integer.valueOf(arg) }, t);
/*      */     }
/*      */   }
/*      */   
/*      */   public void debugf(Throwable t, String format, int arg1, int arg2) {
/*  839 */     if (isEnabled(Level.DEBUG)) {
/*  840 */       doLogf(Level.DEBUG, FQCN, format, new Object[] { Integer.valueOf(arg1), Integer.valueOf(arg2) }, t);
/*      */     }
/*      */   }
/*      */   
/*      */   public void debugf(Throwable t, String format, int arg1, Object arg2) {
/*  845 */     if (isEnabled(Level.DEBUG)) {
/*  846 */       doLogf(Level.DEBUG, FQCN, format, new Object[] { Integer.valueOf(arg1), arg2 }, t);
/*      */     }
/*      */   }
/*      */   
/*      */   public void debugf(Throwable t, String format, int arg1, int arg2, int arg3) {
/*  851 */     if (isEnabled(Level.DEBUG)) {
/*  852 */       doLogf(Level.DEBUG, FQCN, format, new Object[] { Integer.valueOf(arg1), Integer.valueOf(arg2), Integer.valueOf(arg3) }, t);
/*      */     }
/*      */   }
/*      */   
/*      */   public void debugf(Throwable t, String format, int arg1, int arg2, Object arg3) {
/*  857 */     if (isEnabled(Level.DEBUG)) {
/*  858 */       doLogf(Level.DEBUG, FQCN, format, new Object[] { Integer.valueOf(arg1), Integer.valueOf(arg2), arg3 }, t);
/*      */     }
/*      */   }
/*      */   
/*      */   public void debugf(Throwable t, String format, int arg1, Object arg2, Object arg3) {
/*  863 */     if (isEnabled(Level.DEBUG)) {
/*  864 */       doLogf(Level.DEBUG, FQCN, format, new Object[] { Integer.valueOf(arg1), arg2, arg3 }, t);
/*      */     }
/*      */   }
/*      */   
/*      */   public void debugf(String format, long arg) {
/*  869 */     if (isEnabled(Level.DEBUG)) {
/*  870 */       doLogf(Level.DEBUG, FQCN, format, new Object[] { Long.valueOf(arg) }, (Throwable)null);
/*      */     }
/*      */   }
/*      */   
/*      */   public void debugf(String format, long arg1, long arg2) {
/*  875 */     if (isEnabled(Level.DEBUG)) {
/*  876 */       doLogf(Level.DEBUG, FQCN, format, new Object[] { Long.valueOf(arg1), Long.valueOf(arg2) }, (Throwable)null);
/*      */     }
/*      */   }
/*      */   
/*      */   public void debugf(String format, long arg1, Object arg2) {
/*  881 */     if (isEnabled(Level.DEBUG)) {
/*  882 */       doLogf(Level.DEBUG, FQCN, format, new Object[] { Long.valueOf(arg1), arg2 }, (Throwable)null);
/*      */     }
/*      */   }
/*      */   
/*      */   public void debugf(String format, long arg1, long arg2, long arg3) {
/*  887 */     if (isEnabled(Level.DEBUG)) {
/*  888 */       doLogf(Level.DEBUG, FQCN, format, new Object[] { Long.valueOf(arg1), Long.valueOf(arg2), Long.valueOf(arg3) }, (Throwable)null);
/*      */     }
/*      */   }
/*      */   
/*      */   public void debugf(String format, long arg1, long arg2, Object arg3) {
/*  893 */     if (isEnabled(Level.DEBUG)) {
/*  894 */       doLogf(Level.DEBUG, FQCN, format, new Object[] { Long.valueOf(arg1), Long.valueOf(arg2), arg3 }, (Throwable)null);
/*      */     }
/*      */   }
/*      */   
/*      */   public void debugf(String format, long arg1, Object arg2, Object arg3) {
/*  899 */     if (isEnabled(Level.DEBUG)) {
/*  900 */       doLogf(Level.DEBUG, FQCN, format, new Object[] { Long.valueOf(arg1), arg2, arg3 }, (Throwable)null);
/*      */     }
/*      */   }
/*      */   
/*      */   public void debugf(Throwable t, String format, long arg) {
/*  905 */     if (isEnabled(Level.DEBUG)) {
/*  906 */       doLogf(Level.DEBUG, FQCN, format, new Object[] { Long.valueOf(arg) }, t);
/*      */     }
/*      */   }
/*      */   
/*      */   public void debugf(Throwable t, String format, long arg1, long arg2) {
/*  911 */     if (isEnabled(Level.DEBUG)) {
/*  912 */       doLogf(Level.DEBUG, FQCN, format, new Object[] { Long.valueOf(arg1), Long.valueOf(arg2) }, t);
/*      */     }
/*      */   }
/*      */   
/*      */   public void debugf(Throwable t, String format, long arg1, Object arg2) {
/*  917 */     if (isEnabled(Level.DEBUG)) {
/*  918 */       doLogf(Level.DEBUG, FQCN, format, new Object[] { Long.valueOf(arg1), arg2 }, t);
/*      */     }
/*      */   }
/*      */   
/*      */   public void debugf(Throwable t, String format, long arg1, long arg2, long arg3) {
/*  923 */     if (isEnabled(Level.DEBUG)) {
/*  924 */       doLogf(Level.DEBUG, FQCN, format, new Object[] { Long.valueOf(arg1), Long.valueOf(arg2), Long.valueOf(arg3) }, t);
/*      */     }
/*      */   }
/*      */   
/*      */   public void debugf(Throwable t, String format, long arg1, long arg2, Object arg3) {
/*  929 */     if (isEnabled(Level.DEBUG)) {
/*  930 */       doLogf(Level.DEBUG, FQCN, format, new Object[] { Long.valueOf(arg1), Long.valueOf(arg2), arg3 }, t);
/*      */     }
/*      */   }
/*      */   
/*      */   public void debugf(Throwable t, String format, long arg1, Object arg2, Object arg3) {
/*  935 */     if (isEnabled(Level.DEBUG)) {
/*  936 */       doLogf(Level.DEBUG, FQCN, format, new Object[] { Long.valueOf(arg1), arg2, arg3 }, t);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isInfoEnabled() {
/*  946 */     return isEnabled(Level.INFO);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void info(Object message) {
/*  955 */     doLog(Level.INFO, FQCN, message, (Object[])null, (Throwable)null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void info(Object message, Throwable t) {
/*  965 */     doLog(Level.INFO, FQCN, message, (Object[])null, t);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void info(String loggerFqcn, Object message, Throwable t) {
/*  976 */     doLog(Level.INFO, loggerFqcn, message, (Object[])null, t);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public void info(Object message, Object[] params) {
/*  988 */     doLog(Level.INFO, FQCN, message, params, (Throwable)null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public void info(Object message, Object[] params, Throwable t) {
/* 1001 */     doLog(Level.INFO, FQCN, message, params, t);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void info(String loggerFqcn, Object message, Object[] params, Throwable t) {
/* 1013 */     doLog(Level.INFO, loggerFqcn, message, params, t);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void infov(String format, Object... params) {
/* 1023 */     doLog(Level.INFO, FQCN, format, params, (Throwable)null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void infov(String format, Object param1) {
/* 1033 */     if (isEnabled(Level.INFO)) {
/* 1034 */       doLog(Level.INFO, FQCN, format, new Object[] { param1 }, (Throwable)null);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void infov(String format, Object param1, Object param2) {
/* 1046 */     if (isEnabled(Level.INFO)) {
/* 1047 */       doLog(Level.INFO, FQCN, format, new Object[] { param1, param2 }, (Throwable)null);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void infov(String format, Object param1, Object param2, Object param3) {
/* 1060 */     if (isEnabled(Level.INFO)) {
/* 1061 */       doLog(Level.INFO, FQCN, format, new Object[] { param1, param2, param3 }, (Throwable)null);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void infov(Throwable t, String format, Object... params) {
/* 1073 */     doLog(Level.INFO, FQCN, format, params, t);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void infov(Throwable t, String format, Object param1) {
/* 1084 */     if (isEnabled(Level.INFO)) {
/* 1085 */       doLog(Level.INFO, FQCN, format, new Object[] { param1 }, t);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void infov(Throwable t, String format, Object param1, Object param2) {
/* 1098 */     if (isEnabled(Level.INFO)) {
/* 1099 */       doLog(Level.INFO, FQCN, format, new Object[] { param1, param2 }, t);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void infov(Throwable t, String format, Object param1, Object param2, Object param3) {
/* 1113 */     if (isEnabled(Level.INFO)) {
/* 1114 */       doLog(Level.INFO, FQCN, format, new Object[] { param1, param2, param3 }, t);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void infof(String format, Object... params) {
/* 1125 */     doLogf(Level.INFO, FQCN, format, params, (Throwable)null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void infof(String format, Object param1) {
/* 1135 */     if (isEnabled(Level.INFO)) {
/* 1136 */       doLogf(Level.INFO, FQCN, format, new Object[] { param1 }, (Throwable)null);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void infof(String format, Object param1, Object param2) {
/* 1148 */     if (isEnabled(Level.INFO)) {
/* 1149 */       doLogf(Level.INFO, FQCN, format, new Object[] { param1, param2 }, (Throwable)null);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void infof(String format, Object param1, Object param2, Object param3) {
/* 1162 */     if (isEnabled(Level.INFO)) {
/* 1163 */       doLogf(Level.INFO, FQCN, format, new Object[] { param1, param2, param3 }, (Throwable)null);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void infof(Throwable t, String format, Object... params) {
/* 1175 */     doLogf(Level.INFO, FQCN, format, params, t);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void infof(Throwable t, String format, Object param1) {
/* 1186 */     if (isEnabled(Level.INFO)) {
/* 1187 */       doLogf(Level.INFO, FQCN, format, new Object[] { param1 }, t);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void infof(Throwable t, String format, Object param1, Object param2) {
/* 1200 */     if (isEnabled(Level.INFO)) {
/* 1201 */       doLogf(Level.INFO, FQCN, format, new Object[] { param1, param2 }, t);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void infof(Throwable t, String format, Object param1, Object param2, Object param3) {
/* 1215 */     if (isEnabled(Level.INFO)) {
/* 1216 */       doLogf(Level.INFO, FQCN, format, new Object[] { param1, param2, param3 }, t);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void warn(Object message) {
/* 1226 */     doLog(Level.WARN, FQCN, message, (Object[])null, (Throwable)null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void warn(Object message, Throwable t) {
/* 1236 */     doLog(Level.WARN, FQCN, message, (Object[])null, t);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void warn(String loggerFqcn, Object message, Throwable t) {
/* 1247 */     doLog(Level.WARN, loggerFqcn, message, (Object[])null, t);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public void warn(Object message, Object[] params) {
/* 1259 */     doLog(Level.WARN, FQCN, message, params, (Throwable)null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public void warn(Object message, Object[] params, Throwable t) {
/* 1272 */     doLog(Level.WARN, FQCN, message, params, t);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void warn(String loggerFqcn, Object message, Object[] params, Throwable t) {
/* 1284 */     doLog(Level.WARN, loggerFqcn, message, params, t);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void warnv(String format, Object... params) {
/* 1294 */     doLog(Level.WARN, FQCN, format, params, (Throwable)null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void warnv(String format, Object param1) {
/* 1304 */     if (isEnabled(Level.WARN)) {
/* 1305 */       doLog(Level.WARN, FQCN, format, new Object[] { param1 }, (Throwable)null);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void warnv(String format, Object param1, Object param2) {
/* 1317 */     if (isEnabled(Level.WARN)) {
/* 1318 */       doLog(Level.WARN, FQCN, format, new Object[] { param1, param2 }, (Throwable)null);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void warnv(String format, Object param1, Object param2, Object param3) {
/* 1331 */     if (isEnabled(Level.WARN)) {
/* 1332 */       doLog(Level.WARN, FQCN, format, new Object[] { param1, param2, param3 }, (Throwable)null);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void warnv(Throwable t, String format, Object... params) {
/* 1344 */     doLog(Level.WARN, FQCN, format, params, t);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void warnv(Throwable t, String format, Object param1) {
/* 1355 */     if (isEnabled(Level.WARN)) {
/* 1356 */       doLog(Level.WARN, FQCN, format, new Object[] { param1 }, t);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void warnv(Throwable t, String format, Object param1, Object param2) {
/* 1369 */     if (isEnabled(Level.WARN)) {
/* 1370 */       doLog(Level.WARN, FQCN, format, new Object[] { param1, param2 }, t);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void warnv(Throwable t, String format, Object param1, Object param2, Object param3) {
/* 1384 */     if (isEnabled(Level.WARN)) {
/* 1385 */       doLog(Level.WARN, FQCN, format, new Object[] { param1, param2, param3 }, t);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void warnf(String format, Object... params) {
/* 1396 */     doLogf(Level.WARN, FQCN, format, params, (Throwable)null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void warnf(String format, Object param1) {
/* 1406 */     if (isEnabled(Level.WARN)) {
/* 1407 */       doLogf(Level.WARN, FQCN, format, new Object[] { param1 }, (Throwable)null);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void warnf(String format, Object param1, Object param2) {
/* 1419 */     if (isEnabled(Level.WARN)) {
/* 1420 */       doLogf(Level.WARN, FQCN, format, new Object[] { param1, param2 }, (Throwable)null);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void warnf(String format, Object param1, Object param2, Object param3) {
/* 1433 */     if (isEnabled(Level.WARN)) {
/* 1434 */       doLogf(Level.WARN, FQCN, format, new Object[] { param1, param2, param3 }, (Throwable)null);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void warnf(Throwable t, String format, Object... params) {
/* 1446 */     doLogf(Level.WARN, FQCN, format, params, t);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void warnf(Throwable t, String format, Object param1) {
/* 1457 */     if (isEnabled(Level.WARN)) {
/* 1458 */       doLogf(Level.WARN, FQCN, format, new Object[] { param1 }, t);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void warnf(Throwable t, String format, Object param1, Object param2) {
/* 1471 */     if (isEnabled(Level.WARN)) {
/* 1472 */       doLogf(Level.WARN, FQCN, format, new Object[] { param1, param2 }, t);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void warnf(Throwable t, String format, Object param1, Object param2, Object param3) {
/* 1486 */     if (isEnabled(Level.WARN)) {
/* 1487 */       doLogf(Level.WARN, FQCN, format, new Object[] { param1, param2, param3 }, t);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void error(Object message) {
/* 1497 */     doLog(Level.ERROR, FQCN, message, (Object[])null, (Throwable)null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void error(Object message, Throwable t) {
/* 1507 */     doLog(Level.ERROR, FQCN, message, (Object[])null, t);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void error(String loggerFqcn, Object message, Throwable t) {
/* 1518 */     doLog(Level.ERROR, loggerFqcn, message, (Object[])null, t);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public void error(Object message, Object[] params) {
/* 1530 */     doLog(Level.ERROR, FQCN, message, params, (Throwable)null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public void error(Object message, Object[] params, Throwable t) {
/* 1543 */     doLog(Level.ERROR, FQCN, message, params, t);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void error(String loggerFqcn, Object message, Object[] params, Throwable t) {
/* 1555 */     doLog(Level.ERROR, loggerFqcn, message, params, t);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void errorv(String format, Object... params) {
/* 1565 */     doLog(Level.ERROR, FQCN, format, params, (Throwable)null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void errorv(String format, Object param1) {
/* 1575 */     if (isEnabled(Level.ERROR)) {
/* 1576 */       doLog(Level.ERROR, FQCN, format, new Object[] { param1 }, (Throwable)null);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void errorv(String format, Object param1, Object param2) {
/* 1588 */     if (isEnabled(Level.ERROR)) {
/* 1589 */       doLog(Level.ERROR, FQCN, format, new Object[] { param1, param2 }, (Throwable)null);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void errorv(String format, Object param1, Object param2, Object param3) {
/* 1602 */     if (isEnabled(Level.ERROR)) {
/* 1603 */       doLog(Level.ERROR, FQCN, format, new Object[] { param1, param2, param3 }, (Throwable)null);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void errorv(Throwable t, String format, Object... params) {
/* 1615 */     doLog(Level.ERROR, FQCN, format, params, t);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void errorv(Throwable t, String format, Object param1) {
/* 1626 */     if (isEnabled(Level.ERROR)) {
/* 1627 */       doLog(Level.ERROR, FQCN, format, new Object[] { param1 }, t);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void errorv(Throwable t, String format, Object param1, Object param2) {
/* 1640 */     if (isEnabled(Level.ERROR)) {
/* 1641 */       doLog(Level.ERROR, FQCN, format, new Object[] { param1, param2 }, t);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void errorv(Throwable t, String format, Object param1, Object param2, Object param3) {
/* 1655 */     if (isEnabled(Level.ERROR)) {
/* 1656 */       doLog(Level.ERROR, FQCN, format, new Object[] { param1, param2, param3 }, t);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void errorf(String format, Object... params) {
/* 1667 */     doLogf(Level.ERROR, FQCN, format, params, (Throwable)null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void errorf(String format, Object param1) {
/* 1677 */     if (isEnabled(Level.ERROR)) {
/* 1678 */       doLogf(Level.ERROR, FQCN, format, new Object[] { param1 }, (Throwable)null);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void errorf(String format, Object param1, Object param2) {
/* 1690 */     if (isEnabled(Level.ERROR)) {
/* 1691 */       doLogf(Level.ERROR, FQCN, format, new Object[] { param1, param2 }, (Throwable)null);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void errorf(String format, Object param1, Object param2, Object param3) {
/* 1704 */     if (isEnabled(Level.ERROR)) {
/* 1705 */       doLogf(Level.ERROR, FQCN, format, new Object[] { param1, param2, param3 }, (Throwable)null);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void errorf(Throwable t, String format, Object... params) {
/* 1717 */     doLogf(Level.ERROR, FQCN, format, params, t);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void errorf(Throwable t, String format, Object param1) {
/* 1728 */     if (isEnabled(Level.ERROR)) {
/* 1729 */       doLogf(Level.ERROR, FQCN, format, new Object[] { param1 }, t);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void errorf(Throwable t, String format, Object param1, Object param2) {
/* 1742 */     if (isEnabled(Level.ERROR)) {
/* 1743 */       doLogf(Level.ERROR, FQCN, format, new Object[] { param1, param2 }, t);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void errorf(Throwable t, String format, Object param1, Object param2, Object param3) {
/* 1757 */     if (isEnabled(Level.ERROR)) {
/* 1758 */       doLogf(Level.ERROR, FQCN, format, new Object[] { param1, param2, param3 }, t);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void fatal(Object message) {
/* 1768 */     doLog(Level.FATAL, FQCN, message, (Object[])null, (Throwable)null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void fatal(Object message, Throwable t) {
/* 1778 */     doLog(Level.FATAL, FQCN, message, (Object[])null, t);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void fatal(String loggerFqcn, Object message, Throwable t) {
/* 1789 */     doLog(Level.FATAL, loggerFqcn, message, (Object[])null, t);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public void fatal(Object message, Object[] params) {
/* 1801 */     doLog(Level.FATAL, FQCN, message, params, (Throwable)null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public void fatal(Object message, Object[] params, Throwable t) {
/* 1814 */     doLog(Level.FATAL, FQCN, message, params, t);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void fatal(String loggerFqcn, Object message, Object[] params, Throwable t) {
/* 1826 */     doLog(Level.FATAL, loggerFqcn, message, params, t);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void fatalv(String format, Object... params) {
/* 1836 */     doLog(Level.FATAL, FQCN, format, params, (Throwable)null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void fatalv(String format, Object param1) {
/* 1846 */     if (isEnabled(Level.FATAL)) {
/* 1847 */       doLog(Level.FATAL, FQCN, format, new Object[] { param1 }, (Throwable)null);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void fatalv(String format, Object param1, Object param2) {
/* 1859 */     if (isEnabled(Level.FATAL)) {
/* 1860 */       doLog(Level.FATAL, FQCN, format, new Object[] { param1, param2 }, (Throwable)null);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void fatalv(String format, Object param1, Object param2, Object param3) {
/* 1873 */     if (isEnabled(Level.FATAL)) {
/* 1874 */       doLog(Level.FATAL, FQCN, format, new Object[] { param1, param2, param3 }, (Throwable)null);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void fatalv(Throwable t, String format, Object... params) {
/* 1886 */     doLog(Level.FATAL, FQCN, format, params, t);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void fatalv(Throwable t, String format, Object param1) {
/* 1897 */     if (isEnabled(Level.FATAL)) {
/* 1898 */       doLog(Level.FATAL, FQCN, format, new Object[] { param1 }, t);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void fatalv(Throwable t, String format, Object param1, Object param2) {
/* 1911 */     if (isEnabled(Level.FATAL)) {
/* 1912 */       doLog(Level.FATAL, FQCN, format, new Object[] { param1, param2 }, t);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void fatalv(Throwable t, String format, Object param1, Object param2, Object param3) {
/* 1926 */     if (isEnabled(Level.FATAL)) {
/* 1927 */       doLog(Level.FATAL, FQCN, format, new Object[] { param1, param2, param3 }, t);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void fatalf(String format, Object... params) {
/* 1938 */     doLogf(Level.FATAL, FQCN, format, params, (Throwable)null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void fatalf(String format, Object param1) {
/* 1948 */     if (isEnabled(Level.FATAL)) {
/* 1949 */       doLogf(Level.FATAL, FQCN, format, new Object[] { param1 }, (Throwable)null);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void fatalf(String format, Object param1, Object param2) {
/* 1961 */     if (isEnabled(Level.FATAL)) {
/* 1962 */       doLogf(Level.FATAL, FQCN, format, new Object[] { param1, param2 }, (Throwable)null);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void fatalf(String format, Object param1, Object param2, Object param3) {
/* 1975 */     if (isEnabled(Level.FATAL)) {
/* 1976 */       doLogf(Level.FATAL, FQCN, format, new Object[] { param1, param2, param3 }, (Throwable)null);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void fatalf(Throwable t, String format, Object... params) {
/* 1988 */     doLogf(Level.FATAL, FQCN, format, params, t);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void fatalf(Throwable t, String format, Object param1) {
/* 1999 */     if (isEnabled(Level.FATAL)) {
/* 2000 */       doLogf(Level.FATAL, FQCN, format, new Object[] { param1 }, t);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void fatalf(Throwable t, String format, Object param1, Object param2) {
/* 2013 */     if (isEnabled(Level.FATAL)) {
/* 2014 */       doLogf(Level.FATAL, FQCN, format, new Object[] { param1, param2 }, t);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void fatalf(Throwable t, String format, Object param1, Object param2, Object param3) {
/* 2028 */     if (isEnabled(Level.FATAL)) {
/* 2029 */       doLogf(Level.FATAL, FQCN, format, new Object[] { param1, param2, param3 }, t);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void log(Level level, Object message) {
/* 2040 */     doLog(level, FQCN, message, (Object[])null, (Throwable)null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void log(Level level, Object message, Throwable t) {
/* 2051 */     doLog(level, FQCN, message, (Object[])null, t);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void log(Level level, String loggerFqcn, Object message, Throwable t) {
/* 2063 */     doLog(level, loggerFqcn, message, (Object[])null, t);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public void log(Level level, Object message, Object[] params) {
/* 2076 */     doLog(level, FQCN, message, params, (Throwable)null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public void log(Level level, Object message, Object[] params, Throwable t) {
/* 2090 */     doLog(level, FQCN, message, params, t);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void log(String loggerFqcn, Level level, Object message, Object[] params, Throwable t) {
/* 2103 */     doLog(level, loggerFqcn, message, params, t);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void logv(Level level, String format, Object... params) {
/* 2114 */     doLog(level, FQCN, format, params, (Throwable)null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void logv(Level level, String format, Object param1) {
/* 2125 */     if (isEnabled(level)) {
/* 2126 */       doLog(level, FQCN, format, new Object[] { param1 }, (Throwable)null);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void logv(Level level, String format, Object param1, Object param2) {
/* 2139 */     if (isEnabled(level)) {
/* 2140 */       doLog(level, FQCN, format, new Object[] { param1, param2 }, (Throwable)null);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void logv(Level level, String format, Object param1, Object param2, Object param3) {
/* 2154 */     if (isEnabled(level)) {
/* 2155 */       doLog(level, FQCN, format, new Object[] { param1, param2, param3 }, (Throwable)null);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void logv(Level level, Throwable t, String format, Object... params) {
/* 2168 */     doLog(level, FQCN, format, params, t);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void logv(Level level, Throwable t, String format, Object param1) {
/* 2180 */     if (isEnabled(level)) {
/* 2181 */       doLog(level, FQCN, format, new Object[] { param1 }, t);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void logv(Level level, Throwable t, String format, Object param1, Object param2) {
/* 2195 */     if (isEnabled(level)) {
/* 2196 */       doLog(level, FQCN, format, new Object[] { param1, param2 }, t);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void logv(Level level, Throwable t, String format, Object param1, Object param2, Object param3) {
/* 2211 */     if (isEnabled(level)) {
/* 2212 */       doLog(level, FQCN, format, new Object[] { param1, param2, param3 }, t);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void logv(String loggerFqcn, Level level, Throwable t, String format, Object... params) {
/* 2226 */     doLog(level, loggerFqcn, format, params, t);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void logv(String loggerFqcn, Level level, Throwable t, String format, Object param1) {
/* 2239 */     if (isEnabled(level)) {
/* 2240 */       doLog(level, loggerFqcn, format, new Object[] { param1 }, t);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void logv(String loggerFqcn, Level level, Throwable t, String format, Object param1, Object param2) {
/* 2255 */     if (isEnabled(level)) {
/* 2256 */       doLog(level, loggerFqcn, format, new Object[] { param1, param2 }, t);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void logv(String loggerFqcn, Level level, Throwable t, String format, Object param1, Object param2, Object param3) {
/* 2272 */     if (isEnabled(level)) {
/* 2273 */       doLog(level, loggerFqcn, format, new Object[] { param1, param2, param3 }, t);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void logf(Level level, String format, Object... params) {
/* 2285 */     doLogf(level, FQCN, format, params, (Throwable)null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void logf(Level level, String format, Object param1) {
/* 2296 */     if (isEnabled(level)) {
/* 2297 */       doLogf(level, FQCN, format, new Object[] { param1 }, (Throwable)null);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void logf(Level level, String format, Object param1, Object param2) {
/* 2310 */     if (isEnabled(level)) {
/* 2311 */       doLogf(level, FQCN, format, new Object[] { param1, param2 }, (Throwable)null);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void logf(Level level, String format, Object param1, Object param2, Object param3) {
/* 2325 */     if (isEnabled(level)) {
/* 2326 */       doLogf(level, FQCN, format, new Object[] { param1, param2, param3 }, (Throwable)null);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void logf(Level level, Throwable t, String format, Object... params) {
/* 2339 */     doLogf(level, FQCN, format, params, t);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void logf(Level level, Throwable t, String format, Object param1) {
/* 2351 */     if (isEnabled(level)) {
/* 2352 */       doLogf(level, FQCN, format, new Object[] { param1 }, t);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void logf(Level level, Throwable t, String format, Object param1, Object param2) {
/* 2366 */     if (isEnabled(level)) {
/* 2367 */       doLogf(level, FQCN, format, new Object[] { param1, param2 }, t);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void logf(Level level, Throwable t, String format, Object param1, Object param2, Object param3) {
/* 2382 */     if (isEnabled(level)) {
/* 2383 */       doLogf(level, FQCN, format, new Object[] { param1, param2, param3 }, t);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void logf(String loggerFqcn, Level level, Throwable t, String format, Object param1) {
/* 2397 */     if (isEnabled(level)) {
/* 2398 */       doLogf(level, loggerFqcn, format, new Object[] { param1 }, t);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void logf(String loggerFqcn, Level level, Throwable t, String format, Object param1, Object param2) {
/* 2413 */     if (isEnabled(level)) {
/* 2414 */       doLogf(level, loggerFqcn, format, new Object[] { param1, param2 }, t);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void logf(String loggerFqcn, Level level, Throwable t, String format, Object param1, Object param2, Object param3) {
/* 2430 */     if (isEnabled(level)) {
/* 2431 */       doLogf(level, loggerFqcn, format, new Object[] { param1, param2, param3 }, t);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void logf(String loggerFqcn, Level level, Throwable t, String format, Object... params) {
/* 2445 */     doLogf(level, loggerFqcn, format, params, t);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected final Object writeReplace() {
/* 2454 */     return new SerializedLogger(this.name);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Logger getLogger(String name) {
/* 2465 */     return LoggerProviders.PROVIDER.getLogger(name);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Logger getLogger(String name, String suffix) {
/* 2479 */     return getLogger((name == null || name.length() == 0) ? suffix : (name + "." + suffix));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Logger getLogger(Class<?> clazz) {
/* 2490 */     return getLogger(clazz.getName());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Logger getLogger(Class<?> clazz, String suffix) {
/* 2504 */     return getLogger(clazz.getName(), suffix);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <T> T getMessageLogger(Class<T> type, String category) {
/* 2516 */     return getMessageLogger(type, category, LoggingLocale.getLocale());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <T> T getMessageLogger(final Class<T> type, final String category, final Locale locale) {
/* 2529 */     if (System.getSecurityManager() == null)
/* 2530 */       return doGetMessageLogger(type, category, locale); 
/* 2531 */     return AccessController.doPrivileged(new PrivilegedAction<T>() {
/*      */           public T run() {
/* 2533 */             return Logger.doGetMessageLogger(type, category, locale);
/*      */           }
/*      */         });
/*      */   }
/*      */   private static <T> T doGetMessageLogger(Class<T> type, String category, Locale locale) {
/*      */     Constructor<? extends T> constructor;
/* 2539 */     String language = locale.getLanguage();
/* 2540 */     String country = locale.getCountry();
/* 2541 */     String variant = locale.getVariant();
/*      */     
/* 2543 */     Class<? extends T> loggerClass = null;
/* 2544 */     ClassLoader classLoader = type.getClassLoader();
/* 2545 */     String typeName = type.getName();
/* 2546 */     if (variant != null && variant.length() > 0) {
/* 2547 */       try { loggerClass = Class.forName(join(typeName, "$logger", language, country, variant), true, classLoader).asSubclass(type); }
/* 2548 */       catch (ClassNotFoundException classNotFoundException) {}
/*      */     }
/*      */     
/* 2551 */     if (loggerClass == null && country != null && country.length() > 0) {
/* 2552 */       try { loggerClass = Class.forName(join(typeName, "$logger", language, country, (String)null), true, classLoader).asSubclass(type); }
/* 2553 */       catch (ClassNotFoundException classNotFoundException) {}
/*      */     }
/*      */     
/* 2556 */     if (loggerClass == null && language != null && language.length() > 0) {
/* 2557 */       try { loggerClass = Class.forName(join(typeName, "$logger", language, (String)null, (String)null), true, classLoader).asSubclass(type); }
/* 2558 */       catch (ClassNotFoundException classNotFoundException) {}
/*      */     }
/*      */     
/* 2561 */     if (loggerClass == null) {
/* 2562 */       try { loggerClass = Class.forName(join(typeName, "$logger", (String)null, (String)null, (String)null), true, classLoader).asSubclass(type); }
/* 2563 */       catch (ClassNotFoundException e)
/* 2564 */       { throw new IllegalArgumentException("Invalid logger " + type + " (implementation not found in " + classLoader + ")"); }
/*      */     
/*      */     }
/*      */     try {
/* 2568 */       constructor = loggerClass.getConstructor(new Class[] { Logger.class });
/* 2569 */     } catch (NoSuchMethodException e) {
/* 2570 */       throw new IllegalArgumentException("Logger implementation " + loggerClass + " has no matching constructor");
/*      */     } 
/*      */     try {
/* 2573 */       return constructor.newInstance(new Object[] { getLogger(category) });
/* 2574 */     } catch (InstantiationException e) {
/* 2575 */       throw new IllegalArgumentException("Logger implementation " + loggerClass + " could not be instantiated", e);
/* 2576 */     } catch (IllegalAccessException e) {
/* 2577 */       throw new IllegalArgumentException("Logger implementation " + loggerClass + " could not be instantiated", e);
/* 2578 */     } catch (InvocationTargetException e) {
/* 2579 */       throw new IllegalArgumentException("Logger implementation " + loggerClass + " could not be instantiated", e.getCause());
/*      */     } 
/*      */   }
/*      */   
/*      */   private static String join(String interfaceName, String a, String b, String c, String d) {
/* 2584 */     StringBuilder build = new StringBuilder();
/* 2585 */     build.append(interfaceName).append('_').append(a);
/* 2586 */     if (b != null && b.length() > 0) {
/* 2587 */       build.append('_');
/* 2588 */       build.append(b);
/*      */     } 
/* 2590 */     if (c != null && c.length() > 0) {
/* 2591 */       build.append('_');
/* 2592 */       build.append(c);
/*      */     } 
/* 2594 */     if (d != null && d.length() > 0) {
/* 2595 */       build.append('_');
/* 2596 */       build.append(d);
/*      */     } 
/* 2598 */     return build.toString();
/*      */   }
/*      */   
/*      */   protected abstract void doLog(Level paramLevel, String paramString, Object paramObject, Object[] paramArrayOfObject, Throwable paramThrowable);
/*      */   
/*      */   protected abstract void doLogf(Level paramLevel, String paramString1, String paramString2, Object[] paramArrayOfObject, Throwable paramThrowable);
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\jboss\logging\Logger.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */