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
/*    */ public class TlbPropertyGetStub
/*    */   extends TlbAbstractMethod
/*    */ {
/*    */   public TlbPropertyGetStub(int index, TypeLibUtil typeLibUtil, OaIdl.FUNCDESC funcDesc, TypeInfoUtil typeInfoUtil) {
/* 53 */     super(index, typeLibUtil, funcDesc, typeInfoUtil);
/*    */     
/* 55 */     TypeInfoUtil.TypeInfoDoc typeInfoDoc = typeInfoUtil.getDocumentation(funcDesc.memid);
/* 56 */     String docStr = typeInfoDoc.getDocString();
/* 57 */     String methodname = "get" + typeInfoDoc.getName();
/*    */     
/* 59 */     replaceVariable("helpstring", docStr);
/* 60 */     replaceVariable("returntype", this.returnType);
/* 61 */     replaceVariable("methodname", methodname);
/* 62 */     replaceVariable("vtableid", String.valueOf(this.vtableId));
/* 63 */     replaceVariable("memberid", String.valueOf(this.memberid));
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   protected String getClassTemplate() {
/* 73 */     return "com/sun/jna/platform/win32/COM/tlb/imp/TlbPropertyGetStub.template";
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\jna\platform\win32\COM\tlb\imp\TlbPropertyGetStub.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */