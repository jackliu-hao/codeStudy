package cn.hutool.http;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.io.StreamProgress;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.net.RFC3986;
import cn.hutool.core.net.url.UrlQuery;
import cn.hutool.core.text.StrBuilder;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.http.cookie.GlobalCookieManager;
import cn.hutool.http.server.SimpleServer;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.CookieManager;
import java.net.HttpURLConnection;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class HttpUtil {
   public static final Pattern CHARSET_PATTERN = Pattern.compile("charset\\s*=\\s*([a-z0-9-]*)", 2);
   public static final Pattern META_CHARSET_PATTERN = Pattern.compile("<meta[^>]*?charset\\s*=\\s*['\"]?([a-z0-9-]*)", 2);

   public static boolean isHttps(String url) {
      return url.toLowerCase().startsWith("https:");
   }

   public static boolean isHttp(String url) {
      return url.toLowerCase().startsWith("http:");
   }

   public static HttpRequest createRequest(Method method, String url) {
      return HttpRequest.of(url).method(method);
   }

   public static HttpRequest createGet(String url) {
      return createGet(url, false);
   }

   public static HttpRequest createGet(String url, boolean isFollowRedirects) {
      return HttpRequest.get(url).setFollowRedirects(isFollowRedirects);
   }

   public static HttpRequest createPost(String url) {
      return HttpRequest.post(url);
   }

   public static String get(String urlString, Charset customCharset) {
      return ((HttpRequest)HttpRequest.get(urlString).charset(customCharset)).execute().body();
   }

   public static String get(String urlString) {
      return get(urlString, HttpGlobalConfig.getTimeout());
   }

   public static String get(String urlString, int timeout) {
      return HttpRequest.get(urlString).timeout(timeout).execute().body();
   }

   public static String get(String urlString, Map<String, Object> paramMap) {
      return HttpRequest.get(urlString).form(paramMap).execute().body();
   }

   public static String get(String urlString, Map<String, Object> paramMap, int timeout) {
      return HttpRequest.get(urlString).form(paramMap).timeout(timeout).execute().body();
   }

   public static String post(String urlString, Map<String, Object> paramMap) {
      return post(urlString, paramMap, HttpGlobalConfig.getTimeout());
   }

   public static String post(String urlString, Map<String, Object> paramMap, int timeout) {
      return HttpRequest.post(urlString).form(paramMap).timeout(timeout).execute().body();
   }

   public static String post(String urlString, String body) {
      return post(urlString, body, HttpGlobalConfig.getTimeout());
   }

   public static String post(String urlString, String body, int timeout) {
      return HttpRequest.post(urlString).timeout(timeout).body(body).execute().body();
   }

   public static String downloadString(String url, String customCharsetName) {
      return downloadString(url, CharsetUtil.charset(customCharsetName), (StreamProgress)null);
   }

   public static String downloadString(String url, Charset customCharset) {
      return downloadString(url, customCharset, (StreamProgress)null);
   }

   public static String downloadString(String url, Charset customCharset, StreamProgress streamPress) {
      return HttpDownloader.downloadString(url, customCharset, streamPress);
   }

   public static long downloadFile(String url, String dest) {
      return downloadFile(url, FileUtil.file(dest));
   }

   public static long downloadFile(String url, File destFile) {
      return downloadFile(url, destFile, (StreamProgress)null);
   }

   public static long downloadFile(String url, File destFile, int timeout) {
      return downloadFile(url, destFile, timeout, (StreamProgress)null);
   }

   public static long downloadFile(String url, File destFile, StreamProgress streamProgress) {
      return downloadFile(url, destFile, -1, streamProgress);
   }

   public static long downloadFile(String url, File destFile, int timeout, StreamProgress streamProgress) {
      return HttpDownloader.downloadFile(url, destFile, timeout, streamProgress);
   }

   public static File downloadFileFromUrl(String url, String dest) {
      return downloadFileFromUrl(url, FileUtil.file(dest));
   }

   public static File downloadFileFromUrl(String url, File destFile) {
      return downloadFileFromUrl(url, destFile, (StreamProgress)null);
   }

   public static File downloadFileFromUrl(String url, File destFile, int timeout) {
      return downloadFileFromUrl(url, destFile, timeout, (StreamProgress)null);
   }

   public static File downloadFileFromUrl(String url, File destFile, StreamProgress streamProgress) {
      return downloadFileFromUrl(url, destFile, -1, streamProgress);
   }

   public static File downloadFileFromUrl(String url, File destFile, int timeout, StreamProgress streamProgress) {
      return HttpDownloader.downloadForFile(url, destFile, timeout, streamProgress);
   }

   public static long download(String url, OutputStream out, boolean isCloseOut) {
      return download(url, out, isCloseOut, (StreamProgress)null);
   }

   public static long download(String url, OutputStream out, boolean isCloseOut, StreamProgress streamProgress) {
      return HttpDownloader.download(url, out, isCloseOut, streamProgress);
   }

   public static byte[] downloadBytes(String url) {
      return HttpDownloader.downloadBytes(url);
   }

   public static String toParams(Map<String, ?> paramMap) {
      return toParams(paramMap, CharsetUtil.CHARSET_UTF_8);
   }

   /** @deprecated */
   @Deprecated
   public static String toParams(Map<String, Object> paramMap, String charsetName) {
      return toParams(paramMap, CharsetUtil.charset(charsetName));
   }

   public static String toParams(Map<String, ?> paramMap, Charset charset) {
      return toParams(paramMap, charset, false);
   }

   public static String toParams(Map<String, ?> paramMap, Charset charset, boolean isFormUrlEncoded) {
      return UrlQuery.of(paramMap, isFormUrlEncoded).build(charset);
   }

   public static String encodeParams(String urlWithParams, Charset charset) {
      if (StrUtil.isBlank(urlWithParams)) {
         return "";
      } else {
         String urlPart = null;
         int pathEndPos = urlWithParams.indexOf(63);
         String paramPart;
         if (pathEndPos > -1) {
            urlPart = StrUtil.subPre(urlWithParams, pathEndPos);
            paramPart = StrUtil.subSuf(urlWithParams, pathEndPos + 1);
            if (StrUtil.isBlank(paramPart)) {
               return urlPart;
            }
         } else {
            if (!StrUtil.contains(urlWithParams, '=')) {
               return urlWithParams;
            }

            paramPart = urlWithParams;
         }

         paramPart = normalizeParams(paramPart, charset);
         return StrUtil.isBlank(urlPart) ? paramPart : urlPart + "?" + paramPart;
      }
   }

   public static String normalizeParams(String paramPart, Charset charset) {
      if (StrUtil.isEmpty(paramPart)) {
         return paramPart;
      } else {
         StrBuilder builder = StrBuilder.create(paramPart.length() + 16);
         int len = paramPart.length();
         String name = null;
         int pos = 0;

         int i;
         for(i = 0; i < len; ++i) {
            char c = paramPart.charAt(i);
            if (c == '=') {
               if (null == name) {
                  name = pos == i ? "" : paramPart.substring(pos, i);
                  pos = i + 1;
               }
            } else if (c == '&') {
               if (pos != i) {
                  if (null == name) {
                     name = paramPart.substring(pos, i);
                     builder.append((CharSequence)RFC3986.QUERY_PARAM_NAME.encode(name, charset)).append('=');
                  } else {
                     builder.append((CharSequence)RFC3986.QUERY_PARAM_NAME.encode(name, charset)).append('=').append((CharSequence)RFC3986.QUERY_PARAM_VALUE.encode(paramPart.substring(pos, i), charset)).append('&');
                  }

                  name = null;
               }

               pos = i + 1;
            }
         }

         if (null != name) {
            builder.append((CharSequence)URLUtil.encodeQuery(name, charset)).append('=');
         }

         if (pos != i) {
            if (null == name && pos > 0) {
               builder.append('=');
            }

            builder.append((CharSequence)URLUtil.encodeQuery(paramPart.substring(pos, i), charset));
         }

         int lastIndex = builder.length() - 1;
         if ('&' == builder.charAt(lastIndex)) {
            builder.delTo(lastIndex);
         }

         return builder.toString();
      }
   }

   public static Map<String, String> decodeParamMap(String paramsStr, Charset charset) {
      Map<CharSequence, CharSequence> queryMap = UrlQuery.of(paramsStr, charset).getQueryMap();
      return MapUtil.isEmpty(queryMap) ? MapUtil.empty() : Convert.toMap(String.class, String.class, queryMap);
   }

   public static Map<String, List<String>> decodeParams(String paramsStr, String charset) {
      return decodeParams(paramsStr, CharsetUtil.charset(charset));
   }

   public static Map<String, List<String>> decodeParams(String paramsStr, Charset charset) {
      Map<CharSequence, CharSequence> queryMap = UrlQuery.of(paramsStr, charset).getQueryMap();
      if (MapUtil.isEmpty(queryMap)) {
         return MapUtil.empty();
      } else {
         Map<String, List<String>> params = new LinkedHashMap();
         queryMap.forEach((key, value) -> {
            List<String> values = (List)params.computeIfAbsent(StrUtil.str(key), (k) -> {
               return new ArrayList(1);
            });
            values.add(StrUtil.str(value));
         });
         return params;
      }
   }

   public static String urlWithForm(String url, Map<String, Object> form, Charset charset, boolean isEncodeParams) {
      if (isEncodeParams && StrUtil.contains(url, '?')) {
         url = encodeParams(url, charset);
      }

      return urlWithForm(url, toParams(form, charset), charset, false);
   }

   public static String urlWithForm(String url, String queryString, Charset charset, boolean isEncode) {
      if (StrUtil.isBlank(queryString)) {
         if (StrUtil.contains(url, '?')) {
            return isEncode ? encodeParams(url, charset) : url;
         } else {
            return url;
         }
      } else {
         StrBuilder urlBuilder = StrBuilder.create(url.length() + queryString.length() + 16);
         int qmIndex = url.indexOf(63);
         if (qmIndex > 0) {
            urlBuilder.append((CharSequence)(isEncode ? encodeParams(url, charset) : url));
            if (!StrUtil.endWith(url, '&')) {
               urlBuilder.append('&');
            }
         } else {
            urlBuilder.append((CharSequence)url);
            if (qmIndex < 0) {
               urlBuilder.append('?');
            }
         }

         urlBuilder.append((CharSequence)(isEncode ? encodeParams(queryString, charset) : queryString));
         return urlBuilder.toString();
      }
   }

   public static String getCharset(HttpURLConnection conn) {
      return conn == null ? null : getCharset(conn.getContentType());
   }

   public static String getCharset(String contentType) {
      return StrUtil.isBlank(contentType) ? null : ReUtil.get((Pattern)CHARSET_PATTERN, contentType, 1);
   }

   public static String getString(InputStream in, Charset charset, boolean isGetCharsetFromContent) {
      byte[] contentBytes = IoUtil.readBytes(in);
      return getString(contentBytes, charset, isGetCharsetFromContent);
   }

   public static String getString(byte[] contentBytes, Charset charset, boolean isGetCharsetFromContent) {
      if (null == contentBytes) {
         return null;
      } else {
         if (null == charset) {
            charset = CharsetUtil.CHARSET_UTF_8;
         }

         String content = new String(contentBytes, charset);
         if (isGetCharsetFromContent) {
            String charsetInContentStr = ReUtil.get((Pattern)META_CHARSET_PATTERN, content, 1);
            if (StrUtil.isNotBlank(charsetInContentStr)) {
               Charset charsetInContent = null;

               try {
                  charsetInContent = Charset.forName(charsetInContentStr);
               } catch (Exception var7) {
                  if (!StrUtil.containsIgnoreCase(charsetInContentStr, "utf-8") && !StrUtil.containsIgnoreCase(charsetInContentStr, "utf8")) {
                     if (StrUtil.containsIgnoreCase(charsetInContentStr, "gbk")) {
                        charsetInContent = CharsetUtil.CHARSET_GBK;
                     }
                  } else {
                     charsetInContent = CharsetUtil.CHARSET_UTF_8;
                  }
               }

               if (null != charsetInContent && !charset.equals(charsetInContent)) {
                  content = new String(contentBytes, charsetInContent);
               }
            }
         }

         return content;
      }
   }

   public static String getMimeType(String filePath, String defaultValue) {
      return (String)ObjectUtil.defaultIfNull(getMimeType(filePath), (Object)defaultValue);
   }

   public static String getMimeType(String filePath) {
      return FileUtil.getMimeType(filePath);
   }

   public static String getContentTypeByRequestBody(String body) {
      ContentType contentType = ContentType.get(body);
      return null == contentType ? null : contentType.toString();
   }

   public static SimpleServer createServer(int port) {
      return new SimpleServer(port);
   }

   public static String buildBasicAuth(String username, String password, Charset charset) {
      String data = username.concat(":").concat(password);
      return "Basic " + Base64.encode(data, (Charset)charset);
   }

   public static void closeCookie() {
      GlobalCookieManager.setCookieManager((CookieManager)null);
   }
}
