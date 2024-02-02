/*     */ package org.h2.table;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import org.h2.engine.SessionLocal;
/*     */ import org.h2.expression.Expression;
/*     */ import org.h2.index.Index;
/*     */ import org.h2.index.RangeIndex;
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.schema.Schema;
/*     */ import org.h2.value.TypeInfo;
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
/*     */ public class RangeTable
/*     */   extends VirtualTable
/*     */ {
/*     */   public static final String NAME = "SYSTEM_RANGE";
/*     */   public static final String ALIAS = "GENERATE_SERIES";
/*     */   private Expression min;
/*     */   private Expression max;
/*     */   private Expression step;
/*     */   private boolean optimized;
/*     */   private final RangeIndex index;
/*     */   
/*     */   public RangeTable(Schema paramSchema, Expression paramExpression1, Expression paramExpression2) {
/*  47 */     super(paramSchema, 0, "SYSTEM_RANGE");
/*  48 */     this.min = paramExpression1;
/*  49 */     this.max = paramExpression2;
/*  50 */     Column[] arrayOfColumn = { new Column("X", TypeInfo.TYPE_BIGINT) };
/*  51 */     setColumns(arrayOfColumn);
/*  52 */     this.index = new RangeIndex(this, IndexColumn.wrap(arrayOfColumn));
/*     */   }
/*     */   
/*     */   public RangeTable(Schema paramSchema, Expression paramExpression1, Expression paramExpression2, Expression paramExpression3) {
/*  56 */     this(paramSchema, paramExpression1, paramExpression2);
/*  57 */     this.step = paramExpression3;
/*     */   }
/*     */ 
/*     */   
/*     */   public StringBuilder getSQL(StringBuilder paramStringBuilder, int paramInt) {
/*  62 */     paramStringBuilder.append("SYSTEM_RANGE").append('(');
/*  63 */     this.min.getUnenclosedSQL(paramStringBuilder, paramInt).append(", ");
/*  64 */     this.max.getUnenclosedSQL(paramStringBuilder, paramInt);
/*  65 */     if (this.step != null) {
/*  66 */       this.step.getUnenclosedSQL(paramStringBuilder.append(", "), paramInt);
/*     */     }
/*  68 */     return paramStringBuilder.append(')');
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean canGetRowCount(SessionLocal paramSessionLocal) {
/*  73 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getRowCount(SessionLocal paramSessionLocal) {
/*  78 */     long l1 = getStep(paramSessionLocal);
/*  79 */     if (l1 == 0L) {
/*  80 */       throw DbException.get(90142);
/*     */     }
/*  82 */     long l2 = getMax(paramSessionLocal) - getMin(paramSessionLocal);
/*  83 */     if (l1 > 0L) {
/*  84 */       if (l2 < 0L) {
/*  85 */         return 0L;
/*     */       }
/*  87 */     } else if (l2 > 0L) {
/*  88 */       return 0L;
/*     */     } 
/*  90 */     return l2 / l1 + 1L;
/*     */   }
/*     */ 
/*     */   
/*     */   public TableType getTableType() {
/*  95 */     return TableType.SYSTEM_TABLE;
/*     */   }
/*     */ 
/*     */   
/*     */   public Index getScanIndex(SessionLocal paramSessionLocal) {
/* 100 */     return (Index)this.index;
/*     */   }
/*     */ 
/*     */   
/*     */   public ArrayList<Index> getIndexes() {
/* 105 */     ArrayList<RangeIndex> arrayList = new ArrayList(2);
/*     */     
/* 107 */     arrayList.add(this.index);
/*     */     
/* 109 */     arrayList.add(this.index);
/* 110 */     return (ArrayList)arrayList;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long getMin(SessionLocal paramSessionLocal) {
/* 120 */     optimize(paramSessionLocal);
/* 121 */     return this.min.getValue(paramSessionLocal).getLong();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long getMax(SessionLocal paramSessionLocal) {
/* 131 */     optimize(paramSessionLocal);
/* 132 */     return this.max.getValue(paramSessionLocal).getLong();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long getStep(SessionLocal paramSessionLocal) {
/* 142 */     optimize(paramSessionLocal);
/* 143 */     if (this.step == null) {
/* 144 */       return 1L;
/*     */     }
/* 146 */     return this.step.getValue(paramSessionLocal).getLong();
/*     */   }
/*     */   
/*     */   private void optimize(SessionLocal paramSessionLocal) {
/* 150 */     if (!this.optimized) {
/* 151 */       this.min = this.min.optimize(paramSessionLocal);
/* 152 */       this.max = this.max.optimize(paramSessionLocal);
/* 153 */       if (this.step != null) {
/* 154 */         this.step = this.step.optimize(paramSessionLocal);
/*     */       }
/* 156 */       this.optimized = true;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public long getMaxDataModificationId() {
/* 162 */     return 0L;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getRowCountApproximation(SessionLocal paramSessionLocal) {
/* 167 */     return 100L;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isDeterministic() {
/* 172 */     return true;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\table\RangeTable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */