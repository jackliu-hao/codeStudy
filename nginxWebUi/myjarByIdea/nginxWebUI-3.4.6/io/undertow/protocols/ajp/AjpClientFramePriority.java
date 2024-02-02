package io.undertow.protocols.ajp;

import io.undertow.server.protocol.framed.FramePriority;
import io.undertow.server.protocol.framed.SendFrameHeader;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;

class AjpClientFramePriority implements FramePriority<AjpClientChannel, AbstractAjpClientStreamSourceChannel, AbstractAjpClientStreamSinkChannel> {
   public static AjpClientFramePriority INSTANCE = new AjpClientFramePriority();

   public boolean insertFrame(AbstractAjpClientStreamSinkChannel newFrame, List<AbstractAjpClientStreamSinkChannel> pendingFrames) {
      if (newFrame instanceof AjpClientRequestClientStreamSinkChannel) {
         SendFrameHeader header = ((AjpClientRequestClientStreamSinkChannel)newFrame).generateSendFrameHeader();
         if (header.getByteBuffer() == null) {
            ((AjpClientRequestClientStreamSinkChannel)newFrame).clearHeader();
            return false;
         }
      }

      pendingFrames.add(newFrame);
      return true;
   }

   public void frameAdded(AbstractAjpClientStreamSinkChannel addedFrame, List<AbstractAjpClientStreamSinkChannel> pendingFrames, Deque<AbstractAjpClientStreamSinkChannel> holdFrames) {
      Iterator<AbstractAjpClientStreamSinkChannel> it = holdFrames.iterator();

      while(it.hasNext()) {
         AbstractAjpClientStreamSinkChannel pending = (AbstractAjpClientStreamSinkChannel)it.next();
         if (pending instanceof AjpClientRequestClientStreamSinkChannel) {
            SendFrameHeader header = ((AjpClientRequestClientStreamSinkChannel)pending).generateSendFrameHeader();
            if (header.getByteBuffer() != null) {
               pendingFrames.add(pending);
               it.remove();
            } else {
               ((AjpClientRequestClientStreamSinkChannel)pending).clearHeader();
            }
         }
      }

   }
}
