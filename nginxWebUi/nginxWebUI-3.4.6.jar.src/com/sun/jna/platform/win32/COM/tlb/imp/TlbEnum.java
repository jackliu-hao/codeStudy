/*     */ package com.sun.jna.platform.win32.COM.tlb.imp;
/*     */ 
/*     */ import com.sun.jna.platform.win32.COM.TypeInfoUtil;
/*     */ import com.sun.jna.platform.win32.COM.TypeLibUtil;
/*     */ import com.sun.jna.platform.win32.OaIdl;
/*     */ import com.sun.jna.platform.win32.Variant;
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
/*     */ public class TlbEnum
/*     */   extends TlbBase
/*     */ {
/*     */   public TlbEnum(int index, String packagename, TypeLibUtil typeLibUtil) {
/*  52 */     super(index, typeLibUtil, null);
/*     */     
/*  54 */     TypeLibUtil.TypeLibDoc typeLibDoc = this.typeLibUtil.getDocumentation(index);
/*  55 */     String docString = typeLibDoc.getDocString();
/*     */     
/*  57 */     if (typeLibDoc.getName().length() > 0) {
/*  58 */       this.name = typeLibDoc.getName();
/*     */     }
/*  60 */     logInfo("Type of kind 'Enum' found: " + this.name);
/*     */     
/*  62 */     createPackageName(packagename);
/*  63 */     createClassName(this.name);
/*  64 */     setFilename(this.name);
/*     */ 
/*     */     
/*  67 */     TypeInfoUtil typeInfoUtil = typeLibUtil.getTypeInfoUtil(index);
/*  68 */     OaIdl.TYPEATTR typeAttr = typeInfoUtil.getTypeAttr();
/*     */     
/*  70 */     createJavaDocHeader(typeAttr.guid.toGuidString(), docString);
/*     */     
/*  72 */     int cVars = typeAttr.cVars.intValue();
/*  73 */     for (int i = 0; i < cVars; i++) {
/*     */       
/*  75 */       OaIdl.VARDESC varDesc = typeInfoUtil.getVarDesc(i);
/*  76 */       Variant.VARIANT.ByReference byReference = varDesc._vardesc.lpvarValue;
/*  77 */       Object value = byReference.getValue();
/*     */ 
/*     */       
/*  80 */       OaIdl.MEMBERID memberID = varDesc.memid;
/*     */ 
/*     */       
/*  83 */       TypeInfoUtil.TypeInfoDoc typeInfoDoc2 = typeInfoUtil.getDocumentation(memberID);
/*  84 */       this.content += "\t\t//" + typeInfoDoc2.getName() + "\n";
/*  85 */       this
/*  86 */         .content = this.content + "\t\tpublic static final int " + typeInfoDoc2.getName() + " = " + value.toString() + ";";
/*     */       
/*  88 */       if (i < cVars - 1) {
/*  89 */         this.content += "\n";
/*     */       }
/*     */       
/*  92 */       typeInfoUtil.ReleaseVarDesc(varDesc);
/*     */     } 
/*     */     
/*  95 */     createContent(this.content);
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
/* 107 */     replaceVariable("uuid", guid);
/* 108 */     replaceVariable("helpstring", helpstring);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected String getClassTemplate() {
/* 118 */     return "com/sun/jna/platform/win32/COM/tlb/imp/TlbEnum.template";
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\jna\platform\win32\COM\tlb\imp\TlbEnum.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */