/*     */ package org.apache.commons.compress.utils;
/*     */ 
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.Closeable;
/*     */ import java.io.EOFException;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.channels.ReadableByteChannel;
/*     */ import java.nio.file.Files;
/*     */ import java.nio.file.LinkOption;
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
/*     */ public final class IOUtils
/*     */ {
/*     */   private static final int COPY_BUF_SIZE = 8024;
/*     */   private static final int SKIP_BUF_SIZE = 4096;
/*  47 */   public static final LinkOption[] EMPTY_LINK_OPTIONS = new LinkOption[0];
/*     */ 
/*     */ 
/*     */   
/*  51 */   private static final byte[] SKIP_BUF = new byte[4096];
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
/*     */   public static long copy(InputStream input, OutputStream output) throws IOException {
/*  70 */     return copy(input, output, 8024);
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
/*     */   
/*     */   public static long copy(InputStream input, OutputStream output, int buffersize) throws IOException {
/*  89 */     if (buffersize < 1) {
/*  90 */       throw new IllegalArgumentException("buffersize must be bigger than 0");
/*     */     }
/*  92 */     byte[] buffer = new byte[buffersize];
/*  93 */     int n = 0;
/*  94 */     long count = 0L;
/*  95 */     while (-1 != (n = input.read(buffer))) {
/*  96 */       output.write(buffer, 0, n);
/*  97 */       count += n;
/*     */     } 
/*  99 */     return count;
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
/*     */ 
/*     */   
/*     */   public static long skip(InputStream input, long numToSkip) throws IOException {
/* 119 */     long available = numToSkip;
/* 120 */     while (numToSkip > 0L) {
/* 121 */       long skipped = input.skip(numToSkip);
/* 122 */       if (skipped == 0L) {
/*     */         break;
/*     */       }
/* 125 */       numToSkip -= skipped;
/*     */     } 
/*     */     
/* 128 */     while (numToSkip > 0L) {
/* 129 */       int read = readFully(input, SKIP_BUF, 0, 
/* 130 */           (int)Math.min(numToSkip, 4096L));
/* 131 */       if (read < 1) {
/*     */         break;
/*     */       }
/* 134 */       numToSkip -= read;
/*     */     } 
/* 136 */     return available - numToSkip;
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
/*     */   public static int read(File file, byte[] array) throws IOException {
/* 153 */     try (InputStream inputStream = Files.newInputStream(file.toPath(), new java.nio.file.OpenOption[0])) {
/* 154 */       return readFully(inputStream, array, 0, array.length);
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
/*     */ 
/*     */   
/*     */   public static int readFully(InputStream input, byte[] array) throws IOException {
/* 171 */     return readFully(input, array, 0, array.length);
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
/*     */ 
/*     */ 
/*     */   
/*     */   public static int readFully(InputStream input, byte[] array, int offset, int len) throws IOException {
/* 192 */     if (len < 0 || offset < 0 || len + offset > array.length || len + offset < 0) {
/* 193 */       throw new IndexOutOfBoundsException();
/*     */     }
/* 195 */     int count = 0, x = 0;
/* 196 */     while (count != len) {
/* 197 */       x = input.read(array, offset + count, len - count);
/* 198 */       if (x == -1) {
/*     */         break;
/*     */       }
/* 201 */       count += x;
/*     */     } 
/* 203 */     return count;
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
/*     */   public static void readFully(ReadableByteChannel channel, ByteBuffer b) throws IOException {
/* 221 */     int expectedLength = b.remaining();
/* 222 */     int read = 0;
/* 223 */     while (read < expectedLength) {
/* 224 */       int readNow = channel.read(b);
/* 225 */       if (readNow <= 0) {
/*     */         break;
/*     */       }
/* 228 */       read += readNow;
/*     */     } 
/* 230 */     if (read < expectedLength) {
/* 231 */       throw new EOFException();
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static byte[] toByteArray(InputStream input) throws IOException {
/* 254 */     ByteArrayOutputStream output = new ByteArrayOutputStream();
/* 255 */     copy(input, output);
/* 256 */     return output.toByteArray();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void closeQuietly(Closeable c) {
/* 265 */     if (c != null) {
/*     */       try {
/* 267 */         c.close();
/* 268 */       } catch (IOException iOException) {}
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
/*     */   public static void copy(File sourceFile, OutputStream outputStream) throws IOException {
/* 281 */     Files.copy(sourceFile.toPath(), outputStream);
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
/*     */ 
/*     */   
/*     */   public static long copyRange(InputStream input, long len, OutputStream output) throws IOException {
/* 301 */     return copyRange(input, len, output, 8024);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static long copyRange(InputStream input, long len, OutputStream output, int buffersize) throws IOException {
/* 324 */     if (buffersize < 1) {
/* 325 */       throw new IllegalArgumentException("buffersize must be bigger than 0");
/*     */     }
/* 327 */     byte[] buffer = new byte[(int)Math.min(buffersize, len)];
/* 328 */     int n = 0;
/* 329 */     long count = 0L;
/* 330 */     while (count < len && -1 != (n = input.read(buffer, 0, (int)Math.min(len - count, buffer.length)))) {
/* 331 */       output.write(buffer, 0, n);
/* 332 */       count += n;
/*     */     } 
/* 334 */     return count;
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
/*     */   public static byte[] readRange(InputStream input, int len) throws IOException {
/* 349 */     ByteArrayOutputStream output = new ByteArrayOutputStream();
/* 350 */     copyRange(input, len, output);
/* 351 */     return output.toByteArray();
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
/*     */   public static byte[] readRange(ReadableByteChannel input, int len) throws IOException {
/* 366 */     ByteArrayOutputStream output = new ByteArrayOutputStream();
/* 367 */     ByteBuffer b = ByteBuffer.allocate(Math.min(len, 8024));
/* 368 */     int read = 0;
/* 369 */     while (read < len) {
/* 370 */       int readNow = input.read(b);
/* 371 */       if (readNow <= 0) {
/*     */         break;
/*     */       }
/* 374 */       output.write(b.array(), 0, readNow);
/* 375 */       b.rewind();
/* 376 */       read += readNow;
/*     */     } 
/* 378 */     return output.toByteArray();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compres\\utils\IOUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */