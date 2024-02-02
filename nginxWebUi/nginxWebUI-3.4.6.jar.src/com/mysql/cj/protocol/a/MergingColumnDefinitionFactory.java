/*    */ package com.mysql.cj.protocol.a;
/*    */ 
/*    */ import com.mysql.cj.exceptions.ExceptionFactory;
/*    */ import com.mysql.cj.exceptions.WrongArgumentException;
/*    */ import com.mysql.cj.protocol.ColumnDefinition;
/*    */ import com.mysql.cj.protocol.ProtocolEntityFactory;
/*    */ import com.mysql.cj.result.DefaultColumnDefinition;
/*    */ import com.mysql.cj.result.Field;
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
/*    */ public class MergingColumnDefinitionFactory
/*    */   extends ColumnDefinitionFactory
/*    */   implements ProtocolEntityFactory<ColumnDefinition, NativePacketPayload>
/*    */ {
/*    */   public MergingColumnDefinitionFactory(long columnCount, ColumnDefinition columnDefinitionFromCache) {
/* 45 */     super(columnCount, columnDefinitionFromCache);
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean mergeColumnDefinitions() {
/* 50 */     return true;
/*    */   }
/*    */ 
/*    */   
/*    */   public ColumnDefinition createFromFields(Field[] fields) {
/* 55 */     if (this.columnDefinitionFromCache != null) {
/* 56 */       if (fields.length != this.columnCount) {
/* 57 */         throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, "Wrong number of ColumnDefinition fields.");
/*    */       }
/* 59 */       Field[] f = this.columnDefinitionFromCache.getFields();
/* 60 */       for (int i = 0; i < fields.length; i++) {
/* 61 */         fields[i].setFlags(f[i].getFlags());
/*    */       }
/*    */     } 
/* 64 */     return (ColumnDefinition)new DefaultColumnDefinition(fields);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\protocol\a\MergingColumnDefinitionFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */