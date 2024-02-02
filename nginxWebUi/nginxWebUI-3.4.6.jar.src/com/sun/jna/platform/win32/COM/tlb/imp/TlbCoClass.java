/*     */ package com.sun.jna.platform.win32.COM.tlb.imp;
/*     */ 
/*     */ import com.sun.jna.platform.win32.COM.ITypeInfo;
/*     */ import com.sun.jna.platform.win32.COM.TypeInfoUtil;
/*     */ import com.sun.jna.platform.win32.COM.TypeLibUtil;
/*     */ import com.sun.jna.platform.win32.OaIdl;
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
/*     */ public class TlbCoClass
/*     */   extends TlbBase
/*     */ {
/*     */   public TlbCoClass(int index, String packagename, TypeLibUtil typeLibUtil, String bindingMode) {
/*  54 */     super(index, typeLibUtil, null);
/*     */     
/*  56 */     TypeInfoUtil typeInfoUtil = typeLibUtil.getTypeInfoUtil(index);
/*     */     
/*  58 */     TypeLibUtil.TypeLibDoc typeLibDoc = this.typeLibUtil.getDocumentation(index);
/*  59 */     String docString = typeLibDoc.getDocString();
/*     */     
/*  61 */     if (typeLibDoc.getName().length() > 0) {
/*  62 */       this.name = typeLibDoc.getName();
/*     */     }
/*  64 */     logInfo("Type of kind 'CoClass' found: " + this.name);
/*     */     
/*  66 */     createPackageName(packagename);
/*  67 */     createClassName(this.name);
/*  68 */     setFilename(this.name);
/*     */     
/*  70 */     String guidStr = (this.typeLibUtil.getLibAttr()).guid.toGuidString();
/*  71 */     int majorVerNum = (this.typeLibUtil.getLibAttr()).wMajorVerNum.intValue();
/*  72 */     int minorVerNum = (this.typeLibUtil.getLibAttr()).wMinorVerNum.intValue();
/*  73 */     String version = majorVerNum + "." + minorVerNum;
/*  74 */     String clsid = (typeInfoUtil.getTypeAttr()).guid.toGuidString();
/*     */     
/*  76 */     createJavaDocHeader(guidStr, version, docString);
/*  77 */     createCLSID(clsid);
/*  78 */     createCLSIDName(this.name);
/*     */ 
/*     */     
/*  81 */     OaIdl.TYPEATTR typeAttr = typeInfoUtil.getTypeAttr();
/*  82 */     int cImplTypes = typeAttr.cImplTypes.intValue();
/*  83 */     String interfaces = "";
/*     */     
/*  85 */     for (int i = 0; i < cImplTypes; i++) {
/*  86 */       OaIdl.HREFTYPE refTypeOfImplType = typeInfoUtil.getRefTypeOfImplType(i);
/*     */       
/*  88 */       ITypeInfo refTypeInfo = typeInfoUtil.getRefTypeInfo(refTypeOfImplType);
/*  89 */       TypeInfoUtil refTypeInfoUtil = new TypeInfoUtil(refTypeInfo);
/*  90 */       createFunctions(refTypeInfoUtil, bindingMode);
/*     */       
/*  92 */       TypeInfoUtil.TypeInfoDoc documentation = refTypeInfoUtil.getDocumentation(new OaIdl.MEMBERID(-1));
/*  93 */       interfaces = interfaces + documentation.getName();
/*     */       
/*  95 */       if (i < cImplTypes - 1) {
/*  96 */         interfaces = interfaces + ", ";
/*     */       }
/*     */     } 
/*  99 */     createInterfaces(interfaces);
/* 100 */     createContent(this.content);
/*     */   }
/*     */   
/*     */   protected void createFunctions(TypeInfoUtil typeInfoUtil, String bindingMode) {
/* 104 */     OaIdl.TYPEATTR typeAttr = typeInfoUtil.getTypeAttr();
/* 105 */     int cFuncs = typeAttr.cFuncs.intValue();
/* 106 */     for (int i = 0; i < cFuncs; i++) {
/*     */       
/* 108 */       OaIdl.FUNCDESC funcDesc = typeInfoUtil.getFuncDesc(i);
/*     */       
/* 110 */       TlbAbstractMethod method = null;
/* 111 */       if (funcDesc.invkind.value == OaIdl.INVOKEKIND.INVOKE_FUNC.value) {
/* 112 */         if (isVTableMode()) {
/* 113 */           method = new TlbFunctionVTable(i, this.index, this.typeLibUtil, funcDesc, typeInfoUtil);
/*     */         } else {
/* 115 */           method = new TlbFunctionDispId(i, this.index, this.typeLibUtil, funcDesc, typeInfoUtil);
/*     */         } 
/* 117 */       } else if (funcDesc.invkind.value == OaIdl.INVOKEKIND.INVOKE_PROPERTYGET.value) {
/* 118 */         method = new TlbPropertyGet(i, this.index, this.typeLibUtil, funcDesc, typeInfoUtil);
/* 119 */       } else if (funcDesc.invkind.value == OaIdl.INVOKEKIND.INVOKE_PROPERTYPUT.value) {
/* 120 */         method = new TlbPropertyPut(i, this.index, this.typeLibUtil, funcDesc, typeInfoUtil);
/* 121 */       } else if (funcDesc.invkind.value == OaIdl.INVOKEKIND.INVOKE_PROPERTYPUTREF.value) {
/* 122 */         method = new TlbPropertyPut(i, this.index, this.typeLibUtil, funcDesc, typeInfoUtil);
/*     */       } 
/*     */       
/* 125 */       if (!isReservedMethod(method.getMethodName())) {
/*     */         
/* 127 */         this.content += method.getClassBuffer();
/*     */         
/* 129 */         if (i < cFuncs - 1) {
/* 130 */           this.content += "\n";
/*     */         }
/*     */       } 
/*     */       
/* 134 */       typeInfoUtil.ReleaseFuncDesc(funcDesc);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   protected void createJavaDocHeader(String guid, String version, String helpstring) {
/* 140 */     replaceVariable("uuid", guid);
/* 141 */     replaceVariable("version", version);
/* 142 */     replaceVariable("helpstring", helpstring);
/*     */   }
/*     */   
/*     */   protected void createCLSIDName(String clsidName) {
/* 146 */     replaceVariable("clsidname", clsidName.toUpperCase());
/*     */   }
/*     */   
/*     */   protected void createCLSID(String clsid) {
/* 150 */     replaceVariable("clsid", clsid);
/*     */   }
/*     */   
/*     */   protected void createInterfaces(String interfaces) {
/* 154 */     replaceVariable("interfaces", interfaces);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected String getClassTemplate() {
/* 164 */     return "com/sun/jna/platform/win32/COM/tlb/imp/TlbCoClass.template";
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\jna\platform\win32\COM\tlb\imp\TlbCoClass.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */