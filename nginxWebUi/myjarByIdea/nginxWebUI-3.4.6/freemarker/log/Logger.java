package freemarker.log;

import java.security.AccessControlException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashMap;
import java.util.Map;

public abstract class Logger {
   public static final String SYSTEM_PROPERTY_NAME_LOGGER_LIBRARY = "org.freemarker.loggerLibrary";
   public static final int LIBRARY_AUTO = -1;
   private static final int MIN_LIBRARY_ENUM = -1;
   public static final String LIBRARY_NAME_AUTO = "auto";
   public static final int LIBRARY_NONE = 0;
   public static final String LIBRARY_NAME_NONE = "none";
   public static final int LIBRARY_JAVA = 1;
   public static final String LIBRARY_NAME_JUL = "JUL";
   /** @deprecated */
   @Deprecated
   public static final int LIBRARY_AVALON = 2;
   /** @deprecated */
   @Deprecated
   public static final String LIBRARY_NAME_AVALON = "Avalon";
   public static final int LIBRARY_LOG4J = 3;
   public static final String LIBRARY_NAME_LOG4J = "Log4j";
   public static final int LIBRARY_COMMONS = 4;
   public static final String LIBRARY_NAME_COMMONS_LOGGING = "CommonsLogging";
   public static final int LIBRARY_SLF4J = 5;
   public static final String LIBRARY_NAME_SLF4J = "SLF4J";
   private static final int MAX_LIBRARY_ENUM = 5;
   private static final String REAL_LOG4J_PRESENCE_CLASS = "org.apache.log4j.FileAppender";
   private static final String LOG4J_OVER_SLF4J_TESTER_CLASS = "freemarker.log._Log4jOverSLF4JTester";
   private static final String[] LIBRARIES_BY_PRIORITY = new String[]{null, "JUL", "org.apache.log.Logger", "Avalon", "org.apache.log4j.Logger", "Log4j", "org.apache.commons.logging.Log", "CommonsLogging", "org.slf4j.Logger", "SLF4J"};
   private static int libraryEnum;
   private static LoggerFactory loggerFactory;
   private static boolean initializedFromSystemProperty;
   private static String categoryPrefix;
   private static final Map loggersByCategory;

   private static String getAvailabilityCheckClassName(int libraryEnum) {
      return libraryEnum != -1 && libraryEnum != 0 ? LIBRARIES_BY_PRIORITY[(libraryEnum - 1) * 2] : null;
   }

   private static String getLibraryName(int libraryEnum) {
      if (libraryEnum == -1) {
         return "auto";
      } else {
         return libraryEnum == 0 ? "none" : LIBRARIES_BY_PRIORITY[(libraryEnum - 1) * 2 + 1];
      }
   }

   private static boolean isAutoDetected(int libraryEnum) {
      return libraryEnum != -1 && libraryEnum != 0 && libraryEnum != 5 && libraryEnum != 4;
   }

   /** @deprecated */
   @Deprecated
   public static void selectLoggerLibrary(int libraryEnum) throws ClassNotFoundException {
      if (libraryEnum >= -1 && libraryEnum <= 5) {
         Class var1 = Logger.class;
         synchronized(Logger.class) {
            boolean loggerFactoryWasAlreadySet = loggerFactory != null;
            if (!loggerFactoryWasAlreadySet || libraryEnum != Logger.libraryEnum) {
               ensureLoggerFactorySet(true);
               if (initializedFromSystemProperty && loggerFactory != null) {
                  if (libraryEnum != Logger.libraryEnum) {
                     logWarnInLogger("Ignored " + Logger.class.getName() + ".selectLoggerLibrary(\"" + getLibraryName(libraryEnum) + "\") call, because the \"" + "org.freemarker.loggerLibrary" + "\" system property is set to \"" + getLibraryName(Logger.libraryEnum) + "\".");
                  }
               } else {
                  int replacedLibraryEnum = Logger.libraryEnum;
                  setLibrary(libraryEnum);
                  loggersByCategory.clear();
                  if (loggerFactoryWasAlreadySet) {
                     logWarnInLogger("Logger library was already set earlier to \"" + getLibraryName(replacedLibraryEnum) + "\"; change to \"" + getLibraryName(libraryEnum) + "\" won't effect loggers created earlier.");
                  }
               }
            }

         }
      } else {
         throw new IllegalArgumentException("Library enum value out of range");
      }
   }

   /** @deprecated */
   @Deprecated
   public static void setCategoryPrefix(String prefix) {
      Class var1 = Logger.class;
      synchronized(Logger.class) {
         if (prefix == null) {
            throw new IllegalArgumentException();
         } else {
            categoryPrefix = prefix;
         }
      }
   }

   public abstract void debug(String var1);

   public abstract void debug(String var1, Throwable var2);

   public abstract void info(String var1);

   public abstract void info(String var1, Throwable var2);

   public abstract void warn(String var1);

   public abstract void warn(String var1, Throwable var2);

   public abstract void error(String var1);

   public abstract void error(String var1, Throwable var2);

   public abstract boolean isDebugEnabled();

   public abstract boolean isInfoEnabled();

   public abstract boolean isWarnEnabled();

   public abstract boolean isErrorEnabled();

   public abstract boolean isFatalEnabled();

   public static Logger getLogger(String category) {
      if (categoryPrefix.length() != 0) {
         category = categoryPrefix + category;
      }

      synchronized(loggersByCategory) {
         Logger logger = (Logger)loggersByCategory.get(category);
         if (logger == null) {
            ensureLoggerFactorySet(false);
            logger = loggerFactory.getLogger(category);
            loggersByCategory.put(category, logger);
         }

         return logger;
      }
   }

   private static void ensureLoggerFactorySet(boolean onlyIfCanBeSetFromSysProp) {
      if (loggerFactory == null) {
         Class var1 = Logger.class;
         synchronized(Logger.class) {
            if (loggerFactory == null) {
               String sysPropVal = getSystemProperty("org.freemarker.loggerLibrary");
               int libraryEnum;
               if (sysPropVal != null) {
                  sysPropVal = sysPropVal.trim();
                  boolean foundMatch = false;
                  int matchedEnum = -1;

                  do {
                     if (sysPropVal.equalsIgnoreCase(getLibraryName(matchedEnum))) {
                        foundMatch = true;
                     } else {
                        ++matchedEnum;
                     }
                  } while(matchedEnum <= 5 && !foundMatch);

                  if (!foundMatch) {
                     logWarnInLogger("Ignored invalid \"org.freemarker.loggerLibrary\" system property value: \"" + sysPropVal + "\"");
                     if (onlyIfCanBeSetFromSysProp) {
                        return;
                     }
                  }

                  libraryEnum = foundMatch ? matchedEnum : -1;
               } else {
                  if (onlyIfCanBeSetFromSysProp) {
                     return;
                  }

                  libraryEnum = -1;
               }

               try {
                  setLibrary(libraryEnum);
                  if (sysPropVal != null) {
                     initializedFromSystemProperty = true;
                  }
               } catch (Throwable var9) {
                  boolean disableLogging = !onlyIfCanBeSetFromSysProp || sysPropVal == null;
                  logErrorInLogger("Couldn't set up logger for \"" + getLibraryName(libraryEnum) + "\"" + (disableLogging ? "; logging disabled" : "."), var9);
                  if (disableLogging) {
                     try {
                        setLibrary(0);
                     } catch (ClassNotFoundException var8) {
                        throw new RuntimeException("Bug", var8);
                     }
                  }
               }

            }
         }
      }
   }

   private static LoggerFactory createLoggerFactory(int libraryEnum) throws ClassNotFoundException {
      if (libraryEnum == -1) {
         for(int libraryEnumToTry = 5; libraryEnumToTry >= -1; --libraryEnumToTry) {
            if (isAutoDetected(libraryEnumToTry)) {
               if (libraryEnumToTry == 3 && hasLog4LibraryThatDelegatesToWorkingSLF4J()) {
                  libraryEnumToTry = 5;
               }

               try {
                  return createLoggerFactoryForNonAuto(libraryEnumToTry);
               } catch (ClassNotFoundException var3) {
               } catch (Throwable var4) {
                  logErrorInLogger("Unexpected error when initializing logging for \"" + getLibraryName(libraryEnumToTry) + "\".", var4);
               }
            }
         }

         logWarnInLogger("Auto detecton couldn't set up any logger libraries; FreeMarker logging suppressed.");
         return new _NullLoggerFactory();
      } else {
         return createLoggerFactoryForNonAuto(libraryEnum);
      }
   }

   private static LoggerFactory createLoggerFactoryForNonAuto(int libraryEnum) throws ClassNotFoundException {
      String availabilityCheckClassName = getAvailabilityCheckClassName(libraryEnum);
      if (availabilityCheckClassName != null) {
         Class.forName(availabilityCheckClassName);
         String libraryName = getLibraryName(libraryEnum);

         try {
            return (LoggerFactory)Class.forName("freemarker.log._" + libraryName + "LoggerFactory").newInstance();
         } catch (Exception var4) {
            throw new RuntimeException("Unexpected error when creating logger factory for \"" + libraryName + "\".", var4);
         }
      } else if (libraryEnum == 1) {
         return new _JULLoggerFactory();
      } else if (libraryEnum == 0) {
         return new _NullLoggerFactory();
      } else {
         throw new RuntimeException("Bug");
      }
   }

   private static boolean hasLog4LibraryThatDelegatesToWorkingSLF4J() {
      try {
         Class.forName(getAvailabilityCheckClassName(3));
         Class.forName(getAvailabilityCheckClassName(5));
      } catch (Throwable var4) {
         return false;
      }

      try {
         Class.forName("org.apache.log4j.FileAppender");
         return false;
      } catch (ClassNotFoundException var3) {
         try {
            Object r = Class.forName("freemarker.log._Log4jOverSLF4JTester").getMethod("test").invoke((Object)null);
            return (Boolean)r;
         } catch (Throwable var2) {
            return false;
         }
      }
   }

   private static synchronized void setLibrary(int libraryEnum) throws ClassNotFoundException {
      loggerFactory = createLoggerFactory(libraryEnum);
      Logger.libraryEnum = libraryEnum;
   }

   private static void logWarnInLogger(String message) {
      logInLogger(false, message, (Throwable)null);
   }

   private static void logErrorInLogger(String message, Throwable exception) {
      logInLogger(true, message, exception);
   }

   private static void logInLogger(boolean error, String message, Throwable exception) {
      Class var4 = Logger.class;
      boolean canUseRealLogger;
      synchronized(Logger.class) {
         canUseRealLogger = loggerFactory != null && !(loggerFactory instanceof _NullLoggerFactory);
      }

      if (canUseRealLogger) {
         try {
            Logger logger = getLogger("freemarker.logger");
            if (error) {
               logger.error(message);
            } else {
               logger.warn(message);
            }
         } catch (Throwable var6) {
            canUseRealLogger = false;
         }
      }

      if (!canUseRealLogger) {
         System.err.println((error ? "ERROR" : "WARN") + " " + LoggerFactory.class.getName() + ": " + message);
         if (exception != null) {
            System.err.println("\tException: " + tryToString(exception));

            while(exception.getCause() != null) {
               exception = exception.getCause();
               System.err.println("\tCaused by: " + tryToString(exception));
            }
         }
      }

   }

   private static String getSystemProperty(final String key) {
      try {
         return (String)AccessController.doPrivileged(new PrivilegedAction() {
            public Object run() {
               return System.getProperty(key, (String)null);
            }
         });
      } catch (AccessControlException var2) {
         logWarnInLogger("Insufficient permissions to read system property \"" + key + "\".");
         return null;
      } catch (Throwable var3) {
         logErrorInLogger("Failed to read system property \"" + key + "\".", var3);
         return null;
      }
   }

   private static String tryToString(Object object) {
      if (object == null) {
         return null;
      } else {
         try {
            return object.toString();
         } catch (Throwable var2) {
            return object.getClass().getName();
         }
      }
   }

   static {
      if (LIBRARIES_BY_PRIORITY.length / 2 != 5) {
         throw new AssertionError();
      } else {
         categoryPrefix = "";
         loggersByCategory = new HashMap();
      }
   }
}
