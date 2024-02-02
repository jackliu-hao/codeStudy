/*     */ package org.h2.schema;
/*     */ 
/*     */ import org.h2.command.ddl.SequenceOptions;
/*     */ import org.h2.engine.CastDataProvider;
/*     */ import org.h2.engine.SessionLocal;
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.table.Table;
/*     */ import org.h2.value.TypeInfo;
/*     */ import org.h2.value.Value;
/*     */ import org.h2.value.ValueBigint;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class Sequence
/*     */   extends SchemaObject
/*     */ {
/*     */   public static final int DEFAULT_CACHE_SIZE = 32;
/*     */   private long baseValue;
/*     */   private long margin;
/*     */   private TypeInfo dataType;
/*     */   private long increment;
/*     */   private long cacheSize;
/*     */   private long startValue;
/*     */   private long minValue;
/*     */   private long maxValue;
/*     */   private Cycle cycle;
/*     */   private boolean belongsToTable;
/*     */   private boolean writeWithMargin;
/*     */   
/*     */   public enum Cycle
/*     */   {
/*  33 */     CYCLE,
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  38 */     NO_CYCLE,
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  43 */     EXHAUSTED;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public boolean isCycle() {
/*  52 */       return (this == CYCLE);
/*     */     }
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
/*     */   public Sequence(SessionLocal paramSessionLocal, Schema paramSchema, int paramInt, String paramString, SequenceOptions paramSequenceOptions, boolean paramBoolean) {
/*  95 */     super(paramSchema, paramInt, paramString, 9); boolean bool;
/*  96 */     this.dataType = paramSequenceOptions.getDataType();
/*  97 */     if (this.dataType == null) {
/*  98 */       paramSequenceOptions.setDataType(this.dataType = (paramSessionLocal.getMode()).decimalSequences ? TypeInfo.TYPE_NUMERIC_BIGINT : TypeInfo.TYPE_BIGINT);
/*     */     }
/*     */     
/* 101 */     long[] arrayOfLong = paramSequenceOptions.getBounds();
/* 102 */     Long long_1 = paramSequenceOptions.getIncrement(paramSessionLocal);
/* 103 */     long l1 = (long_1 != null) ? long_1.longValue() : 1L;
/* 104 */     Long long_2 = paramSequenceOptions.getStartValue(paramSessionLocal);
/* 105 */     Long long_3 = paramSequenceOptions.getMinValue(null, paramSessionLocal);
/* 106 */     Long long_4 = paramSequenceOptions.getMaxValue(null, paramSessionLocal);
/* 107 */     long l2 = (long_3 != null) ? long_3.longValue() : getDefaultMinValue(long_2, l1, arrayOfLong);
/* 108 */     long l3 = (long_4 != null) ? long_4.longValue() : getDefaultMaxValue(long_2, l1, arrayOfLong);
/* 109 */     long l4 = (long_2 != null) ? long_2.longValue() : ((l1 >= 0L) ? l2 : l3);
/* 110 */     Long long_5 = paramSequenceOptions.getRestartValue(paramSessionLocal, l4);
/* 111 */     long l5 = (long_5 != null) ? long_5.longValue() : l4;
/* 112 */     long_1 = paramSequenceOptions.getCacheSize(paramSessionLocal);
/*     */ 
/*     */     
/* 115 */     if (long_1 != null) {
/* 116 */       l6 = long_1.longValue();
/* 117 */       bool = false;
/*     */     } else {
/* 119 */       l6 = 32L;
/* 120 */       bool = true;
/*     */     } 
/* 122 */     long l6 = checkOptions(l5, l4, l2, l3, l1, l6, bool);
/* 123 */     Cycle cycle = paramSequenceOptions.getCycle();
/* 124 */     if (cycle == null) {
/* 125 */       cycle = Cycle.NO_CYCLE;
/* 126 */     } else if (cycle == Cycle.EXHAUSTED) {
/* 127 */       l5 = l4;
/*     */     } 
/* 129 */     this.margin = this.baseValue = l5;
/* 130 */     this.increment = l1;
/* 131 */     this.cacheSize = l6;
/* 132 */     this.startValue = l4;
/* 133 */     this.minValue = l2;
/* 134 */     this.maxValue = l3;
/* 135 */     this.cycle = cycle;
/* 136 */     this.belongsToTable = paramBoolean;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized void modify(Long paramLong1, Long paramLong2, Long paramLong3, Long paramLong4, Long paramLong5, Cycle paramCycle, Long paramLong6) {
/*     */     boolean bool;
/* 162 */     long l1 = (paramLong1 != null) ? paramLong1.longValue() : this.baseValue;
/* 163 */     long l2 = (paramLong2 != null) ? paramLong2.longValue() : this.startValue;
/* 164 */     long l3 = (paramLong3 != null) ? paramLong3.longValue() : this.minValue;
/* 165 */     long l4 = (paramLong4 != null) ? paramLong4.longValue() : this.maxValue;
/* 166 */     long l5 = (paramLong5 != null) ? paramLong5.longValue() : this.increment;
/*     */ 
/*     */     
/* 169 */     if (paramLong6 != null) {
/* 170 */       l6 = paramLong6.longValue();
/* 171 */       bool = false;
/*     */     } else {
/* 173 */       l6 = this.cacheSize;
/* 174 */       bool = true;
/*     */     } 
/* 176 */     long l6 = checkOptions(l1, l2, l3, l4, l5, l6, bool);
/*     */     
/* 178 */     if (paramCycle == null) {
/* 179 */       paramCycle = this.cycle;
/* 180 */       if (paramCycle == Cycle.EXHAUSTED && paramLong1 != null) {
/* 181 */         paramCycle = Cycle.NO_CYCLE;
/*     */       }
/* 183 */     } else if (paramCycle == Cycle.EXHAUSTED) {
/* 184 */       l1 = l2;
/*     */     } 
/* 186 */     this.margin = this.baseValue = l1;
/* 187 */     this.startValue = l2;
/* 188 */     this.minValue = l3;
/* 189 */     this.maxValue = l4;
/* 190 */     this.increment = l5;
/* 191 */     this.cacheSize = l6;
/* 192 */     this.cycle = paramCycle;
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
/*     */   private long checkOptions(long paramLong1, long paramLong2, long paramLong3, long paramLong4, long paramLong5, long paramLong6, boolean paramBoolean) {
/* 220 */     if (paramLong3 <= paramLong1 && paramLong1 <= paramLong4 && paramLong3 <= paramLong2 && paramLong2 <= paramLong4 && paramLong3 < paramLong4 && paramLong5 != 0L) {
/*     */ 
/*     */       
/* 223 */       long l = paramLong4 - paramLong3;
/* 224 */       if (Long.compareUnsigned(Math.abs(paramLong5), l) <= 0 && paramLong6 >= 0L) {
/* 225 */         if (paramLong6 <= 1L) {
/* 226 */           return 1L;
/*     */         }
/* 228 */         long l1 = getMaxCacheSize(l, paramLong5);
/* 229 */         if (paramLong6 <= l1) {
/* 230 */           return paramLong6;
/*     */         }
/* 232 */         if (paramBoolean) {
/* 233 */           return l1;
/*     */         }
/*     */       } 
/*     */     } 
/* 237 */     throw DbException.get(90009, new String[] { getName(), Long.toString(paramLong1), 
/* 238 */           Long.toString(paramLong2), Long.toString(paramLong3), Long.toString(paramLong4), Long.toString(paramLong5), 
/* 239 */           Long.toString(paramLong6) });
/*     */   }
/*     */   
/*     */   private static long getMaxCacheSize(long paramLong1, long paramLong2) {
/* 243 */     if (paramLong2 > 0L) {
/* 244 */       if (paramLong1 < 0L) {
/* 245 */         paramLong1 = Long.MAX_VALUE;
/*     */       } else {
/* 247 */         paramLong1 += paramLong2;
/* 248 */         if (paramLong1 < 0L) {
/* 249 */           paramLong1 = Long.MAX_VALUE;
/*     */         }
/*     */       } 
/*     */     } else {
/* 253 */       paramLong1 = -paramLong1;
/* 254 */       if (paramLong1 > 0L) {
/* 255 */         paramLong1 = Long.MIN_VALUE;
/*     */       } else {
/* 257 */         paramLong1 += paramLong2;
/* 258 */         if (paramLong1 >= 0L) {
/* 259 */           paramLong1 = Long.MIN_VALUE;
/*     */         }
/*     */       } 
/*     */     } 
/* 263 */     return paramLong1 / paramLong2;
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
/*     */   public static long getDefaultMinValue(Long paramLong, long paramLong1, long[] paramArrayOflong) {
/* 275 */     long l = (paramLong1 >= 0L) ? 1L : paramArrayOflong[0];
/* 276 */     if (paramLong != null && paramLong1 >= 0L && paramLong.longValue() < l) {
/* 277 */       l = paramLong.longValue();
/*     */     }
/* 279 */     return l;
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
/*     */   public static long getDefaultMaxValue(Long paramLong, long paramLong1, long[] paramArrayOflong) {
/* 291 */     long l = (paramLong1 >= 0L) ? paramArrayOflong[1] : -1L;
/* 292 */     if (paramLong != null && paramLong1 < 0L && paramLong.longValue() > l) {
/* 293 */       l = paramLong.longValue();
/*     */     }
/* 295 */     return l;
/*     */   }
/*     */   
/*     */   public boolean getBelongsToTable() {
/* 299 */     return this.belongsToTable;
/*     */   }
/*     */   
/*     */   public TypeInfo getDataType() {
/* 303 */     return this.dataType;
/*     */   }
/*     */   public int getEffectivePrecision() {
/*     */     int i, j;
/* 307 */     TypeInfo typeInfo = this.dataType;
/* 308 */     switch (typeInfo.getValueType()) {
/*     */       case 13:
/* 310 */         i = (int)typeInfo.getPrecision();
/* 311 */         j = typeInfo.getScale();
/* 312 */         if (i - j > 19) {
/* 313 */           return 19 + j;
/*     */         }
/* 315 */         return i;
/*     */       
/*     */       case 16:
/* 318 */         return Math.min((int)typeInfo.getPrecision(), 19);
/*     */     } 
/* 320 */     return (int)typeInfo.getPrecision();
/*     */   }
/*     */ 
/*     */   
/*     */   public long getIncrement() {
/* 325 */     return this.increment;
/*     */   }
/*     */   
/*     */   public long getStartValue() {
/* 329 */     return this.startValue;
/*     */   }
/*     */   
/*     */   public long getMinValue() {
/* 333 */     return this.minValue;
/*     */   }
/*     */   
/*     */   public long getMaxValue() {
/* 337 */     return this.maxValue;
/*     */   }
/*     */   
/*     */   public Cycle getCycle() {
/* 341 */     return this.cycle;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getDropSQL() {
/* 346 */     if (getBelongsToTable()) {
/* 347 */       return null;
/*     */     }
/* 349 */     StringBuilder stringBuilder = new StringBuilder("DROP SEQUENCE IF EXISTS ");
/* 350 */     return getSQL(stringBuilder, 0).toString();
/*     */   }
/*     */ 
/*     */   
/*     */   public String getCreateSQLForCopy(Table paramTable, String paramString) {
/* 355 */     throw DbException.getInternalError(toString());
/*     */   }
/*     */ 
/*     */   
/*     */   public String getCreateSQL() {
/* 360 */     StringBuilder stringBuilder = getSQL(new StringBuilder("CREATE SEQUENCE "), 0);
/* 361 */     if (this.dataType.getValueType() != 12) {
/* 362 */       this.dataType.getSQL(stringBuilder.append(" AS "), 0);
/*     */     }
/* 364 */     stringBuilder.append(' ');
/* 365 */     synchronized (this) {
/* 366 */       getSequenceOptionsSQL(stringBuilder, this.writeWithMargin ? this.margin : this.baseValue);
/*     */     } 
/* 368 */     if (this.belongsToTable) {
/* 369 */       stringBuilder.append(" BELONGS_TO_TABLE");
/*     */     }
/* 371 */     return stringBuilder.toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized StringBuilder getSequenceOptionsSQL(StringBuilder paramStringBuilder) {
/* 381 */     return getSequenceOptionsSQL(paramStringBuilder, this.baseValue);
/*     */   }
/*     */   
/*     */   private StringBuilder getSequenceOptionsSQL(StringBuilder paramStringBuilder, long paramLong) {
/* 385 */     paramStringBuilder.append("START WITH ").append(this.startValue);
/* 386 */     if (paramLong != this.startValue && this.cycle != Cycle.EXHAUSTED) {
/* 387 */       paramStringBuilder.append(" RESTART WITH ").append(paramLong);
/*     */     }
/* 389 */     if (this.increment != 1L) {
/* 390 */       paramStringBuilder.append(" INCREMENT BY ").append(this.increment);
/*     */     }
/* 392 */     long[] arrayOfLong = SequenceOptions.getBounds(this.dataType);
/* 393 */     if (this.minValue != getDefaultMinValue(Long.valueOf(paramLong), this.increment, arrayOfLong)) {
/* 394 */       paramStringBuilder.append(" MINVALUE ").append(this.minValue);
/*     */     }
/* 396 */     if (this.maxValue != getDefaultMaxValue(Long.valueOf(paramLong), this.increment, arrayOfLong)) {
/* 397 */       paramStringBuilder.append(" MAXVALUE ").append(this.maxValue);
/*     */     }
/* 399 */     if (this.cycle == Cycle.CYCLE) {
/* 400 */       paramStringBuilder.append(" CYCLE");
/* 401 */     } else if (this.cycle == Cycle.EXHAUSTED) {
/* 402 */       paramStringBuilder.append(" EXHAUSTED");
/*     */     } 
/* 404 */     if (this.cacheSize != 32L) {
/* 405 */       if (this.cacheSize == 1L) {
/* 406 */         paramStringBuilder.append(" NO CACHE");
/* 407 */       } else if (this.cacheSize > 32L || this.cacheSize != 
/* 408 */         getMaxCacheSize(this.maxValue - this.minValue, this.increment)) {
/* 409 */         paramStringBuilder.append(" CACHE ").append(this.cacheSize);
/*     */       } 
/*     */     }
/* 412 */     return paramStringBuilder;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Value getNext(SessionLocal paramSessionLocal) {
/*     */     long l;
/*     */     boolean bool;
/* 425 */     synchronized (this) {
/* 426 */       if (this.cycle == Cycle.EXHAUSTED) {
/* 427 */         throw DbException.get(90006, getName());
/*     */       }
/* 429 */       l = this.baseValue;
/* 430 */       long l1 = l + this.increment;
/* 431 */       bool = (this.increment > 0L) ? increment(l, l1) : decrement(l, l1);
/*     */     } 
/* 433 */     if (bool) {
/* 434 */       flush(paramSessionLocal);
/*     */     }
/* 436 */     return ValueBigint.get(l).castTo(this.dataType, (CastDataProvider)paramSessionLocal);
/*     */   }
/*     */   
/*     */   private boolean increment(long paramLong1, long paramLong2) {
/* 440 */     boolean bool = false;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 445 */     if (paramLong2 > this.maxValue || ((paramLong1 ^ 0xFFFFFFFFFFFFFFFFL) & paramLong2) < 0L) {
/* 446 */       paramLong2 = this.minValue;
/* 447 */       bool = true;
/* 448 */       if (this.cycle == Cycle.CYCLE) {
/* 449 */         this.margin = paramLong2 + this.increment * (this.cacheSize - 1L);
/*     */       } else {
/* 451 */         this.margin = paramLong2;
/* 452 */         this.cycle = Cycle.EXHAUSTED;
/*     */       } 
/* 454 */     } else if (paramLong2 > this.margin) {
/* 455 */       long l = paramLong2 + this.increment * (this.cacheSize - 1L);
/* 456 */       if (l > this.maxValue || ((paramLong2 ^ 0xFFFFFFFFFFFFFFFFL) & l) < 0L)
/*     */       {
/*     */ 
/*     */ 
/*     */         
/* 461 */         l = paramLong2;
/*     */       }
/* 463 */       this.margin = l;
/* 464 */       bool = true;
/*     */     } 
/* 466 */     this.baseValue = paramLong2;
/* 467 */     return bool;
/*     */   }
/*     */   
/*     */   private boolean decrement(long paramLong1, long paramLong2) {
/* 471 */     boolean bool = false;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 476 */     if (paramLong2 < this.minValue || (paramLong1 & (paramLong2 ^ 0xFFFFFFFFFFFFFFFFL)) < 0L) {
/* 477 */       paramLong2 = this.maxValue;
/* 478 */       bool = true;
/* 479 */       if (this.cycle == Cycle.CYCLE) {
/* 480 */         this.margin = paramLong2 + this.increment * (this.cacheSize - 1L);
/*     */       } else {
/* 482 */         this.margin = paramLong2;
/* 483 */         this.cycle = Cycle.EXHAUSTED;
/*     */       } 
/* 485 */     } else if (paramLong2 < this.margin) {
/* 486 */       long l = paramLong2 + this.increment * (this.cacheSize - 1L);
/* 487 */       if (l < this.minValue || (paramLong2 & (l ^ 0xFFFFFFFFFFFFFFFFL)) < 0L)
/*     */       {
/*     */ 
/*     */ 
/*     */         
/* 492 */         l = paramLong2;
/*     */       }
/* 494 */       this.margin = l;
/* 495 */       bool = true;
/*     */     } 
/* 497 */     this.baseValue = paramLong2;
/* 498 */     return bool;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void flushWithoutMargin() {
/* 505 */     if (this.margin != this.baseValue) {
/* 506 */       this.margin = this.baseValue;
/* 507 */       flush((SessionLocal)null);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void flush(SessionLocal paramSessionLocal) {
/* 517 */     if (isTemporary()) {
/*     */       return;
/*     */     }
/* 520 */     if (paramSessionLocal == null || !this.database.isSysTableLockedBy(paramSessionLocal)) {
/*     */ 
/*     */ 
/*     */       
/* 524 */       SessionLocal sessionLocal = this.database.getSystemSession();
/* 525 */       synchronized (sessionLocal) {
/* 526 */         flushInternal(sessionLocal);
/* 527 */         sessionLocal.commit(false);
/*     */       } 
/*     */     } else {
/* 530 */       synchronized (paramSessionLocal) {
/* 531 */         flushInternal(paramSessionLocal);
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   private void flushInternal(SessionLocal paramSessionLocal) {
/* 537 */     boolean bool = this.database.lockMeta(paramSessionLocal);
/*     */     
/*     */     try {
/* 540 */       this.writeWithMargin = true;
/* 541 */       this.database.updateMeta(paramSessionLocal, this);
/*     */     } finally {
/* 543 */       this.writeWithMargin = false;
/* 544 */       if (!bool) {
/* 545 */         this.database.unlockMeta(paramSessionLocal);
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void close() {
/* 554 */     flushWithoutMargin();
/*     */   }
/*     */ 
/*     */   
/*     */   public int getType() {
/* 559 */     return 3;
/*     */   }
/*     */ 
/*     */   
/*     */   public void removeChildrenAndResources(SessionLocal paramSessionLocal) {
/* 564 */     this.database.removeMeta(paramSessionLocal, getId());
/* 565 */     invalidate();
/*     */   }
/*     */ 
/*     */   
/*     */   public synchronized long getBaseValue() {
/* 570 */     return this.baseValue;
/*     */   }
/*     */   
/*     */   public synchronized long getCurrentValue() {
/* 574 */     return this.baseValue - this.increment;
/*     */   }
/*     */   
/*     */   public void setBelongsToTable(boolean paramBoolean) {
/* 578 */     this.belongsToTable = paramBoolean;
/*     */   }
/*     */   
/*     */   public long getCacheSize() {
/* 582 */     return this.cacheSize;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\schema\Sequence.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */