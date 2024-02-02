/*    */ package com.mysql.cj.protocol.a;
/*    */ 
/*    */ import com.mysql.cj.conf.RuntimeProperty;
/*    */ import com.mysql.cj.exceptions.ExceptionInterceptor;
/*    */ import com.mysql.cj.protocol.ColumnDefinition;
/*    */ import com.mysql.cj.protocol.ProtocolEntityFactory;
/*    */ import com.mysql.cj.protocol.Resultset;
/*    */ import com.mysql.cj.protocol.ResultsetRow;
/*    */ import com.mysql.cj.protocol.ValueDecoder;
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
/*    */ public abstract class AbstractRowFactory
/*    */   implements ProtocolEntityFactory<ResultsetRow, NativePacketPayload>
/*    */ {
/*    */   protected ColumnDefinition columnDefinition;
/*    */   protected Resultset.Concurrency resultSetConcurrency;
/*    */   protected boolean canReuseRowPacketForBufferRow;
/*    */   protected RuntimeProperty<Integer> useBufferRowSizeThreshold;
/*    */   protected ExceptionInterceptor exceptionInterceptor;
/*    */   protected ValueDecoder valueDecoder;
/*    */   
/*    */   public boolean canReuseRowPacketForBufferRow() {
/* 50 */     return this.canReuseRowPacketForBufferRow;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\protocol\a\AbstractRowFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */