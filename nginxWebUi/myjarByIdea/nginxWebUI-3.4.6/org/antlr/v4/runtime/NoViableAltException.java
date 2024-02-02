package org.antlr.v4.runtime;

import org.antlr.v4.runtime.atn.ATNConfigSet;

public class NoViableAltException extends RecognitionException {
   private final ATNConfigSet deadEndConfigs;
   private final Token startToken;

   public NoViableAltException(Parser recognizer) {
      this(recognizer, recognizer.getInputStream(), recognizer.getCurrentToken(), recognizer.getCurrentToken(), (ATNConfigSet)null, recognizer._ctx);
   }

   public NoViableAltException(Parser recognizer, TokenStream input, Token startToken, Token offendingToken, ATNConfigSet deadEndConfigs, ParserRuleContext ctx) {
      super(recognizer, input, ctx);
      this.deadEndConfigs = deadEndConfigs;
      this.startToken = startToken;
      this.setOffendingToken(offendingToken);
   }

   public Token getStartToken() {
      return this.startToken;
   }

   public ATNConfigSet getDeadEndConfigs() {
      return this.deadEndConfigs;
   }
}
