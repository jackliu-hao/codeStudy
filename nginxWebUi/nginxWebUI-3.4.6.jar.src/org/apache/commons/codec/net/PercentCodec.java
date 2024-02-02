/*     */ package org.apache.commons.codec.net;
/*     */ 
/*     */ import java.nio.ByteBuffer;
/*     */ import java.util.BitSet;
/*     */ import org.apache.commons.codec.BinaryDecoder;
/*     */ import org.apache.commons.codec.BinaryEncoder;
/*     */ import org.apache.commons.codec.DecoderException;
/*     */ import org.apache.commons.codec.EncoderException;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class PercentCodec
/*     */   implements BinaryEncoder, BinaryDecoder
/*     */ {
/*     */   private static final byte ESCAPE_CHAR = 37;
/*  50 */   private final BitSet alwaysEncodeChars = new BitSet();
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final boolean plusForSpace;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  60 */   private int alwaysEncodeCharsMin = Integer.MAX_VALUE, alwaysEncodeCharsMax = Integer.MIN_VALUE;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public PercentCodec() {
/*  68 */     this.plusForSpace = false;
/*  69 */     insertAlwaysEncodeChar((byte)37);
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
/*     */   public PercentCodec(byte[] alwaysEncodeChars, boolean plusForSpace) {
/*  81 */     this.plusForSpace = plusForSpace;
/*  82 */     insertAlwaysEncodeChars(alwaysEncodeChars);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void insertAlwaysEncodeChars(byte[] alwaysEncodeCharsArray) {
/*  91 */     if (alwaysEncodeCharsArray != null) {
/*  92 */       for (byte b : alwaysEncodeCharsArray) {
/*  93 */         insertAlwaysEncodeChar(b);
/*     */       }
/*     */     }
/*  96 */     insertAlwaysEncodeChar((byte)37);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void insertAlwaysEncodeChar(byte b) {
/* 106 */     this.alwaysEncodeChars.set(b);
/* 107 */     if (b < this.alwaysEncodeCharsMin) {
/* 108 */       this.alwaysEncodeCharsMin = b;
/*     */     }
/* 110 */     if (b > this.alwaysEncodeCharsMax) {
/* 111 */       this.alwaysEncodeCharsMax = b;
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public byte[] encode(byte[] bytes) throws EncoderException {
/* 121 */     if (bytes == null) {
/* 122 */       return null;
/*     */     }
/*     */     
/* 125 */     int expectedEncodingBytes = expectedEncodingBytes(bytes);
/* 126 */     boolean willEncode = (expectedEncodingBytes != bytes.length);
/* 127 */     if (willEncode || (this.plusForSpace && containsSpace(bytes))) {
/* 128 */       return doEncode(bytes, expectedEncodingBytes, willEncode);
/*     */     }
/* 130 */     return bytes;
/*     */   }
/*     */   
/*     */   private byte[] doEncode(byte[] bytes, int expectedLength, boolean willEncode) {
/* 134 */     ByteBuffer buffer = ByteBuffer.allocate(expectedLength);
/* 135 */     for (byte b : bytes) {
/* 136 */       if (willEncode && canEncode(b)) {
/* 137 */         byte bb = b;
/* 138 */         if (bb < 0) {
/* 139 */           bb = (byte)(256 + bb);
/*     */         }
/* 141 */         char hex1 = Utils.hexDigit(bb >> 4);
/* 142 */         char hex2 = Utils.hexDigit(bb);
/* 143 */         buffer.put((byte)37);
/* 144 */         buffer.put((byte)hex1);
/* 145 */         buffer.put((byte)hex2);
/*     */       }
/* 147 */       else if (this.plusForSpace && b == 32) {
/* 148 */         buffer.put((byte)43);
/*     */       } else {
/* 150 */         buffer.put(b);
/*     */       } 
/*     */     } 
/*     */     
/* 154 */     return buffer.array();
/*     */   }
/*     */   
/*     */   private int expectedEncodingBytes(byte[] bytes) {
/* 158 */     int byteCount = 0;
/* 159 */     for (byte b : bytes) {
/* 160 */       byteCount += canEncode(b) ? 3 : 1;
/*     */     }
/* 162 */     return byteCount;
/*     */   }
/*     */   
/*     */   private boolean containsSpace(byte[] bytes) {
/* 166 */     for (byte b : bytes) {
/* 167 */       if (b == 32) {
/* 168 */         return true;
/*     */       }
/*     */     } 
/* 171 */     return false;
/*     */   }
/*     */   
/*     */   private boolean canEncode(byte c) {
/* 175 */     return (!isAsciiChar(c) || (inAlwaysEncodeCharsRange(c) && this.alwaysEncodeChars.get(c)));
/*     */   }
/*     */   
/*     */   private boolean inAlwaysEncodeCharsRange(byte c) {
/* 179 */     return (c >= this.alwaysEncodeCharsMin && c <= this.alwaysEncodeCharsMax);
/*     */   }
/*     */   
/*     */   private boolean isAsciiChar(byte c) {
/* 183 */     return (c >= 0);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public byte[] decode(byte[] bytes) throws DecoderException {
/* 192 */     if (bytes == null) {
/* 193 */       return null;
/*     */     }
/*     */     
/* 196 */     ByteBuffer buffer = ByteBuffer.allocate(expectedDecodingBytes(bytes));
/* 197 */     for (int i = 0; i < bytes.length; i++) {
/* 198 */       byte b = bytes[i];
/* 199 */       if (b == 37) {
/*     */         try {
/* 201 */           int u = Utils.digit16(bytes[++i]);
/* 202 */           int l = Utils.digit16(bytes[++i]);
/* 203 */           buffer.put((byte)((u << 4) + l));
/* 204 */         } catch (ArrayIndexOutOfBoundsException e) {
/* 205 */           throw new DecoderException("Invalid percent decoding: ", e);
/*     */         }
/*     */       
/* 208 */       } else if (this.plusForSpace && b == 43) {
/* 209 */         buffer.put((byte)32);
/*     */       } else {
/* 211 */         buffer.put(b);
/*     */       } 
/*     */     } 
/*     */     
/* 215 */     return buffer.array();
/*     */   }
/*     */   
/*     */   private int expectedDecodingBytes(byte[] bytes) {
/* 219 */     int byteCount = 0;
/* 220 */     for (int i = 0; i < bytes.length; ) {
/* 221 */       byte b = bytes[i];
/* 222 */       i += (b == 37) ? 3 : 1;
/* 223 */       byteCount++;
/*     */     } 
/* 225 */     return byteCount;
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
/*     */   public Object encode(Object obj) throws EncoderException {
/* 237 */     if (obj == null)
/* 238 */       return null; 
/* 239 */     if (obj instanceof byte[]) {
/* 240 */       return encode((byte[])obj);
/*     */     }
/* 242 */     throw new EncoderException("Objects of type " + obj.getClass().getName() + " cannot be Percent encoded");
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
/*     */   public Object decode(Object obj) throws DecoderException {
/* 255 */     if (obj == null)
/* 256 */       return null; 
/* 257 */     if (obj instanceof byte[]) {
/* 258 */       return decode((byte[])obj);
/*     */     }
/* 260 */     throw new DecoderException("Objects of type " + obj.getClass().getName() + " cannot be Percent decoded");
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\codec\net\PercentCodec.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */