package org.antlr.v4.runtime;

import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.List;
import org.antlr.v4.runtime.atn.LexerATNSimulator;
import org.antlr.v4.runtime.misc.IntegerStack;
import org.antlr.v4.runtime.misc.Interval;
import org.antlr.v4.runtime.misc.Pair;

public abstract class Lexer extends Recognizer<Integer, LexerATNSimulator> implements TokenSource {
   public static final int DEFAULT_MODE = 0;
   public static final int MORE = -2;
   public static final int SKIP = -3;
   public static final int DEFAULT_TOKEN_CHANNEL = 0;
   public static final int HIDDEN = 1;
   public static final int MIN_CHAR_VALUE = 0;
   public static final int MAX_CHAR_VALUE = 65534;
   public CharStream _input;
   protected Pair<TokenSource, CharStream> _tokenFactorySourcePair;
   protected TokenFactory<?> _factory;
   public Token _token;
   public int _tokenStartCharIndex;
   public int _tokenStartLine;
   public int _tokenStartCharPositionInLine;
   public boolean _hitEOF;
   public int _channel;
   public int _type;
   public final IntegerStack _modeStack;
   public int _mode;
   public String _text;

   public Lexer() {
      this._factory = CommonTokenFactory.DEFAULT;
      this._tokenStartCharIndex = -1;
      this._modeStack = new IntegerStack();
      this._mode = 0;
   }

   public Lexer(CharStream input) {
      this._factory = CommonTokenFactory.DEFAULT;
      this._tokenStartCharIndex = -1;
      this._modeStack = new IntegerStack();
      this._mode = 0;
      this._input = input;
      this._tokenFactorySourcePair = new Pair(this, input);
   }

   public void reset() {
      if (this._input != null) {
         this._input.seek(0);
      }

      this._token = null;
      this._type = 0;
      this._channel = 0;
      this._tokenStartCharIndex = -1;
      this._tokenStartCharPositionInLine = -1;
      this._tokenStartLine = -1;
      this._text = null;
      this._hitEOF = false;
      this._mode = 0;
      this._modeStack.clear();
      ((LexerATNSimulator)this.getInterpreter()).reset();
   }

   public Token nextToken() {
      if (this._input == null) {
         throw new IllegalStateException("nextToken requires a non-null input stream.");
      } else {
         int tokenStartMarker = this._input.mark();

         try {
            Token var9;
            label110:
            while(!this._hitEOF) {
               this._token = null;
               this._channel = 0;
               this._tokenStartCharIndex = this._input.index();
               this._tokenStartCharPositionInLine = ((LexerATNSimulator)this.getInterpreter()).getCharPositionInLine();
               this._tokenStartLine = ((LexerATNSimulator)this.getInterpreter()).getLine();
               this._text = null;

               do {
                  this._type = 0;

                  int ttype;
                  try {
                     ttype = ((LexerATNSimulator)this.getInterpreter()).match(this._input, this._mode);
                  } catch (LexerNoViableAltException var7) {
                     this.notifyListeners(var7);
                     this.recover(var7);
                     ttype = -3;
                  }

                  if (this._input.LA(1) == -1) {
                     this._hitEOF = true;
                  }

                  if (this._type == 0) {
                     this._type = ttype;
                  }

                  if (this._type == -3) {
                     continue label110;
                  }
               } while(this._type == -2);

               if (this._token == null) {
                  this.emit();
               }

               var9 = this._token;
               return var9;
            }

            this.emitEOF();
            var9 = this._token;
            return var9;
         } finally {
            this._input.release(tokenStartMarker);
         }
      }
   }

   public void skip() {
      this._type = -3;
   }

   public void more() {
      this._type = -2;
   }

   public void mode(int m) {
      this._mode = m;
   }

   public void pushMode(int m) {
      this._modeStack.push(this._mode);
      this.mode(m);
   }

   public int popMode() {
      if (this._modeStack.isEmpty()) {
         throw new EmptyStackException();
      } else {
         this.mode(this._modeStack.pop());
         return this._mode;
      }
   }

   public void setTokenFactory(TokenFactory<?> factory) {
      this._factory = factory;
   }

   public TokenFactory<? extends Token> getTokenFactory() {
      return this._factory;
   }

   public void setInputStream(IntStream input) {
      this._input = null;
      this._tokenFactorySourcePair = new Pair(this, this._input);
      this.reset();
      this._input = (CharStream)input;
      this._tokenFactorySourcePair = new Pair(this, this._input);
   }

   public String getSourceName() {
      return this._input.getSourceName();
   }

   public CharStream getInputStream() {
      return this._input;
   }

   public void emit(Token token) {
      this._token = token;
   }

   public Token emit() {
      Token t = this._factory.create(this._tokenFactorySourcePair, this._type, this._text, this._channel, this._tokenStartCharIndex, this.getCharIndex() - 1, this._tokenStartLine, this._tokenStartCharPositionInLine);
      this.emit(t);
      return t;
   }

   public Token emitEOF() {
      int cpos = this.getCharPositionInLine();
      int line = this.getLine();
      Token eof = this._factory.create(this._tokenFactorySourcePair, -1, (String)null, 0, this._input.index(), this._input.index() - 1, line, cpos);
      this.emit(eof);
      return eof;
   }

   public int getLine() {
      return ((LexerATNSimulator)this.getInterpreter()).getLine();
   }

   public int getCharPositionInLine() {
      return ((LexerATNSimulator)this.getInterpreter()).getCharPositionInLine();
   }

   public void setLine(int line) {
      ((LexerATNSimulator)this.getInterpreter()).setLine(line);
   }

   public void setCharPositionInLine(int charPositionInLine) {
      ((LexerATNSimulator)this.getInterpreter()).setCharPositionInLine(charPositionInLine);
   }

   public int getCharIndex() {
      return this._input.index();
   }

   public String getText() {
      return this._text != null ? this._text : ((LexerATNSimulator)this.getInterpreter()).getText(this._input);
   }

   public void setText(String text) {
      this._text = text;
   }

   public Token getToken() {
      return this._token;
   }

   public void setToken(Token _token) {
      this._token = _token;
   }

   public void setType(int ttype) {
      this._type = ttype;
   }

   public int getType() {
      return this._type;
   }

   public void setChannel(int channel) {
      this._channel = channel;
   }

   public int getChannel() {
      return this._channel;
   }

   public String[] getModeNames() {
      return null;
   }

   /** @deprecated */
   @Deprecated
   public String[] getTokenNames() {
      return null;
   }

   public List<? extends Token> getAllTokens() {
      List<Token> tokens = new ArrayList();

      for(Token t = this.nextToken(); t.getType() != -1; t = this.nextToken()) {
         tokens.add(t);
      }

      return tokens;
   }

   public void recover(LexerNoViableAltException e) {
      if (this._input.LA(1) != -1) {
         ((LexerATNSimulator)this.getInterpreter()).consume(this._input);
      }

   }

   public void notifyListeners(LexerNoViableAltException e) {
      String text = this._input.getText(Interval.of(this._tokenStartCharIndex, this._input.index()));
      String msg = "token recognition error at: '" + this.getErrorDisplay(text) + "'";
      ANTLRErrorListener listener = this.getErrorListenerDispatch();
      listener.syntaxError(this, (Object)null, this._tokenStartLine, this._tokenStartCharPositionInLine, msg, e);
   }

   public String getErrorDisplay(String s) {
      StringBuilder buf = new StringBuilder();
      char[] arr$ = s.toCharArray();
      int len$ = arr$.length;

      for(int i$ = 0; i$ < len$; ++i$) {
         char c = arr$[i$];
         buf.append(this.getErrorDisplay(c));
      }

      return buf.toString();
   }

   public String getErrorDisplay(int c) {
      String s = String.valueOf((char)c);
      switch (c) {
         case -1:
            s = "<EOF>";
            break;
         case 9:
            s = "\\t";
            break;
         case 10:
            s = "\\n";
            break;
         case 13:
            s = "\\r";
      }

      return s;
   }

   public String getCharErrorDisplay(int c) {
      String s = this.getErrorDisplay(c);
      return "'" + s + "'";
   }

   public void recover(RecognitionException re) {
      this._input.consume();
   }
}
