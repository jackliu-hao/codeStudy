package com.sun.jna.platform.win32;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.PointerType;
import com.sun.jna.Structure;
import java.io.UnsupportedEncodingException;

public interface WTypes {
   int CLSCTX_INPROC_SERVER = 1;
   int CLSCTX_INPROC_HANDLER = 2;
   int CLSCTX_LOCAL_SERVER = 4;
   int CLSCTX_INPROC_SERVER16 = 8;
   int CLSCTX_REMOTE_SERVER = 16;
   int CLSCTX_INPROC_HANDLER16 = 32;
   int CLSCTX_RESERVED1 = 64;
   int CLSCTX_RESERVED2 = 128;
   int CLSCTX_RESERVED3 = 256;
   int CLSCTX_RESERVED4 = 512;
   int CLSCTX_NO_CODE_DOWNLOAD = 1024;
   int CLSCTX_RESERVED5 = 2048;
   int CLSCTX_NO_CUSTOM_MARSHAL = 4096;
   int CLSCTX_ENABLE_CODE_DOWNLOAD = 8192;
   int CLSCTX_NO_FAILURE_LOG = 16384;
   int CLSCTX_DISABLE_AAA = 32768;
   int CLSCTX_ENABLE_AAA = 65536;
   int CLSCTX_FROM_DEFAULT_CONTEXT = 131072;
   int CLSCTX_ACTIVATE_32_BIT_SERVER = 262144;
   int CLSCTX_ACTIVATE_64_BIT_SERVER = 524288;
   int CLSCTX_ENABLE_CLOAKING = 1048576;
   int CLSCTX_APPCONTAINER = 4194304;
   int CLSCTX_ACTIVATE_AAA_AS_IU = 8388608;
   int CLSCTX_PS_DLL = Integer.MIN_VALUE;
   int CLSCTX_SERVER = 21;
   int CLSCTX_ALL = 7;

   public static class VARTYPEByReference extends com.sun.jna.ptr.ByReference {
      public VARTYPEByReference() {
         super(2);
      }

      public VARTYPEByReference(VARTYPE type) {
         super(2);
         this.setValue(type);
      }

      public VARTYPEByReference(short type) {
         super(2);
         this.getPointer().setShort(0L, type);
      }

      public void setValue(VARTYPE value) {
         this.getPointer().setShort(0L, value.shortValue());
      }

      public VARTYPE getValue() {
         return new VARTYPE(this.getPointer().getShort(0L));
      }
   }

   public static class VARTYPE extends WinDef.USHORT {
      private static final long serialVersionUID = 1L;

      public VARTYPE() {
         this(0);
      }

      public VARTYPE(int value) {
         super((long)value);
      }
   }

   public static class LPOLESTR extends PointerType {
      public LPOLESTR() {
         super(Pointer.NULL);
      }

      public LPOLESTR(Pointer pointer) {
         super(pointer);
      }

      public LPOLESTR(String value) {
         super(new Memory(((long)value.length() + 1L) * (long)Native.WCHAR_SIZE));
         this.setValue(value);
      }

      public void setValue(String value) {
         this.getPointer().setWideString(0L, value);
      }

      public String getValue() {
         Pointer pointer = this.getPointer();
         String str = null;
         if (pointer != null) {
            str = pointer.getWideString(0L);
         }

         return str;
      }

      public String toString() {
         return this.getValue();
      }

      public static class ByReference extends LPOLESTR implements Structure.ByReference {
      }
   }

   public static class LPWSTR extends PointerType {
      public LPWSTR() {
         super(Pointer.NULL);
      }

      public LPWSTR(Pointer pointer) {
         super(pointer);
      }

      public LPWSTR(String value) {
         this((Pointer)(new Memory(((long)value.length() + 1L) * (long)Native.WCHAR_SIZE)));
         this.setValue(value);
      }

      public void setValue(String value) {
         this.getPointer().setWideString(0L, value);
      }

      public String getValue() {
         Pointer pointer = this.getPointer();
         String str = null;
         if (pointer != null) {
            str = pointer.getWideString(0L);
         }

         return str;
      }

      public String toString() {
         return this.getValue();
      }

      public static class ByReference extends LPWSTR implements Structure.ByReference {
      }
   }

   public static class LPSTR extends PointerType {
      public LPSTR() {
         super(Pointer.NULL);
      }

      public LPSTR(Pointer pointer) {
         super(pointer);
      }

      public LPSTR(String value) {
         this((Pointer)(new Memory((long)value.length() + 1L)));
         this.setValue(value);
      }

      public void setValue(String value) {
         this.getPointer().setString(0L, value);
      }

      public String getValue() {
         Pointer pointer = this.getPointer();
         String str = null;
         if (pointer != null) {
            str = pointer.getString(0L);
         }

         return str;
      }

      public String toString() {
         return this.getValue();
      }

      public static class ByReference extends LPSTR implements Structure.ByReference {
      }
   }

   public static class BSTRByReference extends com.sun.jna.ptr.ByReference {
      public BSTRByReference() {
         super(Native.POINTER_SIZE);
      }

      public BSTRByReference(BSTR value) {
         this();
         this.setValue(value);
      }

      public void setValue(BSTR value) {
         this.getPointer().setPointer(0L, value.getPointer());
      }

      public BSTR getValue() {
         return new BSTR(this.getPointer().getPointer(0L));
      }

      public String getString() {
         return this.getValue().getValue();
      }
   }

   public static class BSTR extends PointerType {
      public BSTR() {
         super(Pointer.NULL);
      }

      public BSTR(Pointer pointer) {
         super(pointer);
      }

      public BSTR(String value) {
         this.setValue(value);
      }

      public void setValue(String value) {
         if (value == null) {
            value = "";
         }

         try {
            byte[] encodedValue = value.getBytes("UTF-16LE");
            Memory mem = new Memory((long)(4 + encodedValue.length + 2));
            mem.clear();
            mem.setInt(0L, encodedValue.length);
            mem.write(4L, (byte[])encodedValue, 0, encodedValue.length);
            this.setPointer(mem.share(4L));
         } catch (UnsupportedEncodingException var4) {
            throw new RuntimeException("UTF-16LE charset is not supported", var4);
         }
      }

      public String getValue() {
         try {
            Pointer pointer = this.getPointer();
            if (pointer == null) {
               return "";
            } else {
               int stringLength = pointer.getInt(-4L);
               return new String(pointer.getByteArray(0L, stringLength), "UTF-16LE");
            }
         } catch (UnsupportedEncodingException var3) {
            throw new RuntimeException("UTF-16LE charset is not supported", var3);
         }
      }

      public String toString() {
         return this.getValue();
      }
   }
}
