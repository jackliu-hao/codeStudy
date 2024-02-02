/*     */ package com.github.jaiimageio.stream;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ class StreamSegmentMapperImpl
/*     */   implements StreamSegmentMapper
/*     */ {
/*     */   private long[] segmentPositions;
/*     */   private int[] segmentLengths;
/*     */   
/*     */   public StreamSegmentMapperImpl(long[] segmentPositions, int[] segmentLengths) {
/*  65 */     this.segmentPositions = (long[])segmentPositions.clone();
/*  66 */     this.segmentLengths = (int[])segmentLengths.clone();
/*     */   }
/*     */   
/*     */   public StreamSegment getStreamSegment(long position, int length) {
/*  70 */     int numSegments = this.segmentLengths.length;
/*  71 */     for (int i = 0; i < numSegments; i++) {
/*  72 */       int len = this.segmentLengths[i];
/*  73 */       if (position < len) {
/*  74 */         return new StreamSegment(this.segmentPositions[i] + position, 
/*  75 */             Math.min(len - (int)position, length));
/*     */       }
/*     */       
/*  78 */       position -= len;
/*     */     } 
/*     */     
/*  81 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public void getStreamSegment(long position, int length, StreamSegment seg) {
/*  86 */     int numSegments = this.segmentLengths.length;
/*  87 */     for (int i = 0; i < numSegments; i++) {
/*  88 */       int len = this.segmentLengths[i];
/*  89 */       if (position < len) {
/*  90 */         seg.setStartPos(this.segmentPositions[i] + position);
/*  91 */         seg.setSegmentLength(Math.min(len - (int)position, length));
/*     */         return;
/*     */       } 
/*  94 */       position -= len;
/*     */     } 
/*     */     
/*  97 */     seg.setStartPos(-1L);
/*  98 */     seg.setSegmentLength(-1);
/*     */   }
/*     */ 
/*     */   
/*     */   long length() {
/* 103 */     int numSegments = this.segmentLengths.length;
/* 104 */     long len = 0L;
/*     */     
/* 106 */     for (int i = 0; i < numSegments; i++) {
/* 107 */       len += this.segmentLengths[i];
/*     */     }
/*     */     
/* 110 */     return len;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\github\jaiimageio\stream\StreamSegmentMapperImpl.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */