/*     */ package com.mysql.cj.protocol.a;
/*     */ 
/*     */ import com.mysql.cj.conf.RuntimeProperty;
/*     */ import com.mysql.cj.protocol.MessageSender;
/*     */ import com.mysql.cj.util.StringUtils;
/*     */ import java.io.IOException;
/*     */ import java.util.LinkedList;
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
/*     */ public class DebugBufferingPacketSender
/*     */   implements MessageSender<NativePacketPayload>
/*     */ {
/*     */   private MessageSender<NativePacketPayload> packetSender;
/*     */   private LinkedList<StringBuilder> packetDebugBuffer;
/*     */   private RuntimeProperty<Integer> packetDebugBufferSize;
/*  43 */   private int maxPacketDumpLength = 1024;
/*     */   
/*     */   private static final int DEBUG_MSG_LEN = 64;
/*     */ 
/*     */   
/*     */   public DebugBufferingPacketSender(MessageSender<NativePacketPayload> packetSender, LinkedList<StringBuilder> packetDebugBuffer, RuntimeProperty<Integer> packetDebugBufferSize) {
/*  49 */     this.packetSender = packetSender;
/*  50 */     this.packetDebugBuffer = packetDebugBuffer;
/*  51 */     this.packetDebugBufferSize = packetDebugBufferSize;
/*     */   }
/*     */   
/*     */   public void setMaxPacketDumpLength(int maxPacketDumpLength) {
/*  55 */     this.maxPacketDumpLength = maxPacketDumpLength;
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
/*     */   private void pushPacketToDebugBuffer(byte[] packet, int packetLen) {
/*  67 */     int bytesToDump = Math.min(this.maxPacketDumpLength, packetLen);
/*     */     
/*  69 */     String packetPayload = StringUtils.dumpAsHex(packet, bytesToDump);
/*     */     
/*  71 */     StringBuilder packetDump = new StringBuilder(68 + packetPayload.length());
/*     */     
/*  73 */     packetDump.append("Client ");
/*  74 */     packetDump.append(packet.toString());
/*  75 */     packetDump.append("--------------------> Server\n");
/*  76 */     packetDump.append("\nPacket payload:\n\n");
/*  77 */     packetDump.append(packetPayload);
/*     */     
/*  79 */     if (packetLen > this.maxPacketDumpLength) {
/*  80 */       packetDump.append("\nNote: Packet of " + packetLen + " bytes truncated to " + this.maxPacketDumpLength + " bytes.\n");
/*     */     }
/*     */     
/*  83 */     if (this.packetDebugBuffer.size() + 1 > ((Integer)this.packetDebugBufferSize.getValue()).intValue()) {
/*  84 */       this.packetDebugBuffer.removeFirst();
/*     */     }
/*     */     
/*  87 */     this.packetDebugBuffer.addLast(packetDump);
/*     */   }
/*     */   
/*     */   public void send(byte[] packet, int packetLen, byte packetSequence) throws IOException {
/*  91 */     pushPacketToDebugBuffer(packet, packetLen);
/*  92 */     this.packetSender.send(packet, packetLen, packetSequence);
/*     */   }
/*     */ 
/*     */   
/*     */   public MessageSender<NativePacketPayload> undecorateAll() {
/*  97 */     return this.packetSender.undecorateAll();
/*     */   }
/*     */ 
/*     */   
/*     */   public MessageSender<NativePacketPayload> undecorate() {
/* 102 */     return this.packetSender;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\protocol\a\DebugBufferingPacketSender.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */