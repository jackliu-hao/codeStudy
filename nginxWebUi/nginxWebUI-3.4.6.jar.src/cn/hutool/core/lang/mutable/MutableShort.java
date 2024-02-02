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
/*     */ public class MutableShort
/*     */   extends Number
/*     */   implements Comparable<MutableShort>, Mutable<Number>
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   private short value;
/*     */   
/*     */   public MutableShort() {}
/*     */   
/*     */   public MutableShort(short value) {
/*  27 */     this.value = value;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public MutableShort(Number value) {
/*  35 */     this(value.shortValue());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public MutableShort(String value) throws NumberFormatException {
/*  44 */     this.value = Short.parseShort(value);
/*     */   }
/*     */ 
/*     */   
/*     */   public Short get() {
/*  49 */     return Short.valueOf(this.value);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void set(short value) {
/*  57 */     this.value = value;
/*     */   }
/*     */ 
/*     */   
/*     */   public void set(Number value) {
/*  62 */     this.value = value.shortValue();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public MutableShort increment() {
/*  71 */     this.value = (short)(this.value + 1);
/*  72 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public MutableShort decrement() {
/*  80 */     this.value = (short)(this.value - 1);
/*  81 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public MutableShort add(short operand) {
/*  91 */     this.value = (short)(this.value + operand);
/*  92 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public MutableShort add(Number operand) {
/* 102 */     this.value = (short)(this.value + operand.shortValue());
/* 103 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public MutableShort subtract(short operand) {
/* 113 */     this.value = (short)(this.value - operand);
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
/*     */   public MutableShort subtract(Number operand) {
/* 125 */     this.value = (short)(this.value - operand.shortValue());
/* 126 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public short shortValue() {
/* 132 */     return this.value;
/*     */   }
/*     */ 
/*     */   
/*     */   public int intValue() {
/* 137 */     return this.value;
/*     */   }
/*     */ 
/*     */   
/*     */   public long longValue() {
/* 142 */     return this.value;
/*     */   }
/*     */ 
/*     */   
/*     */   public float floatValue() {
/* 147 */     return this.value;
/*     */   }
/*     */ 
/*     */   
/*     */   public double doubleValue() {
/* 152 */     return this.value;
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
/* 169 */     if (obj instanceof MutableShort) {
/* 170 */       return (this.value == ((MutableShort)obj).shortValue());
/*     */     }
/* 172 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 177 */     return this.value;
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
/*     */   public int compareTo(MutableShort other) {
/* 189 */     return NumberUtil.compare(this.value, other.value);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/* 195 */     return String.valueOf(this.value);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\lang\mutable\MutableShort.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */