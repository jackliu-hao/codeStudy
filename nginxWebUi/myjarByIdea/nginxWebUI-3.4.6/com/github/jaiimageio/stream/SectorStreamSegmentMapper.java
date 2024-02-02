package com.github.jaiimageio.stream;

class SectorStreamSegmentMapper implements StreamSegmentMapper {
   long[] segmentPositions;
   int segmentLength;
   int totalLength;
   int lastSegmentLength;

   public SectorStreamSegmentMapper(long[] segmentPositions, int segmentLength, int totalLength) {
      this.segmentPositions = (long[])((long[])segmentPositions.clone());
      this.segmentLength = segmentLength;
      this.totalLength = totalLength;
      this.lastSegmentLength = totalLength - (segmentPositions.length - 1) * segmentLength;
   }

   public StreamSegment getStreamSegment(long position, int length) {
      int index = (int)(position / (long)this.segmentLength);
      int len = index == this.segmentPositions.length - 1 ? this.lastSegmentLength : this.segmentLength;
      position -= (long)(index * this.segmentLength);
      len = (int)((long)len - position);
      if (len > length) {
         len = length;
      }

      return new StreamSegment(this.segmentPositions[index] + position, len);
   }

   public void getStreamSegment(long position, int length, StreamSegment seg) {
      int index = (int)(position / (long)this.segmentLength);
      int len = index == this.segmentPositions.length - 1 ? this.lastSegmentLength : this.segmentLength;
      position -= (long)(index * this.segmentLength);
      len = (int)((long)len - position);
      if (len > length) {
         len = length;
      }

      seg.setStartPos(this.segmentPositions[index] + position);
      seg.setSegmentLength(len);
   }

   long length() {
      return (long)this.totalLength;
   }
}
