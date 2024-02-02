/*     */ package com.google.protobuf;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.InvalidObjectException;
/*     */ import java.io.ObjectInputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.ByteOrder;
/*     */ import java.nio.InvalidMarkException;
/*     */ import java.nio.charset.Charset;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ final class NioByteString
/*     */   extends ByteString.LeafByteString
/*     */ {
/*     */   private final ByteBuffer buffer;
/*     */   
/*     */   NioByteString(ByteBuffer buffer) {
/*  52 */     Internal.checkNotNull(buffer, "buffer");
/*     */ 
/*     */     
/*  55 */     this.buffer = buffer.slice().order(ByteOrder.nativeOrder());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private Object writeReplace() {
/*  63 */     return ByteString.copyFrom(this.buffer.slice());
/*     */   }
/*     */ 
/*     */   
/*     */   private void readObject(ObjectInputStream in) throws IOException {
/*  68 */     throw new InvalidObjectException("NioByteString instances are not to be serialized directly");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public byte byteAt(int index) {
/*     */     try {
/*  76 */       return this.buffer.get(index);
/*  77 */     } catch (ArrayIndexOutOfBoundsException e) {
/*  78 */       throw e;
/*  79 */     } catch (IndexOutOfBoundsException e) {
/*  80 */       throw new ArrayIndexOutOfBoundsException(e.getMessage());
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public byte internalByteAt(int index) {
/*  88 */     return byteAt(index);
/*     */   }
/*     */ 
/*     */   
/*     */   public int size() {
/*  93 */     return this.buffer.remaining();
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteString substring(int beginIndex, int endIndex) {
/*     */     try {
/*  99 */       ByteBuffer slice = slice(beginIndex, endIndex);
/* 100 */       return new NioByteString(slice);
/* 101 */     } catch (ArrayIndexOutOfBoundsException e) {
/* 102 */       throw e;
/* 103 */     } catch (IndexOutOfBoundsException e) {
/* 104 */       throw new ArrayIndexOutOfBoundsException(e.getMessage());
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected void copyToInternal(byte[] target, int sourceOffset, int targetOffset, int numberToCopy) {
/* 111 */     ByteBuffer slice = this.buffer.slice();
/* 112 */     slice.position(sourceOffset);
/* 113 */     slice.get(target, targetOffset, numberToCopy);
/*     */   }
/*     */ 
/*     */   
/*     */   public void copyTo(ByteBuffer target) {
/* 118 */     target.put(this.buffer.slice());
/*     */   }
/*     */ 
/*     */   
/*     */   public void writeTo(OutputStream out) throws IOException {
/* 123 */     out.write(toByteArray());
/*     */   }
/*     */ 
/*     */   
/*     */   boolean equalsRange(ByteString other, int offset, int length) {
/* 128 */     return substring(0, length).equals(other.substring(offset, offset + length));
/*     */   }
/*     */ 
/*     */   
/*     */   void writeToInternal(OutputStream out, int sourceOffset, int numberToWrite) throws IOException {
/* 133 */     if (this.buffer.hasArray()) {
/*     */ 
/*     */       
/* 136 */       int bufferOffset = this.buffer.arrayOffset() + this.buffer.position() + sourceOffset;
/* 137 */       out.write(this.buffer.array(), bufferOffset, numberToWrite);
/*     */       
/*     */       return;
/*     */     } 
/* 141 */     ByteBufferWriter.write(slice(sourceOffset, sourceOffset + numberToWrite), out);
/*     */   }
/*     */ 
/*     */   
/*     */   void writeTo(ByteOutput output) throws IOException {
/* 146 */     output.writeLazy(this.buffer.slice());
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuffer asReadOnlyByteBuffer() {
/* 151 */     return this.buffer.asReadOnlyBuffer();
/*     */   }
/*     */ 
/*     */   
/*     */   public List<ByteBuffer> asReadOnlyByteBufferList() {
/* 156 */     return Collections.singletonList(asReadOnlyByteBuffer());
/*     */   }
/*     */ 
/*     */   
/*     */   protected String toStringInternal(Charset charset) {
/*     */     byte[] bytes;
/*     */     int offset;
/*     */     int length;
/* 164 */     if (this.buffer.hasArray()) {
/* 165 */       bytes = this.buffer.array();
/* 166 */       offset = this.buffer.arrayOffset() + this.buffer.position();
/* 167 */       length = this.buffer.remaining();
/*     */     } else {
/*     */       
/* 170 */       bytes = toByteArray();
/* 171 */       offset = 0;
/* 172 */       length = bytes.length;
/*     */     } 
/* 174 */     return new String(bytes, offset, length, charset);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isValidUtf8() {
/* 179 */     return Utf8.isValidUtf8(this.buffer);
/*     */   }
/*     */ 
/*     */   
/*     */   protected int partialIsValidUtf8(int state, int offset, int length) {
/* 184 */     return Utf8.partialIsValidUtf8(state, this.buffer, offset, offset + length);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object other) {
/* 189 */     if (other == this) {
/* 190 */       return true;
/*     */     }
/* 192 */     if (!(other instanceof ByteString)) {
/* 193 */       return false;
/*     */     }
/* 195 */     ByteString otherString = (ByteString)other;
/* 196 */     if (size() != otherString.size()) {
/* 197 */       return false;
/*     */     }
/* 199 */     if (size() == 0) {
/* 200 */       return true;
/*     */     }
/* 202 */     if (other instanceof NioByteString) {
/* 203 */       return this.buffer.equals(((NioByteString)other).buffer);
/*     */     }
/* 205 */     if (other instanceof RopeByteString) {
/* 206 */       return other.equals(this);
/*     */     }
/* 208 */     return this.buffer.equals(otherString.asReadOnlyByteBuffer());
/*     */   }
/*     */ 
/*     */   
/*     */   protected int partialHash(int h, int offset, int length) {
/* 213 */     for (int i = offset; i < offset + length; i++) {
/* 214 */       h = h * 31 + this.buffer.get(i);
/*     */     }
/* 216 */     return h;
/*     */   }
/*     */ 
/*     */   
/*     */   public InputStream newInput() {
/* 221 */     return new InputStream() {
/* 222 */         private final ByteBuffer buf = NioByteString.this.buffer.slice();
/*     */ 
/*     */         
/*     */         public void mark(int readlimit) {
/* 226 */           this.buf.mark();
/*     */         }
/*     */ 
/*     */         
/*     */         public boolean markSupported() {
/* 231 */           return true;
/*     */         }
/*     */ 
/*     */         
/*     */         public void reset() throws IOException {
/*     */           try {
/* 237 */             this.buf.reset();
/* 238 */           } catch (InvalidMarkException e) {
/* 239 */             throw new IOException(e);
/*     */           } 
/*     */         }
/*     */ 
/*     */         
/*     */         public int available() throws IOException {
/* 245 */           return this.buf.remaining();
/*     */         }
/*     */ 
/*     */         
/*     */         public int read() throws IOException {
/* 250 */           if (!this.buf.hasRemaining()) {
/* 251 */             return -1;
/*     */           }
/* 253 */           return this.buf.get() & 0xFF;
/*     */         }
/*     */ 
/*     */         
/*     */         public int read(byte[] bytes, int off, int len) throws IOException {
/* 258 */           if (!this.buf.hasRemaining()) {
/* 259 */             return -1;
/*     */           }
/*     */           
/* 262 */           len = Math.min(len, this.buf.remaining());
/* 263 */           this.buf.get(bytes, off, len);
/* 264 */           return len;
/*     */         }
/*     */       };
/*     */   }
/*     */ 
/*     */   
/*     */   public CodedInputStream newCodedInput() {
/* 271 */     return CodedInputStream.newInstance(this.buffer, true);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private ByteBuffer slice(int beginIndex, int endIndex) {
/* 282 */     if (beginIndex < this.buffer.position() || endIndex > this.buffer.limit() || beginIndex > endIndex) {
/* 283 */       throw new IllegalArgumentException(
/* 284 */           String.format("Invalid indices [%d, %d]", new Object[] { Integer.valueOf(beginIndex), Integer.valueOf(endIndex) }));
/*     */     }
/*     */     
/* 287 */     ByteBuffer slice = this.buffer.slice();
/* 288 */     slice.position(beginIndex - this.buffer.position());
/* 289 */     slice.limit(endIndex - this.buffer.position());
/* 290 */     return slice;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\protobuf\NioByteString.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */