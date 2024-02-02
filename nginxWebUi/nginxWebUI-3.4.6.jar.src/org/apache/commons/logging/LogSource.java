/*     */ package org.apache.commons.logging;
/*     */ 
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.util.Hashtable;
/*     */ import org.apache.commons.logging.impl.NoOpLog;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class LogSource
/*     */ {
/*  59 */   protected static Hashtable logs = new Hashtable();
/*     */ 
/*     */   
/*     */   protected static boolean log4jIsAvailable = false;
/*     */ 
/*     */   
/*     */   protected static boolean jdk14IsAvailable = false;
/*     */ 
/*     */   
/*  68 */   protected static Constructor logImplctor = null;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static {
/*     */     try {
/*  76 */       log4jIsAvailable = (null != Class.forName("org.apache.log4j.Logger"));
/*  77 */     } catch (Throwable t) {
/*  78 */       log4jIsAvailable = false;
/*     */     } 
/*     */ 
/*     */     
/*     */     try {
/*  83 */       jdk14IsAvailable = (null != Class.forName("java.util.logging.Logger") && null != Class.forName("org.apache.commons.logging.impl.Jdk14Logger"));
/*     */     }
/*  85 */     catch (Throwable t) {
/*  86 */       jdk14IsAvailable = false;
/*     */     } 
/*     */ 
/*     */     
/*  90 */     String name = null;
/*     */     try {
/*  92 */       name = System.getProperty("org.apache.commons.logging.log");
/*  93 */       if (name == null) {
/*  94 */         name = System.getProperty("org.apache.commons.logging.Log");
/*     */       }
/*  96 */     } catch (Throwable t) {}
/*     */     
/*  98 */     if (name != null) {
/*     */       try {
/* 100 */         setLogImplementation(name);
/* 101 */       } catch (Throwable t) {
/*     */         try {
/* 103 */           setLogImplementation("org.apache.commons.logging.impl.NoOpLog");
/* 104 */         } catch (Throwable u) {}
/*     */       } 
/*     */     } else {
/*     */ 
/*     */       
/*     */       try {
/* 110 */         if (log4jIsAvailable) {
/* 111 */           setLogImplementation("org.apache.commons.logging.impl.Log4JLogger");
/* 112 */         } else if (jdk14IsAvailable) {
/* 113 */           setLogImplementation("org.apache.commons.logging.impl.Jdk14Logger");
/*     */         } else {
/* 115 */           setLogImplementation("org.apache.commons.logging.impl.NoOpLog");
/*     */         } 
/* 117 */       } catch (Throwable t) {
/*     */         try {
/* 119 */           setLogImplementation("org.apache.commons.logging.impl.NoOpLog");
/* 120 */         } catch (Throwable u) {}
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void setLogImplementation(String classname) throws LinkageError, NoSuchMethodException, SecurityException, ClassNotFoundException {
/*     */     try {
/* 145 */       Class logclass = Class.forName(classname);
/* 146 */       Class[] argtypes = new Class[1];
/* 147 */       argtypes[0] = "".getClass();
/* 148 */       logImplctor = logclass.getConstructor(argtypes);
/* 149 */     } catch (Throwable t) {
/* 150 */       logImplctor = null;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void setLogImplementation(Class logclass) throws LinkageError, ExceptionInInitializerError, NoSuchMethodException, SecurityException {
/* 161 */     Class[] argtypes = new Class[1];
/* 162 */     argtypes[0] = "".getClass();
/* 163 */     logImplctor = logclass.getConstructor(argtypes);
/*     */   }
/*     */ 
/*     */   
/*     */   public static Log getInstance(String name) {
/* 168 */     Log log = (Log)logs.get(name);
/* 169 */     if (null == log) {
/* 170 */       log = makeNewLogInstance(name);
/* 171 */       logs.put(name, log);
/*     */     } 
/* 173 */     return log;
/*     */   }
/*     */ 
/*     */   
/*     */   public static Log getInstance(Class clazz) {
/* 178 */     return getInstance(clazz.getName());
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
/*     */   public static Log makeNewLogInstance(String name) {
/*     */     NoOpLog noOpLog;
/*     */     try {
/* 201 */       Object[] args = { name };
/* 202 */       noOpLog = (NoOpLog)logImplctor.newInstance(args);
/* 203 */     } catch (Throwable t) {
/* 204 */       noOpLog = null;
/*     */     } 
/* 206 */     if (null == noOpLog) {
/* 207 */       noOpLog = new NoOpLog(name);
/*     */     }
/* 209 */     return (Log)noOpLog;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String[] getLogNames() {
/* 217 */     return (String[])logs.keySet().toArray((Object[])new String[logs.size()]);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\logging\LogSource.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */