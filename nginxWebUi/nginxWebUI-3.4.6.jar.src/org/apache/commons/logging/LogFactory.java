/*      */ package org.apache.commons.logging;
/*      */ 
/*      */ import java.io.BufferedReader;
/*      */ import java.io.FileOutputStream;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.io.InputStreamReader;
/*      */ import java.io.PrintStream;
/*      */ import java.io.UnsupportedEncodingException;
/*      */ import java.net.URL;
/*      */ import java.net.URLConnection;
/*      */ import java.security.AccessController;
/*      */ import java.security.PrivilegedAction;
/*      */ import java.util.Enumeration;
/*      */ import java.util.Hashtable;
/*      */ import java.util.Properties;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public abstract class LogFactory
/*      */ {
/*      */   public static final String PRIORITY_KEY = "priority";
/*      */   public static final String TCCL_KEY = "use_tccl";
/*      */   public static final String FACTORY_PROPERTY = "org.apache.commons.logging.LogFactory";
/*      */   public static final String FACTORY_DEFAULT = "org.apache.commons.logging.impl.LogFactoryImpl";
/*      */   public static final String FACTORY_PROPERTIES = "commons-logging.properties";
/*      */   protected static final String SERVICE_ID = "META-INF/services/org.apache.commons.logging.LogFactory";
/*      */   public static final String DIAGNOSTICS_DEST_PROPERTY = "org.apache.commons.logging.diagnostics.dest";
/*      */   
/*      */   static {
/*      */     String str;
/*      */   }
/*      */   
/*  136 */   private static PrintStream diagnosticsStream = null;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static final String diagnosticPrefix;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final String HASHTABLE_IMPLEMENTATION_PROPERTY = "org.apache.commons.logging.LogFactory.HashtableImpl";
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static final String WEAK_HASHTABLE_CLASSNAME = "org.apache.commons.logging.impl.WeakHashtable";
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static final ClassLoader thisClassLoader;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  282 */   protected static Hashtable factories = null;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  299 */   protected static volatile LogFactory nullClassLoaderFactory = null;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static final Hashtable createFactoryStore() {
/*      */     String str;
/*  317 */     Hashtable result = null;
/*      */     
/*      */     try {
/*  320 */       str = getSystemProperty("org.apache.commons.logging.LogFactory.HashtableImpl", null);
/*  321 */     } catch (SecurityException ex) {
/*      */ 
/*      */       
/*  324 */       str = null;
/*      */     } 
/*      */     
/*  327 */     if (str == null) {
/*  328 */       str = "org.apache.commons.logging.impl.WeakHashtable";
/*      */     }
/*      */     try {
/*  331 */       Class implementationClass = Class.forName(str);
/*  332 */       result = (Hashtable)implementationClass.newInstance();
/*  333 */     } catch (Throwable t) {
/*  334 */       handleThrowable(t);
/*      */ 
/*      */       
/*  337 */       if (!"org.apache.commons.logging.impl.WeakHashtable".equals(str))
/*      */       {
/*  339 */         if (isDiagnosticsEnabled()) {
/*      */           
/*  341 */           logDiagnostic("[ERROR] LogFactory: Load of custom hashtable failed");
/*      */         }
/*      */         else {
/*      */           
/*  345 */           System.err.println("[ERROR] LogFactory: Load of custom hashtable failed");
/*      */         } 
/*      */       }
/*      */     } 
/*  349 */     if (result == null) {
/*  350 */       result = new Hashtable();
/*      */     }
/*  352 */     return result;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static String trim(String src) {
/*  359 */     if (src == null) {
/*  360 */       return null;
/*      */     }
/*  362 */     return src.trim();
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
/*      */   protected static void handleThrowable(Throwable t) {
/*  378 */     if (t instanceof ThreadDeath) {
/*  379 */       throw (ThreadDeath)t;
/*      */     }
/*  381 */     if (t instanceof VirtualMachineError) {
/*  382 */       throw (VirtualMachineError)t;
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
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static LogFactory getFactory() throws LogConfigurationException {
/*  419 */     ClassLoader contextClassLoader = getContextClassLoaderInternal();
/*      */     
/*  421 */     if (contextClassLoader == null)
/*      */     {
/*      */ 
/*      */       
/*  425 */       if (isDiagnosticsEnabled()) {
/*  426 */         logDiagnostic("Context classloader is null.");
/*      */       }
/*      */     }
/*      */ 
/*      */     
/*  431 */     LogFactory factory = getCachedFactory(contextClassLoader);
/*  432 */     if (factory != null) {
/*  433 */       return factory;
/*      */     }
/*      */     
/*  436 */     if (isDiagnosticsEnabled()) {
/*  437 */       logDiagnostic("[LOOKUP] LogFactory implementation requested for the first time for context classloader " + objectId(contextClassLoader));
/*      */ 
/*      */       
/*  440 */       logHierarchy("[LOOKUP] ", contextClassLoader);
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
/*  453 */     Properties props = getConfigurationFile(contextClassLoader, "commons-logging.properties");
/*      */ 
/*      */ 
/*      */     
/*  457 */     ClassLoader baseClassLoader = contextClassLoader;
/*  458 */     if (props != null) {
/*  459 */       String useTCCLStr = props.getProperty("use_tccl");
/*  460 */       if (useTCCLStr != null)
/*      */       {
/*      */         
/*  463 */         if (!Boolean.valueOf(useTCCLStr).booleanValue())
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/*  471 */           baseClassLoader = thisClassLoader;
/*      */         }
/*      */       }
/*      */     } 
/*      */ 
/*      */ 
/*      */     
/*  478 */     if (isDiagnosticsEnabled()) {
/*  479 */       logDiagnostic("[LOOKUP] Looking for system property [org.apache.commons.logging.LogFactory] to define the LogFactory subclass to use...");
/*      */     }
/*      */ 
/*      */     
/*      */     try {
/*  484 */       String factoryClass = getSystemProperty("org.apache.commons.logging.LogFactory", null);
/*  485 */       if (factoryClass != null) {
/*  486 */         if (isDiagnosticsEnabled()) {
/*  487 */           logDiagnostic("[LOOKUP] Creating an instance of LogFactory class '" + factoryClass + "' as specified by system property " + "org.apache.commons.logging.LogFactory");
/*      */         }
/*      */         
/*  490 */         factory = newFactory(factoryClass, baseClassLoader, contextClassLoader);
/*      */       }
/*  492 */       else if (isDiagnosticsEnabled()) {
/*  493 */         logDiagnostic("[LOOKUP] No system property [org.apache.commons.logging.LogFactory] defined.");
/*      */       }
/*      */     
/*  496 */     } catch (SecurityException e) {
/*  497 */       if (isDiagnosticsEnabled()) {
/*  498 */         logDiagnostic("[LOOKUP] A security exception occurred while trying to create an instance of the custom factory class: [" + trim(e.getMessage()) + "]. Trying alternative implementations...");
/*      */       
/*      */       }
/*      */     
/*      */     }
/*  503 */     catch (RuntimeException e) {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  509 */       if (isDiagnosticsEnabled()) {
/*  510 */         logDiagnostic("[LOOKUP] An exception occurred while trying to create an instance of the custom factory class: [" + trim(e.getMessage()) + "] as specified by a system property.");
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*  515 */       throw e;
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  524 */     if (factory == null) {
/*  525 */       if (isDiagnosticsEnabled()) {
/*  526 */         logDiagnostic("[LOOKUP] Looking for a resource file of name [META-INF/services/org.apache.commons.logging.LogFactory] to define the LogFactory subclass to use...");
/*      */       }
/*      */       
/*      */       try {
/*  530 */         InputStream is = getResourceAsStream(contextClassLoader, "META-INF/services/org.apache.commons.logging.LogFactory");
/*      */         
/*  532 */         if (is != null) {
/*      */           BufferedReader bufferedReader;
/*      */ 
/*      */           
/*      */           try {
/*  537 */             bufferedReader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
/*  538 */           } catch (UnsupportedEncodingException e) {
/*  539 */             bufferedReader = new BufferedReader(new InputStreamReader(is));
/*      */           } 
/*      */           
/*  542 */           String factoryClassName = bufferedReader.readLine();
/*  543 */           bufferedReader.close();
/*      */           
/*  545 */           if (factoryClassName != null && !"".equals(factoryClassName)) {
/*  546 */             if (isDiagnosticsEnabled()) {
/*  547 */               logDiagnostic("[LOOKUP]  Creating an instance of LogFactory class " + factoryClassName + " as specified by file '" + "META-INF/services/org.apache.commons.logging.LogFactory" + "' which was present in the path of the context classloader.");
/*      */             }
/*      */ 
/*      */ 
/*      */             
/*  552 */             factory = newFactory(factoryClassName, baseClassLoader, contextClassLoader);
/*      */           }
/*      */         
/*      */         }
/*  556 */         else if (isDiagnosticsEnabled()) {
/*  557 */           logDiagnostic("[LOOKUP] No resource file with name 'META-INF/services/org.apache.commons.logging.LogFactory' found.");
/*      */         }
/*      */       
/*  560 */       } catch (Exception ex) {
/*      */ 
/*      */ 
/*      */         
/*  564 */         if (isDiagnosticsEnabled()) {
/*  565 */           logDiagnostic("[LOOKUP] A security exception occurred while trying to create an instance of the custom factory class: [" + trim(ex.getMessage()) + "]. Trying alternative implementations...");
/*      */         }
/*      */       } 
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  577 */     if (factory == null) {
/*  578 */       if (props != null) {
/*  579 */         if (isDiagnosticsEnabled()) {
/*  580 */           logDiagnostic("[LOOKUP] Looking in properties file for entry with key 'org.apache.commons.logging.LogFactory' to define the LogFactory subclass to use...");
/*      */         }
/*      */ 
/*      */         
/*  584 */         String factoryClass = props.getProperty("org.apache.commons.logging.LogFactory");
/*  585 */         if (factoryClass != null) {
/*  586 */           if (isDiagnosticsEnabled()) {
/*  587 */             logDiagnostic("[LOOKUP] Properties file specifies LogFactory subclass '" + factoryClass + "'");
/*      */           }
/*      */           
/*  590 */           factory = newFactory(factoryClass, baseClassLoader, contextClassLoader);
/*      */ 
/*      */         
/*      */         }
/*  594 */         else if (isDiagnosticsEnabled()) {
/*  595 */           logDiagnostic("[LOOKUP] Properties file has no entry specifying LogFactory subclass.");
/*      */         }
/*      */       
/*      */       }
/*  599 */       else if (isDiagnosticsEnabled()) {
/*  600 */         logDiagnostic("[LOOKUP] No properties file available to determine LogFactory subclass from..");
/*      */       } 
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  607 */     if (factory == null) {
/*  608 */       if (isDiagnosticsEnabled()) {
/*  609 */         logDiagnostic("[LOOKUP] Loading the default LogFactory implementation 'org.apache.commons.logging.impl.LogFactoryImpl' via the same classloader that loaded this LogFactory class (ie not looking in the context classloader).");
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  624 */       factory = newFactory("org.apache.commons.logging.impl.LogFactoryImpl", thisClassLoader, contextClassLoader);
/*      */     } 
/*      */     
/*  627 */     if (factory != null) {
/*      */ 
/*      */ 
/*      */       
/*  631 */       cacheFactory(contextClassLoader, factory);
/*      */       
/*  633 */       if (props != null) {
/*  634 */         Enumeration names = props.propertyNames();
/*  635 */         while (names.hasMoreElements()) {
/*  636 */           String name = (String)names.nextElement();
/*  637 */           String value = props.getProperty(name);
/*  638 */           factory.setAttribute(name, value);
/*      */         } 
/*      */       } 
/*      */     } 
/*      */     
/*  643 */     return factory;
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
/*      */   public static Log getLog(Class clazz) throws LogConfigurationException {
/*  655 */     return getFactory().getInstance(clazz);
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
/*      */   public static Log getLog(String name) throws LogConfigurationException {
/*  669 */     return getFactory().getInstance(name);
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
/*      */   public static void release(ClassLoader classLoader) {
/*  681 */     if (isDiagnosticsEnabled()) {
/*  682 */       logDiagnostic("Releasing factory for classloader " + objectId(classLoader));
/*      */     }
/*      */     
/*  685 */     Hashtable factories = LogFactory.factories;
/*  686 */     synchronized (factories) {
/*  687 */       if (classLoader == null) {
/*  688 */         if (nullClassLoaderFactory != null) {
/*  689 */           nullClassLoaderFactory.release();
/*  690 */           nullClassLoaderFactory = null;
/*      */         } 
/*      */       } else {
/*  693 */         LogFactory factory = (LogFactory)factories.get(classLoader);
/*  694 */         if (factory != null) {
/*  695 */           factory.release();
/*  696 */           factories.remove(classLoader);
/*      */         } 
/*      */       } 
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
/*      */   public static void releaseAll() {
/*  711 */     if (isDiagnosticsEnabled()) {
/*  712 */       logDiagnostic("Releasing factory for all classloaders.");
/*      */     }
/*      */     
/*  715 */     Hashtable factories = LogFactory.factories;
/*  716 */     synchronized (factories) {
/*  717 */       Enumeration elements = factories.elements();
/*  718 */       while (elements.hasMoreElements()) {
/*  719 */         LogFactory element = elements.nextElement();
/*  720 */         element.release();
/*      */       } 
/*  722 */       factories.clear();
/*      */       
/*  724 */       if (nullClassLoaderFactory != null) {
/*  725 */         nullClassLoaderFactory.release();
/*  726 */         nullClassLoaderFactory = null;
/*      */       } 
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
/*      */   
/*      */   protected static ClassLoader getClassLoader(Class clazz) {
/*      */     try {
/*  762 */       return clazz.getClassLoader();
/*  763 */     } catch (SecurityException ex) {
/*  764 */       if (isDiagnosticsEnabled()) {
/*  765 */         logDiagnostic("Unable to get classloader for class '" + clazz + "' due to security restrictions - " + ex.getMessage());
/*      */       }
/*      */       
/*  768 */       throw ex;
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
/*      */   protected static ClassLoader getContextClassLoader() throws LogConfigurationException {
/*  790 */     return directGetContextClassLoader();
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
/*      */   private static ClassLoader getContextClassLoaderInternal() throws LogConfigurationException {
/*  808 */     return AccessController.<ClassLoader>doPrivileged(new PrivilegedAction()
/*      */         {
/*      */           public Object run() {
/*  811 */             return LogFactory.directGetContextClassLoader();
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected static ClassLoader directGetContextClassLoader() throws LogConfigurationException {
/*  837 */     ClassLoader classLoader = null;
/*      */     
/*      */     try {
/*  840 */       classLoader = Thread.currentThread().getContextClassLoader();
/*  841 */     } catch (SecurityException ex) {}
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  855 */     return classLoader;
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
/*      */   private static LogFactory getCachedFactory(ClassLoader contextClassLoader) {
/*  873 */     if (contextClassLoader == null)
/*      */     {
/*      */ 
/*      */ 
/*      */       
/*  878 */       return nullClassLoaderFactory;
/*      */     }
/*  880 */     return (LogFactory)factories.get(contextClassLoader);
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
/*      */   private static void cacheFactory(ClassLoader classLoader, LogFactory factory) {
/*  897 */     if (factory != null) {
/*  898 */       if (classLoader == null) {
/*  899 */         nullClassLoaderFactory = factory;
/*      */       } else {
/*  901 */         factories.put(classLoader, factory);
/*      */       } 
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected static LogFactory newFactory(String factoryClass, ClassLoader classLoader, ClassLoader contextClassLoader) throws LogConfigurationException {
/*  957 */     Object result = AccessController.doPrivileged(new PrivilegedAction(factoryClass, classLoader) { private final String val$factoryClass;
/*      */           
/*      */           public Object run() {
/*  960 */             return LogFactory.createFactory(this.val$factoryClass, this.val$classLoader);
/*      */           }
/*      */           private final ClassLoader val$classLoader; }
/*      */       );
/*  964 */     if (result instanceof LogConfigurationException) {
/*  965 */       LogConfigurationException ex = (LogConfigurationException)result;
/*  966 */       if (isDiagnosticsEnabled()) {
/*  967 */         logDiagnostic("An error occurred while loading the factory class:" + ex.getMessage());
/*      */       }
/*  969 */       throw ex;
/*      */     } 
/*  971 */     if (isDiagnosticsEnabled()) {
/*  972 */       logDiagnostic("Created object " + objectId(result) + " to manage classloader " + objectId(contextClassLoader));
/*      */     }
/*      */     
/*  975 */     return (LogFactory)result;
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
/*      */   protected static LogFactory newFactory(String factoryClass, ClassLoader classLoader) {
/*  995 */     return newFactory(factoryClass, classLoader, null);
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
/*      */   protected static Object createFactory(String factoryClass, ClassLoader classLoader) {
/* 1012 */     Class logFactoryClass = null;
/*      */     try {
/* 1014 */       if (classLoader != null) {
/*      */         
/*      */         try {
/*      */ 
/*      */ 
/*      */           
/* 1020 */           logFactoryClass = classLoader.loadClass(factoryClass);
/* 1021 */           if (LogFactory.class.isAssignableFrom(logFactoryClass)) {
/* 1022 */             if (isDiagnosticsEnabled()) {
/* 1023 */               logDiagnostic("Loaded class " + logFactoryClass.getName() + " from classloader " + objectId(classLoader));
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */             
/*      */             }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/*      */           }
/* 1038 */           else if (isDiagnosticsEnabled()) {
/* 1039 */             logDiagnostic("Factory class " + logFactoryClass.getName() + " loaded from classloader " + objectId(logFactoryClass.getClassLoader()) + " does not extend '" + LogFactory.class.getName() + "' as loaded by this classloader.");
/*      */ 
/*      */ 
/*      */             
/* 1043 */             logHierarchy("[BAD CL TREE] ", classLoader);
/*      */           } 
/*      */ 
/*      */           
/* 1047 */           return logFactoryClass.newInstance();
/*      */         }
/* 1049 */         catch (ClassNotFoundException ex) {
/* 1050 */           if (classLoader == thisClassLoader)
/*      */           {
/* 1052 */             if (isDiagnosticsEnabled()) {
/* 1053 */               logDiagnostic("Unable to locate any class called '" + factoryClass + "' via classloader " + objectId(classLoader));
/*      */             }
/*      */             
/* 1056 */             throw ex;
/*      */           }
/*      */         
/* 1059 */         } catch (NoClassDefFoundError e) {
/* 1060 */           if (classLoader == thisClassLoader)
/*      */           {
/* 1062 */             if (isDiagnosticsEnabled()) {
/* 1063 */               logDiagnostic("Class '" + factoryClass + "' cannot be loaded" + " via classloader " + objectId(classLoader) + " - it depends on some other class that cannot be found.");
/*      */             }
/*      */ 
/*      */             
/* 1067 */             throw e;
/*      */           }
/*      */         
/* 1070 */         } catch (ClassCastException e) {
/* 1071 */           if (classLoader == thisClassLoader) {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */             
/* 1077 */             boolean implementsLogFactory = implementsLogFactory(logFactoryClass);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */             
/* 1084 */             StringBuffer msg = new StringBuffer();
/* 1085 */             msg.append("The application has specified that a custom LogFactory implementation ");
/* 1086 */             msg.append("should be used but Class '");
/* 1087 */             msg.append(factoryClass);
/* 1088 */             msg.append("' cannot be converted to '");
/* 1089 */             msg.append(LogFactory.class.getName());
/* 1090 */             msg.append("'. ");
/* 1091 */             if (implementsLogFactory) {
/* 1092 */               msg.append("The conflict is caused by the presence of multiple LogFactory classes ");
/* 1093 */               msg.append("in incompatible classloaders. ");
/* 1094 */               msg.append("Background can be found in http://commons.apache.org/logging/tech.html. ");
/* 1095 */               msg.append("If you have not explicitly specified a custom LogFactory then it is likely ");
/* 1096 */               msg.append("that the container has set one without your knowledge. ");
/* 1097 */               msg.append("In this case, consider using the commons-logging-adapters.jar file or ");
/* 1098 */               msg.append("specifying the standard LogFactory from the command line. ");
/*      */             } else {
/* 1100 */               msg.append("Please check the custom implementation. ");
/*      */             } 
/* 1102 */             msg.append("Help can be found @http://commons.apache.org/logging/troubleshooting.html.");
/*      */             
/* 1104 */             if (isDiagnosticsEnabled()) {
/* 1105 */               logDiagnostic(msg.toString());
/*      */             }
/*      */             
/* 1108 */             throw new ClassCastException(msg.toString());
/*      */           } 
/*      */         } 
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 1142 */       if (isDiagnosticsEnabled()) {
/* 1143 */         logDiagnostic("Unable to load factory class via classloader " + objectId(classLoader) + " - trying the classloader associated with this LogFactory.");
/*      */       }
/*      */       
/* 1146 */       logFactoryClass = Class.forName(factoryClass);
/* 1147 */       return logFactoryClass.newInstance();
/* 1148 */     } catch (Exception e) {
/*      */       
/* 1150 */       if (isDiagnosticsEnabled()) {
/* 1151 */         logDiagnostic("Unable to create LogFactory instance.");
/*      */       }
/* 1153 */       if (logFactoryClass != null && !LogFactory.class.isAssignableFrom(logFactoryClass)) {
/* 1154 */         return new LogConfigurationException("The chosen LogFactory implementation does not extend LogFactory. Please check your configuration.", e);
/*      */       }
/*      */ 
/*      */       
/* 1158 */       return new LogConfigurationException(e);
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
/*      */   private static boolean implementsLogFactory(Class logFactoryClass) {
/* 1175 */     boolean implementsLogFactory = false;
/* 1176 */     if (logFactoryClass != null) {
/*      */       try {
/* 1178 */         ClassLoader logFactoryClassLoader = logFactoryClass.getClassLoader();
/* 1179 */         if (logFactoryClassLoader == null) {
/* 1180 */           logDiagnostic("[CUSTOM LOG FACTORY] was loaded by the boot classloader");
/*      */         } else {
/* 1182 */           logHierarchy("[CUSTOM LOG FACTORY] ", logFactoryClassLoader);
/* 1183 */           Class factoryFromCustomLoader = Class.forName("org.apache.commons.logging.LogFactory", false, logFactoryClassLoader);
/*      */           
/* 1185 */           implementsLogFactory = factoryFromCustomLoader.isAssignableFrom(logFactoryClass);
/* 1186 */           if (implementsLogFactory) {
/* 1187 */             logDiagnostic("[CUSTOM LOG FACTORY] " + logFactoryClass.getName() + " implements LogFactory but was loaded by an incompatible classloader.");
/*      */           } else {
/*      */             
/* 1190 */             logDiagnostic("[CUSTOM LOG FACTORY] " + logFactoryClass.getName() + " does not implement LogFactory.");
/*      */           }
/*      */         
/*      */         } 
/* 1194 */       } catch (SecurityException e) {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 1200 */         logDiagnostic("[CUSTOM LOG FACTORY] SecurityException thrown whilst trying to determine whether the compatibility was caused by a classloader conflict: " + e.getMessage());
/*      */       }
/* 1202 */       catch (LinkageError e) {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 1209 */         logDiagnostic("[CUSTOM LOG FACTORY] LinkageError thrown whilst trying to determine whether the compatibility was caused by a classloader conflict: " + e.getMessage());
/*      */       }
/* 1211 */       catch (ClassNotFoundException e) {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 1219 */         logDiagnostic("[CUSTOM LOG FACTORY] LogFactory class cannot be loaded by classloader which loaded the custom LogFactory implementation. Is the custom factory in the right classloader?");
/*      */       } 
/*      */     }
/*      */     
/* 1223 */     return implementsLogFactory;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static InputStream getResourceAsStream(ClassLoader loader, String name) {
/* 1233 */     return AccessController.<InputStream>doPrivileged(new PrivilegedAction(loader, name) { private final ClassLoader val$loader; private final String val$name;
/*      */           
/*      */           public Object run() {
/* 1236 */             if (this.val$loader != null) {
/* 1237 */               return this.val$loader.getResourceAsStream(this.val$name);
/*      */             }
/* 1239 */             return ClassLoader.getSystemResourceAsStream(this.val$name);
/*      */           } }
/*      */       );
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
/*      */   private static Enumeration getResources(ClassLoader loader, String name) {
/* 1259 */     PrivilegedAction action = new PrivilegedAction(loader, name) { private final ClassLoader val$loader; private final String val$name;
/*      */         
/*      */         public Object run() {
/*      */           try {
/* 1263 */             if (this.val$loader != null) {
/* 1264 */               return this.val$loader.getResources(this.val$name);
/*      */             }
/* 1266 */             return ClassLoader.getSystemResources(this.val$name);
/*      */           }
/* 1268 */           catch (IOException e) {
/* 1269 */             if (LogFactory.isDiagnosticsEnabled()) {
/* 1270 */               LogFactory.logDiagnostic("Exception while trying to find configuration file " + this.val$name + ":" + e.getMessage());
/*      */             }
/*      */             
/* 1273 */             return null;
/* 1274 */           } catch (NoSuchMethodError e) {
/*      */ 
/*      */ 
/*      */             
/* 1278 */             return null;
/*      */           } 
/*      */         } }
/*      */       ;
/* 1282 */     Object result = AccessController.doPrivileged(action);
/* 1283 */     return (Enumeration)result;
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
/*      */   private static Properties getProperties(URL url) {
/* 1295 */     PrivilegedAction action = new PrivilegedAction(url) { private final URL val$url;
/*      */         
/*      */         public Object run() {
/* 1298 */           InputStream stream = null;
/*      */ 
/*      */ 
/*      */           
/*      */           try {
/* 1303 */             URLConnection connection = this.val$url.openConnection();
/* 1304 */             connection.setUseCaches(false);
/* 1305 */             stream = connection.getInputStream();
/* 1306 */             if (stream != null) {
/* 1307 */               Properties props = new Properties();
/* 1308 */               props.load(stream);
/* 1309 */               stream.close();
/* 1310 */               stream = null;
/* 1311 */               return props;
/*      */             } 
/* 1313 */           } catch (IOException e) {
/* 1314 */             if (LogFactory.isDiagnosticsEnabled()) {
/* 1315 */               LogFactory.logDiagnostic("Unable to read URL " + this.val$url);
/*      */             }
/*      */           } finally {
/* 1318 */             if (stream != null) {
/*      */               try {
/* 1320 */                 stream.close();
/* 1321 */               } catch (IOException e) {
/*      */                 
/* 1323 */                 if (LogFactory.isDiagnosticsEnabled()) {
/* 1324 */                   LogFactory.logDiagnostic("Unable to close stream for URL " + this.val$url);
/*      */                 }
/*      */               } 
/*      */             }
/*      */           } 
/*      */           
/* 1330 */           return null;
/*      */         } }
/*      */       ;
/* 1333 */     return AccessController.<Properties>doPrivileged(action);
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
/*      */   private static final Properties getConfigurationFile(ClassLoader classLoader, String fileName) {
/* 1356 */     Properties props = null;
/* 1357 */     double priority = 0.0D;
/* 1358 */     URL propsUrl = null;
/*      */     try {
/* 1360 */       Enumeration urls = getResources(classLoader, fileName);
/*      */       
/* 1362 */       if (urls == null) {
/* 1363 */         return null;
/*      */       }
/*      */       
/* 1366 */       while (urls.hasMoreElements()) {
/* 1367 */         URL url = urls.nextElement();
/*      */         
/* 1369 */         Properties newProps = getProperties(url);
/* 1370 */         if (newProps != null) {
/* 1371 */           if (props == null) {
/* 1372 */             propsUrl = url;
/* 1373 */             props = newProps;
/* 1374 */             String priorityStr = props.getProperty("priority");
/* 1375 */             priority = 0.0D;
/* 1376 */             if (priorityStr != null) {
/* 1377 */               priority = Double.parseDouble(priorityStr);
/*      */             }
/*      */             
/* 1380 */             if (isDiagnosticsEnabled()) {
/* 1381 */               logDiagnostic("[LOOKUP] Properties file found at '" + url + "'" + " with priority " + priority);
/*      */             }
/*      */             continue;
/*      */           } 
/* 1385 */           String newPriorityStr = newProps.getProperty("priority");
/* 1386 */           double newPriority = 0.0D;
/* 1387 */           if (newPriorityStr != null) {
/* 1388 */             newPriority = Double.parseDouble(newPriorityStr);
/*      */           }
/*      */           
/* 1391 */           if (newPriority > priority) {
/* 1392 */             if (isDiagnosticsEnabled()) {
/* 1393 */               logDiagnostic("[LOOKUP] Properties file at '" + url + "'" + " with priority " + newPriority + " overrides file at '" + propsUrl + "'" + " with priority " + priority);
/*      */             }
/*      */ 
/*      */ 
/*      */ 
/*      */             
/* 1399 */             propsUrl = url;
/* 1400 */             props = newProps;
/* 1401 */             priority = newPriority; continue;
/*      */           } 
/* 1403 */           if (isDiagnosticsEnabled()) {
/* 1404 */             logDiagnostic("[LOOKUP] Properties file at '" + url + "'" + " with priority " + newPriority + " does not override file at '" + propsUrl + "'" + " with priority " + priority);
/*      */           
/*      */           }
/*      */         
/*      */         }
/*      */ 
/*      */       
/*      */       }
/*      */     
/*      */     }
/* 1414 */     catch (SecurityException e) {
/* 1415 */       if (isDiagnosticsEnabled()) {
/* 1416 */         logDiagnostic("SecurityException thrown while trying to find/read config files.");
/*      */       }
/*      */     } 
/*      */     
/* 1420 */     if (isDiagnosticsEnabled()) {
/* 1421 */       if (props == null) {
/* 1422 */         logDiagnostic("[LOOKUP] No properties file of name '" + fileName + "' found.");
/*      */       } else {
/* 1424 */         logDiagnostic("[LOOKUP] Properties file of name '" + fileName + "' found at '" + propsUrl + '"');
/*      */       } 
/*      */     }
/*      */     
/* 1428 */     return props;
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
/* 1442 */     return AccessController.<String>doPrivileged(new PrivilegedAction(key, def) { private final String val$key; private final String val$def;
/*      */           
/*      */           public Object run() {
/* 1445 */             return System.getProperty(this.val$key, this.val$def);
/*      */           } }
/*      */       );
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static PrintStream initDiagnostics() {
/*      */     String dest;
/*      */     try {
/* 1459 */       dest = getSystemProperty("org.apache.commons.logging.diagnostics.dest", null);
/* 1460 */       if (dest == null) {
/* 1461 */         return null;
/*      */       }
/* 1463 */     } catch (SecurityException ex) {
/*      */ 
/*      */       
/* 1466 */       return null;
/*      */     } 
/*      */     
/* 1469 */     if (dest.equals("STDOUT"))
/* 1470 */       return System.out; 
/* 1471 */     if (dest.equals("STDERR")) {
/* 1472 */       return System.err;
/*      */     }
/*      */     
/*      */     try {
/* 1476 */       FileOutputStream fos = new FileOutputStream(dest, true);
/* 1477 */       return new PrintStream(fos);
/* 1478 */     } catch (IOException ex) {
/*      */       
/* 1480 */       return null;
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
/*      */   protected static boolean isDiagnosticsEnabled() {
/* 1495 */     return (diagnosticsStream != null);
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
/*      */   private static final void logDiagnostic(String msg) {
/* 1517 */     if (diagnosticsStream != null) {
/* 1518 */       diagnosticsStream.print(diagnosticPrefix);
/* 1519 */       diagnosticsStream.println(msg);
/* 1520 */       diagnosticsStream.flush();
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected static final void logRawDiagnostic(String msg) {
/* 1531 */     if (diagnosticsStream != null) {
/* 1532 */       diagnosticsStream.println(msg);
/* 1533 */       diagnosticsStream.flush();
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
/*      */   private static void logClassLoaderEnvironment(Class clazz) {
/*      */     ClassLoader classLoader;
/* 1555 */     if (!isDiagnosticsEnabled()) {
/*      */       return;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     try {
/* 1563 */       logDiagnostic("[ENV] Extension directories (java.ext.dir): " + System.getProperty("java.ext.dir"));
/* 1564 */       logDiagnostic("[ENV] Application classpath (java.class.path): " + System.getProperty("java.class.path"));
/* 1565 */     } catch (SecurityException ex) {
/* 1566 */       logDiagnostic("[ENV] Security setting prevent interrogation of system classpaths.");
/*      */     } 
/*      */     
/* 1569 */     String className = clazz.getName();
/*      */ 
/*      */     
/*      */     try {
/* 1573 */       classLoader = getClassLoader(clazz);
/* 1574 */     } catch (SecurityException ex) {
/*      */       
/* 1576 */       logDiagnostic("[ENV] Security forbids determining the classloader for " + className);
/*      */       
/*      */       return;
/*      */     } 
/* 1580 */     logDiagnostic("[ENV] Class " + className + " was loaded via classloader " + objectId(classLoader));
/* 1581 */     logHierarchy("[ENV] Ancestry of classloader which loaded " + className + " is ", classLoader);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static void logHierarchy(String prefix, ClassLoader classLoader) {
/*      */     ClassLoader systemClassLoader;
/* 1592 */     if (!isDiagnosticsEnabled()) {
/*      */       return;
/*      */     }
/*      */     
/* 1596 */     if (classLoader != null) {
/* 1597 */       String classLoaderString = classLoader.toString();
/* 1598 */       logDiagnostic(prefix + objectId(classLoader) + " == '" + classLoaderString + "'");
/*      */     } 
/*      */     
/*      */     try {
/* 1602 */       systemClassLoader = ClassLoader.getSystemClassLoader();
/* 1603 */     } catch (SecurityException ex) {
/* 1604 */       logDiagnostic(prefix + "Security forbids determining the system classloader.");
/*      */       return;
/*      */     } 
/* 1607 */     if (classLoader != null) {
/* 1608 */       StringBuffer buf = new StringBuffer(prefix + "ClassLoader tree:");
/*      */       while (true) {
/* 1610 */         buf.append(objectId(classLoader));
/* 1611 */         if (classLoader == systemClassLoader) {
/* 1612 */           buf.append(" (SYSTEM) ");
/*      */         }
/*      */         
/*      */         try {
/* 1616 */           classLoader = classLoader.getParent();
/* 1617 */         } catch (SecurityException ex) {
/* 1618 */           buf.append(" --> SECRET");
/*      */           
/*      */           break;
/*      */         } 
/* 1622 */         buf.append(" --> ");
/* 1623 */         if (classLoader == null) {
/* 1624 */           buf.append("BOOT");
/*      */           break;
/*      */         } 
/*      */       } 
/* 1628 */       logDiagnostic(buf.toString());
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
/*      */   public static String objectId(Object o) {
/* 1645 */     if (o == null) {
/* 1646 */       return "null";
/*      */     }
/* 1648 */     return o.getClass().getName() + "@" + System.identityHashCode(o);
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
/*      */   static {
/* 1674 */     thisClassLoader = getClassLoader(LogFactory.class);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     try {
/* 1686 */       ClassLoader classLoader = thisClassLoader;
/* 1687 */       if (thisClassLoader == null) {
/* 1688 */         str = "BOOTLOADER";
/*      */       } else {
/* 1690 */         str = objectId(classLoader);
/*      */       } 
/* 1692 */     } catch (SecurityException e) {
/* 1693 */       str = "UNKNOWN";
/*      */     } 
/* 1695 */     diagnosticPrefix = "[LogFactory from " + str + "] ";
/* 1696 */     diagnosticsStream = initDiagnostics();
/* 1697 */     logClassLoaderEnvironment(LogFactory.class);
/* 1698 */     factories = createFactoryStore();
/* 1699 */     if (isDiagnosticsEnabled())
/* 1700 */       logDiagnostic("BOOTSTRAP COMPLETED"); 
/*      */   }
/*      */   
/*      */   public abstract Object getAttribute(String paramString);
/*      */   
/*      */   public abstract String[] getAttributeNames();
/*      */   
/*      */   public abstract Log getInstance(Class paramClass) throws LogConfigurationException;
/*      */   
/*      */   public abstract Log getInstance(String paramString) throws LogConfigurationException;
/*      */   
/*      */   public abstract void release();
/*      */   
/*      */   public abstract void removeAttribute(String paramString);
/*      */   
/*      */   public abstract void setAttribute(String paramString, Object paramObject);
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\logging\LogFactory.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */