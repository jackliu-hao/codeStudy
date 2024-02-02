package io.undertow.conduits;

import io.undertow.UndertowMessages;
import io.undertow.util.Attachable;
import io.undertow.util.AttachmentKey;
import io.undertow.util.HeaderMap;
import io.undertow.util.HttpString;
import java.io.IOException;
import java.nio.ByteBuffer;
import org.xnio.Bits;
import org.xnio.conduits.Conduit;

class ChunkReader<T extends Conduit> {
   private static final long FLAG_FINISHED = 4611686018427387904L;
   private static final long FLAG_READING_LENGTH = 2305843009213693952L;
   private static final long FLAG_READING_TILL_END_OF_LINE = 1152921504606846976L;
   private static final long FLAG_READING_NEWLINE = 576460752303423488L;
   private static final long FLAG_READING_AFTER_LAST = 288230376151711744L;
   private static final long MASK_COUNT = Bits.longBitMask(0, 56);
   private static final long LIMIT = 576460752303423487L;
   private long state;
   private final Attachable attachable;
   private final AttachmentKey<HeaderMap> trailerAttachmentKey;
   private ChunkReader<T>.TrailerParser trailerParser;
   private final T conduit;

   ChunkReader(Attachable attachable, AttachmentKey<HeaderMap> trailerAttachmentKey, T conduit) {
      this.attachable = attachable;
      this.trailerAttachmentKey = trailerAttachmentKey;
      this.conduit = conduit;
      this.state = 2305843009213693952L;
   }

   public long readChunk(ByteBuffer buf) throws IOException {
      long oldVal = this.state;
      long chunkRemaining = this.state & MASK_COUNT;
      if (chunkRemaining > 0L && !Bits.anyAreSet(this.state, 4323455642275676160L)) {
         return chunkRemaining;
      } else {
         long newVal = oldVal & ~MASK_COUNT;

         long var9;
         try {
            int ret;
            if (!Bits.anyAreSet(oldVal, 288230376151711744L)) {
               long var15;
               do {
                  byte b;
                  if (!Bits.anyAreSet(newVal, 576460752303423488L)) {
                     do {
                        if (!Bits.anyAreSet(newVal, 2305843009213693952L)) {
                           do {
                              if (!Bits.anyAreSet(newVal, 1152921504606846976L)) {
                                 if (Bits.allAreClear(newVal, 4035225266123964416L) && chunkRemaining == 0L) {
                                    newVal |= 288230376151711744L;
                                    ret = this.handleChunkedRequestEnd(buf);
                                    if (ret == -1) {
                                       newVal |= 4611686018427387904L;
                                       var9 = -1L;
                                       return var9;
                                    }

                                    var9 = 0L;
                                    return var9;
                                 }

                                 var15 = chunkRemaining;
                                 return var15;
                              }

                              while(buf.hasRemaining()) {
                                 if (buf.get() == 10) {
                                    newVal &= -1152921504606846977L;
                                    break;
                                 }
                              }
                           } while(!Bits.anyAreSet(newVal, 1152921504606846976L));

                           var15 = 0L;
                           return var15;
                        }

                        while(buf.hasRemaining()) {
                           b = buf.get();
                           if ((b < 48 || b > 57) && (b < 97 || b > 102) && (b < 65 || b > 70)) {
                              if (b == 10) {
                                 newVal &= -2305843009213693953L;
                              } else {
                                 newVal = newVal & -2305843009213693953L | 1152921504606846976L;
                              }
                              break;
                           }

                           if (chunkRemaining > 576460752303423487L) {
                              throw UndertowMessages.MESSAGES.chunkSizeTooLarge();
                           }

                           chunkRemaining <<= 4;
                           chunkRemaining += (long)Character.digit((char)b, 16);
                        }
                     } while(!Bits.anyAreSet(newVal, 2305843009213693952L));

                     var15 = 0L;
                     return var15;
                  }

                  while(buf.hasRemaining()) {
                     b = buf.get();
                     if (b == 10) {
                        newVal = newVal & -576460752303423489L | 2305843009213693952L;
                        break;
                     }
                  }
               } while(!Bits.anyAreSet(newVal, 576460752303423488L));

               var15 = 0L;
               return var15;
            }

            ret = this.handleChunkedRequestEnd(buf);
            if (ret != -1) {
               var9 = 0L;
               return var9;
            }

            newVal |= 4611686018427387904L;
            var9 = -1L;
         } finally {
            this.state = newVal | chunkRemaining;
         }

         return var9;
      }
   }

   public long getChunkRemaining() {
      if (Bits.anyAreSet(this.state, 4611686018427387904L)) {
         return -1L;
      } else {
         return Bits.anyAreSet(this.state, 4323455642275676160L) ? 0L : this.state & MASK_COUNT;
      }
   }

   public void setChunkRemaining(long remaining) {
      if (remaining >= 0L && !Bits.anyAreSet(this.state, 4323455642275676160L)) {
         long old = this.state;
         long oldRemaining = old & MASK_COUNT;
         if (remaining == 0L && oldRemaining != 0L) {
            old |= 576460752303423488L;
         }

         this.state = old & ~MASK_COUNT | remaining;
      }
   }

   private int handleChunkedRequestEnd(ByteBuffer buffer) throws IOException {
      if (this.trailerParser != null) {
         return this.trailerParser.handle(buffer);
      } else {
         byte b;
         do {
            if (!buffer.hasRemaining()) {
               return 0;
            }

            b = buffer.get();
            if (b == 10) {
               return -1;
            }
         } while(b == 13);

         buffer.position(buffer.position() - 1);
         this.trailerParser = new TrailerParser();
         return this.trailerParser.handle(buffer);
      }
   }

   private final class TrailerParser {
      private HeaderMap headerMap;
      private StringBuilder builder;
      private HttpString httpString;
      int state;
      private static final int STATE_TRAILER_NAME = 0;
      private static final int STATE_TRAILER_VALUE = 1;
      private static final int STATE_ENDING = 2;

      private TrailerParser() {
         this.headerMap = new HeaderMap();
         this.builder = new StringBuilder();
         this.state = 0;
      }

      public int handle(ByteBuffer buf) throws IOException {
         while(true) {
            if (buf.hasRemaining()) {
               byte b = buf.get();
               if (this.state == 0) {
                  if (b == 13) {
                     if (this.builder.length() == 0) {
                        this.state = 2;
                        continue;
                     }

                     throw UndertowMessages.MESSAGES.couldNotDecodeTrailers();
                  }

                  if (b == 10) {
                     if (this.builder.length() == 0) {
                        ChunkReader.this.attachable.putAttachment(ChunkReader.this.trailerAttachmentKey, this.headerMap);
                        return -1;
                     }

                     throw UndertowMessages.MESSAGES.couldNotDecodeTrailers();
                  }

                  if (b == 58) {
                     this.httpString = HttpString.tryFromString(this.builder.toString().trim());
                     this.state = 1;
                     this.builder.setLength(0);
                     continue;
                  }

                  this.builder.append((char)b);
                  continue;
               }

               if (this.state == 1) {
                  if (b == 10) {
                     this.headerMap.put(this.httpString, this.builder.toString().trim());
                     this.httpString = null;
                     this.builder.setLength(0);
                     this.state = 0;
                     continue;
                  }

                  if (b != 13) {
                     this.builder.append((char)b);
                  }
                  continue;
               }

               if (this.state == 2) {
                  if (b == 10) {
                     if (ChunkReader.this.attachable != null) {
                        ChunkReader.this.attachable.putAttachment(ChunkReader.this.trailerAttachmentKey, this.headerMap);
                     }

                     return -1;
                  }

                  throw UndertowMessages.MESSAGES.couldNotDecodeTrailers();
               }

               throw new IllegalStateException();
            }

            return 0;
         }
      }

      // $FF: synthetic method
      TrailerParser(Object x1) {
         this();
      }
   }
}
