package com.sun.jna.platform.win32.COM;

import com.sun.jna.Pointer;
import com.sun.jna.WString;
import com.sun.jna.platform.win32.Guid;
import com.sun.jna.platform.win32.OaIdl;
import com.sun.jna.platform.win32.OaIdlUtil;
import com.sun.jna.platform.win32.Ole32;
import com.sun.jna.platform.win32.OleAuto;
import com.sun.jna.platform.win32.Variant;
import com.sun.jna.platform.win32.WTypes;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;

public interface Wbemcli {
   int WBEM_FLAG_RETURN_IMMEDIATELY = 16;
   int WBEM_FLAG_FORWARD_ONLY = 32;
   int WBEM_INFINITE = -1;
   int WBEM_S_NO_ERROR = 0;
   int WBEM_S_FALSE = 1;
   int WBEM_S_TIMEDOUT = 262148;
   int WBEM_S_NO_MORE_DATA = 262149;
   int WBEM_E_INVALID_NAMESPACE = -2147217394;
   int WBEM_E_INVALID_CLASS = -2147217392;
   int WBEM_E_INVALID_QUERY = -2147217385;
   int CIM_ILLEGAL = 4095;
   int CIM_EMPTY = 0;
   int CIM_SINT8 = 16;
   int CIM_UINT8 = 17;
   int CIM_SINT16 = 2;
   int CIM_UINT16 = 18;
   int CIM_SINT32 = 3;
   int CIM_UINT32 = 19;
   int CIM_SINT64 = 20;
   int CIM_UINT64 = 21;
   int CIM_REAL32 = 4;
   int CIM_REAL64 = 5;
   int CIM_BOOLEAN = 11;
   int CIM_STRING = 8;
   int CIM_DATETIME = 101;
   int CIM_REFERENCE = 102;
   int CIM_CHAR16 = 103;
   int CIM_OBJECT = 13;
   int CIM_FLAG_ARRAY = 8192;

   public static class IWbemContext extends Unknown {
      public IWbemContext() {
      }

      public IWbemContext(Pointer pvInstance) {
         super(pvInstance);
      }
   }

   public static class IWbemServices extends Unknown {
      public IWbemServices() {
      }

      public IWbemServices(Pointer pvInstance) {
         super(pvInstance);
      }

      public WinNT.HRESULT ExecQuery(WTypes.BSTR strQueryLanguage, WTypes.BSTR strQuery, int lFlags, IWbemContext pCtx, PointerByReference ppEnum) {
         return (WinNT.HRESULT)this._invokeNativeObject(20, new Object[]{this.getPointer(), strQueryLanguage, strQuery, lFlags, pCtx, ppEnum}, WinNT.HRESULT.class);
      }

      public IEnumWbemClassObject ExecQuery(String strQueryLanguage, String strQuery, int lFlags, IWbemContext pCtx) {
         WTypes.BSTR strQueryLanguageBSTR = OleAuto.INSTANCE.SysAllocString(strQueryLanguage);
         WTypes.BSTR strQueryBSTR = OleAuto.INSTANCE.SysAllocString(strQuery);

         IEnumWbemClassObject var9;
         try {
            PointerByReference pbr = new PointerByReference();
            WinNT.HRESULT res = this.ExecQuery(strQueryLanguageBSTR, strQueryBSTR, lFlags, pCtx, pbr);
            COMUtils.checkRC(res);
            var9 = new IEnumWbemClassObject(pbr.getValue());
         } finally {
            OleAuto.INSTANCE.SysFreeString(strQueryLanguageBSTR);
            OleAuto.INSTANCE.SysFreeString(strQueryBSTR);
         }

         return var9;
      }
   }

   public static class IWbemLocator extends Unknown {
      public static final Guid.CLSID CLSID_WbemLocator = new Guid.CLSID("4590f811-1d3a-11d0-891f-00aa004b2e24");
      public static final Guid.GUID IID_IWbemLocator = new Guid.GUID("dc12a687-737f-11cf-884d-00aa004b2e24");

      public IWbemLocator() {
      }

      private IWbemLocator(Pointer pvInstance) {
         super(pvInstance);
      }

      public static IWbemLocator create() {
         PointerByReference pbr = new PointerByReference();
         WinNT.HRESULT hres = Ole32.INSTANCE.CoCreateInstance(CLSID_WbemLocator, (Pointer)null, 1, IID_IWbemLocator, pbr);
         return COMUtils.FAILED(hres) ? null : new IWbemLocator(pbr.getValue());
      }

      public WinNT.HRESULT ConnectServer(WTypes.BSTR strNetworkResource, WTypes.BSTR strUser, WTypes.BSTR strPassword, WTypes.BSTR strLocale, int lSecurityFlags, WTypes.BSTR strAuthority, IWbemContext pCtx, PointerByReference ppNamespace) {
         return (WinNT.HRESULT)this._invokeNativeObject(3, new Object[]{this.getPointer(), strNetworkResource, strUser, strPassword, strLocale, lSecurityFlags, strAuthority, pCtx, ppNamespace}, WinNT.HRESULT.class);
      }

      public IWbemServices ConnectServer(String strNetworkResource, String strUser, String strPassword, String strLocale, int lSecurityFlags, String strAuthority, IWbemContext pCtx) {
         WTypes.BSTR strNetworkResourceBSTR = OleAuto.INSTANCE.SysAllocString(strNetworkResource);
         WTypes.BSTR strUserBSTR = OleAuto.INSTANCE.SysAllocString(strUser);
         WTypes.BSTR strPasswordBSTR = OleAuto.INSTANCE.SysAllocString(strPassword);
         WTypes.BSTR strLocaleBSTR = OleAuto.INSTANCE.SysAllocString(strLocale);
         WTypes.BSTR strAuthorityBSTR = OleAuto.INSTANCE.SysAllocString(strAuthority);
         PointerByReference pbr = new PointerByReference();

         IWbemServices var15;
         try {
            WinNT.HRESULT result = this.ConnectServer(strNetworkResourceBSTR, strUserBSTR, strPasswordBSTR, strLocaleBSTR, lSecurityFlags, strAuthorityBSTR, pCtx, pbr);
            COMUtils.checkRC(result);
            var15 = new IWbemServices(pbr.getValue());
         } finally {
            OleAuto.INSTANCE.SysFreeString(strNetworkResourceBSTR);
            OleAuto.INSTANCE.SysFreeString(strUserBSTR);
            OleAuto.INSTANCE.SysFreeString(strPasswordBSTR);
            OleAuto.INSTANCE.SysFreeString(strLocaleBSTR);
            OleAuto.INSTANCE.SysFreeString(strAuthorityBSTR);
         }

         return var15;
      }
   }

   public static class IEnumWbemClassObject extends Unknown {
      public IEnumWbemClassObject() {
      }

      public IEnumWbemClassObject(Pointer pvInstance) {
         super(pvInstance);
      }

      public WinNT.HRESULT Next(int lTimeOut, int uCount, Pointer[] ppObjects, IntByReference puReturned) {
         return (WinNT.HRESULT)this._invokeNativeObject(4, new Object[]{this.getPointer(), lTimeOut, uCount, ppObjects, puReturned}, WinNT.HRESULT.class);
      }

      public IWbemClassObject[] Next(int lTimeOut, int uCount) {
         Pointer[] resultArray = new Pointer[uCount];
         IntByReference resultCount = new IntByReference();
         WinNT.HRESULT result = this.Next(lTimeOut, uCount, resultArray, resultCount);
         COMUtils.checkRC(result);
         IWbemClassObject[] returnValue = new IWbemClassObject[resultCount.getValue()];

         for(int i = 0; i < resultCount.getValue(); ++i) {
            returnValue[i] = new IWbemClassObject(resultArray[i]);
         }

         return returnValue;
      }
   }

   public static class IWbemClassObject extends Unknown {
      public IWbemClassObject() {
      }

      public IWbemClassObject(Pointer pvInstance) {
         super(pvInstance);
      }

      public WinNT.HRESULT Get(WString wszName, int lFlags, Variant.VARIANT.ByReference pVal, IntByReference pType, IntByReference plFlavor) {
         return (WinNT.HRESULT)this._invokeNativeObject(4, new Object[]{this.getPointer(), wszName, lFlags, pVal, pType, plFlavor}, WinNT.HRESULT.class);
      }

      public WinNT.HRESULT Get(String wszName, int lFlags, Variant.VARIANT.ByReference pVal, IntByReference pType, IntByReference plFlavor) {
         return this.Get(wszName == null ? null : new WString(wszName), lFlags, pVal, pType, plFlavor);
      }

      public WinNT.HRESULT GetNames(String wszQualifierName, int lFlags, Variant.VARIANT.ByReference pQualifierVal, PointerByReference pNames) {
         return this.GetNames(wszQualifierName == null ? null : new WString(wszQualifierName), lFlags, pQualifierVal, pNames);
      }

      public WinNT.HRESULT GetNames(WString wszQualifierName, int lFlags, Variant.VARIANT.ByReference pQualifierVal, PointerByReference pNames) {
         return (WinNT.HRESULT)this._invokeNativeObject(7, new Object[]{this.getPointer(), wszQualifierName, lFlags, pQualifierVal, pNames}, WinNT.HRESULT.class);
      }

      public String[] GetNames(String wszQualifierName, int lFlags, Variant.VARIANT.ByReference pQualifierVal) {
         PointerByReference pbr = new PointerByReference();
         COMUtils.checkRC(this.GetNames(wszQualifierName, lFlags, pQualifierVal, pbr));
         Object[] nameObjects = (Object[])((Object[])OaIdlUtil.toPrimitiveArray(new OaIdl.SAFEARRAY(pbr.getValue()), true));
         String[] names = new String[nameObjects.length];

         for(int i = 0; i < nameObjects.length; ++i) {
            names[i] = (String)nameObjects[i];
         }

         return names;
      }
   }

   public interface WBEM_CONDITION_FLAG_TYPE {
      int WBEM_FLAG_ALWAYS = 0;
      int WBEM_FLAG_ONLY_IF_TRUE = 1;
      int WBEM_FLAG_ONLY_IF_FALSE = 2;
      int WBEM_FLAG_ONLY_IF_IDENTICAL = 3;
      int WBEM_MASK_PRIMARY_CONDITION = 3;
      int WBEM_FLAG_KEYS_ONLY = 4;
      int WBEM_FLAG_REFS_ONLY = 8;
      int WBEM_FLAG_LOCAL_ONLY = 16;
      int WBEM_FLAG_PROPAGATED_ONLY = 32;
      int WBEM_FLAG_SYSTEM_ONLY = 48;
      int WBEM_FLAG_NONSYSTEM_ONLY = 64;
      int WBEM_MASK_CONDITION_ORIGIN = 112;
      int WBEM_FLAG_CLASS_OVERRIDES_ONLY = 256;
      int WBEM_FLAG_CLASS_LOCAL_AND_OVERRIDES = 512;
      int WBEM_MASK_CLASS_CONDITION = 768;
   }
}
