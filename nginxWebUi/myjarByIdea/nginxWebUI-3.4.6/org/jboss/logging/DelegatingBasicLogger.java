package org.jboss.logging;

import java.io.Serializable;

public class DelegatingBasicLogger implements BasicLogger, Serializable {
   private static final long serialVersionUID = -5774903162389601853L;
   private static final String FQCN = DelegatingBasicLogger.class.getName();
   protected final Logger log;

   public DelegatingBasicLogger(Logger log) {
      this.log = log;
   }

   public boolean isTraceEnabled() {
      return this.log.isTraceEnabled();
   }

   public void trace(Object message) {
      this.log.trace((String)FQCN, (Object)message, (Throwable)null);
   }

   public void trace(Object message, Throwable t) {
      this.log.trace(FQCN, message, t);
   }

   public void trace(String loggerFqcn, Object message, Throwable t) {
      this.log.trace(loggerFqcn, message, t);
   }

   public void trace(String loggerFqcn, Object message, Object[] params, Throwable t) {
      this.log.trace(loggerFqcn, message, params, t);
   }

   public void tracev(String format, Object... params) {
      this.log.logv((String)FQCN, (Logger.Level)Logger.Level.TRACE, (Throwable)null, (String)format, (Object[])params);
   }

   public void tracev(String format, Object param1) {
      this.log.logv((String)FQCN, (Logger.Level)Logger.Level.TRACE, (Throwable)null, (String)format, (Object)param1);
   }

   public void tracev(String format, Object param1, Object param2) {
      this.log.logv((String)FQCN, (Logger.Level)Logger.Level.TRACE, (Throwable)null, (String)format, param1, param2);
   }

   public void tracev(String format, Object param1, Object param2, Object param3) {
      this.log.logv(FQCN, Logger.Level.TRACE, (Throwable)null, format, param1, param2, param3);
   }

   public void tracev(Throwable t, String format, Object... params) {
      this.log.logv(FQCN, Logger.Level.TRACE, t, format, params);
   }

   public void tracev(Throwable t, String format, Object param1) {
      this.log.logv(FQCN, Logger.Level.TRACE, t, format, param1);
   }

   public void tracev(Throwable t, String format, Object param1, Object param2) {
      this.log.logv(FQCN, Logger.Level.TRACE, t, format, param1, param2);
   }

   public void tracev(Throwable t, String format, Object param1, Object param2, Object param3) {
      this.log.logv(FQCN, Logger.Level.TRACE, t, format, param1, param2, param3);
   }

   public void tracef(String format, Object... params) {
      this.log.logf((String)FQCN, (Logger.Level)Logger.Level.TRACE, (Throwable)null, (String)format, (Object[])params);
   }

   public void tracef(String format, Object param1) {
      this.log.logf((String)FQCN, (Logger.Level)Logger.Level.TRACE, (Throwable)null, (String)format, (Object)param1);
   }

   public void tracef(String format, Object param1, Object param2) {
      this.log.logf((String)FQCN, (Logger.Level)Logger.Level.TRACE, (Throwable)null, (String)format, param1, param2);
   }

   public void tracef(String format, Object param1, Object param2, Object param3) {
      this.log.logf(FQCN, Logger.Level.TRACE, (Throwable)null, format, param1, param2, param3);
   }

   public void tracef(Throwable t, String format, Object... params) {
      this.log.logf(FQCN, Logger.Level.TRACE, t, format, params);
   }

   public void tracef(Throwable t, String format, Object param1) {
      this.log.logf(FQCN, Logger.Level.TRACE, t, format, param1);
   }

   public void tracef(Throwable t, String format, Object param1, Object param2) {
      this.log.logf(FQCN, Logger.Level.TRACE, t, format, param1, param2);
   }

   public void tracef(Throwable t, String format, Object param1, Object param2, Object param3) {
      this.log.logf(FQCN, Logger.Level.TRACE, t, format, param1, param2, param3);
   }

   public void tracef(String format, int arg) {
      this.log.tracef(format, arg);
   }

   public void tracef(String format, int arg1, int arg2) {
      this.log.tracef(format, arg1, arg2);
   }

   public void tracef(String format, int arg1, Object arg2) {
      this.log.tracef(format, arg1, arg2);
   }

   public void tracef(String format, int arg1, int arg2, int arg3) {
      this.log.tracef(format, arg1, arg2, arg3);
   }

   public void tracef(String format, int arg1, int arg2, Object arg3) {
      this.log.tracef(format, arg1, arg2, arg3);
   }

   public void tracef(String format, int arg1, Object arg2, Object arg3) {
      this.log.tracef(format, arg1, arg2, arg3);
   }

   public void tracef(Throwable t, String format, int arg) {
      this.log.tracef(t, format, arg);
   }

   public void tracef(Throwable t, String format, int arg1, int arg2) {
      this.log.tracef(t, format, arg1, arg2);
   }

   public void tracef(Throwable t, String format, int arg1, Object arg2) {
      this.log.tracef(t, format, arg1, arg2);
   }

   public void tracef(Throwable t, String format, int arg1, int arg2, int arg3) {
      this.log.tracef(t, format, arg1, arg2, arg3);
   }

   public void tracef(Throwable t, String format, int arg1, int arg2, Object arg3) {
      this.log.tracef(t, format, arg1, arg2, arg3);
   }

   public void tracef(Throwable t, String format, int arg1, Object arg2, Object arg3) {
      this.log.tracef(t, format, arg1, arg2, arg3);
   }

   public void tracef(String format, long arg) {
      this.log.tracef(format, arg);
   }

   public void tracef(String format, long arg1, long arg2) {
      this.log.tracef(format, arg1, arg2);
   }

   public void tracef(String format, long arg1, Object arg2) {
      this.log.tracef(format, arg1, arg2);
   }

   public void tracef(String format, long arg1, long arg2, long arg3) {
      this.log.tracef(format, arg1, arg2, arg3);
   }

   public void tracef(String format, long arg1, long arg2, Object arg3) {
      this.log.tracef(format, arg1, arg2, arg3);
   }

   public void tracef(String format, long arg1, Object arg2, Object arg3) {
      this.log.tracef(format, arg1, arg2, arg3);
   }

   public void tracef(Throwable t, String format, long arg) {
      this.log.tracef(t, format, arg);
   }

   public void tracef(Throwable t, String format, long arg1, long arg2) {
      this.log.tracef(t, format, arg1, arg2);
   }

   public void tracef(Throwable t, String format, long arg1, Object arg2) {
      this.log.tracef(t, format, arg1, arg2);
   }

   public void tracef(Throwable t, String format, long arg1, long arg2, long arg3) {
      this.log.tracef(t, format, arg1, arg2, arg3);
   }

   public void tracef(Throwable t, String format, long arg1, long arg2, Object arg3) {
      this.log.tracef(t, format, arg1, arg2, arg3);
   }

   public void tracef(Throwable t, String format, long arg1, Object arg2, Object arg3) {
      this.log.tracef(t, format, arg1, arg2, arg3);
   }

   public boolean isDebugEnabled() {
      return this.log.isDebugEnabled();
   }

   public void debug(Object message) {
      this.log.debug((String)FQCN, (Object)message, (Throwable)null);
   }

   public void debug(Object message, Throwable t) {
      this.log.debug(FQCN, message, t);
   }

   public void debug(String loggerFqcn, Object message, Throwable t) {
      this.log.debug(loggerFqcn, message, t);
   }

   public void debug(String loggerFqcn, Object message, Object[] params, Throwable t) {
      this.log.debug(loggerFqcn, message, params, t);
   }

   public void debugv(String format, Object... params) {
      this.log.logv((String)FQCN, (Logger.Level)Logger.Level.DEBUG, (Throwable)null, (String)format, (Object[])params);
   }

   public void debugv(String format, Object param1) {
      this.log.logv((String)FQCN, (Logger.Level)Logger.Level.DEBUG, (Throwable)null, (String)format, (Object)param1);
   }

   public void debugv(String format, Object param1, Object param2) {
      this.log.logv((String)FQCN, (Logger.Level)Logger.Level.DEBUG, (Throwable)null, (String)format, param1, param2);
   }

   public void debugv(String format, Object param1, Object param2, Object param3) {
      this.log.logv(FQCN, Logger.Level.DEBUG, (Throwable)null, format, param1, param2, param3);
   }

   public void debugv(Throwable t, String format, Object... params) {
      this.log.logv(FQCN, Logger.Level.DEBUG, t, format, params);
   }

   public void debugv(Throwable t, String format, Object param1) {
      this.log.logv(FQCN, Logger.Level.DEBUG, t, format, param1);
   }

   public void debugv(Throwable t, String format, Object param1, Object param2) {
      this.log.logv(FQCN, Logger.Level.DEBUG, t, format, param1, param2);
   }

   public void debugv(Throwable t, String format, Object param1, Object param2, Object param3) {
      this.log.logv(FQCN, Logger.Level.DEBUG, t, format, param1, param2, param3);
   }

   public void debugf(String format, Object... params) {
      this.log.logf((String)FQCN, (Logger.Level)Logger.Level.DEBUG, (Throwable)null, (String)format, (Object[])params);
   }

   public void debugf(String format, Object param1) {
      this.log.logf((String)FQCN, (Logger.Level)Logger.Level.DEBUG, (Throwable)null, (String)format, (Object)param1);
   }

   public void debugf(String format, Object param1, Object param2) {
      this.log.logf((String)FQCN, (Logger.Level)Logger.Level.DEBUG, (Throwable)null, (String)format, param1, param2);
   }

   public void debugf(String format, Object param1, Object param2, Object param3) {
      this.log.logf(FQCN, Logger.Level.DEBUG, (Throwable)null, format, param1, param2, param3);
   }

   public void debugf(Throwable t, String format, Object... params) {
      this.log.logf(FQCN, Logger.Level.DEBUG, t, format, params);
   }

   public void debugf(Throwable t, String format, Object param1) {
      this.log.logf(FQCN, Logger.Level.DEBUG, t, format, param1);
   }

   public void debugf(Throwable t, String format, Object param1, Object param2) {
      this.log.logf(FQCN, Logger.Level.DEBUG, t, format, param1, param2);
   }

   public void debugf(Throwable t, String format, Object param1, Object param2, Object param3) {
      this.log.logf(FQCN, Logger.Level.DEBUG, t, format, param1, param2, param3);
   }

   public void debugf(String format, int arg) {
      this.log.debugf(format, arg);
   }

   public void debugf(String format, int arg1, int arg2) {
      this.log.debugf(format, arg1, arg2);
   }

   public void debugf(String format, int arg1, Object arg2) {
      this.log.debugf(format, arg1, arg2);
   }

   public void debugf(String format, int arg1, int arg2, int arg3) {
      this.log.debugf(format, arg1, arg2, arg3);
   }

   public void debugf(String format, int arg1, int arg2, Object arg3) {
      this.log.debugf(format, arg1, arg2, arg3);
   }

   public void debugf(String format, int arg1, Object arg2, Object arg3) {
      this.log.debugf(format, arg1, arg2, arg3);
   }

   public void debugf(Throwable t, String format, int arg) {
      this.log.debugf(t, format, arg);
   }

   public void debugf(Throwable t, String format, int arg1, int arg2) {
      this.log.debugf(t, format, arg1, arg2);
   }

   public void debugf(Throwable t, String format, int arg1, Object arg2) {
      this.log.debugf(t, format, arg1, arg2);
   }

   public void debugf(Throwable t, String format, int arg1, int arg2, int arg3) {
      this.log.debugf(t, format, arg1, arg2, arg3);
   }

   public void debugf(Throwable t, String format, int arg1, int arg2, Object arg3) {
      this.log.debugf(t, format, arg1, arg2, arg3);
   }

   public void debugf(Throwable t, String format, int arg1, Object arg2, Object arg3) {
      this.log.debugf(t, format, arg1, arg2, arg3);
   }

   public void debugf(String format, long arg) {
      this.log.debugf(format, arg);
   }

   public void debugf(String format, long arg1, long arg2) {
      this.log.debugf(format, arg1, arg2);
   }

   public void debugf(String format, long arg1, Object arg2) {
      this.log.debugf(format, arg1, arg2);
   }

   public void debugf(String format, long arg1, long arg2, long arg3) {
      this.log.debugf(format, arg1, arg2, arg3);
   }

   public void debugf(String format, long arg1, long arg2, Object arg3) {
      this.log.debugf(format, arg1, arg2, arg3);
   }

   public void debugf(String format, long arg1, Object arg2, Object arg3) {
      this.log.debugf(format, arg1, arg2, arg3);
   }

   public void debugf(Throwable t, String format, long arg) {
      this.log.debugf(t, format, arg);
   }

   public void debugf(Throwable t, String format, long arg1, long arg2) {
      this.log.debugf(t, format, arg1, arg2);
   }

   public void debugf(Throwable t, String format, long arg1, Object arg2) {
      this.log.debugf(t, format, arg1, arg2);
   }

   public void debugf(Throwable t, String format, long arg1, long arg2, long arg3) {
      this.log.debugf(t, format, arg1, arg2, arg3);
   }

   public void debugf(Throwable t, String format, long arg1, long arg2, Object arg3) {
      this.log.debugf(t, format, arg1, arg2, arg3);
   }

   public void debugf(Throwable t, String format, long arg1, Object arg2, Object arg3) {
      this.log.debugf(t, format, arg1, arg2, arg3);
   }

   public boolean isInfoEnabled() {
      return this.log.isInfoEnabled();
   }

   public void info(Object message) {
      this.log.info((String)FQCN, (Object)message, (Throwable)null);
   }

   public void info(Object message, Throwable t) {
      this.log.info(FQCN, message, t);
   }

   public void info(String loggerFqcn, Object message, Throwable t) {
      this.log.info(loggerFqcn, message, t);
   }

   public void info(String loggerFqcn, Object message, Object[] params, Throwable t) {
      this.log.info(loggerFqcn, message, params, t);
   }

   public void infov(String format, Object... params) {
      this.log.logv((String)FQCN, (Logger.Level)Logger.Level.INFO, (Throwable)null, (String)format, (Object[])params);
   }

   public void infov(String format, Object param1) {
      this.log.logv((String)FQCN, (Logger.Level)Logger.Level.INFO, (Throwable)null, (String)format, (Object)param1);
   }

   public void infov(String format, Object param1, Object param2) {
      this.log.logv((String)FQCN, (Logger.Level)Logger.Level.INFO, (Throwable)null, (String)format, param1, param2);
   }

   public void infov(String format, Object param1, Object param2, Object param3) {
      this.log.logv(FQCN, Logger.Level.INFO, (Throwable)null, format, param1, param2, param3);
   }

   public void infov(Throwable t, String format, Object... params) {
      this.log.logv(FQCN, Logger.Level.INFO, t, format, params);
   }

   public void infov(Throwable t, String format, Object param1) {
      this.log.logv(FQCN, Logger.Level.INFO, t, format, param1);
   }

   public void infov(Throwable t, String format, Object param1, Object param2) {
      this.log.logv(FQCN, Logger.Level.INFO, t, format, param1, param2);
   }

   public void infov(Throwable t, String format, Object param1, Object param2, Object param3) {
      this.log.logv(FQCN, Logger.Level.INFO, t, format, param1, param2, param3);
   }

   public void infof(String format, Object... params) {
      this.log.logf((String)FQCN, (Logger.Level)Logger.Level.INFO, (Throwable)null, (String)format, (Object[])params);
   }

   public void infof(String format, Object param1) {
      this.log.logf((String)FQCN, (Logger.Level)Logger.Level.INFO, (Throwable)null, (String)format, (Object)param1);
   }

   public void infof(String format, Object param1, Object param2) {
      this.log.logf((String)FQCN, (Logger.Level)Logger.Level.INFO, (Throwable)null, (String)format, param1, param2);
   }

   public void infof(String format, Object param1, Object param2, Object param3) {
      this.log.logf(FQCN, Logger.Level.INFO, (Throwable)null, format, param1, param2, param3);
   }

   public void infof(Throwable t, String format, Object... params) {
      this.log.logf(FQCN, Logger.Level.INFO, t, format, params);
   }

   public void infof(Throwable t, String format, Object param1) {
      this.log.logf(FQCN, Logger.Level.INFO, t, format, param1);
   }

   public void infof(Throwable t, String format, Object param1, Object param2) {
      this.log.logf(FQCN, Logger.Level.INFO, t, format, param1, param2);
   }

   public void infof(Throwable t, String format, Object param1, Object param2, Object param3) {
      this.log.logf(FQCN, Logger.Level.INFO, t, format, param1, param2, param3);
   }

   public void warn(Object message) {
      this.log.warn((String)FQCN, (Object)message, (Throwable)null);
   }

   public void warn(Object message, Throwable t) {
      this.log.warn(FQCN, message, t);
   }

   public void warn(String loggerFqcn, Object message, Throwable t) {
      this.log.warn(loggerFqcn, message, t);
   }

   public void warn(String loggerFqcn, Object message, Object[] params, Throwable t) {
      this.log.warn(loggerFqcn, message, params, t);
   }

   public void warnv(String format, Object... params) {
      this.log.logv((String)FQCN, (Logger.Level)Logger.Level.WARN, (Throwable)null, (String)format, (Object[])params);
   }

   public void warnv(String format, Object param1) {
      this.log.logv((String)FQCN, (Logger.Level)Logger.Level.WARN, (Throwable)null, (String)format, (Object)param1);
   }

   public void warnv(String format, Object param1, Object param2) {
      this.log.logv((String)FQCN, (Logger.Level)Logger.Level.WARN, (Throwable)null, (String)format, param1, param2);
   }

   public void warnv(String format, Object param1, Object param2, Object param3) {
      this.log.logv(FQCN, Logger.Level.WARN, (Throwable)null, format, param1, param2, param3);
   }

   public void warnv(Throwable t, String format, Object... params) {
      this.log.logv(FQCN, Logger.Level.WARN, t, format, params);
   }

   public void warnv(Throwable t, String format, Object param1) {
      this.log.logv(FQCN, Logger.Level.WARN, t, format, param1);
   }

   public void warnv(Throwable t, String format, Object param1, Object param2) {
      this.log.logv(FQCN, Logger.Level.WARN, t, format, param1, param2);
   }

   public void warnv(Throwable t, String format, Object param1, Object param2, Object param3) {
      this.log.logv(FQCN, Logger.Level.WARN, t, format, param1, param2, param3);
   }

   public void warnf(String format, Object... params) {
      this.log.logf((String)FQCN, (Logger.Level)Logger.Level.WARN, (Throwable)null, (String)format, (Object[])params);
   }

   public void warnf(String format, Object param1) {
      this.log.logf((String)FQCN, (Logger.Level)Logger.Level.WARN, (Throwable)null, (String)format, (Object)param1);
   }

   public void warnf(String format, Object param1, Object param2) {
      this.log.logf((String)FQCN, (Logger.Level)Logger.Level.WARN, (Throwable)null, (String)format, param1, param2);
   }

   public void warnf(String format, Object param1, Object param2, Object param3) {
      this.log.logf(FQCN, Logger.Level.WARN, (Throwable)null, format, param1, param2, param3);
   }

   public void warnf(Throwable t, String format, Object... params) {
      this.log.logf(FQCN, Logger.Level.WARN, t, format, params);
   }

   public void warnf(Throwable t, String format, Object param1) {
      this.log.logf(FQCN, Logger.Level.WARN, t, format, param1);
   }

   public void warnf(Throwable t, String format, Object param1, Object param2) {
      this.log.logf(FQCN, Logger.Level.WARN, t, format, param1, param2);
   }

   public void warnf(Throwable t, String format, Object param1, Object param2, Object param3) {
      this.log.logf(FQCN, Logger.Level.WARN, t, format, param1, param2, param3);
   }

   public void error(Object message) {
      this.log.error((String)FQCN, (Object)message, (Throwable)null);
   }

   public void error(Object message, Throwable t) {
      this.log.error(FQCN, message, t);
   }

   public void error(String loggerFqcn, Object message, Throwable t) {
      this.log.error(loggerFqcn, message, t);
   }

   public void error(String loggerFqcn, Object message, Object[] params, Throwable t) {
      this.log.error(loggerFqcn, message, params, t);
   }

   public void errorv(String format, Object... params) {
      this.log.logv((String)FQCN, (Logger.Level)Logger.Level.ERROR, (Throwable)null, (String)format, (Object[])params);
   }

   public void errorv(String format, Object param1) {
      this.log.logv((String)FQCN, (Logger.Level)Logger.Level.ERROR, (Throwable)null, (String)format, (Object)param1);
   }

   public void errorv(String format, Object param1, Object param2) {
      this.log.logv((String)FQCN, (Logger.Level)Logger.Level.ERROR, (Throwable)null, (String)format, param1, param2);
   }

   public void errorv(String format, Object param1, Object param2, Object param3) {
      this.log.logv(FQCN, Logger.Level.ERROR, (Throwable)null, format, param1, param2, param3);
   }

   public void errorv(Throwable t, String format, Object... params) {
      this.log.logv(FQCN, Logger.Level.ERROR, t, format, params);
   }

   public void errorv(Throwable t, String format, Object param1) {
      this.log.logv(FQCN, Logger.Level.ERROR, t, format, param1);
   }

   public void errorv(Throwable t, String format, Object param1, Object param2) {
      this.log.logv(FQCN, Logger.Level.ERROR, t, format, param1, param2);
   }

   public void errorv(Throwable t, String format, Object param1, Object param2, Object param3) {
      this.log.logv(FQCN, Logger.Level.ERROR, t, format, param1, param2, param3);
   }

   public void errorf(String format, Object... params) {
      this.log.logf((String)FQCN, (Logger.Level)Logger.Level.ERROR, (Throwable)null, (String)format, (Object[])params);
   }

   public void errorf(String format, Object param1) {
      this.log.logf((String)FQCN, (Logger.Level)Logger.Level.ERROR, (Throwable)null, (String)format, (Object)param1);
   }

   public void errorf(String format, Object param1, Object param2) {
      this.log.logf((String)FQCN, (Logger.Level)Logger.Level.ERROR, (Throwable)null, (String)format, param1, param2);
   }

   public void errorf(String format, Object param1, Object param2, Object param3) {
      this.log.logf(FQCN, Logger.Level.ERROR, (Throwable)null, format, param1, param2, param3);
   }

   public void errorf(Throwable t, String format, Object... params) {
      this.log.logf(FQCN, Logger.Level.ERROR, t, format, params);
   }

   public void errorf(Throwable t, String format, Object param1) {
      this.log.logf(FQCN, Logger.Level.ERROR, t, format, param1);
   }

   public void errorf(Throwable t, String format, Object param1, Object param2) {
      this.log.logf(FQCN, Logger.Level.ERROR, t, format, param1, param2);
   }

   public void errorf(Throwable t, String format, Object param1, Object param2, Object param3) {
      this.log.logf(FQCN, Logger.Level.ERROR, t, format, param1, param2, param3);
   }

   public void fatal(Object message) {
      this.log.fatal((String)FQCN, (Object)message, (Throwable)null);
   }

   public void fatal(Object message, Throwable t) {
      this.log.fatal(FQCN, message, t);
   }

   public void fatal(String loggerFqcn, Object message, Throwable t) {
      this.log.fatal(loggerFqcn, message, t);
   }

   public void fatal(String loggerFqcn, Object message, Object[] params, Throwable t) {
      this.log.fatal(loggerFqcn, message, params, t);
   }

   public void fatalv(String format, Object... params) {
      this.log.logv((String)FQCN, (Logger.Level)Logger.Level.FATAL, (Throwable)null, (String)format, (Object[])params);
   }

   public void fatalv(String format, Object param1) {
      this.log.logv((String)FQCN, (Logger.Level)Logger.Level.FATAL, (Throwable)null, (String)format, (Object)param1);
   }

   public void fatalv(String format, Object param1, Object param2) {
      this.log.logv((String)FQCN, (Logger.Level)Logger.Level.FATAL, (Throwable)null, (String)format, param1, param2);
   }

   public void fatalv(String format, Object param1, Object param2, Object param3) {
      this.log.logv(FQCN, Logger.Level.FATAL, (Throwable)null, format, param1, param2, param3);
   }

   public void fatalv(Throwable t, String format, Object... params) {
      this.log.logv(FQCN, Logger.Level.FATAL, t, format, params);
   }

   public void fatalv(Throwable t, String format, Object param1) {
      this.log.logv(FQCN, Logger.Level.FATAL, t, format, param1);
   }

   public void fatalv(Throwable t, String format, Object param1, Object param2) {
      this.log.logv(FQCN, Logger.Level.FATAL, t, format, param1, param2);
   }

   public void fatalv(Throwable t, String format, Object param1, Object param2, Object param3) {
      this.log.logv(FQCN, Logger.Level.FATAL, t, format, param1, param2, param3);
   }

   public void fatalf(String format, Object... params) {
      this.log.logf((String)FQCN, (Logger.Level)Logger.Level.FATAL, (Throwable)null, (String)format, (Object[])params);
   }

   public void fatalf(String format, Object param1) {
      this.log.logf((String)FQCN, (Logger.Level)Logger.Level.FATAL, (Throwable)null, (String)format, (Object)param1);
   }

   public void fatalf(String format, Object param1, Object param2) {
      this.log.logf((String)FQCN, (Logger.Level)Logger.Level.FATAL, (Throwable)null, (String)format, param1, param2);
   }

   public void fatalf(String format, Object param1, Object param2, Object param3) {
      this.log.logf(FQCN, Logger.Level.FATAL, (Throwable)null, format, param1, param2, param3);
   }

   public void fatalf(Throwable t, String format, Object... params) {
      this.log.logf(FQCN, Logger.Level.FATAL, t, format, params);
   }

   public void fatalf(Throwable t, String format, Object param1) {
      this.log.logf(FQCN, Logger.Level.FATAL, t, format, param1);
   }

   public void fatalf(Throwable t, String format, Object param1, Object param2) {
      this.log.logf(FQCN, Logger.Level.FATAL, t, format, param1, param2);
   }

   public void fatalf(Throwable t, String format, Object param1, Object param2, Object param3) {
      this.log.logf(FQCN, Logger.Level.FATAL, t, format, param1, param2, param3);
   }

   public void log(Logger.Level level, Object message) {
      this.log.log(FQCN, level, message, (Object[])null, (Throwable)null);
   }

   public void log(Logger.Level level, Object message, Throwable t) {
      this.log.log(FQCN, level, message, (Object[])null, t);
   }

   public void log(Logger.Level level, String loggerFqcn, Object message, Throwable t) {
      this.log.log(level, loggerFqcn, message, t);
   }

   public void log(String loggerFqcn, Logger.Level level, Object message, Object[] params, Throwable t) {
      this.log.log(loggerFqcn, level, message, params, t);
   }

   public void logv(Logger.Level level, String format, Object... params) {
      this.log.logv((String)FQCN, (Logger.Level)level, (Throwable)null, (String)format, (Object[])params);
   }

   public void logv(Logger.Level level, String format, Object param1) {
      this.log.logv((String)FQCN, (Logger.Level)level, (Throwable)null, (String)format, (Object)param1);
   }

   public void logv(Logger.Level level, String format, Object param1, Object param2) {
      this.log.logv((String)FQCN, (Logger.Level)level, (Throwable)null, (String)format, param1, param2);
   }

   public void logv(Logger.Level level, String format, Object param1, Object param2, Object param3) {
      this.log.logv(FQCN, level, (Throwable)null, format, param1, param2, param3);
   }

   public void logv(Logger.Level level, Throwable t, String format, Object... params) {
      this.log.logv(FQCN, level, t, format, params);
   }

   public void logv(Logger.Level level, Throwable t, String format, Object param1) {
      this.log.logv(FQCN, level, t, format, param1);
   }

   public void logv(Logger.Level level, Throwable t, String format, Object param1, Object param2) {
      this.log.logv(FQCN, level, t, format, param1, param2);
   }

   public void logv(Logger.Level level, Throwable t, String format, Object param1, Object param2, Object param3) {
      this.log.logv(FQCN, level, t, format, param1, param2, param3);
   }

   public void logv(String loggerFqcn, Logger.Level level, Throwable t, String format, Object... params) {
      this.log.logv(loggerFqcn, level, t, format, params);
   }

   public void logv(String loggerFqcn, Logger.Level level, Throwable t, String format, Object param1) {
      this.log.logv(loggerFqcn, level, t, format, param1);
   }

   public void logv(String loggerFqcn, Logger.Level level, Throwable t, String format, Object param1, Object param2) {
      this.log.logv(loggerFqcn, level, t, format, param1, param2);
   }

   public void logv(String loggerFqcn, Logger.Level level, Throwable t, String format, Object param1, Object param2, Object param3) {
      this.log.logv(loggerFqcn, level, t, format, param1, param2, param3);
   }

   public void logf(Logger.Level level, String format, Object... params) {
      this.log.logf((String)FQCN, (Logger.Level)level, (Throwable)null, (String)format, (Object[])params);
   }

   public void logf(Logger.Level level, String format, Object param1) {
      this.log.logf((String)FQCN, (Logger.Level)level, (Throwable)null, (String)format, (Object)param1);
   }

   public void logf(Logger.Level level, String format, Object param1, Object param2) {
      this.log.logf((String)FQCN, (Logger.Level)level, (Throwable)null, (String)format, param1, param2);
   }

   public void logf(Logger.Level level, String format, Object param1, Object param2, Object param3) {
      this.log.logf(FQCN, level, (Throwable)null, format, param1, param2, param3);
   }

   public void logf(Logger.Level level, Throwable t, String format, Object... params) {
      this.log.logf(FQCN, level, t, format, params);
   }

   public void logf(Logger.Level level, Throwable t, String format, Object param1) {
      this.log.logf(FQCN, level, t, format, param1);
   }

   public void logf(Logger.Level level, Throwable t, String format, Object param1, Object param2) {
      this.log.logf(FQCN, level, t, format, param1, param2);
   }

   public void logf(Logger.Level level, Throwable t, String format, Object param1, Object param2, Object param3) {
      this.log.logf(FQCN, level, t, format, param1, param2, param3);
   }

   public void logf(String loggerFqcn, Logger.Level level, Throwable t, String format, Object param1) {
      this.log.logf(loggerFqcn, level, t, format, param1);
   }

   public void logf(String loggerFqcn, Logger.Level level, Throwable t, String format, Object param1, Object param2) {
      this.log.logf(loggerFqcn, level, t, format, param1, param2);
   }

   public void logf(String loggerFqcn, Logger.Level level, Throwable t, String format, Object param1, Object param2, Object param3) {
      this.log.logf(loggerFqcn, level, t, format, param1, param2, param3);
   }

   public void logf(String loggerFqcn, Logger.Level level, Throwable t, String format, Object... params) {
      this.log.logf(loggerFqcn, level, t, format, params);
   }

   public boolean isEnabled(Logger.Level level) {
      return this.log.isEnabled(level);
   }
}
