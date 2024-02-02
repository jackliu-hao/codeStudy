/*    */ package com.mysql.cj.xdevapi;
/*    */ 
/*    */ import com.mysql.cj.MysqlxSession;
/*    */ import com.mysql.cj.conf.PropertySet;
/*    */ import com.mysql.cj.protocol.ColumnDefinition;
/*    */ import com.mysql.cj.protocol.ProtocolEntity;
/*    */ import com.mysql.cj.protocol.ResultBuilder;
/*    */ import com.mysql.cj.protocol.x.Notice;
/*    */ import com.mysql.cj.protocol.x.StatementExecuteOkBuilder;
/*    */ import com.mysql.cj.protocol.x.XProtocol;
/*    */ import com.mysql.cj.protocol.x.XProtocolRowInputStream;
/*    */ import com.mysql.cj.result.DefaultColumnDefinition;
/*    */ import com.mysql.cj.result.Field;
/*    */ import com.mysql.cj.result.Row;
/*    */ import com.mysql.cj.result.RowList;
/*    */ import java.util.ArrayList;
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
/*    */ public class StreamingRowResultBuilder
/*    */   implements ResultBuilder<RowResult>
/*    */ {
/* 53 */   private ArrayList<Field> fields = new ArrayList<>();
/*    */   private ColumnDefinition metadata;
/* 55 */   private RowList rowList = null;
/*    */   
/*    */   TimeZone defaultTimeZone;
/*    */   PropertySet pset;
/*    */   XProtocol protocol;
/* 60 */   private StatementExecuteOkBuilder statementExecuteOkBuilder = new StatementExecuteOkBuilder();
/*    */   
/*    */   public StreamingRowResultBuilder(MysqlxSession sess) {
/* 63 */     this.defaultTimeZone = sess.getServerSession().getDefaultTimeZone();
/* 64 */     this.pset = sess.getPropertySet();
/* 65 */     this.protocol = sess.getProtocol();
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean addProtocolEntity(ProtocolEntity entity) {
/* 70 */     if (entity instanceof Field) {
/* 71 */       this.fields.add((Field)entity);
/* 72 */       return false;
/*    */     } 
/* 74 */     if (entity instanceof Notice) {
/* 75 */       this.statementExecuteOkBuilder.addProtocolEntity(entity);
/*    */     }
/*    */     
/* 78 */     if (this.metadata == null) {
/* 79 */       this.metadata = (ColumnDefinition)new DefaultColumnDefinition(this.fields.<Field>toArray(new Field[0]));
/*    */     }
/*    */     
/* 82 */     this.rowList = (entity instanceof Row) ? (RowList)new XProtocolRowInputStream(this.metadata, (Row)entity, this.protocol, n -> this.statementExecuteOkBuilder.addProtocolEntity((ProtocolEntity)n)) : (RowList)new XProtocolRowInputStream(this.metadata, this.protocol, n -> this.statementExecuteOkBuilder.addProtocolEntity((ProtocolEntity)n));
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */     
/* 88 */     return true;
/*    */   }
/*    */ 
/*    */   
/*    */   public RowResult build() {
/* 93 */     return new RowResultImpl(this.metadata, this.defaultTimeZone, this.rowList, () -> (ProtocolEntity)this.protocol.readQueryResult((ResultBuilder)this.statementExecuteOkBuilder), this.pset);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\xdevapi\StreamingRowResultBuilder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */