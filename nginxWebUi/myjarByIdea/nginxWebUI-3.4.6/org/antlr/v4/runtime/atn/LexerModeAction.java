package org.antlr.v4.runtime.atn;

import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.misc.MurmurHash;

public final class LexerModeAction implements LexerAction {
   private final int mode;

   public LexerModeAction(int mode) {
      this.mode = mode;
   }

   public int getMode() {
      return this.mode;
   }

   public LexerActionType getActionType() {
      return LexerActionType.MODE;
   }

   public boolean isPositionDependent() {
      return false;
   }

   public void execute(Lexer lexer) {
      lexer.mode(this.mode);
   }

   public int hashCode() {
      int hash = MurmurHash.initialize();
      hash = MurmurHash.update(hash, this.getActionType().ordinal());
      hash = MurmurHash.update(hash, this.mode);
      return MurmurHash.finish(hash, 2);
   }

   public boolean equals(Object obj) {
      if (obj == this) {
         return true;
      } else if (!(obj instanceof LexerModeAction)) {
         return false;
      } else {
         return this.mode == ((LexerModeAction)obj).mode;
      }
   }

   public String toString() {
      return String.format("mode(%d)", this.mode);
   }
}
