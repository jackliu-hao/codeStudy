/*     */ package org.apache.http.protocol;
/*     */ 
/*     */ import java.util.Collection;
/*     */ import java.util.HashMap;
/*     */ import java.util.LinkedList;
/*     */ import java.util.Map;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ final class ChainBuilder<E>
/*     */ {
/*  49 */   private final LinkedList<E> list = new LinkedList<E>();
/*  50 */   private final Map<Class<?>, E> uniqueClasses = new HashMap<Class<?>, E>();
/*     */ 
/*     */   
/*     */   private void ensureUnique(E e) {
/*  54 */     E previous = this.uniqueClasses.remove(e.getClass());
/*  55 */     if (previous != null) {
/*  56 */       this.list.remove(previous);
/*     */     }
/*  58 */     this.uniqueClasses.put(e.getClass(), e);
/*     */   }
/*     */   
/*     */   public ChainBuilder<E> addFirst(E e) {
/*  62 */     if (e == null) {
/*  63 */       return this;
/*     */     }
/*  65 */     ensureUnique(e);
/*  66 */     this.list.addFirst(e);
/*  67 */     return this;
/*     */   }
/*     */   
/*     */   public ChainBuilder<E> addLast(E e) {
/*  71 */     if (e == null) {
/*  72 */       return this;
/*     */     }
/*  74 */     ensureUnique(e);
/*  75 */     this.list.addLast(e);
/*  76 */     return this;
/*     */   }
/*     */   
/*     */   public ChainBuilder<E> addAllFirst(Collection<E> c) {
/*  80 */     if (c == null) {
/*  81 */       return this;
/*     */     }
/*  83 */     for (E e : c) {
/*  84 */       addFirst(e);
/*     */     }
/*  86 */     return this;
/*     */   }
/*     */   
/*     */   public ChainBuilder<E> addAllFirst(E... c) {
/*  90 */     if (c == null) {
/*  91 */       return this;
/*     */     }
/*  93 */     for (E e : c) {
/*  94 */       addFirst(e);
/*     */     }
/*  96 */     return this;
/*     */   }
/*     */   
/*     */   public ChainBuilder<E> addAllLast(Collection<E> c) {
/* 100 */     if (c == null) {
/* 101 */       return this;
/*     */     }
/* 103 */     for (E e : c) {
/* 104 */       addLast(e);
/*     */     }
/* 106 */     return this;
/*     */   }
/*     */   
/*     */   public ChainBuilder<E> addAllLast(E... c) {
/* 110 */     if (c == null) {
/* 111 */       return this;
/*     */     }
/* 113 */     for (E e : c) {
/* 114 */       addLast(e);
/*     */     }
/* 116 */     return this;
/*     */   }
/*     */   
/*     */   public LinkedList<E> build() {
/* 120 */     return new LinkedList<E>(this.list);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\protocol\ChainBuilder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */