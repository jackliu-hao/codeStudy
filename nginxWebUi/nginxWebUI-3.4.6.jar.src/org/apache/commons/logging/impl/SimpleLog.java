/*     */ package org.apache.commons.logging.impl;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.PrintWriter;
/*     */ import java.io.Serializable;
/*     */ import java.io.StringWriter;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.Method;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedAction;
/*     */ import java.text.DateFormat;
/*     */ import java.text.SimpleDateFormat;
/*     */ import java.util.Date;
/*     */ import java.util.Properties;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.apache.commons.logging.LogConfigurationException;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class SimpleLog
/*     */   implements Log, Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 136942970684951178L;
/*     */   protected static final String systemPrefix = "org.apache.commons.logging.simplelog.";
/*  82 */   protected static final Properties simpleLogProps = new Properties();
/*     */ 
/*     */ 
/*     */   
/*     */   protected static final String DEFAULT_DATE_TIME_FORMAT = "yyyy/MM/dd HH:mm:ss:SSS zzz";
/*     */ 
/*     */ 
/*     */   
/*     */   protected static volatile boolean showLogName = false;
/*     */ 
/*     */ 
/*     */   
/*     */   protected static volatile boolean showShortName = true;
/*     */ 
/*     */   
/*     */   protected static volatile boolean showDateTime = false;
/*     */ 
/*     */   
/* 100 */   protected static volatile String dateTimeFormat = "yyyy/MM/dd HH:mm:ss:SSS zzz";
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 110 */   protected static DateFormat dateFormatter = null;
/*     */ 
/*     */   
/*     */   public static final int LOG_LEVEL_TRACE = 1;
/*     */ 
/*     */   
/*     */   public static final int LOG_LEVEL_DEBUG = 2;
/*     */ 
/*     */   
/*     */   public static final int LOG_LEVEL_INFO = 3;
/*     */ 
/*     */   
/*     */   public static final int LOG_LEVEL_WARN = 4;
/*     */ 
/*     */   
/*     */   public static final int LOG_LEVEL_ERROR = 5;
/*     */ 
/*     */   
/*     */   public static final int LOG_LEVEL_FATAL = 6;
/*     */   
/*     */   public static final int LOG_LEVEL_ALL = 0;
/*     */   
/*     */   public static final int LOG_LEVEL_OFF = 7;
/*     */ 
/*     */   
/*     */   private static String getStringProperty(String name) {
/* 136 */     String prop = null;
/*     */     try {
/* 138 */       prop = System.getProperty(name);
/* 139 */     } catch (SecurityException e) {}
/*     */ 
/*     */     
/* 142 */     return (prop == null) ? simpleLogProps.getProperty(name) : prop;
/*     */   }
/*     */   
/*     */   private static String getStringProperty(String name, String dephault) {
/* 146 */     String prop = getStringProperty(name);
/* 147 */     return (prop == null) ? dephault : prop;
/*     */   }
/*     */   
/*     */   private static boolean getBooleanProperty(String name, boolean dephault) {
/* 151 */     String prop = getStringProperty(name);
/* 152 */     return (prop == null) ? dephault : "true".equalsIgnoreCase(prop);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static {
/* 160 */     InputStream in = getResourceAsStream("simplelog.properties");
/* 161 */     if (null != in) {
/*     */       try {
/* 163 */         simpleLogProps.load(in);
/* 164 */         in.close();
/* 165 */       } catch (IOException e) {}
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 170 */     showLogName = getBooleanProperty("org.apache.commons.logging.simplelog.showlogname", showLogName);
/* 171 */     showShortName = getBooleanProperty("org.apache.commons.logging.simplelog.showShortLogname", showShortName);
/* 172 */     showDateTime = getBooleanProperty("org.apache.commons.logging.simplelog.showdatetime", showDateTime);
/*     */     
/* 174 */     if (showDateTime) {
/* 175 */       dateTimeFormat = getStringProperty("org.apache.commons.logging.simplelog.dateTimeFormat", dateTimeFormat);
/*     */       
/*     */       try {
/* 178 */         dateFormatter = new SimpleDateFormat(dateTimeFormat);
/* 179 */       } catch (IllegalArgumentException e) {
/*     */         
/* 181 */         dateTimeFormat = "yyyy/MM/dd HH:mm:ss:SSS zzz";
/* 182 */         dateFormatter = new SimpleDateFormat(dateTimeFormat);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 190 */   protected volatile String logName = null;
/*     */   
/*     */   protected volatile int currentLogLevel;
/*     */   
/* 194 */   private volatile String shortLogName = null;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SimpleLog(String name) {
/* 204 */     this.logName = name;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 209 */     setLevel(3);
/*     */ 
/*     */     
/* 212 */     String lvl = getStringProperty("org.apache.commons.logging.simplelog.log." + this.logName);
/* 213 */     int i = String.valueOf(name).lastIndexOf(".");
/* 214 */     while (null == lvl && i > -1) {
/* 215 */       name = name.substring(0, i);
/* 216 */       lvl = getStringProperty("org.apache.commons.logging.simplelog.log." + name);
/* 217 */       i = String.valueOf(name).lastIndexOf(".");
/*     */     } 
/*     */     
/* 220 */     if (null == lvl) {
/* 221 */       lvl = getStringProperty("org.apache.commons.logging.simplelog.defaultlog");
/*     */     }
/*     */     
/* 224 */     if ("all".equalsIgnoreCase(lvl)) {
/* 225 */       setLevel(0);
/* 226 */     } else if ("trace".equalsIgnoreCase(lvl)) {
/* 227 */       setLevel(1);
/* 228 */     } else if ("debug".equalsIgnoreCase(lvl)) {
/* 229 */       setLevel(2);
/* 230 */     } else if ("info".equalsIgnoreCase(lvl)) {
/* 231 */       setLevel(3);
/* 232 */     } else if ("warn".equalsIgnoreCase(lvl)) {
/* 233 */       setLevel(4);
/* 234 */     } else if ("error".equalsIgnoreCase(lvl)) {
/* 235 */       setLevel(5);
/* 236 */     } else if ("fatal".equalsIgnoreCase(lvl)) {
/* 237 */       setLevel(6);
/* 238 */     } else if ("off".equalsIgnoreCase(lvl)) {
/* 239 */       setLevel(7);
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
/*     */   public void setLevel(int currentLogLevel) {
/* 251 */     this.currentLogLevel = currentLogLevel;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getLevel() {
/* 258 */     return this.currentLogLevel;
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
/*     */   protected void log(int type, Object message, Throwable t) {
/* 275 */     StringBuffer buf = new StringBuffer();
/*     */ 
/*     */     
/* 278 */     if (showDateTime) {
/* 279 */       String dateText; Date now = new Date();
/*     */       
/* 281 */       synchronized (dateFormatter) {
/* 282 */         dateText = dateFormatter.format(now);
/*     */       } 
/* 284 */       buf.append(dateText);
/* 285 */       buf.append(" ");
/*     */     } 
/*     */ 
/*     */     
/* 289 */     switch (type) { case 1:
/* 290 */         buf.append("[TRACE] "); break;
/* 291 */       case 2: buf.append("[DEBUG] "); break;
/* 292 */       case 3: buf.append("[INFO] "); break;
/* 293 */       case 4: buf.append("[WARN] "); break;
/* 294 */       case 5: buf.append("[ERROR] "); break;
/* 295 */       case 6: buf.append("[FATAL] ");
/*     */         break; }
/*     */ 
/*     */     
/* 299 */     if (showShortName) {
/* 300 */       if (this.shortLogName == null) {
/*     */         
/* 302 */         String slName = this.logName.substring(this.logName.lastIndexOf(".") + 1);
/* 303 */         this.shortLogName = slName.substring(slName.lastIndexOf("/") + 1);
/*     */       } 
/* 305 */       buf.append(String.valueOf(this.shortLogName)).append(" - ");
/* 306 */     } else if (showLogName) {
/* 307 */       buf.append(String.valueOf(this.logName)).append(" - ");
/*     */     } 
/*     */ 
/*     */     
/* 311 */     buf.append(String.valueOf(message));
/*     */ 
/*     */     
/* 314 */     if (t != null) {
/* 315 */       buf.append(" <");
/* 316 */       buf.append(t.toString());
/* 317 */       buf.append(">");
/*     */       
/* 319 */       StringWriter sw = new StringWriter(1024);
/* 320 */       PrintWriter pw = new PrintWriter(sw);
/* 321 */       t.printStackTrace(pw);
/* 322 */       pw.close();
/* 323 */       buf.append(sw.toString());
/*     */     } 
/*     */ 
/*     */     
/* 327 */     write(buf);
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
/*     */   protected void write(StringBuffer buffer) {
/* 339 */     System.err.println(buffer.toString());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected boolean isLevelEnabled(int logLevel) {
/* 350 */     return (logLevel >= this.currentLogLevel);
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
/*     */   public final void debug(Object message) {
/* 363 */     if (isLevelEnabled(2)) {
/* 364 */       log(2, message, null);
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
/*     */   public final void debug(Object message, Throwable t) {
/* 377 */     if (isLevelEnabled(2)) {
/* 378 */       log(2, message, t);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final void trace(Object message) {
/* 389 */     if (isLevelEnabled(1)) {
/* 390 */       log(1, message, null);
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
/*     */   public final void trace(Object message, Throwable t) {
/* 402 */     if (isLevelEnabled(1)) {
/* 403 */       log(1, message, t);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final void info(Object message) {
/* 414 */     if (isLevelEnabled(3)) {
/* 415 */       log(3, message, null);
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
/*     */   public final void info(Object message, Throwable t) {
/* 427 */     if (isLevelEnabled(3)) {
/* 428 */       log(3, message, t);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final void warn(Object message) {
/* 439 */     if (isLevelEnabled(4)) {
/* 440 */       log(4, message, null);
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
/*     */   public final void warn(Object message, Throwable t) {
/* 452 */     if (isLevelEnabled(4)) {
/* 453 */       log(4, message, t);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final void error(Object message) {
/* 464 */     if (isLevelEnabled(5)) {
/* 465 */       log(5, message, null);
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
/*     */   public final void error(Object message, Throwable t) {
/* 477 */     if (isLevelEnabled(5)) {
/* 478 */       log(5, message, t);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final void fatal(Object message) {
/* 489 */     if (isLevelEnabled(6)) {
/* 490 */       log(6, message, null);
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
/*     */   public final void fatal(Object message, Throwable t) {
/* 502 */     if (isLevelEnabled(6)) {
/* 503 */       log(6, message, t);
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
/*     */   public final boolean isDebugEnabled() {
/* 515 */     return isLevelEnabled(2);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final boolean isErrorEnabled() {
/* 526 */     return isLevelEnabled(5);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final boolean isFatalEnabled() {
/* 537 */     return isLevelEnabled(6);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final boolean isInfoEnabled() {
/* 548 */     return isLevelEnabled(3);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final boolean isTraceEnabled() {
/* 559 */     return isLevelEnabled(1);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final boolean isWarnEnabled() {
/* 570 */     return isLevelEnabled(4);
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
/*     */   private static ClassLoader getContextClassLoader() {
/* 584 */     ClassLoader classLoader = null;
/*     */ 
/*     */     
/*     */     try {
/* 588 */       Method method = Thread.class.getMethod("getContextClassLoader", (Class[])null);
/*     */ 
/*     */       
/*     */       try {
/* 592 */         classLoader = (ClassLoader)method.invoke(Thread.currentThread(), (Object[])null);
/* 593 */       } catch (IllegalAccessException e) {
/*     */       
/* 595 */       } catch (InvocationTargetException e) {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 612 */         if (!(e.getTargetException() instanceof SecurityException))
/*     */         {
/*     */ 
/*     */ 
/*     */           
/* 617 */           throw new LogConfigurationException("Unexpected InvocationTargetException", e.getTargetException());
/*     */         }
/*     */       }
/*     */     
/* 621 */     } catch (NoSuchMethodException e) {}
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 626 */     if (classLoader == null) {
/* 627 */       classLoader = SimpleLog.class.getClassLoader();
/*     */     }
/*     */ 
/*     */     
/* 631 */     return classLoader;
/*     */   }
/*     */   
/*     */   private static InputStream getResourceAsStream(String name) {
/* 635 */     return AccessController.<InputStream>doPrivileged(new PrivilegedAction(name) { private final String val$name;
/*     */           
/*     */           public Object run() {
/* 638 */             ClassLoader threadCL = SimpleLog.getContextClassLoader();
/*     */             
/* 640 */             if (threadCL != null) {
/* 641 */               return threadCL.getResourceAsStream(this.val$name);
/*     */             }
/* 643 */             return ClassLoader.getSystemResourceAsStream(this.val$name);
/*     */           } }
/*     */       );
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\logging\impl\SimpleLog.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */