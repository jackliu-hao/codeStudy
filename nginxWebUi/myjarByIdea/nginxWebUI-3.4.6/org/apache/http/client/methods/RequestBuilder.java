package org.apache.http.client.methods;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.apache.http.Consts;
import org.apache.http.Header;
import org.apache.http.HeaderIterator;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.NameValuePair;
import org.apache.http.ProtocolVersion;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.ContentType;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.message.HeaderGroup;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.Args;

public class RequestBuilder {
   private String method;
   private Charset charset;
   private ProtocolVersion version;
   private URI uri;
   private HeaderGroup headerGroup;
   private HttpEntity entity;
   private List<NameValuePair> parameters;
   private RequestConfig config;

   RequestBuilder(String method) {
      this.charset = Consts.UTF_8;
      this.method = method;
   }

   RequestBuilder(String method, URI uri) {
      this.method = method;
      this.uri = uri;
   }

   RequestBuilder(String method, String uri) {
      this.method = method;
      this.uri = uri != null ? URI.create(uri) : null;
   }

   RequestBuilder() {
      this((String)null);
   }

   public static RequestBuilder create(String method) {
      Args.notBlank(method, "HTTP method");
      return new RequestBuilder(method);
   }

   public static RequestBuilder get() {
      return new RequestBuilder("GET");
   }

   public static RequestBuilder get(URI uri) {
      return new RequestBuilder("GET", uri);
   }

   public static RequestBuilder get(String uri) {
      return new RequestBuilder("GET", uri);
   }

   public static RequestBuilder head() {
      return new RequestBuilder("HEAD");
   }

   public static RequestBuilder head(URI uri) {
      return new RequestBuilder("HEAD", uri);
   }

   public static RequestBuilder head(String uri) {
      return new RequestBuilder("HEAD", uri);
   }

   public static RequestBuilder patch() {
      return new RequestBuilder("PATCH");
   }

   public static RequestBuilder patch(URI uri) {
      return new RequestBuilder("PATCH", uri);
   }

   public static RequestBuilder patch(String uri) {
      return new RequestBuilder("PATCH", uri);
   }

   public static RequestBuilder post() {
      return new RequestBuilder("POST");
   }

   public static RequestBuilder post(URI uri) {
      return new RequestBuilder("POST", uri);
   }

   public static RequestBuilder post(String uri) {
      return new RequestBuilder("POST", uri);
   }

   public static RequestBuilder put() {
      return new RequestBuilder("PUT");
   }

   public static RequestBuilder put(URI uri) {
      return new RequestBuilder("PUT", uri);
   }

   public static RequestBuilder put(String uri) {
      return new RequestBuilder("PUT", uri);
   }

   public static RequestBuilder delete() {
      return new RequestBuilder("DELETE");
   }

   public static RequestBuilder delete(URI uri) {
      return new RequestBuilder("DELETE", uri);
   }

   public static RequestBuilder delete(String uri) {
      return new RequestBuilder("DELETE", uri);
   }

   public static RequestBuilder trace() {
      return new RequestBuilder("TRACE");
   }

   public static RequestBuilder trace(URI uri) {
      return new RequestBuilder("TRACE", uri);
   }

   public static RequestBuilder trace(String uri) {
      return new RequestBuilder("TRACE", uri);
   }

   public static RequestBuilder options() {
      return new RequestBuilder("OPTIONS");
   }

   public static RequestBuilder options(URI uri) {
      return new RequestBuilder("OPTIONS", uri);
   }

   public static RequestBuilder options(String uri) {
      return new RequestBuilder("OPTIONS", uri);
   }

   public static RequestBuilder copy(HttpRequest request) {
      Args.notNull(request, "HTTP request");
      return (new RequestBuilder()).doCopy(request);
   }

   private RequestBuilder doCopy(HttpRequest request) {
      if (request == null) {
         return this;
      } else {
         this.method = request.getRequestLine().getMethod();
         this.version = request.getRequestLine().getProtocolVersion();
         if (this.headerGroup == null) {
            this.headerGroup = new HeaderGroup();
         }

         this.headerGroup.clear();
         this.headerGroup.setHeaders(request.getAllHeaders());
         this.parameters = null;
         this.entity = null;
         if (request instanceof HttpEntityEnclosingRequest) {
            HttpEntity originalEntity = ((HttpEntityEnclosingRequest)request).getEntity();
            ContentType contentType = ContentType.get(originalEntity);
            if (contentType != null && contentType.getMimeType().equals(ContentType.APPLICATION_FORM_URLENCODED.getMimeType())) {
               try {
                  List<NameValuePair> formParams = URLEncodedUtils.parse(originalEntity);
                  if (!formParams.isEmpty()) {
                     this.parameters = formParams;
                  }
               } catch (IOException var5) {
               }
            } else {
               this.entity = originalEntity;
            }
         }

         if (request instanceof HttpUriRequest) {
            this.uri = ((HttpUriRequest)request).getURI();
         } else {
            this.uri = URI.create(request.getRequestLine().getUri());
         }

         if (request instanceof Configurable) {
            this.config = ((Configurable)request).getConfig();
         } else {
            this.config = null;
         }

         return this;
      }
   }

   public RequestBuilder setCharset(Charset charset) {
      this.charset = charset;
      return this;
   }

   public Charset getCharset() {
      return this.charset;
   }

   public String getMethod() {
      return this.method;
   }

   public ProtocolVersion getVersion() {
      return this.version;
   }

   public RequestBuilder setVersion(ProtocolVersion version) {
      this.version = version;
      return this;
   }

   public URI getUri() {
      return this.uri;
   }

   public RequestBuilder setUri(URI uri) {
      this.uri = uri;
      return this;
   }

   public RequestBuilder setUri(String uri) {
      this.uri = uri != null ? URI.create(uri) : null;
      return this;
   }

   public Header getFirstHeader(String name) {
      return this.headerGroup != null ? this.headerGroup.getFirstHeader(name) : null;
   }

   public Header getLastHeader(String name) {
      return this.headerGroup != null ? this.headerGroup.getLastHeader(name) : null;
   }

   public Header[] getHeaders(String name) {
      return this.headerGroup != null ? this.headerGroup.getHeaders(name) : null;
   }

   public RequestBuilder addHeader(Header header) {
      if (this.headerGroup == null) {
         this.headerGroup = new HeaderGroup();
      }

      this.headerGroup.addHeader(header);
      return this;
   }

   public RequestBuilder addHeader(String name, String value) {
      if (this.headerGroup == null) {
         this.headerGroup = new HeaderGroup();
      }

      this.headerGroup.addHeader(new BasicHeader(name, value));
      return this;
   }

   public RequestBuilder removeHeader(Header header) {
      if (this.headerGroup == null) {
         this.headerGroup = new HeaderGroup();
      }

      this.headerGroup.removeHeader(header);
      return this;
   }

   public RequestBuilder removeHeaders(String name) {
      if (name != null && this.headerGroup != null) {
         HeaderIterator i = this.headerGroup.iterator();

         while(i.hasNext()) {
            Header header = i.nextHeader();
            if (name.equalsIgnoreCase(header.getName())) {
               i.remove();
            }
         }

         return this;
      } else {
         return this;
      }
   }

   public RequestBuilder setHeader(Header header) {
      if (this.headerGroup == null) {
         this.headerGroup = new HeaderGroup();
      }

      this.headerGroup.updateHeader(header);
      return this;
   }

   public RequestBuilder setHeader(String name, String value) {
      if (this.headerGroup == null) {
         this.headerGroup = new HeaderGroup();
      }

      this.headerGroup.updateHeader(new BasicHeader(name, value));
      return this;
   }

   public HttpEntity getEntity() {
      return this.entity;
   }

   public RequestBuilder setEntity(HttpEntity entity) {
      this.entity = entity;
      return this;
   }

   public List<NameValuePair> getParameters() {
      return this.parameters != null ? new ArrayList(this.parameters) : new ArrayList();
   }

   public RequestBuilder addParameter(NameValuePair nvp) {
      Args.notNull(nvp, "Name value pair");
      if (this.parameters == null) {
         this.parameters = new LinkedList();
      }

      this.parameters.add(nvp);
      return this;
   }

   public RequestBuilder addParameter(String name, String value) {
      return this.addParameter(new BasicNameValuePair(name, value));
   }

   public RequestBuilder addParameters(NameValuePair... nvps) {
      NameValuePair[] arr$ = nvps;
      int len$ = nvps.length;

      for(int i$ = 0; i$ < len$; ++i$) {
         NameValuePair nvp = arr$[i$];
         this.addParameter(nvp);
      }

      return this;
   }

   public RequestConfig getConfig() {
      return this.config;
   }

   public RequestBuilder setConfig(RequestConfig config) {
      this.config = config;
      return this;
   }

   public HttpUriRequest build() {
      URI uriNotNull = this.uri != null ? this.uri : URI.create("/");
      HttpEntity entityCopy = this.entity;
      if (this.parameters != null && !this.parameters.isEmpty()) {
         if (entityCopy != null || !"POST".equalsIgnoreCase(this.method) && !"PUT".equalsIgnoreCase(this.method)) {
            try {
               uriNotNull = (new URIBuilder(uriNotNull)).setCharset(this.charset).addParameters(this.parameters).build();
            } catch (URISyntaxException var5) {
            }
         } else {
            entityCopy = new UrlEncodedFormEntity(this.parameters, this.charset != null ? this.charset : HTTP.DEF_CONTENT_CHARSET);
         }
      }

      Object result;
      if (entityCopy == null) {
         result = new InternalRequest(this.method);
      } else {
         InternalEntityEclosingRequest request = new InternalEntityEclosingRequest(this.method);
         request.setEntity((HttpEntity)entityCopy);
         result = request;
      }

      ((HttpRequestBase)result).setProtocolVersion(this.version);
      ((HttpRequestBase)result).setURI(uriNotNull);
      if (this.headerGroup != null) {
         ((HttpRequestBase)result).setHeaders(this.headerGroup.getAllHeaders());
      }

      ((HttpRequestBase)result).setConfig(this.config);
      return (HttpUriRequest)result;
   }

   public String toString() {
      StringBuilder builder = new StringBuilder();
      builder.append("RequestBuilder [method=");
      builder.append(this.method);
      builder.append(", charset=");
      builder.append(this.charset);
      builder.append(", version=");
      builder.append(this.version);
      builder.append(", uri=");
      builder.append(this.uri);
      builder.append(", headerGroup=");
      builder.append(this.headerGroup);
      builder.append(", entity=");
      builder.append(this.entity);
      builder.append(", parameters=");
      builder.append(this.parameters);
      builder.append(", config=");
      builder.append(this.config);
      builder.append("]");
      return builder.toString();
   }

   static class InternalEntityEclosingRequest extends HttpEntityEnclosingRequestBase {
      private final String method;

      InternalEntityEclosingRequest(String method) {
         this.method = method;
      }

      public String getMethod() {
         return this.method;
      }
   }

   static class InternalRequest extends HttpRequestBase {
      private final String method;

      InternalRequest(String method) {
         this.method = method;
      }

      public String getMethod() {
         return this.method;
      }
   }
}
