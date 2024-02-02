package com.github.jaiimageio.impl.plugins.tiff;

import com.github.jaiimageio.plugins.tiff.TIFFDecompressor;
import com.github.jaiimageio.plugins.tiff.TIFFField;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.util.Hashtable;
import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.MemoryCacheImageInputStream;

public class TIFFYCbCrDecompressor extends TIFFDecompressor {
   private static final boolean debug = false;
   private static final int FRAC_BITS = 16;
   private static final float FRAC_SCALE = 65536.0F;
   private float LumaRed = 0.299F;
   private float LumaGreen = 0.587F;
   private float LumaBlue = 0.114F;
   private float referenceBlackY = 0.0F;
   private float referenceWhiteY = 255.0F;
   private float referenceBlackCb = 128.0F;
   private float referenceWhiteCb = 255.0F;
   private float referenceBlackCr = 128.0F;
   private float referenceWhiteCr = 255.0F;
   private float codingRangeY = 255.0F;
   private int[] iYTab = new int[256];
   private int[] iCbTab = new int[256];
   private int[] iCrTab = new int[256];
   private int[] iGYTab = new int[256];
   private int[] iGCbTab = new int[256];
   private int[] iGCrTab = new int[256];
   private int chromaSubsampleH = 2;
   private int chromaSubsampleV = 2;
   private boolean colorConvert;
   private TIFFDecompressor decompressor;
   private BufferedImage tmpImage;

   public TIFFYCbCrDecompressor(TIFFDecompressor decompressor, boolean colorConvert) {
      this.decompressor = decompressor;
      this.colorConvert = colorConvert;
   }

   private void warning(String message) {
      if (this.reader instanceof TIFFImageReader) {
         ((TIFFImageReader)this.reader).forwardWarningMessage(message);
      }

   }

   public void setReader(ImageReader reader) {
      if (this.decompressor != null) {
         this.decompressor.setReader(reader);
      }

      super.setReader(reader);
   }

   public void setMetadata(IIOMetadata metadata) {
      if (this.decompressor != null) {
         this.decompressor.setMetadata(metadata);
      }

      super.setMetadata(metadata);
   }

   public void setPhotometricInterpretation(int photometricInterpretation) {
      if (this.decompressor != null) {
         this.decompressor.setPhotometricInterpretation(photometricInterpretation);
      }

      super.setPhotometricInterpretation(photometricInterpretation);
   }

   public void setCompression(int compression) {
      if (this.decompressor != null) {
         this.decompressor.setCompression(compression);
      }

      super.setCompression(compression);
   }

   public void setPlanar(boolean planar) {
      if (this.decompressor != null) {
         this.decompressor.setPlanar(planar);
      }

      super.setPlanar(planar);
   }

   public void setSamplesPerPixel(int samplesPerPixel) {
      if (this.decompressor != null) {
         this.decompressor.setSamplesPerPixel(samplesPerPixel);
      }

      super.setSamplesPerPixel(samplesPerPixel);
   }

   public void setBitsPerSample(int[] bitsPerSample) {
      if (this.decompressor != null) {
         this.decompressor.setBitsPerSample(bitsPerSample);
      }

      super.setBitsPerSample(bitsPerSample);
   }

   public void setSampleFormat(int[] sampleFormat) {
      if (this.decompressor != null) {
         this.decompressor.setSampleFormat(sampleFormat);
      }

      super.setSampleFormat(sampleFormat);
   }

   public void setExtraSamples(int[] extraSamples) {
      if (this.decompressor != null) {
         this.decompressor.setExtraSamples(extraSamples);
      }

      super.setExtraSamples(extraSamples);
   }

   public void setColorMap(char[] colorMap) {
      if (this.decompressor != null) {
         this.decompressor.setColorMap(colorMap);
      }

      super.setColorMap(colorMap);
   }

   public void setStream(ImageInputStream stream) {
      if (this.decompressor != null) {
         this.decompressor.setStream(stream);
      } else {
         super.setStream(stream);
      }

   }

   public void setOffset(long offset) {
      if (this.decompressor != null) {
         this.decompressor.setOffset(offset);
      }

      super.setOffset(offset);
   }

   public void setByteCount(int byteCount) {
      if (this.decompressor != null) {
         this.decompressor.setByteCount(byteCount);
      }

      super.setByteCount(byteCount);
   }

   public void setSrcMinX(int srcMinX) {
      if (this.decompressor != null) {
         this.decompressor.setSrcMinX(srcMinX);
      }

      super.setSrcMinX(srcMinX);
   }

   public void setSrcMinY(int srcMinY) {
      if (this.decompressor != null) {
         this.decompressor.setSrcMinY(srcMinY);
      }

      super.setSrcMinY(srcMinY);
   }

   public void setSrcWidth(int srcWidth) {
      if (this.decompressor != null) {
         this.decompressor.setSrcWidth(srcWidth);
      }

      super.setSrcWidth(srcWidth);
   }

   public void setSrcHeight(int srcHeight) {
      if (this.decompressor != null) {
         this.decompressor.setSrcHeight(srcHeight);
      }

      super.setSrcHeight(srcHeight);
   }

   public void setSourceXOffset(int sourceXOffset) {
      if (this.decompressor != null) {
         this.decompressor.setSourceXOffset(sourceXOffset);
      }

      super.setSourceXOffset(sourceXOffset);
   }

   public void setDstXOffset(int dstXOffset) {
      if (this.decompressor != null) {
         this.decompressor.setDstXOffset(dstXOffset);
      }

      super.setDstXOffset(dstXOffset);
   }

   public void setSourceYOffset(int sourceYOffset) {
      if (this.decompressor != null) {
         this.decompressor.setSourceYOffset(sourceYOffset);
      }

      super.setSourceYOffset(sourceYOffset);
   }

   public void setDstYOffset(int dstYOffset) {
      if (this.decompressor != null) {
         this.decompressor.setDstYOffset(dstYOffset);
      }

      super.setDstYOffset(dstYOffset);
   }

   public void setSourceBands(int[] sourceBands) {
      if (this.decompressor != null) {
         this.decompressor.setSourceBands(sourceBands);
      }

      super.setSourceBands(sourceBands);
   }

   public void setDestinationBands(int[] destinationBands) {
      if (this.decompressor != null) {
         this.decompressor.setDestinationBands(destinationBands);
      }

      super.setDestinationBands(destinationBands);
   }

   public void setImage(BufferedImage image) {
      if (this.decompressor != null) {
         ColorModel cm = image.getColorModel();
         this.tmpImage = new BufferedImage(cm, image.getRaster().createCompatibleWritableRaster(1, 1), cm.isAlphaPremultiplied(), (Hashtable)null);
         this.decompressor.setImage(this.tmpImage);
      }

      super.setImage(image);
   }

   public void setDstMinX(int dstMinX) {
      if (this.decompressor != null) {
         this.decompressor.setDstMinX(dstMinX);
      }

      super.setDstMinX(dstMinX);
   }

   public void setDstMinY(int dstMinY) {
      if (this.decompressor != null) {
         this.decompressor.setDstMinY(dstMinY);
      }

      super.setDstMinY(dstMinY);
   }

   public void setDstWidth(int dstWidth) {
      if (this.decompressor != null) {
         this.decompressor.setDstWidth(dstWidth);
      }

      super.setDstWidth(dstWidth);
   }

   public void setDstHeight(int dstHeight) {
      if (this.decompressor != null) {
         this.decompressor.setDstHeight(dstHeight);
      }

      super.setDstHeight(dstHeight);
   }

   public void setActiveSrcMinX(int activeSrcMinX) {
      if (this.decompressor != null) {
         this.decompressor.setActiveSrcMinX(activeSrcMinX);
      }

      super.setActiveSrcMinX(activeSrcMinX);
   }

   public void setActiveSrcMinY(int activeSrcMinY) {
      if (this.decompressor != null) {
         this.decompressor.setActiveSrcMinY(activeSrcMinY);
      }

      super.setActiveSrcMinY(activeSrcMinY);
   }

   public void setActiveSrcWidth(int activeSrcWidth) {
      if (this.decompressor != null) {
         this.decompressor.setActiveSrcWidth(activeSrcWidth);
      }

      super.setActiveSrcWidth(activeSrcWidth);
   }

   public void setActiveSrcHeight(int activeSrcHeight) {
      if (this.decompressor != null) {
         this.decompressor.setActiveSrcHeight(activeSrcHeight);
      }

      super.setActiveSrcHeight(activeSrcHeight);
   }

   private byte clamp(int f) {
      if (f < 0) {
         return 0;
      } else {
         return f > 16711680 ? -1 : (byte)(f >> 16);
      }
   }

   public void beginDecoding() {
      if (this.decompressor != null) {
         this.decompressor.beginDecoding();
      }

      TIFFImageMetadata tmetadata = (TIFFImageMetadata)this.metadata;
      TIFFField f = tmetadata.getTIFFField(530);
      if (f != null) {
         if (f.getCount() == 2) {
            this.chromaSubsampleH = f.getAsInt(0);
            this.chromaSubsampleV = f.getAsInt(1);
            if (this.chromaSubsampleH != 1 && this.chromaSubsampleH != 2 && this.chromaSubsampleH != 4) {
               this.warning("Y_CB_CR_SUBSAMPLING[0] has illegal value " + this.chromaSubsampleH + " (should be 1, 2, or 4), setting to 1");
               this.chromaSubsampleH = 1;
            }

            if (this.chromaSubsampleV != 1 && this.chromaSubsampleV != 2 && this.chromaSubsampleV != 4) {
               this.warning("Y_CB_CR_SUBSAMPLING[1] has illegal value " + this.chromaSubsampleV + " (should be 1, 2, or 4), setting to 1");
               this.chromaSubsampleV = 1;
            }
         } else {
            this.warning("Y_CB_CR_SUBSAMPLING count != 2, assuming no subsampling");
         }
      }

      f = tmetadata.getTIFFField(529);
      if (f != null) {
         if (f.getCount() == 3) {
            this.LumaRed = f.getAsFloat(0);
            this.LumaGreen = f.getAsFloat(1);
            this.LumaBlue = f.getAsFloat(2);
         } else {
            this.warning("Y_CB_CR_COEFFICIENTS count != 3, assuming default values for CCIR 601-1");
         }
      }

      f = tmetadata.getTIFFField(532);
      if (f != null) {
         if (f.getCount() == 6) {
            this.referenceBlackY = f.getAsFloat(0);
            this.referenceWhiteY = f.getAsFloat(1);
            this.referenceBlackCb = f.getAsFloat(2);
            this.referenceWhiteCb = f.getAsFloat(3);
            this.referenceBlackCr = f.getAsFloat(4);
            this.referenceWhiteCr = f.getAsFloat(5);
         } else {
            this.warning("REFERENCE_BLACK_WHITE count != 6, ignoring it");
         }
      } else {
         this.warning("REFERENCE_BLACK_WHITE not found, assuming 0-255/128-255/128-255");
      }

      this.colorConvert = true;
      float BCb = 2.0F - 2.0F * this.LumaBlue;
      float RCr = 2.0F - 2.0F * this.LumaRed;
      float GY = (1.0F - this.LumaBlue - this.LumaRed) / this.LumaGreen;
      float GCb = 2.0F * this.LumaBlue * (this.LumaBlue - 1.0F) / this.LumaGreen;
      float GCr = 2.0F * this.LumaRed * (this.LumaRed - 1.0F) / this.LumaGreen;

      for(int i = 0; i < 256; ++i) {
         float fY = ((float)i - this.referenceBlackY) * this.codingRangeY / (this.referenceWhiteY - this.referenceBlackY);
         float fCb = ((float)i - this.referenceBlackCb) * 127.0F / (this.referenceWhiteCb - this.referenceBlackCb);
         float fCr = ((float)i - this.referenceBlackCr) * 127.0F / (this.referenceWhiteCr - this.referenceBlackCr);
         this.iYTab[i] = (int)(fY * 65536.0F);
         this.iCbTab[i] = (int)(fCb * BCb * 65536.0F);
         this.iCrTab[i] = (int)(fCr * RCr * 65536.0F);
         this.iGYTab[i] = (int)(fY * GY * 65536.0F);
         this.iGCbTab[i] = (int)(fCb * GCb * 65536.0F);
         this.iGCrTab[i] = (int)(fCr * GCr * 65536.0F);
      }

   }

   public void decodeRaw(byte[] buf, int dstOffset, int bitsPerPixel, int scanlineStride) throws IOException {
      byte[] var10000 = new byte[3 * this.srcWidth * this.chromaSubsampleV];
      int elementsPerPacket = this.chromaSubsampleH * this.chromaSubsampleV + 2;
      byte[] packet = new byte[elementsPerPacket];
      int y;
      if (this.decompressor != null) {
         y = 3 * this.srcWidth;
         byte[] tmpBuf = new byte[y * this.srcHeight];
         this.decompressor.decodeRaw(tmpBuf, dstOffset, bitsPerPixel, y);
         ByteArrayInputStream byteStream = new ByteArrayInputStream(tmpBuf);
         this.stream = new MemoryCacheImageInputStream(byteStream);
      } else {
         this.stream.seek(this.offset);
      }

      for(y = this.srcMinY; y < this.srcMinY + this.srcHeight; y += this.chromaSubsampleV) {
         for(int x = this.srcMinX; x < this.srcMinX + this.srcWidth; x += this.chromaSubsampleH) {
            try {
               this.stream.readFully(packet);
            } catch (EOFException var30) {
               System.out.println("e = " + var30);
               return;
            }

            byte Cb = packet[elementsPerPacket - 2];
            byte Cr = packet[elementsPerPacket - 1];
            int iCb = 0;
            int iCr = 0;
            int iGCb = 0;
            int iGCr = 0;
            int yIndex;
            int v;
            if (this.colorConvert) {
               yIndex = Cb & 255;
               v = Cr & 255;
               iCb = this.iCbTab[yIndex];
               iCr = this.iCrTab[v];
               iGCb = this.iGCbTab[yIndex];
               iGCr = this.iGCrTab[v];
            }

            yIndex = 0;

            for(v = 0; v < this.chromaSubsampleV; ++v) {
               int idx = dstOffset + 3 * (x - this.srcMinX) + scanlineStride * (y - this.srcMinY + v);
               if (y + v >= this.srcMinY + this.srcHeight) {
                  break;
               }

               for(int h = 0; h < this.chromaSubsampleH && x + h < this.srcMinX + this.srcWidth; ++h) {
                  byte Y = packet[yIndex++];
                  if (this.colorConvert) {
                     int Yp = Y & 255;
                     int iY = this.iYTab[Yp];
                     int iGY = this.iGYTab[Yp];
                     int iR = iY + iCr;
                     int iG = iGY + iGCb + iGCr;
                     int iB = iY + iCb;
                     byte r = this.clamp(iR);
                     byte g = this.clamp(iG);
                     byte b = this.clamp(iB);
                     buf[idx] = r;
                     buf[idx + 1] = g;
                     buf[idx + 2] = b;
                  } else {
                     buf[idx] = Y;
                     buf[idx + 1] = Cb;
                     buf[idx + 2] = Cr;
                  }

                  idx += 3;
               }
            }
         }
      }

   }
}
