/*     */ package io.undertow.conduits;
/*     */ 
/*     */ import io.undertow.UndertowMessages;
/*     */ import io.undertow.server.HttpServerExchange;
/*     */ import io.undertow.util.AttachmentKey;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.channels.FileChannel;
/*     */ import java.nio.channels.ReadableByteChannel;
/*     */ import java.nio.channels.WritableByteChannel;
/*     */ import org.xnio.IoUtils;
/*     */ import org.xnio.channels.StreamSourceChannel;
/*     */ import org.xnio.conduits.AbstractStreamSinkConduit;
/*     */ import org.xnio.conduits.ConduitWritableByteChannel;
/*     */ import org.xnio.conduits.StreamSinkConduit;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class StoredResponseStreamSinkConduit
/*     */   extends AbstractStreamSinkConduit<StreamSinkConduit>
/*     */ {
/*  40 */   public static final AttachmentKey<byte[]> RESPONSE = AttachmentKey.create(byte[].class);
/*     */ 
/*     */   
/*     */   private ByteArrayOutputStream outputStream;
/*     */ 
/*     */   
/*     */   private final HttpServerExchange exchange;
/*     */ 
/*     */ 
/*     */   
/*     */   public StoredResponseStreamSinkConduit(StreamSinkConduit next, HttpServerExchange exchange) {
/*  51 */     super(next);
/*  52 */     this.exchange = exchange;
/*  53 */     long length = exchange.getResponseContentLength();
/*  54 */     if (length <= 0L) {
/*  55 */       this.outputStream = new ByteArrayOutputStream();
/*     */     } else {
/*  57 */       if (length > 2147483647L) {
/*  58 */         throw UndertowMessages.MESSAGES.responseTooLargeToBuffer(Long.valueOf(length));
/*     */       }
/*  60 */       this.outputStream = new ByteArrayOutputStream((int)length);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public long transferFrom(StreamSourceChannel source, long count, ByteBuffer throughBuffer) throws IOException {
/*  66 */     return IoUtils.transfer((ReadableByteChannel)source, count, throughBuffer, (WritableByteChannel)new ConduitWritableByteChannel((StreamSinkConduit)this));
/*     */   }
/*     */ 
/*     */   
/*     */   public long transferFrom(FileChannel src, long position, long count) throws IOException {
/*  71 */     return src.transferTo(position, count, (WritableByteChannel)new ConduitWritableByteChannel((StreamSinkConduit)this));
/*     */   }
/*     */ 
/*     */   
/*     */   public int write(ByteBuffer src) throws IOException {
/*  76 */     int start = src.position();
/*  77 */     int ret = super.write(src);
/*  78 */     if (this.outputStream != null) {
/*  79 */       for (int i = start; i < start + ret; i++) {
/*  80 */         this.outputStream.write(src.get(i));
/*     */       }
/*     */     }
/*  83 */     return ret;
/*     */   }
/*     */ 
/*     */   
/*     */   public long write(ByteBuffer[] srcs, int offs, int len) throws IOException {
/*  88 */     int[] starts = new int[len];
/*  89 */     for (int i = 0; i < len; i++) {
/*  90 */       starts[i] = srcs[i + offs].position();
/*     */     }
/*  92 */     long ret = super.write(srcs, offs, len);
/*  93 */     long rem = ret;
/*     */     
/*  95 */     if (this.outputStream != null) {
/*  96 */       for (int j = 0; j < len; j++) {
/*  97 */         ByteBuffer buf = srcs[j + offs];
/*  98 */         int pos = starts[j];
/*  99 */         while (rem > 0L && pos < buf.position()) {
/* 100 */           this.outputStream.write(buf.get(pos));
/* 101 */           pos++;
/* 102 */           rem--;
/*     */         } 
/*     */       } 
/*     */     }
/* 106 */     return ret;
/*     */   }
/*     */ 
/*     */   
/*     */   public int writeFinal(ByteBuffer src) throws IOException {
/* 111 */     int start = src.position();
/* 112 */     int ret = super.writeFinal(src);
/* 113 */     if (this.outputStream != null) {
/* 114 */       for (int i = start; i < start + ret; i++) {
/* 115 */         this.outputStream.write(src.get(i));
/*     */       }
/* 117 */       if (!src.hasRemaining()) {
/* 118 */         this.exchange.putAttachment(RESPONSE, this.outputStream.toByteArray());
/* 119 */         this.outputStream = null;
/*     */       } 
/*     */     } 
/* 122 */     return ret;
/*     */   }
/*     */ 
/*     */   
/*     */   public long writeFinal(ByteBuffer[] srcs, int offs, int len) throws IOException {
/* 127 */     int[] starts = new int[len];
/* 128 */     long toWrite = 0L;
/* 129 */     for (int i = 0; i < len; i++) {
/* 130 */       starts[i] = srcs[i + offs].position();
/* 131 */       toWrite += srcs[i + offs].remaining();
/*     */     } 
/* 133 */     long ret = super.write(srcs, offs, len);
/* 134 */     long rem = ret;
/*     */     
/* 136 */     if (this.outputStream != null) {
/* 137 */       for (int j = 0; j < len; j++) {
/* 138 */         ByteBuffer buf = srcs[j + offs];
/* 139 */         int pos = starts[j];
/* 140 */         while (rem > 0L && pos < buf.position()) {
/* 141 */           this.outputStream.write(buf.get(pos));
/* 142 */           pos++;
/* 143 */           rem--;
/*     */         } 
/*     */       } 
/* 146 */       if (toWrite == ret) {
/* 147 */         this.exchange.putAttachment(RESPONSE, this.outputStream.toByteArray());
/* 148 */         this.outputStream = null;
/*     */       } 
/*     */     } 
/* 151 */     return ret;
/*     */   }
/*     */ 
/*     */   
/*     */   public void terminateWrites() throws IOException {
/* 156 */     if (this.outputStream != null) {
/* 157 */       this.exchange.putAttachment(RESPONSE, this.outputStream.toByteArray());
/* 158 */       this.outputStream = null;
/*     */     } 
/* 160 */     super.terminateWrites();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\conduits\StoredResponseStreamSinkConduit.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */