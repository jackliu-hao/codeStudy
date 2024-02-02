/*    */ package com.mysql.cj.protocol.a;
/*    */ 
/*    */ import com.mysql.cj.protocol.ColumnDefinition;
/*    */ import com.mysql.cj.protocol.Message;
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
/*    */ public class ColumnDefinitionFactory
/*    */   implements ProtocolEntityFactory<ColumnDefinition, NativePacketPayload>
/*    */ {
/*    */   protected long columnCount;
/*    */   protected ColumnDefinition columnDefinitionFromCache;
/*    */   
/*    */   public ColumnDefinitionFactory(long columnCount, ColumnDefinition columnDefinitionFromCache) {
/* 43 */     this.columnCount = columnCount;
/* 44 */     this.columnDefinitionFromCache = columnDefinitionFromCache;
/*    */   }
/*    */   
/*    */   public long getColumnCount() {
/* 48 */     return this.columnCount;
/*    */   }
/*    */   
/*    */   public ColumnDefinition getColumnDefinitionFromCache() {
/* 52 */     return this.columnDefinitionFromCache;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public ColumnDefinition createFromMessage(NativePacketPayload packetPayload) {
/* 58 */     return null;
/*    */   }
/*    */   
/*    */   public boolean mergeColumnDefinitions() {
/* 62 */     return false;
/*    */   }
/*    */   
/*    */   public ColumnDefinition createFromFields(Field[] fields) {
/* 66 */     return (ColumnDefinition)new DefaultColumnDefinition(fields);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\protocol\a\ColumnDefinitionFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */