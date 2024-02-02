/*     */ package org.h2.expression.function;
/*     */ 
/*     */ import java.math.BigDecimal;
/*     */ import java.math.MathContext;
/*     */ import org.h2.engine.CastDataProvider;
/*     */ import org.h2.engine.SessionLocal;
/*     */ import org.h2.expression.Expression;
/*     */ import org.h2.expression.TypedValueExpression;
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.util.MathUtils;
/*     */ import org.h2.value.DataType;
/*     */ import org.h2.value.TypeInfo;
/*     */ import org.h2.value.Value;
/*     */ import org.h2.value.ValueDecfloat;
/*     */ import org.h2.value.ValueNumeric;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class TruncateValueFunction
/*     */   extends FunctionN
/*     */ {
/*     */   public TruncateValueFunction(Expression paramExpression1, Expression paramExpression2, Expression paramExpression3) {
/*  29 */     super(new Expression[] { paramExpression1, paramExpression2, paramExpression3 });
/*     */   }
/*     */ 
/*     */   
/*     */   public Value getValue(SessionLocal paramSessionLocal, Value paramValue1, Value paramValue2, Value paramValue3) {
/*  34 */     long l = paramValue2.getLong();
/*  35 */     boolean bool = paramValue3.getBoolean();
/*  36 */     if (l <= 0L) {
/*  37 */       throw DbException.get(90150, new String[] { Long.toString(l), "1", "2147483647" });
/*     */     }
/*     */     
/*  40 */     TypeInfo typeInfo = paramValue1.getType();
/*  41 */     int i = typeInfo.getValueType();
/*  42 */     if ((DataType.getDataType(i)).supportsPrecision) {
/*  43 */       if (l < typeInfo.getPrecision()) {
/*  44 */         BigDecimal bigDecimal; switch (i) {
/*     */           case 13:
/*  46 */             bigDecimal = paramValue1.getBigDecimal().round(new MathContext(MathUtils.convertLongToInt(l)));
/*  47 */             if (bigDecimal.scale() < 0) {
/*  48 */               bigDecimal = bigDecimal.setScale(0);
/*     */             }
/*  50 */             return (Value)ValueNumeric.get(bigDecimal);
/*     */           
/*     */           case 16:
/*  53 */             return 
/*  54 */               (Value)ValueDecfloat.get(paramValue1.getBigDecimal().round(new MathContext(MathUtils.convertLongToInt(l))));
/*     */         } 
/*  56 */         return paramValue1.castTo(TypeInfo.getTypeInfo(i, l, typeInfo.getScale(), typeInfo.getExtTypeInfo()), (CastDataProvider)paramSessionLocal);
/*     */       }
/*     */     
/*     */     }
/*  60 */     else if (bool) {
/*     */       
/*  62 */       switch (i) {
/*     */         case 9:
/*     */         case 10:
/*     */         case 11:
/*  66 */           bigDecimal = BigDecimal.valueOf(paramValue1.getInt());
/*     */           break;
/*     */         case 12:
/*  69 */           bigDecimal = BigDecimal.valueOf(paramValue1.getLong());
/*     */           break;
/*     */         case 14:
/*     */         case 15:
/*  73 */           bigDecimal = paramValue1.getBigDecimal();
/*     */           break;
/*     */         default:
/*  76 */           return paramValue1;
/*     */       } 
/*  78 */       BigDecimal bigDecimal = bigDecimal.round(new MathContext(MathUtils.convertLongToInt(l)));
/*  79 */       if (i == 16) {
/*  80 */         return (Value)ValueDecfloat.get(bigDecimal);
/*     */       }
/*  82 */       if (bigDecimal.scale() < 0) {
/*  83 */         bigDecimal = bigDecimal.setScale(0);
/*     */       }
/*  85 */       return ValueNumeric.get(bigDecimal).convertTo(i);
/*     */     } 
/*  87 */     return paramValue1;
/*     */   }
/*     */ 
/*     */   
/*     */   public Expression optimize(SessionLocal paramSessionLocal) {
/*  92 */     boolean bool = optimizeArguments(paramSessionLocal, true);
/*  93 */     this.type = this.args[0].getType();
/*  94 */     if (bool) {
/*  95 */       return (Expression)TypedValueExpression.getTypedIfNull(getValue(paramSessionLocal), this.type);
/*     */     }
/*  97 */     return (Expression)this;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getName() {
/* 102 */     return "TRUNCATE_VALUE";
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\expression\function\TruncateValueFunction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */