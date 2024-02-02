/*     */ package io.undertow.server.handlers.cache;
/*     */ 
/*     */ import io.undertow.server.HttpServerExchange;
/*     */ import io.undertow.server.handlers.encoding.AllowedContentEncodings;
/*     */ import io.undertow.util.DateUtils;
/*     */ import io.undertow.util.ETag;
/*     */ import io.undertow.util.ETagUtils;
/*     */ import io.undertow.util.Headers;
/*     */ import java.util.Date;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class CachedHttpRequest
/*     */ {
/*     */   private final String path;
/*     */   private final ETag etag;
/*     */   private final String contentEncoding;
/*     */   private final String contentLocation;
/*     */   private final String language;
/*     */   private final String contentType;
/*     */   private final Date lastModified;
/*     */   private final int responseCode;
/*     */   
/*     */   public CachedHttpRequest(HttpServerExchange exchange) {
/*  45 */     this.path = exchange.getRequestPath();
/*  46 */     this.etag = ETagUtils.getETag(exchange);
/*  47 */     this.contentLocation = exchange.getResponseHeaders().getFirst(Headers.CONTENT_LOCATION);
/*  48 */     this.language = exchange.getResponseHeaders().getFirst(Headers.CONTENT_LANGUAGE);
/*  49 */     this.contentType = exchange.getResponseHeaders().getFirst(Headers.CONTENT_TYPE);
/*  50 */     String lmString = exchange.getResponseHeaders().getFirst(Headers.LAST_MODIFIED);
/*  51 */     if (lmString == null) {
/*  52 */       this.lastModified = null;
/*     */     } else {
/*  54 */       this.lastModified = DateUtils.parseDate(lmString);
/*     */     } 
/*     */ 
/*     */     
/*  58 */     AllowedContentEncodings encoding = (AllowedContentEncodings)exchange.getAttachment(AllowedContentEncodings.ATTACHMENT_KEY);
/*  59 */     if (encoding != null) {
/*  60 */       this.contentEncoding = encoding.getCurrentContentEncoding();
/*     */     } else {
/*  62 */       this.contentEncoding = exchange.getResponseHeaders().getFirst(Headers.CONTENT_ENCODING);
/*     */     } 
/*  64 */     this.responseCode = exchange.getStatusCode();
/*     */   }
/*     */   
/*     */   public String getPath() {
/*  68 */     return this.path;
/*     */   }
/*     */   
/*     */   public ETag getEtag() {
/*  72 */     return this.etag;
/*     */   }
/*     */   
/*     */   public String getContentEncoding() {
/*  76 */     return this.contentEncoding;
/*     */   }
/*     */   
/*     */   public String getLanguage() {
/*  80 */     return this.language;
/*     */   }
/*     */   
/*     */   public String getContentType() {
/*  84 */     return this.contentType;
/*     */   }
/*     */   
/*     */   public Date getLastModified() {
/*  88 */     return this.lastModified;
/*     */   }
/*     */   
/*     */   public String getContentLocation() {
/*  92 */     return this.contentLocation;
/*     */   }
/*     */   
/*     */   public int getResponseCode() {
/*  96 */     return this.responseCode;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object o) {
/* 101 */     if (this == o) return true; 
/* 102 */     if (o == null || getClass() != o.getClass()) return false;
/*     */     
/* 104 */     CachedHttpRequest that = (CachedHttpRequest)o;
/*     */     
/* 106 */     if (this.responseCode != that.responseCode) return false; 
/* 107 */     if ((this.contentEncoding != null) ? !this.contentEncoding.equals(that.contentEncoding) : (that.contentEncoding != null))
/* 108 */       return false; 
/* 109 */     if ((this.contentLocation != null) ? !this.contentLocation.equals(that.contentLocation) : (that.contentLocation != null))
/* 110 */       return false; 
/* 111 */     if ((this.contentType != null) ? !this.contentType.equals(that.contentType) : (that.contentType != null)) return false; 
/* 112 */     if ((this.etag != null) ? !this.etag.equals(that.etag) : (that.etag != null)) return false; 
/* 113 */     if ((this.language != null) ? !this.language.equals(that.language) : (that.language != null)) return false; 
/* 114 */     if ((this.lastModified != null) ? !this.lastModified.equals(that.lastModified) : (that.lastModified != null)) return false; 
/* 115 */     if ((this.path != null) ? !this.path.equals(that.path) : (that.path != null)) return false;
/*     */     
/* 117 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 122 */     int result = (this.path != null) ? this.path.hashCode() : 0;
/* 123 */     result = 31 * result + ((this.etag != null) ? this.etag.hashCode() : 0);
/* 124 */     result = 31 * result + ((this.contentEncoding != null) ? this.contentEncoding.hashCode() : 0);
/* 125 */     result = 31 * result + ((this.contentLocation != null) ? this.contentLocation.hashCode() : 0);
/* 126 */     result = 31 * result + ((this.language != null) ? this.language.hashCode() : 0);
/* 127 */     result = 31 * result + ((this.contentType != null) ? this.contentType.hashCode() : 0);
/* 128 */     result = 31 * result + ((this.lastModified != null) ? this.lastModified.hashCode() : 0);
/* 129 */     result = 31 * result + this.responseCode;
/* 130 */     return result;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\handlers\cache\CachedHttpRequest.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */