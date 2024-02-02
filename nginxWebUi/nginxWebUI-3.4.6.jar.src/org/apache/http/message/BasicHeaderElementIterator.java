/*     */ package org.apache.http.message;
/*     */ 
/*     */ import java.util.NoSuchElementException;
/*     */ import org.apache.http.FormattedHeader;
/*     */ import org.apache.http.Header;
/*     */ import org.apache.http.HeaderElement;
/*     */ import org.apache.http.HeaderElementIterator;
/*     */ import org.apache.http.HeaderIterator;
/*     */ import org.apache.http.util.Args;
/*     */ import org.apache.http.util.CharArrayBuffer;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class BasicHeaderElementIterator
/*     */   implements HeaderElementIterator
/*     */ {
/*     */   private final HeaderIterator headerIt;
/*     */   private final HeaderValueParser parser;
/*  50 */   private HeaderElement currentElement = null;
/*  51 */   private CharArrayBuffer buffer = null;
/*  52 */   private ParserCursor cursor = null;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public BasicHeaderElementIterator(HeaderIterator headerIterator, HeaderValueParser parser) {
/*  60 */     this.headerIt = (HeaderIterator)Args.notNull(headerIterator, "Header iterator");
/*  61 */     this.parser = (HeaderValueParser)Args.notNull(parser, "Parser");
/*     */   }
/*     */ 
/*     */   
/*     */   public BasicHeaderElementIterator(HeaderIterator headerIterator) {
/*  66 */     this(headerIterator, BasicHeaderValueParser.INSTANCE);
/*     */   }
/*     */ 
/*     */   
/*     */   private void bufferHeaderValue() {
/*  71 */     this.cursor = null;
/*  72 */     this.buffer = null;
/*  73 */     while (this.headerIt.hasNext()) {
/*  74 */       Header h = this.headerIt.nextHeader();
/*  75 */       if (h instanceof FormattedHeader) {
/*  76 */         this.buffer = ((FormattedHeader)h).getBuffer();
/*  77 */         this.cursor = new ParserCursor(0, this.buffer.length());
/*  78 */         this.cursor.updatePos(((FormattedHeader)h).getValuePos());
/*     */         break;
/*     */       } 
/*  81 */       String value = h.getValue();
/*  82 */       if (value != null) {
/*  83 */         this.buffer = new CharArrayBuffer(value.length());
/*  84 */         this.buffer.append(value);
/*  85 */         this.cursor = new ParserCursor(0, this.buffer.length());
/*     */         break;
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private void parseNextElement() {
/*  93 */     while (this.headerIt.hasNext() || this.cursor != null) {
/*  94 */       if (this.cursor == null || this.cursor.atEnd())
/*     */       {
/*  96 */         bufferHeaderValue();
/*     */       }
/*     */       
/*  99 */       if (this.cursor != null) {
/*     */         
/* 101 */         while (!this.cursor.atEnd()) {
/* 102 */           HeaderElement e = this.parser.parseHeaderElement(this.buffer, this.cursor);
/* 103 */           if (!e.getName().isEmpty() || e.getValue() != null) {
/*     */             
/* 105 */             this.currentElement = e;
/*     */             
/*     */             return;
/*     */           } 
/*     */         } 
/* 110 */         if (this.cursor.atEnd()) {
/*     */           
/* 112 */           this.cursor = null;
/* 113 */           this.buffer = null;
/*     */         } 
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean hasNext() {
/* 121 */     if (this.currentElement == null) {
/* 122 */       parseNextElement();
/*     */     }
/* 124 */     return (this.currentElement != null);
/*     */   }
/*     */ 
/*     */   
/*     */   public HeaderElement nextElement() throws NoSuchElementException {
/* 129 */     if (this.currentElement == null) {
/* 130 */       parseNextElement();
/*     */     }
/*     */     
/* 133 */     if (this.currentElement == null) {
/* 134 */       throw new NoSuchElementException("No more header elements available");
/*     */     }
/*     */     
/* 137 */     HeaderElement element = this.currentElement;
/* 138 */     this.currentElement = null;
/* 139 */     return element;
/*     */   }
/*     */ 
/*     */   
/*     */   public final Object next() throws NoSuchElementException {
/* 144 */     return nextElement();
/*     */   }
/*     */ 
/*     */   
/*     */   public void remove() throws UnsupportedOperationException {
/* 149 */     throw new UnsupportedOperationException("Remove not supported");
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\message\BasicHeaderElementIterator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */