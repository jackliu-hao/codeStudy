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
   <T> void putAttachment(AttachmentKey<T> var1, T var2);

   <T> T getAttachment(AttachmentKey<T> var1);

   String getRequestHeader(String var1);

   Map<String, List<String>> getRequestHeaders();

   String getResponseHeader(String var1);

   Map<String, List<String>> getResponseHeaders();

   void setResponseHeaders(Map<String, List<String>> var1);

   void setResponseHeader(String var1, String var2);

   void upgradeChannel(HttpUpgradeListener var1);

   IoFuture<Void> sendData(ByteBuffer var1);

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

   boolean isUserInRole(String var1);

   Set<WebSocketChannel> getPeerConnections();

   OptionMap getOptions();
}
