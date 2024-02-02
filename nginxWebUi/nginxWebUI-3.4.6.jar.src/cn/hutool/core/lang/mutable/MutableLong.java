/*     */ package cn.hutool.core.lang.mutable;
/*     */ 
/*     */ import cn.hutool.core.util.NumberUtil;
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
/*     */ public class MutableLong
/*     */   extends Number
/*     */   implements Comparable<MutableLong>, Mutable<Number>
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   private long value;
/*     */   
/*     */   public MutableLong() {}
/*     */   
/*     */   public MutableLong(long value) {
/*  28 */     this.value = value;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public MutableLong(Number value) {
/*  37 */     this(value.longValue());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public MutableLong(String value) throws NumberFormatException {
/*  47 */     this.value = Long.parseLong(value);
/*     */   }
/*     */ 
/*     */   
/*     */   public Long get() {
/*  52 */     return Long.valueOf(this.value);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void set(long value) {
/*  61 */     this.value = value;
/*     */   }
/*     */ 
/*     */   
/*     */   public void set(Number value) {
/*  66 */     this.value = value.longValue();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public MutableLong increment() {
/*  77 */     this.value++;
/*  78 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public MutableLong decrement() {
/*  87 */     this.value--;
/*  88 */     return this;
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
/*     */   public MutableLong add(long operand) {
/* 100 */     this.value += operand;
/* 101 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public MutableLong add(Number operand) {
/* 112 */     this.value += operand.longValue();
/* 113 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public MutableLong subtract(long operand) {
/* 123 */     this.value -= operand;
/* 124 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public MutableLong subtract(Number operand) {
/* 135 */     this.value -= operand.longValue();
/* 136 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public int intValue() {
/* 142 */     return (int)this.value;
/*     */   }
/*     */ 
/*     */   
/*     */   public long longValue() {
/* 147 */     return this.value;
/*     */   }
/*     */ 
/*     */   
/*     */   public float floatValue() {
/* 152 */     return (float)this.value;
/*     */   }
/*     */ 
/*     */   
/*     */   public double doubleValue() {
/* 157 */     return this.value;
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
/*     */   public boolean equals(Object obj) {
/* 175 */     if (obj instanceof MutableLong) {
/* 176 */       return (this.value == ((MutableLong)obj).longValue());
/*     */     }
/* 178 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 183 */     return (int)(this.value ^ this.value >>> 32L);
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
/*     */   public int compareTo(MutableLong other) {
/* 196 */     return NumberUtil.compare(this.value, other.value);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/* 202 */     return String.valueOf(this.value);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\lang\mutable\MutableLong.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */