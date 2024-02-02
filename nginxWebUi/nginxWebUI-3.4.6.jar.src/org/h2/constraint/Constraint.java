/*     */ package org.h2.constraint;
/*     */ 
/*     */ import java.util.HashSet;
/*     */ import org.h2.engine.SessionLocal;
/*     */ import org.h2.expression.Expression;
/*     */ import org.h2.expression.ExpressionVisitor;
/*     */ import org.h2.index.Index;
/*     */ import org.h2.result.Row;
/*     */ import org.h2.schema.Schema;
/*     */ import org.h2.schema.SchemaObject;
/*     */ import org.h2.table.Column;
/*     */ import org.h2.table.Table;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class Constraint
/*     */   extends SchemaObject
/*     */   implements Comparable<Constraint>
/*     */ {
/*     */   protected Table table;
/*     */   
/*     */   public enum Type
/*     */   {
/*  30 */     CHECK,
/*     */ 
/*     */ 
/*     */     
/*  34 */     PRIMARY_KEY,
/*     */ 
/*     */ 
/*     */     
/*  38 */     UNIQUE,
/*     */ 
/*     */ 
/*     */     
/*  42 */     REFERENTIAL,
/*     */ 
/*     */ 
/*     */     
/*  46 */     DOMAIN;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public String getSqlName() {
/*  54 */       if (this == PRIMARY_KEY) {
/*  55 */         return "PRIMARY KEY";
/*     */       }
/*  57 */       if (this == REFERENTIAL) {
/*  58 */         return "FOREIGN KEY";
/*     */       }
/*  60 */       return name();
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   Constraint(Schema paramSchema, int paramInt, String paramString, Table paramTable) {
/*  71 */     super(paramSchema, paramInt, paramString, 1);
/*  72 */     this.table = paramTable;
/*  73 */     if (paramTable != null) {
/*  74 */       setTemporary(paramTable.isTemporary());
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Expression getExpression() {
/* 125 */     return null;
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
/*     */   
/*     */   public Index getIndex() {
/* 163 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ConstraintUnique getReferencedConstraint() {
/* 172 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getType() {
/* 177 */     return 5;
/*     */   }
/*     */   
/*     */   public Table getTable() {
/* 181 */     return this.table;
/*     */   }
/*     */   
/*     */   public Table getRefTable() {
/* 185 */     return this.table;
/*     */   }
/*     */ 
/*     */   
/*     */   public int compareTo(Constraint paramConstraint) {
/* 190 */     if (this == paramConstraint) {
/* 191 */       return 0;
/*     */     }
/* 193 */     return Integer.compare(getConstraintType().ordinal(), paramConstraint.getConstraintType().ordinal());
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isHidden() {
/* 198 */     return (this.table != null && this.table.isHidden());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isEverything(ExpressionVisitor paramExpressionVisitor) {
/* 209 */     return true;
/*     */   }
/*     */   
/*     */   public abstract Type getConstraintType();
/*     */   
/*     */   public abstract void checkRow(SessionLocal paramSessionLocal, Table paramTable, Row paramRow1, Row paramRow2);
/*     */   
/*     */   public abstract boolean usesIndex(Index paramIndex);
/*     */   
/*     */   public abstract void setIndexOwner(Index paramIndex);
/*     */   
/*     */   public abstract HashSet<Column> getReferencedColumns(Table paramTable);
/*     */   
/*     */   public abstract String getCreateSQLWithoutIndexes();
/*     */   
/*     */   public abstract boolean isBefore();
/*     */   
/*     */   public abstract void checkExistingData(SessionLocal paramSessionLocal);
/*     */   
/*     */   public abstract void rebuild();
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\constraint\Constraint.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */