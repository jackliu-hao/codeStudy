package org.slf4j.impl;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.util.ContextInitializer;
import ch.qos.logback.classic.util.ContextSelectorStaticBinder;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.status.StatusUtil;
import ch.qos.logback.core.util.StatusPrinter;
import org.slf4j.ILoggerFactory;
import org.slf4j.helpers.Util;
import org.slf4j.spi.LoggerFactoryBinder;

public class StaticLoggerBinder implements LoggerFactoryBinder {
   public static String REQUESTED_API_VERSION = "1.7.16";
   static final String NULL_CS_URL = "http://logback.qos.ch/codes.html#null_CS";
   private static StaticLoggerBinder SINGLETON = new StaticLoggerBinder();
   private static Object KEY = new Object();
   private boolean initialized = false;
   private LoggerContext defaultLoggerContext = new LoggerContext();
   private final ContextSelectorStaticBinder contextSelectorBinder = ContextSelectorStaticBinder.getSingleton();

   private StaticLoggerBinder() {
      this.defaultLoggerContext.setName("default");
   }

   public static StaticLoggerBinder getSingleton() {
      return SINGLETON;
   }

   static void reset() {
      SINGLETON = new StaticLoggerBinder();
      SINGLETON.init();
   }

   void init() {
      try {
         try {
            (new ContextInitializer(this.defaultLoggerContext)).autoConfig();
         } catch (JoranException var2) {
            Util.report("Failed to auto configure default logger context", var2);
         }

         if (!StatusUtil.contextHasStatusListener(this.defaultLoggerContext)) {
            StatusPrinter.printInCaseOfErrorsOrWarnings(this.defaultLoggerContext);
         }

         this.contextSelectorBinder.init(this.defaultLoggerContext, KEY);
         this.initialized = true;
      } catch (Exception var3) {
         Util.report("Failed to instantiate [" + LoggerContext.class.getName() + "]", var3);
      }

   }

   public ILoggerFactory getLoggerFactory() {
      if (!this.initialized) {
         return this.defaultLoggerContext;
      } else if (this.contextSelectorBinder.getContextSelector() == null) {
         throw new IllegalStateException("contextSelector cannot be null. See also http://logback.qos.ch/codes.html#null_CS");
      } else {
         return this.contextSelectorBinder.getContextSelector().getLoggerContext();
      }
   }

   public String getLoggerFactoryClassStr() {
      return this.contextSelectorBinder.getClass().getName();
   }

   static {
      SINGLETON.init();
   }
}
