/*     */ package org.apache.commons.compress.utils;
/*     */ 
/*     */ import java.io.DataInput;
/*     */ import java.io.DataOutput;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
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
/*     */ public final class ByteUtils
/*     */ {
/*  38 */   public static final byte[] EMPTY_BYTE_ARRAY = new byte[0];
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
/*     */   public static long fromLittleEndian(byte[] bytes) {
/*  76 */     return fromLittleEndian(bytes, 0, bytes.length);
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
/*     */   public static long fromLittleEndian(byte[] bytes, int off, int length) {
/*  88 */     checkReadLength(length);
/*  89 */     long l = 0L;
/*  90 */     for (int i = 0; i < length; i++) {
/*  91 */       l |= (bytes[off + i] & 0xFFL) << 8 * i;
/*     */     }
/*  93 */     return l;
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
/*     */   public static long fromLittleEndian(InputStream in, int length) throws IOException {
/* 107 */     checkReadLength(length);
/* 108 */     long l = 0L;
/* 109 */     for (int i = 0; i < length; i++) {
/* 110 */       long b = in.read();
/* 111 */       if (b == -1L) {
/* 112 */         throw new IOException("Premature end of data");
/*     */       }
/* 114 */       l |= b << i * 8;
/*     */     } 
/* 116 */     return l;
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
/*     */   public static long fromLittleEndian(ByteSupplier supplier, int length) throws IOException {
/* 133 */     checkReadLength(length);
/* 134 */     long l = 0L;
/* 135 */     for (int i = 0; i < length; i++) {
/* 136 */       long b = supplier.getAsByte();
/* 137 */       if (b == -1L) {
/* 138 */         throw new IOException("Premature end of data");
/*     */       }
/* 140 */       l |= b << i * 8;
/*     */     } 
/* 142 */     return l;
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
/*     */   public static long fromLittleEndian(DataInput in, int length) throws IOException {
/* 156 */     checkReadLength(length);
/* 157 */     long l = 0L;
/* 158 */     for (int i = 0; i < length; i++) {
/* 159 */       long b = in.readUnsignedByte();
/* 160 */       l |= b << i * 8;
/*     */     } 
/* 162 */     return l;
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
/*     */   public static void toLittleEndian(byte[] b, long value, int off, int length) {
/* 174 */     long num = value;
/* 175 */     for (int i = 0; i < length; i++) {
/* 176 */       b[off + i] = (byte)(int)(num & 0xFFL);
/* 177 */       num >>= 8L;
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
/*     */   public static void toLittleEndian(OutputStream out, long value, int length) throws IOException {
/* 192 */     long num = value;
/* 193 */     for (int i = 0; i < length; i++) {
/* 194 */       out.write((int)(num & 0xFFL));
/* 195 */       num >>= 8L;
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
/*     */   public static void toLittleEndian(ByteConsumer consumer, long value, int length) throws IOException {
/* 209 */     long num = value;
/* 210 */     for (int i = 0; i < length; i++) {
/* 211 */       consumer.accept((int)(num & 0xFFL));
/* 212 */       num >>= 8L;
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
/*     */   public static void toLittleEndian(DataOutput out, long value, int length) throws IOException {
/* 227 */     long num = value;
/* 228 */     for (int i = 0; i < length; i++) {
/* 229 */       out.write((int)(num & 0xFFL));
/* 230 */       num >>= 8L;
/*     */     } 
/*     */   }
/*     */   
/*     */   public static interface ByteSupplier {
/*     */     int getAsByte() throws IOException; }
/*     */   
/*     */   public static interface ByteConsumer {
/*     */     void accept(int param1Int) throws IOException; }
/*     */   
/*     */   public static class InputStreamByteSupplier implements ByteSupplier { public InputStreamByteSupplier(InputStream is) {
/* 241 */       this.is = is;
/*     */     }
/*     */     private final InputStream is;
/*     */     public int getAsByte() throws IOException {
/* 245 */       return this.is.read();
/*     */     } }
/*     */ 
/*     */ 
/*     */   
/*     */   public static class OutputStreamByteConsumer
/*     */     implements ByteConsumer
/*     */   {
/*     */     private final OutputStream os;
/*     */     
/*     */     public OutputStreamByteConsumer(OutputStream os) {
/* 256 */       this.os = os;
/*     */     }
/*     */     
/*     */     public void accept(int b) throws IOException {
/* 260 */       this.os.write(b);
/*     */     }
/*     */   }
/*     */   
/*     */   private static void checkReadLength(int length) {
/* 265 */     if (length > 8)
/* 266 */       throw new IllegalArgumentException("Can't read more than eight bytes into a long value"); 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compres\\utils\ByteUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */