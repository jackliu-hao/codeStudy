package com.github.jaiimageio.impl.plugins.tiff;

import com.github.jaiimageio.plugins.tiff.TIFFDecompressor;
import java.io.IOException;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;
import javax.imageio.IIOException;

public class TIFFDeflateDecompressor extends TIFFDecompressor {
   private static final boolean DEBUG = false;
   Inflater inflater = null;
   int predictor;

   public TIFFDeflateDecompressor(int predictor) throws IIOException {
      this.inflater = new Inflater();
      if (predictor != 1 && predictor != 2) {
         throw new IIOException("Illegal value for Predictor in TIFF file");
      } else {
         this.predictor = predictor;
      }
   }

   public synchronized void decodeRaw(byte[] b, int dstOffset, int bitsPerPixel, int scanlineStride) throws IOException {
      int bytesPerRow;
      if (this.predictor == 2) {
         int len = this.bitsPerSample.length;

         for(bytesPerRow = 0; bytesPerRow < len; ++bytesPerRow) {
            if (this.bitsPerSample[bytesPerRow] != 8) {
               throw new IIOException(this.bitsPerSample[bytesPerRow] + "-bit samples " + "are not supported for Horizontal " + "differencing Predictor");
            }
         }
      }

      this.stream.seek(this.offset);
      byte[] srcData = new byte[this.byteCount];
      this.stream.readFully(srcData);
      bytesPerRow = (this.srcWidth * bitsPerPixel + 7) / 8;
      byte[] buf;
      int bufOffset;
      if (bytesPerRow == scanlineStride) {
         buf = b;
         bufOffset = dstOffset;
      } else {
         buf = new byte[bytesPerRow * this.srcHeight];
         bufOffset = 0;
      }

      this.inflater.setInput(srcData);

      try {
         this.inflater.inflate(buf, bufOffset, bytesPerRow * this.srcHeight);
      } catch (DataFormatException var12) {
         throw new IIOException(I18N.getString("TIFFDeflateDecompressor0"), var12);
      }

      this.inflater.reset();
      int off;
      int count;
      if (this.predictor == 2) {
         for(off = 0; off < this.srcHeight; ++off) {
            count = bufOffset + this.samplesPerPixel * (off * this.srcWidth + 1);

            for(int i = this.samplesPerPixel; i < this.srcWidth * this.samplesPerPixel; ++i) {
               buf[count] += buf[count - this.samplesPerPixel];
               ++count;
            }
         }
      }

      if (bytesPerRow != scanlineStride) {
         off = 0;

         for(count = 0; count < this.srcHeight; ++count) {
            System.arraycopy(buf, off, b, dstOffset, bytesPerRow);
            off += bytesPerRow;
            dstOffset += scanlineStride;
         }
      }

   }
}
