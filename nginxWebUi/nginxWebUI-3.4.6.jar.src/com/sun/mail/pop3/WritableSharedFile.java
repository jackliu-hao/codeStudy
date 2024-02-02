/*     */ package com.sun.mail.pop3;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.RandomAccessFile;
/*     */ import javax.mail.util.SharedFileInputStream;
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
/*     */ 
/*     */ 
/*     */ class WritableSharedFile
/*     */   extends SharedFileInputStream
/*     */ {
/*     */   private RandomAccessFile raf;
/*     */   private AppendStream af;
/*     */   
/*     */   public WritableSharedFile(File file) throws IOException {
/* 103 */     super(file);
/*     */     try {
/* 105 */       this.raf = new RandomAccessFile(file, "rw");
/* 106 */     } catch (IOException ex) {
/*     */ 
/*     */       
/* 109 */       super.close();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public RandomAccessFile getWritableFile() {
/* 117 */     return this.raf;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void close() throws IOException {
/*     */     try {
/* 125 */       super.close();
/*     */     } finally {
/* 127 */       this.raf.close();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   synchronized long updateLength() throws IOException {
/* 137 */     this.datalen = this.in.length();
/* 138 */     this.af = null;
/* 139 */     return this.datalen;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized AppendStream getAppendStream() throws IOException {
/* 146 */     if (this.af != null) {
/* 147 */       throw new IOException("POP3 file cache only supports single threaded access");
/*     */     }
/* 149 */     this.af = new AppendStream(this);
/* 150 */     return this.af;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\mail\pop3\WritableSharedFile.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */