/*     */ package org.apache.http.entity;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ import java.nio.charset.Charset;
/*     */ import java.nio.charset.UnsupportedCharsetException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.List;
/*     */ import java.util.Locale;
/*     */ import java.util.Map;
/*     */ import org.apache.http.Consts;
/*     */ import org.apache.http.Header;
/*     */ import org.apache.http.HeaderElement;
/*     */ import org.apache.http.HttpEntity;
/*     */ import org.apache.http.NameValuePair;
/*     */ import org.apache.http.ParseException;
/*     */ import org.apache.http.annotation.Contract;
/*     */ import org.apache.http.annotation.ThreadingBehavior;
/*     */ import org.apache.http.message.BasicHeaderValueFormatter;
/*     */ import org.apache.http.message.BasicHeaderValueParser;
/*     */ import org.apache.http.message.BasicNameValuePair;
/*     */ import org.apache.http.message.ParserCursor;
/*     */ import org.apache.http.util.Args;
/*     */ import org.apache.http.util.CharArrayBuffer;
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
/*     */ @Contract(threading = ThreadingBehavior.IMMUTABLE)
/*     */ public final class ContentType
/*     */   implements Serializable
/*     */ {
/*     */   private static final long serialVersionUID = -7768694718232371896L;
/*  72 */   public static final ContentType APPLICATION_ATOM_XML = create("application/atom+xml", Consts.ISO_8859_1);
/*     */   
/*  74 */   public static final ContentType APPLICATION_FORM_URLENCODED = create("application/x-www-form-urlencoded", Consts.ISO_8859_1);
/*     */   
/*  76 */   public static final ContentType APPLICATION_JSON = create("application/json", Consts.UTF_8);
/*     */   
/*  78 */   public static final ContentType APPLICATION_OCTET_STREAM = create("application/octet-stream", (Charset)null);
/*     */   
/*  80 */   public static final ContentType APPLICATION_SOAP_XML = create("application/soap+xml", Consts.UTF_8);
/*     */   
/*  82 */   public static final ContentType APPLICATION_SVG_XML = create("application/svg+xml", Consts.ISO_8859_1);
/*     */   
/*  84 */   public static final ContentType APPLICATION_XHTML_XML = create("application/xhtml+xml", Consts.ISO_8859_1);
/*     */   
/*  86 */   public static final ContentType APPLICATION_XML = create("application/xml", Consts.ISO_8859_1);
/*     */   
/*  88 */   public static final ContentType IMAGE_BMP = create("image/bmp");
/*     */   
/*  90 */   public static final ContentType IMAGE_GIF = create("image/gif");
/*     */   
/*  92 */   public static final ContentType IMAGE_JPEG = create("image/jpeg");
/*     */   
/*  94 */   public static final ContentType IMAGE_PNG = create("image/png");
/*     */   
/*  96 */   public static final ContentType IMAGE_SVG = create("image/svg+xml");
/*     */   
/*  98 */   public static final ContentType IMAGE_TIFF = create("image/tiff");
/*     */   
/* 100 */   public static final ContentType IMAGE_WEBP = create("image/webp");
/*     */   
/* 102 */   public static final ContentType MULTIPART_FORM_DATA = create("multipart/form-data", Consts.ISO_8859_1);
/*     */   
/* 104 */   public static final ContentType TEXT_HTML = create("text/html", Consts.ISO_8859_1);
/*     */   
/* 106 */   public static final ContentType TEXT_PLAIN = create("text/plain", Consts.ISO_8859_1);
/*     */   
/* 108 */   public static final ContentType TEXT_XML = create("text/xml", Consts.ISO_8859_1);
/*     */   
/* 110 */   public static final ContentType WILDCARD = create("*/*", (Charset)null);
/*     */ 
/*     */   
/*     */   private static final Map<String, ContentType> CONTENT_TYPE_MAP;
/*     */ 
/*     */   
/*     */   static {
/* 117 */     ContentType[] contentTypes = { APPLICATION_ATOM_XML, APPLICATION_FORM_URLENCODED, APPLICATION_JSON, APPLICATION_SVG_XML, APPLICATION_XHTML_XML, APPLICATION_XML, IMAGE_BMP, IMAGE_GIF, IMAGE_JPEG, IMAGE_PNG, IMAGE_SVG, IMAGE_TIFF, IMAGE_WEBP, MULTIPART_FORM_DATA, TEXT_HTML, TEXT_PLAIN, TEXT_XML };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 135 */     HashMap<String, ContentType> map = new HashMap<String, ContentType>();
/* 136 */     for (ContentType contentType : contentTypes) {
/* 137 */       map.put(contentType.getMimeType(), contentType);
/*     */     }
/* 139 */     CONTENT_TYPE_MAP = Collections.unmodifiableMap(map);
/*     */   }
/*     */ 
/*     */   
/* 143 */   public static final ContentType DEFAULT_TEXT = TEXT_PLAIN;
/* 144 */   public static final ContentType DEFAULT_BINARY = APPLICATION_OCTET_STREAM;
/*     */   
/*     */   private final String mimeType;
/*     */   
/*     */   private final Charset charset;
/*     */   
/*     */   private final NameValuePair[] params;
/*     */   
/*     */   ContentType(String mimeType, Charset charset) {
/* 153 */     this.mimeType = mimeType;
/* 154 */     this.charset = charset;
/* 155 */     this.params = null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   ContentType(String mimeType, Charset charset, NameValuePair[] params) {
/* 162 */     this.mimeType = mimeType;
/* 163 */     this.charset = charset;
/* 164 */     this.params = params;
/*     */   }
/*     */   
/*     */   public String getMimeType() {
/* 168 */     return this.mimeType;
/*     */   }
/*     */   
/*     */   public Charset getCharset() {
/* 172 */     return this.charset;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getParameter(String name) {
/* 179 */     Args.notEmpty(name, "Parameter name");
/* 180 */     if (this.params == null) {
/* 181 */       return null;
/*     */     }
/* 183 */     for (NameValuePair param : this.params) {
/* 184 */       if (param.getName().equalsIgnoreCase(name)) {
/* 185 */         return param.getValue();
/*     */       }
/*     */     } 
/* 188 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/* 197 */     CharArrayBuffer buf = new CharArrayBuffer(64);
/* 198 */     buf.append(this.mimeType);
/* 199 */     if (this.params != null) {
/* 200 */       buf.append("; ");
/* 201 */       BasicHeaderValueFormatter.INSTANCE.formatParameters(buf, this.params, false);
/* 202 */     } else if (this.charset != null) {
/* 203 */       buf.append("; charset=");
/* 204 */       buf.append(this.charset.name());
/*     */     } 
/* 206 */     return buf.toString();
/*     */   }
/*     */   
/*     */   private static boolean valid(String s) {
/* 210 */     for (int i = 0; i < s.length(); i++) {
/* 211 */       char ch = s.charAt(i);
/* 212 */       if (ch == '"' || ch == ',' || ch == ';') {
/* 213 */         return false;
/*     */       }
/*     */     } 
/* 216 */     return true;
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
/*     */   public static ContentType create(String mimeType, Charset charset) {
/* 228 */     String normalizedMimeType = ((String)Args.notBlank(mimeType, "MIME type")).toLowerCase(Locale.ROOT);
/* 229 */     Args.check(valid(normalizedMimeType), "MIME type may not contain reserved characters");
/* 230 */     return new ContentType(normalizedMimeType, charset);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ContentType create(String mimeType) {
/* 241 */     return create(mimeType, (Charset)null);
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
/*     */   public static ContentType create(String mimeType, String charset) throws UnsupportedCharsetException {
/* 257 */     return create(mimeType, !TextUtils.isBlank(charset) ? Charset.forName(charset) : null);
/*     */   }
/*     */   
/*     */   private static ContentType create(HeaderElement helem, boolean strict) {
/* 261 */     return create(helem.getName(), helem.getParameters(), strict);
/*     */   }
/*     */   
/*     */   private static ContentType create(String mimeType, NameValuePair[] params, boolean strict) {
/* 265 */     Charset charset = null;
/* 266 */     for (NameValuePair param : params) {
/* 267 */       if (param.getName().equalsIgnoreCase("charset")) {
/* 268 */         String s = param.getValue();
/* 269 */         if (!TextUtils.isBlank(s)) {
/*     */           try {
/* 271 */             charset = Charset.forName(s);
/* 272 */           } catch (UnsupportedCharsetException ex) {
/* 273 */             if (strict) {
/* 274 */               throw ex;
/*     */             }
/*     */           } 
/*     */         }
/*     */         break;
/*     */       } 
/*     */     } 
/* 281 */     return new ContentType(mimeType, charset, (params != null && params.length > 0) ? params : null);
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
/*     */   public static ContentType create(String mimeType, NameValuePair... params) throws UnsupportedCharsetException {
/* 296 */     String type = ((String)Args.notBlank(mimeType, "MIME type")).toLowerCase(Locale.ROOT);
/* 297 */     Args.check(valid(type), "MIME type may not contain reserved characters");
/* 298 */     return create(mimeType, params, true);
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
/*     */   public static ContentType parse(String s) throws ParseException, UnsupportedCharsetException {
/* 313 */     Args.notNull(s, "Content type");
/* 314 */     CharArrayBuffer buf = new CharArrayBuffer(s.length());
/* 315 */     buf.append(s);
/* 316 */     ParserCursor cursor = new ParserCursor(0, s.length());
/* 317 */     HeaderElement[] elements = BasicHeaderValueParser.INSTANCE.parseElements(buf, cursor);
/* 318 */     if (elements.length > 0) {
/* 319 */       return create(elements[0], true);
/*     */     }
/* 321 */     throw new ParseException("Invalid content type: " + s);
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
/*     */   public static ContentType get(HttpEntity entity) throws ParseException, UnsupportedCharsetException {
/* 338 */     if (entity == null) {
/* 339 */       return null;
/*     */     }
/* 341 */     Header header = entity.getContentType();
/* 342 */     if (header != null) {
/* 343 */       HeaderElement[] elements = header.getElements();
/* 344 */       if (elements.length > 0) {
/* 345 */         return create(elements[0], true);
/*     */       }
/*     */     } 
/* 348 */     return null;
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
/*     */   public static ContentType getLenient(HttpEntity entity) {
/* 362 */     if (entity == null) {
/* 363 */       return null;
/*     */     }
/* 365 */     Header header = entity.getContentType();
/* 366 */     if (header != null) {
/*     */       try {
/* 368 */         HeaderElement[] elements = header.getElements();
/* 369 */         if (elements.length > 0) {
/* 370 */           return create(elements[0], false);
/*     */         }
/* 372 */       } catch (ParseException ex) {
/* 373 */         return null;
/*     */       } 
/*     */     }
/* 376 */     return null;
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
/*     */   public static ContentType getOrDefault(HttpEntity entity) throws ParseException, UnsupportedCharsetException {
/* 392 */     ContentType contentType = get(entity);
/* 393 */     return (contentType != null) ? contentType : DEFAULT_TEXT;
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
/*     */   public static ContentType getLenientOrDefault(HttpEntity entity) throws ParseException, UnsupportedCharsetException {
/* 407 */     ContentType contentType = get(entity);
/* 408 */     return (contentType != null) ? contentType : DEFAULT_TEXT;
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
/*     */   public static ContentType getByMimeType(String mimeType) {
/* 421 */     if (mimeType == null) {
/* 422 */       return null;
/*     */     }
/* 424 */     return CONTENT_TYPE_MAP.get(mimeType);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ContentType withCharset(Charset charset) {
/* 435 */     return create(getMimeType(), charset);
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
/*     */   public ContentType withCharset(String charset) {
/* 448 */     return create(getMimeType(), charset);
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
/*     */   public ContentType withParameters(NameValuePair... params) throws UnsupportedCharsetException {
/* 460 */     if (params.length == 0) {
/* 461 */       return this;
/*     */     }
/* 463 */     Map<String, String> paramMap = new LinkedHashMap<String, String>();
/* 464 */     if (this.params != null) {
/* 465 */       for (NameValuePair param : this.params) {
/* 466 */         paramMap.put(param.getName(), param.getValue());
/*     */       }
/*     */     }
/* 469 */     for (NameValuePair param : params) {
/* 470 */       paramMap.put(param.getName(), param.getValue());
/*     */     }
/* 472 */     List<NameValuePair> newParams = new ArrayList<NameValuePair>(paramMap.size() + 1);
/* 473 */     if (this.charset != null && !paramMap.containsKey("charset")) {
/* 474 */       newParams.add(new BasicNameValuePair("charset", this.charset.name()));
/*     */     }
/* 476 */     for (Map.Entry<String, String> entry : paramMap.entrySet()) {
/* 477 */       newParams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
/*     */     }
/* 479 */     return create(getMimeType(), newParams.<NameValuePair>toArray(new NameValuePair[newParams.size()]), true);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\entity\ContentType.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */