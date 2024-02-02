/*     */ package org.antlr.v4.runtime;
/*     */ 
/*     */ import java.util.Arrays;
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
/*     */ public class UnbufferedTokenStream<T extends Token>
/*     */   implements TokenStream
/*     */ {
/*     */   protected TokenSource tokenSource;
/*     */   protected Token[] tokens;
/*     */   protected int n;
/*  60 */   protected int p = 0;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  68 */   protected int numMarkers = 0;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected Token lastToken;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected Token lastTokenBufferStart;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  89 */   protected int currentTokenIndex = 0;
/*     */   
/*     */   public UnbufferedTokenStream(TokenSource tokenSource) {
/*  92 */     this(tokenSource, 256);
/*     */   }
/*     */   
/*     */   public UnbufferedTokenStream(TokenSource tokenSource, int bufferSize) {
/*  96 */     this.tokenSource = tokenSource;
/*  97 */     this.tokens = new Token[bufferSize];
/*  98 */     this.n = 0;
/*  99 */     fill(1);
/*     */   }
/*     */ 
/*     */   
/*     */   public Token get(int i) {
/* 104 */     int bufferStartIndex = getBufferStartIndex();
/* 105 */     if (i < bufferStartIndex || i >= bufferStartIndex + this.n) {
/* 106 */       throw new IndexOutOfBoundsException("get(" + i + ") outside buffer: " + bufferStartIndex + ".." + (bufferStartIndex + this.n));
/*     */     }
/*     */     
/* 109 */     return this.tokens[i - bufferStartIndex];
/*     */   }
/*     */ 
/*     */   
/*     */   public Token LT(int i) {
/* 114 */     if (i == -1) {
/* 115 */       return this.lastToken;
/*     */     }
/*     */     
/* 118 */     sync(i);
/* 119 */     int index = this.p + i - 1;
/* 120 */     if (index < 0) {
/* 121 */       throw new IndexOutOfBoundsException("LT(" + i + ") gives negative index");
/*     */     }
/*     */     
/* 124 */     if (index >= this.n) {
/* 125 */       assert this.n > 0 && this.tokens[this.n - 1].getType() == -1;
/* 126 */       return this.tokens[this.n - 1];
/*     */     } 
/*     */     
/* 129 */     return this.tokens[index];
/*     */   }
/*     */ 
/*     */   
/*     */   public int LA(int i) {
/* 134 */     return LT(i).getType();
/*     */   }
/*     */ 
/*     */   
/*     */   public TokenSource getTokenSource() {
/* 139 */     return this.tokenSource;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public String getText() {
/* 145 */     return "";
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public String getText(RuleContext ctx) {
/* 151 */     return getText(ctx.getSourceInterval());
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public String getText(Token start, Token stop) {
/* 157 */     return getText(Interval.of(start.getTokenIndex(), stop.getTokenIndex()));
/*     */   }
/*     */ 
/*     */   
/*     */   public void consume() {
/* 162 */     if (LA(1) == -1) {
/* 163 */       throw new IllegalStateException("cannot consume EOF");
/*     */     }
/*     */ 
/*     */     
/* 167 */     this.lastToken = this.tokens[this.p];
/*     */ 
/*     */     
/* 170 */     if (this.p == this.n - 1 && this.numMarkers == 0) {
/* 171 */       this.n = 0;
/* 172 */       this.p = -1;
/* 173 */       this.lastTokenBufferStart = this.lastToken;
/*     */     } 
/*     */     
/* 176 */     this.p++;
/* 177 */     this.currentTokenIndex++;
/* 178 */     sync(1);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void sync(int want) {
/* 186 */     int need = this.p + want - 1 - this.n + 1;
/* 187 */     if (need > 0) {
/* 188 */       fill(need);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected int fill(int n) {
/* 198 */     for (int i = 0; i < n; i++) {
/* 199 */       if (this.n > 0 && this.tokens[this.n - 1].getType() == -1) {
/* 200 */         return i;
/*     */       }
/*     */       
/* 203 */       Token t = this.tokenSource.nextToken();
/* 204 */       add(t);
/*     */     } 
/*     */     
/* 207 */     return n;
/*     */   }
/*     */   
/*     */   protected void add(Token t) {
/* 211 */     if (this.n >= this.tokens.length) {
/* 212 */       this.tokens = Arrays.<Token>copyOf(this.tokens, this.tokens.length * 2);
/*     */     }
/*     */     
/* 215 */     if (t instanceof WritableToken) {
/* 216 */       ((WritableToken)t).setTokenIndex(getBufferStartIndex() + this.n);
/*     */     }
/*     */     
/* 219 */     this.tokens[this.n++] = t;
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
/*     */   public int mark() {
/* 231 */     if (this.numMarkers == 0) {
/* 232 */       this.lastTokenBufferStart = this.lastToken;
/*     */     }
/*     */     
/* 235 */     int mark = -this.numMarkers - 1;
/* 236 */     this.numMarkers++;
/* 237 */     return mark;
/*     */   }
/*     */ 
/*     */   
/*     */   public void release(int marker) {
/* 242 */     int expectedMark = -this.numMarkers;
/* 243 */     if (marker != expectedMark) {
/* 244 */       throw new IllegalStateException("release() called with an invalid marker.");
/*     */     }
/*     */     
/* 247 */     this.numMarkers--;
/* 248 */     if (this.numMarkers == 0) {
/* 249 */       if (this.p > 0) {
/*     */ 
/*     */         
/* 252 */         System.arraycopy(this.tokens, this.p, this.tokens, 0, this.n - this.p);
/* 253 */         this.n -= this.p;
/* 254 */         this.p = 0;
/*     */       } 
/*     */       
/* 257 */       this.lastTokenBufferStart = this.lastToken;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public int index() {
/* 263 */     return this.currentTokenIndex;
/*     */   }
/*     */ 
/*     */   
/*     */   public void seek(int index) {
/* 268 */     if (index == this.currentTokenIndex) {
/*     */       return;
/*     */     }
/*     */     
/* 272 */     if (index > this.currentTokenIndex) {
/* 273 */       sync(index - this.currentTokenIndex);
/* 274 */       index = Math.min(index, getBufferStartIndex() + this.n - 1);
/*     */     } 
/*     */     
/* 277 */     int bufferStartIndex = getBufferStartIndex();
/* 278 */     int i = index - bufferStartIndex;
/* 279 */     if (i < 0) {
/* 280 */       throw new IllegalArgumentException("cannot seek to negative index " + index);
/*     */     }
/* 282 */     if (i >= this.n) {
/* 283 */       throw new UnsupportedOperationException("seek to index outside buffer: " + index + " not in " + bufferStartIndex + ".." + (bufferStartIndex + this.n));
/*     */     }
/*     */ 
/*     */     
/* 287 */     this.p = i;
/* 288 */     this.currentTokenIndex = index;
/* 289 */     if (this.p == 0) {
/* 290 */       this.lastToken = this.lastTokenBufferStart;
/*     */     } else {
/*     */       
/* 293 */       this.lastToken = this.tokens[this.p - 1];
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public int size() {
/* 299 */     throw new UnsupportedOperationException("Unbuffered stream cannot know its size");
/*     */   }
/*     */ 
/*     */   
/*     */   public String getSourceName() {
/* 304 */     return this.tokenSource.getSourceName();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public String getText(Interval interval) {
/* 310 */     int bufferStartIndex = getBufferStartIndex();
/* 311 */     int bufferStopIndex = bufferStartIndex + this.tokens.length - 1;
/*     */     
/* 313 */     int start = interval.a;
/* 314 */     int stop = interval.b;
/* 315 */     if (start < bufferStartIndex || stop > bufferStopIndex) {
/* 316 */       throw new UnsupportedOperationException("interval " + interval + " not in token buffer window: " + bufferStartIndex + ".." + bufferStopIndex);
/*     */     }
/*     */ 
/*     */     
/* 320 */     int a = start - bufferStartIndex;
/* 321 */     int b = stop - bufferStartIndex;
/*     */     
/* 323 */     StringBuilder buf = new StringBuilder();
/* 324 */     for (int i = a; i <= b; i++) {
/* 325 */       Token t = this.tokens[i];
/* 326 */       buf.append(t.getText());
/*     */     } 
/*     */     
/* 329 */     return buf.toString();
/*     */   }
/*     */   
/*     */   protected final int getBufferStartIndex() {
/* 333 */     return this.currentTokenIndex - this.p;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\antlr\v4\runtime\UnbufferedTokenStream.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */