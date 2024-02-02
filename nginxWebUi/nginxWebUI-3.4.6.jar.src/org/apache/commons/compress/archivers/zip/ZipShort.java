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
/*     */ public final class ZipShort
/*     */   implements Cloneable, Serializable
/*     */ {
/*  34 */   public static final ZipShort ZERO = new ZipShort(0);
/*     */ 
/*     */   
/*     */   private static final long serialVersionUID = 1L;
/*     */ 
/*     */   
/*     */   private final int value;
/*     */ 
/*     */ 
/*     */   
/*     */   public ZipShort(int value) {
/*  45 */     this.value = value;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ZipShort(byte[] bytes) {
/*  53 */     this(bytes, 0);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ZipShort(byte[] bytes, int offset) {
/*  62 */     this.value = getValue(bytes, offset);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public byte[] getBytes() {
/*  70 */     byte[] result = new byte[2];
/*  71 */     ByteUtils.toLittleEndian(result, this.value, 0, 2);
/*  72 */     return result;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getValue() {
/*  80 */     return this.value;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static byte[] getBytes(int value) {
/*  89 */     byte[] result = new byte[2];
/*  90 */     putShort(value, result, 0);
/*  91 */     return result;
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
/*     */   public static void putShort(int value, byte[] buf, int offset) {
/* 103 */     ByteUtils.toLittleEndian(buf, value, offset, 2);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int getValue(byte[] bytes, int offset) {
/* 113 */     return (int)ByteUtils.fromLittleEndian(bytes, offset, 2);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int getValue(byte[] bytes) {
/* 122 */     return getValue(bytes, 0);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean equals(Object o) {
/* 132 */     if (!(o instanceof ZipShort)) {
/* 133 */       return false;
/*     */     }
/* 135 */     return (this.value == ((ZipShort)o).getValue());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 144 */     return this.value;
/*     */   }
/*     */ 
/*     */   
/*     */   public Object clone() {
/*     */     try {
/* 150 */       return super.clone();
/* 151 */     } catch (CloneNotSupportedException cnfe) {
/*     */       
/* 153 */       throw new RuntimeException(cnfe);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 159 */     return "ZipShort value: " + this.value;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\archivers\zip\ZipShort.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */