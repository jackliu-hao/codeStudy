package io.undertow.server;

import io.undertow.io.Receiver;
import io.undertow.io.Sender;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface BlockingHttpExchange extends Closeable {
  InputStream getInputStream();
  
  OutputStream getOutputStream();
  
  Sender getSender();
  
  void close() throws IOException;
  
  Receiver getReceiver();
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\BlockingHttpExchange.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */