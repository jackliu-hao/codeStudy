package cn.hutool.http.server;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.http.ContentType;
import cn.hutool.http.Header;
import cn.hutool.http.HttpUtil;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

public class HttpServerResponse extends HttpServerBase {
   private Charset charset;
   private boolean isSendCode;

   public HttpServerResponse(HttpExchange httpExchange) {
      super(httpExchange);
   }

   public HttpServerResponse send(int httpStatusCode) {
      return this.send(httpStatusCode, 0L);
   }

   public HttpServerResponse sendOk() {
      return this.send(200);
   }

   public HttpServerResponse sendOk(int bodyLength) {
      return this.send(200, (long)bodyLength);
   }

   public HttpServerResponse send404(String content) {
      return this.sendError(404, content);
   }

   public HttpServerResponse sendError(int errorCode, String content) {
      this.send(errorCode);
      this.setContentType(ContentType.TEXT_HTML.toString());
      return this.write(content);
   }

   public HttpServerResponse send(int httpStatusCode, long bodyLength) {
      if (this.isSendCode) {
         throw new IORuntimeException("Http status code has been send!");
      } else {
         try {
            this.httpExchange.sendResponseHeaders(httpStatusCode, bodyLength);
         } catch (IOException var5) {
            throw new IORuntimeException(var5);
         }

         this.isSendCode = true;
         return this;
      }
   }

   public Headers getHeaders() {
      return this.httpExchange.getResponseHeaders();
   }

   public HttpServerResponse addHeader(String header, String value) {
      this.getHeaders().add(header, value);
      return this;
   }

   public HttpServerResponse setHeader(Header header, String value) {
      return this.setHeader(header.getValue(), value);
   }

   public HttpServerResponse setHeader(String header, String value) {
      this.getHeaders().set(header, value);
      return this;
   }

   public HttpServerResponse setHeader(String header, List<String> value) {
      this.getHeaders().put(header, value);
      return this;
   }

   public HttpServerResponse setHeaders(Map<String, List<String>> headers) {
      this.getHeaders().putAll(headers);
      return this;
   }

   public HttpServerResponse setContentType(String contentType) {
      if (null != contentType && null != this.charset && !contentType.contains(";charset=")) {
         contentType = ContentType.build(contentType, this.charset);
      }

      return this.setHeader(Header.CONTENT_TYPE, contentType);
   }

   public HttpServerResponse setContentLength(long contentLength) {
      return this.setHeader(Header.CONTENT_LENGTH, String.valueOf(contentLength));
   }

   public HttpServerResponse setCharset(Charset charset) {
      this.charset = charset;
      return this;
   }

   public HttpServerResponse setAttr(String name, Object value) {
      this.httpExchange.setAttribute(name, value);
      return this;
   }

   public OutputStream getOut() {
      if (!this.isSendCode) {
         this.sendOk();
      }

      return this.httpExchange.getResponseBody();
   }

   public PrintWriter getWriter() {
      Charset charset = (Charset)ObjectUtil.defaultIfNull(this.charset, (Object)DEFAULT_CHARSET);
      return new PrintWriter(new OutputStreamWriter(this.getOut(), charset));
   }

   public HttpServerResponse write(String data, String contentType) {
      this.setContentType(contentType);
      return this.write(data);
   }

   public HttpServerResponse write(String data) {
      Charset charset = (Charset)ObjectUtil.defaultIfNull(this.charset, (Object)DEFAULT_CHARSET);
      return this.write(StrUtil.bytes(data, charset));
   }

   public HttpServerResponse write(byte[] data, String contentType) {
      this.setContentType(contentType);
      return this.write(data);
   }

   public HttpServerResponse write(byte[] data) {
      ByteArrayInputStream in = new ByteArrayInputStream(data);
      return this.write(in, in.available());
   }

   public HttpServerResponse write(InputStream in, String contentType) {
      return this.write(in, 0, contentType);
   }

   public HttpServerResponse write(InputStream in, int length, String contentType) {
      this.setContentType(contentType);
      return this.write(in, length);
   }

   public HttpServerResponse write(InputStream in) {
      return this.write(in, 0);
   }

   public HttpServerResponse write(InputStream in, int length) {
      if (!this.isSendCode) {
         this.sendOk(Math.max(0, length));
      }

      OutputStream out = null;

      try {
         out = this.httpExchange.getResponseBody();
         IoUtil.copy(in, out);
      } finally {
         IoUtil.close(out);
         IoUtil.close(in);
      }

      return this;
   }

   public HttpServerResponse write(File file) {
      return this.write((File)file, (String)null);
   }

   public HttpServerResponse write(File file, String fileName) {
      long fileSize = file.length();
      if (fileSize > 2147483647L) {
         throw new IllegalArgumentException("File size is too bigger than 2147483647");
      } else {
         if (StrUtil.isBlank(fileName)) {
            fileName = file.getName();
         }

         String contentType = (String)ObjectUtil.defaultIfNull(HttpUtil.getMimeType(fileName), (Object)"application/octet-stream");
         BufferedInputStream in = null;

         try {
            in = FileUtil.getInputStream(file);
            this.write(in, (int)fileSize, contentType, fileName);
         } finally {
            IoUtil.close(in);
         }

         return this;
      }
   }

   public void write(InputStream in, String contentType, String fileName) {
      this.write(in, 0, contentType, fileName);
   }

   public HttpServerResponse write(InputStream in, int length, String contentType, String fileName) {
      Charset charset = (Charset)ObjectUtil.defaultIfNull(this.charset, (Object)DEFAULT_CHARSET);
      if (!contentType.startsWith("text/")) {
         this.setHeader(Header.CONTENT_DISPOSITION, StrUtil.format("attachment;filename={}", new Object[]{URLUtil.encode(fileName, charset)}));
      }

      return this.write(in, length, contentType);
   }
}
