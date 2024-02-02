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
/*     */ public class MutableFloat
/*     */   extends Number
/*     */   implements Comparable<MutableFloat>, Mutable<Number>
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   private float value;
/*     */   
/*     */   public MutableFloat() {}
/*     */   
/*     */   public MutableFloat(float value) {
/*  27 */     this.value = value;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public MutableFloat(Number value) {
/*  35 */     this(value.floatValue());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public MutableFloat(String value) throws NumberFormatException {
/*  44 */     this.value = Float.parseFloat(value);
/*     */   }
/*     */ 
/*     */   
/*     */   public Float get() {
/*  49 */     return Float.valueOf(this.value);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void set(float value) {
/*  57 */     this.value = value;
/*     */   }
/*     */ 
/*     */   
/*     */   public void set(Number value) {
/*  62 */     this.value = value.floatValue();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public MutableFloat increment() {
/*  71 */     this.value++;
/*  72 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public MutableFloat decrement() {
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
/*     */   public MutableFloat add(float operand) {
/*  91 */     this.value += operand;
/*  92 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public MutableFloat add(Number operand) {
/* 102 */     this.value += operand.floatValue();
/* 103 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public MutableFloat subtract(float operand) {
/* 113 */     this.value -= operand;
/* 114 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public MutableFloat subtract(Number operand) {
/* 125 */     this.value -= operand.floatValue();
/* 126 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public int intValue() {
/* 132 */     return (int)this.value;
/*     */   }
/*     */ 
/*     */   
/*     */   public long longValue() {
/* 137 */     return (long)this.value;
/*     */   }
/*     */ 
/*     */   
/*     */   public float floatValue() {
/* 142 */     return this.value;
/*     */   }
/*     */ 
/*     */   
/*     */   public double doubleValue() {
/* 147 */     return this.value;
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
/* 164 */     if (obj instanceof MutableFloat) {
/* 165 */       return (Float.floatToIntBits(((MutableFloat)obj).value) == Float.floatToIntBits(this.value));
/*     */     }
/* 167 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 172 */     return Float.floatToIntBits(this.value);
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
/*     */   public int compareTo(MutableFloat other) {
/* 184 */     return NumberUtil.compare(this.value, other.value);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/* 190 */     return String.valueOf(this.value);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\lang\mutable\MutableFloat.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */