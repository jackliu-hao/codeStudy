/*     */ package org.xnio.streams;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.Reader;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.CharBuffer;
/*     */ import java.nio.charset.Charset;
/*     */ import java.nio.charset.CharsetEncoder;
/*     */ import java.nio.charset.CoderResult;
/*     */ import java.nio.charset.CodingErrorAction;
/*     */ import org.xnio.Buffers;
/*     */ import org.xnio._private.Messages;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class ReaderInputStream
/*     */   extends InputStream
/*     */ {
/*     */   private final Reader reader;
/*     */   private final CharsetEncoder encoder;
/*     */   private final CharBuffer charBuffer;
/*     */   private final ByteBuffer byteBuffer;
/*     */   
/*     */   public ReaderInputStream(Reader reader) {
/*  51 */     this(reader, Charset.defaultCharset());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ReaderInputStream(Reader reader, String charsetName) throws UnsupportedEncodingException {
/*  62 */     this(reader, Streams.getCharset(charsetName));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ReaderInputStream(Reader reader, Charset charset) {
/*  72 */     this(reader, getEncoder(charset));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ReaderInputStream(Reader reader, CharsetEncoder encoder) {
/*  82 */     this(reader, encoder, 1024);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ReaderInputStream(Reader reader, CharsetEncoder encoder, int bufferSize) {
/*  93 */     if (reader == null) {
/*  94 */       throw Messages.msg.nullParameter("writer");
/*     */     }
/*  96 */     if (encoder == null) {
/*  97 */       throw Messages.msg.nullParameter("decoder");
/*     */     }
/*  99 */     if (bufferSize < 1) {
/* 100 */       throw Messages.msg.parameterOutOfRange("bufferSize");
/*     */     }
/* 102 */     this.reader = reader;
/* 103 */     this.encoder = encoder;
/* 104 */     this.charBuffer = CharBuffer.wrap(new char[bufferSize]);
/* 105 */     this.byteBuffer = ByteBuffer.wrap(new byte[(int)(bufferSize * encoder.averageBytesPerChar() + 0.5F)]);
/* 106 */     this.charBuffer.flip();
/* 107 */     this.byteBuffer.flip();
/*     */   }
/*     */   
/*     */   private static CharsetEncoder getEncoder(Charset charset) {
/* 111 */     CharsetEncoder encoder = charset.newEncoder();
/* 112 */     encoder.onMalformedInput(CodingErrorAction.REPLACE);
/* 113 */     encoder.onUnmappableCharacter(CodingErrorAction.REPLACE);
/* 114 */     return encoder;
/*     */   }
/*     */ 
/*     */   
/*     */   public int read() throws IOException {
/* 119 */     ByteBuffer byteBuffer = this.byteBuffer;
/* 120 */     if (!byteBuffer.hasRemaining() && 
/* 121 */       !fill()) {
/* 122 */       return -1;
/*     */     }
/*     */     
/* 125 */     return byteBuffer.get() & 0xFF;
/*     */   }
/*     */ 
/*     */   
/*     */   public int read(byte[] b, int off, int len) throws IOException {
/* 130 */     ByteBuffer byteBuffer = this.byteBuffer;
/* 131 */     int cnt = 0;
/* 132 */     while (len > 0) {
/* 133 */       int r = byteBuffer.remaining();
/* 134 */       if (r == 0) {
/* 135 */         if (!fill()) return (cnt == 0) ? -1 : cnt; 
/*     */         continue;
/*     */       } 
/* 138 */       int c = Math.min(r, len);
/* 139 */       byteBuffer.get(b, off, c);
/* 140 */       cnt += c;
/* 141 */       off += c;
/* 142 */       len -= c;
/*     */     } 
/* 144 */     return cnt;
/*     */   }
/*     */   
/*     */   private boolean fill() throws IOException {
/* 148 */     CharBuffer charBuffer = this.charBuffer;
/* 149 */     ByteBuffer byteBuffer = this.byteBuffer;
/* 150 */     byteBuffer.compact();
/* 151 */     boolean filled = false;
/*     */     try {
/* 153 */       while (byteBuffer.hasRemaining()) {
/* 154 */         while (charBuffer.hasRemaining()) {
/* 155 */           CoderResult result = this.encoder.encode(charBuffer, byteBuffer, false);
/* 156 */           if (result.isOverflow()) {
/* 157 */             return true;
/*     */           }
/* 159 */           if (result.isUnderflow()) {
/* 160 */             filled = true;
/*     */             break;
/*     */           } 
/* 163 */           if (result.isError()) {
/* 164 */             if (result.isMalformed()) {
/* 165 */               throw Messages.msg.malformedInput();
/*     */             }
/* 167 */             if (result.isUnmappable()) {
/* 168 */               throw Messages.msg.unmappableCharacter();
/*     */             }
/* 170 */             throw Messages.msg.characterDecodingProblem();
/*     */           } 
/*     */         } 
/* 173 */         charBuffer.compact();
/*     */         try {
/* 175 */           int cnt = this.reader.read(charBuffer);
/* 176 */           if (cnt == -1)
/* 177 */             return filled; 
/* 178 */           if (cnt > 0) {
/* 179 */             filled = true;
/*     */           }
/*     */         } finally {
/* 182 */           charBuffer.flip();
/*     */         } 
/*     */       } 
/* 185 */       return true;
/*     */     } finally {
/* 187 */       byteBuffer.flip();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public long skip(long n) throws IOException {
/* 193 */     ByteBuffer byteBuffer = this.byteBuffer;
/* 194 */     int cnt = 0;
/* 195 */     while (n > 0L) {
/* 196 */       int r = byteBuffer.remaining();
/* 197 */       if (r == 0) {
/* 198 */         if (!fill()) return cnt; 
/*     */         continue;
/*     */       } 
/* 201 */       int c = Math.min(r, (n > 2147483647L) ? Integer.MAX_VALUE : (int)n);
/* 202 */       Buffers.skip(byteBuffer, c);
/* 203 */       cnt += c;
/* 204 */       n -= c;
/*     */     } 
/* 206 */     return cnt;
/*     */   }
/*     */ 
/*     */   
/*     */   public int available() throws IOException {
/* 211 */     return this.byteBuffer.remaining();
/*     */   }
/*     */ 
/*     */   
/*     */   public void close() throws IOException {
/* 216 */     this.byteBuffer.clear().flip();
/* 217 */     this.charBuffer.clear().flip();
/* 218 */     this.reader.close();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/* 227 */     return "ReaderInputStream over " + this.reader;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\streams\ReaderInputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */