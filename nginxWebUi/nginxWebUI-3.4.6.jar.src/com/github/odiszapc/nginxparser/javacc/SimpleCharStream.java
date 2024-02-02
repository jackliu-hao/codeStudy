/*     */ package com.github.odiszapc.nginxparser.javacc;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.InputStreamReader;
/*     */ import java.io.Reader;
/*     */ import java.io.UnsupportedEncodingException;
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
/*     */ public class SimpleCharStream
/*     */ {
/*     */   public static final boolean staticFlag = false;
/*     */   int bufsize;
/*     */   int available;
/*     */   int tokenBegin;
/*  34 */   public int bufpos = -1;
/*     */   
/*     */   protected int[] bufline;
/*     */   protected int[] bufcolumn;
/*  38 */   protected int column = 0;
/*  39 */   protected int line = 1;
/*     */   
/*     */   protected boolean prevCharIsCR = false;
/*     */   
/*     */   protected boolean prevCharIsLF = false;
/*     */   
/*     */   protected Reader inputStream;
/*     */   protected char[] buffer;
/*  47 */   protected int maxNextCharInd = 0;
/*  48 */   protected int inBuf = 0;
/*  49 */   protected int tabSize = 8;
/*     */   protected boolean trackLineColumn = true;
/*     */   
/*  52 */   public void setTabSize(int i) { this.tabSize = i; } public int getTabSize() {
/*  53 */     return this.tabSize;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void ExpandBuff(boolean wrapAround) {
/*  58 */     char[] newbuffer = new char[this.bufsize + 2048];
/*  59 */     int[] newbufline = new int[this.bufsize + 2048];
/*  60 */     int[] newbufcolumn = new int[this.bufsize + 2048];
/*     */ 
/*     */     
/*     */     try {
/*  64 */       if (wrapAround)
/*     */       {
/*  66 */         System.arraycopy(this.buffer, this.tokenBegin, newbuffer, 0, this.bufsize - this.tokenBegin);
/*  67 */         System.arraycopy(this.buffer, 0, newbuffer, this.bufsize - this.tokenBegin, this.bufpos);
/*  68 */         this.buffer = newbuffer;
/*     */         
/*  70 */         System.arraycopy(this.bufline, this.tokenBegin, newbufline, 0, this.bufsize - this.tokenBegin);
/*  71 */         System.arraycopy(this.bufline, 0, newbufline, this.bufsize - this.tokenBegin, this.bufpos);
/*  72 */         this.bufline = newbufline;
/*     */         
/*  74 */         System.arraycopy(this.bufcolumn, this.tokenBegin, newbufcolumn, 0, this.bufsize - this.tokenBegin);
/*  75 */         System.arraycopy(this.bufcolumn, 0, newbufcolumn, this.bufsize - this.tokenBegin, this.bufpos);
/*  76 */         this.bufcolumn = newbufcolumn;
/*     */         
/*  78 */         this.maxNextCharInd = this.bufpos += this.bufsize - this.tokenBegin;
/*     */       }
/*     */       else
/*     */       {
/*  82 */         System.arraycopy(this.buffer, this.tokenBegin, newbuffer, 0, this.bufsize - this.tokenBegin);
/*  83 */         this.buffer = newbuffer;
/*     */         
/*  85 */         System.arraycopy(this.bufline, this.tokenBegin, newbufline, 0, this.bufsize - this.tokenBegin);
/*  86 */         this.bufline = newbufline;
/*     */         
/*  88 */         System.arraycopy(this.bufcolumn, this.tokenBegin, newbufcolumn, 0, this.bufsize - this.tokenBegin);
/*  89 */         this.bufcolumn = newbufcolumn;
/*     */         
/*  91 */         this.maxNextCharInd = this.bufpos -= this.tokenBegin;
/*     */       }
/*     */     
/*  94 */     } catch (Throwable t) {
/*     */       
/*  96 */       throw new Error(t.getMessage());
/*     */     } 
/*     */ 
/*     */     
/* 100 */     this.bufsize += 2048;
/* 101 */     this.available = this.bufsize;
/* 102 */     this.tokenBegin = 0;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void FillBuff() throws IOException {
/* 107 */     if (this.maxNextCharInd == this.available)
/*     */     {
/* 109 */       if (this.available == this.bufsize) {
/*     */         
/* 111 */         if (this.tokenBegin > 2048) {
/*     */           
/* 113 */           this.bufpos = this.maxNextCharInd = 0;
/* 114 */           this.available = this.tokenBegin;
/*     */         }
/* 116 */         else if (this.tokenBegin < 0) {
/* 117 */           this.bufpos = this.maxNextCharInd = 0;
/*     */         } else {
/* 119 */           ExpandBuff(false);
/*     */         } 
/* 121 */       } else if (this.available > this.tokenBegin) {
/* 122 */         this.available = this.bufsize;
/* 123 */       } else if (this.tokenBegin - this.available < 2048) {
/* 124 */         ExpandBuff(true);
/*     */       } else {
/* 126 */         this.available = this.tokenBegin;
/*     */       } 
/*     */     }
/*     */     try {
/*     */       int i;
/* 131 */       if ((i = this.inputStream.read(this.buffer, this.maxNextCharInd, this.available - this.maxNextCharInd)) == -1) {
/*     */         
/* 133 */         this.inputStream.close();
/* 134 */         throw new IOException();
/*     */       } 
/*     */       
/* 137 */       this.maxNextCharInd += i;
/*     */       
/*     */       return;
/* 140 */     } catch (IOException e) {
/* 141 */       this.bufpos--;
/* 142 */       backup(0);
/* 143 */       if (this.tokenBegin == -1)
/* 144 */         this.tokenBegin = this.bufpos; 
/* 145 */       throw e;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public char BeginToken() throws IOException {
/* 152 */     this.tokenBegin = -1;
/* 153 */     char c = readChar();
/* 154 */     this.tokenBegin = this.bufpos;
/*     */     
/* 156 */     return c;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void UpdateLineColumn(char c) {
/* 161 */     this.column++;
/*     */     
/* 163 */     if (this.prevCharIsLF) {
/*     */       
/* 165 */       this.prevCharIsLF = false;
/* 166 */       this.line += this.column = 1;
/*     */     }
/* 168 */     else if (this.prevCharIsCR) {
/*     */       
/* 170 */       this.prevCharIsCR = false;
/* 171 */       if (c == '\n') {
/*     */         
/* 173 */         this.prevCharIsLF = true;
/*     */       } else {
/*     */         
/* 176 */         this.line += this.column = 1;
/*     */       } 
/*     */     } 
/* 179 */     switch (c) {
/*     */       
/*     */       case '\r':
/* 182 */         this.prevCharIsCR = true;
/*     */         break;
/*     */       case '\n':
/* 185 */         this.prevCharIsLF = true;
/*     */         break;
/*     */       case '\t':
/* 188 */         this.column--;
/* 189 */         this.column += this.tabSize - this.column % this.tabSize;
/*     */         break;
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 195 */     this.bufline[this.bufpos] = this.line;
/* 196 */     this.bufcolumn[this.bufpos] = this.column;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public char readChar() throws IOException {
/* 202 */     if (this.inBuf > 0) {
/*     */       
/* 204 */       this.inBuf--;
/*     */       
/* 206 */       if (++this.bufpos == this.bufsize) {
/* 207 */         this.bufpos = 0;
/*     */       }
/* 209 */       return this.buffer[this.bufpos];
/*     */     } 
/*     */     
/* 212 */     if (++this.bufpos >= this.maxNextCharInd) {
/* 213 */       FillBuff();
/*     */     }
/* 215 */     char c = this.buffer[this.bufpos];
/*     */     
/* 217 */     UpdateLineColumn(c);
/* 218 */     return c;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public int getColumn() {
/* 228 */     return this.bufcolumn[this.bufpos];
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public int getLine() {
/* 238 */     return this.bufline[this.bufpos];
/*     */   }
/*     */ 
/*     */   
/*     */   public int getEndColumn() {
/* 243 */     return this.bufcolumn[this.bufpos];
/*     */   }
/*     */ 
/*     */   
/*     */   public int getEndLine() {
/* 248 */     return this.bufline[this.bufpos];
/*     */   }
/*     */ 
/*     */   
/*     */   public int getBeginColumn() {
/* 253 */     return this.bufcolumn[this.tokenBegin];
/*     */   }
/*     */ 
/*     */   
/*     */   public int getBeginLine() {
/* 258 */     return this.bufline[this.tokenBegin];
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void backup(int amount) {
/* 264 */     this.inBuf += amount;
/* 265 */     if ((this.bufpos -= amount) < 0) {
/* 266 */       this.bufpos += this.bufsize;
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public SimpleCharStream(Reader dstream, int startline, int startcolumn, int buffersize) {
/* 273 */     this.inputStream = dstream;
/* 274 */     this.line = startline;
/* 275 */     this.column = startcolumn - 1;
/*     */     
/* 277 */     this.available = this.bufsize = buffersize;
/* 278 */     this.buffer = new char[buffersize];
/* 279 */     this.bufline = new int[buffersize];
/* 280 */     this.bufcolumn = new int[buffersize];
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SimpleCharStream(Reader dstream, int startline, int startcolumn) {
/* 287 */     this(dstream, startline, startcolumn, 4096);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public SimpleCharStream(Reader dstream) {
/* 293 */     this(dstream, 1, 1, 4096);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void ReInit(Reader dstream, int startline, int startcolumn, int buffersize) {
/* 300 */     this.inputStream = dstream;
/* 301 */     this.line = startline;
/* 302 */     this.column = startcolumn - 1;
/*     */     
/* 304 */     if (this.buffer == null || buffersize != this.buffer.length) {
/*     */       
/* 306 */       this.available = this.bufsize = buffersize;
/* 307 */       this.buffer = new char[buffersize];
/* 308 */       this.bufline = new int[buffersize];
/* 309 */       this.bufcolumn = new int[buffersize];
/*     */     } 
/* 311 */     this.prevCharIsLF = this.prevCharIsCR = false;
/* 312 */     this.tokenBegin = this.inBuf = this.maxNextCharInd = 0;
/* 313 */     this.bufpos = -1;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void ReInit(Reader dstream, int startline, int startcolumn) {
/* 320 */     ReInit(dstream, startline, startcolumn, 4096);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void ReInit(Reader dstream) {
/* 326 */     ReInit(dstream, 1, 1, 4096);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public SimpleCharStream(InputStream dstream, String encoding, int startline, int startcolumn, int buffersize) throws UnsupportedEncodingException {
/* 332 */     this((encoding == null) ? new InputStreamReader(dstream) : new InputStreamReader(dstream, encoding), startline, startcolumn, buffersize);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SimpleCharStream(InputStream dstream, int startline, int startcolumn, int buffersize) {
/* 339 */     this(new InputStreamReader(dstream), startline, startcolumn, buffersize);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SimpleCharStream(InputStream dstream, String encoding, int startline, int startcolumn) throws UnsupportedEncodingException {
/* 346 */     this(dstream, encoding, startline, startcolumn, 4096);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SimpleCharStream(InputStream dstream, int startline, int startcolumn) {
/* 353 */     this(dstream, startline, startcolumn, 4096);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public SimpleCharStream(InputStream dstream, String encoding) throws UnsupportedEncodingException {
/* 359 */     this(dstream, encoding, 1, 1, 4096);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public SimpleCharStream(InputStream dstream) {
/* 365 */     this(dstream, 1, 1, 4096);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void ReInit(InputStream dstream, String encoding, int startline, int startcolumn, int buffersize) throws UnsupportedEncodingException {
/* 372 */     ReInit((encoding == null) ? new InputStreamReader(dstream) : new InputStreamReader(dstream, encoding), startline, startcolumn, buffersize);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void ReInit(InputStream dstream, int startline, int startcolumn, int buffersize) {
/* 379 */     ReInit(new InputStreamReader(dstream), startline, startcolumn, buffersize);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void ReInit(InputStream dstream, String encoding) throws UnsupportedEncodingException {
/* 385 */     ReInit(dstream, encoding, 1, 1, 4096);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void ReInit(InputStream dstream) {
/* 391 */     ReInit(dstream, 1, 1, 4096);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void ReInit(InputStream dstream, String encoding, int startline, int startcolumn) throws UnsupportedEncodingException {
/* 397 */     ReInit(dstream, encoding, startline, startcolumn, 4096);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void ReInit(InputStream dstream, int startline, int startcolumn) {
/* 403 */     ReInit(dstream, startline, startcolumn, 4096);
/*     */   }
/*     */ 
/*     */   
/*     */   public String GetImage() {
/* 408 */     if (this.bufpos >= this.tokenBegin) {
/* 409 */       return new String(this.buffer, this.tokenBegin, this.bufpos - this.tokenBegin + 1);
/*     */     }
/* 411 */     return new String(this.buffer, this.tokenBegin, this.bufsize - this.tokenBegin) + new String(this.buffer, 0, this.bufpos + 1);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public char[] GetSuffix(int len) {
/* 418 */     char[] ret = new char[len];
/*     */     
/* 420 */     if (this.bufpos + 1 >= len) {
/* 421 */       System.arraycopy(this.buffer, this.bufpos - len + 1, ret, 0, len);
/*     */     } else {
/*     */       
/* 424 */       System.arraycopy(this.buffer, this.bufsize - len - this.bufpos - 1, ret, 0, len - this.bufpos - 1);
/*     */       
/* 426 */       System.arraycopy(this.buffer, 0, ret, len - this.bufpos - 1, this.bufpos + 1);
/*     */     } 
/*     */     
/* 429 */     return ret;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void Done() {
/* 435 */     this.buffer = null;
/* 436 */     this.bufline = null;
/* 437 */     this.bufcolumn = null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void adjustBeginLineColumn(int newLine, int newCol) {
/* 445 */     int len, start = this.tokenBegin;
/*     */ 
/*     */     
/* 448 */     if (this.bufpos >= this.tokenBegin) {
/*     */       
/* 450 */       len = this.bufpos - this.tokenBegin + this.inBuf + 1;
/*     */     }
/*     */     else {
/*     */       
/* 454 */       len = this.bufsize - this.tokenBegin + this.bufpos + 1 + this.inBuf;
/*     */     } 
/*     */     
/* 457 */     int i = 0, j = 0, k = 0;
/* 458 */     int nextColDiff = 0, columnDiff = 0;
/*     */     
/* 460 */     while (i < len && this.bufline[j = start % this.bufsize] == this.bufline[k = ++start % this.bufsize]) {
/*     */       
/* 462 */       this.bufline[j] = newLine;
/* 463 */       nextColDiff = columnDiff + this.bufcolumn[k] - this.bufcolumn[j];
/* 464 */       this.bufcolumn[j] = newCol + columnDiff;
/* 465 */       columnDiff = nextColDiff;
/* 466 */       i++;
/*     */     } 
/*     */     
/* 469 */     if (i < len) {
/*     */       
/* 471 */       this.bufline[j] = newLine++;
/* 472 */       this.bufcolumn[j] = newCol + columnDiff;
/*     */       
/* 474 */       while (i++ < len) {
/*     */         
/* 476 */         if (this.bufline[j = start % this.bufsize] != this.bufline[++start % this.bufsize]) {
/* 477 */           this.bufline[j] = newLine++; continue;
/*     */         } 
/* 479 */         this.bufline[j] = newLine;
/*     */       } 
/*     */     } 
/*     */     
/* 483 */     this.line = this.bufline[j];
/* 484 */     this.column = this.bufcolumn[j];
/*     */   }
/*     */   
/* 487 */   boolean getTrackLineColumn() { return this.trackLineColumn; } void setTrackLineColumn(boolean tlc) {
/* 488 */     this.trackLineColumn = tlc;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\github\odiszapc\nginxparser\javacc\SimpleCharStream.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */