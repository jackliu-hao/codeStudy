package io.undertow.server.protocol.framed;

public interface FrameHeaderData {
   long getFrameLength();

   AbstractFramedStreamSourceChannel<?, ?, ?> getExistingChannel();
}
