package org.xnio.streams;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.Queue;
import org.xnio.Buffers;
import org.xnio.Pooled;
import org.xnio.Xnio;
import org.xnio._private.Messages;

public class BufferPipeInputStream extends InputStream {
   private final Queue<Pooled<ByteBuffer>> queue;
   private final InputHandler inputHandler;
   private boolean eof;
   private IOException failure;

   public BufferPipeInputStream(InputHandler inputHandler) {
      this.inputHandler = inputHandler;
      this.queue = new ArrayDeque();
   }

   public void push(ByteBuffer buffer) {
      synchronized(this) {
         if (buffer.hasRemaining() && !this.eof && this.failure == null) {
            this.queue.add(Buffers.pooledWrapper(buffer));
            this.notifyAll();
         }

      }
   }

   public void push(Pooled<ByteBuffer> pooledBuffer) {
      synchronized(this) {
         if (((ByteBuffer)pooledBuffer.getResource()).hasRemaining() && !this.eof && this.failure == null) {
            this.queue.add(pooledBuffer);
            this.notifyAll();
         } else {
            pooledBuffer.free();
         }

      }
   }

   public void pushException(IOException e) {
      synchronized(this) {
         if (!this.eof) {
            this.failure = e;
            this.notifyAll();
         }

      }
   }

   public void pushEof() {
      synchronized(this) {
         this.eof = true;
         this.notifyAll();
      }
   }

   public int read() throws IOException {
      Queue<Pooled<ByteBuffer>> queue = this.queue;
      synchronized(this) {
         while(queue.isEmpty()) {
            if (this.eof) {
               return -1;
            }

            this.checkFailure();
            Xnio.checkBlockingAllowed();

            try {
               this.wait();
            } catch (InterruptedException var15) {
               Thread.currentThread().interrupt();
               throw Messages.msg.interruptedIO();
            }
         }

         Pooled<ByteBuffer> entry = (Pooled)queue.peek();
         ByteBuffer buf = (ByteBuffer)entry.getResource();
         int v = buf.get() & 255;
         if (buf.remaining() == 0) {
            queue.poll();

            try {
               this.inputHandler.acknowledge(entry);
            } catch (IOException var13) {
            } finally {
               entry.free();
            }
         }

         return v;
      }
   }

   private void clearQueue() {
      synchronized(this) {
         Pooled entry;
         while((entry = (Pooled)this.queue.poll()) != null) {
            entry.free();
         }

      }
   }

   public int read(byte[] b, int off, int len) throws IOException {
      if (len == 0) {
         return 0;
      } else {
         Queue<Pooled<ByteBuffer>> queue = this.queue;
         synchronized(this) {
            while(queue.isEmpty()) {
               if (this.eof) {
                  return -1;
               }

               this.checkFailure();
               Xnio.checkBlockingAllowed();

               try {
                  this.wait();
               } catch (InterruptedException var19) {
                  Thread.currentThread().interrupt();
                  throw Messages.msg.interruptedIO();
               }
            }

            int total = 0;

            while(len > 0) {
               Pooled<ByteBuffer> entry = (Pooled)queue.peek();
               if (entry == null) {
                  break;
               }

               ByteBuffer buffer = (ByteBuffer)entry.getResource();
               int byteCnt = Math.min(buffer.remaining(), len);
               buffer.get(b, off, byteCnt);
               off += byteCnt;
               total += byteCnt;
               len -= byteCnt;
               if (buffer.remaining() == 0) {
                  queue.poll();

                  try {
                     this.inputHandler.acknowledge(entry);
                  } catch (IOException var17) {
                  } finally {
                     entry.free();
                  }
               }
            }

            return total;
         }
      }
   }

   public int available() throws IOException {
      synchronized(this) {
         int total = 0;
         Iterator var3 = this.queue.iterator();

         do {
            if (!var3.hasNext()) {
               return total;
            }

            Pooled<ByteBuffer> entry = (Pooled)var3.next();
            total += ((ByteBuffer)entry.getResource()).remaining();
         } while(total >= 0);

         return Integer.MAX_VALUE;
      }
   }

   public long skip(long qty) throws IOException {
      Queue<Pooled<ByteBuffer>> queue = this.queue;
      synchronized(this) {
         while(queue.isEmpty()) {
            if (this.eof) {
               return 0L;
            }

            this.checkFailure();
            Xnio.checkBlockingAllowed();

            try {
               this.wait();
            } catch (InterruptedException var19) {
               Thread.currentThread().interrupt();
               throw Messages.msg.interruptedIO();
            }
         }

         long skipped = 0L;

         while(qty > 0L) {
            Pooled<ByteBuffer> entry = (Pooled)queue.peek();
            if (entry == null) {
               break;
            }

            ByteBuffer buffer = (ByteBuffer)entry.getResource();
            int byteCnt = (int)Math.min((long)buffer.remaining(), Math.max(2147483647L, qty));
            buffer.position(buffer.position() + byteCnt);
            skipped += (long)byteCnt;
            qty -= (long)byteCnt;
            if (buffer.remaining() == 0) {
               queue.poll();

               try {
                  this.inputHandler.acknowledge(entry);
               } catch (IOException var17) {
               } finally {
                  entry.free();
               }
            }
         }

         return skipped;
      }
   }

   public void close() throws IOException {
      synchronized(this) {
         if (!this.eof) {
            this.clearQueue();
            this.eof = true;
            this.failure = null;
            this.notifyAll();
            this.inputHandler.close();
         }

      }
   }

   private void checkFailure() throws IOException {
      assert Thread.holdsLock(this);

      IOException failure = this.failure;
      if (failure != null) {
         failure.fillInStackTrace();

         try {
            throw failure;
         } finally {
            this.clearQueue();
            this.notifyAll();
         }
      }
   }

   public interface InputHandler extends Closeable {
      void acknowledge(Pooled<ByteBuffer> var1) throws IOException;

      void close() throws IOException;
   }
}
