/*     */ package com.sun.jna.platform.win32.COM;
/*     */ 
/*     */ import com.sun.jna.platform.win32.Guid;
/*     */ import com.sun.jna.platform.win32.OaIdl;
/*     */ import com.sun.jna.platform.win32.OleAuto;
/*     */ import com.sun.jna.platform.win32.Variant;
/*     */ import com.sun.jna.platform.win32.WTypes;
/*     */ import com.sun.jna.platform.win32.WinDef;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class TypeInfoUtil
/*     */ {
/*  60 */   public static final OleAuto OLEAUTO = OleAuto.INSTANCE;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private ITypeInfo typeInfo;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public TypeInfoUtil(ITypeInfo typeInfo) {
/*  72 */     this.typeInfo = typeInfo;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public OaIdl.TYPEATTR getTypeAttr() {
/*  81 */     PointerByReference ppTypeAttr = new PointerByReference();
/*  82 */     WinNT.HRESULT hr = this.typeInfo.GetTypeAttr(ppTypeAttr);
/*  83 */     COMUtils.checkRC(hr);
/*     */     
/*  85 */     return new OaIdl.TYPEATTR(ppTypeAttr.getValue());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public TypeComp getTypeComp() {
/*  94 */     PointerByReference ppTypeAttr = new PointerByReference();
/*  95 */     WinNT.HRESULT hr = this.typeInfo.GetTypeComp(ppTypeAttr);
/*  96 */     COMUtils.checkRC(hr);
/*     */     
/*  98 */     return new TypeComp(ppTypeAttr.getValue());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public OaIdl.FUNCDESC getFuncDesc(int index) {
/* 109 */     PointerByReference ppFuncDesc = new PointerByReference();
/* 110 */     WinNT.HRESULT hr = this.typeInfo.GetFuncDesc(new WinDef.UINT(index), ppFuncDesc);
/* 111 */     COMUtils.checkRC(hr);
/*     */     
/* 113 */     return new OaIdl.FUNCDESC(ppFuncDesc.getValue());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public OaIdl.VARDESC getVarDesc(int index) {
/* 124 */     PointerByReference ppVarDesc = new PointerByReference();
/* 125 */     WinNT.HRESULT hr = this.typeInfo.GetVarDesc(new WinDef.UINT(index), ppVarDesc);
/* 126 */     COMUtils.checkRC(hr);
/*     */     
/* 128 */     return new OaIdl.VARDESC(ppVarDesc.getValue());
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
/*     */   public String[] getNames(OaIdl.MEMBERID memid, int maxNames) {
/* 141 */     WTypes.BSTR[] rgBstrNames = new WTypes.BSTR[maxNames];
/* 142 */     WinDef.UINTByReference pcNames = new WinDef.UINTByReference();
/* 143 */     WinNT.HRESULT hr = this.typeInfo.GetNames(memid, rgBstrNames, new WinDef.UINT(maxNames), pcNames);
/*     */     
/* 145 */     COMUtils.checkRC(hr);
/*     */     
/* 147 */     int cNames = pcNames.getValue().intValue();
/* 148 */     String[] result = new String[cNames];
/*     */     
/* 150 */     for (int i = 0; i < result.length; i++) {
/* 151 */       result[i] = rgBstrNames[i].getValue();
/* 152 */       OLEAUTO.SysFreeString(rgBstrNames[i]);
/*     */     } 
/*     */     
/* 155 */     return result;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public OaIdl.HREFTYPE getRefTypeOfImplType(int index) {
/* 166 */     OaIdl.HREFTYPEByReference ppTInfo = new OaIdl.HREFTYPEByReference();
/* 167 */     WinNT.HRESULT hr = this.typeInfo.GetRefTypeOfImplType(new WinDef.UINT(index), ppTInfo);
/*     */     
/* 169 */     COMUtils.checkRC(hr);
/*     */     
/* 171 */     return ppTInfo.getValue();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getImplTypeFlags(int index) {
/* 182 */     IntByReference pImplTypeFlags = new IntByReference();
/* 183 */     WinNT.HRESULT hr = this.typeInfo.GetImplTypeFlags(new WinDef.UINT(index), pImplTypeFlags);
/*     */     
/* 185 */     COMUtils.checkRC(hr);
/*     */     
/* 187 */     return pImplTypeFlags.getValue();
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
/*     */   public OaIdl.MEMBERID[] getIDsOfNames(WTypes.LPOLESTR[] rgszNames, int cNames) {
/* 200 */     OaIdl.MEMBERID[] pMemId = new OaIdl.MEMBERID[cNames];
/* 201 */     WinNT.HRESULT hr = this.typeInfo.GetIDsOfNames(rgszNames, new WinDef.UINT(cNames), pMemId);
/*     */     
/* 203 */     COMUtils.checkRC(hr);
/*     */     
/* 205 */     return pMemId;
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
/*     */   public Invoke Invoke(WinDef.PVOID pvInstance, OaIdl.MEMBERID memid, WinDef.WORD wFlags, OleAuto.DISPPARAMS.ByReference pDispParams) {
/* 224 */     Variant.VARIANT.ByReference pVarResult = new Variant.VARIANT.ByReference();
/* 225 */     OaIdl.EXCEPINFO.ByReference pExcepInfo = new OaIdl.EXCEPINFO.ByReference();
/* 226 */     WinDef.UINTByReference puArgErr = new WinDef.UINTByReference();
/*     */     
/* 228 */     WinNT.HRESULT hr = this.typeInfo.Invoke(pvInstance, memid, wFlags, pDispParams, pVarResult, pExcepInfo, puArgErr);
/*     */     
/* 230 */     COMUtils.checkRC(hr);
/*     */     
/* 232 */     return new Invoke(pVarResult, pExcepInfo, puArgErr.getValue()
/* 233 */         .intValue());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static class Invoke
/*     */   {
/*     */     private Variant.VARIANT.ByReference pVarResult;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     private OaIdl.EXCEPINFO.ByReference pExcepInfo;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     private int puArgErr;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Invoke(Variant.VARIANT.ByReference pVarResult, OaIdl.EXCEPINFO.ByReference pExcepInfo, int puArgErr) {
/* 264 */       this.pVarResult = pVarResult;
/* 265 */       this.pExcepInfo = pExcepInfo;
/* 266 */       this.puArgErr = puArgErr;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Variant.VARIANT.ByReference getpVarResult() {
/* 275 */       return this.pVarResult;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public OaIdl.EXCEPINFO.ByReference getpExcepInfo() {
/* 284 */       return this.pExcepInfo;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public int getPuArgErr() {
/* 293 */       return this.puArgErr;
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
/*     */   public TypeInfoDoc getDocumentation(OaIdl.MEMBERID memid) {
/* 305 */     WTypes.BSTRByReference pBstrName = new WTypes.BSTRByReference();
/* 306 */     WTypes.BSTRByReference pBstrDocString = new WTypes.BSTRByReference();
/* 307 */     WinDef.DWORDByReference pdwHelpContext = new WinDef.DWORDByReference();
/* 308 */     WTypes.BSTRByReference pBstrHelpFile = new WTypes.BSTRByReference();
/*     */     
/* 310 */     WinNT.HRESULT hr = this.typeInfo.GetDocumentation(memid, pBstrName, pBstrDocString, pdwHelpContext, pBstrHelpFile);
/*     */     
/* 312 */     COMUtils.checkRC(hr);
/*     */ 
/*     */ 
/*     */     
/* 316 */     TypeInfoDoc TypeInfoDoc = new TypeInfoDoc(pBstrName.getString(), pBstrDocString.getString(), pdwHelpContext.getValue().intValue(), pBstrHelpFile.getString());
/*     */     
/* 318 */     OLEAUTO.SysFreeString(pBstrName.getValue());
/* 319 */     OLEAUTO.SysFreeString(pBstrDocString.getValue());
/* 320 */     OLEAUTO.SysFreeString(pBstrHelpFile.getValue());
/*     */     
/* 322 */     return TypeInfoDoc;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static class TypeInfoDoc
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
/*     */     public TypeInfoDoc(String name, String docString, int helpContext, String helpFile) {
/* 358 */       this.name = name;
/* 359 */       this.docString = docString;
/* 360 */       this.helpContext = helpContext;
/* 361 */       this.helpFile = helpFile;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public String getName() {
/* 370 */       return this.name;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public String getDocString() {
/* 379 */       return this.docString;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public int getHelpContext() {
/* 388 */       return this.helpContext;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public String getHelpFile() {
/* 397 */       return this.helpFile;
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
/*     */   public DllEntry GetDllEntry(OaIdl.MEMBERID memid, OaIdl.INVOKEKIND invKind) {
/* 411 */     WTypes.BSTRByReference pBstrDllName = new WTypes.BSTRByReference();
/* 412 */     WTypes.BSTRByReference pBstrName = new WTypes.BSTRByReference();
/* 413 */     WinDef.WORDByReference pwOrdinal = new WinDef.WORDByReference();
/*     */     
/* 415 */     WinNT.HRESULT hr = this.typeInfo.GetDllEntry(memid, invKind, pBstrDllName, pBstrName, pwOrdinal);
/*     */     
/* 417 */     COMUtils.checkRC(hr);
/*     */     
/* 419 */     OLEAUTO.SysFreeString(pBstrDllName.getValue());
/* 420 */     OLEAUTO.SysFreeString(pBstrName.getValue());
/*     */     
/* 422 */     return new DllEntry(pBstrDllName.getString(), pBstrName.getString(), pwOrdinal
/* 423 */         .getValue().intValue());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static class DllEntry
/*     */   {
/*     */     private String dllName;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     private String name;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     private int ordinal;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public DllEntry(String dllName, String name, int ordinal) {
/* 453 */       this.dllName = dllName;
/* 454 */       this.name = name;
/* 455 */       this.ordinal = ordinal;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public String getDllName() {
/* 464 */       return this.dllName;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public void setDllName(String dllName) {
/* 474 */       this.dllName = dllName;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public String getName() {
/* 483 */       return this.name;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public void setName(String name) {
/* 493 */       this.name = name;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public int getOrdinal() {
/* 502 */       return this.ordinal;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public void setOrdinal(int ordinal) {
/* 512 */       this.ordinal = ordinal;
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
/*     */   public ITypeInfo getRefTypeInfo(OaIdl.HREFTYPE hreftype) {
/* 524 */     PointerByReference ppTInfo = new PointerByReference();
/* 525 */     WinNT.HRESULT hr = this.typeInfo.GetRefTypeInfo(hreftype, ppTInfo);
/* 526 */     COMUtils.checkRC(hr);
/*     */     
/* 528 */     return new TypeInfo(ppTInfo.getValue());
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
/*     */   public PointerByReference AddressOfMember(OaIdl.MEMBERID memid, OaIdl.INVOKEKIND invKind) {
/* 541 */     PointerByReference ppv = new PointerByReference();
/* 542 */     WinNT.HRESULT hr = this.typeInfo.AddressOfMember(memid, invKind, ppv);
/* 543 */     COMUtils.checkRC(hr);
/*     */     
/* 545 */     return ppv;
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
/*     */   public PointerByReference CreateInstance(IUnknown pUnkOuter, Guid.REFIID riid) {
/* 558 */     PointerByReference ppvObj = new PointerByReference();
/* 559 */     WinNT.HRESULT hr = this.typeInfo.CreateInstance(pUnkOuter, riid, ppvObj);
/* 560 */     COMUtils.checkRC(hr);
/*     */     
/* 562 */     return ppvObj;
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
/*     */   public String GetMops(OaIdl.MEMBERID memid) {
/* 574 */     WTypes.BSTRByReference pBstrMops = new WTypes.BSTRByReference();
/* 575 */     WinNT.HRESULT hr = this.typeInfo.GetMops(memid, pBstrMops);
/* 576 */     COMUtils.checkRC(hr);
/*     */     
/* 578 */     return pBstrMops.getString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ContainingTypeLib GetContainingTypeLib() {
/* 588 */     PointerByReference ppTLib = new PointerByReference();
/* 589 */     WinDef.UINTByReference pIndex = new WinDef.UINTByReference();
/*     */     
/* 591 */     WinNT.HRESULT hr = this.typeInfo.GetContainingTypeLib(ppTLib, pIndex);
/* 592 */     COMUtils.checkRC(hr);
/*     */     
/* 594 */     return new ContainingTypeLib(new TypeLib(ppTLib.getValue()), pIndex
/* 595 */         .getValue().intValue());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static class ContainingTypeLib
/*     */   {
/*     */     private ITypeLib typeLib;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     private int index;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public ContainingTypeLib(ITypeLib typeLib, int index) {
/* 620 */       this.typeLib = typeLib;
/* 621 */       this.index = index;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public ITypeLib getTypeLib() {
/* 630 */       return this.typeLib;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public void setTypeLib(ITypeLib typeLib) {
/* 640 */       this.typeLib = typeLib;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public int getIndex() {
/* 649 */       return this.index;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public void setIndex(int index) {
/* 659 */       this.index = index;
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void ReleaseTypeAttr(OaIdl.TYPEATTR pTypeAttr) {
/* 670 */     this.typeInfo.ReleaseTypeAttr(pTypeAttr);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void ReleaseFuncDesc(OaIdl.FUNCDESC pFuncDesc) {
/* 680 */     this.typeInfo.ReleaseFuncDesc(pFuncDesc);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void ReleaseVarDesc(OaIdl.VARDESC pVarDesc) {
/* 690 */     this.typeInfo.ReleaseVarDesc(pVarDesc);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\jna\platform\win32\COM\TypeInfoUtil.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */