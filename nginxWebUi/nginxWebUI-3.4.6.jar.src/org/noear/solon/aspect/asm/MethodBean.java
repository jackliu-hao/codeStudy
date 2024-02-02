/*    */ package org.noear.solon.aspect.asm;
/*    */ 
/*    */ 
/*    */ public class MethodBean
/*    */ {
/*    */   public int access;
/*    */   public String methodName;
/*    */   public String methodDesc;
/*    */   
/*    */   public MethodBean() {}
/*    */   
/*    */   public MethodBean(int access, String methodName, String methodDesc) {
/* 13 */     this.access = access;
/* 14 */     this.methodName = methodName;
/* 15 */     this.methodDesc = methodDesc;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean equals(Object obj) {
/* 20 */     if (obj == null) {
/* 21 */       return false;
/*    */     }
/* 23 */     if (!(obj instanceof MethodBean)) {
/* 24 */       return false;
/*    */     }
/* 26 */     MethodBean bean = (MethodBean)obj;
/* 27 */     if (this.access == bean.access && this.methodName != null && bean.methodName != null && this.methodName
/*    */ 
/*    */       
/* 30 */       .equals(bean.methodName) && this.methodDesc != null && bean.methodDesc != null && this.methodDesc
/*    */ 
/*    */       
/* 33 */       .equals(bean.methodDesc)) {
/* 34 */       return true;
/*    */     }
/* 36 */     return false;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\aspect\asm\MethodBean.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */