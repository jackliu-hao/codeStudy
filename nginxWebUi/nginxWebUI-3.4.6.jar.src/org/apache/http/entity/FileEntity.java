/*     */ package org.apache.http.entity;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import org.apache.http.util.Args;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class FileEntity
/*     */   extends AbstractHttpEntity
/*     */   implements Cloneable
/*     */ {
/*     */   protected final File file;
/*     */   
/*     */   @Deprecated
/*     */   public FileEntity(File file, String contentType) {
/*  58 */     this.file = (File)Args.notNull(file, "File");
/*  59 */     setContentType(contentType);
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
/*     */   public FileEntity(File file, ContentType contentType) {
/*  72 */     this.file = (File)Args.notNull(file, "File");
/*  73 */     if (contentType != null) {
/*  74 */       setContentType(contentType.toString());
/*     */     }
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
/*     */   public FileEntity(File file) {
/*  87 */     this.file = (File)Args.notNull(file, "File");
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isRepeatable() {
/*  92 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getContentLength() {
/*  97 */     return this.file.length();
/*     */   }
/*     */ 
/*     */   
/*     */   public InputStream getContent() throws IOException {
/* 102 */     return new FileInputStream(this.file);
/*     */   }
/*     */ 
/*     */   
/*     */   public void writeTo(OutputStream outStream) throws IOException {
/* 107 */     Args.notNull(outStream, "Output stream");
/* 108 */     InputStream inStream = new FileInputStream(this.file);
/*     */     try {
/* 110 */       byte[] tmp = new byte[4096];
/*     */       int l;
/* 112 */       while ((l = inStream.read(tmp)) != -1) {
/* 113 */         outStream.write(tmp, 0, l);
/*     */       }
/* 115 */       outStream.flush();
/*     */     } finally {
/* 117 */       inStream.close();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isStreaming() {
/* 128 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object clone() throws CloneNotSupportedException {
/* 135 */     return super.clone();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\entity\FileEntity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */