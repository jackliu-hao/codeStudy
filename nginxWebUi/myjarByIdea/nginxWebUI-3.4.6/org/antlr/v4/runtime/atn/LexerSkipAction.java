package org.antlr.v4.runtime.atn;

import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.misc.MurmurHash;

public final class LexerSkipAction implements LexerAction {
   public static final LexerSkipAction INSTANCE = new LexerSkipAction();

   private LexerSkipAction() {
   }

   public LexerActionType getActionType() {
      return LexerActionType.SKIP;
   }

   public boolean isPositionDependent() {
      return false;
   }

   public void execute(Lexer lexer) {
      lexer.skip();
   }

   public int hashCode() {
      int hash = MurmurHash.initialize();
      hash = MurmurHash.update(hash, this.getActionType().ordinal());
      return MurmurHash.finish(hash, 1);
   }

   public boolean equals(Object obj) {
      return obj == this;
   }

   public String toString() {
      return "skip";
   }
}
