/*     */ package org.apache.http.client.entity;
/*     */ 
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.nio.charset.Charset;
/*     */ import java.util.List;
/*     */ import org.apache.http.NameValuePair;
/*     */ import org.apache.http.client.utils.URLEncodedUtils;
/*     */ import org.apache.http.entity.ContentType;
/*     */ import org.apache.http.entity.StringEntity;
/*     */ import org.apache.http.protocol.HTTP;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class UrlEncodedFormEntity
/*     */   extends StringEntity
/*     */ {
/*     */   public UrlEncodedFormEntity(List<? extends NameValuePair> parameters, String charset) throws UnsupportedEncodingException {
/*  58 */     super(URLEncodedUtils.format(parameters, (charset != null) ? charset : HTTP.DEF_CONTENT_CHARSET.name()), ContentType.create("application/x-www-form-urlencoded", charset));
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
/*     */   public UrlEncodedFormEntity(Iterable<? extends NameValuePair> parameters, Charset charset) {
/*  75 */     super(URLEncodedUtils.format(parameters, (charset != null) ? charset : HTTP.DEF_CONTENT_CHARSET), ContentType.create("application/x-www-form-urlencoded", charset));
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
/*     */   public UrlEncodedFormEntity(List<? extends NameValuePair> parameters) throws UnsupportedEncodingException {
/*  89 */     this(parameters, (Charset)null);
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
/*     */   public UrlEncodedFormEntity(Iterable<? extends NameValuePair> parameters) {
/* 102 */     this(parameters, (Charset)null);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\client\entity\UrlEncodedFormEntity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */