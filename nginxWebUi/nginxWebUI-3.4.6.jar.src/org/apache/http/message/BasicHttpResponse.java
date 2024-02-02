/*     */ package org.apache.http.message;
/*     */ 
/*     */ import java.util.Locale;
/*     */ import org.apache.http.HttpEntity;
/*     */ import org.apache.http.HttpResponse;
/*     */ import org.apache.http.HttpVersion;
/*     */ import org.apache.http.ProtocolVersion;
/*     */ import org.apache.http.ReasonPhraseCatalog;
/*     */ import org.apache.http.StatusLine;
/*     */ import org.apache.http.util.Args;
/*     */ import org.apache.http.util.TextUtils;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class BasicHttpResponse
/*     */   extends AbstractHttpMessage
/*     */   implements HttpResponse
/*     */ {
/*     */   private StatusLine statusline;
/*     */   private ProtocolVersion ver;
/*     */   private int code;
/*     */   private String reasonPhrase;
/*     */   private HttpEntity entity;
/*     */   private final ReasonPhraseCatalog reasonCatalog;
/*     */   private Locale locale;
/*     */   
/*     */   public BasicHttpResponse(StatusLine statusline, ReasonPhraseCatalog catalog, Locale locale) {
/*  73 */     this.statusline = (StatusLine)Args.notNull(statusline, "Status line");
/*  74 */     this.ver = statusline.getProtocolVersion();
/*  75 */     this.code = statusline.getStatusCode();
/*  76 */     this.reasonPhrase = statusline.getReasonPhrase();
/*  77 */     this.reasonCatalog = catalog;
/*  78 */     this.locale = locale;
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
/*     */   public BasicHttpResponse(StatusLine statusline) {
/*  90 */     this.statusline = (StatusLine)Args.notNull(statusline, "Status line");
/*  91 */     this.ver = statusline.getProtocolVersion();
/*  92 */     this.code = statusline.getStatusCode();
/*  93 */     this.reasonPhrase = statusline.getReasonPhrase();
/*  94 */     this.reasonCatalog = null;
/*  95 */     this.locale = null;
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
/*     */   public BasicHttpResponse(ProtocolVersion ver, int code, String reason) {
/* 112 */     Args.notNegative(code, "Status code");
/* 113 */     this.statusline = null;
/* 114 */     this.ver = ver;
/* 115 */     this.code = code;
/* 116 */     this.reasonPhrase = reason;
/* 117 */     this.reasonCatalog = null;
/* 118 */     this.locale = null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ProtocolVersion getProtocolVersion() {
/* 125 */     return this.ver;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public StatusLine getStatusLine() {
/* 131 */     if (this.statusline == null) {
/* 132 */       this.statusline = new BasicStatusLine((this.ver != null) ? this.ver : (ProtocolVersion)HttpVersion.HTTP_1_1, this.code, (this.reasonPhrase != null) ? this.reasonPhrase : getReason(this.code));
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 137 */     return this.statusline;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public HttpEntity getEntity() {
/* 143 */     return this.entity;
/*     */   }
/*     */ 
/*     */   
/*     */   public Locale getLocale() {
/* 148 */     return this.locale;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void setStatusLine(StatusLine statusline) {
/* 154 */     this.statusline = (StatusLine)Args.notNull(statusline, "Status line");
/* 155 */     this.ver = statusline.getProtocolVersion();
/* 156 */     this.code = statusline.getStatusCode();
/* 157 */     this.reasonPhrase = statusline.getReasonPhrase();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void setStatusLine(ProtocolVersion ver, int code) {
/* 163 */     Args.notNegative(code, "Status code");
/* 164 */     this.statusline = null;
/* 165 */     this.ver = ver;
/* 166 */     this.code = code;
/* 167 */     this.reasonPhrase = null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setStatusLine(ProtocolVersion ver, int code, String reason) {
/* 174 */     Args.notNegative(code, "Status code");
/* 175 */     this.statusline = null;
/* 176 */     this.ver = ver;
/* 177 */     this.code = code;
/* 178 */     this.reasonPhrase = reason;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void setStatusCode(int code) {
/* 184 */     Args.notNegative(code, "Status code");
/* 185 */     this.statusline = null;
/* 186 */     this.code = code;
/* 187 */     this.reasonPhrase = null;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void setReasonPhrase(String reason) {
/* 193 */     this.statusline = null;
/* 194 */     this.reasonPhrase = TextUtils.isBlank(reason) ? null : reason;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void setEntity(HttpEntity entity) {
/* 200 */     this.entity = entity;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setLocale(Locale locale) {
/* 205 */     this.locale = (Locale)Args.notNull(locale, "Locale");
/* 206 */     this.statusline = null;
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
/*     */   protected String getReason(int code) {
/* 219 */     return (this.reasonCatalog != null) ? this.reasonCatalog.getReason(code, (this.locale != null) ? this.locale : Locale.getDefault()) : null;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/* 225 */     StringBuilder sb = new StringBuilder();
/* 226 */     sb.append(getStatusLine());
/* 227 */     sb.append(' ');
/* 228 */     sb.append(this.headergroup);
/* 229 */     if (this.entity != null) {
/* 230 */       sb.append(' ');
/* 231 */       sb.append(this.entity);
/*     */     } 
/* 233 */     return sb.toString();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\message\BasicHttpResponse.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */