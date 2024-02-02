/*     */ package cn.hutool.core.lang;
/*     */ 
/*     */ import cn.hutool.core.date.SystemClock;
/*     */ import cn.hutool.core.util.IdUtil;
/*     */ import cn.hutool.core.util.RandomUtil;
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import java.io.Serializable;
/*     */ import java.util.Date;
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
/*     */ public class Snowflake
/*     */   implements Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*  42 */   public static long DEFAULT_TWEPOCH = 1288834974657L;
/*     */ 
/*     */ 
/*     */   
/*  46 */   public static long DEFAULT_TIME_OFFSET = 2000L;
/*     */ 
/*     */   
/*     */   private static final long WORKER_ID_BITS = 5L;
/*     */ 
/*     */   
/*     */   private static final long MAX_WORKER_ID = 31L;
/*     */ 
/*     */   
/*     */   private static final long DATA_CENTER_ID_BITS = 5L;
/*     */ 
/*     */   
/*     */   private static final long MAX_DATA_CENTER_ID = 31L;
/*     */ 
/*     */   
/*     */   private static final long SEQUENCE_BITS = 12L;
/*     */ 
/*     */   
/*     */   private static final long WORKER_ID_SHIFT = 12L;
/*     */ 
/*     */   
/*     */   private static final long DATA_CENTER_ID_SHIFT = 17L;
/*     */ 
/*     */   
/*     */   private static final long TIMESTAMP_LEFT_SHIFT = 22L;
/*     */ 
/*     */   
/*     */   private static final long SEQUENCE_MASK = 4095L;
/*     */ 
/*     */   
/*     */   private final long twepoch;
/*     */ 
/*     */   
/*     */   private final long workerId;
/*     */   
/*     */   private final long dataCenterId;
/*     */   
/*     */   private final boolean useSystemClock;
/*     */   
/*     */   private final long timeOffset;
/*     */   
/*     */   private final long randomSequenceLimit;
/*     */   
/*  89 */   private long sequence = 0L;
/*  90 */   private long lastTimestamp = -1L;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Snowflake() {
/*  96 */     this(IdUtil.getWorkerId(IdUtil.getDataCenterId(31L), 31L));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Snowflake(long workerId) {
/* 105 */     this(workerId, IdUtil.getDataCenterId(31L));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Snowflake(long workerId, long dataCenterId) {
/* 115 */     this(workerId, dataCenterId, false);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Snowflake(long workerId, long dataCenterId, boolean isUseSystemClock) {
/* 126 */     this(null, workerId, dataCenterId, isUseSystemClock);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Snowflake(Date epochDate, long workerId, long dataCenterId, boolean isUseSystemClock) {
/* 137 */     this(epochDate, workerId, dataCenterId, isUseSystemClock, DEFAULT_TIME_OFFSET);
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
/*     */   public Snowflake(Date epochDate, long workerId, long dataCenterId, boolean isUseSystemClock, long timeOffset) {
/* 149 */     this(epochDate, workerId, dataCenterId, isUseSystemClock, timeOffset, 0L);
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
/*     */   public Snowflake(Date epochDate, long workerId, long dataCenterId, boolean isUseSystemClock, long timeOffset, long randomSequenceLimit) {
/* 163 */     this.twepoch = (null != epochDate) ? epochDate.getTime() : DEFAULT_TWEPOCH;
/* 164 */     this.workerId = Assert.checkBetween(workerId, 0L, 31L);
/* 165 */     this.dataCenterId = Assert.checkBetween(dataCenterId, 0L, 31L);
/* 166 */     this.useSystemClock = isUseSystemClock;
/* 167 */     this.timeOffset = timeOffset;
/* 168 */     this.randomSequenceLimit = Assert.checkBetween(randomSequenceLimit, 0L, 4095L);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long getWorkerId(long id) {
/* 178 */     return id >> 12L & 0x1FL;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long getDataCenterId(long id) {
/* 188 */     return id >> 17L & 0x1FL;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long getGenerateDateTime(long id) {
/* 198 */     return (id >> 22L & 0x1FFFFFFFFFFL) + this.twepoch;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized long nextId() {
/* 207 */     long timestamp = genTime();
/* 208 */     if (timestamp < this.lastTimestamp) {
/* 209 */       if (this.lastTimestamp - timestamp < this.timeOffset) {
/*     */         
/* 211 */         timestamp = this.lastTimestamp;
/*     */       } else {
/*     */         
/* 214 */         throw new IllegalStateException(StrUtil.format("Clock moved backwards. Refusing to generate id for {}ms", new Object[] { Long.valueOf(this.lastTimestamp - timestamp) }));
/*     */       } 
/*     */     }
/*     */     
/* 218 */     if (timestamp == this.lastTimestamp) {
/* 219 */       long sequence = this.sequence + 1L & 0xFFFL;
/* 220 */       if (sequence == 0L) {
/* 221 */         timestamp = tilNextMillis(this.lastTimestamp);
/*     */       }
/* 223 */       this.sequence = sequence;
/*     */     
/*     */     }
/* 226 */     else if (this.randomSequenceLimit > 1L) {
/* 227 */       this.sequence = RandomUtil.randomLong(this.randomSequenceLimit);
/*     */     } else {
/* 229 */       this.sequence = 0L;
/*     */     } 
/*     */ 
/*     */     
/* 233 */     this.lastTimestamp = timestamp;
/*     */     
/* 235 */     return timestamp - this.twepoch << 22L | this.dataCenterId << 17L | this.workerId << 12L | this.sequence;
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
/*     */   public String nextIdStr() {
/* 247 */     return Long.toString(nextId());
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
/*     */   private long tilNextMillis(long lastTimestamp) {
/* 259 */     long timestamp = genTime();
/*     */     
/* 261 */     while (timestamp == lastTimestamp) {
/* 262 */       timestamp = genTime();
/*     */     }
/* 264 */     if (timestamp < lastTimestamp)
/*     */     {
/* 266 */       throw new IllegalStateException(
/* 267 */           StrUtil.format("Clock moved backwards. Refusing to generate id for {}ms", new Object[] { Long.valueOf(lastTimestamp - timestamp) }));
/*     */     }
/* 269 */     return timestamp;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private long genTime() {
/* 278 */     return this.useSystemClock ? SystemClock.now() : System.currentTimeMillis();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\lang\Snowflake.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */