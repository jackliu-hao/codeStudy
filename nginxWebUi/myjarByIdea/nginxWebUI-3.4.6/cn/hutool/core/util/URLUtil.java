package cn.hutool.core.util;

import cn.hutool.core.exceptions.UtilException;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.net.URLDecoder;
import cn.hutool.core.net.URLEncodeUtil;
import cn.hutool.core.net.url.UrlQuery;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.jar.JarFile;

public class URLUtil extends URLEncodeUtil {
   public static final String CLASSPATH_URL_PREFIX = "classpath:";
   public static final String FILE_URL_PREFIX = "file:";
   public static final String JAR_URL_PREFIX = "jar:";
   public static final String WAR_URL_PREFIX = "war:";
   public static final String URL_PROTOCOL_FILE = "file";
   public static final String URL_PROTOCOL_JAR = "jar";
   public static final String URL_PROTOCOL_ZIP = "zip";
   public static final String URL_PROTOCOL_WSJAR = "wsjar";
   public static final String URL_PROTOCOL_VFSZIP = "vfszip";
   public static final String URL_PROTOCOL_VFSFILE = "vfsfile";
   public static final String URL_PROTOCOL_VFS = "vfs";
   public static final String JAR_URL_SEPARATOR = "!/";
   public static final String WAR_URL_SEPARATOR = "*/";

   public static URL url(URI uri) throws UtilException {
      if (null == uri) {
         return null;
      } else {
         try {
            return uri.toURL();
         } catch (MalformedURLException var2) {
            throw new UtilException(var2);
         }
      }
   }

   public static URL url(String url) {
      return url(url, (URLStreamHandler)null);
   }

   public static URL url(String url, URLStreamHandler handler) {
      if (null == url) {
         return null;
      } else if (url.startsWith("classpath:")) {
         url = url.substring("classpath:".length());
         return ClassLoaderUtil.getClassLoader().getResource(url);
      } else {
         try {
            return new URL((URL)null, url, handler);
         } catch (MalformedURLException var5) {
            try {
               return (new File(url)).toURI().toURL();
            } catch (MalformedURLException var4) {
               throw new UtilException(var5);
            }
         }
      }
   }

   public static URI getStringURI(CharSequence content) {
      if (null == content) {
         return null;
      } else {
         String contentStr = StrUtil.addPrefixIfNot(content, "string:///");
         return URI.create(contentStr);
      }
   }

   public static URL toUrlForHttp(String urlStr) {
      return toUrlForHttp(urlStr, (URLStreamHandler)null);
   }

   public static URL toUrlForHttp(String urlStr, URLStreamHandler handler) {
      Assert.notBlank(urlStr, "Url is blank !");
      urlStr = encodeBlank(urlStr);

      try {
         return new URL((URL)null, urlStr, handler);
      } catch (MalformedURLException var3) {
         throw new UtilException(var3);
      }
   }

   public static String encodeBlank(CharSequence urlStr) {
      if (urlStr == null) {
         return null;
      } else {
         int len = urlStr.length();
         StringBuilder sb = new StringBuilder(len);

         for(int i = 0; i < len; ++i) {
            char c = urlStr.charAt(i);
            if (CharUtil.isBlankChar(c)) {
               sb.append("%20");
            } else {
               sb.append(c);
            }
         }

         return sb.toString();
      }
   }

   public static URL getURL(String pathBaseClassLoader) {
      return ResourceUtil.getResource(pathBaseClassLoader);
   }

   public static URL getURL(String path, Class<?> clazz) {
      return ResourceUtil.getResource(path, clazz);
   }

   public static URL getURL(File file) {
      Assert.notNull(file, "File is null !");

      try {
         return file.toURI().toURL();
      } catch (MalformedURLException var2) {
         throw new UtilException(var2, "Error occured when get URL!", new Object[0]);
      }
   }

   public static URL[] getURLs(File... files) {
      URL[] urls = new URL[files.length];

      try {
         for(int i = 0; i < files.length; ++i) {
            urls[i] = files[i].toURI().toURL();
         }

         return urls;
      } catch (MalformedURLException var3) {
         throw new UtilException(var3, "Error occured when get URL!", new Object[0]);
      }
   }

   public static URI getHost(URL url) {
      if (null == url) {
         return null;
      } else {
         try {
            return new URI(url.getProtocol(), url.getHost(), (String)null, (String)null);
         } catch (URISyntaxException var2) {
            throw new UtilException(var2);
         }
      }
   }

   public static String completeUrl(String baseUrl, String relativePath) {
      baseUrl = normalize(baseUrl, false);
      if (StrUtil.isBlank(baseUrl)) {
         return null;
      } else {
         try {
            URL absoluteUrl = new URL(baseUrl);
            URL parseUrl = new URL(absoluteUrl, relativePath);
            return parseUrl.toString();
         } catch (MalformedURLException var4) {
            throw new UtilException(var4);
         }
      }
   }

   public static String decode(String url) throws UtilException {
      return decode(url, "UTF-8");
   }

   public static String decode(String content, Charset charset) {
      return URLDecoder.decode(content, charset);
   }

   public static String decode(String content, Charset charset, boolean isPlusToSpace) {
      return URLDecoder.decode(content, charset, isPlusToSpace);
   }

   public static String decode(String content, String charset) throws UtilException {
      return decode(content, StrUtil.isEmpty(charset) ? null : CharsetUtil.charset(charset));
   }

   public static String getPath(String uriStr) {
      return toURI(uriStr).getPath();
   }

   public static String getDecodedPath(URL url) {
      if (null == url) {
         return null;
      } else {
         String path = null;

         try {
            path = toURI(url).getPath();
         } catch (UtilException var3) {
         }

         return null != path ? path : url.getPath();
      }
   }

   public static URI toURI(URL url) throws UtilException {
      return toURI(url, false);
   }

   public static URI toURI(URL url, boolean isEncode) throws UtilException {
      return null == url ? null : toURI(url.toString(), isEncode);
   }

   public static URI toURI(String location) throws UtilException {
      return toURI(location, false);
   }

   public static URI toURI(String location, boolean isEncode) throws UtilException {
      if (isEncode) {
         location = encode(location);
      }

      try {
         return new URI(StrUtil.trim(location));
      } catch (URISyntaxException var3) {
         throw new UtilException(var3);
      }
   }

   public static boolean isFileURL(URL url) {
      Assert.notNull(url, "URL must be not null");
      String protocol = url.getProtocol();
      return "file".equals(protocol) || "vfsfile".equals(protocol) || "vfs".equals(protocol);
   }

   public static boolean isJarURL(URL url) {
      Assert.notNull(url, "URL must be not null");
      String protocol = url.getProtocol();
      return "jar".equals(protocol) || "zip".equals(protocol) || "vfszip".equals(protocol) || "wsjar".equals(protocol);
   }

   public static boolean isJarFileURL(URL url) {
      Assert.notNull(url, "URL must be not null");
      return "file".equals(url.getProtocol()) && url.getPath().toLowerCase().endsWith(".jar");
   }

   public static InputStream getStream(URL url) {
      Assert.notNull(url, "URL must be not null");

      try {
         return url.openStream();
      } catch (IOException var2) {
         throw new IORuntimeException(var2);
      }
   }

   public static BufferedReader getReader(URL url, Charset charset) {
      return IoUtil.getReader(getStream(url), charset);
   }

   public static JarFile getJarFile(URL url) {
      try {
         JarURLConnection urlConnection = (JarURLConnection)url.openConnection();
         return urlConnection.getJarFile();
      } catch (IOException var2) {
         throw new IORuntimeException(var2);
      }
   }

   public static String normalize(String url) {
      return normalize(url, false);
   }

   public static String normalize(String url, boolean isEncodePath) {
      return normalize(url, isEncodePath, false);
   }

   public static String normalize(String url, boolean isEncodePath, boolean replaceSlash) {
      if (StrUtil.isBlank(url)) {
         return url;
      } else {
         int sepIndex = url.indexOf("://");
         String protocol;
         String body;
         if (sepIndex > 0) {
            protocol = StrUtil.subPre(url, sepIndex + 3);
            body = StrUtil.subSuf(url, sepIndex + 3);
         } else {
            protocol = "http://";
            body = url;
         }

         int paramsSepIndex = StrUtil.indexOf(body, '?');
         String params = null;
         if (paramsSepIndex > 0) {
            params = StrUtil.subSuf(body, paramsSepIndex);
            body = StrUtil.subPre(body, paramsSepIndex);
         }

         if (StrUtil.isNotEmpty(body)) {
            body = body.replaceAll("^[\\\\/]+", "");
            body = body.replace("\\", "/");
            if (replaceSlash) {
               body = body.replaceAll("//+", "/");
            }
         }

         int pathSepIndex = StrUtil.indexOf(body, '/');
         String domain = body;
         String path = null;
         if (pathSepIndex > 0) {
            domain = StrUtil.subPre(body, pathSepIndex);
            path = StrUtil.subSuf(body, pathSepIndex);
         }

         if (isEncodePath) {
            path = encode(path);
         }

         return protocol + domain + StrUtil.nullToEmpty(path) + StrUtil.nullToEmpty(params);
      }
   }

   public static String buildQuery(Map<String, ?> paramMap, Charset charset) {
      return UrlQuery.of(paramMap).build(charset);
   }

   public static long getContentLength(URL url) throws IORuntimeException {
      if (null == url) {
         return -1L;
      } else {
         URLConnection conn = null;

         long var2;
         try {
            conn = url.openConnection();
            var2 = conn.getContentLengthLong();
         } catch (IOException var7) {
            throw new IORuntimeException(var7);
         } finally {
            if (conn instanceof HttpURLConnection) {
               ((HttpURLConnection)conn).disconnect();
            }

         }

         return var2;
      }
   }

   public static String getDataUriBase64(String mimeType, String data) {
      return getDataUri(mimeType, (Charset)null, "base64", data);
   }

   public static String getDataUri(String mimeType, String encoding, String data) {
      return getDataUri(mimeType, (Charset)null, encoding, data);
   }

   public static String getDataUri(String mimeType, Charset charset, String encoding, String data) {
      StringBuilder builder = StrUtil.builder(new CharSequence[]{"data:"});
      if (StrUtil.isNotBlank(mimeType)) {
         builder.append(mimeType);
      }

      if (null != charset) {
         builder.append(";charset=").append(charset.name());
      }

      if (StrUtil.isNotBlank(encoding)) {
         builder.append(';').append(encoding);
      }

      builder.append(',').append(data);
      return builder.toString();
   }
}
