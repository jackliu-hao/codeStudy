package cn.hutool.http.server.filter;

import cn.hutool.http.server.HttpServerRequest;
import cn.hutool.http.server.HttpServerResponse;
import com.sun.net.httpserver.Filter;
import java.io.IOException;

@FunctionalInterface
public interface HttpFilter {
   void doFilter(HttpServerRequest var1, HttpServerResponse var2, Filter.Chain var3) throws IOException;
}
