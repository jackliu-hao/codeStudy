package com.github.jaiimageio.impl.plugins.tiff;

import com.github.jaiimageio.plugins.tiff.TIFFDecompressor;
import com.github.jaiimageio.plugins.tiff.TIFFField;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Iterator;
import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.MemoryCacheImageInputStream;

public class TIFFJPEGDecompressor extends TIFFDecompressor {
   private static final boolean DEBUG = false;
   protected static final int SOI = 216;
   protected static final int EOI = 217;
   protected ImageReader JPEGReader = null;
   protected ImageReadParam JPEGParam;
   protected boolean hasJPEGTables = false;
   protected byte[] tables = null;
   private byte[] data = new byte[0];

   public void beginDecoding() {
      if (this.JPEGReader == null) {
         Iterator iter = ImageIO.getImageReadersByFormatName("jpeg");
         if (!iter.hasNext()) {
            throw new IllegalStateException("No JPEG readers found!");
         }

         this.JPEGReader = (ImageReader)iter.next();
         this.JPEGParam = this.JPEGReader.getDefaultReadParam();
      }

      TIFFImageMetadata tmetadata = (TIFFImageMetadata)this.metadata;
      TIFFField f = tmetadata.getTIFFField(347);
      if (f != null) {
         this.hasJPEGTables = true;
         this.tables = f.getAsBytes();
      } else {
         this.hasJPEGTables = false;
      }

   }

   public void decodeRaw(byte[] b, int dstOffset, int bitsPerPixel, int scanlineStride) throws IOException {
      this.stream.seek(this.offset);
      Object is;
      if (this.hasJPEGTables) {
         int dataLength = this.tables.length + this.byteCount;
         if (this.data.length < dataLength) {
            this.data = new byte[dataLength];
         }

         int dataOffset = this.tables.length;

         for(int i = this.tables.length - 2; i > 0; --i) {
            if ((this.tables[i] & 255) == 255 && (this.tables[i + 1] & 255) == 217) {
               dataOffset = i;
               break;
            }
         }

         System.arraycopy(this.tables, 0, this.data, 0, dataOffset);
         byte byte1 = (byte)this.stream.read();
         byte byte2 = (byte)this.stream.read();
         if ((byte1 & 255) != 255 || (byte2 & 255) != 216) {
            this.data[dataOffset++] = byte1;
            this.data[dataOffset++] = byte2;
         }

         this.stream.readFully(this.data, dataOffset, this.byteCount - 2);
         ByteArrayInputStream bais = new ByteArrayInputStream(this.data);
         is = new MemoryCacheImageInputStream(bais);
      } else {
         is = this.stream;
      }

      this.JPEGReader.setInput(is, false, true);
      this.JPEGParam.setDestination(this.rawImage);
      this.JPEGReader.read(0, this.JPEGParam);
   }

   protected void finalize() throws Throwable {
      super.finalize();
      this.JPEGReader.dispose();
   }
}
