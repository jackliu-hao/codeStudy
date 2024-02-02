/*     */ package javax.activation;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.net.URL;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedAction;
/*     */ import java.security.PrivilegedActionException;
/*     */ import java.security.PrivilegedExceptionAction;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Enumeration;
/*     */ import java.util.List;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ class SecuritySupport
/*     */ {
/*     */   public static ClassLoader getContextClassLoader() {
/*  45 */     return AccessController.<ClassLoader>doPrivileged(new PrivilegedAction()
/*     */         {
/*     */           public Object run() {
/*  48 */             ClassLoader cl = null;
/*     */             try {
/*  50 */               cl = Thread.currentThread().getContextClassLoader();
/*  51 */             } catch (SecurityException ex) {}
/*  52 */             return cl;
/*     */           }
/*     */         });
/*     */   }
/*     */ 
/*     */   
/*     */   public static InputStream getResourceAsStream(final Class c, final String name) throws IOException {
/*     */     try {
/*  60 */       return AccessController.<InputStream>doPrivileged(new PrivilegedExceptionAction() { private final Class val$c;
/*     */             
/*     */             public Object run() throws IOException {
/*  63 */               return c.getResourceAsStream(name);
/*     */             } private final String val$name; }
/*     */         );
/*  66 */     } catch (PrivilegedActionException e) {
/*  67 */       throw (IOException)e.getException();
/*     */     } 
/*     */   }
/*     */   
/*     */   public static URL[] getResources(final ClassLoader cl, final String name) {
/*  72 */     return AccessController.<URL[]>doPrivileged(new PrivilegedAction() { private final ClassLoader val$cl; private final String val$name;
/*     */           
/*     */           public Object run() {
/*  75 */             URL[] ret = null;
/*     */             
/*  77 */             try { List v = new ArrayList();
/*  78 */               Enumeration e = cl.getResources(name);
/*  79 */               while (e != null && e.hasMoreElements()) {
/*  80 */                 URL url = e.nextElement();
/*  81 */                 if (url != null)
/*  82 */                   v.add(url); 
/*     */               } 
/*  84 */               if (v.size() > 0) {
/*  85 */                 ret = new URL[v.size()];
/*  86 */                 ret = v.<URL>toArray(ret);
/*     */               }  }
/*  88 */             catch (IOException ioex) {  }
/*  89 */             catch (SecurityException ex) {}
/*  90 */             return ret;
/*     */           } }
/*     */       );
/*     */   }
/*     */   
/*     */   public static URL[] getSystemResources(final String name) {
/*  96 */     return AccessController.<URL[]>doPrivileged(new PrivilegedAction() { private final String val$name;
/*     */           
/*     */           public Object run() {
/*  99 */             URL[] ret = null;
/*     */             
/* 101 */             try { List v = new ArrayList();
/* 102 */               Enumeration e = ClassLoader.getSystemResources(name);
/* 103 */               while (e != null && e.hasMoreElements()) {
/* 104 */                 URL url = e.nextElement();
/* 105 */                 if (url != null)
/* 106 */                   v.add(url); 
/*     */               } 
/* 108 */               if (v.size() > 0) {
/* 109 */                 ret = new URL[v.size()];
/* 110 */                 ret = v.<URL>toArray(ret);
/*     */               }  }
/* 112 */             catch (IOException ioex) {  }
/* 113 */             catch (SecurityException ex) {}
/* 114 */             return ret;
/*     */           } }
/*     */       );
/*     */   }
/*     */   
/*     */   public static InputStream openStream(final URL url) throws IOException {
/*     */     try {
/* 121 */       return AccessController.<InputStream>doPrivileged(new PrivilegedExceptionAction() { private final URL val$url;
/*     */             
/*     */             public Object run() throws IOException {
/* 124 */               return url.openStream();
/*     */             } }
/*     */         );
/* 127 */     } catch (PrivilegedActionException e) {
/* 128 */       throw (IOException)e.getException();
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\javax\activation\SecuritySupport.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */