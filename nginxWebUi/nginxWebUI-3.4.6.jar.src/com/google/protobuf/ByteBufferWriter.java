/*     */ package com.google.protobuf;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ import java.lang.ref.SoftReference;
/*     */ import java.lang.reflect.Field;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.channels.WritableByteChannel;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ final class ByteBufferWriter
/*     */ {
/*     */   private static final int MIN_CACHED_BUFFER_SIZE = 1024;
/*     */   private static final int MAX_CACHED_BUFFER_SIZE = 16384;
/*     */   private static final float BUFFER_REALLOCATION_THRESHOLD = 0.5F;
/*  71 */   private static final ThreadLocal<SoftReference<byte[]>> BUFFER = new ThreadLocal<>();
/*     */ 
/*     */ 
/*     */   
/*  75 */   private static final Class<?> FILE_OUTPUT_STREAM_CLASS = safeGetClass("java.io.FileOutputStream");
/*     */   
/*  77 */   private static final long CHANNEL_FIELD_OFFSET = getChannelFieldOffset(FILE_OUTPUT_STREAM_CLASS);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static void clearCachedBuffer() {
/*  84 */     BUFFER.set(null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static void write(ByteBuffer buffer, OutputStream output) throws IOException {
/*  92 */     int initialPos = buffer.position();
/*     */     try {
/*  94 */       if (buffer.hasArray()) {
/*     */ 
/*     */         
/*  97 */         output.write(buffer.array(), buffer.arrayOffset() + buffer.position(), buffer.remaining());
/*  98 */       } else if (!writeToChannel(buffer, output)) {
/*     */ 
/*     */         
/* 101 */         byte[] array = getOrCreateBuffer(buffer.remaining());
/* 102 */         while (buffer.hasRemaining()) {
/* 103 */           int length = Math.min(buffer.remaining(), array.length);
/* 104 */           buffer.get(array, 0, length);
/* 105 */           output.write(array, 0, length);
/*     */         } 
/*     */       } 
/*     */     } finally {
/*     */       
/* 110 */       buffer.position(initialPos);
/*     */     } 
/*     */   }
/*     */   
/*     */   private static byte[] getOrCreateBuffer(int requestedSize) {
/* 115 */     requestedSize = Math.max(requestedSize, 1024);
/*     */     
/* 117 */     byte[] buffer = getBuffer();
/*     */     
/* 119 */     if (buffer == null || needToReallocate(requestedSize, buffer.length)) {
/* 120 */       buffer = new byte[requestedSize];
/*     */ 
/*     */       
/* 123 */       if (requestedSize <= 16384) {
/* 124 */         setBuffer(buffer);
/*     */       }
/*     */     } 
/* 127 */     return buffer;
/*     */   }
/*     */ 
/*     */   
/*     */   private static boolean needToReallocate(int requestedSize, int bufferLength) {
/* 132 */     return (bufferLength < requestedSize && bufferLength < requestedSize * 0.5F);
/*     */   }
/*     */ 
/*     */   
/*     */   private static byte[] getBuffer() {
/* 137 */     SoftReference<byte[]> sr = BUFFER.get();
/* 138 */     return (sr == null) ? null : sr.get();
/*     */   }
/*     */   
/*     */   private static void setBuffer(byte[] value) {
/* 142 */     BUFFER.set((SoftReference)new SoftReference<>(value));
/*     */   }
/*     */   
/*     */   private static boolean writeToChannel(ByteBuffer buffer, OutputStream output) throws IOException {
/* 146 */     if (CHANNEL_FIELD_OFFSET >= 0L && FILE_OUTPUT_STREAM_CLASS.isInstance(output)) {
/*     */       
/* 148 */       WritableByteChannel channel = null;
/*     */       try {
/* 150 */         channel = (WritableByteChannel)UnsafeUtil.getObject(output, CHANNEL_FIELD_OFFSET);
/* 151 */       } catch (ClassCastException classCastException) {}
/*     */ 
/*     */       
/* 154 */       if (channel != null) {
/* 155 */         channel.write(buffer);
/* 156 */         return true;
/*     */       } 
/*     */     } 
/* 159 */     return false;
/*     */   }
/*     */   
/*     */   private static Class<?> safeGetClass(String className) {
/*     */     try {
/* 164 */       return Class.forName(className);
/* 165 */     } catch (ClassNotFoundException e) {
/* 166 */       return null;
/*     */     } 
/*     */   }
/*     */   
/*     */   private static long getChannelFieldOffset(Class<?> clazz) {
/*     */     try {
/* 172 */       if (clazz != null && UnsafeUtil.hasUnsafeArrayOperations()) {
/* 173 */         Field field = clazz.getDeclaredField("channel");
/* 174 */         return UnsafeUtil.objectFieldOffset(field);
/*     */       } 
/* 176 */     } catch (Throwable throwable) {}
/*     */ 
/*     */     
/* 179 */     return -1L;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\protobuf\ByteBufferWriter.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */