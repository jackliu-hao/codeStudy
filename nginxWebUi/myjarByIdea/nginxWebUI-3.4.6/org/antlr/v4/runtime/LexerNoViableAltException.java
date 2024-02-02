package org.antlr.v4.runtime;

import java.util.Locale;
import org.antlr.v4.runtime.atn.ATNConfigSet;
import org.antlr.v4.runtime.misc.Interval;
import org.antlr.v4.runtime.misc.Utils;

public class LexerNoViableAltException extends RecognitionException {
   private final int startIndex;
   private final ATNConfigSet deadEndConfigs;

   public LexerNoViableAltException(Lexer lexer, CharStream input, int startIndex, ATNConfigSet deadEndConfigs) {
      super(lexer, input, (ParserRuleContext)null);
      this.startIndex = startIndex;
      this.deadEndConfigs = deadEndConfigs;
   }

   public int getStartIndex() {
      return this.startIndex;
   }

   public ATNConfigSet getDeadEndConfigs() {
      return this.deadEndConfigs;
   }

   public CharStream getInputStream() {
      return (CharStream)super.getInputStream();
   }

   public String toString() {
      String symbol = "";
      if (this.startIndex >= 0 && this.startIndex < this.getInputStream().size()) {
         symbol = this.getInputStream().getText(Interval.of(this.startIndex, this.startIndex));
         symbol = Utils.escapeWhitespace(symbol, false);
      }

      return String.format(Locale.getDefault(), "%s('%s')", LexerNoViableAltException.class.getSimpleName(), symbol);
   }
}
