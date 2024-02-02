/*     */ package org.noear.solon.boot.jlhttp;
/*     */ 
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.net.URI;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.noear.solon.Utils;
/*     */ import org.noear.solon.core.NvMap;
/*     */ import org.noear.solon.core.event.EventBus;
/*     */ import org.noear.solon.core.handle.Context;
/*     */ import org.noear.solon.core.handle.UploadedFile;
/*     */ 
/*     */ public class JlHttpContext extends Context {
/*     */   private HTTPServer.Request _request;
/*     */   private HTTPServer.Response _response;
/*     */   protected Map<String, List<UploadedFile>> _fileMap;
/*     */   private boolean _loadMultipartFormData;
/*     */   private String _ip;
/*     */   private URI _uri;
/*     */   
/*  27 */   public JlHttpContext(HTTPServer.Request request, HTTPServer.Response response) { this._loadMultipartFormData = false;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 395 */     this._status = 200;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 425 */     this._allows_write = true; this._request = request;
/*     */     this._response = response;
/* 427 */     this._fileMap = new HashMap<>(); } private String _url; private NvMap _paramMap; private Map<String, List<String>> _paramsMap; private NvMap _cookieMap; private NvMap _headerMap; private int _status; private boolean _allows_write; private void sendHeaders() throws IOException { if (!this._response.headersSent()) {
/* 428 */       if ("HEAD".equals(method())) {
/* 429 */         this._allows_write = false;
/*     */       }
/*     */       
/* 432 */       if (sessionState() != null) {
/* 433 */         sessionState().sessionPublish();
/*     */       }
/*     */ 
/*     */       
/* 437 */       this._response.sendHeaders(status(), -1L, -1L, null, null, null);
/*     */     }  }
/*     */ 
/*     */   
/*     */   private void loadMultipartFormData() throws IOException {
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
/*     */         this._ip = this._request.getSocket().getInetAddress().getHostAddress(); 
/*     */     } 
/*     */     return this._ip;
/*     */   }
/*     */   
/*     */   public String method() {
/*     */     return this._request.getMethod();
/*     */   }
/*     */   
/*     */   public String protocol() {
/*     */     return this._request.getVersion();
/*     */   }
/*     */   
/*     */   public URI uri() {
/*     */     if (this._uri == null)
/*     */       this._uri = URI.create(url()); 
/*     */     return this._uri;
/*     */   }
/*     */   
/*     */   public String url() {
/*     */     if (this._url == null) {
/*     */       this._url = this._request.getURI().toString();
/*     */       if (this._url != null) {
/*     */         if (this._url.startsWith("/")) {
/*     */           String host = header("Host");
/*     */           if (host == null) {
/*     */             host = header(":authority");
/*     */             String scheme = header(":scheme");
/*     */             if (host == null)
/*     */               host = "localhost"; 
/*     */             if (scheme != null) {
/*     */               this._url = "https://" + host + this._url;
/*     */             } else {
/*     */               this._url = scheme + "://" + host + this._url;
/*     */             } 
/*     */           } else {
/*     */             this._url = "http://" + host + this._url;
/*     */           } 
/*     */         } 
/*     */         int idx = this._url.indexOf("?");
/*     */         if (idx > 0)
/*     */           this._url = this._url.substring(0, idx); 
/*     */       } 
/*     */     } 
/*     */     return this._url;
/*     */   }
/*     */   
/*     */   public long contentLength() {
/*     */     try {
/*     */       return this._request.getBody().available();
/*     */     } catch (Exception ex) {
/*     */       EventBus.push(ex);
/*     */       return 0L;
/*     */     } 
/*     */   }
/*     */   
/*     */   public String contentType() {
/*     */     return header("Content-Type");
/*     */   }
/*     */   
/*     */   public String queryString() {
/*     */     return this._request.getURI().getQuery();
/*     */   }
/*     */   
/*     */   public InputStream bodyAsStream() throws IOException {
/*     */     return this._request.getBody();
/*     */   }
/*     */   
/*     */   public String[] paramValues(String key) {
/*     */     List<String> list = paramsMap().get(key);
/*     */     if (list == null)
/*     */       return null; 
/*     */     return list.<String>toArray(new String[list.size()]);
/*     */   }
/*     */   
/*     */   public String param(String key) {
/*     */     return (String)paramMap().get(key);
/*     */   }
/*     */   
/*     */   public String param(String key, String def) {
/*     */     try {
/*     */       String temp = (String)paramMap().get(key);
/*     */       if (Utils.isEmpty(temp))
/*     */         return def; 
/*     */       return temp;
/*     */     } catch (Exception ex) {
/*     */       EventBus.push(ex);
/*     */       return def;
/*     */     } 
/*     */   }
/*     */   
/*     */   public NvMap paramMap() {
/*     */     if (this._paramMap == null) {
/*     */       this._paramMap = new NvMap();
/*     */       try {
/*     */         if (autoMultipart())
/*     */           loadMultipartFormData(); 
/*     */         this._paramMap.putAll(this._request.getParams());
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
/*     */       try {
/*     */         for (String[] kv : this._request.getParamsList()) {
/*     */           List<String> list = this._paramsMap.get(kv[0]);
/*     */           if (list == null) {
/*     */             list = new ArrayList<>();
/*     */             this._paramsMap.put(kv[0], list);
/*     */           } 
/*     */           list.add(kv[1]);
/*     */         } 
/*     */       } catch (Exception ex) {
/*     */         EventBus.push(ex);
/*     */         return null;
/*     */       } 
/*     */     } 
/*     */     return this._paramsMap;
/*     */   }
/*     */   
/*     */   public List<UploadedFile> files(String key) throws Exception {
/*     */     if (isMultipartFormData()) {
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
/*     */     if (this._cookieMap == null)
/*     */       this._cookieMap = new NvMap(this._request.getHeaders().getParams("Cookie")); 
/*     */     return this._cookieMap;
/*     */   }
/*     */   
/*     */   public NvMap headerMap() {
/*     */     if (this._headerMap == null) {
/*     */       this._headerMap = new NvMap();
/*     */       HTTPServer.Headers headers = this._request.getHeaders();
/*     */       if (headers != null)
/*     */         for (HTTPServer.Header h : headers)
/*     */           this._headerMap.put(h.getName(), h.getValue());  
/*     */     } 
/*     */     return this._headerMap;
/*     */   }
/*     */   
/*     */   public Object response() {
/*     */     return this._response;
/*     */   }
/*     */   
/*     */   protected void contentTypeDoSet(String contentType) {
/*     */     if (this.charset != null && contentType.indexOf(";") < 0) {
/*     */       headerSet("Content-Type", contentType + ";charset=" + this.charset);
/*     */       return;
/*     */     } 
/*     */     headerSet("Content-Type", contentType);
/*     */   }
/*     */   
/*     */   public OutputStream outputStream() throws IOException {
/*     */     sendHeaders();
/*     */     if (this._allows_write)
/*     */       return this._response.getBody(); 
/*     */     return new ByteArrayOutputStream();
/*     */   }
/*     */   
/*     */   public void output(byte[] bytes) {
/*     */     try {
/*     */       OutputStream out = outputStream();
/*     */       if (!this._allows_write)
/*     */         return; 
/*     */       out.write(bytes);
/*     */     } catch (Throwable ex) {
/*     */       throw new RuntimeException(ex);
/*     */     } 
/*     */   }
/*     */   
/*     */   public void output(InputStream stream) {
/*     */     try {
/*     */       OutputStream out = outputStream();
/*     */       if (!this._allows_write)
/*     */         return; 
/*     */       int len = 0;
/*     */       byte[] buf = new byte[512];
/*     */       while ((len = stream.read(buf)) != -1)
/*     */         out.write(buf, 0, len); 
/*     */     } catch (Throwable ex) {
/*     */       throw new RuntimeException(ex);
/*     */     } 
/*     */   }
/*     */   
/*     */   public void headerSet(String key, String val) {
/*     */     this._response.getHeaders().replace(key, val);
/*     */   }
/*     */   
/*     */   public void headerAdd(String key, String val) {
/*     */     this._response.getHeaders().add(key, val);
/*     */   }
/*     */   
/*     */   public void cookieSet(String key, String val, String domain, String path, int maxAge) {
/*     */     StringBuilder sb = new StringBuilder();
/*     */     sb.append(key).append("=").append(val).append(";");
/*     */     if (Utils.isNotEmpty(path))
/*     */       sb.append("path=").append(path).append(";"); 
/*     */     if (maxAge >= 0)
/*     */       sb.append("max-age=").append(maxAge).append(";"); 
/*     */     if (Utils.isNotEmpty(domain))
/*     */       sb.append("domain=").append(domain.toLowerCase()).append(";"); 
/*     */     this._response.getHeaders().add("Set-Cookie", sb.toString());
/*     */   }
/*     */   
/*     */   public void redirect(String url) {
/*     */     redirect(url, 302);
/*     */   }
/*     */   
/*     */   public void redirect(String url, int code) {
/*     */     try {
/*     */       headerSet("Location", url);
/*     */       this._response.sendHeaders(code);
/*     */     } catch (Throwable ex) {
/*     */       throw new RuntimeException(ex);
/*     */     } 
/*     */   }
/*     */   
/*     */   public int status() {
/*     */     return this._status;
/*     */   }
/*     */   
/*     */   protected void statusDoSet(int status) {
/*     */     this._status = status;
/*     */   }
/*     */   
/*     */   public void flush() throws IOException {
/*     */     if (this._allows_write)
/*     */       outputStream().flush(); 
/*     */   }
/*     */   
/*     */   protected void commit() throws IOException {
/*     */     if (!this._response.headersSent())
/*     */       output(""); 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\boot\jlhttp\JlHttpContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */