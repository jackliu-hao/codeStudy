/*    */ package com.mysql.cj.protocol.x;
/*    */ 
/*    */ import com.mysql.cj.exceptions.ExceptionFactory;
/*    */ import com.mysql.cj.exceptions.WrongArgumentException;
/*    */ import com.mysql.cj.protocol.ProtocolEntity;
/*    */ import com.mysql.cj.protocol.ResultBuilder;
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
/*    */ public class OkBuilder
/*    */   implements ResultBuilder<Ok>
/*    */ {
/*    */   public boolean addProtocolEntity(ProtocolEntity entity) {
/* 44 */     if (entity instanceof Notice) {
/* 45 */       return false;
/*    */     }
/* 47 */     if (entity instanceof Ok) {
/* 48 */       return true;
/*    */     }
/* 50 */     throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, "Unexpected protocol entity " + entity);
/*    */   }
/*    */   
/*    */   public Ok build() {
/* 54 */     return new Ok();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\protocol\x\OkBuilder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */