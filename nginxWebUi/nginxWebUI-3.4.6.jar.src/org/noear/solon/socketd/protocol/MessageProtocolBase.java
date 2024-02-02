/*     */ package org.noear.solon.socketd.protocol;
/*     */ 
/*     */ import java.nio.ByteBuffer;
/*     */ import org.noear.solon.core.message.Message;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class MessageProtocolBase
/*     */   implements MessageProtocol
/*     */ {
/*  15 */   public static final MessageProtocol instance = new MessageProtocolBase();
/*     */ 
/*     */   
/*     */   public ByteBuffer encode(Message message) throws Exception {
/*  19 */     if (message.flag() == 1) {
/*     */       
/*  21 */       int i = (message.body()).length + 4 + 4;
/*     */       
/*  23 */       ByteBuffer byteBuffer = ByteBuffer.allocate(i);
/*     */ 
/*     */       
/*  26 */       byteBuffer.putInt(i);
/*     */ 
/*     */       
/*  29 */       byteBuffer.putInt(message.flag());
/*     */ 
/*     */       
/*  32 */       byteBuffer.put(message.body());
/*     */       
/*  34 */       byteBuffer.flip();
/*     */       
/*  36 */       return byteBuffer;
/*     */     } 
/*     */     
/*  39 */     byte[] keyB = message.key().getBytes(message.getCharset());
/*     */     
/*  41 */     byte[] resourceDescriptorB = message.resourceDescriptor().getBytes(message.getCharset());
/*     */     
/*  43 */     byte[] headerB = message.header().getBytes(message.getCharset());
/*     */ 
/*     */     
/*  46 */     int len = keyB.length + resourceDescriptorB.length + headerB.length + (message.body()).length + 6 + 4 + 4;
/*     */     
/*  48 */     ByteBuffer buffer = ByteBuffer.allocate(len);
/*     */ 
/*     */     
/*  51 */     buffer.putInt(len);
/*     */ 
/*     */     
/*  54 */     buffer.putInt(message.flag());
/*     */ 
/*     */     
/*  57 */     buffer.put(keyB);
/*  58 */     buffer.putChar('\n');
/*     */ 
/*     */     
/*  61 */     buffer.put(resourceDescriptorB);
/*  62 */     buffer.putChar('\n');
/*     */     
/*  64 */     buffer.put(headerB);
/*  65 */     buffer.putChar('\n');
/*     */ 
/*     */     
/*  68 */     buffer.put(message.body());
/*     */     
/*  70 */     buffer.flip();
/*     */     
/*  72 */     return buffer;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Message decode(ByteBuffer buffer) throws Exception {
/*  78 */     int len0 = buffer.getInt();
/*     */     
/*  80 */     if (len0 > buffer.remaining() + 4) {
/*  81 */       return null;
/*     */     }
/*     */     
/*  84 */     int flag = buffer.getInt();
/*     */     
/*  86 */     if (flag == 1) {
/*     */       
/*  88 */       int i = len0 - buffer.position();
/*  89 */       byte[] arrayOfByte = new byte[i];
/*  90 */       if (i > 0) {
/*  91 */         buffer.get(arrayOfByte, 0, i);
/*     */       }
/*     */       
/*  94 */       return new Message(flag, null, null, null, arrayOfByte);
/*     */     } 
/*     */     
/*  97 */     ByteBuffer sb = ByteBuffer.allocate(Math.min(4096, buffer.limit()));
/*     */ 
/*     */     
/* 100 */     String key = decodeString(buffer, sb, 256);
/* 101 */     if (key == null) {
/* 102 */       return null;
/*     */     }
/*     */ 
/*     */     
/* 106 */     String resourceDescriptor = decodeString(buffer, sb, 512);
/* 107 */     if (resourceDescriptor == null) {
/* 108 */       return null;
/*     */     }
/*     */ 
/*     */     
/* 112 */     String header = decodeString(buffer, sb, 0);
/* 113 */     if (header == null) {
/* 114 */       return null;
/*     */     }
/*     */ 
/*     */     
/* 118 */     int len = len0 - buffer.position();
/* 119 */     byte[] body = new byte[len];
/* 120 */     if (len > 0) {
/* 121 */       buffer.get(body, 0, len);
/*     */     }
/*     */     
/* 124 */     return new Message(flag, key, resourceDescriptor, header, body);
/*     */   }
/*     */ 
/*     */   
/*     */   protected String decodeString(ByteBuffer buffer, ByteBuffer sb, int maxLen) {
/* 129 */     sb.clear();
/*     */     
/*     */     while (true) {
/* 132 */       byte c = buffer.get();
/*     */       
/* 134 */       if (c == 10)
/*     */         break; 
/* 136 */       if (c != 0) {
/* 137 */         sb.put(c);
/*     */       }
/*     */       
/* 140 */       if (maxLen > 0 && maxLen < sb.position()) {
/* 141 */         return null;
/*     */       }
/*     */     } 
/*     */     
/* 145 */     sb.flip();
/* 146 */     if (sb.limit() < 1) {
/* 147 */       return "";
/*     */     }
/*     */     
/* 150 */     return new String(sb.array(), 0, sb.limit());
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\socketd\protocol\MessageProtocolBase.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */