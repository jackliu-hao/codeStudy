/*    */ package org.h2.table;
/*    */ 
/*    */ import java.util.ArrayList;
/*    */ import org.h2.command.query.TableValueConstructor;
/*    */ import org.h2.engine.SessionLocal;
/*    */ import org.h2.expression.Expression;
/*    */ import org.h2.result.ResultInterface;
/*    */ import org.h2.result.ResultTarget;
/*    */ import org.h2.result.SimpleResult;
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
/*    */ public class TableValueConstructorTable
/*    */   extends VirtualConstructedTable
/*    */ {
/*    */   private final ArrayList<ArrayList<Expression>> rows;
/*    */   
/*    */   public TableValueConstructorTable(Schema paramSchema, SessionLocal paramSessionLocal, Column[] paramArrayOfColumn, ArrayList<ArrayList<Expression>> paramArrayList) {
/* 26 */     super(paramSchema, 0, "VALUES");
/* 27 */     setColumns(paramArrayOfColumn);
/* 28 */     this.rows = paramArrayList;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean canGetRowCount(SessionLocal paramSessionLocal) {
/* 33 */     return true;
/*    */   }
/*    */ 
/*    */   
/*    */   public long getRowCount(SessionLocal paramSessionLocal) {
/* 38 */     return this.rows.size();
/*    */   }
/*    */ 
/*    */   
/*    */   public long getRowCountApproximation(SessionLocal paramSessionLocal) {
/* 43 */     return this.rows.size();
/*    */   }
/*    */ 
/*    */   
/*    */   public ResultInterface getResult(SessionLocal paramSessionLocal) {
/* 48 */     SimpleResult simpleResult = new SimpleResult();
/* 49 */     int i = this.columns.length;
/* 50 */     for (byte b = 0; b < i; b++) {
/* 51 */       Column column = this.columns[b];
/* 52 */       simpleResult.addColumn(column.getName(), column.getType());
/*    */     } 
/* 54 */     TableValueConstructor.getVisibleResult(paramSessionLocal, (ResultTarget)simpleResult, this.columns, this.rows);
/* 55 */     return (ResultInterface)simpleResult;
/*    */   }
/*    */ 
/*    */   
/*    */   public StringBuilder getSQL(StringBuilder paramStringBuilder, int paramInt) {
/* 60 */     paramStringBuilder.append('(');
/* 61 */     TableValueConstructor.getValuesSQL(paramStringBuilder, paramInt, this.rows);
/* 62 */     return paramStringBuilder.append(')');
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean isDeterministic() {
/* 67 */     return true;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\table\TableValueConstructorTable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */