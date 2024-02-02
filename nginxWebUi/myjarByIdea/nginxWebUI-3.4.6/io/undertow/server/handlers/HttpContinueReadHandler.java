package io.undertow.server.handlers;

import io.undertow.server.ConduitWrapper;
import io.undertow.server.Connectors;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.ResponseCommitListener;
import io.undertow.server.protocol.http.HttpContinue;
import io.undertow.util.ConduitFactory;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.concurrent.TimeUnit;
import org.xnio.channels.StreamSinkChannel;
import org.xnio.conduits.AbstractStreamSourceConduit;
import org.xnio.conduits.StreamSourceConduit;

public class HttpContinueReadHandler implements HttpHandler {
   private static final ConduitWrapper<StreamSourceConduit> WRAPPER = new ConduitWrapper<StreamSourceConduit>() {
      public StreamSourceConduit wrap(ConduitFactory<StreamSourceConduit> factory, HttpServerExchange exchange) {
         return (StreamSourceConduit)(exchange.isRequestChannelAvailable() && !exchange.isResponseStarted() ? new ContinueConduit((StreamSourceConduit)factory.create(), exchange) : (StreamSourceConduit)factory.create());
      }
   };
   private final HttpHandler handler;

   public HttpContinueReadHandler(HttpHandler handler) {
      this.handler = handler;
   }

   public void handleRequest(HttpServerExchange exchange) throws Exception {
      if (HttpContinue.requiresContinueResponse(exchange)) {
         exchange.addRequestWrapper(WRAPPER);
         exchange.addResponseCommitListener(HttpContinueReadHandler.ContinueResponseCommitListener.INSTANCE);
      }

      this.handler.handleRequest(exchange);
   }

   private static final class ContinueConduit extends AbstractStreamSourceConduit<StreamSourceConduit> implements StreamSourceConduit {
      private boolean sent = false;
      private HttpContinue.ContinueResponseSender response = null;
      private final HttpServerExchange exchange;

      protected ContinueConduit(StreamSourceConduit next, HttpServerExchange exchange) {
         super(next);
         this.exchange = exchange;
      }

      public long transferTo(long position, long count, FileChannel target) throws IOException {
         if (this.exchange.getStatusCode() == 417) {
            Connectors.terminateRequest(this.exchange);
            return -1L;
         } else {
            if (!this.sent) {
               this.sent = true;
               this.response = HttpContinue.createResponseSender(this.exchange);
            }

            if (this.response != null) {
               if (!this.response.send()) {
                  return 0L;
               }

               this.response = null;
            }

            return super.transferTo(position, count, target);
         }
      }

      public long transferTo(long count, ByteBuffer throughBuffer, StreamSinkChannel target) throws IOException {
         if (this.exchange.getStatusCode() == 417) {
            Connectors.terminateRequest(this.exchange);
            return -1L;
         } else {
            if (!this.sent) {
               this.sent = true;
               this.response = HttpContinue.createResponseSender(this.exchange);
            }

            if (this.response != null) {
               if (!this.response.send()) {
                  return 0L;
               }

               this.response = null;
            }

            return super.transferTo(count, throughBuffer, target);
         }
      }

      public int read(ByteBuffer dst) throws IOException {
         if (this.exchange.getStatusCode() == 417) {
            Connectors.terminateRequest(this.exchange);
            return -1;
         } else {
            if (!this.sent) {
               this.sent = true;
               this.response = HttpContinue.createResponseSender(this.exchange);
            }

            if (this.response != null) {
               if (!this.response.send()) {
                  return 0;
               }

               this.response = null;
            }

            return super.read(dst);
         }
      }

      public long read(ByteBuffer[] dsts, int offs, int len) throws IOException {
         if (this.exchange.getStatusCode() == 417) {
            Connectors.terminateRequest(this.exchange);
            return -1L;
         } else {
            if (!this.sent) {
               this.sent = true;
               this.response = HttpContinue.createResponseSender(this.exchange);
            }

            if (this.response != null) {
               if (!this.response.send()) {
                  return 0L;
               }

               this.response = null;
            }

            return super.read(dsts, offs, len);
         }
      }

      public void awaitReadable(long time, TimeUnit timeUnit) throws IOException {
         if (this.exchange.getStatusCode() != 417) {
            if (!this.sent) {
               this.sent = true;
               this.response = HttpContinue.createResponseSender(this.exchange);
            }

            long exitTime = System.currentTimeMillis() + timeUnit.toMillis(time);
            long currentTime;
            if (this.response != null) {
               while(!this.response.send()) {
                  currentTime = System.currentTimeMillis();
                  if (currentTime > exitTime) {
                     return;
                  }

                  this.response.awaitWritable(exitTime - currentTime, TimeUnit.MILLISECONDS);
               }

               this.response = null;
            }

            currentTime = System.currentTimeMillis();
            super.awaitReadable(exitTime - currentTime, TimeUnit.MILLISECONDS);
         }
      }

      public void awaitReadable() throws IOException {
         if (this.exchange.getStatusCode() != 417) {
            if (!this.sent) {
               this.sent = true;
               this.response = HttpContinue.createResponseSender(this.exchange);
            }

            if (this.response != null) {
               while(!this.response.send()) {
                  this.response.awaitWritable();
               }

               this.response = null;
            }

            super.awaitReadable();
         }
      }
   }

   private static enum ContinueResponseCommitListener implements ResponseCommitListener {
      INSTANCE;

      public void beforeCommit(HttpServerExchange exchange) {
         if (!HttpContinue.isContinueResponseSent(exchange)) {
            exchange.setPersistent(false);
            if (!exchange.isRequestComplete()) {
               exchange.getConnection().terminateRequestChannel(exchange);
            } else {
               Connectors.terminateRequest(exchange);
            }
         }

      }
   }
}
