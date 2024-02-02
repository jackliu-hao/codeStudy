/*     */ package org.apache.http.entity;
/*     */ 
/*     */ import java.io.ByteArrayInputStream;
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
/*     */ public class ByteArrayEntity
/*     */   extends AbstractHttpEntity
/*     */   implements Cloneable
/*     */ {
/*     */   @Deprecated
/*     */   protected final byte[] content;
/*     */   private final byte[] b;
/*     */   private final int off;
/*     */   private final int len;
/*     */   
/*     */   public ByteArrayEntity(byte[] b, ContentType contentType) {
/*  57 */     Args.notNull(b, "Source byte array");
/*  58 */     this.content = b;
/*  59 */     this.b = b;
/*  60 */     this.off = 0;
/*  61 */     this.len = this.b.length;
/*  62 */     if (contentType != null) {
/*  63 */       setContentType(contentType.toString());
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ByteArrayEntity(byte[] b, int off, int len, ContentType contentType) {
/*  72 */     Args.notNull(b, "Source byte array");
/*  73 */     if (off < 0 || off > b.length || len < 0 || off + len < 0 || off + len > b.length)
/*     */     {
/*  75 */       throw new IndexOutOfBoundsException("off: " + off + " len: " + len + " b.length: " + b.length);
/*     */     }
/*  77 */     this.content = b;
/*  78 */     this.b = b;
/*  79 */     this.off = off;
/*  80 */     this.len = len;
/*  81 */     if (contentType != null) {
/*  82 */       setContentType(contentType.toString());
/*     */     }
/*     */   }
/*     */   
/*     */   public ByteArrayEntity(byte[] b) {
/*  87 */     this(b, (ContentType)null);
/*     */   }
/*     */   
/*     */   public ByteArrayEntity(byte[] b, int off, int len) {
/*  91 */     this(b, off, len, (ContentType)null);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isRepeatable() {
/*  96 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getContentLength() {
/* 101 */     return this.len;
/*     */   }
/*     */ 
/*     */   
/*     */   public InputStream getContent() {
/* 106 */     return new ByteArrayInputStream(this.b, this.off, this.len);
/*     */   }
/*     */ 
/*     */   
/*     */   public void writeTo(OutputStream outStream) throws IOException {
/* 111 */     Args.notNull(outStream, "Output stream");
/* 112 */     outStream.write(this.b, this.off, this.len);
/* 113 */     outStream.flush();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isStreaming() {
/* 124 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public Object clone() throws CloneNotSupportedException {
/* 129 */     return super.clone();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\entity\ByteArrayEntity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */