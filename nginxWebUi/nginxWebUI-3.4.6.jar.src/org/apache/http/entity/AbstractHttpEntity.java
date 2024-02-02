/*     */ package org.apache.http.entity;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import org.apache.http.Header;
/*     */ import org.apache.http.HttpEntity;
/*     */ import org.apache.http.message.BasicHeader;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class AbstractHttpEntity
/*     */   implements HttpEntity
/*     */ {
/*     */   protected static final int OUTPUT_BUFFER_SIZE = 4096;
/*     */   protected Header contentType;
/*     */   protected Header contentEncoding;
/*     */   protected boolean chunked;
/*     */   
/*     */   public Header getContentType() {
/*  76 */     return this.contentType;
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
/*     */   public Header getContentEncoding() {
/*  89 */     return this.contentEncoding;
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
/*     */   public boolean isChunked() {
/* 101 */     return this.chunked;
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
/*     */   public void setContentType(Header contentType) {
/* 114 */     this.contentType = contentType;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setContentType(String ctString) {
/*     */     BasicHeader basicHeader;
/* 126 */     Header h = null;
/* 127 */     if (ctString != null) {
/* 128 */       basicHeader = new BasicHeader("Content-Type", ctString);
/*     */     }
/* 130 */     setContentType((Header)basicHeader);
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
/*     */   public void setContentEncoding(Header contentEncoding) {
/* 143 */     this.contentEncoding = contentEncoding;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setContentEncoding(String ceString) {
/*     */     BasicHeader basicHeader;
/* 155 */     Header h = null;
/* 156 */     if (ceString != null) {
/* 157 */       basicHeader = new BasicHeader("Content-Encoding", ceString);
/*     */     }
/* 159 */     setContentEncoding((Header)basicHeader);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setChunked(boolean b) {
/* 178 */     this.chunked = b;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public void consumeContent() throws IOException {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/* 195 */     StringBuilder sb = new StringBuilder();
/* 196 */     sb.append('[');
/* 197 */     if (this.contentType != null) {
/* 198 */       sb.append("Content-Type: ");
/* 199 */       sb.append(this.contentType.getValue());
/* 200 */       sb.append(',');
/*     */     } 
/* 202 */     if (this.contentEncoding != null) {
/* 203 */       sb.append("Content-Encoding: ");
/* 204 */       sb.append(this.contentEncoding.getValue());
/* 205 */       sb.append(',');
/*     */     } 
/* 207 */     long len = getContentLength();
/* 208 */     if (len >= 0L) {
/* 209 */       sb.append("Content-Length: ");
/* 210 */       sb.append(len);
/* 211 */       sb.append(',');
/*     */     } 
/* 213 */     sb.append("Chunked: ");
/* 214 */     sb.append(this.chunked);
/* 215 */     sb.append(']');
/* 216 */     return sb.toString();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\entity\AbstractHttpEntity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */