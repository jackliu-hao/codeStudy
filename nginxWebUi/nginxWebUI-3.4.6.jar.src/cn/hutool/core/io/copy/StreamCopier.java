/*     */ package cn.hutool.core.io.copy;
/*     */ 
/*     */ import cn.hutool.core.io.IORuntimeException;
/*     */ import cn.hutool.core.io.StreamProgress;
/*     */ import cn.hutool.core.lang.Assert;
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
/*     */ public class StreamCopier
/*     */   extends IoCopier<InputStream, OutputStream>
/*     */ {
/*     */   public StreamCopier() {
/*  24 */     this(8192);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public StreamCopier(int bufferSize) {
/*  33 */     this(bufferSize, -1L);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public StreamCopier(int bufferSize, long count) {
/*  43 */     this(bufferSize, count, (StreamProgress)null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public StreamCopier(int bufferSize, long count, StreamProgress progress) {
/*  54 */     super(bufferSize, count, progress);
/*     */   }
/*     */   
/*     */   public long copy(InputStream source, OutputStream target) {
/*     */     long size;
/*  59 */     Assert.notNull(source, "InputStream is null !", new Object[0]);
/*  60 */     Assert.notNull(target, "OutputStream is null !", new Object[0]);
/*     */     
/*  62 */     StreamProgress progress = this.progress;
/*  63 */     if (null != progress) {
/*  64 */       progress.start();
/*     */     }
/*     */     
/*     */     try {
/*  68 */       size = doCopy(source, target, new byte[bufferSize(this.count)], progress);
/*  69 */       target.flush();
/*  70 */     } catch (IOException e) {
/*  71 */       throw new IORuntimeException(e);
/*     */     } 
/*     */     
/*  74 */     if (null != progress) {
/*  75 */       progress.finish();
/*     */     }
/*     */     
/*  78 */     return size;
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
/*     */   private long doCopy(InputStream source, OutputStream target, byte[] buffer, StreamProgress progress) throws IOException {
/*  92 */     long numToRead = (this.count > 0L) ? this.count : Long.MAX_VALUE;
/*  93 */     long total = 0L;
/*     */ 
/*     */     
/*  96 */     while (numToRead > 0L) {
/*  97 */       int read = source.read(buffer, 0, bufferSize(numToRead));
/*  98 */       if (read < 0) {
/*     */         break;
/*     */       }
/*     */       
/* 102 */       target.write(buffer, 0, read);
/* 103 */       if (this.flushEveryBuffer) {
/* 104 */         target.flush();
/*     */       }
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


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\io\copy\StreamCopier.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */