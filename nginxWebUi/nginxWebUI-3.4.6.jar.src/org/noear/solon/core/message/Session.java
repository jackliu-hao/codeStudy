/*     */ package org.noear.solon.core.message;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.net.URI;
/*     */ import java.util.Collection;
/*     */ import java.util.Map;
/*     */ import java.util.function.BiConsumer;
/*     */ import org.noear.solon.core.NvMap;
/*     */ import org.noear.solon.core.handle.MethodType;
/*     */ import org.noear.solon.core.util.PathUtil;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public interface Session
/*     */ {
/*     */   Object real();
/*     */   
/*     */   String sessionId();
/*     */   
/*     */   MethodType method();
/*     */   
/*     */   URI uri();
/*     */   
/*     */   String path();
/*     */   
/*     */   void pathNew(String paramString);
/*     */   
/*     */   String pathNew();
/*     */   
/*     */   default NvMap pathMap(String expr) {
/*  58 */     return PathUtil.pathVarMap(pathNew(), expr);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   String header(String paramString);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   void headerSet(String paramString1, String paramString2);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   NvMap headerMap();
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   String param(String paramString);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   void paramSet(String paramString1, String paramString2);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   NvMap paramMap();
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   default Object attr(String name) {
/*  96 */     return attrMap().get(name);
/*     */   }
/*     */   
/*     */   default void attrSet(String name, Object value) {
/* 100 */     attrMap().put(name, value);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   Map<String, Object> attrMap();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   int flag();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   void flagSet(int paramInt);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   void sendAsync(String paramString);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   void sendAsync(Message paramMessage);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   void send(String paramString);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   void send(Message paramMessage);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   String sendAndResponse(String paramString);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   String sendAndResponse(String paramString, int paramInt);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   Message sendAndResponse(Message paramMessage);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   Message sendAndResponse(Message paramMessage, int paramInt);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   void sendAndCallback(String paramString, BiConsumer<String, Throwable> paramBiConsumer);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   void sendAndCallback(Message paramMessage, BiConsumer<Message, Throwable> paramBiConsumer);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   default void listener(Listener listener) {}
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   default Listener listener() {
/* 180 */     return null;
/*     */   }
/*     */   
/*     */   void close() throws IOException;
/*     */   
/*     */   boolean isValid();
/*     */   
/*     */   boolean isSecure();
/*     */   
/*     */   void setHandshaked(boolean paramBoolean);
/*     */   
/*     */   boolean getHandshaked();
/*     */   
/*     */   InetSocketAddress getRemoteAddress();
/*     */   
/*     */   InetSocketAddress getLocalAddress();
/*     */   
/*     */   void setAttachment(Object paramObject);
/*     */   
/*     */   <T> T getAttachment();
/*     */   
/*     */   Collection<Session> getOpenSessions();
/*     */   
/*     */   void sendHeartbeat();
/*     */   
/*     */   void sendHeartbeatAuto(int paramInt);
/*     */   
/*     */   void sendHandshake(Message paramMessage);
/*     */   
/*     */   Message sendHandshakeAndResponse(Message paramMessage);
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\core\message\Session.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */