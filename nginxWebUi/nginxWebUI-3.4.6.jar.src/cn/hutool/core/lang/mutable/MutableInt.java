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
/*     */ public class MutableInt
/*     */   extends Number
/*     */   implements Comparable<MutableInt>, Mutable<Number>
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   private int value;
/*     */   
/*     */   public MutableInt() {}
/*     */   
/*     */   public MutableInt(int value) {
/*  27 */     this.value = value;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public MutableInt(Number value) {
/*  35 */     this(value.intValue());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public MutableInt(String value) throws NumberFormatException {
/*  44 */     this.value = Integer.parseInt(value);
/*     */   }
/*     */ 
/*     */   
/*     */   public Integer get() {
/*  49 */     return Integer.valueOf(this.value);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void set(int value) {
/*  57 */     this.value = value;
/*     */   }
/*     */ 
/*     */   
/*     */   public void set(Number value) {
/*  62 */     this.value = value.intValue();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public MutableInt increment() {
/*  71 */     this.value++;
/*  72 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public MutableInt decrement() {
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
/*     */   public MutableInt add(int operand) {
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
/*     */   public MutableInt add(Number operand) {
/* 102 */     this.value += operand.intValue();
/* 103 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public MutableInt subtract(int operand) {
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
/*     */   public MutableInt subtract(Number operand) {
/* 125 */     this.value -= operand.intValue();
/* 126 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public int intValue() {
/* 132 */     return this.value;
/*     */   }
/*     */ 
/*     */   
/*     */   public long longValue() {
/* 137 */     return this.value;
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
/* 164 */     if (obj instanceof MutableInt) {
/* 165 */       return (this.value == ((MutableInt)obj).intValue());
/*     */     }
/* 167 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 172 */     return this.value;
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
/*     */   public int compareTo(MutableInt other) {
/* 184 */     return NumberUtil.compare(this.value, other.value);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/* 190 */     return String.valueOf(this.value);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\lang\mutable\MutableInt.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */