/*     */ package org.h2.expression;
/*     */ 
/*     */ import java.util.Arrays;
/*     */ import org.h2.engine.CastDataProvider;
/*     */ import org.h2.engine.SessionLocal;
/*     */ import org.h2.expression.function.CastSpecification;
/*     */ import org.h2.expression.function.ConcatFunction;
/*     */ import org.h2.value.DataType;
/*     */ import org.h2.value.TypeInfo;
/*     */ import org.h2.value.Typed;
/*     */ import org.h2.value.Value;
/*     */ import org.h2.value.ValueArray;
/*     */ import org.h2.value.ValueNull;
/*     */ import org.h2.value.ValueVarbinary;
/*     */ import org.h2.value.ValueVarchar;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class ConcatenationOperation
/*     */   extends OperationN
/*     */ {
/*     */   public ConcatenationOperation() {
/*  29 */     super(new Expression[4]);
/*     */   }
/*     */   
/*     */   public ConcatenationOperation(Expression paramExpression1, Expression paramExpression2) {
/*  33 */     super(new Expression[] { paramExpression1, paramExpression2 });
/*  34 */     this.argsCount = 2;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean needParentheses() {
/*  39 */     return true;
/*     */   }
/*     */   public StringBuilder getUnenclosedSQL(StringBuilder paramStringBuilder, int paramInt) {
/*     */     byte b;
/*     */     int i;
/*  44 */     for (b = 0, i = this.args.length; b < i; b++) {
/*  45 */       if (b > 0) {
/*  46 */         paramStringBuilder.append(" || ");
/*     */       }
/*  48 */       this.args[b].getSQL(paramStringBuilder, paramInt, 0);
/*     */     } 
/*  50 */     return paramStringBuilder;
/*     */   }
/*     */ 
/*     */   
/*     */   public Value getValue(SessionLocal paramSessionLocal) {
/*  55 */     int i = this.args.length;
/*  56 */     if (i == 2) {
/*  57 */       Value value1 = this.args[0].getValue(paramSessionLocal);
/*  58 */       value1 = value1.convertTo(this.type, (CastDataProvider)paramSessionLocal);
/*  59 */       if (value1 == ValueNull.INSTANCE) {
/*  60 */         return (Value)ValueNull.INSTANCE;
/*     */       }
/*  62 */       Value value2 = this.args[1].getValue(paramSessionLocal);
/*  63 */       value2 = value2.convertTo(this.type, (CastDataProvider)paramSessionLocal);
/*  64 */       if (value2 == ValueNull.INSTANCE) {
/*  65 */         return (Value)ValueNull.INSTANCE;
/*     */       }
/*  67 */       return getValue(paramSessionLocal, value1, value2);
/*     */     } 
/*  69 */     return getValue(paramSessionLocal, i);
/*     */   }
/*     */   
/*     */   private Value getValue(SessionLocal paramSessionLocal, Value paramValue1, Value paramValue2) {
/*  73 */     int i = this.type.getValueType();
/*  74 */     if (i == 2) {
/*  75 */       String str1 = paramValue1.getString(), str2 = paramValue2.getString();
/*  76 */       return ValueVarchar.get((new StringBuilder(str1.length() + str2.length())).append(str1).append(str2).toString());
/*  77 */     }  if (i == 6) {
/*  78 */       byte[] arrayOfByte1 = paramValue1.getBytesNoCopy(), arrayOfByte2 = paramValue2.getBytesNoCopy();
/*  79 */       int m = arrayOfByte1.length, n = arrayOfByte2.length;
/*  80 */       byte[] arrayOfByte3 = Arrays.copyOf(arrayOfByte1, m + n);
/*  81 */       System.arraycopy(arrayOfByte2, 0, arrayOfByte3, m, n);
/*  82 */       return (Value)ValueVarbinary.getNoCopy(arrayOfByte3);
/*     */     } 
/*  84 */     Value[] arrayOfValue1 = ((ValueArray)paramValue1).getList(), arrayOfValue2 = ((ValueArray)paramValue2).getList();
/*  85 */     int j = arrayOfValue1.length, k = arrayOfValue2.length;
/*  86 */     Value[] arrayOfValue3 = Arrays.<Value>copyOf(arrayOfValue1, j + k);
/*  87 */     System.arraycopy(arrayOfValue2, 0, arrayOfValue3, j, k);
/*  88 */     return (Value)ValueArray.get((TypeInfo)this.type.getExtTypeInfo(), arrayOfValue3, (CastDataProvider)paramSessionLocal);
/*     */   }
/*     */ 
/*     */   
/*     */   private Value getValue(SessionLocal paramSessionLocal, int paramInt) {
/*  93 */     Value[] arrayOfValue1 = new Value[paramInt]; int i;
/*  94 */     for (i = 0; i < paramInt; i++) {
/*  95 */       Value value = this.args[i].getValue(paramSessionLocal).convertTo(this.type, (CastDataProvider)paramSessionLocal);
/*  96 */       if (value == ValueNull.INSTANCE) {
/*  97 */         return (Value)ValueNull.INSTANCE;
/*     */       }
/*  99 */       arrayOfValue1[i] = value;
/*     */     } 
/* 101 */     i = this.type.getValueType();
/* 102 */     if (i == 2) {
/* 103 */       StringBuilder stringBuilder = new StringBuilder();
/* 104 */       for (byte b = 0; b < paramInt; b++) {
/* 105 */         stringBuilder.append(arrayOfValue1[b].getString());
/*     */       }
/* 107 */       return ValueVarchar.get(stringBuilder.toString(), (CastDataProvider)paramSessionLocal);
/* 108 */     }  if (i == 6) {
/* 109 */       int m = 0;
/* 110 */       for (byte b3 = 0; b3 < paramInt; b3++) {
/* 111 */         m += (arrayOfValue1[b3].getBytesNoCopy()).length;
/*     */       }
/* 113 */       byte[] arrayOfByte = new byte[m];
/* 114 */       int n = 0;
/* 115 */       for (byte b4 = 0; b4 < paramInt; b4++) {
/* 116 */         byte[] arrayOfByte1 = arrayOfValue1[b4].getBytesNoCopy();
/* 117 */         int i1 = arrayOfByte1.length;
/* 118 */         System.arraycopy(arrayOfByte1, 0, arrayOfByte, n, i1);
/* 119 */         n += i1;
/*     */       } 
/* 121 */       return (Value)ValueVarbinary.getNoCopy(arrayOfByte);
/*     */     } 
/* 123 */     int j = 0;
/* 124 */     for (byte b1 = 0; b1 < paramInt; b1++) {
/* 125 */       j += (((ValueArray)arrayOfValue1[b1]).getList()).length;
/*     */     }
/* 127 */     Value[] arrayOfValue2 = new Value[j];
/* 128 */     int k = 0;
/* 129 */     for (byte b2 = 0; b2 < paramInt; b2++) {
/* 130 */       Value[] arrayOfValue = ((ValueArray)arrayOfValue1[b2]).getList();
/* 131 */       int m = arrayOfValue.length;
/* 132 */       System.arraycopy(arrayOfValue, 0, arrayOfValue2, k, m);
/* 133 */       k += m;
/*     */     } 
/* 135 */     return (Value)ValueArray.get((TypeInfo)this.type.getExtTypeInfo(), arrayOfValue2, (CastDataProvider)paramSessionLocal);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Expression optimize(SessionLocal paramSessionLocal) {
/* 141 */     determineType(paramSessionLocal);
/* 142 */     inlineArguments();
/* 143 */     if (this.type.getValueType() == 2 && (paramSessionLocal.getMode()).treatEmptyStringsAsNull) {
/* 144 */       return (new ConcatFunction(0, this.args)).optimize(paramSessionLocal);
/*     */     }
/* 146 */     int i = this.args.length;
/* 147 */     boolean bool1 = true, bool2 = false; byte b;
/* 148 */     for (b = 0; b < i; b++) {
/* 149 */       if (this.args[b].isConstant()) {
/* 150 */         bool2 = true;
/*     */       } else {
/* 152 */         bool1 = false;
/*     */       } 
/*     */     } 
/* 155 */     if (bool1) {
/* 156 */       return TypedValueExpression.getTypedIfNull(getValue(paramSessionLocal), this.type);
/*     */     }
/* 158 */     if (bool2) {
/* 159 */       b = 0;
/* 160 */       for (byte b1 = 0; b1 < i; b1++) {
/* 161 */         Expression expression = this.args[b1];
/* 162 */         if (expression.isConstant()) {
/* 163 */           Value value = expression.getValue(paramSessionLocal).convertTo(this.type, (CastDataProvider)paramSessionLocal);
/* 164 */           if (value == ValueNull.INSTANCE) {
/* 165 */             return TypedValueExpression.get((Value)ValueNull.INSTANCE, this.type);
/*     */           }
/* 167 */           if (isEmpty(value))
/*     */             continue; 
/*     */           Expression expression1;
/* 170 */           for (; b1 + 1 < i && (expression1 = this.args[b1 + 1]).isConstant(); b1++) {
/* 171 */             Value value1 = expression1.getValue(paramSessionLocal).convertTo(this.type, (CastDataProvider)paramSessionLocal);
/* 172 */             if (value1 == ValueNull.INSTANCE) {
/* 173 */               return TypedValueExpression.get((Value)ValueNull.INSTANCE, this.type);
/*     */             }
/* 175 */             if (!isEmpty(value1)) {
/* 176 */               value = getValue(paramSessionLocal, value, value1);
/*     */             }
/*     */           } 
/* 179 */           expression = ValueExpression.get(value);
/*     */         } 
/* 181 */         this.args[b++] = expression; continue;
/*     */       } 
/* 183 */       if (b == 1) {
/* 184 */         Expression expression = this.args[0];
/* 185 */         TypeInfo typeInfo = expression.getType();
/* 186 */         if (TypeInfo.areSameTypes(this.type, typeInfo)) {
/* 187 */           return expression;
/*     */         }
/* 189 */         return (Expression)new CastSpecification(expression, this.type);
/*     */       } 
/* 191 */       this.argsCount = b;
/* 192 */       doneWithParameters();
/*     */     } 
/* 194 */     return this;
/*     */   }
/*     */   
/*     */   private void determineType(SessionLocal paramSessionLocal) {
/* 198 */     int i = this.args.length;
/* 199 */     boolean bool1 = false, bool2 = true, bool3 = true;
/* 200 */     for (byte b = 0; b < i; b++) {
/* 201 */       Expression expression = this.args[b].optimize(paramSessionLocal);
/* 202 */       this.args[b] = expression;
/* 203 */       int j = expression.getType().getValueType();
/* 204 */       if (j == 40) {
/* 205 */         bool1 = true;
/* 206 */         bool2 = bool3 = false;
/* 207 */       } else if (j != 0) {
/*     */         
/* 209 */         if (DataType.isBinaryStringType(j)) {
/* 210 */           bool3 = false;
/* 211 */         } else if (DataType.isCharacterStringType(j)) {
/* 212 */           bool2 = false;
/*     */         } else {
/* 214 */           bool2 = bool3 = false;
/*     */         } 
/*     */       } 
/* 217 */     }  if (bool1) {
/* 218 */       this.type = TypeInfo.getTypeInfo(40, -1L, 0, TypeInfo.getHigherType((Typed[])this.args).getExtTypeInfo());
/* 219 */     } else if (bool2) {
/* 220 */       long l = getPrecision(0);
/* 221 */       for (byte b1 = 1; b1 < i; b1++) {
/* 222 */         l = DataType.addPrecision(l, getPrecision(b1));
/*     */       }
/* 224 */       this.type = TypeInfo.getTypeInfo(6, l, 0, null);
/* 225 */     } else if (bool3) {
/* 226 */       long l = getPrecision(0);
/* 227 */       for (byte b1 = 1; b1 < i; b1++) {
/* 228 */         l = DataType.addPrecision(l, getPrecision(b1));
/*     */       }
/* 230 */       this.type = TypeInfo.getTypeInfo(2, l, 0, null);
/*     */     } else {
/* 232 */       this.type = TypeInfo.TYPE_VARCHAR;
/*     */     } 
/*     */   }
/*     */   
/*     */   private long getPrecision(int paramInt) {
/* 237 */     TypeInfo typeInfo = this.args[paramInt].getType();
/* 238 */     return (typeInfo.getValueType() != 0) ? typeInfo.getPrecision() : 0L;
/*     */   }
/*     */   
/*     */   private void inlineArguments() {
/* 242 */     int i = this.type.getValueType();
/* 243 */     int j = this.args.length;
/* 244 */     int k = j;
/* 245 */     for (byte b = 0; b < j; b++) {
/* 246 */       Expression expression = this.args[b];
/* 247 */       if (expression instanceof ConcatenationOperation && expression.getType().getValueType() == i) {
/* 248 */         k += expression.getSubexpressionCount() - 1;
/*     */       }
/*     */     } 
/* 251 */     if (k > j) {
/* 252 */       Expression[] arrayOfExpression = new Expression[k]; byte b1; int m;
/* 253 */       for (b1 = 0, m = 0; b1 < j; b1++) {
/* 254 */         Expression expression = this.args[b1];
/* 255 */         if (expression instanceof ConcatenationOperation && expression.getType().getValueType() == i) {
/* 256 */           ConcatenationOperation concatenationOperation = (ConcatenationOperation)expression;
/* 257 */           Expression[] arrayOfExpression1 = concatenationOperation.args;
/* 258 */           int n = arrayOfExpression1.length;
/* 259 */           System.arraycopy(arrayOfExpression1, 0, arrayOfExpression, m, n);
/* 260 */           m += n;
/*     */         } else {
/* 262 */           arrayOfExpression[m++] = expression;
/*     */         } 
/*     */       } 
/* 265 */       this.args = arrayOfExpression;
/* 266 */       this.argsCount = k;
/*     */     } 
/*     */   }
/*     */   
/*     */   private static boolean isEmpty(Value paramValue) {
/* 271 */     int i = paramValue.getValueType();
/* 272 */     if (i == 2)
/* 273 */       return paramValue.getString().isEmpty(); 
/* 274 */     if (i == 6) {
/* 275 */       return ((paramValue.getBytesNoCopy()).length == 0);
/*     */     }
/* 277 */     return ((((ValueArray)paramValue).getList()).length == 0);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\expression\ConcatenationOperation.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */