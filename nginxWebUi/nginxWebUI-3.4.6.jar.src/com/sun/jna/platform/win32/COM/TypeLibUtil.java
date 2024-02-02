/*     */ package com.sun.jna.platform.win32.COM;
/*     */ 
/*     */ import com.sun.jna.Native;
/*     */ import com.sun.jna.Pointer;
/*     */ import com.sun.jna.platform.win32.Guid;
/*     */ import com.sun.jna.platform.win32.Kernel32;
/*     */ import com.sun.jna.platform.win32.OaIdl;
/*     */ import com.sun.jna.platform.win32.Ole32;
/*     */ import com.sun.jna.platform.win32.OleAuto;
/*     */ import com.sun.jna.platform.win32.WTypes;
/*     */ import com.sun.jna.platform.win32.WinDef;
/*     */ import com.sun.jna.platform.win32.WinNT;
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
/*     */ public class TypeLibUtil
/*     */ {
/*  56 */   public static final OleAuto OLEAUTO = OleAuto.INSTANCE;
/*     */ 
/*     */   
/*     */   private ITypeLib typelib;
/*     */ 
/*     */   
/*  62 */   private WinDef.LCID lcid = Kernel32.INSTANCE.GetUserDefaultLCID();
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private String name;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private String docString;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private int helpContext;
/*     */ 
/*     */ 
/*     */   
/*     */   private String helpFile;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public TypeLibUtil(String clsidStr, int wVerMajor, int wVerMinor) {
/*  87 */     Guid.CLSID.ByReference clsid = new Guid.CLSID.ByReference();
/*     */     
/*  89 */     WinNT.HRESULT hr = Ole32.INSTANCE.CLSIDFromString(clsidStr, clsid);
/*  90 */     COMUtils.checkRC(hr);
/*     */ 
/*     */     
/*  93 */     PointerByReference pTypeLib = new PointerByReference();
/*  94 */     hr = OleAuto.INSTANCE.LoadRegTypeLib((Guid.GUID)clsid, wVerMajor, wVerMinor, this.lcid, pTypeLib);
/*     */     
/*  96 */     COMUtils.checkRC(hr);
/*     */ 
/*     */     
/*  99 */     this.typelib = new TypeLib(pTypeLib.getValue());
/*     */     
/* 101 */     initTypeLibInfo();
/*     */   }
/*     */ 
/*     */   
/*     */   public TypeLibUtil(String file) {
/* 106 */     PointerByReference pTypeLib = new PointerByReference();
/* 107 */     WinNT.HRESULT hr = OleAuto.INSTANCE.LoadTypeLib(file, pTypeLib);
/* 108 */     COMUtils.checkRC(hr);
/*     */ 
/*     */     
/* 111 */     this.typelib = new TypeLib(pTypeLib.getValue());
/*     */     
/* 113 */     initTypeLibInfo();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void initTypeLibInfo() {
/* 120 */     TypeLibDoc documentation = getDocumentation(-1);
/* 121 */     this.name = documentation.getName();
/* 122 */     this.docString = documentation.getDocString();
/* 123 */     this.helpContext = documentation.getHelpContext();
/* 124 */     this.helpFile = documentation.getHelpFile();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getTypeInfoCount() {
/* 133 */     return this.typelib.GetTypeInfoCount().intValue();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public OaIdl.TYPEKIND getTypeInfoType(int index) {
/* 144 */     OaIdl.TYPEKIND.ByReference typekind = new OaIdl.TYPEKIND.ByReference();
/* 145 */     WinNT.HRESULT hr = this.typelib.GetTypeInfoType(new WinDef.UINT(index), typekind);
/* 146 */     COMUtils.checkRC(hr);
/* 147 */     return (OaIdl.TYPEKIND)typekind;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ITypeInfo getTypeInfo(int index) {
/* 158 */     PointerByReference ppTInfo = new PointerByReference();
/* 159 */     WinNT.HRESULT hr = this.typelib.GetTypeInfo(new WinDef.UINT(index), ppTInfo);
/* 160 */     COMUtils.checkRC(hr);
/* 161 */     return new TypeInfo(ppTInfo.getValue());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public TypeInfoUtil getTypeInfoUtil(int index) {
/* 172 */     return new TypeInfoUtil(getTypeInfo(index));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public OaIdl.TLIBATTR getLibAttr() {
/* 181 */     PointerByReference ppTLibAttr = new PointerByReference();
/* 182 */     WinNT.HRESULT hr = this.typelib.GetLibAttr(ppTLibAttr);
/* 183 */     COMUtils.checkRC(hr);
/*     */     
/* 185 */     return new OaIdl.TLIBATTR(ppTLibAttr.getValue());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public TypeComp GetTypeComp() {
/* 194 */     PointerByReference ppTComp = new PointerByReference();
/* 195 */     WinNT.HRESULT hr = this.typelib.GetTypeComp(ppTComp);
/* 196 */     COMUtils.checkRC(hr);
/*     */     
/* 198 */     return new TypeComp(ppTComp.getValue());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public TypeLibDoc getDocumentation(int index) {
/* 209 */     WTypes.BSTRByReference pBstrName = new WTypes.BSTRByReference();
/* 210 */     WTypes.BSTRByReference pBstrDocString = new WTypes.BSTRByReference();
/* 211 */     WinDef.DWORDByReference pdwHelpContext = new WinDef.DWORDByReference();
/* 212 */     WTypes.BSTRByReference pBstrHelpFile = new WTypes.BSTRByReference();
/*     */     
/* 214 */     WinNT.HRESULT hr = this.typelib.GetDocumentation(index, pBstrName, pBstrDocString, pdwHelpContext, pBstrHelpFile);
/*     */     
/* 216 */     COMUtils.checkRC(hr);
/*     */ 
/*     */ 
/*     */     
/* 220 */     TypeLibDoc typeLibDoc = new TypeLibDoc(pBstrName.getString(), pBstrDocString.getString(), pdwHelpContext.getValue().intValue(), pBstrHelpFile.getString());
/*     */     
/* 222 */     OLEAUTO.SysFreeString(pBstrName.getValue());
/* 223 */     OLEAUTO.SysFreeString(pBstrDocString.getValue());
/* 224 */     OLEAUTO.SysFreeString(pBstrHelpFile.getValue());
/*     */     
/* 226 */     return typeLibDoc;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static class TypeLibDoc
/*     */   {
/*     */     private String name;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     private String docString;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     private int helpContext;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     private String helpFile;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public TypeLibDoc(String name, String docString, int helpContext, String helpFile) {
/* 262 */       this.name = name;
/* 263 */       this.docString = docString;
/* 264 */       this.helpContext = helpContext;
/* 265 */       this.helpFile = helpFile;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public String getName() {
/* 274 */       return this.name;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public String getDocString() {
/* 283 */       return this.docString;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public int getHelpContext() {
/* 292 */       return this.helpContext;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public String getHelpFile() {
/* 301 */       return this.helpFile;
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
/*     */ 
/*     */ 
/*     */   
/*     */   public IsName IsName(String nameBuf, int hashVal) {
/* 316 */     WTypes.LPOLESTR szNameBuf = new WTypes.LPOLESTR(nameBuf);
/* 317 */     WinDef.ULONG lHashVal = new WinDef.ULONG(hashVal);
/* 318 */     WinDef.BOOLByReference pfName = new WinDef.BOOLByReference();
/*     */     
/* 320 */     WinNT.HRESULT hr = this.typelib.IsName(szNameBuf, lHashVal, pfName);
/* 321 */     COMUtils.checkRC(hr);
/*     */     
/* 323 */     return new IsName(szNameBuf.getValue(), pfName.getValue()
/* 324 */         .booleanValue());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static class IsName
/*     */   {
/*     */     private String nameBuf;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     private boolean name;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public IsName(String nameBuf, boolean name) {
/* 349 */       this.nameBuf = nameBuf;
/* 350 */       this.name = name;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public String getNameBuf() {
/* 359 */       return this.nameBuf;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public boolean isName() {
/* 368 */       return this.name;
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
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public FindName FindName(String name, int hashVal, short maxResult) {
/* 384 */     Pointer p = Ole32.INSTANCE.CoTaskMemAlloc((name.length() + 1L) * Native.WCHAR_SIZE);
/* 385 */     WTypes.LPOLESTR olestr = new WTypes.LPOLESTR(p);
/* 386 */     olestr.setValue(name);
/*     */     
/* 388 */     WinDef.ULONG lHashVal = new WinDef.ULONG(hashVal);
/* 389 */     WinDef.USHORTByReference pcFound = new WinDef.USHORTByReference(maxResult);
/*     */     
/* 391 */     Pointer[] ppTInfo = new Pointer[maxResult];
/* 392 */     OaIdl.MEMBERID[] rgMemId = new OaIdl.MEMBERID[maxResult];
/* 393 */     WinNT.HRESULT hr = this.typelib.FindName(olestr, lHashVal, ppTInfo, rgMemId, pcFound);
/*     */     
/* 395 */     COMUtils.checkRC(hr);
/*     */ 
/*     */     
/* 398 */     FindName findName = new FindName(olestr.getValue(), ppTInfo, rgMemId, pcFound.getValue().shortValue());
/*     */     
/* 400 */     Ole32.INSTANCE.CoTaskMemFree(p);
/*     */     
/* 402 */     return findName;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static class FindName
/*     */   {
/*     */     private String nameBuf;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     private Pointer[] pTInfo;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     private OaIdl.MEMBERID[] rgMemId;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     private short pcFound;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     FindName(String nameBuf, Pointer[] pTInfo, OaIdl.MEMBERID[] rgMemId, short pcFound) {
/* 436 */       this.nameBuf = nameBuf;
/* 437 */       this.pTInfo = new Pointer[pcFound];
/* 438 */       this.rgMemId = new OaIdl.MEMBERID[pcFound];
/* 439 */       this.pcFound = pcFound;
/* 440 */       System.arraycopy(pTInfo, 0, this.pTInfo, 0, pcFound);
/* 441 */       System.arraycopy(rgMemId, 0, this.rgMemId, 0, pcFound);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public String getNameBuf() {
/* 450 */       return this.nameBuf;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public ITypeInfo[] getTInfo() {
/* 459 */       ITypeInfo[] values = new ITypeInfo[this.pcFound];
/* 460 */       for (int i = 0; i < this.pcFound; i++)
/*     */       {
/* 462 */         values[i] = new TypeInfo(this.pTInfo[i]);
/*     */       }
/* 464 */       return values;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public OaIdl.MEMBERID[] getMemId() {
/* 473 */       return this.rgMemId;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public short getFound() {
/* 482 */       return this.pcFound;
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void ReleaseTLibAttr(OaIdl.TLIBATTR pTLibAttr) {
/* 493 */     this.typelib.ReleaseTLibAttr(pTLibAttr);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public WinDef.LCID getLcid() {
/* 502 */     return this.lcid;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ITypeLib getTypelib() {
/* 511 */     return this.typelib;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getName() {
/* 520 */     return this.name;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getDocString() {
/* 529 */     return this.docString;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getHelpContext() {
/* 538 */     return this.helpContext;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getHelpFile() {
/* 547 */     return this.helpFile;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\jna\platform\win32\COM\TypeLibUtil.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */