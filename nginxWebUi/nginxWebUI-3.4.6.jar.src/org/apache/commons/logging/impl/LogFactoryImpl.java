/*      */ package org.apache.commons.logging.impl;
/*      */ 
/*      */ import java.io.PrintWriter;
/*      */ import java.io.StringWriter;
/*      */ import java.lang.reflect.Constructor;
/*      */ import java.lang.reflect.InvocationTargetException;
/*      */ import java.lang.reflect.Method;
/*      */ import java.net.URL;
/*      */ import java.security.AccessController;
/*      */ import java.security.PrivilegedAction;
/*      */ import java.util.Hashtable;
/*      */ import org.apache.commons.logging.Log;
/*      */ import org.apache.commons.logging.LogConfigurationException;
/*      */ import org.apache.commons.logging.LogFactory;
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
/*      */ public class LogFactoryImpl
/*      */   extends LogFactory
/*      */ {
/*      */   private static final String LOGGING_IMPL_LOG4J_LOGGER = "org.apache.commons.logging.impl.Log4JLogger";
/*      */   private static final String LOGGING_IMPL_JDK14_LOGGER = "org.apache.commons.logging.impl.Jdk14Logger";
/*      */   private static final String LOGGING_IMPL_LUMBERJACK_LOGGER = "org.apache.commons.logging.impl.Jdk13LumberjackLogger";
/*      */   private static final String LOGGING_IMPL_SIMPLE_LOGGER = "org.apache.commons.logging.impl.SimpleLog";
/*      */   private static final String PKG_IMPL = "org.apache.commons.logging.impl.";
/*   78 */   private static final int PKG_LEN = "org.apache.commons.logging.impl.".length();
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
/*      */   public static final String LOG_PROPERTY = "org.apache.commons.logging.Log";
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
/*      */   protected static final String LOG_PROPERTY_OLD = "org.apache.commons.logging.log";
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
/*      */   public static final String ALLOW_FLAWED_CONTEXT_PROPERTY = "org.apache.commons.logging.Log.allowFlawedContext";
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
/*      */   public static final String ALLOW_FLAWED_DISCOVERY_PROPERTY = "org.apache.commons.logging.Log.allowFlawedDiscovery";
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
/*      */   public static final String ALLOW_FLAWED_HIERARCHY_PROPERTY = "org.apache.commons.logging.Log.allowFlawedHierarchy";
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
/*      */   public LogFactoryImpl() {
/*  175 */     this.useTCCL = true;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  185 */     this.attributes = new Hashtable();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  191 */     this.instances = new Hashtable();
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
/*  205 */     this.logConstructor = null;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  210 */     this.logConstructorSignature = new Class[] { String.class };
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  216 */     this.logMethod = null;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  221 */     this.logMethodSignature = new Class[] { LogFactory.class };
/*      */     initDiagnostics();
/*      */     if (isDiagnosticsEnabled()) {
/*      */       logDiagnostic("Instance created.");
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   private static final String[] classesToDiscover = new String[] { "org.apache.commons.logging.impl.Log4JLogger", "org.apache.commons.logging.impl.Jdk14Logger", "org.apache.commons.logging.impl.Jdk13LumberjackLogger", "org.apache.commons.logging.impl.SimpleLog" };
/*      */   
/*      */   private boolean useTCCL;
/*      */   
/*      */   private String diagnosticPrefix;
/*      */   
/*      */   protected Hashtable attributes;
/*      */   protected Hashtable instances;
/*      */   private String logClassName;
/*      */   protected Constructor logConstructor;
/*      */   protected Class[] logConstructorSignature;
/*      */   protected Method logMethod;
/*      */   protected Class[] logMethodSignature;
/*      */   private boolean allowFlawedContext;
/*      */   private boolean allowFlawedDiscovery;
/*      */   private boolean allowFlawedHierarchy;
/*      */   
/*      */   public Object getAttribute(String name) {
/*  247 */     return this.attributes.get(name);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String[] getAttributeNames() {
/*  256 */     return (String[])this.attributes.keySet().toArray((Object[])new String[this.attributes.size()]);
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
/*      */   public Log getInstance(Class clazz) throws LogConfigurationException {
/*  269 */     return getInstance(clazz.getName());
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
/*      */   public Log getInstance(String name) throws LogConfigurationException {
/*  290 */     Log instance = (Log)this.instances.get(name);
/*  291 */     if (instance == null) {
/*  292 */       instance = newInstance(name);
/*  293 */       this.instances.put(name, instance);
/*      */     } 
/*  295 */     return instance;
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
/*      */   public void release() {
/*  308 */     logDiagnostic("Releasing all known loggers");
/*  309 */     this.instances.clear();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void removeAttribute(String name) {
/*  319 */     this.attributes.remove(name);
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
/*      */   public void setAttribute(String name, Object value) {
/*  347 */     if (this.logConstructor != null) {
/*  348 */       logDiagnostic("setAttribute: call too late; configuration already performed.");
/*      */     }
/*      */     
/*  351 */     if (value == null) {
/*  352 */       this.attributes.remove(name);
/*      */     } else {
/*  354 */       this.attributes.put(name, value);
/*      */     } 
/*      */     
/*  357 */     if (name.equals("use_tccl")) {
/*  358 */       this.useTCCL = (value != null && Boolean.valueOf(value.toString()).booleanValue());
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
/*      */   
/*      */   protected static ClassLoader getContextClassLoader() throws LogConfigurationException {
/*  375 */     return LogFactory.getContextClassLoader();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected static boolean isDiagnosticsEnabled() {
/*  383 */     return LogFactory.isDiagnosticsEnabled();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected static ClassLoader getClassLoader(Class clazz) {
/*  392 */     return LogFactory.getClassLoader(clazz);
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
/*      */   private void initDiagnostics() {
/*      */     String str;
/*  419 */     Class clazz = getClass();
/*  420 */     ClassLoader classLoader = getClassLoader(clazz);
/*      */     
/*      */     try {
/*  423 */       if (classLoader == null) {
/*  424 */         str = "BOOTLOADER";
/*      */       } else {
/*  426 */         str = objectId(classLoader);
/*      */       } 
/*  428 */     } catch (SecurityException e) {
/*  429 */       str = "UNKNOWN";
/*      */     } 
/*  431 */     this.diagnosticPrefix = "[LogFactoryImpl@" + System.identityHashCode(this) + " from " + str + "] ";
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void logDiagnostic(String msg) {
/*  442 */     if (isDiagnosticsEnabled()) {
/*  443 */       logRawDiagnostic(this.diagnosticPrefix + msg);
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
/*      */   protected String getLogClassName() {
/*  455 */     if (this.logClassName == null) {
/*  456 */       discoverLogImplementation(getClass().getName());
/*      */     }
/*      */     
/*  459 */     return this.logClassName;
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
/*      */   protected Constructor getLogConstructor() throws LogConfigurationException {
/*  482 */     if (this.logConstructor == null) {
/*  483 */       discoverLogImplementation(getClass().getName());
/*      */     }
/*      */     
/*  486 */     return this.logConstructor;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected boolean isJdk13LumberjackAvailable() {
/*  496 */     return isLogLibraryAvailable("Jdk13Lumberjack", "org.apache.commons.logging.impl.Jdk13LumberjackLogger");
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
/*      */   protected boolean isJdk14Available() {
/*  511 */     return isLogLibraryAvailable("Jdk14", "org.apache.commons.logging.impl.Jdk14Logger");
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
/*      */   protected boolean isLog4JAvailable() {
/*  523 */     return isLogLibraryAvailable("Log4J", "org.apache.commons.logging.impl.Log4JLogger");
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
/*      */   protected Log newInstance(String name) throws LogConfigurationException {
/*      */     try {
/*      */       Log instance;
/*  540 */       if (this.logConstructor == null) {
/*  541 */         instance = discoverLogImplementation(name);
/*      */       } else {
/*      */         
/*  544 */         Object[] params = { name };
/*  545 */         instance = this.logConstructor.newInstance(params);
/*      */       } 
/*      */       
/*  548 */       if (this.logMethod != null) {
/*  549 */         Object[] params = { this };
/*  550 */         this.logMethod.invoke(instance, params);
/*      */       } 
/*      */       
/*  553 */       return instance;
/*      */     }
/*  555 */     catch (LogConfigurationException lce) {
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  560 */       throw lce;
/*      */     }
/*  562 */     catch (InvocationTargetException e) {
/*      */ 
/*      */       
/*  565 */       Throwable c = e.getTargetException();
/*  566 */       throw new LogConfigurationException((c == null) ? e : c);
/*  567 */     } catch (Throwable t) {
/*  568 */       handleThrowable(t);
/*      */ 
/*      */       
/*  571 */       throw new LogConfigurationException(t);
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
/*      */   private static ClassLoader getContextClassLoaderInternal() throws LogConfigurationException {
/*  597 */     return AccessController.<ClassLoader>doPrivileged(new PrivilegedAction()
/*      */         {
/*      */           public Object run() {
/*  600 */             return LogFactoryImpl.directGetContextClassLoader();
/*      */           }
/*      */         });
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
/*      */   private static String getSystemProperty(String key, String def) throws SecurityException {
/*  616 */     return AccessController.<String>doPrivileged(new PrivilegedAction(key, def) { private final String val$key;
/*      */           
/*      */           public Object run() {
/*  619 */             return System.getProperty(this.val$key, this.val$def);
/*      */           }
/*      */ 
/*      */ 
/*      */ 
/*      */           
/*      */           private final String val$def; }
/*      */       );
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private ClassLoader getParentClassLoader(ClassLoader cl) {
/*      */     try {
/*  633 */       return AccessController.<ClassLoader>doPrivileged(new PrivilegedAction(this, cl) { private final ClassLoader val$cl;
/*      */             
/*      */             public Object run() {
/*  636 */               return this.val$cl.getParent();
/*      */             } private final LogFactoryImpl this$0; }
/*      */         );
/*  639 */     } catch (SecurityException ex) {
/*  640 */       logDiagnostic("[SECURITY] Unable to obtain parent classloader");
/*  641 */       return null;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean isLogLibraryAvailable(String name, String classname) {
/*  652 */     if (isDiagnosticsEnabled()) {
/*  653 */       logDiagnostic("Checking for '" + name + "'.");
/*      */     }
/*      */     try {
/*  656 */       Log log = createLogFromClass(classname, getClass().getName(), false);
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  661 */       if (log == null) {
/*  662 */         if (isDiagnosticsEnabled()) {
/*  663 */           logDiagnostic("Did not find '" + name + "'.");
/*      */         }
/*  665 */         return false;
/*      */       } 
/*  667 */       if (isDiagnosticsEnabled()) {
/*  668 */         logDiagnostic("Found '" + name + "'.");
/*      */       }
/*  670 */       return true;
/*      */     }
/*  672 */     catch (LogConfigurationException e) {
/*  673 */       if (isDiagnosticsEnabled()) {
/*  674 */         logDiagnostic("Logging system '" + name + "' is available but not useable.");
/*      */       }
/*  676 */       return false;
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
/*      */   private String getConfigurationValue(String property) {
/*  692 */     if (isDiagnosticsEnabled()) {
/*  693 */       logDiagnostic("[ENV] Trying to get configuration for item " + property);
/*      */     }
/*      */     
/*  696 */     Object valueObj = getAttribute(property);
/*  697 */     if (valueObj != null) {
/*  698 */       if (isDiagnosticsEnabled()) {
/*  699 */         logDiagnostic("[ENV] Found LogFactory attribute [" + valueObj + "] for " + property);
/*      */       }
/*  701 */       return valueObj.toString();
/*      */     } 
/*      */     
/*  704 */     if (isDiagnosticsEnabled()) {
/*  705 */       logDiagnostic("[ENV] No LogFactory attribute found for " + property);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     try {
/*  713 */       String value = getSystemProperty(property, (String)null);
/*  714 */       if (value != null) {
/*  715 */         if (isDiagnosticsEnabled()) {
/*  716 */           logDiagnostic("[ENV] Found system property [" + value + "] for " + property);
/*      */         }
/*  718 */         return value;
/*      */       } 
/*      */       
/*  721 */       if (isDiagnosticsEnabled()) {
/*  722 */         logDiagnostic("[ENV] No system property found for property " + property);
/*      */       }
/*  724 */     } catch (SecurityException e) {
/*  725 */       if (isDiagnosticsEnabled()) {
/*  726 */         logDiagnostic("[ENV] Security prevented reading system property " + property);
/*      */       }
/*      */     } 
/*      */     
/*  730 */     if (isDiagnosticsEnabled()) {
/*  731 */       logDiagnostic("[ENV] No configuration defined for item " + property);
/*      */     }
/*      */     
/*  734 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean getBooleanConfiguration(String key, boolean dflt) {
/*  742 */     String val = getConfigurationValue(key);
/*  743 */     if (val == null) {
/*  744 */       return dflt;
/*      */     }
/*  746 */     return Boolean.valueOf(val).booleanValue();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void initConfiguration() {
/*  757 */     this.allowFlawedContext = getBooleanConfiguration("org.apache.commons.logging.Log.allowFlawedContext", true);
/*  758 */     this.allowFlawedDiscovery = getBooleanConfiguration("org.apache.commons.logging.Log.allowFlawedDiscovery", true);
/*  759 */     this.allowFlawedHierarchy = getBooleanConfiguration("org.apache.commons.logging.Log.allowFlawedHierarchy", true);
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
/*      */   private Log discoverLogImplementation(String logCategory) throws LogConfigurationException {
/*  773 */     if (isDiagnosticsEnabled()) {
/*  774 */       logDiagnostic("Discovering a Log implementation...");
/*      */     }
/*      */     
/*  777 */     initConfiguration();
/*      */     
/*  779 */     Log result = null;
/*      */ 
/*      */     
/*  782 */     String specifiedLogClassName = findUserSpecifiedLogClassName();
/*      */     
/*  784 */     if (specifiedLogClassName != null) {
/*  785 */       if (isDiagnosticsEnabled()) {
/*  786 */         logDiagnostic("Attempting to load user-specified log class '" + specifiedLogClassName + "'...");
/*      */       }
/*      */ 
/*      */       
/*  790 */       result = createLogFromClass(specifiedLogClassName, logCategory, true);
/*      */ 
/*      */       
/*  793 */       if (result == null) {
/*  794 */         StringBuffer messageBuffer = new StringBuffer("User-specified log class '");
/*  795 */         messageBuffer.append(specifiedLogClassName);
/*  796 */         messageBuffer.append("' cannot be found or is not useable.");
/*      */ 
/*      */ 
/*      */         
/*  800 */         informUponSimilarName(messageBuffer, specifiedLogClassName, "org.apache.commons.logging.impl.Log4JLogger");
/*  801 */         informUponSimilarName(messageBuffer, specifiedLogClassName, "org.apache.commons.logging.impl.Jdk14Logger");
/*  802 */         informUponSimilarName(messageBuffer, specifiedLogClassName, "org.apache.commons.logging.impl.Jdk13LumberjackLogger");
/*  803 */         informUponSimilarName(messageBuffer, specifiedLogClassName, "org.apache.commons.logging.impl.SimpleLog");
/*  804 */         throw new LogConfigurationException(messageBuffer.toString());
/*      */       } 
/*      */       
/*  807 */       return result;
/*      */     } 
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
/*      */     
/*  838 */     if (isDiagnosticsEnabled()) {
/*  839 */       logDiagnostic("No user-specified Log implementation; performing discovery using the standard supported logging implementations...");
/*      */     }
/*      */ 
/*      */     
/*  843 */     for (int i = 0; i < classesToDiscover.length && result == null; i++) {
/*  844 */       result = createLogFromClass(classesToDiscover[i], logCategory, true);
/*      */     }
/*      */     
/*  847 */     if (result == null) {
/*  848 */       throw new LogConfigurationException("No suitable Log implementation");
/*      */     }
/*      */ 
/*      */     
/*  852 */     return result;
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
/*      */   private void informUponSimilarName(StringBuffer messageBuffer, String name, String candidate) {
/*  864 */     if (name.equals(candidate)) {
/*      */       return;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  873 */     if (name.regionMatches(true, 0, candidate, 0, PKG_LEN + 5)) {
/*  874 */       messageBuffer.append(" Did you mean '");
/*  875 */       messageBuffer.append(candidate);
/*  876 */       messageBuffer.append("'?");
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
/*      */   private String findUserSpecifiedLogClassName() {
/*  888 */     if (isDiagnosticsEnabled()) {
/*  889 */       logDiagnostic("Trying to get log class from attribute 'org.apache.commons.logging.Log'");
/*      */     }
/*  891 */     String specifiedClass = (String)getAttribute("org.apache.commons.logging.Log");
/*      */     
/*  893 */     if (specifiedClass == null) {
/*  894 */       if (isDiagnosticsEnabled()) {
/*  895 */         logDiagnostic("Trying to get log class from attribute 'org.apache.commons.logging.log'");
/*      */       }
/*      */       
/*  898 */       specifiedClass = (String)getAttribute("org.apache.commons.logging.log");
/*      */     } 
/*      */     
/*  901 */     if (specifiedClass == null) {
/*  902 */       if (isDiagnosticsEnabled()) {
/*  903 */         logDiagnostic("Trying to get log class from system property 'org.apache.commons.logging.Log'");
/*      */       }
/*      */       
/*      */       try {
/*  907 */         specifiedClass = getSystemProperty("org.apache.commons.logging.Log", (String)null);
/*  908 */       } catch (SecurityException e) {
/*  909 */         if (isDiagnosticsEnabled()) {
/*  910 */           logDiagnostic("No access allowed to system property 'org.apache.commons.logging.Log' - " + e.getMessage());
/*      */         }
/*      */       } 
/*      */     } 
/*      */ 
/*      */     
/*  916 */     if (specifiedClass == null) {
/*  917 */       if (isDiagnosticsEnabled()) {
/*  918 */         logDiagnostic("Trying to get log class from system property 'org.apache.commons.logging.log'");
/*      */       }
/*      */       
/*      */       try {
/*  922 */         specifiedClass = getSystemProperty("org.apache.commons.logging.log", (String)null);
/*  923 */       } catch (SecurityException e) {
/*  924 */         if (isDiagnosticsEnabled()) {
/*  925 */           logDiagnostic("No access allowed to system property 'org.apache.commons.logging.log' - " + e.getMessage());
/*      */         }
/*      */       } 
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  934 */     if (specifiedClass != null) {
/*  935 */       specifiedClass = specifiedClass.trim();
/*      */     }
/*      */     
/*  938 */     return specifiedClass;
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
/*      */   private Log createLogFromClass(String logAdapterClassName, String logCategory, boolean affectState) throws LogConfigurationException {
/*  960 */     if (isDiagnosticsEnabled()) {
/*  961 */       logDiagnostic("Attempting to instantiate '" + logAdapterClassName + "'");
/*      */     }
/*      */     
/*  964 */     Object[] params = { logCategory };
/*  965 */     Log logAdapter = null;
/*  966 */     Constructor constructor = null;
/*      */     
/*  968 */     Class logAdapterClass = null;
/*  969 */     ClassLoader currentCL = getBaseClassLoader();
/*      */ 
/*      */ 
/*      */     
/*      */     while (true) {
/*  974 */       logDiagnostic("Trying to load '" + logAdapterClassName + "' from classloader " + objectId(currentCL)); try {
/*      */         Class clazz;
/*  976 */         if (isDiagnosticsEnabled()) {
/*      */           URL url;
/*      */ 
/*      */ 
/*      */ 
/*      */           
/*  982 */           String resourceName = logAdapterClassName.replace('.', '/') + ".class";
/*  983 */           if (currentCL != null) {
/*  984 */             url = currentCL.getResource(resourceName);
/*      */           } else {
/*  986 */             url = ClassLoader.getSystemResource(resourceName + ".class");
/*      */           } 
/*      */           
/*  989 */           if (url == null) {
/*  990 */             logDiagnostic("Class '" + logAdapterClassName + "' [" + resourceName + "] cannot be found.");
/*      */           } else {
/*  992 */             logDiagnostic("Class '" + logAdapterClassName + "' was found at '" + url + "'");
/*      */           } 
/*      */         } 
/*      */ 
/*      */         
/*      */         try {
/*  998 */           clazz = Class.forName(logAdapterClassName, true, currentCL);
/*  999 */         } catch (ClassNotFoundException originalClassNotFoundException) {
/*      */ 
/*      */ 
/*      */           
/* 1003 */           String msg = originalClassNotFoundException.getMessage();
/* 1004 */           logDiagnostic("The log adapter '" + logAdapterClassName + "' is not available via classloader " + objectId(currentCL) + ": " + msg.trim());
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/*      */           try {
/* 1014 */             clazz = Class.forName(logAdapterClassName);
/* 1015 */           } catch (ClassNotFoundException secondaryClassNotFoundException) {
/*      */             
/* 1017 */             msg = secondaryClassNotFoundException.getMessage();
/* 1018 */             logDiagnostic("The log adapter '" + logAdapterClassName + "' is not available via the LogFactoryImpl class classloader: " + msg.trim());
/*      */             
/*      */             break;
/*      */           } 
/*      */         } 
/*      */         
/* 1024 */         constructor = clazz.getConstructor(this.logConstructorSignature);
/* 1025 */         Object o = constructor.newInstance(params);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 1031 */         if (o instanceof Log) {
/* 1032 */           logAdapterClass = clazz;
/* 1033 */           logAdapter = (Log)o;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/*      */           break;
/*      */         } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 1047 */         handleFlawedHierarchy(currentCL, clazz);
/* 1048 */       } catch (NoClassDefFoundError e) {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 1054 */         String msg = e.getMessage();
/* 1055 */         logDiagnostic("The log adapter '" + logAdapterClassName + "' is missing dependencies when loaded via classloader " + objectId(currentCL) + ": " + msg.trim());
/*      */ 
/*      */         
/*      */         break;
/* 1059 */       } catch (ExceptionInInitializerError e) {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 1066 */         String msg = e.getMessage();
/* 1067 */         logDiagnostic("The log adapter '" + logAdapterClassName + "' is unable to initialize itself when loaded via classloader " + objectId(currentCL) + ": " + msg.trim());
/*      */ 
/*      */         
/*      */         break;
/* 1071 */       } catch (LogConfigurationException e) {
/*      */ 
/*      */         
/* 1074 */         throw e;
/* 1075 */       } catch (Throwable t) {
/* 1076 */         handleThrowable(t);
/*      */ 
/*      */ 
/*      */         
/* 1080 */         handleFlawedDiscovery(logAdapterClassName, currentCL, t);
/*      */       } 
/*      */       
/* 1083 */       if (currentCL == null) {
/*      */         break;
/*      */       }
/*      */ 
/*      */ 
/*      */       
/* 1089 */       currentCL = getParentClassLoader(currentCL);
/*      */     } 
/*      */     
/* 1092 */     if (logAdapterClass != null && affectState) {
/*      */       
/* 1094 */       this.logClassName = logAdapterClassName;
/* 1095 */       this.logConstructor = constructor;
/*      */ 
/*      */       
/*      */       try {
/* 1099 */         this.logMethod = logAdapterClass.getMethod("setLogFactory", this.logMethodSignature);
/* 1100 */         logDiagnostic("Found method setLogFactory(LogFactory) in '" + logAdapterClassName + "'");
/* 1101 */       } catch (Throwable t) {
/* 1102 */         handleThrowable(t);
/* 1103 */         this.logMethod = null;
/* 1104 */         logDiagnostic("[INFO] '" + logAdapterClassName + "' from classloader " + objectId(currentCL) + " does not declare optional method " + "setLogFactory(LogFactory)");
/*      */       } 
/*      */ 
/*      */       
/* 1108 */       logDiagnostic("Log adapter '" + logAdapterClassName + "' from classloader " + objectId(logAdapterClass.getClassLoader()) + " has been selected for use.");
/*      */     } 
/*      */ 
/*      */     
/* 1112 */     return logAdapter;
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
/*      */   private ClassLoader getBaseClassLoader() throws LogConfigurationException {
/* 1134 */     ClassLoader thisClassLoader = getClassLoader(LogFactoryImpl.class);
/*      */     
/* 1136 */     if (!this.useTCCL) {
/* 1137 */       return thisClassLoader;
/*      */     }
/*      */     
/* 1140 */     ClassLoader contextClassLoader = getContextClassLoaderInternal();
/*      */     
/* 1142 */     ClassLoader baseClassLoader = getLowestClassLoader(contextClassLoader, thisClassLoader);
/*      */ 
/*      */     
/* 1145 */     if (baseClassLoader == null) {
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 1150 */       if (this.allowFlawedContext) {
/* 1151 */         if (isDiagnosticsEnabled()) {
/* 1152 */           logDiagnostic("[WARNING] the context classloader is not part of a parent-child relationship with the classloader that loaded LogFactoryImpl.");
/*      */         }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 1159 */         return contextClassLoader;
/*      */       } 
/*      */       
/* 1162 */       throw new LogConfigurationException("Bad classloader hierarchy; LogFactoryImpl was loaded via a classloader that is not related to the current context classloader.");
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1168 */     if (baseClassLoader != contextClassLoader)
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 1174 */       if (this.allowFlawedContext) {
/* 1175 */         if (isDiagnosticsEnabled()) {
/* 1176 */           logDiagnostic("Warning: the context classloader is an ancestor of the classloader that loaded LogFactoryImpl; it should be the same or a descendant. The application using commons-logging should ensure the context classloader is used correctly.");
/*      */         
/*      */         }
/*      */       
/*      */       }
/*      */       else {
/*      */ 
/*      */         
/* 1184 */         throw new LogConfigurationException("Bad classloader hierarchy; LogFactoryImpl was loaded via a classloader that is not related to the current context classloader.");
/*      */       } 
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1191 */     return baseClassLoader;
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
/*      */   private ClassLoader getLowestClassLoader(ClassLoader c1, ClassLoader c2) {
/* 1207 */     if (c1 == null) {
/* 1208 */       return c2;
/*      */     }
/*      */     
/* 1211 */     if (c2 == null) {
/* 1212 */       return c1;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1218 */     ClassLoader current = c1;
/* 1219 */     while (current != null) {
/* 1220 */       if (current == c2) {
/* 1221 */         return c1;
/*      */       }
/*      */       
/* 1224 */       current = getParentClassLoader(current);
/*      */     } 
/*      */ 
/*      */     
/* 1228 */     current = c2;
/* 1229 */     while (current != null) {
/* 1230 */       if (current == c1) {
/* 1231 */         return c2;
/*      */       }
/*      */       
/* 1234 */       current = getParentClassLoader(current);
/*      */     } 
/*      */     
/* 1237 */     return null;
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
/*      */   private void handleFlawedDiscovery(String logAdapterClassName, ClassLoader classLoader, Throwable discoveryFlaw) {
/* 1259 */     if (isDiagnosticsEnabled()) {
/* 1260 */       logDiagnostic("Could not instantiate Log '" + logAdapterClassName + "' -- " + discoveryFlaw.getClass().getName() + ": " + discoveryFlaw.getLocalizedMessage());
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 1265 */       if (discoveryFlaw instanceof InvocationTargetException) {
/*      */ 
/*      */ 
/*      */         
/* 1269 */         InvocationTargetException ite = (InvocationTargetException)discoveryFlaw;
/* 1270 */         Throwable cause = ite.getTargetException();
/* 1271 */         if (cause != null) {
/* 1272 */           logDiagnostic("... InvocationTargetException: " + cause.getClass().getName() + ": " + cause.getLocalizedMessage());
/*      */ 
/*      */ 
/*      */           
/* 1276 */           if (cause instanceof ExceptionInInitializerError) {
/* 1277 */             ExceptionInInitializerError eiie = (ExceptionInInitializerError)cause;
/* 1278 */             Throwable cause2 = eiie.getException();
/* 1279 */             if (cause2 != null) {
/* 1280 */               StringWriter sw = new StringWriter();
/* 1281 */               cause2.printStackTrace(new PrintWriter(sw, true));
/* 1282 */               logDiagnostic("... ExceptionInInitializerError: " + sw.toString());
/*      */             } 
/*      */           } 
/*      */         } 
/*      */       } 
/*      */     } 
/*      */     
/* 1289 */     if (!this.allowFlawedDiscovery) {
/* 1290 */       throw new LogConfigurationException(discoveryFlaw);
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
/*      */   private void handleFlawedHierarchy(ClassLoader badClassLoader, Class badClass) throws LogConfigurationException {
/* 1323 */     boolean implementsLog = false;
/* 1324 */     String logInterfaceName = Log.class.getName();
/* 1325 */     Class[] interfaces = badClass.getInterfaces();
/* 1326 */     for (int i = 0; i < interfaces.length; i++) {
/* 1327 */       if (logInterfaceName.equals(interfaces[i].getName())) {
/* 1328 */         implementsLog = true;
/*      */         
/*      */         break;
/*      */       } 
/*      */     } 
/* 1333 */     if (implementsLog) {
/*      */ 
/*      */       
/* 1336 */       if (isDiagnosticsEnabled()) {
/*      */         try {
/* 1338 */           ClassLoader logInterfaceClassLoader = getClassLoader(Log.class);
/* 1339 */           logDiagnostic("Class '" + badClass.getName() + "' was found in classloader " + objectId(badClassLoader) + ". It is bound to a Log interface which is not" + " the one loaded from classloader " + objectId(logInterfaceClassLoader));
/*      */         
/*      */         }
/* 1342 */         catch (Throwable t) {
/* 1343 */           handleThrowable(t);
/* 1344 */           logDiagnostic("Error while trying to output diagnostics about bad class '" + badClass + "'");
/*      */         } 
/*      */       }
/*      */       
/* 1348 */       if (!this.allowFlawedHierarchy) {
/* 1349 */         StringBuffer msg = new StringBuffer();
/* 1350 */         msg.append("Terminating logging for this context ");
/* 1351 */         msg.append("due to bad log hierarchy. ");
/* 1352 */         msg.append("You have more than one version of '");
/* 1353 */         msg.append(Log.class.getName());
/* 1354 */         msg.append("' visible.");
/* 1355 */         if (isDiagnosticsEnabled()) {
/* 1356 */           logDiagnostic(msg.toString());
/*      */         }
/* 1358 */         throw new LogConfigurationException(msg.toString());
/*      */       } 
/*      */       
/* 1361 */       if (isDiagnosticsEnabled()) {
/* 1362 */         StringBuffer msg = new StringBuffer();
/* 1363 */         msg.append("Warning: bad log hierarchy. ");
/* 1364 */         msg.append("You have more than one version of '");
/* 1365 */         msg.append(Log.class.getName());
/* 1366 */         msg.append("' visible.");
/* 1367 */         logDiagnostic(msg.toString());
/*      */       } 
/*      */     } else {
/*      */       
/* 1371 */       if (!this.allowFlawedDiscovery) {
/* 1372 */         StringBuffer msg = new StringBuffer();
/* 1373 */         msg.append("Terminating logging for this context. ");
/* 1374 */         msg.append("Log class '");
/* 1375 */         msg.append(badClass.getName());
/* 1376 */         msg.append("' does not implement the Log interface.");
/* 1377 */         if (isDiagnosticsEnabled()) {
/* 1378 */           logDiagnostic(msg.toString());
/*      */         }
/*      */         
/* 1381 */         throw new LogConfigurationException(msg.toString());
/*      */       } 
/*      */       
/* 1384 */       if (isDiagnosticsEnabled()) {
/* 1385 */         StringBuffer msg = new StringBuffer();
/* 1386 */         msg.append("[WARNING] Log class '");
/* 1387 */         msg.append(badClass.getName());
/* 1388 */         msg.append("' does not implement the Log interface.");
/* 1389 */         logDiagnostic(msg.toString());
/*      */       } 
/*      */     } 
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\logging\impl\LogFactoryImpl.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */