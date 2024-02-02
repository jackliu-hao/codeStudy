/*     */ package org.slf4j;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.net.URL;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Enumeration;
/*     */ import java.util.LinkedHashSet;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.LinkedBlockingQueue;
/*     */ import org.slf4j.event.LoggingEvent;
/*     */ import org.slf4j.event.SubstituteLoggingEvent;
/*     */ import org.slf4j.helpers.NOPLoggerFactory;
/*     */ import org.slf4j.helpers.SubstituteLogger;
/*     */ import org.slf4j.helpers.SubstituteLoggerFactory;
/*     */ import org.slf4j.helpers.Util;
/*     */ import org.slf4j.impl.StaticLoggerBinder;
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
/*     */ public final class LoggerFactory
/*     */ {
/*     */   static final String CODES_PREFIX = "http://www.slf4j.org/codes.html";
/*     */   static final String NO_STATICLOGGERBINDER_URL = "http://www.slf4j.org/codes.html#StaticLoggerBinder";
/*     */   static final String MULTIPLE_BINDINGS_URL = "http://www.slf4j.org/codes.html#multiple_bindings";
/*     */   static final String NULL_LF_URL = "http://www.slf4j.org/codes.html#null_LF";
/*     */   static final String VERSION_MISMATCH = "http://www.slf4j.org/codes.html#version_mismatch";
/*     */   static final String SUBSTITUTE_LOGGER_URL = "http://www.slf4j.org/codes.html#substituteLogger";
/*     */   static final String LOGGER_NAME_MISMATCH_URL = "http://www.slf4j.org/codes.html#loggerNameMismatch";
/*     */   static final String REPLAY_URL = "http://www.slf4j.org/codes.html#replay";
/*     */   static final String UNSUCCESSFUL_INIT_URL = "http://www.slf4j.org/codes.html#unsuccessfulInit";
/*     */   static final String UNSUCCESSFUL_INIT_MSG = "org.slf4j.LoggerFactory in failed state. Original exception was thrown EARLIER. See also http://www.slf4j.org/codes.html#unsuccessfulInit";
/*     */   static final int UNINITIALIZED = 0;
/*     */   static final int ONGOING_INITIALIZATION = 1;
/*     */   static final int FAILED_INITIALIZATION = 2;
/*     */   static final int SUCCESSFUL_INITIALIZATION = 3;
/*     */   static final int NOP_FALLBACK_INITIALIZATION = 4;
/*  85 */   static volatile int INITIALIZATION_STATE = 0;
/*  86 */   static final SubstituteLoggerFactory SUBST_FACTORY = new SubstituteLoggerFactory();
/*  87 */   static final NOPLoggerFactory NOP_FALLBACK_FACTORY = new NOPLoggerFactory();
/*     */   
/*     */   static final String DETECT_LOGGER_NAME_MISMATCH_PROPERTY = "slf4j.detectLoggerNameMismatch";
/*     */   
/*     */   static final String JAVA_VENDOR_PROPERTY = "java.vendor.url";
/*     */   
/*  93 */   static boolean DETECT_LOGGER_NAME_MISMATCH = Util.safeGetBooleanSystemProperty("slf4j.detectLoggerNameMismatch");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 102 */   private static final String[] API_COMPATIBILITY_LIST = new String[] { "1.6", "1.7" };
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
/*     */   static void reset() {
/* 120 */     INITIALIZATION_STATE = 0;
/*     */   }
/*     */   
/*     */   private static final void performInitialization() {
/* 124 */     bind();
/* 125 */     if (INITIALIZATION_STATE == 3) {
/* 126 */       versionSanityCheck();
/*     */     }
/*     */   }
/*     */   
/*     */   private static boolean messageContainsOrgSlf4jImplStaticLoggerBinder(String msg) {
/* 131 */     if (msg == null)
/* 132 */       return false; 
/* 133 */     if (msg.contains("org/slf4j/impl/StaticLoggerBinder"))
/* 134 */       return true; 
/* 135 */     if (msg.contains("org.slf4j.impl.StaticLoggerBinder"))
/* 136 */       return true; 
/* 137 */     return false;
/*     */   }
/*     */   
/*     */   private static final void bind() {
/*     */     try {
/* 142 */       Set<URL> staticLoggerBinderPathSet = null;
/*     */ 
/*     */       
/* 145 */       if (!isAndroid()) {
/* 146 */         staticLoggerBinderPathSet = findPossibleStaticLoggerBinderPathSet();
/* 147 */         reportMultipleBindingAmbiguity(staticLoggerBinderPathSet);
/*     */       } 
/*     */       
/* 150 */       StaticLoggerBinder.getSingleton();
/* 151 */       INITIALIZATION_STATE = 3;
/* 152 */       reportActualBinding(staticLoggerBinderPathSet);
/* 153 */     } catch (NoClassDefFoundError ncde) {
/* 154 */       String msg = ncde.getMessage();
/* 155 */       if (messageContainsOrgSlf4jImplStaticLoggerBinder(msg)) {
/* 156 */         INITIALIZATION_STATE = 4;
/* 157 */         Util.report("Failed to load class \"org.slf4j.impl.StaticLoggerBinder\".");
/* 158 */         Util.report("Defaulting to no-operation (NOP) logger implementation");
/* 159 */         Util.report("See http://www.slf4j.org/codes.html#StaticLoggerBinder for further details.");
/*     */       } else {
/* 161 */         failedBinding(ncde);
/* 162 */         throw ncde;
/*     */       } 
/* 164 */     } catch (NoSuchMethodError nsme) {
/* 165 */       String msg = nsme.getMessage();
/* 166 */       if (msg != null && msg.contains("org.slf4j.impl.StaticLoggerBinder.getSingleton()")) {
/* 167 */         INITIALIZATION_STATE = 2;
/* 168 */         Util.report("slf4j-api 1.6.x (or later) is incompatible with this binding.");
/* 169 */         Util.report("Your binding is version 1.5.5 or earlier.");
/* 170 */         Util.report("Upgrade your binding to version 1.6.x.");
/*     */       } 
/* 172 */       throw nsme;
/* 173 */     } catch (Exception e) {
/* 174 */       failedBinding(e);
/* 175 */       throw new IllegalStateException("Unexpected initialization failure", e);
/*     */     } finally {
/* 177 */       postBindCleanUp();
/*     */     } 
/*     */   }
/*     */   
/*     */   private static void postBindCleanUp() {
/* 182 */     fixSubstituteLoggers();
/* 183 */     replayEvents();
/*     */     
/* 185 */     SUBST_FACTORY.clear();
/*     */   }
/*     */   
/*     */   private static void fixSubstituteLoggers() {
/* 189 */     synchronized (SUBST_FACTORY) {
/* 190 */       SUBST_FACTORY.postInitialization();
/* 191 */       for (SubstituteLogger substLogger : SUBST_FACTORY.getLoggers()) {
/* 192 */         Logger logger = getLogger(substLogger.getName());
/* 193 */         substLogger.setDelegate(logger);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   static void failedBinding(Throwable t) {
/* 200 */     INITIALIZATION_STATE = 2;
/* 201 */     Util.report("Failed to instantiate SLF4J LoggerFactory", t);
/*     */   }
/*     */   
/*     */   private static void replayEvents() {
/* 205 */     LinkedBlockingQueue<SubstituteLoggingEvent> queue = SUBST_FACTORY.getEventQueue();
/* 206 */     int queueSize = queue.size();
/* 207 */     int count = 0;
/* 208 */     int maxDrain = 128;
/* 209 */     List<SubstituteLoggingEvent> eventList = new ArrayList<SubstituteLoggingEvent>(128);
/*     */     while (true) {
/* 211 */       int numDrained = queue.drainTo(eventList, 128);
/* 212 */       if (numDrained == 0)
/*     */         break; 
/* 214 */       for (SubstituteLoggingEvent event : eventList) {
/* 215 */         replaySingleEvent(event);
/* 216 */         if (count++ == 0)
/* 217 */           emitReplayOrSubstituionWarning(event, queueSize); 
/*     */       } 
/* 219 */       eventList.clear();
/*     */     } 
/*     */   }
/*     */   
/*     */   private static void emitReplayOrSubstituionWarning(SubstituteLoggingEvent event, int queueSize) {
/* 224 */     if (event.getLogger().isDelegateEventAware()) {
/* 225 */       emitReplayWarning(queueSize);
/* 226 */     } else if (!event.getLogger().isDelegateNOP()) {
/*     */ 
/*     */       
/* 229 */       emitSubstitutionWarning();
/*     */     } 
/*     */   }
/*     */   
/*     */   private static void replaySingleEvent(SubstituteLoggingEvent event) {
/* 234 */     if (event == null) {
/*     */       return;
/*     */     }
/* 237 */     SubstituteLogger substLogger = event.getLogger();
/* 238 */     String loggerName = substLogger.getName();
/* 239 */     if (substLogger.isDelegateNull()) {
/* 240 */       throw new IllegalStateException("Delegate logger cannot be null at this state.");
/*     */     }
/*     */     
/* 243 */     if (!substLogger.isDelegateNOP())
/*     */     {
/* 245 */       if (substLogger.isDelegateEventAware()) {
/* 246 */         substLogger.log((LoggingEvent)event);
/*     */       } else {
/* 248 */         Util.report(loggerName);
/*     */       }  } 
/*     */   }
/*     */   
/*     */   private static void emitSubstitutionWarning() {
/* 253 */     Util.report("The following set of substitute loggers may have been accessed");
/* 254 */     Util.report("during the initialization phase. Logging calls during this");
/* 255 */     Util.report("phase were not honored. However, subsequent logging calls to these");
/* 256 */     Util.report("loggers will work as normally expected.");
/* 257 */     Util.report("See also http://www.slf4j.org/codes.html#substituteLogger");
/*     */   }
/*     */   
/*     */   private static void emitReplayWarning(int eventCount) {
/* 261 */     Util.report("A number (" + eventCount + ") of logging calls during the initialization phase have been intercepted and are");
/* 262 */     Util.report("now being replayed. These are subject to the filtering rules of the underlying logging system.");
/* 263 */     Util.report("See also http://www.slf4j.org/codes.html#replay");
/*     */   }
/*     */   
/*     */   private static final void versionSanityCheck() {
/*     */     try {
/* 268 */       String requested = StaticLoggerBinder.REQUESTED_API_VERSION;
/*     */       
/* 270 */       boolean match = false;
/* 271 */       for (String aAPI_COMPATIBILITY_LIST : API_COMPATIBILITY_LIST) {
/* 272 */         if (requested.startsWith(aAPI_COMPATIBILITY_LIST)) {
/* 273 */           match = true;
/*     */         }
/*     */       } 
/* 276 */       if (!match) {
/* 277 */         Util.report("The requested version " + requested + " by your slf4j binding is not compatible with " + 
/* 278 */             Arrays.<String>asList(API_COMPATIBILITY_LIST).toString());
/* 279 */         Util.report("See http://www.slf4j.org/codes.html#version_mismatch for further details.");
/*     */       } 
/* 281 */     } catch (NoSuchFieldError noSuchFieldError) {
/*     */ 
/*     */ 
/*     */     
/*     */     }
/* 286 */     catch (Throwable e) {
/*     */       
/* 288 */       Util.report("Unexpected problem occured during version sanity check", e);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 295 */   private static String STATIC_LOGGER_BINDER_PATH = "org/slf4j/impl/StaticLoggerBinder.class";
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static Set<URL> findPossibleStaticLoggerBinderPathSet() {
/* 301 */     Set<URL> staticLoggerBinderPathSet = new LinkedHashSet<URL>(); try {
/*     */       Enumeration<URL> paths;
/* 303 */       ClassLoader loggerFactoryClassLoader = LoggerFactory.class.getClassLoader();
/*     */       
/* 305 */       if (loggerFactoryClassLoader == null) {
/* 306 */         paths = ClassLoader.getSystemResources(STATIC_LOGGER_BINDER_PATH);
/*     */       } else {
/* 308 */         paths = loggerFactoryClassLoader.getResources(STATIC_LOGGER_BINDER_PATH);
/*     */       } 
/* 310 */       while (paths.hasMoreElements()) {
/* 311 */         URL path = paths.nextElement();
/* 312 */         staticLoggerBinderPathSet.add(path);
/*     */       } 
/* 314 */     } catch (IOException ioe) {
/* 315 */       Util.report("Error getting resources from path", ioe);
/*     */     } 
/* 317 */     return staticLoggerBinderPathSet;
/*     */   }
/*     */   
/*     */   private static boolean isAmbiguousStaticLoggerBinderPathSet(Set<URL> binderPathSet) {
/* 321 */     return (binderPathSet.size() > 1);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static void reportMultipleBindingAmbiguity(Set<URL> binderPathSet) {
/* 330 */     if (isAmbiguousStaticLoggerBinderPathSet(binderPathSet)) {
/* 331 */       Util.report("Class path contains multiple SLF4J bindings.");
/* 332 */       for (URL path : binderPathSet) {
/* 333 */         Util.report("Found binding in [" + path + "]");
/*     */       }
/* 335 */       Util.report("See http://www.slf4j.org/codes.html#multiple_bindings for an explanation.");
/*     */     } 
/*     */   }
/*     */   
/*     */   private static boolean isAndroid() {
/* 340 */     String vendor = Util.safeGetSystemProperty("java.vendor.url");
/* 341 */     if (vendor == null)
/* 342 */       return false; 
/* 343 */     return vendor.toLowerCase().contains("android");
/*     */   }
/*     */ 
/*     */   
/*     */   private static void reportActualBinding(Set<URL> binderPathSet) {
/* 348 */     if (binderPathSet != null && isAmbiguousStaticLoggerBinderPathSet(binderPathSet)) {
/* 349 */       Util.report("Actual binding is of type [" + StaticLoggerBinder.getSingleton().getLoggerFactoryClassStr() + "]");
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
/*     */   public static Logger getLogger(String name) {
/* 362 */     ILoggerFactory iLoggerFactory = getILoggerFactory();
/* 363 */     return iLoggerFactory.getLogger(name);
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
/*     */   public static Logger getLogger(Class<?> clazz) {
/* 388 */     Logger logger = getLogger(clazz.getName());
/* 389 */     if (DETECT_LOGGER_NAME_MISMATCH) {
/* 390 */       Class<?> autoComputedCallingClass = Util.getCallingClass();
/* 391 */       if (autoComputedCallingClass != null && nonMatchingClasses(clazz, autoComputedCallingClass)) {
/* 392 */         Util.report(String.format("Detected logger name mismatch. Given name: \"%s\"; computed name: \"%s\".", new Object[] { logger.getName(), autoComputedCallingClass
/* 393 */                 .getName() }));
/* 394 */         Util.report("See http://www.slf4j.org/codes.html#loggerNameMismatch for an explanation");
/*     */       } 
/*     */     } 
/* 397 */     return logger;
/*     */   }
/*     */   
/*     */   private static boolean nonMatchingClasses(Class<?> clazz, Class<?> autoComputedCallingClass) {
/* 401 */     return !autoComputedCallingClass.isAssignableFrom(clazz);
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
/*     */   public static ILoggerFactory getILoggerFactory() {
/* 413 */     if (INITIALIZATION_STATE == 0) {
/* 414 */       synchronized (LoggerFactory.class) {
/* 415 */         if (INITIALIZATION_STATE == 0) {
/* 416 */           INITIALIZATION_STATE = 1;
/* 417 */           performInitialization();
/*     */         } 
/*     */       } 
/*     */     }
/* 421 */     switch (INITIALIZATION_STATE) {
/*     */       case 3:
/* 423 */         return StaticLoggerBinder.getSingleton().getLoggerFactory();
/*     */       case 4:
/* 425 */         return (ILoggerFactory)NOP_FALLBACK_FACTORY;
/*     */       case 2:
/* 427 */         throw new IllegalStateException("org.slf4j.LoggerFactory in failed state. Original exception was thrown EARLIER. See also http://www.slf4j.org/codes.html#unsuccessfulInit");
/*     */ 
/*     */       
/*     */       case 1:
/* 431 */         return (ILoggerFactory)SUBST_FACTORY;
/*     */     } 
/* 433 */     throw new IllegalStateException("Unreachable code");
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\slf4j\LoggerFactory.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */