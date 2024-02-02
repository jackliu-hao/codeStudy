package com.github.jaiimageio.stream;

import java.io.IOException;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageInputStreamImpl;

public class SegmentedImageInputStream extends ImageInputStreamImpl {
   private ImageInputStream stream;
   private StreamSegmentMapper mapper;
   private StreamSegment streamSegment;

   public SegmentedImageInputStream(ImageInputStream stream, StreamSegmentMapper mapper) {
      this.streamSegment = new StreamSegment();
      this.stream = stream;
      this.mapper = mapper;
   }

   public SegmentedImageInputStream(ImageInputStream stream, long[] segmentPositions, int[] segmentLengths) {
      this(stream, new StreamSegmentMapperImpl(segmentPositions, segmentLengths));
   }

   public SegmentedImageInputStream(ImageInputStream stream, long[] segmentPositions, int segmentLength, int totalLength) {
      this(stream, new SectorStreamSegmentMapper(segmentPositions, segmentLength, totalLength));
   }

   public int read() throws IOException {
      this.mapper.getStreamSegment(this.streamPos, 1, this.streamSegment);
      int streamSegmentLength = this.streamSegment.getSegmentLength();
      if (streamSegmentLength < 0) {
         return -1;
      } else {
         this.stream.seek(this.streamSegment.getStartPos());
         int val = this.stream.read();
         ++this.streamPos;
         return val;
      }
   }

   public int read(byte[] b, int off, int len) throws IOException {
      if (b == null) {
         throw new NullPointerException();
      } else if (off >= 0 && len >= 0 && off + len <= b.length) {
         if (len == 0) {
            return 0;
         } else {
            this.mapper.getStreamSegment(this.streamPos, len, this.streamSegment);
            int streamSegmentLength = this.streamSegment.getSegmentLength();
            if (streamSegmentLength < 0) {
               return -1;
            } else {
               this.stream.seek(this.streamSegment.getStartPos());
               int nbytes = this.stream.read(b, off, streamSegmentLength);
               this.streamPos += (long)nbytes;
               return nbytes;
            }
         }
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   public long length() {
      long len;
      if (this.mapper instanceof StreamSegmentMapperImpl) {
         len = ((StreamSegmentMapperImpl)this.mapper).length();
      } else if (this.mapper instanceof SectorStreamSegmentMapper) {
         len = ((SectorStreamSegmentMapper)this.mapper).length();
      } else if (this.mapper != null) {
         len = 0L;
         long pos = 0L;
         StreamSegment seg = this.mapper.getStreamSegment(pos, Integer.MAX_VALUE);

         while((len = (long)seg.getSegmentLength()) > 0L) {
            pos += len;
            seg.setSegmentLength(0);
            this.mapper.getStreamSegment(pos, Integer.MAX_VALUE, seg);
         }

         len = pos;
      } else {
         len = super.length();
      }

      return len;
   }
}
