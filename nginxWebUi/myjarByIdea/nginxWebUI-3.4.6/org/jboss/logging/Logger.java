package org.jboss.logging;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Locale;

public abstract class Logger implements Serializable, BasicLogger {
   private static final long serialVersionUID = 4232175575988879434L;
   private static final String FQCN = Logger.class.getName();
   private final String name;

   protected Logger(String name) {
      this.name = name;
   }

   public String getName() {
      return this.name;
   }

   protected abstract void doLog(Level var1, String var2, Object var3, Object[] var4, Throwable var5);

   protected abstract void doLogf(Level var1, String var2, String var3, Object[] var4, Throwable var5);

   public boolean isTraceEnabled() {
      return this.isEnabled(Logger.Level.TRACE);
   }

   public void trace(Object message) {
      this.doLog(Logger.Level.TRACE, FQCN, message, (Object[])null, (Throwable)null);
   }

   public void trace(Object message, Throwable t) {
      this.doLog(Logger.Level.TRACE, FQCN, message, (Object[])null, t);
   }

   public void trace(String loggerFqcn, Object message, Throwable t) {
      this.doLog(Logger.Level.TRACE, loggerFqcn, message, (Object[])null, t);
   }

   /** @deprecated */
   @Deprecated
   public void trace(Object message, Object[] params) {
      this.doLog(Logger.Level.TRACE, FQCN, message, params, (Throwable)null);
   }

   /** @deprecated */
   @Deprecated
   public void trace(Object message, Object[] params, Throwable t) {
      this.doLog(Logger.Level.TRACE, FQCN, message, params, t);
   }

   public void trace(String loggerFqcn, Object message, Object[] params, Throwable t) {
      this.doLog(Logger.Level.TRACE, loggerFqcn, message, params, t);
   }

   public void tracev(String format, Object... params) {
      this.doLog(Logger.Level.TRACE, FQCN, format, params, (Throwable)null);
   }

   public void tracev(String format, Object param1) {
      if (this.isEnabled(Logger.Level.TRACE)) {
         this.doLog(Logger.Level.TRACE, FQCN, format, new Object[]{param1}, (Throwable)null);
      }

   }

   public void tracev(String format, Object param1, Object param2) {
      if (this.isEnabled(Logger.Level.TRACE)) {
         this.doLog(Logger.Level.TRACE, FQCN, format, new Object[]{param1, param2}, (Throwable)null);
      }

   }

   public void tracev(String format, Object param1, Object param2, Object param3) {
      if (this.isEnabled(Logger.Level.TRACE)) {
         this.doLog(Logger.Level.TRACE, FQCN, format, new Object[]{param1, param2, param3}, (Throwable)null);
      }

   }

   public void tracev(Throwable t, String format, Object... params) {
      this.doLog(Logger.Level.TRACE, FQCN, format, params, t);
   }

   public void tracev(Throwable t, String format, Object param1) {
      if (this.isEnabled(Logger.Level.TRACE)) {
         this.doLog(Logger.Level.TRACE, FQCN, format, new Object[]{param1}, t);
      }

   }

   public void tracev(Throwable t, String format, Object param1, Object param2) {
      if (this.isEnabled(Logger.Level.TRACE)) {
         this.doLog(Logger.Level.TRACE, FQCN, format, new Object[]{param1, param2}, t);
      }

   }

   public void tracev(Throwable t, String format, Object param1, Object param2, Object param3) {
      if (this.isEnabled(Logger.Level.TRACE)) {
         this.doLog(Logger.Level.TRACE, FQCN, format, new Object[]{param1, param2, param3}, t);
      }

   }

   public void tracef(String format, Object... params) {
      this.doLogf(Logger.Level.TRACE, FQCN, format, params, (Throwable)null);
   }

   public void tracef(String format, Object param1) {
      if (this.isEnabled(Logger.Level.TRACE)) {
         this.doLogf(Logger.Level.TRACE, FQCN, format, new Object[]{param1}, (Throwable)null);
      }

   }

   public void tracef(String format, Object param1, Object param2) {
      if (this.isEnabled(Logger.Level.TRACE)) {
         this.doLogf(Logger.Level.TRACE, FQCN, format, new Object[]{param1, param2}, (Throwable)null);
      }

   }

   public void tracef(String format, Object param1, Object param2, Object param3) {
      if (this.isEnabled(Logger.Level.TRACE)) {
         this.doLogf(Logger.Level.TRACE, FQCN, format, new Object[]{param1, param2, param3}, (Throwable)null);
      }

   }

   public void tracef(Throwable t, String format, Object... params) {
      this.doLogf(Logger.Level.TRACE, FQCN, format, params, t);
   }

   public void tracef(Throwable t, String format, Object param1) {
      if (this.isEnabled(Logger.Level.TRACE)) {
         this.doLogf(Logger.Level.TRACE, FQCN, format, new Object[]{param1}, t);
      }

   }

   public void tracef(Throwable t, String format, Object param1, Object param2) {
      if (this.isEnabled(Logger.Level.TRACE)) {
         this.doLogf(Logger.Level.TRACE, FQCN, format, new Object[]{param1, param2}, t);
      }

   }

   public void tracef(Throwable t, String format, Object param1, Object param2, Object param3) {
      if (this.isEnabled(Logger.Level.TRACE)) {
         this.doLogf(Logger.Level.TRACE, FQCN, format, new Object[]{param1, param2, param3}, t);
      }

   }

   public void tracef(String format, int arg) {
      if (this.isEnabled(Logger.Level.TRACE)) {
         this.doLogf(Logger.Level.TRACE, FQCN, format, new Object[]{arg}, (Throwable)null);
      }

   }

   public void tracef(String format, int arg1, int arg2) {
      if (this.isEnabled(Logger.Level.TRACE)) {
         this.doLogf(Logger.Level.TRACE, FQCN, format, new Object[]{arg1, arg2}, (Throwable)null);
      }

   }

   public void tracef(String format, int arg1, Object arg2) {
      if (this.isEnabled(Logger.Level.TRACE)) {
         this.doLogf(Logger.Level.TRACE, FQCN, format, new Object[]{arg1, arg2}, (Throwable)null);
      }

   }

   public void tracef(String format, int arg1, int arg2, int arg3) {
      if (this.isEnabled(Logger.Level.TRACE)) {
         this.doLogf(Logger.Level.TRACE, FQCN, format, new Object[]{arg1, arg2, arg3}, (Throwable)null);
      }

   }

   public void tracef(String format, int arg1, int arg2, Object arg3) {
      if (this.isEnabled(Logger.Level.TRACE)) {
         this.doLogf(Logger.Level.TRACE, FQCN, format, new Object[]{arg1, arg2, arg3}, (Throwable)null);
      }

   }

   public void tracef(String format, int arg1, Object arg2, Object arg3) {
      if (this.isEnabled(Logger.Level.TRACE)) {
         this.doLogf(Logger.Level.TRACE, FQCN, format, new Object[]{arg1, arg2, arg3}, (Throwable)null);
      }

   }

   public void tracef(Throwable t, String format, int arg) {
      if (this.isEnabled(Logger.Level.TRACE)) {
         this.doLogf(Logger.Level.TRACE, FQCN, format, new Object[]{arg}, t);
      }

   }

   public void tracef(Throwable t, String format, int arg1, int arg2) {
      if (this.isEnabled(Logger.Level.TRACE)) {
         this.doLogf(Logger.Level.TRACE, FQCN, format, new Object[]{arg1, arg2}, t);
      }

   }

   public void tracef(Throwable t, String format, int arg1, Object arg2) {
      if (this.isEnabled(Logger.Level.TRACE)) {
         this.doLogf(Logger.Level.TRACE, FQCN, format, new Object[]{arg1, arg2}, t);
      }

   }

   public void tracef(Throwable t, String format, int arg1, int arg2, int arg3) {
      if (this.isEnabled(Logger.Level.TRACE)) {
         this.doLogf(Logger.Level.TRACE, FQCN, format, new Object[]{arg1, arg2, arg3}, t);
      }

   }

   public void tracef(Throwable t, String format, int arg1, int arg2, Object arg3) {
      if (this.isEnabled(Logger.Level.TRACE)) {
         this.doLogf(Logger.Level.TRACE, FQCN, format, new Object[]{arg1, arg2, arg3}, t);
      }

   }

   public void tracef(Throwable t, String format, int arg1, Object arg2, Object arg3) {
      if (this.isEnabled(Logger.Level.TRACE)) {
         this.doLogf(Logger.Level.TRACE, FQCN, format, new Object[]{arg1, arg2, arg3}, t);
      }

   }

   public void tracef(String format, long arg) {
      if (this.isEnabled(Logger.Level.TRACE)) {
         this.doLogf(Logger.Level.TRACE, FQCN, format, new Object[]{arg}, (Throwable)null);
      }

   }

   public void tracef(String format, long arg1, long arg2) {
      if (this.isEnabled(Logger.Level.TRACE)) {
         this.doLogf(Logger.Level.TRACE, FQCN, format, new Object[]{arg1, arg2}, (Throwable)null);
      }

   }

   public void tracef(String format, long arg1, Object arg2) {
      if (this.isEnabled(Logger.Level.TRACE)) {
         this.doLogf(Logger.Level.TRACE, FQCN, format, new Object[]{arg1, arg2}, (Throwable)null);
      }

   }

   public void tracef(String format, long arg1, long arg2, long arg3) {
      if (this.isEnabled(Logger.Level.TRACE)) {
         this.doLogf(Logger.Level.TRACE, FQCN, format, new Object[]{arg1, arg2, arg3}, (Throwable)null);
      }

   }

   public void tracef(String format, long arg1, long arg2, Object arg3) {
      if (this.isEnabled(Logger.Level.TRACE)) {
         this.doLogf(Logger.Level.TRACE, FQCN, format, new Object[]{arg1, arg2, arg3}, (Throwable)null);
      }

   }

   public void tracef(String format, long arg1, Object arg2, Object arg3) {
      if (this.isEnabled(Logger.Level.TRACE)) {
         this.doLogf(Logger.Level.TRACE, FQCN, format, new Object[]{arg1, arg2, arg3}, (Throwable)null);
      }

   }

   public void tracef(Throwable t, String format, long arg) {
      if (this.isEnabled(Logger.Level.TRACE)) {
         this.doLogf(Logger.Level.TRACE, FQCN, format, new Object[]{arg}, t);
      }

   }

   public void tracef(Throwable t, String format, long arg1, long arg2) {
      if (this.isEnabled(Logger.Level.TRACE)) {
         this.doLogf(Logger.Level.TRACE, FQCN, format, new Object[]{arg1, arg2}, t);
      }

   }

   public void tracef(Throwable t, String format, long arg1, Object arg2) {
      if (this.isEnabled(Logger.Level.TRACE)) {
         this.doLogf(Logger.Level.TRACE, FQCN, format, new Object[]{arg1, arg2}, t);
      }

   }

   public void tracef(Throwable t, String format, long arg1, long arg2, long arg3) {
      if (this.isEnabled(Logger.Level.TRACE)) {
         this.doLogf(Logger.Level.TRACE, FQCN, format, new Object[]{arg1, arg2, arg3}, t);
      }

   }

   public void tracef(Throwable t, String format, long arg1, long arg2, Object arg3) {
      if (this.isEnabled(Logger.Level.TRACE)) {
         this.doLogf(Logger.Level.TRACE, FQCN, format, new Object[]{arg1, arg2, arg3}, t);
      }

   }

   public void tracef(Throwable t, String format, long arg1, Object arg2, Object arg3) {
      if (this.isEnabled(Logger.Level.TRACE)) {
         this.doLogf(Logger.Level.TRACE, FQCN, format, new Object[]{arg1, arg2, arg3}, t);
      }

   }

   public boolean isDebugEnabled() {
      return this.isEnabled(Logger.Level.DEBUG);
   }

   public void debug(Object message) {
      this.doLog(Logger.Level.DEBUG, FQCN, message, (Object[])null, (Throwable)null);
   }

   public void debug(Object message, Throwable t) {
      this.doLog(Logger.Level.DEBUG, FQCN, message, (Object[])null, t);
   }

   public void debug(String loggerFqcn, Object message, Throwable t) {
      this.doLog(Logger.Level.DEBUG, loggerFqcn, message, (Object[])null, t);
   }

   /** @deprecated */
   @Deprecated
   public void debug(Object message, Object[] params) {
      this.doLog(Logger.Level.DEBUG, FQCN, message, params, (Throwable)null);
   }

   /** @deprecated */
   @Deprecated
   public void debug(Object message, Object[] params, Throwable t) {
      this.doLog(Logger.Level.DEBUG, FQCN, message, params, t);
   }

   public void debug(String loggerFqcn, Object message, Object[] params, Throwable t) {
      this.doLog(Logger.Level.DEBUG, loggerFqcn, message, params, t);
   }

   public void debugv(String format, Object... params) {
      this.doLog(Logger.Level.DEBUG, FQCN, format, params, (Throwable)null);
   }

   public void debugv(String format, Object param1) {
      if (this.isEnabled(Logger.Level.DEBUG)) {
         this.doLog(Logger.Level.DEBUG, FQCN, format, new Object[]{param1}, (Throwable)null);
      }

   }

   public void debugv(String format, Object param1, Object param2) {
      if (this.isEnabled(Logger.Level.DEBUG)) {
         this.doLog(Logger.Level.DEBUG, FQCN, format, new Object[]{param1, param2}, (Throwable)null);
      }

   }

   public void debugv(String format, Object param1, Object param2, Object param3) {
      if (this.isEnabled(Logger.Level.DEBUG)) {
         this.doLog(Logger.Level.DEBUG, FQCN, format, new Object[]{param1, param2, param3}, (Throwable)null);
      }

   }

   public void debugv(Throwable t, String format, Object... params) {
      this.doLog(Logger.Level.DEBUG, FQCN, format, params, t);
   }

   public void debugv(Throwable t, String format, Object param1) {
      if (this.isEnabled(Logger.Level.DEBUG)) {
         this.doLog(Logger.Level.DEBUG, FQCN, format, new Object[]{param1}, t);
      }

   }

   public void debugv(Throwable t, String format, Object param1, Object param2) {
      if (this.isEnabled(Logger.Level.DEBUG)) {
         this.doLog(Logger.Level.DEBUG, FQCN, format, new Object[]{param1, param2}, t);
      }

   }

   public void debugv(Throwable t, String format, Object param1, Object param2, Object param3) {
      if (this.isEnabled(Logger.Level.DEBUG)) {
         this.doLog(Logger.Level.DEBUG, FQCN, format, new Object[]{param1, param2, param3}, t);
      }

   }

   public void debugf(String format, Object... params) {
      this.doLogf(Logger.Level.DEBUG, FQCN, format, params, (Throwable)null);
   }

   public void debugf(String format, Object param1) {
      if (this.isEnabled(Logger.Level.DEBUG)) {
         this.doLogf(Logger.Level.DEBUG, FQCN, format, new Object[]{param1}, (Throwable)null);
      }

   }

   public void debugf(String format, Object param1, Object param2) {
      if (this.isEnabled(Logger.Level.DEBUG)) {
         this.doLogf(Logger.Level.DEBUG, FQCN, format, new Object[]{param1, param2}, (Throwable)null);
      }

   }

   public void debugf(String format, Object param1, Object param2, Object param3) {
      if (this.isEnabled(Logger.Level.DEBUG)) {
         this.doLogf(Logger.Level.DEBUG, FQCN, format, new Object[]{param1, param2, param3}, (Throwable)null);
      }

   }

   public void debugf(Throwable t, String format, Object... params) {
      this.doLogf(Logger.Level.DEBUG, FQCN, format, params, t);
   }

   public void debugf(Throwable t, String format, Object param1) {
      if (this.isEnabled(Logger.Level.DEBUG)) {
         this.doLogf(Logger.Level.DEBUG, FQCN, format, new Object[]{param1}, t);
      }

   }

   public void debugf(Throwable t, String format, Object param1, Object param2) {
      if (this.isEnabled(Logger.Level.DEBUG)) {
         this.doLogf(Logger.Level.DEBUG, FQCN, format, new Object[]{param1, param2}, t);
      }

   }

   public void debugf(Throwable t, String format, Object param1, Object param2, Object param3) {
      if (this.isEnabled(Logger.Level.DEBUG)) {
         this.doLogf(Logger.Level.DEBUG, FQCN, format, new Object[]{param1, param2, param3}, t);
      }

   }

   public void debugf(String format, int arg) {
      if (this.isEnabled(Logger.Level.DEBUG)) {
         this.doLogf(Logger.Level.DEBUG, FQCN, format, new Object[]{arg}, (Throwable)null);
      }

   }

   public void debugf(String format, int arg1, int arg2) {
      if (this.isEnabled(Logger.Level.DEBUG)) {
         this.doLogf(Logger.Level.DEBUG, FQCN, format, new Object[]{arg1, arg2}, (Throwable)null);
      }

   }

   public void debugf(String format, int arg1, Object arg2) {
      if (this.isEnabled(Logger.Level.DEBUG)) {
         this.doLogf(Logger.Level.DEBUG, FQCN, format, new Object[]{arg1, arg2}, (Throwable)null);
      }

   }

   public void debugf(String format, int arg1, int arg2, int arg3) {
      if (this.isEnabled(Logger.Level.DEBUG)) {
         this.doLogf(Logger.Level.DEBUG, FQCN, format, new Object[]{arg1, arg2, arg3}, (Throwable)null);
      }

   }

   public void debugf(String format, int arg1, int arg2, Object arg3) {
      if (this.isEnabled(Logger.Level.DEBUG)) {
         this.doLogf(Logger.Level.DEBUG, FQCN, format, new Object[]{arg1, arg2, arg3}, (Throwable)null);
      }

   }

   public void debugf(String format, int arg1, Object arg2, Object arg3) {
      if (this.isEnabled(Logger.Level.DEBUG)) {
         this.doLogf(Logger.Level.DEBUG, FQCN, format, new Object[]{arg1, arg2, arg3}, (Throwable)null);
      }

   }

   public void debugf(Throwable t, String format, int arg) {
      if (this.isEnabled(Logger.Level.DEBUG)) {
         this.doLogf(Logger.Level.DEBUG, FQCN, format, new Object[]{arg}, t);
      }

   }

   public void debugf(Throwable t, String format, int arg1, int arg2) {
      if (this.isEnabled(Logger.Level.DEBUG)) {
         this.doLogf(Logger.Level.DEBUG, FQCN, format, new Object[]{arg1, arg2}, t);
      }

   }

   public void debugf(Throwable t, String format, int arg1, Object arg2) {
      if (this.isEnabled(Logger.Level.DEBUG)) {
         this.doLogf(Logger.Level.DEBUG, FQCN, format, new Object[]{arg1, arg2}, t);
      }

   }

   public void debugf(Throwable t, String format, int arg1, int arg2, int arg3) {
      if (this.isEnabled(Logger.Level.DEBUG)) {
         this.doLogf(Logger.Level.DEBUG, FQCN, format, new Object[]{arg1, arg2, arg3}, t);
      }

   }

   public void debugf(Throwable t, String format, int arg1, int arg2, Object arg3) {
      if (this.isEnabled(Logger.Level.DEBUG)) {
         this.doLogf(Logger.Level.DEBUG, FQCN, format, new Object[]{arg1, arg2, arg3}, t);
      }

   }

   public void debugf(Throwable t, String format, int arg1, Object arg2, Object arg3) {
      if (this.isEnabled(Logger.Level.DEBUG)) {
         this.doLogf(Logger.Level.DEBUG, FQCN, format, new Object[]{arg1, arg2, arg3}, t);
      }

   }

   public void debugf(String format, long arg) {
      if (this.isEnabled(Logger.Level.DEBUG)) {
         this.doLogf(Logger.Level.DEBUG, FQCN, format, new Object[]{arg}, (Throwable)null);
      }

   }

   public void debugf(String format, long arg1, long arg2) {
      if (this.isEnabled(Logger.Level.DEBUG)) {
         this.doLogf(Logger.Level.DEBUG, FQCN, format, new Object[]{arg1, arg2}, (Throwable)null);
      }

   }

   public void debugf(String format, long arg1, Object arg2) {
      if (this.isEnabled(Logger.Level.DEBUG)) {
         this.doLogf(Logger.Level.DEBUG, FQCN, format, new Object[]{arg1, arg2}, (Throwable)null);
      }

   }

   public void debugf(String format, long arg1, long arg2, long arg3) {
      if (this.isEnabled(Logger.Level.DEBUG)) {
         this.doLogf(Logger.Level.DEBUG, FQCN, format, new Object[]{arg1, arg2, arg3}, (Throwable)null);
      }

   }

   public void debugf(String format, long arg1, long arg2, Object arg3) {
      if (this.isEnabled(Logger.Level.DEBUG)) {
         this.doLogf(Logger.Level.DEBUG, FQCN, format, new Object[]{arg1, arg2, arg3}, (Throwable)null);
      }

   }

   public void debugf(String format, long arg1, Object arg2, Object arg3) {
      if (this.isEnabled(Logger.Level.DEBUG)) {
         this.doLogf(Logger.Level.DEBUG, FQCN, format, new Object[]{arg1, arg2, arg3}, (Throwable)null);
      }

   }

   public void debugf(Throwable t, String format, long arg) {
      if (this.isEnabled(Logger.Level.DEBUG)) {
         this.doLogf(Logger.Level.DEBUG, FQCN, format, new Object[]{arg}, t);
      }

   }

   public void debugf(Throwable t, String format, long arg1, long arg2) {
      if (this.isEnabled(Logger.Level.DEBUG)) {
         this.doLogf(Logger.Level.DEBUG, FQCN, format, new Object[]{arg1, arg2}, t);
      }

   }

   public void debugf(Throwable t, String format, long arg1, Object arg2) {
      if (this.isEnabled(Logger.Level.DEBUG)) {
         this.doLogf(Logger.Level.DEBUG, FQCN, format, new Object[]{arg1, arg2}, t);
      }

   }

   public void debugf(Throwable t, String format, long arg1, long arg2, long arg3) {
      if (this.isEnabled(Logger.Level.DEBUG)) {
         this.doLogf(Logger.Level.DEBUG, FQCN, format, new Object[]{arg1, arg2, arg3}, t);
      }

   }

   public void debugf(Throwable t, String format, long arg1, long arg2, Object arg3) {
      if (this.isEnabled(Logger.Level.DEBUG)) {
         this.doLogf(Logger.Level.DEBUG, FQCN, format, new Object[]{arg1, arg2, arg3}, t);
      }

   }

   public void debugf(Throwable t, String format, long arg1, Object arg2, Object arg3) {
      if (this.isEnabled(Logger.Level.DEBUG)) {
         this.doLogf(Logger.Level.DEBUG, FQCN, format, new Object[]{arg1, arg2, arg3}, t);
      }

   }

   public boolean isInfoEnabled() {
      return this.isEnabled(Logger.Level.INFO);
   }

   public void info(Object message) {
      this.doLog(Logger.Level.INFO, FQCN, message, (Object[])null, (Throwable)null);
   }

   public void info(Object message, Throwable t) {
      this.doLog(Logger.Level.INFO, FQCN, message, (Object[])null, t);
   }

   public void info(String loggerFqcn, Object message, Throwable t) {
      this.doLog(Logger.Level.INFO, loggerFqcn, message, (Object[])null, t);
   }

   /** @deprecated */
   @Deprecated
   public void info(Object message, Object[] params) {
      this.doLog(Logger.Level.INFO, FQCN, message, params, (Throwable)null);
   }

   /** @deprecated */
   @Deprecated
   public void info(Object message, Object[] params, Throwable t) {
      this.doLog(Logger.Level.INFO, FQCN, message, params, t);
   }

   public void info(String loggerFqcn, Object message, Object[] params, Throwable t) {
      this.doLog(Logger.Level.INFO, loggerFqcn, message, params, t);
   }

   public void infov(String format, Object... params) {
      this.doLog(Logger.Level.INFO, FQCN, format, params, (Throwable)null);
   }

   public void infov(String format, Object param1) {
      if (this.isEnabled(Logger.Level.INFO)) {
         this.doLog(Logger.Level.INFO, FQCN, format, new Object[]{param1}, (Throwable)null);
      }

   }

   public void infov(String format, Object param1, Object param2) {
      if (this.isEnabled(Logger.Level.INFO)) {
         this.doLog(Logger.Level.INFO, FQCN, format, new Object[]{param1, param2}, (Throwable)null);
      }

   }

   public void infov(String format, Object param1, Object param2, Object param3) {
      if (this.isEnabled(Logger.Level.INFO)) {
         this.doLog(Logger.Level.INFO, FQCN, format, new Object[]{param1, param2, param3}, (Throwable)null);
      }

   }

   public void infov(Throwable t, String format, Object... params) {
      this.doLog(Logger.Level.INFO, FQCN, format, params, t);
   }

   public void infov(Throwable t, String format, Object param1) {
      if (this.isEnabled(Logger.Level.INFO)) {
         this.doLog(Logger.Level.INFO, FQCN, format, new Object[]{param1}, t);
      }

   }

   public void infov(Throwable t, String format, Object param1, Object param2) {
      if (this.isEnabled(Logger.Level.INFO)) {
         this.doLog(Logger.Level.INFO, FQCN, format, new Object[]{param1, param2}, t);
      }

   }

   public void infov(Throwable t, String format, Object param1, Object param2, Object param3) {
      if (this.isEnabled(Logger.Level.INFO)) {
         this.doLog(Logger.Level.INFO, FQCN, format, new Object[]{param1, param2, param3}, t);
      }

   }

   public void infof(String format, Object... params) {
      this.doLogf(Logger.Level.INFO, FQCN, format, params, (Throwable)null);
   }

   public void infof(String format, Object param1) {
      if (this.isEnabled(Logger.Level.INFO)) {
         this.doLogf(Logger.Level.INFO, FQCN, format, new Object[]{param1}, (Throwable)null);
      }

   }

   public void infof(String format, Object param1, Object param2) {
      if (this.isEnabled(Logger.Level.INFO)) {
         this.doLogf(Logger.Level.INFO, FQCN, format, new Object[]{param1, param2}, (Throwable)null);
      }

   }

   public void infof(String format, Object param1, Object param2, Object param3) {
      if (this.isEnabled(Logger.Level.INFO)) {
         this.doLogf(Logger.Level.INFO, FQCN, format, new Object[]{param1, param2, param3}, (Throwable)null);
      }

   }

   public void infof(Throwable t, String format, Object... params) {
      this.doLogf(Logger.Level.INFO, FQCN, format, params, t);
   }

   public void infof(Throwable t, String format, Object param1) {
      if (this.isEnabled(Logger.Level.INFO)) {
         this.doLogf(Logger.Level.INFO, FQCN, format, new Object[]{param1}, t);
      }

   }

   public void infof(Throwable t, String format, Object param1, Object param2) {
      if (this.isEnabled(Logger.Level.INFO)) {
         this.doLogf(Logger.Level.INFO, FQCN, format, new Object[]{param1, param2}, t);
      }

   }

   public void infof(Throwable t, String format, Object param1, Object param2, Object param3) {
      if (this.isEnabled(Logger.Level.INFO)) {
         this.doLogf(Logger.Level.INFO, FQCN, format, new Object[]{param1, param2, param3}, t);
      }

   }

   public void warn(Object message) {
      this.doLog(Logger.Level.WARN, FQCN, message, (Object[])null, (Throwable)null);
   }

   public void warn(Object message, Throwable t) {
      this.doLog(Logger.Level.WARN, FQCN, message, (Object[])null, t);
   }

   public void warn(String loggerFqcn, Object message, Throwable t) {
      this.doLog(Logger.Level.WARN, loggerFqcn, message, (Object[])null, t);
   }

   /** @deprecated */
   @Deprecated
   public void warn(Object message, Object[] params) {
      this.doLog(Logger.Level.WARN, FQCN, message, params, (Throwable)null);
   }

   /** @deprecated */
   @Deprecated
   public void warn(Object message, Object[] params, Throwable t) {
      this.doLog(Logger.Level.WARN, FQCN, message, params, t);
   }

   public void warn(String loggerFqcn, Object message, Object[] params, Throwable t) {
      this.doLog(Logger.Level.WARN, loggerFqcn, message, params, t);
   }

   public void warnv(String format, Object... params) {
      this.doLog(Logger.Level.WARN, FQCN, format, params, (Throwable)null);
   }

   public void warnv(String format, Object param1) {
      if (this.isEnabled(Logger.Level.WARN)) {
         this.doLog(Logger.Level.WARN, FQCN, format, new Object[]{param1}, (Throwable)null);
      }

   }

   public void warnv(String format, Object param1, Object param2) {
      if (this.isEnabled(Logger.Level.WARN)) {
         this.doLog(Logger.Level.WARN, FQCN, format, new Object[]{param1, param2}, (Throwable)null);
      }

   }

   public void warnv(String format, Object param1, Object param2, Object param3) {
      if (this.isEnabled(Logger.Level.WARN)) {
         this.doLog(Logger.Level.WARN, FQCN, format, new Object[]{param1, param2, param3}, (Throwable)null);
      }

   }

   public void warnv(Throwable t, String format, Object... params) {
      this.doLog(Logger.Level.WARN, FQCN, format, params, t);
   }

   public void warnv(Throwable t, String format, Object param1) {
      if (this.isEnabled(Logger.Level.WARN)) {
         this.doLog(Logger.Level.WARN, FQCN, format, new Object[]{param1}, t);
      }

   }

   public void warnv(Throwable t, String format, Object param1, Object param2) {
      if (this.isEnabled(Logger.Level.WARN)) {
         this.doLog(Logger.Level.WARN, FQCN, format, new Object[]{param1, param2}, t);
      }

   }

   public void warnv(Throwable t, String format, Object param1, Object param2, Object param3) {
      if (this.isEnabled(Logger.Level.WARN)) {
         this.doLog(Logger.Level.WARN, FQCN, format, new Object[]{param1, param2, param3}, t);
      }

   }

   public void warnf(String format, Object... params) {
      this.doLogf(Logger.Level.WARN, FQCN, format, params, (Throwable)null);
   }

   public void warnf(String format, Object param1) {
      if (this.isEnabled(Logger.Level.WARN)) {
         this.doLogf(Logger.Level.WARN, FQCN, format, new Object[]{param1}, (Throwable)null);
      }

   }

   public void warnf(String format, Object param1, Object param2) {
      if (this.isEnabled(Logger.Level.WARN)) {
         this.doLogf(Logger.Level.WARN, FQCN, format, new Object[]{param1, param2}, (Throwable)null);
      }

   }

   public void warnf(String format, Object param1, Object param2, Object param3) {
      if (this.isEnabled(Logger.Level.WARN)) {
         this.doLogf(Logger.Level.WARN, FQCN, format, new Object[]{param1, param2, param3}, (Throwable)null);
      }

   }

   public void warnf(Throwable t, String format, Object... params) {
      this.doLogf(Logger.Level.WARN, FQCN, format, params, t);
   }

   public void warnf(Throwable t, String format, Object param1) {
      if (this.isEnabled(Logger.Level.WARN)) {
         this.doLogf(Logger.Level.WARN, FQCN, format, new Object[]{param1}, t);
      }

   }

   public void warnf(Throwable t, String format, Object param1, Object param2) {
      if (this.isEnabled(Logger.Level.WARN)) {
         this.doLogf(Logger.Level.WARN, FQCN, format, new Object[]{param1, param2}, t);
      }

   }

   public void warnf(Throwable t, String format, Object param1, Object param2, Object param3) {
      if (this.isEnabled(Logger.Level.WARN)) {
         this.doLogf(Logger.Level.WARN, FQCN, format, new Object[]{param1, param2, param3}, t);
      }

   }

   public void error(Object message) {
      this.doLog(Logger.Level.ERROR, FQCN, message, (Object[])null, (Throwable)null);
   }

   public void error(Object message, Throwable t) {
      this.doLog(Logger.Level.ERROR, FQCN, message, (Object[])null, t);
   }

   public void error(String loggerFqcn, Object message, Throwable t) {
      this.doLog(Logger.Level.ERROR, loggerFqcn, message, (Object[])null, t);
   }

   /** @deprecated */
   @Deprecated
   public void error(Object message, Object[] params) {
      this.doLog(Logger.Level.ERROR, FQCN, message, params, (Throwable)null);
   }

   /** @deprecated */
   @Deprecated
   public void error(Object message, Object[] params, Throwable t) {
      this.doLog(Logger.Level.ERROR, FQCN, message, params, t);
   }

   public void error(String loggerFqcn, Object message, Object[] params, Throwable t) {
      this.doLog(Logger.Level.ERROR, loggerFqcn, message, params, t);
   }

   public void errorv(String format, Object... params) {
      this.doLog(Logger.Level.ERROR, FQCN, format, params, (Throwable)null);
   }

   public void errorv(String format, Object param1) {
      if (this.isEnabled(Logger.Level.ERROR)) {
         this.doLog(Logger.Level.ERROR, FQCN, format, new Object[]{param1}, (Throwable)null);
      }

   }

   public void errorv(String format, Object param1, Object param2) {
      if (this.isEnabled(Logger.Level.ERROR)) {
         this.doLog(Logger.Level.ERROR, FQCN, format, new Object[]{param1, param2}, (Throwable)null);
      }

   }

   public void errorv(String format, Object param1, Object param2, Object param3) {
      if (this.isEnabled(Logger.Level.ERROR)) {
         this.doLog(Logger.Level.ERROR, FQCN, format, new Object[]{param1, param2, param3}, (Throwable)null);
      }

   }

   public void errorv(Throwable t, String format, Object... params) {
      this.doLog(Logger.Level.ERROR, FQCN, format, params, t);
   }

   public void errorv(Throwable t, String format, Object param1) {
      if (this.isEnabled(Logger.Level.ERROR)) {
         this.doLog(Logger.Level.ERROR, FQCN, format, new Object[]{param1}, t);
      }

   }

   public void errorv(Throwable t, String format, Object param1, Object param2) {
      if (this.isEnabled(Logger.Level.ERROR)) {
         this.doLog(Logger.Level.ERROR, FQCN, format, new Object[]{param1, param2}, t);
      }

   }

   public void errorv(Throwable t, String format, Object param1, Object param2, Object param3) {
      if (this.isEnabled(Logger.Level.ERROR)) {
         this.doLog(Logger.Level.ERROR, FQCN, format, new Object[]{param1, param2, param3}, t);
      }

   }

   public void errorf(String format, Object... params) {
      this.doLogf(Logger.Level.ERROR, FQCN, format, params, (Throwable)null);
   }

   public void errorf(String format, Object param1) {
      if (this.isEnabled(Logger.Level.ERROR)) {
         this.doLogf(Logger.Level.ERROR, FQCN, format, new Object[]{param1}, (Throwable)null);
      }

   }

   public void errorf(String format, Object param1, Object param2) {
      if (this.isEnabled(Logger.Level.ERROR)) {
         this.doLogf(Logger.Level.ERROR, FQCN, format, new Object[]{param1, param2}, (Throwable)null);
      }

   }

   public void errorf(String format, Object param1, Object param2, Object param3) {
      if (this.isEnabled(Logger.Level.ERROR)) {
         this.doLogf(Logger.Level.ERROR, FQCN, format, new Object[]{param1, param2, param3}, (Throwable)null);
      }

   }

   public void errorf(Throwable t, String format, Object... params) {
      this.doLogf(Logger.Level.ERROR, FQCN, format, params, t);
   }

   public void errorf(Throwable t, String format, Object param1) {
      if (this.isEnabled(Logger.Level.ERROR)) {
         this.doLogf(Logger.Level.ERROR, FQCN, format, new Object[]{param1}, t);
      }

   }

   public void errorf(Throwable t, String format, Object param1, Object param2) {
      if (this.isEnabled(Logger.Level.ERROR)) {
         this.doLogf(Logger.Level.ERROR, FQCN, format, new Object[]{param1, param2}, t);
      }

   }

   public void errorf(Throwable t, String format, Object param1, Object param2, Object param3) {
      if (this.isEnabled(Logger.Level.ERROR)) {
         this.doLogf(Logger.Level.ERROR, FQCN, format, new Object[]{param1, param2, param3}, t);
      }

   }

   public void fatal(Object message) {
      this.doLog(Logger.Level.FATAL, FQCN, message, (Object[])null, (Throwable)null);
   }

   public void fatal(Object message, Throwable t) {
      this.doLog(Logger.Level.FATAL, FQCN, message, (Object[])null, t);
   }

   public void fatal(String loggerFqcn, Object message, Throwable t) {
      this.doLog(Logger.Level.FATAL, loggerFqcn, message, (Object[])null, t);
   }

   /** @deprecated */
   @Deprecated
   public void fatal(Object message, Object[] params) {
      this.doLog(Logger.Level.FATAL, FQCN, message, params, (Throwable)null);
   }

   /** @deprecated */
   @Deprecated
   public void fatal(Object message, Object[] params, Throwable t) {
      this.doLog(Logger.Level.FATAL, FQCN, message, params, t);
   }

   public void fatal(String loggerFqcn, Object message, Object[] params, Throwable t) {
      this.doLog(Logger.Level.FATAL, loggerFqcn, message, params, t);
   }

   public void fatalv(String format, Object... params) {
      this.doLog(Logger.Level.FATAL, FQCN, format, params, (Throwable)null);
   }

   public void fatalv(String format, Object param1) {
      if (this.isEnabled(Logger.Level.FATAL)) {
         this.doLog(Logger.Level.FATAL, FQCN, format, new Object[]{param1}, (Throwable)null);
      }

   }

   public void fatalv(String format, Object param1, Object param2) {
      if (this.isEnabled(Logger.Level.FATAL)) {
         this.doLog(Logger.Level.FATAL, FQCN, format, new Object[]{param1, param2}, (Throwable)null);
      }

   }

   public void fatalv(String format, Object param1, Object param2, Object param3) {
      if (this.isEnabled(Logger.Level.FATAL)) {
         this.doLog(Logger.Level.FATAL, FQCN, format, new Object[]{param1, param2, param3}, (Throwable)null);
      }

   }

   public void fatalv(Throwable t, String format, Object... params) {
      this.doLog(Logger.Level.FATAL, FQCN, format, params, t);
   }

   public void fatalv(Throwable t, String format, Object param1) {
      if (this.isEnabled(Logger.Level.FATAL)) {
         this.doLog(Logger.Level.FATAL, FQCN, format, new Object[]{param1}, t);
      }

   }

   public void fatalv(Throwable t, String format, Object param1, Object param2) {
      if (this.isEnabled(Logger.Level.FATAL)) {
         this.doLog(Logger.Level.FATAL, FQCN, format, new Object[]{param1, param2}, t);
      }

   }

   public void fatalv(Throwable t, String format, Object param1, Object param2, Object param3) {
      if (this.isEnabled(Logger.Level.FATAL)) {
         this.doLog(Logger.Level.FATAL, FQCN, format, new Object[]{param1, param2, param3}, t);
      }

   }

   public void fatalf(String format, Object... params) {
      this.doLogf(Logger.Level.FATAL, FQCN, format, params, (Throwable)null);
   }

   public void fatalf(String format, Object param1) {
      if (this.isEnabled(Logger.Level.FATAL)) {
         this.doLogf(Logger.Level.FATAL, FQCN, format, new Object[]{param1}, (Throwable)null);
      }

   }

   public void fatalf(String format, Object param1, Object param2) {
      if (this.isEnabled(Logger.Level.FATAL)) {
         this.doLogf(Logger.Level.FATAL, FQCN, format, new Object[]{param1, param2}, (Throwable)null);
      }

   }

   public void fatalf(String format, Object param1, Object param2, Object param3) {
      if (this.isEnabled(Logger.Level.FATAL)) {
         this.doLogf(Logger.Level.FATAL, FQCN, format, new Object[]{param1, param2, param3}, (Throwable)null);
      }

   }

   public void fatalf(Throwable t, String format, Object... params) {
      this.doLogf(Logger.Level.FATAL, FQCN, format, params, t);
   }

   public void fatalf(Throwable t, String format, Object param1) {
      if (this.isEnabled(Logger.Level.FATAL)) {
         this.doLogf(Logger.Level.FATAL, FQCN, format, new Object[]{param1}, t);
      }

   }

   public void fatalf(Throwable t, String format, Object param1, Object param2) {
      if (this.isEnabled(Logger.Level.FATAL)) {
         this.doLogf(Logger.Level.FATAL, FQCN, format, new Object[]{param1, param2}, t);
      }

   }

   public void fatalf(Throwable t, String format, Object param1, Object param2, Object param3) {
      if (this.isEnabled(Logger.Level.FATAL)) {
         this.doLogf(Logger.Level.FATAL, FQCN, format, new Object[]{param1, param2, param3}, t);
      }

   }

   public void log(Level level, Object message) {
      this.doLog(level, FQCN, message, (Object[])null, (Throwable)null);
   }

   public void log(Level level, Object message, Throwable t) {
      this.doLog(level, FQCN, message, (Object[])null, t);
   }

   public void log(Level level, String loggerFqcn, Object message, Throwable t) {
      this.doLog(level, loggerFqcn, message, (Object[])null, t);
   }

   /** @deprecated */
   @Deprecated
   public void log(Level level, Object message, Object[] params) {
      this.doLog(level, FQCN, message, params, (Throwable)null);
   }

   /** @deprecated */
   @Deprecated
   public void log(Level level, Object message, Object[] params, Throwable t) {
      this.doLog(level, FQCN, message, params, t);
   }

   public void log(String loggerFqcn, Level level, Object message, Object[] params, Throwable t) {
      this.doLog(level, loggerFqcn, message, params, t);
   }

   public void logv(Level level, String format, Object... params) {
      this.doLog(level, FQCN, format, params, (Throwable)null);
   }

   public void logv(Level level, String format, Object param1) {
      if (this.isEnabled(level)) {
         this.doLog(level, FQCN, format, new Object[]{param1}, (Throwable)null);
      }

   }

   public void logv(Level level, String format, Object param1, Object param2) {
      if (this.isEnabled(level)) {
         this.doLog(level, FQCN, format, new Object[]{param1, param2}, (Throwable)null);
      }

   }

   public void logv(Level level, String format, Object param1, Object param2, Object param3) {
      if (this.isEnabled(level)) {
         this.doLog(level, FQCN, format, new Object[]{param1, param2, param3}, (Throwable)null);
      }

   }

   public void logv(Level level, Throwable t, String format, Object... params) {
      this.doLog(level, FQCN, format, params, t);
   }

   public void logv(Level level, Throwable t, String format, Object param1) {
      if (this.isEnabled(level)) {
         this.doLog(level, FQCN, format, new Object[]{param1}, t);
      }

   }

   public void logv(Level level, Throwable t, String format, Object param1, Object param2) {
      if (this.isEnabled(level)) {
         this.doLog(level, FQCN, format, new Object[]{param1, param2}, t);
      }

   }

   public void logv(Level level, Throwable t, String format, Object param1, Object param2, Object param3) {
      if (this.isEnabled(level)) {
         this.doLog(level, FQCN, format, new Object[]{param1, param2, param3}, t);
      }

   }

   public void logv(String loggerFqcn, Level level, Throwable t, String format, Object... params) {
      this.doLog(level, loggerFqcn, format, params, t);
   }

   public void logv(String loggerFqcn, Level level, Throwable t, String format, Object param1) {
      if (this.isEnabled(level)) {
         this.doLog(level, loggerFqcn, format, new Object[]{param1}, t);
      }

   }

   public void logv(String loggerFqcn, Level level, Throwable t, String format, Object param1, Object param2) {
      if (this.isEnabled(level)) {
         this.doLog(level, loggerFqcn, format, new Object[]{param1, param2}, t);
      }

   }

   public void logv(String loggerFqcn, Level level, Throwable t, String format, Object param1, Object param2, Object param3) {
      if (this.isEnabled(level)) {
         this.doLog(level, loggerFqcn, format, new Object[]{param1, param2, param3}, t);
      }

   }

   public void logf(Level level, String format, Object... params) {
      this.doLogf(level, FQCN, format, params, (Throwable)null);
   }

   public void logf(Level level, String format, Object param1) {
      if (this.isEnabled(level)) {
         this.doLogf(level, FQCN, format, new Object[]{param1}, (Throwable)null);
      }

   }

   public void logf(Level level, String format, Object param1, Object param2) {
      if (this.isEnabled(level)) {
         this.doLogf(level, FQCN, format, new Object[]{param1, param2}, (Throwable)null);
      }

   }

   public void logf(Level level, String format, Object param1, Object param2, Object param3) {
      if (this.isEnabled(level)) {
         this.doLogf(level, FQCN, format, new Object[]{param1, param2, param3}, (Throwable)null);
      }

   }

   public void logf(Level level, Throwable t, String format, Object... params) {
      this.doLogf(level, FQCN, format, params, t);
   }

   public void logf(Level level, Throwable t, String format, Object param1) {
      if (this.isEnabled(level)) {
         this.doLogf(level, FQCN, format, new Object[]{param1}, t);
      }

   }

   public void logf(Level level, Throwable t, String format, Object param1, Object param2) {
      if (this.isEnabled(level)) {
         this.doLogf(level, FQCN, format, new Object[]{param1, param2}, t);
      }

   }

   public void logf(Level level, Throwable t, String format, Object param1, Object param2, Object param3) {
      if (this.isEnabled(level)) {
         this.doLogf(level, FQCN, format, new Object[]{param1, param2, param3}, t);
      }

   }

   public void logf(String loggerFqcn, Level level, Throwable t, String format, Object param1) {
      if (this.isEnabled(level)) {
         this.doLogf(level, loggerFqcn, format, new Object[]{param1}, t);
      }

   }

   public void logf(String loggerFqcn, Level level, Throwable t, String format, Object param1, Object param2) {
      if (this.isEnabled(level)) {
         this.doLogf(level, loggerFqcn, format, new Object[]{param1, param2}, t);
      }

   }

   public void logf(String loggerFqcn, Level level, Throwable t, String format, Object param1, Object param2, Object param3) {
      if (this.isEnabled(level)) {
         this.doLogf(level, loggerFqcn, format, new Object[]{param1, param2, param3}, t);
      }

   }

   public void logf(String loggerFqcn, Level level, Throwable t, String format, Object... params) {
      this.doLogf(level, loggerFqcn, format, params, t);
   }

   protected final Object writeReplace() {
      return new SerializedLogger(this.name);
   }

   public static Logger getLogger(String name) {
      return LoggerProviders.PROVIDER.getLogger(name);
   }

   public static Logger getLogger(String name, String suffix) {
      return getLogger(name != null && name.length() != 0 ? name + "." + suffix : suffix);
   }

   public static Logger getLogger(Class<?> clazz) {
      return getLogger(clazz.getName());
   }

   public static Logger getLogger(Class<?> clazz, String suffix) {
      return getLogger(clazz.getName(), suffix);
   }

   public static <T> T getMessageLogger(Class<T> type, String category) {
      return getMessageLogger(type, category, LoggingLocale.getLocale());
   }

   public static <T> T getMessageLogger(final Class<T> type, final String category, final Locale locale) {
      return System.getSecurityManager() == null ? doGetMessageLogger(type, category, locale) : AccessController.doPrivileged(new PrivilegedAction<T>() {
         public T run() {
            return Logger.doGetMessageLogger(type, category, locale);
         }
      });
   }

   private static <T> T doGetMessageLogger(Class<T> type, String category, Locale locale) {
      String language = locale.getLanguage();
      String country = locale.getCountry();
      String variant = locale.getVariant();
      Class<? extends T> loggerClass = null;
      ClassLoader classLoader = type.getClassLoader();
      String typeName = type.getName();
      if (variant != null && variant.length() > 0) {
         try {
            loggerClass = Class.forName(join(typeName, "$logger", language, country, variant), true, classLoader).asSubclass(type);
         } catch (ClassNotFoundException var18) {
         }
      }

      if (loggerClass == null && country != null && country.length() > 0) {
         try {
            loggerClass = Class.forName(join(typeName, "$logger", language, country, (String)null), true, classLoader).asSubclass(type);
         } catch (ClassNotFoundException var17) {
         }
      }

      if (loggerClass == null && language != null && language.length() > 0) {
         try {
            loggerClass = Class.forName(join(typeName, "$logger", language, (String)null, (String)null), true, classLoader).asSubclass(type);
         } catch (ClassNotFoundException var16) {
         }
      }

      if (loggerClass == null) {
         try {
            loggerClass = Class.forName(join(typeName, "$logger", (String)null, (String)null, (String)null), true, classLoader).asSubclass(type);
         } catch (ClassNotFoundException var15) {
            throw new IllegalArgumentException("Invalid logger " + type + " (implementation not found in " + classLoader + ")");
         }
      }

      Constructor constructor;
      try {
         constructor = loggerClass.getConstructor(Logger.class);
      } catch (NoSuchMethodException var14) {
         throw new IllegalArgumentException("Logger implementation " + loggerClass + " has no matching constructor");
      }

      try {
         return constructor.newInstance(getLogger(category));
      } catch (InstantiationException var11) {
         throw new IllegalArgumentException("Logger implementation " + loggerClass + " could not be instantiated", var11);
      } catch (IllegalAccessException var12) {
         throw new IllegalArgumentException("Logger implementation " + loggerClass + " could not be instantiated", var12);
      } catch (InvocationTargetException var13) {
         throw new IllegalArgumentException("Logger implementation " + loggerClass + " could not be instantiated", var13.getCause());
      }
   }

   private static String join(String interfaceName, String a, String b, String c, String d) {
      StringBuilder build = new StringBuilder();
      build.append(interfaceName).append('_').append(a);
      if (b != null && b.length() > 0) {
         build.append('_');
         build.append(b);
      }

      if (c != null && c.length() > 0) {
         build.append('_');
         build.append(c);
      }

      if (d != null && d.length() > 0) {
         build.append('_');
         build.append(d);
      }

      return build.toString();
   }

   public static enum Level {
      FATAL,
      ERROR,
      WARN,
      INFO,
      DEBUG,
      TRACE;
   }
}
