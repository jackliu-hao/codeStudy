/*     */ package com.sun.jna.platform;
/*     */ 
/*     */ import java.awt.Rectangle;
/*     */ import java.awt.image.DataBuffer;
/*     */ import java.awt.image.DataBufferByte;
/*     */ import java.awt.image.DataBufferInt;
/*     */ import java.awt.image.MultiPixelPackedSampleModel;
/*     */ import java.awt.image.Raster;
/*     */ import java.awt.image.SampleModel;
/*     */ import java.util.Collections;
/*     */ import java.util.Comparator;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.Set;
/*     */ import java.util.TreeSet;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class RasterRangesUtils
/*     */ {
/*  53 */   private static final int[] subColMasks = new int[] { 128, 64, 32, 16, 8, 4, 2, 1 };
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  58 */   private static final Comparator<Object> COMPARATOR = new Comparator()
/*     */     {
/*     */       public int compare(Object o1, Object o2) {
/*  61 */         return ((Rectangle)o1).x - ((Rectangle)o2).x;
/*     */       }
/*     */     };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static interface RangesOutput
/*     */   {
/*     */     boolean outputRange(int param1Int1, int param1Int2, int param1Int3, int param1Int4);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean outputOccupiedRanges(Raster raster, RangesOutput out) {
/*  89 */     Rectangle bounds = raster.getBounds();
/*  90 */     SampleModel sampleModel = raster.getSampleModel();
/*  91 */     boolean hasAlpha = (sampleModel.getNumBands() == 4);
/*     */ 
/*     */     
/*  94 */     if (raster.getParent() == null && bounds.x == 0 && bounds.y == 0) {
/*     */ 
/*     */       
/*  97 */       DataBuffer data = raster.getDataBuffer();
/*  98 */       if (data.getNumBanks() == 1)
/*     */       {
/*     */         
/* 101 */         if (sampleModel instanceof MultiPixelPackedSampleModel) {
/* 102 */           MultiPixelPackedSampleModel packedSampleModel = (MultiPixelPackedSampleModel)sampleModel;
/* 103 */           if (packedSampleModel.getPixelBitStride() == 1)
/*     */           {
/* 105 */             return outputOccupiedRangesOfBinaryPixels(((DataBufferByte)data).getData(), bounds.width, bounds.height, out);
/*     */           }
/* 107 */         } else if (sampleModel instanceof java.awt.image.SinglePixelPackedSampleModel && 
/* 108 */           sampleModel.getDataType() == 3) {
/*     */           
/* 110 */           return outputOccupiedRanges(((DataBufferInt)data).getData(), bounds.width, bounds.height, hasAlpha ? -16777216 : 16777215, out);
/*     */         } 
/*     */       }
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 118 */     int[] pixels = raster.getPixels(0, 0, bounds.width, bounds.height, (int[])null);
/* 119 */     return outputOccupiedRanges(pixels, bounds.width, bounds.height, hasAlpha ? -16777216 : 16777215, out);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean outputOccupiedRangesOfBinaryPixels(byte[] binaryBits, int w, int h, RangesOutput out) {
/* 131 */     Set<Rectangle> rects = new HashSet<Rectangle>();
/* 132 */     Set<Rectangle> prevLine = Collections.emptySet();
/* 133 */     int scanlineBytes = binaryBits.length / h;
/* 134 */     for (int row = 0; row < h; row++) {
/* 135 */       Set<Rectangle> curLine = new TreeSet(COMPARATOR);
/* 136 */       int rowOffsetBytes = row * scanlineBytes;
/* 137 */       int startCol = -1;
/*     */       
/* 139 */       for (int byteCol = 0; byteCol < scanlineBytes; byteCol++) {
/* 140 */         int firstByteCol = byteCol << 3;
/* 141 */         byte byteColBits = binaryBits[rowOffsetBytes + byteCol];
/* 142 */         if (byteColBits == 0) {
/*     */           
/* 144 */           if (startCol >= 0) {
/*     */             
/* 146 */             curLine.add(new Rectangle(startCol, row, firstByteCol - startCol, 1));
/* 147 */             startCol = -1;
/*     */           } 
/* 149 */         } else if (byteColBits == 255) {
/*     */           
/* 151 */           if (startCol < 0)
/*     */           {
/* 153 */             startCol = firstByteCol;
/*     */           }
/*     */         } else {
/*     */           
/* 157 */           for (int subCol = 0; subCol < 8; subCol++) {
/* 158 */             int col = firstByteCol | subCol;
/* 159 */             if ((byteColBits & subColMasks[subCol]) != 0) {
/* 160 */               if (startCol < 0)
/*     */               {
/* 162 */                 startCol = col;
/*     */               }
/*     */             }
/* 165 */             else if (startCol >= 0) {
/*     */               
/* 167 */               curLine.add(new Rectangle(startCol, row, col - startCol, 1));
/* 168 */               startCol = -1;
/*     */             } 
/*     */           } 
/*     */         } 
/*     */       } 
/*     */       
/* 174 */       if (startCol >= 0)
/*     */       {
/* 176 */         curLine.add(new Rectangle(startCol, row, w - startCol, 1));
/*     */       }
/* 178 */       Set<Rectangle> unmerged = mergeRects(prevLine, curLine);
/* 179 */       rects.addAll(unmerged);
/* 180 */       prevLine = curLine;
/*     */     } 
/*     */     
/* 183 */     rects.addAll(prevLine);
/* 184 */     for (Iterator<Rectangle> i = rects.iterator(); i.hasNext(); ) {
/* 185 */       Rectangle r = i.next();
/* 186 */       if (!out.outputRange(r.x, r.y, r.width, r.height)) {
/* 187 */         return false;
/*     */       }
/*     */     } 
/* 190 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean outputOccupiedRanges(int[] pixels, int w, int h, int occupationMask, RangesOutput out) {
/* 204 */     Set<Rectangle> rects = new HashSet<Rectangle>();
/* 205 */     Set<Rectangle> prevLine = Collections.emptySet();
/* 206 */     for (int row = 0; row < h; row++) {
/* 207 */       Set<Rectangle> curLine = new TreeSet(COMPARATOR);
/* 208 */       int idxOffset = row * w;
/* 209 */       int startCol = -1;
/*     */       
/* 211 */       for (int col = 0; col < w; col++) {
/* 212 */         if ((pixels[idxOffset + col] & occupationMask) != 0) {
/* 213 */           if (startCol < 0) {
/* 214 */             startCol = col;
/*     */           }
/*     */         }
/* 217 */         else if (startCol >= 0) {
/*     */           
/* 219 */           curLine.add(new Rectangle(startCol, row, col - startCol, 1));
/* 220 */           startCol = -1;
/*     */         } 
/*     */       } 
/*     */       
/* 224 */       if (startCol >= 0)
/*     */       {
/* 226 */         curLine.add(new Rectangle(startCol, row, w - startCol, 1));
/*     */       }
/* 228 */       Set<Rectangle> unmerged = mergeRects(prevLine, curLine);
/* 229 */       rects.addAll(unmerged);
/* 230 */       prevLine = curLine;
/*     */     } 
/*     */     
/* 233 */     rects.addAll(prevLine);
/* 234 */     for (Iterator<Rectangle> i = rects.iterator(); i.hasNext(); ) {
/* 235 */       Rectangle r = i.next();
/* 236 */       if (!out.outputRange(r.x, r.y, r.width, r.height)) {
/* 237 */         return false;
/*     */       }
/*     */     } 
/* 240 */     return true;
/*     */   }
/*     */   
/*     */   private static Set<Rectangle> mergeRects(Set<Rectangle> prev, Set<Rectangle> current) {
/* 244 */     Set<Rectangle> unmerged = new HashSet<Rectangle>(prev);
/* 245 */     if (!prev.isEmpty() && !current.isEmpty()) {
/* 246 */       Rectangle[] pr = prev.<Rectangle>toArray(new Rectangle[0]);
/* 247 */       Rectangle[] cr = current.<Rectangle>toArray(new Rectangle[0]);
/* 248 */       int ipr = 0;
/* 249 */       int icr = 0;
/* 250 */       while (ipr < pr.length && icr < cr.length) {
/* 251 */         while ((cr[icr]).x < (pr[ipr]).x) {
/* 252 */           if (++icr == cr.length) {
/* 253 */             return unmerged;
/*     */           }
/*     */         } 
/* 256 */         if ((cr[icr]).x == (pr[ipr]).x && (cr[icr]).width == (pr[ipr]).width) {
/* 257 */           unmerged.remove(pr[ipr]);
/* 258 */           (cr[icr]).y = (pr[ipr]).y;
/* 259 */           (pr[ipr]).height++;
/* 260 */           icr++;
/*     */           continue;
/*     */         } 
/* 263 */         ipr++;
/*     */       } 
/*     */     } 
/*     */     
/* 267 */     return unmerged;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\jna\platform\RasterRangesUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */