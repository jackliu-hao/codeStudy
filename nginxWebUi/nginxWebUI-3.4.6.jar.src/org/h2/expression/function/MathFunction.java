/*     */ package org.h2.expression.function;
/*     */ 
/*     */ import java.math.BigDecimal;
/*     */ import java.math.RoundingMode;
/*     */ import org.h2.engine.CastDataProvider;
/*     */ import org.h2.engine.SessionLocal;
/*     */ import org.h2.expression.Expression;
/*     */ import org.h2.expression.TypedValueExpression;
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.value.DataType;
/*     */ import org.h2.value.TypeInfo;
/*     */ import org.h2.value.Value;
/*     */ import org.h2.value.ValueBigint;
/*     */ import org.h2.value.ValueDecfloat;
/*     */ import org.h2.value.ValueDouble;
/*     */ import org.h2.value.ValueInteger;
/*     */ import org.h2.value.ValueNull;
/*     */ import org.h2.value.ValueNumeric;
/*     */ import org.h2.value.ValueReal;
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
/*     */ public final class MathFunction
/*     */   extends Function1_2
/*     */ {
/*     */   public static final int ABS = 0;
/*     */   public static final int MOD = 1;
/*     */   public static final int FLOOR = 2;
/*     */   public static final int CEIL = 3;
/*     */   public static final int ROUND = 4;
/*     */   public static final int ROUNDMAGIC = 5;
/*     */   public static final int SIGN = 6;
/*     */   public static final int TRUNC = 7;
/*  72 */   private static final String[] NAMES = new String[] { "ABS", "MOD", "FLOOR", "CEIL", "ROUND", "ROUNDMAGIC", "SIGN", "TRUNC" };
/*     */ 
/*     */   
/*     */   private final int function;
/*     */   
/*     */   private TypeInfo commonType;
/*     */ 
/*     */   
/*     */   public MathFunction(Expression paramExpression1, Expression paramExpression2, int paramInt) {
/*  81 */     super(paramExpression1, paramExpression2);
/*  82 */     this.function = paramInt;
/*     */   }
/*     */   
/*     */   public Value getValue(SessionLocal paramSessionLocal, Value paramValue1, Value paramValue2) { ValueDouble valueDouble;
/*     */     ValueInteger valueInteger;
/*  87 */     switch (this.function) {
/*     */       case 0:
/*  89 */         if (paramValue1.getSignum() < 0) {
/*  90 */           paramValue1 = paramValue1.negate();
/*     */         }
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
/*     */         
/* 117 */         return paramValue1;case 1: paramValue1 = paramValue1.convertTo(this.commonType, (CastDataProvider)paramSessionLocal).modulus(paramValue2.convertTo(this.commonType, (CastDataProvider)paramSessionLocal)).convertTo(this.type, (CastDataProvider)paramSessionLocal); return paramValue1;case 2: paramValue1 = round(paramValue1, paramValue2, RoundingMode.FLOOR); return paramValue1;case 3: paramValue1 = round(paramValue1, paramValue2, RoundingMode.CEILING); return paramValue1;case 4: paramValue1 = round(paramValue1, paramValue2, RoundingMode.HALF_UP); return paramValue1;case 5: valueDouble = ValueDouble.get(roundMagic(paramValue1.getDouble())); return (Value)valueDouble;case 6: valueInteger = ValueInteger.get(valueDouble.getSignum()); return (Value)valueInteger;
/*     */       case 7:
/*     */         return round((Value)valueInteger, paramValue2, RoundingMode.DOWN);
/*     */     }  throw DbException.getInternalError("function=" + this.function); } private Value round(Value paramValue1, Value paramValue2, RoundingMode paramRoundingMode) { ValueNumeric valueNumeric; ValueDecfloat valueDecfloat; int j;
/*     */     BigDecimal bigDecimal;
/* 122 */     byte b = (paramValue2 != null) ? paramValue2.getInt() : 0;
/* 123 */     int i = this.type.getValueType();
/* 124 */     switch (i) {
/*     */       case 9:
/*     */       case 10:
/*     */       case 11:
/*     */       case 12:
/* 129 */         if (b) {
/* 130 */           long l1 = paramValue1.getLong();
/* 131 */           long l2 = BigDecimal.valueOf(l1).setScale(b, paramRoundingMode).longValue();
/* 132 */           if (l1 != l2) {
/* 133 */             paramValue1 = ValueBigint.get(l2).convertTo(this.type);
/*     */           }
/*     */         } 
/*     */         break;
/*     */       
/*     */       case 13:
/* 139 */         j = this.type.getScale();
/* 140 */         bigDecimal = paramValue1.getBigDecimal();
/* 141 */         if (b < j) {
/* 142 */           bigDecimal = bigDecimal.setScale(b, paramRoundingMode);
/*     */         }
/* 144 */         valueNumeric = ValueNumeric.get(bigDecimal.setScale(j, paramRoundingMode));
/*     */         break;
/*     */       
/*     */       case 14:
/*     */       case 15:
/* 149 */         if (b == 0) {
/*     */           double d; BigDecimal bigDecimal1;
/* 151 */           switch (paramRoundingMode) {
/*     */             case DOWN:
/* 153 */               d = valueNumeric.getDouble();
/* 154 */               d = (d < 0.0D) ? Math.ceil(d) : Math.floor(d);
/*     */               break;
/*     */             case CEILING:
/* 157 */               d = Math.ceil(valueNumeric.getDouble());
/*     */               break;
/*     */             case FLOOR:
/* 160 */               d = Math.floor(valueNumeric.getDouble());
/*     */               break;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */             
/*     */             default:
/* 168 */               bigDecimal1 = valueNumeric.getBigDecimal().setScale(b, paramRoundingMode);
/* 169 */               valueNumeric = (i == 14) ? (ValueNumeric)ValueReal.get(bigDecimal1.floatValue()) : (ValueNumeric)ValueDouble.get(bigDecimal1.doubleValue()); break;
/*     */           }  valueNumeric = (i == 14) ? (ValueNumeric)ValueReal.get((float)bigDecimal1) : (ValueNumeric)ValueDouble.get(bigDecimal1); break;
/*     */         } 
/*     */       case 16:
/* 173 */         valueDecfloat = ValueDecfloat.get(valueNumeric.getBigDecimal().setScale(b, paramRoundingMode)); break;
/*     */     } 
/* 175 */     return (Value)valueDecfloat; }
/*     */ 
/*     */   
/*     */   private static double roundMagic(double paramDouble) {
/* 179 */     if (paramDouble < 1.0E-13D && paramDouble > -1.0E-13D) {
/* 180 */       return 0.0D;
/*     */     }
/* 182 */     if (paramDouble > 1.0E12D || paramDouble < -1.0E12D) {
/* 183 */       return paramDouble;
/*     */     }
/* 185 */     StringBuilder stringBuilder = new StringBuilder();
/* 186 */     stringBuilder.append(paramDouble);
/* 187 */     if (stringBuilder.toString().indexOf('E') >= 0) {
/* 188 */       return paramDouble;
/*     */     }
/* 190 */     int i = stringBuilder.length();
/* 191 */     if (i < 16) {
/* 192 */       return paramDouble;
/*     */     }
/* 194 */     if (stringBuilder.toString().indexOf('.') > i - 3) {
/* 195 */       return paramDouble;
/*     */     }
/* 197 */     stringBuilder.delete(i - 2, i);
/* 198 */     i -= 2;
/* 199 */     char c1 = stringBuilder.charAt(i - 2);
/* 200 */     char c2 = stringBuilder.charAt(i - 3);
/* 201 */     char c3 = stringBuilder.charAt(i - 4);
/* 202 */     if (c1 == '0' && c2 == '0' && c3 == '0') {
/* 203 */       stringBuilder.setCharAt(i - 1, '0');
/* 204 */     } else if (c1 == '9' && c2 == '9' && c3 == '9') {
/* 205 */       stringBuilder.setCharAt(i - 1, '9');
/* 206 */       stringBuilder.append('9');
/* 207 */       stringBuilder.append('9');
/* 208 */       stringBuilder.append('9');
/*     */     } 
/* 210 */     return Double.parseDouble(stringBuilder.toString()); } public Expression optimize(SessionLocal paramSessionLocal) {
/*     */     Expression expression1;
/*     */     TypeInfo typeInfo;
/*     */     int i;
/*     */     Expression expression2;
/* 215 */     this.left = this.left.optimize(paramSessionLocal);
/* 216 */     if (this.right != null) {
/* 217 */       this.right = this.right.optimize(paramSessionLocal);
/*     */     }
/* 219 */     switch (this.function) {
/*     */       case 0:
/* 221 */         this.type = this.left.getType();
/* 222 */         if (this.type.getValueType() == 0) {
/* 223 */           this.type = TypeInfo.TYPE_NUMERIC_FLOATING_POINT;
/*     */         }
/*     */         break;
/*     */       case 2:
/*     */       case 3:
/* 228 */         expression1 = optimizeRound(0, true, false, true);
/* 229 */         if (expression1 != null) {
/* 230 */           return expression1;
/*     */         }
/*     */         break;
/*     */       
/*     */       case 1:
/* 235 */         typeInfo = this.right.getType();
/* 236 */         this.commonType = TypeInfo.getHigherType(this.left.getType(), typeInfo);
/* 237 */         i = this.commonType.getValueType();
/* 238 */         if (i == 0) {
/* 239 */           this.commonType = TypeInfo.TYPE_BIGINT;
/* 240 */         } else if (!DataType.isNumericType(i)) {
/* 241 */           throw DbException.getInvalidExpressionTypeException("MOD argument", 
/* 242 */               DataType.isNumericType(this.left.getType().getValueType()) ? this.right : this.left);
/*     */         } 
/* 244 */         this.type = DataType.isNumericType(typeInfo.getValueType()) ? typeInfo : this.commonType;
/*     */         break;
/*     */       case 4:
/* 247 */         expression2 = optimizeRoundWithScale(paramSessionLocal, true);
/* 248 */         if (expression2 != null) {
/* 249 */           return expression2;
/*     */         }
/*     */         break;
/*     */       
/*     */       case 5:
/* 254 */         this.type = TypeInfo.TYPE_DOUBLE;
/*     */         break;
/*     */       case 6:
/* 257 */         this.type = TypeInfo.TYPE_INTEGER;
/*     */         break;
/*     */       case 7:
/* 260 */         switch (this.left.getType().getValueType()) {
/*     */           case 2:
/* 262 */             this
/* 263 */               .left = (new CastSpecification(this.left, TypeInfo.getTypeInfo(20, -1L, 0, null))).optimize(paramSessionLocal);
/*     */           
/*     */           case 20:
/*     */           case 21:
/* 267 */             if (this.right != null) {
/* 268 */               throw DbException.get(7001, new String[] { "TRUNC", "1" });
/*     */             }
/* 270 */             return (new DateTimeFunction(1, 2, this.left, null))
/* 271 */               .optimize(paramSessionLocal);
/*     */           case 17:
/* 273 */             if (this.right != null) {
/* 274 */               throw DbException.get(7001, new String[] { "TRUNC", "1" });
/*     */             }
/* 276 */             return (new CastSpecification(this.left, TypeInfo.getTypeInfo(20, -1L, 0, null)))
/* 277 */               .optimize(paramSessionLocal);
/*     */         } 
/* 279 */         expression2 = optimizeRoundWithScale(paramSessionLocal, false);
/* 280 */         if (expression2 != null) {
/* 281 */           return expression2;
/*     */         }
/*     */         break;
/*     */ 
/*     */       
/*     */       default:
/* 287 */         throw DbException.getInternalError("function=" + this.function);
/*     */     } 
/* 289 */     if (this.left.isConstant() && (this.right == null || this.right.isConstant())) {
/* 290 */       return (Expression)TypedValueExpression.getTypedIfNull(getValue(paramSessionLocal), this.type);
/*     */     }
/* 292 */     return (Expression)this;
/*     */   }
/*     */ 
/*     */   
/*     */   private Expression optimizeRoundWithScale(SessionLocal paramSessionLocal, boolean paramBoolean) {
/* 297 */     boolean bool1, bool2 = false, bool3 = false;
/* 298 */     if (this.right != null) {
/* 299 */       if (this.right.isConstant()) {
/* 300 */         Value value = this.right.getValue(paramSessionLocal);
/* 301 */         bool2 = true;
/* 302 */         if (value != ValueNull.INSTANCE) {
/* 303 */           bool1 = value.getInt();
/*     */         } else {
/* 305 */           bool1 = true;
/* 306 */           bool3 = true;
/*     */         } 
/*     */       } else {
/* 309 */         bool1 = true;
/*     */       } 
/*     */     } else {
/* 312 */       bool1 = false;
/* 313 */       bool2 = true;
/*     */     } 
/* 315 */     return optimizeRound(bool1, bool2, bool3, paramBoolean);
/*     */   }
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
/*     */   private Expression optimizeRound(int paramInt, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3) {
/*     */     long l;
/*     */     int i;
/* 334 */     TypeInfo typeInfo = this.left.getType();
/* 335 */     switch (typeInfo.getValueType()) {
/*     */       case 0:
/* 337 */         this.type = TypeInfo.TYPE_NUMERIC_SCALE_0;
/*     */         break;
/*     */       case 9:
/*     */       case 10:
/*     */       case 11:
/*     */       case 12:
/* 343 */         if (paramBoolean1 && paramInt >= 0) {
/* 344 */           return this.left;
/*     */         }
/* 346 */         this.type = typeInfo;
/*     */         break;
/*     */       case 14:
/*     */       case 15:
/*     */       case 16:
/* 351 */         this.type = typeInfo;
/*     */         break;
/*     */       
/*     */       case 13:
/* 355 */         i = typeInfo.getScale();
/* 356 */         if (paramBoolean1) {
/* 357 */           if (i <= paramInt) {
/* 358 */             return this.left;
/*     */           }
/* 360 */           if (paramInt < 0) {
/* 361 */             paramInt = 0;
/* 362 */           } else if (paramInt > 100000) {
/* 363 */             paramInt = 100000;
/*     */           } 
/* 365 */           l = typeInfo.getPrecision() - i + paramInt;
/* 366 */           if (paramBoolean3) {
/* 367 */             l++;
/*     */           }
/*     */         } else {
/*     */           
/* 371 */           l = typeInfo.getPrecision();
/* 372 */           if (paramBoolean3) {
/* 373 */             l++;
/*     */           }
/* 375 */           paramInt = i;
/*     */         } 
/* 377 */         this.type = TypeInfo.getTypeInfo(13, l, paramInt, null);
/*     */         break;
/*     */       
/*     */       default:
/* 381 */         throw DbException.getInvalidExpressionTypeException(getName() + " argument", this.left);
/*     */     } 
/* 383 */     if (paramBoolean2) {
/* 384 */       return (Expression)TypedValueExpression.get((Value)ValueNull.INSTANCE, this.type);
/*     */     }
/* 386 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getName() {
/* 391 */     return NAMES[this.function];
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\expression\function\MathFunction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */