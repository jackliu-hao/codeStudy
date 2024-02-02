/*    */ package org.xnio.channels;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import javax.net.ssl.SSLSession;
/*    */ import org.xnio.ChannelListener;
/*    */ import org.xnio.ChannelListeners;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class AssembledConnectedSslStreamChannel
/*    */   extends AssembledConnectedStreamChannel
/*    */   implements ConnectedSslStreamChannel
/*    */ {
/*    */   private final SslChannel sslChannel;
/*    */   private final ChannelListener.Setter<AssembledConnectedSslStreamChannel> handshakeSetter;
/*    */   
/*    */   public AssembledConnectedSslStreamChannel(SslChannel sslChannel, StreamSourceChannel source, StreamSinkChannel sink) {
/* 45 */     super(sslChannel, source, sink);
/* 46 */     this.sslChannel = sslChannel;
/* 47 */     this.handshakeSetter = ChannelListeners.getDelegatingSetter(sslChannel.getHandshakeSetter(), this);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public AssembledConnectedSslStreamChannel(StreamSourceChannel source, StreamSinkChannel sink) {
/* 57 */     this(new AssembledSslChannel(source, sink), source, sink);
/*    */   }
/*    */   
/*    */   public void startHandshake() throws IOException {
/* 61 */     this.sslChannel.startHandshake();
/*    */   }
/*    */   
/*    */   public SSLSession getSslSession() {
/* 65 */     return this.sslChannel.getSslSession();
/*    */   }
/*    */   
/*    */   public ChannelListener.Setter<? extends AssembledConnectedSslStreamChannel> getHandshakeSetter() {
/* 69 */     return this.handshakeSetter;
/*    */   }
/*    */ 
/*    */   
/*    */   public ChannelListener.Setter<? extends AssembledConnectedSslStreamChannel> getCloseSetter() {
/* 74 */     return (ChannelListener.Setter)super.getCloseSetter();
/*    */   }
/*    */ 
/*    */   
/*    */   public ChannelListener.Setter<? extends AssembledConnectedSslStreamChannel> getReadSetter() {
/* 79 */     return (ChannelListener.Setter)super.getReadSetter();
/*    */   }
/*    */ 
/*    */   
/*    */   public ChannelListener.Setter<? extends AssembledConnectedSslStreamChannel> getWriteSetter() {
/* 84 */     return (ChannelListener.Setter)super.getWriteSetter();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\channels\AssembledConnectedSslStreamChannel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */