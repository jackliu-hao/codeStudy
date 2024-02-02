/*    */ package org.xnio.channels;
/*    */ 
/*    */ import java.net.SocketAddress;
/*    */ import org.xnio.ChannelListener;
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
/*    */ public class AssembledConnectedStreamChannel
/*    */   extends AssembledStreamChannel
/*    */   implements ConnectedStreamChannel
/*    */ {
/*    */   private final ConnectedChannel connection;
/*    */   
/*    */   public AssembledConnectedStreamChannel(ConnectedChannel connection, StreamSourceChannel source, StreamSinkChannel sink) {
/* 41 */     super(connection, source, sink);
/* 42 */     this.connection = connection;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public AssembledConnectedStreamChannel(StreamSourceChannel source, StreamSinkChannel sink) {
/* 52 */     this(new AssembledConnectedChannel(source, sink), source, sink);
/*    */   }
/*    */ 
/*    */   
/*    */   public ChannelListener.Setter<? extends AssembledConnectedStreamChannel> getCloseSetter() {
/* 57 */     return (ChannelListener.Setter)super.getCloseSetter();
/*    */   }
/*    */ 
/*    */   
/*    */   public ChannelListener.Setter<? extends AssembledConnectedStreamChannel> getReadSetter() {
/* 62 */     return (ChannelListener.Setter)super.getReadSetter();
/*    */   }
/*    */ 
/*    */   
/*    */   public ChannelListener.Setter<? extends AssembledConnectedStreamChannel> getWriteSetter() {
/* 67 */     return (ChannelListener.Setter)super.getWriteSetter();
/*    */   }
/*    */   
/*    */   public SocketAddress getPeerAddress() {
/* 71 */     return this.connection.getPeerAddress();
/*    */   }
/*    */   
/*    */   public <A extends SocketAddress> A getPeerAddress(Class<A> type) {
/* 75 */     return this.connection.getPeerAddress(type);
/*    */   }
/*    */   
/*    */   public SocketAddress getLocalAddress() {
/* 79 */     return this.connection.getLocalAddress();
/*    */   }
/*    */   
/*    */   public <A extends SocketAddress> A getLocalAddress(Class<A> type) {
/* 83 */     return this.connection.getLocalAddress(type);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\channels\AssembledConnectedStreamChannel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */