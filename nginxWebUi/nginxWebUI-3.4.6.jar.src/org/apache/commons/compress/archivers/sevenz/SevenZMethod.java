/*     */ package org.apache.commons.compress.archivers.sevenz;
/*     */ 
/*     */ import java.util.Arrays;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public enum SevenZMethod
/*     */ {
/*  38 */   COPY(new byte[] { 0
/*     */     }),
/*  40 */   LZMA(new byte[] { 3, 1, 1
/*     */     }),
/*  42 */   LZMA2(new byte[] { 33
/*     */     }),
/*  44 */   DEFLATE(new byte[] { 4, 1, 8
/*     */ 
/*     */ 
/*     */     
/*     */     }),
/*  49 */   DEFLATE64(new byte[] { 4, 1, 9
/*     */     }),
/*  51 */   BZIP2(new byte[] { 4, 2, 2
/*     */ 
/*     */ 
/*     */     
/*     */     }),
/*  56 */   AES256SHA256(new byte[] { 6, -15, 7, 1
/*     */ 
/*     */ 
/*     */     
/*     */     }),
/*  61 */   BCJ_X86_FILTER(new byte[] { 3, 3, 1, 3
/*     */ 
/*     */ 
/*     */     
/*     */     }),
/*  66 */   BCJ_PPC_FILTER(new byte[] { 3, 3, 2, 5
/*     */ 
/*     */ 
/*     */     
/*     */     }),
/*  71 */   BCJ_IA64_FILTER(new byte[] { 3, 3, 4, 1
/*     */ 
/*     */ 
/*     */     
/*     */     }),
/*  76 */   BCJ_ARM_FILTER(new byte[] { 3, 3, 5, 1
/*     */ 
/*     */ 
/*     */     
/*     */     }),
/*  81 */   BCJ_ARM_THUMB_FILTER(new byte[] { 3, 3, 7, 1
/*     */ 
/*     */ 
/*     */     
/*     */     }),
/*  86 */   BCJ_SPARC_FILTER(new byte[] { 3, 3, 8, 5
/*     */ 
/*     */ 
/*     */     
/*     */     }),
/*  91 */   DELTA_FILTER(new byte[] { 3 });
/*     */   
/*     */   private final byte[] id;
/*     */   
/*     */   SevenZMethod(byte[] id) {
/*  96 */     this.id = id;
/*     */   }
/*     */   
/*     */   byte[] getId() {
/* 100 */     int idLength = this.id.length;
/* 101 */     byte[] copy = new byte[idLength];
/* 102 */     System.arraycopy(this.id, 0, copy, 0, idLength);
/* 103 */     return copy;
/*     */   }
/*     */   
/*     */   static SevenZMethod byId(byte[] id) {
/* 107 */     for (SevenZMethod m : (SevenZMethod[])SevenZMethod.class.getEnumConstants()) {
/* 108 */       if (Arrays.equals(m.id, id)) {
/* 109 */         return m;
/*     */       }
/*     */     } 
/* 112 */     return null;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\archivers\sevenz\SevenZMethod.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */