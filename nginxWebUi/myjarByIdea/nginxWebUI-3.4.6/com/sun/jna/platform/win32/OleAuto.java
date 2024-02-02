package com.sun.jna.platform.win32;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.ptr.DoubleByReference;
import com.sun.jna.ptr.PointerByReference;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;

public interface OleAuto extends StdCallLibrary {
   OleAuto INSTANCE = (OleAuto)Native.load("OleAut32", OleAuto.class, W32APIOptions.DEFAULT_OPTIONS);
   int DISPATCH_METHOD = 1;
   int DISPATCH_PROPERTYGET = 2;
   int DISPATCH_PROPERTYPUT = 4;
   int DISPATCH_PROPERTYPUTREF = 8;
   int FADF_AUTO = 1;
   int FADF_STATIC = 2;
   int FADF_EMBEDDED = 4;
   int FADF_FIXEDSIZE = 16;
   int FADF_RECORD = 32;
   int FADF_HAVEIID = 64;
   int FADF_HAVEVARTYPE = 128;
   int FADF_BSTR = 256;
   int FADF_UNKNOWN = 512;
   int FADF_DISPATCH = 1024;
   int FADF_VARIANT = 2048;
   int FADF_RESERVED = 61448;
   short VARIANT_NOVALUEPROP = 1;
   short VARIANT_ALPHABOOL = 2;
   short VARIANT_NOUSEROVERRIDE = 4;
   short VARIANT_CALENDAR_HIJRI = 8;
   short VARIANT_LOCALBOOL = 16;
   short VARIANT_CALENDAR_THAI = 32;
   short VARIANT_CALENDAR_GREGORIAN = 64;
   short VARIANT_USE_NLS = 128;

   WTypes.BSTR SysAllocString(String var1);

   void SysFreeString(WTypes.BSTR var1);

   int SysStringByteLen(WTypes.BSTR var1);

   int SysStringLen(WTypes.BSTR var1);

   void VariantInit(Variant.VARIANT.ByReference var1);

   void VariantInit(Variant.VARIANT var1);

   WinNT.HRESULT VariantCopy(Pointer var1, Variant.VARIANT var2);

   WinNT.HRESULT VariantClear(Variant.VARIANT var1);

   WinNT.HRESULT VariantChangeType(Variant.VARIANT var1, Variant.VARIANT var2, short var3, WTypes.VARTYPE var4);

   WinNT.HRESULT VariantChangeType(Variant.VARIANT.ByReference var1, Variant.VARIANT.ByReference var2, short var3, WTypes.VARTYPE var4);

   OaIdl.SAFEARRAY.ByReference SafeArrayCreate(WTypes.VARTYPE var1, WinDef.UINT var2, OaIdl.SAFEARRAYBOUND[] var3);

   WinNT.HRESULT SafeArrayPutElement(OaIdl.SAFEARRAY var1, WinDef.LONG[] var2, Pointer var3);

   WinNT.HRESULT SafeArrayGetUBound(OaIdl.SAFEARRAY var1, WinDef.UINT var2, WinDef.LONGByReference var3);

   WinNT.HRESULT SafeArrayGetLBound(OaIdl.SAFEARRAY var1, WinDef.UINT var2, WinDef.LONGByReference var3);

   WinNT.HRESULT SafeArrayGetElement(OaIdl.SAFEARRAY var1, WinDef.LONG[] var2, Pointer var3);

   WinNT.HRESULT SafeArrayPtrOfIndex(OaIdl.SAFEARRAY var1, WinDef.LONG[] var2, PointerByReference var3);

   WinNT.HRESULT SafeArrayLock(OaIdl.SAFEARRAY var1);

   WinNT.HRESULT SafeArrayUnlock(OaIdl.SAFEARRAY var1);

   WinNT.HRESULT SafeArrayDestroy(OaIdl.SAFEARRAY var1);

   WinNT.HRESULT SafeArrayRedim(OaIdl.SAFEARRAY var1, OaIdl.SAFEARRAYBOUND var2);

   WinNT.HRESULT SafeArrayGetVartype(OaIdl.SAFEARRAY var1, WTypes.VARTYPEByReference var2);

   WinDef.UINT SafeArrayGetDim(OaIdl.SAFEARRAY var1);

   WinNT.HRESULT SafeArrayAccessData(OaIdl.SAFEARRAY var1, PointerByReference var2);

   WinNT.HRESULT SafeArrayUnaccessData(OaIdl.SAFEARRAY var1);

   WinDef.UINT SafeArrayGetElemsize(OaIdl.SAFEARRAY var1);

   WinNT.HRESULT GetActiveObject(Guid.GUID var1, WinDef.PVOID var2, PointerByReference var3);

   WinNT.HRESULT LoadRegTypeLib(Guid.GUID var1, int var2, int var3, WinDef.LCID var4, PointerByReference var5);

   WinNT.HRESULT LoadTypeLib(String var1, PointerByReference var2);

   int SystemTimeToVariantTime(WinBase.SYSTEMTIME var1, DoubleByReference var2);

   @Structure.FieldOrder({"rgvarg", "rgdispidNamedArgs", "cArgs", "cNamedArgs"})
   public static class DISPPARAMS extends Structure {
      public Variant.VariantArg.ByReference rgvarg;
      public Pointer rgdispidNamedArgs;
      public WinDef.UINT cArgs;
      public WinDef.UINT cNamedArgs;

      public OaIdl.DISPID[] getRgdispidNamedArgs() {
         OaIdl.DISPID[] namedArgs = null;
         int count = this.cNamedArgs.intValue();
         if (this.rgdispidNamedArgs != null && count > 0) {
            int[] rawData = this.rgdispidNamedArgs.getIntArray(0L, count);
            namedArgs = new OaIdl.DISPID[count];

            for(int i = 0; i < count; ++i) {
               namedArgs[i] = new OaIdl.DISPID(rawData[i]);
            }
         } else {
            namedArgs = new OaIdl.DISPID[0];
         }

         return namedArgs;
      }

      public void setRgdispidNamedArgs(OaIdl.DISPID[] namedArgs) {
         if (namedArgs == null) {
            namedArgs = new OaIdl.DISPID[0];
         }

         this.cNamedArgs = new WinDef.UINT((long)namedArgs.length);
         this.rgdispidNamedArgs = new Memory((long)(OaIdl.DISPID.SIZE * namedArgs.length));
         int[] rawData = new int[namedArgs.length];

         for(int i = 0; i < rawData.length; ++i) {
            rawData[i] = namedArgs[i].intValue();
         }

         this.rgdispidNamedArgs.write(0L, (int[])rawData, 0, namedArgs.length);
      }

      public Variant.VARIANT[] getArgs() {
         if (this.rgvarg != null) {
            this.rgvarg.setArraySize(this.cArgs.intValue());
            return this.rgvarg.variantArg;
         } else {
            return new Variant.VARIANT[0];
         }
      }

      public void setArgs(Variant.VARIANT[] arguments) {
         if (arguments == null) {
            arguments = new Variant.VARIANT[0];
         }

         this.rgvarg = new Variant.VariantArg.ByReference(arguments);
         this.cArgs = new WinDef.UINT((long)arguments.length);
      }

      public DISPPARAMS() {
         this.rgdispidNamedArgs = Pointer.NULL;
         this.cArgs = new WinDef.UINT(0L);
         this.cNamedArgs = new WinDef.UINT(0L);
      }

      public DISPPARAMS(Pointer memory) {
         super(memory);
         this.rgdispidNamedArgs = Pointer.NULL;
         this.cArgs = new WinDef.UINT(0L);
         this.cNamedArgs = new WinDef.UINT(0L);
         this.read();
      }

      public static class ByReference extends DISPPARAMS implements Structure.ByReference {
      }
   }
}
