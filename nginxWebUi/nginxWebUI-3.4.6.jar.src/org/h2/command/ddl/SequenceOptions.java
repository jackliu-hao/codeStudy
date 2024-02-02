/*     */ package org.h2.command.ddl;
/*     */ 
/*     */ import org.h2.engine.SessionLocal;
/*     */ import org.h2.expression.Expression;
/*     */ import org.h2.expression.ValueExpression;
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.schema.Sequence;
/*     */ import org.h2.value.TypeInfo;
/*     */ import org.h2.value.Value;
/*     */ import org.h2.value.ValueBigint;
/*     */ import org.h2.value.ValueNull;
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
/*     */ public class SequenceOptions
/*     */ {
/*     */   private TypeInfo dataType;
/*     */   private Expression start;
/*     */   private Expression restart;
/*     */   private Expression increment;
/*     */   private Expression maxValue;
/*     */   private Expression minValue;
/*     */   private Sequence.Cycle cycle;
/*     */   private Expression cacheSize;
/*     */   private long[] bounds;
/*     */   private final Sequence oldSequence;
/*     */   
/*     */   private static Long getLong(SessionLocal paramSessionLocal, Expression paramExpression) {
/*  45 */     if (paramExpression != null) {
/*  46 */       Value value = paramExpression.optimize(paramSessionLocal).getValue(paramSessionLocal);
/*  47 */       if (value != ValueNull.INSTANCE) {
/*  48 */         return Long.valueOf(value.getLong());
/*     */       }
/*     */     } 
/*  51 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SequenceOptions() {
/*  58 */     this.oldSequence = null;
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
/*     */   public SequenceOptions(Sequence paramSequence, TypeInfo paramTypeInfo) {
/*  70 */     this.oldSequence = paramSequence;
/*  71 */     this.dataType = paramTypeInfo;
/*     */     
/*  73 */     getBounds();
/*     */   }
/*     */   
/*     */   public TypeInfo getDataType() {
/*  77 */     if (this.oldSequence != null) {
/*  78 */       synchronized (this.oldSequence) {
/*  79 */         copyFromOldSequence();
/*     */       } 
/*     */     }
/*  82 */     return this.dataType;
/*     */   }
/*     */   
/*     */   private void copyFromOldSequence() {
/*  86 */     long[] arrayOfLong = getBounds();
/*  87 */     long l1 = Math.max(this.oldSequence.getMinValue(), arrayOfLong[0]);
/*  88 */     long l2 = Math.min(this.oldSequence.getMaxValue(), arrayOfLong[1]);
/*  89 */     if (l2 < l1) {
/*  90 */       l1 = arrayOfLong[0];
/*  91 */       l2 = arrayOfLong[1];
/*     */     } 
/*  93 */     this.minValue = (Expression)ValueExpression.get((Value)ValueBigint.get(l1));
/*  94 */     this.maxValue = (Expression)ValueExpression.get((Value)ValueBigint.get(l2));
/*  95 */     long l3 = this.oldSequence.getStartValue();
/*  96 */     if (l3 >= l1 && l3 <= l2) {
/*  97 */       this.start = (Expression)ValueExpression.get((Value)ValueBigint.get(l3));
/*     */     }
/*  99 */     l3 = this.oldSequence.getBaseValue();
/* 100 */     if (l3 >= l1 && l3 <= l2) {
/* 101 */       this.restart = (Expression)ValueExpression.get((Value)ValueBigint.get(l3));
/*     */     }
/* 103 */     this.increment = (Expression)ValueExpression.get((Value)ValueBigint.get(this.oldSequence.getIncrement()));
/* 104 */     this.cycle = this.oldSequence.getCycle();
/* 105 */     this.cacheSize = (Expression)ValueExpression.get((Value)ValueBigint.get(this.oldSequence.getCacheSize()));
/*     */   }
/*     */   
/*     */   public void setDataType(TypeInfo paramTypeInfo) {
/* 109 */     this.dataType = paramTypeInfo;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Long getStartValue(SessionLocal paramSessionLocal) {
/* 119 */     return check(getLong(paramSessionLocal, this.start));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setStartValue(Expression paramExpression) {
/* 128 */     this.start = paramExpression;
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
/*     */   public Long getRestartValue(SessionLocal paramSessionLocal, long paramLong) {
/* 141 */     return check((this.restart == ValueExpression.DEFAULT) ? Long.valueOf(paramLong) : getLong(paramSessionLocal, this.restart));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setRestartValue(Expression paramExpression) {
/* 152 */     this.restart = paramExpression;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Long getIncrement(SessionLocal paramSessionLocal) {
/* 162 */     return check(getLong(paramSessionLocal, this.increment));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setIncrement(Expression paramExpression) {
/* 171 */     this.increment = paramExpression;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Long getMaxValue(Sequence paramSequence, SessionLocal paramSessionLocal) {
/*     */     Long long_;
/* 183 */     if (this.maxValue == ValueExpression.NULL && paramSequence != null) {
/* 184 */       long_ = Long.valueOf(Sequence.getDefaultMaxValue(Long.valueOf(getCurrentStart(paramSequence, paramSessionLocal)), (this.increment != null) ? 
/* 185 */             getIncrement(paramSessionLocal).longValue() : paramSequence.getIncrement(), getBounds()));
/*     */     } else {
/* 187 */       long_ = getLong(paramSessionLocal, this.maxValue);
/*     */     } 
/* 189 */     return check(long_);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setMaxValue(Expression paramExpression) {
/* 198 */     this.maxValue = paramExpression;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Long getMinValue(Sequence paramSequence, SessionLocal paramSessionLocal) {
/*     */     Long long_;
/* 210 */     if (this.minValue == ValueExpression.NULL && paramSequence != null) {
/* 211 */       long_ = Long.valueOf(Sequence.getDefaultMinValue(Long.valueOf(getCurrentStart(paramSequence, paramSessionLocal)), (this.increment != null) ? 
/* 212 */             getIncrement(paramSessionLocal).longValue() : paramSequence.getIncrement(), getBounds()));
/*     */     } else {
/* 214 */       long_ = getLong(paramSessionLocal, this.minValue);
/*     */     } 
/* 216 */     return check(long_);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setMinValue(Expression paramExpression) {
/* 225 */     this.minValue = paramExpression;
/*     */   }
/*     */   
/*     */   private Long check(Long paramLong) {
/* 229 */     if (paramLong == null) {
/* 230 */       return null;
/*     */     }
/* 232 */     long[] arrayOfLong = getBounds();
/* 233 */     long l = paramLong.longValue();
/* 234 */     if (l < arrayOfLong[0] || l > arrayOfLong[1]) {
/* 235 */       throw DbException.get(22003, Long.toString(l));
/*     */     }
/*     */     
/* 238 */     return paramLong;
/*     */   }
/*     */   
/*     */   public long[] getBounds() {
/* 242 */     long[] arrayOfLong = this.bounds;
/* 243 */     if (arrayOfLong == null) {
/* 244 */       this.bounds = arrayOfLong = getBounds(this.dataType);
/*     */     }
/* 246 */     return arrayOfLong;
/*     */   }
/*     */ 
/*     */   
/*     */   public static long[] getBounds(TypeInfo paramTypeInfo) {
/*     */     long l1;
/*     */     long l2;
/*     */     long[] arrayOfLong2;
/*     */     long l4;
/*     */     long[] arrayOfLong1;
/*     */     long l3;
/* 257 */     switch (paramTypeInfo.getValueType()) {
/*     */       case 9:
/* 259 */         l1 = -128L;
/* 260 */         l2 = 127L;
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
/* 318 */         arrayOfLong2 = new long[] { l1, l2 };
/* 319 */         return arrayOfLong2;case 10: l1 = -32768L; l2 = 32767L; arrayOfLong2 = new long[] { l1, l2 }; return arrayOfLong2;case 11: l1 = -2147483648L; l2 = 2147483647L; arrayOfLong2 = new long[] { l1, l2 }; return arrayOfLong2;case 12: l1 = Long.MIN_VALUE; l2 = Long.MAX_VALUE; arrayOfLong2 = new long[] { l1, l2 }; return arrayOfLong2;case 14: l1 = -16777216L; l2 = 16777216L; arrayOfLong2 = new long[] { l1, l2 }; return arrayOfLong2;case 15: l1 = -9007199254740992L; l2 = 9007199254740992L; arrayOfLong2 = new long[] { l1, l2 }; return arrayOfLong2;
/*     */       case 13:
/*     */         if (paramTypeInfo.getScale() != 0)
/*     */           throw DbException.getUnsupportedException(paramTypeInfo.getTraceSQL());  l4 = paramTypeInfo.getPrecision() - paramTypeInfo.getScale(); if (l4 <= 0L)
/*     */           throw DbException.getUnsupportedException(paramTypeInfo.getTraceSQL());  if (l4 > 18L) { l1 = Long.MIN_VALUE; l2 = Long.MAX_VALUE; } else { l2 = 10L; for (byte b = 1; b < l4; b++)
/*     */             l2 *= 10L;  l1 = ---l2; }  return new long[] { l1, l2 };
/*     */       case 16:
/*     */         l3 = paramTypeInfo.getPrecision(); if (l3 > 18L) { l1 = Long.MIN_VALUE; l2 = Long.MAX_VALUE; } else { l2 = 10L; for (byte b = 1; b < l3; b++)
/*     */             l2 *= 10L;  l1 = -l2; }  return new long[] { l1, l2 };
/* 328 */     }  throw DbException.getUnsupportedException(paramTypeInfo.getTraceSQL()); } public Sequence.Cycle getCycle() { return this.cycle; }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setCycle(Sequence.Cycle paramCycle) {
/* 337 */     this.cycle = paramCycle;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Long getCacheSize(SessionLocal paramSessionLocal) {
/* 347 */     return getLong(paramSessionLocal, this.cacheSize);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setCacheSize(Expression paramExpression) {
/* 356 */     this.cacheSize = paramExpression;
/*     */   }
/*     */   
/*     */   private long getCurrentStart(Sequence paramSequence, SessionLocal paramSessionLocal) {
/* 360 */     return (this.start != null) ? getStartValue(paramSessionLocal).longValue() : paramSequence.getBaseValue();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\command\ddl\SequenceOptions.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */