/*     */ package net.jodah.expiringmap;
/*     */ 
/*     */ import java.util.concurrent.TimeUnit;
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
/*     */ public final class ExpiringValue<V>
/*     */ {
/*     */   private static final long UNSET_DURATION = -1L;
/*     */   private final V value;
/*     */   private final ExpirationPolicy expirationPolicy;
/*     */   private final long duration;
/*     */   private final TimeUnit timeUnit;
/*     */   
/*     */   public ExpiringValue(V value) {
/*  25 */     this(value, -1L, (TimeUnit)null, (ExpirationPolicy)null);
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
/*     */   public ExpiringValue(V value, ExpirationPolicy expirationPolicy) {
/*  37 */     this(value, -1L, (TimeUnit)null, expirationPolicy);
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
/*     */   public ExpiringValue(V value, long duration, TimeUnit timeUnit) {
/*  51 */     this(value, duration, timeUnit, (ExpirationPolicy)null);
/*  52 */     if (timeUnit == null) {
/*  53 */       throw new NullPointerException();
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
/*     */   public ExpiringValue(V value, ExpirationPolicy expirationPolicy, long duration, TimeUnit timeUnit) {
/*  68 */     this(value, duration, timeUnit, expirationPolicy);
/*  69 */     if (timeUnit == null) {
/*  70 */       throw new NullPointerException();
/*     */     }
/*     */   }
/*     */   
/*     */   private ExpiringValue(V value, long duration, TimeUnit timeUnit, ExpirationPolicy expirationPolicy) {
/*  75 */     this.value = value;
/*  76 */     this.expirationPolicy = expirationPolicy;
/*  77 */     this.duration = duration;
/*  78 */     this.timeUnit = timeUnit;
/*     */   }
/*     */   
/*     */   public V getValue() {
/*  82 */     return this.value;
/*     */   }
/*     */   
/*     */   public ExpirationPolicy getExpirationPolicy() {
/*  86 */     return this.expirationPolicy;
/*     */   }
/*     */   
/*     */   public long getDuration() {
/*  90 */     return this.duration;
/*     */   }
/*     */   
/*     */   public TimeUnit getTimeUnit() {
/*  94 */     return this.timeUnit;
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/*  99 */     return (this.value != null) ? this.value.hashCode() : 0;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object o) {
/* 104 */     if (this == o) {
/* 105 */       return true;
/*     */     }
/* 107 */     if (o == null || getClass() != o.getClass()) {
/* 108 */       return false;
/*     */     }
/*     */     
/* 111 */     ExpiringValue<?> that = (ExpiringValue)o;
/* 112 */     return (((this.value != null) ? !this.value.equals(that.value) : (that.value != null)) && this.expirationPolicy == that.expirationPolicy && this.duration == that.duration && this.timeUnit == that.timeUnit);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/* 119 */     return "ExpiringValue{value=" + this.value + ", expirationPolicy=" + this.expirationPolicy + ", duration=" + this.duration + ", timeUnit=" + this.timeUnit + '}';
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\net\jodah\expiringmap\ExpiringValue.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */