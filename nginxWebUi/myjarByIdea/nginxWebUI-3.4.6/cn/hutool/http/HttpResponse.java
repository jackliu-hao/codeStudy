package cn.hutool.http;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.io.FastByteArrayOutputStream;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.io.StreamProgress;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.http.cookie.GlobalCookieManager;
import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.EOFException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpCookie;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class HttpResponse extends HttpBase<HttpResponse> implements Closeable {
   protected HttpConfig config;
   protected HttpConnection httpConnection;
   protected InputStream in;
   private volatile boolean isAsync;
   protected int status;
   private final boolean ignoreBody;
   private Charset charsetFromResponse;

   protected HttpResponse(HttpConnection httpConnection, HttpConfig config, Charset charset, boolean isAsync, boolean isIgnoreBody) {
      this.httpConnection = httpConnection;
      this.config = config;
      this.charset = charset;
      this.isAsync = isAsync;
      this.ignoreBody = isIgnoreBody;
      this.initWithDisconnect();
   }

   public int getStatus() {
      return this.status;
   }

   public boolean isOk() {
      return this.status >= 200 && this.status < 300;
   }

   public HttpResponse sync() {
      return this.isAsync ? this.forceSync() : this;
   }

   public String contentEncoding() {
      return this.header(Header.CONTENT_ENCODING);
   }

   public long contentLength() {
      long contentLength = Convert.toLong(this.header(Header.CONTENT_LENGTH), -1L);
      if (contentLength > 0L && (this.isChunked() || StrUtil.isNotBlank(this.contentEncoding()))) {
         contentLength = -1L;
      }

      return contentLength;
   }

   public boolean isGzip() {
      String contentEncoding = this.contentEncoding();
      return "gzip".equalsIgnoreCase(contentEncoding);
   }

   public boolean isDeflate() {
      String contentEncoding = this.contentEncoding();
      return "deflate".equalsIgnoreCase(contentEncoding);
   }

   public boolean isChunked() {
      String transferEncoding = this.header(Header.TRANSFER_ENCODING);
      return "Chunked".equalsIgnoreCase(transferEncoding);
   }

   public String getCookieStr() {
      return this.header(Header.SET_COOKIE);
   }

   public List<HttpCookie> getCookies() {
      return GlobalCookieManager.getCookies(this.httpConnection);
   }

   public HttpCookie getCookie(String name) {
      List<HttpCookie> cookie = this.getCookies();
      if (null != cookie) {
         Iterator var3 = cookie.iterator();

         while(var3.hasNext()) {
            HttpCookie httpCookie = (HttpCookie)var3.next();
            if (httpCookie.getName().equals(name)) {
               return httpCookie;
            }
         }
      }

      return null;
   }

   public String getCookieValue(String name) {
      HttpCookie cookie = this.getCookie(name);
      return null == cookie ? null : cookie.getValue();
   }

   public InputStream bodyStream() {
      return (InputStream)(this.isAsync ? this.in : new ByteArrayInputStream(this.bodyBytes));
   }

   public byte[] bodyBytes() {
      this.sync();
      return this.bodyBytes;
   }

   public String body() throws HttpException {
      return HttpUtil.getString(this.bodyBytes(), this.charset, null == this.charsetFromResponse);
   }

   public long writeBody(OutputStream out, boolean isCloseOut, StreamProgress streamProgress) {
      Assert.notNull(out, "[out] must be not null!");
      long contentLength = this.contentLength();

      long var6;
      try {
         var6 = copyBody(this.bodyStream(), out, contentLength, streamProgress, this.config.ignoreEOFError);
      } finally {
         IoUtil.close(this);
         if (isCloseOut) {
            IoUtil.close(out);
         }

      }

      return var6;
   }

   public long writeBody(File targetFileOrDir, StreamProgress streamProgress) {
      Assert.notNull(targetFileOrDir, "[targetFileOrDir] must be not null!");
      File outFile = this.completeFileNameFromHeader(targetFileOrDir);
      return this.writeBody(FileUtil.getOutputStream(outFile), true, streamProgress);
   }

   public long writeBody(File targetFileOrDir, String tempFileSuffix, StreamProgress streamProgress) {
      Assert.notNull(targetFileOrDir, "[targetFileOrDir] must be not null!");
      File outFile = this.completeFileNameFromHeader(targetFileOrDir);
      if (StrUtil.isBlank(tempFileSuffix)) {
         tempFileSuffix = ".temp";
      } else {
         tempFileSuffix = StrUtil.addPrefixIfNot(tempFileSuffix, ".");
      }

      String fileName = outFile.getName();
      String tempFileName = fileName + tempFileSuffix;
      outFile = new File(outFile.getParentFile(), tempFileName);

      try {
         long length = this.writeBody(outFile, streamProgress);
         FileUtil.rename(outFile, fileName, true);
         return length;
      } catch (Throwable var10) {
         FileUtil.del(outFile);
         throw new HttpException(var10);
      }
   }

   public File writeBodyForFile(File targetFileOrDir, StreamProgress streamProgress) {
      Assert.notNull(targetFileOrDir, "[targetFileOrDir] must be not null!");
      File outFile = this.completeFileNameFromHeader(targetFileOrDir);
      this.writeBody(FileUtil.getOutputStream(outFile), true, streamProgress);
      return outFile;
   }

   public long writeBody(File targetFileOrDir) {
      return this.writeBody(targetFileOrDir, (StreamProgress)null);
   }

   public long writeBody(String targetFileOrDir) {
      return this.writeBody(FileUtil.file(targetFileOrDir));
   }

   public void close() {
      IoUtil.close(this.in);
      this.in = null;
      this.httpConnection.disconnectQuietly();
   }

   public String toString() {
      StringBuilder sb = StrUtil.builder();
      sb.append("Response Headers: ").append("\r\n");
      Iterator var2 = this.headers.entrySet().iterator();

      while(var2.hasNext()) {
         Map.Entry<String, List<String>> entry = (Map.Entry)var2.next();
         sb.append("    ").append(entry).append("\r\n");
      }

      sb.append("Response Body: ").append("\r\n");
      sb.append("    ").append(this.body()).append("\r\n");
      return sb.toString();
   }

   public File completeFileNameFromHeader(File targetFileOrDir) {
      if (!targetFileOrDir.isDirectory()) {
         return targetFileOrDir;
      } else {
         String fileName = this.getFileNameFromDisposition();
         if (StrUtil.isBlank(fileName)) {
            String path = this.httpConnection.getUrl().getPath();
            fileName = StrUtil.subSuf(path, path.lastIndexOf(47) + 1);
            if (StrUtil.isBlank(fileName)) {
               fileName = URLUtil.encodeQuery(path, this.charset);
            } else {
               fileName = URLUtil.decode(fileName, this.charset);
            }
         }

         return FileUtil.file(targetFileOrDir, fileName);
      }
   }

   private HttpResponse initWithDisconnect() throws HttpException {
      try {
         this.init();
         return this;
      } catch (HttpException var2) {
         this.httpConnection.disconnectQuietly();
         throw var2;
      }
   }

   private HttpResponse init() throws HttpException {
      try {
         this.status = this.httpConnection.responseCode();
      } catch (IOException var3) {
         if (!(var3 instanceof FileNotFoundException)) {
            throw new HttpException(var3);
         }
      }

      try {
         this.headers = this.httpConnection.headers();
      } catch (IllegalArgumentException var2) {
      }

      GlobalCookieManager.store(this.httpConnection);
      Charset charset = this.httpConnection.getCharset();
      this.charsetFromResponse = charset;
      if (null != charset) {
         this.charset = charset;
      }

      this.in = new HttpInputStream(this);
      return this.isAsync ? this : this.forceSync();
   }

   private HttpResponse forceSync() {
      try {
         this.readBody(this.in);
      } catch (IORuntimeException var5) {
         if (!(var5.getCause() instanceof FileNotFoundException)) {
            throw new HttpException(var5);
         }
      } finally {
         if (this.isAsync) {
            this.isAsync = false;
         }

         this.close();
      }

      return this;
   }

   private void readBody(InputStream in) throws IORuntimeException {
      if (!this.ignoreBody) {
         long contentLength = this.contentLength();
         FastByteArrayOutputStream out = new FastByteArrayOutputStream((int)contentLength);
         copyBody(in, out, contentLength, (StreamProgress)null, this.config.ignoreEOFError);
         this.bodyBytes = out.toByteArray();
      }
   }

   private static long copyBody(InputStream in, OutputStream out, long contentLength, StreamProgress streamProgress, boolean isIgnoreEOFError) {
      if (null == out) {
         throw new NullPointerException("[out] is null!");
      } else {
         long copyLength = -1L;

         try {
            copyLength = IoUtil.copy((InputStream)in, (OutputStream)out, 8192, contentLength, streamProgress);
         } catch (IORuntimeException var9) {
            if (!isIgnoreEOFError || !(var9.getCause() instanceof EOFException) && !StrUtil.containsIgnoreCase(var9.getMessage(), "Premature EOF")) {
               throw var9;
            }
         }

         return copyLength;
      }
   }

   private String getFileNameFromDisposition() {
      String fileName = null;
      String disposition = this.header(Header.CONTENT_DISPOSITION);
      if (StrUtil.isNotBlank(disposition)) {
         fileName = ReUtil.get((String)"filename=\"(.*?)\"", disposition, 1);
         if (StrUtil.isBlank(fileName)) {
            fileName = StrUtil.subAfter(disposition, "filename=", true);
         }
      }

      return fileName;
   }
}
