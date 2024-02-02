package org.xnio.streams;

import java.io.Flushable;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import org.xnio.Pooled;

public class BufferPipeOutputStream extends OutputStream {
   private Pooled<ByteBuffer> buffer;
   private boolean closed;
   private final BufferWriter bufferWriterTask;

   public BufferPipeOutputStream(BufferWriter bufferWriterTask) throws IOException {
      this.bufferWriterTask = bufferWriterTask;
      synchronized(this) {
         this.buffer = bufferWriterTask.getBuffer(true);
      }
   }

   private static IOException closed() {
      return new IOException("Stream is closed");
   }

   private void checkClosed() throws IOException {
      assert Thread.holdsLock(this);

      if (this.closed) {
         throw closed();
      }
   }

   private Pooled<ByteBuffer> getBuffer() throws IOException {
      assert Thread.holdsLock(this);

      Pooled<ByteBuffer> buffer = this.buffer;
      if (buffer != null && ((ByteBuffer)buffer.getResource()).hasRemaining()) {
         return buffer;
      } else {
         if (buffer != null) {
            this.send(false);
         }

         return this.buffer = this.bufferWriterTask.getBuffer(false);
      }
   }

   public void write(int b) throws IOException {
      synchronized(this) {
         this.checkClosed();
         ((ByteBuffer)this.getBuffer().getResource()).put((byte)b);
      }
   }

   public void write(byte[] b, int off, int len) throws IOException {
      synchronized(this) {
         this.checkClosed();

         while(len > 0) {
            ByteBuffer buffer = (ByteBuffer)this.getBuffer().getResource();
            int cnt = Math.min(len, buffer.remaining());
            buffer.put(b, off, cnt);
            len -= cnt;
            off += cnt;
         }

      }
   }

   private void send(boolean eof) throws IOException {
      assert Thread.holdsLock(this);

      assert !this.closed;

      Pooled<ByteBuffer> pooledBuffer = this.buffer;
      ByteBuffer buffer = pooledBuffer == null ? null : (ByteBuffer)pooledBuffer.getResource();
      this.buffer = null;
      if (buffer != null && buffer.position() > 0) {
         buffer.flip();
         this.send(pooledBuffer, eof);
      } else if (eof) {
         Pooled<ByteBuffer> pooledBuffer1 = this.getBuffer();
         ByteBuffer buffer1 = (ByteBuffer)pooledBuffer1.getResource();
         buffer1.flip();
         this.send(pooledBuffer1, eof);
      }

   }

   private void send(Pooled<ByteBuffer> buffer, boolean eof) throws IOException {
      assert Thread.holdsLock(this);

      try {
         this.bufferWriterTask.accept(buffer, eof);
      } catch (IOException var4) {
         this.closed = true;
         throw var4;
      }
   }

   public void flush() throws IOException {
      this.flush(false);
   }

   private void flush(boolean eof) throws IOException {
      synchronized(this) {
         if (!this.closed) {
            this.send(eof);

            try {
               this.bufferWriterTask.flush();
            } catch (IOException var5) {
               this.closed = true;
               this.buffer = null;
               throw var5;
            }

         }
      }
   }

   public void close() throws IOException {
      synchronized(this) {
         if (!this.closed) {
            try {
               this.flush(true);
            } finally {
               this.closed = true;
            }

         }
      }
   }

   public Pooled<ByteBuffer> breakPipe() {
      synchronized(this) {
         if (this.closed) {
            return null;
         } else {
            this.closed = true;

            Pooled var2;
            try {
               var2 = this.buffer;
            } finally {
               this.buffer = null;
            }

            return var2;
         }
      }
   }

   public interface BufferWriter extends Flushable {
      Pooled<ByteBuffer> getBuffer(boolean var1) throws IOException;

      void accept(Pooled<ByteBuffer> var1, boolean var2) throws IOException;

      void flush() throws IOException;
   }
}
