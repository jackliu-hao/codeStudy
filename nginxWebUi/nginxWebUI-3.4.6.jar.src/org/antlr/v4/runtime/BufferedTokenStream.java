/*     */ package org.antlr.v4.runtime;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashSet;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ import org.antlr.v4.runtime.misc.Interval;
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
/*     */ public class BufferedTokenStream
/*     */   implements TokenStream
/*     */ {
/*     */   protected TokenSource tokenSource;
/*  63 */   protected List<Token> tokens = new ArrayList<Token>(100);
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
/*  76 */   protected int p = -1;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected boolean fetchedEOF;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public BufferedTokenStream(TokenSource tokenSource) {
/*  94 */     if (tokenSource == null) {
/*  95 */       throw new NullPointerException("tokenSource cannot be null");
/*     */     }
/*  97 */     this.tokenSource = tokenSource;
/*     */   }
/*     */   
/*     */   public TokenSource getTokenSource() {
/* 101 */     return this.tokenSource;
/*     */   }
/*     */   public int index() {
/* 104 */     return this.p;
/*     */   }
/*     */   
/*     */   public int mark() {
/* 108 */     return 0;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void release(int marker) {}
/*     */ 
/*     */   
/*     */   public void reset() {
/* 117 */     seek(0);
/*     */   }
/*     */ 
/*     */   
/*     */   public void seek(int index) {
/* 122 */     lazyInit();
/* 123 */     this.p = adjustSeekIndex(index);
/*     */   }
/*     */   
/*     */   public int size() {
/* 127 */     return this.tokens.size();
/*     */   }
/*     */   
/*     */   public void consume() {
/*     */     boolean skipEofCheck;
/* 132 */     if (this.p >= 0) {
/* 133 */       if (this.fetchedEOF)
/*     */       {
/*     */         
/* 136 */         skipEofCheck = (this.p < this.tokens.size() - 1);
/*     */       }
/*     */       else
/*     */       {
/* 140 */         skipEofCheck = (this.p < this.tokens.size());
/*     */       }
/*     */     
/*     */     } else {
/*     */       
/* 145 */       skipEofCheck = false;
/*     */     } 
/*     */     
/* 148 */     if (!skipEofCheck && LA(1) == -1) {
/* 149 */       throw new IllegalStateException("cannot consume EOF");
/*     */     }
/*     */     
/* 152 */     if (sync(this.p + 1)) {
/* 153 */       this.p = adjustSeekIndex(this.p + 1);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected boolean sync(int i) {
/* 164 */     assert i >= 0;
/* 165 */     int n = i - this.tokens.size() + 1;
/*     */     
/* 167 */     if (n > 0) {
/* 168 */       int fetched = fetch(n);
/* 169 */       return (fetched >= n);
/*     */     } 
/*     */     
/* 172 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected int fetch(int n) {
/* 180 */     if (this.fetchedEOF) {
/* 181 */       return 0;
/*     */     }
/*     */     
/* 184 */     for (int i = 0; i < n; i++) {
/* 185 */       Token t = this.tokenSource.nextToken();
/* 186 */       if (t instanceof WritableToken) {
/* 187 */         ((WritableToken)t).setTokenIndex(this.tokens.size());
/*     */       }
/* 189 */       this.tokens.add(t);
/* 190 */       if (t.getType() == -1) {
/* 191 */         this.fetchedEOF = true;
/* 192 */         return i + 1;
/*     */       } 
/*     */     } 
/*     */     
/* 196 */     return n;
/*     */   }
/*     */ 
/*     */   
/*     */   public Token get(int i) {
/* 201 */     if (i < 0 || i >= this.tokens.size()) {
/* 202 */       throw new IndexOutOfBoundsException("token index " + i + " out of range 0.." + (this.tokens.size() - 1));
/*     */     }
/* 204 */     return this.tokens.get(i);
/*     */   }
/*     */ 
/*     */   
/*     */   public List<Token> get(int start, int stop) {
/* 209 */     if (start < 0 || stop < 0) return null; 
/* 210 */     lazyInit();
/* 211 */     List<Token> subset = new ArrayList<Token>();
/* 212 */     if (stop >= this.tokens.size()) stop = this.tokens.size() - 1; 
/* 213 */     for (int i = start; i <= stop; i++) {
/* 214 */       Token t = this.tokens.get(i);
/* 215 */       if (t.getType() == -1)
/* 216 */         break;  subset.add(t);
/*     */     } 
/* 218 */     return subset;
/*     */   }
/*     */   
/*     */   public int LA(int i) {
/* 222 */     return LT(i).getType();
/*     */   }
/*     */   protected Token LB(int k) {
/* 225 */     if (this.p - k < 0) return null; 
/* 226 */     return this.tokens.get(this.p - k);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Token LT(int k) {
/* 232 */     lazyInit();
/* 233 */     if (k == 0) return null; 
/* 234 */     if (k < 0) return LB(-k);
/*     */     
/* 236 */     int i = this.p + k - 1;
/* 237 */     sync(i);
/* 238 */     if (i >= this.tokens.size())
/*     */     {
/* 240 */       return this.tokens.get(this.tokens.size() - 1);
/*     */     }
/*     */     
/* 243 */     return this.tokens.get(i);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected int adjustSeekIndex(int i) {
/* 260 */     return i;
/*     */   }
/*     */   
/*     */   protected final void lazyInit() {
/* 264 */     if (this.p == -1) {
/* 265 */       setup();
/*     */     }
/*     */   }
/*     */   
/*     */   protected void setup() {
/* 270 */     sync(0);
/* 271 */     this.p = adjustSeekIndex(0);
/*     */   }
/*     */ 
/*     */   
/*     */   public void setTokenSource(TokenSource tokenSource) {
/* 276 */     this.tokenSource = tokenSource;
/* 277 */     this.tokens.clear();
/* 278 */     this.p = -1;
/*     */   }
/*     */   public List<Token> getTokens() {
/* 281 */     return this.tokens;
/*     */   }
/*     */   public List<Token> getTokens(int start, int stop) {
/* 284 */     return getTokens(start, stop, (Set<Integer>)null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public List<Token> getTokens(int start, int stop, Set<Integer> types) {
/* 292 */     lazyInit();
/* 293 */     if (start < 0 || stop >= this.tokens.size() || stop < 0 || start >= this.tokens.size())
/*     */     {
/*     */       
/* 296 */       throw new IndexOutOfBoundsException("start " + start + " or stop " + stop + " not in 0.." + (this.tokens.size() - 1));
/*     */     }
/*     */     
/* 299 */     if (start > stop) return null;
/*     */ 
/*     */     
/* 302 */     List<Token> filteredTokens = new ArrayList<Token>();
/* 303 */     for (int i = start; i <= stop; i++) {
/* 304 */       Token t = this.tokens.get(i);
/* 305 */       if (types == null || types.contains(Integer.valueOf(t.getType()))) {
/* 306 */         filteredTokens.add(t);
/*     */       }
/*     */     } 
/* 309 */     if (filteredTokens.isEmpty()) {
/* 310 */       filteredTokens = null;
/*     */     }
/* 312 */     return filteredTokens;
/*     */   }
/*     */   
/*     */   public List<Token> getTokens(int start, int stop, int ttype) {
/* 316 */     HashSet<Integer> s = new HashSet<Integer>(ttype);
/* 317 */     s.add(Integer.valueOf(ttype));
/* 318 */     return getTokens(start, stop, s);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected int nextTokenOnChannel(int i, int channel) {
/* 328 */     sync(i);
/* 329 */     if (i >= size()) {
/* 330 */       return size() - 1;
/*     */     }
/*     */     
/* 333 */     Token token = this.tokens.get(i);
/* 334 */     while (token.getChannel() != channel) {
/* 335 */       if (token.getType() == -1) {
/* 336 */         return i;
/*     */       }
/*     */       
/* 339 */       i++;
/* 340 */       sync(i);
/* 341 */       token = this.tokens.get(i);
/*     */     } 
/*     */     
/* 344 */     return i;
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
/*     */ 
/*     */   
/*     */   protected int previousTokenOnChannel(int i, int channel) {
/* 358 */     sync(i);
/* 359 */     if (i >= size())
/*     */     {
/* 361 */       return size() - 1;
/*     */     }
/*     */     
/* 364 */     while (i >= 0) {
/* 365 */       Token token = this.tokens.get(i);
/* 366 */       if (token.getType() == -1 || token.getChannel() == channel) {
/* 367 */         return i;
/*     */       }
/*     */       
/* 370 */       i--;
/*     */     } 
/*     */     
/* 373 */     return i;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public List<Token> getHiddenTokensToRight(int tokenIndex, int channel) {
/*     */     int to;
/* 381 */     lazyInit();
/* 382 */     if (tokenIndex < 0 || tokenIndex >= this.tokens.size()) {
/* 383 */       throw new IndexOutOfBoundsException(tokenIndex + " not in 0.." + (this.tokens.size() - 1));
/*     */     }
/*     */     
/* 386 */     int nextOnChannel = nextTokenOnChannel(tokenIndex + 1, 0);
/*     */ 
/*     */     
/* 389 */     int from = tokenIndex + 1;
/*     */     
/* 391 */     if (nextOnChannel == -1) { to = size() - 1; }
/* 392 */     else { to = nextOnChannel; }
/*     */     
/* 394 */     return filterForChannel(from, to, channel);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public List<Token> getHiddenTokensToRight(int tokenIndex) {
/* 402 */     return getHiddenTokensToRight(tokenIndex, -1);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public List<Token> getHiddenTokensToLeft(int tokenIndex, int channel) {
/* 410 */     lazyInit();
/* 411 */     if (tokenIndex < 0 || tokenIndex >= this.tokens.size()) {
/* 412 */       throw new IndexOutOfBoundsException(tokenIndex + " not in 0.." + (this.tokens.size() - 1));
/*     */     }
/*     */     
/* 415 */     if (tokenIndex == 0)
/*     */     {
/* 417 */       return null;
/*     */     }
/*     */     
/* 420 */     int prevOnChannel = previousTokenOnChannel(tokenIndex - 1, 0);
/*     */     
/* 422 */     if (prevOnChannel == tokenIndex - 1) return null;
/*     */     
/* 424 */     int from = prevOnChannel + 1;
/* 425 */     int to = tokenIndex - 1;
/*     */     
/* 427 */     return filterForChannel(from, to, channel);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public List<Token> getHiddenTokensToLeft(int tokenIndex) {
/* 434 */     return getHiddenTokensToLeft(tokenIndex, -1);
/*     */   }
/*     */   
/*     */   protected List<Token> filterForChannel(int from, int to, int channel) {
/* 438 */     List<Token> hidden = new ArrayList<Token>();
/* 439 */     for (int i = from; i <= to; i++) {
/* 440 */       Token t = this.tokens.get(i);
/* 441 */       if (channel == -1)
/* 442 */       { if (t.getChannel() != 0) hidden.add(t);
/*     */          }
/*     */       
/* 445 */       else if (t.getChannel() == channel) { hidden.add(t); }
/*     */     
/*     */     } 
/* 448 */     if (hidden.size() == 0) return null; 
/* 449 */     return hidden;
/*     */   }
/*     */   
/*     */   public String getSourceName() {
/* 453 */     return this.tokenSource.getSourceName();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public String getText() {
/* 459 */     lazyInit();
/* 460 */     fill();
/* 461 */     return getText(Interval.of(0, size() - 1));
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public String getText(Interval interval) {
/* 467 */     int start = interval.a;
/* 468 */     int stop = interval.b;
/* 469 */     if (start < 0 || stop < 0) return ""; 
/* 470 */     lazyInit();
/* 471 */     if (stop >= this.tokens.size()) stop = this.tokens.size() - 1;
/*     */     
/* 473 */     StringBuilder buf = new StringBuilder();
/* 474 */     for (int i = start; i <= stop; i++) {
/* 475 */       Token t = this.tokens.get(i);
/* 476 */       if (t.getType() == -1)
/* 477 */         break;  buf.append(t.getText());
/*     */     } 
/* 479 */     return buf.toString();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public String getText(RuleContext ctx) {
/* 485 */     return getText(ctx.getSourceInterval());
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public String getText(Token start, Token stop) {
/* 491 */     if (start != null && stop != null) {
/* 492 */       return getText(Interval.of(start.getTokenIndex(), stop.getTokenIndex()));
/*     */     }
/*     */     
/* 495 */     return "";
/*     */   }
/*     */   
/*     */   public void fill() {
/*     */     int fetched;
/* 500 */     lazyInit();
/* 501 */     int blockSize = 1000;
/*     */     do {
/* 503 */       fetched = fetch(1000);
/* 504 */     } while (fetched >= 1000);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\antlr\v4\runtime\BufferedTokenStream.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */