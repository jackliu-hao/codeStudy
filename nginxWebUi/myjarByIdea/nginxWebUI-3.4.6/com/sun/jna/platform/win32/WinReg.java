package com.sun.jna.platform.win32;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.ByReference;

public interface WinReg {
   HKEY HKEY_CLASSES_ROOT = new HKEY(Integer.MIN_VALUE);
   HKEY HKEY_CURRENT_USER = new HKEY(-2147483647);
   HKEY HKEY_LOCAL_MACHINE = new HKEY(-2147483646);
   HKEY HKEY_USERS = new HKEY(-2147483645);
   HKEY HKEY_PERFORMANCE_DATA = new HKEY(-2147483644);
   HKEY HKEY_PERFORMANCE_TEXT = new HKEY(-2147483568);
   HKEY HKEY_PERFORMANCE_NLSTEXT = new HKEY(-2147483552);
   HKEY HKEY_CURRENT_CONFIG = new HKEY(-2147483643);
   HKEY HKEY_DYN_DATA = new HKEY(-2147483642);

   public static class HKEYByReference extends ByReference {
      public HKEYByReference() {
         this((HKEY)null);
      }

      public HKEYByReference(HKEY h) {
         super(Native.POINTER_SIZE);
         this.setValue(h);
      }

      public void setValue(HKEY h) {
         this.getPointer().setPointer(0L, h != null ? h.getPointer() : null);
      }

      public HKEY getValue() {
         Pointer p = this.getPointer().getPointer(0L);
         if (p == null) {
            return null;
         } else if (WinBase.INVALID_HANDLE_VALUE.getPointer().equals(p)) {
            return (HKEY)WinBase.INVALID_HANDLE_VALUE;
         } else {
            HKEY h = new HKEY();
            h.setPointer(p);
            return h;
         }
      }
   }

   public static class HKEY extends WinNT.HANDLE {
      public HKEY() {
      }

      public HKEY(Pointer p) {
         super(p);
      }

      public HKEY(int value) {
         super(new Pointer((long)value));
      }
   }
}
