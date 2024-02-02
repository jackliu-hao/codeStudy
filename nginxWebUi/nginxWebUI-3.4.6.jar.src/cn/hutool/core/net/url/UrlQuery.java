/*     */ package cn.hutool.core.net.url;
/*     */ 
/*     */ import cn.hutool.core.codec.PercentCodec;
/*     */ import cn.hutool.core.collection.CollUtil;
/*     */ import cn.hutool.core.collection.IterUtil;
/*     */ import cn.hutool.core.convert.Convert;
/*     */ import cn.hutool.core.map.MapUtil;
/*     */ import cn.hutool.core.map.TableMap;
/*     */ import cn.hutool.core.net.FormUrlencoded;
/*     */ import cn.hutool.core.net.RFC3986;
/*     */ import cn.hutool.core.net.URLDecoder;
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import java.nio.charset.Charset;
/*     */ import java.util.Iterator;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class UrlQuery
/*     */ {
/*     */   private final TableMap<CharSequence, CharSequence> query;
/*     */   private final boolean isFormUrlEncoded;
/*     */   
/*     */   public static UrlQuery of(Map<? extends CharSequence, ?> queryMap) {
/*  44 */     return new UrlQuery(queryMap);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static UrlQuery of(Map<? extends CharSequence, ?> queryMap, boolean isFormUrlEncoded) {
/*  55 */     return new UrlQuery(queryMap, isFormUrlEncoded);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static UrlQuery of(String queryStr, Charset charset) {
/*  66 */     return of(queryStr, charset, true);
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
/*     */   public static UrlQuery of(String queryStr, Charset charset, boolean autoRemovePath) {
/*  79 */     return of(queryStr, charset, autoRemovePath, false);
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
/*     */   public static UrlQuery of(String queryStr, Charset charset, boolean autoRemovePath, boolean isFormUrlEncoded) {
/*  93 */     return (new UrlQuery(isFormUrlEncoded)).parse(queryStr, charset, autoRemovePath);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public UrlQuery() {
/* 100 */     this((Map<? extends CharSequence, ?>)null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public UrlQuery(boolean isFormUrlEncoded) {
/* 110 */     this(null, isFormUrlEncoded);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public UrlQuery(Map<? extends CharSequence, ?> queryMap) {
/* 119 */     this(queryMap, false);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public UrlQuery(Map<? extends CharSequence, ?> queryMap, boolean isFormUrlEncoded) {
/* 130 */     if (MapUtil.isNotEmpty(queryMap)) {
/* 131 */       this.query = new TableMap(queryMap.size());
/* 132 */       addAll(queryMap);
/*     */     } else {
/* 134 */       this.query = new TableMap(16);
/*     */     } 
/* 136 */     this.isFormUrlEncoded = isFormUrlEncoded;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public UrlQuery add(CharSequence key, Object value) {
/* 147 */     this.query.put(key, toStr(value));
/* 148 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public UrlQuery addAll(Map<? extends CharSequence, ?> queryMap) {
/* 158 */     if (MapUtil.isNotEmpty(queryMap)) {
/* 159 */       queryMap.forEach(this::add);
/*     */     }
/* 161 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public UrlQuery parse(String queryStr, Charset charset) {
/* 172 */     return parse(queryStr, charset, true);
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
/*     */   public UrlQuery parse(String queryStr, Charset charset, boolean autoRemovePath) {
/* 185 */     if (StrUtil.isBlank(queryStr)) {
/* 186 */       return this;
/*     */     }
/*     */     
/* 189 */     if (autoRemovePath) {
/*     */       
/* 191 */       int pathEndPos = queryStr.indexOf('?');
/* 192 */       if (pathEndPos > -1) {
/* 193 */         queryStr = StrUtil.subSuf(queryStr, pathEndPos + 1);
/* 194 */         if (StrUtil.isBlank(queryStr)) {
/* 195 */           return this;
/*     */         }
/*     */       } 
/*     */     } 
/*     */     
/* 200 */     return doParse(queryStr, charset);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Map<CharSequence, CharSequence> getQueryMap() {
/* 209 */     return MapUtil.unmodifiable((Map)this.query);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CharSequence get(CharSequence key) {
/* 219 */     if (MapUtil.isEmpty((Map)this.query)) {
/* 220 */       return null;
/*     */     }
/* 222 */     return (CharSequence)this.query.get(key);
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
/*     */   public String build(Charset charset) {
/* 237 */     return build(charset, true);
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
/*     */   public String build(Charset charset, boolean encodePercent) {
/* 253 */     if (this.isFormUrlEncoded) {
/* 254 */       return build(FormUrlencoded.ALL, FormUrlencoded.ALL, charset, encodePercent);
/*     */     }
/*     */     
/* 257 */     return build(RFC3986.QUERY_PARAM_NAME, RFC3986.QUERY_PARAM_VALUE, charset, encodePercent);
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
/*     */   public String build(PercentCodec keyCoder, PercentCodec valueCoder, Charset charset) {
/* 275 */     return build(keyCoder, valueCoder, charset, true);
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
/*     */   public String build(PercentCodec keyCoder, PercentCodec valueCoder, Charset charset, boolean encodePercent) {
/* 294 */     if (MapUtil.isEmpty((Map)this.query)) {
/* 295 */       return "";
/*     */     }
/*     */     
/* 298 */     (new char[1])[0] = '%'; char[] safeChars = encodePercent ? null : new char[1];
/* 299 */     StringBuilder sb = new StringBuilder();
/*     */ 
/*     */     
/* 302 */     for (Map.Entry<CharSequence, CharSequence> entry : this.query) {
/* 303 */       CharSequence name = entry.getKey();
/* 304 */       if (null != name) {
/* 305 */         if (sb.length() > 0) {
/* 306 */           sb.append("&");
/*     */         }
/* 308 */         sb.append(keyCoder.encode(name, charset, safeChars));
/* 309 */         CharSequence value = entry.getValue();
/* 310 */         if (null != value) {
/* 311 */           sb.append("=").append(valueCoder.encode(value, charset, safeChars));
/*     */         }
/*     */       } 
/*     */     } 
/* 315 */     return sb.toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/* 326 */     return build(null);
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
/*     */   private UrlQuery doParse(String queryStr, Charset charset) {
/* 339 */     int len = queryStr.length();
/* 340 */     String name = null;
/* 341 */     int pos = 0;
/*     */     
/*     */     int i;
/* 344 */     for (i = 0; i < len; i++) {
/* 345 */       char c = queryStr.charAt(i);
/* 346 */       switch (c) {
/*     */         case '=':
/* 348 */           if (null == name) {
/*     */             
/* 350 */             name = queryStr.substring(pos, i);
/*     */             
/* 352 */             pos = i + 1;
/*     */           } 
/*     */           break;
/*     */         
/*     */         case '&':
/* 357 */           addParam(name, queryStr.substring(pos, i), charset);
/* 358 */           name = null;
/* 359 */           if (i + 4 < len && "amp;".equals(queryStr.substring(i + 1, i + 5)))
/*     */           {
/* 361 */             i += 4;
/*     */           }
/*     */           
/* 364 */           pos = i + 1;
/*     */           break;
/*     */       } 
/*     */ 
/*     */     
/*     */     } 
/* 370 */     addParam(name, queryStr.substring(pos, i), charset);
/*     */     
/* 372 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static String toStr(Object value) {
/*     */     String result;
/* 383 */     if (value instanceof Iterable) {
/* 384 */       result = CollUtil.join((Iterable)value, ",");
/* 385 */     } else if (value instanceof Iterator) {
/* 386 */       result = IterUtil.join((Iterator)value, ",");
/*     */     } else {
/* 388 */       result = Convert.toStr(value);
/*     */     } 
/* 390 */     return result;
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
/*     */   private void addParam(String key, String value, Charset charset) {
/* 407 */     if (null != key) {
/* 408 */       String actualKey = URLDecoder.decode(key, charset, this.isFormUrlEncoded);
/* 409 */       this.query.put(actualKey, StrUtil.nullToEmpty(URLDecoder.decode(value, charset, this.isFormUrlEncoded)));
/* 410 */     } else if (null != value) {
/*     */       
/* 412 */       this.query.put(URLDecoder.decode(value, charset, this.isFormUrlEncoded), null);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\ne\\url\UrlQuery.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */