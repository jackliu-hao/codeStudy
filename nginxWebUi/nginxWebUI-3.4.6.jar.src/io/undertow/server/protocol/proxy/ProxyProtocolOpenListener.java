/*    */ package io.undertow.server.protocol.proxy;
/*    */ 
/*    */ import io.undertow.connector.ByteBufferPool;
/*    */ import io.undertow.protocols.ssl.UndertowXnioSsl;
/*    */ import io.undertow.server.OpenListener;
/*    */ import java.nio.channels.Channel;
/*    */ import org.xnio.ChannelListener;
/*    */ import org.xnio.OptionMap;
/*    */ import org.xnio.StreamConnection;
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ProxyProtocolOpenListener
/*    */   implements ChannelListener<StreamConnection>
/*    */ {
/*    */   private final OpenListener openListener;
/*    */   private final UndertowXnioSsl ssl;
/*    */   private final ByteBufferPool bufferPool;
/*    */   private final OptionMap sslOptionMap;
/*    */   
/*    */   public ProxyProtocolOpenListener(OpenListener openListener, UndertowXnioSsl ssl, ByteBufferPool bufferPool, OptionMap sslOptionMap) {
/* 22 */     this.openListener = openListener;
/* 23 */     this.ssl = ssl;
/* 24 */     this.bufferPool = bufferPool;
/* 25 */     this.sslOptionMap = sslOptionMap;
/*    */   }
/*    */ 
/*    */   
/*    */   public void handleEvent(StreamConnection streamConnection) {
/* 30 */     streamConnection.getSourceChannel().setReadListener(new ProxyProtocolReadListener(streamConnection, this.openListener, this.ssl, this.bufferPool, this.sslOptionMap));
/* 31 */     streamConnection.getSourceChannel().wakeupReads();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\protocol\proxy\ProxyProtocolOpenListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */