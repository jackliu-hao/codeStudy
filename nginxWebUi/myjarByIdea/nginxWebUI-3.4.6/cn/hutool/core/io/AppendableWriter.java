package cn.hutool.core.io;

import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;
import java.io.Writer;
import java.nio.CharBuffer;

public class AppendableWriter extends Writer implements Appendable {
   private final Appendable appendable;
   private final boolean flushable;
   private boolean closed;

   public AppendableWriter(Appendable appendable) {
      this.appendable = appendable;
      this.flushable = appendable instanceof Flushable;
      this.closed = false;
   }

   public void write(char[] cbuf, int off, int len) throws IOException {
      this.checkNotClosed();
      this.appendable.append(CharBuffer.wrap(cbuf), off, off + len);
   }

   public void write(int c) throws IOException {
      this.checkNotClosed();
      this.appendable.append((char)c);
   }

   public Writer append(char c) throws IOException {
      this.checkNotClosed();
      this.appendable.append(c);
      return this;
   }

   public Writer append(CharSequence csq, int start, int end) throws IOException {
      this.checkNotClosed();
      this.appendable.append(csq, start, end);
      return this;
   }

   public Writer append(CharSequence csq) throws IOException {
      this.checkNotClosed();
      this.appendable.append(csq);
      return this;
   }

   public void write(String str, int off, int len) throws IOException {
      this.checkNotClosed();
      this.appendable.append(str, off, off + len);
   }

   public void write(String str) throws IOException {
      this.appendable.append(str);
   }

   public void write(char[] cbuf) throws IOException {
      this.appendable.append(CharBuffer.wrap(cbuf));
   }

   public void flush() throws IOException {
      this.checkNotClosed();
      if (this.flushable) {
         ((Flushable)this.appendable).flush();
      }

   }

   private void checkNotClosed() throws IOException {
      if (this.closed) {
         throw new IOException("Writer is closed!" + this);
      }
   }

   public void close() throws IOException {
      if (!this.closed) {
         this.flush();
         if (this.appendable instanceof Closeable) {
            ((Closeable)this.appendable).close();
         }

         this.closed = true;
      }

   }
}
