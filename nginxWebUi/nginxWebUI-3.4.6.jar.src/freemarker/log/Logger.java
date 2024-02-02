/*     */ package freemarker.log;
/*     */ 
/*     */ import java.security.AccessControlException;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedAction;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class Logger
/*     */ {
/*     */   public static final String SYSTEM_PROPERTY_NAME_LOGGER_LIBRARY = "org.freemarker.loggerLibrary";
/*     */   public static final int LIBRARY_AUTO = -1;
/*     */   private static final int MIN_LIBRARY_ENUM = -1;
/*     */   public static final String LIBRARY_NAME_AUTO = "auto";
/*     */   public static final int LIBRARY_NONE = 0;
/*     */   public static final String LIBRARY_NAME_NONE = "none";
/*     */   public static final int LIBRARY_JAVA = 1;
/*     */   public static final String LIBRARY_NAME_JUL = "JUL";
/*     */   @Deprecated
/*     */   public static final int LIBRARY_AVALON = 2;
/*     */   @Deprecated
/*     */   public static final String LIBRARY_NAME_AVALON = "Avalon";
/*     */   public static final int LIBRARY_LOG4J = 3;
/*     */   public static final String LIBRARY_NAME_LOG4J = "Log4j";
/*     */   public static final int LIBRARY_COMMONS = 4;
/*     */   public static final String LIBRARY_NAME_COMMONS_LOGGING = "CommonsLogging";
/*     */   public static final int LIBRARY_SLF4J = 5;
/*     */   public static final String LIBRARY_NAME_SLF4J = "SLF4J";
/*     */   private static final int MAX_LIBRARY_ENUM = 5;
/*     */   private static final String REAL_LOG4J_PRESENCE_CLASS = "org.apache.log4j.FileAppender";
/*     */   private static final String LOG4J_OVER_SLF4J_TESTER_CLASS = "freemarker.log._Log4jOverSLF4JTester";
/* 163 */   private static final String[] LIBRARIES_BY_PRIORITY = new String[] { null, "JUL", "org.apache.log.Logger", "Avalon", "org.apache.log4j.Logger", "Log4j", "org.apache.commons.logging.Log", "CommonsLogging", "org.slf4j.Logger", "SLF4J" };
/*     */   
/*     */   private static int libraryEnum;
/*     */   
/*     */   private static LoggerFactory loggerFactory;
/*     */   
/*     */   private static boolean initializedFromSystemProperty;
/*     */ 
/*     */   
/*     */   private static String getAvailabilityCheckClassName(int libraryEnum) {
/* 173 */     if (libraryEnum == -1 || libraryEnum == 0)
/*     */     {
/* 175 */       return null;
/*     */     }
/* 177 */     return LIBRARIES_BY_PRIORITY[(libraryEnum - 1) * 2];
/*     */   }
/*     */   
/*     */   static {
/* 181 */     if (LIBRARIES_BY_PRIORITY.length / 2 != 5) {
/* 182 */       throw new AssertionError();
/*     */     }
/*     */   }
/*     */   
/*     */   private static String getLibraryName(int libraryEnum) {
/* 187 */     if (libraryEnum == -1) {
/* 188 */       return "auto";
/*     */     }
/* 190 */     if (libraryEnum == 0) {
/* 191 */       return "none";
/*     */     }
/* 193 */     return LIBRARIES_BY_PRIORITY[(libraryEnum - 1) * 2 + 1];
/*     */   }
/*     */ 
/*     */   
/*     */   private static boolean isAutoDetected(int libraryEnum) {
/* 198 */     return (libraryEnum != -1 && libraryEnum != 0 && libraryEnum != 5 && libraryEnum != 4);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 206 */   private static String categoryPrefix = "";
/*     */   
/* 208 */   private static final Map loggersByCategory = new HashMap<>();
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
/*     */   @Deprecated
/*     */   public static void selectLoggerLibrary(int libraryEnum) throws ClassNotFoundException {
/* 230 */     if (libraryEnum < -1 || libraryEnum > 5) {
/* 231 */       throw new IllegalArgumentException("Library enum value out of range");
/*     */     }
/*     */     
/* 234 */     synchronized (Logger.class) {
/* 235 */       boolean loggerFactoryWasAlreadySet = (loggerFactory != null);
/* 236 */       if (!loggerFactoryWasAlreadySet || libraryEnum != Logger.libraryEnum) {
/*     */         
/* 238 */         ensureLoggerFactorySet(true);
/*     */ 
/*     */         
/* 241 */         if (!initializedFromSystemProperty || loggerFactory == null) {
/* 242 */           int replacedLibraryEnum = Logger.libraryEnum;
/* 243 */           setLibrary(libraryEnum);
/* 244 */           loggersByCategory.clear();
/* 245 */           if (loggerFactoryWasAlreadySet) {
/* 246 */             logWarnInLogger("Logger library was already set earlier to \"" + 
/* 247 */                 getLibraryName(replacedLibraryEnum) + "\"; change to \"" + 
/* 248 */                 getLibraryName(libraryEnum) + "\" won't effect loggers created earlier.");
/*     */           }
/*     */         }
/* 251 */         else if (libraryEnum != Logger.libraryEnum) {
/* 252 */           logWarnInLogger("Ignored " + Logger.class.getName() + ".selectLoggerLibrary(\"" + 
/* 253 */               getLibraryName(libraryEnum) + "\") call, because the \"" + "org.freemarker.loggerLibrary" + "\" system property is set to \"" + 
/*     */               
/* 255 */               getLibraryName(Logger.libraryEnum) + "\".");
/*     */         } 
/*     */       } 
/*     */     } 
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
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public static void setCategoryPrefix(String prefix) {
/* 274 */     synchronized (Logger.class) {
/* 275 */       if (prefix == null) {
/* 276 */         throw new IllegalArgumentException();
/*     */       }
/* 278 */       categoryPrefix = prefix;
/*     */     } 
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
/*     */   
/*     */   public static Logger getLogger(String category) {
/* 355 */     if (categoryPrefix.length() != 0) {
/* 356 */       category = categoryPrefix + category;
/*     */     }
/* 358 */     synchronized (loggersByCategory) {
/* 359 */       Logger logger = (Logger)loggersByCategory.get(category);
/* 360 */       if (logger == null) {
/* 361 */         ensureLoggerFactorySet(false);
/* 362 */         logger = loggerFactory.getLogger(category);
/* 363 */         loggersByCategory.put(category, logger);
/*     */       } 
/* 365 */       return logger;
/*     */     } 
/*     */   }
/*     */   
/*     */   private static void ensureLoggerFactorySet(boolean onlyIfCanBeSetFromSysProp) {
/* 370 */     if (loggerFactory != null)
/* 371 */       return;  synchronized (Logger.class) {
/* 372 */       int libraryEnum; if (loggerFactory != null)
/*     */         return; 
/* 374 */       String sysPropVal = getSystemProperty("org.freemarker.loggerLibrary");
/*     */ 
/*     */       
/* 377 */       if (sysPropVal != null) {
/* 378 */         sysPropVal = sysPropVal.trim();
/*     */         
/* 380 */         boolean foundMatch = false;
/* 381 */         int matchedEnum = -1;
/*     */         do {
/* 383 */           if (sysPropVal.equalsIgnoreCase(getLibraryName(matchedEnum))) {
/* 384 */             foundMatch = true;
/*     */           } else {
/* 386 */             matchedEnum++;
/*     */           } 
/* 388 */         } while (matchedEnum <= 5 && !foundMatch);
/*     */         
/* 390 */         if (!foundMatch) {
/* 391 */           logWarnInLogger("Ignored invalid \"org.freemarker.loggerLibrary\" system property value: \"" + sysPropVal + "\"");
/*     */           
/* 393 */           if (onlyIfCanBeSetFromSysProp)
/*     */             return; 
/*     */         } 
/* 396 */         libraryEnum = foundMatch ? matchedEnum : -1;
/*     */       } else {
/* 398 */         if (onlyIfCanBeSetFromSysProp)
/* 399 */           return;  libraryEnum = -1;
/*     */       } 
/*     */       
/*     */       try {
/* 403 */         setLibrary(libraryEnum);
/* 404 */         if (sysPropVal != null) {
/* 405 */           initializedFromSystemProperty = true;
/*     */         }
/* 407 */       } catch (Throwable e) {
/* 408 */         boolean disableLogging = (!onlyIfCanBeSetFromSysProp || sysPropVal == null);
/* 409 */         logErrorInLogger("Couldn't set up logger for \"" + 
/* 410 */             getLibraryName(libraryEnum) + "\"" + (disableLogging ? "; logging disabled" : "."), e);
/*     */         
/* 412 */         if (disableLogging) {
/*     */           try {
/* 414 */             setLibrary(0);
/* 415 */           } catch (ClassNotFoundException e2) {
/* 416 */             throw new RuntimeException("Bug", e2);
/*     */           } 
/*     */         }
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static LoggerFactory createLoggerFactory(int libraryEnum) throws ClassNotFoundException {
/* 428 */     if (libraryEnum == -1) {
/* 429 */       for (int libraryEnumToTry = 5; libraryEnumToTry >= -1; libraryEnumToTry--) {
/* 430 */         if (isAutoDetected(libraryEnumToTry)) {
/* 431 */           if (libraryEnumToTry == 3 && hasLog4LibraryThatDelegatesToWorkingSLF4J()) {
/* 432 */             libraryEnumToTry = 5;
/*     */           }
/*     */           
/*     */           try {
/* 436 */             return createLoggerFactoryForNonAuto(libraryEnumToTry);
/* 437 */           } catch (ClassNotFoundException classNotFoundException) {
/*     */           
/* 439 */           } catch (Throwable e) {
/* 440 */             logErrorInLogger("Unexpected error when initializing logging for \"" + 
/*     */                 
/* 442 */                 getLibraryName(libraryEnumToTry) + "\".", e);
/*     */           } 
/*     */         } 
/*     */       } 
/* 446 */       logWarnInLogger("Auto detecton couldn't set up any logger libraries; FreeMarker logging suppressed.");
/* 447 */       return new _NullLoggerFactory();
/*     */     } 
/* 449 */     return createLoggerFactoryForNonAuto(libraryEnum);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static LoggerFactory createLoggerFactoryForNonAuto(int libraryEnum) throws ClassNotFoundException {
/* 458 */     String availabilityCheckClassName = getAvailabilityCheckClassName(libraryEnum);
/* 459 */     if (availabilityCheckClassName != null) {
/* 460 */       Class.forName(availabilityCheckClassName);
/* 461 */       String libraryName = getLibraryName(libraryEnum);
/*     */       try {
/* 463 */         return (LoggerFactory)Class.forName("freemarker.log._" + libraryName + "LoggerFactory")
/* 464 */           .newInstance();
/* 465 */       } catch (Exception e) {
/* 466 */         throw new RuntimeException("Unexpected error when creating logger factory for \"" + libraryName + "\".", e);
/*     */       } 
/*     */     } 
/*     */     
/* 470 */     if (libraryEnum == 1)
/* 471 */       return new _JULLoggerFactory(); 
/* 472 */     if (libraryEnum == 0) {
/* 473 */       return new _NullLoggerFactory();
/*     */     }
/* 475 */     throw new RuntimeException("Bug");
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static boolean hasLog4LibraryThatDelegatesToWorkingSLF4J() {
/*     */     try {
/* 482 */       Class.forName(getAvailabilityCheckClassName(3));
/* 483 */       Class.forName(getAvailabilityCheckClassName(5));
/* 484 */     } catch (Throwable e) {
/* 485 */       return false;
/*     */     } 
/*     */     try {
/* 488 */       Class.forName("org.apache.log4j.FileAppender");
/* 489 */       return false;
/* 490 */     } catch (ClassNotFoundException e) {
/*     */       
/*     */       try {
/* 493 */         Object r = Class.forName("freemarker.log._Log4jOverSLF4JTester").getMethod("test", new Class[0]).invoke(null, new Object[0]);
/* 494 */         return ((Boolean)r).booleanValue();
/* 495 */       } catch (Throwable e2) {
/* 496 */         return false;
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   private static synchronized void setLibrary(int libraryEnum) throws ClassNotFoundException {
/* 502 */     loggerFactory = createLoggerFactory(libraryEnum);
/* 503 */     Logger.libraryEnum = libraryEnum;
/*     */   }
/*     */   
/*     */   private static void logWarnInLogger(String message) {
/* 507 */     logInLogger(false, message, null);
/*     */   }
/*     */   
/*     */   private static void logErrorInLogger(String message, Throwable exception) {
/* 511 */     logInLogger(true, message, exception);
/*     */   }
/*     */   
/*     */   private static void logInLogger(boolean error, String message, Throwable exception) {
/*     */     boolean canUseRealLogger;
/* 516 */     synchronized (Logger.class) {
/* 517 */       canUseRealLogger = (loggerFactory != null && !(loggerFactory instanceof _NullLoggerFactory));
/*     */     } 
/*     */     
/* 520 */     if (canUseRealLogger) {
/*     */       try {
/* 522 */         Logger logger = getLogger("freemarker.logger");
/* 523 */         if (error) {
/* 524 */           logger.error(message);
/*     */         } else {
/* 526 */           logger.warn(message);
/*     */         } 
/* 528 */       } catch (Throwable e) {
/* 529 */         canUseRealLogger = false;
/*     */       } 
/*     */     }
/*     */     
/* 533 */     if (!canUseRealLogger) {
/* 534 */       System.err.println((error ? "ERROR" : "WARN") + " " + LoggerFactory.class
/* 535 */           .getName() + ": " + message);
/* 536 */       if (exception != null) {
/* 537 */         System.err.println("\tException: " + tryToString(exception));
/* 538 */         while (exception.getCause() != null) {
/* 539 */           exception = exception.getCause();
/* 540 */           System.err.println("\tCaused by: " + tryToString(exception));
/*     */         } 
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static String getSystemProperty(final String key) {
/*     */     try {
/* 552 */       return AccessController.<String>doPrivileged(new PrivilegedAction<String>()
/*     */           {
/*     */             public Object run()
/*     */             {
/* 556 */               return System.getProperty(key, null);
/*     */             }
/*     */           });
/* 559 */     } catch (AccessControlException e) {
/* 560 */       logWarnInLogger("Insufficient permissions to read system property \"" + key + "\".");
/* 561 */       return null;
/* 562 */     } catch (Throwable e) {
/* 563 */       logErrorInLogger("Failed to read system property \"" + key + "\".", e);
/* 564 */       return null;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static String tryToString(Object object) {
/* 573 */     if (object == null) return null; 
/*     */     try {
/* 575 */       return object.toString();
/* 576 */     } catch (Throwable e) {
/* 577 */       return object.getClass().getName();
/*     */     } 
/*     */   }
/*     */   
/*     */   public abstract void debug(String paramString);
/*     */   
/*     */   public abstract void debug(String paramString, Throwable paramThrowable);
/*     */   
/*     */   public abstract void info(String paramString);
/*     */   
/*     */   public abstract void info(String paramString, Throwable paramThrowable);
/*     */   
/*     */   public abstract void warn(String paramString);
/*     */   
/*     */   public abstract void warn(String paramString, Throwable paramThrowable);
/*     */   
/*     */   public abstract void error(String paramString);
/*     */   
/*     */   public abstract void error(String paramString, Throwable paramThrowable);
/*     */   
/*     */   public abstract boolean isDebugEnabled();
/*     */   
/*     */   public abstract boolean isInfoEnabled();
/*     */   
/*     */   public abstract boolean isWarnEnabled();
/*     */   
/*     */   public abstract boolean isErrorEnabled();
/*     */   
/*     */   public abstract boolean isFatalEnabled();
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\log\Logger.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */