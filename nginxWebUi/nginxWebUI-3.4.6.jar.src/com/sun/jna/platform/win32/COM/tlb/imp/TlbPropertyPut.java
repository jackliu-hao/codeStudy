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
/*    */ public class TlbPropertyPut
/*    */   extends TlbAbstractMethod
/*    */ {
/*    */   public TlbPropertyPut(int count, int index, TypeLibUtil typeLibUtil, OaIdl.FUNCDESC funcDesc, TypeInfoUtil typeInfoUtil) {
/* 53 */     super(index, typeLibUtil, funcDesc, typeInfoUtil);
/*    */     
/* 55 */     this.methodName = "set" + getMethodName();
/* 56 */     String[] names = typeInfoUtil.getNames(funcDesc.memid, this.paramCount + 1);
/*    */     
/* 58 */     if (this.paramCount > 0) {
/* 59 */       this.methodvariables += ", ";
/*    */     }
/* 61 */     for (int i = 0; i < this.paramCount; i++) {
/* 62 */       OaIdl.ELEMDESC elemdesc = funcDesc.lprgelemdescParam.elemDescArg[i];
/* 63 */       String varType = getType(elemdesc);
/* 64 */       this
/* 65 */         .methodparams = this.methodparams + varType + " " + replaceJavaKeyword(names[i].toLowerCase());
/* 66 */       this.methodvariables += replaceJavaKeyword(names[i].toLowerCase());
/*    */ 
/*    */       
/* 69 */       if (i < this.paramCount - 1) {
/* 70 */         this.methodparams += ", ";
/* 71 */         this.methodvariables += ", ";
/*    */       } 
/*    */     } 
/*    */     
/* 75 */     replaceVariable("helpstring", this.docStr);
/* 76 */     replaceVariable("methodname", this.methodName);
/* 77 */     replaceVariable("methodparams", this.methodparams);
/* 78 */     replaceVariable("methodvariables", this.methodvariables);
/* 79 */     replaceVariable("vtableid", String.valueOf(this.vtableId));
/* 80 */     replaceVariable("memberid", String.valueOf(this.memberid));
/* 81 */     replaceVariable("functionCount", String.valueOf(count));
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   protected String getClassTemplate() {
/* 91 */     return "com/sun/jna/platform/win32/COM/tlb/imp/TlbPropertyPut.template";
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\jna\platform\win32\COM\tlb\imp\TlbPropertyPut.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */