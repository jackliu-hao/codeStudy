/*     */ package cn.hutool.core.io.copy;
/*     */ 
/*     */ import cn.hutool.core.io.IORuntimeException;
/*     */ import cn.hutool.core.io.StreamProgress;
/*     */ import cn.hutool.core.lang.Assert;
/*     */ import java.io.IOException;
/*     */ import java.io.Reader;
/*     */ import java.io.Writer;
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
/*     */ public class ReaderWriterCopier
/*     */   extends IoCopier<Reader, Writer>
/*     */ {
/*     */   public ReaderWriterCopier() {
/*  26 */     this(8192);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ReaderWriterCopier(int bufferSize) {
/*  35 */     this(bufferSize, -1L);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ReaderWriterCopier(int bufferSize, long count) {
/*  45 */     this(bufferSize, count, (StreamProgress)null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ReaderWriterCopier(int bufferSize, long count, StreamProgress progress) {
/*  56 */     super(bufferSize, count, progress);
/*     */   }
/*     */   
/*     */   public long copy(Reader source, Writer target) {
/*     */     long size;
/*  61 */     Assert.notNull(source, "InputStream is null !", new Object[0]);
/*  62 */     Assert.notNull(target, "OutputStream is null !", new Object[0]);
/*     */     
/*  64 */     StreamProgress progress = this.progress;
/*  65 */     if (null != progress) {
/*  66 */       progress.start();
/*     */     }
/*     */     
/*     */     try {
/*  70 */       size = doCopy(source, target, new char[bufferSize(this.count)], progress);
/*  71 */       target.flush();
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
/*     */   private long doCopy(Reader source, Writer target, char[] buffer, StreamProgress progress) throws IOException {
/*  93 */     long numToRead = (this.count > 0L) ? this.count : Long.MAX_VALUE;
/*  94 */     long total = 0L;
/*     */ 
/*     */     
/*  97 */     while (numToRead > 0L) {
/*  98 */       int read = source.read(buffer, 0, bufferSize(numToRead));
/*  99 */       if (read < 0) {
/*     */         break;
/*     */       }
/*     */       
/* 103 */       target.write(buffer, 0, read);
/* 104 */       if (this.flushEveryBuffer) {
/* 105 */         target.flush();
/*     */       }
/*     */       
/* 108 */       numToRead -= read;
/* 109 */       total += read;
/* 110 */       if (null != progress) {
/* 111 */         progress.progress(this.count, total);
/*     */       }
/*     */     } 
/*     */     
/* 115 */     return total;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\io\copy\ReaderWriterCopier.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */