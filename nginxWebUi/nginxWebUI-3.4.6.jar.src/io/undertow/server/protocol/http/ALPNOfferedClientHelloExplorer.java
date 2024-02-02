/*     */ package io.undertow.server.protocol.http;
/*     */ 
/*     */ import io.undertow.UndertowMessages;
/*     */ import java.nio.BufferUnderflowException;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import javax.net.ssl.SSLException;
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
/*     */ final class ALPNOfferedClientHelloExplorer
/*     */ {
/*     */   private static final int RECORD_HEADER_SIZE = 5;
/*     */   
/*     */   static boolean isIncompleteHeader(ByteBuffer source) {
/*  46 */     return (source.remaining() < 5);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static List<Integer> parseClientHello(ByteBuffer source) throws SSLException {
/*  56 */     ByteBuffer input = source.duplicate();
/*     */ 
/*     */     
/*  59 */     if (isIncompleteHeader(input)) {
/*  60 */       throw new BufferUnderflowException();
/*     */     }
/*     */     
/*  63 */     byte firstByte = input.get();
/*  64 */     byte secondByte = input.get();
/*  65 */     byte thirdByte = input.get();
/*     */     
/*  67 */     if ((firstByte & 0x80) != 0 && thirdByte == 1)
/*     */     {
/*  69 */       return null; } 
/*  70 */     if (firstByte == 22 && 
/*  71 */       secondByte == 3 && thirdByte >= 1 && thirdByte <= 3) {
/*  72 */       return exploreTLSRecord(input, firstByte, secondByte, thirdByte);
/*     */     }
/*     */ 
/*     */     
/*  76 */     return null;
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
/*     */   private static List<Integer> exploreTLSRecord(ByteBuffer input, byte firstByte, byte secondByte, byte thirdByte) throws SSLException {
/* 102 */     if (firstByte != 22) {
/* 103 */       throw UndertowMessages.MESSAGES.notHandshakeRecord();
/*     */     }
/*     */ 
/*     */     
/* 107 */     int recordLength = getInt16(input);
/* 108 */     if (recordLength > input.remaining()) {
/* 109 */       throw new BufferUnderflowException();
/*     */     }
/*     */ 
/*     */     
/*     */     try {
/* 114 */       return exploreHandshake(input, secondByte, thirdByte, recordLength);
/*     */     }
/* 116 */     catch (BufferUnderflowException ignored) {
/* 117 */       throw UndertowMessages.MESSAGES.invalidHandshakeRecord();
/*     */     } 
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
/*     */   private static List<Integer> exploreHandshake(ByteBuffer input, byte recordMajorVersion, byte recordMinorVersion, int recordLength) throws SSLException {
/* 153 */     byte handshakeType = input.get();
/* 154 */     if (handshakeType != 1) {
/* 155 */       throw UndertowMessages.MESSAGES.expectedClientHello();
/*     */     }
/*     */ 
/*     */     
/* 159 */     int handshakeLength = getInt24(input);
/*     */ 
/*     */ 
/*     */     
/* 163 */     if (handshakeLength > recordLength - 4) {
/* 164 */       throw UndertowMessages.MESSAGES.multiRecordSSLHandshake();
/*     */     }
/*     */     
/* 167 */     input = input.duplicate();
/* 168 */     input.limit(handshakeLength + input.position());
/* 169 */     return exploreRecord(input);
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
/*     */   private static List<Integer> exploreRecord(ByteBuffer input) throws SSLException {
/* 202 */     byte helloMajorVersion = input.get();
/* 203 */     byte helloMinorVersion = input.get();
/* 204 */     if (helloMajorVersion != 3 && helloMinorVersion != 3)
/*     */     {
/* 206 */       return null;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 211 */     for (int i = 0; i < 32; i++) {
/* 212 */       byte b = input.get();
/*     */     }
/*     */ 
/*     */     
/* 216 */     processByteVector8(input);
/*     */ 
/*     */ 
/*     */     
/* 220 */     int int16 = getInt16(input);
/* 221 */     List<Integer> ciphers = new ArrayList<>();
/* 222 */     for (int j = 0; j < int16; j += 2) {
/* 223 */       ciphers.add(Integer.valueOf(getInt16(input)));
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 228 */     processByteVector8(input);
/* 229 */     if (input.remaining() > 0) {
/* 230 */       return exploreExtensions(input, ciphers);
/*     */     }
/* 232 */     return null;
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
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static List<Integer> exploreExtensions(ByteBuffer input, List<Integer> ciphers) throws SSLException {
/* 249 */     int length = getInt16(input);
/* 250 */     while (length > 0) {
/* 251 */       int extType = getInt16(input);
/* 252 */       int extLen = getInt16(input);
/* 253 */       if (extType == 16) {
/* 254 */         return ciphers;
/*     */       }
/* 256 */       processByteVector(input, extLen);
/*     */ 
/*     */       
/* 259 */       length -= extLen + 4;
/*     */     } 
/* 261 */     return null;
/*     */   }
/*     */   
/*     */   private static int getInt8(ByteBuffer input) {
/* 265 */     return input.get();
/*     */   }
/*     */   
/*     */   private static int getInt16(ByteBuffer input) {
/* 269 */     return (input.get() & 0xFF) << 8 | input.get() & 0xFF;
/*     */   }
/*     */   
/*     */   private static int getInt24(ByteBuffer input) {
/* 273 */     return (input.get() & 0xFF) << 16 | (input.get() & 0xFF) << 8 | input
/* 274 */       .get() & 0xFF;
/*     */   }
/*     */   
/*     */   private static void processByteVector8(ByteBuffer input) {
/* 278 */     int int8 = getInt8(input);
/* 279 */     processByteVector(input, int8);
/*     */   }
/*     */ 
/*     */   
/*     */   private static void processByteVector(ByteBuffer input, int length) {
/* 284 */     for (int i = 0; i < length; i++)
/* 285 */       byte b = input.get(); 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\protocol\http\ALPNOfferedClientHelloExplorer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */