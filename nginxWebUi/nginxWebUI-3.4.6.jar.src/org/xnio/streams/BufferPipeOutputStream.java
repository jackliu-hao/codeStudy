/*     */ package org.xnio.streams;
/*     */ 
/*     */ import java.io.Flushable;
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ import java.nio.ByteBuffer;
/*     */ import org.xnio.Pooled;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class BufferPipeOutputStream
/*     */   extends OutputStream
/*     */ {
/*     */   private Pooled<ByteBuffer> buffer;
/*     */   private boolean closed;
/*     */   private final BufferWriter bufferWriterTask;
/*     */   
/*     */   public BufferPipeOutputStream(BufferWriter bufferWriterTask) throws IOException {
/*  51 */     this.bufferWriterTask = bufferWriterTask;
/*  52 */     synchronized (this) {
/*  53 */       this.buffer = bufferWriterTask.getBuffer(true);
/*     */     } 
/*     */   }
/*     */   
/*     */   private static IOException closed() {
/*  58 */     return new IOException("Stream is closed");
/*     */   }
/*     */   
/*     */   private void checkClosed() throws IOException {
/*  62 */     assert Thread.holdsLock(this);
/*  63 */     if (this.closed) {
/*  64 */       throw closed();
/*     */     }
/*     */   }
/*     */   
/*     */   private Pooled<ByteBuffer> getBuffer() throws IOException {
/*  69 */     assert Thread.holdsLock(this);
/*  70 */     Pooled<ByteBuffer> buffer = this.buffer;
/*  71 */     if (buffer != null && ((ByteBuffer)buffer.getResource()).hasRemaining()) {
/*  72 */       return buffer;
/*     */     }
/*  74 */     if (buffer != null) send(false); 
/*  75 */     return this.buffer = this.bufferWriterTask.getBuffer(false);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void write(int b) throws IOException {
/*  81 */     synchronized (this) {
/*  82 */       checkClosed();
/*  83 */       ((ByteBuffer)getBuffer().getResource()).put((byte)b);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void write(byte[] b, int off, int len) throws IOException {
/*  89 */     synchronized (this) {
/*  90 */       checkClosed();
/*  91 */       while (len > 0) {
/*  92 */         ByteBuffer buffer = (ByteBuffer)getBuffer().getResource();
/*  93 */         int cnt = Math.min(len, buffer.remaining());
/*  94 */         buffer.put(b, off, cnt);
/*  95 */         len -= cnt;
/*  96 */         off += cnt;
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private void send(boolean eof) throws IOException {
/* 103 */     assert Thread.holdsLock(this);
/* 104 */     assert !this.closed;
/* 105 */     Pooled<ByteBuffer> pooledBuffer = this.buffer;
/* 106 */     ByteBuffer buffer = (pooledBuffer == null) ? null : (ByteBuffer)pooledBuffer.getResource();
/* 107 */     this.buffer = null;
/* 108 */     if (buffer != null && buffer.position() > 0) {
/* 109 */       buffer.flip();
/* 110 */       send(pooledBuffer, eof);
/* 111 */     } else if (eof) {
/* 112 */       Pooled<ByteBuffer> pooledBuffer1 = getBuffer();
/* 113 */       ByteBuffer buffer1 = (ByteBuffer)pooledBuffer1.getResource();
/* 114 */       buffer1.flip();
/* 115 */       send(pooledBuffer1, eof);
/*     */     } 
/*     */   }
/*     */   
/*     */   private void send(Pooled<ByteBuffer> buffer, boolean eof) throws IOException {
/* 120 */     assert Thread.holdsLock(this);
/*     */     try {
/* 122 */       this.bufferWriterTask.accept(buffer, eof);
/* 123 */     } catch (IOException e) {
/* 124 */       this.closed = true;
/* 125 */       throw e;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void flush() throws IOException {
/* 131 */     flush(false);
/*     */   }
/*     */   
/*     */   private void flush(boolean eof) throws IOException {
/* 135 */     synchronized (this) {
/* 136 */       if (this.closed) {
/*     */         return;
/*     */       }
/* 139 */       send(eof);
/*     */       try {
/* 141 */         this.bufferWriterTask.flush();
/* 142 */       } catch (IOException e) {
/* 143 */         this.closed = true;
/* 144 */         this.buffer = null;
/* 145 */         throw e;
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void close() throws IOException {
/* 152 */     synchronized (this) {
/* 153 */       if (this.closed) {
/*     */         return;
/*     */       }
/*     */       try {
/* 157 */         flush(true);
/*     */       } finally {
/* 159 */         this.closed = true;
/*     */       } 
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
/*     */   public Pooled<ByteBuffer> breakPipe() {
/* 172 */     synchronized (this) {
/* 173 */       if (this.closed) {
/* 174 */         return null;
/*     */       }
/* 176 */       this.closed = true;
/*     */       try {
/* 178 */         return this.buffer;
/*     */       } finally {
/* 180 */         this.buffer = null;
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   public static interface BufferWriter extends Flushable {
/*     */     Pooled<ByteBuffer> getBuffer(boolean param1Boolean) throws IOException;
/*     */     
/*     */     void accept(Pooled<ByteBuffer> param1Pooled, boolean param1Boolean) throws IOException;
/*     */     
/*     */     void flush() throws IOException;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\streams\BufferPipeOutputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */