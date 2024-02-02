/*    */ package org.xnio.channels;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import javax.net.ssl.SSLSession;
/*    */ import org.xnio.ChannelListener;
/*    */ import org.xnio.ChannelListeners;
/*    */ import org.xnio._private.Messages;
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
/*    */ 
/*    */ 
/*    */ public class AssembledSslChannel
/*    */   extends AssembledConnectedChannel
/*    */   implements SslChannel
/*    */ {
/*    */   private final SslChannel sslChannel;
/*    */   private final ChannelListener.Setter<AssembledSslChannel> handshakeSetter;
/*    */   
/*    */   public AssembledSslChannel(SuspendableReadChannel readChannel, SuspendableWriteChannel writeChannel) {
/* 48 */     super(readChannel, writeChannel);
/* 49 */     if (readChannel instanceof SslChannel) {
/* 50 */       this.sslChannel = (SslChannel)readChannel;
/* 51 */     } else if (writeChannel instanceof SslChannel) {
/* 52 */       this.sslChannel = (SslChannel)writeChannel;
/*    */     } else {
/* 54 */       throw Messages.msg.oneChannelMustBeSSL();
/*    */     } 
/* 56 */     this.handshakeSetter = ChannelListeners.getDelegatingSetter(this.sslChannel.getHandshakeSetter(), this);
/*    */   }
/*    */   
/*    */   public void startHandshake() throws IOException {
/* 60 */     this.sslChannel.startHandshake();
/*    */   }
/*    */   
/*    */   public SSLSession getSslSession() {
/* 64 */     return this.sslChannel.getSslSession();
/*    */   }
/*    */   
/*    */   public ChannelListener.Setter<? extends AssembledSslChannel> getHandshakeSetter() {
/* 68 */     return this.handshakeSetter;
/*    */   }
/*    */ 
/*    */   
/*    */   public ChannelListener.Setter<? extends AssembledSslChannel> getCloseSetter() {
/* 73 */     return (ChannelListener.Setter)super.getCloseSetter();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\channels\AssembledSslChannel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */