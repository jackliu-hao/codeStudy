/*     */ package com.sun.jna.platform.win32;
/*     */ 
/*     */ import com.sun.jna.Memory;
/*     */ import com.sun.jna.Native;
/*     */ import com.sun.jna.Pointer;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class WinCryptUtil
/*     */ {
/*     */   public static class MANAGED_CRYPT_SIGN_MESSAGE_PARA
/*     */     extends WinCrypt.CRYPT_SIGN_MESSAGE_PARA
/*     */   {
/*     */     private WinCrypt.CERT_CONTEXT[] rgpMsgCerts;
/*     */     private WinCrypt.CRL_CONTEXT[] rgpMsgCrls;
/*     */     private WinCrypt.CRYPT_ATTRIBUTE[] rgAuthAttrs;
/*     */     private WinCrypt.CRYPT_ATTRIBUTE[] rgUnauthAttrs;
/*     */     
/*     */     public void setRgpMsgCert(WinCrypt.CERT_CONTEXT[] rgpMsgCerts) {
/*  44 */       this.rgpMsgCerts = rgpMsgCerts;
/*  45 */       if (rgpMsgCerts == null || rgpMsgCerts.length == 0) {
/*  46 */         this.rgpMsgCert = null;
/*  47 */         this.cMsgCert = 0;
/*     */       } else {
/*  49 */         this.cMsgCert = rgpMsgCerts.length;
/*  50 */         Memory mem = new Memory((Native.POINTER_SIZE * rgpMsgCerts.length));
/*  51 */         for (int i = 0; i < rgpMsgCerts.length; i++) {
/*  52 */           mem.setPointer((i * Native.POINTER_SIZE), rgpMsgCerts[i].getPointer());
/*     */         }
/*  54 */         this.rgpMsgCert = (Pointer)mem;
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/*     */     public WinCrypt.CERT_CONTEXT[] getRgpMsgCert() {
/*  60 */       return this.rgpMsgCerts;
/*     */     }
/*     */     
/*     */     public void setRgpMsgCrl(WinCrypt.CRL_CONTEXT[] rgpMsgCrls) {
/*  64 */       this.rgpMsgCrls = rgpMsgCrls;
/*  65 */       if (rgpMsgCrls == null || rgpMsgCrls.length == 0) {
/*  66 */         this.rgpMsgCert = null;
/*  67 */         this.cMsgCert = 0;
/*     */       } else {
/*  69 */         this.cMsgCert = rgpMsgCrls.length;
/*  70 */         Memory mem = new Memory((Native.POINTER_SIZE * rgpMsgCrls.length));
/*  71 */         for (int i = 0; i < rgpMsgCrls.length; i++) {
/*  72 */           mem.setPointer((i * Native.POINTER_SIZE), rgpMsgCrls[i].getPointer());
/*     */         }
/*  74 */         this.rgpMsgCert = (Pointer)mem;
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/*     */     public WinCrypt.CRL_CONTEXT[] getRgpMsgCrl() {
/*  80 */       return this.rgpMsgCrls;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public void setRgAuthAttr(WinCrypt.CRYPT_ATTRIBUTE[] rgAuthAttrs) {
/*  89 */       this.rgAuthAttrs = rgAuthAttrs;
/*  90 */       if (rgAuthAttrs == null || rgAuthAttrs.length == 0) {
/*  91 */         this.rgAuthAttr = null;
/*  92 */         this.cMsgCert = 0;
/*     */       } else {
/*  94 */         this.cMsgCert = this.rgpMsgCerts.length;
/*  95 */         this.rgAuthAttr = rgAuthAttrs[0].getPointer();
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/*     */     public WinCrypt.CRYPT_ATTRIBUTE[] getRgAuthAttr() {
/* 101 */       return this.rgAuthAttrs;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public void setRgUnauthAttr(WinCrypt.CRYPT_ATTRIBUTE[] rgUnauthAttrs) {
/* 110 */       this.rgUnauthAttrs = rgUnauthAttrs;
/* 111 */       if (rgUnauthAttrs == null || rgUnauthAttrs.length == 0) {
/* 112 */         this.rgUnauthAttr = null;
/* 113 */         this.cMsgCert = 0;
/*     */       } else {
/* 115 */         this.cMsgCert = this.rgpMsgCerts.length;
/* 116 */         this.rgUnauthAttr = rgUnauthAttrs[0].getPointer();
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/*     */     public WinCrypt.CRYPT_ATTRIBUTE[] getRgUnauthAttr() {
/* 122 */       return this.rgUnauthAttrs;
/*     */     }
/*     */ 
/*     */     
/*     */     public void write() {
/* 127 */       if (this.rgpMsgCerts != null) {
/* 128 */         for (WinCrypt.CERT_CONTEXT cc : this.rgpMsgCerts) {
/* 129 */           cc.write();
/*     */         }
/*     */       }
/* 132 */       if (this.rgpMsgCrls != null) {
/* 133 */         for (WinCrypt.CRL_CONTEXT cc : this.rgpMsgCrls) {
/* 134 */           cc.write();
/*     */         }
/*     */       }
/* 137 */       if (this.rgAuthAttrs != null) {
/* 138 */         for (WinCrypt.CRYPT_ATTRIBUTE cc : this.rgAuthAttrs) {
/* 139 */           cc.write();
/*     */         }
/*     */       }
/* 142 */       if (this.rgUnauthAttrs != null) {
/* 143 */         for (WinCrypt.CRYPT_ATTRIBUTE cc : this.rgUnauthAttrs) {
/* 144 */           cc.write();
/*     */         }
/*     */       }
/* 147 */       this.cbSize = size();
/* 148 */       super.write();
/*     */     }
/*     */ 
/*     */     
/*     */     public void read() {
/* 153 */       if (this.rgpMsgCerts != null) {
/* 154 */         for (WinCrypt.CERT_CONTEXT cc : this.rgpMsgCerts) {
/* 155 */           cc.read();
/*     */         }
/*     */       }
/* 158 */       if (this.rgpMsgCrls != null) {
/* 159 */         for (WinCrypt.CRL_CONTEXT cc : this.rgpMsgCrls) {
/* 160 */           cc.read();
/*     */         }
/*     */       }
/* 163 */       if (this.rgAuthAttrs != null) {
/* 164 */         for (WinCrypt.CRYPT_ATTRIBUTE cc : this.rgAuthAttrs) {
/* 165 */           cc.read();
/*     */         }
/*     */       }
/* 168 */       if (this.rgUnauthAttrs != null) {
/* 169 */         for (WinCrypt.CRYPT_ATTRIBUTE cc : this.rgUnauthAttrs) {
/* 170 */           cc.read();
/*     */         }
/*     */       }
/* 173 */       super.read();
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\jna\platform\win32\WinCryptUtil.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */