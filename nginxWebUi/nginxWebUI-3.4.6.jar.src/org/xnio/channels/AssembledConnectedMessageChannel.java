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
/*    */ public class AssembledConnectedMessageChannel
/*    */   extends AssembledMessageChannel
/*    */   implements ConnectedMessageChannel
/*    */ {
/*    */   private final ConnectedChannel connection;
/*    */   
/*    */   public AssembledConnectedMessageChannel(ConnectedChannel connection, ReadableMessageChannel readable, WritableMessageChannel writable) {
/* 41 */     super(connection, readable, writable);
/* 42 */     this.connection = connection;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public AssembledConnectedMessageChannel(ReadableMessageChannel readable, WritableMessageChannel writable) {
/* 52 */     this(new AssembledConnectedChannel(readable, writable), readable, writable);
/*    */   }
/*    */ 
/*    */   
/*    */   public ChannelListener.Setter<? extends AssembledConnectedMessageChannel> getCloseSetter() {
/* 57 */     return (ChannelListener.Setter)super.getCloseSetter();
/*    */   }
/*    */ 
/*    */   
/*    */   public ChannelListener.Setter<? extends AssembledConnectedMessageChannel> getReadSetter() {
/* 62 */     return (ChannelListener.Setter)super.getReadSetter();
/*    */   }
/*    */ 
/*    */   
/*    */   public ChannelListener.Setter<? extends AssembledConnectedMessageChannel> getWriteSetter() {
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


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\channels\AssembledConnectedMessageChannel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */