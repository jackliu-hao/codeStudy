/*     */ package com.sun.mail.util;
/*     */ 
/*     */ import java.lang.reflect.Method;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedAction;
/*     */ import javax.mail.internet.MimePart;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class MimeUtil
/*     */ {
/*     */   private static final Method cleanContentType;
/*     */   
/*     */   static {
/*  59 */     Method meth = null;
/*     */     try {
/*  61 */       String cth = System.getProperty("mail.mime.contenttypehandler");
/*  62 */       if (cth != null) {
/*  63 */         ClassLoader cl = getContextClassLoader();
/*  64 */         Class clsHandler = null;
/*  65 */         if (cl != null) {
/*     */           try {
/*  67 */             clsHandler = Class.forName(cth, false, cl);
/*  68 */           } catch (ClassNotFoundException cex) {}
/*     */         }
/*  70 */         if (clsHandler == null)
/*  71 */           clsHandler = Class.forName(cth); 
/*  72 */         meth = clsHandler.getMethod("cleanContentType", new Class[] { MimePart.class, String.class });
/*     */       }
/*     */     
/*  75 */     } catch (ClassNotFoundException ex) {
/*     */     
/*  77 */     } catch (NoSuchMethodException ex) {
/*     */     
/*  79 */     } catch (RuntimeException ex) {
/*     */     
/*     */     } finally {
/*  82 */       cleanContentType = meth;
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
/*     */   public static String cleanContentType(MimePart mp, String contentType) {
/*  95 */     if (cleanContentType != null) {
/*     */       try {
/*  97 */         return (String)cleanContentType.invoke(null, new Object[] { mp, contentType });
/*     */       }
/*  99 */       catch (Exception ex) {
/* 100 */         return contentType;
/*     */       } 
/*     */     }
/* 103 */     return contentType;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static ClassLoader getContextClassLoader() {
/* 112 */     return AccessController.<ClassLoader>doPrivileged(new PrivilegedAction()
/*     */         {
/*     */           public Object run() {
/* 115 */             ClassLoader cl = null;
/*     */             try {
/* 117 */               cl = Thread.currentThread().getContextClassLoader();
/* 118 */             } catch (SecurityException ex) {}
/* 119 */             return cl;
/*     */           }
/*     */         });
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\mai\\util\MimeUtil.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */