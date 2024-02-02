/*     */ package org.apache.commons.compress.compressors.lz77support;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class Parameters
/*     */ {
/*     */   public static final int TRUE_MIN_BACK_REFERENCE_LENGTH = 3;
/*     */   private final int windowSize;
/*     */   private final int minBackReferenceLength;
/*     */   private final int maxBackReferenceLength;
/*     */   private final int maxOffset;
/*     */   private final int maxLiteralLength;
/*     */   private final int niceBackReferenceLength;
/*     */   private final int maxCandidates;
/*     */   private final int lazyThreshold;
/*     */   private final boolean lazyMatching;
/*     */   
/*     */   public static Builder builder(int windowSize) {
/*  47 */     return new Builder(windowSize);
/*     */   }
/*     */ 
/*     */   
/*     */   public static class Builder
/*     */   {
/*     */     private final int windowSize;
/*     */     
/*     */     private int minBackReferenceLength;
/*     */     private int maxBackReferenceLength;
/*     */     private int maxOffset;
/*     */     
/*     */     private Builder(int windowSize) {
/*  60 */       if (windowSize < 2 || !Parameters.isPowerOfTwo(windowSize)) {
/*  61 */         throw new IllegalArgumentException("windowSize must be a power of two");
/*     */       }
/*  63 */       this.windowSize = windowSize;
/*  64 */       this.minBackReferenceLength = 3;
/*  65 */       this.maxBackReferenceLength = windowSize - 1;
/*  66 */       this.maxOffset = windowSize - 1;
/*  67 */       this.maxLiteralLength = windowSize;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     private int maxLiteralLength;
/*     */ 
/*     */     
/*     */     private Integer niceBackReferenceLength;
/*     */ 
/*     */     
/*     */     private Integer maxCandidates;
/*     */ 
/*     */     
/*     */     private Integer lazyThreshold;
/*     */ 
/*     */     
/*     */     private Boolean lazyMatches;
/*     */ 
/*     */ 
/*     */     
/*     */     public Builder withMinBackReferenceLength(int minBackReferenceLength) {
/*  89 */       this.minBackReferenceLength = Math.max(3, minBackReferenceLength);
/*  90 */       if (this.windowSize < this.minBackReferenceLength) {
/*  91 */         throw new IllegalArgumentException("minBackReferenceLength can't be bigger than windowSize");
/*     */       }
/*  93 */       if (this.maxBackReferenceLength < this.minBackReferenceLength) {
/*  94 */         this.maxBackReferenceLength = this.minBackReferenceLength;
/*     */       }
/*  96 */       return this;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Builder withMaxBackReferenceLength(int maxBackReferenceLength) {
/* 115 */       this
/* 116 */         .maxBackReferenceLength = (maxBackReferenceLength < this.minBackReferenceLength) ? this.minBackReferenceLength : Math.min(maxBackReferenceLength, this.windowSize - 1);
/* 117 */       return this;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Builder withMaxOffset(int maxOffset) {
/* 135 */       this.maxOffset = (maxOffset < 1) ? (this.windowSize - 1) : Math.min(maxOffset, this.windowSize - 1);
/* 136 */       return this;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Builder withMaxLiteralLength(int maxLiteralLength) {
/* 154 */       this
/* 155 */         .maxLiteralLength = (maxLiteralLength < 1) ? this.windowSize : Math.min(maxLiteralLength, this.windowSize);
/* 156 */       return this;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Builder withNiceBackReferenceLength(int niceLen) {
/* 169 */       this.niceBackReferenceLength = Integer.valueOf(niceLen);
/* 170 */       return this;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Builder withMaxNumberOfCandidates(int maxCandidates) {
/* 181 */       this.maxCandidates = Integer.valueOf(maxCandidates);
/* 182 */       return this;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Builder withLazyMatching(boolean lazy) {
/* 196 */       this.lazyMatches = Boolean.valueOf(lazy);
/* 197 */       return this;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Builder withLazyThreshold(int threshold) {
/* 209 */       this.lazyThreshold = Integer.valueOf(threshold);
/* 210 */       return this;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Builder tunedForSpeed() {
/* 221 */       this.niceBackReferenceLength = Integer.valueOf(Math.max(this.minBackReferenceLength, this.maxBackReferenceLength / 8));
/* 222 */       this.maxCandidates = Integer.valueOf(Math.max(32, this.windowSize / 1024));
/* 223 */       this.lazyMatches = Boolean.valueOf(false);
/* 224 */       this.lazyThreshold = Integer.valueOf(this.minBackReferenceLength);
/* 225 */       return this;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Builder tunedForCompressionRatio() {
/* 236 */       this.niceBackReferenceLength = this.lazyThreshold = Integer.valueOf(this.maxBackReferenceLength);
/* 237 */       this.maxCandidates = Integer.valueOf(Math.max(32, this.windowSize / 16));
/* 238 */       this.lazyMatches = Boolean.valueOf(true);
/* 239 */       return this;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Parameters build() {
/* 249 */       int niceLen = (this.niceBackReferenceLength != null) ? this.niceBackReferenceLength.intValue() : Math.max(this.minBackReferenceLength, this.maxBackReferenceLength / 2);
/* 250 */       int candidates = (this.maxCandidates != null) ? this.maxCandidates.intValue() : Math.max(256, this.windowSize / 128);
/* 251 */       boolean lazy = (this.lazyMatches == null || this.lazyMatches.booleanValue());
/* 252 */       int threshold = lazy ? ((this.lazyThreshold != null) ? this.lazyThreshold.intValue() : niceLen) : this.minBackReferenceLength;
/*     */       
/* 254 */       return new Parameters(this.windowSize, this.minBackReferenceLength, this.maxBackReferenceLength, this.maxOffset, this.maxLiteralLength, niceLen, candidates, lazy, threshold);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private Parameters(int windowSize, int minBackReferenceLength, int maxBackReferenceLength, int maxOffset, int maxLiteralLength, int niceBackReferenceLength, int maxCandidates, boolean lazyMatching, int lazyThreshold) {
/* 266 */     this.windowSize = windowSize;
/* 267 */     this.minBackReferenceLength = minBackReferenceLength;
/* 268 */     this.maxBackReferenceLength = maxBackReferenceLength;
/* 269 */     this.maxOffset = maxOffset;
/* 270 */     this.maxLiteralLength = maxLiteralLength;
/* 271 */     this.niceBackReferenceLength = niceBackReferenceLength;
/* 272 */     this.maxCandidates = maxCandidates;
/* 273 */     this.lazyMatching = lazyMatching;
/* 274 */     this.lazyThreshold = lazyThreshold;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getWindowSize() {
/* 283 */     return this.windowSize;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getMinBackReferenceLength() {
/* 290 */     return this.minBackReferenceLength;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getMaxBackReferenceLength() {
/* 297 */     return this.maxBackReferenceLength;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getMaxOffset() {
/* 304 */     return this.maxOffset;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getMaxLiteralLength() {
/* 311 */     return this.maxLiteralLength;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getNiceBackReferenceLength() {
/* 319 */     return this.niceBackReferenceLength;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getMaxCandidates() {
/* 327 */     return this.maxCandidates;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean getLazyMatching() {
/* 335 */     return this.lazyMatching;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getLazyMatchingThreshold() {
/* 343 */     return this.lazyThreshold;
/*     */   }
/*     */ 
/*     */   
/*     */   private static boolean isPowerOfTwo(int x) {
/* 348 */     return ((x & x - 1) == 0);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\compressors\lz77support\Parameters.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */