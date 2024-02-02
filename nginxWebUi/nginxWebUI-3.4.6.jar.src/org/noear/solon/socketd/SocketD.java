/*     */ package org.noear.solon.socketd;
/*     */ 
/*     */ import java.net.URI;
/*     */ import java.util.function.Supplier;
/*     */ import org.noear.nami.Channel;
/*     */ import org.noear.nami.Decoder;
/*     */ import org.noear.nami.Encoder;
/*     */ import org.noear.nami.Nami;
/*     */ import org.noear.nami.channel.socketd.SocketChannel;
/*     */ import org.noear.solon.annotation.Note;
/*     */ import org.noear.solon.core.handle.Context;
/*     */ import org.noear.solon.core.message.Session;
/*     */ import org.noear.solon.socketd.protocol.MessageProtocol;
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
/*     */ public class SocketD
/*     */ {
/*     */   public static void setProtocol(MessageProtocol protocol) {
/*  31 */     ProtocolManager.setProtocol(protocol);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Session createSession(Connector connector) {
/*  42 */     return SessionFactoryManager.create(connector);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Note("ServerUri 以：ws:// 或 wss:// 或 tcp:// 开头")
/*     */   public static Session createSession(URI serverUri, boolean autoReconnect) {
/*  53 */     return SessionFactoryManager.create(serverUri, autoReconnect);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Note("ServerUri 以：ws:// 或 wss:// 或 tcp:// 开头")
/*     */   public static Session createSession(URI serverUri) {
/*  63 */     return createSession(serverUri, true);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Note("ServerUri 以：ws:// 或 wss:// 或 tcp:// 开头")
/*     */   public static Session createSession(String serverUri, boolean autoReconnect) {
/*  74 */     return createSession(URI.create(serverUri), autoReconnect);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Note("ServerUri 以：ws:// 或 wss:// 或 tcp:// 开头")
/*     */   public static Session createSession(String serverUri) {
/*  84 */     return createSession(serverUri, true);
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
/*     */   public static <T> T create(URI serverUri, Class<T> service) {
/*  97 */     Session session = createSession(serverUri, true);
/*  98 */     return create(() -> session, service);
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
/*     */   public static <T> T create(URI serverUri, Encoder encoder, Decoder decoder, Class<T> service) {
/* 111 */     Session session = createSession(serverUri, true);
/* 112 */     return create(() -> session, encoder, decoder, service);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static <T> T create(String serverUri, Class<T> service) {
/* 119 */     Session session = createSession(serverUri, true);
/* 120 */     return create(() -> session, service);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static <T> T create(String serverUri, Encoder encoder, Decoder decoder, Class<T> service) {
/* 128 */     Session session = createSession(serverUri, true);
/* 129 */     return create(() -> session, encoder, decoder, service);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static <T> T create(Context context, Class<T> service) {
/* 136 */     if (context.request() instanceof Session) {
/* 137 */       Session session = (Session)context.request();
/* 138 */       return create(() -> session, (Encoder)null, (Decoder)null, service);
/*     */     } 
/* 140 */     throw new IllegalArgumentException("Request context nonsupport socketd");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static <T> T create(Session session, Class<T> service) {
/* 148 */     return create(() -> session, service);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static <T> T create(Supplier<Session> sessions, Class<T> service) {
/* 155 */     return create(sessions, (Encoder)null, (Decoder)null, service);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static <T> T create(Session session, Encoder encoder, Decoder decoder, Class<T> service) {
/* 162 */     return create(() -> session, encoder, decoder, service);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static <T> T create(Supplier<Session> sessions, Encoder encoder, Decoder decoder, Class<T> service) {
/* 169 */     URI uri = ((Session)sessions.get()).uri();
/* 170 */     if (uri == null) {
/* 171 */       uri = URI.create("tcp://socketd");
/*     */     }
/*     */     
/* 174 */     String server = uri.getScheme() + ":" + uri.getSchemeSpecificPart();
/*     */     
/* 176 */     return (T)Nami.builder()
/* 177 */       .encoder(encoder)
/* 178 */       .decoder(decoder)
/* 179 */       .headerSet("Accept", "application/json")
/* 180 */       .headerSet("Content-Type", "application/json")
/* 181 */       .channel((Channel)new SocketChannel(sessions))
/* 182 */       .upstream(() -> server)
/* 183 */       .create(service);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\socketd\SocketD.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */