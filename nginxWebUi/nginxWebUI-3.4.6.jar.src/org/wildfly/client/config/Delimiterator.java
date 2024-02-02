/*     */ package org.wildfly.client.config;
/*     */ 
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
/*     */ final class Delimiterator
/*     */   implements Iterator<String>
/*     */ {
/*     */   private final String subject;
/*     */   private final char delimiter;
/*     */   private int i;
/*  32 */   private static final int[] NO_INTS = new int[0];
/*  33 */   private static final long[] NO_LONGS = new long[0];
/*  34 */   private static final String[] NO_STRINGS = new String[0];
/*     */   
/*     */   Delimiterator(String subject, char delimiter) {
/*  37 */     this.subject = subject;
/*  38 */     this.delimiter = delimiter;
/*  39 */     this.i = 0;
/*     */   }
/*     */   
/*     */   static Delimiterator over(String subject, char delimiter) {
/*  43 */     return new Delimiterator(subject, delimiter);
/*     */   }
/*     */   
/*     */   public boolean hasNext() {
/*  47 */     return (this.i != -1);
/*     */   }
/*     */   
/*     */   public String next() {
/*  51 */     int i = this.i;
/*  52 */     if (i == -1) {
/*  53 */       throw new NoSuchElementException();
/*     */     }
/*  55 */     int n = this.subject.indexOf(this.delimiter, i);
/*     */     try {
/*  57 */       return (n == -1) ? this.subject.substring(i) : this.subject.substring(i, n);
/*     */     } finally {
/*  59 */       this.i = (n == -1) ? -1 : (n + 1);
/*     */     } 
/*     */   }
/*     */   
/*     */   public void remove() {
/*  64 */     throw new UnsupportedOperationException();
/*     */   }
/*     */   
/*     */   public String[] toStringArray() {
/*  68 */     return toStringArray(0);
/*     */   }
/*     */   
/*     */   String[] toStringArray(int count) {
/*  72 */     if (hasNext()) {
/*  73 */       String next = next();
/*  74 */       String[] strings = toStringArray(count + 1);
/*  75 */       strings[count] = next;
/*  76 */       return strings;
/*     */     } 
/*  78 */     return (count == 0) ? NO_STRINGS : new String[count];
/*     */   }
/*     */ 
/*     */   
/*     */   public int[] toIntArray() throws NumberFormatException {
/*  83 */     return toIntArray(0);
/*     */   }
/*     */   
/*     */   int[] toIntArray(int count) {
/*  87 */     if (hasNext()) {
/*  88 */       String next = next();
/*  89 */       int[] ints = toIntArray(count + 1);
/*  90 */       ints[count] = Integer.parseInt(next);
/*  91 */       return ints;
/*     */     } 
/*  93 */     return (count == 0) ? NO_INTS : new int[count];
/*     */   }
/*     */ 
/*     */   
/*     */   public long[] toLongArray() throws NumberFormatException {
/*  98 */     return toLongArray(0);
/*     */   }
/*     */   
/*     */   long[] toLongArray(int count) {
/* 102 */     if (hasNext()) {
/* 103 */       String next = next();
/* 104 */       long[] longs = toLongArray(count + 1);
/* 105 */       longs[count] = Long.parseLong(next);
/* 106 */       return longs;
/*     */     } 
/* 108 */     return (count == 0) ? NO_LONGS : new long[count];
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\wildfly\client\config\Delimiterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */