/*     */ package org.apache.http.message;
/*     */ 
/*     */ import java.util.List;
/*     */ import java.util.NoSuchElementException;
/*     */ import org.apache.http.Header;
/*     */ import org.apache.http.HeaderIterator;
/*     */ import org.apache.http.util.Args;
/*     */ import org.apache.http.util.Asserts;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class BasicListHeaderIterator
/*     */   implements HeaderIterator
/*     */ {
/*     */   protected final List<Header> allHeaders;
/*     */   protected int currentIndex;
/*     */   protected int lastIndex;
/*     */   protected String headerName;
/*     */   
/*     */   public BasicListHeaderIterator(List<Header> headers, String name) {
/*  84 */     this.allHeaders = (List<Header>)Args.notNull(headers, "Header list");
/*  85 */     this.headerName = name;
/*  86 */     this.currentIndex = findNext(-1);
/*  87 */     this.lastIndex = -1;
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
/* 101 */     int from = pos;
/* 102 */     if (from < -1) {
/* 103 */       return -1;
/*     */     }
/*     */     
/* 106 */     int to = this.allHeaders.size() - 1;
/* 107 */     boolean found = false;
/* 108 */     while (!found && from < to) {
/* 109 */       from++;
/* 110 */       found = filterHeader(from);
/*     */     } 
/* 112 */     return found ? from : -1;
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
/* 125 */     if (this.headerName == null) {
/* 126 */       return true;
/*     */     }
/*     */ 
/*     */     
/* 130 */     String name = ((Header)this.allHeaders.get(index)).getName();
/*     */     
/* 132 */     return this.headerName.equalsIgnoreCase(name);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean hasNext() {
/* 139 */     return (this.currentIndex >= 0);
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
/* 154 */     int current = this.currentIndex;
/* 155 */     if (current < 0) {
/* 156 */       throw new NoSuchElementException("Iteration already finished.");
/*     */     }
/*     */     
/* 159 */     this.lastIndex = current;
/* 160 */     this.currentIndex = findNext(current);
/*     */     
/* 162 */     return this.allHeaders.get(current);
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
/* 177 */     return nextHeader();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void remove() throws UnsupportedOperationException {
/* 187 */     Asserts.check((this.lastIndex >= 0), "No header to remove");
/* 188 */     this.allHeaders.remove(this.lastIndex);
/* 189 */     this.lastIndex = -1;
/* 190 */     this.currentIndex--;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\message\BasicListHeaderIterator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */