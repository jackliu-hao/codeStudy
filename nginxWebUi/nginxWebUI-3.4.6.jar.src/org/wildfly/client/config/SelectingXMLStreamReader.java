/*     */ package org.wildfly.client.config;
/*     */ 
/*     */ import java.util.NoSuchElementException;
/*     */ import java.util.Set;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ class SelectingXMLStreamReader
/*     */   extends AbstractDelegatingXMLStreamReader
/*     */ {
/*     */   private final Set<String> namespaces;
/*     */   private int state;
/*     */   private int level;
/*     */   private static final int ST_SEEKING = 0;
/*     */   private static final int ST_FOUND_PRE = 1;
/*     */   private static final int ST_FOUND = 2;
/*     */   private static final int ST_DONE = 3;
/*     */   
/*     */   SelectingXMLStreamReader(boolean closeDelegate, ConfigurationXMLStreamReader delegate, Set<String> namespaces) {
/*  38 */     super(closeDelegate, delegate);
/*  39 */     this.namespaces = namespaces;
/*     */   }
/*     */   
/*     */   public boolean hasNext() throws ConfigXMLParseException {
/*  43 */     switch (this.state) {
/*     */       case 0:
/*  45 */         while (super.hasNext()) {
/*  46 */           int next = super.next();
/*  47 */           switch (next) {
/*     */             case 1:
/*  49 */               if (this.namespaces.contains(getNamespaceURI())) {
/*  50 */                 this.state = 1;
/*  51 */                 return true;
/*     */               } 
/*  53 */               getDelegate().skipContent();
/*     */ 
/*     */ 
/*     */             
/*     */             case 2:
/*  58 */               this.state = 3;
/*  59 */               return false;
/*     */           } 
/*     */         
/*     */         } 
/*  63 */         return false;
/*     */       
/*     */       case 1:
/*  66 */         return true;
/*     */       
/*     */       case 2:
/*  69 */         return super.hasNext();
/*     */       
/*     */       case 3:
/*  72 */         return false;
/*     */     } 
/*     */     
/*  75 */     throw new IllegalStateException();
/*     */   }
/*     */ 
/*     */   
/*     */   public int next() throws ConfigXMLParseException {
/*     */     int next;
/*  81 */     if (!hasNext()) throw new NoSuchElementException(); 
/*  82 */     switch (this.state) {
/*     */       case 1:
/*  84 */         this.state = 2;
/*  85 */         return getEventType();
/*     */       
/*     */       case 2:
/*  88 */         next = super.next();
/*  89 */         switch (next) {
/*     */           case 1:
/*  91 */             this.level++;
/*     */             break;
/*     */           
/*     */           case 2:
/*  95 */             if (this.level-- == 0) {
/*  96 */               this.state = 3;
/*     */             }
/*     */             break;
/*     */         } 
/*     */         
/* 101 */         return next;
/*     */     } 
/*     */     
/* 104 */     throw new IllegalStateException();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\wildfly\client\config\SelectingXMLStreamReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */