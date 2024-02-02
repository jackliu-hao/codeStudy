package ch.qos.logback.core.subst;

import ch.qos.logback.core.spi.ScanException;
import java.util.ArrayList;
import java.util.List;

public class Tokenizer {
   final String pattern;
   final int patternLength;
   TokenizerState state;
   int pointer;

   public Tokenizer(String pattern) {
      this.state = Tokenizer.TokenizerState.LITERAL_STATE;
      this.pointer = 0;
      this.pattern = pattern;
      this.patternLength = pattern.length();
   }

   List<Token> tokenize() throws ScanException {
      List<Token> tokenList = new ArrayList();
      StringBuilder buf = new StringBuilder();

      while(this.pointer < this.patternLength) {
         char c = this.pattern.charAt(this.pointer);
         ++this.pointer;
         switch (this.state) {
            case LITERAL_STATE:
               this.handleLiteralState(c, tokenList, buf);
               break;
            case START_STATE:
               this.handleStartState(c, tokenList, buf);
               break;
            case DEFAULT_VAL_STATE:
               this.handleDefaultValueState(c, tokenList, buf);
         }
      }

      switch (this.state) {
         case LITERAL_STATE:
            this.addLiteralToken(tokenList, buf);
            break;
         case START_STATE:
            buf.append('$');
            this.addLiteralToken(tokenList, buf);
            break;
         case DEFAULT_VAL_STATE:
            buf.append(':');
            this.addLiteralToken(tokenList, buf);
      }

      return tokenList;
   }

   private void handleDefaultValueState(char c, List<Token> tokenList, StringBuilder stringBuilder) {
      switch (c) {
         case '$':
            stringBuilder.append(':');
            this.addLiteralToken(tokenList, stringBuilder);
            stringBuilder.setLength(0);
            this.state = Tokenizer.TokenizerState.START_STATE;
            break;
         case '-':
            tokenList.add(Token.DEFAULT_SEP_TOKEN);
            this.state = Tokenizer.TokenizerState.LITERAL_STATE;
            break;
         default:
            stringBuilder.append(':').append(c);
            this.state = Tokenizer.TokenizerState.LITERAL_STATE;
      }

   }

   private void handleStartState(char c, List<Token> tokenList, StringBuilder stringBuilder) {
      if (c == '{') {
         tokenList.add(Token.START_TOKEN);
      } else {
         stringBuilder.append('$').append(c);
      }

      this.state = Tokenizer.TokenizerState.LITERAL_STATE;
   }

   private void handleLiteralState(char c, List<Token> tokenList, StringBuilder stringBuilder) {
      if (c == '$') {
         this.addLiteralToken(tokenList, stringBuilder);
         stringBuilder.setLength(0);
         this.state = Tokenizer.TokenizerState.START_STATE;
      } else if (c == ':') {
         this.addLiteralToken(tokenList, stringBuilder);
         stringBuilder.setLength(0);
         this.state = Tokenizer.TokenizerState.DEFAULT_VAL_STATE;
      } else if (c == '{') {
         this.addLiteralToken(tokenList, stringBuilder);
         tokenList.add(Token.CURLY_LEFT_TOKEN);
         stringBuilder.setLength(0);
      } else if (c == '}') {
         this.addLiteralToken(tokenList, stringBuilder);
         tokenList.add(Token.CURLY_RIGHT_TOKEN);
         stringBuilder.setLength(0);
      } else {
         stringBuilder.append(c);
      }

   }

   private void addLiteralToken(List<Token> tokenList, StringBuilder stringBuilder) {
      if (stringBuilder.length() != 0) {
         tokenList.add(new Token(Token.Type.LITERAL, stringBuilder.toString()));
      }
   }

   static enum TokenizerState {
      LITERAL_STATE,
      START_STATE,
      DEFAULT_VAL_STATE;
   }
}
