package cn.hutool.http;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.net.SSLUtil;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Proxy.Type;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;

public class HttpConfig {
   int connectionTimeout = HttpGlobalConfig.getTimeout();
   int readTimeout = HttpGlobalConfig.getTimeout();
   boolean isDisableCache;
   int maxRedirectCount = HttpGlobalConfig.getMaxRedirectCount();
   Proxy proxy;
   HostnameVerifier hostnameVerifier;
   SSLSocketFactory ssf;
   int blockSize;
   boolean ignoreEOFError = HttpGlobalConfig.isIgnoreEOFError();
   boolean decodeUrl = HttpGlobalConfig.isDecodeUrl();
   final HttpInterceptor.Chain<HttpRequest> requestInterceptors;
   final HttpInterceptor.Chain<HttpResponse> responseInterceptors;
   boolean interceptorOnRedirect;

   public HttpConfig() {
      this.requestInterceptors = GlobalInterceptor.INSTANCE.getCopiedRequestInterceptor();
      this.responseInterceptors = GlobalInterceptor.INSTANCE.getCopiedResponseInterceptor();
   }

   public static HttpConfig create() {
      return new HttpConfig();
   }

   public HttpConfig timeout(int milliseconds) {
      this.setConnectionTimeout(milliseconds);
      this.setReadTimeout(milliseconds);
      return this;
   }

   public HttpConfig setConnectionTimeout(int milliseconds) {
      this.connectionTimeout = milliseconds;
      return this;
   }

   public HttpConfig setReadTimeout(int milliseconds) {
      this.readTimeout = milliseconds;
      return this;
   }

   public HttpConfig disableCache() {
      this.isDisableCache = true;
      return this;
   }

   public HttpConfig setMaxRedirectCount(int maxRedirectCount) {
      this.maxRedirectCount = Math.max(maxRedirectCount, 0);
      return this;
   }

   public HttpConfig setHostnameVerifier(HostnameVerifier hostnameVerifier) {
      this.hostnameVerifier = hostnameVerifier;
      return this;
   }

   public HttpConfig setHttpProxy(String host, int port) {
      Proxy proxy = new Proxy(Type.HTTP, new InetSocketAddress(host, port));
      return this.setProxy(proxy);
   }

   public HttpConfig setProxy(Proxy proxy) {
      this.proxy = proxy;
      return this;
   }

   public HttpConfig setSSLSocketFactory(SSLSocketFactory ssf) {
      this.ssf = ssf;
      return this;
   }

   public HttpConfig setSSLProtocol(String protocol) {
      Assert.notBlank(protocol, "protocol must be not blank!");
      this.setSSLSocketFactory(SSLUtil.createSSLContext(protocol).getSocketFactory());
      return this;
   }

   public HttpConfig setBlockSize(int blockSize) {
      this.blockSize = blockSize;
      return this;
   }

   public HttpConfig setIgnoreEOFError(boolean ignoreEOFError) {
      this.ignoreEOFError = ignoreEOFError;
      return this;
   }

   public HttpConfig setDecodeUrl(boolean decodeUrl) {
      this.decodeUrl = decodeUrl;
      return this;
   }

   public HttpConfig addRequestInterceptor(HttpInterceptor<HttpRequest> interceptor) {
      this.requestInterceptors.addChain(interceptor);
      return this;
   }

   public HttpConfig addResponseInterceptor(HttpInterceptor<HttpResponse> interceptor) {
      this.responseInterceptors.addChain(interceptor);
      return this;
   }

   public HttpConfig setInterceptorOnRedirect(boolean interceptorOnRedirect) {
      this.interceptorOnRedirect = interceptorOnRedirect;
      return this;
   }
}
