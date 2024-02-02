package com.github.jaiimageio.impl.plugins.tiff;

import com.github.jaiimageio.plugins.tiff.TIFFCompressor;
import com.github.jaiimageio.plugins.tiff.TIFFField;
import javax.imageio.metadata.IIOMetadata;

public abstract class TIFFFaxCompressor extends TIFFCompressor {
   public static final int WHITE = 0;
   public static final int BLACK = 1;
   public static byte[] byteTable = new byte[]{8, 7, 6, 6, 5, 5, 5, 5, 4, 4, 4, 4, 4, 4, 4, 4, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
   public static int[] termCodesBlack = new int[]{230686730, 1073741827, -1073741822, -2147483646, 1610612739, 805306372, 536870916, 402653189, 335544326, 268435462, 134217735, 167772167, 234881031, 67108872, 117440520, 201326601, 96469002, 100663306, 33554442, 216006667, 218103819, 226492427, 115343371, 83886091, 48234507, 50331659, 211812364, 212860940, 213909516, 214958092, 109051916, 110100492, 111149068, 112197644, 220200972, 221249548, 222298124, 223346700, 224395276, 225443852, 113246220, 114294796, 228589580, 229638156, 88080396, 89128972, 90177548, 91226124, 104857612, 105906188, 85983244, 87031820, 37748748, 57671692, 58720268, 40894476, 41943052, 92274700, 93323276, 45088780, 46137356, 94371852, 106954764, 108003340};
   public static int[] termCodesWhite = new int[]{889192456, 469762054, 1879048196, -2147483644, -1342177276, -1073741820, -536870908, -268435452, -1744830459, -1610612731, 939524101, 1073741829, 536870918, 201326598, -805306362, -738197498, -1476395002, -1409286138, 1308622855, 402653191, 268435463, 771751943, 100663303, 134217735, 1342177287, 1442840583, 637534215, 1207959559, 805306375, 33554440, 50331656, 436207624, 452984840, 301989896, 318767112, 335544328, 352321544, 369098760, 385875976, 671088648, 687865864, 704643080, 721420296, 738197512, 754974728, 67108872, 83886088, 167772168, 184549384, 1375731720, 1392508936, 1409286152, 1426063368, 603979784, 620757000, 1476395016, 1493172232, 1509949448, 1526726664, 1241513992, 1258291208, 838860808, 855638024, 872415240};
   public static int[] makeupCodesBlack = new int[]{0, 62914570, 209715212, 210763788, 95420428, 53477388, 54525964, 55574540, 56623117, 57147405, 38797325, 39321613, 39845901, 40370189, 59768845, 60293133, 60817421, 61341709, 61865997, 62390285, 42991629, 43515917, 44040205, 44564493, 47185933, 47710221, 52428813, 52953101, 16777227, 25165835, 27262987, 18874380, 19922956, 20971532, 22020108, 23068684, 24117260, 29360140, 30408716, 31457292, 32505868, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
   public static int[] makeupCodesWhite = new int[]{0, -671088635, -1879048187, 1543503878, 1845493767, 905969672, 922746888, 1677721608, 1694498824, 1744830472, 1728053256, 1711276041, 1719664649, 1761607689, 1769996297, 1778384905, 1786773513, 1795162121, 1803550729, 1811939337, 1820327945, 1828716553, 1837105161, 1275068425, 1283457033, 1291845641, 1610612742, 1300234249, 16777227, 25165835, 27262987, 18874380, 19922956, 20971532, 22020108, 23068684, 24117260, 29360140, 30408716, 31457292, 32505868, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
   public static int[] passMode = new int[]{268435460};
   public static int[] vertMode = new int[]{100663303, 201326598, 1610612739, -2147483647, 1073741827, 134217734, 67108871};
   public static int[] horzMode = new int[]{536870915};
   public static int[][] termCodes;
   public static int[][] makeupCodes;
   public static int[][] pass;
   public static int[][] vert;
   public static int[][] horz;
   public boolean inverseFill = false;
   public int bits;
   public int ndex;

   protected TIFFFaxCompressor(String compressionType, int compressionTagValue, boolean isCompressionLossless) {
      super(compressionType, compressionTagValue, isCompressionLossless);
   }

   public void setMetadata(IIOMetadata metadata) {
      super.setMetadata(metadata);
      if (metadata instanceof TIFFImageMetadata) {
         TIFFImageMetadata tim = (TIFFImageMetadata)metadata;
         TIFFField f = tim.getTIFFField(266);
         this.inverseFill = f != null && f.getAsInt(0) == 2;
      }

   }

   public int nextState(byte[] data, int base, int bitOffset, int maxOffset) {
      if (data == null) {
         return maxOffset;
      } else {
         int next = base + (bitOffset >>> 3);
         if (next >= data.length) {
            return maxOffset;
         } else {
            int end = base + (maxOffset >>> 3);
            if (end == data.length) {
               --end;
            }

            int extra = bitOffset & 7;
            int testbyte;
            if ((data[next] & 128 >>> extra) != 0) {
               for(testbyte = ~data[next] & 255 >>> extra; next < end && testbyte == 0; testbyte = ~data[next] & 255) {
                  ++next;
               }
            } else {
               if ((testbyte = data[next] & 255 >>> extra) != 0) {
                  bitOffset = (next - base) * 8 + byteTable[testbyte];
                  return bitOffset < maxOffset ? bitOffset : maxOffset;
               }

               while(next < end) {
                  ++next;
                  if ((testbyte = data[next] & 255) != 0) {
                     bitOffset = (next - base) * 8 + byteTable[testbyte];
                     return bitOffset < maxOffset ? bitOffset : maxOffset;
                  }
               }
            }

            bitOffset = (next - base) * 8 + byteTable[testbyte];
            return bitOffset < maxOffset ? bitOffset : maxOffset;
         }
      }
   }

   public void initBitBuf() {
      this.ndex = 0;
      this.bits = 0;
   }

   public int add1DBits(byte[] buf, int where, int count, int color) {
      int len = where;
      int sixtyfours = count >>> 6;
      count &= 63;
      int mask;
      if (sixtyfours != 0) {
         label37:
         while(true) {
            if (sixtyfours <= 40) {
               mask = makeupCodes[color][sixtyfours];
               this.bits |= (mask & -524288) >>> this.ndex;
               this.ndex += mask & '\uffff';

               while(true) {
                  if (this.ndex <= 7) {
                     break label37;
                  }

                  buf[len++] = (byte)(this.bits >>> 24);
                  this.bits <<= 8;
                  this.ndex -= 8;
               }
            }

            mask = makeupCodes[color][40];
            this.bits |= (mask & -524288) >>> this.ndex;

            for(this.ndex += mask & '\uffff'; this.ndex > 7; this.ndex -= 8) {
               buf[len++] = (byte)(this.bits >>> 24);
               this.bits <<= 8;
            }

            sixtyfours -= 40;
         }
      }

      mask = termCodes[color][count];
      this.bits |= (mask & -524288) >>> this.ndex;

      for(this.ndex += mask & '\uffff'; this.ndex > 7; this.ndex -= 8) {
         buf[len++] = (byte)(this.bits >>> 24);
         this.bits <<= 8;
      }

      return len - where;
   }

   public int add2DBits(byte[] buf, int where, int[][] mode, int entry) {
      int len = where;
      int color = 0;
      int mask = mode[color][entry];
      this.bits |= (mask & -524288) >>> this.ndex;

      for(this.ndex += mask & '\uffff'; this.ndex > 7; this.ndex -= 8) {
         buf[len++] = (byte)(this.bits >>> 24);
         this.bits <<= 8;
      }

      return len - where;
   }

   public int addEOL(boolean is1DMode, boolean addFill, boolean add1, byte[] buf, int where) {
      int len = where;
      if (addFill) {
         this.ndex += this.ndex <= 4 ? 4 - this.ndex : 12 - this.ndex;
      }

      if (is1DMode) {
         this.bits |= 1048576 >>> this.ndex;
         this.ndex += 12;
      } else {
         this.bits |= (add1 ? 1572864 : 1048576) >>> this.ndex;
         this.ndex += 13;
      }

      while(this.ndex > 7) {
         buf[len++] = (byte)(this.bits >>> 24);
         this.bits <<= 8;
         this.ndex -= 8;
      }

      return len - where;
   }

   public int addEOFB(byte[] buf, int where) {
      int len = where;
      this.bits |= 1048832 >>> this.ndex;

      for(this.ndex += 24; this.ndex > 0; this.ndex -= 8) {
         buf[len++] = (byte)(this.bits >>> 24);
         this.bits <<= 8;
      }

      return len - where;
   }

   public int encode1D(byte[] data, int rowOffset, int colOffset, int rowLength, byte[] compData, int compOffset) {
      int lineAddr = rowOffset;
      int bitIndex = colOffset;
      int last = colOffset + rowLength;
      int outIndex = compOffset;
      int testbit = (data[rowOffset + (colOffset >>> 3)] & 255) >>> 7 - (colOffset & 7) & 1;
      int currentColor = 1;
      if (testbit != 0) {
         outIndex = compOffset + this.add1DBits(compData, compOffset, 0, 0);
      } else {
         currentColor = 0;
      }

      while(bitIndex < last) {
         int bitCount = this.nextState(data, lineAddr, bitIndex, last) - bitIndex;
         outIndex += this.add1DBits(compData, outIndex, bitCount, currentColor);
         bitIndex += bitCount;
         currentColor ^= 1;
      }

      return outIndex - compOffset;
   }

   static {
      termCodes = new int[][]{termCodesWhite, termCodesBlack};
      makeupCodes = new int[][]{makeupCodesWhite, makeupCodesBlack};
      pass = new int[][]{passMode, passMode};
      vert = new int[][]{vertMode, vertMode};
      horz = new int[][]{horzMode, horzMode};
   }
}
