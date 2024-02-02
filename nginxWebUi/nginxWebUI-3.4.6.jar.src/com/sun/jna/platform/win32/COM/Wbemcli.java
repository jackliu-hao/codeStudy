/*     */ package com.sun.jna.platform.win32.COM;
/*     */ 
/*     */ import com.sun.jna.Pointer;
/*     */ import com.sun.jna.WString;
/*     */ import com.sun.jna.platform.win32.Guid;
/*     */ import com.sun.jna.platform.win32.OaIdl;
/*     */ import com.sun.jna.platform.win32.OaIdlUtil;
/*     */ import com.sun.jna.platform.win32.Ole32;
/*     */ import com.sun.jna.platform.win32.OleAuto;
/*     */ import com.sun.jna.platform.win32.Variant;
/*     */ import com.sun.jna.platform.win32.WTypes;
/*     */ import com.sun.jna.platform.win32.WinNT;
/*     */ import com.sun.jna.ptr.IntByReference;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public interface Wbemcli
/*     */ {
/*     */   public static final int WBEM_FLAG_RETURN_IMMEDIATELY = 16;
/*     */   public static final int WBEM_FLAG_FORWARD_ONLY = 32;
/*     */   public static final int WBEM_INFINITE = -1;
/*     */   public static final int WBEM_S_NO_ERROR = 0;
/*     */   public static final int WBEM_S_FALSE = 1;
/*     */   public static final int WBEM_S_TIMEDOUT = 262148;
/*     */   public static final int WBEM_S_NO_MORE_DATA = 262149;
/*     */   public static final int WBEM_E_INVALID_NAMESPACE = -2147217394;
/*     */   public static final int WBEM_E_INVALID_CLASS = -2147217392;
/*     */   public static final int WBEM_E_INVALID_QUERY = -2147217385;
/*     */   public static final int CIM_ILLEGAL = 4095;
/*     */   public static final int CIM_EMPTY = 0;
/*     */   public static final int CIM_SINT8 = 16;
/*     */   public static final int CIM_UINT8 = 17;
/*     */   public static final int CIM_SINT16 = 2;
/*     */   public static final int CIM_UINT16 = 18;
/*     */   public static final int CIM_SINT32 = 3;
/*     */   public static final int CIM_UINT32 = 19;
/*     */   public static final int CIM_SINT64 = 20;
/*     */   public static final int CIM_UINT64 = 21;
/*     */   public static final int CIM_REAL32 = 4;
/*     */   public static final int CIM_REAL64 = 5;
/*     */   public static final int CIM_BOOLEAN = 11;
/*     */   public static final int CIM_STRING = 8;
/*     */   public static final int CIM_DATETIME = 101;
/*     */   public static final int CIM_REFERENCE = 102;
/*     */   public static final int CIM_CHAR16 = 103;
/*     */   public static final int CIM_OBJECT = 13;
/*     */   public static final int CIM_FLAG_ARRAY = 8192;
/*     */   
/*     */   public static interface WBEM_CONDITION_FLAG_TYPE
/*     */   {
/*     */     public static final int WBEM_FLAG_ALWAYS = 0;
/*     */     public static final int WBEM_FLAG_ONLY_IF_TRUE = 1;
/*     */     public static final int WBEM_FLAG_ONLY_IF_FALSE = 2;
/*     */     public static final int WBEM_FLAG_ONLY_IF_IDENTICAL = 3;
/*     */     public static final int WBEM_MASK_PRIMARY_CONDITION = 3;
/*     */     public static final int WBEM_FLAG_KEYS_ONLY = 4;
/*     */     public static final int WBEM_FLAG_REFS_ONLY = 8;
/*     */     public static final int WBEM_FLAG_LOCAL_ONLY = 16;
/*     */     public static final int WBEM_FLAG_PROPAGATED_ONLY = 32;
/*     */     public static final int WBEM_FLAG_SYSTEM_ONLY = 48;
/*     */     public static final int WBEM_FLAG_NONSYSTEM_ONLY = 64;
/*     */     public static final int WBEM_MASK_CONDITION_ORIGIN = 112;
/*     */     public static final int WBEM_FLAG_CLASS_OVERRIDES_ONLY = 256;
/*     */     public static final int WBEM_FLAG_CLASS_LOCAL_AND_OVERRIDES = 512;
/*     */     public static final int WBEM_MASK_CLASS_CONDITION = 768;
/*     */   }
/*     */   
/*     */   public static class IWbemClassObject
/*     */     extends Unknown
/*     */   {
/*     */     public IWbemClassObject() {}
/*     */     
/*     */     public IWbemClassObject(Pointer pvInstance) {
/* 114 */       super(pvInstance);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public WinNT.HRESULT Get(WString wszName, int lFlags, Variant.VARIANT.ByReference pVal, IntByReference pType, IntByReference plFlavor) {
/* 120 */       return (WinNT.HRESULT)_invokeNativeObject(4, new Object[] {
/* 121 */             getPointer(), wszName, Integer.valueOf(lFlags), pVal, pType, plFlavor }, WinNT.HRESULT.class);
/*     */     }
/*     */ 
/*     */     
/*     */     public WinNT.HRESULT Get(String wszName, int lFlags, Variant.VARIANT.ByReference pVal, IntByReference pType, IntByReference plFlavor) {
/* 126 */       return Get((wszName == null) ? null : new WString(wszName), lFlags, pVal, pType, plFlavor);
/*     */     }
/*     */     
/*     */     public WinNT.HRESULT GetNames(String wszQualifierName, int lFlags, Variant.VARIANT.ByReference pQualifierVal, PointerByReference pNames) {
/* 130 */       return GetNames((wszQualifierName == null) ? null : new WString(wszQualifierName), lFlags, pQualifierVal, pNames);
/*     */     }
/*     */ 
/*     */     
/*     */     public WinNT.HRESULT GetNames(WString wszQualifierName, int lFlags, Variant.VARIANT.ByReference pQualifierVal, PointerByReference pNames) {
/* 135 */       return (WinNT.HRESULT)_invokeNativeObject(7, new Object[] {
/* 136 */             getPointer(), wszQualifierName, Integer.valueOf(lFlags), pQualifierVal, pNames }, WinNT.HRESULT.class);
/*     */     }
/*     */     
/*     */     public String[] GetNames(String wszQualifierName, int lFlags, Variant.VARIANT.ByReference pQualifierVal) {
/* 140 */       PointerByReference pbr = new PointerByReference();
/* 141 */       COMUtils.checkRC(GetNames(wszQualifierName, lFlags, pQualifierVal, pbr));
/* 142 */       Object[] nameObjects = (Object[])OaIdlUtil.toPrimitiveArray(new OaIdl.SAFEARRAY(pbr.getValue()), true);
/* 143 */       String[] names = new String[nameObjects.length];
/* 144 */       for (int i = 0; i < nameObjects.length; i++) {
/* 145 */         names[i] = (String)nameObjects[i];
/*     */       }
/* 147 */       return names;
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static class IEnumWbemClassObject
/*     */     extends Unknown
/*     */   {
/*     */     public IEnumWbemClassObject() {}
/*     */ 
/*     */     
/*     */     public IEnumWbemClassObject(Pointer pvInstance) {
/* 160 */       super(pvInstance);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public WinNT.HRESULT Next(int lTimeOut, int uCount, Pointer[] ppObjects, IntByReference puReturned) {
/* 166 */       return (WinNT.HRESULT)_invokeNativeObject(4, new Object[] {
/* 167 */             getPointer(), Integer.valueOf(lTimeOut), Integer.valueOf(uCount), ppObjects, puReturned }, WinNT.HRESULT.class);
/*     */     }
/*     */     
/*     */     public Wbemcli.IWbemClassObject[] Next(int lTimeOut, int uCount) {
/* 171 */       Pointer[] resultArray = new Pointer[uCount];
/* 172 */       IntByReference resultCount = new IntByReference();
/* 173 */       WinNT.HRESULT result = Next(lTimeOut, uCount, resultArray, resultCount);
/* 174 */       COMUtils.checkRC(result);
/* 175 */       Wbemcli.IWbemClassObject[] returnValue = new Wbemcli.IWbemClassObject[resultCount.getValue()];
/* 176 */       for (int i = 0; i < resultCount.getValue(); i++) {
/* 177 */         returnValue[i] = new Wbemcli.IWbemClassObject(resultArray[i]);
/*     */       }
/* 179 */       return returnValue;
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static class IWbemLocator
/*     */     extends Unknown
/*     */   {
/* 189 */     public static final Guid.CLSID CLSID_WbemLocator = new Guid.CLSID("4590f811-1d3a-11d0-891f-00aa004b2e24");
/* 190 */     public static final Guid.GUID IID_IWbemLocator = new Guid.GUID("dc12a687-737f-11cf-884d-00aa004b2e24");
/*     */ 
/*     */     
/*     */     public IWbemLocator() {}
/*     */     
/*     */     private IWbemLocator(Pointer pvInstance) {
/* 196 */       super(pvInstance);
/*     */     }
/*     */     
/*     */     public static IWbemLocator create() {
/* 200 */       PointerByReference pbr = new PointerByReference();
/*     */       
/* 202 */       WinNT.HRESULT hres = Ole32.INSTANCE.CoCreateInstance((Guid.GUID)CLSID_WbemLocator, null, 1, IID_IWbemLocator, pbr);
/*     */       
/* 204 */       if (COMUtils.FAILED(hres)) {
/* 205 */         return null;
/*     */       }
/*     */       
/* 208 */       return new IWbemLocator(pbr.getValue());
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public WinNT.HRESULT ConnectServer(WTypes.BSTR strNetworkResource, WTypes.BSTR strUser, WTypes.BSTR strPassword, WTypes.BSTR strLocale, int lSecurityFlags, WTypes.BSTR strAuthority, Wbemcli.IWbemContext pCtx, PointerByReference ppNamespace) {
/* 214 */       return (WinNT.HRESULT)_invokeNativeObject(3, new Object[] { getPointer(), strNetworkResource, strUser, strPassword, strLocale, 
/* 215 */             Integer.valueOf(lSecurityFlags), strAuthority, pCtx, ppNamespace }, WinNT.HRESULT.class);
/*     */     }
/*     */ 
/*     */     
/*     */     public Wbemcli.IWbemServices ConnectServer(String strNetworkResource, String strUser, String strPassword, String strLocale, int lSecurityFlags, String strAuthority, Wbemcli.IWbemContext pCtx) {
/* 220 */       WTypes.BSTR strNetworkResourceBSTR = OleAuto.INSTANCE.SysAllocString(strNetworkResource);
/* 221 */       WTypes.BSTR strUserBSTR = OleAuto.INSTANCE.SysAllocString(strUser);
/* 222 */       WTypes.BSTR strPasswordBSTR = OleAuto.INSTANCE.SysAllocString(strPassword);
/* 223 */       WTypes.BSTR strLocaleBSTR = OleAuto.INSTANCE.SysAllocString(strLocale);
/* 224 */       WTypes.BSTR strAuthorityBSTR = OleAuto.INSTANCE.SysAllocString(strAuthority);
/*     */       
/* 226 */       PointerByReference pbr = new PointerByReference();
/*     */       
/*     */       try {
/* 229 */         WinNT.HRESULT result = ConnectServer(strNetworkResourceBSTR, strUserBSTR, strPasswordBSTR, strLocaleBSTR, lSecurityFlags, strAuthorityBSTR, pCtx, pbr);
/*     */ 
/*     */         
/* 232 */         COMUtils.checkRC(result);
/*     */         
/* 234 */         return new Wbemcli.IWbemServices(pbr.getValue());
/*     */       } finally {
/* 236 */         OleAuto.INSTANCE.SysFreeString(strNetworkResourceBSTR);
/* 237 */         OleAuto.INSTANCE.SysFreeString(strUserBSTR);
/* 238 */         OleAuto.INSTANCE.SysFreeString(strPasswordBSTR);
/* 239 */         OleAuto.INSTANCE.SysFreeString(strLocaleBSTR);
/* 240 */         OleAuto.INSTANCE.SysFreeString(strAuthorityBSTR);
/*     */       } 
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static class IWbemServices
/*     */     extends Unknown
/*     */   {
/*     */     public IWbemServices() {}
/*     */ 
/*     */ 
/*     */     
/*     */     public IWbemServices(Pointer pvInstance) {
/* 255 */       super(pvInstance);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public WinNT.HRESULT ExecQuery(WTypes.BSTR strQueryLanguage, WTypes.BSTR strQuery, int lFlags, Wbemcli.IWbemContext pCtx, PointerByReference ppEnum) {
/* 261 */       return (WinNT.HRESULT)_invokeNativeObject(20, new Object[] {
/* 262 */             getPointer(), strQueryLanguage, strQuery, Integer.valueOf(lFlags), pCtx, ppEnum }, WinNT.HRESULT.class);
/*     */     }
/*     */     
/*     */     public Wbemcli.IEnumWbemClassObject ExecQuery(String strQueryLanguage, String strQuery, int lFlags, Wbemcli.IWbemContext pCtx) {
/* 266 */       WTypes.BSTR strQueryLanguageBSTR = OleAuto.INSTANCE.SysAllocString(strQueryLanguage);
/* 267 */       WTypes.BSTR strQueryBSTR = OleAuto.INSTANCE.SysAllocString(strQuery);
/*     */       try {
/* 269 */         PointerByReference pbr = new PointerByReference();
/*     */         
/* 271 */         WinNT.HRESULT res = ExecQuery(strQueryLanguageBSTR, strQueryBSTR, lFlags, pCtx, pbr);
/*     */         
/* 273 */         COMUtils.checkRC(res);
/*     */         
/* 275 */         return new Wbemcli.IEnumWbemClassObject(pbr.getValue());
/*     */       } finally {
/* 277 */         OleAuto.INSTANCE.SysFreeString(strQueryLanguageBSTR);
/* 278 */         OleAuto.INSTANCE.SysFreeString(strQueryBSTR);
/*     */       } 
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static class IWbemContext
/*     */     extends Unknown
/*     */   {
/*     */     public IWbemContext() {}
/*     */ 
/*     */ 
/*     */     
/*     */     public IWbemContext(Pointer pvInstance) {
/* 293 */       super(pvInstance);
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\jna\platform\win32\COM\Wbemcli.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */