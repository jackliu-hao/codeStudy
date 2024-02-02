/*    */ package org.noear.solon.core.handle;
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
/*    */ public enum Endpoint
/*    */ {
/* 14 */   before(0),
/*    */   
/* 16 */   main(1),
/*    */   
/* 18 */   after(2);
/*    */   public final int code;
/*    */   
/*    */   Endpoint(int code) {
/* 22 */     this.code = code;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\core\handle\Endpoint.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */