/*     */ package org.h2.constraint;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashSet;
/*     */ import org.h2.engine.SessionLocal;
/*     */ import org.h2.index.Index;
/*     */ import org.h2.result.Row;
/*     */ import org.h2.schema.Schema;
/*     */ import org.h2.table.Column;
/*     */ import org.h2.table.IndexColumn;
/*     */ import org.h2.table.Table;
/*     */ import org.h2.util.StringUtils;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ConstraintUnique
/*     */   extends Constraint
/*     */ {
/*     */   private Index index;
/*     */   private boolean indexOwner;
/*     */   private IndexColumn[] columns;
/*     */   private final boolean primaryKey;
/*     */   
/*     */   public ConstraintUnique(Schema paramSchema, int paramInt, String paramString, Table paramTable, boolean paramBoolean) {
/*  31 */     super(paramSchema, paramInt, paramString, paramTable);
/*  32 */     this.primaryKey = paramBoolean;
/*     */   }
/*     */ 
/*     */   
/*     */   public Constraint.Type getConstraintType() {
/*  37 */     return this.primaryKey ? Constraint.Type.PRIMARY_KEY : Constraint.Type.UNIQUE;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getCreateSQLForCopy(Table paramTable, String paramString) {
/*  42 */     return getCreateSQLForCopy(paramTable, paramString, true);
/*     */   }
/*     */   
/*     */   private String getCreateSQLForCopy(Table paramTable, String paramString, boolean paramBoolean) {
/*  46 */     StringBuilder stringBuilder = new StringBuilder("ALTER TABLE ");
/*  47 */     paramTable.getSQL(stringBuilder, 0).append(" ADD CONSTRAINT ");
/*  48 */     if (paramTable.isHidden()) {
/*  49 */       stringBuilder.append("IF NOT EXISTS ");
/*     */     }
/*  51 */     stringBuilder.append(paramString);
/*  52 */     if (this.comment != null) {
/*  53 */       stringBuilder.append(" COMMENT ");
/*  54 */       StringUtils.quoteStringSQL(stringBuilder, this.comment);
/*     */     } 
/*  56 */     stringBuilder.append(' ').append(getConstraintType().getSqlName()).append('(');
/*  57 */     IndexColumn.writeColumns(stringBuilder, this.columns, 0).append(')');
/*  58 */     if (paramBoolean && this.indexOwner && paramTable == this.table) {
/*  59 */       stringBuilder.append(" INDEX ");
/*  60 */       this.index.getSQL(stringBuilder, 0);
/*     */     } 
/*  62 */     return stringBuilder.toString();
/*     */   }
/*     */ 
/*     */   
/*     */   public String getCreateSQLWithoutIndexes() {
/*  67 */     return getCreateSQLForCopy(this.table, getSQL(0), false);
/*     */   }
/*     */ 
/*     */   
/*     */   public String getCreateSQL() {
/*  72 */     return getCreateSQLForCopy(this.table, getSQL(0));
/*     */   }
/*     */   
/*     */   public void setColumns(IndexColumn[] paramArrayOfIndexColumn) {
/*  76 */     this.columns = paramArrayOfIndexColumn;
/*     */   }
/*     */   
/*     */   public IndexColumn[] getColumns() {
/*  80 */     return this.columns;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setIndex(Index paramIndex, boolean paramBoolean) {
/*  91 */     this.index = paramIndex;
/*  92 */     this.indexOwner = paramBoolean;
/*     */   }
/*     */ 
/*     */   
/*     */   public void removeChildrenAndResources(SessionLocal paramSessionLocal) {
/*  97 */     ArrayList arrayList = this.table.getConstraints();
/*  98 */     if (arrayList != null) {
/*  99 */       arrayList = new ArrayList(this.table.getConstraints());
/* 100 */       for (Constraint constraint : arrayList) {
/* 101 */         if (constraint.getReferencedConstraint() == this) {
/* 102 */           this.database.removeSchemaObject(paramSessionLocal, constraint);
/*     */         }
/*     */       } 
/*     */     } 
/* 106 */     this.table.removeConstraint(this);
/* 107 */     if (this.indexOwner) {
/* 108 */       this.table.removeIndexOrTransferOwnership(paramSessionLocal, this.index);
/*     */     }
/* 110 */     this.database.removeMeta(paramSessionLocal, getId());
/* 111 */     this.index = null;
/* 112 */     this.columns = null;
/* 113 */     this.table = null;
/* 114 */     invalidate();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void checkRow(SessionLocal paramSessionLocal, Table paramTable, Row paramRow1, Row paramRow2) {}
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean usesIndex(Index paramIndex) {
/* 124 */     return (paramIndex == this.index);
/*     */   }
/*     */ 
/*     */   
/*     */   public void setIndexOwner(Index paramIndex) {
/* 129 */     this.indexOwner = true;
/*     */   }
/*     */ 
/*     */   
/*     */   public HashSet<Column> getReferencedColumns(Table paramTable) {
/* 134 */     HashSet<Column> hashSet = new HashSet();
/* 135 */     for (IndexColumn indexColumn : this.columns) {
/* 136 */       hashSet.add(indexColumn.column);
/*     */     }
/* 138 */     return hashSet;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isBefore() {
/* 143 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void checkExistingData(SessionLocal paramSessionLocal) {}
/*     */ 
/*     */ 
/*     */   
/*     */   public Index getIndex() {
/* 154 */     return this.index;
/*     */   }
/*     */   
/*     */   public void rebuild() {}
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\constraint\ConstraintUnique.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */