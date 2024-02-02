/*     */ package io.undertow.websockets.core.function;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.MappedByteBuffer;
/*     */ import java.nio.channels.FileChannel;
/*     */ import java.nio.channels.FileLock;
/*     */ import java.nio.channels.ReadableByteChannel;
/*     */ import java.nio.channels.SeekableByteChannel;
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
/*     */ public class ChannelFunctionFileChannel
/*     */   extends FileChannel
/*     */ {
/*     */   private final ChannelFunction[] functions;
/*     */   private final FileChannel channel;
/*     */   
/*     */   public ChannelFunctionFileChannel(FileChannel channel, ChannelFunction... functions) {
/*  36 */     this.channel = channel;
/*  37 */     this.functions = functions;
/*     */   }
/*     */ 
/*     */   
/*     */   public long position() throws IOException {
/*  42 */     return this.channel.position();
/*     */   }
/*     */ 
/*     */   
/*     */   public FileChannel position(long newPosition) throws IOException {
/*  47 */     this.channel.position(newPosition);
/*  48 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public long size() throws IOException {
/*  53 */     return this.channel.size();
/*     */   }
/*     */ 
/*     */   
/*     */   public FileChannel truncate(long size) throws IOException {
/*  58 */     this.channel.truncate(size);
/*  59 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public void force(boolean metaData) throws IOException {
/*  64 */     this.channel.force(metaData);
/*     */   }
/*     */ 
/*     */   
/*     */   public MappedByteBuffer map(FileChannel.MapMode mode, long position, long size) throws IOException {
/*  69 */     return this.channel.map(mode, position, size);
/*     */   }
/*     */ 
/*     */   
/*     */   public FileLock lock(long position, long size, boolean shared) throws IOException {
/*  74 */     return this.channel.lock(position, size, shared);
/*     */   }
/*     */ 
/*     */   
/*     */   public FileLock tryLock(long position, long size, boolean shared) throws IOException {
/*  79 */     return this.channel.tryLock(position, size, shared);
/*     */   }
/*     */ 
/*     */   
/*     */   protected void implCloseChannel() throws IOException {
/*  84 */     this.channel.close();
/*     */   }
/*     */ 
/*     */   
/*     */   public int write(ByteBuffer src, long position) throws IOException {
/*  89 */     beforeWriting(src);
/*  90 */     return this.channel.write(src, position);
/*     */   }
/*     */ 
/*     */   
/*     */   public int read(ByteBuffer dst) throws IOException {
/*  95 */     int pos = dst.position();
/*  96 */     int r = this.channel.read(dst);
/*  97 */     if (r > 0) {
/*  98 */       afterReading(dst, pos, r);
/*     */     }
/* 100 */     return r;
/*     */   }
/*     */ 
/*     */   
/*     */   public long read(ByteBuffer[] dsts, int offset, int length) throws IOException {
/* 105 */     int[] positions = new int[length];
/* 106 */     for (int i = 0; i < positions.length; i++) {
/* 107 */       positions[i] = dsts[i].position();
/*     */     }
/* 109 */     long r = this.channel.read(dsts, offset, length);
/* 110 */     if (r > 0L) {
/* 111 */       for (int j = offset; j < length; j++) {
/* 112 */         ByteBuffer dst = dsts[j];
/* 113 */         afterReading(dst, positions[j], dst.position());
/*     */       } 
/*     */     }
/* 116 */     return r;
/*     */   }
/*     */ 
/*     */   
/*     */   public int write(ByteBuffer src) throws IOException {
/* 121 */     beforeWriting(src);
/* 122 */     return this.channel.write(src);
/*     */   }
/*     */ 
/*     */   
/*     */   public long write(ByteBuffer[] srcs, int offset, int length) throws IOException {
/* 127 */     for (int i = offset; i < length; i++) {
/* 128 */       beforeWriting(srcs[i]);
/*     */     }
/* 130 */     return this.channel.write(srcs, offset, length);
/*     */   }
/*     */ 
/*     */   
/*     */   public int read(ByteBuffer dst, long position) throws IOException {
/* 135 */     int pos = dst.position();
/* 136 */     int r = this.channel.read(dst, position);
/* 137 */     if (r > 0) {
/* 138 */       afterReading(dst, pos, r);
/*     */     }
/* 140 */     return r;
/*     */   }
/*     */ 
/*     */   
/*     */   public long transferTo(long position, long count, WritableByteChannel target) throws IOException {
/* 145 */     return this.channel.transferTo(position, count, new ChannelFunctionWritableByteChannel(target, this.functions));
/*     */   }
/*     */ 
/*     */   
/*     */   public long transferFrom(ReadableByteChannel src, long position, long count) throws IOException {
/* 150 */     return this.channel.transferFrom(new ChannelFunctionReadableByteChannel(this.channel, this.functions), position, count);
/*     */   }
/*     */ 
/*     */   
/*     */   private void beforeWriting(ByteBuffer buffer) throws IOException {
/* 155 */     for (ChannelFunction func : this.functions) {
/* 156 */       int pos = buffer.position();
/* 157 */       func.beforeWrite(buffer, pos, buffer.limit() - pos);
/*     */     } 
/*     */   }
/*     */   
/*     */   private void afterReading(ByteBuffer buffer, int position, int length) throws IOException {
/* 162 */     for (ChannelFunction func : this.functions)
/* 163 */       func.afterRead(buffer, position, length); 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\websockets\core\function\ChannelFunctionFileChannel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */