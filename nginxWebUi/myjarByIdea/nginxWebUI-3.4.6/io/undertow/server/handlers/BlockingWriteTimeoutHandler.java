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
import org.xnio.channels.StreamSourceChannel;
import org.xnio.channels.WriteTimeoutException;
import org.xnio.conduits.StreamSinkConduit;
import org.xnio.conduits.WriteReadyHandler;

public final class BlockingWriteTimeoutHandler implements HttpHandler {
   private final HttpHandler next;
   private final ConduitWrapper<StreamSinkConduit> streamSinkConduitWrapper;

   private BlockingWriteTimeoutHandler(HttpHandler next, Duration writeTimeout) {
      this.next = next;
      this.streamSinkConduitWrapper = new TimeoutStreamSinkConduitWrapper(writeTimeout);
   }

   public void handleRequest(HttpServerExchange exchange) throws Exception {
      exchange.addResponseWrapper(this.streamSinkConduitWrapper);
      this.next.handleRequest(exchange);
   }

   public static Builder builder() {
      return new Builder();
   }

   // $FF: synthetic method
   BlockingWriteTimeoutHandler(HttpHandler x0, Duration x1, Object x2) {
      this(x0, x1);
   }

   public static final class Builder {
      private HttpHandler nextHandler;
      private Duration writeTimeout;

      private Builder() {
      }

      public Builder writeTimeout(Duration writeTimeout) {
         this.writeTimeout = (Duration)Objects.requireNonNull(writeTimeout, "A write timeout is required");
         return this;
      }

      public Builder nextHandler(HttpHandler nextHandler) {
         this.nextHandler = (HttpHandler)Objects.requireNonNull(nextHandler, "HttpHandler is required");
         return this;
      }

      public HttpHandler build() {
         HttpHandler next = (HttpHandler)Objects.requireNonNull(this.nextHandler, "HttpHandler is required");
         if (this.writeTimeout == null) {
            throw new IllegalArgumentException("A write timeout is required");
         } else if (!this.writeTimeout.isZero() && !this.writeTimeout.isNegative()) {
            return new BlockingWriteTimeoutHandler(next, this.writeTimeout);
         } else {
            throw new IllegalArgumentException("Write timeout must be positive: " + this.writeTimeout);
         }
      }

      // $FF: synthetic method
      Builder(Object x0) {
         this();
      }
   }

   private static final class TimeoutStreamSinkConduit implements StreamSinkConduit {
      private final StreamSinkConduit delegate;
      private final ServerConnection serverConnection;
      private final long timeoutNanos;
      private long remaining;

      TimeoutStreamSinkConduit(StreamSinkConduit delegate, ServerConnection serverConnection, long timeoutNanos) {
         this.delegate = delegate;
         this.serverConnection = serverConnection;
         this.timeoutNanos = timeoutNanos;
         this.remaining = timeoutNanos;
      }

      public long transferFrom(FileChannel fileChannel, long position, long count) throws IOException {
         return this.resetTimeoutIfWriteSucceeded(this.delegate.transferFrom(fileChannel, position, count));
      }

      public long transferFrom(StreamSourceChannel streamSourceChannel, long count, ByteBuffer byteBuffer) throws IOException {
         return this.resetTimeoutIfWriteSucceeded(this.delegate.transferFrom(streamSourceChannel, count, byteBuffer));
      }

      public int write(ByteBuffer byteBuffer) throws IOException {
         return this.resetTimeoutIfWriteSucceeded(this.delegate.write(byteBuffer));
      }

      public long write(ByteBuffer[] byteBuffers, int offset, int length) throws IOException {
         return this.resetTimeoutIfWriteSucceeded(this.delegate.write(byteBuffers, offset, length));
      }

      public int writeFinal(ByteBuffer byteBuffer) throws IOException {
         return this.resetTimeoutIfWriteSucceeded(this.delegate.writeFinal(byteBuffer));
      }

      public long writeFinal(ByteBuffer[] byteBuffers, int offset, int length) throws IOException {
         return this.resetTimeoutIfWriteSucceeded(this.delegate.writeFinal(byteBuffers, offset, length));
      }

      public void terminateWrites() throws IOException {
         this.delegate.terminateWrites();
      }

      public boolean isWriteShutdown() {
         return this.delegate.isWriteShutdown();
      }

      public void resumeWrites() {
         this.delegate.resumeWrites();
      }

      public void suspendWrites() {
         this.delegate.suspendWrites();
      }

      public void wakeupWrites() {
         this.delegate.wakeupWrites();
      }

      public boolean isWriteResumed() {
         return this.delegate.isWriteResumed();
      }

      public void awaitWritable() throws IOException {
         this.awaitWritable(this.remaining, TimeUnit.NANOSECONDS);
      }

      public void awaitWritable(long duration, TimeUnit unit) throws IOException {
         long startTime = System.nanoTime();
         long requestedNanos = unit.toNanos(duration);

         try {
            this.delegate.awaitWritable(Math.min(requestedNanos, this.remaining), TimeUnit.NANOSECONDS);
         } finally {
            this.remaining -= System.nanoTime() - startTime;
         }

         if (this.remaining < 0L) {
            WriteTimeoutException wte = UndertowMessages.MESSAGES.blockingWriteTimedOut(this.timeoutNanos);
            UndertowLogger.REQUEST_IO_LOGGER.blockingWriteTimedOut(wte);
            IoUtils.safeClose((Closeable)this.serverConnection);
            throw wte;
         }
      }

      public XnioIoThread getWriteThread() {
         return this.delegate.getWriteThread();
      }

      public void setWriteReadyHandler(WriteReadyHandler writeReadyHandler) {
         this.delegate.setWriteReadyHandler(writeReadyHandler);
      }

      public void truncateWrites() throws IOException {
         this.delegate.truncateWrites();
      }

      public boolean flush() throws IOException {
         return this.resetTimeoutIfWriteSucceeded(this.delegate.flush());
      }

      public XnioWorker getWorker() {
         return this.delegate.getWorker();
      }

      private long resetTimeoutIfWriteSucceeded(long value) {
         if (value != 0L) {
            this.remaining = this.timeoutNanos;
         }

         return value;
      }

      private int resetTimeoutIfWriteSucceeded(int value) {
         if (value != 0) {
            this.remaining = this.timeoutNanos;
         }

         return value;
      }

      private boolean resetTimeoutIfWriteSucceeded(boolean value) {
         if (value) {
            this.remaining = this.timeoutNanos;
         }

         return value;
      }
   }

   private static final class TimeoutStreamSinkConduitWrapper implements ConduitWrapper<StreamSinkConduit> {
      private final long timeoutNanoseconds;

      TimeoutStreamSinkConduitWrapper(Duration writeTimeout) {
         this.timeoutNanoseconds = writeTimeout.toNanos();
      }

      public StreamSinkConduit wrap(ConduitFactory<StreamSinkConduit> factory, HttpServerExchange exchange) {
         return new TimeoutStreamSinkConduit((StreamSinkConduit)factory.create(), exchange.getConnection(), this.timeoutNanoseconds);
      }
   }
}
