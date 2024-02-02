/*     */ package org.apache.http.message;
/*     */ 
/*     */ import java.util.NoSuchElementException;
/*     */ import org.apache.http.Header;
/*     */ import org.apache.http.HeaderIterator;
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
/*     */ public class BasicHeaderIterator
/*     */   implements HeaderIterator
/*     */ {
/*     */   protected final Header[] allHeaders;
/*     */   protected int currentIndex;
/*     */   protected String headerName;
/*     */   
/*     */   public BasicHeaderIterator(Header[] headers, String name) {
/*  76 */     this.allHeaders = (Header[])Args.notNull(headers, "Header array");
/*  77 */     this.headerName = name;
/*  78 */     this.currentIndex = findNext(-1);
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
/*     */   protected int findNext(int pos) {
/*  92 */     int from = pos;
/*  93 */     if (from < -1) {
/*  94 */       return -1;
/*     */     }
/*     */     
/*  97 */     int to = this.allHeaders.length - 1;
/*  98 */     boolean found = false;
/*  99 */     while (!found && from < to) {
/* 100 */       from++;
/* 101 */       found = filterHeader(from);
/*     */     } 
/* 103 */     return found ? from : -1;
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
/*     */   protected boolean filterHeader(int index) {
/* 116 */     return (this.headerName == null || this.headerName.equalsIgnoreCase(this.allHeaders[index].getName()));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean hasNext() {
/* 124 */     return (this.currentIndex >= 0);
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
/*     */   public Header nextHeader() throws NoSuchElementException {
/* 139 */     int current = this.currentIndex;
/* 140 */     if (current < 0) {
/* 141 */       throw new NoSuchElementException("Iteration already finished.");
/*     */     }
/*     */     
/* 144 */     this.currentIndex = findNext(current);
/*     */     
/* 146 */     return this.allHeaders[current];
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
/*     */   public final Object next() throws NoSuchElementException {
/* 161 */     return nextHeader();
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
/*     */   public void remove() throws UnsupportedOperationException {
/* 174 */     throw new UnsupportedOperationException("Removing headers is not supported.");
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\message\BasicHeaderIterator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */