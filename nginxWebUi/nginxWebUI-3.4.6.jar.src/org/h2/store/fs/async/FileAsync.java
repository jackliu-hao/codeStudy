/*    */ package org.h2.store.fs.async;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.nio.ByteBuffer;
/*    */ import java.nio.channels.AsynchronousFileChannel;
/*    */ import java.nio.channels.FileLock;
/*    */ import java.nio.file.Paths;
/*    */ import java.nio.file.attribute.FileAttribute;
/*    */ import java.util.concurrent.ExecutionException;
/*    */ import java.util.concurrent.ExecutorService;
/*    */ import java.util.concurrent.Future;
/*    */ import org.h2.store.fs.FileBaseDefault;
/*    */ import org.h2.store.fs.FileUtils;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ class FileAsync
/*    */   extends FileBaseDefault
/*    */ {
/*    */   private final String name;
/*    */   private final AsynchronousFileChannel channel;
/*    */   
/*    */   private static <T> T complete(Future<T> paramFuture) throws IOException {
/* 27 */     boolean bool = false;
/*    */     while (true) {
/*    */       try {
/* 30 */         T t = paramFuture.get();
/* 31 */         if (bool) {
/* 32 */           Thread.currentThread().interrupt();
/*    */         }
/* 34 */         return t;
/* 35 */       } catch (InterruptedException interruptedException) {
/* 36 */         bool = true;
/* 37 */       } catch (ExecutionException executionException) {
/* 38 */         throw new IOException(executionException.getCause());
/*    */       } 
/*    */     } 
/*    */   }
/*    */   
/*    */   FileAsync(String paramString1, String paramString2) throws IOException {
/* 44 */     this.name = paramString1;
/* 45 */     this.channel = AsynchronousFileChannel.open(Paths.get(paramString1, new String[0]), FileUtils.modeToOptions(paramString2), (ExecutorService)null, (FileAttribute<?>[])FileUtils.NO_ATTRIBUTES);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public void implCloseChannel() throws IOException {
/* 51 */     this.channel.close();
/*    */   }
/*    */ 
/*    */   
/*    */   public long size() throws IOException {
/* 56 */     return this.channel.size();
/*    */   }
/*    */ 
/*    */   
/*    */   public int read(ByteBuffer paramByteBuffer, long paramLong) throws IOException {
/* 61 */     return ((Integer)complete(this.channel.read(paramByteBuffer, paramLong))).intValue();
/*    */   }
/*    */ 
/*    */   
/*    */   public int write(ByteBuffer paramByteBuffer, long paramLong) throws IOException {
/* 66 */     return ((Integer)complete(this.channel.write(paramByteBuffer, paramLong))).intValue();
/*    */   }
/*    */ 
/*    */   
/*    */   protected void implTruncate(long paramLong) throws IOException {
/* 71 */     this.channel.truncate(paramLong);
/*    */   }
/*    */ 
/*    */   
/*    */   public void force(boolean paramBoolean) throws IOException {
/* 76 */     this.channel.force(paramBoolean);
/*    */   }
/*    */ 
/*    */   
/*    */   public FileLock tryLock(long paramLong1, long paramLong2, boolean paramBoolean) throws IOException {
/* 81 */     return this.channel.tryLock(paramLong1, paramLong2, paramBoolean);
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 86 */     return "async:" + this.name;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\store\fs\async\FileAsync.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */