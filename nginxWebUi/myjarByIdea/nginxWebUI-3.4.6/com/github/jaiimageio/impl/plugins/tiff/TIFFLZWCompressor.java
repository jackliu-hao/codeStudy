package com.github.jaiimageio.impl.plugins.tiff;

import com.github.jaiimageio.impl.common.LZWCompressor;
import com.github.jaiimageio.plugins.tiff.TIFFCompressor;
import java.io.IOException;
import javax.imageio.stream.ImageOutputStream;

public class TIFFLZWCompressor extends TIFFCompressor {
   int predictor;

   public TIFFLZWCompressor(int predictorValue) {
      super("LZW", 5, true);
      this.predictor = predictorValue;
   }

   public void setStream(ImageOutputStream stream) {
      super.setStream(stream);
   }

   public int encode(byte[] b, int off, int width, int height, int[] bitsPerSample, int scanlineStride) throws IOException {
      LZWCompressor lzwCompressor = new LZWCompressor(this.stream, 8, true);
      int samplesPerPixel = bitsPerSample.length;
      int bitsPerPixel = 0;

      int bytesPerRow;
      for(bytesPerRow = 0; bytesPerRow < samplesPerPixel; ++bytesPerRow) {
         bitsPerPixel += bitsPerSample[bytesPerRow];
      }

      bytesPerRow = (bitsPerPixel * width + 7) / 8;
      long initialStreamPosition = this.stream.getStreamPosition();
      boolean usePredictor = this.predictor == 2;
      if (bytesPerRow == scanlineStride && !usePredictor) {
         lzwCompressor.compress(b, off, bytesPerRow * height);
      } else {
         byte[] rowBuf = usePredictor ? new byte[bytesPerRow] : null;

         for(int i = 0; i < height; ++i) {
            if (!usePredictor) {
               lzwCompressor.compress(b, off, bytesPerRow);
            } else {
               System.arraycopy(b, off, rowBuf, 0, bytesPerRow);

               for(int j = bytesPerRow - 1; j >= samplesPerPixel; --j) {
                  rowBuf[j] -= rowBuf[j - samplesPerPixel];
               }

               lzwCompressor.compress(rowBuf, 0, bytesPerRow);
            }

            off += scanlineStride;
         }
      }

      lzwCompressor.flush();
      int bytesWritten = (int)(this.stream.getStreamPosition() - initialStreamPosition);
      return bytesWritten;
   }
}
