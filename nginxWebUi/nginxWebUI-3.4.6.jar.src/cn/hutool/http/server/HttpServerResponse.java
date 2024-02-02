/*     */ package cn.hutool.http.server;
/*     */ 
/*     */ import cn.hutool.core.io.FileUtil;
/*     */ import cn.hutool.core.io.IORuntimeException;
/*     */ import cn.hutool.core.io.IoUtil;
/*     */ import cn.hutool.core.util.ObjectUtil;
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import cn.hutool.core.util.URLUtil;
/*     */ import cn.hutool.http.ContentType;
/*     */ import cn.hutool.http.Header;
/*     */ import cn.hutool.http.HttpUtil;
/*     */ import com.sun.net.httpserver.Headers;
/*     */ import com.sun.net.httpserver.HttpExchange;
/*     */ import java.io.BufferedInputStream;
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.io.OutputStreamWriter;
/*     */ import java.io.PrintWriter;
/*     */ import java.nio.charset.Charset;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class HttpServerResponse
/*     */   extends HttpServerBase
/*     */ {
/*     */   private Charset charset;
/*     */   private boolean isSendCode;
/*     */   
/*     */   public HttpServerResponse(HttpExchange httpExchange) {
/*  45 */     super(httpExchange);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HttpServerResponse send(int httpStatusCode) {
/*  55 */     return send(httpStatusCode, 0L);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HttpServerResponse sendOk() {
/*  64 */     return send(200);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HttpServerResponse sendOk(int bodyLength) {
/*  75 */     return send(200, bodyLength);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HttpServerResponse send404(String content) {
/*  85 */     return sendError(404, content);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HttpServerResponse sendError(int errorCode, String content) {
/*  96 */     send(errorCode);
/*  97 */     setContentType(ContentType.TEXT_HTML.toString());
/*  98 */     return write(content);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HttpServerResponse send(int httpStatusCode, long bodyLength) {
/* 109 */     if (this.isSendCode) {
/* 110 */       throw new IORuntimeException("Http status code has been send!");
/*     */     }
/*     */     
/*     */     try {
/* 114 */       this.httpExchange.sendResponseHeaders(httpStatusCode, bodyLength);
/* 115 */     } catch (IOException e) {
/* 116 */       throw new IORuntimeException(e);
/*     */     } 
/*     */     
/* 119 */     this.isSendCode = true;
/* 120 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Headers getHeaders() {
/* 129 */     return this.httpExchange.getResponseHeaders();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HttpServerResponse addHeader(String header, String value) {
/* 140 */     getHeaders().add(header, value);
/* 141 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HttpServerResponse setHeader(Header header, String value) {
/* 152 */     return setHeader(header.getValue(), value);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HttpServerResponse setHeader(String header, String value) {
/* 163 */     getHeaders().set(header, value);
/* 164 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HttpServerResponse setHeader(String header, List<String> value) {
/* 175 */     getHeaders().put(header, value);
/* 176 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HttpServerResponse setHeaders(Map<String, List<String>> headers) {
/* 186 */     getHeaders().putAll(headers);
/* 187 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HttpServerResponse setContentType(String contentType) {
/* 198 */     if (null != contentType && null != this.charset && false == 
/* 199 */       contentType.contains(";charset=")) {
/* 200 */       contentType = ContentType.build(contentType, this.charset);
/*     */     }
/*     */ 
/*     */     
/* 204 */     return setHeader(Header.CONTENT_TYPE, contentType);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HttpServerResponse setContentLength(long contentLength) {
/* 214 */     return setHeader(Header.CONTENT_LENGTH, String.valueOf(contentLength));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HttpServerResponse setCharset(Charset charset) {
/* 224 */     this.charset = charset;
/* 225 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HttpServerResponse setAttr(String name, Object value) {
/* 236 */     this.httpExchange.setAttribute(name, value);
/* 237 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public OutputStream getOut() {
/* 246 */     if (false == this.isSendCode) {
/* 247 */       sendOk();
/*     */     }
/* 249 */     return this.httpExchange.getResponseBody();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public PrintWriter getWriter() {
/* 258 */     Charset charset = (Charset)ObjectUtil.defaultIfNull(this.charset, DEFAULT_CHARSET);
/* 259 */     return new PrintWriter(new OutputStreamWriter(getOut(), charset));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HttpServerResponse write(String data, String contentType) {
/* 270 */     setContentType(contentType);
/* 271 */     return write(data);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HttpServerResponse write(String data) {
/* 281 */     Charset charset = (Charset)ObjectUtil.defaultIfNull(this.charset, DEFAULT_CHARSET);
/* 282 */     return write(StrUtil.bytes(data, charset));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HttpServerResponse write(byte[] data, String contentType) {
/* 293 */     setContentType(contentType);
/* 294 */     return write(data);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HttpServerResponse write(byte[] data) {
/* 304 */     ByteArrayInputStream in = new ByteArrayInputStream(data);
/* 305 */     return write(in, in.available());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HttpServerResponse write(InputStream in, String contentType) {
/* 317 */     return write(in, 0, contentType);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HttpServerResponse write(InputStream in, int length, String contentType) {
/* 330 */     setContentType(contentType);
/* 331 */     return write(in, length);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HttpServerResponse write(InputStream in) {
/* 341 */     return write(in, 0);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HttpServerResponse write(InputStream in, int length) {
/* 352 */     if (false == this.isSendCode) {
/* 353 */       sendOk(Math.max(0, length));
/*     */     }
/* 355 */     OutputStream out = null;
/*     */     try {
/* 357 */       out = this.httpExchange.getResponseBody();
/* 358 */       IoUtil.copy(in, out);
/*     */     } finally {
/* 360 */       IoUtil.close(out);
/* 361 */       IoUtil.close(in);
/*     */     } 
/* 363 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HttpServerResponse write(File file) {
/* 374 */     return write(file, (String)null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HttpServerResponse write(File file, String fileName) {
/* 386 */     long fileSize = file.length();
/* 387 */     if (fileSize > 2147483647L) {
/* 388 */       throw new IllegalArgumentException("File size is too bigger than 2147483647");
/*     */     }
/*     */     
/* 391 */     if (StrUtil.isBlank(fileName)) {
/* 392 */       fileName = file.getName();
/*     */     }
/* 394 */     String contentType = (String)ObjectUtil.defaultIfNull(HttpUtil.getMimeType(fileName), "application/octet-stream");
/* 395 */     BufferedInputStream in = null;
/*     */     try {
/* 397 */       in = FileUtil.getInputStream(file);
/* 398 */       write(in, (int)fileSize, contentType, fileName);
/*     */     } finally {
/* 400 */       IoUtil.close(in);
/*     */     } 
/* 402 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void write(InputStream in, String contentType, String fileName) {
/* 414 */     write(in, 0, contentType, fileName);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HttpServerResponse write(InputStream in, int length, String contentType, String fileName) {
/* 428 */     Charset charset = (Charset)ObjectUtil.defaultIfNull(this.charset, DEFAULT_CHARSET);
/*     */     
/* 430 */     if (false == contentType.startsWith("text/"))
/*     */     {
/* 432 */       setHeader(Header.CONTENT_DISPOSITION, StrUtil.format("attachment;filename={}", new Object[] { URLUtil.encode(fileName, charset) }));
/*     */     }
/* 434 */     return write(in, length, contentType);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\http\server\HttpServerResponse.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */