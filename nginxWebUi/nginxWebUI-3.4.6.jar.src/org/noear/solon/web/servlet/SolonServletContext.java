/*     */ package org.noear.solon.web.servlet;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.net.URI;
/*     */ import java.nio.charset.Charset;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Enumeration;
/*     */ import java.util.HashMap;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import javax.servlet.ServletException;
/*     */ import javax.servlet.http.Cookie;
/*     */ import javax.servlet.http.HttpServletRequest;
/*     */ import javax.servlet.http.HttpServletResponse;
/*     */ import org.noear.solon.Utils;
/*     */ import org.noear.solon.core.NvMap;
/*     */ import org.noear.solon.core.handle.Context;
/*     */ import org.noear.solon.core.handle.UploadedFile;
/*     */ 
/*     */ public class SolonServletContext extends Context {
/*     */   private HttpServletRequest _request;
/*     */   private HttpServletResponse _response;
/*     */   protected Map<String, List<UploadedFile>> _fileMap;
/*     */   private boolean _loadMultipartFormData;
/*     */   private String _ip;
/*     */   private URI _uri;
/*     */   private String _url;
/*     */   private NvMap _paramMap;
/*     */   private Map<String, List<String>> _paramsMap;
/*     */   private NvMap _cookieMap;
/*     */   private NvMap _headerMap;
/*     */   private boolean _headers_sent;
/*     */   
/*  38 */   public SolonServletContext(HttpServletRequest request, HttpServletResponse response) { this._loadMultipartFormData = false;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 362 */     this._headers_sent = false; this._request = request; this._response = response;
/*     */     this._fileMap = new HashMap<>();
/*     */     if (sessionState().replaceable())
/* 365 */       this.sessionState = new SolonServletSessionState(request);  } private void sendHeaders() throws IOException { if (!this._headers_sent) {
/* 366 */       this._headers_sent = true;
/*     */       
/* 368 */       if (sessionState() != null)
/* 369 */         sessionState().sessionPublish(); 
/*     */     }  }
/*     */ 
/*     */   
/*     */   private void loadMultipartFormData() throws IOException, ServletException {
/*     */     if (this._loadMultipartFormData)
/*     */       return; 
/*     */     this._loadMultipartFormData = true;
/*     */     if (isMultipartFormData())
/*     */       MultipartUtil.buildParamsAndFiles(this); 
/*     */   }
/*     */   
/*     */   public Object request() {
/*     */     return this._request;
/*     */   }
/*     */   
/*     */   public String ip() {
/*     */     if (this._ip == null) {
/*     */       this._ip = header("X-Forwarded-For");
/*     */       if (this._ip == null)
/*     */         this._ip = this._request.getRemoteAddr(); 
/*     */     } 
/*     */     return this._ip;
/*     */   }
/*     */   
/*     */   public String method() {
/*     */     return this._request.getMethod();
/*     */   }
/*     */   
/*     */   public String protocol() {
/*     */     return this._request.getProtocol();
/*     */   }
/*     */   
/*     */   public URI uri() {
/*     */     if (this._uri == null)
/*     */       this._uri = URI.create(url()); 
/*     */     return this._uri;
/*     */   }
/*     */   
/*     */   public String url() {
/*     */     if (this._url == null)
/*     */       this._url = this._request.getRequestURL().toString(); 
/*     */     return this._url;
/*     */   }
/*     */   
/*     */   public long contentLength() {
/*     */     return this._request.getContentLength();
/*     */   }
/*     */   
/*     */   public String contentType() {
/*     */     return this._request.getContentType();
/*     */   }
/*     */   
/*     */   public String queryString() {
/*     */     return this._request.getQueryString();
/*     */   }
/*     */   
/*     */   public InputStream bodyAsStream() throws IOException {
/*     */     return (InputStream)this._request.getInputStream();
/*     */   }
/*     */   
/*     */   public String[] paramValues(String key) {
/*     */     return this._request.getParameterValues(key);
/*     */   }
/*     */   
/*     */   public String param(String key) {
/*     */     return (String)paramMap().get(key);
/*     */   }
/*     */   
/*     */   public String param(String key, String def) {
/*     */     String temp = (String)paramMap().get(key);
/*     */     if (Utils.isEmpty(temp))
/*     */       return def; 
/*     */     return temp;
/*     */   }
/*     */   
/*     */   public NvMap paramMap() {
/*     */     if (this._paramMap == null) {
/*     */       this._paramMap = new NvMap();
/*     */       try {
/*     */         if (autoMultipart())
/*     */           loadMultipartFormData(); 
/*     */         Enumeration<String> names = this._request.getParameterNames();
/*     */         while (names.hasMoreElements()) {
/*     */           String name = names.nextElement();
/*     */           String value = this._request.getParameter(name);
/*     */           this._paramMap.put(name, value);
/*     */         } 
/*     */       } catch (RuntimeException e) {
/*     */         throw e;
/*     */       } catch (Throwable e) {
/*     */         throw new RuntimeException(e);
/*     */       } 
/*     */     } 
/*     */     return this._paramMap;
/*     */   }
/*     */   
/*     */   public Map<String, List<String>> paramsMap() {
/*     */     if (this._paramsMap == null) {
/*     */       this._paramsMap = new LinkedHashMap<>();
/*     */       this._request.getParameterMap().forEach((k, v) -> this._paramsMap.put(k, Arrays.asList(v)));
/*     */     } 
/*     */     return this._paramsMap;
/*     */   }
/*     */   
/*     */   public List<UploadedFile> files(String key) throws Exception {
/*     */     if (this._fileMap != null && isMultipartFormData()) {
/*     */       loadMultipartFormData();
/*     */       List<UploadedFile> temp = this._fileMap.get(key);
/*     */       if (temp == null)
/*     */         return new ArrayList<>(); 
/*     */       return temp;
/*     */     } 
/*     */     return new ArrayList<>();
/*     */   }
/*     */   
/*     */   public NvMap cookieMap() {
/*     */     if (this._cookieMap == null) {
/*     */       this._cookieMap = new NvMap();
/*     */       Cookie[] _cookies = this._request.getCookies();
/*     */       if (_cookies != null)
/*     */         for (Cookie c : _cookies)
/*     */           this._cookieMap.put(c.getName(), c.getValue());  
/*     */     } 
/*     */     return this._cookieMap;
/*     */   }
/*     */   
/*     */   public NvMap headerMap() {
/*     */     if (this._headerMap == null) {
/*     */       this._headerMap = new NvMap();
/*     */       Enumeration<String> headers = this._request.getHeaderNames();
/*     */       while (headers.hasMoreElements()) {
/*     */         String key = headers.nextElement();
/*     */         String value = this._request.getHeader(key);
/*     */         this._headerMap.put(key, value);
/*     */       } 
/*     */     } 
/*     */     return this._headerMap;
/*     */   }
/*     */   
/*     */   public Object response() {
/*     */     return this._response;
/*     */   }
/*     */   
/*     */   public void charset(String charset) {
/*     */     this._response.setCharacterEncoding(charset);
/*     */     this.charset = Charset.forName(charset);
/*     */   }
/*     */   
/*     */   protected void contentTypeDoSet(String contentType) {
/*     */     this._response.setContentType(contentType);
/*     */   }
/*     */   
/*     */   public OutputStream outputStream() throws IOException {
/*     */     sendHeaders();
/*     */     return (OutputStream)this._response.getOutputStream();
/*     */   }
/*     */   
/*     */   public void output(byte[] bytes) {
/*     */     try {
/*     */       OutputStream out = outputStream();
/*     */       out.write(bytes);
/*     */     } catch (Throwable ex) {
/*     */       throw new RuntimeException(ex);
/*     */     } 
/*     */   }
/*     */   
/*     */   public void output(InputStream stream) {
/*     */     try {
/*     */       OutputStream out = outputStream();
/*     */       byte[] buff = new byte[100];
/*     */       int rc = 0;
/*     */       while ((rc = stream.read(buff, 0, 100)) > 0)
/*     */         out.write(buff, 0, rc); 
/*     */     } catch (Throwable ex) {
/*     */       throw new RuntimeException(ex);
/*     */     } 
/*     */   }
/*     */   
/*     */   public void headerSet(String key, String val) {
/*     */     this._response.setHeader(key, val);
/*     */   }
/*     */   
/*     */   public void headerAdd(String key, String val) {
/*     */     this._response.addHeader(key, val);
/*     */   }
/*     */   
/*     */   public void cookieSet(String key, String val, String domain, String path, int maxAge) {
/*     */     Cookie c = new Cookie(key, val);
/*     */     if (Utils.isNotEmpty(path))
/*     */       c.setPath(path); 
/*     */     if (maxAge > 0)
/*     */       c.setMaxAge(maxAge); 
/*     */     if (Utils.isNotEmpty(domain))
/*     */       c.setDomain(domain); 
/*     */     this._response.addCookie(c);
/*     */   }
/*     */   
/*     */   public void redirect(String url) {
/*     */     try {
/*     */       this._response.sendRedirect(url);
/*     */     } catch (Throwable ex) {
/*     */       throw new RuntimeException(ex);
/*     */     } 
/*     */   }
/*     */   
/*     */   public void redirect(String url, int code) {
/*     */     statusDoSet(code);
/*     */     this._response.setHeader("Location", url);
/*     */   }
/*     */   
/*     */   public int status() {
/*     */     return this._response.getStatus();
/*     */   }
/*     */   
/*     */   protected void statusDoSet(int status) {
/*     */     this._response.setStatus(status);
/*     */   }
/*     */   
/*     */   public void flush() throws IOException {
/*     */     outputStream().flush();
/*     */   }
/*     */   
/*     */   protected void commit() throws IOException {
/*     */     sendHeaders();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\web\servlet\SolonServletContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */