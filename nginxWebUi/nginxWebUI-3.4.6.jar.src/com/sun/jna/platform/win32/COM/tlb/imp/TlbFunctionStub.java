/*    */ package com.sun.jna.platform.win32.COM.tlb.imp;
/*    */ 
/*    */ import com.sun.jna.platform.win32.COM.TypeInfoUtil;
/*    */ import com.sun.jna.platform.win32.COM.TypeLibUtil;
/*    */ import com.sun.jna.platform.win32.OaIdl;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class TlbFunctionStub
/*    */   extends TlbAbstractMethod
/*    */ {
/*    */   public TlbFunctionStub(int index, TypeLibUtil typeLibUtil, OaIdl.FUNCDESC funcDesc, TypeInfoUtil typeInfoUtil) {
/* 54 */     super(index, typeLibUtil, funcDesc, typeInfoUtil);
/*    */     
/* 56 */     TypeInfoUtil.TypeInfoDoc typeInfoDoc = typeInfoUtil.getDocumentation(funcDesc.memid);
/* 57 */     String methodname = typeInfoDoc.getName();
/* 58 */     String docStr = typeInfoDoc.getDocString();
/* 59 */     String[] names = typeInfoUtil.getNames(funcDesc.memid, this.paramCount + 1);
/*    */ 
/*    */     
/* 62 */     if (this.paramCount > 0) {
/* 63 */       this.methodvariables = ", ";
/*    */     }
/* 65 */     for (int i = 0; i < this.paramCount; i++) {
/* 66 */       OaIdl.ELEMDESC elemdesc = funcDesc.lprgelemdescParam.elemDescArg[i];
/* 67 */       String methodName = names[i + 1].toLowerCase();
/* 68 */       this
/* 69 */         .methodparams = this.methodparams + getType(elemdesc.tdesc) + " " + replaceJavaKeyword(methodName);
/* 70 */       this.methodvariables += methodName;
/*    */ 
/*    */       
/* 73 */       if (i < this.paramCount - 1) {
/* 74 */         this.methodparams += ", ";
/* 75 */         this.methodvariables += ", ";
/*    */       } 
/*    */     } 
/*    */     
/* 79 */     replaceVariable("helpstring", docStr);
/* 80 */     replaceVariable("returntype", this.returnType);
/* 81 */     replaceVariable("methodname", methodname);
/* 82 */     replaceVariable("methodparams", this.methodparams);
/* 83 */     replaceVariable("vtableid", String.valueOf(this.vtableId));
/* 84 */     replaceVariable("memberid", String.valueOf(this.memberid));
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   protected String getClassTemplate() {
/* 94 */     return "com/sun/jna/platform/win32/COM/tlb/imp/TlbFunctionStub.template";
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\jna\platform\win32\COM\tlb\imp\TlbFunctionStub.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */