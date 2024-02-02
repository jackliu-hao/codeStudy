package org.apache.http.client.params;

import java.net.InetAddress;
import java.util.Collection;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.params.HttpParams;

/** @deprecated */
@Deprecated
public final class HttpClientParamConfig {
   private HttpClientParamConfig() {
   }

   public static RequestConfig getRequestConfig(HttpParams params) {
      return getRequestConfig(params, RequestConfig.DEFAULT);
   }

   public static RequestConfig getRequestConfig(HttpParams params, RequestConfig defaultConfig) {
      RequestConfig.Builder builder = RequestConfig.copy(defaultConfig).setSocketTimeout(params.getIntParameter("http.socket.timeout", defaultConfig.getSocketTimeout())).setStaleConnectionCheckEnabled(params.getBooleanParameter("http.connection.stalecheck", defaultConfig.isStaleConnectionCheckEnabled())).setConnectTimeout(params.getIntParameter("http.connection.timeout", defaultConfig.getConnectTimeout())).setExpectContinueEnabled(params.getBooleanParameter("http.protocol.expect-continue", defaultConfig.isExpectContinueEnabled())).setAuthenticationEnabled(params.getBooleanParameter("http.protocol.handle-authentication", defaultConfig.isAuthenticationEnabled())).setCircularRedirectsAllowed(params.getBooleanParameter("http.protocol.allow-circular-redirects", defaultConfig.isCircularRedirectsAllowed())).setConnectionRequestTimeout((int)params.getLongParameter("http.conn-manager.timeout", (long)defaultConfig.getConnectionRequestTimeout())).setMaxRedirects(params.getIntParameter("http.protocol.max-redirects", defaultConfig.getMaxRedirects())).setRedirectsEnabled(params.getBooleanParameter("http.protocol.handle-redirects", defaultConfig.isRedirectsEnabled())).setRelativeRedirectsAllowed(!params.getBooleanParameter("http.protocol.reject-relative-redirect", !defaultConfig.isRelativeRedirectsAllowed()));
      HttpHost proxy = (HttpHost)params.getParameter("http.route.default-proxy");
      if (proxy != null) {
         builder.setProxy(proxy);
      }

      InetAddress localAddress = (InetAddress)params.getParameter("http.route.local-address");
      if (localAddress != null) {
         builder.setLocalAddress(localAddress);
      }

      Collection<String> targetAuthPrefs = (Collection)params.getParameter("http.auth.target-scheme-pref");
      if (targetAuthPrefs != null) {
         builder.setTargetPreferredAuthSchemes(targetAuthPrefs);
      }

      Collection<String> proxySuthPrefs = (Collection)params.getParameter("http.auth.proxy-scheme-pref");
      if (proxySuthPrefs != null) {
         builder.setProxyPreferredAuthSchemes(proxySuthPrefs);
      }

      String cookiePolicy = (String)params.getParameter("http.protocol.cookie-policy");
      if (cookiePolicy != null) {
         builder.setCookieSpec(cookiePolicy);
      }

      return builder.build();
   }
}
