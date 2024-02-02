package org.apache.http.impl.client;

import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.HttpVersion;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.client.protocol.RequestAddCookies;
import org.apache.http.client.protocol.RequestAuthCache;
import org.apache.http.client.protocol.RequestClientConnControl;
import org.apache.http.client.protocol.RequestDefaultHeaders;
import org.apache.http.client.protocol.RequestProxyAuthentication;
import org.apache.http.client.protocol.RequestTargetAuthentication;
import org.apache.http.client.protocol.ResponseProcessCookies;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.params.SyncBasicHttpParams;
import org.apache.http.protocol.BasicHttpProcessor;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.RequestContent;
import org.apache.http.protocol.RequestExpectContinue;
import org.apache.http.protocol.RequestTargetHost;
import org.apache.http.protocol.RequestUserAgent;
import org.apache.http.util.VersionInfo;

/** @deprecated */
@Deprecated
@Contract(
   threading = ThreadingBehavior.SAFE_CONDITIONAL
)
public class DefaultHttpClient extends AbstractHttpClient {
   public DefaultHttpClient(ClientConnectionManager conman, HttpParams params) {
      super(conman, params);
   }

   public DefaultHttpClient(ClientConnectionManager conman) {
      super(conman, (HttpParams)null);
   }

   public DefaultHttpClient(HttpParams params) {
      super((ClientConnectionManager)null, params);
   }

   public DefaultHttpClient() {
      super((ClientConnectionManager)null, (HttpParams)null);
   }

   protected HttpParams createHttpParams() {
      HttpParams params = new SyncBasicHttpParams();
      setDefaultHttpParams(params);
      return params;
   }

   public static void setDefaultHttpParams(HttpParams params) {
      HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
      HttpProtocolParams.setContentCharset(params, HTTP.DEF_CONTENT_CHARSET.name());
      HttpConnectionParams.setTcpNoDelay(params, true);
      HttpConnectionParams.setSocketBufferSize(params, 8192);
      HttpProtocolParams.setUserAgent(params, VersionInfo.getUserAgent("Apache-HttpClient", "org.apache.http.client", DefaultHttpClient.class));
   }

   protected BasicHttpProcessor createHttpProcessor() {
      BasicHttpProcessor httpproc = new BasicHttpProcessor();
      httpproc.addInterceptor((HttpRequestInterceptor)(new RequestDefaultHeaders()));
      httpproc.addInterceptor((HttpRequestInterceptor)(new RequestContent()));
      httpproc.addInterceptor((HttpRequestInterceptor)(new RequestTargetHost()));
      httpproc.addInterceptor((HttpRequestInterceptor)(new RequestClientConnControl()));
      httpproc.addInterceptor((HttpRequestInterceptor)(new RequestUserAgent()));
      httpproc.addInterceptor((HttpRequestInterceptor)(new RequestExpectContinue()));
      httpproc.addInterceptor((HttpRequestInterceptor)(new RequestAddCookies()));
      httpproc.addInterceptor((HttpResponseInterceptor)(new ResponseProcessCookies()));
      httpproc.addInterceptor((HttpRequestInterceptor)(new RequestAuthCache()));
      httpproc.addInterceptor((HttpRequestInterceptor)(new RequestTargetAuthentication()));
      httpproc.addInterceptor((HttpRequestInterceptor)(new RequestProxyAuthentication()));
      return httpproc;
   }
}
