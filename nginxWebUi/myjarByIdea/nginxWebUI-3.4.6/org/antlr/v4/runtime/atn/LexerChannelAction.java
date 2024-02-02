package org.antlr.v4.runtime.atn;

import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.misc.MurmurHash;

public final class LexerChannelAction implements LexerAction {
   private final int channel;

   public LexerChannelAction(int channel) {
      this.channel = channel;
   }

   public int getChannel() {
      return this.channel;
   }

   public LexerActionType getActionType() {
      return LexerActionType.CHANNEL;
   }

   public boolean isPositionDependent() {
      return false;
   }

   public void execute(Lexer lexer) {
      lexer.setChannel(this.channel);
   }

   public int hashCode() {
      int hash = MurmurHash.initialize();
      hash = MurmurHash.update(hash, this.getActionType().ordinal());
      hash = MurmurHash.update(hash, this.channel);
      return MurmurHash.finish(hash, 2);
   }

   public boolean equals(Object obj) {
      if (obj == this) {
         return true;
      } else if (!(obj instanceof LexerChannelAction)) {
         return false;
      } else {
         return this.channel == ((LexerChannelAction)obj).channel;
      }
   }

   public String toString() {
      return String.format("channel(%d)", this.channel);
   }
}
