package io.undertow.protocols.http2;

import io.undertow.UndertowMessages;
import java.io.IOException;
import java.nio.ByteBuffer;

public abstract class Http2PushBackParser {
   private byte[] pushedBackData;
   private boolean finished;
   private int remainingData;
   private final int frameLength;
   int cnt;

   public Http2PushBackParser(int frameLength) {
      this.remainingData = frameLength;
      this.frameLength = frameLength;
   }

   public void parse(ByteBuffer data, Http2FrameHeaderParser headerParser) throws IOException {
      int used = 0;
      ByteBuffer dataToParse = data;
      int oldLimit = data.limit();
      Object original = null;
      boolean var15 = false;

      int leftOver;
      label313: {
         try {
            var15 = true;
            if (this.pushedBackData != null) {
               leftOver = Math.min(this.remainingData - this.pushedBackData.length, data.remaining());
               dataToParse = ByteBuffer.wrap(new byte[this.pushedBackData.length + leftOver]);
               dataToParse.put(this.pushedBackData);
               data.limit(data.position() + leftOver);
               dataToParse.put(data);
               dataToParse.flip();
            }

            if (dataToParse.remaining() > this.remainingData) {
               dataToParse.limit(dataToParse.position() + this.remainingData);
            }

            leftOver = dataToParse.remaining();
            this.handleData(dataToParse, headerParser);
            used = leftOver - dataToParse.remaining();
            if (!this.isFinished()) {
               if (this.remainingData > 0) {
                  if (used == 0) {
                     if (dataToParse.remaining() >= this.remainingData) {
                        if (this.cnt++ == 100) {
                           original = UndertowMessages.MESSAGES.parserDidNotMakeProgress();
                           var15 = false;
                        } else {
                           var15 = false;
                        }
                     } else {
                        var15 = false;
                     }
                  } else {
                     var15 = false;
                  }
               } else {
                  var15 = false;
               }
            } else {
               var15 = false;
            }
            break label313;
         } catch (Throwable var19) {
            original = var19;
            var15 = false;
         } finally {
            if (var15) {
               try {
                  if (this.finished) {
                     data.limit(oldLimit);
                  } else {
                     int leftOver = dataToParse.remaining();
                     if (leftOver > 0) {
                        this.pushedBackData = new byte[leftOver];
                        dataToParse.get(this.pushedBackData);
                     } else {
                        this.pushedBackData = null;
                     }

                     data.limit(oldLimit);
                     this.remainingData -= used;
                     if (this.remainingData == 0) {
                        this.finished = true;
                     }
                  }
               } catch (Throwable var16) {
                  if (original != null) {
                     ((Throwable)original).addSuppressed(var16);
                  } else {
                     original = var16;
                  }
               }

               if (original != null) {
                  if (original instanceof RuntimeException) {
                     throw (RuntimeException)original;
                  }

                  if (original instanceof Error) {
                     throw (Error)original;
                  }

                  if (original instanceof IOException) {
                     throw (IOException)original;
                  }
               }

            }
         }

         try {
            if (this.finished) {
               data.limit(oldLimit);
            } else {
               leftOver = dataToParse.remaining();
               if (leftOver > 0) {
                  this.pushedBackData = new byte[leftOver];
                  dataToParse.get(this.pushedBackData);
               } else {
                  this.pushedBackData = null;
               }

               data.limit(oldLimit);
               this.remainingData -= used;
               if (this.remainingData == 0) {
                  this.finished = true;
               }
            }
         } catch (Throwable var18) {
            if (original != null) {
               ((Throwable)original).addSuppressed(var18);
            } else {
               original = var18;
            }
         }

         if (original != null) {
            if (original instanceof RuntimeException) {
               throw (RuntimeException)original;
            }

            if (original instanceof Error) {
               throw (Error)original;
            }

            if (original instanceof IOException) {
               throw (IOException)original;
            }
         }

         return;
      }

      try {
         if (this.finished) {
            data.limit(oldLimit);
         } else {
            leftOver = dataToParse.remaining();
            if (leftOver > 0) {
               this.pushedBackData = new byte[leftOver];
               dataToParse.get(this.pushedBackData);
            } else {
               this.pushedBackData = null;
            }

            data.limit(oldLimit);
            this.remainingData -= used;
            if (this.remainingData == 0) {
               this.finished = true;
            }
         }
      } catch (Throwable var17) {
         if (original != null) {
            ((Throwable)original).addSuppressed(var17);
         } else {
            original = var17;
         }
      }

      if (original != null) {
         if (original instanceof RuntimeException) {
            throw (RuntimeException)original;
         }

         if (original instanceof Error) {
            throw (Error)original;
         }

         if (original instanceof IOException) {
            throw (IOException)original;
         }
      }

   }

   protected abstract void handleData(ByteBuffer var1, Http2FrameHeaderParser var2) throws IOException;

   public boolean isFinished() {
      return this.pushedBackData != null && this.remainingData == this.pushedBackData.length ? true : this.finished;
   }

   protected void finish() {
      this.finished = true;
   }

   protected void moreData(int data) {
      this.finished = false;
      this.remainingData += data;
   }

   public int getFrameLength() {
      return this.frameLength;
   }
}
