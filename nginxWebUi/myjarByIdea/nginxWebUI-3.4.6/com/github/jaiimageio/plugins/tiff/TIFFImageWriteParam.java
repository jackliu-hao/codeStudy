package com.github.jaiimageio.plugins.tiff;

import com.github.jaiimageio.impl.plugins.tiff.TIFFImageWriter;
import java.util.Locale;
import javax.imageio.ImageWriteParam;

public class TIFFImageWriteParam extends ImageWriteParam {
   TIFFCompressor compressor = null;
   TIFFColorConverter colorConverter = null;
   int photometricInterpretation;
   private boolean appendedCompressionType = false;

   public TIFFImageWriteParam(Locale locale) {
      super(locale);
      this.canWriteCompressed = true;
      this.canWriteTiles = true;
      this.compressionTypes = TIFFImageWriter.TIFFCompressionTypes;
   }

   public boolean isCompressionLossless() {
      if (this.getCompressionMode() != 2) {
         throw new IllegalStateException("Compression mode not MODE_EXPLICIT!");
      } else if (this.compressionType == null) {
         throw new IllegalStateException("No compression type set!");
      } else if (this.compressor != null && this.compressionType.equals(this.compressor.getCompressionType())) {
         return this.compressor.isCompressionLossless();
      } else {
         for(int i = 0; i < this.compressionTypes.length; ++i) {
            if (this.compressionType.equals(this.compressionTypes[i])) {
               return TIFFImageWriter.isCompressionLossless[i];
            }
         }

         return false;
      }
   }

   public void setTIFFCompressor(TIFFCompressor compressor) {
      if (this.getCompressionMode() != 2) {
         throw new IllegalStateException("Compression mode not MODE_EXPLICIT!");
      } else {
         this.compressor = compressor;
         if (this.appendedCompressionType) {
            int len = this.compressionTypes.length - 1;
            String[] types = new String[len];
            System.arraycopy(this.compressionTypes, 0, types, 0, len);
            this.compressionTypes = types;
            this.appendedCompressionType = false;
         }

         if (compressor != null) {
            String compressorType = compressor.getCompressionType();
            int len = this.compressionTypes.length;
            boolean appendCompressionType = true;

            for(int i = 0; i < len; ++i) {
               if (compressorType.equals(this.compressionTypes[i])) {
                  appendCompressionType = false;
                  break;
               }
            }

            if (appendCompressionType) {
               String[] types = new String[len + 1];
               System.arraycopy(this.compressionTypes, 0, types, 0, len);
               types[len] = compressorType;
               this.compressionTypes = types;
               this.appendedCompressionType = true;
            }
         }

      }
   }

   public TIFFCompressor getTIFFCompressor() {
      if (this.getCompressionMode() != 2) {
         throw new IllegalStateException("Compression mode not MODE_EXPLICIT!");
      } else {
         return this.compressor;
      }
   }

   public void setColorConverter(TIFFColorConverter colorConverter, int photometricInterpretation) {
      this.colorConverter = colorConverter;
      this.photometricInterpretation = photometricInterpretation;
   }

   public TIFFColorConverter getColorConverter() {
      return this.colorConverter;
   }

   public int getPhotometricInterpretation() {
      if (this.colorConverter == null) {
         throw new IllegalStateException("Color converter not set!");
      } else {
         return this.photometricInterpretation;
      }
   }

   public void unsetColorConverter() {
      this.colorConverter = null;
   }
}
