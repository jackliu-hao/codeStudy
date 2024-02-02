/*      */ package io.undertow.websockets.core.protocol.version07;
/*      */ 
/*      */ import io.undertow.UndertowLogger;
/*      */ import java.io.BufferedInputStream;
/*      */ import java.io.BufferedOutputStream;
/*      */ import java.io.ByteArrayInputStream;
/*      */ import java.io.ByteArrayOutputStream;
/*      */ import java.io.FileOutputStream;
/*      */ import java.io.FilterInputStream;
/*      */ import java.io.FilterOutputStream;
/*      */ import java.io.IOException;
/*      */ import java.io.ObjectInputStream;
/*      */ import java.io.ObjectOutputStream;
/*      */ import java.io.ObjectStreamClass;
/*      */ import java.io.Serializable;
/*      */ import java.nio.ByteBuffer;
/*      */ import java.nio.CharBuffer;
/*      */ import java.nio.charset.StandardCharsets;
/*      */ import java.nio.file.Files;
/*      */ import java.nio.file.Path;
/*      */ import java.nio.file.Paths;
/*      */ import java.util.zip.GZIPInputStream;
/*      */ import java.util.zip.GZIPOutputStream;
/*      */ import org.xnio.IoUtils;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ class Base64
/*      */ {
/*      */   public static final int NO_OPTIONS = 0;
/*      */   public static final int ENCODE = 1;
/*      */   public static final int DECODE = 0;
/*      */   public static final int GZIP = 2;
/*      */   public static final int DONT_GUNZIP = 4;
/*      */   public static final int DO_BREAK_LINES = 8;
/*      */   public static final int URL_SAFE = 16;
/*      */   public static final int ORDERED = 32;
/*      */   private static final int MAX_LINE_LENGTH = 76;
/*      */   private static final byte EQUALS_SIGN = 61;
/*      */   private static final byte NEW_LINE = 10;
/*      */   private static final byte WHITE_SPACE_ENC = -5;
/*      */   private static final byte EQUALS_SIGN_ENC = -1;
/*  214 */   private static final byte[] _STANDARD_ALPHABET = new byte[] { 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 43, 47 };
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  226 */   private static final byte[] _STANDARD_DECODABET = new byte[] { -9, -9, -9, -9, -9, -9, -9, -9, -9, -5, -5, -9, -9, -5, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -5, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, 62, -9, -9, -9, 63, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, -9, -9, -9, -1, -9, -9, -9, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, -9, -9, -9, -9, -9, -9, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9 };
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  266 */   private static final byte[] _URL_SAFE_ALPHABET = new byte[] { 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 45, 95 };
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  278 */   private static final byte[] _URL_SAFE_DECODABET = new byte[] { -9, -9, -9, -9, -9, -9, -9, -9, -9, -5, -5, -9, -9, -5, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -5, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, 62, -9, -9, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, -9, -9, -9, -1, -9, -9, -9, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, -9, -9, -9, -9, 63, -9, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9 };
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  321 */   private static final byte[] _ORDERED_ALPHABET = new byte[] { 45, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 95, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122 };
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  333 */   private static final byte[] _ORDERED_DECODABET = new byte[] { -9, -9, -9, -9, -9, -9, -9, -9, -9, -5, -5, -9, -9, -5, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -5, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, 0, -9, -9, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, -9, -9, -9, -1, -9, -9, -9, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, -9, -9, -9, -9, 37, -9, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9 };
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static byte[] getAlphabet(int options) {
/*  378 */     if ((options & 0x10) == 16)
/*  379 */       return _URL_SAFE_ALPHABET; 
/*  380 */     if ((options & 0x20) == 32) {
/*  381 */       return _ORDERED_ALPHABET;
/*      */     }
/*  383 */     return _STANDARD_ALPHABET;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static byte[] getDecodabet(int options) {
/*  393 */     if ((options & 0x10) == 16)
/*  394 */       return _URL_SAFE_DECODABET; 
/*  395 */     if ((options & 0x20) == 32) {
/*  396 */       return _ORDERED_DECODABET;
/*      */     }
/*  398 */     return _STANDARD_DECODABET;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static byte[] encode3to4(byte[] b4, byte[] threeBytes, int numSigBytes, int options) {
/*  421 */     encode3to4(threeBytes, 0, numSigBytes, b4, 0, options);
/*  422 */     return b4;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static byte[] encode3to4(byte[] source, int srcOffset, int numSigBytes, byte[] destination, int destOffset, int options) {
/*  448 */     byte[] ALPHABET = getAlphabet(options);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  461 */     int inBuff = ((numSigBytes > 0) ? (source[srcOffset] << 24 >>> 8) : 0) | ((numSigBytes > 1) ? (source[srcOffset + 1] << 24 >>> 16) : 0) | ((numSigBytes > 2) ? (source[srcOffset + 2] << 24 >>> 24) : 0);
/*      */ 
/*      */ 
/*      */     
/*  465 */     switch (numSigBytes) {
/*      */       case 3:
/*  467 */         destination[destOffset] = ALPHABET[inBuff >>> 18];
/*  468 */         destination[destOffset + 1] = ALPHABET[inBuff >>> 12 & 0x3F];
/*  469 */         destination[destOffset + 2] = ALPHABET[inBuff >>> 6 & 0x3F];
/*  470 */         destination[destOffset + 3] = ALPHABET[inBuff & 0x3F];
/*  471 */         return destination;
/*      */       
/*      */       case 2:
/*  474 */         destination[destOffset] = ALPHABET[inBuff >>> 18];
/*  475 */         destination[destOffset + 1] = ALPHABET[inBuff >>> 12 & 0x3F];
/*  476 */         destination[destOffset + 2] = ALPHABET[inBuff >>> 6 & 0x3F];
/*  477 */         destination[destOffset + 3] = 61;
/*  478 */         return destination;
/*      */       
/*      */       case 1:
/*  481 */         destination[destOffset] = ALPHABET[inBuff >>> 18];
/*  482 */         destination[destOffset + 1] = ALPHABET[inBuff >>> 12 & 0x3F];
/*  483 */         destination[destOffset + 2] = 61;
/*  484 */         destination[destOffset + 3] = 61;
/*  485 */         return destination;
/*      */     } 
/*      */     
/*  488 */     return destination;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void encode(ByteBuffer raw, ByteBuffer encoded) {
/*  501 */     byte[] raw3 = new byte[3];
/*  502 */     byte[] enc4 = new byte[4];
/*      */     
/*  504 */     while (raw.hasRemaining()) {
/*  505 */       int rem = Math.min(3, raw.remaining());
/*  506 */       raw.get(raw3, 0, rem);
/*  507 */       encode3to4(enc4, raw3, rem, 0);
/*  508 */       encoded.put(enc4);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void encode(ByteBuffer raw, CharBuffer encoded) {
/*  521 */     byte[] raw3 = new byte[3];
/*  522 */     byte[] enc4 = new byte[4];
/*      */     
/*  524 */     while (raw.hasRemaining()) {
/*  525 */       int rem = Math.min(3, raw.remaining());
/*  526 */       raw.get(raw3, 0, rem);
/*  527 */       encode3to4(enc4, raw3, rem, 0);
/*  528 */       for (int i = 0; i < 4; i++) {
/*  529 */         encoded.put((char)(enc4[i] & 0xFF));
/*      */       }
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String encodeObject(Serializable serializableObject) throws IOException {
/*  552 */     return encodeObject(serializableObject, 0);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String encodeObject(Serializable serializableObject, int options) throws IOException {
/*  587 */     if (serializableObject == null) {
/*  588 */       throw new NullPointerException("Cannot serialize a null object.");
/*      */     }
/*      */ 
/*      */     
/*  592 */     ByteArrayOutputStream baos = null;
/*  593 */     java.io.OutputStream b64os = null;
/*  594 */     GZIPOutputStream gzos = null;
/*  595 */     ObjectOutputStream oos = null;
/*      */ 
/*      */     
/*      */     try {
/*  599 */       baos = new ByteArrayOutputStream();
/*  600 */       b64os = new OutputStream(baos, 0x1 | options);
/*  601 */       if ((options & 0x2) != 0) {
/*      */         
/*  603 */         gzos = new GZIPOutputStream(b64os);
/*  604 */         oos = new ObjectOutputStream(gzos);
/*      */       } else {
/*      */         
/*  607 */         oos = new ObjectOutputStream(b64os);
/*      */       } 
/*  609 */       oos.writeObject(serializableObject);
/*      */     }
/*  611 */     catch (IOException e) {
/*      */ 
/*      */       
/*  614 */       throw e;
/*      */     } finally {
/*      */       
/*  617 */       IoUtils.safeClose(oos);
/*  618 */       IoUtils.safeClose(gzos);
/*  619 */       IoUtils.safeClose(b64os);
/*  620 */       IoUtils.safeClose(baos);
/*      */     } 
/*      */ 
/*      */     
/*  624 */     return new String(baos.toByteArray(), StandardCharsets.US_ASCII);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String encodeBytes(byte[] source) {
/*  640 */     String encoded = null;
/*      */     try {
/*  642 */       encoded = encodeBytes(source, 0, source.length, 0);
/*  643 */     } catch (IOException ex) {
/*  644 */       assert false : ex.getMessage();
/*      */     } 
/*  646 */     assert encoded != null;
/*  647 */     return encoded;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String encodeBytes(byte[] source, int options) throws IOException {
/*  682 */     return encodeBytes(source, 0, source.length, options);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String encodeBytes(byte[] source, int off, int len) {
/*  706 */     String encoded = null;
/*      */     try {
/*  708 */       encoded = encodeBytes(source, off, len, 0);
/*  709 */     } catch (IOException ex) {
/*  710 */       assert false : ex.getMessage();
/*      */     } 
/*  712 */     assert encoded != null;
/*  713 */     return encoded;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String encodeBytes(byte[] source, int off, int len, int options) throws IOException {
/*  751 */     byte[] encoded = encodeBytesToBytes(source, off, len, options);
/*      */ 
/*      */     
/*  754 */     return new String(encoded, StandardCharsets.US_ASCII);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static byte[] encodeBytesToBytes(byte[] source) {
/*  769 */     byte[] encoded = null;
/*      */     try {
/*  771 */       encoded = encodeBytesToBytes(source, 0, source.length, 0);
/*  772 */     } catch (IOException ex) {
/*  773 */       assert false : "IOExceptions only come from GZipping, which is turned off: " + ex.getMessage();
/*      */     } 
/*  775 */     return encoded;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static byte[] encodeBytesToBytes(byte[] source, int off, int len, int options) throws IOException {
/*  797 */     if (source == null) {
/*  798 */       throw new NullPointerException("Cannot serialize a null array.");
/*      */     }
/*      */     
/*  801 */     if (off < 0) {
/*  802 */       throw new IllegalArgumentException("Cannot have negative offset: " + off);
/*      */     }
/*      */     
/*  805 */     if (len < 0) {
/*  806 */       throw new IllegalArgumentException("Cannot have length offset: " + len);
/*      */     }
/*      */     
/*  809 */     if (off + len > source.length) {
/*  810 */       throw new IllegalArgumentException(String.format("Cannot have offset of %d and length of %d with array of length %d", new Object[] {
/*  811 */               Integer.valueOf(off), Integer.valueOf(len), Integer.valueOf(source.length)
/*      */             }));
/*      */     }
/*      */     
/*  815 */     if ((options & 0x2) != 0) {
/*  816 */       ByteArrayOutputStream baos = null;
/*  817 */       GZIPOutputStream gzos = null;
/*  818 */       OutputStream b64os = null;
/*      */ 
/*      */       
/*      */       try {
/*  822 */         baos = new ByteArrayOutputStream();
/*  823 */         b64os = new OutputStream(baos, 0x1 | options);
/*  824 */         gzos = new GZIPOutputStream(b64os);
/*      */         
/*  826 */         gzos.write(source, off, len);
/*  827 */         gzos.close();
/*      */       }
/*  829 */       catch (IOException iOException) {
/*      */ 
/*      */         
/*  832 */         throw iOException;
/*      */       } finally {
/*      */         
/*  835 */         IoUtils.safeClose(gzos);
/*  836 */         IoUtils.safeClose(b64os);
/*  837 */         IoUtils.safeClose(baos);
/*      */       } 
/*      */       
/*  840 */       return baos.toByteArray();
/*      */     } 
/*      */ 
/*      */ 
/*      */     
/*  845 */     boolean breakLines = ((options & 0x8) != 0);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  854 */     int encLen = len / 3 * 4 + ((len % 3 > 0) ? 4 : 0);
/*  855 */     if (breakLines) {
/*  856 */       encLen += encLen / 76;
/*      */     }
/*  858 */     byte[] outBuff = new byte[encLen];
/*      */     
/*  860 */     int d = 0;
/*  861 */     int e = 0;
/*  862 */     int len2 = len - 2;
/*  863 */     int lineLength = 0;
/*  864 */     for (; d < len2; d += 3, e += 4) {
/*  865 */       encode3to4(source, d + off, 3, outBuff, e, options);
/*      */       
/*  867 */       lineLength += 4;
/*  868 */       if (breakLines && lineLength >= 76) {
/*  869 */         outBuff[e + 4] = 10;
/*  870 */         e++;
/*  871 */         lineLength = 0;
/*      */       } 
/*      */     } 
/*      */     
/*  875 */     if (d < len) {
/*  876 */       encode3to4(source, d + off, len - d, outBuff, e, options);
/*  877 */       e += 4;
/*      */     } 
/*      */ 
/*      */     
/*  881 */     if (e <= outBuff.length - 1) {
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  886 */       byte[] finalOut = new byte[e];
/*  887 */       System.arraycopy(outBuff, 0, finalOut, 0, e);
/*      */       
/*  889 */       return finalOut;
/*      */     } 
/*      */     
/*  892 */     return outBuff;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static int decode4to3(byte[] source, int srcOffset, byte[] destination, int destOffset, int options) {
/*  926 */     if (source == null) {
/*  927 */       throw new NullPointerException("Source array was null.");
/*      */     }
/*  929 */     if (destination == null) {
/*  930 */       throw new NullPointerException("Destination array was null.");
/*      */     }
/*  932 */     if (srcOffset < 0 || srcOffset + 3 >= source.length)
/*  933 */       throw new IllegalArgumentException(String.format("Source array with length %d cannot have offset of %d and still process four bytes.", new Object[] {
/*  934 */               Integer.valueOf(source.length), 
/*  935 */               Integer.valueOf(srcOffset)
/*      */             })); 
/*  937 */     if (destOffset < 0 || destOffset + 2 >= destination.length) {
/*  938 */       throw new IllegalArgumentException(String.format("Destination array with length %d cannot have offset of %d and still store three bytes.", new Object[] {
/*      */               
/*  940 */               Integer.valueOf(destination.length), Integer.valueOf(destOffset)
/*      */             }));
/*      */     }
/*  943 */     byte[] DECODABET = getDecodabet(options);
/*      */ 
/*      */     
/*  946 */     if (source[srcOffset + 2] == 61) {
/*      */ 
/*      */ 
/*      */       
/*  950 */       int i = (DECODABET[source[srcOffset]] & 0xFF) << 18 | (DECODABET[source[srcOffset + 1]] & 0xFF) << 12;
/*      */       
/*  952 */       destination[destOffset] = (byte)(i >>> 16);
/*  953 */       return 1;
/*      */     } 
/*      */ 
/*      */     
/*  957 */     if (source[srcOffset + 3] == 61) {
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  962 */       int i = (DECODABET[source[srcOffset]] & 0xFF) << 18 | (DECODABET[source[srcOffset + 1]] & 0xFF) << 12 | (DECODABET[source[srcOffset + 2]] & 0xFF) << 6;
/*      */ 
/*      */       
/*  965 */       destination[destOffset] = (byte)(i >>> 16);
/*  966 */       destination[destOffset + 1] = (byte)(i >>> 8);
/*  967 */       return 2;
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  977 */     int outBuff = (DECODABET[source[srcOffset]] & 0xFF) << 18 | (DECODABET[source[srcOffset + 1]] & 0xFF) << 12 | (DECODABET[source[srcOffset + 2]] & 0xFF) << 6 | DECODABET[source[srcOffset + 3]] & 0xFF;
/*      */ 
/*      */     
/*  980 */     destination[destOffset] = (byte)(outBuff >> 16);
/*  981 */     destination[destOffset + 1] = (byte)(outBuff >> 8);
/*  982 */     destination[destOffset + 2] = (byte)outBuff;
/*      */     
/*  984 */     return 3;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static byte[] decode(byte[] source) throws IOException {
/*  999 */     byte[] decoded = null;
/*      */     
/* 1001 */     decoded = decode(source, 0, source.length, 0);
/*      */ 
/*      */ 
/*      */     
/* 1005 */     return decoded;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static byte[] decode(byte[] source, int off, int len, int options) throws IOException {
/* 1025 */     if (source == null) {
/* 1026 */       throw new NullPointerException("Cannot decode null source array.");
/*      */     }
/* 1028 */     if (off < 0 || off + len > source.length) {
/* 1029 */       throw new IllegalArgumentException(String.format("Source array with length %d cannot have offset of %d and process %d bytes.", new Object[] {
/* 1030 */               Integer.valueOf(source.length), Integer.valueOf(off), Integer.valueOf(len)
/*      */             }));
/*      */     }
/* 1033 */     if (len == 0)
/* 1034 */       return new byte[0]; 
/* 1035 */     if (len < 4) {
/* 1036 */       throw new IllegalArgumentException("Base64-encoded string must have at least four characters, but length specified was " + len);
/*      */     }
/*      */ 
/*      */     
/* 1040 */     byte[] DECODABET = getDecodabet(options);
/*      */     
/* 1042 */     int len34 = len * 3 / 4;
/* 1043 */     byte[] outBuff = new byte[len34];
/* 1044 */     int outBuffPosn = 0;
/*      */     
/* 1046 */     byte[] b4 = new byte[4];
/* 1047 */     int b4Posn = 0;
/* 1048 */     int i = 0;
/* 1049 */     byte sbiDecode = 0;
/*      */     
/* 1051 */     for (i = off; i < off + len; i++) {
/*      */       
/* 1053 */       sbiDecode = DECODABET[source[i] & 0xFF];
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 1058 */       if (sbiDecode >= -5) {
/* 1059 */         if (sbiDecode >= -1) {
/* 1060 */           b4[b4Posn++] = source[i];
/* 1061 */           if (b4Posn > 3) {
/* 1062 */             outBuffPosn += decode4to3(b4, 0, outBuff, outBuffPosn, options);
/* 1063 */             b4Posn = 0;
/*      */ 
/*      */             
/* 1066 */             if (source[i] == 61) {
/*      */               break;
/*      */             }
/*      */           }
/*      */         
/*      */         } 
/*      */       } else {
/*      */         
/* 1074 */         throw new IOException(String.format("Bad Base64 input character decimal %d in array position %d", new Object[] {
/* 1075 */                 Integer.valueOf(source[i] & 0xFF), Integer.valueOf(i)
/*      */               }));
/*      */       } 
/*      */     } 
/* 1079 */     byte[] out = new byte[outBuffPosn];
/* 1080 */     System.arraycopy(outBuff, 0, out, 0, outBuffPosn);
/* 1081 */     return out;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static byte[] decode(String s) throws IOException {
/* 1093 */     return decode(s, 0);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static byte[] decode(String s, int options) throws IOException {
/* 1108 */     if (s == null) {
/* 1109 */       throw new NullPointerException("Input string was null.");
/*      */     }
/*      */     
/* 1112 */     byte[] bytes = s.getBytes(StandardCharsets.US_ASCII);
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1117 */     bytes = decode(bytes, 0, bytes.length, options);
/*      */ 
/*      */ 
/*      */     
/* 1121 */     boolean dontGunzip = ((options & 0x4) != 0);
/* 1122 */     if (bytes != null && bytes.length >= 4 && !dontGunzip) {
/*      */       
/* 1124 */       int head = bytes[0] & 0xFF | bytes[1] << 8 & 0xFF00;
/* 1125 */       if (35615 == head) {
/* 1126 */         ByteArrayInputStream bais = null;
/* 1127 */         GZIPInputStream gzis = null;
/* 1128 */         ByteArrayOutputStream baos = null;
/* 1129 */         byte[] buffer = new byte[2048];
/* 1130 */         int length = 0;
/*      */         
/*      */         try {
/* 1133 */           baos = new ByteArrayOutputStream();
/* 1134 */           bais = new ByteArrayInputStream(bytes);
/* 1135 */           gzis = new GZIPInputStream(bais);
/*      */           
/* 1137 */           while ((length = gzis.read(buffer)) >= 0) {
/* 1138 */             baos.write(buffer, 0, length);
/*      */           }
/*      */ 
/*      */           
/* 1142 */           bytes = baos.toByteArray();
/*      */         
/*      */         }
/* 1145 */         catch (IOException e) {
/* 1146 */           UndertowLogger.REQUEST_IO_LOGGER.ioException(e);
/*      */         }
/*      */         finally {
/*      */           
/* 1150 */           IoUtils.safeClose(baos);
/* 1151 */           IoUtils.safeClose(gzis);
/* 1152 */           IoUtils.safeClose(bais);
/*      */         } 
/*      */       } 
/*      */     } 
/*      */ 
/*      */     
/* 1158 */     return bytes;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Object decodeToObject(String encodedObject) throws IOException, ClassNotFoundException {
/* 1172 */     return decodeToObject(encodedObject, 0, null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Object decodeToObject(String encodedObject, int options, final ClassLoader loader) throws IOException, ClassNotFoundException {
/* 1192 */     byte[] objBytes = decode(encodedObject, options);
/*      */     
/* 1194 */     ByteArrayInputStream bais = null;
/* 1195 */     ObjectInputStream ois = null;
/* 1196 */     Object obj = null;
/*      */     
/*      */     try {
/* 1199 */       bais = new ByteArrayInputStream(objBytes);
/*      */ 
/*      */       
/* 1202 */       if (loader == null) {
/* 1203 */         ois = new ObjectInputStream(bais);
/*      */       
/*      */       }
/*      */       else {
/*      */ 
/*      */         
/* 1209 */         ois = new ObjectInputStream(bais)
/*      */           {
/*      */             public Class<?> resolveClass(ObjectStreamClass streamClass) throws IOException, ClassNotFoundException
/*      */             {
/* 1213 */               Class<?> c = Class.forName(streamClass.getName(), false, loader);
/* 1214 */               if (c == null) {
/* 1215 */                 return super.resolveClass(streamClass);
/*      */               }
/* 1217 */               return c;
/*      */             }
/*      */           };
/*      */       } 
/*      */ 
/*      */       
/* 1223 */       obj = ois.readObject();
/*      */     }
/* 1225 */     catch (IOException e) {
/* 1226 */       throw e;
/*      */     }
/* 1228 */     catch (ClassNotFoundException e) {
/* 1229 */       throw e;
/*      */     } finally {
/*      */       
/* 1232 */       IoUtils.safeClose(bais);
/* 1233 */       IoUtils.safeClose(ois);
/*      */     } 
/*      */     
/* 1236 */     return obj;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void encodeToFile(byte[] dataToEncode, String filename) throws IOException {
/* 1255 */     if (dataToEncode == null) {
/* 1256 */       throw new NullPointerException("Data to encode was null.");
/*      */     }
/*      */     
/* 1259 */     OutputStream bos = null;
/*      */     try {
/* 1261 */       bos = new OutputStream(new FileOutputStream(filename), 1);
/* 1262 */       bos.write(dataToEncode);
/*      */     }
/* 1264 */     catch (IOException e) {
/* 1265 */       throw e;
/*      */     } finally {
/*      */       
/* 1268 */       IoUtils.safeClose(bos);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void decodeToFile(String dataToDecode, String filename) throws IOException {
/* 1288 */     OutputStream bos = null;
/*      */     try {
/* 1290 */       bos = new OutputStream(new FileOutputStream(filename), 0);
/* 1291 */       bos.write(dataToDecode.getBytes(StandardCharsets.US_ASCII));
/*      */     }
/* 1293 */     catch (IOException e) {
/* 1294 */       throw e;
/*      */     } finally {
/*      */       
/* 1297 */       IoUtils.safeClose(bos);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static byte[] decodeFromFile(String filename) throws IOException {
/* 1317 */     byte[] decodedData = null;
/* 1318 */     InputStream bis = null;
/*      */     
/*      */     try {
/* 1321 */       Path file = Paths.get(filename, new String[0]);
/*      */       
/* 1323 */       int length = 0;
/*      */ 
/*      */ 
/*      */       
/* 1327 */       if (Files.size(file) > 2147483647L) {
/* 1328 */         throw new IOException("File is too big for this convenience method (" + Files.size(file) + " bytes).");
/*      */       }
/* 1330 */       byte[] buffer = new byte[(int)Files.size(file)];
/*      */ 
/*      */       
/* 1333 */       bis = new InputStream(new BufferedInputStream(Files.newInputStream(file, new java.nio.file.OpenOption[0])), 0);
/*      */       
/*      */       int numBytes;
/* 1336 */       while ((numBytes = bis.read(buffer, length, 4096)) >= 0) {
/* 1337 */         length += numBytes;
/*      */       }
/*      */ 
/*      */       
/* 1341 */       decodedData = new byte[length];
/* 1342 */       System.arraycopy(buffer, 0, decodedData, 0, length);
/*      */     
/*      */     }
/* 1345 */     catch (IOException e) {
/* 1346 */       throw e;
/*      */     } finally {
/*      */       
/* 1349 */       IoUtils.safeClose(bis);
/*      */     } 
/*      */     
/* 1352 */     return decodedData;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String encodeFromFile(String filename) throws IOException {
/* 1370 */     String encodedData = null;
/* 1371 */     InputStream bis = null;
/*      */     
/*      */     try {
/* 1374 */       Path file = Paths.get(filename, new String[0]);
/* 1375 */       byte[] buffer = new byte[Math.max((int)(Files.size(file) * 1.4D + 1.0D), 40)];
/*      */ 
/*      */       
/* 1378 */       int length = 0;
/*      */ 
/*      */ 
/*      */       
/* 1382 */       bis = new InputStream(new BufferedInputStream(Files.newInputStream(file, new java.nio.file.OpenOption[0])), 1);
/*      */       
/*      */       int numBytes;
/* 1385 */       while ((numBytes = bis.read(buffer, length, 4096)) >= 0) {
/* 1386 */         length += numBytes;
/*      */       }
/*      */ 
/*      */       
/* 1390 */       encodedData = new String(buffer, 0, length, StandardCharsets.US_ASCII);
/*      */     
/*      */     }
/* 1393 */     catch (IOException e) {
/* 1394 */       throw e;
/*      */     } finally {
/*      */       
/* 1397 */       IoUtils.safeClose(bis);
/*      */     } 
/*      */     
/* 1400 */     return encodedData;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void encodeFileToFile(String infile, String outfile) throws IOException {
/* 1413 */     String encoded = encodeFromFile(infile);
/* 1414 */     java.io.OutputStream out = null;
/*      */     try {
/* 1416 */       out = new BufferedOutputStream(new FileOutputStream(outfile));
/* 1417 */       out.write(encoded.getBytes(StandardCharsets.US_ASCII));
/*      */     }
/* 1419 */     catch (IOException e) {
/* 1420 */       throw e;
/*      */     } finally {
/*      */       
/* 1423 */       IoUtils.safeClose(out);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void decodeFileToFile(String infile, String outfile) throws IOException {
/* 1437 */     byte[] decoded = decodeFromFile(infile);
/* 1438 */     java.io.OutputStream out = null;
/*      */     try {
/* 1440 */       out = new BufferedOutputStream(new FileOutputStream(outfile));
/* 1441 */       out.write(decoded);
/*      */     }
/* 1443 */     catch (IOException e) {
/* 1444 */       throw e;
/*      */     } finally {
/*      */       
/* 1447 */       IoUtils.safeClose(out);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public static class InputStream
/*      */     extends FilterInputStream
/*      */   {
/*      */     private boolean encode;
/*      */ 
/*      */     
/*      */     private int position;
/*      */ 
/*      */     
/*      */     private byte[] buffer;
/*      */ 
/*      */     
/*      */     private int bufferLength;
/*      */     
/*      */     private int numSigBytes;
/*      */     
/*      */     private int lineLength;
/*      */     
/*      */     private boolean breakLines;
/*      */     
/*      */     private int options;
/*      */     
/*      */     private byte[] decodabet;
/*      */ 
/*      */     
/*      */     InputStream(java.io.InputStream in) {
/* 1479 */       this(in, 0);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     InputStream(java.io.InputStream in, int options) {
/* 1505 */       super(in);
/* 1506 */       this.options = options;
/* 1507 */       this.breakLines = ((options & 0x8) > 0);
/* 1508 */       this.encode = ((options & 0x1) > 0);
/* 1509 */       this.bufferLength = this.encode ? 4 : 3;
/* 1510 */       this.buffer = new byte[this.bufferLength];
/* 1511 */       this.position = -1;
/* 1512 */       this.lineLength = 0;
/* 1513 */       this.decodabet = Base64.getDecodabet(options);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public int read() throws IOException {
/* 1526 */       if (this.position < 0) {
/* 1527 */         if (this.encode) {
/* 1528 */           byte[] b3 = new byte[3];
/* 1529 */           int numBinaryBytes = 0;
/* 1530 */           for (int i = 0; i < 3; ) {
/* 1531 */             int b = this.in.read();
/*      */ 
/*      */             
/* 1534 */             if (b >= 0) {
/* 1535 */               b3[i] = (byte)b;
/* 1536 */               numBinaryBytes++;
/*      */ 
/*      */               
/*      */               i++;
/*      */             } 
/*      */           } 
/*      */           
/* 1543 */           if (numBinaryBytes > 0) {
/* 1544 */             Base64.encode3to4(b3, 0, numBinaryBytes, this.buffer, 0, this.options);
/* 1545 */             this.position = 0;
/* 1546 */             this.numSigBytes = 4;
/*      */           } else {
/*      */             
/* 1549 */             return -1;
/*      */           }
/*      */         
/*      */         }
/*      */         else {
/*      */           
/* 1555 */           byte[] b4 = new byte[4];
/* 1556 */           int i = 0;
/* 1557 */           for (i = 0; i < 4; i++) {
/*      */             
/* 1559 */             int b = 0;
/*      */             do {
/* 1561 */               b = this.in.read();
/* 1562 */             } while (b >= 0 && this.decodabet[b & 0x7F] <= -5);
/*      */             
/* 1564 */             if (b < 0) {
/*      */               break;
/*      */             }
/*      */             
/* 1568 */             b4[i] = (byte)b;
/*      */           } 
/*      */           
/* 1571 */           if (i == 4) {
/* 1572 */             this.numSigBytes = Base64.decode4to3(b4, 0, this.buffer, 0, this.options);
/* 1573 */             this.position = 0;
/*      */           } else {
/* 1575 */             if (i == 0) {
/* 1576 */               return -1;
/*      */             }
/*      */ 
/*      */             
/* 1580 */             throw new IOException("Improperly padded Base64 input.");
/*      */           } 
/*      */         } 
/*      */       }
/*      */ 
/*      */ 
/*      */       
/* 1587 */       if (this.position >= 0) {
/*      */         
/* 1589 */         if (this.position >= this.numSigBytes) {
/* 1590 */           return -1;
/*      */         }
/*      */         
/* 1593 */         if (this.encode && this.breakLines && this.lineLength >= 76) {
/* 1594 */           this.lineLength = 0;
/* 1595 */           return 10;
/*      */         } 
/*      */         
/* 1598 */         this.lineLength++;
/*      */ 
/*      */ 
/*      */         
/* 1602 */         int b = this.buffer[this.position++];
/*      */         
/* 1604 */         if (this.position >= this.bufferLength) {
/* 1605 */           this.position = -1;
/*      */         }
/*      */         
/* 1608 */         return b & 0xFF;
/*      */       } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 1615 */       throw new IOException("Error in Base64 code reading stream.");
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public int read(byte[] dest, int off, int len) throws IOException {
/*      */       int i;
/* 1633 */       for (i = 0; i < len; i++) {
/* 1634 */         int b = read();
/*      */         
/* 1636 */         if (b >= 0)
/* 1637 */         { dest[off + i] = (byte)b; }
/* 1638 */         else { if (i == 0) {
/* 1639 */             return -1;
/*      */           }
/*      */           break; }
/*      */       
/*      */       } 
/* 1644 */       return i;
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public static class OutputStream
/*      */     extends FilterOutputStream
/*      */   {
/*      */     private boolean encode;
/*      */ 
/*      */     
/*      */     private int position;
/*      */ 
/*      */     
/*      */     private byte[] buffer;
/*      */ 
/*      */     
/*      */     private int bufferLength;
/*      */     
/*      */     private int lineLength;
/*      */     
/*      */     private boolean breakLines;
/*      */     
/*      */     private byte[] b4;
/*      */     
/*      */     private boolean suspendEncoding;
/*      */     
/*      */     private int options;
/*      */     
/*      */     private byte[] decodabet;
/*      */ 
/*      */     
/*      */     OutputStream(java.io.OutputStream out) {
/* 1678 */       this(out, 1);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     OutputStream(java.io.OutputStream out, int options) {
/* 1702 */       super(out);
/* 1703 */       this.breakLines = ((options & 0x8) != 0);
/* 1704 */       this.encode = ((options & 0x1) != 0);
/* 1705 */       this.bufferLength = this.encode ? 3 : 4;
/* 1706 */       this.buffer = new byte[this.bufferLength];
/* 1707 */       this.position = 0;
/* 1708 */       this.lineLength = 0;
/* 1709 */       this.suspendEncoding = false;
/* 1710 */       this.b4 = new byte[4];
/* 1711 */       this.options = options;
/* 1712 */       this.decodabet = Base64.getDecodabet(options);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public void write(int theByte) throws IOException {
/* 1726 */       if (this.suspendEncoding) {
/* 1727 */         this.out.write(theByte);
/*      */         
/*      */         return;
/*      */       } 
/*      */       
/* 1732 */       if (this.encode) {
/* 1733 */         this.buffer[this.position++] = (byte)theByte;
/* 1734 */         if (this.position >= this.bufferLength)
/*      */         {
/* 1736 */           this.out.write(Base64.encode3to4(this.b4, this.buffer, this.bufferLength, this.options));
/*      */           
/* 1738 */           this.lineLength += 4;
/* 1739 */           if (this.breakLines && this.lineLength >= 76) {
/* 1740 */             this.out.write(10);
/* 1741 */             this.lineLength = 0;
/*      */           } 
/*      */           
/* 1744 */           this.position = 0;
/*      */ 
/*      */         
/*      */         }
/*      */ 
/*      */       
/*      */       }
/* 1751 */       else if (this.decodabet[theByte & 0x7F] > -5) {
/* 1752 */         this.buffer[this.position++] = (byte)theByte;
/* 1753 */         if (this.position >= this.bufferLength)
/*      */         {
/* 1755 */           int len = Base64.decode4to3(this.buffer, 0, this.b4, 0, this.options);
/* 1756 */           this.out.write(this.b4, 0, len);
/* 1757 */           this.position = 0;
/*      */         }
/*      */       
/* 1760 */       } else if (this.decodabet[theByte & 0x7F] != -5) {
/* 1761 */         throw new IOException("Invalid character in Base64 data.");
/*      */       } 
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public void write(byte[] theBytes, int off, int len) throws IOException {
/* 1777 */       if (this.suspendEncoding) {
/* 1778 */         this.out.write(theBytes, off, len);
/*      */         
/*      */         return;
/*      */       } 
/* 1782 */       for (int i = 0; i < len; i++) {
/* 1783 */         write(theBytes[off + i]);
/*      */       }
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public void flushBase64() throws IOException {
/* 1794 */       if (this.position > 0) {
/* 1795 */         if (this.encode) {
/* 1796 */           this.out.write(Base64.encode3to4(this.b4, this.buffer, this.position, this.options));
/* 1797 */           this.position = 0;
/*      */         } else {
/*      */           
/* 1800 */           throw new IOException("Base64 input not properly padded.");
/*      */         } 
/*      */       }
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public void close() throws IOException {
/* 1814 */       flushBase64();
/*      */ 
/*      */ 
/*      */       
/* 1818 */       super.close();
/*      */       
/* 1820 */       this.buffer = null;
/* 1821 */       this.out = null;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public void suspendEncoding() throws IOException {
/* 1831 */       flushBase64();
/* 1832 */       this.suspendEncoding = true;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public void resumeEncoding() {
/* 1841 */       this.suspendEncoding = false;
/*      */     }
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\websockets\core\protocol\version07\Base64.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */