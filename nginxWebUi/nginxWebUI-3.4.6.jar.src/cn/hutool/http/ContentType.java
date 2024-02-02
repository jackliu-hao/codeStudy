/*     */ package cn.hutool.http;
/*     */ 
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import java.nio.charset.Charset;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public enum ContentType
/*     */ {
/*  18 */   FORM_URLENCODED("application/x-www-form-urlencoded"),
/*     */ 
/*     */ 
/*     */   
/*  22 */   MULTIPART("multipart/form-data"),
/*     */ 
/*     */ 
/*     */   
/*  26 */   JSON("application/json"),
/*     */ 
/*     */ 
/*     */   
/*  30 */   XML("application/xml"),
/*     */ 
/*     */ 
/*     */   
/*  34 */   TEXT_PLAIN("text/plain"),
/*     */ 
/*     */ 
/*     */   
/*  38 */   TEXT_XML("text/xml"),
/*     */ 
/*     */ 
/*     */   
/*  42 */   TEXT_HTML("text/html"),
/*     */ 
/*     */ 
/*     */   
/*  46 */   OCTET_STREAM("application/octet-stream");
/*     */ 
/*     */ 
/*     */   
/*     */   private final String value;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   ContentType(String value) {
/*  56 */     this.value = value;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getValue() {
/*  66 */     return this.value;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/*  71 */     return getValue();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString(Charset charset) {
/*  81 */     return build(this.value, charset);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean isDefault(String contentType) {
/*  92 */     return (null == contentType || isFormUrlEncode(contentType));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean isFormUrlEncode(String contentType) {
/* 102 */     return StrUtil.startWithIgnoreCase(contentType, FORM_URLENCODED.toString());
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
/*     */   public static ContentType get(String body) {
/* 117 */     ContentType contentType = null;
/* 118 */     if (StrUtil.isNotBlank(body)) {
/* 119 */       char firstChar = body.charAt(0);
/* 120 */       switch (firstChar) {
/*     */         
/*     */         case '[':
/*     */         case '{':
/* 124 */           contentType = JSON;
/*     */           break;
/*     */         
/*     */         case '<':
/* 128 */           contentType = XML;
/*     */           break;
/*     */       } 
/*     */ 
/*     */ 
/*     */     
/*     */     } 
/* 135 */     return contentType;
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
/*     */   public static String build(String contentType, Charset charset) {
/* 147 */     return StrUtil.format("{};charset={}", new Object[] { contentType, charset.name() });
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
/*     */   public static String build(ContentType contentType, Charset charset) {
/* 159 */     return build(contentType.getValue(), charset);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\http\ContentType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */