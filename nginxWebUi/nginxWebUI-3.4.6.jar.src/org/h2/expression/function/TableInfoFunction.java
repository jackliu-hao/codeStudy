/*     */ package org.h2.expression.function;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import org.h2.command.Parser;
/*     */ import org.h2.engine.SessionLocal;
/*     */ import org.h2.expression.Expression;
/*     */ import org.h2.expression.ExpressionVisitor;
/*     */ import org.h2.index.Index;
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.mvstore.db.MVSpatialIndex;
/*     */ import org.h2.table.Column;
/*     */ import org.h2.table.Table;
/*     */ import org.h2.value.TypeInfo;
/*     */ import org.h2.value.Value;
/*     */ import org.h2.value.ValueBigint;
/*     */ import org.h2.value.ValueNull;
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
/*     */ public final class TableInfoFunction
/*     */   extends Function1_2
/*     */ {
/*     */   public static final int DISK_SPACE_USED = 0;
/*     */   public static final int ESTIMATED_ENVELOPE = 1;
/*  39 */   private static final String[] NAMES = new String[] { "DISK_SPACE_USED", "ESTIMATED_ENVELOPE" };
/*     */ 
/*     */   
/*     */   private final int function;
/*     */ 
/*     */   
/*     */   public TableInfoFunction(Expression paramExpression1, Expression paramExpression2, int paramInt) {
/*  46 */     super(paramExpression1, paramExpression2);
/*  47 */     this.function = paramInt;
/*     */   } public Value getValue(SessionLocal paramSessionLocal, Value paramValue1, Value paramValue2) {
/*     */     ValueBigint valueBigint;
/*     */     Column column;
/*     */     ArrayList<Index> arrayList;
/*  52 */     Table table = (new Parser(paramSessionLocal)).parseTableName(paramValue1.getString());
/*  53 */     switch (this.function) {
/*     */       case 0:
/*  55 */         return (Value)ValueBigint.get(table.getDiskSpaceUsed());
/*     */       
/*     */       case 1:
/*  58 */         column = table.getColumn(paramValue2.getString());
/*  59 */         arrayList = table.getIndexes();
/*  60 */         if (arrayList != null) {
/*  61 */           byte b; int i; for (b = 1, i = arrayList.size(); b < i; b++) {
/*  62 */             Index index = arrayList.get(b);
/*  63 */             if (index instanceof MVSpatialIndex && index.isFirstColumn(column)) {
/*  64 */               Value value = ((MVSpatialIndex)index).getEstimatedBounds(paramSessionLocal);
/*     */               // Byte code: goto -> 182
/*     */             } 
/*     */           } 
/*     */         } 
/*  69 */         return (Value)ValueNull.INSTANCE;
/*     */     } 
/*     */ 
/*     */     
/*  73 */     throw DbException.getInternalError("function=" + this.function);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Expression optimize(SessionLocal paramSessionLocal) {
/*  80 */     this.left = this.left.optimize(paramSessionLocal);
/*  81 */     if (this.right != null) {
/*  82 */       this.right = this.right.optimize(paramSessionLocal);
/*     */     }
/*  84 */     switch (this.function) {
/*     */       case 0:
/*  86 */         this.type = TypeInfo.TYPE_BIGINT;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/*  94 */         return (Expression)this;case 1: this.type = TypeInfo.TYPE_GEOMETRY; return (Expression)this;
/*     */     } 
/*     */     throw DbException.getInternalError("function=" + this.function);
/*     */   }
/*     */   public boolean isEverything(ExpressionVisitor paramExpressionVisitor) {
/*  99 */     switch (paramExpressionVisitor.getType()) {
/*     */       case 2:
/* 101 */         return false;
/*     */     } 
/* 103 */     return super.isEverything(paramExpressionVisitor);
/*     */   }
/*     */ 
/*     */   
/*     */   public String getName() {
/* 108 */     return NAMES[this.function];
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\expression\function\TableInfoFunction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */