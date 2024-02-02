/*     */ package cn.hutool.core.io.file;
/*     */ 
/*     */ import cn.hutool.core.thread.lock.LockUtil;
/*     */ import cn.hutool.core.util.CharsetUtil;
/*     */ import cn.hutool.core.util.ObjectUtil;
/*     */ import java.io.File;
/*     */ import java.io.PrintWriter;
/*     */ import java.io.Serializable;
/*     */ import java.nio.charset.Charset;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.concurrent.locks.Lock;
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
/*     */ public class FileAppender
/*     */   implements Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   private final FileWriter writer;
/*     */   private final int capacity;
/*     */   private final boolean isNewLineMode;
/*     */   private final List<String> list;
/*     */   private final Lock lock;
/*     */   
/*     */   public FileAppender(File destFile, int capacity, boolean isNewLineMode) {
/*  53 */     this(destFile, CharsetUtil.CHARSET_UTF_8, capacity, isNewLineMode);
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
/*     */   public FileAppender(File destFile, Charset charset, int capacity, boolean isNewLineMode) {
/*  65 */     this(destFile, charset, capacity, isNewLineMode, null);
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
/*     */   public FileAppender(File destFile, Charset charset, int capacity, boolean isNewLineMode, Lock lock) {
/*  78 */     this.capacity = capacity;
/*  79 */     this.list = new ArrayList<>(capacity);
/*  80 */     this.isNewLineMode = isNewLineMode;
/*  81 */     this.writer = FileWriter.create(destFile, charset);
/*  82 */     this.lock = (Lock)ObjectUtil.defaultIfNull(lock, LockUtil::getNoLock);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public FileAppender append(String line) {
/*  92 */     if (this.list.size() >= this.capacity) {
/*  93 */       flush();
/*     */     }
/*     */     
/*  96 */     this.lock.lock();
/*     */     try {
/*  98 */       this.list.add(line);
/*     */     } finally {
/* 100 */       this.lock.unlock();
/*     */     } 
/* 102 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public FileAppender flush() {
/* 111 */     this.lock.lock();
/*     */     try {
/* 113 */       try (PrintWriter pw = this.writer.getPrintWriter(true)) {
/* 114 */         for (String str : this.list) {
/* 115 */           pw.print(str);
/* 116 */           if (this.isNewLineMode) {
/* 117 */             pw.println();
/*     */           }
/*     */         } 
/*     */       } 
/* 121 */       this.list.clear();
/*     */     } finally {
/* 123 */       this.lock.unlock();
/*     */     } 
/* 125 */     return this;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\io\file\FileAppender.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */