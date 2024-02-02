/*     */ package cn.hutool.json;
/*     */ 
/*     */ import cn.hutool.core.io.IoUtil;
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import java.io.BufferedReader;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.Reader;
/*     */ import java.io.StringReader;
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
/*     */ public class JSONTokener
/*     */ {
/*     */   private long character;
/*     */   private boolean eof;
/*     */   private long index;
/*     */   private long line;
/*     */   private char previous;
/*     */   private boolean usePrevious;
/*     */   private final Reader reader;
/*     */   private final JSONConfig config;
/*     */   
/*     */   public JSONTokener(Reader reader, JSONConfig config) {
/*  59 */     this.reader = reader.markSupported() ? reader : new BufferedReader(reader);
/*  60 */     this.eof = false;
/*  61 */     this.usePrevious = false;
/*  62 */     this.previous = Character.MIN_VALUE;
/*  63 */     this.index = 0L;
/*  64 */     this.character = 1L;
/*  65 */     this.line = 1L;
/*  66 */     this.config = config;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JSONTokener(InputStream inputStream, JSONConfig config) throws JSONException {
/*  76 */     this(IoUtil.getUtf8Reader(inputStream), config);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JSONTokener(CharSequence s, JSONConfig config) {
/*  86 */     this(new StringReader(StrUtil.str(s)), config);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void back() throws JSONException {
/*  94 */     if (this.usePrevious || this.index <= 0L) {
/*  95 */       throw new JSONException("Stepping back two steps is not supported");
/*     */     }
/*  97 */     this.index--;
/*  98 */     this.character--;
/*  99 */     this.usePrevious = true;
/* 100 */     this.eof = false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean end() {
/* 107 */     return (this.eof && false == this.usePrevious);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean more() throws JSONException {
/* 116 */     next();
/* 117 */     if (end()) {
/* 118 */       return false;
/*     */     }
/* 120 */     back();
/* 121 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public char next() throws JSONException {
/*     */     int c;
/* 132 */     if (this.usePrevious) {
/* 133 */       this.usePrevious = false;
/* 134 */       c = this.previous;
/*     */     } else {
/*     */       try {
/* 137 */         c = this.reader.read();
/* 138 */       } catch (IOException exception) {
/* 139 */         throw new JSONException(exception);
/*     */       } 
/*     */       
/* 142 */       if (c <= 0) {
/* 143 */         this.eof = true;
/* 144 */         c = 0;
/*     */       } 
/*     */     } 
/* 147 */     this.index++;
/* 148 */     if (this.previous == '\r') {
/* 149 */       this.line++;
/* 150 */       this.character = (c == 10) ? 0L : 1L;
/* 151 */     } else if (c == 10) {
/* 152 */       this.line++;
/* 153 */       this.character = 0L;
/*     */     } else {
/* 155 */       this.character++;
/*     */     } 
/* 157 */     this.previous = (char)c;
/* 158 */     return this.previous;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public char next(char c) throws JSONException {
/* 169 */     char n = next();
/* 170 */     if (n != c) {
/* 171 */       throw syntaxError("Expected '" + c + "' and instead saw '" + n + "'");
/*     */     }
/* 173 */     return n;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String next(int n) throws JSONException {
/* 184 */     if (n == 0) {
/* 185 */       return "";
/*     */     }
/*     */     
/* 188 */     char[] chars = new char[n];
/* 189 */     int pos = 0;
/* 190 */     while (pos < n) {
/* 191 */       chars[pos] = next();
/* 192 */       if (end()) {
/* 193 */         throw syntaxError("Substring bounds error");
/*     */       }
/* 195 */       pos++;
/*     */     } 
/* 197 */     return new String(chars);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public char nextClean() throws JSONException {
/*     */     char c;
/*     */     do {
/* 209 */       c = next();
/* 210 */     } while (c != '\000' && c <= ' ');
/* 211 */     return c;
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
/*     */   public String nextString(char quote) throws JSONException {
/* 226 */     StringBuilder sb = new StringBuilder();
/*     */     while (true) {
/* 228 */       char c = next();
/* 229 */       switch (c) {
/*     */         case '\000':
/*     */         case '\n':
/*     */         case '\r':
/* 233 */           throw syntaxError("Unterminated string");
/*     */         case '\\':
/* 235 */           c = next();
/* 236 */           switch (c) {
/*     */             case 'b':
/* 238 */               sb.append('\b');
/*     */               continue;
/*     */             case 't':
/* 241 */               sb.append('\t');
/*     */               continue;
/*     */             case 'n':
/* 244 */               sb.append('\n');
/*     */               continue;
/*     */             case 'f':
/* 247 */               sb.append('\f');
/*     */               continue;
/*     */             case 'r':
/* 250 */               sb.append('\r');
/*     */               continue;
/*     */             case 'u':
/* 253 */               sb.append((char)Integer.parseInt(next(4), 16));
/*     */               continue;
/*     */             case '"':
/*     */             case '\'':
/*     */             case '/':
/*     */             case '\\':
/* 259 */               sb.append(c);
/*     */               continue;
/*     */           } 
/* 262 */           throw syntaxError("Illegal escape.");
/*     */       } 
/*     */ 
/*     */       
/* 266 */       if (c == quote) {
/* 267 */         return sb.toString();
/*     */       }
/* 269 */       sb.append(c);
/*     */     } 
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
/*     */   public String nextTo(char delimiter) throws JSONException {
/* 282 */     StringBuilder sb = new StringBuilder();
/*     */     while (true) {
/* 284 */       char c = next();
/* 285 */       if (c == delimiter || c == '\000' || c == '\n' || c == '\r') {
/* 286 */         if (c != '\000') {
/* 287 */           back();
/*     */         }
/* 289 */         return sb.toString().trim();
/*     */       } 
/* 291 */       sb.append(c);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String nextTo(String delimiters) throws JSONException {
/* 303 */     StringBuilder sb = new StringBuilder();
/*     */     while (true) {
/* 305 */       char c = next();
/* 306 */       if (delimiters.indexOf(c) >= 0 || c == '\000' || c == '\n' || c == '\r') {
/* 307 */         if (c != '\000') {
/* 308 */           back();
/*     */         }
/* 310 */         return sb.toString().trim();
/*     */       } 
/* 312 */       sb.append(c);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object nextValue() throws JSONException {
/* 323 */     char c = nextClean();
/*     */ 
/*     */     
/* 326 */     switch (c) {
/*     */       case '"':
/*     */       case '\'':
/* 329 */         return nextString(c);
/*     */       case '{':
/* 331 */         back();
/* 332 */         return new JSONObject(this, this.config);
/*     */       case '[':
/* 334 */         back();
/* 335 */         return new JSONArray(this, this.config);
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 344 */     StringBuilder sb = new StringBuilder();
/* 345 */     while (c >= ' ' && ",:]}/\\\"[{;=#".indexOf(c) < 0) {
/* 346 */       sb.append(c);
/* 347 */       c = next();
/*     */     } 
/* 349 */     back();
/*     */     
/* 351 */     String string = sb.toString().trim();
/* 352 */     if (0 == string.length()) {
/* 353 */       throw syntaxError("Missing value");
/*     */     }
/* 355 */     return InternalJSONUtil.stringToValue(string);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public char skipTo(char to) throws JSONException {
/*     */     char c;
/*     */     try {
/* 367 */       long startIndex = this.index;
/* 368 */       long startCharacter = this.character;
/* 369 */       long startLine = this.line;
/* 370 */       this.reader.mark(1000000);
/*     */       do {
/* 372 */         c = next();
/* 373 */         if (c == '\000') {
/* 374 */           this.reader.reset();
/* 375 */           this.index = startIndex;
/* 376 */           this.character = startCharacter;
/* 377 */           this.line = startLine;
/* 378 */           return c;
/*     */         } 
/* 380 */       } while (c != to);
/* 381 */     } catch (IOException exception) {
/* 382 */       throw new JSONException(exception);
/*     */     } 
/* 384 */     back();
/* 385 */     return c;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JSONException syntaxError(String message) {
/* 396 */     return new JSONException(message + this);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JSONArray toJSONArray() {
/* 405 */     JSONArray jsonArray = new JSONArray(this.config);
/* 406 */     if (nextClean() != '[') {
/* 407 */       throw syntaxError("A JSONArray text must start with '['");
/*     */     }
/* 409 */     if (nextClean() != ']') {
/* 410 */       back();
/*     */       while (true) {
/* 412 */         if (nextClean() == ',') {
/* 413 */           back();
/* 414 */           jsonArray.add(JSONNull.NULL);
/*     */         } else {
/* 416 */           back();
/* 417 */           jsonArray.add(nextValue());
/*     */         } 
/* 419 */         switch (nextClean()) {
/*     */           case ',':
/* 421 */             if (nextClean() == ']') {
/* 422 */               return jsonArray;
/*     */             }
/* 424 */             back();
/*     */             continue;
/*     */           case ']':
/* 427 */             return jsonArray;
/*     */         }  break;
/* 429 */       }  throw syntaxError("Expected a ',' or ']'");
/*     */     } 
/*     */ 
/*     */     
/* 433 */     return jsonArray;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/* 443 */     return " at " + this.index + " [character " + this.character + " line " + this.line + "]";
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\json\JSONTokener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */