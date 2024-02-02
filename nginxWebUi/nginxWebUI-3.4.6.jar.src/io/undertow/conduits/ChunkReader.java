/*     */ package io.undertow.conduits;
/*     */ 
/*     */ import io.undertow.UndertowMessages;
/*     */ import io.undertow.util.Attachable;
/*     */ import io.undertow.util.AttachmentKey;
/*     */ import io.undertow.util.HeaderMap;
/*     */ import io.undertow.util.HttpString;
/*     */ import java.io.IOException;
/*     */ import java.nio.ByteBuffer;
/*     */ import org.xnio.Bits;
/*     */ import org.xnio.conduits.Conduit;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ class ChunkReader<T extends Conduit>
/*     */ {
/*     */   private static final long FLAG_FINISHED = 4611686018427387904L;
/*     */   private static final long FLAG_READING_LENGTH = 2305843009213693952L;
/*     */   private static final long FLAG_READING_TILL_END_OF_LINE = 1152921504606846976L;
/*     */   private static final long FLAG_READING_NEWLINE = 576460752303423488L;
/*     */   private static final long FLAG_READING_AFTER_LAST = 288230376151711744L;
/*  48 */   private static final long MASK_COUNT = Bits.longBitMask(0, 56);
/*     */   
/*     */   private static final long LIMIT = 576460752303423487L;
/*     */   
/*     */   private long state;
/*     */   
/*     */   private final Attachable attachable;
/*     */   
/*     */   private final AttachmentKey<HeaderMap> trailerAttachmentKey;
/*     */   
/*     */   private TrailerParser trailerParser;
/*     */   
/*     */   private final T conduit;
/*     */ 
/*     */   
/*     */   ChunkReader(Attachable attachable, AttachmentKey<HeaderMap> trailerAttachmentKey, T conduit) {
/*  64 */     this.attachable = attachable;
/*  65 */     this.trailerAttachmentKey = trailerAttachmentKey;
/*  66 */     this.conduit = conduit;
/*  67 */     this.state = 2305843009213693952L;
/*     */   }
/*     */   
/*     */   public long readChunk(ByteBuffer buf) throws IOException {
/*  71 */     long oldVal = this.state;
/*  72 */     long chunkRemaining = this.state & MASK_COUNT;
/*     */     
/*  74 */     if (chunkRemaining > 0L && !Bits.anyAreSet(this.state, 4323455642275676160L)) {
/*  75 */       return chunkRemaining;
/*     */     }
/*  77 */     long newVal = oldVal & (MASK_COUNT ^ 0xFFFFFFFFFFFFFFFFL);
/*     */     try {
/*  79 */       if (Bits.anyAreSet(oldVal, 288230376151711744L)) {
/*  80 */         int ret = handleChunkedRequestEnd(buf);
/*  81 */         if (ret == -1) {
/*  82 */           newVal |= 0x4000000000000000L;
/*  83 */           return -1L;
/*     */         } 
/*  85 */         return 0L;
/*     */       } 
/*     */       
/*  88 */       while (Bits.anyAreSet(newVal, 576460752303423488L)) {
/*  89 */         while (buf.hasRemaining()) {
/*  90 */           byte b = buf.get();
/*  91 */           if (b == 10) {
/*  92 */             newVal = newVal & 0xF7FFFFFFFFFFFFFFL | 0x2000000000000000L;
/*     */             break;
/*     */           } 
/*     */         } 
/*  96 */         if (Bits.anyAreSet(newVal, 576460752303423488L)) {
/*  97 */           return 0L;
/*     */         }
/*     */       } 
/*     */       
/* 101 */       while (Bits.anyAreSet(newVal, 2305843009213693952L)) {
/* 102 */         while (buf.hasRemaining()) {
/* 103 */           byte b = buf.get();
/* 104 */           if ((b >= 48 && b <= 57) || (b >= 97 && b <= 102) || (b >= 65 && b <= 70)) {
/* 105 */             if (chunkRemaining > 576460752303423487L) {
/* 106 */               throw UndertowMessages.MESSAGES.chunkSizeTooLarge();
/*     */             }
/* 108 */             chunkRemaining <<= 4L;
/* 109 */             chunkRemaining += Character.digit((char)b, 16); continue;
/*     */           } 
/* 111 */           if (b == 10) {
/* 112 */             newVal &= 0xDFFFFFFFFFFFFFFFL; break;
/*     */           } 
/* 114 */           newVal = newVal & 0xDFFFFFFFFFFFFFFFL | 0x1000000000000000L;
/*     */         } 
/*     */ 
/*     */ 
/*     */         
/* 119 */         if (Bits.anyAreSet(newVal, 2305843009213693952L)) {
/* 120 */           return 0L;
/*     */         }
/*     */       } 
/* 123 */       while (Bits.anyAreSet(newVal, 1152921504606846976L)) {
/* 124 */         while (buf.hasRemaining()) {
/* 125 */           if (buf.get() == 10) {
/* 126 */             newVal &= 0xEFFFFFFFFFFFFFFFL;
/*     */             break;
/*     */           } 
/*     */         } 
/* 130 */         if (Bits.anyAreSet(newVal, 1152921504606846976L)) {
/* 131 */           return 0L;
/*     */         }
/*     */       } 
/*     */ 
/*     */       
/* 136 */       if (Bits.allAreClear(newVal, 4035225266123964416L) && chunkRemaining == 0L) {
/* 137 */         newVal |= 0x400000000000000L;
/* 138 */         int ret = handleChunkedRequestEnd(buf);
/* 139 */         if (ret == -1) {
/* 140 */           newVal |= 0x4000000000000000L;
/* 141 */           return -1L;
/*     */         } 
/* 143 */         return 0L;
/*     */       } 
/* 145 */       return chunkRemaining;
/*     */     } finally {
/* 147 */       this.state = newVal | chunkRemaining;
/*     */     } 
/*     */   }
/*     */   
/*     */   public long getChunkRemaining() {
/* 152 */     if (Bits.anyAreSet(this.state, 4611686018427387904L)) {
/* 153 */       return -1L;
/*     */     }
/* 155 */     if (Bits.anyAreSet(this.state, 4323455642275676160L)) {
/* 156 */       return 0L;
/*     */     }
/* 158 */     return this.state & MASK_COUNT;
/*     */   }
/*     */   
/*     */   public void setChunkRemaining(long remaining) {
/* 162 */     if (remaining < 0L || Bits.anyAreSet(this.state, 4323455642275676160L)) {
/*     */       return;
/*     */     }
/* 165 */     long old = this.state;
/* 166 */     long oldRemaining = old & MASK_COUNT;
/* 167 */     if (remaining == 0L && oldRemaining != 0L)
/*     */     {
/*     */       
/* 170 */       old |= 0x800000000000000L;
/*     */     }
/* 172 */     this.state = old & (MASK_COUNT ^ 0xFFFFFFFFFFFFFFFFL) | remaining;
/*     */   }
/*     */   
/*     */   private int handleChunkedRequestEnd(ByteBuffer buffer) throws IOException {
/* 176 */     if (this.trailerParser != null) {
/* 177 */       return this.trailerParser.handle(buffer);
/*     */     }
/* 179 */     while (buffer.hasRemaining()) {
/* 180 */       byte b = buffer.get();
/* 181 */       if (b == 10)
/* 182 */         return -1; 
/* 183 */       if (b != 13) {
/* 184 */         buffer.position(buffer.position() - 1);
/* 185 */         this.trailerParser = new TrailerParser();
/* 186 */         return this.trailerParser.handle(buffer);
/*     */       } 
/*     */     } 
/* 189 */     return 0;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final class TrailerParser
/*     */   {
/* 198 */     private HeaderMap headerMap = new HeaderMap();
/* 199 */     private StringBuilder builder = new StringBuilder();
/*     */     private HttpString httpString;
/* 201 */     int state = 0;
/*     */     
/*     */     private static final int STATE_TRAILER_NAME = 0;
/*     */     
/*     */     private static final int STATE_TRAILER_VALUE = 1;
/*     */     private static final int STATE_ENDING = 2;
/*     */     
/*     */     public int handle(ByteBuffer buf) throws IOException {
/* 209 */       while (buf.hasRemaining()) {
/* 210 */         byte b = buf.get();
/* 211 */         if (this.state == 0) {
/* 212 */           if (b == 13) {
/* 213 */             if (this.builder.length() == 0) {
/* 214 */               this.state = 2; continue;
/*     */             } 
/* 216 */             throw UndertowMessages.MESSAGES.couldNotDecodeTrailers();
/*     */           } 
/* 218 */           if (b == 10) {
/* 219 */             if (this.builder.length() == 0) {
/* 220 */               ChunkReader.this.attachable.putAttachment(ChunkReader.this.trailerAttachmentKey, this.headerMap);
/* 221 */               return -1;
/*     */             } 
/* 223 */             throw UndertowMessages.MESSAGES.couldNotDecodeTrailers();
/*     */           } 
/* 225 */           if (b == 58) {
/* 226 */             this.httpString = HttpString.tryFromString(this.builder.toString().trim());
/* 227 */             this.state = 1;
/* 228 */             this.builder.setLength(0); continue;
/*     */           } 
/* 230 */           this.builder.append((char)b); continue;
/*     */         } 
/* 232 */         if (this.state == 1) {
/* 233 */           if (b == 10) {
/* 234 */             this.headerMap.put(this.httpString, this.builder.toString().trim());
/* 235 */             this.httpString = null;
/* 236 */             this.builder.setLength(0);
/* 237 */             this.state = 0; continue;
/* 238 */           }  if (b != 13)
/* 239 */             this.builder.append((char)b);  continue;
/*     */         } 
/* 241 */         if (this.state == 2) {
/* 242 */           if (b == 10) {
/* 243 */             if (ChunkReader.this.attachable != null) {
/* 244 */               ChunkReader.this.attachable.putAttachment(ChunkReader.this.trailerAttachmentKey, this.headerMap);
/*     */             }
/* 246 */             return -1;
/*     */           } 
/* 248 */           throw UndertowMessages.MESSAGES.couldNotDecodeTrailers();
/*     */         } 
/*     */         
/* 251 */         throw new IllegalStateException();
/*     */       } 
/*     */       
/* 254 */       return 0;
/*     */     }
/*     */     
/*     */     private TrailerParser() {}
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\conduits\ChunkReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */