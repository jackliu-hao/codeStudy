package org.noear.solon.boot.jlhttp;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.noear.solon.Utils;
import org.noear.solon.core.NvMap;
import org.noear.solon.core.event.EventBus;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.UploadedFile;

public class JlHttpContext extends Context {
   private HTTPServer.Request _request;
   private HTTPServer.Response _response;
   protected Map<String, List<UploadedFile>> _fileMap;
   private boolean _loadMultipartFormData = false;
   private String _ip;
   private URI _uri;
   private String _url;
   private NvMap _paramMap;
   private Map<String, List<String>> _paramsMap;
   private NvMap _cookieMap;
   private NvMap _headerMap;
   private int _status = 200;
   private boolean _allows_write = true;

   public JlHttpContext(HTTPServer.Request request, HTTPServer.Response response) {
      this._request = request;
      this._response = response;
      this._fileMap = new HashMap();
   }

   private void loadMultipartFormData() throws IOException {
      if (!this._loadMultipartFormData) {
         this._loadMultipartFormData = true;
         if (this.isMultipartFormData()) {
            MultipartUtil.buildParamsAndFiles(this);
         }

      }
   }

   public Object request() {
      return this._request;
   }

   public String ip() {
      if (this._ip == null) {
         this._ip = this.header("X-Forwarded-For");
         if (this._ip == null) {
            this._ip = this._request.getSocket().getInetAddress().getHostAddress();
         }
      }

      return this._ip;
   }

   public String method() {
      return this._request.getMethod();
   }

   public String protocol() {
      return this._request.getVersion();
   }

   public URI uri() {
      if (this._uri == null) {
         this._uri = URI.create(this.url());
      }

      return this._uri;
   }

   public String url() {
      if (this._url == null) {
         this._url = this._request.getURI().toString();
         if (this._url != null) {
            if (this._url.startsWith("/")) {
               String host = this.header("Host");
               if (host == null) {
                  host = this.header(":authority");
                  String scheme = this.header(":scheme");
                  if (host == null) {
                     host = "localhost";
                  }

                  if (scheme != null) {
                     this._url = "https://" + host + this._url;
                  } else {
                     this._url = scheme + "://" + host + this._url;
                  }
               } else {
                  this._url = "http://" + host + this._url;
               }
            }

            int idx = this._url.indexOf("?");
            if (idx > 0) {
               this._url = this._url.substring(0, idx);
            }
         }
      }

      return this._url;
   }

   public long contentLength() {
      try {
         return (long)this._request.getBody().available();
      } catch (Exception var2) {
         EventBus.push(var2);
         return 0L;
      }
   }

   public String contentType() {
      return this.header("Content-Type");
   }

   public String queryString() {
      return this._request.getURI().getQuery();
   }

   public InputStream bodyAsStream() throws IOException {
      return this._request.getBody();
   }

   public String[] paramValues(String key) {
      List<String> list = (List)this.paramsMap().get(key);
      return list == null ? null : (String[])list.toArray(new String[list.size()]);
   }

   public String param(String key) {
      return (String)this.paramMap().get(key);
   }

   public String param(String key, String def) {
      try {
         String temp = (String)this.paramMap().get(key);
         return Utils.isEmpty(temp) ? def : temp;
      } catch (Exception var4) {
         EventBus.push(var4);
         return def;
      }
   }

   public NvMap paramMap() {
      if (this._paramMap == null) {
         this._paramMap = new NvMap();

         try {
            if (this.autoMultipart()) {
               this.loadMultipartFormData();
            }

            this._paramMap.putAll(this._request.getParams());
         } catch (RuntimeException var2) {
            throw var2;
         } catch (Throwable var3) {
            throw new RuntimeException(var3);
         }
      }

      return this._paramMap;
   }

   public Map<String, List<String>> paramsMap() {
      if (this._paramsMap == null) {
         this._paramsMap = new LinkedHashMap();

         String[] kv;
         Object list;
         try {
            for(Iterator var1 = this._request.getParamsList().iterator(); var1.hasNext(); ((List)list).add(kv[1])) {
               kv = (String[])var1.next();
               list = (List)this._paramsMap.get(kv[0]);
               if (list == null) {
                  list = new ArrayList();
                  this._paramsMap.put(kv[0], list);
               }
            }
         } catch (Exception var4) {
            EventBus.push(var4);
            return null;
         }
      }

      return this._paramsMap;
   }

   public List<UploadedFile> files(String key) throws Exception {
      if (this.isMultipartFormData()) {
         this.loadMultipartFormData();
         List<UploadedFile> temp = (List)this._fileMap.get(key);
         return (List)(temp == null ? new ArrayList() : temp);
      } else {
         return new ArrayList();
      }
   }

   public NvMap cookieMap() {
      if (this._cookieMap == null) {
         this._cookieMap = new NvMap(this._request.getHeaders().getParams("Cookie"));
      }

      return this._cookieMap;
   }

   public NvMap headerMap() {
      if (this._headerMap == null) {
         this._headerMap = new NvMap();
         HTTPServer.Headers headers = this._request.getHeaders();
         if (headers != null) {
            Iterator var2 = headers.iterator();

            while(var2.hasNext()) {
               HTTPServer.Header h = (HTTPServer.Header)var2.next();
               this._headerMap.put(h.getName(), h.getValue());
            }
         }
      }

      return this._headerMap;
   }

   public Object response() {
      return this._response;
   }

   protected void contentTypeDoSet(String contentType) {
      if (this.charset != null && contentType.indexOf(";") < 0) {
         this.headerSet("Content-Type", contentType + ";charset=" + this.charset);
      } else {
         this.headerSet("Content-Type", contentType);
      }
   }

   public OutputStream outputStream() throws IOException {
      this.sendHeaders();
      return (OutputStream)(this._allows_write ? this._response.getBody() : new ByteArrayOutputStream());
   }

   public void output(byte[] bytes) {
      try {
         OutputStream out = this.outputStream();
         if (this._allows_write) {
            out.write(bytes);
         }
      } catch (Throwable var3) {
         throw new RuntimeException(var3);
      }
   }

   public void output(InputStream stream) {
      try {
         OutputStream out = this.outputStream();
         if (this._allows_write) {
            int len = false;
            byte[] buf = new byte[512];

            int len;
            while((len = stream.read(buf)) != -1) {
               out.write(buf, 0, len);
            }

         }
      } catch (Throwable var5) {
         throw new RuntimeException(var5);
      }
   }

   public void headerSet(String key, String val) {
      this._response.getHeaders().replace(key, val);
   }

   public void headerAdd(String key, String val) {
      this._response.getHeaders().add(key, val);
   }

   public void cookieSet(String key, String val, String domain, String path, int maxAge) {
      StringBuilder sb = new StringBuilder();
      sb.append(key).append("=").append(val).append(";");
      if (Utils.isNotEmpty(path)) {
         sb.append("path=").append(path).append(";");
      }

      if (maxAge >= 0) {
         sb.append("max-age=").append(maxAge).append(";");
      }

      if (Utils.isNotEmpty(domain)) {
         sb.append("domain=").append(domain.toLowerCase()).append(";");
      }

      this._response.getHeaders().add("Set-Cookie", sb.toString());
   }

   public void redirect(String url) {
      this.redirect(url, 302);
   }

   public void redirect(String url, int code) {
      try {
         this.headerSet("Location", url);
         this._response.sendHeaders(code);
      } catch (Throwable var4) {
         throw new RuntimeException(var4);
      }
   }

   public int status() {
      return this._status;
   }

   protected void statusDoSet(int status) {
      this._status = status;
   }

   public void flush() throws IOException {
      if (this._allows_write) {
         this.outputStream().flush();
      }

   }

   protected void commit() throws IOException {
      if (!this._response.headersSent()) {
         this.output((String)"");
      }

   }

   private void sendHeaders() throws IOException {
      if (!this._response.headersSent()) {
         if ("HEAD".equals(this.method())) {
            this._allows_write = false;
         }

         if (this.sessionState() != null) {
            this.sessionState().sessionPublish();
         }

         this._response.sendHeaders(this.status(), -1L, -1L, (String)null, (String)null, (long[])null);
      }

   }
}
