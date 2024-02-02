/*     */ package com.sun.activation.registries;
/*     */ 
/*     */ import java.util.NoSuchElementException;
/*     */ import java.util.Vector;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ class LineTokenizer
/*     */ {
/*     */   private int currentPosition;
/*     */   private int maxPosition;
/*     */   private String str;
/* 220 */   private Vector stack = new Vector();
/*     */ 
/*     */ 
/*     */   
/*     */   private static final String singles = "=";
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public LineTokenizer(String str) {
/* 230 */     this.currentPosition = 0;
/* 231 */     this.str = str;
/* 232 */     this.maxPosition = str.length();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void skipWhiteSpace() {
/* 239 */     while (this.currentPosition < this.maxPosition && Character.isWhitespace(this.str.charAt(this.currentPosition)))
/*     */     {
/* 241 */       this.currentPosition++;
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean hasMoreTokens() {
/* 252 */     if (this.stack.size() > 0)
/* 253 */       return true; 
/* 254 */     skipWhiteSpace();
/* 255 */     return (this.currentPosition < this.maxPosition);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String nextToken() {
/* 266 */     int size = this.stack.size();
/* 267 */     if (size > 0) {
/* 268 */       String t = this.stack.elementAt(size - 1);
/* 269 */       this.stack.removeElementAt(size - 1);
/* 270 */       return t;
/*     */     } 
/* 272 */     skipWhiteSpace();
/*     */     
/* 274 */     if (this.currentPosition >= this.maxPosition) {
/* 275 */       throw new NoSuchElementException();
/*     */     }
/*     */     
/* 278 */     int start = this.currentPosition;
/* 279 */     char c = this.str.charAt(start);
/* 280 */     if (c == '"') {
/* 281 */       this.currentPosition++;
/* 282 */       boolean filter = false;
/* 283 */       while (this.currentPosition < this.maxPosition) {
/* 284 */         c = this.str.charAt(this.currentPosition++);
/* 285 */         if (c == '\\') {
/* 286 */           this.currentPosition++;
/* 287 */           filter = true; continue;
/* 288 */         }  if (c == '"') {
/*     */           String s;
/*     */           
/* 291 */           if (filter) {
/* 292 */             StringBuffer sb = new StringBuffer();
/* 293 */             for (int i = start + 1; i < this.currentPosition - 1; i++) {
/* 294 */               c = this.str.charAt(i);
/* 295 */               if (c != '\\')
/* 296 */                 sb.append(c); 
/*     */             } 
/* 298 */             s = sb.toString();
/*     */           } else {
/* 300 */             s = this.str.substring(start + 1, this.currentPosition - 1);
/* 301 */           }  return s;
/*     */         } 
/*     */       } 
/* 304 */     } else if ("=".indexOf(c) >= 0) {
/* 305 */       this.currentPosition++;
/*     */     } else {
/*     */       
/* 308 */       while (this.currentPosition < this.maxPosition && "=".indexOf(this.str.charAt(this.currentPosition)) < 0 && !Character.isWhitespace(this.str.charAt(this.currentPosition)))
/*     */       {
/* 310 */         this.currentPosition++;
/*     */       }
/*     */     } 
/* 313 */     return this.str.substring(start, this.currentPosition);
/*     */   }
/*     */   
/*     */   public void pushToken(String token) {
/* 317 */     this.stack.addElement(token);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\activation\registries\LineTokenizer.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */