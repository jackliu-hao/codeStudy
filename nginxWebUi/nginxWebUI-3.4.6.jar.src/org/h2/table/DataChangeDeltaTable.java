/*     */ package org.h2.table;
/*     */ 
/*     */ import org.h2.command.dml.DataChangeStatement;
/*     */ import org.h2.engine.SessionLocal;
/*     */ import org.h2.expression.Expression;
/*     */ import org.h2.expression.ExpressionColumn;
/*     */ import org.h2.result.LocalResult;
/*     */ import org.h2.result.ResultInterface;
/*     */ import org.h2.result.ResultTarget;
/*     */ import org.h2.result.Row;
/*     */ import org.h2.schema.Schema;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class DataChangeDeltaTable
/*     */   extends VirtualConstructedTable
/*     */ {
/*     */   private final DataChangeStatement statement;
/*     */   private final ResultOption resultOption;
/*     */   private final Expression[] expressions;
/*     */   
/*     */   public enum ResultOption
/*     */   {
/*  31 */     OLD,
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  36 */     NEW,
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  41 */     FINAL;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void collectInsertedFinalRow(SessionLocal paramSessionLocal, Table paramTable, ResultTarget paramResultTarget, ResultOption paramResultOption, Row paramRow) {
/*  61 */     if ((paramSessionLocal.getMode()).takeInsertedIdentity) {
/*  62 */       Column column = paramTable.getIdentityColumn();
/*  63 */       if (column != null) {
/*  64 */         paramSessionLocal.setLastIdentity(paramRow.getValue(column.getColumnId()));
/*     */       }
/*     */     } 
/*  67 */     if (paramResultOption == ResultOption.FINAL) {
/*  68 */       paramResultTarget.addRow(paramRow.getValueList());
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public DataChangeDeltaTable(Schema paramSchema, SessionLocal paramSessionLocal, DataChangeStatement paramDataChangeStatement, ResultOption paramResultOption) {
/*  80 */     super(paramSchema, 0, paramDataChangeStatement.getStatementName());
/*  81 */     this.statement = paramDataChangeStatement;
/*  82 */     this.resultOption = paramResultOption;
/*  83 */     Table table = paramDataChangeStatement.getTable();
/*  84 */     Column[] arrayOfColumn1 = table.getColumns();
/*  85 */     int i = arrayOfColumn1.length;
/*  86 */     Column[] arrayOfColumn2 = new Column[i];
/*  87 */     for (byte b1 = 0; b1 < i; b1++) {
/*  88 */       arrayOfColumn2[b1] = arrayOfColumn1[b1].getClone();
/*     */     }
/*  90 */     setColumns(arrayOfColumn2);
/*  91 */     Expression[] arrayOfExpression = new Expression[i];
/*  92 */     String str = getName();
/*  93 */     for (byte b2 = 0; b2 < i; b2++) {
/*  94 */       arrayOfExpression[b2] = (Expression)new ExpressionColumn(this.database, null, str, arrayOfColumn2[b2].getName());
/*     */     }
/*  96 */     this.expressions = arrayOfExpression;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean canGetRowCount(SessionLocal paramSessionLocal) {
/* 101 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getRowCount(SessionLocal paramSessionLocal) {
/* 106 */     return Long.MAX_VALUE;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getRowCountApproximation(SessionLocal paramSessionLocal) {
/* 111 */     return Long.MAX_VALUE;
/*     */   }
/*     */ 
/*     */   
/*     */   public ResultInterface getResult(SessionLocal paramSessionLocal) {
/* 116 */     this.statement.prepare();
/* 117 */     int i = this.expressions.length;
/* 118 */     LocalResult localResult = new LocalResult(paramSessionLocal, this.expressions, i, i);
/* 119 */     localResult.setForDataChangeDeltaTable();
/* 120 */     this.statement.update((ResultTarget)localResult, this.resultOption);
/* 121 */     return (ResultInterface)localResult;
/*     */   }
/*     */ 
/*     */   
/*     */   public StringBuilder getSQL(StringBuilder paramStringBuilder, int paramInt) {
/* 126 */     return paramStringBuilder.append(this.resultOption.name()).append(" TABLE (").append(this.statement.getSQL()).append(')');
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isDeterministic() {
/* 131 */     return false;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\table\DataChangeDeltaTable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */