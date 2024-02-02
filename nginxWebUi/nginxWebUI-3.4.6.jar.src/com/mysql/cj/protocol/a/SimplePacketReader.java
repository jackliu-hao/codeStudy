/*     */ package com.mysql.cj.protocol.a;
/*     */ 
/*     */ import com.mysql.cj.Messages;
/*     */ import com.mysql.cj.conf.RuntimeProperty;
/*     */ import com.mysql.cj.exceptions.CJPacketTooBigException;
/*     */ import com.mysql.cj.protocol.Message;
/*     */ import com.mysql.cj.protocol.MessageHeader;
/*     */ import com.mysql.cj.protocol.MessageReader;
/*     */ import com.mysql.cj.protocol.SocketConnection;
/*     */ import java.io.IOException;
/*     */ import java.util.Optional;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class SimplePacketReader
/*     */   implements MessageReader<NativePacketHeader, NativePacketPayload>
/*     */ {
/*     */   protected SocketConnection socketConnection;
/*     */   protected RuntimeProperty<Integer> maxAllowedPacket;
/*  50 */   private byte readPacketSequence = -1;
/*     */   
/*  52 */   NativePacketHeader lastHeader = null;
/*  53 */   NativePacketPayload lastMessage = null;
/*     */   
/*     */   public SimplePacketReader(SocketConnection socketConnection, RuntimeProperty<Integer> maxAllowedPacket) {
/*  56 */     this.socketConnection = socketConnection;
/*  57 */     this.maxAllowedPacket = maxAllowedPacket;
/*     */   }
/*     */ 
/*     */   
/*     */   public NativePacketHeader readHeader() throws IOException {
/*  62 */     if (this.lastHeader == null) {
/*  63 */       return readHeaderLocal();
/*     */     }
/*  65 */     NativePacketHeader hdr = this.lastHeader;
/*  66 */     this.lastHeader = null;
/*  67 */     this.readPacketSequence = hdr.getMessageSequence();
/*  68 */     return hdr;
/*     */   }
/*     */ 
/*     */   
/*     */   public NativePacketHeader probeHeader() throws IOException {
/*  73 */     this.lastHeader = readHeaderLocal();
/*  74 */     return this.lastHeader;
/*     */   }
/*     */   
/*     */   private NativePacketHeader readHeaderLocal() throws IOException {
/*  78 */     NativePacketHeader hdr = new NativePacketHeader();
/*     */     
/*     */     try {
/*  81 */       this.socketConnection.getMysqlInput().readFully(hdr.getBuffer().array(), 0, 4);
/*  82 */       int packetLength = hdr.getMessageSize();
/*  83 */       if (packetLength > ((Integer)this.maxAllowedPacket.getValue()).intValue()) {
/*  84 */         throw new CJPacketTooBigException(packetLength, ((Integer)this.maxAllowedPacket.getValue()).intValue());
/*     */       }
/*  86 */     } catch (IOException|CJPacketTooBigException e) {
/*     */       try {
/*  88 */         this.socketConnection.forceClose();
/*  89 */       } catch (Exception exception) {}
/*     */ 
/*     */       
/*  92 */       throw e;
/*     */     } 
/*     */     
/*  95 */     this.readPacketSequence = hdr.getMessageSequence();
/*  96 */     return hdr;
/*     */   }
/*     */ 
/*     */   
/*     */   public NativePacketPayload readMessage(Optional<NativePacketPayload> reuse, NativePacketHeader header) throws IOException {
/* 101 */     if (this.lastMessage == null) {
/* 102 */       return readMessageLocal(reuse, header);
/*     */     }
/* 104 */     NativePacketPayload buf = this.lastMessage;
/* 105 */     this.lastMessage = null;
/* 106 */     return buf;
/*     */   }
/*     */ 
/*     */   
/*     */   public NativePacketPayload probeMessage(Optional<NativePacketPayload> reuse, NativePacketHeader header) throws IOException {
/* 111 */     this.lastMessage = readMessageLocal(reuse, header);
/* 112 */     return this.lastMessage;
/*     */   }
/*     */   private NativePacketPayload readMessageLocal(Optional<NativePacketPayload> reuse, NativePacketHeader header) throws IOException {
/*     */     try {
/*     */       NativePacketPayload message;
/* 117 */       int packetLength = header.getMessageSize();
/*     */       
/* 119 */       if (reuse.isPresent()) {
/* 120 */         message = reuse.get();
/*     */         
/* 122 */         message.setPosition(0);
/*     */         
/* 124 */         if ((message.getByteBuffer()).length < packetLength)
/*     */         {
/*     */           
/* 127 */           message.setByteBuffer(new byte[packetLength]);
/*     */         }
/*     */ 
/*     */         
/* 131 */         message.setPayloadLength(packetLength);
/*     */       } else {
/* 133 */         message = new NativePacketPayload(new byte[packetLength]);
/*     */       } 
/*     */ 
/*     */       
/* 137 */       int numBytesRead = this.socketConnection.getMysqlInput().readFully(message.getByteBuffer(), 0, packetLength);
/* 138 */       if (numBytesRead != packetLength) {
/* 139 */         throw new IOException(Messages.getString("PacketReader.1", new Object[] { Integer.valueOf(packetLength), Integer.valueOf(numBytesRead) }));
/*     */       }
/* 141 */       return message;
/*     */     }
/* 143 */     catch (IOException e) {
/*     */       try {
/* 145 */         this.socketConnection.forceClose();
/* 146 */       } catch (Exception exception) {}
/*     */ 
/*     */       
/* 149 */       throw e;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public byte getMessageSequence() {
/* 155 */     return this.readPacketSequence;
/*     */   }
/*     */ 
/*     */   
/*     */   public void resetMessageSequence() {
/* 160 */     this.readPacketSequence = 0;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\protocol\a\SimplePacketReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */