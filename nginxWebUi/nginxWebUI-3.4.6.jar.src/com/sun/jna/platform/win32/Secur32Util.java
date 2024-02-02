/*     */ package com.sun.jna.platform.win32;
/*     */ 
/*     */ import com.sun.jna.Native;
/*     */ import com.sun.jna.ptr.IntByReference;
/*     */ import java.util.ArrayList;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class Secur32Util
/*     */ {
/*     */   public static class SecurityPackage
/*     */   {
/*     */     public String name;
/*     */     public String comment;
/*     */   }
/*     */   
/*     */   public static String getUserNameEx(int format) {
/*  61 */     char[] buffer = new char[128];
/*  62 */     IntByReference len = new IntByReference(buffer.length);
/*  63 */     boolean result = Secur32.INSTANCE.GetUserNameEx(format, buffer, len);
/*     */     
/*  65 */     if (!result) {
/*     */       
/*  67 */       int rc = Kernel32.INSTANCE.GetLastError();
/*     */       
/*  69 */       switch (rc) {
/*     */         case 234:
/*  71 */           buffer = new char[len.getValue() + 1];
/*     */           break;
/*     */         default:
/*  74 */           throw new Win32Exception(Native.getLastError());
/*     */       } 
/*     */       
/*  77 */       result = Secur32.INSTANCE.GetUserNameEx(format, buffer, len);
/*     */     } 
/*     */     
/*  80 */     if (!result) {
/*  81 */       throw new Win32Exception(Native.getLastError());
/*     */     }
/*     */     
/*  84 */     return Native.toString(buffer);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static SecurityPackage[] getSecurityPackages() {
/*  93 */     IntByReference pcPackages = new IntByReference();
/*  94 */     Sspi.PSecPkgInfo pPackageInfo = new Sspi.PSecPkgInfo();
/*  95 */     int rc = Secur32.INSTANCE.EnumerateSecurityPackages(pcPackages, pPackageInfo);
/*  96 */     if (0 != rc) {
/*  97 */       throw new Win32Exception(rc);
/*     */     }
/*  99 */     Sspi.SecPkgInfo.ByReference[] arrayOfByReference = pPackageInfo.toArray(pcPackages.getValue());
/* 100 */     ArrayList<SecurityPackage> packages = new ArrayList<SecurityPackage>(pcPackages.getValue());
/* 101 */     for (Sspi.SecPkgInfo packageInfo : arrayOfByReference) {
/* 102 */       SecurityPackage securityPackage = new SecurityPackage();
/* 103 */       securityPackage.name = packageInfo.Name.toString();
/* 104 */       securityPackage.comment = packageInfo.Comment.toString();
/* 105 */       packages.add(securityPackage);
/*     */     } 
/* 107 */     rc = Secur32.INSTANCE.FreeContextBuffer(pPackageInfo.pPkgInfo.getPointer());
/* 108 */     if (0 != rc) {
/* 109 */       throw new Win32Exception(rc);
/*     */     }
/* 111 */     return packages.<SecurityPackage>toArray(new SecurityPackage[0]);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\jna\platform\win32\Secur32Util.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */