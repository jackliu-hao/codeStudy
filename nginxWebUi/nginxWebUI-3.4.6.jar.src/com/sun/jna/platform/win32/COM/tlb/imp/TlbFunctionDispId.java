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
/*     */ public class TlbFunctionDispId
/*     */   extends TlbAbstractMethod
/*     */ {
/*     */   public TlbFunctionDispId(int count, int index, TypeLibUtil typeLibUtil, OaIdl.FUNCDESC funcDesc, TypeInfoUtil typeInfoUtil) {
/*  53 */     super(index, typeLibUtil, funcDesc, typeInfoUtil);
/*     */     
/*  55 */     String returnValue, names[] = typeInfoUtil.getNames(funcDesc.memid, this.paramCount + 1);
/*     */     
/*  57 */     for (int i = 0; i < this.paramCount; i++) {
/*  58 */       OaIdl.ELEMDESC elemdesc = funcDesc.lprgelemdescParam.elemDescArg[i];
/*  59 */       String methodName = names[i + 1].toLowerCase();
/*  60 */       String type = getType(elemdesc.tdesc);
/*  61 */       String _methodName = replaceJavaKeyword(methodName);
/*  62 */       this.methodparams += type + " " + _methodName;
/*     */ 
/*     */       
/*  65 */       if (type.equals("VARIANT")) {
/*  66 */         this.methodvariables += _methodName;
/*     */       } else {
/*  68 */         this.methodvariables += "new VARIANT(" + _methodName + ")";
/*     */       } 
/*     */       
/*  71 */       if (i < this.paramCount - 1) {
/*  72 */         this.methodparams += ", ";
/*  73 */         this.methodvariables += ", ";
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/*  78 */     if (this.returnType.equalsIgnoreCase("VARIANT")) {
/*  79 */       returnValue = "pResult";
/*     */     } else {
/*  81 */       returnValue = "((" + this.returnType + ") pResult.getValue())";
/*     */     } 
/*  83 */     replaceVariable("helpstring", this.docStr);
/*  84 */     replaceVariable("returntype", this.returnType);
/*  85 */     replaceVariable("returnvalue", returnValue);
/*  86 */     replaceVariable("methodname", this.methodName);
/*  87 */     replaceVariable("methodparams", this.methodparams);
/*  88 */     replaceVariable("methodvariables", this.methodvariables);
/*  89 */     replaceVariable("vtableid", String.valueOf(this.vtableId));
/*  90 */     replaceVariable("memberid", String.valueOf(this.memberid));
/*  91 */     replaceVariable("functionCount", String.valueOf(count));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected String getClassTemplate() {
/* 101 */     return "com/sun/jna/platform/win32/COM/tlb/imp/TlbFunctionDispId.template";
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\jna\platform\win32\COM\tlb\imp\TlbFunctionDispId.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */