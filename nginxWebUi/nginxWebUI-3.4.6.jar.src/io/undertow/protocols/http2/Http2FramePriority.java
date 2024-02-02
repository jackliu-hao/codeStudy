/*     */ package io.undertow.protocols.http2;
/*     */ 
/*     */ import io.undertow.UndertowLogger;
/*     */ import io.undertow.server.protocol.framed.AbstractFramedStreamSinkChannel;
/*     */ import io.undertow.server.protocol.framed.FramePriority;
/*     */ import io.undertow.server.protocol.framed.SendFrameHeader;
/*     */ import java.util.Deque;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
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
/*     */ class Http2FramePriority
/*     */   implements FramePriority<Http2Channel, AbstractHttp2StreamSourceChannel, AbstractHttp2StreamSinkChannel>
/*     */ {
/*     */   private int nextId;
/*     */   
/*     */   Http2FramePriority(int nextId) {
/*  39 */     this.nextId = nextId;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean insertFrame(AbstractHttp2StreamSinkChannel newFrame, List<AbstractHttp2StreamSinkChannel> pendingFrames) {
/*  46 */     boolean incrementIfAccepted = false;
/*  47 */     if ((((Http2Channel)newFrame.getChannel()).isClient() && newFrame instanceof Http2HeadersStreamSinkChannel) || newFrame instanceof Http2PushPromiseStreamSinkChannel)
/*     */     {
/*  49 */       if (newFrame instanceof Http2PushPromiseStreamSinkChannel) {
/*  50 */         int streamId = ((Http2PushPromiseStreamSinkChannel)newFrame).getPushedStreamId();
/*  51 */         if (streamId > this.nextId)
/*  52 */           return false; 
/*  53 */         if (streamId == this.nextId) {
/*  54 */           incrementIfAccepted = true;
/*     */         }
/*     */       } else {
/*  57 */         int streamId = ((Http2HeadersStreamSinkChannel)newFrame).getStreamId();
/*  58 */         if (streamId > this.nextId)
/*  59 */           return false; 
/*  60 */         if (streamId == this.nextId) {
/*  61 */           incrementIfAccepted = true;
/*     */         }
/*     */       } 
/*     */     }
/*     */     
/*  66 */     if (newFrame instanceof Http2StreamSinkChannel) {
/*  67 */       if (newFrame.isBroken() || !newFrame.isOpen()) {
/*  68 */         return true;
/*     */       }
/*     */       try {
/*  71 */         SendFrameHeader header = ((Http2StreamSinkChannel)newFrame).generateSendFrameHeader();
/*     */         
/*  73 */         if (header.getByteBuffer() == null) {
/*     */           
/*  75 */           ((Http2StreamSinkChannel)newFrame).clearHeader();
/*  76 */           return false;
/*     */         } 
/*  78 */       } catch (Exception e) {
/*  79 */         UndertowLogger.REQUEST_LOGGER.debugf("Failed to generate header %s", newFrame);
/*     */       } 
/*     */     } 
/*     */     
/*  83 */     pendingFrames.add(newFrame);
/*  84 */     if (incrementIfAccepted) {
/*  85 */       this.nextId += 2;
/*     */     }
/*  87 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public void frameAdded(AbstractHttp2StreamSinkChannel addedFrame, List<AbstractHttp2StreamSinkChannel> pendingFrames, Deque<AbstractHttp2StreamSinkChannel> holdFrames) {
/*  92 */     Iterator<AbstractHttp2StreamSinkChannel> it = holdFrames.iterator();
/*  93 */     while (it.hasNext()) {
/*  94 */       AbstractHttp2StreamSinkChannel pending = it.next();
/*  95 */       boolean incrementNextId = false;
/*  96 */       if ((((Http2Channel)pending.getChannel()).isClient() && pending instanceof Http2HeadersStreamSinkChannel) || pending instanceof Http2PushPromiseStreamSinkChannel)
/*     */       {
/*  98 */         if (pending instanceof Http2PushPromiseStreamSinkChannel) {
/*  99 */           int streamId = ((Http2PushPromiseStreamSinkChannel)pending).getPushedStreamId();
/* 100 */           if (streamId > this.nextId)
/*     */             continue; 
/* 102 */           if (streamId == this.nextId) {
/* 103 */             incrementNextId = true;
/*     */           }
/*     */         } else {
/* 106 */           int streamId = ((Http2HeadersStreamSinkChannel)pending).getStreamId();
/* 107 */           if (streamId > this.nextId)
/*     */             continue; 
/* 109 */           if (streamId == this.nextId) {
/* 110 */             incrementNextId = true;
/*     */           }
/*     */         } 
/*     */       }
/*     */       
/* 115 */       if (pending instanceof Http2StreamSinkChannel) {
/* 116 */         SendFrameHeader header = ((Http2StreamSinkChannel)pending).generateSendFrameHeader();
/* 117 */         if (header.getByteBuffer() != null) {
/* 118 */           pendingFrames.add(pending);
/* 119 */           it.remove();
/* 120 */           it = holdFrames.iterator();
/* 121 */           if (incrementNextId) {
/* 122 */             this.nextId += 2;
/*     */           }
/*     */           continue;
/*     */         } 
/* 126 */         ((Http2StreamSinkChannel)pending).clearHeader();
/*     */       } 
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\protocols\http2\Http2FramePriority.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */