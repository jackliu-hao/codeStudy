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
/*     */ public class ANTLRInputStream
/*     */   implements CharStream
/*     */ {
/*     */   public static final int READ_BUFFER_SIZE = 1024;
/*     */   public static final int INITIAL_BUFFER_SIZE = 1024;
/*     */   protected char[] data;
/*     */   protected int n;
/*  58 */   protected int p = 0;
/*     */   
/*     */   public String name;
/*     */ 
/*     */   
/*     */   public ANTLRInputStream() {}
/*     */ 
/*     */   
/*     */   public ANTLRInputStream(String input) {
/*  67 */     this.data = input.toCharArray();
/*  68 */     this.n = input.length();
/*     */   }
/*     */ 
/*     */   
/*     */   public ANTLRInputStream(char[] data, int numberOfActualCharsInArray) {
/*  73 */     this.data = data;
/*  74 */     this.n = numberOfActualCharsInArray;
/*     */   }
/*     */   
/*     */   public ANTLRInputStream(Reader r) throws IOException {
/*  78 */     this(r, 1024, 1024);
/*     */   }
/*     */   
/*     */   public ANTLRInputStream(Reader r, int initialSize) throws IOException {
/*  82 */     this(r, initialSize, 1024);
/*     */   }
/*     */   
/*     */   public ANTLRInputStream(Reader r, int initialSize, int readChunkSize) throws IOException {
/*  86 */     load(r, initialSize, readChunkSize);
/*     */   }
/*     */   
/*     */   public ANTLRInputStream(InputStream input) throws IOException {
/*  90 */     this(new InputStreamReader(input), 1024);
/*     */   }
/*     */   
/*     */   public ANTLRInputStream(InputStream input, int initialSize) throws IOException {
/*  94 */     this(new InputStreamReader(input), initialSize);
/*     */   }
/*     */   
/*     */   public ANTLRInputStream(InputStream input, int initialSize, int readChunkSize) throws IOException {
/*  98 */     this(new InputStreamReader(input), initialSize, readChunkSize);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void load(Reader r, int size, int readChunkSize) throws IOException {
/* 104 */     if (r == null) {
/*     */       return;
/*     */     }
/* 107 */     if (size <= 0) {
/* 108 */       size = 1024;
/*     */     }
/* 110 */     if (readChunkSize <= 0) {
/* 111 */       readChunkSize = 1024;
/*     */     }
/*     */ 
/*     */     
/*     */     try {
/* 116 */       this.data = new char[size];
/*     */       
/* 118 */       int numRead = 0;
/* 119 */       int p = 0;
/*     */       do {
/* 121 */         if (p + readChunkSize > this.data.length)
/*     */         {
/* 123 */           this.data = Arrays.copyOf(this.data, this.data.length * 2);
/*     */         }
/* 125 */         numRead = r.read(this.data, p, readChunkSize);
/*     */         
/* 127 */         p += numRead;
/* 128 */       } while (numRead != -1);
/*     */ 
/*     */       
/* 131 */       this.n = p + 1;
/*     */     }
/*     */     finally {
/*     */       
/* 135 */       r.close();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void reset() {
/* 144 */     this.p = 0;
/*     */   }
/*     */ 
/*     */   
/*     */   public void consume() {
/* 149 */     if (this.p >= this.n) {
/* 150 */       assert LA(1) == -1;
/* 151 */       throw new IllegalStateException("cannot consume EOF");
/*     */     } 
/*     */ 
/*     */     
/* 155 */     if (this.p < this.n) {
/* 156 */       this.p++;
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public int LA(int i) {
/* 163 */     if (i == 0) {
/* 164 */       return 0;
/*     */     }
/* 166 */     if (i < 0) {
/* 167 */       i++;
/* 168 */       if (this.p + i - 1 < 0) {
/* 169 */         return -1;
/*     */       }
/*     */     } 
/*     */     
/* 173 */     if (this.p + i - 1 >= this.n)
/*     */     {
/* 175 */       return -1;
/*     */     }
/*     */ 
/*     */     
/* 179 */     return this.data[this.p + i - 1];
/*     */   }
/*     */   
/*     */   public int LT(int i) {
/* 183 */     return LA(i);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int index() {
/* 192 */     return this.p;
/*     */   }
/*     */ 
/*     */   
/*     */   public int size() {
/* 197 */     return this.n;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public int mark() {
/* 203 */     return -1;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void release(int marker) {}
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void seek(int index) {
/* 215 */     if (index <= this.p) {
/* 216 */       this.p = index;
/*     */       
/*     */       return;
/*     */     } 
/* 220 */     index = Math.min(index, this.n);
/* 221 */     while (this.p < index) {
/* 222 */       consume();
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public String getText(Interval interval) {
/* 228 */     int start = interval.a;
/* 229 */     int stop = interval.b;
/* 230 */     if (stop >= this.n) stop = this.n - 1; 
/* 231 */     int count = stop - start + 1;
/* 232 */     if (start >= this.n) return "";
/*     */ 
/*     */ 
/*     */     
/* 236 */     return new String(this.data, start, count);
/*     */   }
/*     */ 
/*     */   
/*     */   public String getSourceName() {
/* 241 */     if (this.name == null || this.name.isEmpty()) {
/* 242 */       return "<unknown>";
/*     */     }
/*     */     
/* 245 */     return this.name;
/*     */   }
/*     */   
/*     */   public String toString() {
/* 249 */     return new String(this.data);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\antlr\v4\runtime\ANTLRInputStream.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */