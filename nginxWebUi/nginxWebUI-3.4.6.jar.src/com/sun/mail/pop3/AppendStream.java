/*     */ package com.sun.mail.pop3;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.io.RandomAccessFile;
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
/*     */ class AppendStream
/*     */   extends OutputStream
/*     */ {
/*     */   private final WritableSharedFile tf;
/*     */   private RandomAccessFile raf;
/*     */   private final long start;
/*     */   private long end;
/*     */   
/*     */   public AppendStream(WritableSharedFile tf) throws IOException {
/* 167 */     this.tf = tf;
/* 168 */     this.raf = tf.getWritableFile();
/* 169 */     this.start = this.raf.length();
/* 170 */     this.raf.seek(this.start);
/*     */   }
/*     */   
/*     */   public void write(int b) throws IOException {
/* 174 */     this.raf.write(b);
/*     */   }
/*     */   
/*     */   public void write(byte[] b) throws IOException {
/* 178 */     this.raf.write(b);
/*     */   }
/*     */   
/*     */   public void write(byte[] b, int off, int len) throws IOException {
/* 182 */     this.raf.write(b, off, len);
/*     */   }
/*     */   
/*     */   public synchronized void close() throws IOException {
/* 186 */     this.end = this.tf.updateLength();
/* 187 */     this.raf = null;
/*     */   }
/*     */   
/*     */   public synchronized InputStream getInputStream() throws IOException {
/* 191 */     return this.tf.newStream(this.start, this.end);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\mail\pop3\AppendStream.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */