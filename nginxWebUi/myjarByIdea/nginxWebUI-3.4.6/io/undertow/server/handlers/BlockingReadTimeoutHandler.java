package io.undertow.server.handlers;

import io.undertow.UndertowLogger;
import io.undertow.UndertowMessages;
import io.undertow.server.ConduitWrapper;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.ServerConnection;
import io.undertow.util.ConduitFactory;
import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import org.xnio.IoUtils;
import org.xnio.XnioIoThread;
import org.xnio.XnioWorker;
import org.xnio.channels.ReadTimeoutException;
import org.xnio.channels.StreamSinkChannel;
import org.xnio.conduits.ReadReadyHandler;
import org.xnio.conduits.StreamSourceConduit;

public final class BlockingReadTimeoutHandler implements HttpHandler {
   private final HttpHandler next;
   private final ConduitWrapper<StreamSourceConduit> streamSourceConduitWrapper;

   private BlockingReadTimeoutHandler(HttpHandler next, Duration readTimeout) {
      this.next = next;
      this.streamSourceConduitWrapper = new TimeoutStreamSourceConduitWrapper(readTimeout);
   }

   public void handleRequest(HttpServerExchange exchange) throws Exception {
      exchange.addRequestWrapper(this.streamSourceConduitWrapper);
      this.next.handleRequest(exchange);
   }

   public static Builder builder() {
      return new Builder();
   }

   // $FF: synthetic method
   BlockingReadTimeoutHandler(HttpHandler x0, Duration x1, Object x2) {
      this(x0, x1);
   }

   public static final class Builder {
      private HttpHandler nextHandler;
      private Duration readTimeout;

      private Builder() {
      }

      public Builder readTimeout(Duration readTimeout) {
         this.readTimeout = (Duration)Objects.requireNonNull(readTimeout, "A read timeout is required");
         return this;
      }

      public Builder nextHandler(HttpHandler nextHandler) {
         this.nextHandler = (HttpHandler)Objects.requireNonNull(nextHandler, "HttpHandler is required");
         return this;
      }

      public HttpHandler build() {
         HttpHandler next = (HttpHandler)Objects.requireNonNull(this.nextHandler, "HttpHandler is required");
         if (this.readTimeout == null) {
            throw new IllegalArgumentException("A read timeout is required");
         } else if (!this.readTimeout.isZero() && !this.readTimeout.isNegative()) {
            return new BlockingReadTimeoutHandler(next, this.readTimeout);
         } else {
            throw new IllegalArgumentException("Read timeout must be positive: " + this.readTimeout);
         }
      }

      // $FF: synthetic method
      Builder(Object x0) {
         this();
      }
   }

   private static final class TimeoutStreamSourceConduit implements StreamSourceConduit {
      private final StreamSourceConduit delegate;
      private final ServerConnection serverConnection;
      private final long timeoutNanos;
      private long remaining;

      TimeoutStreamSourceConduit(StreamSourceConduit delegate, ServerConnection serverConnection, long timeoutNanos) {
         this.delegate = delegate;
         this.serverConnection = serverConnection;
         this.timeoutNanos = timeoutNanos;
         this.remaining = timeoutNanos;
      }

      public long transferTo(long position, long count, FileChannel fileChannel) throws IOException {
         return this.resetTimeoutIfReadSucceeded(this.delegate.transferTo(position, count, fileChannel));
      }

      public long transferTo(long count, ByteBuffer byteBuffer, StreamSinkChannel streamSinkChannel) throws IOException {
         return this.resetTimeoutIfReadSucceeded(this.delegate.transferTo(count, byteBuffer, streamSinkChannel));
      }

      public int read(ByteBuffer byteBuffer) throws IOException {
         return this.resetTimeoutIfReadSucceeded(this.delegate.read(byteBuffer));
      }

      public long read(ByteBuffer[] byteBuffers, int offset, int length) throws IOException {
         return this.resetTimeoutIfReadSucceeded(this.delegate.read(byteBuffers, offset, length));
      }

      public void terminateReads() throws IOException {
         this.delegate.terminateReads();
      }

      public boolean isReadShutdown() {
         return this.delegate.isReadShutdown();
      }

      public void resumeReads() {
         this.delegate.resumeReads();
      }

      public void suspendReads() {
         this.delegate.suspendReads();
      }

      public void wakeupReads() {
         this.delegate.wakeupReads();
      }

      public boolean isReadResumed() {
         return this.delegate.isReadResumed();
      }

      public void awaitReadable() throws IOException {
         this.awaitReadable(this.remaining, TimeUnit.NANOSECONDS);
      }

      public void awaitReadable(long duration, TimeUnit unit) throws IOException {
         long startTime = System.nanoTime();
         long requestedNanos = unit.toNanos(duration);

         try {
            this.delegate.awaitReadable(Math.min(requestedNanos, this.remaining), TimeUnit.NANOSECONDS);
         } finally {
            this.remaining -= System.nanoTime() - startTime;
         }

         if (this.remaining < 0L) {
            ReadTimeoutException rte = UndertowMessages.MESSAGES.blockingReadTimedOut(this.timeoutNanos);
            UndertowLogger.REQUEST_IO_LOGGER.blockingReadTimedOut(rte);
            IoUtils.safeClose((Closeable)this.serverConnection);
            throw rte;
         }
      }

      public XnioIoThread getReadThread() {
         return this.delegate.getReadThread();
      }

      public void setReadReadyHandler(ReadReadyHandler readReadyHandler) {
         this.delegate.setReadReadyHandler(readReadyHandler);
      }

      public XnioWorker getWorker() {
         return this.delegate.getWorker();
      }

      private long resetTimeoutIfReadSucceeded(long value) {
         if (value != 0L) {
            this.remaining = this.timeoutNanos;
         }

         return value;
      }

      private int resetTimeoutIfReadSucceeded(int value) {
         if (value != 0) {
            this.remaining = this.timeoutNanos;
         }

         return value;
      }
   }

   private static final class TimeoutStreamSourceConduitWrapper implements ConduitWrapper<StreamSourceConduit> {
      private final long timeoutNanoseconds;

      TimeoutStreamSourceConduitWrapper(Duration readTimeout) {
         this.timeoutNanoseconds = readTimeout.toNanos();
      }

      public StreamSourceConduit wrap(ConduitFactory<StreamSourceConduit> factory, HttpServerExchange exchange) {
         return new TimeoutStreamSourceConduit((StreamSourceConduit)factory.create(), exchange.getConnection(), this.timeoutNanoseconds);
      }
   }
}
