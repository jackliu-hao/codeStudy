/*      */ package com.github.jaiimageio.impl.plugins.bmp;
/*      */ 
/*      */ import com.github.jaiimageio.impl.common.ImageUtil;
/*      */ import java.awt.Point;
/*      */ import java.awt.Rectangle;
/*      */ import java.awt.color.ColorSpace;
/*      */ import java.awt.color.ICC_ColorSpace;
/*      */ import java.awt.color.ICC_Profile;
/*      */ import java.awt.image.BufferedImage;
/*      */ import java.awt.image.ColorModel;
/*      */ import java.awt.image.ComponentSampleModel;
/*      */ import java.awt.image.DataBufferByte;
/*      */ import java.awt.image.DataBufferInt;
/*      */ import java.awt.image.DataBufferUShort;
/*      */ import java.awt.image.DirectColorModel;
/*      */ import java.awt.image.IndexColorModel;
/*      */ import java.awt.image.MultiPixelPackedSampleModel;
/*      */ import java.awt.image.PixelInterleavedSampleModel;
/*      */ import java.awt.image.Raster;
/*      */ import java.awt.image.SampleModel;
/*      */ import java.awt.image.SinglePixelPackedSampleModel;
/*      */ import java.awt.image.WritableRaster;
/*      */ import java.io.ByteArrayInputStream;
/*      */ import java.io.IOException;
/*      */ import java.nio.ByteOrder;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Iterator;
/*      */ import javax.imageio.ImageIO;
/*      */ import javax.imageio.ImageReadParam;
/*      */ import javax.imageio.ImageReader;
/*      */ import javax.imageio.ImageTypeSpecifier;
/*      */ import javax.imageio.event.IIOReadProgressListener;
/*      */ import javax.imageio.event.IIOReadUpdateListener;
/*      */ import javax.imageio.event.IIOReadWarningListener;
/*      */ import javax.imageio.metadata.IIOMetadata;
/*      */ import javax.imageio.spi.ImageReaderSpi;
/*      */ import javax.imageio.stream.ImageInputStream;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class BMPImageReader
/*      */   extends ImageReader
/*      */   implements BMPConstants
/*      */ {
/*      */   private static final int VERSION_2_1_BIT = 0;
/*      */   private static final int VERSION_2_4_BIT = 1;
/*      */   private static final int VERSION_2_8_BIT = 2;
/*      */   private static final int VERSION_2_24_BIT = 3;
/*      */   private static final int VERSION_3_1_BIT = 4;
/*      */   private static final int VERSION_3_4_BIT = 5;
/*      */   private static final int VERSION_3_8_BIT = 6;
/*      */   private static final int VERSION_3_24_BIT = 7;
/*      */   private static final int VERSION_3_NT_16_BIT = 8;
/*      */   private static final int VERSION_3_NT_32_BIT = 9;
/*      */   private static final int VERSION_4_1_BIT = 10;
/*      */   private static final int VERSION_4_4_BIT = 11;
/*      */   private static final int VERSION_4_8_BIT = 12;
/*      */   private static final int VERSION_4_16_BIT = 13;
/*      */   private static final int VERSION_4_24_BIT = 14;
/*      */   private static final int VERSION_4_32_BIT = 15;
/*      */   private static final int VERSION_3_XP_EMBEDDED = 16;
/*      */   private static final int VERSION_4_XP_EMBEDDED = 17;
/*      */   private static final int VERSION_5_XP_EMBEDDED = 18;
/*      */   private long bitmapFileSize;
/*      */   private long bitmapOffset;
/*      */   private long compression;
/*      */   private long imageSize;
/*      */   private byte[] palette;
/*      */   private int imageType;
/*      */   private int numBands;
/*      */   private boolean isBottomUp;
/*      */   private int bitsPerPixel;
/*      */   private int redMask;
/*      */   private int greenMask;
/*      */   private int blueMask;
/*      */   private int alphaMask;
/*      */   private SampleModel sampleModel;
/*      */   private SampleModel originalSampleModel;
/*      */   private ColorModel colorModel;
/*      */   private ColorModel originalColorModel;
/*  137 */   private ImageInputStream iis = null;
/*      */ 
/*      */   
/*      */   private boolean gotHeader = false;
/*      */ 
/*      */   
/*      */   private long imageDataOffset;
/*      */ 
/*      */   
/*      */   private int width;
/*      */ 
/*      */   
/*      */   private int height;
/*      */ 
/*      */   
/*      */   private Rectangle destinationRegion;
/*      */ 
/*      */   
/*      */   private Rectangle sourceRegion;
/*      */ 
/*      */   
/*      */   private BMPMetadata metadata;
/*      */ 
/*      */   
/*      */   private BufferedImage bi;
/*      */ 
/*      */   
/*      */   private boolean noTransform = true;
/*      */ 
/*      */   
/*      */   private boolean seleBand = false;
/*      */ 
/*      */   
/*      */   private int scaleX;
/*      */ 
/*      */   
/*      */   private int scaleY;
/*      */   
/*      */   private int[] sourceBands;
/*      */   
/*      */   private int[] destBands;
/*      */ 
/*      */   
/*      */   public BMPImageReader(ImageReaderSpi originator) {
/*  181 */     super(originator);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setInput(Object input, boolean seekForwardOnly, boolean ignoreMetadata) {
/*  188 */     super.setInput(input, seekForwardOnly, ignoreMetadata);
/*  189 */     this.iis = (ImageInputStream)input;
/*  190 */     if (this.iis != null)
/*  191 */       this.iis.setByteOrder(ByteOrder.LITTLE_ENDIAN); 
/*  192 */     resetHeaderInfo();
/*      */   }
/*      */ 
/*      */   
/*      */   public int getNumImages(boolean allowSearch) throws IOException {
/*  197 */     if (this.iis == null) {
/*  198 */       throw new IllegalStateException(I18N.getString("GetNumImages0"));
/*      */     }
/*  200 */     if (this.seekForwardOnly && allowSearch) {
/*  201 */       throw new IllegalStateException(I18N.getString("GetNumImages1"));
/*      */     }
/*  203 */     return 1;
/*      */   }
/*      */   
/*      */   public int getWidth(int imageIndex) throws IOException {
/*  207 */     checkIndex(imageIndex);
/*  208 */     readHeader();
/*  209 */     return this.width;
/*      */   }
/*      */   
/*      */   public int getHeight(int imageIndex) throws IOException {
/*  213 */     checkIndex(imageIndex);
/*  214 */     readHeader();
/*  215 */     return this.height;
/*      */   }
/*      */   
/*      */   private void checkIndex(int imageIndex) {
/*  219 */     if (imageIndex != 0) {
/*  220 */       throw new IndexOutOfBoundsException(I18N.getString("BMPImageReader0"));
/*      */     }
/*      */   }
/*      */   
/*      */   public void readHeader() throws IOException {
/*  225 */     if (this.gotHeader) {
/*      */ 
/*      */       
/*  228 */       this.iis.seek(this.imageDataOffset);
/*      */       
/*      */       return;
/*      */     } 
/*  232 */     if (this.iis == null) {
/*  233 */       throw new IllegalStateException(I18N.getString("BMPImageReader5"));
/*      */     }
/*  235 */     int profileData = 0, profileSize = 0;
/*      */     
/*  237 */     this.metadata = new BMPMetadata();
/*  238 */     this.iis.mark();
/*      */ 
/*      */     
/*  241 */     byte[] marker = new byte[2];
/*  242 */     this.iis.read(marker);
/*  243 */     if (marker[0] != 66 || marker[1] != 77) {
/*  244 */       throw new IllegalArgumentException(I18N.getString("BMPImageReader1"));
/*      */     }
/*      */     
/*  247 */     this.bitmapFileSize = this.iis.readUnsignedInt();
/*      */     
/*  249 */     this.iis.skipBytes(4);
/*      */ 
/*      */     
/*  252 */     this.bitmapOffset = this.iis.readUnsignedInt();
/*      */ 
/*      */ 
/*      */     
/*  256 */     long size = this.iis.readUnsignedInt();
/*      */     
/*  258 */     if (size == 12L) {
/*  259 */       this.width = this.iis.readShort();
/*  260 */       this.height = this.iis.readShort();
/*      */     } else {
/*  262 */       this.width = this.iis.readInt();
/*  263 */       this.height = this.iis.readInt();
/*      */     } 
/*      */     
/*  266 */     this.metadata.width = this.width;
/*  267 */     this.metadata.height = this.height;
/*      */     
/*  269 */     int planes = this.iis.readUnsignedShort();
/*  270 */     this.bitsPerPixel = this.iis.readUnsignedShort();
/*      */ 
/*      */     
/*  273 */     this.metadata.bitsPerPixel = (short)this.bitsPerPixel;
/*      */ 
/*      */ 
/*      */     
/*  277 */     this.numBands = 3;
/*      */     
/*  279 */     if (size == 12L) {
/*      */       
/*  281 */       this.metadata.bmpVersion = "BMP v. 2.x";
/*      */ 
/*      */       
/*  284 */       if (this.bitsPerPixel == 1) {
/*  285 */         this.imageType = 0;
/*  286 */       } else if (this.bitsPerPixel == 4) {
/*  287 */         this.imageType = 1;
/*  288 */       } else if (this.bitsPerPixel == 8) {
/*  289 */         this.imageType = 2;
/*  290 */       } else if (this.bitsPerPixel == 24) {
/*  291 */         this.imageType = 3;
/*      */       } 
/*      */ 
/*      */       
/*  295 */       int numberOfEntries = (int)((this.bitmapOffset - 14L - size) / 3L);
/*  296 */       int sizeOfPalette = numberOfEntries * 3;
/*  297 */       this.palette = new byte[sizeOfPalette];
/*  298 */       this.iis.readFully(this.palette, 0, sizeOfPalette);
/*  299 */       this.metadata.palette = this.palette;
/*  300 */       this.metadata.paletteSize = numberOfEntries;
/*      */     } else {
/*  302 */       this.compression = this.iis.readUnsignedInt();
/*  303 */       this.imageSize = this.iis.readUnsignedInt();
/*  304 */       long xPelsPerMeter = this.iis.readInt();
/*  305 */       long yPelsPerMeter = this.iis.readInt();
/*  306 */       long colorsUsed = this.iis.readUnsignedInt();
/*  307 */       long colorsImportant = this.iis.readUnsignedInt();
/*      */       
/*  309 */       this.metadata.compression = (int)this.compression;
/*  310 */       this.metadata.imageSize = (int)this.imageSize;
/*  311 */       this.metadata.xPixelsPerMeter = (int)xPelsPerMeter;
/*  312 */       this.metadata.yPixelsPerMeter = (int)yPelsPerMeter;
/*  313 */       this.metadata.colorsUsed = (int)colorsUsed;
/*  314 */       this.metadata.colorsImportant = (int)colorsImportant;
/*      */       
/*  316 */       if (size == 40L) {
/*      */         int numberOfEntries; int sizeOfPalette;
/*  318 */         switch ((int)this.compression) {
/*      */           
/*      */           case 4:
/*      */           case 5:
/*  322 */             this.metadata.bmpVersion = "BMP v. 3.x";
/*  323 */             this.imageType = 16;
/*      */             break;
/*      */ 
/*      */ 
/*      */           
/*      */           case 0:
/*      */           case 1:
/*      */           case 2:
/*  331 */             numberOfEntries = (int)((this.bitmapOffset - 14L - size) / 4L);
/*  332 */             sizeOfPalette = numberOfEntries * 4;
/*  333 */             this.palette = new byte[sizeOfPalette];
/*  334 */             this.iis.readFully(this.palette, 0, sizeOfPalette);
/*      */             
/*  336 */             this.metadata.palette = this.palette;
/*  337 */             this.metadata.paletteSize = numberOfEntries;
/*      */             
/*  339 */             if (this.bitsPerPixel == 1) {
/*  340 */               this.imageType = 4;
/*  341 */             } else if (this.bitsPerPixel == 4) {
/*  342 */               this.imageType = 5;
/*  343 */             } else if (this.bitsPerPixel == 8) {
/*  344 */               this.imageType = 6;
/*  345 */             } else if (this.bitsPerPixel == 24) {
/*  346 */               this.imageType = 7;
/*  347 */             } else if (this.bitsPerPixel == 16) {
/*  348 */               this.imageType = 8;
/*      */               
/*  350 */               this.redMask = 31744;
/*  351 */               this.greenMask = 992;
/*  352 */               this.blueMask = 31;
/*  353 */               this.metadata.redMask = this.redMask;
/*  354 */               this.metadata.greenMask = this.greenMask;
/*  355 */               this.metadata.blueMask = this.blueMask;
/*  356 */             } else if (this.bitsPerPixel == 32) {
/*  357 */               this.imageType = 9;
/*  358 */               this.redMask = 16711680;
/*  359 */               this.greenMask = 65280;
/*  360 */               this.blueMask = 255;
/*  361 */               this.metadata.redMask = this.redMask;
/*  362 */               this.metadata.greenMask = this.greenMask;
/*  363 */               this.metadata.blueMask = this.blueMask;
/*      */             } 
/*      */             
/*  366 */             this.metadata.bmpVersion = "BMP v. 3.x";
/*      */             break;
/*      */ 
/*      */           
/*      */           case 3:
/*  371 */             if (this.bitsPerPixel == 16) {
/*  372 */               this.imageType = 8;
/*  373 */             } else if (this.bitsPerPixel == 32) {
/*  374 */               this.imageType = 9;
/*      */             } 
/*      */ 
/*      */             
/*  378 */             this.redMask = (int)this.iis.readUnsignedInt();
/*  379 */             this.greenMask = (int)this.iis.readUnsignedInt();
/*  380 */             this.blueMask = (int)this.iis.readUnsignedInt();
/*  381 */             this.metadata.redMask = this.redMask;
/*  382 */             this.metadata.greenMask = this.greenMask;
/*  383 */             this.metadata.blueMask = this.blueMask;
/*      */             
/*  385 */             if (colorsUsed != 0L) {
/*      */               
/*  387 */               sizeOfPalette = (int)colorsUsed * 4;
/*  388 */               this.palette = new byte[sizeOfPalette];
/*  389 */               this.iis.readFully(this.palette, 0, sizeOfPalette);
/*  390 */               this.metadata.palette = this.palette;
/*  391 */               this.metadata.paletteSize = (int)colorsUsed;
/*      */             } 
/*  393 */             this.metadata.bmpVersion = "BMP v. 3.x NT";
/*      */             break;
/*      */           
/*      */           default:
/*  397 */             throw new RuntimeException(
/*  398 */                 I18N.getString("BMPImageReader2"));
/*      */         } 
/*  400 */       } else if (size == 108L || size == 124L) {
/*      */         
/*  402 */         if (size == 108L) {
/*  403 */           this.metadata.bmpVersion = "BMP v. 4.x";
/*  404 */         } else if (size == 124L) {
/*  405 */           this.metadata.bmpVersion = "BMP v. 5.x";
/*      */         } 
/*      */         
/*  408 */         this.redMask = (int)this.iis.readUnsignedInt();
/*  409 */         this.greenMask = (int)this.iis.readUnsignedInt();
/*  410 */         this.blueMask = (int)this.iis.readUnsignedInt();
/*      */         
/*  412 */         this.alphaMask = (int)this.iis.readUnsignedInt();
/*  413 */         long csType = this.iis.readUnsignedInt();
/*  414 */         int redX = this.iis.readInt();
/*  415 */         int redY = this.iis.readInt();
/*  416 */         int redZ = this.iis.readInt();
/*  417 */         int greenX = this.iis.readInt();
/*  418 */         int greenY = this.iis.readInt();
/*  419 */         int greenZ = this.iis.readInt();
/*  420 */         int blueX = this.iis.readInt();
/*  421 */         int blueY = this.iis.readInt();
/*  422 */         int blueZ = this.iis.readInt();
/*  423 */         long gammaRed = this.iis.readUnsignedInt();
/*  424 */         long gammaGreen = this.iis.readUnsignedInt();
/*  425 */         long gammaBlue = this.iis.readUnsignedInt();
/*      */         
/*  427 */         if (size == 124L) {
/*  428 */           this.metadata.intent = this.iis.readInt();
/*  429 */           profileData = this.iis.readInt();
/*  430 */           profileSize = this.iis.readInt();
/*  431 */           this.iis.skipBytes(4);
/*      */         } 
/*      */         
/*  434 */         this.metadata.colorSpace = (int)csType;
/*      */         
/*  436 */         if (csType == 0L) {
/*      */           
/*  438 */           this.metadata.redX = redX;
/*  439 */           this.metadata.redY = redY;
/*  440 */           this.metadata.redZ = redZ;
/*  441 */           this.metadata.greenX = greenX;
/*  442 */           this.metadata.greenY = greenY;
/*  443 */           this.metadata.greenZ = greenZ;
/*  444 */           this.metadata.blueX = blueX;
/*  445 */           this.metadata.blueY = blueY;
/*  446 */           this.metadata.blueZ = blueZ;
/*  447 */           this.metadata.gammaRed = (int)gammaRed;
/*  448 */           this.metadata.gammaGreen = (int)gammaGreen;
/*  449 */           this.metadata.gammaBlue = (int)gammaBlue;
/*      */         } 
/*      */ 
/*      */         
/*  453 */         int numberOfEntries = (int)((this.bitmapOffset - 14L - size) / 4L);
/*  454 */         int sizeOfPalette = numberOfEntries * 4;
/*  455 */         this.palette = new byte[sizeOfPalette];
/*  456 */         this.iis.readFully(this.palette, 0, sizeOfPalette);
/*  457 */         this.metadata.palette = this.palette;
/*  458 */         this.metadata.paletteSize = numberOfEntries;
/*      */         
/*  460 */         switch ((int)this.compression) {
/*      */           case 4:
/*      */           case 5:
/*  463 */             if (size == 108L) {
/*  464 */               this.imageType = 17; break;
/*  465 */             }  if (size == 124L) {
/*  466 */               this.imageType = 18;
/*      */             }
/*      */             break;
/*      */           default:
/*  470 */             if (this.bitsPerPixel == 1) {
/*  471 */               this.imageType = 10;
/*  472 */             } else if (this.bitsPerPixel == 4) {
/*  473 */               this.imageType = 11;
/*  474 */             } else if (this.bitsPerPixel == 8) {
/*  475 */               this.imageType = 12;
/*  476 */             } else if (this.bitsPerPixel == 16) {
/*  477 */               this.imageType = 13;
/*  478 */               if ((int)this.compression == 0) {
/*  479 */                 this.redMask = 31744;
/*  480 */                 this.greenMask = 992;
/*  481 */                 this.blueMask = 31;
/*      */               } 
/*  483 */             } else if (this.bitsPerPixel == 24) {
/*  484 */               this.imageType = 14;
/*  485 */             } else if (this.bitsPerPixel == 32) {
/*  486 */               this.imageType = 15;
/*  487 */               if ((int)this.compression == 0) {
/*  488 */                 this.redMask = 16711680;
/*  489 */                 this.greenMask = 65280;
/*  490 */                 this.blueMask = 255;
/*      */               } 
/*      */             } 
/*      */             
/*  494 */             this.metadata.redMask = this.redMask;
/*  495 */             this.metadata.greenMask = this.greenMask;
/*  496 */             this.metadata.blueMask = this.blueMask;
/*  497 */             this.metadata.alphaMask = this.alphaMask; break;
/*      */         } 
/*      */       } else {
/*  500 */         throw new RuntimeException(
/*  501 */             I18N.getString("BMPImageReader3"));
/*      */       } 
/*      */     } 
/*      */     
/*  505 */     if (this.height > 0) {
/*      */       
/*  507 */       this.isBottomUp = true;
/*      */     } else {
/*      */       
/*  510 */       this.isBottomUp = false;
/*  511 */       this.height = Math.abs(this.height);
/*      */     } 
/*      */ 
/*      */ 
/*      */     
/*  516 */     ColorSpace colorSpace = ColorSpace.getInstance(1000);
/*  517 */     if (this.metadata.colorSpace == 3 || this.metadata.colorSpace == 4) {
/*      */ 
/*      */       
/*  520 */       this.iis.mark();
/*  521 */       this.iis.skipBytes(profileData - size);
/*  522 */       byte[] profile = new byte[profileSize];
/*  523 */       this.iis.readFully(profile, 0, profileSize);
/*  524 */       this.iis.reset();
/*      */       
/*      */       try {
/*  527 */         if (this.metadata.colorSpace == 3)
/*      */         
/*  529 */         { colorSpace = new ICC_ColorSpace(ICC_Profile.getInstance(new String(profile))); }
/*      */         else
/*      */         
/*  532 */         { colorSpace = new ICC_ColorSpace(ICC_Profile.getInstance(profile)); } 
/*  533 */       } catch (Exception e) {
/*  534 */         colorSpace = ColorSpace.getInstance(1000);
/*      */       } 
/*      */     } 
/*      */     
/*  538 */     if (this.bitsPerPixel == 0 || this.compression == 4L || this.compression == 5L) {
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  543 */       this.colorModel = null;
/*  544 */       this.sampleModel = null;
/*  545 */     } else if (this.bitsPerPixel == 1 || this.bitsPerPixel == 4 || this.bitsPerPixel == 8) {
/*      */       byte[] r, g, b;
/*  547 */       this.numBands = 1;
/*      */       
/*  549 */       if (this.bitsPerPixel == 8) {
/*  550 */         int[] bandOffsets = new int[this.numBands];
/*  551 */         for (int i = 0; i < this.numBands; i++) {
/*  552 */           bandOffsets[i] = this.numBands - 1 - i;
/*      */         }
/*  554 */         this.sampleModel = new PixelInterleavedSampleModel(0, this.width, this.height, this.numBands, this.numBands * this.width, bandOffsets);
/*      */ 
/*      */       
/*      */       }
/*      */       else {
/*      */ 
/*      */ 
/*      */         
/*  562 */         this.sampleModel = new MultiPixelPackedSampleModel(0, this.width, this.height, this.bitsPerPixel);
/*      */       } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  570 */       if (this.imageType == 0 || this.imageType == 1 || this.imageType == 2) {
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  575 */         size = (this.palette.length / 3);
/*      */         
/*  577 */         if (size > 256L) {
/*  578 */           size = 256L;
/*      */         }
/*      */ 
/*      */         
/*  582 */         r = new byte[(int)size];
/*  583 */         g = new byte[(int)size];
/*  584 */         b = new byte[(int)size];
/*  585 */         for (int i = 0; i < (int)size; i++) {
/*  586 */           int off = 3 * i;
/*  587 */           b[i] = this.palette[off];
/*  588 */           g[i] = this.palette[off + 1];
/*  589 */           r[i] = this.palette[off + 2];
/*      */         } 
/*      */       } else {
/*  592 */         size = (this.palette.length / 4);
/*      */         
/*  594 */         if (size > 256L) {
/*  595 */           size = 256L;
/*      */         }
/*      */ 
/*      */         
/*  599 */         r = new byte[(int)size];
/*  600 */         g = new byte[(int)size];
/*  601 */         b = new byte[(int)size];
/*  602 */         for (int i = 0; i < size; i++) {
/*  603 */           int off = 4 * i;
/*  604 */           b[i] = this.palette[off];
/*  605 */           g[i] = this.palette[off + 1];
/*  606 */           r[i] = this.palette[off + 2];
/*      */         } 
/*      */       } 
/*      */       
/*  610 */       if (ImageUtil.isIndicesForGrayscale(r, g, b))
/*  611 */       { this
/*  612 */           .colorModel = ImageUtil.createColorModel(null, this.sampleModel); }
/*      */       else
/*  614 */       { this.colorModel = new IndexColorModel(this.bitsPerPixel, (int)size, r, g, b); } 
/*  615 */     } else if (this.bitsPerPixel == 16) {
/*  616 */       this.numBands = 3;
/*  617 */       this.sampleModel = new SinglePixelPackedSampleModel(1, this.width, this.height, new int[] { this.redMask, this.greenMask, this.blueMask });
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  622 */       this.colorModel = new DirectColorModel(colorSpace, 16, this.redMask, this.greenMask, this.blueMask, 0, false, 1);
/*      */ 
/*      */ 
/*      */     
/*      */     }
/*  627 */     else if (this.bitsPerPixel == 32) {
/*  628 */       this.numBands = (this.alphaMask == 0) ? 3 : 4;
/*      */       
/*  630 */       if (this.redMask == 0 || this.greenMask == 0 || this.blueMask == 0) {
/*  631 */         this.redMask = 16711680;
/*  632 */         this.greenMask = 65280;
/*  633 */         this.blueMask = 255;
/*  634 */         this.alphaMask = -16777216;
/*      */       } 
/*      */ 
/*      */ 
/*      */       
/*  639 */       (new int[3])[0] = this.redMask; (new int[3])[1] = this.greenMask; (new int[3])[2] = this.blueMask; (new int[4])[0] = this.redMask; (new int[4])[1] = this.greenMask; (new int[4])[2] = this.blueMask; (new int[4])[3] = this.alphaMask; int[] bitMasks = (this.numBands == 3) ? new int[3] : new int[4];
/*      */ 
/*      */ 
/*      */       
/*  643 */       this.sampleModel = new SinglePixelPackedSampleModel(3, this.width, this.height, bitMasks);
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  648 */       this.colorModel = new DirectColorModel(colorSpace, 32, this.redMask, this.greenMask, this.blueMask, this.alphaMask, false, 3);
/*      */     
/*      */     }
/*      */     else {
/*      */       
/*  653 */       this.numBands = 3;
/*      */       
/*  655 */       int[] bandOffsets = new int[this.numBands];
/*  656 */       for (int i = 0; i < this.numBands; i++) {
/*  657 */         bandOffsets[i] = this.numBands - 1 - i;
/*      */       }
/*      */       
/*  660 */       this.sampleModel = new PixelInterleavedSampleModel(0, this.width, this.height, this.numBands, this.numBands * this.width, bandOffsets);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  667 */       this
/*  668 */         .colorModel = ImageUtil.createColorModel(colorSpace, this.sampleModel);
/*      */     } 
/*      */     
/*  671 */     this.originalSampleModel = this.sampleModel;
/*  672 */     this.originalColorModel = this.colorModel;
/*      */ 
/*      */ 
/*      */     
/*  676 */     this.iis.reset();
/*  677 */     this.iis.skipBytes(this.bitmapOffset);
/*  678 */     this.gotHeader = true;
/*      */ 
/*      */     
/*  681 */     this.imageDataOffset = this.iis.getStreamPosition();
/*      */   }
/*      */ 
/*      */   
/*      */   public Iterator getImageTypes(int imageIndex) throws IOException {
/*  686 */     checkIndex(imageIndex);
/*  687 */     readHeader();
/*  688 */     ArrayList<ImageTypeSpecifier> list = new ArrayList(1);
/*  689 */     list.add(new ImageTypeSpecifier(this.originalColorModel, this.originalSampleModel));
/*      */     
/*  691 */     return list.iterator();
/*      */   }
/*      */   
/*      */   public ImageReadParam getDefaultReadParam() {
/*  695 */     return new ImageReadParam();
/*      */   }
/*      */ 
/*      */   
/*      */   public IIOMetadata getImageMetadata(int imageIndex) throws IOException {
/*  700 */     checkIndex(imageIndex);
/*  701 */     if (this.metadata == null) {
/*  702 */       readHeader();
/*      */     }
/*  704 */     return this.metadata;
/*      */   }
/*      */   
/*      */   public IIOMetadata getStreamMetadata() throws IOException {
/*  708 */     return null;
/*      */   }
/*      */   
/*      */   public boolean isRandomAccessEasy(int imageIndex) throws IOException {
/*  712 */     checkIndex(imageIndex);
/*  713 */     readHeader();
/*  714 */     return (this.metadata.compression == 0);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public BufferedImage read(int imageIndex, ImageReadParam param) throws IOException {
/*  720 */     if (this.iis == null) {
/*  721 */       throw new IllegalStateException(I18N.getString("BMPImageReader5"));
/*      */     }
/*      */     
/*  724 */     checkIndex(imageIndex);
/*  725 */     clearAbortRequest();
/*  726 */     processImageStarted(imageIndex);
/*      */     
/*  728 */     if (param == null) {
/*  729 */       param = getDefaultReadParam();
/*      */     }
/*      */     
/*  732 */     readHeader();
/*      */     
/*  734 */     this.sourceRegion = new Rectangle(0, 0, 0, 0);
/*  735 */     this.destinationRegion = new Rectangle(0, 0, 0, 0);
/*      */     
/*  737 */     computeRegions(param, this.width, this.height, param
/*  738 */         .getDestination(), this.sourceRegion, this.destinationRegion);
/*      */ 
/*      */ 
/*      */     
/*  742 */     this.scaleX = param.getSourceXSubsampling();
/*  743 */     this.scaleY = param.getSourceYSubsampling();
/*      */ 
/*      */     
/*  746 */     this.sourceBands = param.getSourceBands();
/*  747 */     this.destBands = param.getDestinationBands();
/*      */     
/*  749 */     this.seleBand = (this.sourceBands != null && this.destBands != null);
/*  750 */     this
/*  751 */       .noTransform = (this.destinationRegion.equals(new Rectangle(0, 0, this.width, this.height)) || this.seleBand);
/*      */ 
/*      */     
/*  754 */     if (!this.seleBand) {
/*  755 */       this.sourceBands = new int[this.numBands];
/*  756 */       this.destBands = new int[this.numBands];
/*  757 */       for (int i = 0; i < this.numBands; i++) {
/*  758 */         this.sourceBands[i] = i; this.destBands[i] = i;
/*      */       } 
/*      */     } 
/*      */     
/*  762 */     this.bi = param.getDestination();
/*      */ 
/*      */     
/*  765 */     WritableRaster raster = null;
/*      */     
/*  767 */     if (this.bi == null) {
/*  768 */       if (this.sampleModel != null && this.colorModel != null) {
/*  769 */         this
/*  770 */           .sampleModel = this.sampleModel.createCompatibleSampleModel(this.destinationRegion.x + this.destinationRegion.width, this.destinationRegion.y + this.destinationRegion.height);
/*      */ 
/*      */ 
/*      */         
/*  774 */         if (this.seleBand)
/*  775 */           this.sampleModel = this.sampleModel.createSubsetSampleModel(this.sourceBands); 
/*  776 */         raster = Raster.createWritableRaster(this.sampleModel, new Point());
/*  777 */         this.bi = new BufferedImage(this.colorModel, raster, false, null);
/*      */       } 
/*      */     } else {
/*  780 */       raster = this.bi.getWritableTile(0, 0);
/*  781 */       this.sampleModel = this.bi.getSampleModel();
/*  782 */       this.colorModel = this.bi.getColorModel();
/*      */       
/*  784 */       this.noTransform &= this.destinationRegion.equals(raster.getBounds());
/*      */     } 
/*      */     
/*  787 */     byte[] bdata = null;
/*  788 */     short[] sdata = null;
/*  789 */     int[] idata = null;
/*      */ 
/*      */     
/*  792 */     if (this.sampleModel != null) {
/*  793 */       if (this.sampleModel.getDataType() == 0) {
/*      */         
/*  795 */         bdata = ((DataBufferByte)raster.getDataBuffer()).getData();
/*  796 */       } else if (this.sampleModel.getDataType() == 1) {
/*      */         
/*  798 */         sdata = ((DataBufferUShort)raster.getDataBuffer()).getData();
/*  799 */       } else if (this.sampleModel.getDataType() == 3) {
/*      */         
/*  801 */         idata = ((DataBufferInt)raster.getDataBuffer()).getData();
/*      */       } 
/*      */     }
/*      */     
/*  805 */     switch (this.imageType) {
/*      */ 
/*      */       
/*      */       case 0:
/*  809 */         read1Bit(bdata);
/*      */         break;
/*      */ 
/*      */       
/*      */       case 1:
/*  814 */         read4Bit(bdata);
/*      */         break;
/*      */ 
/*      */       
/*      */       case 2:
/*  819 */         read8Bit(bdata);
/*      */         break;
/*      */ 
/*      */       
/*      */       case 3:
/*  824 */         read24Bit(bdata);
/*      */         break;
/*      */ 
/*      */       
/*      */       case 4:
/*  829 */         read1Bit(bdata);
/*      */         break;
/*      */       
/*      */       case 5:
/*  833 */         switch ((int)this.compression) {
/*      */           case 0:
/*  835 */             read4Bit(bdata);
/*      */             break;
/*      */           
/*      */           case 2:
/*  839 */             readRLE4(bdata);
/*      */             break;
/*      */         } 
/*      */         
/*  843 */         throw new RuntimeException(
/*  844 */             I18N.getString("BMPImageReader1"));
/*      */ 
/*      */ 
/*      */       
/*      */       case 6:
/*  849 */         switch ((int)this.compression) {
/*      */           case 0:
/*  851 */             read8Bit(bdata);
/*      */             break;
/*      */           
/*      */           case 1:
/*  855 */             readRLE8(bdata);
/*      */             break;
/*      */         } 
/*      */         
/*  859 */         throw new RuntimeException(
/*  860 */             I18N.getString("BMPImageReader1"));
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       case 7:
/*  867 */         read24Bit(bdata);
/*      */         break;
/*      */       
/*      */       case 8:
/*  871 */         read16Bit(sdata);
/*      */         break;
/*      */       
/*      */       case 9:
/*  875 */         read32Bit(idata);
/*      */         break;
/*      */       
/*      */       case 16:
/*      */       case 17:
/*      */       case 18:
/*  881 */         this.bi = readEmbedded((int)this.compression, this.bi, param);
/*      */         break;
/*      */       
/*      */       case 10:
/*  885 */         read1Bit(bdata);
/*      */         break;
/*      */       
/*      */       case 11:
/*  889 */         switch ((int)this.compression) {
/*      */           
/*      */           case 0:
/*  892 */             read4Bit(bdata);
/*      */             break;
/*      */           
/*      */           case 2:
/*  896 */             readRLE4(bdata);
/*      */             break;
/*      */           
/*      */           default:
/*  900 */             throw new RuntimeException(
/*  901 */                 I18N.getString("BMPImageReader1"));
/*      */         } 
/*      */       
/*      */       case 12:
/*  905 */         switch ((int)this.compression) {
/*      */           
/*      */           case 0:
/*  908 */             read8Bit(bdata);
/*      */             break;
/*      */           
/*      */           case 1:
/*  912 */             readRLE8(bdata);
/*      */             break;
/*      */         } 
/*      */         
/*  916 */         throw new RuntimeException(
/*  917 */             I18N.getString("BMPImageReader1"));
/*      */ 
/*      */ 
/*      */       
/*      */       case 13:
/*  922 */         read16Bit(sdata);
/*      */         break;
/*      */       
/*      */       case 14:
/*  926 */         read24Bit(bdata);
/*      */         break;
/*      */       
/*      */       case 15:
/*  930 */         read32Bit(idata);
/*      */         break;
/*      */     } 
/*      */     
/*  934 */     if (abortRequested()) {
/*  935 */       processReadAborted();
/*      */     } else {
/*  937 */       processImageComplete();
/*      */     } 
/*  939 */     return this.bi;
/*      */   }
/*      */   
/*      */   public boolean canReadRaster() {
/*  943 */     return true;
/*      */   }
/*      */ 
/*      */   
/*      */   public Raster readRaster(int imageIndex, ImageReadParam param) throws IOException {
/*  948 */     BufferedImage bi = read(imageIndex, param);
/*  949 */     return bi.getData();
/*      */   }
/*      */   
/*      */   private void resetHeaderInfo() {
/*  953 */     this.gotHeader = false;
/*  954 */     this.bi = null;
/*  955 */     this.sampleModel = this.originalSampleModel = null;
/*  956 */     this.colorModel = this.originalColorModel = null;
/*      */   }
/*      */   
/*      */   public void reset() {
/*  960 */     super.reset();
/*  961 */     this.iis = null;
/*  962 */     resetHeaderInfo();
/*      */   }
/*      */ 
/*      */   
/*      */   private void read1Bit(byte[] bdata) throws IOException {
/*  967 */     int bytesPerScanline = (this.width + 7) / 8;
/*  968 */     int padding = bytesPerScanline % 4;
/*  969 */     if (padding != 0) {
/*  970 */       padding = 4 - padding;
/*      */     }
/*      */     
/*  973 */     int lineLength = bytesPerScanline + padding;
/*      */     
/*  975 */     if (this.noTransform) {
/*  976 */       int j = this.isBottomUp ? ((this.height - 1) * bytesPerScanline) : 0;
/*      */       
/*  978 */       for (int i = 0; i < this.height && 
/*  979 */         !abortRequested(); i++) {
/*      */ 
/*      */         
/*  982 */         this.iis.readFully(bdata, j, bytesPerScanline);
/*  983 */         this.iis.skipBytes(padding);
/*  984 */         j += this.isBottomUp ? -bytesPerScanline : bytesPerScanline;
/*  985 */         processImageUpdate(this.bi, 0, i, this.destinationRegion.width, 1, 1, 1, new int[] { 0 });
/*      */ 
/*      */         
/*  988 */         processImageProgress(100.0F * i / this.destinationRegion.height);
/*      */       } 
/*      */     } else {
/*  991 */       byte[] buf = new byte[lineLength];
/*      */       
/*  993 */       int lineStride = ((MultiPixelPackedSampleModel)this.sampleModel).getScanlineStride();
/*      */       
/*  995 */       if (this.isBottomUp) {
/*  996 */         int lastLine = this.sourceRegion.y + (this.destinationRegion.height - 1) * this.scaleY;
/*      */         
/*  998 */         this.iis.skipBytes(lineLength * (this.height - 1 - lastLine));
/*      */       } else {
/* 1000 */         this.iis.skipBytes(lineLength * this.sourceRegion.y);
/*      */       } 
/* 1002 */       int skipLength = lineLength * (this.scaleY - 1);
/*      */ 
/*      */       
/* 1005 */       int[] srcOff = new int[this.destinationRegion.width];
/* 1006 */       int[] destOff = new int[this.destinationRegion.width];
/* 1007 */       int[] srcPos = new int[this.destinationRegion.width];
/* 1008 */       int[] destPos = new int[this.destinationRegion.width];
/*      */       
/* 1010 */       int i = this.destinationRegion.x, x = this.sourceRegion.x, m = 0;
/* 1011 */       for (; i < this.destinationRegion.x + this.destinationRegion.width; 
/* 1012 */         i++, m++, x += this.scaleX) {
/* 1013 */         srcPos[m] = x >> 3;
/* 1014 */         srcOff[m] = 7 - (x & 0x7);
/* 1015 */         destPos[m] = i >> 3;
/* 1016 */         destOff[m] = 7 - (i & 0x7);
/*      */       } 
/*      */       
/* 1019 */       int k = this.destinationRegion.y * lineStride;
/* 1020 */       if (this.isBottomUp) {
/* 1021 */         k += (this.destinationRegion.height - 1) * lineStride;
/*      */       }
/* 1023 */       int j = 0, y = this.sourceRegion.y;
/* 1024 */       for (; j < this.destinationRegion.height; j++, y += this.scaleY) {
/*      */         
/* 1026 */         if (abortRequested())
/*      */           break; 
/* 1028 */         this.iis.read(buf, 0, lineLength);
/* 1029 */         for (int n = 0; n < this.destinationRegion.width; n++) {
/*      */           
/* 1031 */           int v = buf[srcPos[n]] >> srcOff[n] & 0x1;
/* 1032 */           bdata[k + destPos[n]] = (byte)(bdata[k + destPos[n]] | v << destOff[n]);
/*      */         } 
/*      */         
/* 1035 */         k += this.isBottomUp ? -lineStride : lineStride;
/* 1036 */         this.iis.skipBytes(skipLength);
/* 1037 */         processImageUpdate(this.bi, 0, j, this.destinationRegion.width, 1, 1, 1, new int[] { 0 });
/*      */ 
/*      */         
/* 1040 */         processImageProgress(100.0F * j / this.destinationRegion.height);
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private void read4Bit(byte[] bdata) throws IOException {
/* 1048 */     int bytesPerScanline = (this.width + 1) / 2;
/*      */ 
/*      */     
/* 1051 */     int padding = bytesPerScanline % 4;
/* 1052 */     if (padding != 0) {
/* 1053 */       padding = 4 - padding;
/*      */     }
/* 1055 */     int lineLength = bytesPerScanline + padding;
/*      */     
/* 1057 */     if (this.noTransform) {
/* 1058 */       int j = this.isBottomUp ? ((this.height - 1) * bytesPerScanline) : 0;
/*      */       
/* 1060 */       for (int i = 0; i < this.height && 
/* 1061 */         !abortRequested(); i++) {
/*      */ 
/*      */         
/* 1064 */         this.iis.readFully(bdata, j, bytesPerScanline);
/* 1065 */         this.iis.skipBytes(padding);
/* 1066 */         j += this.isBottomUp ? -bytesPerScanline : bytesPerScanline;
/* 1067 */         processImageUpdate(this.bi, 0, i, this.destinationRegion.width, 1, 1, 1, new int[] { 0 });
/*      */ 
/*      */         
/* 1070 */         processImageProgress(100.0F * i / this.destinationRegion.height);
/*      */       } 
/*      */     } else {
/* 1073 */       byte[] buf = new byte[lineLength];
/*      */       
/* 1075 */       int lineStride = ((MultiPixelPackedSampleModel)this.sampleModel).getScanlineStride();
/*      */       
/* 1077 */       if (this.isBottomUp) {
/* 1078 */         int lastLine = this.sourceRegion.y + (this.destinationRegion.height - 1) * this.scaleY;
/*      */         
/* 1080 */         this.iis.skipBytes(lineLength * (this.height - 1 - lastLine));
/*      */       } else {
/* 1082 */         this.iis.skipBytes(lineLength * this.sourceRegion.y);
/*      */       } 
/* 1084 */       int skipLength = lineLength * (this.scaleY - 1);
/*      */ 
/*      */       
/* 1087 */       int[] srcOff = new int[this.destinationRegion.width];
/* 1088 */       int[] destOff = new int[this.destinationRegion.width];
/* 1089 */       int[] srcPos = new int[this.destinationRegion.width];
/* 1090 */       int[] destPos = new int[this.destinationRegion.width];
/*      */       
/* 1092 */       int i = this.destinationRegion.x, x = this.sourceRegion.x, m = 0;
/* 1093 */       for (; i < this.destinationRegion.x + this.destinationRegion.width; 
/* 1094 */         i++, m++, x += this.scaleX) {
/* 1095 */         srcPos[m] = x >> 1;
/* 1096 */         srcOff[m] = 1 - (x & 0x1) << 2;
/* 1097 */         destPos[m] = i >> 1;
/* 1098 */         destOff[m] = 1 - (i & 0x1) << 2;
/*      */       } 
/*      */       
/* 1101 */       int k = this.destinationRegion.y * lineStride;
/* 1102 */       if (this.isBottomUp) {
/* 1103 */         k += (this.destinationRegion.height - 1) * lineStride;
/*      */       }
/* 1105 */       int j = 0, y = this.sourceRegion.y;
/* 1106 */       for (; j < this.destinationRegion.height; j++, y += this.scaleY) {
/*      */         
/* 1108 */         if (abortRequested())
/*      */           break; 
/* 1110 */         this.iis.read(buf, 0, lineLength);
/* 1111 */         for (int n = 0; n < this.destinationRegion.width; n++) {
/*      */           
/* 1113 */           int v = buf[srcPos[n]] >> srcOff[n] & 0xF;
/* 1114 */           bdata[k + destPos[n]] = (byte)(bdata[k + destPos[n]] | v << destOff[n]);
/*      */         } 
/*      */         
/* 1117 */         k += this.isBottomUp ? -lineStride : lineStride;
/* 1118 */         this.iis.skipBytes(skipLength);
/* 1119 */         processImageUpdate(this.bi, 0, j, this.destinationRegion.width, 1, 1, 1, new int[] { 0 });
/*      */ 
/*      */         
/* 1122 */         processImageProgress(100.0F * j / this.destinationRegion.height);
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void read8Bit(byte[] bdata) throws IOException {
/* 1131 */     int padding = this.width % 4;
/* 1132 */     if (padding != 0) {
/* 1133 */       padding = 4 - padding;
/*      */     }
/*      */     
/* 1136 */     int lineLength = this.width + padding;
/*      */     
/* 1138 */     if (this.noTransform) {
/* 1139 */       int j = this.isBottomUp ? ((this.height - 1) * this.width) : 0;
/*      */       
/* 1141 */       for (int i = 0; i < this.height && 
/* 1142 */         !abortRequested(); i++) {
/*      */ 
/*      */         
/* 1145 */         this.iis.readFully(bdata, j, this.width);
/* 1146 */         this.iis.skipBytes(padding);
/* 1147 */         j += this.isBottomUp ? -this.width : this.width;
/* 1148 */         processImageUpdate(this.bi, 0, i, this.destinationRegion.width, 1, 1, 1, new int[] { 0 });
/*      */ 
/*      */         
/* 1151 */         processImageProgress(100.0F * i / this.destinationRegion.height);
/*      */       } 
/*      */     } else {
/* 1154 */       byte[] buf = new byte[lineLength];
/*      */       
/* 1156 */       int lineStride = ((ComponentSampleModel)this.sampleModel).getScanlineStride();
/*      */       
/* 1158 */       if (this.isBottomUp) {
/* 1159 */         int lastLine = this.sourceRegion.y + (this.destinationRegion.height - 1) * this.scaleY;
/*      */         
/* 1161 */         this.iis.skipBytes(lineLength * (this.height - 1 - lastLine));
/*      */       } else {
/* 1163 */         this.iis.skipBytes(lineLength * this.sourceRegion.y);
/*      */       } 
/* 1165 */       int skipLength = lineLength * (this.scaleY - 1);
/*      */       
/* 1167 */       int k = this.destinationRegion.y * lineStride;
/* 1168 */       if (this.isBottomUp)
/* 1169 */         k += (this.destinationRegion.height - 1) * lineStride; 
/* 1170 */       k += this.destinationRegion.x;
/*      */       
/* 1172 */       int j = 0, y = this.sourceRegion.y;
/* 1173 */       for (; j < this.destinationRegion.height; j++, y += this.scaleY) {
/*      */         
/* 1175 */         if (abortRequested())
/*      */           break; 
/* 1177 */         this.iis.read(buf, 0, lineLength);
/* 1178 */         int i = 0, m = this.sourceRegion.x;
/* 1179 */         for (; i < this.destinationRegion.width; i++, m += this.scaleX)
/*      */         {
/* 1181 */           bdata[k + i] = buf[m];
/*      */         }
/*      */         
/* 1184 */         k += this.isBottomUp ? -lineStride : lineStride;
/* 1185 */         this.iis.skipBytes(skipLength);
/* 1186 */         processImageUpdate(this.bi, 0, j, this.destinationRegion.width, 1, 1, 1, new int[] { 0 });
/*      */ 
/*      */         
/* 1189 */         processImageProgress(100.0F * j / this.destinationRegion.height);
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void read24Bit(byte[] bdata) throws IOException {
/* 1198 */     int padding = this.width * 3 % 4;
/* 1199 */     if (padding != 0) {
/* 1200 */       padding = 4 - padding;
/*      */     }
/* 1202 */     int lineStride = this.width * 3;
/* 1203 */     int lineLength = lineStride + padding;
/*      */     
/* 1205 */     if (this.noTransform) {
/* 1206 */       int j = this.isBottomUp ? ((this.height - 1) * this.width * 3) : 0;
/*      */       
/* 1208 */       for (int i = 0; i < this.height && 
/* 1209 */         !abortRequested(); i++) {
/*      */ 
/*      */         
/* 1212 */         this.iis.readFully(bdata, j, lineStride);
/* 1213 */         this.iis.skipBytes(padding);
/* 1214 */         j += this.isBottomUp ? -lineStride : lineStride;
/* 1215 */         processImageUpdate(this.bi, 0, i, this.destinationRegion.width, 1, 1, 1, new int[] { 0 });
/*      */ 
/*      */         
/* 1218 */         processImageProgress(100.0F * i / this.destinationRegion.height);
/*      */       } 
/*      */     } else {
/* 1221 */       byte[] buf = new byte[lineLength];
/*      */       
/* 1223 */       lineStride = ((ComponentSampleModel)this.sampleModel).getScanlineStride();
/*      */       
/* 1225 */       if (this.isBottomUp) {
/* 1226 */         int lastLine = this.sourceRegion.y + (this.destinationRegion.height - 1) * this.scaleY;
/*      */         
/* 1228 */         this.iis.skipBytes(lineLength * (this.height - 1 - lastLine));
/*      */       } else {
/* 1230 */         this.iis.skipBytes(lineLength * this.sourceRegion.y);
/*      */       } 
/* 1232 */       int skipLength = lineLength * (this.scaleY - 1);
/*      */       
/* 1234 */       int k = this.destinationRegion.y * lineStride;
/* 1235 */       if (this.isBottomUp)
/* 1236 */         k += (this.destinationRegion.height - 1) * lineStride; 
/* 1237 */       k += this.destinationRegion.x * 3;
/*      */       
/* 1239 */       int j = 0, y = this.sourceRegion.y;
/* 1240 */       for (; j < this.destinationRegion.height; j++, y += this.scaleY) {
/*      */         
/* 1242 */         if (abortRequested())
/*      */           break; 
/* 1244 */         this.iis.read(buf, 0, lineLength);
/* 1245 */         int i = 0, m = 3 * this.sourceRegion.x;
/* 1246 */         for (; i < this.destinationRegion.width; i++, m += 3 * this.scaleX) {
/*      */           
/* 1248 */           int n = 3 * i + k;
/* 1249 */           for (int b = 0; b < this.destBands.length; b++) {
/* 1250 */             bdata[n + this.destBands[b]] = buf[m + this.sourceBands[b]];
/*      */           }
/*      */         } 
/* 1253 */         k += this.isBottomUp ? -lineStride : lineStride;
/* 1254 */         this.iis.skipBytes(skipLength);
/* 1255 */         processImageUpdate(this.bi, 0, j, this.destinationRegion.width, 1, 1, 1, new int[] { 0 });
/*      */ 
/*      */         
/* 1258 */         processImageProgress(100.0F * j / this.destinationRegion.height);
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private void read16Bit(short[] sdata) throws IOException {
/* 1266 */     int padding = this.width * 2 % 4;
/*      */     
/* 1268 */     if (padding != 0) {
/* 1269 */       padding = 4 - padding;
/*      */     }
/* 1271 */     int lineLength = this.width + padding / 2;
/*      */     
/* 1273 */     if (this.noTransform) {
/* 1274 */       int j = this.isBottomUp ? ((this.height - 1) * this.width) : 0;
/* 1275 */       for (int i = 0; i < this.height && 
/* 1276 */         !abortRequested(); i++) {
/*      */ 
/*      */ 
/*      */         
/* 1280 */         this.iis.readFully(sdata, j, this.width);
/* 1281 */         this.iis.skipBytes(padding);
/* 1282 */         j += this.isBottomUp ? -this.width : this.width;
/* 1283 */         processImageUpdate(this.bi, 0, i, this.destinationRegion.width, 1, 1, 1, new int[] { 0 });
/*      */ 
/*      */         
/* 1286 */         processImageProgress(100.0F * i / this.destinationRegion.height);
/*      */       } 
/*      */     } else {
/* 1289 */       short[] buf = new short[lineLength];
/*      */       
/* 1291 */       int lineStride = ((SinglePixelPackedSampleModel)this.sampleModel).getScanlineStride();
/*      */       
/* 1293 */       if (this.isBottomUp) {
/* 1294 */         int lastLine = this.sourceRegion.y + (this.destinationRegion.height - 1) * this.scaleY;
/*      */         
/* 1296 */         this.iis.skipBytes(lineLength * (this.height - 1 - lastLine) << 1);
/*      */       } else {
/* 1298 */         this.iis.skipBytes(lineLength * this.sourceRegion.y << 1);
/*      */       } 
/* 1300 */       int skipLength = lineLength * (this.scaleY - 1) << 1;
/*      */       
/* 1302 */       int k = this.destinationRegion.y * lineStride;
/* 1303 */       if (this.isBottomUp)
/* 1304 */         k += (this.destinationRegion.height - 1) * lineStride; 
/* 1305 */       k += this.destinationRegion.x;
/*      */       
/* 1307 */       int j = 0, y = this.sourceRegion.y;
/* 1308 */       for (; j < this.destinationRegion.height; j++, y += this.scaleY) {
/*      */         
/* 1310 */         if (abortRequested())
/*      */           break; 
/* 1312 */         this.iis.readFully(buf, 0, lineLength);
/* 1313 */         int i = 0, m = this.sourceRegion.x;
/* 1314 */         for (; i < this.destinationRegion.width; i++, m += this.scaleX)
/*      */         {
/* 1316 */           sdata[k + i] = buf[m];
/*      */         }
/*      */         
/* 1319 */         k += this.isBottomUp ? -lineStride : lineStride;
/* 1320 */         this.iis.skipBytes(skipLength);
/* 1321 */         processImageUpdate(this.bi, 0, j, this.destinationRegion.width, 1, 1, 1, new int[] { 0 });
/*      */ 
/*      */         
/* 1324 */         processImageProgress(100.0F * j / this.destinationRegion.height);
/*      */       } 
/*      */     } 
/*      */   }
/*      */   
/*      */   private void read32Bit(int[] idata) throws IOException {
/* 1330 */     if (this.noTransform) {
/* 1331 */       int j = this.isBottomUp ? ((this.height - 1) * this.width) : 0;
/*      */       
/* 1333 */       for (int i = 0; i < this.height && 
/* 1334 */         !abortRequested(); i++) {
/*      */ 
/*      */         
/* 1337 */         this.iis.readFully(idata, j, this.width);
/* 1338 */         j += this.isBottomUp ? -this.width : this.width;
/* 1339 */         processImageUpdate(this.bi, 0, i, this.destinationRegion.width, 1, 1, 1, new int[] { 0 });
/*      */ 
/*      */         
/* 1342 */         processImageProgress(100.0F * i / this.destinationRegion.height);
/*      */       } 
/*      */     } else {
/* 1345 */       int[] buf = new int[this.width];
/*      */       
/* 1347 */       int lineStride = ((SinglePixelPackedSampleModel)this.sampleModel).getScanlineStride();
/*      */       
/* 1349 */       if (this.isBottomUp) {
/* 1350 */         int lastLine = this.sourceRegion.y + (this.destinationRegion.height - 1) * this.scaleY;
/*      */         
/* 1352 */         this.iis.skipBytes(this.width * (this.height - 1 - lastLine) << 2);
/*      */       } else {
/* 1354 */         this.iis.skipBytes(this.width * this.sourceRegion.y << 2);
/*      */       } 
/* 1356 */       int skipLength = this.width * (this.scaleY - 1) << 2;
/*      */       
/* 1358 */       int k = this.destinationRegion.y * lineStride;
/* 1359 */       if (this.isBottomUp)
/* 1360 */         k += (this.destinationRegion.height - 1) * lineStride; 
/* 1361 */       k += this.destinationRegion.x;
/*      */       
/* 1363 */       int j = 0, y = this.sourceRegion.y;
/* 1364 */       for (; j < this.destinationRegion.height; j++, y += this.scaleY) {
/*      */         
/* 1366 */         if (abortRequested())
/*      */           break; 
/* 1368 */         this.iis.readFully(buf, 0, this.width);
/* 1369 */         int i = 0, m = this.sourceRegion.x;
/* 1370 */         for (; i < this.destinationRegion.width; i++, m += this.scaleX)
/*      */         {
/* 1372 */           idata[k + i] = buf[m];
/*      */         }
/*      */         
/* 1375 */         k += this.isBottomUp ? -lineStride : lineStride;
/* 1376 */         this.iis.skipBytes(skipLength);
/* 1377 */         processImageUpdate(this.bi, 0, j, this.destinationRegion.width, 1, 1, 1, new int[] { 0 });
/*      */ 
/*      */         
/* 1380 */         processImageProgress(100.0F * j / this.destinationRegion.height);
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   private void readRLE8(byte[] bdata) throws IOException {
/* 1387 */     int imSize = (int)this.imageSize;
/* 1388 */     if (imSize == 0) {
/* 1389 */       imSize = (int)(this.bitmapFileSize - this.bitmapOffset);
/*      */     }
/*      */     
/* 1392 */     int padding = 0;
/*      */ 
/*      */     
/* 1395 */     int remainder = this.width % 4;
/* 1396 */     if (remainder != 0) {
/* 1397 */       padding = 4 - remainder;
/*      */     }
/*      */ 
/*      */     
/* 1401 */     byte[] values = new byte[imSize];
/* 1402 */     int bytesRead = 0;
/* 1403 */     this.iis.readFully(values, 0, imSize);
/*      */ 
/*      */     
/* 1406 */     decodeRLE8(imSize, padding, values, bdata);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void decodeRLE8(int imSize, int padding, byte[] values, byte[] bdata) throws IOException {
/* 1414 */     byte[] val = new byte[this.width * this.height];
/* 1415 */     int count = 0, l = 0;
/*      */     
/* 1417 */     boolean flag = false;
/* 1418 */     int lineNo = this.isBottomUp ? (this.height - 1) : 0;
/*      */     
/* 1420 */     int lineStride = ((ComponentSampleModel)this.sampleModel).getScanlineStride();
/* 1421 */     int finished = 0;
/*      */     
/* 1423 */     while (count != imSize) {
/* 1424 */       int value = values[count++] & 0xFF;
/* 1425 */       if (value == 0) {
/* 1426 */         int xoff; int yoff; int end; int i; switch (values[count++] & 0xFF) {
/*      */ 
/*      */ 
/*      */ 
/*      */           
/*      */           case 0:
/*      */           case 1:
/* 1433 */             if (lineNo >= this.sourceRegion.y && lineNo < this.sourceRegion.y + this.sourceRegion.height)
/*      */             {
/* 1435 */               if (this.noTransform) {
/* 1436 */                 int pos = lineNo * this.width;
/* 1437 */                 for (int j = 0; j < this.width; j++)
/* 1438 */                   bdata[pos++] = val[j]; 
/* 1439 */                 processImageUpdate(this.bi, 0, lineNo, this.destinationRegion.width, 1, 1, 1, new int[] { 0 });
/*      */ 
/*      */                 
/* 1442 */                 finished++;
/* 1443 */               } else if ((lineNo - this.sourceRegion.y) % this.scaleY == 0) {
/* 1444 */                 int currentLine = (lineNo - this.sourceRegion.y) / this.scaleY + this.destinationRegion.y;
/*      */                 
/* 1446 */                 int pos = currentLine * lineStride;
/* 1447 */                 pos += this.destinationRegion.x;
/* 1448 */                 int j = this.sourceRegion.x;
/* 1449 */                 for (; j < this.sourceRegion.x + this.sourceRegion.width; 
/* 1450 */                   j += this.scaleX)
/* 1451 */                   bdata[pos++] = val[j]; 
/* 1452 */                 processImageUpdate(this.bi, 0, currentLine, this.destinationRegion.width, 1, 1, 1, new int[] { 0 });
/*      */ 
/*      */                 
/* 1455 */                 finished++;
/*      */               } 
/*      */             }
/* 1458 */             processImageProgress(100.0F * finished / this.destinationRegion.height);
/* 1459 */             lineNo += this.isBottomUp ? -1 : 1;
/* 1460 */             l = 0;
/*      */             
/* 1462 */             if (abortRequested()) {
/*      */               break;
/*      */             }
/*      */ 
/*      */             
/* 1467 */             if ((values[count - 1] & 0xFF) == 1) {
/* 1468 */               flag = true;
/*      */             }
/*      */             break;
/*      */ 
/*      */           
/*      */           case 2:
/* 1474 */             xoff = values[count++] & 0xFF;
/* 1475 */             yoff = values[count] & 0xFF;
/*      */             
/* 1477 */             l += xoff + yoff * this.width;
/*      */             break;
/*      */           
/*      */           default:
/* 1481 */             end = values[count - 1] & 0xFF;
/* 1482 */             for (i = 0; i < end; i++) {
/* 1483 */               val[l++] = (byte)(values[count++] & 0xFF);
/*      */             }
/*      */ 
/*      */ 
/*      */             
/* 1488 */             if ((end & 0x1) == 1)
/* 1489 */               count++; 
/*      */             break;
/*      */         } 
/*      */       } else {
/* 1493 */         for (int i = 0; i < value; i++) {
/* 1494 */           val[l++] = (byte)(values[count] & 0xFF);
/*      */         }
/*      */         
/* 1497 */         count++;
/*      */       } 
/*      */ 
/*      */       
/* 1501 */       if (flag) {
/*      */         break;
/*      */       }
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private void readRLE4(byte[] bdata) throws IOException {
/* 1510 */     int imSize = (int)this.imageSize;
/* 1511 */     if (imSize == 0) {
/* 1512 */       imSize = (int)(this.bitmapFileSize - this.bitmapOffset);
/*      */     }
/*      */     
/* 1515 */     int padding = 0;
/*      */ 
/*      */     
/* 1518 */     int remainder = this.width % 4;
/* 1519 */     if (remainder != 0) {
/* 1520 */       padding = 4 - remainder;
/*      */     }
/*      */ 
/*      */     
/* 1524 */     byte[] values = new byte[imSize];
/* 1525 */     this.iis.readFully(values, 0, imSize);
/*      */ 
/*      */     
/* 1528 */     decodeRLE4(imSize, padding, values, bdata);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void decodeRLE4(int imSize, int padding, byte[] values, byte[] bdata) throws IOException {
/* 1535 */     byte[] val = new byte[this.width];
/* 1536 */     int count = 0, l = 0;
/*      */     
/* 1538 */     boolean flag = false;
/* 1539 */     int lineNo = this.isBottomUp ? (this.height - 1) : 0;
/*      */     
/* 1541 */     int lineStride = ((MultiPixelPackedSampleModel)this.sampleModel).getScanlineStride();
/* 1542 */     int finished = 0;
/*      */     
/* 1544 */     while (count != imSize) {
/*      */       
/* 1546 */       int value = values[count++] & 0xFF;
/* 1547 */       if (value == 0) {
/*      */         int xoff; int yoff;
/*      */         int end;
/*      */         int i;
/* 1551 */         switch (values[count++] & 0xFF) {
/*      */ 
/*      */ 
/*      */ 
/*      */           
/*      */           case 0:
/*      */           case 1:
/* 1558 */             if (lineNo >= this.sourceRegion.y && lineNo < this.sourceRegion.y + this.sourceRegion.height)
/*      */             {
/* 1560 */               if (this.noTransform) {
/* 1561 */                 int pos = lineNo * (this.width + 1 >> 1);
/* 1562 */                 for (int k = 0, j = 0; k < this.width >> 1; k++) {
/* 1563 */                   bdata[pos++] = (byte)(val[j++] << 4 | val[j++]);
/*      */                 }
/* 1565 */                 if ((this.width & 0x1) == 1) {
/* 1566 */                   bdata[pos] = (byte)(bdata[pos] | val[this.width - 1] << 4);
/*      */                 }
/* 1568 */                 processImageUpdate(this.bi, 0, lineNo, this.destinationRegion.width, 1, 1, 1, new int[] { 0 });
/*      */ 
/*      */                 
/* 1571 */                 finished++;
/* 1572 */               } else if ((lineNo - this.sourceRegion.y) % this.scaleY == 0) {
/* 1573 */                 int currentLine = (lineNo - this.sourceRegion.y) / this.scaleY + this.destinationRegion.y;
/*      */                 
/* 1575 */                 int pos = currentLine * lineStride;
/* 1576 */                 pos += this.destinationRegion.x >> 1;
/* 1577 */                 int shift = 1 - (this.destinationRegion.x & 0x1) << 2;
/* 1578 */                 int j = this.sourceRegion.x;
/* 1579 */                 for (; j < this.sourceRegion.x + this.sourceRegion.width; 
/* 1580 */                   j += this.scaleX) {
/* 1581 */                   bdata[pos] = (byte)(bdata[pos] | val[j] << shift);
/* 1582 */                   shift += 4;
/* 1583 */                   if (shift == 4) {
/* 1584 */                     pos++;
/*      */                   }
/* 1586 */                   shift &= 0x7;
/*      */                 } 
/* 1588 */                 processImageUpdate(this.bi, 0, currentLine, this.destinationRegion.width, 1, 1, 1, new int[] { 0 });
/*      */ 
/*      */                 
/* 1591 */                 finished++;
/*      */               } 
/*      */             }
/* 1594 */             processImageProgress(100.0F * finished / this.destinationRegion.height);
/* 1595 */             lineNo += this.isBottomUp ? -1 : 1;
/* 1596 */             l = 0;
/*      */             
/* 1598 */             if (abortRequested()) {
/*      */               break;
/*      */             }
/*      */ 
/*      */             
/* 1603 */             if ((values[count - 1] & 0xFF) == 1) {
/* 1604 */               flag = true;
/*      */             }
/*      */             break;
/*      */           
/*      */           case 2:
/* 1609 */             xoff = values[count++] & 0xFF;
/* 1610 */             yoff = values[count] & 0xFF;
/*      */             
/* 1612 */             l += xoff + yoff * this.width;
/*      */             break;
/*      */           
/*      */           default:
/* 1616 */             end = values[count - 1] & 0xFF;
/* 1617 */             for (i = 0; i < end; i++) {
/* 1618 */               val[l++] = (byte)(((i & 0x1) == 0) ? ((values[count] & 0xF0) >> 4) : (values[count++] & 0xF));
/*      */             }
/*      */ 
/*      */ 
/*      */ 
/*      */             
/* 1624 */             if ((end & 0x1) == 1) {
/* 1625 */               count++;
/*      */             }
/*      */ 
/*      */ 
/*      */             
/* 1630 */             if (((int)Math.ceil((end / 2)) & 0x1) == 1) {
/* 1631 */               count++;
/*      */             }
/*      */             break;
/*      */         } 
/*      */       
/*      */       } else {
/* 1637 */         int[] alternate = { (values[count] & 0xF0) >> 4, values[count] & 0xF };
/*      */         
/* 1639 */         for (int i = 0; i < value && l < this.width; i++) {
/* 1640 */           val[l++] = (byte)alternate[i & 0x1];
/*      */         }
/*      */         
/* 1643 */         count++;
/*      */       } 
/*      */ 
/*      */       
/* 1647 */       if (flag) {
/*      */         break;
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
/*      */ 
/*      */   
/*      */   private BufferedImage readEmbedded(int type, BufferedImage bi, ImageReadParam bmpParam) throws IOException {
/*      */     String format;
/* 1666 */     switch (type) {
/*      */       case 4:
/* 1668 */         format = "JPEG";
/*      */         break;
/*      */       case 5:
/* 1671 */         format = "PNG";
/*      */         break;
/*      */       default:
/* 1674 */         throw new IOException("Unexpected compression type: " + type);
/*      */     } 
/*      */ 
/*      */     
/* 1678 */     ImageReader reader = ImageIO.getImageReadersByFormatName(format).next();
/* 1679 */     if (reader == null) {
/* 1680 */       throw new RuntimeException(I18N.getString("BMPImageReader4") + " " + format);
/*      */     }
/*      */ 
/*      */     
/* 1684 */     byte[] buff = new byte[(int)this.imageSize];
/* 1685 */     this.iis.read(buff);
/* 1686 */     reader.setInput(ImageIO.createImageInputStream(new ByteArrayInputStream(buff)));
/* 1687 */     if (bi == null) {
/* 1688 */       ImageTypeSpecifier embType = reader.getImageTypes(0).next();
/* 1689 */       bi = embType.createBufferedImage(this.destinationRegion.x + this.destinationRegion.width, this.destinationRegion.y + this.destinationRegion.height);
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1695 */     reader.addIIOReadProgressListener(new EmbeddedProgressAdapter()
/*      */         {
/*      */           public void imageProgress(ImageReader source, float percentageDone)
/*      */           {
/* 1699 */             BMPImageReader.this.processImageProgress(percentageDone);
/*      */           }
/*      */         });
/*      */     
/* 1703 */     reader.addIIOReadUpdateListener(new IIOReadUpdateListener()
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/*      */           
/*      */           public void imageUpdate(ImageReader source, BufferedImage theImage, int minX, int minY, int width, int height, int periodX, int periodY, int[] bands)
/*      */           {
/* 1711 */             BMPImageReader.this.processImageUpdate(theImage, minX, minY, width, height, periodX, periodY, bands);
/*      */           }
/*      */ 
/*      */ 
/*      */ 
/*      */           
/*      */           public void passComplete(ImageReader source, BufferedImage theImage) {
/* 1718 */             BMPImageReader.this.processPassComplete(theImage);
/*      */           }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/*      */           public void passStarted(ImageReader source, BufferedImage theImage, int pass, int minPass, int maxPass, int minX, int minY, int periodX, int periodY, int[] bands) {
/* 1728 */             BMPImageReader.this.processPassStarted(theImage, pass, minPass, maxPass, minX, minY, periodX, periodY, bands);
/*      */           }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/*      */           public void thumbnailPassComplete(ImageReader source, BufferedImage thumb) {}
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/*      */           public void thumbnailPassStarted(ImageReader source, BufferedImage thumb, int pass, int minPass, int maxPass, int minX, int minY, int periodX, int periodY, int[] bands) {}
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/*      */           public void thumbnailUpdate(ImageReader source, BufferedImage theThumbnail, int minX, int minY, int width, int height, int periodX, int periodY, int[] bands) {}
/*      */         });
/* 1749 */     reader.addIIOReadWarningListener(new IIOReadWarningListener()
/*      */         {
/*      */           public void warningOccurred(ImageReader source, String warning) {
/* 1752 */             BMPImageReader.this.processWarningOccurred(warning);
/*      */           }
/*      */         });
/*      */     
/* 1756 */     ImageReadParam param = reader.getDefaultReadParam();
/* 1757 */     param.setDestination(bi);
/* 1758 */     param.setDestinationBands(bmpParam.getDestinationBands());
/* 1759 */     param.setDestinationOffset(bmpParam.getDestinationOffset());
/* 1760 */     param.setSourceBands(bmpParam.getSourceBands());
/* 1761 */     param.setSourceRegion(bmpParam.getSourceRegion());
/* 1762 */     param.setSourceSubsampling(bmpParam.getSourceXSubsampling(), bmpParam
/* 1763 */         .getSourceYSubsampling(), bmpParam
/* 1764 */         .getSubsamplingXOffset(), bmpParam
/* 1765 */         .getSubsamplingYOffset());
/* 1766 */     reader.read(0, param);
/* 1767 */     return bi;
/*      */   }
/*      */   
/*      */   private class EmbeddedProgressAdapter implements IIOReadProgressListener {
/*      */     private EmbeddedProgressAdapter() {}
/*      */     
/*      */     public void imageComplete(ImageReader src) {}
/*      */     
/*      */     public void imageProgress(ImageReader src, float percentageDone) {}
/*      */     
/*      */     public void imageStarted(ImageReader src, int imageIndex) {}
/*      */     
/*      */     public void thumbnailComplete(ImageReader src) {}
/*      */     
/*      */     public void thumbnailProgress(ImageReader src, float percentageDone) {}
/*      */     
/*      */     public void thumbnailStarted(ImageReader src, int iIdx, int tIdx) {}
/*      */     
/*      */     public void sequenceComplete(ImageReader src) {}
/*      */     
/*      */     public void sequenceStarted(ImageReader src, int minIndex) {}
/*      */     
/*      */     public void readAborted(ImageReader src) {}
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\github\jaiimageio\impl\plugins\bmp\BMPImageReader.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */