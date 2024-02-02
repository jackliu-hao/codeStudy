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
/*     */ public class MutableByte
/*     */   extends Number
/*     */   implements Comparable<MutableByte>, Mutable<Number>
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   private byte value;
/*     */   
/*     */   public MutableByte() {}
/*     */   
/*     */   public MutableByte(byte value) {
/*  27 */     this.value = value;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public MutableByte(Number value) {
/*  35 */     this(value.byteValue());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public MutableByte(String value) throws NumberFormatException {
/*  44 */     this.value = Byte.parseByte(value);
/*     */   }
/*     */ 
/*     */   
/*     */   public Byte get() {
/*  49 */     return Byte.valueOf(this.value);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void set(byte value) {
/*  57 */     this.value = value;
/*     */   }
/*     */ 
/*     */   
/*     */   public void set(Number value) {
/*  62 */     this.value = value.byteValue();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public MutableByte increment() {
/*  71 */     this.value = (byte)(this.value + 1);
/*  72 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public MutableByte decrement() {
/*  80 */     this.value = (byte)(this.value - 1);
/*  81 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public MutableByte add(byte operand) {
/*  91 */     this.value = (byte)(this.value + operand);
/*  92 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public MutableByte add(Number operand) {
/* 102 */     this.value = (byte)(this.value + operand.byteValue());
/* 103 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public MutableByte subtract(byte operand) {
/* 113 */     this.value = (byte)(this.value - operand);
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
/*     */   public MutableByte subtract(Number operand) {
/* 125 */     this.value = (byte)(this.value - operand.byteValue());
/* 126 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public byte byteValue() {
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
/* 169 */     if (obj instanceof MutableByte) {
/* 170 */       return (this.value == ((MutableByte)obj).byteValue());
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
/*     */   public int compareTo(MutableByte other) {
/* 189 */     return NumberUtil.compare(this.value, other.value);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/* 195 */     return String.valueOf(this.value);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\lang\mutable\MutableByte.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */