/*     */ package cn.hutool.http;
/*     */ 
/*     */ import cn.hutool.core.collection.CollUtil;
/*     */ import cn.hutool.core.collection.CollectionUtil;
/*     */ import cn.hutool.core.map.CaseInsensitiveMap;
/*     */ import cn.hutool.core.map.MapUtil;
/*     */ import cn.hutool.core.util.CharsetUtil;
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import java.nio.charset.Charset;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class HttpBase<T>
/*     */ {
/*  30 */   protected static final Charset DEFAULT_CHARSET = CharsetUtil.CHARSET_UTF_8;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final String HTTP_1_0 = "HTTP/1.0";
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final String HTTP_1_1 = "HTTP/1.1";
/*     */ 
/*     */ 
/*     */   
/*  44 */   protected Map<String, List<String>> headers = new HashMap<>();
/*     */ 
/*     */ 
/*     */   
/*  48 */   protected Charset charset = DEFAULT_CHARSET;
/*     */ 
/*     */ 
/*     */   
/*  52 */   protected String httpVersion = "HTTP/1.1";
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected byte[] bodyBytes;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String header(String name) {
/*  68 */     List<String> values = headerList(name);
/*  69 */     if (CollectionUtil.isEmpty(values)) {
/*  70 */       return null;
/*     */     }
/*  72 */     return values.get(0);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public List<String> headerList(String name) {
/*  83 */     if (StrUtil.isBlank(name)) {
/*  84 */       return null;
/*     */     }
/*     */     
/*  87 */     CaseInsensitiveMap<String, List<String>> headersIgnoreCase = new CaseInsensitiveMap(this.headers);
/*  88 */     return (List<String>)headersIgnoreCase.get(name.trim());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String header(Header name) {
/*  98 */     if (null == name) {
/*  99 */       return null;
/*     */     }
/* 101 */     return header(name.toString());
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
/*     */   public T header(String name, String value, boolean isOverride) {
/* 114 */     if (null != name && null != value) {
/* 115 */       List<String> values = this.headers.get(name.trim());
/* 116 */       if (isOverride || CollectionUtil.isEmpty(values)) {
/* 117 */         ArrayList<String> valueList = new ArrayList<>();
/* 118 */         valueList.add(value);
/* 119 */         this.headers.put(name.trim(), valueList);
/*     */       } else {
/* 121 */         values.add(value.trim());
/*     */       } 
/*     */     } 
/* 124 */     return (T)this;
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
/*     */   public T header(Header name, String value, boolean isOverride) {
/* 137 */     return header(name.toString(), value, isOverride);
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
/*     */   public T header(Header name, String value) {
/* 149 */     return header(name.toString(), value, true);
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
/*     */   public T header(String name, String value) {
/* 161 */     return header(name, value, true);
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
/*     */   public T headerMap(Map<String, String> headers, boolean isOverride) {
/* 173 */     if (MapUtil.isEmpty(headers)) {
/* 174 */       return (T)this;
/*     */     }
/*     */     
/* 177 */     for (Map.Entry<String, String> entry : headers.entrySet()) {
/* 178 */       header(entry.getKey(), StrUtil.nullToEmpty(entry.getValue()), isOverride);
/*     */     }
/* 180 */     return (T)this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public T header(Map<String, List<String>> headers) {
/* 191 */     return header(headers, false);
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
/*     */   public T header(Map<String, List<String>> headers, boolean isOverride) {
/* 203 */     if (MapUtil.isEmpty(headers)) {
/* 204 */       return (T)this;
/*     */     }
/*     */ 
/*     */     
/* 208 */     for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
/* 209 */       String name = entry.getKey();
/* 210 */       for (String value : entry.getValue()) {
/* 211 */         header(name, StrUtil.nullToEmpty(value), isOverride);
/*     */       }
/*     */     } 
/* 214 */     return (T)this;
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
/*     */   public T addHeaders(Map<String, String> headers) {
/* 226 */     if (MapUtil.isEmpty(headers)) {
/* 227 */       return (T)this;
/*     */     }
/*     */     
/* 230 */     for (Map.Entry<String, String> entry : headers.entrySet()) {
/* 231 */       header(entry.getKey(), StrUtil.nullToEmpty(entry.getValue()), false);
/*     */     }
/* 233 */     return (T)this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public T removeHeader(String name) {
/* 243 */     if (name != null) {
/* 244 */       this.headers.remove(name.trim());
/*     */     }
/* 246 */     return (T)this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public T removeHeader(Header name) {
/* 256 */     return removeHeader(name.toString());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Map<String, List<String>> headers() {
/* 265 */     return Collections.unmodifiableMap(this.headers);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public T clearHeaders() {
/* 275 */     this.headers.clear();
/* 276 */     return (T)this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String httpVersion() {
/* 286 */     return this.httpVersion;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public T httpVersion(String httpVersion) {
/* 296 */     this.httpVersion = httpVersion;
/* 297 */     return (T)this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public byte[] bodyBytes() {
/* 306 */     return this.bodyBytes;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String charset() {
/* 315 */     return this.charset.name();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public T charset(String charset) {
/* 326 */     if (StrUtil.isNotBlank(charset)) {
/* 327 */       charset(Charset.forName(charset));
/*     */     }
/* 329 */     return (T)this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public T charset(Charset charset) {
/* 340 */     if (null != charset) {
/* 341 */       this.charset = charset;
/*     */     }
/* 343 */     return (T)this;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 348 */     StringBuilder sb = StrUtil.builder();
/* 349 */     sb.append("Request Headers: ").append("\r\n");
/* 350 */     for (Map.Entry<String, List<String>> entry : this.headers.entrySet()) {
/* 351 */       sb.append("    ").append(entry
/* 352 */           .getKey()).append(": ").append(CollUtil.join(entry.getValue(), ","))
/* 353 */         .append("\r\n");
/*     */     }
/*     */     
/* 356 */     sb.append("Request Body: ").append("\r\n");
/* 357 */     sb.append("    ").append(StrUtil.str(this.bodyBytes, this.charset)).append("\r\n");
/*     */     
/* 359 */     return sb.toString();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\http\HttpBase.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */