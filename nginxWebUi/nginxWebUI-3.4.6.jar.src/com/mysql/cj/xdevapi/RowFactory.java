/*    */ package com.mysql.cj.xdevapi;
/*    */ 
/*    */ import com.mysql.cj.conf.PropertySet;
/*    */ import com.mysql.cj.protocol.ColumnDefinition;
/*    */ import com.mysql.cj.protocol.ProtocolEntity;
/*    */ import com.mysql.cj.protocol.ProtocolEntityFactory;
/*    */ import com.mysql.cj.protocol.x.XMessage;
/*    */ import com.mysql.cj.result.Row;
/*    */ import java.util.TimeZone;
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
/*    */ public class RowFactory
/*    */   implements ProtocolEntityFactory<Row, XMessage>
/*    */ {
/*    */   private ColumnDefinition metadata;
/*    */   private TimeZone defaultTimeZone;
/*    */   private PropertySet pset;
/*    */   
/*    */   public RowFactory(ColumnDefinition metadata, TimeZone defaultTimeZone, PropertySet pset) {
/* 59 */     this.metadata = metadata;
/* 60 */     this.defaultTimeZone = defaultTimeZone;
/* 61 */     this.pset = pset;
/*    */   }
/*    */ 
/*    */   
/*    */   public Row createFromProtocolEntity(ProtocolEntity internalRow) {
/* 66 */     return new RowImpl((Row)internalRow, this.metadata, this.defaultTimeZone, this.pset);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\xdevapi\RowFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */