package org.antlr.v4.runtime;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Arrays;
import org.antlr.v4.runtime.misc.Interval;

public class ANTLRInputStream implements CharStream {
   public static final int READ_BUFFER_SIZE = 1024;
   public static final int INITIAL_BUFFER_SIZE = 1024;
   protected char[] data;
   protected int n;
   protected int p;
   public String name;

   public ANTLRInputStream() {
      this.p = 0;
   }

   public ANTLRInputStream(String input) {
      this.p = 0;
      this.data = input.toCharArray();
      this.n = input.length();
   }

   public ANTLRInputStream(char[] data, int numberOfActualCharsInArray) {
      this.p = 0;
      this.data = data;
      this.n = numberOfActualCharsInArray;
   }

   public ANTLRInputStream(Reader r) throws IOException {
      this((Reader)r, 1024, 1024);
   }

   public ANTLRInputStream(Reader r, int initialSize) throws IOException {
      this((Reader)r, initialSize, 1024);
   }

   public ANTLRInputStream(Reader r, int initialSize, int readChunkSize) throws IOException {
      this.p = 0;
      this.load(r, initialSize, readChunkSize);
   }

   public ANTLRInputStream(InputStream input) throws IOException {
      this((Reader)(new InputStreamReader(input)), 1024);
   }

   public ANTLRInputStream(InputStream input, int initialSize) throws IOException {
      this((Reader)(new InputStreamReader(input)), initialSize);
   }

   public ANTLRInputStream(InputStream input, int initialSize, int readChunkSize) throws IOException {
      this((Reader)(new InputStreamReader(input)), initialSize, readChunkSize);
   }

   public void load(Reader r, int size, int readChunkSize) throws IOException {
      if (r != null) {
         if (size <= 0) {
            size = 1024;
         }

         if (readChunkSize <= 0) {
            readChunkSize = 1024;
         }

         try {
            this.data = new char[size];
            int numRead = false;
            int p = 0;

            int numRead;
            do {
               if (p + readChunkSize > this.data.length) {
                  this.data = Arrays.copyOf(this.data, this.data.length * 2);
               }

               numRead = r.read(this.data, p, readChunkSize);
               p += numRead;
            } while(numRead != -1);

            this.n = p + 1;
         } finally {
            r.close();
         }
      }
   }

   public void reset() {
      this.p = 0;
   }

   public void consume() {
      if (this.p >= this.n) {
         assert this.LA(1) == -1;

         throw new IllegalStateException("cannot consume EOF");
      } else {
         if (this.p < this.n) {
            ++this.p;
         }

      }
   }

   public int LA(int i) {
      if (i == 0) {
         return 0;
      } else {
         if (i < 0) {
            ++i;
            if (this.p + i - 1 < 0) {
               return -1;
            }
         }

         return this.p + i - 1 >= this.n ? -1 : this.data[this.p + i - 1];
      }
   }

   public int LT(int i) {
      return this.LA(i);
   }

   public int index() {
      return this.p;
   }

   public int size() {
      return this.n;
   }

   public int mark() {
      return -1;
   }

   public void release(int marker) {
   }

   public void seek(int index) {
      if (index <= this.p) {
         this.p = index;
      } else {
         index = Math.min(index, this.n);

         while(this.p < index) {
            this.consume();
         }

      }
   }

   public String getText(Interval interval) {
      int start = interval.a;
      int stop = interval.b;
      if (stop >= this.n) {
         stop = this.n - 1;
      }

      int count = stop - start + 1;
      return start >= this.n ? "" : new String(this.data, start, count);
   }

   public String getSourceName() {
      return this.name != null && !this.name.isEmpty() ? this.name : "<unknown>";
   }

   public String toString() {
      return new String(this.data);
   }
}
