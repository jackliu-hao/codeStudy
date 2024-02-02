/*    */ package org.h2.expression.condition;
/*    */ 
/*    */ import java.util.ArrayList;
/*    */ import org.h2.engine.SessionLocal;
/*    */ import org.h2.expression.Expression;
/*    */ import org.h2.expression.ExpressionColumn;
/*    */ import org.h2.expression.TypedValueExpression;
/*    */ import org.h2.expression.ValueExpression;
/*    */ import org.h2.index.IndexCondition;
/*    */ import org.h2.table.TableFilter;
/*    */ import org.h2.value.Value;
/*    */ import org.h2.value.ValueBoolean;
/*    */ import org.h2.value.ValueNull;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public final class BooleanTest
/*    */   extends SimplePredicate
/*    */ {
/*    */   private final Boolean right;
/*    */   
/*    */   public BooleanTest(Expression paramExpression, boolean paramBoolean1, boolean paramBoolean2, Boolean paramBoolean) {
/* 29 */     super(paramExpression, paramBoolean1, paramBoolean2);
/* 30 */     this.right = paramBoolean;
/*    */   }
/*    */ 
/*    */   
/*    */   public StringBuilder getUnenclosedSQL(StringBuilder paramStringBuilder, int paramInt) {
/* 35 */     return getWhenSQL(this.left.getSQL(paramStringBuilder, paramInt, 0), paramInt);
/*    */   }
/*    */ 
/*    */   
/*    */   public StringBuilder getWhenSQL(StringBuilder paramStringBuilder, int paramInt) {
/* 40 */     return paramStringBuilder.append(this.not ? " IS NOT " : " IS ").append((this.right == null) ? "UNKNOWN" : (this.right.booleanValue() ? "TRUE" : "FALSE"));
/*    */   }
/*    */ 
/*    */   
/*    */   public Value getValue(SessionLocal paramSessionLocal) {
/* 45 */     return (Value)ValueBoolean.get(getValue(this.left.getValue(paramSessionLocal)));
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean getWhenValue(SessionLocal paramSessionLocal, Value paramValue) {
/* 50 */     if (!this.whenOperand) {
/* 51 */       return super.getWhenValue(paramSessionLocal, paramValue);
/*    */     }
/* 53 */     return getValue(paramValue);
/*    */   }
/*    */   
/*    */   private boolean getValue(Value paramValue) {
/* 57 */     return ((paramValue == ValueNull.INSTANCE) ? ((this.right == null)) : ((this.right != null && this.right.booleanValue() == paramValue.getBoolean()))) ^ this.not;
/*    */   }
/*    */ 
/*    */   
/*    */   public Expression getNotIfPossible(SessionLocal paramSessionLocal) {
/* 62 */     if (this.whenOperand) {
/* 63 */       return null;
/*    */     }
/* 65 */     return new BooleanTest(this.left, !this.not, false, this.right);
/*    */   }
/*    */ 
/*    */   
/*    */   public void createIndexConditions(SessionLocal paramSessionLocal, TableFilter paramTableFilter) {
/* 70 */     if (this.whenOperand || !paramTableFilter.getTable().isQueryComparable()) {
/*    */       return;
/*    */     }
/* 73 */     if (this.left instanceof ExpressionColumn) {
/* 74 */       ExpressionColumn expressionColumn = (ExpressionColumn)this.left;
/* 75 */       if (expressionColumn.getType().getValueType() == 8 && paramTableFilter == expressionColumn.getTableFilter())
/* 76 */         if (this.not) {
/* 77 */           if (this.right == null && expressionColumn.getColumn().isNullable()) {
/* 78 */             ArrayList<ValueExpression> arrayList = new ArrayList(2);
/* 79 */             arrayList.add(ValueExpression.FALSE);
/* 80 */             arrayList.add(ValueExpression.TRUE);
/* 81 */             paramTableFilter.addIndexCondition(IndexCondition.getInList(expressionColumn, arrayList));
/*    */           } 
/*    */         } else {
/* 84 */           paramTableFilter.addIndexCondition(IndexCondition.get(6, expressionColumn, (this.right == null) ? (Expression)TypedValueExpression.UNKNOWN : 
/* 85 */                 (Expression)ValueExpression.getBoolean(this.right.booleanValue())));
/*    */         }  
/*    */     } 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\expression\condition\BooleanTest.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */