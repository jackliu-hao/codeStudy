/*      */ package com.github.jaiimageio.impl.plugins.raw;
/*      */ 
/*      */ import com.github.jaiimageio.impl.common.ImageUtil;
/*      */ import com.github.jaiimageio.impl.common.SimpleRenderedImage;
/*      */ import com.github.jaiimageio.stream.RawImageInputStream;
/*      */ import java.awt.Dimension;
/*      */ import java.awt.Point;
/*      */ import java.awt.Rectangle;
/*      */ import java.awt.image.BufferedImage;
/*      */ import java.awt.image.ComponentSampleModel;
/*      */ import java.awt.image.DataBuffer;
/*      */ import java.awt.image.DataBufferByte;
/*      */ import java.awt.image.DataBufferDouble;
/*      */ import java.awt.image.DataBufferFloat;
/*      */ import java.awt.image.DataBufferInt;
/*      */ import java.awt.image.DataBufferShort;
/*      */ import java.awt.image.DataBufferUShort;
/*      */ import java.awt.image.MultiPixelPackedSampleModel;
/*      */ import java.awt.image.Raster;
/*      */ import java.awt.image.SampleModel;
/*      */ import java.awt.image.SinglePixelPackedSampleModel;
/*      */ import java.awt.image.WritableRaster;
/*      */ import java.io.IOException;
/*      */ import javax.imageio.ImageReadParam;
/*      */ import javax.imageio.ImageTypeSpecifier;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class RawRenderedImage
/*      */   extends SimpleRenderedImage
/*      */ {
/*      */   private SampleModel originalSampleModel;
/*      */   private Raster currentTile;
/*      */   private Point currentTileGrid;
/*   83 */   private RawImageInputStream iis = null;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private RawImageReader reader;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*   93 */   private ImageReadParam param = null;
/*      */   
/*      */   private int imageIndex;
/*      */   
/*      */   private Rectangle destinationRegion;
/*      */   private Rectangle originalRegion;
/*      */   private Point sourceOrigin;
/*      */   private Dimension originalDimension;
/*      */   private int maxXTile;
/*      */   private int maxYTile;
/*      */   private int scaleX;
/*      */   private int scaleY;
/*      */   private int xOffset;
/*      */   private int yOffset;
/*  107 */   private int[] destinationBands = null;
/*  108 */   private int[] sourceBands = null;
/*      */ 
/*      */   
/*      */   private int nComp;
/*      */ 
/*      */   
/*      */   private boolean noTransform = true;
/*      */ 
/*      */   
/*      */   private WritableRaster rasForATile;
/*      */ 
/*      */   
/*      */   private BufferedImage destImage;
/*      */ 
/*      */   
/*      */   private long position;
/*      */ 
/*      */   
/*      */   private long tileDataSize;
/*      */ 
/*      */   
/*      */   private int originalNumXTiles;
/*      */ 
/*      */ 
/*      */   
/*      */   public RawRenderedImage(RawImageInputStream iis, RawImageReader reader, ImageReadParam param, int imageIndex) throws IOException {
/*  134 */     this.iis = iis;
/*  135 */     this.reader = reader;
/*  136 */     this.param = param;
/*  137 */     this.imageIndex = imageIndex;
/*  138 */     this.position = iis.getImageOffset(imageIndex);
/*  139 */     this.originalDimension = iis.getImageDimension(imageIndex);
/*      */     
/*  141 */     ImageTypeSpecifier type = iis.getImageType();
/*  142 */     this.sampleModel = this.originalSampleModel = type.getSampleModel();
/*  143 */     this.colorModel = type.getColorModel();
/*      */ 
/*      */     
/*  146 */     this.sourceBands = (param == null) ? null : param.getSourceBands();
/*      */     
/*  148 */     if (this.sourceBands == null) {
/*  149 */       this.nComp = this.originalSampleModel.getNumBands();
/*  150 */       this.sourceBands = new int[this.nComp];
/*  151 */       for (int i = 0; i < this.nComp; i++)
/*  152 */         this.sourceBands[i] = i; 
/*      */     } else {
/*  154 */       this
/*  155 */         .sampleModel = this.originalSampleModel.createSubsetSampleModel(this.sourceBands);
/*  156 */       this.colorModel = ImageUtil.createColorModel(null, this.sampleModel);
/*      */     } 
/*      */     
/*  159 */     this.nComp = this.sourceBands.length;
/*      */     
/*  161 */     this.destinationBands = (param == null) ? null : param.getDestinationBands();
/*  162 */     if (this.destinationBands == null) {
/*  163 */       this.destinationBands = new int[this.nComp];
/*  164 */       for (int i = 0; i < this.nComp; i++) {
/*  165 */         this.destinationBands[i] = i;
/*      */       }
/*      */     } 
/*  168 */     Dimension dim = iis.getImageDimension(imageIndex);
/*  169 */     this.width = dim.width;
/*  170 */     this.height = dim.height;
/*      */     
/*  172 */     Rectangle sourceRegion = new Rectangle(0, 0, this.width, this.height);
/*      */ 
/*      */     
/*  175 */     this.originalRegion = (Rectangle)sourceRegion.clone();
/*      */     
/*  177 */     this.destinationRegion = (Rectangle)sourceRegion.clone();
/*      */     
/*  179 */     if (param != null) {
/*  180 */       RawImageReader.computeRegionsWrapper(param, this.width, this.height, param
/*      */           
/*  182 */           .getDestination(), sourceRegion, this.destinationRegion);
/*      */ 
/*      */       
/*  185 */       this.scaleX = param.getSourceXSubsampling();
/*  186 */       this.scaleY = param.getSourceYSubsampling();
/*  187 */       this.xOffset = param.getSubsamplingXOffset();
/*  188 */       this.yOffset = param.getSubsamplingYOffset();
/*      */     } 
/*      */     
/*  191 */     this.sourceOrigin = new Point(sourceRegion.x, sourceRegion.y);
/*  192 */     if (!this.destinationRegion.equals(sourceRegion)) {
/*  193 */       this.noTransform = false;
/*      */     }
/*  195 */     this.tileDataSize = ImageUtil.getTileSize(this.originalSampleModel);
/*      */     
/*  197 */     this.tileWidth = this.originalSampleModel.getWidth();
/*  198 */     this.tileHeight = this.originalSampleModel.getHeight();
/*  199 */     this.tileGridXOffset = this.destinationRegion.x;
/*  200 */     this.tileGridYOffset = this.destinationRegion.y;
/*  201 */     this.originalNumXTiles = getNumXTiles();
/*      */     
/*  203 */     this.width = this.destinationRegion.width;
/*  204 */     this.height = this.destinationRegion.height;
/*  205 */     this.minX = this.destinationRegion.x;
/*  206 */     this.minY = this.destinationRegion.y;
/*      */     
/*  208 */     this
/*  209 */       .sampleModel = this.sampleModel.createCompatibleSampleModel(this.tileWidth, this.tileHeight);
/*      */     
/*  211 */     this.maxXTile = this.originalDimension.width / this.tileWidth;
/*  212 */     this.maxYTile = this.originalDimension.height / this.tileHeight;
/*      */   }
/*      */   
/*      */   public synchronized Raster getTile(int tileX, int tileY) {
/*  216 */     if (this.currentTile != null && this.currentTileGrid.x == tileX && this.currentTileGrid.y == tileY)
/*      */     {
/*      */       
/*  219 */       return this.currentTile;
/*      */     }
/*  221 */     if (tileX >= getNumXTiles() || tileY >= getNumYTiles()) {
/*  222 */       throw new IllegalArgumentException(I18N.getString("RawRenderedImage0"));
/*      */     }
/*      */     try {
/*  225 */       this.iis.seek(this.position + (tileY * this.originalNumXTiles + tileX) * this.tileDataSize);
/*      */       
/*  227 */       int x = tileXToX(tileX);
/*  228 */       int y = tileYToY(tileY);
/*  229 */       this.currentTile = Raster.createWritableRaster(this.sampleModel, new Point(x, y));
/*      */       
/*  231 */       if (this.noTransform) {
/*  232 */         byte[][] buf; int i; short[][] sbuf; int j; short[][] usbuf; int k; int[][] ibuf; int m; float[][] fbuf; int n; double[][] dbuf; int i1; switch (this.sampleModel.getDataType()) {
/*      */           
/*      */           case 0:
/*  235 */             buf = ((DataBufferByte)this.currentTile.getDataBuffer()).getBankData();
/*  236 */             for (i = 0; i < buf.length; i++) {
/*  237 */               this.iis.readFully(buf[i], 0, (buf[i]).length);
/*      */             }
/*      */             break;
/*      */           
/*      */           case 2:
/*  242 */             sbuf = ((DataBufferShort)this.currentTile.getDataBuffer()).getBankData();
/*  243 */             for (j = 0; j < sbuf.length; j++) {
/*  244 */               this.iis.readFully(sbuf[j], 0, (sbuf[j]).length);
/*      */             }
/*      */             break;
/*      */           
/*      */           case 1:
/*  249 */             usbuf = ((DataBufferUShort)this.currentTile.getDataBuffer()).getBankData();
/*  250 */             for (k = 0; k < usbuf.length; k++) {
/*  251 */               this.iis.readFully(usbuf[k], 0, (usbuf[k]).length);
/*      */             }
/*      */             break;
/*      */           case 3:
/*  255 */             ibuf = ((DataBufferInt)this.currentTile.getDataBuffer()).getBankData();
/*  256 */             for (m = 0; m < ibuf.length; m++) {
/*  257 */               this.iis.readFully(ibuf[m], 0, (ibuf[m]).length);
/*      */             }
/*      */             break;
/*      */           case 4:
/*  261 */             fbuf = ((DataBufferFloat)this.currentTile.getDataBuffer()).getBankData();
/*  262 */             for (n = 0; n < fbuf.length; n++) {
/*  263 */               this.iis.readFully(fbuf[n], 0, (fbuf[n]).length);
/*      */             }
/*      */             break;
/*      */           case 5:
/*  267 */             dbuf = ((DataBufferDouble)this.currentTile.getDataBuffer()).getBankData();
/*  268 */             for (i1 = 0; i1 < dbuf.length; i1++)
/*  269 */               this.iis.readFully(dbuf[i1], 0, (dbuf[i1]).length); 
/*      */             break;
/*      */         } 
/*      */       } else {
/*  273 */         this.currentTile = readSubsampledRaster((WritableRaster)this.currentTile);
/*      */       } 
/*  275 */     } catch (IOException e) {
/*  276 */       throw new RuntimeException(e);
/*      */     } 
/*      */     
/*  279 */     if (this.currentTileGrid == null) {
/*  280 */       this.currentTileGrid = new Point(tileX, tileY);
/*      */     } else {
/*  282 */       this.currentTileGrid.x = tileX;
/*  283 */       this.currentTileGrid.y = tileY;
/*      */     } 
/*      */     
/*  286 */     return this.currentTile;
/*      */   }
/*      */   
/*      */   public void readAsRaster(WritableRaster raster) throws IOException {
/*  290 */     readSubsampledRaster(raster);
/*      */   }
/*      */   
/*      */   private Raster readSubsampledRaster(WritableRaster raster) throws IOException {
/*  294 */     if (raster == null) {
/*  295 */       raster = Raster.createWritableRaster(this.sampleModel
/*  296 */           .createCompatibleSampleModel(this.destinationRegion.x + this.destinationRegion.width, this.destinationRegion.y + this.destinationRegion.height), new Point(this.destinationRegion.x, this.destinationRegion.y));
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  302 */     int numBands = this.sourceBands.length;
/*  303 */     int dataType = this.sampleModel.getDataType();
/*  304 */     int sampleSizeBit = DataBuffer.getDataTypeSize(dataType);
/*  305 */     int sampleSizeByte = (sampleSizeBit + 7) / 8;
/*      */     
/*  307 */     Rectangle destRect = raster.getBounds().intersection(this.destinationRegion);
/*      */     
/*  309 */     int offx = this.destinationRegion.x;
/*  310 */     int offy = this.destinationRegion.y;
/*      */     
/*  312 */     int sourceSX = (destRect.x - offx) * this.scaleX + this.sourceOrigin.x;
/*  313 */     int sourceSY = (destRect.y - offy) * this.scaleY + this.sourceOrigin.y;
/*  314 */     int sourceEX = (destRect.width - 1) * this.scaleX + sourceSX;
/*  315 */     int sourceEY = (destRect.height - 1) * this.scaleY + sourceSY;
/*  316 */     int startXTile = sourceSX / this.tileWidth;
/*  317 */     int startYTile = sourceSY / this.tileHeight;
/*  318 */     int endXTile = sourceEX / this.tileWidth;
/*  319 */     int endYTile = sourceEY / this.tileHeight;
/*      */     
/*  321 */     startXTile = clip(startXTile, 0, this.maxXTile);
/*  322 */     startYTile = clip(startYTile, 0, this.maxYTile);
/*  323 */     endXTile = clip(endXTile, 0, this.maxXTile);
/*  324 */     endYTile = clip(endYTile, 0, this.maxYTile);
/*      */     
/*  326 */     int totalXTiles = getNumXTiles();
/*  327 */     int totalYTiles = getNumYTiles();
/*  328 */     int totalTiles = totalXTiles * totalYTiles;
/*      */ 
/*      */     
/*  331 */     byte[] pixbuf = null;
/*  332 */     short[] spixbuf = null;
/*  333 */     int[] ipixbuf = null;
/*  334 */     float[] fpixbuf = null;
/*  335 */     double[] dpixbuf = null;
/*      */ 
/*      */     
/*  338 */     boolean singleBank = true;
/*  339 */     int pixelStride = 0;
/*  340 */     int scanlineStride = 0;
/*  341 */     int bandStride = 0;
/*  342 */     int[] bandOffsets = null;
/*  343 */     int[] bankIndices = null;
/*      */     
/*  345 */     if (this.originalSampleModel instanceof ComponentSampleModel) {
/*  346 */       ComponentSampleModel csm = (ComponentSampleModel)this.originalSampleModel;
/*  347 */       bankIndices = csm.getBankIndices();
/*  348 */       int maxBank = 0; int i;
/*  349 */       for (i = 0; i < bankIndices.length; i++) {
/*  350 */         if (maxBank > bankIndices[i])
/*  351 */           maxBank = bankIndices[i]; 
/*      */       } 
/*  353 */       if (maxBank > 0)
/*  354 */         singleBank = false; 
/*  355 */       pixelStride = csm.getPixelStride();
/*      */       
/*  357 */       scanlineStride = csm.getScanlineStride();
/*  358 */       bandOffsets = csm.getBandOffsets();
/*  359 */       for (i = 0; i < bandOffsets.length; i++)
/*  360 */       { if (bandStride < bandOffsets[i])
/*  361 */           bandStride = bandOffsets[i];  } 
/*  362 */     } else if (this.originalSampleModel instanceof MultiPixelPackedSampleModel) {
/*      */       
/*  364 */       scanlineStride = ((MultiPixelPackedSampleModel)this.originalSampleModel).getScanlineStride();
/*  365 */     } else if (this.originalSampleModel instanceof SinglePixelPackedSampleModel) {
/*  366 */       pixelStride = 1;
/*      */       
/*  368 */       scanlineStride = ((SinglePixelPackedSampleModel)this.originalSampleModel).getScanlineStride();
/*      */     } 
/*      */ 
/*      */     
/*  372 */     byte[] destPixbuf = null;
/*  373 */     short[] destSPixbuf = null;
/*  374 */     int[] destIPixbuf = null;
/*  375 */     float[] destFPixbuf = null;
/*  376 */     double[] destDPixbuf = null;
/*  377 */     int[] destBandOffsets = null;
/*  378 */     int destPixelStride = 0;
/*  379 */     int destScanlineStride = 0;
/*  380 */     int destSX = 0;
/*      */     
/*  382 */     if (raster.getSampleModel() instanceof ComponentSampleModel) {
/*      */       
/*  384 */       ComponentSampleModel csm = (ComponentSampleModel)raster.getSampleModel();
/*  385 */       bankIndices = csm.getBankIndices();
/*  386 */       destBandOffsets = csm.getBandOffsets();
/*  387 */       destPixelStride = csm.getPixelStride();
/*  388 */       destScanlineStride = csm.getScanlineStride();
/*  389 */       destSX = csm.getOffset(raster.getMinX() - raster
/*  390 */           .getSampleModelTranslateX(), raster
/*  391 */           .getMinY() - raster
/*  392 */           .getSampleModelTranslateY()) - destBandOffsets[0];
/*      */ 
/*      */       
/*  395 */       switch (dataType) {
/*      */         case 0:
/*  397 */           destPixbuf = ((DataBufferByte)raster.getDataBuffer()).getData();
/*      */           break;
/*      */         
/*      */         case 2:
/*  401 */           destSPixbuf = ((DataBufferShort)raster.getDataBuffer()).getData();
/*      */           break;
/*      */ 
/*      */         
/*      */         case 1:
/*  406 */           destSPixbuf = ((DataBufferUShort)raster.getDataBuffer()).getData();
/*      */           break;
/*      */ 
/*      */         
/*      */         case 3:
/*  411 */           destIPixbuf = ((DataBufferInt)raster.getDataBuffer()).getData();
/*      */           break;
/*      */ 
/*      */         
/*      */         case 4:
/*  416 */           destFPixbuf = ((DataBufferFloat)raster.getDataBuffer()).getData();
/*      */           break;
/*      */ 
/*      */         
/*      */         case 5:
/*  421 */           destDPixbuf = ((DataBufferDouble)raster.getDataBuffer()).getData();
/*      */           break;
/*      */       } 
/*  424 */     } else if (raster.getSampleModel() instanceof SinglePixelPackedSampleModel) {
/*  425 */       numBands = 1;
/*  426 */       bankIndices = new int[] { 0 };
/*  427 */       destBandOffsets = new int[numBands];
/*  428 */       for (int i = 0; i < numBands; i++)
/*  429 */         destBandOffsets[i] = 0; 
/*  430 */       destPixelStride = 1;
/*      */       
/*  432 */       destScanlineStride = ((SinglePixelPackedSampleModel)raster.getSampleModel()).getScanlineStride();
/*      */     } 
/*      */ 
/*      */     
/*  436 */     for (int y = startYTile; y <= endYTile && 
/*  437 */       !this.reader.getAbortRequest(); y++) {
/*      */ 
/*      */ 
/*      */       
/*  441 */       for (int x = startXTile; x <= endXTile && 
/*  442 */         !this.reader.getAbortRequest(); x++) {
/*      */ 
/*      */         
/*  445 */         long tilePosition = this.position + (y * this.originalNumXTiles + x) * this.tileDataSize;
/*      */         
/*  447 */         this.iis.seek(tilePosition);
/*  448 */         float percentage = ((x - startXTile + y * totalXTiles) / totalXTiles);
/*      */ 
/*      */         
/*  451 */         int startX = x * this.tileWidth;
/*  452 */         int startY = y * this.tileHeight;
/*      */         
/*  454 */         int cTileHeight = this.tileHeight;
/*  455 */         int cTileWidth = this.tileWidth;
/*      */         
/*  457 */         if (startY + cTileHeight >= this.originalDimension.height) {
/*  458 */           cTileHeight = this.originalDimension.height - startY;
/*      */         }
/*  460 */         if (startX + cTileWidth >= this.originalDimension.width) {
/*  461 */           cTileWidth = this.originalDimension.width - startX;
/*      */         }
/*  463 */         int tx = startX;
/*  464 */         int ty = startY;
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  469 */         if (sourceSX > startX) {
/*  470 */           cTileWidth += startX - sourceSX;
/*  471 */           tx = sourceSX;
/*  472 */           startX = sourceSX;
/*      */         } 
/*      */         
/*  475 */         if (sourceSY > startY) {
/*  476 */           cTileHeight += startY - sourceSY;
/*  477 */           ty = sourceSY;
/*  478 */           startY = sourceSY;
/*      */         } 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  484 */         if (sourceEX < startX + cTileWidth - 1) {
/*  485 */           cTileWidth += sourceEX - startX - cTileWidth + 1;
/*      */         }
/*      */         
/*  488 */         if (sourceEY < startY + cTileHeight - 1) {
/*  489 */           cTileHeight += sourceEY - startY - cTileHeight + 1;
/*      */         }
/*      */ 
/*      */         
/*  493 */         int x1 = (startX + this.scaleX - 1 - this.sourceOrigin.x) / this.scaleX;
/*  494 */         int x2 = (startX + this.scaleX - 1 + cTileWidth - this.sourceOrigin.x) / this.scaleX;
/*      */         
/*  496 */         int lineLength = x2 - x1;
/*  497 */         x2 = (x2 - 1) * this.scaleX + this.sourceOrigin.x;
/*      */         
/*  499 */         int y1 = (startY + this.scaleY - 1 - this.sourceOrigin.y) / this.scaleY;
/*  500 */         startX = x1 * this.scaleX + this.sourceOrigin.x;
/*  501 */         startY = y1 * this.scaleY + this.sourceOrigin.y;
/*      */ 
/*      */         
/*  504 */         x1 += offx;
/*  505 */         y1 += offy;
/*      */         
/*  507 */         tx -= x * this.tileWidth;
/*  508 */         ty -= y * this.tileHeight;
/*      */         
/*  510 */         if (this.sampleModel instanceof MultiPixelPackedSampleModel) {
/*  511 */           MultiPixelPackedSampleModel mppsm = (MultiPixelPackedSampleModel)this.originalSampleModel;
/*      */ 
/*      */           
/*  514 */           this.iis.skipBytes(mppsm.getOffset(tx, ty) * sampleSizeByte);
/*      */ 
/*      */           
/*  517 */           int readBytes = (mppsm.getOffset(x2, 0) - mppsm.getOffset(startX, 0) + 1) * sampleSizeByte;
/*      */ 
/*      */           
/*  520 */           int skipLength = (scanlineStride * this.scaleY - readBytes) * sampleSizeByte;
/*      */           
/*  522 */           readBytes *= sampleSizeByte;
/*      */           
/*  524 */           if (pixbuf == null || pixbuf.length < readBytes) {
/*  525 */             pixbuf = new byte[readBytes];
/*      */           }
/*  527 */           int bitoff = mppsm.getBitOffset(tx);
/*      */           
/*  529 */           for (int l = 0, m = y1; l < cTileHeight; 
/*  530 */             l += this.scaleY, m++) {
/*  531 */             if (this.reader.getAbortRequest())
/*      */               break; 
/*  533 */             this.iis.readFully(pixbuf, 0, readBytes);
/*  534 */             if (this.scaleX == 1) {
/*      */               
/*  536 */               if (bitoff != 0) {
/*  537 */                 int mask1 = 255 << bitoff & 0xFF;
/*  538 */                 int mask2 = (mask1 ^ 0xFFFFFFFF) & 0xFF;
/*  539 */                 int shift = 8 - bitoff;
/*      */                 
/*  541 */                 int n = 0;
/*  542 */                 for (; n < readBytes - 1; n++) {
/*  543 */                   pixbuf[n] = (byte)((pixbuf[n] & mask2) << shift | (pixbuf[n + 1] & mask1) >> bitoff);
/*      */                 }
/*  545 */                 pixbuf[n] = (byte)((pixbuf[n] & mask2) << shift);
/*      */               } 
/*      */             } else {
/*      */               
/*  549 */               int bit = 7;
/*  550 */               int pos = 0;
/*  551 */               int mask = 128;
/*      */               
/*  553 */               int n = 0, n1 = startX & 0x7;
/*  554 */               for (; n < lineLength; n++, n1 += this.scaleX) {
/*  555 */                 pixbuf[pos] = (byte)(pixbuf[pos] & (1 << bit ^ 0xFFFFFFFF) | (pixbuf[n1 >> 3] >> 7 - (n1 & 0x7) & 0x1) << bit);
/*      */                 
/*  557 */                 bit--;
/*  558 */                 if (bit == -1) {
/*  559 */                   bit = 7;
/*  560 */                   pos++;
/*      */                 } 
/*      */               } 
/*      */             } 
/*      */             
/*  565 */             ImageUtil.setPackedBinaryData(pixbuf, raster, new Rectangle(x1, m, lineLength, 1));
/*      */ 
/*      */ 
/*      */             
/*  569 */             this.iis.skipBytes(skipLength);
/*  570 */             if (this.destImage != null) {
/*  571 */               this.reader.processImageUpdateWrapper(this.destImage, x1, m, cTileWidth, 1, 1, 1, this.destinationBands);
/*      */             }
/*      */ 
/*      */             
/*  575 */             this.reader.processImageProgressWrapper(percentage + ((l - startY) + 1.0F) / cTileHeight / totalTiles);
/*      */           } 
/*      */         } else {
/*      */           int readLength;
/*      */           
/*      */           int skipLength;
/*      */           
/*  582 */           if (pixelStride < scanlineStride) {
/*  583 */             readLength = cTileWidth * pixelStride;
/*  584 */             skipLength = (scanlineStride * this.scaleY - readLength) * sampleSizeByte;
/*      */           } else {
/*      */             
/*  587 */             readLength = cTileHeight * scanlineStride;
/*  588 */             skipLength = (pixelStride * this.scaleX - readLength) * sampleSizeByte;
/*      */           } 
/*      */ 
/*      */ 
/*      */           
/*  593 */           switch (this.sampleModel.getDataType()) {
/*      */             case 0:
/*  595 */               if (pixbuf == null || pixbuf.length < readLength) {
/*  596 */                 pixbuf = new byte[readLength];
/*      */               }
/*      */               break;
/*      */             case 1:
/*      */             case 2:
/*  601 */               if (spixbuf == null || spixbuf.length < readLength) {
/*  602 */                 spixbuf = new short[readLength];
/*      */               }
/*      */               break;
/*      */             case 3:
/*  606 */               if (ipixbuf == null || ipixbuf.length < readLength) {
/*  607 */                 ipixbuf = new int[readLength];
/*      */               }
/*      */               break;
/*      */             case 4:
/*  611 */               if (fpixbuf == null || fpixbuf.length < readLength) {
/*  612 */                 fpixbuf = new float[readLength];
/*      */               }
/*      */               break;
/*      */             case 5:
/*  616 */               if (dpixbuf == null || dpixbuf.length < readLength) {
/*  617 */                 dpixbuf = new double[readLength];
/*      */               }
/*      */               break;
/*      */           } 
/*  621 */           if (this.sampleModel instanceof java.awt.image.PixelInterleavedSampleModel) {
/*  622 */             int outerFirst, outerSecond, outerStep, outerBound, innerStep, innerStep1, outerStep1; this.iis.skipBytes((tx * pixelStride + ty * scanlineStride) * sampleSizeByte);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */             
/*  628 */             if (pixelStride < scanlineStride) {
/*  629 */               outerFirst = 0;
/*  630 */               outerSecond = y1;
/*  631 */               outerStep = this.scaleY;
/*  632 */               outerBound = cTileHeight;
/*  633 */               innerStep = this.scaleX * pixelStride;
/*  634 */               innerStep1 = destPixelStride;
/*  635 */               outerStep1 = destScanlineStride;
/*      */             } else {
/*  637 */               outerFirst = 0;
/*  638 */               outerSecond = x1;
/*  639 */               outerStep = this.scaleX;
/*  640 */               outerBound = cTileWidth;
/*  641 */               innerStep = this.scaleY * scanlineStride;
/*  642 */               innerStep1 = destScanlineStride;
/*  643 */               outerStep1 = destPixelStride;
/*      */             } 
/*      */ 
/*      */ 
/*      */ 
/*      */             
/*  649 */             int destPos = destSX + (y1 - raster.getSampleModelTranslateY()) * destScanlineStride + (x1 - raster.getSampleModelTranslateX()) * destPixelStride;
/*      */ 
/*      */             
/*  652 */             for (int l = outerFirst, m = outerSecond; l < outerBound; 
/*  653 */               l += outerStep, m++) {
/*  654 */               if (this.reader.getAbortRequest()) {
/*      */                 break;
/*      */               }
/*  657 */               switch (dataType) {
/*      */                 case 0:
/*  659 */                   if (innerStep == numBands && innerStep1 == numBands) {
/*      */                     
/*  661 */                     this.iis.readFully(destPixbuf, destPos, readLength); break;
/*      */                   } 
/*  663 */                   this.iis.readFully(pixbuf, 0, readLength);
/*      */                   break;
/*      */                 case 1:
/*      */                 case 2:
/*  667 */                   if (innerStep == numBands && innerStep1 == numBands) {
/*      */                     
/*  669 */                     this.iis.readFully(destSPixbuf, destPos, readLength); break;
/*      */                   } 
/*  671 */                   this.iis.readFully(spixbuf, 0, readLength);
/*      */                   break;
/*      */                 case 3:
/*  674 */                   if (innerStep == numBands && innerStep1 == numBands) {
/*      */                     
/*  676 */                     this.iis.readFully(destIPixbuf, destPos, readLength); break;
/*      */                   } 
/*  678 */                   this.iis.readFully(ipixbuf, 0, readLength);
/*      */                   break;
/*      */                 case 4:
/*  681 */                   if (innerStep == numBands && innerStep1 == numBands) {
/*      */                     
/*  683 */                     this.iis.readFully(destFPixbuf, destPos, readLength); break;
/*      */                   } 
/*  685 */                   this.iis.readFully(fpixbuf, 0, readLength);
/*      */                   break;
/*      */                 case 5:
/*  688 */                   if (innerStep == numBands && innerStep1 == numBands) {
/*      */                     
/*  690 */                     this.iis.readFully(destDPixbuf, destPos, readLength); break;
/*      */                   } 
/*  692 */                   this.iis.readFully(dpixbuf, 0, readLength);
/*      */                   break;
/*      */               } 
/*      */               
/*  696 */               if (innerStep != numBands || innerStep1 != numBands) {
/*  697 */                 for (int b = 0; b < numBands; b++) {
/*  698 */                   int m1, n, destBandOffset = destBandOffsets[this.destinationBands[b]];
/*      */                   
/*  700 */                   destPos += destBandOffset;
/*      */                   
/*  702 */                   int sourceBandOffset = bandOffsets[this.sourceBands[b]];
/*      */ 
/*      */                   
/*  705 */                   switch (dataType) {
/*      */                     case 0:
/*  707 */                       for (m1 = 0, n = destPos; m1 < readLength; 
/*  708 */                         m1 += innerStep, n += innerStep1) {
/*  709 */                         destPixbuf[n] = pixbuf[m1 + sourceBandOffset];
/*      */                       }
/*      */                       break;
/*      */                     
/*      */                     case 1:
/*      */                     case 2:
/*  715 */                       for (m1 = 0, n = destPos; m1 < readLength; 
/*  716 */                         m1 += innerStep, n += innerStep1) {
/*  717 */                         destSPixbuf[n] = spixbuf[m1 + sourceBandOffset];
/*      */                       }
/*      */                       break;
/*      */                     
/*      */                     case 3:
/*  722 */                       for (m1 = 0, n = destPos; m1 < readLength; 
/*  723 */                         m1 += innerStep, n += innerStep1) {
/*  724 */                         destIPixbuf[n] = ipixbuf[m1 + sourceBandOffset];
/*      */                       }
/*      */                       break;
/*      */                     
/*      */                     case 4:
/*  729 */                       for (m1 = 0, n = destPos; m1 < readLength; 
/*  730 */                         m1 += innerStep, n += innerStep1) {
/*  731 */                         destFPixbuf[n] = fpixbuf[m1 + sourceBandOffset];
/*      */                       }
/*      */                       break;
/*      */                     
/*      */                     case 5:
/*  736 */                       for (m1 = 0, n = destPos; m1 < readLength; 
/*  737 */                         m1 += innerStep, n += innerStep1) {
/*  738 */                         destDPixbuf[n] = dpixbuf[m1 + sourceBandOffset];
/*      */                       }
/*      */                       break;
/*      */                   } 
/*      */                   
/*  743 */                   destPos -= destBandOffset;
/*      */                 } 
/*      */               }
/*  746 */               this.iis.skipBytes(skipLength);
/*  747 */               destPos += outerStep1;
/*      */               
/*  749 */               if (this.destImage != null) {
/*  750 */                 if (pixelStride < scanlineStride) {
/*  751 */                   this.reader.processImageUpdateWrapper(this.destImage, x1, m, outerBound, 1, 1, 1, this.destinationBands);
/*      */                 
/*      */                 }
/*      */                 else {
/*      */ 
/*      */                   
/*  757 */                   this.reader.processImageUpdateWrapper(this.destImage, m, y1, 1, outerBound, 1, 1, this.destinationBands);
/*      */                 } 
/*      */               }
/*      */ 
/*      */ 
/*      */               
/*  763 */               this.reader.processImageProgressWrapper(percentage + (l + 1.0F) / outerBound / totalTiles);
/*      */             
/*      */             }
/*      */           
/*      */           }
/*  768 */           else if (this.sampleModel instanceof java.awt.image.BandedSampleModel || this.sampleModel instanceof SinglePixelPackedSampleModel || bandStride == 0) {
/*      */ 
/*      */             
/*  771 */             boolean isBanded = this.sampleModel instanceof java.awt.image.BandedSampleModel;
/*      */ 
/*      */             
/*  774 */             int bandSize = (int)ImageUtil.getBandSize(this.originalSampleModel);
/*      */             
/*  776 */             for (int b = 0; b < numBands; b++) {
/*  777 */               int outerFirst, outerSecond, outerStep, outerBound, innerStep, innerStep1, outerStep1; this.iis.seek(tilePosition + (bandSize * this.sourceBands[b] * sampleSizeByte));
/*      */               
/*  779 */               int destBandOffset = destBandOffsets[this.destinationBands[b]];
/*      */ 
/*      */               
/*  782 */               this.iis.skipBytes((ty * scanlineStride + tx * pixelStride) * sampleSizeByte);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */               
/*  788 */               if (pixelStride < scanlineStride) {
/*  789 */                 outerFirst = 0;
/*  790 */                 outerSecond = y1;
/*  791 */                 outerStep = this.scaleY;
/*  792 */                 outerBound = cTileHeight;
/*  793 */                 innerStep = this.scaleX * pixelStride;
/*  794 */                 innerStep1 = destPixelStride;
/*  795 */                 outerStep1 = destScanlineStride;
/*      */               } else {
/*  797 */                 outerFirst = 0;
/*  798 */                 outerSecond = x1;
/*  799 */                 outerStep = this.scaleX;
/*  800 */                 outerBound = cTileWidth;
/*  801 */                 innerStep = this.scaleY * scanlineStride;
/*  802 */                 innerStep1 = destScanlineStride;
/*  803 */                 outerStep1 = destPixelStride;
/*      */               } 
/*      */ 
/*      */               
/*  807 */               int destPos = destSX + (y1 - raster.getSampleModelTranslateY()) * destScanlineStride + (x1 - raster.getSampleModelTranslateX()) * destPixelStride + destBandOffset;
/*      */               
/*  809 */               int bank = bankIndices[this.destinationBands[b]];
/*      */               
/*  811 */               switch (dataType) {
/*      */                 
/*      */                 case 0:
/*  814 */                   destPixbuf = ((DataBufferByte)raster.getDataBuffer()).getData(bank);
/*      */                   break;
/*      */                 
/*      */                 case 2:
/*  818 */                   destSPixbuf = ((DataBufferShort)raster.getDataBuffer()).getData(bank);
/*      */                   break;
/*      */ 
/*      */                 
/*      */                 case 1:
/*  823 */                   destSPixbuf = ((DataBufferUShort)raster.getDataBuffer()).getData(bank);
/*      */                   break;
/*      */ 
/*      */                 
/*      */                 case 3:
/*  828 */                   destIPixbuf = ((DataBufferInt)raster.getDataBuffer()).getData(bank);
/*      */                   break;
/*      */ 
/*      */                 
/*      */                 case 4:
/*  833 */                   destFPixbuf = ((DataBufferFloat)raster.getDataBuffer()).getData(bank);
/*      */                   break;
/*      */ 
/*      */                 
/*      */                 case 5:
/*  838 */                   destDPixbuf = ((DataBufferDouble)raster.getDataBuffer()).getData(bank);
/*      */                   break;
/*      */               } 
/*      */               
/*  842 */               for (int l = outerFirst, m = outerSecond; l < outerBound; 
/*  843 */                 l += outerStep, m++) {
/*  844 */                 int m1, n; if (this.reader.getAbortRequest()) {
/*      */                   break;
/*      */                 }
/*  847 */                 switch (dataType) {
/*      */                   case 0:
/*  849 */                     if (innerStep == 1 && innerStep1 == 1) {
/*  850 */                       this.iis.readFully(destPixbuf, destPos, readLength); break;
/*      */                     } 
/*  852 */                     this.iis.readFully(pixbuf, 0, readLength);
/*  853 */                     for (m1 = 0, n = destPos; m1 < readLength; 
/*  854 */                       m1 += innerStep, n += innerStep1) {
/*  855 */                       destPixbuf[n] = pixbuf[m1];
/*      */                     }
/*      */                     break;
/*      */                   
/*      */                   case 1:
/*      */                   case 2:
/*  861 */                     if (innerStep == 1 && innerStep1 == 1) {
/*  862 */                       this.iis.readFully(destSPixbuf, destPos, readLength); break;
/*      */                     } 
/*  864 */                     this.iis.readFully(spixbuf, 0, readLength);
/*  865 */                     for (m1 = 0, n = destPos; m1 < readLength; 
/*  866 */                       m1 += innerStep, n += innerStep1) {
/*  867 */                       destSPixbuf[n] = spixbuf[m1];
/*      */                     }
/*      */                     break;
/*      */                   
/*      */                   case 3:
/*  872 */                     if (innerStep == 1 && innerStep1 == 1) {
/*  873 */                       this.iis.readFully(destIPixbuf, destPos, readLength); break;
/*      */                     } 
/*  875 */                     this.iis.readFully(ipixbuf, 0, readLength);
/*  876 */                     for (m1 = 0, n = destPos; m1 < readLength; 
/*  877 */                       m1 += innerStep, n += innerStep1) {
/*  878 */                       destIPixbuf[n] = ipixbuf[m1];
/*      */                     }
/*      */                     break;
/*      */                   
/*      */                   case 4:
/*  883 */                     if (innerStep == 1 && innerStep1 == 1) {
/*  884 */                       this.iis.readFully(destFPixbuf, destPos, readLength); break;
/*      */                     } 
/*  886 */                     this.iis.readFully(fpixbuf, 0, readLength);
/*  887 */                     for (m1 = 0, n = destPos; m1 < readLength; 
/*  888 */                       m1 += innerStep, n += innerStep1) {
/*  889 */                       destFPixbuf[n] = fpixbuf[m1];
/*      */                     }
/*      */                     break;
/*      */                   
/*      */                   case 5:
/*  894 */                     if (innerStep == 1 && innerStep1 == 1) {
/*  895 */                       this.iis.readFully(destDPixbuf, destPos, readLength); break;
/*      */                     } 
/*  897 */                     this.iis.readFully(dpixbuf, 0, readLength);
/*  898 */                     for (m1 = 0, n = destPos; m1 < readLength; 
/*  899 */                       m1 += innerStep, n += innerStep1) {
/*  900 */                       destDPixbuf[n] = dpixbuf[m1];
/*      */                     }
/*      */                     break;
/*      */                 } 
/*      */ 
/*      */                 
/*  906 */                 this.iis.skipBytes(skipLength);
/*  907 */                 destPos += outerStep1;
/*      */                 
/*  909 */                 if (this.destImage != null) {
/*  910 */                   int[] destBands = { this.destinationBands[b] };
/*      */                   
/*  912 */                   if (pixelStride < scanlineStride) {
/*  913 */                     this.reader.processImageUpdateWrapper(this.destImage, x1, m, outerBound, 1, 1, 1, destBands);
/*      */                   
/*      */                   }
/*      */                   else {
/*      */ 
/*      */                     
/*  919 */                     this.reader.processImageUpdateWrapper(this.destImage, m, y1, 1, outerBound, 1, 1, destBands);
/*      */                   } 
/*      */                 } 
/*      */ 
/*      */ 
/*      */ 
/*      */                 
/*  926 */                 this.reader.processImageProgressWrapper((percentage + (l + 1.0F) / outerBound / numBands / totalTiles) * 100.0F);
/*      */               }
/*      */             
/*      */             }
/*      */           
/*      */           }
/*  932 */           else if (this.sampleModel instanceof ComponentSampleModel) {
/*      */ 
/*      */             
/*  935 */             int bufferSize = (int)this.tileDataSize;
/*      */             
/*  937 */             switch (this.sampleModel.getDataType()) {
/*      */               case 0:
/*  939 */                 if (pixbuf == null || pixbuf.length < this.tileDataSize)
/*  940 */                   pixbuf = new byte[(int)this.tileDataSize]; 
/*  941 */                 this.iis.readFully(pixbuf, 0, (int)this.tileDataSize);
/*      */                 break;
/*      */               
/*      */               case 1:
/*      */               case 2:
/*  946 */                 bufferSize /= 2;
/*  947 */                 if (spixbuf == null || spixbuf.length < bufferSize)
/*  948 */                   spixbuf = new short[bufferSize]; 
/*  949 */                 this.iis.readFully(spixbuf, 0, bufferSize);
/*      */                 break;
/*      */               
/*      */               case 3:
/*  953 */                 bufferSize /= 4;
/*  954 */                 if (ipixbuf == null || ipixbuf.length < bufferSize)
/*  955 */                   ipixbuf = new int[bufferSize]; 
/*  956 */                 this.iis.readFully(ipixbuf, 0, bufferSize);
/*      */                 break;
/*      */               
/*      */               case 4:
/*  960 */                 bufferSize /= 4;
/*  961 */                 if (fpixbuf == null || fpixbuf.length < bufferSize)
/*  962 */                   fpixbuf = new float[bufferSize]; 
/*  963 */                 this.iis.readFully(fpixbuf, 0, bufferSize);
/*      */                 break;
/*      */               
/*      */               case 5:
/*  967 */                 bufferSize /= 8;
/*  968 */                 if (dpixbuf == null || dpixbuf.length < bufferSize)
/*  969 */                   dpixbuf = new double[bufferSize]; 
/*  970 */                 this.iis.readFully(dpixbuf, 0, bufferSize);
/*      */                 break;
/*      */             } 
/*      */             
/*  974 */             for (int b = 0; b < numBands; b++) {
/*  975 */               int destBandOffset = destBandOffsets[this.destinationBands[b]];
/*      */ 
/*      */ 
/*      */               
/*  979 */               int destPos = ((ComponentSampleModel)raster.getSampleModel()).getOffset(x1 - raster
/*  980 */                   .getSampleModelTranslateX(), y1 - raster
/*  981 */                   .getSampleModelTranslateY(), this.destinationBands[b]);
/*      */ 
/*      */               
/*  984 */               int bank = bankIndices[this.destinationBands[b]];
/*      */               
/*  986 */               switch (dataType) {
/*      */                 
/*      */                 case 0:
/*  989 */                   destPixbuf = ((DataBufferByte)raster.getDataBuffer()).getData(bank);
/*      */                   break;
/*      */                 
/*      */                 case 2:
/*  993 */                   destSPixbuf = ((DataBufferShort)raster.getDataBuffer()).getData(bank);
/*      */                   break;
/*      */ 
/*      */                 
/*      */                 case 1:
/*  998 */                   destSPixbuf = ((DataBufferUShort)raster.getDataBuffer()).getData(bank);
/*      */                   break;
/*      */ 
/*      */                 
/*      */                 case 3:
/* 1003 */                   destIPixbuf = ((DataBufferInt)raster.getDataBuffer()).getData(bank);
/*      */                   break;
/*      */ 
/*      */                 
/*      */                 case 4:
/* 1008 */                   destFPixbuf = ((DataBufferFloat)raster.getDataBuffer()).getData(bank);
/*      */                   break;
/*      */ 
/*      */                 
/*      */                 case 5:
/* 1013 */                   destDPixbuf = ((DataBufferDouble)raster.getDataBuffer()).getData(bank);
/*      */                   break;
/*      */               } 
/*      */ 
/*      */               
/* 1018 */               int srcPos = ((ComponentSampleModel)this.originalSampleModel).getOffset(tx, ty, this.sourceBands[b]);
/* 1019 */               int skipX = this.scaleX * pixelStride;
/* 1020 */               for (int l = 0, m = y1; l < cTileHeight; 
/* 1021 */                 l += this.scaleY, m++) {
/* 1022 */                 int n, m1, m2; if (this.reader.getAbortRequest()) {
/*      */                   break;
/*      */                 }
/* 1025 */                 switch (dataType) {
/*      */                   case 0:
/* 1027 */                     n = 0; m1 = srcPos; m2 = destPos;
/* 1028 */                     for (; n < lineLength; 
/* 1029 */                       n++, m1 += skipX, m2 += destPixelStride)
/* 1030 */                       destPixbuf[m2] = pixbuf[m1]; 
/*      */                     break;
/*      */                   case 1:
/*      */                   case 2:
/* 1034 */                     n = 0; m1 = srcPos; m2 = destPos;
/* 1035 */                     for (; n < lineLength; 
/* 1036 */                       n++, m1 += skipX, m2 += destPixelStride)
/* 1037 */                       destSPixbuf[m2] = spixbuf[m1]; 
/*      */                     break;
/*      */                   case 3:
/* 1040 */                     n = 0; m1 = srcPos; m2 = destPos;
/* 1041 */                     for (; n < lineLength; 
/* 1042 */                       n++, m1 += skipX, m2 += destPixelStride)
/* 1043 */                       destIPixbuf[m2] = ipixbuf[m1]; 
/*      */                     break;
/*      */                   case 4:
/* 1046 */                     n = 0; m1 = srcPos; m2 = destPos;
/* 1047 */                     for (; n < lineLength; 
/* 1048 */                       n++, m1 += skipX, m2 += destPixelStride)
/* 1049 */                       destFPixbuf[m2] = fpixbuf[m1]; 
/*      */                     break;
/*      */                   case 5:
/* 1052 */                     n = 0; m1 = srcPos; m2 = destPos;
/* 1053 */                     for (; n < lineLength; 
/* 1054 */                       n++, m1 += skipX, m2 += destPixelStride) {
/* 1055 */                       destDPixbuf[m2] = dpixbuf[m1];
/*      */                     }
/*      */                     break;
/*      */                 } 
/* 1059 */                 destPos += destScanlineStride;
/* 1060 */                 srcPos += scanlineStride * this.scaleY;
/*      */                 
/* 1062 */                 if (this.destImage != null) {
/* 1063 */                   int[] destBands = { this.destinationBands[b] };
/*      */                   
/* 1065 */                   this.reader.processImageUpdateWrapper(this.destImage, x1, m, cTileHeight, 1, 1, 1, destBands);
/*      */                 } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */                 
/* 1072 */                 this.reader.processImageProgressWrapper(percentage + (l + 1.0F) / cTileHeight / numBands / totalTiles);
/*      */               }
/*      */             
/*      */             }
/*      */           
/*      */           }
/*      */           else {
/*      */             
/* 1080 */             throw new IllegalArgumentException(I18N.getString("RawRenderedImage1"));
/*      */           } 
/*      */         } 
/*      */       } 
/*      */     } 
/*      */     
/* 1086 */     return raster;
/*      */   }
/*      */   
/*      */   public void setDestImage(BufferedImage image) {
/* 1090 */     this.destImage = image;
/*      */   }
/*      */   
/*      */   public void clearDestImage() {
/* 1094 */     this.destImage = null;
/*      */   }
/*      */   
/*      */   private int getTileNum(int x, int y) {
/* 1098 */     int num = (y - getMinTileY()) * getNumXTiles() + x - getMinTileX();
/*      */     
/* 1100 */     if (num < 0 || num >= getNumXTiles() * getNumYTiles()) {
/* 1101 */       throw new IllegalArgumentException(I18N.getString("RawRenderedImage0"));
/*      */     }
/* 1103 */     return num;
/*      */   }
/*      */   
/*      */   private int clip(int value, int min, int max) {
/* 1107 */     if (value < min)
/* 1108 */       value = min; 
/* 1109 */     if (value > max)
/* 1110 */       value = max; 
/* 1111 */     return value;
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\github\jaiimageio\impl\plugins\raw\RawRenderedImage.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */