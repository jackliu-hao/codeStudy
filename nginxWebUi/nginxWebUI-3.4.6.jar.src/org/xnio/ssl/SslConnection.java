/*    */ package org.xnio.ssl;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import javax.net.ssl.SSLSession;
/*    */ import org.xnio.ChannelListener;
/*    */ import org.xnio.StreamConnection;
/*    */ import org.xnio.XnioIoThread;
/*    */ import org.xnio.channels.SslChannel;
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
/*    */ public abstract class SslConnection
/*    */   extends StreamConnection
/*    */   implements SslChannel
/*    */ {
/*    */   protected SslConnection(XnioIoThread thread) {
/* 44 */     super(thread);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public abstract void startHandshake() throws IOException;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public abstract SSLSession getSslSession();
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public abstract ChannelListener.Setter<? extends SslConnection> getHandshakeSetter();
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public ChannelListener.Setter<? extends SslConnection> getCloseSetter() {
/* 75 */     return super.getCloseSetter();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\ssl\SslConnection.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */