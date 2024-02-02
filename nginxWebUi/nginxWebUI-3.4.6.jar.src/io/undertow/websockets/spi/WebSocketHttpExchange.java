package io.undertow.websockets.spi;

import io.undertow.connector.ByteBufferPool;
import io.undertow.server.HttpUpgradeListener;
import io.undertow.util.AttachmentKey;
import io.undertow.websockets.core.WebSocketChannel;
import java.io.Closeable;
import java.nio.ByteBuffer;
import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.xnio.IoFuture;
import org.xnio.OptionMap;

public interface WebSocketHttpExchange extends Closeable {
  <T> void putAttachment(AttachmentKey<T> paramAttachmentKey, T paramT);
  
  <T> T getAttachment(AttachmentKey<T> paramAttachmentKey);
  
  String getRequestHeader(String paramString);
  
  Map<String, List<String>> getRequestHeaders();
  
  String getResponseHeader(String paramString);
  
  Map<String, List<String>> getResponseHeaders();
  
  void setResponseHeaders(Map<String, List<String>> paramMap);
  
  void setResponseHeader(String paramString1, String paramString2);
  
  void upgradeChannel(HttpUpgradeListener paramHttpUpgradeListener);
  
  IoFuture<Void> sendData(ByteBuffer paramByteBuffer);
  
  IoFuture<byte[]> readRequestData();
  
  void endExchange();
  
  void close();
  
  String getRequestScheme();
  
  String getRequestURI();
  
  ByteBufferPool getBufferPool();
  
  String getQueryString();
  
  Object getSession();
  
  Map<String, List<String>> getRequestParameters();
  
  Principal getUserPrincipal();
  
  boolean isUserInRole(String paramString);
  
  Set<WebSocketChannel> getPeerConnections();
  
  OptionMap getOptions();
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\websockets\spi\WebSocketHttpExchange.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */