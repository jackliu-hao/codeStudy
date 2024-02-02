/*     */ package io.undertow.server.handlers.proxy;
/*     */ 
/*     */ import java.nio.CharBuffer;
/*     */ import java.util.Iterator;
/*     */ import java.util.NoSuchElementException;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class RouteIteratorFactory
/*     */ {
/*     */   private final RouteParsingStrategy routeParsingStrategy;
/*     */   private final ParsingCompatibility parsingCompatibility;
/*     */   private final String rankedRouteDelimiter;
/*     */   
/*     */   public enum ParsingCompatibility
/*     */   {
/*  35 */     MOD_JK,
/*  36 */     MOD_CLUSTER;
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
/*     */   public RouteIteratorFactory(RouteParsingStrategy routeParsingStrategy, ParsingCompatibility parsingCompatibility) {
/*  48 */     this(routeParsingStrategy, parsingCompatibility, null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public RouteIteratorFactory(RouteParsingStrategy routeParsingStrategy, ParsingCompatibility parsingCompatibility, String rankedRouteDelimiter) {
/*  57 */     if (routeParsingStrategy == RouteParsingStrategy.RANKED && rankedRouteDelimiter == null) {
/*  58 */       throw new IllegalArgumentException();
/*     */     }
/*  60 */     this.routeParsingStrategy = routeParsingStrategy;
/*  61 */     this.parsingCompatibility = parsingCompatibility;
/*  62 */     this.rankedRouteDelimiter = rankedRouteDelimiter;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Iterator<CharSequence> iterator(String sessionId) {
/*  72 */     return new RouteIterator(sessionId);
/*     */   }
/*     */   
/*     */   private class RouteIterator
/*     */     implements Iterator<CharSequence>
/*     */   {
/*     */     private final String sessionId;
/*     */     private boolean nextResolved;
/*     */     private int nextPos;
/*     */     private CharSequence next;
/*     */     
/*     */     RouteIterator(String sessionId) {
/*  84 */       this.sessionId = sessionId;
/*     */       
/*  86 */       if (RouteIteratorFactory.this.routeParsingStrategy == RouteParsingStrategy.NONE) {
/*  87 */         this.nextResolved = true;
/*  88 */         this.next = null;
/*     */       } else {
/*  90 */         int index = (sessionId == null) ? -1 : sessionId.indexOf('.');
/*  91 */         if (index == -1) {
/*     */           
/*  93 */           this.nextResolved = true;
/*  94 */           this.next = null;
/*     */         } else {
/*  96 */           this.nextPos = index + 1;
/*     */         } 
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean hasNext() {
/* 103 */       resolveNext();
/*     */       
/* 105 */       return (this.next != null);
/*     */     }
/*     */ 
/*     */     
/*     */     public CharSequence next() {
/* 110 */       resolveNext();
/*     */       
/* 112 */       if (this.next != null) {
/* 113 */         CharSequence result = this.next;
/* 114 */         this.nextResolved = (RouteIteratorFactory.this.routeParsingStrategy != RouteParsingStrategy.RANKED);
/* 115 */         this.next = null;
/*     */         
/* 117 */         return result;
/*     */       } 
/* 119 */       throw new NoSuchElementException();
/*     */     }
/*     */     
/*     */     private void resolveNext() {
/* 123 */       if (!this.nextResolved) {
/* 124 */         if (RouteIteratorFactory.this.routeParsingStrategy != RouteParsingStrategy.RANKED) {
/* 125 */           if (RouteIteratorFactory.this.parsingCompatibility == RouteIteratorFactory.ParsingCompatibility.MOD_JK) {
/*     */ 
/*     */ 
/*     */             
/* 129 */             int last = this.sessionId.indexOf('.', this.nextPos);
/* 130 */             if (last == -1) {
/* 131 */               last = this.sessionId.length();
/*     */             }
/* 133 */             this.next = CharBuffer.wrap(this.sessionId, this.nextPos, last);
/*     */           } else {
/*     */             
/* 136 */             this.next = CharBuffer.wrap(this.sessionId, this.nextPos, this.sessionId.length());
/*     */           } 
/* 138 */         } else if (this.nextPos >= this.sessionId.length()) {
/* 139 */           this.next = null;
/*     */         } else {
/* 141 */           int currentPos = this.sessionId.indexOf(RouteIteratorFactory.this.rankedRouteDelimiter, this.nextPos);
/* 142 */           this.next = CharBuffer.wrap(this.sessionId, this.nextPos, (currentPos != -1) ? currentPos : this.sessionId.length());
/* 143 */           this.nextPos += this.next.length() + RouteIteratorFactory.this.rankedRouteDelimiter.length();
/*     */         } 
/* 145 */         this.nextResolved = true;
/*     */       } 
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\handlers\proxy\RouteIteratorFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */