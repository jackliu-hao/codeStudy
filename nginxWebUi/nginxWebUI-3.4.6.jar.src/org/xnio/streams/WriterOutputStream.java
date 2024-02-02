/*     */ package org.xnio.streams;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.io.Writer;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.CharBuffer;
/*     */ import java.nio.charset.Charset;
/*     */ import java.nio.charset.CharsetDecoder;
/*     */ import java.nio.charset.CoderResult;
/*     */ import java.nio.charset.CodingErrorAction;
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
/*     */ public final class WriterOutputStream
/*     */   extends OutputStream
/*     */ {
/*     */   private final Writer writer;
/*     */   private final CharsetDecoder decoder;
/*     */   private final ByteBuffer byteBuffer;
/*     */   private final char[] chars;
/*     */   private volatile boolean closed;
/*     */   
/*     */   public WriterOutputStream(Writer writer) {
/*  51 */     this(writer, Charset.defaultCharset());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public WriterOutputStream(Writer writer, CharsetDecoder decoder) {
/*  61 */     this(writer, decoder, 1024);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public WriterOutputStream(Writer writer, CharsetDecoder decoder, int bufferSize) {
/*  72 */     if (writer == null) {
/*  73 */       throw Messages.msg.nullParameter("writer");
/*     */     }
/*  75 */     if (decoder == null) {
/*  76 */       throw Messages.msg.nullParameter("decoder");
/*     */     }
/*  78 */     if (bufferSize < 1) {
/*  79 */       throw Messages.msg.parameterOutOfRange("bufferSize");
/*     */     }
/*  81 */     this.writer = writer;
/*  82 */     this.decoder = decoder;
/*  83 */     this.byteBuffer = ByteBuffer.allocate(bufferSize);
/*  84 */     this.chars = new char[(int)(bufferSize * decoder.maxCharsPerByte() + 0.5F)];
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public WriterOutputStream(Writer writer, Charset charset) {
/*  94 */     this(writer, getDecoder(charset));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public WriterOutputStream(Writer writer, String charsetName) throws UnsupportedEncodingException {
/* 105 */     this(writer, Streams.getCharset(charsetName));
/*     */   }
/*     */   
/*     */   private static CharsetDecoder getDecoder(Charset charset) {
/* 109 */     CharsetDecoder decoder = charset.newDecoder();
/* 110 */     decoder.onMalformedInput(CodingErrorAction.REPLACE);
/* 111 */     decoder.onUnmappableCharacter(CodingErrorAction.REPLACE);
/* 112 */     decoder.replaceWith("?");
/* 113 */     return decoder;
/*     */   }
/*     */ 
/*     */   
/*     */   public void write(int b) throws IOException {
/* 118 */     if (this.closed) throw Messages.msg.streamClosed(); 
/* 119 */     ByteBuffer byteBuffer = this.byteBuffer;
/* 120 */     if (!byteBuffer.hasRemaining()) {
/* 121 */       doFlush(false);
/*     */     }
/* 123 */     byteBuffer.put((byte)b);
/*     */   }
/*     */ 
/*     */   
/*     */   public void write(byte[] b, int off, int len) throws IOException {
/* 128 */     if (this.closed) throw Messages.msg.streamClosed(); 
/* 129 */     ByteBuffer byteBuffer = this.byteBuffer;
/*     */     
/* 131 */     while (len > 0) {
/* 132 */       int r = byteBuffer.remaining();
/* 133 */       if (r == 0) {
/* 134 */         doFlush(false);
/*     */         continue;
/*     */       } 
/* 137 */       int c = Math.min(len, r);
/* 138 */       byteBuffer.put(b, off, c);
/* 139 */       len -= c;
/* 140 */       off += c;
/*     */     } 
/*     */   }
/*     */   
/*     */   private void doFlush(boolean eof) throws IOException {
/* 145 */     CharBuffer charBuffer = CharBuffer.wrap(this.chars);
/* 146 */     ByteBuffer byteBuffer = this.byteBuffer;
/* 147 */     CharsetDecoder decoder = this.decoder;
/* 148 */     byteBuffer.flip();
/*     */     try {
/* 150 */       while (byteBuffer.hasRemaining()) {
/* 151 */         CoderResult result = decoder.decode(byteBuffer, charBuffer, eof);
/* 152 */         if (result.isOverflow()) {
/* 153 */           this.writer.write(this.chars, 0, charBuffer.position());
/* 154 */           charBuffer.clear();
/*     */           continue;
/*     */         } 
/* 157 */         if (result.isUnderflow()) {
/* 158 */           int p = charBuffer.position();
/* 159 */           if (p > 0) {
/* 160 */             this.writer.write(this.chars, 0, p);
/*     */           }
/*     */           return;
/*     */         } 
/* 164 */         if (result.isError()) {
/* 165 */           if (result.isMalformed()) {
/* 166 */             throw Messages.msg.malformedInput();
/*     */           }
/* 168 */           if (result.isUnmappable()) {
/* 169 */             throw Messages.msg.unmappableCharacter();
/*     */           }
/* 171 */           throw Messages.msg.characterDecodingProblem();
/*     */         } 
/*     */       } 
/*     */     } finally {
/* 175 */       byteBuffer.compact();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void flush() throws IOException {
/* 181 */     if (this.closed) throw Messages.msg.streamClosed(); 
/* 182 */     doFlush(false);
/* 183 */     this.writer.flush();
/*     */   }
/*     */ 
/*     */   
/*     */   public void close() throws IOException {
/* 188 */     this.closed = true;
/* 189 */     doFlush(true);
/* 190 */     this.byteBuffer.clear();
/* 191 */     this.writer.close();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/* 200 */     return "Output stream writing to " + this.writer;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\streams\WriterOutputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */