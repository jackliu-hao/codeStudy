/*     */ package com.sun.jna.platform.win32.COM.tlb.imp;
/*     */ 
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
/*     */ public class TlbDispInterface
/*     */   extends TlbBase
/*     */ {
/*     */   public TlbDispInterface(int index, String packagename, TypeLibUtil typeLibUtil) {
/*  53 */     super(index, typeLibUtil, null);
/*     */     
/*  55 */     TypeLibUtil.TypeLibDoc typeLibDoc = this.typeLibUtil.getDocumentation(index);
/*  56 */     String docString = typeLibDoc.getDocString();
/*     */     
/*  58 */     if (typeLibDoc.getName().length() > 0) {
/*  59 */       this.name = typeLibDoc.getName();
/*     */     }
/*  61 */     logInfo("Type of kind 'DispInterface' found: " + this.name);
/*     */     
/*  63 */     createPackageName(packagename);
/*  64 */     createClassName(this.name);
/*  65 */     setFilename(this.name);
/*     */ 
/*     */     
/*  68 */     TypeInfoUtil typeInfoUtil = typeLibUtil.getTypeInfoUtil(index);
/*  69 */     OaIdl.TYPEATTR typeAttr = typeInfoUtil.getTypeAttr();
/*     */     
/*  71 */     createJavaDocHeader(typeAttr.guid.toGuidString(), docString);
/*     */     
/*  73 */     int cFuncs = typeAttr.cFuncs.intValue();
/*  74 */     for (int i = 0; i < cFuncs; i++) {
/*     */       
/*  76 */       OaIdl.FUNCDESC funcDesc = typeInfoUtil.getFuncDesc(i);
/*     */ 
/*     */       
/*  79 */       OaIdl.MEMBERID memberID = funcDesc.memid;
/*     */ 
/*     */       
/*  82 */       TypeInfoUtil.TypeInfoDoc typeInfoDoc2 = typeInfoUtil.getDocumentation(memberID);
/*  83 */       String methodName = typeInfoDoc2.getName();
/*  84 */       TlbAbstractMethod method = null;
/*     */       
/*  86 */       if (!isReservedMethod(methodName)) {
/*  87 */         if (funcDesc.invkind.value == OaIdl.INVOKEKIND.INVOKE_FUNC.value) {
/*  88 */           method = new TlbFunctionStub(index, typeLibUtil, funcDesc, typeInfoUtil);
/*  89 */         } else if (funcDesc.invkind.value == OaIdl.INVOKEKIND.INVOKE_PROPERTYGET.value) {
/*  90 */           method = new TlbPropertyGetStub(index, typeLibUtil, funcDesc, typeInfoUtil);
/*  91 */         } else if (funcDesc.invkind.value == OaIdl.INVOKEKIND.INVOKE_PROPERTYPUT.value) {
/*  92 */           method = new TlbPropertyPutStub(index, typeLibUtil, funcDesc, typeInfoUtil);
/*  93 */         } else if (funcDesc.invkind.value == OaIdl.INVOKEKIND.INVOKE_PROPERTYPUTREF.value) {
/*  94 */           method = new TlbPropertyPutStub(index, typeLibUtil, funcDesc, typeInfoUtil);
/*     */         } 
/*     */         
/*  97 */         this.content += method.getClassBuffer();
/*     */         
/*  99 */         if (i < cFuncs - 1) {
/* 100 */           this.content += "\n";
/*     */         }
/*     */       } 
/*     */ 
/*     */       
/* 105 */       typeInfoUtil.ReleaseFuncDesc(funcDesc);
/*     */     } 
/*     */     
/* 108 */     createContent(this.content);
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
/*     */   protected void createJavaDocHeader(String guid, String helpstring) {
/* 120 */     replaceVariable("uuid", guid);
/* 121 */     replaceVariable("helpstring", helpstring);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected String getClassTemplate() {
/* 131 */     return "com/sun/jna/platform/win32/COM/tlb/imp/TlbDispInterface.template";
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\jna\platform\win32\COM\tlb\imp\TlbDispInterface.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */