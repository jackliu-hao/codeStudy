package io.undertow.protocols.http2;

public class Http2PingStreamSourceChannel extends AbstractHttp2StreamSourceChannel {
   private final byte[] data;
   private final boolean ack;

   Http2PingStreamSourceChannel(Http2Channel framedChannel, byte[] pingData, boolean ack) {
      super(framedChannel);
      this.data = pingData;
      this.ack = ack;
      this.lastFrame();
   }

   public byte[] getData() {
      return this.data;
   }

   public boolean isAck() {
      return this.ack;
   }
}
