/*    */ package com.mysql.cj.xdevapi;
/*    */ 
/*    */ import com.mysql.cj.conf.PropertySet;
/*    */ import com.mysql.cj.protocol.ProtocolEntity;
/*    */ import com.mysql.cj.protocol.ProtocolEntityFactory;
/*    */ import com.mysql.cj.protocol.x.XMessage;
/*    */ import com.mysql.cj.result.Row;
/*    */ import com.mysql.cj.result.ValueFactory;
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
/*    */ public class DbDocFactory
/*    */   implements ProtocolEntityFactory<DbDoc, XMessage>
/*    */ {
/*    */   private PropertySet pset;
/*    */   
/*    */   public DbDocFactory(PropertySet pset) {
/* 45 */     this.pset = pset;
/*    */   }
/*    */ 
/*    */   
/*    */   public DbDoc createFromProtocolEntity(ProtocolEntity internalRow) {
/* 50 */     return (DbDoc)((Row)internalRow).getValue(0, (ValueFactory)new DbDocValueFactory(this.pset));
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\xdevapi\DbDocFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */