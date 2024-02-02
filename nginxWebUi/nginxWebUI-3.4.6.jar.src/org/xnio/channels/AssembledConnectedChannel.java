/*    */ package org.xnio.channels;
/*    */ 
/*    */ import java.net.SocketAddress;
/*    */ import org.xnio.ChannelListener;
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
/*    */ public class AssembledConnectedChannel
/*    */   extends AssembledChannel
/*    */   implements ConnectedChannel
/*    */ {
/*    */   private final ConnectedChannel connection;
/*    */   
/*    */   public AssembledConnectedChannel(SuspendableReadChannel readChannel, SuspendableWriteChannel writeChannel) {
/* 42 */     super(readChannel, writeChannel);
/* 43 */     ConnectedChannel ch = Channels.<ConnectedChannel>unwrap(ConnectedChannel.class, readChannel);
/* 44 */     if (ch == null) ch = Channels.<ConnectedChannel>unwrap(ConnectedChannel.class, writeChannel); 
/* 45 */     if (ch == null) throw Messages.msg.oneChannelMustBeConnection(); 
/* 46 */     this.connection = ch;
/*    */   }
/*    */ 
/*    */   
/*    */   public ChannelListener.Setter<? extends AssembledConnectedChannel> getCloseSetter() {
/* 51 */     return (ChannelListener.Setter)super.getCloseSetter();
/*    */   }
/*    */   
/*    */   public SocketAddress getPeerAddress() {
/* 55 */     return this.connection.getPeerAddress();
/*    */   }
/*    */   
/*    */   public <A extends SocketAddress> A getPeerAddress(Class<A> type) {
/* 59 */     return this.connection.getPeerAddress(type);
/*    */   }
/*    */   
/*    */   public SocketAddress getLocalAddress() {
/* 63 */     return this.connection.getLocalAddress();
/*    */   }
/*    */   
/*    */   public <A extends SocketAddress> A getLocalAddress(Class<A> type) {
/* 67 */     return this.connection.getLocalAddress(type);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\channels\AssembledConnectedChannel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */