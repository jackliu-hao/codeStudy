package org.antlr.v4.runtime;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Arrays;
import org.antlr.v4.runtime.misc.Interval;

public class UnbufferedCharStream implements CharStream {
   protected char[] data;
   protected int n;
   protected int p;
   protected int numMarkers;
   protected int lastChar;
   protected int lastCharBufferStart;
   protected int currentCharIndex;
   protected Reader input;
   public String name;

   public UnbufferedCharStream() {
      this(256);
   }

   public UnbufferedCharStream(int bufferSize) {
      this.p = 0;
      this.numMarkers = 0;
      this.lastChar = -1;
      this.currentCharIndex = 0;
      this.n = 0;
      this.data = new char[bufferSize];
   }

   public UnbufferedCharStream(InputStream input) {
      this((InputStream)input, 256);
   }

   public UnbufferedCharStream(Reader input) {
      this((Reader)input, 256);
   }

   public UnbufferedCharStream(InputStream input, int bufferSize) {
      this(bufferSize);
      this.input = new InputStreamReader(input);
      this.fill(1);
   }

   public UnbufferedCharStream(Reader input, int bufferSize) {
      this(bufferSize);
      this.input = input;
      this.fill(1);
   }

   public void consume() {
      if (this.LA(1) == -1) {
         throw new IllegalStateException("cannot consume EOF");
      } else {
         this.lastChar = this.data[this.p];
         if (this.p == this.n - 1 && this.numMarkers == 0) {
            this.n = 0;
            this.p = -1;
            this.lastCharBufferStart = this.lastChar;
         }

         ++this.p;
         ++this.currentCharIndex;
         this.sync(1);
      }
   }

   protected void sync(int want) {
      int need = this.p + want - 1 - this.n + 1;
      if (need > 0) {
         this.fill(need);
      }

   }

   protected int fill(int n) {
      for(int i = 0; i < n; ++i) {
         if (this.n > 0 && this.data[this.n - 1] == '\uffff') {
            return i;
         }

         try {
            int c = this.nextChar();
            this.add(c);
         } catch (IOException var4) {
            throw new RuntimeException(var4);
         }
      }

      return n;
   }

   protected int nextChar() throws IOException {
      return this.input.read();
   }

   protected void add(int c) {
      if (this.n >= this.data.length) {
         this.data = Arrays.copyOf(this.data, this.data.length * 2);
      }

      this.data[this.n++] = (char)c;
   }

   public int LA(int i) {
      if (i == -1) {
         return this.lastChar;
      } else {
         this.sync(i);
         int index = this.p + i - 1;
         if (index < 0) {
            throw new IndexOutOfBoundsException();
         } else if (index >= this.n) {
            return -1;
         } else {
            char c = this.data[index];
            return c == '\uffff' ? -1 : c;
         }
      }
   }

   public int mark() {
      if (this.numMarkers == 0) {
         this.lastCharBufferStart = this.lastChar;
      }

      int mark = -this.numMarkers - 1;
      ++this.numMarkers;
      return mark;
   }

   public void release(int marker) {
      int expectedMark = -this.numMarkers;
      if (marker != expectedMark) {
         throw new IllegalStateException("release() called with an invalid marker.");
      } else {
         --this.numMarkers;
         if (this.numMarkers == 0 && this.p > 0) {
            System.arraycopy(this.data, this.p, this.data, 0, this.n - this.p);
            this.n -= this.p;
            this.p = 0;
            this.lastCharBufferStart = this.lastChar;
         }

      }
   }

   public int index() {
      return this.currentCharIndex;
   }

   public void seek(int index) {
      if (index != this.currentCharIndex) {
         if (index > this.currentCharIndex) {
            this.sync(index - this.currentCharIndex);
            index = Math.min(index, this.getBufferStartIndex() + this.n - 1);
         }

         int i = index - this.getBufferStartIndex();
         if (i < 0) {
            throw new IllegalArgumentException("cannot seek to negative index " + index);
         } else if (i >= this.n) {
            throw new UnsupportedOperationException("seek to index outside buffer: " + index + " not in " + this.getBufferStartIndex() + ".." + (this.getBufferStartIndex() + this.n));
         } else {
            this.p = i;
            this.currentCharIndex = index;
            if (this.p == 0) {
               this.lastChar = this.lastCharBufferStart;
            } else {
               this.lastChar = this.data[this.p - 1];
            }

         }
      }
   }

   public int size() {
      throw new UnsupportedOperationException("Unbuffered stream cannot know its size");
   }

   public String getSourceName() {
      return this.name != null && !this.name.isEmpty() ? this.name : "<unknown>";
   }

   public String getText(Interval interval) {
      if (interval.a >= 0 && interval.b >= interval.a - 1) {
         int bufferStartIndex = this.getBufferStartIndex();
         if (this.n > 0 && this.data[this.n - 1] == '\uffff' && interval.a + interval.length() > bufferStartIndex + this.n) {
            throw new IllegalArgumentException("the interval extends past the end of the stream");
         } else if (interval.a >= bufferStartIndex && interval.b < bufferStartIndex + this.n) {
            int i = interval.a - bufferStartIndex;
            return new String(this.data, i, interval.length());
         } else {
            throw new UnsupportedOperationException("interval " + interval + " outside buffer: " + bufferStartIndex + ".." + (bufferStartIndex + this.n - 1));
         }
      } else {
         throw new IllegalArgumentException("invalid interval");
      }
   }

   protected final int getBufferStartIndex() {
      return this.currentCharIndex - this.p;
   }
}
