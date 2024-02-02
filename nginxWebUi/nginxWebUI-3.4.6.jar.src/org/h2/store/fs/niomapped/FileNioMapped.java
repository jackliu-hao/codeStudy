/*     */ package org.h2.store.fs.niomapped;
/*     */ 
/*     */ import java.io.EOFException;
/*     */ import java.io.IOException;
/*     */ import java.lang.ref.WeakReference;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.MappedByteBuffer;
/*     */ import java.nio.channels.FileChannel;
/*     */ import java.nio.channels.FileLock;
/*     */ import java.nio.channels.NonWritableChannelException;
/*     */ import java.nio.file.Paths;
/*     */ import java.nio.file.attribute.FileAttribute;
/*     */ import org.h2.engine.SysProperties;
/*     */ import org.h2.store.fs.FileBaseDefault;
/*     */ import org.h2.store.fs.FileUtils;
/*     */ import org.h2.util.MemoryUnmapper;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ class FileNioMapped
/*     */   extends FileBaseDefault
/*     */ {
/*     */   private static final int GC_TIMEOUT_MS = 10000;
/*     */   private final String name;
/*     */   private final FileChannel.MapMode mode;
/*     */   private FileChannel channel;
/*     */   private MappedByteBuffer mapped;
/*     */   private long fileLength;
/*     */   
/*     */   FileNioMapped(String paramString1, String paramString2) throws IOException {
/*  37 */     if ("r".equals(paramString2)) {
/*  38 */       this.mode = FileChannel.MapMode.READ_ONLY;
/*     */     } else {
/*  40 */       this.mode = FileChannel.MapMode.READ_WRITE;
/*     */     } 
/*  42 */     this.name = paramString1;
/*  43 */     this.channel = FileChannel.open(Paths.get(paramString1, new String[0]), FileUtils.modeToOptions(paramString2), (FileAttribute<?>[])FileUtils.NO_ATTRIBUTES);
/*  44 */     reMap();
/*     */   }
/*     */   
/*     */   private void unMap() throws IOException {
/*  48 */     if (this.mapped == null) {
/*     */       return;
/*     */     }
/*     */     
/*  52 */     this.mapped.force();
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  57 */     if (SysProperties.NIO_CLEANER_HACK && 
/*  58 */       MemoryUnmapper.unmap(this.mapped)) {
/*  59 */       this.mapped = null;
/*     */       
/*     */       return;
/*     */     } 
/*  63 */     WeakReference<MappedByteBuffer> weakReference = new WeakReference<>(this.mapped);
/*  64 */     this.mapped = null;
/*  65 */     long l = System.nanoTime() + 10000000000L;
/*  66 */     while (weakReference.get() != null) {
/*  67 */       if (System.nanoTime() - l > 0L) {
/*  68 */         throw new IOException("Timeout (10000 ms) reached while trying to GC mapped buffer");
/*     */       }
/*  70 */       System.gc();
/*  71 */       Thread.yield();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void reMap() throws IOException {
/*  80 */     if (this.mapped != null) {
/*  81 */       unMap();
/*     */     }
/*  83 */     this.fileLength = this.channel.size();
/*  84 */     checkFileSizeLimit(this.fileLength);
/*     */     
/*  86 */     this.mapped = this.channel.map(this.mode, 0L, this.fileLength);
/*  87 */     int i = this.mapped.limit();
/*  88 */     int j = this.mapped.capacity();
/*  89 */     if (i < this.fileLength || j < this.fileLength) {
/*  90 */       throw new IOException("Unable to map: length=" + i + " capacity=" + j + " length=" + this.fileLength);
/*     */     }
/*     */     
/*  93 */     if (SysProperties.NIO_LOAD_MAPPED) {
/*  94 */       this.mapped.load();
/*     */     }
/*     */   }
/*     */   
/*     */   private static void checkFileSizeLimit(long paramLong) throws IOException {
/*  99 */     if (paramLong > 2147483647L) {
/* 100 */       throw new IOException("File over 2GB is not supported yet when using this file system");
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void implCloseChannel() throws IOException {
/* 107 */     if (this.channel != null) {
/* 108 */       unMap();
/* 109 */       this.channel.close();
/* 110 */       this.channel = null;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 116 */     return "nioMapped:" + this.name;
/*     */   }
/*     */ 
/*     */   
/*     */   public synchronized long size() throws IOException {
/* 121 */     return this.fileLength;
/*     */   }
/*     */ 
/*     */   
/*     */   public synchronized int read(ByteBuffer paramByteBuffer, long paramLong) throws IOException {
/* 126 */     checkFileSizeLimit(paramLong);
/*     */     try {
/* 128 */       int i = paramByteBuffer.remaining();
/* 129 */       if (i == 0) {
/* 130 */         return 0;
/*     */       }
/* 132 */       i = (int)Math.min(i, this.fileLength - paramLong);
/* 133 */       if (i <= 0) {
/* 134 */         return -1;
/*     */       }
/* 136 */       this.mapped.position((int)paramLong);
/* 137 */       this.mapped.get(paramByteBuffer.array(), paramByteBuffer.arrayOffset() + paramByteBuffer.position(), i);
/* 138 */       paramByteBuffer.position(paramByteBuffer.position() + i);
/* 139 */       paramLong += i;
/* 140 */       return i;
/* 141 */     } catch (IllegalArgumentException|java.nio.BufferUnderflowException illegalArgumentException) {
/* 142 */       EOFException eOFException = new EOFException("EOF");
/* 143 */       eOFException.initCause(illegalArgumentException);
/* 144 */       throw eOFException;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected void implTruncate(long paramLong) throws IOException {
/* 151 */     if (this.mode == FileChannel.MapMode.READ_ONLY) {
/* 152 */       throw new NonWritableChannelException();
/*     */     }
/* 154 */     if (paramLong < size()) {
/* 155 */       setFileLength(paramLong);
/*     */     }
/*     */   }
/*     */   
/*     */   public synchronized void setFileLength(long paramLong) throws IOException {
/* 160 */     if (this.mode == FileChannel.MapMode.READ_ONLY) {
/* 161 */       throw new NonWritableChannelException();
/*     */     }
/* 163 */     checkFileSizeLimit(paramLong);
/* 164 */     unMap();
/* 165 */     for (byte b = 0;; b++) {
/*     */       try {
/* 167 */         long l = this.channel.size();
/* 168 */         if (l >= paramLong) {
/* 169 */           this.channel.truncate(paramLong); break;
/*     */         } 
/* 171 */         this.channel.write(ByteBuffer.wrap(new byte[1]), paramLong - 1L);
/*     */         
/*     */         break;
/* 174 */       } catch (IOException iOException) {
/* 175 */         if (b > 16 || !iOException.toString().contains("user-mapped section open")) {
/* 176 */           throw iOException;
/*     */         }
/*     */         
/* 179 */         System.gc();
/*     */       } 
/* 181 */     }  reMap();
/*     */   }
/*     */ 
/*     */   
/*     */   public void force(boolean paramBoolean) throws IOException {
/* 186 */     this.mapped.force();
/* 187 */     this.channel.force(paramBoolean);
/*     */   }
/*     */ 
/*     */   
/*     */   public synchronized int write(ByteBuffer paramByteBuffer, long paramLong) throws IOException {
/* 192 */     checkFileSizeLimit(paramLong);
/* 193 */     int i = paramByteBuffer.remaining();
/*     */     
/* 195 */     if (this.mapped.capacity() < paramLong + i) {
/* 196 */       setFileLength(paramLong + i);
/*     */     }
/* 198 */     this.mapped.position((int)paramLong);
/* 199 */     this.mapped.put(paramByteBuffer);
/* 200 */     return i;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized FileLock tryLock(long paramLong1, long paramLong2, boolean paramBoolean) throws IOException {
/* 206 */     return this.channel.tryLock(paramLong1, paramLong2, paramBoolean);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\store\fs\niomapped\FileNioMapped.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */