/*     */ package org.h2.constraint;
/*     */ 
/*     */ import java.util.HashSet;
/*     */ import org.h2.engine.SessionLocal;
/*     */ import org.h2.expression.Expression;
/*     */ import org.h2.expression.ExpressionVisitor;
/*     */ import org.h2.index.Index;
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.result.ResultInterface;
/*     */ import org.h2.result.Row;
/*     */ import org.h2.schema.Schema;
/*     */ import org.h2.table.Column;
/*     */ import org.h2.table.Table;
/*     */ import org.h2.table.TableFilter;
/*     */ import org.h2.util.StringUtils;
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
/*     */ public class ConstraintCheck
/*     */   extends Constraint
/*     */ {
/*     */   private TableFilter filter;
/*     */   private Expression expr;
/*     */   
/*     */   public ConstraintCheck(Schema paramSchema, int paramInt, String paramString, Table paramTable) {
/*  33 */     super(paramSchema, paramInt, paramString, paramTable);
/*     */   }
/*     */ 
/*     */   
/*     */   public Constraint.Type getConstraintType() {
/*  38 */     return Constraint.Type.CHECK;
/*     */   }
/*     */   
/*     */   public void setTableFilter(TableFilter paramTableFilter) {
/*  42 */     this.filter = paramTableFilter;
/*     */   }
/*     */   
/*     */   public void setExpression(Expression paramExpression) {
/*  46 */     this.expr = paramExpression;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getCreateSQLForCopy(Table paramTable, String paramString) {
/*  51 */     StringBuilder stringBuilder = new StringBuilder("ALTER TABLE ");
/*  52 */     paramTable.getSQL(stringBuilder, 0).append(" ADD CONSTRAINT ");
/*  53 */     if (paramTable.isHidden()) {
/*  54 */       stringBuilder.append("IF NOT EXISTS ");
/*     */     }
/*  56 */     stringBuilder.append(paramString);
/*  57 */     if (this.comment != null) {
/*  58 */       stringBuilder.append(" COMMENT ");
/*  59 */       StringUtils.quoteStringSQL(stringBuilder, this.comment);
/*     */     } 
/*  61 */     stringBuilder.append(" CHECK");
/*  62 */     this.expr.getEnclosedSQL(stringBuilder, 0).append(" NOCHECK");
/*  63 */     return stringBuilder.toString();
/*     */   }
/*     */   
/*     */   private String getShortDescription() {
/*  67 */     StringBuilder stringBuilder = (new StringBuilder()).append(getName()).append(": ");
/*  68 */     this.expr.getTraceSQL();
/*  69 */     return stringBuilder.toString();
/*     */   }
/*     */ 
/*     */   
/*     */   public String getCreateSQLWithoutIndexes() {
/*  74 */     return getCreateSQL();
/*     */   }
/*     */ 
/*     */   
/*     */   public String getCreateSQL() {
/*  79 */     return getCreateSQLForCopy(this.table, getSQL(0));
/*     */   }
/*     */ 
/*     */   
/*     */   public void removeChildrenAndResources(SessionLocal paramSessionLocal) {
/*  84 */     this.table.removeConstraint(this);
/*  85 */     this.database.removeMeta(paramSessionLocal, getId());
/*  86 */     this.filter = null;
/*  87 */     this.expr = null;
/*  88 */     this.table = null;
/*  89 */     invalidate();
/*     */   }
/*     */   
/*     */   public void checkRow(SessionLocal paramSessionLocal, Table paramTable, Row paramRow1, Row paramRow2) {
/*     */     boolean bool;
/*  94 */     if (paramRow2 == null) {
/*     */       return;
/*     */     }
/*     */     
/*     */     try {
/*     */       Value value;
/* 100 */       synchronized (this) {
/* 101 */         this.filter.set(paramRow2);
/* 102 */         value = this.expr.getValue(paramSessionLocal);
/*     */       } 
/*     */       
/* 105 */       bool = value.isFalse();
/* 106 */     } catch (DbException dbException) {
/* 107 */       throw DbException.get(23514, dbException, new String[] { getShortDescription() });
/*     */     } 
/* 109 */     if (bool) {
/* 110 */       throw DbException.get(23513, getShortDescription());
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean usesIndex(Index paramIndex) {
/* 116 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setIndexOwner(Index paramIndex) {
/* 121 */     throw DbException.getInternalError(toString());
/*     */   }
/*     */ 
/*     */   
/*     */   public HashSet<Column> getReferencedColumns(Table paramTable) {
/* 126 */     HashSet<Column> hashSet = new HashSet();
/* 127 */     this.expr.isEverything(ExpressionVisitor.getColumnsVisitor(hashSet, paramTable));
/* 128 */     return hashSet;
/*     */   }
/*     */ 
/*     */   
/*     */   public Expression getExpression() {
/* 133 */     return this.expr;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isBefore() {
/* 138 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public void checkExistingData(SessionLocal paramSessionLocal) {
/* 143 */     if (paramSessionLocal.getDatabase().isStarting()) {
/*     */       return;
/*     */     }
/*     */     
/* 147 */     StringBuilder stringBuilder = (new StringBuilder()).append("SELECT NULL FROM ");
/* 148 */     this.filter.getTable().getSQL(stringBuilder, 0).append(" WHERE NOT ");
/* 149 */     this.expr.getSQL(stringBuilder, 0, 0);
/* 150 */     String str = stringBuilder.toString();
/* 151 */     ResultInterface resultInterface = paramSessionLocal.prepare(str).query(1L);
/* 152 */     if (resultInterface.next()) {
/* 153 */       throw DbException.get(23513, getName());
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void rebuild() {}
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isEverything(ExpressionVisitor paramExpressionVisitor) {
/* 164 */     return this.expr.isEverything(paramExpressionVisitor);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\constraint\ConstraintCheck.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */