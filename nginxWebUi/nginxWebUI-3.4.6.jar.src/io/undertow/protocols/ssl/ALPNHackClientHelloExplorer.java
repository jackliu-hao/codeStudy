/*     */ package io.undertow.protocols.ssl;
/*     */ 
/*     */ import io.undertow.UndertowMessages;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.nio.BufferUnderflowException;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.charset.StandardCharsets;
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
/*     */ final class ALPNHackClientHelloExplorer
/*     */ {
/*     */   public static final int RECORD_HEADER_SIZE = 5;
/*     */   
/*     */   static List<String> exploreClientHello(ByteBuffer source) throws SSLException {
/*  61 */     ByteBuffer input = source.duplicate();
/*     */ 
/*     */     
/*  64 */     if (input.remaining() < 5) {
/*  65 */       throw new BufferUnderflowException();
/*     */     }
/*  67 */     List<String> alpnProtocols = new ArrayList<>();
/*     */     
/*  69 */     byte firstByte = input.get();
/*  70 */     byte secondByte = input.get();
/*  71 */     byte thirdByte = input.get();
/*     */     
/*  73 */     if ((firstByte & 0x80) != 0 && thirdByte == 1)
/*     */     {
/*  75 */       return null; } 
/*  76 */     if (firstByte == 22) {
/*  77 */       if (secondByte == 3 && thirdByte >= 1 && thirdByte <= 3) {
/*  78 */         exploreTLSRecord(input, firstByte, secondByte, thirdByte, alpnProtocols, null);
/*     */         
/*  80 */         return alpnProtocols;
/*     */       } 
/*  82 */       return null;
/*     */     } 
/*  84 */     throw UndertowMessages.MESSAGES.notHandshakeRecord();
/*     */   }
/*     */ 
/*     */   
/*     */   static byte[] rewriteClientHello(byte[] source, List<String> alpnProtocols) throws SSLException {
/*  89 */     ByteBuffer input = ByteBuffer.wrap(source);
/*  90 */     ByteArrayOutputStream out = new ByteArrayOutputStream();
/*     */ 
/*     */     
/*  93 */     if (input.remaining() < 5) {
/*  94 */       throw new BufferUnderflowException();
/*     */     }
/*     */ 
/*     */     
/*     */     try {
/*  99 */       byte firstByte = input.get();
/* 100 */       byte secondByte = input.get();
/* 101 */       byte thirdByte = input.get();
/* 102 */       out.write(firstByte & 0xFF);
/* 103 */       out.write(secondByte & 0xFF);
/* 104 */       out.write(thirdByte & 0xFF);
/* 105 */       if ((firstByte & 0x80) != 0 && thirdByte == 1)
/*     */       {
/* 107 */         return null; } 
/* 108 */       if (firstByte == 22) {
/* 109 */         if (secondByte == 3 && thirdByte == 3) {
/*     */           
/* 111 */           exploreTLSRecord(input, firstByte, secondByte, thirdByte, alpnProtocols, out);
/*     */ 
/*     */           
/* 114 */           int clientHelloLength = out.size() - 9;
/* 115 */           byte[] data = out.toByteArray();
/* 116 */           int newLength = data.length - 5;
/* 117 */           data[3] = (byte)(newLength >> 8 & 0xFF);
/* 118 */           data[4] = (byte)(newLength & 0xFF);
/*     */ 
/*     */           
/* 121 */           data[6] = (byte)(clientHelloLength >> 16 & 0xFF);
/* 122 */           data[7] = (byte)(clientHelloLength >> 8 & 0xFF);
/* 123 */           data[8] = (byte)(clientHelloLength & 0xFF);
/*     */           
/* 125 */           return data;
/*     */         } 
/* 127 */         return null;
/*     */       } 
/* 129 */       throw UndertowMessages.MESSAGES.notHandshakeRecord();
/*     */     }
/* 131 */     catch (ALPNPresentException e) {
/* 132 */       return null;
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
/*     */   private static void exploreTLSRecord(ByteBuffer input, byte firstByte, byte secondByte, byte thirdByte, List<String> alpnProtocols, ByteArrayOutputStream out) throws SSLException {
/* 159 */     if (firstByte != 22) {
/* 160 */       throw UndertowMessages.MESSAGES.notHandshakeRecord();
/*     */     }
/*     */ 
/*     */     
/* 164 */     int recordLength = getInt16(input);
/* 165 */     if (recordLength > input.remaining()) {
/* 166 */       throw new BufferUnderflowException();
/*     */     }
/* 168 */     if (out != null) {
/* 169 */       out.write(0);
/* 170 */       out.write(0);
/*     */     } 
/*     */ 
/*     */     
/*     */     try {
/* 175 */       exploreHandshake(input, secondByte, thirdByte, recordLength, alpnProtocols, out);
/*     */     }
/* 177 */     catch (BufferUnderflowException ignored) {
/* 178 */       throw UndertowMessages.MESSAGES.invalidHandshakeRecord();
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
/*     */   private static void exploreHandshake(ByteBuffer input, byte recordMajorVersion, byte recordMinorVersion, int recordLength, List<String> alpnProtocols, ByteArrayOutputStream out) throws SSLException {
/* 214 */     byte handshakeType = input.get();
/* 215 */     if (handshakeType != 1) {
/* 216 */       throw UndertowMessages.MESSAGES.expectedClientHello();
/*     */     }
/* 218 */     if (out != null) {
/* 219 */       out.write(handshakeType & 0xFF);
/*     */     }
/*     */ 
/*     */     
/* 223 */     int handshakeLength = getInt24(input);
/* 224 */     if (out != null) {
/*     */       
/* 226 */       out.write(0);
/* 227 */       out.write(0);
/* 228 */       out.write(0);
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 233 */     if (handshakeLength > recordLength - 4) {
/* 234 */       throw UndertowMessages.MESSAGES.multiRecordSSLHandshake();
/*     */     }
/*     */     
/* 237 */     input = input.duplicate();
/* 238 */     input.limit(handshakeLength + input.position());
/* 239 */     exploreClientHello(input, alpnProtocols, out);
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
/*     */   private static void exploreClientHello(ByteBuffer input, List<String> alpnProtocols, ByteArrayOutputStream out) throws SSLException {
/* 274 */     byte helloMajorVersion = input.get();
/* 275 */     byte helloMinorVersion = input.get();
/* 276 */     if (out != null) {
/* 277 */       out.write(helloMajorVersion & 0xFF);
/* 278 */       out.write(helloMinorVersion & 0xFF);
/*     */     } 
/* 280 */     if (helloMajorVersion != 3 && helloMinorVersion != 3) {
/*     */       return;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 287 */     for (int i = 0; i < 32; i++) {
/* 288 */       byte d = input.get();
/* 289 */       if (out != null) {
/* 290 */         out.write(d & 0xFF);
/*     */       }
/*     */     } 
/*     */ 
/*     */     
/* 295 */     processByteVector8(input, out);
/*     */ 
/*     */     
/* 298 */     processByteVector16(input, out);
/*     */ 
/*     */     
/* 301 */     processByteVector8(input, out);
/* 302 */     if (input.remaining() > 0) {
/* 303 */       exploreExtensions(input, alpnProtocols, out);
/* 304 */     } else if (out != null) {
/* 305 */       byte[] data = generateAlpnExtension(alpnProtocols);
/* 306 */       writeInt16(out, data.length);
/* 307 */       out.write(data, 0, data.length);
/*     */     } 
/*     */   }
/*     */   
/*     */   private static void writeInt16(ByteArrayOutputStream out, int l) {
/* 312 */     if (out == null)
/* 313 */       return;  out.write(l >> 8 & 0xFF);
/* 314 */     out.write(l & 0xFF);
/*     */   }
/*     */   
/*     */   private static byte[] generateAlpnExtension(List<String> alpnProtocols) {
/* 318 */     ByteArrayOutputStream alpnBits = new ByteArrayOutputStream();
/* 319 */     alpnBits.write(0);
/* 320 */     alpnBits.write(16);
/* 321 */     int length = 2;
/* 322 */     for (String p : alpnProtocols) {
/* 323 */       length++;
/* 324 */       length += p.length();
/*     */     } 
/* 326 */     writeInt16(alpnBits, length);
/* 327 */     length -= 2;
/* 328 */     writeInt16(alpnBits, length);
/* 329 */     for (String p : alpnProtocols) {
/* 330 */       alpnBits.write(p.length() & 0xFF);
/* 331 */       for (int i = 0; i < p.length(); i++) {
/* 332 */         alpnBits.write(p.charAt(i) & 0xFF);
/*     */       }
/*     */     } 
/* 335 */     return alpnBits.toByteArray();
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
/*     */   private static void exploreExtensions(ByteBuffer input, List<String> alpnProtocols, ByteArrayOutputStream out) throws SSLException {
/* 352 */     ByteArrayOutputStream extensionOut = (out == null) ? null : new ByteArrayOutputStream();
/* 353 */     int length = getInt16(input);
/* 354 */     writeInt16(extensionOut, 0);
/* 355 */     while (length > 0) {
/* 356 */       int extType = getInt16(input);
/* 357 */       writeInt16(extensionOut, extType);
/* 358 */       int extLen = getInt16(input);
/* 359 */       writeInt16(extensionOut, extLen);
/* 360 */       if (extType == 16) {
/* 361 */         if (out == null) {
/* 362 */           exploreALPNExt(input, alpnProtocols);
/*     */         } else {
/* 364 */           throw new ALPNPresentException();
/*     */         } 
/*     */       } else {
/* 367 */         processByteVector(input, extLen, extensionOut);
/*     */       } 
/*     */       
/* 370 */       length -= extLen + 4;
/*     */     } 
/* 372 */     if (out != null) {
/* 373 */       byte[] alpnBits = generateAlpnExtension(alpnProtocols);
/* 374 */       extensionOut.write(alpnBits, 0, alpnBits.length);
/* 375 */       byte[] extensionsData = extensionOut.toByteArray();
/* 376 */       int newLength = extensionsData.length - 2;
/* 377 */       extensionsData[0] = (byte)(newLength >> 8 & 0xFF);
/* 378 */       extensionsData[1] = (byte)(newLength & 0xFF);
/* 379 */       out.write(extensionsData, 0, extensionsData.length);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private static void exploreALPNExt(ByteBuffer input, List<String> alpnProtocols) {
/* 385 */     int length = getInt16(input);
/* 386 */     int end = input.position() + length;
/* 387 */     while (input.position() < end) {
/* 388 */       alpnProtocols.add(readByteVector8(input));
/*     */     }
/*     */   }
/*     */   
/*     */   private static int getInt8(ByteBuffer input) {
/* 393 */     return input.get();
/*     */   }
/*     */   
/*     */   private static int getInt16(ByteBuffer input) {
/* 397 */     return (input.get() & 0xFF) << 8 | input.get() & 0xFF;
/*     */   }
/*     */   
/*     */   private static int getInt24(ByteBuffer input) {
/* 401 */     return (input.get() & 0xFF) << 16 | (input.get() & 0xFF) << 8 | input
/* 402 */       .get() & 0xFF;
/*     */   }
/*     */   
/*     */   private static void processByteVector8(ByteBuffer input, ByteArrayOutputStream out) {
/* 406 */     int int8 = getInt8(input);
/* 407 */     if (out != null) {
/* 408 */       out.write(int8 & 0xFF);
/*     */     }
/* 410 */     processByteVector(input, int8, out);
/*     */   }
/*     */ 
/*     */   
/*     */   private static void processByteVector(ByteBuffer input, int length, ByteArrayOutputStream out) {
/* 415 */     for (int i = 0; i < length; i++) {
/* 416 */       byte b = input.get();
/* 417 */       if (out != null)
/* 418 */         out.write(b & 0xFF); 
/*     */     } 
/*     */   }
/*     */   
/*     */   private static String readByteVector8(ByteBuffer input) {
/* 423 */     int length = getInt8(input);
/* 424 */     byte[] data = new byte[length];
/* 425 */     input.get(data);
/* 426 */     return new String(data, StandardCharsets.US_ASCII);
/*     */   }
/*     */   
/*     */   private static void processByteVector16(ByteBuffer input, ByteArrayOutputStream out) {
/* 430 */     int int16 = getInt16(input);
/* 431 */     writeInt16(out, int16);
/* 432 */     processByteVector(input, int16, out);
/*     */   }
/*     */   
/*     */   private static final class ALPNPresentException extends RuntimeException {
/*     */     private ALPNPresentException() {}
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\protocols\ssl\ALPNHackClientHelloExplorer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */