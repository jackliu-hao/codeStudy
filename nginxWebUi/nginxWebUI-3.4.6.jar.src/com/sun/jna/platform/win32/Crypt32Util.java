/*     */ package com.sun.jna.platform.win32;
/*     */ 
/*     */ import com.sun.jna.Memory;
/*     */ import com.sun.jna.Native;
/*     */ import com.sun.jna.Pointer;
/*     */ import com.sun.jna.ptr.PointerByReference;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class Crypt32Util
/*     */ {
/*     */   public static byte[] cryptProtectData(byte[] data) {
/*  47 */     return cryptProtectData(data, 0);
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
/*     */   public static byte[] cryptProtectData(byte[] data, int flags) {
/*  60 */     return cryptProtectData(data, null, flags, "", null);
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
/*     */   public static byte[] cryptProtectData(byte[] data, byte[] entropy, int flags, String description, WinCrypt.CRYPTPROTECT_PROMPTSTRUCT prompt) {
/*  80 */     WinCrypt.DATA_BLOB pDataIn = new WinCrypt.DATA_BLOB(data);
/*  81 */     WinCrypt.DATA_BLOB pDataProtected = new WinCrypt.DATA_BLOB();
/*  82 */     WinCrypt.DATA_BLOB pEntropy = (entropy == null) ? null : new WinCrypt.DATA_BLOB(entropy);
/*     */     try {
/*  84 */       if (!Crypt32.INSTANCE.CryptProtectData(pDataIn, description, pEntropy, null, prompt, flags, pDataProtected))
/*     */       {
/*  86 */         throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
/*     */       }
/*  88 */       return pDataProtected.getData();
/*     */     } finally {
/*  90 */       if (pDataProtected.pbData != null) {
/*  91 */         Kernel32Util.freeLocalMemory(pDataProtected.pbData);
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
/*     */   public static byte[] cryptUnprotectData(byte[] data) {
/* 104 */     return cryptUnprotectData(data, 0);
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
/*     */   public static byte[] cryptUnprotectData(byte[] data, int flags) {
/* 117 */     return cryptUnprotectData(data, null, flags, null);
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
/*     */   public static byte[] cryptUnprotectData(byte[] data, byte[] entropy, int flags, WinCrypt.CRYPTPROTECT_PROMPTSTRUCT prompt) {
/* 135 */     WinCrypt.DATA_BLOB pDataIn = new WinCrypt.DATA_BLOB(data);
/* 136 */     WinCrypt.DATA_BLOB pDataUnprotected = new WinCrypt.DATA_BLOB();
/* 137 */     WinCrypt.DATA_BLOB pEntropy = (entropy == null) ? null : new WinCrypt.DATA_BLOB(entropy);
/* 138 */     PointerByReference pDescription = new PointerByReference();
/* 139 */     Win32Exception err = null;
/* 140 */     byte[] unProtectedData = null;
/*     */     try {
/* 142 */       if (!Crypt32.INSTANCE.CryptUnprotectData(pDataIn, pDescription, pEntropy, null, prompt, flags, pDataUnprotected)) {
/*     */         
/* 144 */         err = new Win32Exception(Kernel32.INSTANCE.GetLastError());
/*     */       } else {
/* 146 */         unProtectedData = pDataUnprotected.getData();
/*     */       } 
/*     */     } finally {
/* 149 */       if (pDataUnprotected.pbData != null) {
/*     */         try {
/* 151 */           Kernel32Util.freeLocalMemory(pDataUnprotected.pbData);
/* 152 */         } catch (Win32Exception e) {
/* 153 */           if (err == null) {
/* 154 */             err = e;
/*     */           } else {
/* 156 */             err.addSuppressedReflected((Throwable)e);
/*     */           } 
/*     */         } 
/*     */       }
/*     */       
/* 161 */       if (pDescription.getValue() != null) {
/*     */         try {
/* 163 */           Kernel32Util.freeLocalMemory(pDescription.getValue());
/* 164 */         } catch (Win32Exception e) {
/* 165 */           if (err == null) {
/* 166 */             err = e;
/*     */           } else {
/* 168 */             err.addSuppressedReflected((Throwable)e);
/*     */           } 
/*     */         } 
/*     */       }
/*     */     } 
/*     */     
/* 174 */     if (err != null) {
/* 175 */       throw err;
/*     */     }
/*     */     
/* 178 */     return unProtectedData;
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
/*     */   public static String CertNameToStr(int dwCertEncodingType, int dwStrType, WinCrypt.DATA_BLOB pName) {
/* 196 */     int charToBytes = Boolean.getBoolean("w32.ascii") ? 1 : Native.WCHAR_SIZE;
/*     */ 
/*     */     
/* 199 */     int requiredSize = Crypt32.INSTANCE.CertNameToStr(dwCertEncodingType, pName, dwStrType, Pointer.NULL, 0);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 206 */     Memory mem = new Memory((requiredSize * charToBytes));
/*     */ 
/*     */     
/* 209 */     int resultBytes = Crypt32.INSTANCE.CertNameToStr(dwCertEncodingType, pName, dwStrType, (Pointer)mem, requiredSize);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 216 */     assert resultBytes == requiredSize;
/*     */     
/* 218 */     if (Boolean.getBoolean("w32.ascii")) {
/* 219 */       return mem.getString(0L);
/*     */     }
/* 221 */     return mem.getWideString(0L);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\jna\platform\win32\Crypt32Util.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */