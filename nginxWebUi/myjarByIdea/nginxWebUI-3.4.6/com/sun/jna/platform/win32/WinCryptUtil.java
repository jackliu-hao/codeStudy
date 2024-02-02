package com.sun.jna.platform.win32;

import com.sun.jna.Memory;
import com.sun.jna.Native;

public abstract class WinCryptUtil {
   public static class MANAGED_CRYPT_SIGN_MESSAGE_PARA extends WinCrypt.CRYPT_SIGN_MESSAGE_PARA {
      private WinCrypt.CERT_CONTEXT[] rgpMsgCerts;
      private WinCrypt.CRL_CONTEXT[] rgpMsgCrls;
      private WinCrypt.CRYPT_ATTRIBUTE[] rgAuthAttrs;
      private WinCrypt.CRYPT_ATTRIBUTE[] rgUnauthAttrs;

      public void setRgpMsgCert(WinCrypt.CERT_CONTEXT[] rgpMsgCerts) {
         this.rgpMsgCerts = rgpMsgCerts;
         if (rgpMsgCerts != null && rgpMsgCerts.length != 0) {
            this.cMsgCert = rgpMsgCerts.length;
            Memory mem = new Memory((long)(Native.POINTER_SIZE * rgpMsgCerts.length));

            for(int i = 0; i < rgpMsgCerts.length; ++i) {
               mem.setPointer((long)(i * Native.POINTER_SIZE), rgpMsgCerts[i].getPointer());
            }

            this.rgpMsgCert = mem;
         } else {
            this.rgpMsgCert = null;
            this.cMsgCert = 0;
         }

      }

      public WinCrypt.CERT_CONTEXT[] getRgpMsgCert() {
         return this.rgpMsgCerts;
      }

      public void setRgpMsgCrl(WinCrypt.CRL_CONTEXT[] rgpMsgCrls) {
         this.rgpMsgCrls = rgpMsgCrls;
         if (rgpMsgCrls != null && rgpMsgCrls.length != 0) {
            this.cMsgCert = rgpMsgCrls.length;
            Memory mem = new Memory((long)(Native.POINTER_SIZE * rgpMsgCrls.length));

            for(int i = 0; i < rgpMsgCrls.length; ++i) {
               mem.setPointer((long)(i * Native.POINTER_SIZE), rgpMsgCrls[i].getPointer());
            }

            this.rgpMsgCert = mem;
         } else {
            this.rgpMsgCert = null;
            this.cMsgCert = 0;
         }

      }

      public WinCrypt.CRL_CONTEXT[] getRgpMsgCrl() {
         return this.rgpMsgCrls;
      }

      public void setRgAuthAttr(WinCrypt.CRYPT_ATTRIBUTE[] rgAuthAttrs) {
         this.rgAuthAttrs = rgAuthAttrs;
         if (rgAuthAttrs != null && rgAuthAttrs.length != 0) {
            this.cMsgCert = this.rgpMsgCerts.length;
            this.rgAuthAttr = rgAuthAttrs[0].getPointer();
         } else {
            this.rgAuthAttr = null;
            this.cMsgCert = 0;
         }

      }

      public WinCrypt.CRYPT_ATTRIBUTE[] getRgAuthAttr() {
         return this.rgAuthAttrs;
      }

      public void setRgUnauthAttr(WinCrypt.CRYPT_ATTRIBUTE[] rgUnauthAttrs) {
         this.rgUnauthAttrs = rgUnauthAttrs;
         if (rgUnauthAttrs != null && rgUnauthAttrs.length != 0) {
            this.cMsgCert = this.rgpMsgCerts.length;
            this.rgUnauthAttr = rgUnauthAttrs[0].getPointer();
         } else {
            this.rgUnauthAttr = null;
            this.cMsgCert = 0;
         }

      }

      public WinCrypt.CRYPT_ATTRIBUTE[] getRgUnauthAttr() {
         return this.rgUnauthAttrs;
      }

      public void write() {
         int var2;
         int var3;
         if (this.rgpMsgCerts != null) {
            WinCrypt.CERT_CONTEXT[] var1 = this.rgpMsgCerts;
            var2 = var1.length;

            for(var3 = 0; var3 < var2; ++var3) {
               WinCrypt.CERT_CONTEXT cc = var1[var3];
               cc.write();
            }
         }

         if (this.rgpMsgCrls != null) {
            WinCrypt.CRL_CONTEXT[] var5 = this.rgpMsgCrls;
            var2 = var5.length;

            for(var3 = 0; var3 < var2; ++var3) {
               WinCrypt.CRL_CONTEXT cc = var5[var3];
               cc.write();
            }
         }

         WinCrypt.CRYPT_ATTRIBUTE[] var6;
         WinCrypt.CRYPT_ATTRIBUTE cc;
         if (this.rgAuthAttrs != null) {
            var6 = this.rgAuthAttrs;
            var2 = var6.length;

            for(var3 = 0; var3 < var2; ++var3) {
               cc = var6[var3];
               cc.write();
            }
         }

         if (this.rgUnauthAttrs != null) {
            var6 = this.rgUnauthAttrs;
            var2 = var6.length;

            for(var3 = 0; var3 < var2; ++var3) {
               cc = var6[var3];
               cc.write();
            }
         }

         this.cbSize = this.size();
         super.write();
      }

      public void read() {
         int var2;
         int var3;
         if (this.rgpMsgCerts != null) {
            WinCrypt.CERT_CONTEXT[] var1 = this.rgpMsgCerts;
            var2 = var1.length;

            for(var3 = 0; var3 < var2; ++var3) {
               WinCrypt.CERT_CONTEXT cc = var1[var3];
               cc.read();
            }
         }

         if (this.rgpMsgCrls != null) {
            WinCrypt.CRL_CONTEXT[] var5 = this.rgpMsgCrls;
            var2 = var5.length;

            for(var3 = 0; var3 < var2; ++var3) {
               WinCrypt.CRL_CONTEXT cc = var5[var3];
               cc.read();
            }
         }

         WinCrypt.CRYPT_ATTRIBUTE[] var6;
         WinCrypt.CRYPT_ATTRIBUTE cc;
         if (this.rgAuthAttrs != null) {
            var6 = this.rgAuthAttrs;
            var2 = var6.length;

            for(var3 = 0; var3 < var2; ++var3) {
               cc = var6[var3];
               cc.read();
            }
         }

         if (this.rgUnauthAttrs != null) {
            var6 = this.rgUnauthAttrs;
            var2 = var6.length;

            for(var3 = 0; var3 < var2; ++var3) {
               cc = var6[var3];
               cc.read();
            }
         }

         super.read();
      }
   }
}
