/*     */ package com.sun.mail.util.logging;
/*     */ 
/*     */ import java.io.ObjectStreamException;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedAction;
/*     */ import java.util.Comparator;
/*     */ import java.util.Enumeration;
/*     */ import java.util.Locale;
/*     */ import java.util.Properties;
/*     */ import java.util.logging.ErrorManager;
/*     */ import java.util.logging.Filter;
/*     */ import java.util.logging.Formatter;
/*     */ import java.util.logging.LogManager;
/*     */ import javax.mail.Authenticator;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ final class LogManagerProperties
/*     */   extends Properties
/*     */ {
/*     */   private static final long serialVersionUID = -2239983349056806252L;
/*  81 */   private static final LogManager LOG_MANAGER = LogManager.getLogManager();
/*     */   
/*     */   private final String prefix;
/*     */   
/*     */   static final boolean $assertionsDisabled;
/*     */ 
/*     */   
/*     */   static LogManager getLogManager() {
/*  89 */     return LOG_MANAGER;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static String toLanguageTag(Locale locale) {
/* 100 */     String l = locale.getLanguage();
/* 101 */     String c = locale.getCountry();
/* 102 */     String v = locale.getVariant();
/* 103 */     char[] b = new char[l.length() + c.length() + v.length() + 2];
/* 104 */     int count = l.length();
/* 105 */     l.getChars(0, count, b, 0);
/* 106 */     if (c.length() != 0 || (l.length() != 0 && v.length() != 0)) {
/* 107 */       b[count] = '-';
/* 108 */       count++;
/* 109 */       c.getChars(0, c.length(), b, count);
/* 110 */       count += c.length();
/*     */     } 
/*     */     
/* 113 */     if (v.length() != 0 && (l.length() != 0 || c.length() != 0)) {
/* 114 */       b[count] = '-';
/* 115 */       count++;
/* 116 */       v.getChars(0, v.length(), b, count);
/* 117 */       count += v.length();
/*     */     } 
/* 119 */     return String.valueOf(b, 0, count);
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
/*     */   static Filter newFilter(String name) throws Exception {
/* 139 */     return (Filter)newObjectFrom(name, Filter.class);
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
/*     */   static Formatter newFormatter(String name) throws Exception {
/* 159 */     return (Formatter)newObjectFrom(name, Formatter.class);
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
/*     */   static Comparator newComparator(String name) throws Exception {
/* 180 */     return (Comparator)newObjectFrom(name, Comparator.class);
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
/*     */   static ErrorManager newErrorManager(String name) throws Exception {
/* 200 */     return (ErrorManager)newObjectFrom(name, ErrorManager.class);
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
/*     */   static Authenticator newAuthenticator(String name) throws Exception {
/* 220 */     return (Authenticator)newObjectFrom(name, Authenticator.class);
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
/*     */   private static Object newObjectFrom(String name, Class type) throws Exception {
/*     */     try {
/* 242 */       Class clazz = findClass(name);
/*     */ 
/*     */       
/* 245 */       if (type.isAssignableFrom(clazz)) {
/*     */         try {
/* 247 */           return clazz.getConstructor((Class[])null).newInstance((Object[])null);
/* 248 */         } catch (InvocationTargetException ITE) {
/* 249 */           throw paramOrError(ITE);
/*     */         } 
/*     */       }
/* 252 */       throw new ClassCastException(clazz.getName() + " cannot be cast to " + type.getName());
/*     */     
/*     */     }
/* 255 */     catch (NoClassDefFoundError NCDFE) {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 261 */       throw new ClassNotFoundException(NCDFE.toString(), NCDFE);
/* 262 */     } catch (ExceptionInInitializerError EIIE) {
/*     */ 
/*     */       
/* 265 */       if (EIIE.getCause() instanceof Error) {
/* 266 */         throw EIIE;
/*     */       }
/*     */ 
/*     */       
/* 270 */       throw new InvocationTargetException(EIIE);
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
/*     */   private static Exception paramOrError(InvocationTargetException ite) {
/* 284 */     Throwable cause = ite.getCause();
/* 285 */     if (cause != null && (
/* 286 */       cause instanceof VirtualMachineError || cause instanceof ThreadDeath))
/*     */     {
/* 288 */       throw (Error)cause;
/*     */     }
/*     */     
/* 291 */     return ite;
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
/*     */   private static Class findClass(String name) throws ClassNotFoundException {
/*     */     Class clazz;
/* 306 */     ClassLoader[] loaders = getClassLoaders();
/* 307 */     assert loaders.length == 2 : loaders.length;
/*     */     
/* 309 */     if (loaders[0] != null) {
/*     */       try {
/* 311 */         clazz = Class.forName(name, false, loaders[0]);
/* 312 */       } catch (ClassNotFoundException tryContext) {
/* 313 */         clazz = tryLoad(name, loaders[1]);
/*     */       } 
/*     */     } else {
/* 316 */       clazz = tryLoad(name, loaders[1]);
/*     */     } 
/* 318 */     return clazz;
/*     */   }
/*     */   
/*     */   private static Class tryLoad(String name, ClassLoader l) throws ClassNotFoundException {
/* 322 */     if (l != null) {
/* 323 */       return Class.forName(name, false, l);
/*     */     }
/* 325 */     return Class.forName(name);
/*     */   }
/*     */ 
/*     */   
/*     */   private static ClassLoader[] getClassLoaders() {
/* 330 */     return AccessController.<ClassLoader[]>doPrivileged(new PrivilegedAction()
/*     */         {
/*     */           public Object run() {
/* 333 */             ClassLoader[] loaders = new ClassLoader[2];
/*     */             try {
/* 335 */               loaders[0] = ClassLoader.getSystemClassLoader();
/* 336 */             } catch (SecurityException ignore) {
/* 337 */               loaders[0] = null;
/*     */             } 
/*     */             
/*     */             try {
/* 341 */               loaders[1] = Thread.currentThread().getContextClassLoader();
/* 342 */             } catch (SecurityException ignore) {
/* 343 */               loaders[1] = null;
/*     */             } 
/* 345 */             return loaders;
/*     */           }
/*     */         });
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
/*     */   LogManagerProperties(Properties parent, String prefix) {
/* 362 */     super(parent);
/* 363 */     parent.isEmpty();
/* 364 */     if (prefix == null) {
/* 365 */       throw new NullPointerException();
/*     */     }
/* 367 */     this.prefix = prefix;
/*     */ 
/*     */     
/* 370 */     isEmpty();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized Object clone() {
/* 381 */     return exportCopy(this.defaults);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized String getProperty(String key) {
/* 391 */     String value = this.defaults.getProperty(key);
/* 392 */     if (value == null) {
/* 393 */       LogManager manager = getLogManager();
/* 394 */       if (key.length() > 0) {
/* 395 */         value = manager.getProperty(this.prefix + '.' + key);
/*     */       }
/*     */       
/* 398 */       if (value == null) {
/* 399 */         value = manager.getProperty(key);
/*     */       }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 409 */       if (value != null) {
/* 410 */         super.put(key, value);
/*     */       } else {
/* 412 */         Object v = super.get(key);
/* 413 */         value = (v instanceof String) ? (String)v : null;
/*     */       } 
/*     */     } 
/* 416 */     return value;
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
/*     */   public String getProperty(String key, String def) {
/* 428 */     String value = getProperty(key);
/* 429 */     return (value == null) ? def : value;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object get(Object key) {
/* 440 */     if (key instanceof String) {
/* 441 */       return getProperty((String)key);
/*     */     }
/* 443 */     return super.get(key);
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
/*     */   public synchronized Object put(Object key, Object value) {
/* 455 */     Object def = preWrite(key);
/* 456 */     Object man = super.put(key, value);
/* 457 */     return (man == null) ? def : man;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object setProperty(String key, String value) {
/* 467 */     return put(key, value);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean containsKey(Object key) {
/* 478 */     if (key instanceof String) {
/* 479 */       return (getProperty((String)key) != null);
/*     */     }
/* 481 */     return super.containsKey(key);
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
/*     */   public synchronized Object remove(Object key) {
/* 493 */     Object def = preWrite(key);
/* 494 */     Object man = super.remove(key);
/* 495 */     return (man == null) ? def : man;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Enumeration propertyNames() {
/*     */     assert false;
/* 505 */     return super.propertyNames();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean equals(Object o) {
/* 515 */     if (o == null) {
/* 516 */       return false;
/*     */     }
/* 518 */     if (o == this) {
/* 519 */       return true;
/*     */     }
/* 521 */     if (!(o instanceof Properties)) {
/* 522 */       return false;
/*     */     }
/* 524 */     assert false : this.prefix;
/* 525 */     return super.equals(o);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 533 */     assert false : this.prefix.hashCode();
/* 534 */     return super.hashCode();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private Object preWrite(Object key) {
/*     */     Object value;
/* 546 */     assert Thread.holdsLock(this);
/*     */     
/* 548 */     if (key instanceof String && !super.containsKey(key)) {
/* 549 */       value = getProperty((String)key);
/*     */     } else {
/* 551 */       value = null;
/*     */     } 
/* 553 */     return value;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private Properties exportCopy(Properties parent) {
/* 563 */     Thread.holdsLock(this);
/* 564 */     Properties child = new Properties(parent);
/* 565 */     child.putAll(this);
/* 566 */     return child;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private synchronized Object writeReplace() throws ObjectStreamException {
/*     */     assert false;
/* 578 */     return exportCopy((Properties)this.defaults.clone());
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\mai\\util\logging\LogManagerProperties.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */