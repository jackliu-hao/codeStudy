/*     */ package cn.hutool.extra.servlet;
/*     */ 
/*     */ import cn.hutool.core.bean.BeanUtil;
/*     */ import cn.hutool.core.bean.copier.CopyOptions;
/*     */ import cn.hutool.core.bean.copier.ValueProvider;
/*     */ import cn.hutool.core.collection.ArrayIter;
/*     */ import cn.hutool.core.collection.IterUtil;
/*     */ import cn.hutool.core.exceptions.UtilException;
/*     */ import cn.hutool.core.io.FileUtil;
/*     */ import cn.hutool.core.io.IORuntimeException;
/*     */ import cn.hutool.core.io.IoUtil;
/*     */ import cn.hutool.core.map.CaseInsensitiveMap;
/*     */ import cn.hutool.core.map.MapUtil;
/*     */ import cn.hutool.core.net.NetUtil;
/*     */ import cn.hutool.core.net.multipart.MultipartFormData;
/*     */ import cn.hutool.core.net.multipart.UploadSetting;
/*     */ import cn.hutool.core.util.ArrayUtil;
/*     */ import cn.hutool.core.util.CharsetUtil;
/*     */ import cn.hutool.core.util.ObjectUtil;
/*     */ import cn.hutool.core.util.ReflectUtil;
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import cn.hutool.core.util.URLUtil;
/*     */ import jakarta.servlet.ServletOutputStream;
/*     */ import jakarta.servlet.ServletRequest;
/*     */ import jakarta.servlet.http.Cookie;
/*     */ import jakarta.servlet.http.HttpServletRequest;
/*     */ import jakarta.servlet.http.HttpServletResponse;
/*     */ import java.io.BufferedInputStream;
/*     */ import java.io.BufferedReader;
/*     */ import java.io.Closeable;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.io.PrintWriter;
/*     */ import java.io.Writer;
/*     */ import java.lang.invoke.SerializedLambda;
/*     */ import java.lang.reflect.Type;
/*     */ import java.nio.charset.Charset;
/*     */ import java.util.Collections;
/*     */ import java.util.Date;
/*     */ import java.util.Enumeration;
/*     */ import java.util.HashMap;
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
/*     */ public class JakartaServletUtil
/*     */ {
/*     */   public static final String METHOD_DELETE = "DELETE";
/*     */   public static final String METHOD_HEAD = "HEAD";
/*     */   public static final String METHOD_GET = "GET";
/*     */   public static final String METHOD_OPTIONS = "OPTIONS";
/*     */   public static final String METHOD_POST = "POST";
/*     */   public static final String METHOD_PUT = "PUT";
/*     */   public static final String METHOD_TRACE = "TRACE";
/*     */   
/*     */   public static Map<String, String[]> getParams(ServletRequest request) {
/*  69 */     Map<String, String[]> map = request.getParameterMap();
/*  70 */     return (Map)Collections.unmodifiableMap((Map)map);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Map<String, String> getParamMap(ServletRequest request) {
/*  80 */     Map<String, String> params = new HashMap<>();
/*  81 */     for (Map.Entry<String, String[]> entry : getParams(request).entrySet()) {
/*  82 */       params.put(entry.getKey(), ArrayUtil.join((Object[])entry.getValue(), ","));
/*     */     }
/*  84 */     return params;
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
/*     */   public static String getBody(ServletRequest request) {
/*  96 */     try (BufferedReader reader = request.getReader()) {
/*  97 */       return IoUtil.read(reader);
/*  98 */     } catch (IOException e) {
/*  99 */       throw new IORuntimeException(e);
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
/*     */   public static byte[] getBodyBytes(ServletRequest request) {
/*     */     try {
/* 113 */       return IoUtil.readBytes((InputStream)request.getInputStream());
/* 114 */     } catch (IOException e) {
/* 115 */       throw new IORuntimeException(e);
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
/*     */   public static <T> T fillBean(final ServletRequest request, T bean, CopyOptions copyOptions) {
/* 133 */     final String beanName = StrUtil.lowerFirst(bean.getClass().getSimpleName());
/* 134 */     return (T)BeanUtil.fillBean(bean, new ValueProvider<String>()
/*     */         {
/*     */           public Object value(String key, Type valueType) {
/* 137 */             String[] values = request.getParameterValues(key);
/* 138 */             if (ArrayUtil.isEmpty((Object[])values)) {
/* 139 */               values = request.getParameterValues(beanName + "." + key);
/* 140 */               if (ArrayUtil.isEmpty((Object[])values)) {
/* 141 */                 return null;
/*     */               }
/*     */             } 
/*     */             
/* 145 */             if (1 == values.length)
/*     */             {
/* 147 */               return values[0];
/*     */             }
/*     */             
/* 150 */             return values;
/*     */           }
/*     */ 
/*     */ 
/*     */ 
/*     */           
/*     */           public boolean containsKey(String key) {
/* 157 */             return (null != request.getParameter(key) || null != request.getParameter(beanName + "." + key));
/*     */           }
/*     */         },  copyOptions);
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
/*     */   public static <T> T fillBean(ServletRequest request, T bean, boolean isIgnoreError) {
/* 172 */     return fillBean(request, bean, CopyOptions.create().setIgnoreError(isIgnoreError));
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
/*     */   public static <T> T toBean(ServletRequest request, Class<T> beanClass, boolean isIgnoreError) {
/* 185 */     return fillBean(request, (T)ReflectUtil.newInstanceIfPossible(beanClass), isIgnoreError);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String getClientIP(HttpServletRequest request, String... otherHeaderNames) {
/* 212 */     String[] headers = { "X-Forwarded-For", "X-Real-IP", "Proxy-Client-IP", "WL-Proxy-Client-IP", "HTTP_CLIENT_IP", "HTTP_X_FORWARDED_FOR" };
/* 213 */     if (ArrayUtil.isNotEmpty((Object[])otherHeaderNames)) {
/* 214 */       headers = (String[])ArrayUtil.addAll((Object[][])new String[][] { headers, otherHeaderNames });
/*     */     }
/*     */     
/* 217 */     return getClientIPByHeader(request, headers);
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
/*     */   public static String getClientIPByHeader(HttpServletRequest request, String... headerNames) {
/* 235 */     for (String header : headerNames) {
/* 236 */       String str1 = request.getHeader(header);
/* 237 */       if (false == NetUtil.isUnknown(str1)) {
/* 238 */         return NetUtil.getMultistageReverseProxyIp(str1);
/*     */       }
/*     */     } 
/*     */     
/* 242 */     String ip = request.getRemoteAddr();
/* 243 */     return NetUtil.getMultistageReverseProxyIp(ip);
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
/*     */   public static MultipartFormData getMultipart(ServletRequest request) throws IORuntimeException {
/* 255 */     return getMultipart(request, new UploadSetting());
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
/*     */   public static MultipartFormData getMultipart(ServletRequest request, UploadSetting uploadSetting) throws IORuntimeException {
/* 270 */     MultipartFormData formData = new MultipartFormData(uploadSetting);
/*     */     try {
/* 272 */       formData.parseRequestStream((InputStream)request.getInputStream(), CharsetUtil.charset(request.getCharacterEncoding()));
/* 273 */     } catch (IOException e) {
/* 274 */       throw new IORuntimeException(e);
/*     */     } 
/*     */     
/* 277 */     return formData;
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
/*     */   public static Map<String, String> getHeaderMap(HttpServletRequest request) {
/* 290 */     Map<String, String> headerMap = new HashMap<>();
/*     */     
/* 292 */     Enumeration<String> names = request.getHeaderNames();
/*     */     
/* 294 */     while (names.hasMoreElements()) {
/* 295 */       String name = names.nextElement();
/* 296 */       headerMap.put(name, request.getHeader(name));
/*     */     } 
/*     */     
/* 299 */     return headerMap;
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
/*     */   public static String getHeaderIgnoreCase(HttpServletRequest request, String nameIgnoreCase) {
/* 311 */     Enumeration<String> names = request.getHeaderNames();
/*     */     
/* 313 */     while (names.hasMoreElements()) {
/* 314 */       String name = names.nextElement();
/* 315 */       if (name != null && name.equalsIgnoreCase(nameIgnoreCase)) {
/* 316 */         return request.getHeader(name);
/*     */       }
/*     */     } 
/*     */     
/* 320 */     return null;
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
/*     */   public static String getHeader(HttpServletRequest request, String name, String charsetName) {
/* 332 */     return getHeader(request, name, CharsetUtil.charset(charsetName));
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
/*     */   public static String getHeader(HttpServletRequest request, String name, Charset charset) {
/* 345 */     String header = request.getHeader(name);
/* 346 */     if (null != header) {
/* 347 */       return CharsetUtil.convert(header, CharsetUtil.CHARSET_ISO_8859_1, charset);
/*     */     }
/* 349 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean isIE(HttpServletRequest request) {
/* 359 */     String userAgent = getHeaderIgnoreCase(request, "User-Agent");
/* 360 */     if (StrUtil.isNotBlank(userAgent)) {
/*     */       
/* 362 */       userAgent = userAgent.toUpperCase();
/* 363 */       return (userAgent.contains("MSIE") || userAgent.contains("TRIDENT"));
/*     */     } 
/* 365 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean isGetMethod(HttpServletRequest request) {
/* 375 */     return "GET".equalsIgnoreCase(request.getMethod());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean isPostMethod(HttpServletRequest request) {
/* 385 */     return "POST".equalsIgnoreCase(request.getMethod());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean isMultipart(HttpServletRequest request) {
/* 395 */     if (false == isPostMethod(request)) {
/* 396 */       return false;
/*     */     }
/*     */     
/* 399 */     String contentType = request.getContentType();
/* 400 */     if (StrUtil.isBlank(contentType)) {
/* 401 */       return false;
/*     */     }
/* 403 */     return contentType.toLowerCase().startsWith("multipart/");
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
/*     */   public static Cookie getCookie(HttpServletRequest httpServletRequest, String name) {
/* 417 */     return readCookieMap(httpServletRequest).get(name);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Map<String, Cookie> readCookieMap(HttpServletRequest httpServletRequest) {
/* 427 */     Cookie[] cookies = httpServletRequest.getCookies();
/* 428 */     if (ArrayUtil.isEmpty((Object[])cookies)) {
/* 429 */       return MapUtil.empty();
/*     */     }
/*     */     
/* 432 */     return IterUtil.toMap((Iterator)new ArrayIter((Object[])httpServletRequest
/* 433 */           .getCookies()), (Map)new CaseInsensitiveMap(), Cookie::getName);
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
/*     */   public static void addCookie(HttpServletResponse response, Cookie cookie) {
/* 445 */     response.addCookie(cookie);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void addCookie(HttpServletResponse response, String name, String value) {
/* 456 */     response.addCookie(new Cookie(name, value));
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
/*     */   public static void addCookie(HttpServletResponse response, String name, String value, int maxAgeInSeconds, String path, String domain) {
/* 470 */     Cookie cookie = new Cookie(name, value);
/* 471 */     if (domain != null) {
/* 472 */       cookie.setDomain(domain);
/*     */     }
/* 474 */     cookie.setMaxAge(maxAgeInSeconds);
/* 475 */     cookie.setPath(path);
/* 476 */     addCookie(response, cookie);
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
/*     */   public static void addCookie(HttpServletResponse response, String name, String value, int maxAgeInSeconds) {
/* 490 */     addCookie(response, name, value, maxAgeInSeconds, "/", null);
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
/*     */   public static PrintWriter getWriter(HttpServletResponse response) throws IORuntimeException {
/*     */     try {
/* 505 */       return response.getWriter();
/* 506 */     } catch (IOException e) {
/* 507 */       throw new IORuntimeException(e);
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
/*     */   public static void write(HttpServletResponse response, String text, String contentType) {
/* 519 */     response.setContentType(contentType);
/* 520 */     Writer writer = null;
/*     */     try {
/* 522 */       writer = response.getWriter();
/* 523 */       writer.write(text);
/* 524 */       writer.flush();
/* 525 */     } catch (IOException e) {
/* 526 */       throw new UtilException(e);
/*     */     } finally {
/* 528 */       IoUtil.close(writer);
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
/*     */   public static void write(HttpServletResponse response, File file) {
/* 540 */     String fileName = file.getName();
/* 541 */     String contentType = (String)ObjectUtil.defaultIfNull(FileUtil.getMimeType(fileName), "application/octet-stream");
/* 542 */     BufferedInputStream in = null;
/*     */     try {
/* 544 */       in = FileUtil.getInputStream(file);
/* 545 */       write(response, in, contentType, fileName);
/*     */     } finally {
/* 547 */       IoUtil.close(in);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void write(HttpServletResponse response, InputStream in, String contentType, String fileName) {
/* 575 */     String charset = (String)ObjectUtil.defaultIfNull(response.getCharacterEncoding(), "UTF-8");
/* 576 */     response.setHeader("Content-Disposition", StrUtil.format("attachment;filename=\"{}\"", new Object[] {
/* 577 */             URLUtil.encode(fileName, CharsetUtil.charset(charset)) }));
/* 578 */     response.setContentType(contentType);
/* 579 */     write(response, in);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void write(HttpServletResponse response, InputStream in, String contentType) {
/* 590 */     response.setContentType(contentType);
/* 591 */     write(response, in);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void write(HttpServletResponse response, InputStream in) {
/* 601 */     write(response, in, 8192);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void write(HttpServletResponse response, InputStream in, int bufferSize) {
/* 612 */     ServletOutputStream out = null;
/*     */     try {
/* 614 */       out = response.getOutputStream();
/* 615 */       IoUtil.copy(in, (OutputStream)out, bufferSize);
/* 616 */     } catch (IOException e) {
/* 617 */       throw new UtilException(e);
/*     */     } finally {
/* 619 */       IoUtil.close((Closeable)out);
/* 620 */       IoUtil.close(in);
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
/*     */   public static void setHeader(HttpServletResponse response, String name, Object value) {
/* 632 */     if (value instanceof String) {
/* 633 */       response.setHeader(name, (String)value);
/* 634 */     } else if (Date.class.isAssignableFrom(value.getClass())) {
/* 635 */       response.setDateHeader(name, ((Date)value).getTime());
/* 636 */     } else if (value instanceof Integer || "int".equalsIgnoreCase(value.getClass().getSimpleName())) {
/* 637 */       response.setIntHeader(name, ((Integer)value).intValue());
/*     */     } else {
/* 639 */       response.setHeader(name, value.toString());
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\extra\servlet\JakartaServletUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */