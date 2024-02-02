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
  
  void connect(ClientCallback<ClientConnection> paramClientCallback, URI paramURI, XnioWorker paramXnioWorker, XnioSsl paramXnioSsl, ByteBufferPool paramByteBufferPool, OptionMap paramOptionMap);
  
  void connect(ClientCallback<ClientConnection> paramClientCallback, InetSocketAddress paramInetSocketAddress, URI paramURI, XnioWorker paramXnioWorker, XnioSsl paramXnioSsl, ByteBufferPool paramByteBufferPool, OptionMap paramOptionMap);
  
  void connect(ClientCallback<ClientConnection> paramClientCallback, URI paramURI, XnioIoThread paramXnioIoThread, XnioSsl paramXnioSsl, ByteBufferPool paramByteBufferPool, OptionMap paramOptionMap);
  
  void connect(ClientCallback<ClientConnection> paramClientCallback, InetSocketAddress paramInetSocketAddress, URI paramURI, XnioIoThread paramXnioIoThread, XnioSsl paramXnioSsl, ByteBufferPool paramByteBufferPool, OptionMap paramOptionMap);
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\client\ClientProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */