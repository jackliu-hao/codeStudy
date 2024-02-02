/*     */ package com.sun.jna.platform.win32.COM.tlb.imp;
/*     */ 
/*     */ import com.sun.jna.Pointer;
/*     */ import com.sun.jna.platform.win32.COM.IDispatch;
/*     */ import com.sun.jna.platform.win32.COM.ITypeInfo;
/*     */ import com.sun.jna.platform.win32.COM.IUnknown;
/*     */ import com.sun.jna.platform.win32.COM.TypeInfoUtil;
/*     */ import com.sun.jna.platform.win32.COM.TypeLibUtil;
/*     */ import com.sun.jna.platform.win32.Guid;
/*     */ import com.sun.jna.platform.win32.OaIdl;
/*     */ import com.sun.jna.platform.win32.Variant;
/*     */ import com.sun.jna.platform.win32.WTypes;
/*     */ import com.sun.jna.platform.win32.WinBase;
/*     */ import com.sun.jna.platform.win32.WinDef;
/*     */ import com.sun.jna.platform.win32.WinNT;
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
/*     */ public abstract class TlbAbstractMethod
/*     */   extends TlbBase
/*     */   implements Variant
/*     */ {
/*     */   protected TypeInfoUtil.TypeInfoDoc typeInfoDoc;
/*     */   protected String methodName;
/*     */   protected String docStr;
/*     */   protected short vtableId;
/*     */   protected OaIdl.MEMBERID memberid;
/*     */   protected short paramCount;
/*     */   protected String returnType;
/*  84 */   protected String methodparams = "";
/*     */   
/*  86 */   protected String methodvariables = "";
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
/*     */   public TlbAbstractMethod(int index, TypeLibUtil typeLibUtil, OaIdl.FUNCDESC funcDesc, TypeInfoUtil typeInfoUtil) {
/* 102 */     super(index, typeLibUtil, typeInfoUtil);
/* 103 */     this.typeInfoDoc = typeInfoUtil.getDocumentation(funcDesc.memid);
/* 104 */     this.methodName = this.typeInfoDoc.getName();
/* 105 */     this.docStr = this.typeInfoDoc.getDocString();
/*     */ 
/*     */     
/* 108 */     this.vtableId = funcDesc.oVft.shortValue();
/* 109 */     this.memberid = funcDesc.memid;
/* 110 */     this.paramCount = funcDesc.cParams.shortValue();
/* 111 */     this.returnType = getType(funcDesc);
/*     */   }
/*     */   
/*     */   public TypeInfoUtil.TypeInfoDoc getTypeInfoDoc() {
/* 115 */     return this.typeInfoDoc;
/*     */   }
/*     */   
/*     */   public String getMethodName() {
/* 119 */     return this.methodName;
/*     */   }
/*     */   
/*     */   public String getDocStr() {
/* 123 */     return this.docStr;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected String getVarType(WTypes.VARTYPE vt) {
/* 134 */     switch (vt.intValue()) {
/*     */       case 0:
/* 136 */         return "";
/*     */       case 1:
/* 138 */         return "null";
/*     */       case 2:
/* 140 */         return "short";
/*     */       case 3:
/* 142 */         return "int";
/*     */       case 4:
/* 144 */         return "float";
/*     */       case 5:
/* 146 */         return "double";
/*     */       case 6:
/* 148 */         return OaIdl.CURRENCY.class.getSimpleName();
/*     */       case 7:
/* 150 */         return OaIdl.DATE.class.getSimpleName();
/*     */       case 8:
/* 152 */         return WTypes.BSTR.class.getSimpleName();
/*     */       case 9:
/* 154 */         return IDispatch.class.getSimpleName();
/*     */       case 10:
/* 156 */         return WinDef.SCODE.class.getSimpleName();
/*     */       case 11:
/* 158 */         return WinDef.BOOL.class.getSimpleName();
/*     */       case 12:
/* 160 */         return Variant.VARIANT.class.getSimpleName();
/*     */       case 13:
/* 162 */         return IUnknown.class.getSimpleName();
/*     */       case 14:
/* 164 */         return OaIdl.DECIMAL.class.getSimpleName();
/*     */       case 16:
/* 166 */         return WinDef.CHAR.class.getSimpleName();
/*     */       case 17:
/* 168 */         return WinDef.UCHAR.class.getSimpleName();
/*     */       case 18:
/* 170 */         return WinDef.USHORT.class.getSimpleName();
/*     */       case 19:
/* 172 */         return WinDef.UINT.class.getSimpleName();
/*     */       case 20:
/* 174 */         return WinDef.LONG.class.getSimpleName();
/*     */       case 21:
/* 176 */         return WinDef.ULONG.class.getSimpleName();
/*     */       case 22:
/* 178 */         return "int";
/*     */       case 23:
/* 180 */         return WinDef.UINT.class.getSimpleName();
/*     */       case 24:
/* 182 */         return WinDef.PVOID.class.getSimpleName();
/*     */       case 25:
/* 184 */         return WinNT.HRESULT.class.getSimpleName();
/*     */       case 26:
/* 186 */         return Pointer.class.getSimpleName();
/*     */       case 27:
/* 188 */         return "safearray";
/*     */       case 28:
/* 190 */         return "carray";
/*     */       case 29:
/* 192 */         return "userdefined";
/*     */       case 30:
/* 194 */         return WTypes.LPSTR.class.getSimpleName();
/*     */       case 31:
/* 196 */         return WTypes.LPWSTR.class.getSimpleName();
/*     */       case 36:
/* 198 */         return "record";
/*     */       case 37:
/* 200 */         return WinDef.INT_PTR.class.getSimpleName();
/*     */       case 38:
/* 202 */         return WinDef.UINT_PTR.class.getSimpleName();
/*     */       case 64:
/* 204 */         return WinBase.FILETIME.class.getSimpleName();
/*     */       case 66:
/* 206 */         return "steam";
/*     */       case 67:
/* 208 */         return "storage";
/*     */       case 68:
/* 210 */         return "steamed_object";
/*     */       case 69:
/* 212 */         return "stored_object";
/*     */       case 70:
/* 214 */         return "blob_object";
/*     */       case 71:
/* 216 */         return "cf";
/*     */       case 72:
/* 218 */         return Guid.CLSID.class.getSimpleName();
/*     */       case 73:
/* 220 */         return "";
/*     */ 
/*     */       
/*     */       case 4096:
/* 224 */         return "";
/*     */       case 8192:
/* 226 */         return "";
/*     */       case 16384:
/* 228 */         return WinDef.PVOID.class.getSimpleName();
/*     */       case 32768:
/* 230 */         return "";
/*     */       case 65535:
/* 232 */         return "illegal";
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 238 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   protected String getUserdefinedType(OaIdl.HREFTYPE hreftype) {
/* 243 */     ITypeInfo refTypeInfo = this.typeInfoUtil.getRefTypeInfo(hreftype);
/* 244 */     TypeInfoUtil typeInfoUtil = new TypeInfoUtil(refTypeInfo);
/*     */     
/* 246 */     TypeInfoUtil.TypeInfoDoc documentation = typeInfoUtil.getDocumentation(OaIdl.MEMBERID_NIL);
/* 247 */     return documentation.getName();
/*     */   }
/*     */   
/*     */   protected String getType(OaIdl.FUNCDESC funcDesc) {
/* 251 */     OaIdl.ELEMDESC elemDesc = funcDesc.elemdescFunc;
/* 252 */     return getType(elemDesc);
/*     */   }
/*     */   
/*     */   protected String getType(OaIdl.ELEMDESC elemDesc) {
/* 256 */     OaIdl.TYPEDESC _typeDesc = elemDesc.tdesc;
/* 257 */     return getType(_typeDesc);
/*     */   }
/*     */   
/*     */   protected String getType(OaIdl.TYPEDESC typeDesc) {
/* 261 */     WTypes.VARTYPE vt = typeDesc.vt;
/* 262 */     String type = "not_defined";
/*     */     
/* 264 */     if (vt.intValue() == 26) {
/* 265 */       OaIdl.TYPEDESC.ByReference byReference = typeDesc._typedesc.getLptdesc();
/* 266 */       type = getType((OaIdl.TYPEDESC)byReference);
/* 267 */     } else if (vt.intValue() == 27 || vt
/* 268 */       .intValue() == 28) {
/* 269 */       OaIdl.TYPEDESC tdescElem = (typeDesc._typedesc.getLpadesc()).tdescElem;
/* 270 */       type = getType(tdescElem);
/* 271 */     } else if (vt.intValue() == 29) {
/* 272 */       OaIdl.HREFTYPE hreftype = typeDesc._typedesc.hreftype;
/* 273 */       type = getUserdefinedType(hreftype);
/*     */     } else {
/* 275 */       type = getVarType(vt);
/*     */     } 
/*     */     
/* 278 */     return type;
/*     */   }
/*     */   
/*     */   protected String replaceJavaKeyword(String name) {
/* 282 */     if (name.equals("final"))
/* 283 */       return "_" + name; 
/* 284 */     if (name.equals("default"))
/* 285 */       return "_" + name; 
/* 286 */     if (name.equals("case"))
/* 287 */       return "_" + name; 
/* 288 */     if (name.equals("char"))
/* 289 */       return "_" + name; 
/* 290 */     if (name.equals("private"))
/* 291 */       return "_" + name; 
/* 292 */     if (name.equals("default")) {
/* 293 */       return "_" + name;
/*     */     }
/* 295 */     return name;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\jna\platform\win32\COM\tlb\imp\TlbAbstractMethod.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */