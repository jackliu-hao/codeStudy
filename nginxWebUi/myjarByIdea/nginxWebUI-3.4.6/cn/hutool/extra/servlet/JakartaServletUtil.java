package cn.hutool.extra.servlet;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.bean.copier.ValueProvider;
import cn.hutool.core.collection.ArrayIter;
import cn.hutool.core.collection.IterUtil;
import cn.hutool.core.exceptions.UtilException;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.lang.func.Func1;
import cn.hutool.core.map.CaseInsensitiveMap;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.net.NetUtil;
import cn.hutool.core.net.multipart.MultipartFormData;
import cn.hutool.core.net.multipart.UploadSetting;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class JakartaServletUtil {
   public static final String METHOD_DELETE = "DELETE";
   public static final String METHOD_HEAD = "HEAD";
   public static final String METHOD_GET = "GET";
   public static final String METHOD_OPTIONS = "OPTIONS";
   public static final String METHOD_POST = "POST";
   public static final String METHOD_PUT = "PUT";
   public static final String METHOD_TRACE = "TRACE";

   public static Map<String, String[]> getParams(ServletRequest request) {
      Map<String, String[]> map = request.getParameterMap();
      return Collections.unmodifiableMap(map);
   }

   public static Map<String, String> getParamMap(ServletRequest request) {
      Map<String, String> params = new HashMap();
      Iterator var2 = getParams(request).entrySet().iterator();

      while(var2.hasNext()) {
         Map.Entry<String, String[]> entry = (Map.Entry)var2.next();
         params.put(entry.getKey(), ArrayUtil.join((Object[])((Object[])entry.getValue()), ","));
      }

      return params;
   }

   public static String getBody(ServletRequest request) {
      try {
         BufferedReader reader = request.getReader();
         Throwable var2 = null;

         String var3;
         try {
            var3 = IoUtil.read((Reader)reader);
         } catch (Throwable var13) {
            var2 = var13;
            throw var13;
         } finally {
            if (reader != null) {
               if (var2 != null) {
                  try {
                     reader.close();
                  } catch (Throwable var12) {
                     var2.addSuppressed(var12);
                  }
               } else {
                  reader.close();
               }
            }

         }

         return var3;
      } catch (IOException var15) {
         throw new IORuntimeException(var15);
      }
   }

   public static byte[] getBodyBytes(ServletRequest request) {
      try {
         return IoUtil.readBytes(request.getInputStream());
      } catch (IOException var2) {
         throw new IORuntimeException(var2);
      }
   }

   public static <T> T fillBean(final ServletRequest request, T bean, CopyOptions copyOptions) {
      final String beanName = StrUtil.lowerFirst(bean.getClass().getSimpleName());
      return BeanUtil.fillBean(bean, new ValueProvider<String>() {
         public Object value(String key, Type valueType) {
            String[] values = request.getParameterValues(key);
            if (ArrayUtil.isEmpty((Object[])values)) {
               values = request.getParameterValues(beanName + "." + key);
               if (ArrayUtil.isEmpty((Object[])values)) {
                  return null;
               }
            }

            return 1 == values.length ? values[0] : values;
         }

         public boolean containsKey(String key) {
            return null != request.getParameter(key) || null != request.getParameter(beanName + "." + key);
         }
      }, copyOptions);
   }

   public static <T> T fillBean(ServletRequest request, T bean, boolean isIgnoreError) {
      return fillBean(request, bean, CopyOptions.create().setIgnoreError(isIgnoreError));
   }

   public static <T> T toBean(ServletRequest request, Class<T> beanClass, boolean isIgnoreError) {
      return fillBean(request, ReflectUtil.newInstanceIfPossible(beanClass), isIgnoreError);
   }

   public static String getClientIP(HttpServletRequest request, String... otherHeaderNames) {
      String[] headers = new String[]{"X-Forwarded-For", "X-Real-IP", "Proxy-Client-IP", "WL-Proxy-Client-IP", "HTTP_CLIENT_IP", "HTTP_X_FORWARDED_FOR"};
      if (ArrayUtil.isNotEmpty((Object[])otherHeaderNames)) {
         headers = (String[])ArrayUtil.addAll(headers, otherHeaderNames);
      }

      return getClientIPByHeader(request, headers);
   }

   public static String getClientIPByHeader(HttpServletRequest request, String... headerNames) {
      String[] var3 = headerNames;
      int var4 = headerNames.length;

      String ip;
      for(int var5 = 0; var5 < var4; ++var5) {
         String header = var3[var5];
         ip = request.getHeader(header);
         if (!NetUtil.isUnknown(ip)) {
            return NetUtil.getMultistageReverseProxyIp(ip);
         }
      }

      ip = request.getRemoteAddr();
      return NetUtil.getMultistageReverseProxyIp(ip);
   }

   public static MultipartFormData getMultipart(ServletRequest request) throws IORuntimeException {
      return getMultipart(request, new UploadSetting());
   }

   public static MultipartFormData getMultipart(ServletRequest request, UploadSetting uploadSetting) throws IORuntimeException {
      MultipartFormData formData = new MultipartFormData(uploadSetting);

      try {
         formData.parseRequestStream(request.getInputStream(), CharsetUtil.charset(request.getCharacterEncoding()));
         return formData;
      } catch (IOException var4) {
         throw new IORuntimeException(var4);
      }
   }

   public static Map<String, String> getHeaderMap(HttpServletRequest request) {
      Map<String, String> headerMap = new HashMap();
      Enumeration<String> names = request.getHeaderNames();

      while(names.hasMoreElements()) {
         String name = (String)names.nextElement();
         headerMap.put(name, request.getHeader(name));
      }

      return headerMap;
   }

   public static String getHeaderIgnoreCase(HttpServletRequest request, String nameIgnoreCase) {
      Enumeration<String> names = request.getHeaderNames();

      String name;
      do {
         if (!names.hasMoreElements()) {
            return null;
         }

         name = (String)names.nextElement();
      } while(name == null || !name.equalsIgnoreCase(nameIgnoreCase));

      return request.getHeader(name);
   }

   public static String getHeader(HttpServletRequest request, String name, String charsetName) {
      return getHeader(request, name, CharsetUtil.charset(charsetName));
   }

   public static String getHeader(HttpServletRequest request, String name, Charset charset) {
      String header = request.getHeader(name);
      return null != header ? CharsetUtil.convert(header, CharsetUtil.CHARSET_ISO_8859_1, charset) : null;
   }

   public static boolean isIE(HttpServletRequest request) {
      String userAgent = getHeaderIgnoreCase(request, "User-Agent");
      if (!StrUtil.isNotBlank(userAgent)) {
         return false;
      } else {
         userAgent = userAgent.toUpperCase();
         return userAgent.contains("MSIE") || userAgent.contains("TRIDENT");
      }
   }

   public static boolean isGetMethod(HttpServletRequest request) {
      return "GET".equalsIgnoreCase(request.getMethod());
   }

   public static boolean isPostMethod(HttpServletRequest request) {
      return "POST".equalsIgnoreCase(request.getMethod());
   }

   public static boolean isMultipart(HttpServletRequest request) {
      if (!isPostMethod(request)) {
         return false;
      } else {
         String contentType = request.getContentType();
         return StrUtil.isBlank(contentType) ? false : contentType.toLowerCase().startsWith("multipart/");
      }
   }

   public static Cookie getCookie(HttpServletRequest httpServletRequest, String name) {
      return (Cookie)readCookieMap(httpServletRequest).get(name);
   }

   public static Map<String, Cookie> readCookieMap(HttpServletRequest httpServletRequest) {
      Cookie[] cookies = httpServletRequest.getCookies();
      return ArrayUtil.isEmpty((Object[])cookies) ? MapUtil.empty() : IterUtil.toMap((Iterator)(new ArrayIter(httpServletRequest.getCookies())), (Map)(new CaseInsensitiveMap()), (Func1)(Cookie::getName));
   }

   public static void addCookie(HttpServletResponse response, Cookie cookie) {
      response.addCookie(cookie);
   }

   public static void addCookie(HttpServletResponse response, String name, String value) {
      response.addCookie(new Cookie(name, value));
   }

   public static void addCookie(HttpServletResponse response, String name, String value, int maxAgeInSeconds, String path, String domain) {
      Cookie cookie = new Cookie(name, value);
      if (domain != null) {
         cookie.setDomain(domain);
      }

      cookie.setMaxAge(maxAgeInSeconds);
      cookie.setPath(path);
      addCookie(response, cookie);
   }

   public static void addCookie(HttpServletResponse response, String name, String value, int maxAgeInSeconds) {
      addCookie(response, name, value, maxAgeInSeconds, "/", (String)null);
   }

   public static PrintWriter getWriter(HttpServletResponse response) throws IORuntimeException {
      try {
         return response.getWriter();
      } catch (IOException var2) {
         throw new IORuntimeException(var2);
      }
   }

   public static void write(HttpServletResponse response, String text, String contentType) {
      response.setContentType(contentType);
      Writer writer = null;

      try {
         writer = response.getWriter();
         writer.write(text);
         writer.flush();
      } catch (IOException var8) {
         throw new UtilException(var8);
      } finally {
         IoUtil.close(writer);
      }

   }

   public static void write(HttpServletResponse response, File file) {
      String fileName = file.getName();
      String contentType = (String)ObjectUtil.defaultIfNull(FileUtil.getMimeType(fileName), (Object)"application/octet-stream");
      BufferedInputStream in = null;

      try {
         in = FileUtil.getInputStream(file);
         write(response, in, contentType, fileName);
      } finally {
         IoUtil.close(in);
      }

   }

   public static void write(HttpServletResponse response, InputStream in, String contentType, String fileName) {
      String charset = (String)ObjectUtil.defaultIfNull(response.getCharacterEncoding(), (Object)"UTF-8");
      response.setHeader("Content-Disposition", StrUtil.format("attachment;filename=\"{}\"", new Object[]{URLUtil.encode(fileName, CharsetUtil.charset(charset))}));
      response.setContentType(contentType);
      write(response, in);
   }

   public static void write(HttpServletResponse response, InputStream in, String contentType) {
      response.setContentType(contentType);
      write(response, in);
   }

   public static void write(HttpServletResponse response, InputStream in) {
      write(response, in, 8192);
   }

   public static void write(HttpServletResponse response, InputStream in, int bufferSize) {
      ServletOutputStream out = null;

      try {
         out = response.getOutputStream();
         IoUtil.copy((InputStream)in, (OutputStream)out, bufferSize);
      } catch (IOException var8) {
         throw new UtilException(var8);
      } finally {
         IoUtil.close(out);
         IoUtil.close(in);
      }

   }

   public static void setHeader(HttpServletResponse response, String name, Object value) {
      if (value instanceof String) {
         response.setHeader(name, (String)value);
      } else if (Date.class.isAssignableFrom(value.getClass())) {
         response.setDateHeader(name, ((Date)value).getTime());
      } else if (!(value instanceof Integer) && !"int".equalsIgnoreCase(value.getClass().getSimpleName())) {
         response.setHeader(name, value.toString());
      } else {
         response.setIntHeader(name, (Integer)value);
      }

   }

   // $FF: synthetic method
   private static Object $deserializeLambda$(SerializedLambda lambda) {
      switch (lambda.getImplMethodName()) {
         case "getName":
            if (lambda.getImplMethodKind() == 5 && lambda.getFunctionalInterfaceClass().equals("cn/hutool/core/lang/func/Func1") && lambda.getFunctionalInterfaceMethodName().equals("call") && lambda.getFunctionalInterfaceMethodSignature().equals("(Ljava/lang/Object;)Ljava/lang/Object;") && lambda.getImplClass().equals("jakarta/servlet/http/Cookie") && lambda.getImplMethodSignature().equals("()Ljava/lang/String;")) {
               return Cookie::getName;
            }
         default:
            throw new IllegalArgumentException("Invalid lambda deserialization");
      }
   }
}
