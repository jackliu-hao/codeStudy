package io.undertow.websockets.core;

import io.undertow.UndertowMessages;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import org.xnio.channels.Channels;

public final class BinaryOutputStream extends OutputStream {
   private final StreamSinkFrameChannel sender;
   private boolean closed;

   public BinaryOutputStream(StreamSinkFrameChannel sender) {
      this.sender = sender;
   }

   public void write(byte[] b, int off, int len) throws IOException {
      this.checkClosed();
      if (Thread.currentThread() == this.sender.getIoThread()) {
         throw UndertowMessages.MESSAGES.awaitCalledFromIoThread();
      } else {
         Channels.writeBlocking(this.sender, ByteBuffer.wrap(b, off, len));
      }
   }

   public void write(int b) throws IOException {
      this.checkClosed();
      if (Thread.currentThread() == this.sender.getIoThread()) {
         throw UndertowMessages.MESSAGES.awaitCalledFromIoThread();
      } else {
         Channels.writeBlocking(this.sender, ByteBuffer.wrap(new byte[]{(byte)b}));
      }
   }

   public void flush() throws IOException {
      this.checkClosed();
      if (Thread.currentThread() == this.sender.getIoThread()) {
         throw UndertowMessages.MESSAGES.awaitCalledFromIoThread();
      } else {
         this.sender.flush();
      }
   }

   public void close() throws IOException {
      if (!this.closed) {
         this.closed = true;
         this.sender.shutdownWrites();
         Channels.flushBlocking(this.sender);
      }

   }

   private void checkClosed() throws IOException {
      if (this.closed) {
         throw UndertowMessages.MESSAGES.streamIsClosed();
      }
   }
}
