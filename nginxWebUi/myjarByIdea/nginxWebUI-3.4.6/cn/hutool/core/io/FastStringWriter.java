package cn.hutool.core.io;

import cn.hutool.core.text.StrBuilder;
import java.io.Writer;

public final class FastStringWriter extends Writer {
   private final StrBuilder builder;

   public FastStringWriter() {
      this(16);
   }

   public FastStringWriter(int initialSize) {
      if (initialSize < 0) {
         initialSize = 16;
      }

      this.builder = new StrBuilder(initialSize);
   }

   public void write(int c) {
      this.builder.append((char)c);
   }

   public void write(String str) {
      this.builder.append((CharSequence)str);
   }

   public void write(String str, int off, int len) {
      this.builder.append((CharSequence)str, off, off + len);
   }

   public void write(char[] cbuf) {
      this.builder.append((char[])cbuf, 0, cbuf.length);
   }

   public void write(char[] cbuf, int off, int len) {
      if (off >= 0 && off <= cbuf.length && len >= 0 && off + len <= cbuf.length && off + len >= 0) {
         if (len != 0) {
            this.builder.append(cbuf, off, len);
         }
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   public void flush() {
   }

   public void close() {
   }

   public String toString() {
      return this.builder.toString();
   }
}
