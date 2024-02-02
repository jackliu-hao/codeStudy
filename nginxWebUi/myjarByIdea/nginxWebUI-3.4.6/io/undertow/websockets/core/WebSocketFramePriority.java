package io.undertow.websockets.core;

import io.undertow.server.protocol.framed.FramePriority;
import java.util.Deque;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;

public class WebSocketFramePriority implements FramePriority<WebSocketChannel, StreamSourceFrameChannel, StreamSinkFrameChannel> {
   private final Queue<StreamSinkFrameChannel> strictOrderQueue = new ConcurrentLinkedDeque();
   private StreamSinkFrameChannel currentFragmentedSender;
   boolean closed = false;
   boolean immediateCloseFrame = false;

   public boolean insertFrame(StreamSinkFrameChannel newFrame, List<StreamSinkFrameChannel> pendingFrames) {
      if (newFrame.getType() != WebSocketFrameType.PONG && newFrame.getType() != WebSocketFrameType.PING) {
         StreamSinkFrameChannel order = (StreamSinkFrameChannel)this.strictOrderQueue.peek();
         if (order != null) {
            if (order != newFrame && order.isOpen()) {
               if (newFrame.getType() != WebSocketFrameType.CLOSE) {
                  return false;
               }

               if (!newFrame.getWebSocketChannel().isCloseFrameReceived() && !this.immediateCloseFrame) {
                  return false;
               }
            }

            if (order == newFrame && newFrame.isFinalFragment()) {
               this.strictOrderQueue.poll();
            }
         }
      }

      if (this.closed) {
         newFrame.markBroken();
         return true;
      } else {
         if (this.currentFragmentedSender == null) {
            if (!newFrame.isWritesShutdown()) {
               this.currentFragmentedSender = newFrame;
            }

            if (pendingFrames.isEmpty()) {
               pendingFrames.add(newFrame);
            } else if (newFrame.getType() != WebSocketFrameType.PING && newFrame.getType() != WebSocketFrameType.PONG) {
               pendingFrames.add(newFrame);
            } else {
               int index = 1;

               boolean done;
               for(done = false; index < pendingFrames.size(); ++index) {
                  WebSocketFrameType type = ((StreamSinkFrameChannel)pendingFrames.get(index)).getType();
                  if (type != WebSocketFrameType.PING && type != WebSocketFrameType.PONG) {
                     pendingFrames.add(index, newFrame);
                     done = true;
                     break;
                  }
               }

               if (!done) {
                  pendingFrames.add(newFrame);
               }
            }
         } else if (newFrame.getType() != WebSocketFrameType.PING && newFrame.getType() != WebSocketFrameType.PONG) {
            if (this.currentFragmentedSender != newFrame) {
               return false;
            }

            if (newFrame.isFinalFragment()) {
               this.currentFragmentedSender = null;
            }

            pendingFrames.add(newFrame);
         } else if (pendingFrames.isEmpty()) {
            pendingFrames.add(newFrame);
         } else {
            pendingFrames.add(1, newFrame);
         }

         if (newFrame.getType() == WebSocketFrameType.CLOSE) {
            this.closed = true;
         }

         return true;
      }
   }

   public void frameAdded(StreamSinkFrameChannel addedFrame, List<StreamSinkFrameChannel> pendingFrames, Deque<StreamSinkFrameChannel> holdFrames) {
      if (addedFrame.isFinalFragment()) {
         while(true) {
            StreamSinkFrameChannel frame = (StreamSinkFrameChannel)this.strictOrderQueue.peek();
            if (frame == null || !holdFrames.contains(frame) || !this.insertFrame(frame, pendingFrames)) {
               while(!holdFrames.isEmpty()) {
                  frame = (StreamSinkFrameChannel)holdFrames.peek();
                  if (!this.insertFrame(frame, pendingFrames)) {
                     return;
                  }

                  holdFrames.poll();
               }

               return;
            }

            holdFrames.remove(frame);
         }
      }
   }

   void addToOrderQueue(StreamSinkFrameChannel channel) {
      if (channel.getType() != WebSocketFrameType.PING && channel.getType() != WebSocketFrameType.PONG) {
         this.strictOrderQueue.add(channel);
      }

   }

   void immediateCloseFrame() {
      this.immediateCloseFrame = true;
   }
}
