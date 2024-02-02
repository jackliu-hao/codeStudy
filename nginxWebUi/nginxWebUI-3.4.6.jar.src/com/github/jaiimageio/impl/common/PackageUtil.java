/*     */ package com.github.jaiimageio.impl.common;
/*     */ 
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedAction;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class PackageUtil
/*     */ {
/*     */   private static boolean isCodecLibAvailable = false;
/*  60 */   private static String version = "1.0";
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  65 */   private static String vendor = "Sun Microsystems, Inc.";
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  70 */   private static String specTitle = "Java Advanced Imaging Image I/O Tools";
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static {
/*  83 */     isCodecLibAvailable = false;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     try {
/*  89 */       Class<?> thisClass = Class.forName("com.github.jaiimageio.impl.common.PackageUtil");
/*  90 */       Package thisPackage = thisClass.getPackage();
/*  91 */       if (thisPackage.getImplementationVersion() != null && thisPackage.getImplementationVendor() != null) {
/*  92 */         version = thisPackage.getImplementationVersion();
/*  93 */         vendor = thisPackage.getImplementationVendor();
/*  94 */         specTitle = thisPackage.getSpecificationTitle();
/*     */       } 
/*  96 */     } catch (ClassNotFoundException classNotFoundException) {}
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final boolean isCodecLibAvailable() {
/* 107 */     Boolean result = AccessController.<Boolean>doPrivileged(new PrivilegedAction<Boolean>() {
/*     */           public Object run() {
/* 109 */             String property = null;
/*     */             
/*     */             try {
/* 112 */               property = System.getProperty("com.github.jaiimageio.disableCodecLib");
/* 113 */             } catch (SecurityException securityException) {}
/*     */ 
/*     */             
/* 116 */             return (property != null && property
/* 117 */               .equalsIgnoreCase("true")) ? Boolean.TRUE : Boolean.FALSE;
/*     */           }
/*     */         });
/*     */     
/* 121 */     boolean isCodecLibDisabled = result.booleanValue();
/*     */     
/* 123 */     return (isCodecLibAvailable && !isCodecLibDisabled);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final String getVersion() {
/* 130 */     return version;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final String getVendor() {
/* 137 */     return vendor;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final String getSpecificationTitle() {
/* 144 */     return specTitle;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\github\jaiimageio\impl\common\PackageUtil.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */