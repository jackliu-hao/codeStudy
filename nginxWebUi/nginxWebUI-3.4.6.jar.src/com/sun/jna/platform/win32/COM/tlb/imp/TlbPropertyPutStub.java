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
/*    */ public class TlbPropertyPutStub
/*    */   extends TlbAbstractMethod
/*    */ {
/*    */   public TlbPropertyPutStub(int index, TypeLibUtil typeLibUtil, OaIdl.FUNCDESC funcDesc, TypeInfoUtil typeInfoUtil) {
/* 54 */     super(index, typeLibUtil, funcDesc, typeInfoUtil);
/*    */     
/* 56 */     TypeInfoUtil.TypeInfoDoc typeInfoDoc = typeInfoUtil.getDocumentation(funcDesc.memid);
/* 57 */     String docStr = typeInfoDoc.getDocString();
/* 58 */     String methodname = "set" + typeInfoDoc.getName();
/* 59 */     String[] names = typeInfoUtil.getNames(funcDesc.memid, this.paramCount + 1);
/*    */     
/* 61 */     for (int i = 0; i < this.paramCount; i++) {
/* 62 */       OaIdl.ELEMDESC elemdesc = funcDesc.lprgelemdescParam.elemDescArg[i];
/* 63 */       String varType = getType(elemdesc);
/* 64 */       this
/* 65 */         .methodparams = this.methodparams + varType + " " + replaceJavaKeyword(names[i].toLowerCase());
/*    */ 
/*    */       
/* 68 */       if (i < this.paramCount - 1) {
/* 69 */         this.methodparams += ", ";
/*    */       }
/*    */     } 
/*    */     
/* 73 */     replaceVariable("helpstring", docStr);
/* 74 */     replaceVariable("methodname", methodname);
/* 75 */     replaceVariable("methodparams", this.methodparams);
/* 76 */     replaceVariable("vtableid", String.valueOf(this.vtableId));
/* 77 */     replaceVariable("memberid", String.valueOf(this.memberid));
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   protected String getClassTemplate() {
/* 87 */     return "com/sun/jna/platform/win32/COM/tlb/imp/TlbPropertyPutStub.template";
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\jna\platform\win32\COM\tlb\imp\TlbPropertyPutStub.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */