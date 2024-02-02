/*     */ package io.undertow.client;
/*     */ 
/*     */ import io.undertow.protocols.alpn.ALPNManager;
/*     */ import io.undertow.protocols.alpn.ALPNProvider;
/*     */ import io.undertow.protocols.ssl.SslConduit;
/*     */ import io.undertow.protocols.ssl.UndertowXnioSsl;
/*     */ import io.undertow.util.ImmediatePooled;
/*     */ import java.io.IOException;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.channels.Channel;
/*     */ import java.nio.channels.ClosedChannelException;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import java.util.concurrent.atomic.AtomicReference;
/*     */ import javax.net.ssl.SSLEngine;
/*     */ import org.xnio.ChannelListener;
/*     */ import org.xnio.Pooled;
/*     */ import org.xnio.channels.StreamSourceChannel;
/*     */ import org.xnio.conduits.PushBackStreamSourceConduit;
/*     */ import org.xnio.conduits.StreamSourceConduit;
/*     */ import org.xnio.ssl.SslConnection;
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
/*     */ public class ALPNClientSelector
/*     */ {
/*     */   public static void runAlpn(final SslConnection sslConnection, final ChannelListener<SslConnection> fallback, final ClientCallback<ClientConnection> failedListener, ALPNProtocol... details) {
/*  49 */     SslConduit conduit = UndertowXnioSsl.getSslConduit(sslConnection);
/*     */     
/*  51 */     final ALPNProvider provider = ALPNManager.INSTANCE.getProvider(conduit.getSSLEngine());
/*  52 */     if (provider == null) {
/*  53 */       fallback.handleEvent((Channel)sslConnection);
/*     */       return;
/*     */     } 
/*  56 */     String[] protocols = new String[details.length];
/*  57 */     final Map<String, ALPNProtocol> protocolMap = new HashMap<>();
/*  58 */     for (int i = 0; i < protocols.length; i++) {
/*  59 */       protocols[i] = details[i].getProtocol();
/*  60 */       protocolMap.put(details[i].getProtocol(), details[i]);
/*     */     } 
/*  62 */     final SSLEngine sslEngine = provider.setProtocols(conduit.getSSLEngine(), protocols);
/*  63 */     conduit.setSslEngine(sslEngine);
/*  64 */     final AtomicReference<Boolean> handshakeDone = new AtomicReference<>(Boolean.valueOf(false));
/*     */     
/*     */     try {
/*  67 */       sslConnection.startHandshake();
/*  68 */       sslConnection.getHandshakeSetter().set(new ChannelListener<SslConnection>()
/*     */           {
/*     */             public void handleEvent(SslConnection channel) {
/*  71 */               if (((Boolean)handshakeDone.get()).booleanValue()) {
/*     */                 return;
/*     */               }
/*  74 */               handshakeDone.set(Boolean.valueOf(true));
/*     */             }
/*     */           });
/*  77 */       sslConnection.getSourceChannel().getReadSetter().set(new ChannelListener<StreamSourceChannel>()
/*     */           {
/*     */             public void handleEvent(StreamSourceChannel channel)
/*     */             {
/*  81 */               String selectedProtocol = provider.getSelectedProtocol(sslEngine);
/*  82 */               if (selectedProtocol != null) {
/*  83 */                 handleSelected(selectedProtocol);
/*     */               } else {
/*  85 */                 ByteBuffer buf = ByteBuffer.allocate(100);
/*     */                 try {
/*  87 */                   int read = channel.read(buf);
/*  88 */                   if (read > 0) {
/*  89 */                     buf.flip();
/*  90 */                     PushBackStreamSourceConduit pb = new PushBackStreamSourceConduit(sslConnection.getSourceChannel().getConduit());
/*  91 */                     pb.pushBack((Pooled)new ImmediatePooled(buf));
/*  92 */                     sslConnection.getSourceChannel().setConduit((StreamSourceConduit)pb);
/*  93 */                   } else if (read == -1) {
/*  94 */                     failedListener.failed(new ClosedChannelException());
/*     */                   } 
/*  96 */                   selectedProtocol = provider.getSelectedProtocol(sslEngine);
/*  97 */                   if (selectedProtocol != null) {
/*  98 */                     handleSelected(selectedProtocol);
/*  99 */                   } else if (read > 0 || ((Boolean)handshakeDone.get()).booleanValue()) {
/* 100 */                     sslConnection.getSourceChannel().suspendReads();
/* 101 */                     fallback.handleEvent((Channel)sslConnection);
/*     */                     return;
/*     */                   } 
/* 104 */                 } catch (Throwable t) {
/* 105 */                   IOException e = (t instanceof IOException) ? (IOException)t : new IOException(t);
/* 106 */                   failedListener.failed(e);
/*     */                 } 
/*     */               } 
/*     */             }
/*     */             
/*     */             private void handleSelected(String selected) {
/* 112 */               if (selected.isEmpty()) {
/* 113 */                 sslConnection.getSourceChannel().suspendReads();
/* 114 */                 fallback.handleEvent((Channel)sslConnection);
/*     */                 return;
/*     */               } 
/* 117 */               ALPNClientSelector.ALPNProtocol details = (ALPNClientSelector.ALPNProtocol)protocolMap.get(selected);
/* 118 */               if (details == null) {
/*     */                 
/* 120 */                 sslConnection.getSourceChannel().suspendReads();
/* 121 */                 fallback.handleEvent((Channel)sslConnection);
/*     */                 return;
/*     */               } 
/* 124 */               sslConnection.getSourceChannel().suspendReads();
/* 125 */               details.getSelected().handleEvent((Channel)sslConnection);
/*     */             }
/*     */           });
/*     */ 
/*     */       
/* 130 */       sslConnection.getSourceChannel().resumeReads();
/* 131 */     } catch (IOException e) {
/* 132 */       failedListener.failed(e);
/* 133 */     } catch (Throwable e) {
/* 134 */       failedListener.failed(new IOException(e));
/*     */     } 
/*     */   }
/*     */   
/*     */   public static class ALPNProtocol
/*     */   {
/*     */     private final ChannelListener<SslConnection> selected;
/*     */     private final String protocol;
/*     */     
/*     */     public ALPNProtocol(ChannelListener<SslConnection> selected, String protocol) {
/* 144 */       this.selected = selected;
/* 145 */       this.protocol = protocol;
/*     */     }
/*     */     
/*     */     public ChannelListener<SslConnection> getSelected() {
/* 149 */       return this.selected;
/*     */     }
/*     */     
/*     */     public String getProtocol() {
/* 153 */       return this.protocol;
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\client\ALPNClientSelector.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */