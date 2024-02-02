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
/*    */ public class StreamingDocResultBuilder
/*    */   implements ResultBuilder<DocResult>
/*    */ {
/* 52 */   private ArrayList<Field> fields = new ArrayList<>();
/*    */   private ColumnDefinition metadata;
/* 54 */   private RowList rowList = null;
/*    */   
/*    */   PropertySet pset;
/*    */   XProtocol protocol;
/* 58 */   private StatementExecuteOkBuilder statementExecuteOkBuilder = new StatementExecuteOkBuilder();
/*    */   
/*    */   public StreamingDocResultBuilder(MysqlxSession sess) {
/* 61 */     this.pset = sess.getPropertySet();
/* 62 */     this.protocol = sess.getProtocol();
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean addProtocolEntity(ProtocolEntity entity) {
/* 67 */     if (entity instanceof Field) {
/* 68 */       this.fields.add((Field)entity);
/* 69 */       return false;
/*    */     } 
/* 71 */     if (entity instanceof Notice) {
/* 72 */       this.statementExecuteOkBuilder.addProtocolEntity(entity);
/* 73 */       return false;
/*    */     } 
/*    */     
/* 76 */     if (this.metadata == null) {
/* 77 */       this.metadata = (ColumnDefinition)new DefaultColumnDefinition(this.fields.<Field>toArray(new Field[0]));
/*    */     }
/*    */     
/* 80 */     this.rowList = (entity instanceof Row) ? (RowList)new XProtocolRowInputStream(this.metadata, (Row)entity, this.protocol, n -> this.statementExecuteOkBuilder.addProtocolEntity((ProtocolEntity)n)) : (RowList)new XProtocolRowInputStream(this.metadata, this.protocol, n -> this.statementExecuteOkBuilder.addProtocolEntity((ProtocolEntity)n));
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */     
/* 86 */     return true;
/*    */   }
/*    */ 
/*    */   
/*    */   public DocResult build() {
/* 91 */     return new DocResultImpl(this.rowList, () -> (ProtocolEntity)this.protocol.readQueryResult((ResultBuilder)this.statementExecuteOkBuilder), this.pset);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\xdevapi\StreamingDocResultBuilder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */