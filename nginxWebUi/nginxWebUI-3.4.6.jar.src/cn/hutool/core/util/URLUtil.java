/*     */ package cn.hutool.core.util;
/*     */ 
/*     */ import cn.hutool.core.exceptions.UtilException;
/*     */ import cn.hutool.core.io.IORuntimeException;
/*     */ import cn.hutool.core.io.IoUtil;
/*     */ import cn.hutool.core.io.resource.ResourceUtil;
/*     */ import cn.hutool.core.lang.Assert;
/*     */ import cn.hutool.core.net.URLDecoder;
/*     */ import cn.hutool.core.net.URLEncodeUtil;
/*     */ import cn.hutool.core.net.url.UrlQuery;
/*     */ import java.io.BufferedReader;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.net.HttpURLConnection;
/*     */ import java.net.JarURLConnection;
/*     */ import java.net.MalformedURLException;
/*     */ import java.net.URI;
/*     */ import java.net.URISyntaxException;
/*     */ import java.net.URL;
/*     */ import java.net.URLConnection;
/*     */ import java.net.URLStreamHandler;
/*     */ import java.nio.charset.Charset;
/*     */ import java.util.Map;
/*     */ import java.util.jar.JarFile;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class URLUtil
/*     */   extends URLEncodeUtil
/*     */ {
/*     */   public static final String CLASSPATH_URL_PREFIX = "classpath:";
/*     */   public static final String FILE_URL_PREFIX = "file:";
/*     */   public static final String JAR_URL_PREFIX = "jar:";
/*     */   public static final String WAR_URL_PREFIX = "war:";
/*     */   public static final String URL_PROTOCOL_FILE = "file";
/*     */   public static final String URL_PROTOCOL_JAR = "jar";
/*     */   public static final String URL_PROTOCOL_ZIP = "zip";
/*     */   public static final String URL_PROTOCOL_WSJAR = "wsjar";
/*     */   public static final String URL_PROTOCOL_VFSZIP = "vfszip";
/*     */   public static final String URL_PROTOCOL_VFSFILE = "vfsfile";
/*     */   public static final String URL_PROTOCOL_VFS = "vfs";
/*     */   public static final String JAR_URL_SEPARATOR = "!/";
/*     */   public static final String WAR_URL_SEPARATOR = "*/";
/*     */   
/*     */   public static URL url(URI uri) throws UtilException {
/* 108 */     if (null == uri) {
/* 109 */       return null;
/*     */     }
/*     */     try {
/* 112 */       return uri.toURL();
/* 113 */     } catch (MalformedURLException e) {
/* 114 */       throw new UtilException(e);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static URL url(String url) {
/* 125 */     return url(url, null);
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
/*     */   public static URL url(String url, URLStreamHandler handler) {
/* 137 */     if (null == url) {
/* 138 */       return null;
/*     */     }
/*     */ 
/*     */     
/* 142 */     if (url.startsWith("classpath:")) {
/* 143 */       url = url.substring("classpath:".length());
/* 144 */       return ClassLoaderUtil.getClassLoader().getResource(url);
/*     */     } 
/*     */     
/*     */     try {
/* 148 */       return new URL(null, url, handler);
/* 149 */     } catch (MalformedURLException e) {
/*     */       
/*     */       try {
/* 152 */         return (new File(url)).toURI().toURL();
/* 153 */       } catch (MalformedURLException ex2) {
/* 154 */         throw new UtilException(e);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static URI getStringURI(CharSequence content) {
/* 167 */     if (null == content) {
/* 168 */       return null;
/*     */     }
/* 170 */     String contentStr = StrUtil.addPrefixIfNot(content, "string:///");
/* 171 */     return URI.create(contentStr);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static URL toUrlForHttp(String urlStr) {
/* 182 */     return toUrlForHttp(urlStr, null);
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
/*     */   public static URL toUrlForHttp(String urlStr, URLStreamHandler handler) {
/* 194 */     Assert.notBlank(urlStr, "Url is blank !", new Object[0]);
/*     */     
/* 196 */     urlStr = encodeBlank(urlStr);
/*     */     try {
/* 198 */       return new URL(null, urlStr, handler);
/* 199 */     } catch (MalformedURLException e) {
/* 200 */       throw new UtilException(e);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String encodeBlank(CharSequence urlStr) {
/* 212 */     if (urlStr == null) {
/* 213 */       return null;
/*     */     }
/*     */     
/* 216 */     int len = urlStr.length();
/* 217 */     StringBuilder sb = new StringBuilder(len);
/*     */     
/* 219 */     for (int i = 0; i < len; i++) {
/* 220 */       char c = urlStr.charAt(i);
/* 221 */       if (CharUtil.isBlankChar(c)) {
/* 222 */         sb.append("%20");
/*     */       } else {
/* 224 */         sb.append(c);
/*     */       } 
/*     */     } 
/* 227 */     return sb.toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static URL getURL(String pathBaseClassLoader) {
/* 238 */     return ResourceUtil.getResource(pathBaseClassLoader);
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
/*     */   public static URL getURL(String path, Class<?> clazz) {
/* 250 */     return ResourceUtil.getResource(path, clazz);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static URL getURL(File file) {
/* 261 */     Assert.notNull(file, "File is null !", new Object[0]);
/*     */     try {
/* 263 */       return file.toURI().toURL();
/* 264 */     } catch (MalformedURLException e) {
/* 265 */       throw new UtilException(e, "Error occured when get URL!", new Object[0]);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static URL[] getURLs(File... files) {
/* 277 */     URL[] urls = new URL[files.length];
/*     */     try {
/* 279 */       for (int i = 0; i < files.length; i++) {
/* 280 */         urls[i] = files[i].toURI().toURL();
/*     */       }
/* 282 */     } catch (MalformedURLException e) {
/* 283 */       throw new UtilException(e, "Error occured when get URL!", new Object[0]);
/*     */     } 
/*     */     
/* 286 */     return urls;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static URI getHost(URL url) {
/* 297 */     if (null == url) {
/* 298 */       return null;
/*     */     }
/*     */     
/*     */     try {
/* 302 */       return new URI(url.getProtocol(), url.getHost(), null, null);
/* 303 */     } catch (URISyntaxException e) {
/* 304 */       throw new UtilException(e);
/*     */     } 
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
/*     */   public static String completeUrl(String baseUrl, String relativePath) {
/* 317 */     baseUrl = normalize(baseUrl, false);
/* 318 */     if (StrUtil.isBlank(baseUrl)) {
/* 319 */       return null;
/*     */     }
/*     */     
/*     */     try {
/* 323 */       URL absoluteUrl = new URL(baseUrl);
/* 324 */       URL parseUrl = new URL(absoluteUrl, relativePath);
/* 325 */       return parseUrl.toString();
/* 326 */     } catch (MalformedURLException e) {
/* 327 */       throw new UtilException(e);
/*     */     } 
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
/*     */   public static String decode(String url) throws UtilException {
/* 342 */     return decode(url, "UTF-8");
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
/*     */   public static String decode(String content, Charset charset) {
/* 356 */     return URLDecoder.decode(content, charset);
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
/*     */   public static String decode(String content, Charset charset, boolean isPlusToSpace) {
/* 370 */     return URLDecoder.decode(content, charset, isPlusToSpace);
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
/*     */   public static String decode(String content, String charset) throws UtilException {
/* 383 */     return decode(content, StrUtil.isEmpty(charset) ? null : CharsetUtil.charset(charset));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String getPath(String uriStr) {
/* 394 */     return toURI(uriStr).getPath();
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
/*     */   public static String getDecodedPath(URL url) {
/* 407 */     if (null == url) {
/* 408 */       return null;
/*     */     }
/*     */     
/* 411 */     String path = null;
/*     */     
/*     */     try {
/* 414 */       path = toURI(url).getPath();
/* 415 */     } catch (UtilException utilException) {}
/*     */ 
/*     */     
/* 418 */     return (null != path) ? path : url.getPath();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static URI toURI(URL url) throws UtilException {
/* 429 */     return toURI(url, false);
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
/*     */   public static URI toURI(URL url, boolean isEncode) throws UtilException {
/* 442 */     if (null == url) {
/* 443 */       return null;
/*     */     }
/*     */     
/* 446 */     return toURI(url.toString(), isEncode);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static URI toURI(String location) throws UtilException {
/* 457 */     return toURI(location, false);
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
/*     */   public static URI toURI(String location, boolean isEncode) throws UtilException {
/* 470 */     if (isEncode) {
/* 471 */       location = encode(location);
/*     */     }
/*     */     try {
/* 474 */       return new URI(StrUtil.trim(location));
/* 475 */     } catch (URISyntaxException e) {
/* 476 */       throw new UtilException(e);
/*     */     } 
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
/*     */   public static boolean isFileURL(URL url) {
/* 489 */     Assert.notNull(url, "URL must be not null", new Object[0]);
/* 490 */     String protocol = url.getProtocol();
/* 491 */     return ("file".equals(protocol) || "vfsfile"
/* 492 */       .equals(protocol) || "vfs"
/* 493 */       .equals(protocol));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean isJarURL(URL url) {
/* 503 */     Assert.notNull(url, "URL must be not null", new Object[0]);
/* 504 */     String protocol = url.getProtocol();
/* 505 */     return ("jar".equals(protocol) || "zip"
/* 506 */       .equals(protocol) || "vfszip"
/* 507 */       .equals(protocol) || "wsjar"
/* 508 */       .equals(protocol));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean isJarFileURL(URL url) {
/* 519 */     Assert.notNull(url, "URL must be not null", new Object[0]);
/* 520 */     return ("file".equals(url.getProtocol()) && url
/* 521 */       .getPath().toLowerCase().endsWith(".jar"));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static InputStream getStream(URL url) {
/* 532 */     Assert.notNull(url, "URL must be not null", new Object[0]);
/*     */     try {
/* 534 */       return url.openStream();
/* 535 */     } catch (IOException e) {
/* 536 */       throw new IORuntimeException(e);
/*     */     } 
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
/*     */   public static BufferedReader getReader(URL url, Charset charset) {
/* 549 */     return IoUtil.getReader(getStream(url), charset);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static JarFile getJarFile(URL url) {
/*     */     try {
/* 561 */       JarURLConnection urlConnection = (JarURLConnection)url.openConnection();
/* 562 */       return urlConnection.getJarFile();
/* 563 */     } catch (IOException e) {
/* 564 */       throw new IORuntimeException(e);
/*     */     } 
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
/*     */   public static String normalize(String url) {
/* 581 */     return normalize(url, false);
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
/*     */   public static String normalize(String url, boolean isEncodePath) {
/* 599 */     return normalize(url, isEncodePath, false);
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
/*     */   public static String normalize(String url, boolean isEncodePath, boolean replaceSlash) {
/*     */     String protocol, body;
/* 619 */     if (StrUtil.isBlank(url)) {
/* 620 */       return url;
/*     */     }
/* 622 */     int sepIndex = url.indexOf("://");
/*     */ 
/*     */     
/* 625 */     if (sepIndex > 0) {
/* 626 */       protocol = StrUtil.subPre(url, sepIndex + 3);
/* 627 */       body = StrUtil.subSuf(url, sepIndex + 3);
/*     */     } else {
/* 629 */       protocol = "http://";
/* 630 */       body = url;
/*     */     } 
/*     */     
/* 633 */     int paramsSepIndex = StrUtil.indexOf(body, '?');
/* 634 */     String params = null;
/* 635 */     if (paramsSepIndex > 0) {
/* 636 */       params = StrUtil.subSuf(body, paramsSepIndex);
/* 637 */       body = StrUtil.subPre(body, paramsSepIndex);
/*     */     } 
/*     */     
/* 640 */     if (StrUtil.isNotEmpty(body)) {
/*     */ 
/*     */       
/* 643 */       body = body.replaceAll("^[\\\\/]+", "");
/*     */       
/* 645 */       body = body.replace("\\", "/");
/* 646 */       if (replaceSlash)
/*     */       {
/* 648 */         body = body.replaceAll("//+", "/");
/*     */       }
/*     */     } 
/*     */     
/* 652 */     int pathSepIndex = StrUtil.indexOf(body, '/');
/* 653 */     String domain = body;
/* 654 */     String path = null;
/* 655 */     if (pathSepIndex > 0) {
/* 656 */       domain = StrUtil.subPre(body, pathSepIndex);
/* 657 */       path = StrUtil.subSuf(body, pathSepIndex);
/*     */     } 
/* 659 */     if (isEncodePath) {
/* 660 */       path = encode(path);
/*     */     }
/* 662 */     return protocol + domain + StrUtil.nullToEmpty(path) + StrUtil.nullToEmpty(params);
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
/*     */   public static String buildQuery(Map<String, ?> paramMap, Charset charset) {
/* 679 */     return UrlQuery.of(paramMap).build(charset);
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
/*     */   public static long getContentLength(URL url) throws IORuntimeException {
/* 691 */     if (null == url) {
/* 692 */       return -1L;
/*     */     }
/*     */     
/* 695 */     URLConnection conn = null;
/*     */     try {
/* 697 */       conn = url.openConnection();
/* 698 */       return conn.getContentLengthLong();
/* 699 */     } catch (IOException e) {
/* 700 */       throw new IORuntimeException(e);
/*     */     } finally {
/* 702 */       if (conn instanceof HttpURLConnection) {
/* 703 */         ((HttpURLConnection)conn).disconnect();
/*     */       }
/*     */     } 
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
/*     */   public static String getDataUriBase64(String mimeType, String data) {
/* 724 */     return getDataUri(mimeType, (Charset)null, "base64", data);
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
/*     */   
/*     */   public static String getDataUri(String mimeType, String encoding, String data) {
/* 744 */     return getDataUri(mimeType, (Charset)null, encoding, data);
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
/*     */ 
/*     */   
/*     */   public static String getDataUri(String mimeType, Charset charset, String encoding, String data) {
/* 765 */     StringBuilder builder = StrUtil.builder(new CharSequence[] { "data:" });
/* 766 */     if (StrUtil.isNotBlank(mimeType)) {
/* 767 */       builder.append(mimeType);
/*     */     }
/* 769 */     if (null != charset) {
/* 770 */       builder.append(";charset=").append(charset.name());
/*     */     }
/* 772 */     if (StrUtil.isNotBlank(encoding)) {
/* 773 */       builder.append(';').append(encoding);
/*     */     }
/* 775 */     builder.append(',').append(data);
/*     */     
/* 777 */     return builder.toString();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\cor\\util\URLUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */