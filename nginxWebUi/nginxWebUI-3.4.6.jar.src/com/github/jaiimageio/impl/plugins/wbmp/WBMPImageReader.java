/*     */ package com.github.jaiimageio.impl.plugins.wbmp;
/*     */ 
/*     */ import com.github.jaiimageio.impl.common.ImageUtil;
/*     */ import java.awt.Rectangle;
/*     */ import java.awt.image.BufferedImage;
/*     */ import java.awt.image.DataBufferByte;
/*     */ import java.awt.image.MultiPixelPackedSampleModel;
/*     */ import java.awt.image.Raster;
/*     */ import java.awt.image.WritableRaster;
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Iterator;
/*     */ import javax.imageio.IIOException;
/*     */ import javax.imageio.ImageReadParam;
/*     */ import javax.imageio.ImageReader;
/*     */ import javax.imageio.ImageTypeSpecifier;
/*     */ import javax.imageio.metadata.IIOMetadata;
/*     */ import javax.imageio.spi.ImageReaderSpi;
/*     */ import javax.imageio.stream.ImageInputStream;
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
/*     */ public class WBMPImageReader
/*     */   extends ImageReader
/*     */ {
/*  74 */   private ImageInputStream iis = null;
/*     */ 
/*     */   
/*     */   private boolean gotHeader = false;
/*     */ 
/*     */   
/*     */   private long imageDataOffset;
/*     */ 
/*     */   
/*     */   private int width;
/*     */ 
/*     */   
/*     */   private int height;
/*     */ 
/*     */   
/*     */   private int wbmpType;
/*     */ 
/*     */   
/*     */   private WBMPMetadata metadata;
/*     */ 
/*     */   
/*     */   public WBMPImageReader(ImageReaderSpi originator) {
/*  96 */     super(originator);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setInput(Object input, boolean seekForwardOnly, boolean ignoreMetadata) {
/* 103 */     super.setInput(input, seekForwardOnly, ignoreMetadata);
/* 104 */     this.iis = (ImageInputStream)input;
/* 105 */     this.gotHeader = false;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getNumImages(boolean allowSearch) throws IOException {
/* 110 */     if (this.iis == null) {
/* 111 */       throw new IllegalStateException(I18N.getString("GetNumImages0"));
/*     */     }
/* 113 */     if (this.seekForwardOnly && allowSearch) {
/* 114 */       throw new IllegalStateException(I18N.getString("GetNumImages1"));
/*     */     }
/* 116 */     return 1;
/*     */   }
/*     */   
/*     */   public int getWidth(int imageIndex) throws IOException {
/* 120 */     checkIndex(imageIndex);
/* 121 */     readHeader();
/* 122 */     return this.width;
/*     */   }
/*     */   
/*     */   public int getHeight(int imageIndex) throws IOException {
/* 126 */     checkIndex(imageIndex);
/* 127 */     readHeader();
/* 128 */     return this.height;
/*     */   }
/*     */   
/*     */   public boolean isRandomAccessEasy(int imageIndex) throws IOException {
/* 132 */     checkIndex(imageIndex);
/* 133 */     return true;
/*     */   }
/*     */   
/*     */   private void checkIndex(int imageIndex) {
/* 137 */     if (imageIndex != 0) {
/* 138 */       throw new IndexOutOfBoundsException(I18N.getString("WBMPImageReader0"));
/*     */     }
/*     */   }
/*     */   
/*     */   public void readHeader() throws IOException {
/* 143 */     if (this.gotHeader) {
/*     */ 
/*     */       
/* 146 */       this.iis.seek(this.imageDataOffset);
/*     */       
/*     */       return;
/*     */     } 
/* 150 */     if (this.iis == null) {
/* 151 */       throw new IllegalStateException(I18N.getString("WBMPImageReader1"));
/*     */     }
/*     */     
/* 154 */     this.metadata = new WBMPMetadata();
/* 155 */     this.wbmpType = this.iis.readByte();
/* 156 */     byte fixHeaderField = this.iis.readByte();
/*     */ 
/*     */     
/* 159 */     if (fixHeaderField != 0 || 
/* 160 */       !isValidWbmpType(this.wbmpType)) {
/* 161 */       throw new IIOException(I18N.getString("WBMPImageReader2"));
/*     */     }
/*     */     
/* 164 */     this.metadata.wbmpType = this.wbmpType;
/*     */ 
/*     */     
/* 167 */     this.width = ImageUtil.readMultiByteInteger(this.iis);
/* 168 */     this.metadata.width = this.width;
/*     */ 
/*     */     
/* 171 */     this.height = ImageUtil.readMultiByteInteger(this.iis);
/* 172 */     this.metadata.height = this.height;
/*     */     
/* 174 */     this.gotHeader = true;
/*     */     
/* 176 */     this.imageDataOffset = this.iis.getStreamPosition();
/*     */   }
/*     */ 
/*     */   
/*     */   public Iterator getImageTypes(int imageIndex) throws IOException {
/* 181 */     checkIndex(imageIndex);
/* 182 */     readHeader();
/*     */     
/* 184 */     BufferedImage bi = new BufferedImage(1, 1, 12);
/*     */     
/* 186 */     ArrayList<ImageTypeSpecifier> list = new ArrayList(1);
/* 187 */     list.add(new ImageTypeSpecifier(bi));
/* 188 */     return list.iterator();
/*     */   }
/*     */   
/*     */   public ImageReadParam getDefaultReadParam() {
/* 192 */     return new ImageReadParam();
/*     */   }
/*     */ 
/*     */   
/*     */   public IIOMetadata getImageMetadata(int imageIndex) throws IOException {
/* 197 */     checkIndex(imageIndex);
/* 198 */     if (this.metadata == null) {
/* 199 */       readHeader();
/*     */     }
/* 201 */     return this.metadata;
/*     */   }
/*     */   
/*     */   public IIOMetadata getStreamMetadata() throws IOException {
/* 205 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public BufferedImage read(int imageIndex, ImageReadParam param) throws IOException {
/* 211 */     if (this.iis == null) {
/* 212 */       throw new IllegalStateException(I18N.getString("WBMPImageReader1"));
/*     */     }
/*     */     
/* 215 */     checkIndex(imageIndex);
/* 216 */     clearAbortRequest();
/* 217 */     processImageStarted(imageIndex);
/* 218 */     if (param == null) {
/* 219 */       param = getDefaultReadParam();
/*     */     }
/*     */     
/* 222 */     readHeader();
/*     */     
/* 224 */     Rectangle sourceRegion = new Rectangle(0, 0, 0, 0);
/* 225 */     Rectangle destinationRegion = new Rectangle(0, 0, 0, 0);
/*     */     
/* 227 */     computeRegions(param, this.width, this.height, param
/* 228 */         .getDestination(), sourceRegion, destinationRegion);
/*     */ 
/*     */ 
/*     */     
/* 232 */     int scaleX = param.getSourceXSubsampling();
/* 233 */     int scaleY = param.getSourceYSubsampling();
/* 234 */     int xOffset = param.getSubsamplingXOffset();
/* 235 */     int yOffset = param.getSubsamplingYOffset();
/*     */ 
/*     */     
/* 238 */     BufferedImage bi = param.getDestination();
/*     */     
/* 240 */     if (bi == null) {
/* 241 */       bi = new BufferedImage(destinationRegion.x + destinationRegion.width, destinationRegion.y + destinationRegion.height, 12);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 247 */     boolean noTransform = (destinationRegion.equals(new Rectangle(0, 0, this.width, this.height)) && destinationRegion.equals(new Rectangle(0, 0, bi.getWidth(), bi.getHeight())));
/*     */ 
/*     */     
/* 250 */     WritableRaster tile = bi.getWritableTile(0, 0);
/*     */ 
/*     */ 
/*     */     
/* 254 */     MultiPixelPackedSampleModel sm = (MultiPixelPackedSampleModel)bi.getSampleModel();
/*     */     
/* 256 */     if (noTransform) {
/* 257 */       if (abortRequested()) {
/* 258 */         processReadAborted();
/* 259 */         return bi;
/*     */       } 
/*     */ 
/*     */       
/* 263 */       this.iis.read(((DataBufferByte)tile.getDataBuffer()).getData(), 0, this.height * sm
/* 264 */           .getScanlineStride());
/* 265 */       processImageUpdate(bi, 0, 0, this.width, this.height, 1, 1, new int[] { 0 });
/*     */ 
/*     */ 
/*     */       
/* 269 */       processImageProgress(100.0F);
/*     */     } else {
/* 271 */       int len = (this.width + 7) / 8;
/* 272 */       byte[] buf = new byte[len];
/* 273 */       byte[] data = ((DataBufferByte)tile.getDataBuffer()).getData();
/* 274 */       int lineStride = sm.getScanlineStride();
/* 275 */       this.iis.skipBytes(len * sourceRegion.y);
/* 276 */       int skipLength = len * (scaleY - 1);
/*     */ 
/*     */       
/* 279 */       int[] srcOff = new int[destinationRegion.width];
/* 280 */       int[] destOff = new int[destinationRegion.width];
/* 281 */       int[] srcPos = new int[destinationRegion.width];
/* 282 */       int[] destPos = new int[destinationRegion.width];
/*     */       
/* 284 */       int i = destinationRegion.x, x = sourceRegion.x, m = 0;
/* 285 */       for (; i < destinationRegion.x + destinationRegion.width; 
/* 286 */         i++, m++, x += scaleX) {
/* 287 */         srcPos[m] = x >> 3;
/* 288 */         srcOff[m] = 7 - (x & 0x7);
/* 289 */         destPos[m] = i >> 3;
/* 290 */         destOff[m] = 7 - (i & 0x7);
/*     */       } 
/*     */       
/* 293 */       int j = 0, y = sourceRegion.y;
/* 294 */       int k = destinationRegion.y * lineStride;
/* 295 */       for (; j < destinationRegion.height; j++, y += scaleY) {
/*     */         
/* 297 */         if (abortRequested())
/*     */           break; 
/* 299 */         this.iis.read(buf, 0, len);
/* 300 */         for (int n = 0; n < destinationRegion.width; n++) {
/*     */           
/* 302 */           int v = buf[srcPos[n]] >> srcOff[n] & 0x1;
/* 303 */           data[k + destPos[n]] = (byte)(data[k + destPos[n]] | v << destOff[n]);
/*     */         } 
/*     */         
/* 306 */         k += lineStride;
/* 307 */         this.iis.skipBytes(skipLength);
/* 308 */         processImageUpdate(bi, 0, j, destinationRegion.width, 1, 1, 1, new int[] { 0 });
/*     */ 
/*     */ 
/*     */         
/* 312 */         processImageProgress(100.0F * j / destinationRegion.height);
/*     */       } 
/*     */     } 
/*     */     
/* 316 */     if (abortRequested()) {
/* 317 */       processReadAborted();
/*     */     } else {
/* 319 */       processImageComplete();
/* 320 */     }  return bi;
/*     */   }
/*     */   
/*     */   public boolean canReadRaster() {
/* 324 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public Raster readRaster(int imageIndex, ImageReadParam param) throws IOException {
/* 329 */     BufferedImage bi = read(imageIndex, param);
/* 330 */     return bi.getData();
/*     */   }
/*     */   
/*     */   public void reset() {
/* 334 */     super.reset();
/* 335 */     this.iis = null;
/* 336 */     this.gotHeader = false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   boolean isValidWbmpType(int type) {
/* 344 */     return (type == 0);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\github\jaiimageio\impl\plugins\wbmp\WBMPImageReader.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */