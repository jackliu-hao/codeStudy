package io.undertow.client;

import io.undertow.connector.ByteBufferPool;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.Set;
import org.xnio.OptionMap;
import org.xnio.XnioIoThread;
import org.xnio.XnioWorker;
import org.xnio.ssl.XnioSsl;

public interface ClientProvider {
   Set<String> handlesSchemes();

   void connect(ClientCallback<ClientConnection> var1, URI var2, XnioWorker var3, XnioSsl var4, ByteBufferPool var5, OptionMap var6);

   void connect(ClientCallback<ClientConnection> var1, InetSocketAddress var2, URI var3, XnioWorker var4, XnioSsl var5, ByteBufferPool var6, OptionMap var7);

   void connect(ClientCallback<ClientConnection> var1, URI var2, XnioIoThread var3, XnioSsl var4, ByteBufferPool var5, OptionMap var6);

   void connect(ClientCallback<ClientConnection> var1, InetSocketAddress var2, URI var3, XnioIoThread var4, XnioSsl var5, ByteBufferPool var6, OptionMap var7);
}
