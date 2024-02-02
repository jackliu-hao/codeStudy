/*     */ package org.apache.commons.compress.archivers.sevenz;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.util.Collections;
/*     */ import java.util.LinkedList;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ class Folder
/*     */ {
/*     */   Coder[] coders;
/*     */   long totalInputStreams;
/*     */   long totalOutputStreams;
/*     */   BindPair[] bindPairs;
/*     */   long[] packedStreams;
/*     */   long[] unpackSizes;
/*     */   boolean hasCrc;
/*     */   long crc;
/*     */   int numUnpackSubStreams;
/*  49 */   static final Folder[] EMPTY_FOLDER_ARRAY = new Folder[0];
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   Iterable<Coder> getOrderedCoders() throws IOException {
/*  58 */     if (this.packedStreams == null || this.coders == null || this.packedStreams.length == 0 || this.coders.length == 0) {
/*  59 */       return Collections.emptyList();
/*     */     }
/*  61 */     LinkedList<Coder> l = new LinkedList<>();
/*  62 */     int current = (int)this.packedStreams[0];
/*  63 */     while (current >= 0 && current < this.coders.length) {
/*  64 */       if (l.contains(this.coders[current])) {
/*  65 */         throw new IOException("folder uses the same coder more than once in coder chain");
/*     */       }
/*  67 */       l.addLast(this.coders[current]);
/*  68 */       int pair = findBindPairForOutStream(current);
/*  69 */       current = (pair != -1) ? (int)(this.bindPairs[pair]).inIndex : -1;
/*     */     } 
/*  71 */     return l;
/*     */   }
/*     */   
/*     */   int findBindPairForInStream(int index) {
/*  75 */     if (this.bindPairs != null) {
/*  76 */       for (int i = 0; i < this.bindPairs.length; i++) {
/*  77 */         if ((this.bindPairs[i]).inIndex == index) {
/*  78 */           return i;
/*     */         }
/*     */       } 
/*     */     }
/*  82 */     return -1;
/*     */   }
/*     */   
/*     */   int findBindPairForOutStream(int index) {
/*  86 */     if (this.bindPairs != null) {
/*  87 */       for (int i = 0; i < this.bindPairs.length; i++) {
/*  88 */         if ((this.bindPairs[i]).outIndex == index) {
/*  89 */           return i;
/*     */         }
/*     */       } 
/*     */     }
/*  93 */     return -1;
/*     */   }
/*     */   
/*     */   long getUnpackSize() {
/*  97 */     if (this.totalOutputStreams == 0L) {
/*  98 */       return 0L;
/*     */     }
/* 100 */     for (int i = (int)this.totalOutputStreams - 1; i >= 0; i--) {
/* 101 */       if (findBindPairForOutStream(i) < 0) {
/* 102 */         return this.unpackSizes[i];
/*     */       }
/*     */     } 
/* 105 */     return 0L;
/*     */   }
/*     */   
/*     */   long getUnpackSizeForCoder(Coder coder) {
/* 109 */     if (this.coders != null) {
/* 110 */       for (int i = 0; i < this.coders.length; i++) {
/* 111 */         if (this.coders[i] == coder) {
/* 112 */           return this.unpackSizes[i];
/*     */         }
/*     */       } 
/*     */     }
/* 116 */     return 0L;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 121 */     return "Folder with " + this.coders.length + " coders, " + this.totalInputStreams + " input streams, " + this.totalOutputStreams + " output streams, " + this.bindPairs.length + " bind pairs, " + this.packedStreams.length + " packed streams, " + this.unpackSizes.length + " unpack sizes, " + (this.hasCrc ? ("with CRC " + this.crc) : "without CRC") + " and " + this.numUnpackSubStreams + " unpack streams";
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\archivers\sevenz\Folder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */