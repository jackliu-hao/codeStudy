package org.antlr.v4.runtime;

public class CommonTokenStream extends BufferedTokenStream {
   protected int channel;

   public CommonTokenStream(TokenSource tokenSource) {
      super(tokenSource);
      this.channel = 0;
   }

   public CommonTokenStream(TokenSource tokenSource, int channel) {
      this(tokenSource);
      this.channel = channel;
   }

   protected int adjustSeekIndex(int i) {
      return this.nextTokenOnChannel(i, this.channel);
   }

   protected Token LB(int k) {
      if (k != 0 && this.p - k >= 0) {
         int i = this.p;

         for(int n = 1; n <= k; ++n) {
            i = this.previousTokenOnChannel(i - 1, this.channel);
         }

         return i < 0 ? null : (Token)this.tokens.get(i);
      } else {
         return null;
      }
   }

   public Token LT(int k) {
      this.lazyInit();
      if (k == 0) {
         return null;
      } else if (k < 0) {
         return this.LB(-k);
      } else {
         int i = this.p;

         for(int n = 1; n < k; ++n) {
            if (this.sync(i + 1)) {
               i = this.nextTokenOnChannel(i + 1, this.channel);
            }
         }

         return (Token)this.tokens.get(i);
      }
   }

   public int getNumberOfOnChannelTokens() {
      int n = 0;
      this.fill();

      for(int i = 0; i < this.tokens.size(); ++i) {
         Token t = (Token)this.tokens.get(i);
         if (t.getChannel() == this.channel) {
            ++n;
         }

         if (t.getType() == -1) {
            break;
         }
      }

      return n;
   }
}
