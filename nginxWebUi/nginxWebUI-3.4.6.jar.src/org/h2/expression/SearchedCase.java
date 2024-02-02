/*    */ package org.h2.expression;
/*    */ 
/*    */ import org.h2.engine.CastDataProvider;
/*    */ import org.h2.engine.SessionLocal;
/*    */ import org.h2.value.TypeInfo;
/*    */ import org.h2.value.Value;
/*    */ import org.h2.value.ValueNull;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public final class SearchedCase
/*    */   extends OperationN
/*    */ {
/*    */   public SearchedCase() {
/* 19 */     super(new Expression[4]);
/*    */   }
/*    */   
/*    */   public SearchedCase(Expression[] paramArrayOfExpression) {
/* 23 */     super(paramArrayOfExpression);
/*    */   }
/*    */ 
/*    */   
/*    */   public Value getValue(SessionLocal paramSessionLocal) {
/* 28 */     int i = this.args.length - 1;
/* 29 */     for (byte b = 0; b < i; b += 2) {
/* 30 */       if (this.args[b].getBooleanValue(paramSessionLocal)) {
/* 31 */         return this.args[b + 1].getValue(paramSessionLocal).convertTo(this.type, (CastDataProvider)paramSessionLocal);
/*    */       }
/*    */     } 
/* 34 */     if ((i & 0x1) == 0) {
/* 35 */       return this.args[i].getValue(paramSessionLocal).convertTo(this.type, (CastDataProvider)paramSessionLocal);
/*    */     }
/* 37 */     return (Value)ValueNull.INSTANCE;
/*    */   }
/*    */ 
/*    */   
/*    */   public Expression optimize(SessionLocal paramSessionLocal) {
/* 42 */     TypeInfo typeInfo = TypeInfo.TYPE_UNKNOWN;
/* 43 */     int i = this.args.length - 1;
/* 44 */     boolean bool = true;
/* 45 */     for (byte b = 0; b < i; b += 2) {
/* 46 */       Expression expression1 = this.args[b].optimize(paramSessionLocal);
/* 47 */       Expression expression2 = this.args[b + 1].optimize(paramSessionLocal);
/* 48 */       if (bool) {
/* 49 */         if (expression1.isConstant()) {
/* 50 */           if (expression1.getBooleanValue(paramSessionLocal)) {
/* 51 */             return expression2;
/*    */           }
/*    */         } else {
/* 54 */           bool = false;
/*    */         } 
/*    */       }
/* 57 */       this.args[b] = expression1;
/* 58 */       this.args[b + 1] = expression2;
/* 59 */       typeInfo = SimpleCase.combineTypes(typeInfo, expression2);
/*    */     } 
/* 61 */     if ((i & 0x1) == 0) {
/* 62 */       Expression expression = this.args[i].optimize(paramSessionLocal);
/* 63 */       if (bool) {
/* 64 */         return expression;
/*    */       }
/* 66 */       this.args[i] = expression;
/* 67 */       typeInfo = SimpleCase.combineTypes(typeInfo, expression);
/* 68 */     } else if (bool) {
/* 69 */       return ValueExpression.NULL;
/*    */     } 
/* 71 */     if (typeInfo.getValueType() == -1) {
/* 72 */       typeInfo = TypeInfo.TYPE_VARCHAR;
/*    */     }
/* 74 */     this.type = typeInfo;
/* 75 */     return this;
/*    */   }
/*    */ 
/*    */   
/*    */   public StringBuilder getUnenclosedSQL(StringBuilder paramStringBuilder, int paramInt) {
/* 80 */     paramStringBuilder.append("CASE");
/* 81 */     int i = this.args.length - 1;
/* 82 */     for (byte b = 0; b < i; b += 2) {
/* 83 */       paramStringBuilder.append(" WHEN ");
/* 84 */       this.args[b].getUnenclosedSQL(paramStringBuilder, paramInt);
/* 85 */       paramStringBuilder.append(" THEN ");
/* 86 */       this.args[b + 1].getUnenclosedSQL(paramStringBuilder, paramInt);
/*    */     } 
/* 88 */     if ((i & 0x1) == 0) {
/* 89 */       paramStringBuilder.append(" ELSE ");
/* 90 */       this.args[i].getUnenclosedSQL(paramStringBuilder, paramInt);
/*    */     } 
/* 92 */     return paramStringBuilder.append(" END");
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\expression\SearchedCase.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */