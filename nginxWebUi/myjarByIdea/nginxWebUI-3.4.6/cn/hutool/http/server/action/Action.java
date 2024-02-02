package cn.hutool.http.server.action;

import cn.hutool.http.server.HttpServerRequest;
import cn.hutool.http.server.HttpServerResponse;
import java.io.IOException;

@FunctionalInterface
public interface Action {
   void doAction(HttpServerRequest var1, HttpServerResponse var2) throws IOException;
}
