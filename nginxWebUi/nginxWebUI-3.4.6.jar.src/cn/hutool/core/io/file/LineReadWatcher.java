/*    */ package cn.hutool.core.io.file;
/*    */ 
/*    */ import cn.hutool.core.io.FileUtil;
/*    */ import cn.hutool.core.io.IORuntimeException;
/*    */ import cn.hutool.core.io.LineHandler;
/*    */ import cn.hutool.core.io.watch.SimpleWatcher;
/*    */ import java.io.IOException;
/*    */ import java.io.RandomAccessFile;
/*    */ import java.nio.charset.Charset;
/*    */ import java.nio.file.Path;
/*    */ import java.nio.file.WatchEvent;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class LineReadWatcher
/*    */   extends SimpleWatcher
/*    */   implements Runnable
/*    */ {
/*    */   private final RandomAccessFile randomAccessFile;
/*    */   private final Charset charset;
/*    */   private final LineHandler lineHandler;
/*    */   
/*    */   public LineReadWatcher(RandomAccessFile randomAccessFile, Charset charset, LineHandler lineHandler) {
/* 34 */     this.randomAccessFile = randomAccessFile;
/* 35 */     this.charset = charset;
/* 36 */     this.lineHandler = lineHandler;
/*    */   }
/*    */ 
/*    */   
/*    */   public void run() {
/* 41 */     onModify(null, null);
/*    */   }
/*    */ 
/*    */   
/*    */   public void onModify(WatchEvent<?> event, Path currentPath) {
/* 46 */     RandomAccessFile randomAccessFile = this.randomAccessFile;
/* 47 */     Charset charset = this.charset;
/* 48 */     LineHandler lineHandler = this.lineHandler;
/*    */     
/*    */     try {
/* 51 */       long currentLength = randomAccessFile.length();
/* 52 */       long position = randomAccessFile.getFilePointer();
/* 53 */       if (position == currentLength) {
/*    */         return;
/*    */       }
/* 56 */       if (currentLength < position) {
/*    */         
/* 58 */         randomAccessFile.seek(currentLength);
/*    */         
/*    */         return;
/*    */       } 
/*    */       
/* 63 */       FileUtil.readLines(randomAccessFile, charset, lineHandler);
/*    */ 
/*    */       
/* 66 */       randomAccessFile.seek(currentLength);
/* 67 */     } catch (IOException e) {
/* 68 */       throw new IORuntimeException(e);
/*    */     } 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\io\file\LineReadWatcher.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */