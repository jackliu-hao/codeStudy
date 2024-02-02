package org.antlr.v4.runtime;

import java.util.Arrays;
import org.antlr.v4.runtime.misc.Interval;

public class UnbufferedTokenStream<T extends Token> implements TokenStream {
   protected TokenSource tokenSource;
   protected Token[] tokens;
   protected int n;
   protected int p;
   protected int numMarkers;
   protected Token lastToken;
   protected Token lastTokenBufferStart;
   protected int currentTokenIndex;

   public UnbufferedTokenStream(TokenSource tokenSource) {
      this(tokenSource, 256);
   }

   public UnbufferedTokenStream(TokenSource tokenSource, int bufferSize) {
      this.p = 0;
      this.numMarkers = 0;
      this.currentTokenIndex = 0;
      this.tokenSource = tokenSource;
      this.tokens = new Token[bufferSize];
      this.n = 0;
      this.fill(1);
   }

   public Token get(int i) {
      int bufferStartIndex = this.getBufferStartIndex();
      if (i >= bufferStartIndex && i < bufferStartIndex + this.n) {
         return this.tokens[i - bufferStartIndex];
      } else {
         throw new IndexOutOfBoundsException("get(" + i + ") outside buffer: " + bufferStartIndex + ".." + (bufferStartIndex + this.n));
      }
   }

   public Token LT(int i) {
      if (i == -1) {
         return this.lastToken;
      } else {
         this.sync(i);
         int index = this.p + i - 1;
         if (index < 0) {
            throw new IndexOutOfBoundsException("LT(" + i + ") gives negative index");
         } else if (index < this.n) {
            return this.tokens[index];
         } else {
            assert this.n > 0 && this.tokens[this.n - 1].getType() == -1;

            return this.tokens[this.n - 1];
         }
      }
   }

   public int LA(int i) {
      return this.LT(i).getType();
   }

   public TokenSource getTokenSource() {
      return this.tokenSource;
   }

   public String getText() {
      return "";
   }

   public String getText(RuleContext ctx) {
      return this.getText(ctx.getSourceInterval());
   }

   public String getText(Token start, Token stop) {
      return this.getText(Interval.of(start.getTokenIndex(), stop.getTokenIndex()));
   }

   public void consume() {
      if (this.LA(1) == -1) {
         throw new IllegalStateException("cannot consume EOF");
      } else {
         this.lastToken = this.tokens[this.p];
         if (this.p == this.n - 1 && this.numMarkers == 0) {
            this.n = 0;
            this.p = -1;
            this.lastTokenBufferStart = this.lastToken;
         }

         ++this.p;
         ++this.currentTokenIndex;
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
         if (this.n > 0 && this.tokens[this.n - 1].getType() == -1) {
            return i;
         }

         Token t = this.tokenSource.nextToken();
         this.add(t);
      }

      return n;
   }

   protected void add(Token t) {
      if (this.n >= this.tokens.length) {
         this.tokens = (Token[])Arrays.copyOf(this.tokens, this.tokens.length * 2);
      }

      if (t instanceof WritableToken) {
         ((WritableToken)t).setTokenIndex(this.getBufferStartIndex() + this.n);
      }

      this.tokens[this.n++] = t;
   }

   public int mark() {
      if (this.numMarkers == 0) {
         this.lastTokenBufferStart = this.lastToken;
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
         if (this.numMarkers == 0) {
            if (this.p > 0) {
               System.arraycopy(this.tokens, this.p, this.tokens, 0, this.n - this.p);
               this.n -= this.p;
               this.p = 0;
            }

            this.lastTokenBufferStart = this.lastToken;
         }

      }
   }

   public int index() {
      return this.currentTokenIndex;
   }

   public void seek(int index) {
      if (index != this.currentTokenIndex) {
         if (index > this.currentTokenIndex) {
            this.sync(index - this.currentTokenIndex);
            index = Math.min(index, this.getBufferStartIndex() + this.n - 1);
         }

         int bufferStartIndex = this.getBufferStartIndex();
         int i = index - bufferStartIndex;
         if (i < 0) {
            throw new IllegalArgumentException("cannot seek to negative index " + index);
         } else if (i >= this.n) {
            throw new UnsupportedOperationException("seek to index outside buffer: " + index + " not in " + bufferStartIndex + ".." + (bufferStartIndex + this.n));
         } else {
            this.p = i;
            this.currentTokenIndex = index;
            if (this.p == 0) {
               this.lastToken = this.lastTokenBufferStart;
            } else {
               this.lastToken = this.tokens[this.p - 1];
            }

         }
      }
   }

   public int size() {
      throw new UnsupportedOperationException("Unbuffered stream cannot know its size");
   }

   public String getSourceName() {
      return this.tokenSource.getSourceName();
   }

   public String getText(Interval interval) {
      int bufferStartIndex = this.getBufferStartIndex();
      int bufferStopIndex = bufferStartIndex + this.tokens.length - 1;
      int start = interval.a;
      int stop = interval.b;
      if (start >= bufferStartIndex && stop <= bufferStopIndex) {
         int a = start - bufferStartIndex;
         int b = stop - bufferStartIndex;
         StringBuilder buf = new StringBuilder();

         for(int i = a; i <= b; ++i) {
            Token t = this.tokens[i];
            buf.append(t.getText());
         }

         return buf.toString();
      } else {
         throw new UnsupportedOperationException("interval " + interval + " not in token buffer window: " + bufferStartIndex + ".." + bufferStopIndex);
      }
   }

   protected final int getBufferStartIndex() {
      return this.currentTokenIndex - this.p;
   }
}
