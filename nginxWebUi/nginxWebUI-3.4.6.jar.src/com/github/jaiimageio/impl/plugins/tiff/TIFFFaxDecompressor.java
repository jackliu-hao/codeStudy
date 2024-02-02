/*      */ package com.github.jaiimageio.impl.plugins.tiff;
/*      */ 
/*      */ import com.github.jaiimageio.plugins.tiff.TIFFDecompressor;
/*      */ import com.github.jaiimageio.plugins.tiff.TIFFField;
/*      */ import java.io.ByteArrayOutputStream;
/*      */ import java.io.EOFException;
/*      */ import java.io.IOException;
/*      */ import java.io.PrintStream;
/*      */ import javax.imageio.IIOException;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class TIFFFaxDecompressor
/*      */   extends TIFFDecompressor
/*      */ {
/*      */   protected int fillOrder;
/*      */   protected int compression;
/*      */   private int t4Options;
/*      */   private int t6Options;
/*   76 */   protected int uncompressedMode = 0;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*   82 */   protected int fillBits = 0;
/*      */   
/*      */   protected int oneD;
/*      */   
/*      */   private byte[] data;
/*      */   
/*      */   private int bitPointer;
/*      */   
/*      */   private int bytePointer;
/*      */   
/*      */   private byte[] buffer;
/*      */   
/*      */   private int w;
/*      */   
/*      */   private int h;
/*      */   private int bitsPerScanline;
/*      */   private int lineBitNum;
/*   99 */   private int changingElemSize = 0;
/*      */   
/*      */   private int[] prevChangingElems;
/*      */   
/*      */   private int[] currChangingElems;
/*  104 */   private int lastChangingElement = 0;
/*      */   
/*  106 */   static int[] table1 = new int[] { 0, 1, 3, 7, 15, 31, 63, 127, 255 };
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  118 */   static int[] table2 = new int[] { 0, 128, 192, 224, 240, 248, 252, 254, 255 };
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  131 */   static byte[] flipTable = new byte[] { 0, Byte.MIN_VALUE, 64, -64, 32, -96, 96, -32, 16, -112, 80, -48, 48, -80, 112, -16, 8, -120, 72, -56, 40, -88, 104, -24, 24, -104, 88, -40, 56, -72, 120, -8, 4, -124, 68, -60, 36, -92, 100, -28, 20, -108, 84, -44, 52, -76, 116, -12, 12, -116, 76, -52, 44, -84, 108, -20, 28, -100, 92, -36, 60, -68, 124, -4, 2, -126, 66, -62, 34, -94, 98, -30, 18, -110, 82, -46, 50, -78, 114, -14, 10, -118, 74, -54, 42, -86, 106, -22, 26, -102, 90, -38, 58, -70, 122, -6, 6, -122, 70, -58, 38, -90, 102, -26, 22, -106, 86, -42, 54, -74, 118, -10, 14, -114, 78, -50, 46, -82, 110, -18, 30, -98, 94, -34, 62, -66, 126, -2, 1, -127, 65, -63, 33, -95, 97, -31, 17, -111, 81, -47, 49, -79, 113, -15, 9, -119, 73, -55, 41, -87, 105, -23, 25, -103, 89, -39, 57, -71, 121, -7, 5, -123, 69, -59, 37, -91, 101, -27, 21, -107, 85, -43, 53, -75, 117, -11, 13, -115, 77, -51, 45, -83, 109, -19, 29, -99, 93, -35, 61, -67, 125, -3, 3, -125, 67, -61, 35, -93, 99, -29, 19, -109, 83, -45, 51, -77, 115, -13, 11, -117, 75, -53, 43, -85, 107, -21, 27, -101, 91, -37, 59, -69, 123, -5, 7, -121, 71, -57, 39, -89, 103, -25, 23, -105, 87, -41, 55, -73, 119, -9, 15, -113, 79, -49, 47, -81, 111, -17, 31, -97, 95, -33, 63, -65, Byte.MAX_VALUE, -1 };
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  167 */   static short[] white = new short[] { 6430, 6400, 6400, 6400, 3225, 3225, 3225, 3225, 944, 944, 944, 944, 976, 976, 976, 976, 1456, 1456, 1456, 1456, 1488, 1488, 1488, 1488, 718, 718, 718, 718, 718, 718, 718, 718, 750, 750, 750, 750, 750, 750, 750, 750, 1520, 1520, 1520, 1520, 1552, 1552, 1552, 1552, 428, 428, 428, 428, 428, 428, 428, 428, 428, 428, 428, 428, 428, 428, 428, 428, 654, 654, 654, 654, 654, 654, 654, 654, 1072, 1072, 1072, 1072, 1104, 1104, 1104, 1104, 1136, 1136, 1136, 1136, 1168, 1168, 1168, 1168, 1200, 1200, 1200, 1200, 1232, 1232, 1232, 1232, 622, 622, 622, 622, 622, 622, 622, 622, 1008, 1008, 1008, 1008, 1040, 1040, 1040, 1040, 44, 44, 44, 44, 44, 44, 44, 44, 44, 44, 44, 44, 44, 44, 44, 44, 396, 396, 396, 396, 396, 396, 396, 396, 396, 396, 396, 396, 396, 396, 396, 396, 1712, 1712, 1712, 1712, 1744, 1744, 1744, 1744, 846, 846, 846, 846, 846, 846, 846, 846, 1264, 1264, 1264, 1264, 1296, 1296, 1296, 1296, 1328, 1328, 1328, 1328, 1360, 1360, 1360, 1360, 1392, 1392, 1392, 1392, 1424, 1424, 1424, 1424, 686, 686, 686, 686, 686, 686, 686, 686, 910, 910, 910, 910, 910, 910, 910, 910, 1968, 1968, 1968, 1968, 2000, 2000, 2000, 2000, 2032, 2032, 2032, 2032, 16, 16, 16, 16, 10257, 10257, 10257, 10257, 12305, 12305, 12305, 12305, 330, 330, 330, 330, 330, 330, 330, 330, 330, 330, 330, 330, 330, 330, 330, 330, 330, 330, 330, 330, 330, 330, 330, 330, 330, 330, 330, 330, 330, 330, 330, 330, 362, 362, 362, 362, 362, 362, 362, 362, 362, 362, 362, 362, 362, 362, 362, 362, 362, 362, 362, 362, 362, 362, 362, 362, 362, 362, 362, 362, 362, 362, 362, 362, 878, 878, 878, 878, 878, 878, 878, 878, 1904, 1904, 1904, 1904, 1936, 1936, 1936, 1936, -18413, -18413, -16365, -16365, -14317, -14317, -10221, -10221, 590, 590, 590, 590, 590, 590, 590, 590, 782, 782, 782, 782, 782, 782, 782, 782, 1584, 1584, 1584, 1584, 1616, 1616, 1616, 1616, 1648, 1648, 1648, 1648, 1680, 1680, 1680, 1680, 814, 814, 814, 814, 814, 814, 814, 814, 1776, 1776, 1776, 1776, 1808, 1808, 1808, 1808, 1840, 1840, 1840, 1840, 1872, 1872, 1872, 1872, 6157, 6157, 6157, 6157, 6157, 6157, 6157, 6157, 6157, 6157, 6157, 6157, 6157, 6157, 6157, 6157, -12275, -12275, -12275, -12275, -12275, -12275, -12275, -12275, -12275, -12275, -12275, -12275, -12275, -12275, -12275, -12275, 14353, 14353, 14353, 14353, 16401, 16401, 16401, 16401, 22547, 22547, 24595, 24595, 20497, 20497, 20497, 20497, 18449, 18449, 18449, 18449, 26643, 26643, 28691, 28691, 30739, 30739, -32749, -32749, -30701, -30701, -28653, -28653, -26605, -26605, -24557, -24557, -22509, -22509, -20461, -20461, 8207, 8207, 8207, 8207, 8207, 8207, 8207, 8207, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 4107, 4107, 4107, 4107, 4107, 4107, 4107, 4107, 4107, 4107, 4107, 4107, 4107, 4107, 4107, 4107, 4107, 4107, 4107, 4107, 4107, 4107, 4107, 4107, 4107, 4107, 4107, 4107, 4107, 4107, 4107, 4107, 266, 266, 266, 266, 266, 266, 266, 266, 266, 266, 266, 266, 266, 266, 266, 266, 266, 266, 266, 266, 266, 266, 266, 266, 266, 266, 266, 266, 266, 266, 266, 266, 298, 298, 298, 298, 298, 298, 298, 298, 298, 298, 298, 298, 298, 298, 298, 298, 298, 298, 298, 298, 298, 298, 298, 298, 298, 298, 298, 298, 298, 298, 298, 298, 524, 524, 524, 524, 524, 524, 524, 524, 524, 524, 524, 524, 524, 524, 524, 524, 556, 556, 556, 556, 556, 556, 556, 556, 556, 556, 556, 556, 556, 556, 556, 556, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 460, 460, 460, 460, 460, 460, 460, 460, 460, 460, 460, 460, 460, 460, 460, 460, 492, 492, 492, 492, 492, 492, 492, 492, 492, 492, 492, 492, 492, 492, 492, 492, 2059, 2059, 2059, 2059, 2059, 2059, 2059, 2059, 2059, 2059, 2059, 2059, 2059, 2059, 2059, 2059, 2059, 2059, 2059, 2059, 2059, 2059, 2059, 2059, 2059, 2059, 2059, 2059, 2059, 2059, 2059, 2059, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232 };
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  427 */   static short[] additionalMakeup = new short[] { 28679, 28679, 31752, -32759, -31735, -30711, -29687, -28663, 29703, 29703, 30727, 30727, -27639, -26615, -25591, -24567 };
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  435 */   static short[] initBlack = new short[] { 3226, 6412, 200, 168, 38, 38, 134, 134, 100, 100, 100, 100, 68, 68, 68, 68 };
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  443 */   static short[] twoBitBlack = new short[] { 292, 260, 226, 226 };
/*      */ 
/*      */   
/*  446 */   static short[] black = new short[] { 62, 62, 30, 30, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3225, 3225, 3225, 3225, 3225, 3225, 3225, 3225, 3225, 3225, 3225, 3225, 3225, 3225, 3225, 3225, 3225, 3225, 3225, 3225, 3225, 3225, 3225, 3225, 3225, 3225, 3225, 3225, 3225, 3225, 3225, 3225, 588, 588, 588, 588, 588, 588, 588, 588, 1680, 1680, 20499, 22547, 24595, 26643, 1776, 1776, 1808, 1808, -24557, -22509, -20461, -18413, 1904, 1904, 1936, 1936, -16365, -14317, 782, 782, 782, 782, 814, 814, 814, 814, -12269, -10221, 10257, 10257, 12305, 12305, 14353, 14353, 16403, 18451, 1712, 1712, 1744, 1744, 28691, 30739, -32749, -30701, -28653, -26605, 2061, 2061, 2061, 2061, 2061, 2061, 2061, 2061, 424, 424, 424, 424, 424, 424, 424, 424, 424, 424, 424, 424, 424, 424, 424, 424, 424, 424, 424, 424, 424, 424, 424, 424, 424, 424, 424, 424, 424, 424, 424, 424, 750, 750, 750, 750, 1616, 1616, 1648, 1648, 1424, 1424, 1456, 1456, 1488, 1488, 1520, 1520, 1840, 1840, 1872, 1872, 1968, 1968, 8209, 8209, 524, 524, 524, 524, 524, 524, 524, 524, 556, 556, 556, 556, 556, 556, 556, 556, 1552, 1552, 1584, 1584, 2000, 2000, 2032, 2032, 976, 976, 1008, 1008, 1040, 1040, 1072, 1072, 1296, 1296, 1328, 1328, 718, 718, 718, 718, 456, 456, 456, 456, 456, 456, 456, 456, 456, 456, 456, 456, 456, 456, 456, 456, 456, 456, 456, 456, 456, 456, 456, 456, 456, 456, 456, 456, 456, 456, 456, 456, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 490, 490, 490, 490, 490, 490, 490, 490, 490, 490, 490, 490, 490, 490, 490, 490, 4113, 4113, 6161, 6161, 848, 848, 880, 880, 912, 912, 944, 944, 622, 622, 622, 622, 654, 654, 654, 654, 1104, 1104, 1136, 1136, 1168, 1168, 1200, 1200, 1232, 1232, 1264, 1264, 686, 686, 686, 686, 1360, 1360, 1392, 1392, 12, 12, 12, 12, 12, 12, 12, 12, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390 };
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  577 */   static byte[] twoDCodes = new byte[] { 80, 88, 23, 71, 30, 30, 62, 62, 4, 4, 4, 4, 4, 4, 4, 4, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 51, 51, 51, 51, 51, 51, 51, 51, 51, 51, 51, 51, 51, 51, 51, 51, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41 };
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void beginDecoding() {
/*  619 */     super.beginDecoding();
/*      */     
/*  621 */     if (this.metadata instanceof TIFFImageMetadata) {
/*  622 */       TIFFImageMetadata tmetadata = (TIFFImageMetadata)this.metadata;
/*      */ 
/*      */       
/*  625 */       TIFFField f = tmetadata.getTIFFField(266);
/*  626 */       this.fillOrder = (f == null) ? 1 : f.getAsInt(0);
/*      */       
/*  628 */       f = tmetadata.getTIFFField(259);
/*  629 */       this
/*  630 */         .compression = (f == null) ? 2 : f.getAsInt(0);
/*      */       
/*  632 */       f = tmetadata.getTIFFField(292);
/*  633 */       this.t4Options = (f == null) ? 0 : f.getAsInt(0);
/*  634 */       this.oneD = this.t4Options & 0x1;
/*      */       
/*  636 */       this.uncompressedMode = (this.t4Options & 0x2) >> 1;
/*  637 */       this.fillBits = (this.t4Options & 0x4) >> 2;
/*  638 */       f = tmetadata.getTIFFField(293);
/*  639 */       this.t6Options = (f == null) ? 0 : f.getAsInt(0);
/*      */     } else {
/*  641 */       this.fillOrder = 1;
/*      */       
/*  643 */       this.compression = 2;
/*      */       
/*  645 */       this.t4Options = 0;
/*  646 */       this.oneD = 0;
/*  647 */       this.uncompressedMode = 0;
/*  648 */       this.fillBits = 0;
/*  649 */       this.t6Options = 0;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void decodeRaw(byte[] b, int dstOffset, int pixelBitStride, int scanlineStride) throws IOException {
/*  657 */     this.buffer = b;
/*      */     
/*  659 */     this.w = this.srcWidth;
/*  660 */     this.h = this.srcHeight;
/*  661 */     this.bitsPerScanline = scanlineStride * 8;
/*  662 */     this.lineBitNum = 8 * dstOffset;
/*      */     
/*  664 */     this.data = new byte[this.byteCount];
/*  665 */     this.bitPointer = 0;
/*  666 */     this.bytePointer = 0;
/*  667 */     this.prevChangingElems = new int[this.w + 1];
/*  668 */     this.currChangingElems = new int[this.w + 1];
/*      */     
/*  670 */     this.stream.seek(this.offset);
/*  671 */     this.stream.readFully(this.data);
/*      */     
/*      */     try {
/*  674 */       if (this.compression == 2) {
/*  675 */         decodeRLE();
/*  676 */       } else if (this.compression == 3) {
/*  677 */         decodeT4();
/*  678 */       } else if (this.compression == 4) {
/*  679 */         this.uncompressedMode = (this.t6Options & 0x2) >> 1;
/*  680 */         decodeT6();
/*      */       } else {
/*  682 */         throw new IIOException("Unknown compression type " + this.compression);
/*      */       } 
/*  684 */     } catch (ArrayIndexOutOfBoundsException e) {
/*  685 */       ByteArrayOutputStream baos = new ByteArrayOutputStream();
/*  686 */       e.printStackTrace(new PrintStream(baos));
/*  687 */       String s = new String(baos.toByteArray());
/*  688 */       warning("Ignoring exception:\n " + s);
/*      */     } 
/*      */   }
/*      */   
/*      */   public void decodeRLE() throws IIOException {
/*  693 */     for (int i = 0; i < this.h; i++) {
/*      */       
/*  695 */       decodeNextScanline(this.srcMinY + i);
/*      */ 
/*      */       
/*  698 */       if (this.bitPointer != 0) {
/*  699 */         this.bytePointer++;
/*  700 */         this.bitPointer = 0;
/*      */       } 
/*      */ 
/*      */       
/*  704 */       this.lineBitNum += this.bitsPerScanline;
/*      */     } 
/*      */   }
/*      */   
/*      */   public void decodeNextScanline(int lineIndex) throws IIOException {
/*  709 */     int bits = 0, code = 0, isT = 0;
/*      */     
/*  711 */     boolean isWhite = true;
/*  712 */     int dstEnd = 0;
/*      */     
/*  714 */     int bitOffset = 0;
/*      */ 
/*      */     
/*  717 */     this.changingElemSize = 0;
/*      */ 
/*      */     
/*  720 */     while (bitOffset < this.w) {
/*      */ 
/*      */       
/*  723 */       int runOffset = bitOffset;
/*      */       
/*  725 */       while (isWhite && bitOffset < this.w) {
/*      */         
/*  727 */         int current = nextNBits(10);
/*  728 */         int entry = white[current];
/*      */ 
/*      */         
/*  731 */         isT = entry & 0x1;
/*  732 */         bits = entry >>> 1 & 0xF;
/*      */         
/*  734 */         if (bits == 12) {
/*      */           
/*  736 */           int twoBits = nextLesserThan8Bits(2);
/*      */           
/*  738 */           current = current << 2 & 0xC | twoBits;
/*  739 */           entry = additionalMakeup[current];
/*  740 */           bits = entry >>> 1 & 0x7;
/*  741 */           code = entry >>> 4 & 0xFFF;
/*  742 */           bitOffset += code;
/*      */           
/*  744 */           updatePointer(4 - bits); continue;
/*  745 */         }  if (bits == 0) {
/*  746 */           warning("Error 0"); continue;
/*      */         } 
/*  748 */         if (bits == 15) {
/*      */ 
/*      */ 
/*      */ 
/*      */           
/*  753 */           warning("Premature EOL in white run of line " + lineIndex + ": read " + bitOffset + " of " + this.w + " expected pixels.");
/*      */           
/*      */           return;
/*      */         } 
/*      */         
/*  758 */         code = entry >>> 5 & 0x7FF;
/*  759 */         bitOffset += code;
/*      */         
/*  761 */         updatePointer(10 - bits);
/*  762 */         if (isT == 0) {
/*  763 */           isWhite = false;
/*  764 */           this.currChangingElems[this.changingElemSize++] = bitOffset;
/*      */         } 
/*      */       } 
/*      */ 
/*      */ 
/*      */       
/*  770 */       if (bitOffset == this.w) {
/*      */ 
/*      */ 
/*      */         
/*  774 */         int runLength = bitOffset - runOffset;
/*  775 */         if (isWhite && runLength != 0 && runLength % 64 == 0 && 
/*      */           
/*  777 */           nextNBits(8) != 53) {
/*  778 */           warning("Missing zero white run length terminating code!");
/*  779 */           updatePointer(8);
/*      */         } 
/*      */         
/*      */         break;
/*      */       } 
/*      */       
/*  785 */       runOffset = bitOffset;
/*      */       
/*  787 */       while (!isWhite && bitOffset < this.w) {
/*      */         
/*  789 */         int current = nextLesserThan8Bits(4);
/*  790 */         int entry = initBlack[current];
/*      */ 
/*      */         
/*  793 */         isT = entry & 0x1;
/*  794 */         bits = entry >>> 1 & 0xF;
/*  795 */         code = entry >>> 5 & 0x7FF;
/*      */         
/*  797 */         if (code == 100) {
/*  798 */           current = nextNBits(9);
/*  799 */           entry = black[current];
/*      */ 
/*      */           
/*  802 */           isT = entry & 0x1;
/*  803 */           bits = entry >>> 1 & 0xF;
/*  804 */           code = entry >>> 5 & 0x7FF;
/*      */           
/*  806 */           if (bits == 12) {
/*      */             
/*  808 */             updatePointer(5);
/*  809 */             current = nextLesserThan8Bits(4);
/*  810 */             entry = additionalMakeup[current];
/*  811 */             bits = entry >>> 1 & 0x7;
/*  812 */             code = entry >>> 4 & 0xFFF;
/*      */             
/*  814 */             setToBlack(bitOffset, code);
/*  815 */             bitOffset += code;
/*      */             
/*  817 */             updatePointer(4 - bits); continue;
/*  818 */           }  if (bits == 15) {
/*      */ 
/*      */ 
/*      */ 
/*      */             
/*  823 */             warning("Premature EOL in black run of line " + lineIndex + ": read " + bitOffset + " of " + this.w + " expected pixels.");
/*      */             
/*      */             return;
/*      */           } 
/*      */           
/*  828 */           setToBlack(bitOffset, code);
/*  829 */           bitOffset += code;
/*      */           
/*  831 */           updatePointer(9 - bits);
/*  832 */           if (isT == 0) {
/*  833 */             isWhite = true;
/*  834 */             this.currChangingElems[this.changingElemSize++] = bitOffset;
/*      */           }  continue;
/*      */         } 
/*  837 */         if (code == 200) {
/*      */           
/*  839 */           current = nextLesserThan8Bits(2);
/*  840 */           entry = twoBitBlack[current];
/*  841 */           code = entry >>> 5 & 0x7FF;
/*  842 */           bits = entry >>> 1 & 0xF;
/*      */           
/*  844 */           setToBlack(bitOffset, code);
/*  845 */           bitOffset += code;
/*      */           
/*  847 */           updatePointer(2 - bits);
/*  848 */           isWhite = true;
/*  849 */           this.currChangingElems[this.changingElemSize++] = bitOffset;
/*      */           continue;
/*      */         } 
/*  852 */         setToBlack(bitOffset, code);
/*  853 */         bitOffset += code;
/*      */         
/*  855 */         updatePointer(4 - bits);
/*  856 */         isWhite = true;
/*  857 */         this.currChangingElems[this.changingElemSize++] = bitOffset;
/*      */       } 
/*      */ 
/*      */ 
/*      */       
/*  862 */       if (bitOffset == this.w) {
/*      */ 
/*      */ 
/*      */         
/*  866 */         int runLength = bitOffset - runOffset;
/*  867 */         if (!isWhite && runLength != 0 && runLength % 64 == 0 && 
/*      */           
/*  869 */           nextNBits(10) != 55) {
/*  870 */           warning("Missing zero black run length terminating code!");
/*  871 */           updatePointer(10);
/*      */         } 
/*      */         
/*      */         break;
/*      */       } 
/*      */     } 
/*  877 */     this.currChangingElems[this.changingElemSize++] = bitOffset;
/*      */   }
/*      */   
/*      */   public void decodeT4() throws IIOException {
/*  881 */     int height = this.h;
/*      */ 
/*      */     
/*  884 */     int[] b = new int[2];
/*      */ 
/*      */     
/*  887 */     int currIndex = 0;
/*      */ 
/*      */     
/*  890 */     if (this.data.length < 2) {
/*  891 */       throw new IIOException("Insufficient data to read initial EOL.");
/*      */     }
/*      */ 
/*      */     
/*  895 */     int next12 = nextNBits(12);
/*  896 */     if (next12 != 1) {
/*  897 */       warning("T.4 compressed data should begin with EOL.");
/*      */     }
/*  899 */     updatePointer(12);
/*      */ 
/*      */     
/*  902 */     int modeFlag = 0;
/*  903 */     int lines = -1;
/*  904 */     while (modeFlag != 1) {
/*      */       try {
/*  906 */         modeFlag = findNextLine();
/*  907 */         lines++;
/*  908 */       } catch (EOFException eofe) {
/*  909 */         throw new IIOException("No reference line present.");
/*      */       } 
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  917 */     decodeNextScanline(this.srcMinY);
/*  918 */     lines++;
/*  919 */     this.lineBitNum += this.bitsPerScanline;
/*      */     
/*  921 */     while (lines < height) {
/*      */ 
/*      */       
/*      */       try {
/*      */         
/*  926 */         modeFlag = findNextLine();
/*  927 */       } catch (EOFException eofe) {
/*  928 */         warning("Input exhausted before EOL found at line " + (this.srcMinY + lines) + ": read 0 of " + this.w + " expected pixels.");
/*      */         
/*      */         break;
/*      */       } 
/*  932 */       if (modeFlag == 0) {
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  937 */         int[] temp = this.prevChangingElems;
/*  938 */         this.prevChangingElems = this.currChangingElems;
/*  939 */         this.currChangingElems = temp;
/*  940 */         currIndex = 0;
/*      */ 
/*      */         
/*  943 */         int a0 = -1;
/*  944 */         boolean isWhite = true;
/*  945 */         int bitOffset = 0;
/*      */         
/*  947 */         this.lastChangingElement = 0;
/*      */         
/*  949 */         while (bitOffset < this.w) {
/*      */           
/*  951 */           getNextChangingElement(a0, isWhite, b);
/*      */           
/*  953 */           int b1 = b[0];
/*  954 */           int b2 = b[1];
/*      */ 
/*      */           
/*  957 */           int entry = nextLesserThan8Bits(7);
/*      */ 
/*      */           
/*  960 */           entry = twoDCodes[entry] & 0xFF;
/*      */ 
/*      */           
/*  963 */           int code = (entry & 0x78) >>> 3;
/*  964 */           int bits = entry & 0x7;
/*      */           
/*  966 */           if (code == 0) {
/*  967 */             if (!isWhite) {
/*  968 */               setToBlack(bitOffset, b2 - bitOffset);
/*      */             }
/*  970 */             bitOffset = a0 = b2;
/*      */ 
/*      */             
/*  973 */             updatePointer(7 - bits); continue;
/*  974 */           }  if (code == 1) {
/*      */             
/*  976 */             updatePointer(7 - bits);
/*      */ 
/*      */ 
/*      */             
/*  980 */             if (isWhite) {
/*  981 */               int number = decodeWhiteCodeWord();
/*  982 */               bitOffset += number;
/*  983 */               this.currChangingElems[currIndex++] = bitOffset;
/*      */               
/*  985 */               number = decodeBlackCodeWord();
/*  986 */               setToBlack(bitOffset, number);
/*  987 */               bitOffset += number;
/*  988 */               this.currChangingElems[currIndex++] = bitOffset;
/*      */             } else {
/*  990 */               int number = decodeBlackCodeWord();
/*  991 */               setToBlack(bitOffset, number);
/*  992 */               bitOffset += number;
/*  993 */               this.currChangingElems[currIndex++] = bitOffset;
/*      */               
/*  995 */               number = decodeWhiteCodeWord();
/*  996 */               bitOffset += number;
/*  997 */               this.currChangingElems[currIndex++] = bitOffset;
/*      */             } 
/*      */             
/* 1000 */             a0 = bitOffset; continue;
/* 1001 */           }  if (code <= 8) {
/*      */             
/* 1003 */             int a1 = b1 + code - 5;
/*      */             
/* 1005 */             this.currChangingElems[currIndex++] = a1;
/*      */ 
/*      */ 
/*      */             
/* 1009 */             if (!isWhite) {
/* 1010 */               setToBlack(bitOffset, a1 - bitOffset);
/*      */             }
/* 1012 */             bitOffset = a0 = a1;
/* 1013 */             isWhite = !isWhite;
/*      */             
/* 1015 */             updatePointer(7 - bits); continue;
/*      */           } 
/* 1017 */           warning("Unknown coding mode encountered at line " + (this.srcMinY + lines) + ": read " + bitOffset + " of " + this.w + " expected pixels.");
/*      */ 
/*      */ 
/*      */ 
/*      */           
/* 1022 */           int numLinesTested = 0;
/* 1023 */           while (modeFlag != 1) {
/*      */             try {
/* 1025 */               modeFlag = findNextLine();
/* 1026 */               numLinesTested++;
/* 1027 */             } catch (EOFException eofe) {
/* 1028 */               warning("Sync loss at line " + (this.srcMinY + lines) + ": read " + lines + " of " + height + " lines.");
/*      */               
/*      */               return;
/*      */             } 
/*      */           } 
/*      */           
/* 1034 */           lines += numLinesTested - 1;
/* 1035 */           updatePointer(13);
/*      */         } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 1042 */         this.currChangingElems[currIndex++] = bitOffset;
/* 1043 */         this.changingElemSize = currIndex;
/*      */       } else {
/*      */         
/* 1046 */         decodeNextScanline(this.srcMinY + lines);
/*      */       } 
/*      */       
/* 1049 */       this.lineBitNum += this.bitsPerScanline;
/* 1050 */       lines++;
/*      */     } 
/*      */   }
/*      */   
/*      */   public synchronized void decodeT6() throws IIOException {
/* 1055 */     int height = this.h;
/*      */     
/* 1057 */     int bufferOffset = 0;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1067 */     int[] b = new int[2];
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1074 */     int[] cce = this.currChangingElems;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1079 */     this.changingElemSize = 0;
/* 1080 */     cce[this.changingElemSize++] = this.w;
/* 1081 */     cce[this.changingElemSize++] = this.w;
/*      */ 
/*      */ 
/*      */     
/* 1085 */     for (int lines = 0; lines < height; lines++) {
/*      */       
/* 1087 */       int a0 = -1;
/* 1088 */       boolean isWhite = true;
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 1093 */       int[] temp = this.prevChangingElems;
/* 1094 */       this.prevChangingElems = this.currChangingElems;
/* 1095 */       cce = this.currChangingElems = temp;
/* 1096 */       int currIndex = 0;
/*      */ 
/*      */       
/* 1099 */       int bitOffset = 0;
/*      */ 
/*      */       
/* 1102 */       this.lastChangingElement = 0;
/*      */ 
/*      */       
/* 1105 */       while (bitOffset < this.w) {
/*      */         
/* 1107 */         getNextChangingElement(a0, isWhite, b);
/* 1108 */         int b1 = b[0];
/* 1109 */         int b2 = b[1];
/*      */ 
/*      */         
/* 1112 */         int entry = nextLesserThan8Bits(7);
/*      */         
/* 1114 */         entry = twoDCodes[entry] & 0xFF;
/*      */ 
/*      */         
/* 1117 */         int code = (entry & 0x78) >>> 3;
/* 1118 */         int bits = entry & 0x7;
/*      */         
/* 1120 */         if (code == 0) {
/*      */           
/* 1122 */           if (!isWhite) {
/* 1123 */             if (b2 > this.w) {
/* 1124 */               b2 = this.w;
/* 1125 */               warning("Decoded row " + (this.srcMinY + lines) + " too long; ignoring extra samples.");
/*      */             } 
/*      */             
/* 1128 */             setToBlack(bitOffset, b2 - bitOffset);
/*      */           } 
/* 1130 */           bitOffset = a0 = b2;
/*      */ 
/*      */           
/* 1133 */           updatePointer(7 - bits); continue;
/* 1134 */         }  if (code == 1) {
/*      */           
/* 1136 */           updatePointer(7 - bits);
/*      */ 
/*      */ 
/*      */           
/* 1140 */           if (isWhite) {
/*      */             
/* 1142 */             int number = decodeWhiteCodeWord();
/* 1143 */             bitOffset += number;
/* 1144 */             cce[currIndex++] = bitOffset;
/*      */             
/* 1146 */             number = decodeBlackCodeWord();
/* 1147 */             if (number > this.w - bitOffset) {
/* 1148 */               number = this.w - bitOffset;
/* 1149 */               warning("Decoded row " + (this.srcMinY + lines) + " too long; ignoring extra samples.");
/*      */             } 
/*      */             
/* 1152 */             setToBlack(bitOffset, number);
/* 1153 */             bitOffset += number;
/* 1154 */             cce[currIndex++] = bitOffset;
/*      */           } else {
/*      */             
/* 1157 */             int number = decodeBlackCodeWord();
/* 1158 */             if (number > this.w - bitOffset) {
/* 1159 */               number = this.w - bitOffset;
/* 1160 */               warning("Decoded row " + (this.srcMinY + lines) + " too long; ignoring extra samples.");
/*      */             } 
/*      */             
/* 1163 */             setToBlack(bitOffset, number);
/* 1164 */             bitOffset += number;
/* 1165 */             cce[currIndex++] = bitOffset;
/*      */             
/* 1167 */             number = decodeWhiteCodeWord();
/* 1168 */             bitOffset += number;
/* 1169 */             cce[currIndex++] = bitOffset;
/*      */           } 
/*      */           
/* 1172 */           a0 = bitOffset; continue;
/* 1173 */         }  if (code <= 8) {
/* 1174 */           int a1 = b1 + code - 5;
/* 1175 */           cce[currIndex++] = a1;
/*      */ 
/*      */ 
/*      */           
/* 1179 */           if (!isWhite) {
/* 1180 */             if (a1 > this.w) {
/* 1181 */               a1 = this.w;
/* 1182 */               warning("Decoded row " + (this.srcMinY + lines) + " too long; ignoring extra samples.");
/*      */             } 
/*      */             
/* 1185 */             setToBlack(bitOffset, a1 - bitOffset);
/*      */           } 
/* 1187 */           bitOffset = a0 = a1;
/* 1188 */           isWhite = !isWhite;
/*      */           
/* 1190 */           updatePointer(7 - bits); continue;
/* 1191 */         }  if (code == 11) {
/* 1192 */           int entranceCode = nextLesserThan8Bits(3);
/* 1193 */           if (entranceCode != 7) {
/* 1194 */             String str = "Unsupported entrance code " + entranceCode + " for extension mode at line " + (this.srcMinY + lines) + ".";
/*      */ 
/*      */             
/* 1197 */             warning(str);
/*      */           } 
/*      */           
/* 1200 */           int zeros = 0;
/* 1201 */           boolean exit = false;
/*      */           
/* 1203 */           while (!exit) {
/* 1204 */             while (nextLesserThan8Bits(1) != 1) {
/* 1205 */               zeros++;
/*      */             }
/*      */             
/* 1208 */             if (zeros > 5) {
/*      */ 
/*      */ 
/*      */               
/* 1212 */               zeros -= 6;
/*      */               
/* 1214 */               if (!isWhite && zeros > 0) {
/* 1215 */                 cce[currIndex++] = bitOffset;
/*      */               }
/*      */ 
/*      */               
/* 1219 */               bitOffset += zeros;
/* 1220 */               if (zeros > 0)
/*      */               {
/* 1222 */                 isWhite = true;
/*      */               }
/*      */ 
/*      */ 
/*      */               
/* 1227 */               if (nextLesserThan8Bits(1) == 0) {
/* 1228 */                 if (!isWhite) {
/* 1229 */                   cce[currIndex++] = bitOffset;
/*      */                 }
/* 1231 */                 isWhite = true;
/*      */               } else {
/* 1233 */                 if (isWhite) {
/* 1234 */                   cce[currIndex++] = bitOffset;
/*      */                 }
/* 1236 */                 isWhite = false;
/*      */               } 
/*      */               
/* 1239 */               exit = true;
/*      */             } 
/*      */             
/* 1242 */             if (zeros == 5) {
/* 1243 */               if (!isWhite) {
/* 1244 */                 cce[currIndex++] = bitOffset;
/*      */               }
/* 1246 */               bitOffset += zeros;
/*      */ 
/*      */               
/* 1249 */               isWhite = true; continue;
/*      */             } 
/* 1251 */             bitOffset += zeros;
/*      */             
/* 1253 */             cce[currIndex++] = bitOffset;
/* 1254 */             setToBlack(bitOffset, 1);
/* 1255 */             bitOffset++;
/*      */ 
/*      */             
/* 1258 */             isWhite = false;
/*      */           } 
/*      */           
/*      */           continue;
/*      */         } 
/* 1263 */         String msg = "Unknown coding mode encountered at line " + (this.srcMinY + lines) + ".";
/*      */ 
/*      */         
/* 1266 */         warning(msg);
/*      */       } 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 1272 */       if (currIndex <= this.w) {
/* 1273 */         cce[currIndex++] = bitOffset;
/*      */       }
/*      */       
/* 1276 */       this.changingElemSize = currIndex;
/*      */       
/* 1278 */       this.lineBitNum += this.bitsPerScanline;
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   private void setToBlack(int bitNum, int numBits) {
/* 1284 */     bitNum += this.lineBitNum;
/*      */     
/* 1286 */     int lastBit = bitNum + numBits;
/* 1287 */     int byteNum = bitNum >> 3;
/*      */ 
/*      */     
/* 1290 */     int shift = bitNum & 0x7;
/* 1291 */     if (shift > 0) {
/* 1292 */       int maskVal = 1 << 7 - shift;
/* 1293 */       byte val = this.buffer[byteNum];
/* 1294 */       while (maskVal > 0 && bitNum < lastBit) {
/* 1295 */         val = (byte)(val | maskVal);
/* 1296 */         maskVal >>= 1;
/* 1297 */         bitNum++;
/*      */       } 
/* 1299 */       this.buffer[byteNum] = val;
/*      */     } 
/*      */ 
/*      */     
/* 1303 */     byteNum = bitNum >> 3;
/* 1304 */     while (bitNum < lastBit - 7) {
/* 1305 */       this.buffer[byteNum++] = -1;
/* 1306 */       bitNum += 8;
/*      */     } 
/*      */ 
/*      */     
/* 1310 */     while (bitNum < lastBit) {
/* 1311 */       byteNum = bitNum >> 3;
/* 1312 */       this.buffer[byteNum] = (byte)(this.buffer[byteNum] | 1 << 7 - (bitNum & 0x7));
/* 1313 */       bitNum++;
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   private int decodeWhiteCodeWord() throws IIOException {
/* 1319 */     int code = -1;
/* 1320 */     int runLength = 0;
/* 1321 */     boolean isWhite = true;
/*      */     
/* 1323 */     while (isWhite) {
/* 1324 */       int current = nextNBits(10);
/* 1325 */       int entry = white[current];
/*      */ 
/*      */       
/* 1328 */       int isT = entry & 0x1;
/* 1329 */       int bits = entry >>> 1 & 0xF;
/*      */       
/* 1331 */       if (bits == 12) {
/*      */         
/* 1333 */         int twoBits = nextLesserThan8Bits(2);
/*      */         
/* 1335 */         current = current << 2 & 0xC | twoBits;
/* 1336 */         entry = additionalMakeup[current];
/* 1337 */         bits = entry >>> 1 & 0x7;
/* 1338 */         code = entry >>> 4 & 0xFFF;
/* 1339 */         runLength += code;
/* 1340 */         updatePointer(4 - bits); continue;
/* 1341 */       }  if (bits == 0)
/* 1342 */         throw new IIOException("Error 0"); 
/* 1343 */       if (bits == 15) {
/* 1344 */         throw new IIOException("Error 1");
/*      */       }
/*      */       
/* 1347 */       code = entry >>> 5 & 0x7FF;
/* 1348 */       runLength += code;
/* 1349 */       updatePointer(10 - bits);
/* 1350 */       if (isT == 0) {
/* 1351 */         isWhite = false;
/*      */       }
/*      */     } 
/*      */ 
/*      */     
/* 1356 */     return runLength;
/*      */   }
/*      */ 
/*      */   
/*      */   private int decodeBlackCodeWord() throws IIOException {
/* 1361 */     int code = -1;
/* 1362 */     int runLength = 0;
/* 1363 */     boolean isWhite = false;
/*      */     
/* 1365 */     while (!isWhite) {
/* 1366 */       int current = nextLesserThan8Bits(4);
/* 1367 */       int entry = initBlack[current];
/*      */ 
/*      */       
/* 1370 */       int isT = entry & 0x1;
/* 1371 */       int bits = entry >>> 1 & 0xF;
/* 1372 */       code = entry >>> 5 & 0x7FF;
/*      */       
/* 1374 */       if (code == 100) {
/* 1375 */         current = nextNBits(9);
/* 1376 */         entry = black[current];
/*      */ 
/*      */         
/* 1379 */         isT = entry & 0x1;
/* 1380 */         bits = entry >>> 1 & 0xF;
/* 1381 */         code = entry >>> 5 & 0x7FF;
/*      */         
/* 1383 */         if (bits == 12) {
/*      */           
/* 1385 */           updatePointer(5);
/* 1386 */           current = nextLesserThan8Bits(4);
/* 1387 */           entry = additionalMakeup[current];
/* 1388 */           bits = entry >>> 1 & 0x7;
/* 1389 */           code = entry >>> 4 & 0xFFF;
/* 1390 */           runLength += code;
/*      */           
/* 1392 */           updatePointer(4 - bits); continue;
/* 1393 */         }  if (bits == 15)
/*      */         {
/* 1395 */           throw new IIOException("Error 2");
/*      */         }
/* 1397 */         runLength += code;
/* 1398 */         updatePointer(9 - bits);
/* 1399 */         if (isT == 0)
/* 1400 */           isWhite = true; 
/*      */         continue;
/*      */       } 
/* 1403 */       if (code == 200) {
/*      */         
/* 1405 */         current = nextLesserThan8Bits(2);
/* 1406 */         entry = twoBitBlack[current];
/* 1407 */         code = entry >>> 5 & 0x7FF;
/* 1408 */         runLength += code;
/* 1409 */         bits = entry >>> 1 & 0xF;
/* 1410 */         updatePointer(2 - bits);
/* 1411 */         isWhite = true;
/*      */         continue;
/*      */       } 
/* 1414 */       runLength += code;
/* 1415 */       updatePointer(4 - bits);
/* 1416 */       isWhite = true;
/*      */     } 
/*      */ 
/*      */     
/* 1420 */     return runLength;
/*      */   }
/*      */ 
/*      */   
/*      */   private int findNextLine() throws IIOException, EOFException {
/* 1425 */     int bitIndexMax = this.data.length * 8 - 1;
/* 1426 */     int bitIndexMax12 = bitIndexMax - 12;
/* 1427 */     int bitIndex = this.bytePointer * 8 + this.bitPointer;
/*      */ 
/*      */     
/* 1430 */     while (bitIndex <= bitIndexMax12) {
/*      */       
/* 1432 */       int next12Bits = nextNBits(12);
/* 1433 */       bitIndex += 12;
/*      */ 
/*      */ 
/*      */       
/* 1437 */       while (next12Bits != 1 && bitIndex < bitIndexMax) {
/*      */ 
/*      */         
/* 1440 */         next12Bits = (next12Bits & 0x7FF) << 1 | nextLesserThan8Bits(1) & 0x1;
/* 1441 */         bitIndex++;
/*      */       } 
/*      */       
/* 1444 */       if (next12Bits == 1) {
/* 1445 */         if (this.oneD == 1) {
/* 1446 */           if (bitIndex < bitIndexMax)
/*      */           {
/* 1448 */             return nextLesserThan8Bits(1); } 
/*      */           continue;
/*      */         } 
/* 1451 */         return 1;
/*      */       } 
/*      */     } 
/*      */ 
/*      */ 
/*      */     
/* 1457 */     throw new EOFException();
/*      */   }
/*      */ 
/*      */   
/*      */   private void getNextChangingElement(int a0, boolean isWhite, int[] ret) throws IIOException {
/* 1462 */     int[] pce = this.prevChangingElems;
/* 1463 */     int ces = this.changingElemSize;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1468 */     int start = (this.lastChangingElement > 0) ? (this.lastChangingElement - 1) : 0;
/* 1469 */     if (isWhite) {
/* 1470 */       start &= 0xFFFFFFFE;
/*      */     } else {
/* 1472 */       start |= 0x1;
/*      */     } 
/*      */     
/* 1475 */     int i = start;
/* 1476 */     for (; i < ces; i += 2) {
/* 1477 */       int temp = pce[i];
/* 1478 */       if (temp > a0) {
/* 1479 */         this.lastChangingElement = i;
/* 1480 */         ret[0] = temp;
/*      */         
/*      */         break;
/*      */       } 
/*      */     } 
/* 1485 */     if (i + 1 < ces) {
/* 1486 */       ret[1] = pce[i + 1];
/*      */     }
/*      */   }
/*      */   
/*      */   private int nextNBits(int bitsToGet) throws IIOException {
/*      */     byte b, next, next2next;
/* 1492 */     int l = this.data.length - 1;
/* 1493 */     int bp = this.bytePointer;
/*      */     
/* 1495 */     if (this.fillOrder == 1) {
/* 1496 */       b = this.data[bp];
/*      */       
/* 1498 */       if (bp == l) {
/* 1499 */         next = 0;
/* 1500 */         next2next = 0;
/* 1501 */       } else if (bp + 1 == l) {
/* 1502 */         next = this.data[bp + 1];
/* 1503 */         next2next = 0;
/*      */       } else {
/* 1505 */         next = this.data[bp + 1];
/* 1506 */         next2next = this.data[bp + 2];
/*      */       } 
/* 1508 */     } else if (this.fillOrder == 2) {
/* 1509 */       b = flipTable[this.data[bp] & 0xFF];
/*      */       
/* 1511 */       if (bp == l) {
/* 1512 */         next = 0;
/* 1513 */         next2next = 0;
/* 1514 */       } else if (bp + 1 == l) {
/* 1515 */         next = flipTable[this.data[bp + 1] & 0xFF];
/* 1516 */         next2next = 0;
/*      */       } else {
/* 1518 */         next = flipTable[this.data[bp + 1] & 0xFF];
/* 1519 */         next2next = flipTable[this.data[bp + 2] & 0xFF];
/*      */       } 
/*      */     } else {
/* 1522 */       throw new IIOException("Invalid FillOrder");
/*      */     } 
/*      */     
/* 1525 */     int bitsLeft = 8 - this.bitPointer;
/* 1526 */     int bitsFromNextByte = bitsToGet - bitsLeft;
/* 1527 */     int bitsFromNext2NextByte = 0;
/* 1528 */     if (bitsFromNextByte > 8) {
/* 1529 */       bitsFromNext2NextByte = bitsFromNextByte - 8;
/* 1530 */       bitsFromNextByte = 8;
/*      */     } 
/*      */     
/* 1533 */     this.bytePointer++;
/*      */     
/* 1535 */     int i1 = (b & table1[bitsLeft]) << bitsToGet - bitsLeft;
/* 1536 */     int i2 = (next & table2[bitsFromNextByte]) >>> 8 - bitsFromNextByte;
/*      */     
/* 1538 */     int i3 = 0;
/* 1539 */     if (bitsFromNext2NextByte != 0) {
/* 1540 */       i2 <<= bitsFromNext2NextByte;
/* 1541 */       i3 = (next2next & table2[bitsFromNext2NextByte]) >>> 8 - bitsFromNext2NextByte;
/*      */       
/* 1543 */       i2 |= i3;
/* 1544 */       this.bytePointer++;
/* 1545 */       this.bitPointer = bitsFromNext2NextByte;
/*      */     }
/* 1547 */     else if (bitsFromNextByte == 8) {
/* 1548 */       this.bitPointer = 0;
/* 1549 */       this.bytePointer++;
/*      */     } else {
/* 1551 */       this.bitPointer = bitsFromNextByte;
/*      */     } 
/*      */ 
/*      */     
/* 1555 */     int i = i1 | i2;
/* 1556 */     return i;
/*      */   }
/*      */   
/*      */   private int nextLesserThan8Bits(int bitsToGet) throws IIOException {
/*      */     byte b, next;
/* 1561 */     int i1, l = this.data.length - 1;
/* 1562 */     int bp = this.bytePointer;
/*      */     
/* 1564 */     if (this.fillOrder == 1) {
/* 1565 */       b = this.data[bp];
/* 1566 */       if (bp == l) {
/* 1567 */         next = 0;
/*      */       } else {
/* 1569 */         next = this.data[bp + 1];
/*      */       } 
/* 1571 */     } else if (this.fillOrder == 2) {
/* 1572 */       b = flipTable[this.data[bp] & 0xFF];
/* 1573 */       if (bp == l) {
/* 1574 */         next = 0;
/*      */       } else {
/* 1576 */         next = flipTable[this.data[bp + 1] & 0xFF];
/*      */       } 
/*      */     } else {
/* 1579 */       throw new IIOException("Invalid FillOrder");
/*      */     } 
/*      */     
/* 1582 */     int bitsLeft = 8 - this.bitPointer;
/* 1583 */     int bitsFromNextByte = bitsToGet - bitsLeft;
/*      */     
/* 1585 */     int shift = bitsLeft - bitsToGet;
/*      */     
/* 1587 */     if (shift >= 0) {
/* 1588 */       i1 = (b & table1[bitsLeft]) >>> shift;
/* 1589 */       this.bitPointer += bitsToGet;
/* 1590 */       if (this.bitPointer == 8) {
/* 1591 */         this.bitPointer = 0;
/* 1592 */         this.bytePointer++;
/*      */       } 
/*      */     } else {
/* 1595 */       i1 = (b & table1[bitsLeft]) << -shift;
/* 1596 */       int i2 = (next & table2[bitsFromNextByte]) >>> 8 - bitsFromNextByte;
/*      */       
/* 1598 */       i1 |= i2;
/* 1599 */       this.bytePointer++;
/* 1600 */       this.bitPointer = bitsFromNextByte;
/*      */     } 
/*      */     
/* 1603 */     return i1;
/*      */   }
/*      */ 
/*      */   
/*      */   private void updatePointer(int bitsToMoveBack) {
/* 1608 */     if (bitsToMoveBack > 8) {
/* 1609 */       this.bytePointer -= bitsToMoveBack / 8;
/* 1610 */       bitsToMoveBack %= 8;
/*      */     } 
/*      */     
/* 1613 */     int i = this.bitPointer - bitsToMoveBack;
/* 1614 */     if (i < 0) {
/* 1615 */       this.bytePointer--;
/* 1616 */       this.bitPointer = 8 + i;
/*      */     } else {
/* 1618 */       this.bitPointer = i;
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   private void warning(String msg) {
/* 1624 */     if (this.reader instanceof TIFFImageReader)
/* 1625 */       ((TIFFImageReader)this.reader).forwardWarningMessage(msg); 
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\github\jaiimageio\impl\plugins\tiff\TIFFFaxDecompressor.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */