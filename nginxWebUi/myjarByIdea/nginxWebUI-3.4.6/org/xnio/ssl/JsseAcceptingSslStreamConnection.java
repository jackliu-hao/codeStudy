package org.xnio.ssl;

import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import org.xnio.IoUtils;
import org.xnio.OptionMap;
import org.xnio.Pool;
import org.xnio.StreamConnection;
import org.xnio.channels.AcceptingChannel;

final class JsseAcceptingSslStreamConnection extends AbstractAcceptingSslChannel<SslConnection, StreamConnection> {
   JsseAcceptingSslStreamConnection(SSLContext sslContext, AcceptingChannel<? extends StreamConnection> tcpServer, OptionMap optionMap, Pool<ByteBuffer> socketBufferPool, Pool<ByteBuffer> applicationBufferPool, boolean startTls) {
      super(sslContext, tcpServer, optionMap, socketBufferPool, applicationBufferPool, startTls);
   }

   public SslConnection accept(StreamConnection tcpConnection, SSLEngine engine) throws IOException {
      if (!JsseXnioSsl.NEW_IMPL) {
         return new JsseSslStreamConnection(tcpConnection, engine, this.socketBufferPool, this.applicationBufferPool, this.startTls);
      } else {
         JsseSslConnection connection = new JsseSslConnection(tcpConnection, engine, this.socketBufferPool, this.applicationBufferPool);
         if (!this.startTls) {
            try {
               connection.startHandshake();
            } catch (IOException var5) {
               IoUtils.safeClose((Closeable)connection);
               throw var5;
            }
         }

         return connection;
      }
   }
}
