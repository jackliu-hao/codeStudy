/*     */ package com.github.jaiimageio.stream;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import javax.imageio.stream.ImageInputStream;
/*     */ import javax.imageio.stream.ImageInputStreamImpl;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class SegmentedImageInputStream
/*     */   extends ImageInputStreamImpl
/*     */ {
/*     */   private ImageInputStream stream;
/*     */   private StreamSegmentMapper mapper;
/*     */   private StreamSegment streamSegment;
/*     */   
/*     */   public SegmentedImageInputStream(ImageInputStream stream, StreamSegmentMapper mapper) {
/* 276 */     this.streamSegment = new StreamSegment();
/*     */     this.stream = stream;
/*     */     this.mapper = mapper;
/*     */   }
/*     */ 
/*     */   
/*     */   public SegmentedImageInputStream(ImageInputStream stream, long[] segmentPositions, int[] segmentLengths) {
/*     */     this(stream, new StreamSegmentMapperImpl(segmentPositions, segmentLengths));
/*     */   }
/*     */   
/*     */   public SegmentedImageInputStream(ImageInputStream stream, long[] segmentPositions, int segmentLength, int totalLength) {
/*     */     this(stream, new SectorStreamSegmentMapper(segmentPositions, segmentLength, totalLength));
/*     */   }
/*     */   
/*     */   public int read() throws IOException {
/* 291 */     this.mapper.getStreamSegment(this.streamPos, 1, this.streamSegment);
/* 292 */     int streamSegmentLength = this.streamSegment.getSegmentLength();
/* 293 */     if (streamSegmentLength < 0) {
/* 294 */       return -1;
/*     */     }
/* 296 */     this.stream.seek(this.streamSegment.getStartPos());
/*     */ 
/*     */ 
/*     */     
/* 300 */     int val = this.stream.read();
/* 301 */     this.streamPos++;
/* 302 */     return val;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int read(byte[] b, int off, int len) throws IOException {
/* 354 */     if (b == null) {
/* 355 */       throw new NullPointerException();
/*     */     }
/* 357 */     if (off < 0 || len < 0 || off + len > b.length) {
/* 358 */       throw new IndexOutOfBoundsException();
/*     */     }
/* 360 */     if (len == 0) {
/* 361 */       return 0;
/*     */     }
/*     */     
/* 364 */     this.mapper.getStreamSegment(this.streamPos, len, this.streamSegment);
/* 365 */     int streamSegmentLength = this.streamSegment.getSegmentLength();
/* 366 */     if (streamSegmentLength < 0) {
/* 367 */       return -1;
/*     */     }
/* 369 */     this.stream.seek(this.streamSegment.getStartPos());
/*     */     
/* 371 */     int nbytes = this.stream.read(b, off, streamSegmentLength);
/* 372 */     this.streamPos += nbytes;
/* 373 */     return nbytes;
/*     */   }
/*     */   
/*     */   public long length() {
/*     */     long len;
/* 378 */     if (this.mapper instanceof StreamSegmentMapperImpl) {
/* 379 */       len = ((StreamSegmentMapperImpl)this.mapper).length();
/* 380 */     } else if (this.mapper instanceof SectorStreamSegmentMapper) {
/* 381 */       len = ((SectorStreamSegmentMapper)this.mapper).length();
/* 382 */     } else if (this.mapper != null) {
/* 383 */       long pos = len = 0L;
/* 384 */       StreamSegment seg = this.mapper.getStreamSegment(pos, 2147483647);
/* 385 */       while ((len = seg.getSegmentLength()) > 0L) {
/* 386 */         pos += len;
/* 387 */         seg.setSegmentLength(0);
/* 388 */         this.mapper.getStreamSegment(pos, 2147483647, seg);
/*     */       } 
/* 390 */       len = pos;
/*     */     } else {
/* 392 */       len = super.length();
/*     */     } 
/*     */     
/* 395 */     return len;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\github\jaiimageio\stream\SegmentedImageInputStream.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */