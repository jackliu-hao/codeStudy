/*     */ package cn.hutool.core.io.file;
/*     */ 
/*     */ import cn.hutool.core.date.DateUnit;
/*     */ import cn.hutool.core.exceptions.UtilException;
/*     */ import cn.hutool.core.io.FileUtil;
/*     */ import cn.hutool.core.io.IORuntimeException;
/*     */ import cn.hutool.core.io.LineHandler;
/*     */ import cn.hutool.core.lang.Console;
/*     */ import cn.hutool.core.util.CharsetUtil;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.RandomAccessFile;
/*     */ import java.io.Serializable;
/*     */ import java.nio.charset.Charset;
/*     */ import java.util.Stack;
/*     */ import java.util.concurrent.ExecutionException;
/*     */ import java.util.concurrent.Executors;
/*     */ import java.util.concurrent.ScheduledExecutorService;
/*     */ import java.util.concurrent.ScheduledFuture;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Tailer
/*     */   implements Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*  33 */   public static final LineHandler CONSOLE_HANDLER = new ConsoleLineHandler();
/*     */ 
/*     */   
/*     */   private final Charset charset;
/*     */ 
/*     */   
/*     */   private final LineHandler lineHandler;
/*     */ 
/*     */   
/*     */   private final int initReadLine;
/*     */ 
/*     */   
/*     */   private final long period;
/*     */ 
/*     */   
/*     */   private final RandomAccessFile randomAccessFile;
/*     */   
/*     */   private final ScheduledExecutorService executorService;
/*     */ 
/*     */   
/*     */   public Tailer(File file, LineHandler lineHandler) {
/*  54 */     this(file, lineHandler, 0);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Tailer(File file, LineHandler lineHandler, int initReadLine) {
/*  65 */     this(file, CharsetUtil.CHARSET_UTF_8, lineHandler, initReadLine, DateUnit.SECOND.getMillis());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Tailer(File file, Charset charset, LineHandler lineHandler) {
/*  76 */     this(file, charset, lineHandler, 0, DateUnit.SECOND.getMillis());
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
/*     */   public Tailer(File file, Charset charset, LineHandler lineHandler, int initReadLine, long period) {
/*  89 */     checkFile(file);
/*  90 */     this.charset = charset;
/*  91 */     this.lineHandler = lineHandler;
/*  92 */     this.period = period;
/*  93 */     this.initReadLine = initReadLine;
/*  94 */     this.randomAccessFile = FileUtil.createRandomAccessFile(file, FileMode.r);
/*  95 */     this.executorService = Executors.newSingleThreadScheduledExecutor();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void start() {
/* 102 */     start(false);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void start(boolean async) {
/*     */     try {
/* 113 */       readTail();
/* 114 */     } catch (IOException e) {
/* 115 */       throw new IORuntimeException(e);
/*     */     } 
/*     */     
/* 118 */     LineReadWatcher lineReadWatcher = new LineReadWatcher(this.randomAccessFile, this.charset, this.lineHandler);
/* 119 */     ScheduledFuture<?> scheduledFuture = this.executorService.scheduleAtFixedRate(lineReadWatcher, 0L, this.period, TimeUnit.MILLISECONDS);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 125 */     if (false == async) {
/*     */       try {
/* 127 */         scheduledFuture.get();
/* 128 */       } catch (ExecutionException e) {
/* 129 */         throw new UtilException(e);
/* 130 */       } catch (InterruptedException interruptedException) {}
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void stop() {
/* 140 */     this.executorService.shutdown();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void readTail() throws IOException {
/* 150 */     long len = this.randomAccessFile.length();
/*     */     
/* 152 */     if (this.initReadLine > 0) {
/* 153 */       Stack<String> stack = new Stack<>();
/*     */       
/* 155 */       long start = this.randomAccessFile.getFilePointer();
/* 156 */       long nextEnd = (len - 1L < 0L) ? 0L : (len - 1L);
/* 157 */       this.randomAccessFile.seek(nextEnd);
/*     */       
/* 159 */       int currentLine = 0;
/* 160 */       while (nextEnd > start) {
/*     */         
/* 162 */         if (currentLine > this.initReadLine) {
/*     */           break;
/*     */         }
/*     */         
/* 166 */         int c = this.randomAccessFile.read();
/* 167 */         if (c == 10 || c == 13) {
/*     */           
/* 169 */           String line = FileUtil.readLine(this.randomAccessFile, this.charset);
/* 170 */           if (null != line) {
/* 171 */             stack.push(line);
/*     */           }
/* 173 */           currentLine++;
/* 174 */           nextEnd--;
/*     */         } 
/* 176 */         nextEnd--;
/* 177 */         this.randomAccessFile.seek(nextEnd);
/* 178 */         if (nextEnd == 0L) {
/*     */ 
/*     */           
/* 181 */           String line = FileUtil.readLine(this.randomAccessFile, this.charset);
/* 182 */           if (null != line) {
/* 183 */             stack.push(line);
/*     */           }
/*     */           
/*     */           break;
/*     */         } 
/*     */       } 
/*     */       
/* 190 */       while (false == stack.isEmpty()) {
/* 191 */         this.lineHandler.handle(stack.pop());
/*     */       }
/*     */     } 
/*     */ 
/*     */     
/*     */     try {
/* 197 */       this.randomAccessFile.seek(len);
/* 198 */     } catch (IOException e) {
/* 199 */       throw new IORuntimeException(e);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static void checkFile(File file) {
/* 209 */     if (false == file.exists()) {
/* 210 */       throw new UtilException("File [{}] not exist !", new Object[] { file.getAbsolutePath() });
/*     */     }
/* 212 */     if (false == file.isFile()) {
/* 213 */       throw new UtilException("Path [{}] is not a file !", new Object[] { file.getAbsolutePath() });
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static class ConsoleLineHandler
/*     */     implements LineHandler
/*     */   {
/*     */     public void handle(String line) {
/* 227 */       Console.log(line);
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\io\file\Tailer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */