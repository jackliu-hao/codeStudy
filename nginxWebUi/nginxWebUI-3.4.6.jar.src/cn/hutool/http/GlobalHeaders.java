/*     */ package cn.hutool.http;
/*     */ 
/*     */ import cn.hutool.core.collection.CollectionUtil;
/*     */ import cn.hutool.core.map.MapUtil;
/*     */ import cn.hutool.core.util.StrUtil;
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
/*     */ public enum GlobalHeaders
/*     */ {
/*  21 */   INSTANCE;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  26 */   final Map<String, List<String>> headers = new HashMap<>();
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   GlobalHeaders() {
/*  32 */     putDefault(false);
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
/*     */   public GlobalHeaders putDefault(boolean isReset) {
/*  44 */     System.setProperty("sun.net.http.allowRestrictedHeaders", "true");
/*     */ 
/*     */     
/*  47 */     System.setProperty("jdk.tls.allowUnsafeServerCertChange", "true");
/*  48 */     System.setProperty("sun.security.ssl.allowUnsafeRenegotiation", "true");
/*     */     
/*  50 */     if (isReset) {
/*  51 */       this.headers.clear();
/*     */     }
/*     */     
/*  54 */     header(Header.ACCEPT, "text/html,application/json,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8", true);
/*  55 */     header(Header.ACCEPT_ENCODING, "gzip, deflate", true);
/*  56 */     header(Header.ACCEPT_LANGUAGE, "zh-CN,zh;q=0.8", true);
/*     */ 
/*     */     
/*  59 */     header(Header.USER_AGENT, "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/75.0.3770.142 Safari/537.36 Hutool", true);
/*  60 */     return this;
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
/*     */   public String header(String name) {
/*  72 */     List<String> values = headerList(name);
/*  73 */     if (CollectionUtil.isEmpty(values)) {
/*  74 */       return null;
/*     */     }
/*  76 */     return values.get(0);
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
/*  87 */     if (StrUtil.isBlank(name)) {
/*  88 */       return null;
/*     */     }
/*     */     
/*  91 */     return this.headers.get(name.trim());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String header(Header name) {
/* 101 */     if (null == name) {
/* 102 */       return null;
/*     */     }
/* 104 */     return header(name.toString());
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
/*     */   public synchronized GlobalHeaders header(String name, String value, boolean isOverride) {
/* 117 */     if (null != name && null != value) {
/* 118 */       List<String> values = this.headers.get(name.trim());
/* 119 */       if (isOverride || CollectionUtil.isEmpty(values)) {
/* 120 */         ArrayList<String> valueList = new ArrayList<>();
/* 121 */         valueList.add(value);
/* 122 */         this.headers.put(name.trim(), valueList);
/*     */       } else {
/* 124 */         values.add(value.trim());
/*     */       } 
/*     */     } 
/* 127 */     return this;
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
/*     */   public GlobalHeaders header(Header name, String value, boolean isOverride) {
/* 140 */     return header(name.toString(), value, isOverride);
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
/*     */   public GlobalHeaders header(Header name, String value) {
/* 152 */     return header(name.toString(), value, true);
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
/*     */   public GlobalHeaders header(String name, String value) {
/* 164 */     return header(name, value, true);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public GlobalHeaders header(Map<String, List<String>> headers) {
/* 175 */     if (MapUtil.isEmpty(headers)) {
/* 176 */       return this;
/*     */     }
/*     */ 
/*     */     
/* 180 */     for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
/* 181 */       String name = entry.getKey();
/* 182 */       for (String value : entry.getValue()) {
/* 183 */         header(name, StrUtil.nullToEmpty(value), false);
/*     */       }
/*     */     } 
/* 186 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized GlobalHeaders removeHeader(String name) {
/* 196 */     if (name != null) {
/* 197 */       this.headers.remove(name.trim());
/*     */     }
/* 199 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public GlobalHeaders removeHeader(Header name) {
/* 209 */     return removeHeader(name.toString());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Map<String, List<String>> headers() {
/* 218 */     return Collections.unmodifiableMap(this.headers);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized GlobalHeaders clearHeaders() {
/* 228 */     this.headers.clear();
/* 229 */     return this;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\http\GlobalHeaders.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */