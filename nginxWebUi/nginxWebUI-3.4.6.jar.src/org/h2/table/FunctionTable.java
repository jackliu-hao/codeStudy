/*    */ package org.h2.table;
/*    */ 
/*    */ import org.h2.engine.SessionLocal;
/*    */ import org.h2.expression.function.table.TableFunction;
/*    */ import org.h2.result.ResultInterface;
/*    */ import org.h2.schema.Schema;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class FunctionTable
/*    */   extends VirtualConstructedTable
/*    */ {
/*    */   private final TableFunction function;
/*    */   
/*    */   public FunctionTable(Schema paramSchema, SessionLocal paramSessionLocal, TableFunction paramTableFunction) {
/* 22 */     super(paramSchema, 0, paramTableFunction.getName());
/* 23 */     this.function = paramTableFunction;
/* 24 */     paramTableFunction.optimize(paramSessionLocal);
/* 25 */     ResultInterface resultInterface = paramTableFunction.getValueTemplate(paramSessionLocal);
/* 26 */     int i = resultInterface.getVisibleColumnCount();
/* 27 */     Column[] arrayOfColumn = new Column[i];
/* 28 */     for (byte b = 0; b < i; b++) {
/* 29 */       arrayOfColumn[b] = new Column(resultInterface.getColumnName(b), resultInterface.getColumnType(b));
/*    */     }
/* 31 */     setColumns(arrayOfColumn);
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean canGetRowCount(SessionLocal paramSessionLocal) {
/* 36 */     return false;
/*    */   }
/*    */ 
/*    */   
/*    */   public long getRowCount(SessionLocal paramSessionLocal) {
/* 41 */     return Long.MAX_VALUE;
/*    */   }
/*    */ 
/*    */   
/*    */   public long getRowCountApproximation(SessionLocal paramSessionLocal) {
/* 46 */     return Long.MAX_VALUE;
/*    */   }
/*    */ 
/*    */   
/*    */   public ResultInterface getResult(SessionLocal paramSessionLocal) {
/* 51 */     return this.function.getValue(paramSessionLocal);
/*    */   }
/*    */ 
/*    */   
/*    */   public String getSQL(int paramInt) {
/* 56 */     return this.function.getSQL(paramInt);
/*    */   }
/*    */ 
/*    */   
/*    */   public StringBuilder getSQL(StringBuilder paramStringBuilder, int paramInt) {
/* 61 */     return paramStringBuilder.append(this.function.getSQL(paramInt));
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean isDeterministic() {
/* 66 */     return this.function.isDeterministic();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\table\FunctionTable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */