package io.undertow.io;

import io.undertow.server.HttpServerExchange;
import java.io.IOException;
import java.nio.charset.Charset;

public interface Receiver {
  void setMaxBufferSize(int paramInt);
  
  void receiveFullString(FullStringCallback paramFullStringCallback, ErrorCallback paramErrorCallback);
  
  void receiveFullString(FullStringCallback paramFullStringCallback);
  
  void receivePartialString(PartialStringCallback paramPartialStringCallback, ErrorCallback paramErrorCallback);
  
  void receivePartialString(PartialStringCallback paramPartialStringCallback);
  
  void receiveFullString(FullStringCallback paramFullStringCallback, ErrorCallback paramErrorCallback, Charset paramCharset);
  
  void receiveFullString(FullStringCallback paramFullStringCallback, Charset paramCharset);
  
  void receivePartialString(PartialStringCallback paramPartialStringCallback, ErrorCallback paramErrorCallback, Charset paramCharset);
  
  void receivePartialString(PartialStringCallback paramPartialStringCallback, Charset paramCharset);
  
  void receiveFullBytes(FullBytesCallback paramFullBytesCallback, ErrorCallback paramErrorCallback);
  
  void receiveFullBytes(FullBytesCallback paramFullBytesCallback);
  
  void receivePartialBytes(PartialBytesCallback paramPartialBytesCallback, ErrorCallback paramErrorCallback);
  
  void receivePartialBytes(PartialBytesCallback paramPartialBytesCallback);
  
  void pause();
  
  void resume();
  
  public static class RequestToLargeException extends IOException {}
  
  public static interface PartialBytesCallback {
    void handle(HttpServerExchange param1HttpServerExchange, byte[] param1ArrayOfbyte, boolean param1Boolean);
  }
  
  public static interface PartialStringCallback {
    void handle(HttpServerExchange param1HttpServerExchange, String param1String, boolean param1Boolean);
  }
  
  public static interface FullBytesCallback {
    void handle(HttpServerExchange param1HttpServerExchange, byte[] param1ArrayOfbyte);
  }
  
  public static interface FullStringCallback {
    void handle(HttpServerExchange param1HttpServerExchange, String param1String);
  }
  
  public static interface ErrorCallback {
    void error(HttpServerExchange param1HttpServerExchange, IOException param1IOException);
  }
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\io\Receiver.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */