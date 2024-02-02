package io.undertow.protocols.http2;

import io.undertow.UndertowMessages;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import org.xnio.channels.StreamSourceChannel;

abstract class Http2NoDataStreamSinkChannel extends AbstractHttp2StreamSinkChannel {
   protected Http2NoDataStreamSinkChannel(Http2Channel channel) {
      super(channel);
   }

   public long transferFrom(FileChannel src, long position, long count) throws IOException {
      throw UndertowMessages.MESSAGES.controlFrameCannotHaveBodyContent();
   }

   public long transferFrom(StreamSourceChannel source, long count, ByteBuffer throughBuffer) throws IOException {
      throw UndertowMessages.MESSAGES.controlFrameCannotHaveBodyContent();
   }

   public long write(ByteBuffer[] srcs, int offset, int length) throws IOException {
      throw UndertowMessages.MESSAGES.controlFrameCannotHaveBodyContent();
   }

   public long write(ByteBuffer[] srcs) throws IOException {
      throw UndertowMessages.MESSAGES.controlFrameCannotHaveBodyContent();
   }

   public int write(ByteBuffer src) throws IOException {
      throw UndertowMessages.MESSAGES.controlFrameCannotHaveBodyContent();
   }

   public long writeFinal(ByteBuffer[] srcs, int offset, int length) throws IOException {
      throw UndertowMessages.MESSAGES.controlFrameCannotHaveBodyContent();
   }

   public long writeFinal(ByteBuffer[] srcs) throws IOException {
      throw UndertowMessages.MESSAGES.controlFrameCannotHaveBodyContent();
   }

   public int writeFinal(ByteBuffer src) throws IOException {
      throw UndertowMessages.MESSAGES.controlFrameCannotHaveBodyContent();
   }
}
