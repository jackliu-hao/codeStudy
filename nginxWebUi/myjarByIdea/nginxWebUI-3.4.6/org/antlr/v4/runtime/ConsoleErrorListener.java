package org.antlr.v4.runtime;

public class ConsoleErrorListener extends BaseErrorListener {
   public static final ConsoleErrorListener INSTANCE = new ConsoleErrorListener();

   public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
      System.err.println("line " + line + ":" + charPositionInLine + " " + msg);
   }
}
