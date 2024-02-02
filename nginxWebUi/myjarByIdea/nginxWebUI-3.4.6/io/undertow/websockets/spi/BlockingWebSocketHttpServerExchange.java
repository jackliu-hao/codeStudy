package io.undertow.websockets.spi;

import io.undertow.server.HttpServerExchange;
import io.undertow.websockets.core.WebSocketChannel;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.Set;
import org.xnio.FinishedIoFuture;
import org.xnio.FutureResult;
import org.xnio.IoFuture;

public class BlockingWebSocketHttpServerExchange extends AsyncWebSocketHttpServerExchange {
   private final OutputStream out;
   private final InputStream in;

   public BlockingWebSocketHttpServerExchange(HttpServerExchange exchange, Set<WebSocketChannel> peerConnections) {
      super(exchange, peerConnections);
      this.out = exchange.getOutputStream();
      this.in = exchange.getInputStream();
   }

   public IoFuture<Void> sendData(ByteBuffer data) {
      try {
         while(data.hasRemaining()) {
            this.out.write(data.get());
         }

         return new FinishedIoFuture((Object)null);
      } catch (IOException var4) {
         FutureResult<Void> ioFuture = new FutureResult();
         ioFuture.setException(var4);
         return ioFuture.getIoFuture();
      }
   }

   public IoFuture<byte[]> readRequestData() {
      ByteArrayOutputStream data = new ByteArrayOutputStream();

      try {
         byte[] buf = new byte[1024];

         int r;
         while((r = this.in.read(buf)) != -1) {
            data.write(buf, 0, r);
         }

         return new FinishedIoFuture(data.toByteArray());
      } catch (IOException var4) {
         FutureResult<byte[]> ioFuture = new FutureResult();
         ioFuture.setException(var4);
         return ioFuture.getIoFuture();
      }
   }
}
