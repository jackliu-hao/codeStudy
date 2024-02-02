package ch.qos.logback.classic.net.server;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.net.server.Client;

interface RemoteAppenderClient extends Client {
  void setLoggerContext(LoggerContext paramLoggerContext);
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\classic\net\server\RemoteAppenderClient.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */