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
/*     */ public class MutableDouble
/*     */   extends Number
/*     */   implements Comparable<MutableDouble>, Mutable<Number>
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   private double value;
/*     */   
/*     */   public MutableDouble() {}
/*     */   
/*     */   public MutableDouble(double value) {
/*  27 */     this.value = value;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public MutableDouble(Number value) {
/*  35 */     this(value.doubleValue());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public MutableDouble(String value) throws NumberFormatException {
/*  44 */     this.value = Double.parseDouble(value);
/*     */   }
/*     */ 
/*     */   
/*     */   public Double get() {
/*  49 */     return Double.valueOf(this.value);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void set(double value) {
/*  57 */     this.value = value;
/*     */   }
/*     */ 
/*     */   
/*     */   public void set(Number value) {
/*  62 */     this.value = value.doubleValue();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public MutableDouble increment() {
/*  71 */     this.value++;
/*  72 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public MutableDouble decrement() {
/*  80 */     this.value--;
/*  81 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public MutableDouble add(double operand) {
/*  91 */     this.value += operand;
/*  92 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public MutableDouble add(Number operand) {
/* 101 */     this.value += operand.doubleValue();
/* 102 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public MutableDouble subtract(double operand) {
/* 112 */     this.value -= operand;
/* 113 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public MutableDouble subtract(Number operand) {
/* 123 */     this.value -= operand.doubleValue();
/* 124 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public int intValue() {
/* 130 */     return (int)this.value;
/*     */   }
/*     */ 
/*     */   
/*     */   public long longValue() {
/* 135 */     return (long)this.value;
/*     */   }
/*     */ 
/*     */   
/*     */   public float floatValue() {
/* 140 */     return (float)this.value;
/*     */   }
/*     */ 
/*     */   
/*     */   public double doubleValue() {
/* 145 */     return this.value;
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
/*     */   public boolean equals(Object obj) {
/* 162 */     if (obj instanceof MutableDouble) {
/* 163 */       return (Double.doubleToLongBits(((MutableDouble)obj).value) == Double.doubleToLongBits(this.value));
/*     */     }
/* 165 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 170 */     long bits = Double.doubleToLongBits(this.value);
/* 171 */     return (int)(bits ^ bits >>> 32L);
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
/*     */   public int compareTo(MutableDouble other) {
/* 183 */     return NumberUtil.compare(this.value, other.value);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/* 189 */     return String.valueOf(this.value);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\lang\mutable\MutableDouble.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */