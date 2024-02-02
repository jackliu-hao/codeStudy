/*     */ package org.antlr.v4.runtime;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.EmptyStackException;
/*     */ import java.util.List;
/*     */ import org.antlr.v4.runtime.atn.LexerATNSimulator;
/*     */ import org.antlr.v4.runtime.misc.IntegerStack;
/*     */ import org.antlr.v4.runtime.misc.Interval;
/*     */ import org.antlr.v4.runtime.misc.Pair;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class Lexer
/*     */   extends Recognizer<Integer, LexerATNSimulator>
/*     */   implements TokenSource
/*     */ {
/*     */   public static final int DEFAULT_MODE = 0;
/*     */   public static final int MORE = -2;
/*     */   public static final int SKIP = -3;
/*     */   public static final int DEFAULT_TOKEN_CHANNEL = 0;
/*     */   public static final int HIDDEN = 1;
/*     */   public static final int MIN_CHAR_VALUE = 0;
/*     */   public static final int MAX_CHAR_VALUE = 65534;
/*     */   public CharStream _input;
/*     */   protected Pair<TokenSource, CharStream> _tokenFactorySourcePair;
/*  62 */   protected TokenFactory<?> _factory = CommonTokenFactory.DEFAULT;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Token _token;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  78 */   public int _tokenStartCharIndex = -1;
/*     */ 
/*     */ 
/*     */   
/*     */   public int _tokenStartLine;
/*     */ 
/*     */   
/*     */   public int _tokenStartCharPositionInLine;
/*     */ 
/*     */   
/*     */   public boolean _hitEOF;
/*     */ 
/*     */   
/*     */   public int _channel;
/*     */ 
/*     */   
/*     */   public int _type;
/*     */ 
/*     */   
/*  97 */   public final IntegerStack _modeStack = new IntegerStack();
/*  98 */   public int _mode = 0;
/*     */ 
/*     */ 
/*     */   
/*     */   public String _text;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Lexer(CharStream input) {
/* 108 */     this._input = input;
/* 109 */     this._tokenFactorySourcePair = new Pair<TokenSource, CharStream>(this, input);
/*     */   }
/*     */ 
/*     */   
/*     */   public void reset() {
/* 114 */     if (this._input != null) {
/* 115 */       this._input.seek(0);
/*     */     }
/* 117 */     this._token = null;
/* 118 */     this._type = 0;
/* 119 */     this._channel = 0;
/* 120 */     this._tokenStartCharIndex = -1;
/* 121 */     this._tokenStartCharPositionInLine = -1;
/* 122 */     this._tokenStartLine = -1;
/* 123 */     this._text = null;
/*     */     
/* 125 */     this._hitEOF = false;
/* 126 */     this._mode = 0;
/* 127 */     this._modeStack.clear();
/*     */     
/* 129 */     getInterpreter().reset();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Token nextToken() {
/* 137 */     if (this._input == null) {
/* 138 */       throw new IllegalStateException("nextToken requires a non-null input stream.");
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 143 */     int tokenStartMarker = this._input.mark();
/*     */     
/*     */     try {
/*     */       label26: while (true) {
/* 147 */         if (this._hitEOF) {
/* 148 */           emitEOF();
/* 149 */           return this._token;
/*     */         } 
/*     */         
/* 152 */         this._token = null;
/* 153 */         this._channel = 0;
/* 154 */         this._tokenStartCharIndex = this._input.index();
/* 155 */         this._tokenStartCharPositionInLine = getInterpreter().getCharPositionInLine();
/* 156 */         this._tokenStartLine = getInterpreter().getLine();
/* 157 */         this._text = null; while (true) {
/*     */           byte b;
/* 159 */           this._type = 0;
/*     */ 
/*     */ 
/*     */ 
/*     */           
/*     */           try {
/* 165 */             b = getInterpreter().match(this._input, this._mode);
/*     */           }
/* 167 */           catch (LexerNoViableAltException e) {
/* 168 */             notifyListeners(e);
/* 169 */             recover(e);
/* 170 */             b = -3;
/*     */           } 
/* 172 */           if (this._input.LA(1) == -1) {
/* 173 */             this._hitEOF = true;
/*     */           }
/* 175 */           if (this._type == 0) this._type = b; 
/* 176 */           if (this._type == -3) {
/*     */             continue label26;
/*     */           }
/* 179 */           if (this._type != -2) {
/* 180 */             if (this._token == null) emit(); 
/* 181 */             return this._token;
/*     */           } 
/*     */         } 
/*     */         break;
/*     */       } 
/*     */     } finally {
/* 187 */       this._input.release(tokenStartMarker);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void skip() {
/* 198 */     this._type = -3;
/*     */   }
/*     */   
/*     */   public void more() {
/* 202 */     this._type = -2;
/*     */   }
/*     */   
/*     */   public void mode(int m) {
/* 206 */     this._mode = m;
/*     */   }
/*     */ 
/*     */   
/*     */   public void pushMode(int m) {
/* 211 */     this._modeStack.push(this._mode);
/* 212 */     mode(m);
/*     */   }
/*     */   
/*     */   public int popMode() {
/* 216 */     if (this._modeStack.isEmpty()) throw new EmptyStackException();
/*     */     
/* 218 */     mode(this._modeStack.pop());
/* 219 */     return this._mode;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setTokenFactory(TokenFactory<?> factory) {
/* 224 */     this._factory = factory;
/*     */   }
/*     */ 
/*     */   
/*     */   public TokenFactory<? extends Token> getTokenFactory() {
/* 229 */     return (TokenFactory)this._factory;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void setInputStream(IntStream input) {
/* 235 */     this._input = null;
/* 236 */     this._tokenFactorySourcePair = new Pair<TokenSource, CharStream>(this, this._input);
/* 237 */     reset();
/* 238 */     this._input = (CharStream)input;
/* 239 */     this._tokenFactorySourcePair = new Pair<TokenSource, CharStream>(this, this._input);
/*     */   }
/*     */ 
/*     */   
/*     */   public String getSourceName() {
/* 244 */     return this._input.getSourceName();
/*     */   }
/*     */ 
/*     */   
/*     */   public CharStream getInputStream() {
/* 249 */     return this._input;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void emit(Token token) {
/* 259 */     this._token = token;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Token emit() {
/* 269 */     Token t = (Token)this._factory.create(this._tokenFactorySourcePair, this._type, this._text, this._channel, this._tokenStartCharIndex, getCharIndex() - 1, this._tokenStartLine, this._tokenStartCharPositionInLine);
/*     */     
/* 271 */     emit(t);
/* 272 */     return t;
/*     */   }
/*     */   
/*     */   public Token emitEOF() {
/* 276 */     int cpos = getCharPositionInLine();
/* 277 */     int line = getLine();
/* 278 */     Token eof = (Token)this._factory.create(this._tokenFactorySourcePair, -1, null, 0, this._input.index(), this._input.index() - 1, line, cpos);
/*     */     
/* 280 */     emit(eof);
/* 281 */     return eof;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getLine() {
/* 286 */     return getInterpreter().getLine();
/*     */   }
/*     */ 
/*     */   
/*     */   public int getCharPositionInLine() {
/* 291 */     return getInterpreter().getCharPositionInLine();
/*     */   }
/*     */   
/*     */   public void setLine(int line) {
/* 295 */     getInterpreter().setLine(line);
/*     */   }
/*     */   
/*     */   public void setCharPositionInLine(int charPositionInLine) {
/* 299 */     getInterpreter().setCharPositionInLine(charPositionInLine);
/*     */   }
/*     */ 
/*     */   
/*     */   public int getCharIndex() {
/* 304 */     return this._input.index();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getText() {
/* 311 */     if (this._text != null) {
/* 312 */       return this._text;
/*     */     }
/* 314 */     return getInterpreter().getText(this._input);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setText(String text) {
/* 321 */     this._text = text;
/*     */   }
/*     */   
/*     */   public Token getToken() {
/* 325 */     return this._token;
/*     */   }
/*     */   public void setToken(Token _token) {
/* 328 */     this._token = _token;
/*     */   }
/*     */   
/*     */   public void setType(int ttype) {
/* 332 */     this._type = ttype;
/*     */   }
/*     */   
/*     */   public int getType() {
/* 336 */     return this._type;
/*     */   }
/*     */   
/*     */   public void setChannel(int channel) {
/* 340 */     this._channel = channel;
/*     */   }
/*     */   
/*     */   public int getChannel() {
/* 344 */     return this._channel;
/*     */   }
/*     */   
/*     */   public String[] getModeNames() {
/* 348 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public String[] getTokenNames() {
/* 358 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public List<? extends Token> getAllTokens() {
/* 365 */     List<Token> tokens = new ArrayList<Token>();
/* 366 */     Token t = nextToken();
/* 367 */     while (t.getType() != -1) {
/* 368 */       tokens.add(t);
/* 369 */       t = nextToken();
/*     */     } 
/* 371 */     return tokens;
/*     */   }
/*     */   
/*     */   public void recover(LexerNoViableAltException e) {
/* 375 */     if (this._input.LA(1) != -1)
/*     */     {
/* 377 */       getInterpreter().consume(this._input);
/*     */     }
/*     */   }
/*     */   
/*     */   public void notifyListeners(LexerNoViableAltException e) {
/* 382 */     String text = this._input.getText(Interval.of(this._tokenStartCharIndex, this._input.index()));
/* 383 */     String msg = "token recognition error at: '" + getErrorDisplay(text) + "'";
/*     */     
/* 385 */     ANTLRErrorListener listener = getErrorListenerDispatch();
/* 386 */     listener.syntaxError(this, null, this._tokenStartLine, this._tokenStartCharPositionInLine, msg, e);
/*     */   }
/*     */   
/*     */   public String getErrorDisplay(String s) {
/* 390 */     StringBuilder buf = new StringBuilder();
/* 391 */     for (char c : s.toCharArray()) {
/* 392 */       buf.append(getErrorDisplay(c));
/*     */     }
/* 394 */     return buf.toString();
/*     */   }
/*     */   
/*     */   public String getErrorDisplay(int c) {
/* 398 */     String s = String.valueOf((char)c);
/* 399 */     switch (c) {
/*     */       case -1:
/* 401 */         s = "<EOF>";
/*     */         break;
/*     */       case 10:
/* 404 */         s = "\\n";
/*     */         break;
/*     */       case 9:
/* 407 */         s = "\\t";
/*     */         break;
/*     */       case 13:
/* 410 */         s = "\\r";
/*     */         break;
/*     */     } 
/* 413 */     return s;
/*     */   }
/*     */   
/*     */   public String getCharErrorDisplay(int c) {
/* 417 */     String s = getErrorDisplay(c);
/* 418 */     return "'" + s + "'";
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void recover(RecognitionException re) {
/* 430 */     this._input.consume();
/*     */   }
/*     */   
/*     */   public Lexer() {}
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\antlr\v4\runtime\Lexer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */