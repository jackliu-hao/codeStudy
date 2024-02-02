package io.undertow.protocols.ajp;

import io.undertow.connector.PooledByteBuffer;
import io.undertow.server.protocol.framed.FrameHeaderData;
import io.undertow.util.HeaderMap;
import java.io.IOException;
import org.xnio.ChannelListener;

public class AjpClientResponseStreamSourceChannel extends AbstractAjpClientStreamSourceChannel {
   private ChannelListener<AjpClientResponseStreamSourceChannel> finishListener;
   private final HeaderMap headers;
   private final int statusCode;
   private final String reasonPhrase;

   public AjpClientResponseStreamSourceChannel(AjpClientChannel framedChannel, HeaderMap headers, int statusCode, String reasonPhrase, PooledByteBuffer frameData, int remaining) {
      super(framedChannel, frameData, (long)remaining);
      this.headers = headers;
      this.statusCode = statusCode;
      this.reasonPhrase = reasonPhrase;
   }

   public HeaderMap getHeaders() {
      return this.headers;
   }

   public int getStatusCode() {
      return this.statusCode;
   }

   public String getReasonPhrase() {
      return this.reasonPhrase;
   }

   public void setFinishListener(ChannelListener<AjpClientResponseStreamSourceChannel> finishListener) {
      this.finishListener = finishListener;
   }

   protected void handleHeaderData(FrameHeaderData headerData) {
      if (headerData instanceof AjpClientChannel.EndResponse) {
         this.lastFrame();
      }

   }

   protected long updateFrameDataRemaining(PooledByteBuffer frameData, long frameDataRemaining) {
      if (frameDataRemaining > 0L && (long)frameData.getBuffer().remaining() == frameDataRemaining) {
         frameData.getBuffer().limit(frameData.getBuffer().limit() - 1);
         return frameDataRemaining - 1L;
      } else {
         return frameDataRemaining;
      }
   }

   protected void complete() throws IOException {
      if (this.finishListener != null) {
         ((AjpClientChannel)this.getFramedChannel()).sourceDone();
         this.finishListener.handleEvent(this);
      }

   }

   public void wakeupReads() {
      super.wakeupReads();
      ((AjpClientChannel)this.getFramedChannel()).resumeReceives();
   }

   public void resumeReads() {
      super.resumeReads();
      ((AjpClientChannel)this.getFramedChannel()).resumeReceives();
   }

   public void suspendReads() {
      ((AjpClientChannel)this.getFramedChannel()).suspendReceives();
      super.suspendReads();
   }
}
