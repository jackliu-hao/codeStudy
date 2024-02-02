/*    */ package org.h2.expression.function;
/*    */ 
/*    */ import org.h2.engine.SessionLocal;
/*    */ import org.h2.expression.Expression;
/*    */ import org.h2.expression.TypedValueExpression;
/*    */ import org.h2.message.DbException;
/*    */ import org.h2.util.MathUtils;
/*    */ import org.h2.value.TypeInfo;
/*    */ import org.h2.value.Value;
/*    */ import org.h2.value.ValueArray;
/*    */ import org.h2.value.ValueInteger;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public final class CardinalityExpression
/*    */   extends Function1
/*    */ {
/*    */   private final boolean max;
/*    */   
/*    */   public CardinalityExpression(Expression paramExpression, boolean paramBoolean) {
/* 36 */     super(paramExpression);
/* 37 */     this.max = paramBoolean;
/*    */   }
/*    */ 
/*    */   
/*    */   public Value getValue(SessionLocal paramSessionLocal) {
/*    */     int i;
/* 43 */     if (this.max) {
/* 44 */       TypeInfo typeInfo = this.arg.getType();
/* 45 */       if (typeInfo.getValueType() == 40) {
/* 46 */         i = MathUtils.convertLongToInt(typeInfo.getPrecision());
/*    */       } else {
/* 48 */         throw DbException.getInvalidValueException("array", this.arg.getValue(paramSessionLocal).getTraceSQL());
/*    */       } 
/*    */     } else {
/* 51 */       Value value = this.arg.getValue(paramSessionLocal);
/* 52 */       if (value == ValueNull.INSTANCE) {
/* 53 */         return (Value)ValueNull.INSTANCE;
/*    */       }
/* 55 */       if (value.getValueType() != 40) {
/* 56 */         throw DbException.getInvalidValueException("array", value.getTraceSQL());
/*    */       }
/* 58 */       i = (((ValueArray)value).getList()).length;
/*    */     } 
/* 60 */     return (Value)ValueInteger.get(i);
/*    */   }
/*    */ 
/*    */   
/*    */   public Expression optimize(SessionLocal paramSessionLocal) {
/* 65 */     this.arg = this.arg.optimize(paramSessionLocal);
/* 66 */     this.type = TypeInfo.TYPE_INTEGER;
/* 67 */     if (this.arg.isConstant()) {
/* 68 */       return (Expression)TypedValueExpression.getTypedIfNull(getValue(paramSessionLocal), this.type);
/*    */     }
/* 70 */     return (Expression)this;
/*    */   }
/*    */ 
/*    */   
/*    */   public String getName() {
/* 75 */     return this.max ? "ARRAY_MAX_CARDINALITY" : "CARDINALITY";
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\expression\function\CardinalityExpression.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */