package ch.qos.logback.core.pattern.parser;

import ch.qos.logback.core.pattern.util.IEscapeUtil;
import ch.qos.logback.core.pattern.util.RegularEscapeUtil;
import ch.qos.logback.core.pattern.util.RestrictedEscapeUtil;
import ch.qos.logback.core.spi.ScanException;
import java.util.ArrayList;
import java.util.List;

class TokenStream {
   final String pattern;
   final int patternLength;
   final IEscapeUtil escapeUtil;
   final IEscapeUtil optionEscapeUtil;
   TokenizerState state;
   int pointer;

   TokenStream(String pattern) {
      this(pattern, new RegularEscapeUtil());
   }

   TokenStream(String pattern, IEscapeUtil escapeUtil) {
      this.optionEscapeUtil = new RestrictedEscapeUtil();
      this.state = TokenStream.TokenizerState.LITERAL_STATE;
      this.pointer = 0;
      if (pattern != null && pattern.length() != 0) {
         this.pattern = pattern;
         this.patternLength = pattern.length();
         this.escapeUtil = escapeUtil;
      } else {
         throw new IllegalArgumentException("null or empty pattern string not allowed");
      }
   }

   List tokenize() throws ScanException {
      List<Token> tokenList = new ArrayList();
      StringBuffer buf = new StringBuffer();

      while(this.pointer < this.patternLength) {
         char c = this.pattern.charAt(this.pointer);
         ++this.pointer;
         switch (this.state) {
            case LITERAL_STATE:
               this.handleLiteralState(c, tokenList, buf);
               break;
            case FORMAT_MODIFIER_STATE:
               this.handleFormatModifierState(c, tokenList, buf);
               break;
            case OPTION_STATE:
               this.processOption(c, tokenList, buf);
               break;
            case KEYWORD_STATE:
               this.handleKeywordState(c, tokenList, buf);
               break;
            case RIGHT_PARENTHESIS_STATE:
               this.handleRightParenthesisState(c, tokenList, buf);
         }
      }

      switch (this.state) {
         case LITERAL_STATE:
            this.addValuedToken(1000, buf, tokenList);
            break;
         case FORMAT_MODIFIER_STATE:
         case OPTION_STATE:
            throw new ScanException("Unexpected end of pattern string");
         case KEYWORD_STATE:
            tokenList.add(new Token(1004, buf.toString()));
            break;
         case RIGHT_PARENTHESIS_STATE:
            tokenList.add(Token.RIGHT_PARENTHESIS_TOKEN);
      }

      return tokenList;
   }

   private void handleRightParenthesisState(char c, List<Token> tokenList, StringBuffer buf) {
      tokenList.add(Token.RIGHT_PARENTHESIS_TOKEN);
      switch (c) {
         case ')':
            break;
         case '\\':
            this.escape("%{}", buf);
            this.state = TokenStream.TokenizerState.LITERAL_STATE;
            break;
         case '{':
            this.state = TokenStream.TokenizerState.OPTION_STATE;
            break;
         default:
            buf.append(c);
            this.state = TokenStream.TokenizerState.LITERAL_STATE;
      }

   }

   private void processOption(char c, List<Token> tokenList, StringBuffer buf) throws ScanException {
      OptionTokenizer ot = new OptionTokenizer(this);
      ot.tokenize(c, tokenList);
   }

   private void handleFormatModifierState(char c, List<Token> tokenList, StringBuffer buf) {
      if (c == '(') {
         this.addValuedToken(1002, buf, tokenList);
         tokenList.add(Token.BARE_COMPOSITE_KEYWORD_TOKEN);
         this.state = TokenStream.TokenizerState.LITERAL_STATE;
      } else if (Character.isJavaIdentifierStart(c)) {
         this.addValuedToken(1002, buf, tokenList);
         this.state = TokenStream.TokenizerState.KEYWORD_STATE;
         buf.append(c);
      } else {
         buf.append(c);
      }

   }

   private void handleLiteralState(char c, List<Token> tokenList, StringBuffer buf) {
      switch (c) {
         case '%':
            this.addValuedToken(1000, buf, tokenList);
            tokenList.add(Token.PERCENT_TOKEN);
            this.state = TokenStream.TokenizerState.FORMAT_MODIFIER_STATE;
            break;
         case ')':
            this.addValuedToken(1000, buf, tokenList);
            this.state = TokenStream.TokenizerState.RIGHT_PARENTHESIS_STATE;
            break;
         case '\\':
            this.escape("%()", buf);
            break;
         default:
            buf.append(c);
      }

   }

   private void handleKeywordState(char c, List<Token> tokenList, StringBuffer buf) {
      if (Character.isJavaIdentifierPart(c)) {
         buf.append(c);
      } else if (c == '{') {
         this.addValuedToken(1004, buf, tokenList);
         this.state = TokenStream.TokenizerState.OPTION_STATE;
      } else if (c == '(') {
         this.addValuedToken(1005, buf, tokenList);
         this.state = TokenStream.TokenizerState.LITERAL_STATE;
      } else if (c == '%') {
         this.addValuedToken(1004, buf, tokenList);
         tokenList.add(Token.PERCENT_TOKEN);
         this.state = TokenStream.TokenizerState.FORMAT_MODIFIER_STATE;
      } else if (c == ')') {
         this.addValuedToken(1004, buf, tokenList);
         this.state = TokenStream.TokenizerState.RIGHT_PARENTHESIS_STATE;
      } else {
         this.addValuedToken(1004, buf, tokenList);
         if (c == '\\') {
            if (this.pointer < this.patternLength) {
               char next = this.pattern.charAt(this.pointer++);
               this.escapeUtil.escape("%()", buf, next, this.pointer);
            }
         } else {
            buf.append(c);
         }

         this.state = TokenStream.TokenizerState.LITERAL_STATE;
      }

   }

   void escape(String escapeChars, StringBuffer buf) {
      if (this.pointer < this.patternLength) {
         char next = this.pattern.charAt(this.pointer++);
         this.escapeUtil.escape(escapeChars, buf, next, this.pointer);
      }

   }

   void optionEscape(String escapeChars, StringBuffer buf) {
      if (this.pointer < this.patternLength) {
         char next = this.pattern.charAt(this.pointer++);
         this.optionEscapeUtil.escape(escapeChars, buf, next, this.pointer);
      }

   }

   private void addValuedToken(int type, StringBuffer buf, List<Token> tokenList) {
      if (buf.length() > 0) {
         tokenList.add(new Token(type, buf.toString()));
         buf.setLength(0);
      }

   }

   static enum TokenizerState {
      LITERAL_STATE,
      FORMAT_MODIFIER_STATE,
      KEYWORD_STATE,
      OPTION_STATE,
      RIGHT_PARENTHESIS_STATE;
   }
}
