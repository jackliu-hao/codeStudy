/*      */ package com.github.jaiimageio.impl.plugins.gif;
/*      */ 
/*      */ import com.github.jaiimageio.impl.common.LZWCompressor;
/*      */ import com.github.jaiimageio.impl.common.PaletteBuilder;
/*      */ import java.awt.Dimension;
/*      */ import java.awt.Rectangle;
/*      */ import java.awt.image.ColorModel;
/*      */ import java.awt.image.ComponentSampleModel;
/*      */ import java.awt.image.DataBufferByte;
/*      */ import java.awt.image.IndexColorModel;
/*      */ import java.awt.image.Raster;
/*      */ import java.awt.image.RenderedImage;
/*      */ import java.awt.image.SampleModel;
/*      */ import java.io.IOException;
/*      */ import java.nio.ByteOrder;
/*      */ import java.util.Arrays;
/*      */ import java.util.Iterator;
/*      */ import javax.imageio.IIOException;
/*      */ import javax.imageio.IIOImage;
/*      */ import javax.imageio.ImageTypeSpecifier;
/*      */ import javax.imageio.ImageWriteParam;
/*      */ import javax.imageio.ImageWriter;
/*      */ import javax.imageio.metadata.IIOInvalidTreeException;
/*      */ import javax.imageio.metadata.IIOMetadata;
/*      */ import javax.imageio.metadata.IIOMetadataNode;
/*      */ import javax.imageio.stream.ImageOutputStream;
/*      */ import org.w3c.dom.Node;
/*      */ import org.w3c.dom.NodeList;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class GIFImageWriter
/*      */   extends ImageWriter
/*      */ {
/*      */   private static final boolean DEBUG = false;
/*      */   static final String STANDARD_METADATA_NAME = "javax_imageio_1.0";
/*      */   static final String STREAM_METADATA_NAME = "javax_imageio_gif_stream_1.0";
/*      */   static final String IMAGE_METADATA_NAME = "javax_imageio_gif_image_1.0";
/*   95 */   private ImageOutputStream stream = null;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean isWritingSequence = false;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean wroteSequenceHeader = false;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  110 */   private GIFWritableStreamMetadata theStreamMetadata = null;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  115 */   private int imageIndex = 0;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static int getNumBits(int value) throws IOException {
/*      */     int numBits;
/*  123 */     switch (value) {
/*      */       case 2:
/*  125 */         numBits = 1;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  152 */         return numBits;case 4: numBits = 2; return numBits;case 8: numBits = 3; return numBits;case 16: numBits = 4; return numBits;case 32: numBits = 5; return numBits;case 64: numBits = 6; return numBits;case 128: numBits = 7; return numBits;case 256: numBits = 8; return numBits;
/*      */     } 
/*      */     throw new IOException("Bad palette length: " + value + "!");
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static void computeRegions(Rectangle sourceBounds, Dimension destSize, ImageWriteParam p) {
/*  163 */     int periodX = 1;
/*  164 */     int periodY = 1;
/*  165 */     if (p != null) {
/*  166 */       int[] sourceBands = p.getSourceBands();
/*  167 */       if (sourceBands != null && (sourceBands.length != 1 || sourceBands[0] != 0))
/*      */       {
/*      */         
/*  170 */         throw new IllegalArgumentException("Cannot sub-band image!");
/*      */       }
/*      */ 
/*      */       
/*  174 */       Rectangle sourceRegion = p.getSourceRegion();
/*  175 */       if (sourceRegion != null) {
/*      */         
/*  177 */         sourceRegion = sourceRegion.intersection(sourceBounds);
/*  178 */         sourceBounds.setBounds(sourceRegion);
/*      */       } 
/*      */ 
/*      */       
/*  182 */       int gridX = p.getSubsamplingXOffset();
/*  183 */       int gridY = p.getSubsamplingYOffset();
/*  184 */       sourceBounds.x += gridX;
/*  185 */       sourceBounds.y += gridY;
/*  186 */       sourceBounds.width -= gridX;
/*  187 */       sourceBounds.height -= gridY;
/*      */ 
/*      */       
/*  190 */       periodX = p.getSourceXSubsampling();
/*  191 */       periodY = p.getSourceYSubsampling();
/*      */     } 
/*      */ 
/*      */     
/*  195 */     destSize.setSize((sourceBounds.width + periodX - 1) / periodX, (sourceBounds.height + periodY - 1) / periodY);
/*      */     
/*  197 */     if (destSize.width <= 0 || destSize.height <= 0) {
/*  198 */       throw new IllegalArgumentException("Empty source region!");
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static byte[] createColorTable(ColorModel colorModel, SampleModel sampleModel) {
/*      */     byte[] colorTable;
/*  209 */     if (colorModel instanceof IndexColorModel) {
/*  210 */       IndexColorModel icm = (IndexColorModel)colorModel;
/*  211 */       int mapSize = icm.getMapSize();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  218 */       int ctSize = getGifPaletteSize(mapSize);
/*      */       
/*  220 */       byte[] reds = new byte[ctSize];
/*  221 */       byte[] greens = new byte[ctSize];
/*  222 */       byte[] blues = new byte[ctSize];
/*  223 */       icm.getReds(reds);
/*  224 */       icm.getGreens(greens);
/*  225 */       icm.getBlues(blues);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  231 */       for (int i = mapSize; i < ctSize; i++) {
/*  232 */         reds[i] = reds[0];
/*  233 */         greens[i] = greens[0];
/*  234 */         blues[i] = blues[0];
/*      */       } 
/*      */       
/*  237 */       colorTable = new byte[3 * ctSize];
/*  238 */       int idx = 0;
/*  239 */       for (int j = 0; j < ctSize; j++) {
/*  240 */         colorTable[idx++] = reds[j];
/*  241 */         colorTable[idx++] = greens[j];
/*  242 */         colorTable[idx++] = blues[j];
/*      */       } 
/*  244 */     } else if (sampleModel.getNumBands() == 1) {
/*      */       
/*  246 */       int numBits = sampleModel.getSampleSize()[0];
/*  247 */       if (numBits > 8) {
/*  248 */         numBits = 8;
/*      */       }
/*  250 */       int colorTableLength = 3 * (1 << numBits);
/*  251 */       colorTable = new byte[colorTableLength];
/*  252 */       for (int i = 0; i < colorTableLength; i++) {
/*  253 */         colorTable[i] = (byte)(i / 3);
/*      */       }
/*      */     }
/*      */     else {
/*      */       
/*  258 */       colorTable = null;
/*      */     } 
/*      */     
/*  261 */     return colorTable;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static int getGifPaletteSize(int x) {
/*  269 */     if (x <= 2) {
/*  270 */       return 2;
/*      */     }
/*  272 */     x--;
/*  273 */     x |= x >> 1;
/*  274 */     x |= x >> 2;
/*  275 */     x |= x >> 4;
/*  276 */     x |= x >> 8;
/*  277 */     x |= x >> 16;
/*  278 */     return x + 1;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public GIFImageWriter(GIFImageWriterSpi originatingProvider) {
/*  284 */     super(originatingProvider);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean canWriteSequence() {
/*  291 */     return true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void convertMetadata(String metadataFormatName, IIOMetadata inData, IIOMetadata outData) {
/*  302 */     String formatName = null;
/*      */     
/*  304 */     String nativeFormatName = inData.getNativeMetadataFormatName();
/*  305 */     if (nativeFormatName != null && nativeFormatName
/*  306 */       .equals(metadataFormatName)) {
/*  307 */       formatName = metadataFormatName;
/*      */     } else {
/*  309 */       String[] extraFormatNames = inData.getExtraMetadataFormatNames();
/*      */       
/*  311 */       if (extraFormatNames != null) {
/*  312 */         for (int i = 0; i < extraFormatNames.length; i++) {
/*  313 */           if (extraFormatNames[i].equals(metadataFormatName)) {
/*  314 */             formatName = metadataFormatName;
/*      */             
/*      */             break;
/*      */           } 
/*      */         } 
/*      */       }
/*      */     } 
/*  321 */     if (formatName == null && inData
/*  322 */       .isStandardMetadataFormatSupported()) {
/*  323 */       formatName = "javax_imageio_1.0";
/*      */     }
/*      */     
/*  326 */     if (formatName != null) {
/*      */       try {
/*  328 */         Node root = inData.getAsTree(formatName);
/*  329 */         outData.mergeTree(formatName, root);
/*  330 */       } catch (IIOInvalidTreeException iIOInvalidTreeException) {}
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public IIOMetadata convertStreamMetadata(IIOMetadata inData, ImageWriteParam param) {
/*  342 */     if (inData == null) {
/*  343 */       throw new IllegalArgumentException("inData == null!");
/*      */     }
/*      */     
/*  346 */     IIOMetadata sm = getDefaultStreamMetadata(param);
/*      */     
/*  348 */     convertMetadata("javax_imageio_gif_stream_1.0", inData, sm);
/*      */     
/*  350 */     return sm;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public IIOMetadata convertImageMetadata(IIOMetadata inData, ImageTypeSpecifier imageType, ImageWriteParam param) {
/*  360 */     if (inData == null) {
/*  361 */       throw new IllegalArgumentException("inData == null!");
/*      */     }
/*  363 */     if (imageType == null) {
/*  364 */       throw new IllegalArgumentException("imageType == null!");
/*      */     }
/*      */ 
/*      */     
/*  368 */     GIFWritableImageMetadata im = (GIFWritableImageMetadata)getDefaultImageMetadata(imageType, param);
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  373 */     boolean isProgressive = im.interlaceFlag;
/*      */     
/*  375 */     convertMetadata("javax_imageio_gif_image_1.0", inData, im);
/*      */ 
/*      */ 
/*      */     
/*  379 */     if (param != null && param.canWriteProgressive() && param
/*  380 */       .getProgressiveMode() != 3) {
/*  381 */       im.interlaceFlag = isProgressive;
/*      */     }
/*      */     
/*  384 */     return im;
/*      */   }
/*      */   
/*      */   public void endWriteSequence() throws IOException {
/*  388 */     if (this.stream == null) {
/*  389 */       throw new IllegalStateException("output == null!");
/*      */     }
/*  391 */     if (!this.isWritingSequence) {
/*  392 */       throw new IllegalStateException("prepareWriteSequence() was not invoked!");
/*      */     }
/*  394 */     writeTrailer();
/*  395 */     resetLocal();
/*      */   }
/*      */ 
/*      */   
/*      */   public IIOMetadata getDefaultImageMetadata(ImageTypeSpecifier imageType, ImageWriteParam param) {
/*  400 */     GIFWritableImageMetadata imageMetadata = new GIFWritableImageMetadata();
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  405 */     SampleModel sampleModel = imageType.getSampleModel();
/*      */ 
/*      */     
/*  408 */     Rectangle sourceBounds = new Rectangle(sampleModel.getWidth(), sampleModel.getHeight());
/*  409 */     Dimension destSize = new Dimension();
/*  410 */     computeRegions(sourceBounds, destSize, param);
/*      */     
/*  412 */     imageMetadata.imageWidth = destSize.width;
/*  413 */     imageMetadata.imageHeight = destSize.height;
/*      */ 
/*      */ 
/*      */     
/*  417 */     if (param != null && param.canWriteProgressive() && param
/*  418 */       .getProgressiveMode() == 0) {
/*  419 */       imageMetadata.interlaceFlag = false;
/*      */     } else {
/*  421 */       imageMetadata.interlaceFlag = true;
/*      */     } 
/*      */ 
/*      */ 
/*      */     
/*  426 */     ColorModel colorModel = imageType.getColorModel();
/*      */     
/*  428 */     imageMetadata
/*  429 */       .localColorTable = createColorTable(colorModel, sampleModel);
/*      */ 
/*      */ 
/*      */     
/*  433 */     if (colorModel instanceof IndexColorModel) {
/*      */       
/*  435 */       int transparentIndex = ((IndexColorModel)colorModel).getTransparentPixel();
/*  436 */       if (transparentIndex != -1) {
/*  437 */         imageMetadata.transparentColorFlag = true;
/*  438 */         imageMetadata.transparentColorIndex = transparentIndex;
/*      */       } 
/*      */     } 
/*      */     
/*  442 */     return imageMetadata;
/*      */   }
/*      */   
/*      */   public IIOMetadata getDefaultStreamMetadata(ImageWriteParam param) {
/*  446 */     GIFWritableStreamMetadata streamMetadata = new GIFWritableStreamMetadata();
/*      */     
/*  448 */     streamMetadata.version = "89a";
/*  449 */     return streamMetadata;
/*      */   }
/*      */   
/*      */   public ImageWriteParam getDefaultWriteParam() {
/*  453 */     return new GIFImageWriteParam(getLocale());
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void prepareWriteSequence(IIOMetadata streamMetadata) throws IOException {
/*  459 */     if (this.stream == null) {
/*  460 */       throw new IllegalStateException("Output is not set.");
/*      */     }
/*      */     
/*  463 */     resetLocal();
/*      */ 
/*      */     
/*  466 */     if (streamMetadata == null) {
/*  467 */       this
/*  468 */         .theStreamMetadata = (GIFWritableStreamMetadata)getDefaultStreamMetadata((ImageWriteParam)null);
/*      */     } else {
/*  470 */       this.theStreamMetadata = new GIFWritableStreamMetadata();
/*  471 */       convertMetadata("javax_imageio_gif_stream_1.0", streamMetadata, this.theStreamMetadata);
/*      */     } 
/*      */ 
/*      */     
/*  475 */     this.isWritingSequence = true;
/*      */   }
/*      */   
/*      */   public void reset() {
/*  479 */     super.reset();
/*  480 */     resetLocal();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void resetLocal() {
/*  487 */     this.isWritingSequence = false;
/*  488 */     this.wroteSequenceHeader = false;
/*  489 */     this.theStreamMetadata = null;
/*  490 */     this.imageIndex = 0;
/*      */   }
/*      */   
/*      */   public void setOutput(Object output) {
/*  494 */     super.setOutput(output);
/*  495 */     if (output != null) {
/*  496 */       if (!(output instanceof ImageOutputStream)) {
/*  497 */         throw new IllegalArgumentException("output is not an ImageOutputStream");
/*      */       }
/*      */       
/*  500 */       this.stream = (ImageOutputStream)output;
/*  501 */       this.stream.setByteOrder(ByteOrder.LITTLE_ENDIAN);
/*      */     } else {
/*  503 */       this.stream = null;
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public void write(IIOMetadata sm, IIOImage iioimage, ImageWriteParam p) throws IOException {
/*      */     GIFWritableStreamMetadata streamMetadata;
/*  510 */     if (this.stream == null) {
/*  511 */       throw new IllegalStateException("output == null!");
/*      */     }
/*  513 */     if (iioimage == null) {
/*  514 */       throw new IllegalArgumentException("iioimage == null!");
/*      */     }
/*  516 */     if (iioimage.hasRaster()) {
/*  517 */       throw new UnsupportedOperationException("canWriteRasters() == false!");
/*      */     }
/*      */     
/*  520 */     resetLocal();
/*      */ 
/*      */     
/*  523 */     if (sm == null) {
/*      */       
/*  525 */       streamMetadata = (GIFWritableStreamMetadata)getDefaultStreamMetadata(p);
/*      */     } else {
/*      */       
/*  528 */       streamMetadata = (GIFWritableStreamMetadata)convertStreamMetadata(sm, p);
/*      */     } 
/*      */     
/*  531 */     write(true, true, streamMetadata, iioimage, p);
/*      */   }
/*      */ 
/*      */   
/*      */   public void writeToSequence(IIOImage image, ImageWriteParam param) throws IOException {
/*  536 */     if (this.stream == null) {
/*  537 */       throw new IllegalStateException("output == null!");
/*      */     }
/*  539 */     if (image == null) {
/*  540 */       throw new IllegalArgumentException("image == null!");
/*      */     }
/*  542 */     if (image.hasRaster()) {
/*  543 */       throw new UnsupportedOperationException("canWriteRasters() == false!");
/*      */     }
/*  545 */     if (!this.isWritingSequence) {
/*  546 */       throw new IllegalStateException("prepareWriteSequence() was not invoked!");
/*      */     }
/*      */     
/*  549 */     write(!this.wroteSequenceHeader, false, this.theStreamMetadata, image, param);
/*      */ 
/*      */     
/*  552 */     if (!this.wroteSequenceHeader) {
/*  553 */       this.wroteSequenceHeader = true;
/*      */     }
/*      */     
/*  556 */     this.imageIndex++;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean needToCreateIndex(RenderedImage image) {
/*  562 */     SampleModel sampleModel = image.getSampleModel();
/*  563 */     ColorModel colorModel = image.getColorModel();
/*      */     
/*  565 */     return (sampleModel.getNumBands() != 1 || sampleModel
/*  566 */       .getSampleSize()[0] > 8 || colorModel
/*  567 */       .getComponentSize()[0] > 8);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void write(boolean writeHeader, boolean writeTrailer, IIOMetadata sm, IIOImage iioimage, ImageWriteParam p) throws IOException {
/*  597 */     clearAbortRequest();
/*      */     
/*  599 */     RenderedImage image = iioimage.getRenderedImage();
/*      */ 
/*      */     
/*  602 */     if (needToCreateIndex(image)) {
/*  603 */       image = PaletteBuilder.createIndexedImage(image);
/*  604 */       iioimage.setRenderedImage(image);
/*      */     } 
/*      */     
/*  607 */     ColorModel colorModel = image.getColorModel();
/*  608 */     SampleModel sampleModel = image.getSampleModel();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  614 */     Rectangle sourceBounds = new Rectangle(image.getMinX(), image.getMinY(), image.getWidth(), image.getHeight());
/*  615 */     Dimension destSize = new Dimension();
/*  616 */     computeRegions(sourceBounds, destSize, p);
/*      */ 
/*      */     
/*  619 */     GIFWritableImageMetadata imageMetadata = null;
/*  620 */     if (iioimage.getMetadata() != null) {
/*  621 */       imageMetadata = new GIFWritableImageMetadata();
/*  622 */       convertMetadata("javax_imageio_gif_image_1.0", iioimage.getMetadata(), imageMetadata);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  631 */       if (imageMetadata.localColorTable == null) {
/*  632 */         imageMetadata
/*  633 */           .localColorTable = createColorTable(colorModel, sampleModel);
/*      */ 
/*      */ 
/*      */         
/*  637 */         if (colorModel instanceof IndexColorModel) {
/*  638 */           IndexColorModel icm = (IndexColorModel)colorModel;
/*      */           
/*  640 */           int index = icm.getTransparentPixel();
/*  641 */           imageMetadata.transparentColorFlag = (index != -1);
/*  642 */           if (imageMetadata.transparentColorFlag) {
/*  643 */             imageMetadata.transparentColorIndex = index;
/*      */           }
/*      */         } 
/*      */       } 
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  656 */     byte[] globalColorTable = null;
/*      */ 
/*      */ 
/*      */     
/*  660 */     if (writeHeader) {
/*  661 */       int bitsPerPixel; if (sm == null) {
/*  662 */         throw new IllegalArgumentException("Cannot write null header!");
/*      */       }
/*      */       
/*  665 */       GIFWritableStreamMetadata streamMetadata = (GIFWritableStreamMetadata)sm;
/*      */ 
/*      */ 
/*      */       
/*  669 */       if (streamMetadata.version == null) {
/*  670 */         streamMetadata.version = "89a";
/*      */       }
/*      */ 
/*      */       
/*  674 */       if (streamMetadata.logicalScreenWidth == -1)
/*      */       {
/*      */         
/*  677 */         streamMetadata.logicalScreenWidth = destSize.width;
/*      */       }
/*      */       
/*  680 */       if (streamMetadata.logicalScreenHeight == -1)
/*      */       {
/*      */         
/*  683 */         streamMetadata.logicalScreenHeight = destSize.height;
/*      */       }
/*      */       
/*  686 */       if (streamMetadata.colorResolution == -1)
/*      */       {
/*      */         
/*  689 */         streamMetadata
/*      */           
/*  691 */           .colorResolution = (colorModel != null) ? colorModel.getComponentSize()[0] : sampleModel.getSampleSize()[0];
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*  696 */       if (streamMetadata.globalColorTable == null) {
/*  697 */         if (this.isWritingSequence && imageMetadata != null && imageMetadata.localColorTable != null) {
/*      */ 
/*      */ 
/*      */           
/*  701 */           streamMetadata.globalColorTable = imageMetadata.localColorTable;
/*      */         }
/*  703 */         else if (imageMetadata == null || imageMetadata.localColorTable == null) {
/*      */ 
/*      */           
/*  706 */           streamMetadata
/*  707 */             .globalColorTable = createColorTable(colorModel, sampleModel);
/*      */         } 
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  720 */       globalColorTable = streamMetadata.globalColorTable;
/*      */ 
/*      */ 
/*      */       
/*  724 */       if (globalColorTable != null) {
/*  725 */         bitsPerPixel = getNumBits(globalColorTable.length / 3);
/*  726 */       } else if (imageMetadata != null && imageMetadata.localColorTable != null) {
/*      */ 
/*      */         
/*  729 */         bitsPerPixel = getNumBits(imageMetadata.localColorTable.length / 3);
/*      */       } else {
/*  731 */         bitsPerPixel = sampleModel.getSampleSize(0);
/*      */       } 
/*  733 */       writeHeader(streamMetadata, bitsPerPixel);
/*  734 */     } else if (this.isWritingSequence) {
/*  735 */       globalColorTable = this.theStreamMetadata.globalColorTable;
/*      */     } else {
/*  737 */       throw new IllegalArgumentException("Must write header for single image!");
/*      */     } 
/*      */ 
/*      */     
/*  741 */     writeImage(iioimage.getRenderedImage(), imageMetadata, p, globalColorTable, sourceBounds, destSize);
/*      */ 
/*      */ 
/*      */     
/*  745 */     if (writeTrailer) {
/*  746 */       writeTrailer();
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void writeImage(RenderedImage image, GIFWritableImageMetadata imageMetadata, ImageWriteParam param, byte[] globalColorTable, Rectangle sourceBounds, Dimension destSize) throws IOException {
/*      */     boolean writeGraphicsControlExtension;
/*  765 */     ColorModel colorModel = image.getColorModel();
/*  766 */     SampleModel sampleModel = image.getSampleModel();
/*      */ 
/*      */     
/*  769 */     if (imageMetadata == null) {
/*      */       
/*  771 */       imageMetadata = (GIFWritableImageMetadata)getDefaultImageMetadata(new ImageTypeSpecifier(image), param);
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  776 */       writeGraphicsControlExtension = imageMetadata.transparentColorFlag;
/*      */     } else {
/*      */       
/*  779 */       NodeList list = null;
/*      */       
/*      */       try {
/*  782 */         IIOMetadataNode root = (IIOMetadataNode)imageMetadata.getAsTree("javax_imageio_gif_image_1.0");
/*  783 */         list = root.getElementsByTagName("GraphicControlExtension");
/*  784 */       } catch (IllegalArgumentException illegalArgumentException) {}
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  790 */       writeGraphicsControlExtension = (list != null && list.getLength() > 0);
/*      */ 
/*      */ 
/*      */       
/*  794 */       if (param != null && param.canWriteProgressive()) {
/*  795 */         if (param.getProgressiveMode() == 0) {
/*      */           
/*  797 */           imageMetadata.interlaceFlag = false;
/*  798 */         } else if (param.getProgressiveMode() == 1) {
/*      */           
/*  800 */           imageMetadata.interlaceFlag = true;
/*      */         } 
/*      */       }
/*      */     } 
/*      */ 
/*      */     
/*  806 */     if (Arrays.equals(globalColorTable, imageMetadata.localColorTable)) {
/*  807 */       imageMetadata.localColorTable = null;
/*      */     }
/*      */ 
/*      */     
/*  811 */     imageMetadata.imageWidth = destSize.width;
/*  812 */     imageMetadata.imageHeight = destSize.height;
/*      */ 
/*      */     
/*  815 */     if (writeGraphicsControlExtension) {
/*  816 */       writeGraphicControlExtension(imageMetadata);
/*      */     }
/*      */ 
/*      */     
/*  820 */     writePlainTextExtension(imageMetadata);
/*  821 */     writeApplicationExtension(imageMetadata);
/*  822 */     writeCommentExtension(imageMetadata);
/*      */ 
/*      */ 
/*      */     
/*  826 */     int bitsPerPixel = getNumBits((imageMetadata.localColorTable == null) ? ((globalColorTable == null) ? sampleModel
/*      */         
/*  828 */         .getSampleSize(0) : (globalColorTable.length / 3)) : (imageMetadata.localColorTable.length / 3));
/*      */ 
/*      */     
/*  831 */     writeImageDescriptor(imageMetadata, bitsPerPixel);
/*      */ 
/*      */     
/*  834 */     writeRasterData(image, sourceBounds, destSize, param, imageMetadata.interlaceFlag);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void writeRows(RenderedImage image, LZWCompressor compressor, int sx, int sdx, int sy, int sdy, int sw, int dy, int ddy, int dw, int dh, int numRowsWritten, int progressReportRowPeriod) throws IOException {
/*  845 */     int[] sbuf = new int[sw];
/*  846 */     byte[] dbuf = new byte[dw];
/*      */ 
/*      */ 
/*      */     
/*  850 */     Raster raster = (image.getNumXTiles() == 1 && image.getNumYTiles() == 1) ? image.getTile(0, 0) : image.getData(); int y;
/*  851 */     for (y = dy; y < dh; y += ddy) {
/*  852 */       if (numRowsWritten % progressReportRowPeriod == 0) {
/*  853 */         if (abortRequested()) {
/*  854 */           processWriteAborted();
/*      */           return;
/*      */         } 
/*  857 */         processImageProgress(numRowsWritten * 100.0F / dh);
/*      */       } 
/*      */       
/*  860 */       raster.getSamples(sx, sy, sw, 1, 0, sbuf); int j;
/*  861 */       for (int i = 0; i < dw; i++, j += sdx) {
/*  862 */         dbuf[i] = (byte)sbuf[j];
/*      */       }
/*  864 */       compressor.compress(dbuf, 0, dw);
/*  865 */       numRowsWritten++;
/*  866 */       sy += sdy;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void writeRowsOpt(byte[] data, int offset, int lineStride, LZWCompressor compressor, int dy, int ddy, int dw, int dh, int numRowsWritten, int progressReportRowPeriod) throws IOException {
/*  877 */     offset += dy * lineStride;
/*  878 */     lineStride *= ddy; int y;
/*  879 */     for (y = dy; y < dh; y += ddy) {
/*  880 */       if (numRowsWritten % progressReportRowPeriod == 0) {
/*  881 */         if (abortRequested()) {
/*  882 */           processWriteAborted();
/*      */           return;
/*      */         } 
/*  885 */         processImageProgress(numRowsWritten * 100.0F / dh);
/*      */       } 
/*      */       
/*  888 */       compressor.compress(data, offset, dw);
/*  889 */       numRowsWritten++;
/*  890 */       offset += lineStride;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void writeRasterData(RenderedImage image, Rectangle sourceBounds, Dimension destSize, ImageWriteParam param, boolean interlaceFlag) throws IOException {
/*  900 */     int periodX, periodY, sourceXOffset = sourceBounds.x;
/*  901 */     int sourceYOffset = sourceBounds.y;
/*  902 */     int sourceWidth = sourceBounds.width;
/*  903 */     int sourceHeight = sourceBounds.height;
/*      */     
/*  905 */     int destWidth = destSize.width;
/*  906 */     int destHeight = destSize.height;
/*      */ 
/*      */ 
/*      */     
/*  910 */     if (param == null) {
/*  911 */       periodX = 1;
/*  912 */       periodY = 1;
/*      */     } else {
/*  914 */       periodX = param.getSourceXSubsampling();
/*  915 */       periodY = param.getSourceYSubsampling();
/*      */     } 
/*      */     
/*  918 */     SampleModel sampleModel = image.getSampleModel();
/*  919 */     int bitsPerPixel = sampleModel.getSampleSize()[0];
/*      */     
/*  921 */     int initCodeSize = bitsPerPixel;
/*  922 */     if (initCodeSize == 1) {
/*  923 */       initCodeSize++;
/*      */     }
/*  925 */     this.stream.write(initCodeSize);
/*      */     
/*  927 */     LZWCompressor compressor = new LZWCompressor(this.stream, initCodeSize, false);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  934 */     boolean isOptimizedCase = (periodX == 1 && periodY == 1 && sampleModel instanceof ComponentSampleModel && image.getNumXTiles() == 1 && image.getNumYTiles() == 1 && image.getTile(0, 0).getDataBuffer() instanceof DataBufferByte);
/*      */     
/*  936 */     int numRowsWritten = 0;
/*      */     
/*  938 */     int progressReportRowPeriod = Math.max(destHeight / 20, 1);
/*      */     
/*  940 */     processImageStarted(this.imageIndex);
/*      */     
/*  942 */     if (interlaceFlag) {
/*      */ 
/*      */       
/*  945 */       if (isOptimizedCase) {
/*  946 */         Raster tile = image.getTile(0, 0);
/*  947 */         byte[] data = ((DataBufferByte)tile.getDataBuffer()).getData();
/*      */         
/*  949 */         ComponentSampleModel csm = (ComponentSampleModel)tile.getSampleModel();
/*  950 */         int offset = csm.getOffset(sourceXOffset - tile
/*  951 */             .getSampleModelTranslateX(), sourceYOffset - tile
/*      */             
/*  953 */             .getSampleModelTranslateY(), 0);
/*      */         
/*  955 */         int lineStride = csm.getScanlineStride();
/*      */         
/*  957 */         writeRowsOpt(data, offset, lineStride, compressor, 0, 8, destWidth, destHeight, numRowsWritten, progressReportRowPeriod);
/*      */ 
/*      */ 
/*      */         
/*  961 */         if (abortRequested()) {
/*      */           return;
/*      */         }
/*      */         
/*  965 */         numRowsWritten += destHeight / 8;
/*      */         
/*  967 */         writeRowsOpt(data, offset, lineStride, compressor, 4, 8, destWidth, destHeight, numRowsWritten, progressReportRowPeriod);
/*      */ 
/*      */ 
/*      */         
/*  971 */         if (abortRequested()) {
/*      */           return;
/*      */         }
/*      */         
/*  975 */         numRowsWritten += (destHeight - 4) / 8;
/*      */         
/*  977 */         writeRowsOpt(data, offset, lineStride, compressor, 2, 4, destWidth, destHeight, numRowsWritten, progressReportRowPeriod);
/*      */ 
/*      */ 
/*      */         
/*  981 */         if (abortRequested()) {
/*      */           return;
/*      */         }
/*      */         
/*  985 */         numRowsWritten += (destHeight - 2) / 4;
/*      */         
/*  987 */         writeRowsOpt(data, offset, lineStride, compressor, 1, 2, destWidth, destHeight, numRowsWritten, progressReportRowPeriod);
/*      */       }
/*      */       else {
/*      */         
/*  991 */         writeRows(image, compressor, sourceXOffset, periodX, sourceYOffset, 8 * periodY, sourceWidth, 0, 8, destWidth, destHeight, numRowsWritten, progressReportRowPeriod);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  998 */         if (abortRequested()) {
/*      */           return;
/*      */         }
/*      */         
/* 1002 */         numRowsWritten += destHeight / 8;
/*      */         
/* 1004 */         writeRows(image, compressor, sourceXOffset, periodX, sourceYOffset + 4 * periodY, 8 * periodY, sourceWidth, 4, 8, destWidth, destHeight, numRowsWritten, progressReportRowPeriod);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 1010 */         if (abortRequested()) {
/*      */           return;
/*      */         }
/*      */         
/* 1014 */         numRowsWritten += (destHeight - 4) / 8;
/*      */         
/* 1016 */         writeRows(image, compressor, sourceXOffset, periodX, sourceYOffset + 2 * periodY, 4 * periodY, sourceWidth, 2, 4, destWidth, destHeight, numRowsWritten, progressReportRowPeriod);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 1022 */         if (abortRequested()) {
/*      */           return;
/*      */         }
/*      */         
/* 1026 */         numRowsWritten += (destHeight - 2) / 4;
/*      */         
/* 1028 */         writeRows(image, compressor, sourceXOffset, periodX, sourceYOffset + periodY, 2 * periodY, sourceWidth, 1, 2, destWidth, destHeight, numRowsWritten, progressReportRowPeriod);
/*      */ 
/*      */ 
/*      */       
/*      */       }
/*      */ 
/*      */ 
/*      */     
/*      */     }
/* 1037 */     else if (isOptimizedCase) {
/* 1038 */       Raster tile = image.getTile(0, 0);
/* 1039 */       byte[] data = ((DataBufferByte)tile.getDataBuffer()).getData();
/*      */       
/* 1041 */       ComponentSampleModel csm = (ComponentSampleModel)tile.getSampleModel();
/* 1042 */       int offset = csm.getOffset(sourceXOffset - tile
/* 1043 */           .getSampleModelTranslateX(), sourceYOffset - tile
/*      */           
/* 1045 */           .getSampleModelTranslateY(), 0);
/*      */       
/* 1047 */       int lineStride = csm.getScanlineStride();
/*      */       
/* 1049 */       writeRowsOpt(data, offset, lineStride, compressor, 0, 1, destWidth, destHeight, numRowsWritten, progressReportRowPeriod);
/*      */     }
/*      */     else {
/*      */       
/* 1053 */       writeRows(image, compressor, sourceXOffset, periodX, sourceYOffset, periodY, sourceWidth, 0, 1, destWidth, destHeight, numRowsWritten, progressReportRowPeriod);
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1062 */     if (abortRequested()) {
/*      */       return;
/*      */     }
/*      */     
/* 1066 */     processImageProgress(100.0F);
/*      */     
/* 1068 */     compressor.flush();
/*      */     
/* 1070 */     this.stream.write(0);
/*      */     
/* 1072 */     processImageComplete();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void writeHeader(String version, int logicalScreenWidth, int logicalScreenHeight, int colorResolution, int pixelAspectRatio, int backgroundColorIndex, boolean sortFlag, int bitsPerPixel, byte[] globalColorTable) throws IOException {
/*      */     try {
/* 1086 */       this.stream.writeBytes("GIF" + version);
/*      */ 
/*      */ 
/*      */       
/* 1090 */       this.stream.writeShort((short)logicalScreenWidth);
/*      */ 
/*      */       
/* 1093 */       this.stream.writeShort((short)logicalScreenHeight);
/*      */ 
/*      */ 
/*      */       
/* 1097 */       int packedFields = (globalColorTable != null) ? 128 : 0;
/* 1098 */       packedFields |= (colorResolution - 1 & 0x7) << 4;
/* 1099 */       if (sortFlag) {
/* 1100 */         packedFields |= 0x8;
/*      */       }
/* 1102 */       packedFields |= bitsPerPixel - 1;
/* 1103 */       this.stream.write(packedFields);
/*      */ 
/*      */       
/* 1106 */       this.stream.write(backgroundColorIndex);
/*      */ 
/*      */       
/* 1109 */       this.stream.write(pixelAspectRatio);
/*      */ 
/*      */       
/* 1112 */       if (globalColorTable != null) {
/* 1113 */         this.stream.write(globalColorTable);
/*      */       }
/* 1115 */     } catch (IOException e) {
/* 1116 */       throw new IIOException("I/O error writing header!", e);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private void writeHeader(IIOMetadata streamMetadata, int bitsPerPixel) throws IOException {
/*      */     GIFWritableStreamMetadata sm;
/* 1124 */     if (streamMetadata instanceof GIFWritableStreamMetadata) {
/* 1125 */       sm = (GIFWritableStreamMetadata)streamMetadata;
/*      */     } else {
/* 1127 */       sm = new GIFWritableStreamMetadata();
/*      */       
/* 1129 */       Node root = streamMetadata.getAsTree("javax_imageio_gif_stream_1.0");
/* 1130 */       sm.setFromTree("javax_imageio_gif_stream_1.0", root);
/*      */     } 
/*      */     
/* 1133 */     writeHeader(sm.version, sm.logicalScreenWidth, sm.logicalScreenHeight, sm.colorResolution, sm.pixelAspectRatio, sm.backgroundColorIndex, sm.sortFlag, bitsPerPixel, sm.globalColorTable);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void writeGraphicControlExtension(int disposalMethod, boolean userInputFlag, boolean transparentColorFlag, int delayTime, int transparentColorIndex) throws IOException {
/*      */     try {
/* 1151 */       this.stream.write(33);
/* 1152 */       this.stream.write(249);
/*      */       
/* 1154 */       this.stream.write(4);
/*      */       
/* 1156 */       int packedFields = (disposalMethod & 0x3) << 2;
/* 1157 */       if (userInputFlag) {
/* 1158 */         packedFields |= 0x2;
/*      */       }
/* 1160 */       if (transparentColorFlag) {
/* 1161 */         packedFields |= 0x1;
/*      */       }
/* 1163 */       this.stream.write(packedFields);
/*      */       
/* 1165 */       this.stream.writeShort((short)delayTime);
/*      */       
/* 1167 */       this.stream.write(transparentColorIndex);
/* 1168 */       this.stream.write(0);
/* 1169 */     } catch (IOException e) {
/* 1170 */       throw new IIOException("I/O error writing Graphic Control Extension!", e);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   private void writeGraphicControlExtension(GIFWritableImageMetadata im) throws IOException {
/* 1176 */     writeGraphicControlExtension(im.disposalMethod, im.userInputFlag, im.transparentColorFlag, im.delayTime, im.transparentColorIndex);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void writeBlocks(byte[] data) throws IOException {
/* 1184 */     if (data != null && data.length > 0) {
/* 1185 */       int offset = 0;
/* 1186 */       while (offset < data.length) {
/* 1187 */         int len = Math.min(data.length - offset, 255);
/* 1188 */         this.stream.write(len);
/* 1189 */         this.stream.write(data, offset, len);
/* 1190 */         offset += len;
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   private void writePlainTextExtension(GIFWritableImageMetadata im) throws IOException {
/* 1197 */     if (im.hasPlainTextExtension) {
/*      */       try {
/* 1199 */         this.stream.write(33);
/* 1200 */         this.stream.write(1);
/*      */         
/* 1202 */         this.stream.write(12);
/*      */         
/* 1204 */         this.stream.writeShort(im.textGridLeft);
/* 1205 */         this.stream.writeShort(im.textGridTop);
/* 1206 */         this.stream.writeShort(im.textGridWidth);
/* 1207 */         this.stream.writeShort(im.textGridHeight);
/* 1208 */         this.stream.write(im.characterCellWidth);
/* 1209 */         this.stream.write(im.characterCellHeight);
/* 1210 */         this.stream.write(im.textForegroundColor);
/* 1211 */         this.stream.write(im.textBackgroundColor);
/*      */         
/* 1213 */         writeBlocks(im.text);
/*      */         
/* 1215 */         this.stream.write(0);
/* 1216 */       } catch (IOException e) {
/* 1217 */         throw new IIOException("I/O error writing Plain Text Extension!", e);
/*      */       } 
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   private void writeApplicationExtension(GIFWritableImageMetadata im) throws IOException {
/* 1224 */     if (im.applicationIDs != null) {
/* 1225 */       Iterator<byte[]> iterIDs = im.applicationIDs.iterator();
/* 1226 */       Iterator<byte[]> iterCodes = im.authenticationCodes.iterator();
/* 1227 */       Iterator<byte[]> iterData = im.applicationData.iterator();
/*      */       
/* 1229 */       while (iterIDs.hasNext()) {
/*      */         try {
/* 1231 */           this.stream.write(33);
/* 1232 */           this.stream.write(255);
/*      */           
/* 1234 */           this.stream.write(11);
/* 1235 */           this.stream.write(iterIDs.next(), 0, 8);
/* 1236 */           this.stream.write(iterCodes.next(), 0, 3);
/*      */           
/* 1238 */           writeBlocks(iterData.next());
/*      */           
/* 1240 */           this.stream.write(0);
/* 1241 */         } catch (IOException e) {
/* 1242 */           throw new IIOException("I/O error writing Application Extension!", e);
/*      */         } 
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   private void writeCommentExtension(GIFWritableImageMetadata im) throws IOException {
/* 1250 */     if (im.comments != null) {
/*      */       try {
/* 1252 */         Iterator<byte[]> iter = im.comments.iterator();
/* 1253 */         while (iter.hasNext()) {
/* 1254 */           this.stream.write(33);
/* 1255 */           this.stream.write(254);
/* 1256 */           writeBlocks(iter.next());
/* 1257 */           this.stream.write(0);
/*      */         } 
/* 1259 */       } catch (IOException e) {
/* 1260 */         throw new IIOException("I/O error writing Comment Extension!", e);
/*      */       } 
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void writeImageDescriptor(int imageLeftPosition, int imageTopPosition, int imageWidth, int imageHeight, boolean interlaceFlag, boolean sortFlag, int bitsPerPixel, byte[] localColorTable) throws IOException {
/*      */     try {
/* 1276 */       this.stream.write(44);
/*      */       
/* 1278 */       this.stream.writeShort((short)imageLeftPosition);
/* 1279 */       this.stream.writeShort((short)imageTopPosition);
/* 1280 */       this.stream.writeShort((short)imageWidth);
/* 1281 */       this.stream.writeShort((short)imageHeight);
/*      */       
/* 1283 */       int packedFields = (localColorTable != null) ? 128 : 0;
/* 1284 */       if (interlaceFlag) {
/* 1285 */         packedFields |= 0x40;
/*      */       }
/* 1287 */       if (sortFlag) {
/* 1288 */         packedFields |= 0x8;
/*      */       }
/* 1290 */       packedFields |= bitsPerPixel - 1;
/* 1291 */       this.stream.write(packedFields);
/*      */       
/* 1293 */       if (localColorTable != null) {
/* 1294 */         this.stream.write(localColorTable);
/*      */       }
/* 1296 */     } catch (IOException e) {
/* 1297 */       throw new IIOException("I/O error writing Image Descriptor!", e);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void writeImageDescriptor(GIFWritableImageMetadata imageMetadata, int bitsPerPixel) throws IOException {
/* 1305 */     writeImageDescriptor(imageMetadata.imageLeftPosition, imageMetadata.imageTopPosition, imageMetadata.imageWidth, imageMetadata.imageHeight, imageMetadata.interlaceFlag, imageMetadata.sortFlag, bitsPerPixel, imageMetadata.localColorTable);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void writeTrailer() throws IOException {
/* 1316 */     this.stream.write(59);
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\github\jaiimageio\impl\plugins\gif\GIFImageWriter.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */