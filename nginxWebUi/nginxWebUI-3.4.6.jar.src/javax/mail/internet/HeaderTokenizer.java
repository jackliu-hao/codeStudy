/*     */ package javax.mail.internet;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class HeaderTokenizer
/*     */ {
/*     */   private String string;
/*     */   private boolean skipComments;
/*     */   private String delimiters;
/*     */   private int currentPos;
/*     */   private int maxPos;
/*     */   private int nextPos;
/*     */   private int peekPos;
/*     */   public static final String RFC822 = "()<>@,;:\\\"\t .[]";
/*     */   public static final String MIME = "()<>@,;:\\\"\t []/?=";
/*     */   
/*     */   public static class Token
/*     */   {
/*     */     private int type;
/*     */     private String value;
/*     */     public static final int ATOM = -1;
/*     */     public static final int QUOTEDSTRING = -2;
/*     */     public static final int COMMENT = -3;
/*     */     public static final int EOF = -4;
/*     */     
/*     */     public Token(int type, String value) {
/*  97 */       this.type = type;
/*  98 */       this.value = value;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public int getType() {
/* 118 */       return this.type;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public String getValue() {
/* 130 */       return this.value;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 153 */   private static final Token EOFToken = new Token(-4, null);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HeaderTokenizer(String header, String delimiters, boolean skipComments) {
/* 168 */     this.string = (header == null) ? "" : header;
/* 169 */     this.skipComments = skipComments;
/* 170 */     this.delimiters = delimiters;
/* 171 */     this.currentPos = this.nextPos = this.peekPos = 0;
/* 172 */     this.maxPos = this.string.length();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HeaderTokenizer(String header, String delimiters) {
/* 182 */     this(header, delimiters, true);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HeaderTokenizer(String header) {
/* 191 */     this(header, "()<>@,;:\\\"\t .[]");
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
/*     */   public Token next() throws ParseException {
/* 204 */     return next(false, false);
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
/*     */   
/*     */   Token next(char endOfAtom) throws ParseException {
/* 222 */     return next(endOfAtom, false);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   Token next(char endOfAtom, boolean keepEscapes) throws ParseException {
/* 244 */     this.currentPos = this.nextPos;
/* 245 */     Token tk = getNext(endOfAtom, keepEscapes);
/* 246 */     this.nextPos = this.peekPos = this.currentPos;
/* 247 */     return tk;
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
/*     */   public Token peek() throws ParseException {
/* 262 */     this.currentPos = this.peekPos;
/* 263 */     Token tk = getNext(false, false);
/* 264 */     this.peekPos = this.currentPos;
/* 265 */     return tk;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getRemainder() {
/* 275 */     return this.string.substring(this.nextPos);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private Token getNext(char endOfAtom, boolean keepEscapes) throws ParseException {
/* 286 */     if (this.currentPos >= this.maxPos) {
/* 287 */       return EOFToken;
/*     */     }
/*     */     
/* 290 */     if (skipWhiteSpace() == -4) {
/* 291 */       return EOFToken;
/*     */     }
/*     */ 
/*     */     
/* 295 */     boolean filter = false;
/*     */     
/* 297 */     char c = this.string.charAt(this.currentPos);
/*     */ 
/*     */ 
/*     */     
/* 301 */     while (c == '(') {
/*     */ 
/*     */       
/* 304 */       int i = ++this.currentPos, nesting = 1;
/* 305 */       for (; nesting > 0 && this.currentPos < this.maxPos; 
/* 306 */         this.currentPos++) {
/* 307 */         c = this.string.charAt(this.currentPos);
/* 308 */         if (c == '\\') {
/* 309 */           this.currentPos++;
/* 310 */           filter = true;
/* 311 */         } else if (c == '\r') {
/* 312 */           filter = true;
/* 313 */         } else if (c == '(') {
/* 314 */           nesting++;
/* 315 */         } else if (c == ')') {
/* 316 */           nesting--;
/*     */         } 
/* 318 */       }  if (nesting != 0) {
/* 319 */         throw new ParseException("Unbalanced comments");
/*     */       }
/* 321 */       if (!this.skipComments) {
/*     */         String s;
/*     */ 
/*     */         
/* 325 */         if (filter) {
/* 326 */           s = filterToken(this.string, i, this.currentPos - 1, keepEscapes);
/*     */         } else {
/* 328 */           s = this.string.substring(i, this.currentPos - 1);
/*     */         } 
/* 330 */         return new Token(-3, s);
/*     */       } 
/*     */ 
/*     */       
/* 334 */       if (skipWhiteSpace() == -4)
/* 335 */         return EOFToken; 
/* 336 */       c = this.string.charAt(this.currentPos);
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 341 */     if (c == '"') {
/* 342 */       this.currentPos++;
/* 343 */       return collectString('"', keepEscapes);
/*     */     } 
/*     */ 
/*     */     
/* 347 */     if (c < ' ' || c >= '' || this.delimiters.indexOf(c) >= 0) {
/* 348 */       if (endOfAtom > '\000' && c != endOfAtom)
/*     */       {
/*     */         
/* 351 */         return collectString(endOfAtom, keepEscapes);
/*     */       }
/* 353 */       this.currentPos++;
/* 354 */       char[] ch = new char[1];
/* 355 */       ch[0] = c;
/* 356 */       return new Token(c, new String(ch));
/*     */     } 
/*     */     
/*     */     int start;
/* 360 */     for (start = this.currentPos; this.currentPos < this.maxPos; this.currentPos++) {
/* 361 */       c = this.string.charAt(this.currentPos);
/*     */ 
/*     */       
/* 364 */       if (c < ' ' || c >= '' || c == '(' || c == ' ' || c == '"' || this.delimiters.indexOf(c) >= 0) {
/*     */         
/* 366 */         if (endOfAtom > '\000' && c != endOfAtom) {
/*     */ 
/*     */           
/* 369 */           this.currentPos = start;
/* 370 */           return collectString(endOfAtom, keepEscapes);
/*     */         } 
/*     */         break;
/*     */       } 
/*     */     } 
/* 375 */     return new Token(-1, this.string.substring(start, this.currentPos));
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private Token collectString(char eos, boolean keepEscapes) throws ParseException {
/* 381 */     boolean filter = false; int start;
/* 382 */     for (start = this.currentPos; this.currentPos < this.maxPos; this.currentPos++) {
/* 383 */       char c = this.string.charAt(this.currentPos);
/* 384 */       if (c == '\\') {
/* 385 */         this.currentPos++;
/* 386 */         filter = true;
/* 387 */       } else if (c == '\r') {
/* 388 */         filter = true;
/* 389 */       } else if (c == eos) {
/* 390 */         String str; this.currentPos++;
/*     */ 
/*     */         
/* 393 */         if (filter) {
/* 394 */           str = filterToken(this.string, start, this.currentPos - 1, keepEscapes);
/*     */         } else {
/* 396 */           str = this.string.substring(start, this.currentPos - 1);
/*     */         } 
/* 398 */         if (c != '"') {
/* 399 */           str = trimWhiteSpace(str);
/* 400 */           this.currentPos--;
/*     */         } 
/*     */         
/* 403 */         return new Token(-2, str);
/*     */       } 
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 410 */     if (eos == '"') {
/* 411 */       throw new ParseException("Unbalanced quoted string");
/*     */     }
/*     */ 
/*     */     
/* 415 */     if (filter) {
/* 416 */       s = filterToken(this.string, start, this.currentPos, keepEscapes);
/*     */     } else {
/* 418 */       s = this.string.substring(start, this.currentPos);
/* 419 */     }  String s = trimWhiteSpace(s);
/* 420 */     return new Token(-2, s);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private int skipWhiteSpace() {
/* 426 */     for (; this.currentPos < this.maxPos; this.currentPos++) {
/* 427 */       char c; if ((c = this.string.charAt(this.currentPos)) != ' ' && c != '\t' && c != '\r' && c != '\n')
/*     */       {
/* 429 */         return this.currentPos; } 
/* 430 */     }  return -4;
/*     */   }
/*     */ 
/*     */   
/*     */   private static String trimWhiteSpace(String s) {
/*     */     char c;
/*     */     int i;
/* 437 */     for (i = s.length() - 1; i >= 0 && ((
/* 438 */       c = s.charAt(i)) == ' ' || c == '\t' || c == '\r' || c == '\n'); i--);
/*     */ 
/*     */ 
/*     */     
/* 442 */     if (i <= 0) {
/* 443 */       return "";
/*     */     }
/* 445 */     return s.substring(0, i + 1);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static String filterToken(String s, int start, int end, boolean keepEscapes) {
/* 453 */     StringBuffer sb = new StringBuffer();
/*     */     
/* 455 */     boolean gotEscape = false;
/* 456 */     boolean gotCR = false;
/*     */     
/* 458 */     for (int i = start; i < end; i++) {
/* 459 */       char c = s.charAt(i);
/* 460 */       if (c == '\n' && gotCR) {
/*     */ 
/*     */         
/* 463 */         gotCR = false;
/*     */       }
/*     */       else {
/*     */         
/* 467 */         gotCR = false;
/* 468 */         if (!gotEscape) {
/*     */           
/* 470 */           if (c == '\\') {
/* 471 */             gotEscape = true;
/* 472 */           } else if (c == '\r') {
/* 473 */             gotCR = true;
/*     */           } else {
/* 475 */             sb.append(c);
/*     */           
/*     */           }
/*     */ 
/*     */         
/*     */         }
/*     */         else {
/*     */           
/* 483 */           if (keepEscapes)
/* 484 */             sb.append('\\'); 
/* 485 */           sb.append(c);
/* 486 */           gotEscape = false;
/*     */         } 
/*     */       } 
/* 489 */     }  return sb.toString();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\javax\mail\internet\HeaderTokenizer.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */