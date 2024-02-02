/*     */ package org.xnio.conduits;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.channels.FileChannel;
/*     */ import java.nio.channels.ReadableByteChannel;
/*     */ import java.util.zip.Deflater;
/*     */ import org.xnio.Buffers;
/*     */ import org.xnio._private.Messages;
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
/*     */ public final class DeflatingStreamSinkConduit
/*     */   extends AbstractStreamSinkConduit<StreamSinkConduit>
/*     */   implements StreamSinkConduit
/*     */ {
/*  37 */   private static final byte[] NO_BYTES = new byte[0];
/*     */ 
/*     */   
/*     */   private final Deflater deflater;
/*     */ 
/*     */   
/*     */   private final ByteBuffer outBuffer;
/*     */ 
/*     */ 
/*     */   
/*     */   public DeflatingStreamSinkConduit(StreamSinkConduit next, Deflater deflater) {
/*  48 */     super(next);
/*  49 */     this.deflater = deflater;
/*  50 */     this.outBuffer = ByteBuffer.allocate(16384);
/*     */   }
/*     */   
/*     */   public long transferFrom(FileChannel src, long position, long count) throws IOException {
/*  54 */     return src.transferTo(position, count, new ConduitWritableByteChannel(this));
/*     */   }
/*     */   
/*     */   public long transferFrom(StreamSourceChannel source, long count, ByteBuffer throughBuffer) throws IOException {
/*  58 */     return Conduits.transfer((ReadableByteChannel)source, count, throughBuffer, this);
/*     */   }
/*     */   
/*     */   public int write(ByteBuffer src) throws IOException {
/*  62 */     ByteBuffer outBuffer = this.outBuffer;
/*  63 */     byte[] outArray = outBuffer.array();
/*  64 */     Deflater deflater = this.deflater;
/*  65 */     assert outBuffer.arrayOffset() == 0;
/*  66 */     int cnt = 0;
/*     */     
/*     */     int rem;
/*     */     
/*  70 */     while ((rem = src.remaining()) > 0) {
/*  71 */       if (!outBuffer.hasRemaining()) {
/*  72 */         outBuffer.flip();
/*     */         try {
/*  74 */           if (this.next.write(outBuffer) == 0) {
/*  75 */             return cnt;
/*     */           }
/*     */         } finally {
/*  78 */           outBuffer.compact();
/*     */         } 
/*     */       } 
/*  81 */       int pos = src.position();
/*  82 */       if (src.hasArray()) {
/*  83 */         byte[] array = src.array();
/*  84 */         int arrayOffset = src.arrayOffset();
/*  85 */         deflater.setInput(array, arrayOffset + pos, rem);
/*  86 */         int i = deflater.getTotalIn();
/*  87 */         int k = deflater.deflate(outArray, outBuffer.position(), outBuffer.remaining());
/*  88 */         outBuffer.position(outBuffer.position() + k);
/*  89 */         int j = deflater.getTotalIn() - i;
/*  90 */         src.position(pos + j);
/*  91 */         cnt += j; continue;
/*     */       } 
/*  93 */       byte[] bytes = Buffers.take(src);
/*  94 */       deflater.setInput(bytes);
/*  95 */       int c1 = deflater.getTotalIn();
/*  96 */       int dc = deflater.deflate(outArray, outBuffer.position(), outBuffer.remaining());
/*  97 */       outBuffer.position(outBuffer.position() + dc);
/*  98 */       int t = deflater.getTotalIn() - c1;
/*  99 */       src.position(pos + t);
/* 100 */       cnt += t;
/*     */     } 
/*     */     
/* 103 */     return cnt;
/*     */   }
/*     */   
/*     */   public long write(ByteBuffer[] srcs, int offset, int length) throws IOException {
/* 107 */     ByteBuffer outBuffer = this.outBuffer;
/* 108 */     byte[] outArray = outBuffer.array();
/* 109 */     Deflater deflater = this.deflater;
/* 110 */     assert outBuffer.arrayOffset() == 0;
/* 111 */     long cnt = 0L;
/*     */ 
/*     */ 
/*     */     
/* 115 */     for (int i = 0; i < length; i++) {
/* 116 */       ByteBuffer src = srcs[i + offset]; int rem;
/* 117 */       while ((rem = src.remaining()) > 0) {
/* 118 */         if (!outBuffer.hasRemaining()) {
/* 119 */           outBuffer.flip();
/*     */           try {
/* 121 */             if (this.next.write(outBuffer) == 0) {
/* 122 */               return cnt;
/*     */             }
/*     */           } finally {
/* 125 */             outBuffer.compact();
/*     */           } 
/*     */         } 
/* 128 */         int pos = src.position();
/* 129 */         if (src.hasArray()) {
/* 130 */           byte[] array = src.array();
/* 131 */           int arrayOffset = src.arrayOffset();
/* 132 */           deflater.setInput(array, arrayOffset + pos, rem);
/* 133 */           int j = deflater.getTotalIn();
/* 134 */           int m = deflater.deflate(outArray, outBuffer.position(), outBuffer.remaining());
/* 135 */           outBuffer.position(outBuffer.position() + m);
/* 136 */           int k = deflater.getTotalIn() - j;
/* 137 */           src.position(pos + k);
/* 138 */           cnt += k; continue;
/*     */         } 
/* 140 */         byte[] bytes = Buffers.take(src);
/* 141 */         deflater.setInput(bytes);
/* 142 */         int c1 = deflater.getTotalIn();
/* 143 */         int dc = deflater.deflate(outArray, outBuffer.position(), outBuffer.remaining());
/* 144 */         outBuffer.position(outBuffer.position() + dc);
/* 145 */         int t = deflater.getTotalIn() - c1;
/* 146 */         src.position(pos + t);
/* 147 */         cnt += t;
/*     */       } 
/*     */     } 
/*     */     
/* 151 */     return cnt;
/*     */   }
/*     */   public boolean flush() throws IOException {
/*     */     int pos;
/* 155 */     ByteBuffer outBuffer = this.outBuffer;
/* 156 */     byte[] outArray = outBuffer.array();
/* 157 */     Deflater deflater = this.deflater;
/* 158 */     assert outBuffer.arrayOffset() == 0;
/*     */ 
/*     */ 
/*     */     
/* 162 */     deflater.setInput(NO_BYTES);
/*     */     while (true) {
/* 164 */       int rem = outBuffer.remaining();
/* 165 */       pos = outBuffer.position();
/* 166 */       int res = deflater.deflate(outArray, pos, rem, 2);
/* 167 */       if (pos == 0 && res == rem)
/*     */       {
/* 169 */         throw Messages.msg.flushSmallBuffer();
/*     */       }
/* 171 */       if (res > 0) {
/* 172 */         outBuffer.flip();
/*     */         
/* 174 */         try { if (this.next.write(outBuffer) == 0) {
/* 175 */             return false;
/*     */           } }
/*     */         finally
/* 178 */         { outBuffer.compact(); }  continue;
/*     */       }  break;
/* 180 */     }  if (deflater.needsInput() && pos == 0) {
/* 181 */       if (deflater.finished())
/*     */       {
/* 183 */         this.next.terminateWrites();
/*     */       }
/* 185 */       return this.next.flush();
/*     */     } 
/* 187 */     throw Messages.msg.deflaterState();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int writeFinal(ByteBuffer src) throws IOException {
/* 196 */     return Conduits.writeFinalBasic(this, src);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public long writeFinal(ByteBuffer[] srcs, int offset, int length) throws IOException {
/* 202 */     return Conduits.writeFinalBasic(this, srcs, offset, length);
/*     */   }
/*     */   
/*     */   public void terminateWrites() throws IOException {
/* 206 */     this.deflater.finish();
/*     */   }
/*     */   
/*     */   public void truncateWrites() throws IOException {
/* 210 */     this.deflater.finish();
/* 211 */     this.next.truncateWrites();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\conduits\DeflatingStreamSinkConduit.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */