package io.undertow.protocols.http2;

import io.undertow.UndertowLogger;
import io.undertow.server.protocol.framed.FramePriority;
import io.undertow.server.protocol.framed.SendFrameHeader;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;

class Http2FramePriority implements FramePriority<Http2Channel, AbstractHttp2StreamSourceChannel, AbstractHttp2StreamSinkChannel> {
   private int nextId;

   Http2FramePriority(int nextId) {
      this.nextId = nextId;
   }

   public boolean insertFrame(AbstractHttp2StreamSinkChannel newFrame, List<AbstractHttp2StreamSinkChannel> pendingFrames) {
      boolean incrementIfAccepted = false;
      if (((Http2Channel)newFrame.getChannel()).isClient() && newFrame instanceof Http2HeadersStreamSinkChannel || newFrame instanceof Http2PushPromiseStreamSinkChannel) {
         int streamId;
         if (newFrame instanceof Http2PushPromiseStreamSinkChannel) {
            streamId = ((Http2PushPromiseStreamSinkChannel)newFrame).getPushedStreamId();
            if (streamId > this.nextId) {
               return false;
            }

            if (streamId == this.nextId) {
               incrementIfAccepted = true;
            }
         } else {
            streamId = ((Http2HeadersStreamSinkChannel)newFrame).getStreamId();
            if (streamId > this.nextId) {
               return false;
            }

            if (streamId == this.nextId) {
               incrementIfAccepted = true;
            }
         }
      }

      if (newFrame instanceof Http2StreamSinkChannel) {
         if (newFrame.isBroken() || !newFrame.isOpen()) {
            return true;
         }

         try {
            SendFrameHeader header = ((Http2StreamSinkChannel)newFrame).generateSendFrameHeader();
            if (header.getByteBuffer() == null) {
               ((Http2StreamSinkChannel)newFrame).clearHeader();
               return false;
            }
         } catch (Exception var5) {
            UndertowLogger.REQUEST_LOGGER.debugf("Failed to generate header %s", newFrame);
         }
      }

      pendingFrames.add(newFrame);
      if (incrementIfAccepted) {
         this.nextId += 2;
      }

      return true;
   }

   public void frameAdded(AbstractHttp2StreamSinkChannel addedFrame, List<AbstractHttp2StreamSinkChannel> pendingFrames, Deque<AbstractHttp2StreamSinkChannel> holdFrames) {
      Iterator<AbstractHttp2StreamSinkChannel> it = holdFrames.iterator();

      while(true) {
         AbstractHttp2StreamSinkChannel pending;
         boolean incrementNextId;
         while(true) {
            if (!it.hasNext()) {
               return;
            }

            pending = (AbstractHttp2StreamSinkChannel)it.next();
            incrementNextId = false;
            if ((!((Http2Channel)pending.getChannel()).isClient() || !(pending instanceof Http2HeadersStreamSinkChannel)) && !(pending instanceof Http2PushPromiseStreamSinkChannel)) {
               break;
            }

            int streamId;
            if (pending instanceof Http2PushPromiseStreamSinkChannel) {
               streamId = ((Http2PushPromiseStreamSinkChannel)pending).getPushedStreamId();
               if (streamId <= this.nextId) {
                  if (streamId == this.nextId) {
                     incrementNextId = true;
                  }
                  break;
               }
            } else {
               streamId = ((Http2HeadersStreamSinkChannel)pending).getStreamId();
               if (streamId <= this.nextId) {
                  if (streamId == this.nextId) {
                     incrementNextId = true;
                  }
                  break;
               }
            }
         }

         if (pending instanceof Http2StreamSinkChannel) {
            SendFrameHeader header = ((Http2StreamSinkChannel)pending).generateSendFrameHeader();
            if (header.getByteBuffer() != null) {
               pendingFrames.add(pending);
               it.remove();
               it = holdFrames.iterator();
               if (incrementNextId) {
                  this.nextId += 2;
               }
            } else {
               ((Http2StreamSinkChannel)pending).clearHeader();
            }
         }
      }
   }
}
