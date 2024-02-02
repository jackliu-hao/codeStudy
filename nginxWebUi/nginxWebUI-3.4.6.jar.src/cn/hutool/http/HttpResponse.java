/*     */ package cn.hutool.http;
/*     */ 
/*     */ import cn.hutool.core.convert.Convert;
/*     */ import cn.hutool.core.io.FastByteArrayOutputStream;
/*     */ import cn.hutool.core.io.FileUtil;
/*     */ import cn.hutool.core.io.IORuntimeException;
/*     */ import cn.hutool.core.io.IoUtil;
/*     */ import cn.hutool.core.io.StreamProgress;
/*     */ import cn.hutool.core.lang.Assert;
/*     */ import cn.hutool.core.util.ReUtil;
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import cn.hutool.core.util.URLUtil;
/*     */ import cn.hutool.http.cookie.GlobalCookieManager;
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.Closeable;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.net.HttpCookie;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class HttpResponse
/*     */   extends HttpBase<HttpResponse>
/*     */   implements Closeable
/*     */ {
/*     */   protected HttpConfig config;
/*     */   protected HttpConnection httpConnection;
/*     */   protected InputStream in;
/*     */   private volatile boolean isAsync;
/*     */   protected int status;
/*     */   private final boolean ignoreBody;
/*     */   private Charset charsetFromResponse;
/*     */   
/*     */   protected HttpResponse(HttpConnection httpConnection, HttpConfig config, Charset charset, boolean isAsync, boolean isIgnoreBody) {
/*  76 */     this.httpConnection = httpConnection;
/*  77 */     this.config = config;
/*  78 */     this.charset = charset;
/*  79 */     this.isAsync = isAsync;
/*  80 */     this.ignoreBody = isIgnoreBody;
/*  81 */     initWithDisconnect();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getStatus() {
/*  90 */     return this.status;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isOk() {
/* 100 */     return (this.status >= 200 && this.status < 300);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HttpResponse sync() {
/* 111 */     return this.isAsync ? forceSync() : this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String contentEncoding() {
/* 122 */     return header(Header.CONTENT_ENCODING);
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
/*     */   
/*     */   public long contentLength() {
/* 137 */     long contentLength = Convert.toLong(header(Header.CONTENT_LENGTH), Long.valueOf(-1L)).longValue();
/* 138 */     if (contentLength > 0L && (isChunked() || StrUtil.isNotBlank(contentEncoding())))
/*     */     {
/* 140 */       contentLength = -1L;
/*     */     }
/* 142 */     return contentLength;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isGzip() {
/* 151 */     String contentEncoding = contentEncoding();
/* 152 */     return "gzip".equalsIgnoreCase(contentEncoding);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isDeflate() {
/* 162 */     String contentEncoding = contentEncoding();
/* 163 */     return "deflate".equalsIgnoreCase(contentEncoding);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isChunked() {
/* 173 */     String transferEncoding = header(Header.TRANSFER_ENCODING);
/* 174 */     return "Chunked".equalsIgnoreCase(transferEncoding);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getCookieStr() {
/* 184 */     return header(Header.SET_COOKIE);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public List<HttpCookie> getCookies() {
/* 194 */     return GlobalCookieManager.getCookies(this.httpConnection);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HttpCookie getCookie(String name) {
/* 205 */     List<HttpCookie> cookie = getCookies();
/* 206 */     if (null != cookie) {
/* 207 */       for (HttpCookie httpCookie : cookie) {
/* 208 */         if (httpCookie.getName().equals(name)) {
/* 209 */           return httpCookie;
/*     */         }
/*     */       } 
/*     */     }
/* 213 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getCookieValue(String name) {
/* 224 */     HttpCookie cookie = getCookie(name);
/* 225 */     return (null == cookie) ? null : cookie.getValue();
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
/*     */   
/*     */   public InputStream bodyStream() {
/* 240 */     if (this.isAsync) {
/* 241 */       return this.in;
/*     */     }
/* 243 */     return new ByteArrayInputStream(this.bodyBytes);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public byte[] bodyBytes() {
/* 254 */     sync();
/* 255 */     return this.bodyBytes;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String body() throws HttpException {
/* 265 */     return HttpUtil.getString(bodyBytes(), this.charset, (null == this.charsetFromResponse));
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
/*     */   
/*     */   public long writeBody(OutputStream out, boolean isCloseOut, StreamProgress streamProgress) {
/* 280 */     Assert.notNull(out, "[out] must be not null!", new Object[0]);
/* 281 */     long contentLength = contentLength();
/*     */     try {
/* 283 */       return copyBody(bodyStream(), out, contentLength, streamProgress, this.config.ignoreEOFError);
/*     */     } finally {
/* 285 */       IoUtil.close(this);
/* 286 */       if (isCloseOut) {
/* 287 */         IoUtil.close(out);
/*     */       }
/*     */     } 
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
/*     */   public long writeBody(File targetFileOrDir, StreamProgress streamProgress) {
/* 303 */     Assert.notNull(targetFileOrDir, "[targetFileOrDir] must be not null!", new Object[0]);
/*     */     
/* 305 */     File outFile = completeFileNameFromHeader(targetFileOrDir);
/* 306 */     return writeBody(FileUtil.getOutputStream(outFile), true, streamProgress);
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
/*     */ 
/*     */   
/*     */   public long writeBody(File targetFileOrDir, String tempFileSuffix, StreamProgress streamProgress) {
/*     */     long length;
/* 323 */     Assert.notNull(targetFileOrDir, "[targetFileOrDir] must be not null!", new Object[0]);
/*     */     
/* 325 */     File outFile = completeFileNameFromHeader(targetFileOrDir);
/*     */     
/* 327 */     if (StrUtil.isBlank(tempFileSuffix)) {
/* 328 */       tempFileSuffix = ".temp";
/*     */     } else {
/* 330 */       tempFileSuffix = StrUtil.addPrefixIfNot(tempFileSuffix, ".");
/*     */     } 
/*     */ 
/*     */     
/* 334 */     String fileName = outFile.getName();
/*     */     
/* 336 */     String tempFileName = fileName + tempFileSuffix;
/*     */ 
/*     */     
/* 339 */     outFile = new File(outFile.getParentFile(), tempFileName);
/*     */ 
/*     */     
/*     */     try {
/* 343 */       length = writeBody(outFile, streamProgress);
/*     */       
/* 345 */       FileUtil.rename(outFile, fileName, true);
/* 346 */     } catch (Throwable e) {
/*     */       
/* 348 */       FileUtil.del(outFile);
/* 349 */       throw new HttpException(e);
/*     */     } 
/* 351 */     return length;
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
/*     */   public File writeBodyForFile(File targetFileOrDir, StreamProgress streamProgress) {
/* 365 */     Assert.notNull(targetFileOrDir, "[targetFileOrDir] must be not null!", new Object[0]);
/*     */     
/* 367 */     File outFile = completeFileNameFromHeader(targetFileOrDir);
/* 368 */     writeBody(FileUtil.getOutputStream(outFile), true, streamProgress);
/*     */     
/* 370 */     return outFile;
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
/*     */   public long writeBody(File targetFileOrDir) {
/* 383 */     return writeBody(targetFileOrDir, (StreamProgress)null);
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
/*     */   public long writeBody(String targetFileOrDir) {
/* 396 */     return writeBody(FileUtil.file(targetFileOrDir));
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void close() {
/* 402 */     IoUtil.close(this.in);
/* 403 */     this.in = null;
/*     */     
/* 405 */     this.httpConnection.disconnectQuietly();
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 410 */     StringBuilder sb = StrUtil.builder();
/* 411 */     sb.append("Response Headers: ").append("\r\n");
/* 412 */     for (Map.Entry<String, List<String>> entry : this.headers.entrySet()) {
/* 413 */       sb.append("    ").append(entry).append("\r\n");
/*     */     }
/*     */     
/* 416 */     sb.append("Response Body: ").append("\r\n");
/* 417 */     sb.append("    ").append(body()).append("\r\n");
/*     */     
/* 419 */     return sb.toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public File completeFileNameFromHeader(File targetFileOrDir) {
/* 430 */     if (false == targetFileOrDir.isDirectory())
/*     */     {
/* 432 */       return targetFileOrDir;
/*     */     }
/*     */ 
/*     */     
/* 436 */     String fileName = getFileNameFromDisposition();
/* 437 */     if (StrUtil.isBlank(fileName)) {
/* 438 */       String path = this.httpConnection.getUrl().getPath();
/*     */       
/* 440 */       fileName = StrUtil.subSuf(path, path.lastIndexOf('/') + 1);
/* 441 */       if (StrUtil.isBlank(fileName)) {
/*     */         
/* 443 */         fileName = URLUtil.encodeQuery(path, this.charset);
/*     */       } else {
/*     */         
/* 446 */         fileName = URLUtil.decode(fileName, this.charset);
/*     */       } 
/*     */     } 
/* 449 */     return FileUtil.file(targetFileOrDir, fileName);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private HttpResponse initWithDisconnect() throws HttpException {
/*     */     try {
/* 469 */       init();
/* 470 */     } catch (HttpException e) {
/* 471 */       this.httpConnection.disconnectQuietly();
/* 472 */       throw e;
/*     */     } 
/* 474 */     return this;
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
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private HttpResponse init() throws HttpException {
/*     */     try {
/* 493 */       this.status = this.httpConnection.responseCode();
/* 494 */     } catch (IOException e) {
/* 495 */       if (false == e instanceof java.io.FileNotFoundException) {
/* 496 */         throw new HttpException(e);
/*     */       }
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     try {
/* 504 */       this.headers = this.httpConnection.headers();
/* 505 */     } catch (IllegalArgumentException illegalArgumentException) {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 511 */     GlobalCookieManager.store(this.httpConnection);
/*     */ 
/*     */     
/* 514 */     Charset charset = this.httpConnection.getCharset();
/* 515 */     this.charsetFromResponse = charset;
/* 516 */     if (null != charset) {
/* 517 */       this.charset = charset;
/*     */     }
/*     */ 
/*     */     
/* 521 */     this.in = new HttpInputStream(this);
/*     */ 
/*     */     
/* 524 */     return this.isAsync ? this : forceSync();
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
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private HttpResponse forceSync() {
/*     */     
/* 543 */     try { readBody(this.in); }
/* 544 */     catch (IORuntimeException e)
/*     */     
/* 546 */     { if (e.getCause() instanceof java.io.FileNotFoundException)
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 557 */         return this; }  throw new HttpException(e); } finally { if (this.isAsync) this.isAsync = false;  close(); }  return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void readBody(InputStream in) throws IORuntimeException {
/* 567 */     if (this.ignoreBody) {
/*     */       return;
/*     */     }
/*     */     
/* 571 */     long contentLength = contentLength();
/* 572 */     FastByteArrayOutputStream out = new FastByteArrayOutputStream((int)contentLength);
/* 573 */     copyBody(in, (OutputStream)out, contentLength, (StreamProgress)null, this.config.ignoreEOFError);
/* 574 */     this.bodyBytes = out.toByteArray();
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
/*     */ 
/*     */   
/*     */   private static long copyBody(InputStream in, OutputStream out, long contentLength, StreamProgress streamProgress, boolean isIgnoreEOFError) {
/* 590 */     if (null == out) {
/* 591 */       throw new NullPointerException("[out] is null!");
/*     */     }
/*     */     
/* 594 */     long copyLength = -1L;
/*     */     try {
/* 596 */       copyLength = IoUtil.copy(in, out, 8192, contentLength, streamProgress);
/* 597 */     } catch (IORuntimeException e) {
/*     */       
/* 599 */       if (!isIgnoreEOFError || (
/* 600 */         !(e.getCause() instanceof java.io.EOFException) && !StrUtil.containsIgnoreCase(e.getMessage(), "Premature EOF")))
/*     */       {
/*     */         
/* 603 */         throw e;
/*     */       }
/*     */     } 
/* 606 */     return copyLength;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private String getFileNameFromDisposition() {
/* 615 */     String fileName = null;
/* 616 */     String disposition = header(Header.CONTENT_DISPOSITION);
/* 617 */     if (StrUtil.isNotBlank(disposition)) {
/* 618 */       fileName = ReUtil.get("filename=\"(.*?)\"", disposition, 1);
/* 619 */       if (StrUtil.isBlank(fileName)) {
/* 620 */         fileName = StrUtil.subAfter(disposition, "filename=", true);
/*     */       }
/*     */     } 
/* 623 */     return fileName;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\http\HttpResponse.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */