/*     */ package org.apache.commons.compress.archivers.zip;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ import org.apache.commons.compress.utils.ByteUtils;
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
/*     */ public final class ZipLong
/*     */   implements Cloneable, Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   private final long value;
/*  37 */   public static final ZipLong CFH_SIG = new ZipLong(33639248L);
/*     */ 
/*     */   
/*  40 */   public static final ZipLong LFH_SIG = new ZipLong(67324752L);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  50 */   public static final ZipLong DD_SIG = new ZipLong(134695760L);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  57 */   static final ZipLong ZIP64_MAGIC = new ZipLong(4294967295L);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  67 */   public static final ZipLong SINGLE_SEGMENT_SPLIT_MARKER = new ZipLong(808471376L);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  74 */   public static final ZipLong AED_SIG = new ZipLong(134630224L);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ZipLong(long value) {
/*  81 */     this.value = value;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ZipLong(int value) {
/*  90 */     this.value = value;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ZipLong(byte[] bytes) {
/*  98 */     this(bytes, 0);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ZipLong(byte[] bytes, int offset) {
/* 107 */     this.value = getValue(bytes, offset);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public byte[] getBytes() {
/* 115 */     return getBytes(this.value);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long getValue() {
/* 123 */     return this.value;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getIntValue() {
/* 131 */     return (int)this.value;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static byte[] getBytes(long value) {
/* 139 */     byte[] result = new byte[4];
/* 140 */     putLong(value, result, 0);
/* 141 */     return result;
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
/*     */   public static void putLong(long value, byte[] buf, int offset) {
/* 154 */     ByteUtils.toLittleEndian(buf, value, offset, 4);
/*     */   }
/*     */   
/*     */   public void putLong(byte[] buf, int offset) {
/* 158 */     putLong(this.value, buf, offset);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static long getValue(byte[] bytes, int offset) {
/* 168 */     return ByteUtils.fromLittleEndian(bytes, offset, 4);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static long getValue(byte[] bytes) {
/* 177 */     return getValue(bytes, 0);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean equals(Object o) {
/* 187 */     if (!(o instanceof ZipLong)) {
/* 188 */       return false;
/*     */     }
/* 190 */     return (this.value == ((ZipLong)o).getValue());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 199 */     return (int)this.value;
/*     */   }
/*     */ 
/*     */   
/*     */   public Object clone() {
/*     */     try {
/* 205 */       return super.clone();
/* 206 */     } catch (CloneNotSupportedException cnfe) {
/*     */       
/* 208 */       throw new RuntimeException(cnfe);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 214 */     return "ZipLong value: " + this.value;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\archivers\zip\ZipLong.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */