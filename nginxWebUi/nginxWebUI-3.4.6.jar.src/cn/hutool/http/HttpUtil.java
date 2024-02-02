/*     */ package cn.hutool.http;
/*     */ 
/*     */ import cn.hutool.core.codec.Base64;
/*     */ import cn.hutool.core.convert.Convert;
/*     */ import cn.hutool.core.io.FileUtil;
/*     */ import cn.hutool.core.io.IoUtil;
/*     */ import cn.hutool.core.io.StreamProgress;
/*     */ import cn.hutool.core.map.MapUtil;
/*     */ import cn.hutool.core.net.RFC3986;
/*     */ import cn.hutool.core.net.url.UrlQuery;
/*     */ import cn.hutool.core.text.StrBuilder;
/*     */ import cn.hutool.core.util.CharsetUtil;
/*     */ import cn.hutool.core.util.ObjectUtil;
/*     */ import cn.hutool.core.util.ReUtil;
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import cn.hutool.core.util.URLUtil;
/*     */ import cn.hutool.http.cookie.GlobalCookieManager;
/*     */ import cn.hutool.http.server.SimpleServer;
/*     */ import java.io.File;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.net.HttpURLConnection;
/*     */ import java.nio.charset.Charset;
/*     */ import java.util.ArrayList;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.regex.Pattern;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class HttpUtil
/*     */ {
/*  42 */   public static final Pattern CHARSET_PATTERN = Pattern.compile("charset\\s*=\\s*([a-z0-9-]*)", 2);
/*     */ 
/*     */ 
/*     */   
/*  46 */   public static final Pattern META_CHARSET_PATTERN = Pattern.compile("<meta[^>]*?charset\\s*=\\s*['\"]?([a-z0-9-]*)", 2);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean isHttps(String url) {
/*  55 */     return url.toLowerCase().startsWith("https:");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean isHttp(String url) {
/*  66 */     return url.toLowerCase().startsWith("http:");
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
/*     */   public static HttpRequest createRequest(Method method, String url) {
/*  78 */     return HttpRequest.of(url).method(method);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static HttpRequest createGet(String url) {
/*  89 */     return createGet(url, false);
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
/*     */   public static HttpRequest createGet(String url, boolean isFollowRedirects) {
/* 101 */     return HttpRequest.get(url).setFollowRedirects(isFollowRedirects);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static HttpRequest createPost(String url) {
/* 112 */     return HttpRequest.post(url);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String get(String urlString, Charset customCharset) {
/* 123 */     return HttpRequest.get(urlString).charset(customCharset).execute().body();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String get(String urlString) {
/* 133 */     return get(urlString, HttpGlobalConfig.getTimeout());
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
/*     */   public static String get(String urlString, int timeout) {
/* 145 */     return HttpRequest.get(urlString).timeout(timeout).execute().body();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String get(String urlString, Map<String, Object> paramMap) {
/* 156 */     return HttpRequest.get(urlString).form(paramMap).execute().body();
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
/*     */   public static String get(String urlString, Map<String, Object> paramMap, int timeout) {
/* 169 */     return HttpRequest.get(urlString).form(paramMap).timeout(timeout).execute().body();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String post(String urlString, Map<String, Object> paramMap) {
/* 180 */     return post(urlString, paramMap, HttpGlobalConfig.getTimeout());
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
/*     */   public static String post(String urlString, Map<String, Object> paramMap, int timeout) {
/* 193 */     return HttpRequest.post(urlString).form(paramMap).timeout(timeout).execute().body();
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
/*     */   public static String post(String urlString, String body) {
/* 210 */     return post(urlString, body, HttpGlobalConfig.getTimeout());
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
/*     */   public static String post(String urlString, String body, int timeout) {
/* 229 */     return HttpRequest.post(urlString).timeout(timeout).body(body).execute().body();
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
/*     */   public static String downloadString(String url, String customCharsetName) {
/* 242 */     return downloadString(url, CharsetUtil.charset(customCharsetName), null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String downloadString(String url, Charset customCharset) {
/* 253 */     return downloadString(url, customCharset, null);
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
/*     */   public static String downloadString(String url, Charset customCharset, StreamProgress streamPress) {
/* 265 */     return HttpDownloader.downloadString(url, customCharset, streamPress);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static long downloadFile(String url, String dest) {
/* 276 */     return downloadFile(url, FileUtil.file(dest));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static long downloadFile(String url, File destFile) {
/* 287 */     return downloadFile(url, destFile, (StreamProgress)null);
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
/*     */   public static long downloadFile(String url, File destFile, int timeout) {
/* 300 */     return downloadFile(url, destFile, timeout, null);
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
/*     */   public static long downloadFile(String url, File destFile, StreamProgress streamProgress) {
/* 312 */     return downloadFile(url, destFile, -1, streamProgress);
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
/*     */   public static long downloadFile(String url, File destFile, int timeout, StreamProgress streamProgress) {
/* 326 */     return HttpDownloader.downloadFile(url, destFile, timeout, streamProgress);
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
/*     */   public static File downloadFileFromUrl(String url, String dest) {
/* 338 */     return downloadFileFromUrl(url, FileUtil.file(dest));
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
/*     */   public static File downloadFileFromUrl(String url, File destFile) {
/* 350 */     return downloadFileFromUrl(url, destFile, (StreamProgress)null);
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
/*     */   public static File downloadFileFromUrl(String url, File destFile, int timeout) {
/* 363 */     return downloadFileFromUrl(url, destFile, timeout, null);
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
/*     */   public static File downloadFileFromUrl(String url, File destFile, StreamProgress streamProgress) {
/* 376 */     return downloadFileFromUrl(url, destFile, -1, streamProgress);
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
/*     */   public static File downloadFileFromUrl(String url, File destFile, int timeout, StreamProgress streamProgress) {
/* 390 */     return HttpDownloader.downloadForFile(url, destFile, timeout, streamProgress);
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
/*     */   public static long download(String url, OutputStream out, boolean isCloseOut) {
/* 402 */     return download(url, out, isCloseOut, null);
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
/*     */   public static long download(String url, OutputStream out, boolean isCloseOut, StreamProgress streamProgress) {
/* 415 */     return HttpDownloader.download(url, out, isCloseOut, streamProgress);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static byte[] downloadBytes(String url) {
/* 426 */     return HttpDownloader.downloadBytes(url);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String toParams(Map<String, ?> paramMap) {
/* 436 */     return toParams(paramMap, CharsetUtil.CHARSET_UTF_8);
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
/*     */   @Deprecated
/*     */   public static String toParams(Map<String, Object> paramMap, String charsetName) {
/* 450 */     return toParams(paramMap, CharsetUtil.charset(charsetName));
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
/*     */   public static String toParams(Map<String, ?> paramMap, Charset charset) {
/* 469 */     return toParams(paramMap, charset, false);
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
/*     */   public static String toParams(Map<String, ?> paramMap, Charset charset, boolean isFormUrlEncoded) {
/* 488 */     return UrlQuery.of(paramMap, isFormUrlEncoded).build(charset);
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
/*     */   public static String encodeParams(String urlWithParams, Charset charset) {
/* 503 */     if (StrUtil.isBlank(urlWithParams)) {
/* 504 */       return "";
/*     */     }
/*     */     
/* 507 */     String urlPart = null;
/*     */     
/* 509 */     int pathEndPos = urlWithParams.indexOf('?');
/* 510 */     if (pathEndPos > -1) {
/*     */       
/* 512 */       urlPart = StrUtil.subPre(urlWithParams, pathEndPos);
/* 513 */       paramPart = StrUtil.subSuf(urlWithParams, pathEndPos + 1);
/* 514 */       if (StrUtil.isBlank(paramPart))
/*     */       {
/* 516 */         return urlPart; } 
/*     */     } else {
/* 518 */       if (false == StrUtil.contains(urlWithParams, '='))
/*     */       {
/* 520 */         return urlWithParams;
/*     */       }
/*     */       
/* 523 */       paramPart = urlWithParams;
/*     */     } 
/*     */     
/* 526 */     String paramPart = normalizeParams(paramPart, charset);
/*     */     
/* 528 */     return StrUtil.isBlank(urlPart) ? paramPart : (urlPart + "?" + paramPart);
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
/*     */   public static String normalizeParams(String paramPart, Charset charset) {
/* 542 */     if (StrUtil.isEmpty(paramPart)) {
/* 543 */       return paramPart;
/*     */     }
/* 545 */     StrBuilder builder = StrBuilder.create(paramPart.length() + 16);
/* 546 */     int len = paramPart.length();
/* 547 */     String name = null;
/* 548 */     int pos = 0;
/*     */     
/*     */     int i;
/* 551 */     for (i = 0; i < len; i++) {
/* 552 */       char c = paramPart.charAt(i);
/* 553 */       if (c == '=') {
/* 554 */         if (null == name) {
/*     */           
/* 556 */           name = (pos == i) ? "" : paramPart.substring(pos, i);
/* 557 */           pos = i + 1;
/*     */         } 
/* 559 */       } else if (c == '&') {
/* 560 */         if (pos != i) {
/* 561 */           if (null == name) {
/*     */             
/* 563 */             name = paramPart.substring(pos, i);
/* 564 */             builder.append(RFC3986.QUERY_PARAM_NAME.encode(name, charset, new char[0])).append('=');
/*     */           } else {
/* 566 */             builder.append(RFC3986.QUERY_PARAM_NAME.encode(name, charset, new char[0])).append('=')
/* 567 */               .append(RFC3986.QUERY_PARAM_VALUE.encode(paramPart.substring(pos, i), charset, new char[0])).append('&');
/*     */           } 
/* 569 */           name = null;
/*     */         } 
/* 571 */         pos = i + 1;
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/* 576 */     if (null != name) {
/* 577 */       builder.append(URLUtil.encodeQuery(name, charset)).append('=');
/*     */     }
/* 579 */     if (pos != i) {
/* 580 */       if (null == name && pos > 0) {
/* 581 */         builder.append('=');
/*     */       }
/* 583 */       builder.append(URLUtil.encodeQuery(paramPart.substring(pos, i), charset));
/*     */     } 
/*     */ 
/*     */     
/* 587 */     int lastIndex = builder.length() - 1;
/* 588 */     if ('&' == builder.charAt(lastIndex)) {
/* 589 */       builder.delTo(lastIndex);
/*     */     }
/* 591 */     return builder.toString();
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
/*     */   public static Map<String, String> decodeParamMap(String paramsStr, Charset charset) {
/* 603 */     Map<CharSequence, CharSequence> queryMap = UrlQuery.of(paramsStr, charset).getQueryMap();
/* 604 */     if (MapUtil.isEmpty(queryMap)) {
/* 605 */       return MapUtil.empty();
/*     */     }
/* 607 */     return Convert.toMap(String.class, String.class, queryMap);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Map<String, List<String>> decodeParams(String paramsStr, String charset) {
/* 618 */     return decodeParams(paramsStr, CharsetUtil.charset(charset));
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
/*     */   public static Map<String, List<String>> decodeParams(String paramsStr, Charset charset) {
/* 630 */     Map<CharSequence, CharSequence> queryMap = UrlQuery.of(paramsStr, charset).getQueryMap();
/* 631 */     if (MapUtil.isEmpty(queryMap)) {
/* 632 */       return MapUtil.empty();
/*     */     }
/*     */     
/* 635 */     Map<String, List<String>> params = new LinkedHashMap<>();
/* 636 */     queryMap.forEach((key, value) -> {
/*     */           List<String> values = params.computeIfAbsent(StrUtil.str(key), ());
/*     */           
/*     */           values.add(StrUtil.str(value));
/*     */         });
/* 641 */     return params;
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
/*     */   public static String urlWithForm(String url, Map<String, Object> form, Charset charset, boolean isEncodeParams) {
/* 655 */     if (isEncodeParams && StrUtil.contains(url, '?'))
/*     */     {
/* 657 */       url = encodeParams(url, charset);
/*     */     }
/*     */ 
/*     */     
/* 661 */     return urlWithForm(url, toParams(form, charset), charset, false);
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
/*     */   public static String urlWithForm(String url, String queryString, Charset charset, boolean isEncode) {
/* 674 */     if (StrUtil.isBlank(queryString)) {
/*     */       
/* 676 */       if (StrUtil.contains(url, '?'))
/*     */       {
/* 678 */         return isEncode ? encodeParams(url, charset) : url;
/*     */       }
/* 680 */       return url;
/*     */     } 
/*     */ 
/*     */     
/* 684 */     StrBuilder urlBuilder = StrBuilder.create(url.length() + queryString.length() + 16);
/* 685 */     int qmIndex = url.indexOf('?');
/* 686 */     if (qmIndex > 0) {
/*     */       
/* 688 */       urlBuilder.append(isEncode ? encodeParams(url, charset) : url);
/* 689 */       if (false == StrUtil.endWith(url, '&'))
/*     */       {
/* 691 */         urlBuilder.append('&');
/*     */       }
/*     */     } else {
/*     */       
/* 695 */       urlBuilder.append(url);
/* 696 */       if (qmIndex < 0)
/*     */       {
/* 698 */         urlBuilder.append('?');
/*     */       }
/*     */     } 
/* 701 */     urlBuilder.append(isEncode ? encodeParams(queryString, charset) : queryString);
/* 702 */     return urlBuilder.toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String getCharset(HttpURLConnection conn) {
/* 713 */     if (conn == null) {
/* 714 */       return null;
/*     */     }
/* 716 */     return getCharset(conn.getContentType());
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
/*     */   public static String getCharset(String contentType) {
/* 728 */     if (StrUtil.isBlank(contentType)) {
/* 729 */       return null;
/*     */     }
/* 731 */     return ReUtil.get(CHARSET_PATTERN, contentType, 1);
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
/*     */   public static String getString(InputStream in, Charset charset, boolean isGetCharsetFromContent) {
/* 744 */     byte[] contentBytes = IoUtil.readBytes(in);
/* 745 */     return getString(contentBytes, charset, isGetCharsetFromContent);
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
/*     */   public static String getString(byte[] contentBytes, Charset charset, boolean isGetCharsetFromContent) {
/* 758 */     if (null == contentBytes) {
/* 759 */       return null;
/*     */     }
/*     */     
/* 762 */     if (null == charset) {
/* 763 */       charset = CharsetUtil.CHARSET_UTF_8;
/*     */     }
/* 765 */     String content = new String(contentBytes, charset);
/* 766 */     if (isGetCharsetFromContent) {
/* 767 */       String charsetInContentStr = ReUtil.get(META_CHARSET_PATTERN, content, 1);
/* 768 */       if (StrUtil.isNotBlank(charsetInContentStr)) {
/* 769 */         Charset charsetInContent = null;
/*     */         try {
/* 771 */           charsetInContent = Charset.forName(charsetInContentStr);
/* 772 */         } catch (Exception e) {
/* 773 */           if (StrUtil.containsIgnoreCase(charsetInContentStr, "utf-8") || StrUtil.containsIgnoreCase(charsetInContentStr, "utf8")) {
/* 774 */             charsetInContent = CharsetUtil.CHARSET_UTF_8;
/* 775 */           } else if (StrUtil.containsIgnoreCase(charsetInContentStr, "gbk")) {
/* 776 */             charsetInContent = CharsetUtil.CHARSET_GBK;
/*     */           } 
/*     */         } 
/*     */         
/* 780 */         if (null != charsetInContent && false == charset.equals(charsetInContent)) {
/* 781 */           content = new String(contentBytes, charsetInContent);
/*     */         }
/*     */       } 
/*     */     } 
/* 785 */     return content;
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
/*     */   public static String getMimeType(String filePath, String defaultValue) {
/* 798 */     return (String)ObjectUtil.defaultIfNull(getMimeType(filePath), defaultValue);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String getMimeType(String filePath) {
/* 809 */     return FileUtil.getMimeType(filePath);
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
/*     */   public static String getContentTypeByRequestBody(String body) {
/* 826 */     ContentType contentType = ContentType.get(body);
/* 827 */     return (null == contentType) ? null : contentType.toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static SimpleServer createServer(int port) {
/* 838 */     return new SimpleServer(port);
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
/*     */   public static String buildBasicAuth(String username, String password, Charset charset) {
/* 854 */     String data = username.concat(":").concat(password);
/* 855 */     return "Basic " + Base64.encode(data, charset);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void closeCookie() {
/* 865 */     GlobalCookieManager.setCookieManager(null);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\http\HttpUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */