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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ class SectorStreamSegmentMapper
/*     */   implements StreamSegmentMapper
/*     */ {
/*     */   long[] segmentPositions;
/*     */   int segmentLength;
/*     */   int totalLength;
/*     */   int lastSegmentLength;
/*     */   
/*     */   public SectorStreamSegmentMapper(long[] segmentPositions, int segmentLength, int totalLength) {
/* 128 */     this.segmentPositions = (long[])segmentPositions.clone();
/* 129 */     this.segmentLength = segmentLength;
/* 130 */     this.totalLength = totalLength;
/* 131 */     this.lastSegmentLength = totalLength - (segmentPositions.length - 1) * segmentLength;
/*     */   }
/*     */ 
/*     */   
/*     */   public StreamSegment getStreamSegment(long position, int length) {
/* 136 */     int index = (int)(position / this.segmentLength);
/*     */ 
/*     */     
/* 139 */     int len = (index == this.segmentPositions.length - 1) ? this.lastSegmentLength : this.segmentLength;
/*     */ 
/*     */ 
/*     */     
/* 143 */     position -= (index * this.segmentLength);
/*     */ 
/*     */     
/* 146 */     len = (int)(len - position);
/* 147 */     if (len > length) {
/* 148 */       len = length;
/*     */     }
/* 150 */     return new StreamSegment(this.segmentPositions[index] + position, len);
/*     */   }
/*     */ 
/*     */   
/*     */   public void getStreamSegment(long position, int length, StreamSegment seg) {
/* 155 */     int index = (int)(position / this.segmentLength);
/*     */ 
/*     */     
/* 158 */     int len = (index == this.segmentPositions.length - 1) ? this.lastSegmentLength : this.segmentLength;
/*     */ 
/*     */ 
/*     */     
/* 162 */     position -= (index * this.segmentLength);
/*     */ 
/*     */     
/* 165 */     len = (int)(len - position);
/* 166 */     if (len > length) {
/* 167 */       len = length;
/*     */     }
/*     */     
/* 170 */     seg.setStartPos(this.segmentPositions[index] + position);
/* 171 */     seg.setSegmentLength(len);
/*     */   }
/*     */   
/*     */   long length() {
/* 175 */     return this.totalLength;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\github\jaiimageio\stream\SectorStreamSegmentMapper.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */