package org.antlr.v4.runtime;

public class InputMismatchException extends RecognitionException {
   public InputMismatchException(Parser recognizer) {
      super(recognizer, recognizer.getInputStream(), recognizer._ctx);
      this.setOffendingToken(recognizer.getCurrentToken());
   }
}
