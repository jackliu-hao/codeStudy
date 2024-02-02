/*    */ package org.h2.expression.function;
/*    */ 
/*    */ import org.h2.engine.CastDataProvider;
/*    */ import org.h2.engine.SessionLocal;
/*    */ import org.h2.expression.Expression;
/*    */ import org.h2.expression.TypedValueExpression;
/*    */ import org.h2.util.StringUtils;
/*    */ import org.h2.value.TypeInfo;
/*    */ import org.h2.value.Value;
/*    */ import org.h2.value.ValueVarchar;
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
/*    */ public final class TrimFunction
/*    */   extends Function1_2
/*    */ {
/*    */   public static final int LEADING = 1;
/*    */   public static final int TRAILING = 2;
/*    */   private int flags;
/*    */   
/*    */   public TrimFunction(Expression paramExpression1, Expression paramExpression2, int paramInt) {
/* 34 */     super(paramExpression1, paramExpression2);
/* 35 */     this.flags = paramInt;
/*    */   }
/*    */ 
/*    */   
/*    */   public Value getValue(SessionLocal paramSessionLocal, Value paramValue1, Value paramValue2) {
/* 40 */     return ValueVarchar.get(StringUtils.trim(paramValue1.getString(), ((this.flags & 0x1) != 0), ((this.flags & 0x2) != 0), (paramValue2 != null) ? paramValue2
/* 41 */           .getString() : " "), (CastDataProvider)paramSessionLocal);
/*    */   }
/*    */ 
/*    */   
/*    */   public Expression optimize(SessionLocal paramSessionLocal) {
/* 46 */     this.left = this.left.optimize(paramSessionLocal);
/* 47 */     if (this.right != null) {
/* 48 */       this.right = this.right.optimize(paramSessionLocal);
/*    */     }
/* 50 */     this.type = TypeInfo.getTypeInfo(2, this.left.getType().getPrecision(), 0, null);
/* 51 */     if (this.left.isConstant() && (this.right == null || this.right.isConstant())) {
/* 52 */       return (Expression)TypedValueExpression.getTypedIfNull(getValue(paramSessionLocal), this.type);
/*    */     }
/* 54 */     return (Expression)this;
/*    */   }
/*    */ 
/*    */   
/*    */   public StringBuilder getUnenclosedSQL(StringBuilder paramStringBuilder, int paramInt) {
/* 59 */     paramStringBuilder.append(getName()).append('(');
/* 60 */     boolean bool = false;
/* 61 */     switch (this.flags) {
/*    */       case 1:
/* 63 */         paramStringBuilder.append("LEADING ");
/* 64 */         bool = true;
/*    */         break;
/*    */       case 2:
/* 67 */         paramStringBuilder.append("TRAILING ");
/* 68 */         bool = true;
/*    */         break;
/*    */     } 
/* 71 */     if (this.right != null) {
/* 72 */       this.right.getUnenclosedSQL(paramStringBuilder, paramInt);
/* 73 */       bool = true;
/*    */     } 
/* 75 */     if (bool) {
/* 76 */       paramStringBuilder.append(" FROM ");
/*    */     }
/* 78 */     return this.left.getUnenclosedSQL(paramStringBuilder, paramInt).append(')');
/*    */   }
/*    */ 
/*    */   
/*    */   public String getName() {
/* 83 */     return "TRIM";
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\expression\function\TrimFunction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */