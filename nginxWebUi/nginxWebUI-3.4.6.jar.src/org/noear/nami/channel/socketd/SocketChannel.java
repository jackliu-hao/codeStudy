/*     */ package org.noear.nami.channel.socketd;
/*     */ 
/*     */ import java.util.Map;
/*     */ import java.util.function.Supplier;
/*     */ import org.noear.nami.Channel;
/*     */ import org.noear.nami.Context;
/*     */ import org.noear.nami.Encoder;
/*     */ import org.noear.nami.NamiManager;
/*     */ import org.noear.nami.Result;
/*     */ import org.noear.solon.Utils;
/*     */ import org.noear.solon.core.message.Message;
/*     */ import org.noear.solon.core.message.Session;
/*     */ import org.noear.solon.socketd.annotation.Handshake;
/*     */ import org.noear.solon.socketd.util.HeaderUtil;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class SocketChannel
/*     */   extends SocketChannelBase
/*     */   implements Channel
/*     */ {
/*     */   public Supplier<Session> sessions;
/*     */   
/*     */   public SocketChannel(Supplier<Session> sessions) {
/*  27 */     this.sessions = sessions;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Result call(Context ctx) throws Throwable {
/*  38 */     pretreatment(ctx);
/*     */     
/*  40 */     if (ctx.config.getDecoder() == null) {
/*  41 */       throw new IllegalArgumentException("There is no suitable decoder");
/*     */     }
/*     */ 
/*     */     
/*  45 */     ctx.config.getDecoder().pretreatment(ctx);
/*     */     
/*  47 */     Message message = null;
/*  48 */     String message_key = Message.guid();
/*  49 */     int flag = 10;
/*     */     
/*  51 */     if (ctx.method != null) {
/*     */ 
/*     */       
/*  54 */       Handshake h = ctx.method.<Handshake>getAnnotation(Handshake.class);
/*  55 */       if (h != null) {
/*  56 */         flag = 12;
/*     */         
/*  58 */         if (Utils.isNotEmpty(h.handshakeHeader())) {
/*  59 */           Map<String, String> headerMap = HeaderUtil.decodeHeaderMap(h.handshakeHeader());
/*  60 */           ctx.headers.putAll(headerMap);
/*     */         } 
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/*  66 */     Encoder encoder = ctx.config.getEncoder();
/*  67 */     if (encoder == null) {
/*  68 */       encoder = NamiManager.getEncoder("application/json");
/*     */     }
/*     */     
/*  71 */     if (encoder == null) {
/*  72 */       throw new IllegalArgumentException("There is no suitable encoder");
/*     */     }
/*     */ 
/*     */     
/*  76 */     ctx.headers.put("Content-Type", encoder.enctype());
/*  77 */     byte[] bytes = encoder.encode(ctx.body);
/*  78 */     message = new Message(flag, message_key, ctx.url, HeaderUtil.encodeHeaderMap(ctx.headers), bytes);
/*     */ 
/*     */     
/*  81 */     Session session = this.sessions.get();
/*  82 */     if (ctx.config.getHeartbeat() > 0) {
/*  83 */       session.sendHeartbeatAuto(ctx.config.getHeartbeat());
/*     */     }
/*     */ 
/*     */     
/*  87 */     Message res = session.sendAndResponse(message, ctx.config.getTimeout());
/*     */     
/*  89 */     if (res == null) {
/*  90 */       return null;
/*     */     }
/*     */ 
/*     */     
/*  94 */     Result result = new Result(200, res.body());
/*     */ 
/*     */     
/*  97 */     if (Utils.isNotEmpty(res.header())) {
/*  98 */       Map<String, String> map = HeaderUtil.decodeHeaderMap(res.header());
/*  99 */       map.forEach((k, v) -> result.headerAdd(k, v));
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 105 */     return result;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\nami\channel\socketd\SocketChannel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */