/*     */ package org.h2.value;
/*     */ 
/*     */ import java.math.BigDecimal;
/*     */ import java.math.RoundingMode;
/*     */ import org.h2.engine.CastDataProvider;
/*     */ import org.h2.message.DbException;
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
/*     */ public final class ValueDecfloat
/*     */   extends ValueBigDecimalBase
/*     */ {
/*  23 */   public static final ValueDecfloat ZERO = new ValueDecfloat(BigDecimal.ZERO);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  28 */   public static final ValueDecfloat ONE = new ValueDecfloat(BigDecimal.ONE);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  33 */   public static final ValueDecfloat POSITIVE_INFINITY = new ValueDecfloat(null);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  38 */   public static final ValueDecfloat NEGATIVE_INFINITY = new ValueDecfloat(null);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  43 */   public static final ValueDecfloat NAN = new ValueDecfloat(null);
/*     */   
/*     */   private ValueDecfloat(BigDecimal paramBigDecimal) {
/*  46 */     super(paramBigDecimal);
/*     */   }
/*     */ 
/*     */   
/*     */   public String getString() {
/*  51 */     if (this.value == null) {
/*  52 */       if (this == POSITIVE_INFINITY)
/*  53 */         return "Infinity"; 
/*  54 */       if (this == NEGATIVE_INFINITY) {
/*  55 */         return "-Infinity";
/*     */       }
/*  57 */       return "NaN";
/*     */     } 
/*     */     
/*  60 */     return this.value.toString();
/*     */   }
/*     */ 
/*     */   
/*     */   public StringBuilder getSQL(StringBuilder paramStringBuilder, int paramInt) {
/*  65 */     if ((paramInt & 0x4) == 0) {
/*  66 */       return getSQL(paramStringBuilder.append("CAST(")).append(" AS DECFLOAT)");
/*     */     }
/*  68 */     return getSQL(paramStringBuilder);
/*     */   }
/*     */   
/*     */   private StringBuilder getSQL(StringBuilder paramStringBuilder) {
/*  72 */     if (this.value != null)
/*  73 */       return paramStringBuilder.append(this.value); 
/*  74 */     if (this == POSITIVE_INFINITY)
/*  75 */       return paramStringBuilder.append("'Infinity'"); 
/*  76 */     if (this == NEGATIVE_INFINITY) {
/*  77 */       return paramStringBuilder.append("'-Infinity'");
/*     */     }
/*  79 */     return paramStringBuilder.append("'NaN'");
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public TypeInfo getType() {
/*  85 */     TypeInfo typeInfo = this.type;
/*  86 */     if (typeInfo == null) {
/*  87 */       this.type = typeInfo = new TypeInfo(16, (this.value != null) ? this.value.precision() : 1L, 0, null);
/*     */     }
/*  89 */     return typeInfo;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getValueType() {
/*  94 */     return 16;
/*     */   }
/*     */ 
/*     */   
/*     */   public Value add(Value paramValue) {
/*  99 */     BigDecimal bigDecimal = ((ValueDecfloat)paramValue).value;
/* 100 */     if (this.value != null) {
/* 101 */       if (bigDecimal != null) {
/* 102 */         return get(this.value.add(bigDecimal));
/*     */       }
/* 104 */       return paramValue;
/* 105 */     }  if (bigDecimal != null || this == paramValue) {
/* 106 */       return this;
/*     */     }
/* 108 */     return NAN;
/*     */   }
/*     */ 
/*     */   
/*     */   public Value subtract(Value paramValue) {
/* 113 */     BigDecimal bigDecimal = ((ValueDecfloat)paramValue).value;
/* 114 */     if (this.value != null) {
/* 115 */       if (bigDecimal != null) {
/* 116 */         return get(this.value.subtract(bigDecimal));
/*     */       }
/* 118 */       return (paramValue == POSITIVE_INFINITY) ? NEGATIVE_INFINITY : ((paramValue == NEGATIVE_INFINITY) ? POSITIVE_INFINITY : NAN);
/* 119 */     }  if (bigDecimal != null)
/* 120 */       return this; 
/* 121 */     if (this == POSITIVE_INFINITY) {
/* 122 */       if (paramValue == NEGATIVE_INFINITY) {
/* 123 */         return POSITIVE_INFINITY;
/*     */       }
/* 125 */     } else if (this == NEGATIVE_INFINITY && paramValue == POSITIVE_INFINITY) {
/* 126 */       return NEGATIVE_INFINITY;
/*     */     } 
/* 128 */     return NAN;
/*     */   }
/*     */ 
/*     */   
/*     */   public Value negate() {
/* 133 */     if (this.value != null) {
/* 134 */       return get(this.value.negate());
/*     */     }
/* 136 */     return (this == POSITIVE_INFINITY) ? NEGATIVE_INFINITY : ((this == NEGATIVE_INFINITY) ? POSITIVE_INFINITY : NAN);
/*     */   }
/*     */ 
/*     */   
/*     */   public Value multiply(Value paramValue) {
/* 141 */     BigDecimal bigDecimal = ((ValueDecfloat)paramValue).value;
/* 142 */     if (this.value != null) {
/* 143 */       if (bigDecimal != null) {
/* 144 */         return get(this.value.multiply(bigDecimal));
/*     */       }
/* 146 */       if (paramValue == POSITIVE_INFINITY) {
/* 147 */         int i = this.value.signum();
/* 148 */         if (i > 0)
/* 149 */           return POSITIVE_INFINITY; 
/* 150 */         if (i < 0) {
/* 151 */           return NEGATIVE_INFINITY;
/*     */         }
/* 153 */       } else if (paramValue == NEGATIVE_INFINITY) {
/* 154 */         int i = this.value.signum();
/* 155 */         if (i > 0)
/* 156 */           return NEGATIVE_INFINITY; 
/* 157 */         if (i < 0) {
/* 158 */           return POSITIVE_INFINITY;
/*     */         }
/*     */       } 
/* 161 */     } else if (bigDecimal != null) {
/* 162 */       if (this == POSITIVE_INFINITY) {
/* 163 */         int i = bigDecimal.signum();
/* 164 */         if (i > 0)
/* 165 */           return POSITIVE_INFINITY; 
/* 166 */         if (i < 0) {
/* 167 */           return NEGATIVE_INFINITY;
/*     */         }
/* 169 */       } else if (this == NEGATIVE_INFINITY) {
/* 170 */         int i = bigDecimal.signum();
/* 171 */         if (i > 0)
/* 172 */           return NEGATIVE_INFINITY; 
/* 173 */         if (i < 0) {
/* 174 */           return POSITIVE_INFINITY;
/*     */         }
/*     */       } 
/* 177 */     } else if (this == POSITIVE_INFINITY) {
/* 178 */       if (paramValue == POSITIVE_INFINITY)
/* 179 */         return POSITIVE_INFINITY; 
/* 180 */       if (paramValue == NEGATIVE_INFINITY) {
/* 181 */         return NEGATIVE_INFINITY;
/*     */       }
/* 183 */     } else if (this == NEGATIVE_INFINITY) {
/* 184 */       if (paramValue == POSITIVE_INFINITY)
/* 185 */         return NEGATIVE_INFINITY; 
/* 186 */       if (paramValue == NEGATIVE_INFINITY) {
/* 187 */         return POSITIVE_INFINITY;
/*     */       }
/*     */     } 
/* 190 */     return NAN;
/*     */   }
/*     */ 
/*     */   
/*     */   public Value divide(Value paramValue, TypeInfo paramTypeInfo) {
/* 195 */     BigDecimal bigDecimal = ((ValueDecfloat)paramValue).value;
/* 196 */     if (bigDecimal != null && bigDecimal.signum() == 0) {
/* 197 */       throw DbException.get(22012, getTraceSQL());
/*     */     }
/* 199 */     if (this.value != null) {
/* 200 */       if (bigDecimal != null) {
/* 201 */         return divide(this.value, bigDecimal, paramTypeInfo);
/*     */       }
/* 203 */       if (paramValue != NAN) {
/* 204 */         return ZERO;
/*     */       }
/*     */     }
/* 207 */     else if (bigDecimal != null && this != NAN) {
/* 208 */       return (((this == POSITIVE_INFINITY) ? true : false) == ((bigDecimal.signum() > 0) ? true : false)) ? POSITIVE_INFINITY : NEGATIVE_INFINITY;
/*     */     } 
/* 210 */     return NAN;
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
/*     */   public static ValueDecfloat divide(BigDecimal paramBigDecimal1, BigDecimal paramBigDecimal2, TypeInfo paramTypeInfo) {
/* 223 */     int i = (int)paramTypeInfo.getPrecision();
/* 224 */     BigDecimal bigDecimal = paramBigDecimal1.divide(paramBigDecimal2, paramBigDecimal1
/* 225 */         .scale() - paramBigDecimal1.precision() + paramBigDecimal2.precision() - paramBigDecimal2.scale() + i, RoundingMode.HALF_DOWN);
/*     */     
/* 227 */     int j = bigDecimal.precision();
/* 228 */     if (j > i) {
/* 229 */       bigDecimal = bigDecimal.setScale(bigDecimal.scale() - j + i, RoundingMode.HALF_UP);
/*     */     }
/* 231 */     return get(bigDecimal);
/*     */   }
/*     */ 
/*     */   
/*     */   public Value modulus(Value paramValue) {
/* 236 */     BigDecimal bigDecimal = ((ValueDecfloat)paramValue).value;
/* 237 */     if (bigDecimal != null && bigDecimal.signum() == 0) {
/* 238 */       throw DbException.get(22012, getTraceSQL());
/*     */     }
/* 240 */     if (this.value != null) {
/* 241 */       if (bigDecimal != null)
/* 242 */         return get(this.value.remainder(bigDecimal)); 
/* 243 */       if (paramValue != NAN) {
/* 244 */         return this;
/*     */       }
/*     */     } 
/* 247 */     return NAN;
/*     */   }
/*     */ 
/*     */   
/*     */   public int compareTypeSafe(Value paramValue, CompareMode paramCompareMode, CastDataProvider paramCastDataProvider) {
/* 252 */     BigDecimal bigDecimal = ((ValueDecfloat)paramValue).value;
/* 253 */     if (this.value != null) {
/* 254 */       if (bigDecimal != null) {
/* 255 */         return this.value.compareTo(bigDecimal);
/*     */       }
/* 257 */       return (paramValue == NEGATIVE_INFINITY) ? 1 : -1;
/* 258 */     }  if (bigDecimal != null)
/* 259 */       return (this == NEGATIVE_INFINITY) ? -1 : 1; 
/* 260 */     if (this == paramValue)
/* 261 */       return 0; 
/* 262 */     if (this == NEGATIVE_INFINITY)
/* 263 */       return -1; 
/* 264 */     if (paramValue == NEGATIVE_INFINITY) {
/* 265 */       return 1;
/*     */     }
/* 267 */     return (this == POSITIVE_INFINITY) ? -1 : 1;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public int getSignum() {
/* 273 */     if (this.value != null) {
/* 274 */       return this.value.signum();
/*     */     }
/* 276 */     return (this == POSITIVE_INFINITY) ? 1 : ((this == NEGATIVE_INFINITY) ? -1 : 0);
/*     */   }
/*     */ 
/*     */   
/*     */   public BigDecimal getBigDecimal() {
/* 281 */     if (this.value != null) {
/* 282 */       return this.value;
/*     */     }
/* 284 */     throw getDataConversionError(13);
/*     */   }
/*     */ 
/*     */   
/*     */   public float getFloat() {
/* 289 */     if (this.value != null)
/* 290 */       return this.value.floatValue(); 
/* 291 */     if (this == POSITIVE_INFINITY)
/* 292 */       return Float.POSITIVE_INFINITY; 
/* 293 */     if (this == NEGATIVE_INFINITY) {
/* 294 */       return Float.NEGATIVE_INFINITY;
/*     */     }
/* 296 */     return Float.NaN;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public double getDouble() {
/* 302 */     if (this.value != null)
/* 303 */       return this.value.doubleValue(); 
/* 304 */     if (this == POSITIVE_INFINITY)
/* 305 */       return Double.POSITIVE_INFINITY; 
/* 306 */     if (this == NEGATIVE_INFINITY) {
/* 307 */       return Double.NEGATIVE_INFINITY;
/*     */     }
/* 309 */     return Double.NaN;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 315 */     return (this.value != null) ? (getClass().hashCode() * 31 + this.value.hashCode()) : System.identityHashCode(this);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object paramObject) {
/* 320 */     if (paramObject instanceof ValueDecfloat) {
/* 321 */       BigDecimal bigDecimal = ((ValueDecfloat)paramObject).value;
/* 322 */       if (this.value != null)
/* 323 */         return this.value.equals(bigDecimal); 
/* 324 */       if (bigDecimal == null && this == paramObject) {
/* 325 */         return true;
/*     */       }
/*     */     } 
/* 328 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getMemory() {
/* 333 */     return (this.value != null) ? (this.value.precision() + 120) : 32;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isFinite() {
/* 342 */     return (this.value != null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ValueDecfloat get(BigDecimal paramBigDecimal) {
/* 352 */     paramBigDecimal = paramBigDecimal.stripTrailingZeros();
/* 353 */     if (BigDecimal.ZERO.equals(paramBigDecimal))
/* 354 */       return ZERO; 
/* 355 */     if (BigDecimal.ONE.equals(paramBigDecimal)) {
/* 356 */       return ONE;
/*     */     }
/* 358 */     return (ValueDecfloat)Value.cache(new ValueDecfloat(paramBigDecimal));
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\value\ValueDecfloat.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */