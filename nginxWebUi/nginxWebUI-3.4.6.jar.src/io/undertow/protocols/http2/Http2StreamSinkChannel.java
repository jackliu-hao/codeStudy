/*     */ package io.undertow.protocols.http2;
/*     */ 
/*     */ import io.undertow.UndertowMessages;
/*     */ import io.undertow.connector.PooledByteBuffer;
/*     */ import io.undertow.server.protocol.framed.SendFrameHeader;
/*     */ import java.io.Closeable;
/*     */ import java.io.IOException;
/*     */ import java.nio.ByteBuffer;
/*     */ import org.xnio.IoUtils;
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
/*     */ public abstract class Http2StreamSinkChannel
/*     */   extends AbstractHttp2StreamSinkChannel
/*     */ {
/*     */   private final int streamId;
/*     */   private volatile boolean reset = false;
/*     */   private int flowControlWindow;
/*     */   private int initialWindowSize;
/*     */   private SendFrameHeader header;
/*  43 */   private static final Object flowControlLock = new Object();
/*     */   
/*     */   Http2StreamSinkChannel(Http2Channel channel, int streamId) {
/*  46 */     super(channel);
/*  47 */     this.streamId = streamId;
/*  48 */     this.flowControlWindow = channel.getInitialSendWindowSize();
/*  49 */     this.initialWindowSize = this.flowControlWindow;
/*     */   }
/*     */   
/*     */   public int getStreamId() {
/*  53 */     return this.streamId;
/*     */   }
/*     */   
/*     */   SendFrameHeader generateSendFrameHeader() {
/*  57 */     this.header = createFrameHeaderImpl();
/*  58 */     return this.header;
/*     */   }
/*     */   
/*     */   protected abstract SendFrameHeader createFrameHeaderImpl();
/*     */   
/*     */   void clearHeader() {
/*  64 */     this.header = null;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void channelForciblyClosed() throws IOException {
/*  69 */     ((Http2Channel)getChannel()).removeStreamSink(getStreamId());
/*  70 */     if (this.reset) {
/*     */       return;
/*     */     }
/*  73 */     this.reset = true;
/*  74 */     if (this.streamId % 2 == (((Http2Channel)getChannel()).isClient() ? 1 : 0)) {
/*     */ 
/*     */       
/*  77 */       if (isFirstDataWritten() && !((Http2Channel)getChannel()).isThisGoneAway()) {
/*  78 */         ((Http2Channel)getChannel()).sendRstStream(this.streamId, 8);
/*     */       }
/*  80 */     } else if (!((Http2Channel)getChannel()).isThisGoneAway()) {
/*  81 */       ((Http2Channel)getChannel()).sendRstStream(this.streamId, 5);
/*     */     } 
/*  83 */     markBroken();
/*     */   }
/*     */ 
/*     */   
/*     */   protected final SendFrameHeader createFrameHeader() {
/*  88 */     SendFrameHeader header = this.header;
/*  89 */     this.header = null;
/*  90 */     return header;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void handleFlushComplete(boolean channelClosed) {
/*  95 */     if (channelClosed) {
/*  96 */       ((Http2Channel)getChannel()).removeStreamSink(getStreamId());
/*     */     }
/*  98 */     if (this.reset) {
/*  99 */       IoUtils.safeClose((Closeable)this);
/*     */     }
/*     */   }
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
/*     */   protected int grabFlowControlBytes(int toSend) {
/* 114 */     synchronized (flowControlLock) {
/* 115 */       if (toSend == 0) {
/* 116 */         return 0;
/*     */       }
/* 118 */       int newWindowSize = ((Http2Channel)getChannel()).getInitialSendWindowSize();
/* 119 */       int settingsDelta = newWindowSize - this.initialWindowSize;
/*     */       
/* 121 */       this.initialWindowSize = newWindowSize;
/* 122 */       this.flowControlWindow += settingsDelta;
/*     */       
/* 124 */       int min = Math.min(toSend, this.flowControlWindow);
/* 125 */       int actualBytes = ((Http2Channel)getChannel()).grabFlowControlBytes(min);
/* 126 */       this.flowControlWindow -= actualBytes;
/* 127 */       return actualBytes;
/*     */     } 
/*     */   }
/*     */   
/*     */   void updateFlowControlWindow(int delta) throws IOException {
/*     */     boolean exhausted;
/* 133 */     synchronized (flowControlLock) {
/* 134 */       exhausted = (this.flowControlWindow <= 0);
/* 135 */       long ld = delta;
/* 136 */       ld += this.flowControlWindow;
/* 137 */       if (ld > 2147483647L) {
/* 138 */         ((Http2Channel)getChannel()).sendRstStream(this.streamId, 3);
/* 139 */         markBroken();
/*     */         return;
/*     */       } 
/* 142 */       this.flowControlWindow += delta;
/*     */     } 
/* 144 */     if (exhausted) {
/* 145 */       ((Http2Channel)getChannel()).notifyFlowControlAllowed();
/* 146 */       if (isWriteResumed()) {
/* 147 */         resumeWritesInternal(true);
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   protected PooledByteBuffer[] allocateAll(PooledByteBuffer[] allHeaderBuffers, PooledByteBuffer currentBuffer) {
/*     */     PooledByteBuffer[] ret;
/* 155 */     if (allHeaderBuffers == null) {
/* 156 */       ret = new PooledByteBuffer[2];
/* 157 */       ret[0] = currentBuffer;
/* 158 */       ret[1] = ((Http2Channel)getChannel()).getBufferPool().allocate();
/* 159 */       ByteBuffer newBuffer = ret[1].getBuffer();
/* 160 */       if (newBuffer.remaining() > ((Http2Channel)getChannel()).getSendMaxFrameSize()) {
/* 161 */         newBuffer.limit(newBuffer.position() + ((Http2Channel)getChannel()).getSendMaxFrameSize());
/*     */       }
/*     */     } else {
/* 164 */       ret = new PooledByteBuffer[allHeaderBuffers.length + 1];
/* 165 */       System.arraycopy(allHeaderBuffers, 0, ret, 0, allHeaderBuffers.length);
/* 166 */       ret[ret.length - 1] = ((Http2Channel)getChannel()).getBufferPool().allocate();
/* 167 */       ByteBuffer newBuffer = ret[ret.length - 1].getBuffer();
/* 168 */       if (newBuffer.remaining() > ((Http2Channel)getChannel()).getSendMaxFrameSize()) {
/* 169 */         newBuffer.limit(newBuffer.position() + ((Http2Channel)getChannel()).getSendMaxFrameSize());
/*     */       }
/*     */     } 
/* 172 */     return ret;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void awaitWritable() throws IOException {
/*     */     int flowControlWindow;
/* 183 */     synchronized (flowControlLock) {
/* 184 */       flowControlWindow = this.flowControlWindow;
/*     */     } 
/* 186 */     super.awaitWritable();
/* 187 */     synchronized (flowControlLock) {
/* 188 */       if (isReadyForFlush() && flowControlWindow <= 0 && flowControlWindow == this.flowControlWindow) {
/* 189 */         throw UndertowMessages.MESSAGES.noWindowUpdate(getAwaitWritableTimeout());
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   void rstStream() {
/* 198 */     if (this.reset) {
/*     */       return;
/*     */     }
/* 201 */     this.reset = true;
/* 202 */     if (!isReadyForFlush()) {
/* 203 */       IoUtils.safeClose((Closeable)this);
/*     */     }
/* 205 */     ((Http2Channel)getChannel()).removeStreamSink(getStreamId());
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\protocols\http2\Http2StreamSinkChannel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */