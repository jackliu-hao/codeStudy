package org.apache.http.client.config;

import java.net.InetAddress;
import java.util.Collection;
import org.apache.http.HttpHost;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;

@Contract(
   threading = ThreadingBehavior.IMMUTABLE
)
public class RequestConfig implements Cloneable {
   public static final RequestConfig DEFAULT = (new Builder()).build();
   private final boolean expectContinueEnabled;
   private final HttpHost proxy;
   private final InetAddress localAddress;
   private final boolean staleConnectionCheckEnabled;
   private final String cookieSpec;
   private final boolean redirectsEnabled;
   private final boolean relativeRedirectsAllowed;
   private final boolean circularRedirectsAllowed;
   private final int maxRedirects;
   private final boolean authenticationEnabled;
   private final Collection<String> targetPreferredAuthSchemes;
   private final Collection<String> proxyPreferredAuthSchemes;
   private final int connectionRequestTimeout;
   private final int connectTimeout;
   private final int socketTimeout;
   private final boolean contentCompressionEnabled;
   private final boolean normalizeUri;

   protected RequestConfig() {
      this(false, (HttpHost)null, (InetAddress)null, false, (String)null, false, false, false, 0, false, (Collection)null, (Collection)null, 0, 0, 0, true, true);
   }

   RequestConfig(boolean expectContinueEnabled, HttpHost proxy, InetAddress localAddress, boolean staleConnectionCheckEnabled, String cookieSpec, boolean redirectsEnabled, boolean relativeRedirectsAllowed, boolean circularRedirectsAllowed, int maxRedirects, boolean authenticationEnabled, Collection<String> targetPreferredAuthSchemes, Collection<String> proxyPreferredAuthSchemes, int connectionRequestTimeout, int connectTimeout, int socketTimeout, boolean contentCompressionEnabled, boolean normalizeUri) {
      this.expectContinueEnabled = expectContinueEnabled;
      this.proxy = proxy;
      this.localAddress = localAddress;
      this.staleConnectionCheckEnabled = staleConnectionCheckEnabled;
      this.cookieSpec = cookieSpec;
      this.redirectsEnabled = redirectsEnabled;
      this.relativeRedirectsAllowed = relativeRedirectsAllowed;
      this.circularRedirectsAllowed = circularRedirectsAllowed;
      this.maxRedirects = maxRedirects;
      this.authenticationEnabled = authenticationEnabled;
      this.targetPreferredAuthSchemes = targetPreferredAuthSchemes;
      this.proxyPreferredAuthSchemes = proxyPreferredAuthSchemes;
      this.connectionRequestTimeout = connectionRequestTimeout;
      this.connectTimeout = connectTimeout;
      this.socketTimeout = socketTimeout;
      this.contentCompressionEnabled = contentCompressionEnabled;
      this.normalizeUri = normalizeUri;
   }

   public boolean isExpectContinueEnabled() {
      return this.expectContinueEnabled;
   }

   public HttpHost getProxy() {
      return this.proxy;
   }

   public InetAddress getLocalAddress() {
      return this.localAddress;
   }

   /** @deprecated */
   @Deprecated
   public boolean isStaleConnectionCheckEnabled() {
      return this.staleConnectionCheckEnabled;
   }

   public String getCookieSpec() {
      return this.cookieSpec;
   }

   public boolean isRedirectsEnabled() {
      return this.redirectsEnabled;
   }

   public boolean isRelativeRedirectsAllowed() {
      return this.relativeRedirectsAllowed;
   }

   public boolean isCircularRedirectsAllowed() {
      return this.circularRedirectsAllowed;
   }

   public int getMaxRedirects() {
      return this.maxRedirects;
   }

   public boolean isAuthenticationEnabled() {
      return this.authenticationEnabled;
   }

   public Collection<String> getTargetPreferredAuthSchemes() {
      return this.targetPreferredAuthSchemes;
   }

   public Collection<String> getProxyPreferredAuthSchemes() {
      return this.proxyPreferredAuthSchemes;
   }

   public int getConnectionRequestTimeout() {
      return this.connectionRequestTimeout;
   }

   public int getConnectTimeout() {
      return this.connectTimeout;
   }

   public int getSocketTimeout() {
      return this.socketTimeout;
   }

   /** @deprecated */
   @Deprecated
   public boolean isDecompressionEnabled() {
      return this.contentCompressionEnabled;
   }

   public boolean isContentCompressionEnabled() {
      return this.contentCompressionEnabled;
   }

   public boolean isNormalizeUri() {
      return this.normalizeUri;
   }

   protected RequestConfig clone() throws CloneNotSupportedException {
      return (RequestConfig)super.clone();
   }

   public String toString() {
      StringBuilder builder = new StringBuilder();
      builder.append("[");
      builder.append("expectContinueEnabled=").append(this.expectContinueEnabled);
      builder.append(", proxy=").append(this.proxy);
      builder.append(", localAddress=").append(this.localAddress);
      builder.append(", cookieSpec=").append(this.cookieSpec);
      builder.append(", redirectsEnabled=").append(this.redirectsEnabled);
      builder.append(", relativeRedirectsAllowed=").append(this.relativeRedirectsAllowed);
      builder.append(", maxRedirects=").append(this.maxRedirects);
      builder.append(", circularRedirectsAllowed=").append(this.circularRedirectsAllowed);
      builder.append(", authenticationEnabled=").append(this.authenticationEnabled);
      builder.append(", targetPreferredAuthSchemes=").append(this.targetPreferredAuthSchemes);
      builder.append(", proxyPreferredAuthSchemes=").append(this.proxyPreferredAuthSchemes);
      builder.append(", connectionRequestTimeout=").append(this.connectionRequestTimeout);
      builder.append(", connectTimeout=").append(this.connectTimeout);
      builder.append(", socketTimeout=").append(this.socketTimeout);
      builder.append(", contentCompressionEnabled=").append(this.contentCompressionEnabled);
      builder.append(", normalizeUri=").append(this.normalizeUri);
      builder.append("]");
      return builder.toString();
   }

   public static Builder custom() {
      return new Builder();
   }

   public static Builder copy(RequestConfig config) {
      return (new Builder()).setExpectContinueEnabled(config.isExpectContinueEnabled()).setProxy(config.getProxy()).setLocalAddress(config.getLocalAddress()).setStaleConnectionCheckEnabled(config.isStaleConnectionCheckEnabled()).setCookieSpec(config.getCookieSpec()).setRedirectsEnabled(config.isRedirectsEnabled()).setRelativeRedirectsAllowed(config.isRelativeRedirectsAllowed()).setCircularRedirectsAllowed(config.isCircularRedirectsAllowed()).setMaxRedirects(config.getMaxRedirects()).setAuthenticationEnabled(config.isAuthenticationEnabled()).setTargetPreferredAuthSchemes(config.getTargetPreferredAuthSchemes()).setProxyPreferredAuthSchemes(config.getProxyPreferredAuthSchemes()).setConnectionRequestTimeout(config.getConnectionRequestTimeout()).setConnectTimeout(config.getConnectTimeout()).setSocketTimeout(config.getSocketTimeout()).setDecompressionEnabled(config.isDecompressionEnabled()).setContentCompressionEnabled(config.isContentCompressionEnabled()).setNormalizeUri(config.isNormalizeUri());
   }

   public static class Builder {
      private boolean expectContinueEnabled;
      private HttpHost proxy;
      private InetAddress localAddress;
      private boolean staleConnectionCheckEnabled = false;
      private String cookieSpec;
      private boolean redirectsEnabled = true;
      private boolean relativeRedirectsAllowed = true;
      private boolean circularRedirectsAllowed;
      private int maxRedirects = 50;
      private boolean authenticationEnabled = true;
      private Collection<String> targetPreferredAuthSchemes;
      private Collection<String> proxyPreferredAuthSchemes;
      private int connectionRequestTimeout = -1;
      private int connectTimeout = -1;
      private int socketTimeout = -1;
      private boolean contentCompressionEnabled = true;
      private boolean normalizeUri = true;

      Builder() {
      }

      public Builder setExpectContinueEnabled(boolean expectContinueEnabled) {
         this.expectContinueEnabled = expectContinueEnabled;
         return this;
      }

      public Builder setProxy(HttpHost proxy) {
         this.proxy = proxy;
         return this;
      }

      public Builder setLocalAddress(InetAddress localAddress) {
         this.localAddress = localAddress;
         return this;
      }

      /** @deprecated */
      @Deprecated
      public Builder setStaleConnectionCheckEnabled(boolean staleConnectionCheckEnabled) {
         this.staleConnectionCheckEnabled = staleConnectionCheckEnabled;
         return this;
      }

      public Builder setCookieSpec(String cookieSpec) {
         this.cookieSpec = cookieSpec;
         return this;
      }

      public Builder setRedirectsEnabled(boolean redirectsEnabled) {
         this.redirectsEnabled = redirectsEnabled;
         return this;
      }

      public Builder setRelativeRedirectsAllowed(boolean relativeRedirectsAllowed) {
         this.relativeRedirectsAllowed = relativeRedirectsAllowed;
         return this;
      }

      public Builder setCircularRedirectsAllowed(boolean circularRedirectsAllowed) {
         this.circularRedirectsAllowed = circularRedirectsAllowed;
         return this;
      }

      public Builder setMaxRedirects(int maxRedirects) {
         this.maxRedirects = maxRedirects;
         return this;
      }

      public Builder setAuthenticationEnabled(boolean authenticationEnabled) {
         this.authenticationEnabled = authenticationEnabled;
         return this;
      }

      public Builder setTargetPreferredAuthSchemes(Collection<String> targetPreferredAuthSchemes) {
         this.targetPreferredAuthSchemes = targetPreferredAuthSchemes;
         return this;
      }

      public Builder setProxyPreferredAuthSchemes(Collection<String> proxyPreferredAuthSchemes) {
         this.proxyPreferredAuthSchemes = proxyPreferredAuthSchemes;
         return this;
      }

      public Builder setConnectionRequestTimeout(int connectionRequestTimeout) {
         this.connectionRequestTimeout = connectionRequestTimeout;
         return this;
      }

      public Builder setConnectTimeout(int connectTimeout) {
         this.connectTimeout = connectTimeout;
         return this;
      }

      public Builder setSocketTimeout(int socketTimeout) {
         this.socketTimeout = socketTimeout;
         return this;
      }

      /** @deprecated */
      @Deprecated
      public Builder setDecompressionEnabled(boolean decompressionEnabled) {
         this.contentCompressionEnabled = decompressionEnabled;
         return this;
      }

      public Builder setContentCompressionEnabled(boolean contentCompressionEnabled) {
         this.contentCompressionEnabled = contentCompressionEnabled;
         return this;
      }

      public Builder setNormalizeUri(boolean normalizeUri) {
         this.normalizeUri = normalizeUri;
         return this;
      }

      public RequestConfig build() {
         return new RequestConfig(this.expectContinueEnabled, this.proxy, this.localAddress, this.staleConnectionCheckEnabled, this.cookieSpec, this.redirectsEnabled, this.relativeRedirectsAllowed, this.circularRedirectsAllowed, this.maxRedirects, this.authenticationEnabled, this.targetPreferredAuthSchemes, this.proxyPreferredAuthSchemes, this.connectionRequestTimeout, this.connectTimeout, this.socketTimeout, this.contentCompressionEnabled, this.normalizeUri);
      }
   }
}
