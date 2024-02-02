package org.xnio.streams;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.xnio._private.Messages;

public final class Pipe {
   private final Object lock = new Object();
   private int tail;
   private int size;
   private final byte[] buffer;
   private boolean writeClosed;
   private boolean readClosed;
   private final InputStream in = new InputStream() {
      public int read() throws IOException {
         Object lock = Pipe.this.lock;
         synchronized(lock) {
            if (Pipe.this.writeClosed && Pipe.this.size == 0) {
               return -1;
            } else {
               while(Pipe.this.size == 0) {
                  try {
                     lock.wait();
                     if (Pipe.this.writeClosed && Pipe.this.size == 0) {
                        byte var10000 = -1;
                        return var10000;
                     }
                  } catch (InterruptedException var11) {
                     Thread.currentThread().interrupt();
                     throw Messages.msg.interruptedIO();
                  }
               }

               lock.notifyAll();
               int tail = Pipe.this.tail;

               int var4;
               try {
                  var4 = Pipe.this.buffer[tail++] & 255;
               } finally {
                  Pipe.this.tail = tail == Pipe.this.buffer.length ? 0 : tail;
                  Pipe.this.size--;
               }

               return var4;
            }
         }
      }

      public int read(byte[] b, int off, int len) throws IOException {
         Object lock = Pipe.this.lock;
         synchronized(lock) {
            if (Pipe.this.writeClosed && Pipe.this.size == 0) {
               return -1;
            } else if (len == 0) {
               return 0;
            } else {
               int size;
               while((size = Pipe.this.size) == 0) {
                  try {
                     lock.wait();
                     if (Pipe.this.writeClosed && Pipe.this.size == 0) {
                        byte var10000 = -1;
                        return var10000;
                     }
                  } catch (InterruptedException var15) {
                     Thread.currentThread().interrupt();
                     throw Messages.msg.interruptedIO();
                  }
               }

               byte[] buffer = Pipe.this.buffer;
               int bufLen = buffer.length;
               int tail = Pipe.this.tail;
               int cnt;
               if (size + tail > bufLen) {
                  int lastLen = bufLen - tail;
                  if (lastLen < len) {
                     int firstLen = tail + size - bufLen;
                     System.arraycopy(buffer, tail, b, off, lastLen);
                     int rem = Math.min(len - lastLen, firstLen);
                     System.arraycopy(buffer, 0, b, off + lastLen, rem);
                     cnt = rem + lastLen;
                  } else {
                     System.arraycopy(buffer, tail, b, off, len);
                     cnt = len;
                  }
               } else {
                  cnt = Math.min(len, size);
                  System.arraycopy(buffer, tail, b, off, cnt);
               }

               tail += cnt;
               size -= cnt;
               Pipe.this.tail = tail >= bufLen ? tail - bufLen : tail;
               Pipe.this.size = size;
               lock.notifyAll();
               return cnt;
            }
         }
      }

      public void close() throws IOException {
         Object lock = Pipe.this.lock;
         synchronized(lock) {
            Pipe.this.writeClosed = true;
            Pipe.this.readClosed = true;
            Pipe.this.size = 0;
            lock.notifyAll();
         }
      }

      public String toString() {
         return "Pipe read half";
      }
   };
   private final OutputStream out = new OutputStream() {
      public void write(int b) throws IOException {
         Object lock = Pipe.this.lock;
         synchronized(lock) {
            if (Pipe.this.writeClosed) {
               throw Messages.msg.streamClosed();
            } else {
               byte[] buffer = Pipe.this.buffer;
               int bufLen = buffer.length;

               while(Pipe.this.size == bufLen) {
                  try {
                     lock.wait();
                     if (Pipe.this.writeClosed) {
                        throw Messages.msg.streamClosed();
                     }
                  } catch (InterruptedException var9) {
                     Thread.currentThread().interrupt();
                     throw Messages.msg.interruptedIO();
                  }
               }

               int tail = Pipe.this.tail;
               int startPos = tail + Pipe.this.size;
               if (startPos >= bufLen) {
                  buffer[startPos - bufLen] = (byte)b;
               } else {
                  buffer[startPos] = (byte)b;
               }

               Pipe.this.size++;
               lock.notifyAll();
            }
         }
      }

      public void write(byte[] b, int off, int len) throws IOException {
         int remaining = len;
         Object lock = Pipe.this.lock;
         synchronized(lock) {
            if (Pipe.this.writeClosed) {
               throw Messages.msg.streamClosed();
            } else {
               byte[] buffer = Pipe.this.buffer;
               int bufLen = buffer.length;

               while(remaining > 0) {
                  int size;
                  while((size = Pipe.this.size) == bufLen) {
                     try {
                        lock.wait();
                        if (Pipe.this.writeClosed) {
                           throw Messages.msg.streamClosed();
                        }
                     } catch (InterruptedException var16) {
                        Thread.currentThread().interrupt();
                        throw Messages.msg.interruptedIO(len - remaining);
                     }
                  }

                  int tail = Pipe.this.tail;
                  int startPos = tail + size;
                  int cnt;
                  if (startPos >= bufLen) {
                     startPos -= bufLen;
                     cnt = Math.min(remaining, bufLen - size);
                     System.arraycopy(b, off, buffer, startPos, cnt);
                     remaining -= cnt;
                     off += cnt;
                  } else {
                     int firstPart = Math.min(remaining, bufLen - (tail + size));
                     System.arraycopy(b, off, buffer, startPos, firstPart);
                     off += firstPart;
                     remaining -= firstPart;
                     if (remaining > 0) {
                        int latter = Math.min(remaining, tail);
                        System.arraycopy(b, off, buffer, 0, latter);
                        cnt = firstPart + latter;
                        off += latter;
                        remaining -= latter;
                     } else {
                        cnt = firstPart;
                     }
                  }

                  Pipe var18 = Pipe.this;
                  var18.size = var18.size + cnt;
                  lock.notifyAll();
               }

            }
         }
      }

      public void close() throws IOException {
         Object lock = Pipe.this.lock;
         synchronized(lock) {
            Pipe.this.writeClosed = true;
            lock.notifyAll();
         }
      }

      public String toString() {
         return "Pipe write half";
      }
   };

   public Pipe(int bufferSize) {
      this.buffer = new byte[bufferSize];
   }

   public void await() {
      boolean intr = false;
      Object lock = this.lock;

      try {
         synchronized(lock) {
            while(!this.readClosed) {
               try {
                  lock.wait();
               } catch (InterruptedException var10) {
                  intr = true;
               }
            }
         }
      } finally {
         if (intr) {
            Thread.currentThread().interrupt();
         }

      }

   }

   public InputStream getIn() {
      return this.in;
   }

   public OutputStream getOut() {
      return this.out;
   }
}
