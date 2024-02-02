/*    */ package com.mysql.cj.protocol.a;
/*    */ 
/*    */ import com.mysql.cj.log.Log;
/*    */ import com.mysql.cj.protocol.MessageSender;
/*    */ import com.mysql.cj.util.StringUtils;
/*    */ import java.io.IOException;
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
/*    */ public class TracingPacketSender
/*    */   implements MessageSender<NativePacketPayload>
/*    */ {
/*    */   private MessageSender<NativePacketPayload> packetSender;
/*    */   private String host;
/*    */   private long serverThreadId;
/*    */   private Log log;
/*    */   
/*    */   public TracingPacketSender(MessageSender<NativePacketPayload> packetSender, Log log, String host, long serverThreadId) {
/* 48 */     this.packetSender = packetSender;
/* 49 */     this.host = host;
/* 50 */     this.serverThreadId = serverThreadId;
/* 51 */     this.log = log;
/*    */   }
/*    */   
/*    */   public void setServerThreadId(long serverThreadId) {
/* 55 */     this.serverThreadId = serverThreadId;
/*    */   }
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
/*    */   private void logPacket(byte[] packet, int packetLen, byte packetSequence) {
/* 69 */     StringBuilder traceMessageBuf = new StringBuilder();
/*    */     
/* 71 */     traceMessageBuf.append("send packet payload:\n");
/* 72 */     traceMessageBuf.append("host: '");
/* 73 */     traceMessageBuf.append(this.host);
/* 74 */     traceMessageBuf.append("' serverThreadId: '");
/* 75 */     traceMessageBuf.append(this.serverThreadId);
/* 76 */     traceMessageBuf.append("' packetLen: '");
/* 77 */     traceMessageBuf.append(packetLen);
/* 78 */     traceMessageBuf.append("' packetSequence: '");
/* 79 */     traceMessageBuf.append(packetSequence);
/* 80 */     traceMessageBuf.append("'\n");
/* 81 */     traceMessageBuf.append(StringUtils.dumpAsHex(packet, packetLen));
/*    */     
/* 83 */     this.log.logTrace(traceMessageBuf.toString());
/*    */   }
/*    */   
/*    */   public void send(byte[] packet, int packetLen, byte packetSequence) throws IOException {
/* 87 */     logPacket(packet, packetLen, packetSequence);
/*    */     
/* 89 */     this.packetSender.send(packet, packetLen, packetSequence);
/*    */   }
/*    */ 
/*    */   
/*    */   public MessageSender<NativePacketPayload> undecorateAll() {
/* 94 */     return this.packetSender.undecorateAll();
/*    */   }
/*    */ 
/*    */   
/*    */   public MessageSender<NativePacketPayload> undecorate() {
/* 99 */     return this.packetSender;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\protocol\a\TracingPacketSender.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */