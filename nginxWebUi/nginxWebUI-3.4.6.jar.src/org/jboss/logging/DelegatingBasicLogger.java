/*      */ package org.jboss.logging;
/*      */ 
/*      */ import java.io.Serializable;
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
/*      */ public class DelegatingBasicLogger
/*      */   implements BasicLogger, Serializable
/*      */ {
/*      */   private static final long serialVersionUID = -5774903162389601853L;
/*   36 */   private static final String FQCN = DelegatingBasicLogger.class.getName();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected final Logger log;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public DelegatingBasicLogger(Logger log) {
/*   49 */     this.log = log;
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean isTraceEnabled() {
/*   54 */     return this.log.isTraceEnabled();
/*      */   }
/*      */ 
/*      */   
/*      */   public void trace(Object message) {
/*   59 */     this.log.trace(FQCN, message, (Throwable)null);
/*      */   }
/*      */ 
/*      */   
/*      */   public void trace(Object message, Throwable t) {
/*   64 */     this.log.trace(FQCN, message, t);
/*      */   }
/*      */ 
/*      */   
/*      */   public void trace(String loggerFqcn, Object message, Throwable t) {
/*   69 */     this.log.trace(loggerFqcn, message, t);
/*      */   }
/*      */ 
/*      */   
/*      */   public void trace(String loggerFqcn, Object message, Object[] params, Throwable t) {
/*   74 */     this.log.trace(loggerFqcn, message, params, t);
/*      */   }
/*      */ 
/*      */   
/*      */   public void tracev(String format, Object... params) {
/*   79 */     this.log.logv(FQCN, Logger.Level.TRACE, (Throwable)null, format, params);
/*      */   }
/*      */ 
/*      */   
/*      */   public void tracev(String format, Object param1) {
/*   84 */     this.log.logv(FQCN, Logger.Level.TRACE, (Throwable)null, format, param1);
/*      */   }
/*      */ 
/*      */   
/*      */   public void tracev(String format, Object param1, Object param2) {
/*   89 */     this.log.logv(FQCN, Logger.Level.TRACE, (Throwable)null, format, param1, param2);
/*      */   }
/*      */ 
/*      */   
/*      */   public void tracev(String format, Object param1, Object param2, Object param3) {
/*   94 */     this.log.logv(FQCN, Logger.Level.TRACE, (Throwable)null, format, param1, param2, param3);
/*      */   }
/*      */ 
/*      */   
/*      */   public void tracev(Throwable t, String format, Object... params) {
/*   99 */     this.log.logv(FQCN, Logger.Level.TRACE, t, format, params);
/*      */   }
/*      */ 
/*      */   
/*      */   public void tracev(Throwable t, String format, Object param1) {
/*  104 */     this.log.logv(FQCN, Logger.Level.TRACE, t, format, param1);
/*      */   }
/*      */ 
/*      */   
/*      */   public void tracev(Throwable t, String format, Object param1, Object param2) {
/*  109 */     this.log.logv(FQCN, Logger.Level.TRACE, t, format, param1, param2);
/*      */   }
/*      */ 
/*      */   
/*      */   public void tracev(Throwable t, String format, Object param1, Object param2, Object param3) {
/*  114 */     this.log.logv(FQCN, Logger.Level.TRACE, t, format, param1, param2, param3);
/*      */   }
/*      */ 
/*      */   
/*      */   public void tracef(String format, Object... params) {
/*  119 */     this.log.logf(FQCN, Logger.Level.TRACE, (Throwable)null, format, params);
/*      */   }
/*      */ 
/*      */   
/*      */   public void tracef(String format, Object param1) {
/*  124 */     this.log.logf(FQCN, Logger.Level.TRACE, (Throwable)null, format, param1);
/*      */   }
/*      */ 
/*      */   
/*      */   public void tracef(String format, Object param1, Object param2) {
/*  129 */     this.log.logf(FQCN, Logger.Level.TRACE, (Throwable)null, format, param1, param2);
/*      */   }
/*      */ 
/*      */   
/*      */   public void tracef(String format, Object param1, Object param2, Object param3) {
/*  134 */     this.log.logf(FQCN, Logger.Level.TRACE, (Throwable)null, format, param1, param2, param3);
/*      */   }
/*      */ 
/*      */   
/*      */   public void tracef(Throwable t, String format, Object... params) {
/*  139 */     this.log.logf(FQCN, Logger.Level.TRACE, t, format, params);
/*      */   }
/*      */ 
/*      */   
/*      */   public void tracef(Throwable t, String format, Object param1) {
/*  144 */     this.log.logf(FQCN, Logger.Level.TRACE, t, format, param1);
/*      */   }
/*      */ 
/*      */   
/*      */   public void tracef(Throwable t, String format, Object param1, Object param2) {
/*  149 */     this.log.logf(FQCN, Logger.Level.TRACE, t, format, param1, param2);
/*      */   }
/*      */ 
/*      */   
/*      */   public void tracef(Throwable t, String format, Object param1, Object param2, Object param3) {
/*  154 */     this.log.logf(FQCN, Logger.Level.TRACE, t, format, param1, param2, param3);
/*      */   }
/*      */ 
/*      */   
/*      */   public void tracef(String format, int arg) {
/*  159 */     this.log.tracef(format, arg);
/*      */   }
/*      */ 
/*      */   
/*      */   public void tracef(String format, int arg1, int arg2) {
/*  164 */     this.log.tracef(format, arg1, arg2);
/*      */   }
/*      */ 
/*      */   
/*      */   public void tracef(String format, int arg1, Object arg2) {
/*  169 */     this.log.tracef(format, arg1, arg2);
/*      */   }
/*      */ 
/*      */   
/*      */   public void tracef(String format, int arg1, int arg2, int arg3) {
/*  174 */     this.log.tracef(format, arg1, arg2, arg3);
/*      */   }
/*      */ 
/*      */   
/*      */   public void tracef(String format, int arg1, int arg2, Object arg3) {
/*  179 */     this.log.tracef(format, arg1, arg2, arg3);
/*      */   }
/*      */ 
/*      */   
/*      */   public void tracef(String format, int arg1, Object arg2, Object arg3) {
/*  184 */     this.log.tracef(format, arg1, arg2, arg3);
/*      */   }
/*      */ 
/*      */   
/*      */   public void tracef(Throwable t, String format, int arg) {
/*  189 */     this.log.tracef(t, format, arg);
/*      */   }
/*      */ 
/*      */   
/*      */   public void tracef(Throwable t, String format, int arg1, int arg2) {
/*  194 */     this.log.tracef(t, format, arg1, arg2);
/*      */   }
/*      */ 
/*      */   
/*      */   public void tracef(Throwable t, String format, int arg1, Object arg2) {
/*  199 */     this.log.tracef(t, format, arg1, arg2);
/*      */   }
/*      */ 
/*      */   
/*      */   public void tracef(Throwable t, String format, int arg1, int arg2, int arg3) {
/*  204 */     this.log.tracef(t, format, arg1, arg2, arg3);
/*      */   }
/*      */ 
/*      */   
/*      */   public void tracef(Throwable t, String format, int arg1, int arg2, Object arg3) {
/*  209 */     this.log.tracef(t, format, arg1, arg2, arg3);
/*      */   }
/*      */ 
/*      */   
/*      */   public void tracef(Throwable t, String format, int arg1, Object arg2, Object arg3) {
/*  214 */     this.log.tracef(t, format, arg1, arg2, arg3);
/*      */   }
/*      */ 
/*      */   
/*      */   public void tracef(String format, long arg) {
/*  219 */     this.log.tracef(format, arg);
/*      */   }
/*      */ 
/*      */   
/*      */   public void tracef(String format, long arg1, long arg2) {
/*  224 */     this.log.tracef(format, arg1, arg2);
/*      */   }
/*      */ 
/*      */   
/*      */   public void tracef(String format, long arg1, Object arg2) {
/*  229 */     this.log.tracef(format, arg1, arg2);
/*      */   }
/*      */ 
/*      */   
/*      */   public void tracef(String format, long arg1, long arg2, long arg3) {
/*  234 */     this.log.tracef(format, arg1, arg2, arg3);
/*      */   }
/*      */ 
/*      */   
/*      */   public void tracef(String format, long arg1, long arg2, Object arg3) {
/*  239 */     this.log.tracef(format, arg1, arg2, arg3);
/*      */   }
/*      */ 
/*      */   
/*      */   public void tracef(String format, long arg1, Object arg2, Object arg3) {
/*  244 */     this.log.tracef(format, arg1, arg2, arg3);
/*      */   }
/*      */ 
/*      */   
/*      */   public void tracef(Throwable t, String format, long arg) {
/*  249 */     this.log.tracef(t, format, arg);
/*      */   }
/*      */ 
/*      */   
/*      */   public void tracef(Throwable t, String format, long arg1, long arg2) {
/*  254 */     this.log.tracef(t, format, arg1, arg2);
/*      */   }
/*      */ 
/*      */   
/*      */   public void tracef(Throwable t, String format, long arg1, Object arg2) {
/*  259 */     this.log.tracef(t, format, arg1, arg2);
/*      */   }
/*      */ 
/*      */   
/*      */   public void tracef(Throwable t, String format, long arg1, long arg2, long arg3) {
/*  264 */     this.log.tracef(t, format, arg1, arg2, arg3);
/*      */   }
/*      */ 
/*      */   
/*      */   public void tracef(Throwable t, String format, long arg1, long arg2, Object arg3) {
/*  269 */     this.log.tracef(t, format, arg1, arg2, arg3);
/*      */   }
/*      */ 
/*      */   
/*      */   public void tracef(Throwable t, String format, long arg1, Object arg2, Object arg3) {
/*  274 */     this.log.tracef(t, format, arg1, arg2, arg3);
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean isDebugEnabled() {
/*  279 */     return this.log.isDebugEnabled();
/*      */   }
/*      */ 
/*      */   
/*      */   public void debug(Object message) {
/*  284 */     this.log.debug(FQCN, message, (Throwable)null);
/*      */   }
/*      */ 
/*      */   
/*      */   public void debug(Object message, Throwable t) {
/*  289 */     this.log.debug(FQCN, message, t);
/*      */   }
/*      */ 
/*      */   
/*      */   public void debug(String loggerFqcn, Object message, Throwable t) {
/*  294 */     this.log.debug(loggerFqcn, message, t);
/*      */   }
/*      */ 
/*      */   
/*      */   public void debug(String loggerFqcn, Object message, Object[] params, Throwable t) {
/*  299 */     this.log.debug(loggerFqcn, message, params, t);
/*      */   }
/*      */ 
/*      */   
/*      */   public void debugv(String format, Object... params) {
/*  304 */     this.log.logv(FQCN, Logger.Level.DEBUG, (Throwable)null, format, params);
/*      */   }
/*      */ 
/*      */   
/*      */   public void debugv(String format, Object param1) {
/*  309 */     this.log.logv(FQCN, Logger.Level.DEBUG, (Throwable)null, format, param1);
/*      */   }
/*      */ 
/*      */   
/*      */   public void debugv(String format, Object param1, Object param2) {
/*  314 */     this.log.logv(FQCN, Logger.Level.DEBUG, (Throwable)null, format, param1, param2);
/*      */   }
/*      */ 
/*      */   
/*      */   public void debugv(String format, Object param1, Object param2, Object param3) {
/*  319 */     this.log.logv(FQCN, Logger.Level.DEBUG, (Throwable)null, format, param1, param2, param3);
/*      */   }
/*      */ 
/*      */   
/*      */   public void debugv(Throwable t, String format, Object... params) {
/*  324 */     this.log.logv(FQCN, Logger.Level.DEBUG, t, format, params);
/*      */   }
/*      */ 
/*      */   
/*      */   public void debugv(Throwable t, String format, Object param1) {
/*  329 */     this.log.logv(FQCN, Logger.Level.DEBUG, t, format, param1);
/*      */   }
/*      */ 
/*      */   
/*      */   public void debugv(Throwable t, String format, Object param1, Object param2) {
/*  334 */     this.log.logv(FQCN, Logger.Level.DEBUG, t, format, param1, param2);
/*      */   }
/*      */ 
/*      */   
/*      */   public void debugv(Throwable t, String format, Object param1, Object param2, Object param3) {
/*  339 */     this.log.logv(FQCN, Logger.Level.DEBUG, t, format, param1, param2, param3);
/*      */   }
/*      */ 
/*      */   
/*      */   public void debugf(String format, Object... params) {
/*  344 */     this.log.logf(FQCN, Logger.Level.DEBUG, (Throwable)null, format, params);
/*      */   }
/*      */ 
/*      */   
/*      */   public void debugf(String format, Object param1) {
/*  349 */     this.log.logf(FQCN, Logger.Level.DEBUG, (Throwable)null, format, param1);
/*      */   }
/*      */ 
/*      */   
/*      */   public void debugf(String format, Object param1, Object param2) {
/*  354 */     this.log.logf(FQCN, Logger.Level.DEBUG, (Throwable)null, format, param1, param2);
/*      */   }
/*      */ 
/*      */   
/*      */   public void debugf(String format, Object param1, Object param2, Object param3) {
/*  359 */     this.log.logf(FQCN, Logger.Level.DEBUG, (Throwable)null, format, param1, param2, param3);
/*      */   }
/*      */ 
/*      */   
/*      */   public void debugf(Throwable t, String format, Object... params) {
/*  364 */     this.log.logf(FQCN, Logger.Level.DEBUG, t, format, params);
/*      */   }
/*      */ 
/*      */   
/*      */   public void debugf(Throwable t, String format, Object param1) {
/*  369 */     this.log.logf(FQCN, Logger.Level.DEBUG, t, format, param1);
/*      */   }
/*      */ 
/*      */   
/*      */   public void debugf(Throwable t, String format, Object param1, Object param2) {
/*  374 */     this.log.logf(FQCN, Logger.Level.DEBUG, t, format, param1, param2);
/*      */   }
/*      */ 
/*      */   
/*      */   public void debugf(Throwable t, String format, Object param1, Object param2, Object param3) {
/*  379 */     this.log.logf(FQCN, Logger.Level.DEBUG, t, format, param1, param2, param3);
/*      */   }
/*      */ 
/*      */   
/*      */   public void debugf(String format, int arg) {
/*  384 */     this.log.debugf(format, arg);
/*      */   }
/*      */ 
/*      */   
/*      */   public void debugf(String format, int arg1, int arg2) {
/*  389 */     this.log.debugf(format, arg1, arg2);
/*      */   }
/*      */ 
/*      */   
/*      */   public void debugf(String format, int arg1, Object arg2) {
/*  394 */     this.log.debugf(format, arg1, arg2);
/*      */   }
/*      */ 
/*      */   
/*      */   public void debugf(String format, int arg1, int arg2, int arg3) {
/*  399 */     this.log.debugf(format, arg1, arg2, arg3);
/*      */   }
/*      */ 
/*      */   
/*      */   public void debugf(String format, int arg1, int arg2, Object arg3) {
/*  404 */     this.log.debugf(format, arg1, arg2, arg3);
/*      */   }
/*      */ 
/*      */   
/*      */   public void debugf(String format, int arg1, Object arg2, Object arg3) {
/*  409 */     this.log.debugf(format, arg1, arg2, arg3);
/*      */   }
/*      */ 
/*      */   
/*      */   public void debugf(Throwable t, String format, int arg) {
/*  414 */     this.log.debugf(t, format, arg);
/*      */   }
/*      */ 
/*      */   
/*      */   public void debugf(Throwable t, String format, int arg1, int arg2) {
/*  419 */     this.log.debugf(t, format, arg1, arg2);
/*      */   }
/*      */ 
/*      */   
/*      */   public void debugf(Throwable t, String format, int arg1, Object arg2) {
/*  424 */     this.log.debugf(t, format, arg1, arg2);
/*      */   }
/*      */ 
/*      */   
/*      */   public void debugf(Throwable t, String format, int arg1, int arg2, int arg3) {
/*  429 */     this.log.debugf(t, format, arg1, arg2, arg3);
/*      */   }
/*      */ 
/*      */   
/*      */   public void debugf(Throwable t, String format, int arg1, int arg2, Object arg3) {
/*  434 */     this.log.debugf(t, format, arg1, arg2, arg3);
/*      */   }
/*      */ 
/*      */   
/*      */   public void debugf(Throwable t, String format, int arg1, Object arg2, Object arg3) {
/*  439 */     this.log.debugf(t, format, arg1, arg2, arg3);
/*      */   }
/*      */ 
/*      */   
/*      */   public void debugf(String format, long arg) {
/*  444 */     this.log.debugf(format, arg);
/*      */   }
/*      */ 
/*      */   
/*      */   public void debugf(String format, long arg1, long arg2) {
/*  449 */     this.log.debugf(format, arg1, arg2);
/*      */   }
/*      */ 
/*      */   
/*      */   public void debugf(String format, long arg1, Object arg2) {
/*  454 */     this.log.debugf(format, arg1, arg2);
/*      */   }
/*      */ 
/*      */   
/*      */   public void debugf(String format, long arg1, long arg2, long arg3) {
/*  459 */     this.log.debugf(format, arg1, arg2, arg3);
/*      */   }
/*      */ 
/*      */   
/*      */   public void debugf(String format, long arg1, long arg2, Object arg3) {
/*  464 */     this.log.debugf(format, arg1, arg2, arg3);
/*      */   }
/*      */ 
/*      */   
/*      */   public void debugf(String format, long arg1, Object arg2, Object arg3) {
/*  469 */     this.log.debugf(format, arg1, arg2, arg3);
/*      */   }
/*      */ 
/*      */   
/*      */   public void debugf(Throwable t, String format, long arg) {
/*  474 */     this.log.debugf(t, format, arg);
/*      */   }
/*      */ 
/*      */   
/*      */   public void debugf(Throwable t, String format, long arg1, long arg2) {
/*  479 */     this.log.debugf(t, format, arg1, arg2);
/*      */   }
/*      */ 
/*      */   
/*      */   public void debugf(Throwable t, String format, long arg1, Object arg2) {
/*  484 */     this.log.debugf(t, format, arg1, arg2);
/*      */   }
/*      */ 
/*      */   
/*      */   public void debugf(Throwable t, String format, long arg1, long arg2, long arg3) {
/*  489 */     this.log.debugf(t, format, arg1, arg2, arg3);
/*      */   }
/*      */ 
/*      */   
/*      */   public void debugf(Throwable t, String format, long arg1, long arg2, Object arg3) {
/*  494 */     this.log.debugf(t, format, arg1, arg2, arg3);
/*      */   }
/*      */ 
/*      */   
/*      */   public void debugf(Throwable t, String format, long arg1, Object arg2, Object arg3) {
/*  499 */     this.log.debugf(t, format, arg1, arg2, arg3);
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean isInfoEnabled() {
/*  504 */     return this.log.isInfoEnabled();
/*      */   }
/*      */ 
/*      */   
/*      */   public void info(Object message) {
/*  509 */     this.log.info(FQCN, message, (Throwable)null);
/*      */   }
/*      */ 
/*      */   
/*      */   public void info(Object message, Throwable t) {
/*  514 */     this.log.info(FQCN, message, t);
/*      */   }
/*      */ 
/*      */   
/*      */   public void info(String loggerFqcn, Object message, Throwable t) {
/*  519 */     this.log.info(loggerFqcn, message, t);
/*      */   }
/*      */ 
/*      */   
/*      */   public void info(String loggerFqcn, Object message, Object[] params, Throwable t) {
/*  524 */     this.log.info(loggerFqcn, message, params, t);
/*      */   }
/*      */ 
/*      */   
/*      */   public void infov(String format, Object... params) {
/*  529 */     this.log.logv(FQCN, Logger.Level.INFO, (Throwable)null, format, params);
/*      */   }
/*      */ 
/*      */   
/*      */   public void infov(String format, Object param1) {
/*  534 */     this.log.logv(FQCN, Logger.Level.INFO, (Throwable)null, format, param1);
/*      */   }
/*      */ 
/*      */   
/*      */   public void infov(String format, Object param1, Object param2) {
/*  539 */     this.log.logv(FQCN, Logger.Level.INFO, (Throwable)null, format, param1, param2);
/*      */   }
/*      */ 
/*      */   
/*      */   public void infov(String format, Object param1, Object param2, Object param3) {
/*  544 */     this.log.logv(FQCN, Logger.Level.INFO, (Throwable)null, format, param1, param2, param3);
/*      */   }
/*      */ 
/*      */   
/*      */   public void infov(Throwable t, String format, Object... params) {
/*  549 */     this.log.logv(FQCN, Logger.Level.INFO, t, format, params);
/*      */   }
/*      */ 
/*      */   
/*      */   public void infov(Throwable t, String format, Object param1) {
/*  554 */     this.log.logv(FQCN, Logger.Level.INFO, t, format, param1);
/*      */   }
/*      */ 
/*      */   
/*      */   public void infov(Throwable t, String format, Object param1, Object param2) {
/*  559 */     this.log.logv(FQCN, Logger.Level.INFO, t, format, param1, param2);
/*      */   }
/*      */ 
/*      */   
/*      */   public void infov(Throwable t, String format, Object param1, Object param2, Object param3) {
/*  564 */     this.log.logv(FQCN, Logger.Level.INFO, t, format, param1, param2, param3);
/*      */   }
/*      */ 
/*      */   
/*      */   public void infof(String format, Object... params) {
/*  569 */     this.log.logf(FQCN, Logger.Level.INFO, (Throwable)null, format, params);
/*      */   }
/*      */ 
/*      */   
/*      */   public void infof(String format, Object param1) {
/*  574 */     this.log.logf(FQCN, Logger.Level.INFO, (Throwable)null, format, param1);
/*      */   }
/*      */ 
/*      */   
/*      */   public void infof(String format, Object param1, Object param2) {
/*  579 */     this.log.logf(FQCN, Logger.Level.INFO, (Throwable)null, format, param1, param2);
/*      */   }
/*      */ 
/*      */   
/*      */   public void infof(String format, Object param1, Object param2, Object param3) {
/*  584 */     this.log.logf(FQCN, Logger.Level.INFO, (Throwable)null, format, param1, param2, param3);
/*      */   }
/*      */ 
/*      */   
/*      */   public void infof(Throwable t, String format, Object... params) {
/*  589 */     this.log.logf(FQCN, Logger.Level.INFO, t, format, params);
/*      */   }
/*      */ 
/*      */   
/*      */   public void infof(Throwable t, String format, Object param1) {
/*  594 */     this.log.logf(FQCN, Logger.Level.INFO, t, format, param1);
/*      */   }
/*      */ 
/*      */   
/*      */   public void infof(Throwable t, String format, Object param1, Object param2) {
/*  599 */     this.log.logf(FQCN, Logger.Level.INFO, t, format, param1, param2);
/*      */   }
/*      */ 
/*      */   
/*      */   public void infof(Throwable t, String format, Object param1, Object param2, Object param3) {
/*  604 */     this.log.logf(FQCN, Logger.Level.INFO, t, format, param1, param2, param3);
/*      */   }
/*      */ 
/*      */   
/*      */   public void warn(Object message) {
/*  609 */     this.log.warn(FQCN, message, (Throwable)null);
/*      */   }
/*      */ 
/*      */   
/*      */   public void warn(Object message, Throwable t) {
/*  614 */     this.log.warn(FQCN, message, t);
/*      */   }
/*      */ 
/*      */   
/*      */   public void warn(String loggerFqcn, Object message, Throwable t) {
/*  619 */     this.log.warn(loggerFqcn, message, t);
/*      */   }
/*      */ 
/*      */   
/*      */   public void warn(String loggerFqcn, Object message, Object[] params, Throwable t) {
/*  624 */     this.log.warn(loggerFqcn, message, params, t);
/*      */   }
/*      */ 
/*      */   
/*      */   public void warnv(String format, Object... params) {
/*  629 */     this.log.logv(FQCN, Logger.Level.WARN, (Throwable)null, format, params);
/*      */   }
/*      */ 
/*      */   
/*      */   public void warnv(String format, Object param1) {
/*  634 */     this.log.logv(FQCN, Logger.Level.WARN, (Throwable)null, format, param1);
/*      */   }
/*      */ 
/*      */   
/*      */   public void warnv(String format, Object param1, Object param2) {
/*  639 */     this.log.logv(FQCN, Logger.Level.WARN, (Throwable)null, format, param1, param2);
/*      */   }
/*      */ 
/*      */   
/*      */   public void warnv(String format, Object param1, Object param2, Object param3) {
/*  644 */     this.log.logv(FQCN, Logger.Level.WARN, (Throwable)null, format, param1, param2, param3);
/*      */   }
/*      */ 
/*      */   
/*      */   public void warnv(Throwable t, String format, Object... params) {
/*  649 */     this.log.logv(FQCN, Logger.Level.WARN, t, format, params);
/*      */   }
/*      */ 
/*      */   
/*      */   public void warnv(Throwable t, String format, Object param1) {
/*  654 */     this.log.logv(FQCN, Logger.Level.WARN, t, format, param1);
/*      */   }
/*      */ 
/*      */   
/*      */   public void warnv(Throwable t, String format, Object param1, Object param2) {
/*  659 */     this.log.logv(FQCN, Logger.Level.WARN, t, format, param1, param2);
/*      */   }
/*      */ 
/*      */   
/*      */   public void warnv(Throwable t, String format, Object param1, Object param2, Object param3) {
/*  664 */     this.log.logv(FQCN, Logger.Level.WARN, t, format, param1, param2, param3);
/*      */   }
/*      */ 
/*      */   
/*      */   public void warnf(String format, Object... params) {
/*  669 */     this.log.logf(FQCN, Logger.Level.WARN, (Throwable)null, format, params);
/*      */   }
/*      */ 
/*      */   
/*      */   public void warnf(String format, Object param1) {
/*  674 */     this.log.logf(FQCN, Logger.Level.WARN, (Throwable)null, format, param1);
/*      */   }
/*      */ 
/*      */   
/*      */   public void warnf(String format, Object param1, Object param2) {
/*  679 */     this.log.logf(FQCN, Logger.Level.WARN, (Throwable)null, format, param1, param2);
/*      */   }
/*      */ 
/*      */   
/*      */   public void warnf(String format, Object param1, Object param2, Object param3) {
/*  684 */     this.log.logf(FQCN, Logger.Level.WARN, (Throwable)null, format, param1, param2, param3);
/*      */   }
/*      */ 
/*      */   
/*      */   public void warnf(Throwable t, String format, Object... params) {
/*  689 */     this.log.logf(FQCN, Logger.Level.WARN, t, format, params);
/*      */   }
/*      */ 
/*      */   
/*      */   public void warnf(Throwable t, String format, Object param1) {
/*  694 */     this.log.logf(FQCN, Logger.Level.WARN, t, format, param1);
/*      */   }
/*      */ 
/*      */   
/*      */   public void warnf(Throwable t, String format, Object param1, Object param2) {
/*  699 */     this.log.logf(FQCN, Logger.Level.WARN, t, format, param1, param2);
/*      */   }
/*      */ 
/*      */   
/*      */   public void warnf(Throwable t, String format, Object param1, Object param2, Object param3) {
/*  704 */     this.log.logf(FQCN, Logger.Level.WARN, t, format, param1, param2, param3);
/*      */   }
/*      */ 
/*      */   
/*      */   public void error(Object message) {
/*  709 */     this.log.error(FQCN, message, (Throwable)null);
/*      */   }
/*      */ 
/*      */   
/*      */   public void error(Object message, Throwable t) {
/*  714 */     this.log.error(FQCN, message, t);
/*      */   }
/*      */ 
/*      */   
/*      */   public void error(String loggerFqcn, Object message, Throwable t) {
/*  719 */     this.log.error(loggerFqcn, message, t);
/*      */   }
/*      */ 
/*      */   
/*      */   public void error(String loggerFqcn, Object message, Object[] params, Throwable t) {
/*  724 */     this.log.error(loggerFqcn, message, params, t);
/*      */   }
/*      */ 
/*      */   
/*      */   public void errorv(String format, Object... params) {
/*  729 */     this.log.logv(FQCN, Logger.Level.ERROR, (Throwable)null, format, params);
/*      */   }
/*      */ 
/*      */   
/*      */   public void errorv(String format, Object param1) {
/*  734 */     this.log.logv(FQCN, Logger.Level.ERROR, (Throwable)null, format, param1);
/*      */   }
/*      */ 
/*      */   
/*      */   public void errorv(String format, Object param1, Object param2) {
/*  739 */     this.log.logv(FQCN, Logger.Level.ERROR, (Throwable)null, format, param1, param2);
/*      */   }
/*      */ 
/*      */   
/*      */   public void errorv(String format, Object param1, Object param2, Object param3) {
/*  744 */     this.log.logv(FQCN, Logger.Level.ERROR, (Throwable)null, format, param1, param2, param3);
/*      */   }
/*      */ 
/*      */   
/*      */   public void errorv(Throwable t, String format, Object... params) {
/*  749 */     this.log.logv(FQCN, Logger.Level.ERROR, t, format, params);
/*      */   }
/*      */ 
/*      */   
/*      */   public void errorv(Throwable t, String format, Object param1) {
/*  754 */     this.log.logv(FQCN, Logger.Level.ERROR, t, format, param1);
/*      */   }
/*      */ 
/*      */   
/*      */   public void errorv(Throwable t, String format, Object param1, Object param2) {
/*  759 */     this.log.logv(FQCN, Logger.Level.ERROR, t, format, param1, param2);
/*      */   }
/*      */ 
/*      */   
/*      */   public void errorv(Throwable t, String format, Object param1, Object param2, Object param3) {
/*  764 */     this.log.logv(FQCN, Logger.Level.ERROR, t, format, param1, param2, param3);
/*      */   }
/*      */ 
/*      */   
/*      */   public void errorf(String format, Object... params) {
/*  769 */     this.log.logf(FQCN, Logger.Level.ERROR, (Throwable)null, format, params);
/*      */   }
/*      */ 
/*      */   
/*      */   public void errorf(String format, Object param1) {
/*  774 */     this.log.logf(FQCN, Logger.Level.ERROR, (Throwable)null, format, param1);
/*      */   }
/*      */ 
/*      */   
/*      */   public void errorf(String format, Object param1, Object param2) {
/*  779 */     this.log.logf(FQCN, Logger.Level.ERROR, (Throwable)null, format, param1, param2);
/*      */   }
/*      */ 
/*      */   
/*      */   public void errorf(String format, Object param1, Object param2, Object param3) {
/*  784 */     this.log.logf(FQCN, Logger.Level.ERROR, (Throwable)null, format, param1, param2, param3);
/*      */   }
/*      */ 
/*      */   
/*      */   public void errorf(Throwable t, String format, Object... params) {
/*  789 */     this.log.logf(FQCN, Logger.Level.ERROR, t, format, params);
/*      */   }
/*      */ 
/*      */   
/*      */   public void errorf(Throwable t, String format, Object param1) {
/*  794 */     this.log.logf(FQCN, Logger.Level.ERROR, t, format, param1);
/*      */   }
/*      */ 
/*      */   
/*      */   public void errorf(Throwable t, String format, Object param1, Object param2) {
/*  799 */     this.log.logf(FQCN, Logger.Level.ERROR, t, format, param1, param2);
/*      */   }
/*      */ 
/*      */   
/*      */   public void errorf(Throwable t, String format, Object param1, Object param2, Object param3) {
/*  804 */     this.log.logf(FQCN, Logger.Level.ERROR, t, format, param1, param2, param3);
/*      */   }
/*      */ 
/*      */   
/*      */   public void fatal(Object message) {
/*  809 */     this.log.fatal(FQCN, message, (Throwable)null);
/*      */   }
/*      */ 
/*      */   
/*      */   public void fatal(Object message, Throwable t) {
/*  814 */     this.log.fatal(FQCN, message, t);
/*      */   }
/*      */ 
/*      */   
/*      */   public void fatal(String loggerFqcn, Object message, Throwable t) {
/*  819 */     this.log.fatal(loggerFqcn, message, t);
/*      */   }
/*      */ 
/*      */   
/*      */   public void fatal(String loggerFqcn, Object message, Object[] params, Throwable t) {
/*  824 */     this.log.fatal(loggerFqcn, message, params, t);
/*      */   }
/*      */ 
/*      */   
/*      */   public void fatalv(String format, Object... params) {
/*  829 */     this.log.logv(FQCN, Logger.Level.FATAL, (Throwable)null, format, params);
/*      */   }
/*      */ 
/*      */   
/*      */   public void fatalv(String format, Object param1) {
/*  834 */     this.log.logv(FQCN, Logger.Level.FATAL, (Throwable)null, format, param1);
/*      */   }
/*      */ 
/*      */   
/*      */   public void fatalv(String format, Object param1, Object param2) {
/*  839 */     this.log.logv(FQCN, Logger.Level.FATAL, (Throwable)null, format, param1, param2);
/*      */   }
/*      */ 
/*      */   
/*      */   public void fatalv(String format, Object param1, Object param2, Object param3) {
/*  844 */     this.log.logv(FQCN, Logger.Level.FATAL, (Throwable)null, format, param1, param2, param3);
/*      */   }
/*      */ 
/*      */   
/*      */   public void fatalv(Throwable t, String format, Object... params) {
/*  849 */     this.log.logv(FQCN, Logger.Level.FATAL, t, format, params);
/*      */   }
/*      */ 
/*      */   
/*      */   public void fatalv(Throwable t, String format, Object param1) {
/*  854 */     this.log.logv(FQCN, Logger.Level.FATAL, t, format, param1);
/*      */   }
/*      */ 
/*      */   
/*      */   public void fatalv(Throwable t, String format, Object param1, Object param2) {
/*  859 */     this.log.logv(FQCN, Logger.Level.FATAL, t, format, param1, param2);
/*      */   }
/*      */ 
/*      */   
/*      */   public void fatalv(Throwable t, String format, Object param1, Object param2, Object param3) {
/*  864 */     this.log.logv(FQCN, Logger.Level.FATAL, t, format, param1, param2, param3);
/*      */   }
/*      */ 
/*      */   
/*      */   public void fatalf(String format, Object... params) {
/*  869 */     this.log.logf(FQCN, Logger.Level.FATAL, (Throwable)null, format, params);
/*      */   }
/*      */ 
/*      */   
/*      */   public void fatalf(String format, Object param1) {
/*  874 */     this.log.logf(FQCN, Logger.Level.FATAL, (Throwable)null, format, param1);
/*      */   }
/*      */ 
/*      */   
/*      */   public void fatalf(String format, Object param1, Object param2) {
/*  879 */     this.log.logf(FQCN, Logger.Level.FATAL, (Throwable)null, format, param1, param2);
/*      */   }
/*      */ 
/*      */   
/*      */   public void fatalf(String format, Object param1, Object param2, Object param3) {
/*  884 */     this.log.logf(FQCN, Logger.Level.FATAL, (Throwable)null, format, param1, param2, param3);
/*      */   }
/*      */ 
/*      */   
/*      */   public void fatalf(Throwable t, String format, Object... params) {
/*  889 */     this.log.logf(FQCN, Logger.Level.FATAL, t, format, params);
/*      */   }
/*      */ 
/*      */   
/*      */   public void fatalf(Throwable t, String format, Object param1) {
/*  894 */     this.log.logf(FQCN, Logger.Level.FATAL, t, format, param1);
/*      */   }
/*      */ 
/*      */   
/*      */   public void fatalf(Throwable t, String format, Object param1, Object param2) {
/*  899 */     this.log.logf(FQCN, Logger.Level.FATAL, t, format, param1, param2);
/*      */   }
/*      */ 
/*      */   
/*      */   public void fatalf(Throwable t, String format, Object param1, Object param2, Object param3) {
/*  904 */     this.log.logf(FQCN, Logger.Level.FATAL, t, format, param1, param2, param3);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void log(Logger.Level level, Object message) {
/*  910 */     this.log.log(FQCN, level, message, (Object[])null, (Throwable)null);
/*      */   }
/*      */ 
/*      */   
/*      */   public void log(Logger.Level level, Object message, Throwable t) {
/*  915 */     this.log.log(FQCN, level, message, (Object[])null, t);
/*      */   }
/*      */ 
/*      */   
/*      */   public void log(Logger.Level level, String loggerFqcn, Object message, Throwable t) {
/*  920 */     this.log.log(level, loggerFqcn, message, t);
/*      */   }
/*      */ 
/*      */   
/*      */   public void log(String loggerFqcn, Logger.Level level, Object message, Object[] params, Throwable t) {
/*  925 */     this.log.log(loggerFqcn, level, message, params, t);
/*      */   }
/*      */ 
/*      */   
/*      */   public void logv(Logger.Level level, String format, Object... params) {
/*  930 */     this.log.logv(FQCN, level, (Throwable)null, format, params);
/*      */   }
/*      */ 
/*      */   
/*      */   public void logv(Logger.Level level, String format, Object param1) {
/*  935 */     this.log.logv(FQCN, level, (Throwable)null, format, param1);
/*      */   }
/*      */ 
/*      */   
/*      */   public void logv(Logger.Level level, String format, Object param1, Object param2) {
/*  940 */     this.log.logv(FQCN, level, (Throwable)null, format, param1, param2);
/*      */   }
/*      */ 
/*      */   
/*      */   public void logv(Logger.Level level, String format, Object param1, Object param2, Object param3) {
/*  945 */     this.log.logv(FQCN, level, (Throwable)null, format, param1, param2, param3);
/*      */   }
/*      */ 
/*      */   
/*      */   public void logv(Logger.Level level, Throwable t, String format, Object... params) {
/*  950 */     this.log.logv(FQCN, level, t, format, params);
/*      */   }
/*      */ 
/*      */   
/*      */   public void logv(Logger.Level level, Throwable t, String format, Object param1) {
/*  955 */     this.log.logv(FQCN, level, t, format, param1);
/*      */   }
/*      */ 
/*      */   
/*      */   public void logv(Logger.Level level, Throwable t, String format, Object param1, Object param2) {
/*  960 */     this.log.logv(FQCN, level, t, format, param1, param2);
/*      */   }
/*      */ 
/*      */   
/*      */   public void logv(Logger.Level level, Throwable t, String format, Object param1, Object param2, Object param3) {
/*  965 */     this.log.logv(FQCN, level, t, format, param1, param2, param3);
/*      */   }
/*      */ 
/*      */   
/*      */   public void logv(String loggerFqcn, Logger.Level level, Throwable t, String format, Object... params) {
/*  970 */     this.log.logv(loggerFqcn, level, t, format, params);
/*      */   }
/*      */ 
/*      */   
/*      */   public void logv(String loggerFqcn, Logger.Level level, Throwable t, String format, Object param1) {
/*  975 */     this.log.logv(loggerFqcn, level, t, format, param1);
/*      */   }
/*      */ 
/*      */   
/*      */   public void logv(String loggerFqcn, Logger.Level level, Throwable t, String format, Object param1, Object param2) {
/*  980 */     this.log.logv(loggerFqcn, level, t, format, param1, param2);
/*      */   }
/*      */ 
/*      */   
/*      */   public void logv(String loggerFqcn, Logger.Level level, Throwable t, String format, Object param1, Object param2, Object param3) {
/*  985 */     this.log.logv(loggerFqcn, level, t, format, param1, param2, param3);
/*      */   }
/*      */ 
/*      */   
/*      */   public void logf(Logger.Level level, String format, Object... params) {
/*  990 */     this.log.logf(FQCN, level, (Throwable)null, format, params);
/*      */   }
/*      */ 
/*      */   
/*      */   public void logf(Logger.Level level, String format, Object param1) {
/*  995 */     this.log.logf(FQCN, level, (Throwable)null, format, param1);
/*      */   }
/*      */ 
/*      */   
/*      */   public void logf(Logger.Level level, String format, Object param1, Object param2) {
/* 1000 */     this.log.logf(FQCN, level, (Throwable)null, format, param1, param2);
/*      */   }
/*      */ 
/*      */   
/*      */   public void logf(Logger.Level level, String format, Object param1, Object param2, Object param3) {
/* 1005 */     this.log.logf(FQCN, level, (Throwable)null, format, param1, param2, param3);
/*      */   }
/*      */ 
/*      */   
/*      */   public void logf(Logger.Level level, Throwable t, String format, Object... params) {
/* 1010 */     this.log.logf(FQCN, level, t, format, params);
/*      */   }
/*      */ 
/*      */   
/*      */   public void logf(Logger.Level level, Throwable t, String format, Object param1) {
/* 1015 */     this.log.logf(FQCN, level, t, format, param1);
/*      */   }
/*      */ 
/*      */   
/*      */   public void logf(Logger.Level level, Throwable t, String format, Object param1, Object param2) {
/* 1020 */     this.log.logf(FQCN, level, t, format, param1, param2);
/*      */   }
/*      */ 
/*      */   
/*      */   public void logf(Logger.Level level, Throwable t, String format, Object param1, Object param2, Object param3) {
/* 1025 */     this.log.logf(FQCN, level, t, format, param1, param2, param3);
/*      */   }
/*      */ 
/*      */   
/*      */   public void logf(String loggerFqcn, Logger.Level level, Throwable t, String format, Object param1) {
/* 1030 */     this.log.logf(loggerFqcn, level, t, format, param1);
/*      */   }
/*      */ 
/*      */   
/*      */   public void logf(String loggerFqcn, Logger.Level level, Throwable t, String format, Object param1, Object param2) {
/* 1035 */     this.log.logf(loggerFqcn, level, t, format, param1, param2);
/*      */   }
/*      */ 
/*      */   
/*      */   public void logf(String loggerFqcn, Logger.Level level, Throwable t, String format, Object param1, Object param2, Object param3) {
/* 1040 */     this.log.logf(loggerFqcn, level, t, format, param1, param2, param3);
/*      */   }
/*      */ 
/*      */   
/*      */   public void logf(String loggerFqcn, Logger.Level level, Throwable t, String format, Object... params) {
/* 1045 */     this.log.logf(loggerFqcn, level, t, format, params);
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean isEnabled(Logger.Level level) {
/* 1050 */     return this.log.isEnabled(level);
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\jboss\logging\DelegatingBasicLogger.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */