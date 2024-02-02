/*    */ package com.mysql.cj.protocol.a.result;
/*    */ 
/*    */ import com.mysql.cj.exceptions.ExceptionInterceptor;
/*    */ import com.mysql.cj.protocol.a.NativePacketPayload;
/*    */ import com.mysql.cj.protocol.result.AbstractResultsetRow;
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
/*    */ public abstract class AbstractBufferRow
/*    */   extends AbstractResultsetRow
/*    */ {
/*    */   protected NativePacketPayload rowFromServer;
/* 49 */   protected int homePosition = 0;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/* 55 */   protected int lastRequestedIndex = -1;
/*    */ 
/*    */   
/*    */   protected int lastRequestedPos;
/*    */ 
/*    */ 
/*    */   
/*    */   protected AbstractBufferRow(ExceptionInterceptor exceptionInterceptor) {
/* 63 */     super(exceptionInterceptor);
/*    */   }
/*    */   
/*    */   abstract int findAndSeekToOffset(int paramInt);
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\protocol\a\result\AbstractBufferRow.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */