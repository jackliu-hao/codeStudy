/*     */ package org.h2.expression;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import org.h2.engine.SessionLocal;
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.table.Column;
/*     */ import org.h2.table.ColumnResolver;
/*     */ import org.h2.table.TableFilter;
/*     */ import org.h2.util.StringUtils;
/*     */ import org.h2.value.TypeInfo;
/*     */ import org.h2.value.Value;
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
/*     */ public final class Wildcard
/*     */   extends Expression
/*     */ {
/*     */   private final String schema;
/*     */   private final String table;
/*     */   private ArrayList<ExpressionColumn> exceptColumns;
/*     */   
/*     */   public Wildcard(String paramString1, String paramString2) {
/*  34 */     this.schema = paramString1;
/*  35 */     this.table = paramString2;
/*     */   }
/*     */   
/*     */   public ArrayList<ExpressionColumn> getExceptColumns() {
/*  39 */     return this.exceptColumns;
/*     */   }
/*     */   
/*     */   public void setExceptColumns(ArrayList<ExpressionColumn> paramArrayList) {
/*  43 */     this.exceptColumns = paramArrayList;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HashMap<Column, ExpressionColumn> mapExceptColumns() {
/*  53 */     HashMap<Object, Object> hashMap = new HashMap<>();
/*  54 */     for (ExpressionColumn expressionColumn : this.exceptColumns) {
/*  55 */       Column column = expressionColumn.getColumn();
/*  56 */       if (column == null) {
/*  57 */         throw expressionColumn.getColumnException(42122);
/*     */       }
/*  59 */       if (hashMap.putIfAbsent(column, expressionColumn) != null) {
/*  60 */         throw expressionColumn.getColumnException(42121);
/*     */       }
/*     */     } 
/*  63 */     return (HashMap)hashMap;
/*     */   }
/*     */ 
/*     */   
/*     */   public Value getValue(SessionLocal paramSessionLocal) {
/*  68 */     throw DbException.getInternalError(toString());
/*     */   }
/*     */ 
/*     */   
/*     */   public TypeInfo getType() {
/*  73 */     throw DbException.getInternalError(toString());
/*     */   }
/*     */ 
/*     */   
/*     */   public void mapColumns(ColumnResolver paramColumnResolver, int paramInt1, int paramInt2) {
/*  78 */     if (this.exceptColumns != null) {
/*  79 */       for (ExpressionColumn expressionColumn : this.exceptColumns) {
/*  80 */         expressionColumn.mapColumns(paramColumnResolver, paramInt1, paramInt2);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public Expression optimize(SessionLocal paramSessionLocal) {
/*  87 */     throw DbException.get(42000, this.table);
/*     */   }
/*     */ 
/*     */   
/*     */   public void setEvaluatable(TableFilter paramTableFilter, boolean paramBoolean) {
/*  92 */     throw DbException.getInternalError(toString());
/*     */   }
/*     */ 
/*     */   
/*     */   public String getTableAlias() {
/*  97 */     return this.table;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getSchemaName() {
/* 102 */     return this.schema;
/*     */   }
/*     */ 
/*     */   
/*     */   public StringBuilder getUnenclosedSQL(StringBuilder paramStringBuilder, int paramInt) {
/* 107 */     if (this.table != null) {
/* 108 */       StringUtils.quoteIdentifier(paramStringBuilder, this.table).append('.');
/*     */     }
/* 110 */     paramStringBuilder.append('*');
/* 111 */     if (this.exceptColumns != null) {
/* 112 */       writeExpressions(paramStringBuilder.append(" EXCEPT ("), (List)this.exceptColumns, paramInt).append(')');
/*     */     }
/* 114 */     return paramStringBuilder;
/*     */   }
/*     */ 
/*     */   
/*     */   public void updateAggregate(SessionLocal paramSessionLocal, int paramInt) {
/* 119 */     throw DbException.getInternalError(toString());
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isEverything(ExpressionVisitor paramExpressionVisitor) {
/* 124 */     if (paramExpressionVisitor.getType() == 8) {
/* 125 */       return true;
/*     */     }
/* 127 */     throw DbException.getInternalError(Integer.toString(paramExpressionVisitor.getType()));
/*     */   }
/*     */ 
/*     */   
/*     */   public int getCost() {
/* 132 */     throw DbException.getInternalError(toString());
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\expression\Wildcard.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */