/*     */ package org.apache.commons.compress.archivers.zip;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.CharBuffer;
/*     */ import java.nio.charset.Charset;
/*     */ import java.nio.charset.CharsetDecoder;
/*     */ import java.nio.charset.CharsetEncoder;
/*     */ import java.nio.charset.CoderResult;
/*     */ import java.nio.charset.CodingErrorAction;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ class NioZipEncoding
/*     */   implements ZipEncoding, CharsetAccessor
/*     */ {
/*     */   private final Charset charset;
/*     */   private final boolean useReplacement;
/*     */   private static final char REPLACEMENT = '?';
/*  42 */   private static final byte[] REPLACEMENT_BYTES = new byte[] { 63 };
/*  43 */   private static final String REPLACEMENT_STRING = String.valueOf('?');
/*  44 */   private static final char[] HEX_CHARS = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   NioZipEncoding(Charset charset, boolean useReplacement) {
/*  55 */     this.charset = charset;
/*  56 */     this.useReplacement = useReplacement;
/*     */   }
/*     */ 
/*     */   
/*     */   public Charset getCharset() {
/*  61 */     return this.charset;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean canEncode(String name) {
/*  69 */     CharsetEncoder enc = newEncoder();
/*     */     
/*  71 */     return enc.canEncode(name);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ByteBuffer encode(String name) {
/*  79 */     CharsetEncoder enc = newEncoder();
/*     */     
/*  81 */     CharBuffer cb = CharBuffer.wrap(name);
/*  82 */     CharBuffer tmp = null;
/*  83 */     ByteBuffer out = ByteBuffer.allocate(estimateInitialBufferSize(enc, cb.remaining()));
/*     */     
/*  85 */     while (cb.hasRemaining()) {
/*  86 */       CoderResult res = enc.encode(cb, out, false);
/*     */       
/*  88 */       if (res.isUnmappable() || res.isMalformed()) {
/*     */ 
/*     */ 
/*     */ 
/*     */         
/*  93 */         int spaceForSurrogate = estimateIncrementalEncodingSize(enc, 6 * res.length());
/*  94 */         if (spaceForSurrogate > out.remaining()) {
/*     */ 
/*     */ 
/*     */           
/*  98 */           int charCount = 0;
/*  99 */           for (int j = cb.position(); j < cb.limit(); j++) {
/* 100 */             charCount += !enc.canEncode(cb.get(j)) ? 6 : 1;
/*     */           }
/* 102 */           int totalExtraSpace = estimateIncrementalEncodingSize(enc, charCount);
/* 103 */           out = ZipEncodingHelper.growBufferBy(out, totalExtraSpace - out.remaining());
/*     */         } 
/* 105 */         if (tmp == null) {
/* 106 */           tmp = CharBuffer.allocate(6);
/*     */         }
/* 108 */         for (int i = 0; i < res.length(); i++)
/* 109 */           out = encodeFully(enc, encodeSurrogate(tmp, cb.get()), out); 
/*     */         continue;
/*     */       } 
/* 112 */       if (res.isOverflow()) {
/* 113 */         int increment = estimateIncrementalEncodingSize(enc, cb.remaining());
/* 114 */         out = ZipEncodingHelper.growBufferBy(out, increment); continue;
/*     */       } 
/* 116 */       if (res.isUnderflow() || res.isError()) {
/*     */         break;
/*     */       }
/*     */     } 
/*     */     
/* 121 */     enc.encode(cb, out, true);
/*     */ 
/*     */     
/* 124 */     out.limit(out.position());
/* 125 */     out.rewind();
/* 126 */     return out;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String decode(byte[] data) throws IOException {
/* 135 */     return newDecoder()
/* 136 */       .decode(ByteBuffer.wrap(data)).toString();
/*     */   }
/*     */   
/*     */   private static ByteBuffer encodeFully(CharsetEncoder enc, CharBuffer cb, ByteBuffer out) {
/* 140 */     ByteBuffer o = out;
/* 141 */     while (cb.hasRemaining()) {
/* 142 */       CoderResult result = enc.encode(cb, o, false);
/* 143 */       if (result.isOverflow()) {
/* 144 */         int increment = estimateIncrementalEncodingSize(enc, cb.remaining());
/* 145 */         o = ZipEncodingHelper.growBufferBy(o, increment);
/*     */       } 
/*     */     } 
/* 148 */     return o;
/*     */   }
/*     */   
/*     */   private static CharBuffer encodeSurrogate(CharBuffer cb, char c) {
/* 152 */     cb.position(0).limit(6);
/* 153 */     cb.put('%');
/* 154 */     cb.put('U');
/*     */     
/* 156 */     cb.put(HEX_CHARS[c >> 12 & 0xF]);
/* 157 */     cb.put(HEX_CHARS[c >> 8 & 0xF]);
/* 158 */     cb.put(HEX_CHARS[c >> 4 & 0xF]);
/* 159 */     cb.put(HEX_CHARS[c & 0xF]);
/* 160 */     cb.flip();
/* 161 */     return cb;
/*     */   }
/*     */   
/*     */   private CharsetEncoder newEncoder() {
/* 165 */     if (this.useReplacement) {
/* 166 */       return this.charset.newEncoder()
/* 167 */         .onMalformedInput(CodingErrorAction.REPLACE)
/* 168 */         .onUnmappableCharacter(CodingErrorAction.REPLACE)
/* 169 */         .replaceWith(REPLACEMENT_BYTES);
/*     */     }
/* 171 */     return this.charset.newEncoder()
/* 172 */       .onMalformedInput(CodingErrorAction.REPORT)
/* 173 */       .onUnmappableCharacter(CodingErrorAction.REPORT);
/*     */   }
/*     */   
/*     */   private CharsetDecoder newDecoder() {
/* 177 */     if (!this.useReplacement) {
/* 178 */       return this.charset.newDecoder()
/* 179 */         .onMalformedInput(CodingErrorAction.REPORT)
/* 180 */         .onUnmappableCharacter(CodingErrorAction.REPORT);
/*     */     }
/* 182 */     return this.charset.newDecoder()
/* 183 */       .onMalformedInput(CodingErrorAction.REPLACE)
/* 184 */       .onUnmappableCharacter(CodingErrorAction.REPLACE)
/* 185 */       .replaceWith(REPLACEMENT_STRING);
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
/*     */   private static int estimateInitialBufferSize(CharsetEncoder enc, int charChount) {
/* 201 */     float first = enc.maxBytesPerChar();
/* 202 */     float rest = (charChount - 1) * enc.averageBytesPerChar();
/* 203 */     return (int)Math.ceil((first + rest));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static int estimateIncrementalEncodingSize(CharsetEncoder enc, int charCount) {
/* 214 */     return (int)Math.ceil((charCount * enc.averageBytesPerChar()));
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\archivers\zip\NioZipEncoding.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */