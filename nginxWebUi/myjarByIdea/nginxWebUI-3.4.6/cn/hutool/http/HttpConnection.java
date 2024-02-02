package cn.hutool.http;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.http.ssl.DefaultSSLInfo;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;

public class HttpConnection {
   private final URL url;
   private final Proxy proxy;
   private HttpURLConnection conn;

   public static HttpConnection create(String urlStr, Proxy proxy) {
      return create(URLUtil.toUrlForHttp(urlStr), proxy);
   }

   public static HttpConnection create(URL url, Proxy proxy) {
      return new HttpConnection(url, proxy);
   }

   public HttpConnection(URL url, Proxy proxy) {
      this.url = url;
      this.proxy = proxy;
      this.initConn();
   }

   public HttpConnection initConn() {
      try {
         this.conn = this.openHttp();
      } catch (IOException var2) {
         throw new HttpException(var2);
      }

      this.conn.setDoInput(true);
      return this;
   }

   public Method getMethod() {
      return Method.valueOf(this.conn.getRequestMethod());
   }

   public HttpConnection setMethod(Method method) {
      if (Method.POST.equals(method) || Method.PUT.equals(method) || Method.PATCH.equals(method) || Method.DELETE.equals(method)) {
         this.conn.setUseCaches(false);
         if (Method.PATCH.equals(method)) {
            HttpGlobalConfig.allowPatch();
         }
      }

      try {
         this.conn.setRequestMethod(method.toString());
         return this;
      } catch (ProtocolException var3) {
         throw new HttpException(var3);
      }
   }

   public URL getUrl() {
      return this.url;
   }

   public Proxy getProxy() {
      return this.proxy;
   }

   public HttpURLConnection getHttpURLConnection() {
      return this.conn;
   }

   public HttpConnection header(String header, String value, boolean isOverride) {
      if (null != this.conn) {
         if (isOverride) {
            this.conn.setRequestProperty(header, value);
         } else {
            this.conn.addRequestProperty(header, value);
         }
      }

      return this;
   }

   public HttpConnection header(Header header, String value, boolean isOverride) {
      return this.header(header.toString(), value, isOverride);
   }

   public HttpConnection header(Map<String, List<String>> headerMap, boolean isOverride) {
      if (MapUtil.isNotEmpty(headerMap)) {
         Iterator var4 = headerMap.entrySet().iterator();

         while(var4.hasNext()) {
            Map.Entry<String, List<String>> entry = (Map.Entry)var4.next();
            String name = (String)entry.getKey();
            Iterator var6 = ((List)entry.getValue()).iterator();

            while(var6.hasNext()) {
               String value = (String)var6.next();
               this.header(name, StrUtil.nullToEmpty(value), isOverride);
            }
         }
      }

      return this;
   }

   public String header(String name) {
      return this.conn.getHeaderField(name);
   }

   public String header(Header name) {
      return this.header(name.toString());
   }

   public Map<String, List<String>> headers() {
      return this.conn.getHeaderFields();
   }

   public HttpConnection setHttpsInfo(HostnameVerifier hostnameVerifier, SSLSocketFactory ssf) throws HttpException {
      HttpURLConnection conn = this.conn;
      if (conn instanceof HttpsURLConnection) {
         HttpsURLConnection httpsConn = (HttpsURLConnection)conn;
         httpsConn.setHostnameVerifier((HostnameVerifier)ObjectUtil.defaultIfNull(hostnameVerifier, (Object)DefaultSSLInfo.TRUST_ANY_HOSTNAME_VERIFIER));
         httpsConn.setSSLSocketFactory((SSLSocketFactory)ObjectUtil.defaultIfNull(ssf, (Object)DefaultSSLInfo.DEFAULT_SSF));
      }

      return this;
   }

   public HttpConnection disableCache() {
      this.conn.setUseCaches(false);
      return this;
   }

   public HttpConnection setConnectTimeout(int timeout) {
      if (timeout > 0 && null != this.conn) {
         this.conn.setConnectTimeout(timeout);
      }

      return this;
   }

   public HttpConnection setReadTimeout(int timeout) {
      if (timeout > 0 && null != this.conn) {
         this.conn.setReadTimeout(timeout);
      }

      return this;
   }

   public HttpConnection setConnectionAndReadTimeout(int timeout) {
      this.setConnectTimeout(timeout);
      this.setReadTimeout(timeout);
      return this;
   }

   public HttpConnection setCookie(String cookie) {
      if (cookie != null) {
         this.header(Header.COOKIE, cookie, true);
      }

      return this;
   }

   public HttpConnection setChunkedStreamingMode(int blockSize) {
      if (blockSize > 0) {
         this.conn.setChunkedStreamingMode(blockSize);
      }

      return this;
   }

   public HttpConnection setInstanceFollowRedirects(boolean isInstanceFollowRedirects) {
      this.conn.setInstanceFollowRedirects(isInstanceFollowRedirects);
      return this;
   }

   public HttpConnection connect() throws IOException {
      if (null != this.conn) {
         this.conn.connect();
      }

      return this;
   }

   public HttpConnection disconnectQuietly() {
      try {
         this.disconnect();
      } catch (Throwable var2) {
      }

      return this;
   }

   public HttpConnection disconnect() {
      if (null != this.conn) {
         this.conn.disconnect();
      }

      return this;
   }

   public InputStream getInputStream() throws IOException {
      return null != this.conn ? this.conn.getInputStream() : null;
   }

   public InputStream getErrorStream() {
      return null != this.conn ? this.conn.getErrorStream() : null;
   }

   public OutputStream getOutputStream() throws IOException {
      if (null == this.conn) {
         throw new IOException("HttpURLConnection has not been initialized.");
      } else {
         Method method = this.getMethod();
         this.conn.setDoOutput(true);
         OutputStream out = this.conn.getOutputStream();
         if (method == Method.GET && method != this.getMethod()) {
            ReflectUtil.setFieldValue(this.conn, (String)"method", Method.GET.name());
         }

         return out;
      }
   }

   public int responseCode() throws IOException {
      return null != this.conn ? this.conn.getResponseCode() : 0;
   }

   public String getCharsetName() {
      return HttpUtil.getCharset(this.conn);
   }

   public Charset getCharset() {
      Charset charset = null;
      String charsetName = this.getCharsetName();
      if (StrUtil.isNotBlank(charsetName)) {
         try {
            charset = Charset.forName(charsetName);
         } catch (UnsupportedCharsetException var4) {
         }
      }

      return charset;
   }

   public String toString() {
      StringBuilder sb = StrUtil.builder();
      sb.append("Request URL: ").append(this.url).append("\r\n");
      sb.append("Request Method: ").append(this.getMethod()).append("\r\n");
      return sb.toString();
   }

   private HttpURLConnection openHttp() throws IOException {
      URLConnection conn = this.openConnection();
      if (!(conn instanceof HttpURLConnection)) {
         throw new HttpException("'{}' of URL [{}] is not a http connection, make sure URL is format for http.", new Object[]{conn.getClass().getName(), this.url});
      } else {
         return (HttpURLConnection)conn;
      }
   }

   private URLConnection openConnection() throws IOException {
      return null == this.proxy ? this.url.openConnection() : this.url.openConnection(this.proxy);
   }
}
