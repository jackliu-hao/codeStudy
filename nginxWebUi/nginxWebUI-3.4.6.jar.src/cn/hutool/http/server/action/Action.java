package cn.hutool.http.server.action;

import cn.hutool.http.server.HttpServerRequest;
import cn.hutool.http.server.HttpServerResponse;
import java.io.IOException;

@FunctionalInterface
public interface Action {
  void doAction(HttpServerRequest paramHttpServerRequest, HttpServerResponse paramHttpServerResponse) throws IOException;
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\http\server\action\Action.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */