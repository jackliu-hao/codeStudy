/*     */ package io.undertow.io;
/*     */ 
/*     */ import io.undertow.UndertowMessages;
/*     */ import io.undertow.connector.ByteBufferPool;
/*     */ import io.undertow.connector.PooledByteBuffer;
/*     */ import io.undertow.server.HttpServerExchange;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.channels.ReadableByteChannel;
/*     */ import org.xnio.Bits;
/*     */ import org.xnio.channels.Channels;
/*     */ import org.xnio.channels.EmptyStreamSourceChannel;
/*     */ import org.xnio.channels.StreamSourceChannel;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class UndertowInputStream
/*     */   extends InputStream
/*     */ {
/*     */   private final StreamSourceChannel channel;
/*     */   private final ByteBufferPool bufferPool;
/*     */   private static final int FLAG_CLOSED = 1;
/*     */   private static final int FLAG_FINISHED = 2;
/*     */   private int state;
/*     */   private PooledByteBuffer pooled;
/*     */   
/*     */   public UndertowInputStream(HttpServerExchange exchange) {
/*  57 */     if (exchange.isRequestChannelAvailable()) {
/*  58 */       this.channel = exchange.getRequestChannel();
/*     */     } else {
/*  60 */       this.channel = (StreamSourceChannel)new EmptyStreamSourceChannel(exchange.getIoThread());
/*     */     } 
/*  62 */     this.bufferPool = exchange.getConnection().getByteBufferPool();
/*     */   }
/*     */ 
/*     */   
/*     */   public int read() throws IOException {
/*  67 */     byte[] b = new byte[1];
/*  68 */     int read = read(b);
/*  69 */     if (read == -1) {
/*  70 */       return -1;
/*     */     }
/*  72 */     return b[0] & 0xFF;
/*     */   }
/*     */ 
/*     */   
/*     */   public int read(byte[] b) throws IOException {
/*  77 */     return read(b, 0, b.length);
/*     */   }
/*     */ 
/*     */   
/*     */   public int read(byte[] b, int off, int len) throws IOException {
/*  82 */     if (Thread.currentThread() == this.channel.getIoThread()) {
/*  83 */       throw UndertowMessages.MESSAGES.blockingIoFromIOThread();
/*     */     }
/*  85 */     if (Bits.anyAreSet(this.state, 1)) {
/*  86 */       throw UndertowMessages.MESSAGES.streamIsClosed();
/*     */     }
/*  88 */     readIntoBuffer();
/*  89 */     if (Bits.anyAreSet(this.state, 2)) {
/*  90 */       return -1;
/*     */     }
/*  92 */     if (len == 0) {
/*  93 */       return 0;
/*     */     }
/*  95 */     ByteBuffer buffer = this.pooled.getBuffer();
/*  96 */     int copied = Math.min(buffer.remaining(), len);
/*  97 */     buffer.get(b, off, copied);
/*  98 */     if (!buffer.hasRemaining()) {
/*  99 */       this.pooled.close();
/* 100 */       this.pooled = null;
/*     */     } 
/* 102 */     return copied;
/*     */   }
/*     */   
/*     */   private void readIntoBuffer() throws IOException {
/* 106 */     if (this.pooled == null && !Bits.anyAreSet(this.state, 2)) {
/* 107 */       this.pooled = this.bufferPool.allocate();
/*     */       
/* 109 */       int res = Channels.readBlocking((ReadableByteChannel)this.channel, this.pooled.getBuffer());
/* 110 */       this.pooled.getBuffer().flip();
/* 111 */       if (res == -1) {
/* 112 */         this.state |= 0x2;
/* 113 */         this.pooled.close();
/* 114 */         this.pooled = null;
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   private void readIntoBufferNonBlocking() throws IOException {
/* 120 */     if (this.pooled == null && !Bits.anyAreSet(this.state, 2)) {
/* 121 */       this.pooled = this.bufferPool.allocate();
/* 122 */       int res = this.channel.read(this.pooled.getBuffer());
/* 123 */       if (res == 0) {
/* 124 */         this.pooled.close();
/* 125 */         this.pooled = null;
/*     */         return;
/*     */       } 
/* 128 */       this.pooled.getBuffer().flip();
/* 129 */       if (res == -1) {
/* 130 */         this.state |= 0x2;
/* 131 */         this.pooled.close();
/* 132 */         this.pooled = null;
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public int available() throws IOException {
/* 139 */     if (Bits.anyAreSet(this.state, 1)) {
/* 140 */       throw UndertowMessages.MESSAGES.streamIsClosed();
/*     */     }
/* 142 */     readIntoBufferNonBlocking();
/* 143 */     if (Bits.anyAreSet(this.state, 2)) {
/* 144 */       return -1;
/*     */     }
/* 146 */     if (this.pooled == null) {
/* 147 */       return 0;
/*     */     }
/* 149 */     return this.pooled.getBuffer().remaining();
/*     */   }
/*     */ 
/*     */   
/*     */   public void close() throws IOException {
/* 154 */     if (Bits.anyAreSet(this.state, 1)) {
/*     */       return;
/*     */     }
/* 157 */     this.state |= 0x1;
/*     */     try {
/* 159 */       while (Bits.allAreClear(this.state, 2)) {
/* 160 */         readIntoBuffer();
/* 161 */         if (this.pooled != null) {
/* 162 */           this.pooled.close();
/* 163 */           this.pooled = null;
/*     */         } 
/*     */       } 
/*     */     } finally {
/* 167 */       if (this.pooled != null) {
/* 168 */         this.pooled.close();
/* 169 */         this.pooled = null;
/*     */       } 
/* 171 */       this.channel.shutdownReads();
/* 172 */       this.state |= 0x2;
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\io\UndertowInputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */