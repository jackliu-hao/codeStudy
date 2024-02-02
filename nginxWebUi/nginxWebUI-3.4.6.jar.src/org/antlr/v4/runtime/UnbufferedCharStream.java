/*     */ package org.antlr.v4.runtime;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.InputStreamReader;
/*     */ import java.io.Reader;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class UnbufferedCharStream
/*     */   implements CharStream
/*     */ {
/*     */   protected char[] data;
/*     */   protected int n;
/*  67 */   protected int p = 0;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  75 */   protected int numMarkers = 0;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  80 */   protected int lastChar = -1;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected int lastCharBufferStart;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  94 */   protected int currentCharIndex = 0;
/*     */ 
/*     */   
/*     */   protected Reader input;
/*     */   
/*     */   public String name;
/*     */ 
/*     */   
/*     */   public UnbufferedCharStream() {
/* 103 */     this(256);
/*     */   }
/*     */ 
/*     */   
/*     */   public UnbufferedCharStream(int bufferSize) {
/* 108 */     this.n = 0;
/* 109 */     this.data = new char[bufferSize];
/*     */   }
/*     */   
/*     */   public UnbufferedCharStream(InputStream input) {
/* 113 */     this(input, 256);
/*     */   }
/*     */   
/*     */   public UnbufferedCharStream(Reader input) {
/* 117 */     this(input, 256);
/*     */   }
/*     */   
/*     */   public UnbufferedCharStream(InputStream input, int bufferSize) {
/* 121 */     this(bufferSize);
/* 122 */     this.input = new InputStreamReader(input);
/* 123 */     fill(1);
/*     */   }
/*     */   
/*     */   public UnbufferedCharStream(Reader input, int bufferSize) {
/* 127 */     this(bufferSize);
/* 128 */     this.input = input;
/* 129 */     fill(1);
/*     */   }
/*     */ 
/*     */   
/*     */   public void consume() {
/* 134 */     if (LA(1) == -1) {
/* 135 */       throw new IllegalStateException("cannot consume EOF");
/*     */     }
/*     */ 
/*     */     
/* 139 */     this.lastChar = this.data[this.p];
/*     */     
/* 141 */     if (this.p == this.n - 1 && this.numMarkers == 0) {
/* 142 */       this.n = 0;
/* 143 */       this.p = -1;
/* 144 */       this.lastCharBufferStart = this.lastChar;
/*     */     } 
/*     */     
/* 147 */     this.p++;
/* 148 */     this.currentCharIndex++;
/* 149 */     sync(1);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void sync(int want) {
/* 159 */     int need = this.p + want - 1 - this.n + 1;
/* 160 */     if (need > 0) {
/* 161 */       fill(need);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected int fill(int n) {
/* 171 */     for (int i = 0; i < n; i++) {
/* 172 */       if (this.n > 0 && this.data[this.n - 1] == Character.MAX_VALUE) {
/* 173 */         return i;
/*     */       }
/*     */       
/*     */       try {
/* 177 */         int c = nextChar();
/* 178 */         add(c);
/*     */       }
/* 180 */       catch (IOException ioe) {
/* 181 */         throw new RuntimeException(ioe);
/*     */       } 
/*     */     } 
/*     */     
/* 185 */     return n;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected int nextChar() throws IOException {
/* 193 */     return this.input.read();
/*     */   }
/*     */   
/*     */   protected void add(int c) {
/* 197 */     if (this.n >= this.data.length) {
/* 198 */       this.data = Arrays.copyOf(this.data, this.data.length * 2);
/*     */     }
/* 200 */     this.data[this.n++] = (char)c;
/*     */   }
/*     */ 
/*     */   
/*     */   public int LA(int i) {
/* 205 */     if (i == -1) return this.lastChar; 
/* 206 */     sync(i);
/* 207 */     int index = this.p + i - 1;
/* 208 */     if (index < 0) throw new IndexOutOfBoundsException(); 
/* 209 */     if (index >= this.n) return -1; 
/* 210 */     char c = this.data[index];
/* 211 */     if (c == Character.MAX_VALUE) return -1; 
/* 212 */     return c;
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
/* 224 */     if (this.numMarkers == 0) {
/* 225 */       this.lastCharBufferStart = this.lastChar;
/*     */     }
/*     */     
/* 228 */     int mark = -this.numMarkers - 1;
/* 229 */     this.numMarkers++;
/* 230 */     return mark;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void release(int marker) {
/* 238 */     int expectedMark = -this.numMarkers;
/* 239 */     if (marker != expectedMark) {
/* 240 */       throw new IllegalStateException("release() called with an invalid marker.");
/*     */     }
/*     */     
/* 243 */     this.numMarkers--;
/* 244 */     if (this.numMarkers == 0 && this.p > 0) {
/*     */ 
/*     */       
/* 247 */       System.arraycopy(this.data, this.p, this.data, 0, this.n - this.p);
/* 248 */       this.n -= this.p;
/* 249 */       this.p = 0;
/* 250 */       this.lastCharBufferStart = this.lastChar;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public int index() {
/* 256 */     return this.currentCharIndex;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void seek(int index) {
/* 264 */     if (index == this.currentCharIndex) {
/*     */       return;
/*     */     }
/*     */     
/* 268 */     if (index > this.currentCharIndex) {
/* 269 */       sync(index - this.currentCharIndex);
/* 270 */       index = Math.min(index, getBufferStartIndex() + this.n - 1);
/*     */     } 
/*     */ 
/*     */     
/* 274 */     int i = index - getBufferStartIndex();
/* 275 */     if (i < 0) {
/* 276 */       throw new IllegalArgumentException("cannot seek to negative index " + index);
/*     */     }
/* 278 */     if (i >= this.n) {
/* 279 */       throw new UnsupportedOperationException("seek to index outside buffer: " + index + " not in " + getBufferStartIndex() + ".." + (getBufferStartIndex() + this.n));
/*     */     }
/*     */ 
/*     */     
/* 283 */     this.p = i;
/* 284 */     this.currentCharIndex = index;
/* 285 */     if (this.p == 0) {
/* 286 */       this.lastChar = this.lastCharBufferStart;
/*     */     } else {
/*     */       
/* 289 */       this.lastChar = this.data[this.p - 1];
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public int size() {
/* 295 */     throw new UnsupportedOperationException("Unbuffered stream cannot know its size");
/*     */   }
/*     */ 
/*     */   
/*     */   public String getSourceName() {
/* 300 */     if (this.name == null || this.name.isEmpty()) {
/* 301 */       return "<unknown>";
/*     */     }
/*     */     
/* 304 */     return this.name;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getText(Interval interval) {
/* 309 */     if (interval.a < 0 || interval.b < interval.a - 1) {
/* 310 */       throw new IllegalArgumentException("invalid interval");
/*     */     }
/*     */     
/* 313 */     int bufferStartIndex = getBufferStartIndex();
/* 314 */     if (this.n > 0 && this.data[this.n - 1] == Character.MAX_VALUE && 
/* 315 */       interval.a + interval.length() > bufferStartIndex + this.n) {
/* 316 */       throw new IllegalArgumentException("the interval extends past the end of the stream");
/*     */     }
/*     */ 
/*     */     
/* 320 */     if (interval.a < bufferStartIndex || interval.b >= bufferStartIndex + this.n) {
/* 321 */       throw new UnsupportedOperationException("interval " + interval + " outside buffer: " + bufferStartIndex + ".." + (bufferStartIndex + this.n - 1));
/*     */     }
/*     */ 
/*     */     
/* 325 */     int i = interval.a - bufferStartIndex;
/* 326 */     return new String(this.data, i, interval.length());
/*     */   }
/*     */   
/*     */   protected final int getBufferStartIndex() {
/* 330 */     return this.currentCharIndex - this.p;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\antlr\v4\runtime\UnbufferedCharStream.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */