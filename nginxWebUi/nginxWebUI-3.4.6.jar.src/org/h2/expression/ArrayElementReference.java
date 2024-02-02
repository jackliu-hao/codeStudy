/*    */ package org.h2.expression;
/*    */ 
/*    */ import org.h2.engine.SessionLocal;
/*    */ import org.h2.message.DbException;
/*    */ import org.h2.value.TypeInfo;
/*    */ import org.h2.value.Value;
/*    */ import org.h2.value.ValueArray;
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
/*    */ public final class ArrayElementReference
/*    */   extends Operation2
/*    */ {
/*    */   public ArrayElementReference(Expression paramExpression1, Expression paramExpression2) {
/* 22 */     super(paramExpression1, paramExpression2);
/*    */   }
/*    */ 
/*    */   
/*    */   public StringBuilder getUnenclosedSQL(StringBuilder paramStringBuilder, int paramInt) {
/* 27 */     this.left.getSQL(paramStringBuilder, paramInt, 0).append('[');
/* 28 */     return this.right.getUnenclosedSQL(paramStringBuilder, paramInt).append(']');
/*    */   }
/*    */ 
/*    */   
/*    */   public Value getValue(SessionLocal paramSessionLocal) {
/* 33 */     Value value1 = this.left.getValue(paramSessionLocal);
/* 34 */     Value value2 = this.right.getValue(paramSessionLocal);
/* 35 */     if (value1 != ValueNull.INSTANCE && value2 != ValueNull.INSTANCE) {
/* 36 */       Value[] arrayOfValue = ((ValueArray)value1).getList();
/* 37 */       int i = value2.getInt();
/* 38 */       int j = arrayOfValue.length;
/* 39 */       if (i >= 1 && i <= j) {
/* 40 */         return arrayOfValue[i - 1];
/*    */       }
/* 42 */       throw DbException.get(22034, new String[] { Integer.toString(i), "1.." + j });
/*    */     } 
/* 44 */     return (Value)ValueNull.INSTANCE;
/*    */   }
/*    */ 
/*    */   
/*    */   public Expression optimize(SessionLocal paramSessionLocal) {
/* 49 */     this.left = this.left.optimize(paramSessionLocal);
/* 50 */     this.right = this.right.optimize(paramSessionLocal);
/* 51 */     TypeInfo typeInfo = this.left.getType();
/* 52 */     switch (typeInfo.getValueType()) {
/*    */       case 0:
/* 54 */         return ValueExpression.NULL;
/*    */       case 40:
/* 56 */         this.type = (TypeInfo)typeInfo.getExtTypeInfo();
/* 57 */         if (this.left.isConstant() && this.right.isConstant()) {
/* 58 */           return TypedValueExpression.get(getValue(paramSessionLocal), this.type);
/*    */         }
/*    */ 
/*    */ 
/*    */ 
/*    */         
/* 64 */         return this;
/*    */     } 
/*    */     throw DbException.getInvalidExpressionTypeException("Array", this.left);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\expression\ArrayElementReference.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */