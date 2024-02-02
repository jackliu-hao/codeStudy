package org.noear.solon.web.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.noear.solon.Utils;
import org.noear.solon.core.NvMap;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.UploadedFile;

public class SolonServletContext extends Context {
   private HttpServletRequest _request;
   private HttpServletResponse _response;
   protected Map<String, List<UploadedFile>> _fileMap;
   private boolean _loadMultipartFormData = false;
   private String _ip;
   private URI _uri;
   private String _url;
   private NvMap _paramMap;
   private Map<String, List<String>> _paramsMap;
   private NvMap _cookieMap;
   private NvMap _headerMap;
   private boolean _headers_sent = false;

   public SolonServletContext(HttpServletRequest request, HttpServletResponse response) {
      this._request = request;
      this._response = response;
      this._fileMap = new HashMap();
      if (this.sessionState().replaceable()) {
         this.sessionState = new SolonServletSessionState(request);
      }

   }

   private void loadMultipartFormData() throws IOException, ServletException {
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
            this._ip = this._request.getRemoteAddr();
         }
      }

      return this._ip;
   }

   public String method() {
      return this._request.getMethod();
   }

   public String protocol() {
      return this._request.getProtocol();
   }

   public URI uri() {
      if (this._uri == null) {
         this._uri = URI.create(this.url());
      }

      return this._uri;
   }

   public String url() {
      if (this._url == null) {
         this._url = this._request.getRequestURL().toString();
      }

      return this._url;
   }

   public long contentLength() {
      return (long)this._request.getContentLength();
   }

   public String contentType() {
      return this._request.getContentType();
   }

   public String queryString() {
      return this._request.getQueryString();
   }

   public InputStream bodyAsStream() throws IOException {
      return this._request.getInputStream();
   }

   public String[] paramValues(String key) {
      return this._request.getParameterValues(key);
   }

   public String param(String key) {
      return (String)this.paramMap().get(key);
   }

   public String param(String key, String def) {
      String temp = (String)this.paramMap().get(key);
      return Utils.isEmpty(temp) ? def : temp;
   }

   public NvMap paramMap() {
      if (this._paramMap == null) {
         this._paramMap = new NvMap();

         try {
            if (this.autoMultipart()) {
               this.loadMultipartFormData();
            }

            Enumeration<String> names = this._request.getParameterNames();

            while(names.hasMoreElements()) {
               String name = (String)names.nextElement();
               String value = this._request.getParameter(name);
               this._paramMap.put(name, value);
            }
         } catch (RuntimeException var4) {
            throw var4;
         } catch (Throwable var5) {
            throw new RuntimeException(var5);
         }
      }

      return this._paramMap;
   }

   public Map<String, List<String>> paramsMap() {
      if (this._paramsMap == null) {
         this._paramsMap = new LinkedHashMap();
         this._request.getParameterMap().forEach((k, v) -> {
            this._paramsMap.put(k, Arrays.asList(v));
         });
      }

      return this._paramsMap;
   }

   public List<UploadedFile> files(String key) throws Exception {
      if (this._fileMap != null && this.isMultipartFormData()) {
         this.loadMultipartFormData();
         List<UploadedFile> temp = (List)this._fileMap.get(key);
         return (List)(temp == null ? new ArrayList() : temp);
      } else {
         return new ArrayList();
      }
   }

   public NvMap cookieMap() {
      if (this._cookieMap == null) {
         this._cookieMap = new NvMap();
         Cookie[] _cookies = this._request.getCookies();
         if (_cookies != null) {
            Cookie[] var2 = _cookies;
            int var3 = _cookies.length;

            for(int var4 = 0; var4 < var3; ++var4) {
               Cookie c = var2[var4];
               this._cookieMap.put(c.getName(), c.getValue());
            }
         }
      }

      return this._cookieMap;
   }

   public NvMap headerMap() {
      if (this._headerMap == null) {
         this._headerMap = new NvMap();
         Enumeration<String> headers = this._request.getHeaderNames();

         while(headers.hasMoreElements()) {
            String key = (String)headers.nextElement();
            String value = this._request.getHeader(key);
            this._headerMap.put(key, value);
         }
      }

      return this._headerMap;
   }

   public Object response() {
      return this._response;
   }

   public void charset(String charset) {
      this._response.setCharacterEncoding(charset);
      this.charset = Charset.forName(charset);
   }

   protected void contentTypeDoSet(String contentType) {
      this._response.setContentType(contentType);
   }

   public OutputStream outputStream() throws IOException {
      this.sendHeaders();
      return this._response.getOutputStream();
   }

   public void output(byte[] bytes) {
      try {
         OutputStream out = this.outputStream();
         out.write(bytes);
      } catch (Throwable var3) {
         throw new RuntimeException(var3);
      }
   }

   public void output(InputStream stream) {
      try {
         OutputStream out = this.outputStream();
         byte[] buff = new byte[100];
         int rc = false;

         int rc;
         while((rc = stream.read(buff, 0, 100)) > 0) {
            out.write(buff, 0, rc);
         }

      } catch (Throwable var5) {
         throw new RuntimeException(var5);
      }
   }

   public void headerSet(String key, String val) {
      this._response.setHeader(key, val);
   }

   public void headerAdd(String key, String val) {
      this._response.addHeader(key, val);
   }

   public void cookieSet(String key, String val, String domain, String path, int maxAge) {
      Cookie c = new Cookie(key, val);
      if (Utils.isNotEmpty(path)) {
         c.setPath(path);
      }

      if (maxAge > 0) {
         c.setMaxAge(maxAge);
      }

      if (Utils.isNotEmpty(domain)) {
         c.setDomain(domain);
      }

      this._response.addCookie(c);
   }

   public void redirect(String url) {
      try {
         this._response.sendRedirect(url);
      } catch (Throwable var3) {
         throw new RuntimeException(var3);
      }
   }

   public void redirect(String url, int code) {
      this.statusDoSet(code);
      this._response.setHeader("Location", url);
   }

   public int status() {
      return this._response.getStatus();
   }

   protected void statusDoSet(int status) {
      this._response.setStatus(status);
   }

   public void flush() throws IOException {
      this.outputStream().flush();
   }

   protected void commit() throws IOException {
      this.sendHeaders();
   }

   private void sendHeaders() throws IOException {
      if (!this._headers_sent) {
         this._headers_sent = true;
         if (this.sessionState() != null) {
            this.sessionState().sessionPublish();
         }
      }

   }
}
