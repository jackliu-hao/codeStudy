package org.antlr.v4.runtime;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.antlr.v4.runtime.misc.Interval;

public class BufferedTokenStream implements TokenStream {
   protected TokenSource tokenSource;
   protected List<Token> tokens = new ArrayList(100);
   protected int p = -1;
   protected boolean fetchedEOF;

   public BufferedTokenStream(TokenSource tokenSource) {
      if (tokenSource == null) {
         throw new NullPointerException("tokenSource cannot be null");
      } else {
         this.tokenSource = tokenSource;
      }
   }

   public TokenSource getTokenSource() {
      return this.tokenSource;
   }

   public int index() {
      return this.p;
   }

   public int mark() {
      return 0;
   }

   public void release(int marker) {
   }

   public void reset() {
      this.seek(0);
   }

   public void seek(int index) {
      this.lazyInit();
      this.p = this.adjustSeekIndex(index);
   }

   public int size() {
      return this.tokens.size();
   }

   public void consume() {
      boolean skipEofCheck;
      if (this.p >= 0) {
         if (this.fetchedEOF) {
            skipEofCheck = this.p < this.tokens.size() - 1;
         } else {
            skipEofCheck = this.p < this.tokens.size();
         }
      } else {
         skipEofCheck = false;
      }

      if (!skipEofCheck && this.LA(1) == -1) {
         throw new IllegalStateException("cannot consume EOF");
      } else {
         if (this.sync(this.p + 1)) {
            this.p = this.adjustSeekIndex(this.p + 1);
         }

      }
   }

   protected boolean sync(int i) {
      assert i >= 0;

      int n = i - this.tokens.size() + 1;
      if (n > 0) {
         int fetched = this.fetch(n);
         return fetched >= n;
      } else {
         return true;
      }
   }

   protected int fetch(int n) {
      if (this.fetchedEOF) {
         return 0;
      } else {
         for(int i = 0; i < n; ++i) {
            Token t = this.tokenSource.nextToken();
            if (t instanceof WritableToken) {
               ((WritableToken)t).setTokenIndex(this.tokens.size());
            }

            this.tokens.add(t);
            if (t.getType() == -1) {
               this.fetchedEOF = true;
               return i + 1;
            }
         }

         return n;
      }
   }

   public Token get(int i) {
      if (i >= 0 && i < this.tokens.size()) {
         return (Token)this.tokens.get(i);
      } else {
         throw new IndexOutOfBoundsException("token index " + i + " out of range 0.." + (this.tokens.size() - 1));
      }
   }

   public List<Token> get(int start, int stop) {
      if (start >= 0 && stop >= 0) {
         this.lazyInit();
         List<Token> subset = new ArrayList();
         if (stop >= this.tokens.size()) {
            stop = this.tokens.size() - 1;
         }

         for(int i = start; i <= stop; ++i) {
            Token t = (Token)this.tokens.get(i);
            if (t.getType() == -1) {
               break;
            }

            subset.add(t);
         }

         return subset;
      } else {
         return null;
      }
   }

   public int LA(int i) {
      return this.LT(i).getType();
   }

   protected Token LB(int k) {
      return this.p - k < 0 ? null : (Token)this.tokens.get(this.p - k);
   }

   public Token LT(int k) {
      this.lazyInit();
      if (k == 0) {
         return null;
      } else if (k < 0) {
         return this.LB(-k);
      } else {
         int i = this.p + k - 1;
         this.sync(i);
         return i >= this.tokens.size() ? (Token)this.tokens.get(this.tokens.size() - 1) : (Token)this.tokens.get(i);
      }
   }

   protected int adjustSeekIndex(int i) {
      return i;
   }

   protected final void lazyInit() {
      if (this.p == -1) {
         this.setup();
      }

   }

   protected void setup() {
      this.sync(0);
      this.p = this.adjustSeekIndex(0);
   }

   public void setTokenSource(TokenSource tokenSource) {
      this.tokenSource = tokenSource;
      this.tokens.clear();
      this.p = -1;
   }

   public List<Token> getTokens() {
      return this.tokens;
   }

   public List<Token> getTokens(int start, int stop) {
      return this.getTokens(start, stop, (Set)null);
   }

   public List<Token> getTokens(int start, int stop, Set<Integer> types) {
      this.lazyInit();
      if (start >= 0 && stop < this.tokens.size() && stop >= 0 && start < this.tokens.size()) {
         if (start > stop) {
            return null;
         } else {
            List<Token> filteredTokens = new ArrayList();

            for(int i = start; i <= stop; ++i) {
               Token t = (Token)this.tokens.get(i);
               if (types == null || types.contains(t.getType())) {
                  filteredTokens.add(t);
               }
            }

            if (filteredTokens.isEmpty()) {
               filteredTokens = null;
            }

            return filteredTokens;
         }
      } else {
         throw new IndexOutOfBoundsException("start " + start + " or stop " + stop + " not in 0.." + (this.tokens.size() - 1));
      }
   }

   public List<Token> getTokens(int start, int stop, int ttype) {
      HashSet<Integer> s = new HashSet(ttype);
      s.add(ttype);
      return this.getTokens(start, stop, s);
   }

   protected int nextTokenOnChannel(int i, int channel) {
      this.sync(i);
      if (i >= this.size()) {
         return this.size() - 1;
      } else {
         for(Token token = (Token)this.tokens.get(i); token.getChannel() != channel; token = (Token)this.tokens.get(i)) {
            if (token.getType() == -1) {
               return i;
            }

            ++i;
            this.sync(i);
         }

         return i;
      }
   }

   protected int previousTokenOnChannel(int i, int channel) {
      this.sync(i);
      if (i >= this.size()) {
         return this.size() - 1;
      } else {
         while(i >= 0) {
            Token token = (Token)this.tokens.get(i);
            if (token.getType() == -1 || token.getChannel() == channel) {
               return i;
            }

            --i;
         }

         return i;
      }
   }

   public List<Token> getHiddenTokensToRight(int tokenIndex, int channel) {
      this.lazyInit();
      if (tokenIndex >= 0 && tokenIndex < this.tokens.size()) {
         int nextOnChannel = this.nextTokenOnChannel(tokenIndex + 1, 0);
         int from = tokenIndex + 1;
         int to;
         if (nextOnChannel == -1) {
            to = this.size() - 1;
         } else {
            to = nextOnChannel;
         }

         return this.filterForChannel(from, to, channel);
      } else {
         throw new IndexOutOfBoundsException(tokenIndex + " not in 0.." + (this.tokens.size() - 1));
      }
   }

   public List<Token> getHiddenTokensToRight(int tokenIndex) {
      return this.getHiddenTokensToRight(tokenIndex, -1);
   }

   public List<Token> getHiddenTokensToLeft(int tokenIndex, int channel) {
      this.lazyInit();
      if (tokenIndex >= 0 && tokenIndex < this.tokens.size()) {
         if (tokenIndex == 0) {
            return null;
         } else {
            int prevOnChannel = this.previousTokenOnChannel(tokenIndex - 1, 0);
            if (prevOnChannel == tokenIndex - 1) {
               return null;
            } else {
               int from = prevOnChannel + 1;
               int to = tokenIndex - 1;
               return this.filterForChannel(from, to, channel);
            }
         }
      } else {
         throw new IndexOutOfBoundsException(tokenIndex + " not in 0.." + (this.tokens.size() - 1));
      }
   }

   public List<Token> getHiddenTokensToLeft(int tokenIndex) {
      return this.getHiddenTokensToLeft(tokenIndex, -1);
   }

   protected List<Token> filterForChannel(int from, int to, int channel) {
      List<Token> hidden = new ArrayList();

      for(int i = from; i <= to; ++i) {
         Token t = (Token)this.tokens.get(i);
         if (channel == -1) {
            if (t.getChannel() != 0) {
               hidden.add(t);
            }
         } else if (t.getChannel() == channel) {
            hidden.add(t);
         }
      }

      if (hidden.size() == 0) {
         return null;
      } else {
         return hidden;
      }
   }

   public String getSourceName() {
      return this.tokenSource.getSourceName();
   }

   public String getText() {
      this.lazyInit();
      this.fill();
      return this.getText(Interval.of(0, this.size() - 1));
   }

   public String getText(Interval interval) {
      int start = interval.a;
      int stop = interval.b;
      if (start >= 0 && stop >= 0) {
         this.lazyInit();
         if (stop >= this.tokens.size()) {
            stop = this.tokens.size() - 1;
         }

         StringBuilder buf = new StringBuilder();

         for(int i = start; i <= stop; ++i) {
            Token t = (Token)this.tokens.get(i);
            if (t.getType() == -1) {
               break;
            }

            buf.append(t.getText());
         }

         return buf.toString();
      } else {
         return "";
      }
   }

   public String getText(RuleContext ctx) {
      return this.getText(ctx.getSourceInterval());
   }

   public String getText(Token start, Token stop) {
      return start != null && stop != null ? this.getText(Interval.of(start.getTokenIndex(), stop.getTokenIndex())) : "";
   }

   public void fill() {
      this.lazyInit();
      int blockSize = true;

      int fetched;
      do {
         fetched = this.fetch(1000);
      } while(fetched >= 1000);

   }
}
