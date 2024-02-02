package cn.hutool.http.server;

import cn.hutool.core.util.CharsetUtil;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import java.io.Closeable;
import java.nio.charset.Charset;

public class HttpServerBase implements Closeable {
   static final Charset DEFAULT_CHARSET;
   final HttpExchange httpExchange;

   public HttpServerBase(HttpExchange httpExchange) {
      this.httpExchange = httpExchange;
   }

   public HttpExchange getHttpExchange() {
      return this.httpExchange;
   }

   public HttpContext getHttpContext() {
      return this.getHttpExchange().getHttpContext();
   }

   public void close() {
      this.httpExchange.close();
   }

   static {
      DEFAULT_CHARSET = CharsetUtil.CHARSET_UTF_8;
   }
}
