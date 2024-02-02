package org.antlr.v4.runtime.atn;

import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.misc.MurmurHash;

public class LexerTypeAction implements LexerAction {
   private final int type;

   public LexerTypeAction(int type) {
      this.type = type;
   }

   public int getType() {
      return this.type;
   }

   public LexerActionType getActionType() {
      return LexerActionType.TYPE;
   }

   public boolean isPositionDependent() {
      return false;
   }

   public void execute(Lexer lexer) {
      lexer.setType(this.type);
   }

   public int hashCode() {
      int hash = MurmurHash.initialize();
      hash = MurmurHash.update(hash, this.getActionType().ordinal());
      hash = MurmurHash.update(hash, this.type);
      return MurmurHash.finish(hash, 2);
   }

   public boolean equals(Object obj) {
      if (obj == this) {
         return true;
      } else if (!(obj instanceof LexerTypeAction)) {
         return false;
      } else {
         return this.type == ((LexerTypeAction)obj).type;
      }
   }

   public String toString() {
      return String.format("type(%d)", this.type);
   }
}
