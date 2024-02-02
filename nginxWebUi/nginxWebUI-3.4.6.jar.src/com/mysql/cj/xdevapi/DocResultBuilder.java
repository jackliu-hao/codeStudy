/*    */ package com.mysql.cj.xdevapi;
/*    */ 
/*    */ import com.mysql.cj.MysqlxSession;
/*    */ import com.mysql.cj.conf.PropertySet;
/*    */ import com.mysql.cj.exceptions.ExceptionFactory;
/*    */ import com.mysql.cj.exceptions.WrongArgumentException;
/*    */ import com.mysql.cj.protocol.ColumnDefinition;
/*    */ import com.mysql.cj.protocol.ProtocolEntity;
/*    */ import com.mysql.cj.protocol.ResultBuilder;
/*    */ import com.mysql.cj.protocol.x.StatementExecuteOkBuilder;
/*    */ import com.mysql.cj.result.BufferedRowList;
/*    */ import com.mysql.cj.result.DefaultColumnDefinition;
/*    */ import com.mysql.cj.result.Field;
/*    */ import com.mysql.cj.result.Row;
/*    */ import com.mysql.cj.result.RowList;
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
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
/*    */ public class DocResultBuilder
/*    */   implements ResultBuilder<DocResult>
/*    */ {
/* 56 */   private ArrayList<Field> fields = new ArrayList<>();
/*    */   private ColumnDefinition metadata;
/* 58 */   private List<Row> rows = new ArrayList<>();
/*    */   private DocResult result;
/*    */   PropertySet pset;
/* 61 */   private StatementExecuteOkBuilder statementExecuteOkBuilder = new StatementExecuteOkBuilder();
/*    */   
/*    */   public DocResultBuilder(MysqlxSession sess) {
/* 64 */     this.pset = sess.getPropertySet();
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean addProtocolEntity(ProtocolEntity entity) {
/* 69 */     if (entity instanceof Field) {
/* 70 */       this.fields.add((Field)entity);
/* 71 */       return false;
/*    */     } 
/* 73 */     if (entity instanceof Row) {
/* 74 */       if (this.metadata == null) {
/* 75 */         this.metadata = (ColumnDefinition)new DefaultColumnDefinition(this.fields.<Field>toArray(new Field[0]));
/*    */       }
/* 77 */       this.rows.add(((Row)entity).setMetadata(this.metadata));
/* 78 */       return false;
/*    */     } 
/* 80 */     if (entity instanceof com.mysql.cj.protocol.x.Notice) {
/* 81 */       this.statementExecuteOkBuilder.addProtocolEntity(entity);
/* 82 */       return false;
/*    */     } 
/* 84 */     if (entity instanceof com.mysql.cj.protocol.x.FetchDoneEntity) {
/* 85 */       return false;
/*    */     }
/* 87 */     if (entity instanceof com.mysql.cj.protocol.x.StatementExecuteOk) {
/* 88 */       return true;
/*    */     }
/* 90 */     throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, "Unexpected protocol entity " + entity);
/*    */   }
/*    */ 
/*    */   
/*    */   public DocResult build() {
/* 95 */     this.result = new DocResultImpl((RowList)new BufferedRowList(this.rows), () -> this.statementExecuteOkBuilder.build(), this.pset);
/* 96 */     return this.result;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\xdevapi\DocResultBuilder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */