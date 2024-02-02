/*     */ package org.h2.expression;
/*     */ 
/*     */ import org.h2.engine.CastDataProvider;
/*     */ import org.h2.engine.SessionLocal;
/*     */ import org.h2.expression.function.DateTimeFunction;
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.value.DataType;
/*     */ import org.h2.value.TypeInfo;
/*     */ import org.h2.value.Value;
/*     */ import org.h2.value.ValueInteger;
/*     */ import org.h2.value.ValueNull;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class BinaryOperation
/*     */   extends Operation2
/*     */ {
/*     */   private OpType opType;
/*     */   private TypeInfo forcedType;
/*     */   
/*     */   public enum OpType
/*     */   {
/*  28 */     PLUS,
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  33 */     MINUS,
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  38 */     MULTIPLY,
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  43 */     DIVIDE;
/*     */   }
/*     */ 
/*     */   
/*     */   private boolean convertRight = true;
/*     */ 
/*     */   
/*     */   public BinaryOperation(OpType paramOpType, Expression paramExpression1, Expression paramExpression2) {
/*  51 */     super(paramExpression1, paramExpression2);
/*  52 */     this.opType = paramOpType;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setForcedType(TypeInfo paramTypeInfo) {
/*  61 */     if (this.opType != OpType.MINUS) {
/*  62 */       throw getUnexpectedForcedTypeException();
/*     */     }
/*  64 */     this.forcedType = paramTypeInfo;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean needParentheses() {
/*  69 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public StringBuilder getUnenclosedSQL(StringBuilder paramStringBuilder, int paramInt) {
/*  76 */     this.left.getSQL(paramStringBuilder, paramInt, 0).append(' ').append(getOperationToken()).append(' ');
/*  77 */     return this.right.getSQL(paramStringBuilder, paramInt, 0);
/*     */   }
/*     */   
/*     */   private String getOperationToken() {
/*  81 */     switch (this.opType) {
/*     */       case PLUS:
/*  83 */         return "+";
/*     */       case MINUS:
/*  85 */         return "-";
/*     */       case MULTIPLY:
/*  87 */         return "*";
/*     */       case DIVIDE:
/*  89 */         return "/";
/*     */     } 
/*  91 */     throw DbException.getInternalError("opType=" + this.opType);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Value getValue(SessionLocal paramSessionLocal) {
/*  97 */     Value value1 = this.left.getValue(paramSessionLocal).convertTo(this.type, (CastDataProvider)paramSessionLocal);
/*  98 */     Value value2 = this.right.getValue(paramSessionLocal);
/*  99 */     if (this.convertRight) {
/* 100 */       value2 = value2.convertTo(this.type, (CastDataProvider)paramSessionLocal);
/*     */     }
/* 102 */     switch (this.opType) {
/*     */       case PLUS:
/* 104 */         if (value1 == ValueNull.INSTANCE || value2 == ValueNull.INSTANCE) {
/* 105 */           return (Value)ValueNull.INSTANCE;
/*     */         }
/* 107 */         return value1.add(value2);
/*     */       case MINUS:
/* 109 */         if (value1 == ValueNull.INSTANCE || value2 == ValueNull.INSTANCE) {
/* 110 */           return (Value)ValueNull.INSTANCE;
/*     */         }
/* 112 */         return value1.subtract(value2);
/*     */       case MULTIPLY:
/* 114 */         if (value1 == ValueNull.INSTANCE || value2 == ValueNull.INSTANCE) {
/* 115 */           return (Value)ValueNull.INSTANCE;
/*     */         }
/* 117 */         return value1.multiply(value2);
/*     */       case DIVIDE:
/* 119 */         if (value1 == ValueNull.INSTANCE || value2 == ValueNull.INSTANCE) {
/* 120 */           return (Value)ValueNull.INSTANCE;
/*     */         }
/* 122 */         return value1.divide(value2, this.type);
/*     */     } 
/* 124 */     throw DbException.getInternalError("type=" + this.opType);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Expression optimize(SessionLocal paramSessionLocal) {
/* 130 */     this.left = this.left.optimize(paramSessionLocal);
/* 131 */     this.right = this.right.optimize(paramSessionLocal);
/* 132 */     TypeInfo typeInfo1 = this.left.getType(), typeInfo2 = this.right.getType();
/* 133 */     int i = typeInfo1.getValueType(), j = typeInfo2.getValueType();
/* 134 */     if ((i == 0 && j == 0) || (i == -1 && j == -1)) {
/*     */ 
/*     */       
/* 137 */       if (this.opType == OpType.PLUS && (paramSessionLocal.getDatabase().getMode()).allowPlusForStringConcat) {
/* 138 */         return (new ConcatenationOperation(this.left, this.right)).optimize(paramSessionLocal);
/*     */       }
/* 140 */       this.type = TypeInfo.TYPE_NUMERIC_FLOATING_POINT;
/*     */     } else {
/* 142 */       if (DataType.isIntervalType(i) || DataType.isIntervalType(j)) {
/* 143 */         if (this.forcedType != null) {
/* 144 */           throw getUnexpectedForcedTypeException();
/*     */         }
/* 146 */         return optimizeInterval(i, j);
/* 147 */       }  if (DataType.isDateTimeType(i) || DataType.isDateTimeType(j))
/* 148 */         return optimizeDateTime(paramSessionLocal, i, j); 
/* 149 */       if (this.forcedType != null) {
/* 150 */         throw getUnexpectedForcedTypeException();
/*     */       }
/* 152 */       int k = Value.getHigherOrder(i, j);
/* 153 */       if (k == 13)
/* 154 */       { optimizeNumeric(typeInfo1, typeInfo2); }
/* 155 */       else if (k == 16)
/* 156 */       { optimizeDecfloat(typeInfo1, typeInfo2); }
/* 157 */       else if (k == 36)
/* 158 */       { this.type = TypeInfo.TYPE_INTEGER; }
/* 159 */       else { if (DataType.isCharacterStringType(k) && this.opType == OpType.PLUS && 
/* 160 */           (paramSessionLocal.getDatabase().getMode()).allowPlusForStringConcat) {
/* 161 */           return (new ConcatenationOperation(this.left, this.right)).optimize(paramSessionLocal);
/*     */         }
/* 163 */         this.type = TypeInfo.getTypeInfo(k); }
/*     */     
/*     */     } 
/* 166 */     if (this.left.isConstant() && this.right.isConstant()) {
/* 167 */       return ValueExpression.get(getValue(paramSessionLocal));
/*     */     }
/* 169 */     return this; } private void optimizeNumeric(TypeInfo paramTypeInfo1, TypeInfo paramTypeInfo2) {
/*     */     long l3;
/*     */     int k;
/*     */     long l4;
/* 173 */     paramTypeInfo1 = paramTypeInfo1.toNumericType();
/* 174 */     paramTypeInfo2 = paramTypeInfo2.toNumericType();
/* 175 */     long l1 = paramTypeInfo1.getPrecision(), l2 = paramTypeInfo2.getPrecision();
/* 176 */     int i = paramTypeInfo1.getScale(), j = paramTypeInfo2.getScale();
/*     */ 
/*     */     
/* 179 */     switch (this.opType) {
/*     */ 
/*     */ 
/*     */ 
/*     */       
/*     */       case PLUS:
/*     */       case MINUS:
/* 186 */         if (i < j) {
/* 187 */           l1 += (j - i);
/* 188 */           int m = j;
/*     */         } else {
/* 190 */           l2 += (i - j);
/* 191 */           int m = i;
/*     */         } 
/*     */         
/* 194 */         l3 = Math.max(l1, l2) + 1L;
/*     */         break;
/*     */ 
/*     */ 
/*     */       
/*     */       case MULTIPLY:
/* 200 */         l3 = l1 + l2;
/* 201 */         k = i + j;
/*     */         break;
/*     */       
/*     */       case DIVIDE:
/* 205 */         l4 = (i - j) + l2 * 2L;
/* 206 */         if (l4 >= 100000L) {
/* 207 */           k = 100000;
/* 208 */         } else if (l4 <= 0L) {
/* 209 */           k = 0;
/*     */         } else {
/* 211 */           k = (int)l4;
/*     */         } 
/*     */ 
/*     */ 
/*     */         
/* 216 */         l3 = l1 + j - i + k;
/*     */         break;
/*     */       
/*     */       default:
/* 220 */         throw DbException.getInternalError("type=" + this.opType);
/*     */     } 
/* 222 */     this.type = TypeInfo.getTypeInfo(13, l3, k, null);
/*     */   }
/*     */   private void optimizeDecfloat(TypeInfo paramTypeInfo1, TypeInfo paramTypeInfo2) {
/*     */     long l3;
/* 226 */     paramTypeInfo1 = paramTypeInfo1.toDecfloatType();
/* 227 */     paramTypeInfo2 = paramTypeInfo2.toDecfloatType();
/* 228 */     long l1 = paramTypeInfo1.getPrecision(), l2 = paramTypeInfo2.getPrecision();
/*     */     
/* 230 */     switch (this.opType) {
/*     */       
/*     */       case PLUS:
/*     */       case MINUS:
/*     */       case DIVIDE:
/* 235 */         l3 = Math.max(l1, l2) + 1L;
/*     */         break;
/*     */       
/*     */       case MULTIPLY:
/* 239 */         l3 = l1 + l2;
/*     */         break;
/*     */       default:
/* 242 */         throw DbException.getInternalError("type=" + this.opType);
/*     */     } 
/* 244 */     this.type = TypeInfo.getTypeInfo(16, l3, 0, null);
/*     */   }
/*     */   
/*     */   private Expression optimizeInterval(int paramInt1, int paramInt2) {
/* 248 */     boolean bool1 = false, bool2 = false, bool3 = false;
/* 249 */     if (DataType.isIntervalType(paramInt1)) {
/* 250 */       bool1 = true;
/* 251 */     } else if (DataType.isNumericType(paramInt1)) {
/* 252 */       bool2 = true;
/* 253 */     } else if (DataType.isDateTimeType(paramInt1)) {
/* 254 */       bool3 = true;
/*     */     } else {
/* 256 */       throw getUnsupported(paramInt1, paramInt2);
/*     */     } 
/* 258 */     boolean bool4 = false, bool5 = false, bool6 = false;
/* 259 */     if (DataType.isIntervalType(paramInt2)) {
/* 260 */       bool4 = true;
/* 261 */     } else if (DataType.isNumericType(paramInt2)) {
/* 262 */       bool5 = true;
/* 263 */     } else if (DataType.isDateTimeType(paramInt2)) {
/* 264 */       bool6 = true;
/*     */     } else {
/* 266 */       throw getUnsupported(paramInt1, paramInt2);
/*     */     } 
/* 268 */     switch (this.opType) {
/*     */       case PLUS:
/* 270 */         if (bool1 && bool4) {
/* 271 */           if (DataType.isYearMonthIntervalType(paramInt1) == DataType.isYearMonthIntervalType(paramInt2))
/* 272 */             return new IntervalOperation(IntervalOperation.IntervalOpType.INTERVAL_PLUS_INTERVAL, this.left, this.right);  break;
/*     */         } 
/* 274 */         if (bool1 && bool6) {
/* 275 */           if (paramInt2 == 18 && DataType.isYearMonthIntervalType(paramInt1)) {
/*     */             break;
/*     */           }
/* 278 */           return new IntervalOperation(IntervalOperation.IntervalOpType.DATETIME_PLUS_INTERVAL, this.right, this.left);
/* 279 */         }  if (!bool3 || !bool4 || (
/* 280 */           paramInt1 == 18 && DataType.isYearMonthIntervalType(paramInt2))) {
/*     */           break;
/*     */         }
/* 283 */         return new IntervalOperation(IntervalOperation.IntervalOpType.DATETIME_PLUS_INTERVAL, this.left, this.right);
/*     */ 
/*     */       
/*     */       case MINUS:
/* 287 */         if (bool1 && bool4) {
/* 288 */           if (DataType.isYearMonthIntervalType(paramInt1) == DataType.isYearMonthIntervalType(paramInt2))
/* 289 */             return new IntervalOperation(IntervalOperation.IntervalOpType.INTERVAL_MINUS_INTERVAL, this.left, this.right);  break;
/*     */         } 
/* 291 */         if (!bool3 || !bool4 || (
/* 292 */           paramInt1 == 18 && DataType.isYearMonthIntervalType(paramInt2))) {
/*     */           break;
/*     */         }
/* 295 */         return new IntervalOperation(IntervalOperation.IntervalOpType.DATETIME_MINUS_INTERVAL, this.left, this.right);
/*     */ 
/*     */       
/*     */       case MULTIPLY:
/* 299 */         if (bool1 && bool5)
/* 300 */           return new IntervalOperation(IntervalOperation.IntervalOpType.INTERVAL_MULTIPLY_NUMERIC, this.left, this.right); 
/* 301 */         if (bool2 && bool4) {
/* 302 */           return new IntervalOperation(IntervalOperation.IntervalOpType.INTERVAL_MULTIPLY_NUMERIC, this.right, this.left);
/*     */         }
/*     */         break;
/*     */       case DIVIDE:
/* 306 */         if (bool1) {
/* 307 */           if (bool5)
/* 308 */             return new IntervalOperation(IntervalOperation.IntervalOpType.INTERVAL_DIVIDE_NUMERIC, this.left, this.right); 
/* 309 */           if (bool4 && DataType.isYearMonthIntervalType(paramInt1) == DataType.isYearMonthIntervalType(paramInt2))
/*     */           {
/* 311 */             return new IntervalOperation(IntervalOperation.IntervalOpType.INTERVAL_DIVIDE_INTERVAL, this.left, this.right);
/*     */           }
/*     */         } 
/*     */         break;
/*     */     } 
/*     */     
/* 317 */     throw getUnsupported(paramInt1, paramInt2);
/*     */   }
/*     */   
/*     */   private Expression optimizeDateTime(SessionLocal paramSessionLocal, int paramInt1, int paramInt2) {
/* 321 */     switch (this.opType) {
/*     */       case PLUS:
/* 323 */         if (DataType.isDateTimeType(paramInt1)) {
/* 324 */           if (DataType.isDateTimeType(paramInt2)) {
/* 325 */             if (paramInt1 > paramInt2) {
/* 326 */               swap();
/* 327 */               int j = paramInt1;
/* 328 */               paramInt1 = paramInt2;
/* 329 */               paramInt2 = j;
/*     */             } 
/* 331 */             return (new CompatibilityDatePlusTimeOperation(this.right, this.left)).optimize(paramSessionLocal);
/*     */           } 
/* 333 */           swap();
/* 334 */           int i = paramInt1;
/* 335 */           paramInt1 = paramInt2;
/* 336 */           paramInt2 = i;
/*     */         } 
/* 338 */         switch (paramInt1) {
/*     */           
/*     */           case 11:
/* 341 */             return (new DateTimeFunction(2, 2, this.left, this.right))
/* 342 */               .optimize(paramSessionLocal);
/*     */           
/*     */           case 13:
/*     */           case 14:
/*     */           case 15:
/*     */           case 16:
/* 348 */             return (new DateTimeFunction(2, 5, new BinaryOperation(OpType.MULTIPLY, 
/* 349 */                   ValueExpression.get((Value)ValueInteger.get(86400)), this.left), this.right))
/* 350 */               .optimize(paramSessionLocal);
/*     */         } 
/*     */         
/*     */         break;
/*     */       case MINUS:
/* 355 */         switch (paramInt1) {
/*     */           case 17:
/*     */           case 20:
/*     */           case 21:
/* 359 */             switch (paramInt2) {
/*     */               case 11:
/* 361 */                 if (this.forcedType != null) {
/* 362 */                   throw getUnexpectedForcedTypeException();
/*     */                 }
/*     */                 
/* 365 */                 return (new DateTimeFunction(2, 2, new UnaryOperation(this.right), this.left))
/* 366 */                   .optimize(paramSessionLocal);
/*     */               
/*     */               case 13:
/*     */               case 14:
/*     */               case 15:
/*     */               case 16:
/* 372 */                 if (this.forcedType != null) {
/* 373 */                   throw getUnexpectedForcedTypeException();
/*     */                 }
/*     */                 
/* 376 */                 return (new DateTimeFunction(2, 5, new BinaryOperation(OpType.MULTIPLY, 
/* 377 */                       ValueExpression.get((Value)ValueInteger.get(-86400)), this.right), this.left))
/* 378 */                   .optimize(paramSessionLocal);
/*     */               
/*     */               case 17:
/*     */               case 18:
/*     */               case 19:
/*     */               case 20:
/*     */               case 21:
/* 385 */                 return new IntervalOperation(IntervalOperation.IntervalOpType.DATETIME_MINUS_DATETIME, this.left, this.right, this.forcedType);
/*     */             } 
/*     */             break;
/*     */           case 18:
/*     */           case 19:
/* 390 */             if (DataType.isDateTimeType(paramInt2)) {
/* 391 */               return new IntervalOperation(IntervalOperation.IntervalOpType.DATETIME_MINUS_DATETIME, this.left, this.right, this.forcedType);
/*     */             }
/*     */             break;
/*     */         } 
/*     */         break;
/*     */       case MULTIPLY:
/* 397 */         if (paramInt1 == 18) {
/* 398 */           this.type = TypeInfo.TYPE_TIME;
/* 399 */           this.convertRight = false;
/* 400 */           return this;
/* 401 */         }  if (paramInt2 == 18) {
/* 402 */           swap();
/* 403 */           this.type = TypeInfo.TYPE_TIME;
/* 404 */           this.convertRight = false;
/* 405 */           return this;
/*     */         } 
/*     */         break;
/*     */       case DIVIDE:
/* 409 */         if (paramInt1 == 18) {
/* 410 */           this.type = TypeInfo.TYPE_TIME;
/* 411 */           this.convertRight = false;
/* 412 */           return this;
/*     */         } 
/*     */         break;
/*     */     } 
/*     */     
/* 417 */     throw getUnsupported(paramInt1, paramInt2);
/*     */   }
/*     */   
/*     */   private DbException getUnsupported(int paramInt1, int paramInt2) {
/* 421 */     return DbException.getUnsupportedException(
/* 422 */         Value.getTypeName(paramInt1) + ' ' + getOperationToken() + ' ' + Value.getTypeName(paramInt2));
/*     */   }
/*     */   
/*     */   private DbException getUnexpectedForcedTypeException() {
/* 426 */     StringBuilder stringBuilder = getUnenclosedSQL(new StringBuilder(), 3);
/* 427 */     int i = stringBuilder.length();
/* 428 */     return DbException.getSyntaxError(
/* 429 */         IntervalOperation.getForcedTypeSQL(stringBuilder.append(' '), this.forcedType).toString(), i, "");
/*     */   }
/*     */   
/*     */   private void swap() {
/* 433 */     Expression expression = this.left;
/* 434 */     this.left = this.right;
/* 435 */     this.right = expression;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public OpType getOperationType() {
/* 444 */     return this.opType;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\expression\BinaryOperation.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */