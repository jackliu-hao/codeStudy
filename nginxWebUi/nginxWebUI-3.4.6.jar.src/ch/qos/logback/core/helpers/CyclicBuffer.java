/*     */ package ch.qos.logback.core.helpers;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class CyclicBuffer<E>
/*     */ {
/*     */   E[] ea;
/*     */   int first;
/*     */   int last;
/*     */   int numElems;
/*     */   int maxSize;
/*     */   
/*     */   public CyclicBuffer(int maxSize) throws IllegalArgumentException {
/*  44 */     if (maxSize < 1) {
/*  45 */       throw new IllegalArgumentException("The maxSize argument (" + maxSize + ") is not a positive integer.");
/*     */     }
/*  47 */     init(maxSize);
/*     */   }
/*     */   
/*     */   public CyclicBuffer(CyclicBuffer<E> other) {
/*  51 */     this.maxSize = other.maxSize;
/*  52 */     this.ea = (E[])new Object[this.maxSize];
/*  53 */     System.arraycopy(other.ea, 0, this.ea, 0, this.maxSize);
/*  54 */     this.last = other.last;
/*  55 */     this.first = other.first;
/*  56 */     this.numElems = other.numElems;
/*     */   }
/*     */ 
/*     */   
/*     */   private void init(int maxSize) {
/*  61 */     this.maxSize = maxSize;
/*  62 */     this.ea = (E[])new Object[maxSize];
/*  63 */     this.first = 0;
/*  64 */     this.last = 0;
/*  65 */     this.numElems = 0;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void clear() {
/*  72 */     init(this.maxSize);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void add(E event) {
/*  80 */     this.ea[this.last] = event;
/*  81 */     if (++this.last == this.maxSize) {
/*  82 */       this.last = 0;
/*     */     }
/*  84 */     if (this.numElems < this.maxSize) {
/*  85 */       this.numElems++;
/*  86 */     } else if (++this.first == this.maxSize) {
/*  87 */       this.first = 0;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public E get(int i) {
/*  96 */     if (i < 0 || i >= this.numElems) {
/*  97 */       return null;
/*     */     }
/*  99 */     return this.ea[(this.first + i) % this.maxSize];
/*     */   }
/*     */   
/*     */   public int getMaxSize() {
/* 103 */     return this.maxSize;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public E get() {
/* 111 */     E r = null;
/*     */     
/* 113 */     this.numElems--;
/* 114 */     r = this.ea[this.first];
/* 115 */     this.ea[this.first] = null;
/* 116 */     if (this.numElems > 0 && ++this.first == this.maxSize) {
/* 117 */       this.first = 0;
/*     */     }
/* 119 */     return r;
/*     */   }
/*     */   
/*     */   public List<E> asList() {
/* 123 */     List<E> tList = new ArrayList<E>();
/* 124 */     for (int i = 0; i < length(); i++) {
/* 125 */       tList.add(get(i));
/*     */     }
/* 127 */     return tList;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int length() {
/* 135 */     return this.numElems;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void resize(int newSize) {
/* 146 */     if (newSize < 0) {
/* 147 */       throw new IllegalArgumentException("Negative array size [" + newSize + "] not allowed.");
/*     */     }
/* 149 */     if (newSize == this.numElems) {
/*     */       return;
/*     */     }
/*     */     
/* 153 */     E[] temp = (E[])new Object[newSize];
/*     */     
/* 155 */     int loopLen = (newSize < this.numElems) ? newSize : this.numElems;
/*     */     
/* 157 */     for (int i = 0; i < loopLen; i++) {
/* 158 */       temp[i] = this.ea[this.first];
/* 159 */       this.ea[this.first] = null;
/* 160 */       if (++this.first == this.numElems)
/* 161 */         this.first = 0; 
/*     */     } 
/* 163 */     this.ea = temp;
/* 164 */     this.first = 0;
/* 165 */     this.numElems = loopLen;
/* 166 */     this.maxSize = newSize;
/* 167 */     if (loopLen == newSize) {
/* 168 */       this.last = 0;
/*     */     } else {
/* 170 */       this.last = loopLen;
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\core\helpers\CyclicBuffer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */