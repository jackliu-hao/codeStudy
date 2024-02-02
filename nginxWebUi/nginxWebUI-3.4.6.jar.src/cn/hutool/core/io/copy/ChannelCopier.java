/*     */ package cn.hutool.core.io.copy;
/*     */ 
/*     */ import cn.hutool.core.io.IORuntimeException;
/*     */ import cn.hutool.core.io.StreamProgress;
/*     */ import cn.hutool.core.lang.Assert;
/*     */ import java.io.IOException;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.channels.ReadableByteChannel;
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
/*     */ public class ChannelCopier
/*     */   extends IoCopier<ReadableByteChannel, WritableByteChannel>
/*     */ {
/*     */   public ChannelCopier() {
/*  27 */     this(8192);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ChannelCopier(int bufferSize) {
/*  36 */     this(bufferSize, -1L);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ChannelCopier(int bufferSize, long count) {
/*  46 */     this(bufferSize, count, (StreamProgress)null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ChannelCopier(int bufferSize, long count, StreamProgress progress) {
/*  57 */     super(bufferSize, count, progress);
/*     */   }
/*     */   
/*     */   public long copy(ReadableByteChannel source, WritableByteChannel target) {
/*     */     long size;
/*  62 */     Assert.notNull(source, "InputStream is null !", new Object[0]);
/*  63 */     Assert.notNull(target, "OutputStream is null !", new Object[0]);
/*     */     
/*  65 */     StreamProgress progress = this.progress;
/*  66 */     if (null != progress) {
/*  67 */       progress.start();
/*     */     }
/*     */     
/*     */     try {
/*  71 */       size = doCopy(source, target, ByteBuffer.allocate(bufferSize(this.count)), progress);
/*  72 */     } catch (IOException e) {
/*  73 */       throw new IORuntimeException(e);
/*     */     } 
/*     */     
/*  76 */     if (null != progress) {
/*  77 */       progress.finish();
/*     */     }
/*  79 */     return size;
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
/*     */   private long doCopy(ReadableByteChannel source, WritableByteChannel target, ByteBuffer buffer, StreamProgress progress) throws IOException {
/*  93 */     long numToRead = (this.count > 0L) ? this.count : Long.MAX_VALUE;
/*  94 */     long total = 0L;
/*     */ 
/*     */     
/*  97 */     while (numToRead > 0L) {
/*  98 */       int read = source.read(buffer);
/*  99 */       if (read < 0) {
/*     */         break;
/*     */       }
/*     */       
/* 103 */       buffer.flip();
/* 104 */       target.write(buffer);
/* 105 */       buffer.clear();
/*     */       
/* 107 */       numToRead -= read;
/* 108 */       total += read;
/* 109 */       if (null != progress) {
/* 110 */         progress.progress(this.count, total);
/*     */       }
/*     */     } 
/*     */     
/* 114 */     return total;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\io\copy\ChannelCopier.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */