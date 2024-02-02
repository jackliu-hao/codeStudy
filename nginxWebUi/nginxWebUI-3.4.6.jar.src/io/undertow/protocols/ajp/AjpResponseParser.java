/*     */ package io.undertow.protocols.ajp;
/*     */ 
/*     */ import io.undertow.util.HeaderMap;
/*     */ import io.undertow.util.HttpString;
/*     */ import java.io.IOException;
/*     */ import java.nio.ByteBuffer;
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
/*     */ class AjpResponseParser
/*     */ {
/*  39 */   public static final AjpResponseParser INSTANCE = new AjpResponseParser();
/*     */   
/*     */   private static final int AB = 16706;
/*     */   
/*     */   public static final int BEGIN = 0;
/*     */   
/*     */   public static final int READING_MAGIC_NUMBER = 1;
/*     */   
/*     */   public static final int READING_DATA_SIZE = 2;
/*     */   
/*     */   public static final int READING_PREFIX_CODE = 3;
/*     */   public static final int READING_STATUS_CODE = 4;
/*     */   public static final int READING_REASON_PHRASE = 5;
/*     */   public static final int READING_NUM_HEADERS = 6;
/*     */   public static final int READING_HEADERS = 7;
/*     */   public static final int READING_PERSISTENT_BOOLEAN = 8;
/*     */   public static final int READING_BODY_CHUNK_LENGTH = 9;
/*     */   public static final int DONE = 10;
/*     */   int state;
/*     */   byte prefix;
/*  59 */   int numHeaders = 0;
/*     */   
/*     */   HttpString currentHeader;
/*     */   
/*     */   int statusCode;
/*     */   String reasonPhrase;
/*  65 */   HeaderMap headers = new HeaderMap();
/*     */   int readBodyChunkSize;
/*     */   public static final int STRING_LENGTH_MASK = -2147483648;
/*     */   
/*  69 */   public boolean isComplete() { return (this.state == 10); } public void parse(ByteBuffer buf) throws IOException { IntegerHolder integerHolder2; byte prefix; IntegerHolder integerHolder1;
/*     */     StringHolder stringHolder;
/*     */     IntegerHolder result;
/*     */     int readHeaders;
/*  73 */     if (!buf.hasRemaining()) {
/*     */       return;
/*     */     }
/*  76 */     switch (this.state) {
/*     */       case 0:
/*  78 */         integerHolder2 = parse16BitInteger(buf);
/*  79 */         if (!integerHolder2.readComplete) {
/*     */           return;
/*     */         }
/*  82 */         if (integerHolder2.value != 16706) {
/*  83 */           throw new IOException("Wrong magic number");
/*     */         }
/*     */ 
/*     */       
/*     */       case 2:
/*  88 */         integerHolder2 = parse16BitInteger(buf);
/*  89 */         if (!integerHolder2.readComplete) {
/*  90 */           this.state = 2;
/*     */           return;
/*     */         } 
/*     */       
/*     */       case 3:
/*  95 */         if (!buf.hasRemaining()) {
/*  96 */           this.state = 3;
/*     */           return;
/*     */         } 
/*  99 */         prefix = buf.get();
/* 100 */         this.prefix = prefix;
/* 101 */         if (prefix == 5) {
/* 102 */           this.state = 8; break;
/*     */         } 
/* 104 */         if (prefix == 3) {
/* 105 */           this.state = 9; break;
/*     */         } 
/* 107 */         if (prefix != 4 && prefix != 6) {
/* 108 */           this.state = 10;
/*     */           return;
/*     */         } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/*     */       case 4:
/* 117 */         integerHolder1 = parse16BitInteger(buf);
/* 118 */         if (integerHolder1.readComplete) {
/* 119 */           if (this.prefix == 4) {
/* 120 */             this.statusCode = integerHolder1.value;
/*     */           }
/*     */           else {
/*     */             
/* 124 */             this.state = 10;
/* 125 */             this.readBodyChunkSize = integerHolder1.value;
/*     */             return;
/*     */           } 
/*     */         } else {
/* 129 */           this.state = 4;
/*     */           return;
/*     */         } 
/*     */       
/*     */       case 5:
/* 134 */         stringHolder = parseString(buf, false);
/* 135 */         if (stringHolder.readComplete) {
/* 136 */           this.reasonPhrase = stringHolder.value;
/*     */         } else {
/* 138 */           this.state = 5;
/*     */           return;
/*     */         } 
/*     */       
/*     */       case 6:
/* 143 */         result = parse16BitInteger(buf);
/* 144 */         if (!result.readComplete) {
/* 145 */           this.state = 6;
/*     */           return;
/*     */         } 
/* 148 */         this.numHeaders = result.value;
/*     */ 
/*     */       
/*     */       case 7:
/* 152 */         readHeaders = this.readHeaders;
/* 153 */         while (readHeaders < this.numHeaders) {
/* 154 */           if (this.currentHeader == null) {
/* 155 */             StringHolder stringHolder2 = parseString(buf, true);
/* 156 */             if (!stringHolder2.readComplete) {
/* 157 */               this.state = 7;
/* 158 */               this.readHeaders = readHeaders;
/*     */               return;
/*     */             } 
/* 161 */             if (stringHolder2.header != null) {
/* 162 */               this.currentHeader = stringHolder2.header;
/*     */             } else {
/* 164 */               this.currentHeader = HttpString.tryFromString(stringHolder2.value);
/*     */             } 
/*     */           } 
/* 167 */           StringHolder stringHolder1 = parseString(buf, false);
/* 168 */           if (!stringHolder1.readComplete) {
/* 169 */             this.state = 7;
/* 170 */             this.readHeaders = readHeaders;
/*     */             return;
/*     */           } 
/* 173 */           this.headers.add(this.currentHeader, stringHolder1.value);
/* 174 */           this.currentHeader = null;
/* 175 */           readHeaders++;
/*     */         } 
/*     */         break;
/*     */     } 
/*     */ 
/*     */     
/* 181 */     if (this.state == 8) {
/* 182 */       if (!buf.hasRemaining()) {
/*     */         return;
/*     */       }
/* 185 */       this.currentIntegerPart = buf.get();
/* 186 */       this.state = 10; return;
/*     */     } 
/* 188 */     if (this.state == 9) {
/* 189 */       IntegerHolder integerHolder = parse16BitInteger(buf);
/* 190 */       if (integerHolder.readComplete) {
/* 191 */         this.currentIntegerPart = integerHolder.value;
/* 192 */         this.state = 10;
/*     */       } 
/*     */       return;
/*     */     } 
/* 196 */     this.state = 10; }
/*     */ 
/*     */ 
/*     */   
/*     */   protected HttpString headers(int offset) {
/* 201 */     return AjpConstants.HTTP_HEADERS_ARRAY[offset];
/*     */   }
/*     */   
/*     */   public HeaderMap getHeaders() {
/* 205 */     return this.headers;
/*     */   }
/*     */   
/*     */   public int getStatusCode() {
/* 209 */     return this.statusCode;
/*     */   }
/*     */   
/*     */   public String getReasonPhrase() {
/* 213 */     return this.reasonPhrase;
/*     */   }
/*     */   
/*     */   public int getReadBodyChunkSize() {
/* 217 */     return this.readBodyChunkSize;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 225 */   public int stringLength = -1;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public StringBuilder currentString;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 236 */   public int currentIntegerPart = -1;
/*     */   boolean containsUrlCharacters = false;
/* 238 */   public int readHeaders = 0;
/*     */ 
/*     */   
/*     */   public void reset() {
/* 242 */     this.state = 0;
/* 243 */     this.prefix = 0;
/* 244 */     this.numHeaders = 0;
/* 245 */     this.currentHeader = null;
/*     */     
/* 247 */     this.statusCode = 0;
/* 248 */     this.reasonPhrase = null;
/* 249 */     this.headers = new HeaderMap();
/* 250 */     this.stringLength = -1;
/* 251 */     this.currentString = null;
/* 252 */     this.currentIntegerPart = -1;
/* 253 */     this.readHeaders = 0;
/*     */   }
/*     */   
/*     */   protected IntegerHolder parse16BitInteger(ByteBuffer buf) {
/* 257 */     if (!buf.hasRemaining()) {
/* 258 */       return new IntegerHolder(-1, false);
/*     */     }
/* 260 */     int number = this.currentIntegerPart;
/* 261 */     if (number == -1) {
/* 262 */       number = buf.get() & 0xFF;
/*     */     }
/* 264 */     if (buf.hasRemaining()) {
/* 265 */       byte b = buf.get();
/* 266 */       int result = ((0xFF & number) << 8) + (b & 0xFF);
/* 267 */       this.currentIntegerPart = -1;
/* 268 */       return new IntegerHolder(result, true);
/*     */     } 
/* 270 */     this.currentIntegerPart = number;
/* 271 */     return new IntegerHolder(-1, false);
/*     */   }
/*     */ 
/*     */   
/*     */   protected StringHolder parseString(ByteBuffer buf, boolean header) {
/* 276 */     boolean containsUrlCharacters = this.containsUrlCharacters;
/* 277 */     if (!buf.hasRemaining()) {
/* 278 */       return new StringHolder(null, false, false);
/*     */     }
/* 280 */     int stringLength = this.stringLength;
/* 281 */     if (stringLength == -1) {
/* 282 */       int number = buf.get() & 0xFF;
/* 283 */       if (buf.hasRemaining()) {
/* 284 */         byte b = buf.get();
/* 285 */         stringLength = ((0xFF & number) << 8) + (b & 0xFF);
/*     */       } else {
/* 287 */         this.stringLength = number | Integer.MIN_VALUE;
/* 288 */         return new StringHolder(null, false, false);
/*     */       } 
/* 290 */     } else if ((stringLength & Integer.MIN_VALUE) != 0) {
/* 291 */       int number = stringLength & Integer.MAX_VALUE;
/* 292 */       stringLength = ((0xFF & number) << 8) + (buf.get() & 0xFF);
/*     */     } 
/* 294 */     if (header && (stringLength & 0xFF00) != 0) {
/* 295 */       this.stringLength = -1;
/* 296 */       return new StringHolder(headers(stringLength & 0xFF));
/*     */     } 
/* 298 */     if (stringLength == 65535) {
/*     */       
/* 300 */       this.stringLength = -1;
/* 301 */       return new StringHolder(null, true, false);
/*     */     } 
/* 303 */     StringBuilder builder = this.currentString;
/*     */     
/* 305 */     if (builder == null) {
/* 306 */       builder = new StringBuilder();
/* 307 */       this.currentString = builder;
/*     */     } 
/* 309 */     int length = builder.length();
/* 310 */     while (length < stringLength) {
/* 311 */       if (!buf.hasRemaining()) {
/* 312 */         this.stringLength = stringLength;
/* 313 */         this.containsUrlCharacters = containsUrlCharacters;
/* 314 */         return new StringHolder(null, false, false);
/*     */       } 
/* 316 */       char c = (char)buf.get();
/* 317 */       if (c == '+' || c == '%') {
/* 318 */         containsUrlCharacters = true;
/*     */       }
/* 320 */       builder.append(c);
/* 321 */       length++;
/*     */     } 
/*     */     
/* 324 */     if (buf.hasRemaining()) {
/* 325 */       buf.get();
/* 326 */       this.currentString = null;
/* 327 */       this.stringLength = -1;
/* 328 */       this.containsUrlCharacters = false;
/* 329 */       return new StringHolder(builder.toString(), true, containsUrlCharacters);
/*     */     } 
/* 331 */     this.stringLength = stringLength;
/* 332 */     this.containsUrlCharacters = containsUrlCharacters;
/* 333 */     return new StringHolder(null, false, false);
/*     */   }
/*     */   
/*     */   protected static class IntegerHolder
/*     */   {
/*     */     public final int value;
/*     */     public final boolean readComplete;
/*     */     
/*     */     private IntegerHolder(int value, boolean readComplete) {
/* 342 */       this.value = value;
/* 343 */       this.readComplete = readComplete;
/*     */     }
/*     */   }
/*     */   
/*     */   protected static class StringHolder {
/*     */     public final String value;
/*     */     public final HttpString header;
/*     */     public final boolean readComplete;
/*     */     public final boolean containsUrlCharacters;
/*     */     
/*     */     private StringHolder(String value, boolean readComplete, boolean containsUrlCharacters) {
/* 354 */       this.value = value;
/* 355 */       this.readComplete = readComplete;
/* 356 */       this.containsUrlCharacters = containsUrlCharacters;
/* 357 */       this.header = null;
/*     */     }
/*     */     
/*     */     private StringHolder(HttpString value) {
/* 361 */       this.value = null;
/* 362 */       this.readComplete = true;
/* 363 */       this.header = value;
/* 364 */       this.containsUrlCharacters = false;
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\protocols\ajp\AjpResponseParser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */