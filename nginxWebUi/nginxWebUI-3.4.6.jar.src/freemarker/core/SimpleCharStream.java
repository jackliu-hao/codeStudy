/*     */ package freemarker.core;
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
/*     */ public class SimpleCharStream
/*     */ {
/*     */   public static final boolean staticFlag = false;
/*     */   int bufsize;
/*     */   int available;
/*     */   int tokenBegin;
/*  18 */   public int bufpos = -1;
/*     */   
/*     */   protected int[] bufline;
/*     */   protected int[] bufcolumn;
/*  22 */   protected int column = 0;
/*  23 */   protected int line = 1;
/*     */   
/*     */   protected boolean prevCharIsCR = false;
/*     */   
/*     */   protected boolean prevCharIsLF = false;
/*     */   
/*     */   protected Reader inputStream;
/*     */   protected char[] buffer;
/*  31 */   protected int maxNextCharInd = 0;
/*  32 */   protected int inBuf = 0;
/*  33 */   protected int tabSize = 1;
/*     */   protected boolean trackLineColumn = true;
/*     */   
/*  36 */   public void setTabSize(int i) { this.tabSize = i; } public int getTabSize() {
/*  37 */     return this.tabSize;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected void ExpandBuff(boolean wrapAround) {
/*  43 */     char[] newbuffer = new char[this.bufsize + 2048];
/*  44 */     int[] newbufline = new int[this.bufsize + 2048];
/*  45 */     int[] newbufcolumn = new int[this.bufsize + 2048];
/*     */ 
/*     */     
/*     */     try {
/*  49 */       if (wrapAround)
/*     */       {
/*  51 */         System.arraycopy(this.buffer, this.tokenBegin, newbuffer, 0, this.bufsize - this.tokenBegin);
/*  52 */         System.arraycopy(this.buffer, 0, newbuffer, this.bufsize - this.tokenBegin, this.bufpos);
/*  53 */         this.buffer = newbuffer;
/*     */         
/*  55 */         System.arraycopy(this.bufline, this.tokenBegin, newbufline, 0, this.bufsize - this.tokenBegin);
/*  56 */         System.arraycopy(this.bufline, 0, newbufline, this.bufsize - this.tokenBegin, this.bufpos);
/*  57 */         this.bufline = newbufline;
/*     */         
/*  59 */         System.arraycopy(this.bufcolumn, this.tokenBegin, newbufcolumn, 0, this.bufsize - this.tokenBegin);
/*  60 */         System.arraycopy(this.bufcolumn, 0, newbufcolumn, this.bufsize - this.tokenBegin, this.bufpos);
/*  61 */         this.bufcolumn = newbufcolumn;
/*     */         
/*  63 */         this.maxNextCharInd = this.bufpos += this.bufsize - this.tokenBegin;
/*     */       }
/*     */       else
/*     */       {
/*  67 */         System.arraycopy(this.buffer, this.tokenBegin, newbuffer, 0, this.bufsize - this.tokenBegin);
/*  68 */         this.buffer = newbuffer;
/*     */         
/*  70 */         System.arraycopy(this.bufline, this.tokenBegin, newbufline, 0, this.bufsize - this.tokenBegin);
/*  71 */         this.bufline = newbufline;
/*     */         
/*  73 */         System.arraycopy(this.bufcolumn, this.tokenBegin, newbufcolumn, 0, this.bufsize - this.tokenBegin);
/*  74 */         this.bufcolumn = newbufcolumn;
/*     */         
/*  76 */         this.maxNextCharInd = this.bufpos -= this.tokenBegin;
/*     */       }
/*     */     
/*  79 */     } catch (Throwable t) {
/*     */       
/*  81 */       throw new Error(t.getMessage());
/*     */     } 
/*     */ 
/*     */     
/*  85 */     this.bufsize += 2048;
/*  86 */     this.available = this.bufsize;
/*  87 */     this.tokenBegin = 0;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void FillBuff() throws IOException {
/*  92 */     if (this.maxNextCharInd == this.available)
/*     */     {
/*  94 */       if (this.available == this.bufsize) {
/*     */         
/*  96 */         if (this.tokenBegin > 2048) {
/*     */           
/*  98 */           this.bufpos = this.maxNextCharInd = 0;
/*  99 */           this.available = this.tokenBegin;
/*     */         }
/* 101 */         else if (this.tokenBegin < 0) {
/* 102 */           this.bufpos = this.maxNextCharInd = 0;
/*     */         } else {
/* 104 */           ExpandBuff(false);
/*     */         } 
/* 106 */       } else if (this.available > this.tokenBegin) {
/* 107 */         this.available = this.bufsize;
/* 108 */       } else if (this.tokenBegin - this.available < 2048) {
/* 109 */         ExpandBuff(true);
/*     */       } else {
/* 111 */         this.available = this.tokenBegin;
/*     */       } 
/*     */     }
/*     */     try {
/*     */       int i;
/* 116 */       if ((i = this.inputStream.read(this.buffer, this.maxNextCharInd, this.available - this.maxNextCharInd)) == -1) {
/*     */         
/* 118 */         this.inputStream.close();
/* 119 */         throw new IOException();
/*     */       } 
/*     */       
/* 122 */       this.maxNextCharInd += i;
/*     */       
/*     */       return;
/* 125 */     } catch (IOException e) {
/* 126 */       this.bufpos--;
/* 127 */       backup(0);
/* 128 */       if (this.tokenBegin == -1)
/* 129 */         this.tokenBegin = this.bufpos; 
/* 130 */       throw e;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public char BeginToken() throws IOException {
/* 137 */     this.tokenBegin = -1;
/* 138 */     char c = readChar();
/* 139 */     this.tokenBegin = this.bufpos;
/*     */     
/* 141 */     return c;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void UpdateLineColumn(char c) {
/* 146 */     this.column++;
/*     */     
/* 148 */     if (this.prevCharIsLF) {
/*     */       
/* 150 */       this.prevCharIsLF = false;
/* 151 */       this.line += this.column = 1;
/*     */     }
/* 153 */     else if (this.prevCharIsCR) {
/*     */       
/* 155 */       this.prevCharIsCR = false;
/* 156 */       if (c == '\n') {
/*     */         
/* 158 */         this.prevCharIsLF = true;
/*     */       } else {
/*     */         
/* 161 */         this.line += this.column = 1;
/*     */       } 
/*     */     } 
/* 164 */     switch (c) {
/*     */       
/*     */       case '\r':
/* 167 */         this.prevCharIsCR = true;
/*     */         break;
/*     */       case '\n':
/* 170 */         this.prevCharIsLF = true;
/*     */         break;
/*     */       case '\t':
/* 173 */         this.column--;
/* 174 */         this.column += this.tabSize - this.column % this.tabSize;
/*     */         break;
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 180 */     this.bufline[this.bufpos] = this.line;
/* 181 */     this.bufcolumn[this.bufpos] = this.column;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public char readChar() throws IOException {
/* 187 */     if (this.inBuf > 0) {
/*     */       
/* 189 */       this.inBuf--;
/*     */       
/* 191 */       if (++this.bufpos == this.bufsize) {
/* 192 */         this.bufpos = 0;
/*     */       }
/* 194 */       return this.buffer[this.bufpos];
/*     */     } 
/*     */     
/* 197 */     if (++this.bufpos >= this.maxNextCharInd) {
/* 198 */       FillBuff();
/*     */     }
/* 200 */     char c = this.buffer[this.bufpos];
/*     */     
/* 202 */     UpdateLineColumn(c);
/* 203 */     return c;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public int getColumn() {
/* 213 */     return this.bufcolumn[this.bufpos];
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public int getLine() {
/* 223 */     return this.bufline[this.bufpos];
/*     */   }
/*     */ 
/*     */   
/*     */   public int getEndColumn() {
/* 228 */     return this.bufcolumn[this.bufpos];
/*     */   }
/*     */ 
/*     */   
/*     */   public int getEndLine() {
/* 233 */     return this.bufline[this.bufpos];
/*     */   }
/*     */ 
/*     */   
/*     */   public int getBeginColumn() {
/* 238 */     return this.bufcolumn[this.tokenBegin];
/*     */   }
/*     */ 
/*     */   
/*     */   public int getBeginLine() {
/* 243 */     return this.bufline[this.tokenBegin];
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void backup(int amount) {
/* 249 */     this.inBuf += amount;
/* 250 */     if ((this.bufpos -= amount) < 0) {
/* 251 */       this.bufpos += this.bufsize;
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public SimpleCharStream(Reader dstream, int startline, int startcolumn, int buffersize) {
/* 258 */     this.inputStream = dstream;
/* 259 */     this.line = startline;
/* 260 */     this.column = startcolumn - 1;
/*     */     
/* 262 */     this.available = this.bufsize = buffersize;
/* 263 */     this.buffer = new char[buffersize];
/* 264 */     this.bufline = new int[buffersize];
/* 265 */     this.bufcolumn = new int[buffersize];
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SimpleCharStream(Reader dstream, int startline, int startcolumn) {
/* 272 */     this(dstream, startline, startcolumn, 4096);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public SimpleCharStream(Reader dstream) {
/* 278 */     this(dstream, 1, 1, 4096);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void ReInit(Reader dstream, int startline, int startcolumn, int buffersize) {
/* 285 */     this.inputStream = dstream;
/* 286 */     this.line = startline;
/* 287 */     this.column = startcolumn - 1;
/*     */     
/* 289 */     if (this.buffer == null || buffersize != this.buffer.length) {
/*     */       
/* 291 */       this.available = this.bufsize = buffersize;
/* 292 */       this.buffer = new char[buffersize];
/* 293 */       this.bufline = new int[buffersize];
/* 294 */       this.bufcolumn = new int[buffersize];
/*     */     } 
/* 296 */     this.prevCharIsLF = this.prevCharIsCR = false;
/* 297 */     this.tokenBegin = this.inBuf = this.maxNextCharInd = 0;
/* 298 */     this.bufpos = -1;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void ReInit(Reader dstream, int startline, int startcolumn) {
/* 305 */     ReInit(dstream, startline, startcolumn, 4096);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void ReInit(Reader dstream) {
/* 311 */     ReInit(dstream, 1, 1, 4096);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public SimpleCharStream(InputStream dstream, String encoding, int startline, int startcolumn, int buffersize) throws UnsupportedEncodingException {
/* 317 */     this((encoding == null) ? new InputStreamReader(dstream) : new InputStreamReader(dstream, encoding), startline, startcolumn, buffersize);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SimpleCharStream(InputStream dstream, int startline, int startcolumn, int buffersize) {
/* 324 */     this(new InputStreamReader(dstream), startline, startcolumn, buffersize);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SimpleCharStream(InputStream dstream, String encoding, int startline, int startcolumn) throws UnsupportedEncodingException {
/* 331 */     this(dstream, encoding, startline, startcolumn, 4096);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SimpleCharStream(InputStream dstream, int startline, int startcolumn) {
/* 338 */     this(dstream, startline, startcolumn, 4096);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public SimpleCharStream(InputStream dstream, String encoding) throws UnsupportedEncodingException {
/* 344 */     this(dstream, encoding, 1, 1, 4096);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public SimpleCharStream(InputStream dstream) {
/* 350 */     this(dstream, 1, 1, 4096);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void ReInit(InputStream dstream, String encoding, int startline, int startcolumn, int buffersize) throws UnsupportedEncodingException {
/* 357 */     ReInit((encoding == null) ? new InputStreamReader(dstream) : new InputStreamReader(dstream, encoding), startline, startcolumn, buffersize);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void ReInit(InputStream dstream, int startline, int startcolumn, int buffersize) {
/* 364 */     ReInit(new InputStreamReader(dstream), startline, startcolumn, buffersize);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void ReInit(InputStream dstream, String encoding) throws UnsupportedEncodingException {
/* 370 */     ReInit(dstream, encoding, 1, 1, 4096);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void ReInit(InputStream dstream) {
/* 376 */     ReInit(dstream, 1, 1, 4096);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void ReInit(InputStream dstream, String encoding, int startline, int startcolumn) throws UnsupportedEncodingException {
/* 382 */     ReInit(dstream, encoding, startline, startcolumn, 4096);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void ReInit(InputStream dstream, int startline, int startcolumn) {
/* 388 */     ReInit(dstream, startline, startcolumn, 4096);
/*     */   }
/*     */ 
/*     */   
/*     */   public String GetImage() {
/* 393 */     if (this.bufpos >= this.tokenBegin) {
/* 394 */       return new String(this.buffer, this.tokenBegin, this.bufpos - this.tokenBegin + 1);
/*     */     }
/* 396 */     return new String(this.buffer, this.tokenBegin, this.bufsize - this.tokenBegin) + new String(this.buffer, 0, this.bufpos + 1);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public char[] GetSuffix(int len) {
/* 403 */     char[] ret = new char[len];
/*     */     
/* 405 */     if (this.bufpos + 1 >= len) {
/* 406 */       System.arraycopy(this.buffer, this.bufpos - len + 1, ret, 0, len);
/*     */     } else {
/*     */       
/* 409 */       System.arraycopy(this.buffer, this.bufsize - len - this.bufpos - 1, ret, 0, len - this.bufpos - 1);
/*     */       
/* 411 */       System.arraycopy(this.buffer, 0, ret, len - this.bufpos - 1, this.bufpos + 1);
/*     */     } 
/*     */     
/* 414 */     return ret;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void Done() {
/* 420 */     this.buffer = null;
/* 421 */     this.bufline = null;
/* 422 */     this.bufcolumn = null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void adjustBeginLineColumn(int newLine, int newCol) {
/* 430 */     int len, start = this.tokenBegin;
/*     */ 
/*     */     
/* 433 */     if (this.bufpos >= this.tokenBegin) {
/*     */       
/* 435 */       len = this.bufpos - this.tokenBegin + this.inBuf + 1;
/*     */     }
/*     */     else {
/*     */       
/* 439 */       len = this.bufsize - this.tokenBegin + this.bufpos + 1 + this.inBuf;
/*     */     } 
/*     */     
/* 442 */     int i = 0, j = 0, k = 0;
/* 443 */     int nextColDiff = 0, columnDiff = 0;
/*     */     
/* 445 */     while (i < len && this.bufline[j = start % this.bufsize] == this.bufline[k = ++start % this.bufsize]) {
/*     */       
/* 447 */       this.bufline[j] = newLine;
/* 448 */       nextColDiff = columnDiff + this.bufcolumn[k] - this.bufcolumn[j];
/* 449 */       this.bufcolumn[j] = newCol + columnDiff;
/* 450 */       columnDiff = nextColDiff;
/* 451 */       i++;
/*     */     } 
/*     */     
/* 454 */     if (i < len) {
/*     */       
/* 456 */       this.bufline[j] = newLine++;
/* 457 */       this.bufcolumn[j] = newCol + columnDiff;
/*     */       
/* 459 */       while (i++ < len) {
/*     */         
/* 461 */         if (this.bufline[j = start % this.bufsize] != this.bufline[++start % this.bufsize]) {
/* 462 */           this.bufline[j] = newLine++; continue;
/*     */         } 
/* 464 */         this.bufline[j] = newLine;
/*     */       } 
/*     */     } 
/*     */     
/* 468 */     this.line = this.bufline[j];
/* 469 */     this.column = this.bufcolumn[j];
/*     */   }
/* 471 */   boolean getTrackLineColumn() { return this.trackLineColumn; } void setTrackLineColumn(boolean tlc) {
/* 472 */     this.trackLineColumn = tlc;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\SimpleCharStream.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */