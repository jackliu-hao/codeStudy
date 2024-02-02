package org.antlr.v4.runtime.atn;

import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.misc.MurmurHash;

public final class LexerPopModeAction implements LexerAction {
   public static final LexerPopModeAction INSTANCE = new LexerPopModeAction();

   private LexerPopModeAction() {
   }

   public LexerActionType getActionType() {
      return LexerActionType.POP_MODE;
   }

   public boolean isPositionDependent() {
      return false;
   }

   public void execute(Lexer lexer) {
      lexer.popMode();
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
      return "popMode";
   }
}
