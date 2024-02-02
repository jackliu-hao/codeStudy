/*    */ package org.h2.expression.condition;
/*    */ 
/*    */ import java.util.Arrays;
/*    */ import org.h2.engine.SessionLocal;
/*    */ import org.h2.expression.Expression;
/*    */ import org.h2.value.TypeInfo;
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
/*    */ public final class TypePredicate
/*    */   extends SimplePredicate
/*    */ {
/*    */   private final TypeInfo[] typeList;
/*    */   private int[] valueTypes;
/*    */   
/*    */   public TypePredicate(Expression paramExpression, boolean paramBoolean1, boolean paramBoolean2, TypeInfo[] paramArrayOfTypeInfo) {
/* 26 */     super(paramExpression, paramBoolean1, paramBoolean2);
/* 27 */     this.typeList = paramArrayOfTypeInfo;
/*    */   }
/*    */ 
/*    */   
/*    */   public StringBuilder getUnenclosedSQL(StringBuilder paramStringBuilder, int paramInt) {
/* 32 */     return getWhenSQL(this.left.getSQL(paramStringBuilder, paramInt, 0), paramInt);
/*    */   }
/*    */ 
/*    */   
/*    */   public StringBuilder getWhenSQL(StringBuilder paramStringBuilder, int paramInt) {
/* 37 */     paramStringBuilder.append(" IS");
/* 38 */     if (this.not) {
/* 39 */       paramStringBuilder.append(" NOT");
/*    */     }
/* 41 */     paramStringBuilder.append(" OF (");
/* 42 */     for (byte b = 0; b < this.typeList.length; b++) {
/* 43 */       if (b > 0) {
/* 44 */         paramStringBuilder.append(", ");
/*    */       }
/* 46 */       this.typeList[b].getSQL(paramStringBuilder, paramInt);
/*    */     } 
/* 48 */     return paramStringBuilder.append(')');
/*    */   }
/*    */ 
/*    */   
/*    */   public Expression optimize(SessionLocal paramSessionLocal) {
/* 53 */     int i = this.typeList.length;
/* 54 */     this.valueTypes = new int[i];
/* 55 */     for (byte b = 0; b < i; b++) {
/* 56 */       this.valueTypes[b] = this.typeList[b].getValueType();
/*    */     }
/* 58 */     Arrays.sort(this.valueTypes);
/* 59 */     return super.optimize(paramSessionLocal);
/*    */   }
/*    */ 
/*    */   
/*    */   public Value getValue(SessionLocal paramSessionLocal) {
/* 64 */     Value value = this.left.getValue(paramSessionLocal);
/* 65 */     if (value == ValueNull.INSTANCE) {
/* 66 */       return (Value)ValueNull.INSTANCE;
/*    */     }
/* 68 */     return (Value)ValueBoolean.get(((Arrays.binarySearch(this.valueTypes, value.getValueType()) >= 0)) ^ this.not);
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean getWhenValue(SessionLocal paramSessionLocal, Value paramValue) {
/* 73 */     if (!this.whenOperand) {
/* 74 */       return super.getWhenValue(paramSessionLocal, paramValue);
/*    */     }
/* 76 */     if (paramValue == ValueNull.INSTANCE) {
/* 77 */       return false;
/*    */     }
/* 79 */     return ((Arrays.binarySearch(this.valueTypes, paramValue.getValueType()) >= 0)) ^ this.not;
/*    */   }
/*    */ 
/*    */   
/*    */   public Expression getNotIfPossible(SessionLocal paramSessionLocal) {
/* 84 */     if (this.whenOperand) {
/* 85 */       return null;
/*    */     }
/* 87 */     return new TypePredicate(this.left, !this.not, false, this.typeList);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\expression\condition\TypePredicate.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */