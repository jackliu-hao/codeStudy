package org.antlr.v4.runtime;

import org.antlr.v4.runtime.misc.ParseCancellationException;

public class BailErrorStrategy extends DefaultErrorStrategy {
   public void recover(Parser recognizer, RecognitionException e) {
      for(ParserRuleContext context = recognizer.getContext(); context != null; context = context.getParent()) {
         context.exception = e;
      }

      throw new ParseCancellationException(e);
   }

   public Token recoverInline(Parser recognizer) throws RecognitionException {
      InputMismatchException e = new InputMismatchException(recognizer);

      for(ParserRuleContext context = recognizer.getContext(); context != null; context = context.getParent()) {
         context.exception = e;
      }

      throw new ParseCancellationException(e);
   }

   public void sync(Parser recognizer) {
   }
}
