package cn.hutool.http.server.filter;

import cn.hutool.http.server.HttpServerRequest;
import cn.hutool.http.server.HttpServerResponse;
import com.sun.net.httpserver.Filter;
import java.io.IOException;

@FunctionalInterface
public interface HttpFilter {
  void doFilter(HttpServerRequest paramHttpServerRequest, HttpServerResponse paramHttpServerResponse, Filter.Chain paramChain) throws IOException;
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\http\server\filter\HttpFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */