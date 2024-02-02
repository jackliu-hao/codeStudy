/*    */ package org.h2.expression;
/*    */ 
/*    */ import java.util.ArrayList;
/*    */ import org.h2.command.query.Query;
/*    */ import org.h2.engine.CastDataProvider;
/*    */ import org.h2.engine.SessionLocal;
/*    */ import org.h2.message.DbException;
/*    */ import org.h2.result.ResultInterface;
/*    */ import org.h2.table.ColumnResolver;
/*    */ import org.h2.table.TableFilter;
/*    */ import org.h2.util.StringUtils;
/*    */ import org.h2.value.ExtTypeInfo;
/*    */ import org.h2.value.TypeInfo;
/*    */ import org.h2.value.Value;
/*    */ import org.h2.value.ValueArray;
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
/*    */ public final class ArrayConstructorByQuery
/*    */   extends Expression
/*    */ {
/*    */   private final Query query;
/*    */   private TypeInfo componentType;
/*    */   private TypeInfo type;
/*    */   
/*    */   public ArrayConstructorByQuery(Query paramQuery) {
/* 41 */     this.query = paramQuery;
/*    */   }
/*    */ 
/*    */   
/*    */   public StringBuilder getUnenclosedSQL(StringBuilder paramStringBuilder, int paramInt) {
/* 46 */     return StringUtils.indent(paramStringBuilder.append("ARRAY ("), this.query.getPlanSQL(paramInt), 4, false).append(')');
/*    */   }
/*    */ 
/*    */   
/*    */   public Value getValue(SessionLocal paramSessionLocal) {
/* 51 */     this.query.setSession(paramSessionLocal);
/* 52 */     ArrayList<Value> arrayList = new ArrayList();
/* 53 */     try (ResultInterface null = this.query.query(0L)) {
/* 54 */       while (resultInterface.next()) {
/* 55 */         arrayList.add(resultInterface.currentRow()[0]);
/*    */       }
/*    */     } 
/* 58 */     return (Value)ValueArray.get(this.componentType, arrayList.<Value>toArray(new Value[0]), (CastDataProvider)paramSessionLocal);
/*    */   }
/*    */ 
/*    */   
/*    */   public TypeInfo getType() {
/* 63 */     return this.type;
/*    */   }
/*    */ 
/*    */   
/*    */   public void mapColumns(ColumnResolver paramColumnResolver, int paramInt1, int paramInt2) {
/* 68 */     this.query.mapColumns(paramColumnResolver, paramInt1 + 1);
/*    */   }
/*    */ 
/*    */   
/*    */   public Expression optimize(SessionLocal paramSessionLocal) {
/* 73 */     this.query.prepare();
/* 74 */     if (this.query.getColumnCount() != 1) {
/* 75 */       throw DbException.get(90052);
/*    */     }
/* 77 */     this.componentType = ((Expression)this.query.getExpressions().get(0)).getType();
/* 78 */     this.type = TypeInfo.getTypeInfo(40, -1L, -1, (ExtTypeInfo)this.componentType);
/* 79 */     return this;
/*    */   }
/*    */ 
/*    */   
/*    */   public void setEvaluatable(TableFilter paramTableFilter, boolean paramBoolean) {
/* 84 */     this.query.setEvaluatable(paramTableFilter, paramBoolean);
/*    */   }
/*    */ 
/*    */   
/*    */   public void updateAggregate(SessionLocal paramSessionLocal, int paramInt) {
/* 89 */     this.query.updateAggregate(paramSessionLocal, paramInt);
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean isEverything(ExpressionVisitor paramExpressionVisitor) {
/* 94 */     return this.query.isEverything(paramExpressionVisitor);
/*    */   }
/*    */ 
/*    */   
/*    */   public int getCost() {
/* 99 */     return this.query.getCostAsExpression();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\expression\ArrayConstructorByQuery.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */