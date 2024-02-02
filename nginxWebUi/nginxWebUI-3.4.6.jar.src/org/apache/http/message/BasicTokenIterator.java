/*     */ package org.apache.http.message;
/*     */ 
/*     */ import java.util.NoSuchElementException;
/*     */ import org.apache.http.HeaderIterator;
/*     */ import org.apache.http.ParseException;
/*     */ import org.apache.http.TokenIterator;
/*     */ import org.apache.http.util.Args;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class BasicTokenIterator
/*     */   implements TokenIterator
/*     */ {
/*     */   public static final String HTTP_SEPARATORS = " ,;=()<>@:\\\"/[]?{}\t";
/*     */   protected final HeaderIterator headerIt;
/*     */   protected String currentHeader;
/*     */   protected String currentToken;
/*     */   protected int searchPos;
/*     */   
/*     */   public BasicTokenIterator(HeaderIterator headerIterator) {
/*  83 */     this.headerIt = (HeaderIterator)Args.notNull(headerIterator, "Header iterator");
/*  84 */     this.searchPos = findNext(-1);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean hasNext() {
/*  91 */     return (this.currentToken != null);
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
/*     */   public String nextToken() throws NoSuchElementException, ParseException {
/* 107 */     if (this.currentToken == null) {
/* 108 */       throw new NoSuchElementException("Iteration already finished.");
/*     */     }
/*     */     
/* 111 */     String result = this.currentToken;
/*     */     
/* 113 */     this.searchPos = findNext(this.searchPos);
/*     */     
/* 115 */     return result;
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
/*     */   public final Object next() throws NoSuchElementException, ParseException {
/* 131 */     return nextToken();
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
/*     */   public final void remove() throws UnsupportedOperationException {
/* 144 */     throw new UnsupportedOperationException("Removing tokens is not supported.");
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
/*     */   protected int findNext(int pos) throws ParseException {
/* 166 */     int from = pos;
/* 167 */     if (from < 0) {
/*     */       
/* 169 */       if (!this.headerIt.hasNext()) {
/* 170 */         return -1;
/*     */       }
/* 172 */       this.currentHeader = this.headerIt.nextHeader().getValue();
/* 173 */       from = 0;
/*     */     } else {
/*     */       
/* 176 */       from = findTokenSeparator(from);
/*     */     } 
/*     */     
/* 179 */     int start = findTokenStart(from);
/* 180 */     if (start < 0) {
/* 181 */       this.currentToken = null;
/* 182 */       return -1;
/*     */     } 
/*     */     
/* 185 */     int end = findTokenEnd(start);
/* 186 */     this.currentToken = createToken(this.currentHeader, start, end);
/* 187 */     return end;
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
/*     */ 
/*     */ 
/*     */   
/*     */   protected String createToken(String value, int start, int end) {
/* 213 */     return value.substring(start, end);
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
/*     */   protected int findTokenStart(int pos) {
/* 228 */     int from = Args.notNegative(pos, "Search position");
/* 229 */     boolean found = false;
/* 230 */     while (!found && this.currentHeader != null) {
/*     */       
/* 232 */       int to = this.currentHeader.length();
/* 233 */       while (!found && from < to) {
/*     */         
/* 235 */         char ch = this.currentHeader.charAt(from);
/* 236 */         if (isTokenSeparator(ch) || isWhitespace(ch)) {
/*     */           
/* 238 */           from++; continue;
/* 239 */         }  if (isTokenChar(this.currentHeader.charAt(from))) {
/*     */           
/* 241 */           found = true; continue;
/*     */         } 
/* 243 */         throw new ParseException("Invalid character before token (pos " + from + "): " + this.currentHeader);
/*     */       } 
/*     */ 
/*     */ 
/*     */       
/* 248 */       if (!found) {
/* 249 */         if (this.headerIt.hasNext()) {
/* 250 */           this.currentHeader = this.headerIt.nextHeader().getValue();
/* 251 */           from = 0; continue;
/*     */         } 
/* 253 */         this.currentHeader = null;
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/* 258 */     return found ? from : -1;
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
/*     */   protected int findTokenSeparator(int pos) {
/* 280 */     int from = Args.notNegative(pos, "Search position");
/* 281 */     boolean found = false;
/* 282 */     int to = this.currentHeader.length();
/* 283 */     while (!found && from < to) {
/* 284 */       char ch = this.currentHeader.charAt(from);
/* 285 */       if (isTokenSeparator(ch)) {
/* 286 */         found = true; continue;
/* 287 */       }  if (isWhitespace(ch)) {
/* 288 */         from++; continue;
/* 289 */       }  if (isTokenChar(ch)) {
/* 290 */         throw new ParseException("Tokens without separator (pos " + from + "): " + this.currentHeader);
/*     */       }
/*     */ 
/*     */       
/* 294 */       throw new ParseException("Invalid character after token (pos " + from + "): " + this.currentHeader);
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 300 */     return from;
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
/*     */   protected int findTokenEnd(int from) {
/* 316 */     Args.notNegative(from, "Search position");
/* 317 */     int to = this.currentHeader.length();
/* 318 */     int end = from + 1;
/* 319 */     while (end < to && isTokenChar(this.currentHeader.charAt(end))) {
/* 320 */       end++;
/*     */     }
/*     */     
/* 323 */     return end;
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
/*     */   protected boolean isTokenSeparator(char ch) {
/* 339 */     return (ch == ',');
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
/*     */   protected boolean isWhitespace(char ch) {
/* 358 */     return (ch == '\t' || Character.isSpaceChar(ch));
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
/*     */   protected boolean isTokenChar(char ch) {
/* 377 */     if (Character.isLetterOrDigit(ch)) {
/* 378 */       return true;
/*     */     }
/*     */ 
/*     */     
/* 382 */     if (Character.isISOControl(ch)) {
/* 383 */       return false;
/*     */     }
/*     */ 
/*     */     
/* 387 */     if (isHttpSeparator(ch)) {
/* 388 */       return false;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 397 */     return true;
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
/*     */   protected boolean isHttpSeparator(char ch) {
/* 412 */     return (" ,;=()<>@:\\\"/[]?{}\t".indexOf(ch) >= 0);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\message\BasicTokenIterator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */