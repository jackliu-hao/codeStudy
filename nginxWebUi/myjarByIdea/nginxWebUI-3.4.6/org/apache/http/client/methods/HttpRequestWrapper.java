package org.apache.http.client.methods;

import java.net.URI;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.ProtocolVersion;
import org.apache.http.RequestLine;
import org.apache.http.message.AbstractHttpMessage;
import org.apache.http.message.BasicRequestLine;
import org.apache.http.params.HttpParams;
import org.apache.http.util.Args;

public class HttpRequestWrapper extends AbstractHttpMessage implements HttpUriRequest {
   private final HttpRequest original;
   private final HttpHost target;
   private final String method;
   private RequestLine requestLine;
   private ProtocolVersion version;
   private URI uri;

   private HttpRequestWrapper(HttpRequest request, HttpHost target) {
      this.original = (HttpRequest)Args.notNull(request, "HTTP request");
      this.target = target;
      this.version = this.original.getRequestLine().getProtocolVersion();
      this.method = this.original.getRequestLine().getMethod();
      if (request instanceof HttpUriRequest) {
         this.uri = ((HttpUriRequest)request).getURI();
      } else {
         this.uri = null;
      }

      this.setHeaders(request.getAllHeaders());
   }

   public ProtocolVersion getProtocolVersion() {
      return this.version != null ? this.version : this.original.getProtocolVersion();
   }

   public void setProtocolVersion(ProtocolVersion version) {
      this.version = version;
      this.requestLine = null;
   }

   public URI getURI() {
      return this.uri;
   }

   public void setURI(URI uri) {
      this.uri = uri;
      this.requestLine = null;
   }

   public String getMethod() {
      return this.method;
   }

   public void abort() throws UnsupportedOperationException {
      throw new UnsupportedOperationException();
   }

   public boolean isAborted() {
      return false;
   }

   public RequestLine getRequestLine() {
      if (this.requestLine == null) {
         String requestUri;
         if (this.uri != null) {
            requestUri = this.uri.toASCIIString();
         } else {
            requestUri = this.original.getRequestLine().getUri();
         }

         if (requestUri == null || requestUri.isEmpty()) {
            requestUri = "/";
         }

         this.requestLine = new BasicRequestLine(this.method, requestUri, this.getProtocolVersion());
      }

      return this.requestLine;
   }

   public HttpRequest getOriginal() {
      return this.original;
   }

   public HttpHost getTarget() {
      return this.target;
   }

   public String toString() {
      return this.getRequestLine() + " " + this.headergroup;
   }

   public static HttpRequestWrapper wrap(HttpRequest request) {
      return wrap(request, (HttpHost)null);
   }

   public static HttpRequestWrapper wrap(HttpRequest request, HttpHost target) {
      Args.notNull(request, "HTTP request");
      return (HttpRequestWrapper)(request instanceof HttpEntityEnclosingRequest ? new HttpEntityEnclosingRequestWrapper((HttpEntityEnclosingRequest)request, target) : new HttpRequestWrapper(request, target));
   }

   /** @deprecated */
   @Deprecated
   public HttpParams getParams() {
      if (this.params == null) {
         this.params = this.original.getParams().copy();
      }

      return this.params;
   }

   // $FF: synthetic method
   HttpRequestWrapper(HttpRequest x0, HttpHost x1, Object x2) {
      this(x0, x1);
   }

   static class HttpEntityEnclosingRequestWrapper extends HttpRequestWrapper implements HttpEntityEnclosingRequest {
      private HttpEntity entity;

      HttpEntityEnclosingRequestWrapper(HttpEntityEnclosingRequest request, HttpHost target) {
         super(request, target, null);
         this.entity = request.getEntity();
      }

      public HttpEntity getEntity() {
         return this.entity;
      }

      public void setEntity(HttpEntity entity) {
         this.entity = entity;
      }

      public boolean expectContinue() {
         Header expect = this.getFirstHeader("Expect");
         return expect != null && "100-continue".equalsIgnoreCase(expect.getValue());
      }
   }
}
