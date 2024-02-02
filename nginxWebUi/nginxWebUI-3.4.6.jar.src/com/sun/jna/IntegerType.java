/*     */ package com.sun.jna;
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
/*     */ public abstract class IntegerType
/*     */   extends Number
/*     */   implements NativeMapped
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   private int size;
/*     */   private Number number;
/*     */   private boolean unsigned;
/*     */   private long value;
/*     */   
/*     */   public IntegerType(int size) {
/*  52 */     this(size, 0L, false);
/*     */   }
/*     */ 
/*     */   
/*     */   public IntegerType(int size, boolean unsigned) {
/*  57 */     this(size, 0L, unsigned);
/*     */   }
/*     */ 
/*     */   
/*     */   public IntegerType(int size, long value) {
/*  62 */     this(size, value, false);
/*     */   }
/*     */ 
/*     */   
/*     */   public IntegerType(int size, long value, boolean unsigned) {
/*  67 */     this.size = size;
/*  68 */     this.unsigned = unsigned;
/*  69 */     setValue(value);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setValue(long value) {
/*  76 */     long truncated = value;
/*  77 */     this.value = value;
/*  78 */     switch (this.size) {
/*     */       case 1:
/*  80 */         if (this.unsigned) {
/*  81 */           this.value = value & 0xFFL;
/*     */         }
/*  83 */         truncated = (byte)(int)value;
/*  84 */         this.number = Byte.valueOf((byte)(int)value);
/*     */         break;
/*     */       case 2:
/*  87 */         if (this.unsigned) {
/*  88 */           this.value = value & 0xFFFFL;
/*     */         }
/*  90 */         truncated = (short)(int)value;
/*  91 */         this.number = Short.valueOf((short)(int)value);
/*     */         break;
/*     */       case 4:
/*  94 */         if (this.unsigned) {
/*  95 */           this.value = value & 0xFFFFFFFFL;
/*     */         }
/*  97 */         truncated = (int)value;
/*  98 */         this.number = Integer.valueOf((int)value);
/*     */         break;
/*     */       case 8:
/* 101 */         this.number = Long.valueOf(value);
/*     */         break;
/*     */       default:
/* 104 */         throw new IllegalArgumentException("Unsupported size: " + this.size);
/*     */     } 
/* 106 */     if (this.size < 8) {
/* 107 */       long mask = (1L << this.size * 8) - 1L ^ 0xFFFFFFFFFFFFFFFFL;
/* 108 */       if ((value < 0L && truncated != value) || (value >= 0L && (mask & value) != 0L))
/*     */       {
/* 110 */         throw new IllegalArgumentException("Argument value 0x" + 
/* 111 */             Long.toHexString(value) + " exceeds native capacity (" + this.size + " bytes) mask=0x" + 
/* 112 */             Long.toHexString(mask));
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public Object toNative() {
/* 119 */     return this.number;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object fromNative(Object nativeValue, FromNativeContext context) {
/* 126 */     long value = (nativeValue == null) ? 0L : ((Number)nativeValue).longValue();
/* 127 */     IntegerType number = (IntegerType)Klass.newInstance(getClass());
/* 128 */     number.setValue(value);
/* 129 */     return number;
/*     */   }
/*     */ 
/*     */   
/*     */   public Class<?> nativeType() {
/* 134 */     return this.number.getClass();
/*     */   }
/*     */ 
/*     */   
/*     */   public int intValue() {
/* 139 */     return (int)this.value;
/*     */   }
/*     */ 
/*     */   
/*     */   public long longValue() {
/* 144 */     return this.value;
/*     */   }
/*     */ 
/*     */   
/*     */   public float floatValue() {
/* 149 */     return this.number.floatValue();
/*     */   }
/*     */ 
/*     */   
/*     */   public double doubleValue() {
/* 154 */     return this.number.doubleValue();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object rhs) {
/* 159 */     return (rhs instanceof IntegerType && this.number
/* 160 */       .equals(((IntegerType)rhs).number));
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 165 */     return this.number.toString();
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 170 */     return this.number.hashCode();
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
/*     */   public static <T extends IntegerType> int compare(T v1, T v2) {
/* 188 */     if (v1 == v2)
/* 189 */       return 0; 
/* 190 */     if (v1 == null)
/* 191 */       return 1; 
/* 192 */     if (v2 == null) {
/* 193 */       return -1;
/*     */     }
/* 195 */     return compare(v1.longValue(), v2.longValue());
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
/*     */   public static int compare(IntegerType v1, long v2) {
/* 211 */     if (v1 == null) {
/* 212 */       return 1;
/*     */     }
/* 214 */     return compare(v1.longValue(), v2);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static final int compare(long v1, long v2) {
/* 220 */     if (v1 == v2)
/* 221 */       return 0; 
/* 222 */     if (v1 < v2) {
/* 223 */       return -1;
/*     */     }
/* 225 */     return 1;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\jna\IntegerType.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */