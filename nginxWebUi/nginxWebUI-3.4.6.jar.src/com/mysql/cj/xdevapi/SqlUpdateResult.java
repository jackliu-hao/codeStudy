/*    */ package com.mysql.cj.xdevapi;
/*    */ 
/*    */ import com.mysql.cj.exceptions.FeatureNotAvailableException;
/*    */ import com.mysql.cj.protocol.x.StatementExecuteOk;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ public class SqlUpdateResult
/*    */   extends UpdateResult
/*    */   implements SqlResult
/*    */ {
/*    */   public SqlUpdateResult(StatementExecuteOk ok) {
/* 49 */     super(ok);
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean hasData() {
/* 54 */     return false;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean nextResult() {
/* 59 */     throw new FeatureNotAvailableException("Not a multi-result");
/*    */   }
/*    */ 
/*    */   
/*    */   public List<Row> fetchAll() {
/* 64 */     throw new FeatureNotAvailableException("No data");
/*    */   }
/*    */ 
/*    */   
/*    */   public Row next() {
/* 69 */     throw new FeatureNotAvailableException("No data");
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean hasNext() {
/* 74 */     throw new FeatureNotAvailableException("No data");
/*    */   }
/*    */ 
/*    */   
/*    */   public int getColumnCount() {
/* 79 */     throw new FeatureNotAvailableException("No data");
/*    */   }
/*    */ 
/*    */   
/*    */   public List<Column> getColumns() {
/* 84 */     throw new FeatureNotAvailableException("No data");
/*    */   }
/*    */ 
/*    */   
/*    */   public List<String> getColumnNames() {
/* 89 */     throw new FeatureNotAvailableException("No data");
/*    */   }
/*    */ 
/*    */   
/*    */   public long count() {
/* 94 */     throw new FeatureNotAvailableException("No data");
/*    */   }
/*    */ 
/*    */   
/*    */   public Long getAutoIncrementValue() {
/* 99 */     return this.ok.getLastInsertId();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\xdevapi\SqlUpdateResult.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */