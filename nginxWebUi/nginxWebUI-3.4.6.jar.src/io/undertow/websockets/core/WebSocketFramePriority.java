/*     */ package io.undertow.websockets.core;
/*     */ 
/*     */ import io.undertow.server.protocol.framed.AbstractFramedStreamSinkChannel;
/*     */ import io.undertow.server.protocol.framed.FramePriority;
/*     */ import java.util.Deque;
/*     */ import java.util.List;
/*     */ import java.util.Queue;
/*     */ import java.util.concurrent.ConcurrentLinkedDeque;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class WebSocketFramePriority
/*     */   implements FramePriority<WebSocketChannel, StreamSourceFrameChannel, StreamSinkFrameChannel>
/*     */ {
/*  42 */   private final Queue<StreamSinkFrameChannel> strictOrderQueue = new ConcurrentLinkedDeque<>();
/*     */   
/*     */   private StreamSinkFrameChannel currentFragmentedSender;
/*     */   
/*     */   boolean closed = false;
/*     */   boolean immediateCloseFrame = false;
/*     */   
/*     */   public boolean insertFrame(StreamSinkFrameChannel newFrame, List<StreamSinkFrameChannel> pendingFrames) {
/*  50 */     if (newFrame.getType() != WebSocketFrameType.PONG && newFrame
/*  51 */       .getType() != WebSocketFrameType.PING) {
/*  52 */       StreamSinkFrameChannel order = this.strictOrderQueue.peek();
/*  53 */       if (order != null) {
/*  54 */         if (order != newFrame && order.isOpen()) {
/*     */ 
/*     */ 
/*     */           
/*  58 */           if (newFrame.getType() != WebSocketFrameType.CLOSE)
/*  59 */             return false; 
/*  60 */           if (!newFrame.getWebSocketChannel().isCloseFrameReceived() && !this.immediateCloseFrame) {
/*  61 */             return false;
/*     */           }
/*     */         } 
/*  64 */         if (order == newFrame && newFrame.isFinalFragment()) {
/*  65 */           this.strictOrderQueue.poll();
/*     */         }
/*     */       } 
/*     */     } 
/*     */     
/*  70 */     if (this.closed) {
/*     */       
/*  72 */       newFrame.markBroken();
/*  73 */       return true;
/*     */     } 
/*  75 */     if (this.currentFragmentedSender == null) {
/*     */       
/*  77 */       if (!newFrame.isWritesShutdown())
/*     */       {
/*  79 */         this.currentFragmentedSender = newFrame;
/*     */       }
/*  81 */       if (pendingFrames.isEmpty()) {
/*  82 */         pendingFrames.add(newFrame);
/*  83 */       } else if (newFrame.getType() == WebSocketFrameType.PING || newFrame
/*  84 */         .getType() == WebSocketFrameType.PONG) {
/*     */         
/*  86 */         int index = 1;
/*  87 */         boolean done = false;
/*     */         
/*  89 */         while (index < pendingFrames.size()) {
/*  90 */           WebSocketFrameType type = ((StreamSinkFrameChannel)pendingFrames.get(index)).getType();
/*  91 */           if (type != WebSocketFrameType.PING && type != WebSocketFrameType.PONG) {
/*  92 */             pendingFrames.add(index, newFrame);
/*  93 */             done = true;
/*     */             break;
/*     */           } 
/*  96 */           index++;
/*     */         } 
/*  98 */         if (!done) {
/*  99 */           pendingFrames.add(newFrame);
/*     */         }
/*     */       } else {
/* 102 */         pendingFrames.add(newFrame);
/*     */       } 
/* 104 */     } else if (newFrame.getType() == WebSocketFrameType.PING || newFrame
/* 105 */       .getType() == WebSocketFrameType.PONG) {
/*     */       
/* 107 */       if (pendingFrames.isEmpty()) {
/* 108 */         pendingFrames.add(newFrame);
/*     */       } else {
/* 110 */         pendingFrames.add(1, newFrame);
/*     */       } 
/*     */     } else {
/*     */       
/* 114 */       if (this.currentFragmentedSender != newFrame) {
/* 115 */         return false;
/*     */       }
/* 117 */       if (newFrame.isFinalFragment()) {
/* 118 */         this.currentFragmentedSender = null;
/*     */       }
/* 120 */       pendingFrames.add(newFrame);
/*     */     } 
/*     */     
/* 123 */     if (newFrame.getType() == WebSocketFrameType.CLOSE) {
/* 124 */       this.closed = true;
/*     */     }
/* 126 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public void frameAdded(StreamSinkFrameChannel addedFrame, List<StreamSinkFrameChannel> pendingFrames, Deque<StreamSinkFrameChannel> holdFrames) {
/* 131 */     if (addedFrame.isFinalFragment()) {
/*     */       while (true) {
/* 133 */         StreamSinkFrameChannel frame = this.strictOrderQueue.peek();
/* 134 */         if (frame == null) {
/*     */           break;
/*     */         }
/* 137 */         if (holdFrames.contains(frame) && 
/* 138 */           insertFrame(frame, pendingFrames)) {
/* 139 */           holdFrames.remove(frame);
/*     */           
/*     */           continue;
/*     */         } 
/*     */         
/*     */         break;
/*     */       } 
/*     */       
/* 147 */       while (!holdFrames.isEmpty()) {
/* 148 */         StreamSinkFrameChannel frame = holdFrames.peek();
/* 149 */         if (insertFrame(frame, pendingFrames)) {
/* 150 */           holdFrames.poll();
/*     */           continue;
/*     */         } 
/*     */         return;
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   void addToOrderQueue(StreamSinkFrameChannel channel) {
/* 159 */     if (channel.getType() != WebSocketFrameType.PING && channel.getType() != WebSocketFrameType.PONG) {
/* 160 */       this.strictOrderQueue.add(channel);
/*     */     }
/*     */   }
/*     */   
/*     */   void immediateCloseFrame() {
/* 165 */     this.immediateCloseFrame = true;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\websockets\core\WebSocketFramePriority.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */