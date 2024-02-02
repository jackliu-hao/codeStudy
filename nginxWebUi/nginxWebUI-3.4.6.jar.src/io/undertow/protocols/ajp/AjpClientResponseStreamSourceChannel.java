/*     */ package io.undertow.protocols.ajp;
/*     */ 
/*     */ import io.undertow.connector.PooledByteBuffer;
/*     */ import io.undertow.server.protocol.framed.FrameHeaderData;
/*     */ import io.undertow.util.HeaderMap;
/*     */ import java.io.IOException;
/*     */ import java.nio.channels.Channel;
/*     */ import org.xnio.ChannelListener;
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
/*     */ public class AjpClientResponseStreamSourceChannel
/*     */   extends AbstractAjpClientStreamSourceChannel
/*     */ {
/*     */   private ChannelListener<AjpClientResponseStreamSourceChannel> finishListener;
/*     */   private final HeaderMap headers;
/*     */   private final int statusCode;
/*     */   private final String reasonPhrase;
/*     */   
/*     */   public AjpClientResponseStreamSourceChannel(AjpClientChannel framedChannel, HeaderMap headers, int statusCode, String reasonPhrase, PooledByteBuffer frameData, int remaining) {
/*  40 */     super(framedChannel, frameData, remaining);
/*  41 */     this.headers = headers;
/*  42 */     this.statusCode = statusCode;
/*  43 */     this.reasonPhrase = reasonPhrase;
/*     */   }
/*     */   
/*     */   public HeaderMap getHeaders() {
/*  47 */     return this.headers;
/*     */   }
/*     */   
/*     */   public int getStatusCode() {
/*  51 */     return this.statusCode;
/*     */   }
/*     */   
/*     */   public String getReasonPhrase() {
/*  55 */     return this.reasonPhrase;
/*     */   }
/*     */   
/*     */   public void setFinishListener(ChannelListener<AjpClientResponseStreamSourceChannel> finishListener) {
/*  59 */     this.finishListener = finishListener;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void handleHeaderData(FrameHeaderData headerData) {
/*  64 */     if (headerData instanceof AjpClientChannel.EndResponse) {
/*  65 */       lastFrame();
/*     */     }
/*     */   }
/*     */   
/*     */   protected long updateFrameDataRemaining(PooledByteBuffer frameData, long frameDataRemaining) {
/*  70 */     if (frameDataRemaining > 0L && frameData.getBuffer().remaining() == frameDataRemaining) {
/*     */       
/*  72 */       frameData.getBuffer().limit(frameData.getBuffer().limit() - 1);
/*  73 */       return frameDataRemaining - 1L;
/*     */     } 
/*  75 */     return frameDataRemaining;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void complete() throws IOException {
/*  80 */     if (this.finishListener != null) {
/*  81 */       ((AjpClientChannel)getFramedChannel()).sourceDone();
/*  82 */       this.finishListener.handleEvent((Channel)this);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void wakeupReads() {
/*  88 */     super.wakeupReads();
/*  89 */     ((AjpClientChannel)getFramedChannel()).resumeReceives();
/*     */   }
/*     */ 
/*     */   
/*     */   public void resumeReads() {
/*  94 */     super.resumeReads();
/*  95 */     ((AjpClientChannel)getFramedChannel()).resumeReceives();
/*     */   }
/*     */ 
/*     */   
/*     */   public void suspendReads() {
/* 100 */     ((AjpClientChannel)getFramedChannel()).suspendReceives();
/* 101 */     super.suspendReads();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\protocols\ajp\AjpClientResponseStreamSourceChannel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */