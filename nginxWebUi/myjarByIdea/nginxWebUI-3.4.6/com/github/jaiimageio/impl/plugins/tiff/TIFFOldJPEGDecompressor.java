package com.github.jaiimageio.impl.plugins.tiff;

import com.github.jaiimageio.plugins.tiff.TIFFField;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.imageio.IIOException;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.MemoryCacheImageInputStream;

public class TIFFOldJPEGDecompressor extends TIFFJPEGDecompressor {
   private static final boolean DEBUG = false;
   private static final int DHT = 196;
   private static final int DQT = 219;
   private static final int DRI = 221;
   private static final int SOF0 = 192;
   private static final int SOS = 218;
   private boolean isInitialized = false;
   private Long JPEGStreamOffset = null;
   private int SOFPosition = -1;
   private byte[] SOSMarker = null;
   private int subsamplingX = 2;
   private int subsamplingY = 2;

   private synchronized void initialize() throws IOException {
      if (!this.isInitialized) {
         TIFFImageMetadata tim = (TIFFImageMetadata)this.metadata;
         TIFFField JPEGInterchangeFormatField = tim.getTIFFField(513);
         TIFFField segmentOffsetField = tim.getTIFFField(324);
         if (segmentOffsetField == null) {
            segmentOffsetField = tim.getTIFFField(273);
            if (segmentOffsetField == null) {
               segmentOffsetField = JPEGInterchangeFormatField;
            }
         }

         long[] segmentOffsets = segmentOffsetField.getAsLongs();
         boolean isTiled = segmentOffsets.length > 1;
         if (!isTiled) {
            this.stream.seek(this.offset);
            this.stream.mark();
            if (this.stream.read() == 255 && this.stream.read() == 216) {
               this.JPEGStreamOffset = new Long(this.offset);
               ((TIFFImageReader)this.reader).forwardWarningMessage("SOI marker detected at start of strip or tile.");
               this.isInitialized = true;
               this.stream.reset();
               return;
            }

            this.stream.reset();
            if (JPEGInterchangeFormatField != null) {
               long jpegInterchangeOffset = JPEGInterchangeFormatField.getAsLong(0);
               this.stream.mark();
               this.stream.seek(jpegInterchangeOffset);
               if (this.stream.read() == 255 && this.stream.read() == 216) {
                  this.JPEGStreamOffset = new Long(jpegInterchangeOffset);
               } else {
                  ((TIFFImageReader)this.reader).forwardWarningMessage("JPEGInterchangeFormat does not point to SOI");
               }

               this.stream.reset();
               TIFFField JPEGInterchangeFormatLengthField = tim.getTIFFField(514);
               if (JPEGInterchangeFormatLengthField == null) {
                  ((TIFFImageReader)this.reader).forwardWarningMessage("JPEGInterchangeFormatLength field is missing");
               } else {
                  long jpegInterchangeLength = JPEGInterchangeFormatLengthField.getAsLong(0);
                  if (jpegInterchangeOffset >= segmentOffsets[0] || jpegInterchangeOffset + jpegInterchangeLength <= segmentOffsets[0]) {
                     ((TIFFImageReader)this.reader).forwardWarningMessage("JPEGInterchangeFormatLength field value is invalid");
                  }
               }

               if (this.JPEGStreamOffset != null) {
                  this.isInitialized = true;
                  return;
               }
            }
         }

         TIFFField YCbCrSubsamplingField = tim.getTIFFField(530);
         if (YCbCrSubsamplingField != null) {
            this.subsamplingX = YCbCrSubsamplingField.getAsChars()[0];
            this.subsamplingY = YCbCrSubsamplingField.getAsChars()[1];
         }

         if (JPEGInterchangeFormatField != null) {
            long jpegInterchangeOffset = JPEGInterchangeFormatField.getAsLong(0);
            TIFFField JPEGInterchangeFormatLengthField = tim.getTIFFField(514);
            if (JPEGInterchangeFormatLengthField != null) {
               long jpegInterchangeLength = JPEGInterchangeFormatLengthField.getAsLong(0);
               if (jpegInterchangeLength >= 2L && jpegInterchangeOffset + jpegInterchangeLength <= segmentOffsets[0]) {
                  this.stream.mark();
                  this.stream.seek(jpegInterchangeOffset + jpegInterchangeLength - 2L);
                  if (this.stream.read() == 255 && this.stream.read() == 217) {
                     this.tables = new byte[(int)(jpegInterchangeLength - 2L)];
                  } else {
                     this.tables = new byte[(int)jpegInterchangeLength];
                  }

                  this.stream.reset();
                  this.stream.mark();
                  this.stream.seek(jpegInterchangeOffset);
                  this.stream.readFully(this.tables);
                  this.stream.reset();
                  ((TIFFImageReader)this.reader).forwardWarningMessage("Incorrect JPEG interchange format: using JPEGInterchangeFormat offset to derive tables.");
               } else {
                  ((TIFFImageReader)this.reader).forwardWarningMessage("JPEGInterchangeFormat+JPEGInterchangeFormatLength > offset to first strip or tile.");
               }
            }
         }

         int k;
         if (this.tables == null) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            long streamLength = this.stream.length();
            baos.write(255);
            baos.write(216);
            TIFFField f = tim.getTIFFField(519);
            if (f == null) {
               throw new IIOException("JPEGQTables field missing!");
            }

            long[] off = f.getAsLongs();

            int i;
            for(k = 0; k < off.length; ++k) {
               baos.write(255);
               baos.write(219);
               i = 67;
               baos.write(i >>> 8 & 255);
               baos.write(i & 255);
               baos.write(k);
               byte[] qtable = new byte[64];
               if (streamLength != -1L && off[k] > streamLength) {
                  throw new IIOException("JPEGQTables offset for index " + k + " is not in the stream!");
               }

               this.stream.seek(off[k]);
               this.stream.readFully(qtable);
               baos.write(qtable);
            }

            for(k = 0; k < 2; ++k) {
               i = k == 0 ? 520 : 521;
               f = tim.getTIFFField(i);
               String fieldName = i == 520 ? "JPEGDCTables" : "JPEGACTables";
               if (f == null) {
                  throw new IIOException(fieldName + " field missing!");
               }

               off = f.getAsLongs();

               for(int i = 0; i < off.length; ++i) {
                  baos.write(255);
                  baos.write(196);
                  byte[] blengths = new byte[16];
                  if (streamLength != -1L && off[i] > streamLength) {
                     throw new IIOException(fieldName + " offset for index " + i + " is not in the stream!");
                  }

                  this.stream.seek(off[i]);
                  this.stream.readFully(blengths);
                  int numCodes = 0;

                  for(int j = 0; j < 16; ++j) {
                     numCodes += blengths[j] & 255;
                  }

                  char markerLength = (char)(19 + numCodes);
                  baos.write(markerLength >>> 8 & 255);
                  baos.write(markerLength & 255);
                  baos.write(i | k << 4);
                  baos.write(blengths);
                  byte[] bcodes = new byte[numCodes];
                  this.stream.readFully(bcodes);
                  baos.write(bcodes);
               }
            }

            baos.write(-1);
            baos.write(-64);
            short sval = (short)(8 + 3 * this.samplesPerPixel);
            baos.write((byte)(sval >>> 8 & 255));
            baos.write((byte)(sval & 255));
            baos.write(8);
            sval = (short)this.srcHeight;
            baos.write((byte)(sval >>> 8 & 255));
            baos.write((byte)(sval & 255));
            sval = (short)this.srcWidth;
            baos.write((byte)(sval >>> 8 & 255));
            baos.write((byte)(sval & 255));
            baos.write((byte)this.samplesPerPixel);
            if (this.samplesPerPixel == 1) {
               baos.write(1);
               baos.write(17);
               baos.write(0);
            } else {
               for(i = 0; i < 3; ++i) {
                  baos.write((byte)(i + 1));
                  baos.write(i != 0 ? 17 : (byte)((this.subsamplingX & 15) << 4 | this.subsamplingY & 15));
                  baos.write((byte)i);
               }
            }

            f = tim.getTIFFField(515);
            if (f != null) {
               char restartInterval = f.getAsChars()[0];
               if (restartInterval != 0) {
                  baos.write(-1);
                  baos.write(-35);
                  short sval = 4;
                  baos.write((byte)(sval >>> 8 & 255));
                  baos.write((byte)(sval & 255));
                  baos.write((byte)(restartInterval >>> 8 & 255));
                  baos.write((byte)(restartInterval & 255));
               }
            }

            this.tables = baos.toByteArray();
         }

         int idx = 0;

         for(int idxMax = this.tables.length - 1; idx < idxMax; ++idx) {
            if ((this.tables[idx] & 255) == 255 && (this.tables[idx + 1] & 255) == 192) {
               this.SOFPosition = idx;
               break;
            }
         }

         int i;
         if (this.SOFPosition == -1) {
            byte[] tmpTables = new byte[this.tables.length + 10 + 3 * this.samplesPerPixel];
            System.arraycopy(this.tables, 0, tmpTables, 0, this.tables.length);
            int tmpOffset = this.tables.length;
            this.SOFPosition = this.tables.length;
            this.tables = tmpTables;
            this.tables[tmpOffset++] = -1;
            this.tables[tmpOffset++] = -64;
            i = (short)(8 + 3 * this.samplesPerPixel);
            this.tables[tmpOffset++] = (byte)(i >>> 8 & 255);
            this.tables[tmpOffset++] = (byte)(i & 255);
            this.tables[tmpOffset++] = 8;
            i = (short)this.srcHeight;
            this.tables[tmpOffset++] = (byte)(i >>> 8 & 255);
            this.tables[tmpOffset++] = (byte)(i & 255);
            i = (short)this.srcWidth;
            this.tables[tmpOffset++] = (byte)(i >>> 8 & 255);
            this.tables[tmpOffset++] = (byte)(i & 255);
            this.tables[tmpOffset++] = (byte)this.samplesPerPixel;
            if (this.samplesPerPixel == 1) {
               this.tables[tmpOffset++] = 1;
               this.tables[tmpOffset++] = 17;
               this.tables[tmpOffset++] = 0;
            } else {
               for(k = 0; k < 3; ++k) {
                  this.tables[tmpOffset++] = (byte)(k + 1);
                  this.tables[tmpOffset++] = k != 0 ? 17 : (byte)((this.subsamplingX & 15) << 4 | this.subsamplingY & 15);
                  this.tables[tmpOffset++] = (byte)k;
               }
            }
         }

         this.stream.mark();
         this.stream.seek(segmentOffsets[0]);
         int SOSMarkerIndex;
         if (this.stream.read() == 255 && this.stream.read() == 218) {
            SOSMarkerIndex = this.stream.read() << 8 | this.stream.read();
            this.SOSMarker = new byte[SOSMarkerIndex + 2];
            this.SOSMarker[0] = -1;
            this.SOSMarker[1] = -38;
            this.SOSMarker[2] = (byte)((SOSMarkerIndex & '\uff00') >> 8);
            this.SOSMarker[3] = (byte)(SOSMarkerIndex & 255);
            this.stream.readFully(this.SOSMarker, 4, SOSMarkerIndex - 2);
         } else {
            this.SOSMarker = new byte[8 + 2 * this.samplesPerPixel];
            SOSMarkerIndex = 0;
            this.SOSMarker[SOSMarkerIndex++] = -1;
            this.SOSMarker[SOSMarkerIndex++] = -38;
            short sval = (short)(6 + 2 * this.samplesPerPixel);
            this.SOSMarker[SOSMarkerIndex++] = (byte)(sval >>> 8 & 255);
            this.SOSMarker[SOSMarkerIndex++] = (byte)(sval & 255);
            this.SOSMarker[SOSMarkerIndex++] = (byte)this.samplesPerPixel;
            if (this.samplesPerPixel == 1) {
               this.SOSMarker[SOSMarkerIndex++] = 1;
               this.SOSMarker[SOSMarkerIndex++] = 0;
            } else {
               for(i = 0; i < 3; ++i) {
                  this.SOSMarker[SOSMarkerIndex++] = (byte)(i + 1);
                  this.SOSMarker[SOSMarkerIndex++] = (byte)(i << 4 | i);
               }
            }

            this.SOSMarker[SOSMarkerIndex++] = 0;
            this.SOSMarker[SOSMarkerIndex++] = 63;
            this.SOSMarker[SOSMarkerIndex++] = 0;
         }

         this.stream.reset();
         this.isInitialized = true;
      }
   }

   public void decodeRaw(byte[] b, int dstOffset, int bitsPerPixel, int scanlineStride) throws IOException {
      this.initialize();
      TIFFImageMetadata tim = (TIFFImageMetadata)this.metadata;
      if (this.JPEGStreamOffset != null) {
         this.stream.seek(this.JPEGStreamOffset);
         this.JPEGReader.setInput(this.stream, false, true);
      } else {
         int tableLength = this.tables.length;
         int bufLength = tableLength + this.SOSMarker.length + this.byteCount + 2;
         byte[] buf = new byte[bufLength];
         if (this.tables != null) {
            System.arraycopy(this.tables, 0, buf, 0, tableLength);
         }

         int bufOffset = tableLength;
         short sval = (short)this.srcHeight;
         buf[this.SOFPosition + 5] = (byte)(sval >>> 8 & 255);
         buf[this.SOFPosition + 6] = (byte)(sval & 255);
         sval = (short)this.srcWidth;
         buf[this.SOFPosition + 7] = (byte)(sval >>> 8 & 255);
         buf[this.SOFPosition + 8] = (byte)(sval & 255);
         this.stream.seek(this.offset);
         byte[] twoBytes = new byte[2];
         this.stream.readFully(twoBytes);
         if ((twoBytes[0] & 255) != 255 || (twoBytes[1] & 255) != 218) {
            System.arraycopy(this.SOSMarker, 0, buf, tableLength, this.SOSMarker.length);
            bufOffset = tableLength + this.SOSMarker.length;
         }

         buf[bufOffset++] = twoBytes[0];
         buf[bufOffset++] = twoBytes[1];
         this.stream.readFully(buf, bufOffset, this.byteCount - 2);
         bufOffset += this.byteCount - 2;
         buf[bufOffset++] = -1;
         buf[bufOffset++] = -39;
         ByteArrayInputStream bais = new ByteArrayInputStream(buf, 0, bufOffset);
         ImageInputStream is = new MemoryCacheImageInputStream(bais);
         this.JPEGReader.setInput(is, true, true);
      }

      this.JPEGParam.setDestination(this.rawImage);
      this.JPEGReader.read(0, this.JPEGParam);
   }

   protected void finalize() throws Throwable {
      super.finalize();
      this.JPEGReader.dispose();
   }
}
