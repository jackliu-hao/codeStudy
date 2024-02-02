/*     */ package io.undertow.protocols.ssl;
/*     */ 
/*     */ import io.undertow.UndertowMessages;
/*     */ import java.io.IOException;
/*     */ import java.nio.BufferUnderflowException;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.charset.StandardCharsets;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import javax.net.ssl.SNIHostName;
/*     */ import javax.net.ssl.SNIServerName;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ final class SNISSLExplorer
/*     */ {
/*     */   public static final int RECORD_HEADER_SIZE = 5;
/*     */   
/*     */   public static int getRequiredSize(ByteBuffer source) {
/*  74 */     ByteBuffer input = source.duplicate();
/*     */ 
/*     */     
/*  77 */     if (input.remaining() < 5) {
/*  78 */       throw new BufferUnderflowException();
/*     */     }
/*     */ 
/*     */     
/*  82 */     byte firstByte = input.get();
/*  83 */     input.get();
/*  84 */     byte thirdByte = input.get();
/*  85 */     if ((firstByte & 0x80) != 0 && thirdByte == 1) {
/*  86 */       return 5;
/*     */     }
/*  88 */     return ((input.get() & 0xFF) << 8 | input.get() & 0xFF) + 5;
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
/*     */   public static int getRequiredSize(byte[] source, int offset, int length) throws IOException {
/* 117 */     ByteBuffer byteBuffer = ByteBuffer.wrap(source, offset, length).asReadOnlyBuffer();
/* 118 */     return getRequiredSize(byteBuffer);
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
/*     */   public static List<SNIServerName> explore(ByteBuffer source) throws SSLException {
/* 150 */     ByteBuffer input = source.duplicate();
/*     */ 
/*     */     
/* 153 */     if (input.remaining() < 5) {
/* 154 */       throw new BufferUnderflowException();
/*     */     }
/*     */ 
/*     */     
/* 158 */     byte firstByte = input.get();
/* 159 */     byte secondByte = input.get();
/* 160 */     byte thirdByte = input.get();
/* 161 */     if ((firstByte & 0x80) != 0 && thirdByte == 1)
/*     */     {
/* 163 */       return Collections.emptyList(); } 
/* 164 */     if (firstByte == 22) {
/* 165 */       return exploreTLSRecord(input, firstByte, secondByte, thirdByte);
/*     */     }
/*     */     
/* 168 */     throw UndertowMessages.MESSAGES.notHandshakeRecord();
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
/*     */   public static List<SNIServerName> explore(byte[] source, int offset, int length) throws IOException {
/* 201 */     ByteBuffer byteBuffer = ByteBuffer.wrap(source, offset, length).asReadOnlyBuffer();
/* 202 */     return explore(byteBuffer);
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
/*     */   private static List<SNIServerName> exploreTLSRecord(ByteBuffer input, byte firstByte, byte secondByte, byte thirdByte) throws SSLException {
/* 228 */     if (firstByte != 22) {
/* 229 */       throw UndertowMessages.MESSAGES.notHandshakeRecord();
/*     */     }
/*     */ 
/*     */     
/* 233 */     int recordLength = getInt16(input);
/* 234 */     if (recordLength > input.remaining()) {
/* 235 */       throw new BufferUnderflowException();
/*     */     }
/*     */ 
/*     */     
/*     */     try {
/* 240 */       return exploreHandshake(input, secondByte, thirdByte, recordLength);
/*     */     }
/* 242 */     catch (BufferUnderflowException ignored) {
/* 243 */       throw UndertowMessages.MESSAGES.invalidHandshakeRecord();
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
/*     */   private static List<SNIServerName> exploreHandshake(ByteBuffer input, byte recordMajorVersion, byte recordMinorVersion, int recordLength) throws SSLException {
/* 279 */     byte handshakeType = input.get();
/* 280 */     if (handshakeType != 1) {
/* 281 */       throw UndertowMessages.MESSAGES.expectedClientHello();
/*     */     }
/*     */ 
/*     */     
/* 285 */     int handshakeLength = getInt24(input);
/*     */ 
/*     */ 
/*     */     
/* 289 */     if (handshakeLength > recordLength - 4) {
/* 290 */       throw UndertowMessages.MESSAGES.multiRecordSSLHandshake();
/*     */     }
/*     */     
/* 293 */     input = input.duplicate();
/* 294 */     input.limit(handshakeLength + input.position());
/* 295 */     return exploreClientHello(input, recordMajorVersion, recordMinorVersion);
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
/*     */   private static List<SNIServerName> exploreClientHello(ByteBuffer input, byte recordMajorVersion, byte recordMinorVersion) throws SSLException {
/* 330 */     ExtensionInfo info = null;
/*     */ 
/*     */     
/* 333 */     input.get();
/* 334 */     input.get();
/*     */ 
/*     */     
/* 337 */     int position = input.position();
/* 338 */     input.position(position + 32);
/*     */ 
/*     */     
/* 341 */     ignoreByteVector8(input);
/*     */ 
/*     */     
/* 344 */     int csLen = getInt16(input);
/* 345 */     while (csLen > 0) {
/* 346 */       getInt8(input);
/* 347 */       getInt8(input);
/* 348 */       csLen -= 2;
/*     */     } 
/*     */ 
/*     */     
/* 352 */     ignoreByteVector8(input);
/*     */     
/* 354 */     if (input.remaining() > 0) {
/* 355 */       info = exploreExtensions(input);
/*     */     }
/*     */     
/* 358 */     List<SNIServerName> snList = (info != null) ? info.sni : Collections.<SNIServerName>emptyList();
/*     */     
/* 360 */     return snList;
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
/*     */   private static ExtensionInfo exploreExtensions(ByteBuffer input) throws SSLException {
/* 378 */     List<SNIServerName> sni = Collections.emptyList();
/* 379 */     List<String> alpn = Collections.emptyList();
/*     */     
/* 381 */     int length = getInt16(input);
/* 382 */     while (length > 0) {
/* 383 */       int extType = getInt16(input);
/* 384 */       int extLen = getInt16(input);
/*     */       
/* 386 */       if (extType == 0) {
/* 387 */         sni = exploreSNIExt(input, extLen);
/* 388 */       } else if (extType == 16) {
/* 389 */         alpn = exploreALPN(input, extLen);
/*     */       } else {
/* 391 */         ignoreByteVector(input, extLen);
/*     */       } 
/*     */       
/* 394 */       length -= extLen + 4;
/*     */     } 
/*     */     
/* 397 */     return new ExtensionInfo(sni, alpn);
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
/*     */   private static List<String> exploreALPN(ByteBuffer input, int extLen) throws SSLException {
/* 410 */     ArrayList<String> strings = new ArrayList<>();
/*     */     
/* 412 */     int rem = extLen;
/* 413 */     if (extLen >= 2) {
/* 414 */       int listLen = getInt16(input);
/* 415 */       if (listLen == 0 || listLen + 2 != extLen) {
/* 416 */         throw UndertowMessages.MESSAGES.invalidTlsExt();
/*     */       }
/*     */       
/* 419 */       rem -= 2;
/* 420 */       while (rem > 0) {
/* 421 */         int len = getInt8(input);
/* 422 */         if (len > rem) {
/* 423 */           throw UndertowMessages.MESSAGES.notEnoughData();
/*     */         }
/* 425 */         byte[] b = new byte[len];
/* 426 */         input.get(b);
/* 427 */         strings.add(new String(b, StandardCharsets.UTF_8));
/*     */         
/* 429 */         rem -= len + 1;
/*     */       } 
/*     */     } 
/* 432 */     return strings.isEmpty() ? Collections.<String>emptyList() : strings;
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
/*     */   private static List<SNIServerName> exploreSNIExt(ByteBuffer input, int extLen) throws SSLException {
/* 456 */     Map<Integer, SNIServerName> sniMap = new LinkedHashMap<>();
/*     */     
/* 458 */     int remains = extLen;
/* 459 */     if (extLen >= 2) {
/* 460 */       int listLen = getInt16(input);
/* 461 */       if (listLen == 0 || listLen + 2 != extLen) {
/* 462 */         throw UndertowMessages.MESSAGES.invalidTlsExt();
/*     */       }
/*     */       
/* 465 */       remains -= 2;
/* 466 */       while (remains > 0) {
/* 467 */         SNIServerName serverName; int code = getInt8(input);
/* 468 */         int snLen = getInt16(input);
/* 469 */         if (snLen > remains) {
/* 470 */           throw UndertowMessages.MESSAGES.notEnoughData();
/*     */         }
/* 472 */         byte[] encoded = new byte[snLen];
/* 473 */         input.get(encoded);
/*     */ 
/*     */         
/* 476 */         switch (code) {
/*     */           case 0:
/* 478 */             if (encoded.length == 0) {
/* 479 */               throw UndertowMessages.MESSAGES.emptyHostNameSni();
/*     */             }
/* 481 */             serverName = new SNIHostName(encoded);
/*     */             break;
/*     */           default:
/* 484 */             serverName = new UnknownServerName(code, encoded);
/*     */             break;
/*     */         } 
/* 487 */         if (sniMap.put(Integer.valueOf(serverName.getType()), serverName) != null) {
/* 488 */           throw UndertowMessages.MESSAGES.duplicatedSniServerName(serverName.getType());
/*     */         }
/*     */         
/* 491 */         remains -= encoded.length + 3;
/*     */       }
/*     */     
/* 494 */     } else if (extLen == 0) {
/* 495 */       throw UndertowMessages.MESSAGES.invalidTlsExt();
/*     */     } 
/*     */     
/* 498 */     if (remains != 0) {
/* 499 */       throw UndertowMessages.MESSAGES.invalidTlsExt();
/*     */     }
/*     */     
/* 502 */     return Collections.unmodifiableList(new ArrayList<>(sniMap.values()));
/*     */   }
/*     */   
/*     */   private static int getInt8(ByteBuffer input) {
/* 506 */     return input.get();
/*     */   }
/*     */   
/*     */   private static int getInt16(ByteBuffer input) {
/* 510 */     return (input.get() & 0xFF) << 8 | input.get() & 0xFF;
/*     */   }
/*     */   
/*     */   private static int getInt24(ByteBuffer input) {
/* 514 */     return (input.get() & 0xFF) << 16 | (input.get() & 0xFF) << 8 | input
/* 515 */       .get() & 0xFF;
/*     */   }
/*     */   
/*     */   private static void ignoreByteVector8(ByteBuffer input) {
/* 519 */     ignoreByteVector(input, getInt8(input));
/*     */   }
/*     */   
/*     */   private static void ignoreByteVector(ByteBuffer input, int length) {
/* 523 */     if (length != 0) {
/* 524 */       int position = input.position();
/* 525 */       input.position(position + length);
/*     */     } 
/*     */   }
/*     */   
/*     */   static final class UnknownServerName extends SNIServerName {
/*     */     UnknownServerName(int code, byte[] encoded) {
/* 531 */       super(code, encoded);
/*     */     }
/*     */   }
/*     */   
/*     */   static final class ExtensionInfo {
/*     */     final List<SNIServerName> sni;
/*     */     final List<String> alpn;
/*     */     
/*     */     ExtensionInfo(List<SNIServerName> sni, List<String> alpn) {
/* 540 */       this.sni = sni;
/* 541 */       this.alpn = alpn;
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\protocols\ssl\SNISSLExplorer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */