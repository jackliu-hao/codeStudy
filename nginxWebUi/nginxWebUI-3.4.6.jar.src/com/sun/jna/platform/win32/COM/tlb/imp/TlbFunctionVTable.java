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
/*    */ public class TlbFunctionVTable
/*    */   extends TlbAbstractMethod
/*    */ {
/*    */   public TlbFunctionVTable(int count, int index, TypeLibUtil typeLibUtil, OaIdl.FUNCDESC funcDesc, TypeInfoUtil typeInfoUtil) {
/* 53 */     super(index, typeLibUtil, funcDesc, typeInfoUtil);
/*    */     
/* 55 */     String[] names = typeInfoUtil.getNames(funcDesc.memid, this.paramCount + 1);
/*    */ 
/*    */     
/* 58 */     if (this.paramCount > 0) {
/* 59 */       this.methodvariables = ", ";
/*    */     }
/* 61 */     for (int i = 0; i < this.paramCount; i++) {
/* 62 */       OaIdl.ELEMDESC elemdesc = funcDesc.lprgelemdescParam.elemDescArg[i];
/* 63 */       String methodName = names[i + 1].toLowerCase();
/* 64 */       this
/* 65 */         .methodparams = this.methodparams + getType(elemdesc.tdesc) + " " + replaceJavaKeyword(methodName);
/* 66 */       this.methodvariables += methodName;
/*    */ 
/*    */       
/* 69 */       if (i < this.paramCount - 1) {
/* 70 */         this.methodparams += ", ";
/* 71 */         this.methodvariables += ", ";
/*    */       } 
/*    */     } 
/*    */     
/* 75 */     replaceVariable("helpstring", this.docStr);
/* 76 */     replaceVariable("returntype", this.returnType);
/* 77 */     replaceVariable("methodname", this.methodName);
/* 78 */     replaceVariable("methodparams", this.methodparams);
/* 79 */     replaceVariable("methodvariables", this.methodvariables);
/* 80 */     replaceVariable("vtableid", String.valueOf(this.vtableId));
/* 81 */     replaceVariable("memberid", String.valueOf(this.memberid));
/* 82 */     replaceVariable("functionCount", String.valueOf(count));
/*    */   }
/*    */ 
/*    */   
/*    */   protected String getClassTemplate() {
/* 87 */     return "com/sun/jna/platform/win32/COM/tlb/imp/TlbFunctionVTable.template";
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\jna\platform\win32\COM\tlb\imp\TlbFunctionVTable.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */