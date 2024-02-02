package com.github.jaiimageio.stream;

class StreamSegmentMapperImpl implements StreamSegmentMapper {
   private long[] segmentPositions;
   private int[] segmentLengths;

   public StreamSegmentMapperImpl(long[] segmentPositions, int[] segmentLengths) {
      this.segmentPositions = (long[])((long[])segmentPositions.clone());
      this.segmentLengths = (int[])((int[])segmentLengths.clone());
   }

   public StreamSegment getStreamSegment(long position, int length) {
      int numSegments = this.segmentLengths.length;

      for(int i = 0; i < numSegments; ++i) {
         int len = this.segmentLengths[i];
         if (position < (long)len) {
            return new StreamSegment(this.segmentPositions[i] + position, Math.min(len - (int)position, length));
         }

         position -= (long)len;
      }

      return null;
   }

   public void getStreamSegment(long position, int length, StreamSegment seg) {
      int numSegments = this.segmentLengths.length;

      for(int i = 0; i < numSegments; ++i) {
         int len = this.segmentLengths[i];
         if (position < (long)len) {
            seg.setStartPos(this.segmentPositions[i] + position);
            seg.setSegmentLength(Math.min(len - (int)position, length));
            return;
         }

         position -= (long)len;
      }

      seg.setStartPos(-1L);
      seg.setSegmentLength(-1);
   }

   long length() {
      int numSegments = this.segmentLengths.length;
      long len = 0L;

      for(int i = 0; i < numSegments; ++i) {
         len += (long)this.segmentLengths[i];
      }

      return len;
   }
}
