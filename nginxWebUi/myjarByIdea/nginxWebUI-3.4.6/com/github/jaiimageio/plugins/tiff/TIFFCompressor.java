package com.github.jaiimageio.plugins.tiff;

import com.github.jaiimageio.impl.plugins.tiff.TIFFImageWriter;
import java.io.IOException;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.stream.ImageOutputStream;

public abstract class TIFFCompressor {
   protected ImageWriter writer;
   protected IIOMetadata metadata;
   protected String compressionType;
   protected int compressionTagValue;
   protected boolean isCompressionLossless;
   protected ImageOutputStream stream;

   public TIFFCompressor(String compressionType, int compressionTagValue, boolean isCompressionLossless) {
      if (compressionType == null) {
         throw new IllegalArgumentException("compressionType == null");
      } else if (compressionTagValue < 1) {
         throw new IllegalArgumentException("compressionTagValue < 1");
      } else {
         this.compressionType = compressionType;
         int compressionIndex = -1;
         String[] compressionTypes = TIFFImageWriter.compressionTypes;
         int len = compressionTypes.length;

         for(int i = 0; i < len; ++i) {
            if (compressionTypes[i].equals(compressionType)) {
               compressionIndex = i;
               break;
            }
         }

         if (compressionIndex != -1) {
            this.compressionTagValue = TIFFImageWriter.compressionNumbers[compressionIndex];
            this.isCompressionLossless = TIFFImageWriter.isCompressionLossless[compressionIndex];
         } else {
            this.compressionTagValue = compressionTagValue;
            this.isCompressionLossless = isCompressionLossless;
         }

      }
   }

   public String getCompressionType() {
      return this.compressionType;
   }

   public int getCompressionTagValue() {
      return this.compressionTagValue;
   }

   public boolean isCompressionLossless() {
      return this.isCompressionLossless;
   }

   public void setStream(ImageOutputStream stream) {
      this.stream = stream;
   }

   public ImageOutputStream getStream() {
      return this.stream;
   }

   public void setWriter(ImageWriter writer) {
      this.writer = writer;
   }

   public ImageWriter getWriter() {
      return this.writer;
   }

   public void setMetadata(IIOMetadata metadata) {
      this.metadata = metadata;
   }

   public IIOMetadata getMetadata() {
      return this.metadata;
   }

   public abstract int encode(byte[] var1, int var2, int var3, int var4, int[] var5, int var6) throws IOException;

   public void dispose() {
   }
}
