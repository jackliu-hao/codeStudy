/*     */ package com.github.jaiimageio.impl.plugins.tiff;
/*     */ 
/*     */ import com.github.jaiimageio.plugins.tiff.TIFFImageReadParam;
/*     */ import com.github.jaiimageio.plugins.tiff.TIFFTagSet;
/*     */ import java.awt.Image;
/*     */ import java.awt.Rectangle;
/*     */ import java.awt.image.BufferedImage;
/*     */ import java.awt.image.ColorModel;
/*     */ import java.awt.image.Raster;
/*     */ import java.awt.image.RenderedImage;
/*     */ import java.awt.image.SampleModel;
/*     */ import java.awt.image.WritableRaster;
/*     */ import java.io.IOException;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Vector;
/*     */ import javax.imageio.ImageReadParam;
/*     */ import javax.imageio.ImageTypeSpecifier;
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
/*     */ 
/*     */ public class TIFFRenderedImage
/*     */   implements RenderedImage
/*     */ {
/*     */   TIFFImageReader reader;
/*     */   int imageIndex;
/*     */   ImageReadParam tileParam;
/*     */   int subsampleX;
/*     */   int subsampleY;
/*     */   boolean isSubsampling;
/*     */   int width;
/*     */   int height;
/*     */   int tileWidth;
/*     */   int tileHeight;
/*     */   ImageTypeSpecifier its;
/*     */   
/*     */   public TIFFRenderedImage(TIFFImageReader reader, int imageIndex, ImageReadParam readParam, int width, int height) throws IOException {
/*  87 */     this.reader = reader;
/*  88 */     this.imageIndex = imageIndex;
/*  89 */     this.tileParam = cloneImageReadParam(readParam, false);
/*     */     
/*  91 */     this.subsampleX = this.tileParam.getSourceXSubsampling();
/*  92 */     this.subsampleY = this.tileParam.getSourceYSubsampling();
/*     */     
/*  94 */     this.isSubsampling = (this.subsampleX != 1 || this.subsampleY != 1);
/*     */     
/*  96 */     this.width = width / this.subsampleX;
/*  97 */     this.height = height / this.subsampleY;
/*     */ 
/*     */ 
/*     */     
/* 101 */     this.tileWidth = reader.getTileWidth(imageIndex) / this.subsampleX;
/* 102 */     this.tileHeight = reader.getTileHeight(imageIndex) / this.subsampleY;
/*     */     
/* 104 */     Iterator<ImageTypeSpecifier> iter = reader.getImageTypes(imageIndex);
/* 105 */     this.its = iter.next();
/* 106 */     this.tileParam.setDestinationType(this.its);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private ImageReadParam cloneImageReadParam(ImageReadParam param, boolean copyTagSets) {
/* 125 */     TIFFImageReadParam newParam = new TIFFImageReadParam();
/*     */ 
/*     */     
/* 128 */     newParam.setSourceSubsampling(param.getSourceXSubsampling(), param
/* 129 */         .getSourceYSubsampling(), param
/* 130 */         .getSubsamplingXOffset(), param
/* 131 */         .getSubsamplingYOffset());
/* 132 */     newParam.setSourceBands(param.getSourceBands());
/* 133 */     newParam.setDestinationBands(param.getDestinationBands());
/* 134 */     newParam.setDestinationOffset(param.getDestinationOffset());
/*     */ 
/*     */     
/* 137 */     if (param instanceof TIFFImageReadParam) {
/*     */       
/* 139 */       TIFFImageReadParam tparam = (TIFFImageReadParam)param;
/* 140 */       newParam.setTIFFDecompressor(tparam.getTIFFDecompressor());
/* 141 */       newParam.setColorConverter(tparam.getColorConverter());
/*     */       
/* 143 */       if (copyTagSets) {
/* 144 */         List tagSets = tparam.getAllowedTagSets();
/* 145 */         if (tagSets != null) {
/* 146 */           Iterator<TIFFTagSet> tagSetIter = tagSets.iterator();
/* 147 */           if (tagSetIter != null) {
/* 148 */             while (tagSetIter.hasNext()) {
/* 149 */               TIFFTagSet tagSet = tagSetIter.next();
/* 150 */               newParam.addAllowedTagSet(tagSet);
/*     */             } 
/*     */           }
/*     */         } 
/*     */       } 
/*     */     } else {
/*     */       
/* 157 */       newParam.setTIFFDecompressor(null);
/* 158 */       newParam.setColorConverter(null);
/*     */     } 
/*     */     
/* 161 */     return (ImageReadParam)newParam;
/*     */   }
/*     */   
/*     */   public Vector getSources() {
/* 165 */     return null;
/*     */   }
/*     */   
/*     */   public Object getProperty(String name) {
/* 169 */     return Image.UndefinedProperty;
/*     */   }
/*     */   
/*     */   public String[] getPropertyNames() {
/* 173 */     return null;
/*     */   }
/*     */   
/*     */   public ColorModel getColorModel() {
/* 177 */     return this.its.getColorModel();
/*     */   }
/*     */   
/*     */   public SampleModel getSampleModel() {
/* 181 */     return this.its.getSampleModel();
/*     */   }
/*     */   
/*     */   public int getWidth() {
/* 185 */     return this.width;
/*     */   }
/*     */   
/*     */   public int getHeight() {
/* 189 */     return this.height;
/*     */   }
/*     */   
/*     */   public int getMinX() {
/* 193 */     return 0;
/*     */   }
/*     */   
/*     */   public int getMinY() {
/* 197 */     return 0;
/*     */   }
/*     */   
/*     */   public int getNumXTiles() {
/* 201 */     return (this.width + this.tileWidth - 1) / this.tileWidth;
/*     */   }
/*     */   
/*     */   public int getNumYTiles() {
/* 205 */     return (this.height + this.tileHeight - 1) / this.tileHeight;
/*     */   }
/*     */   
/*     */   public int getMinTileX() {
/* 209 */     return 0;
/*     */   }
/*     */   
/*     */   public int getMinTileY() {
/* 213 */     return 0;
/*     */   }
/*     */   
/*     */   public int getTileWidth() {
/* 217 */     return this.tileWidth;
/*     */   }
/*     */   
/*     */   public int getTileHeight() {
/* 221 */     return this.tileHeight;
/*     */   }
/*     */   
/*     */   public int getTileGridXOffset() {
/* 225 */     return 0;
/*     */   }
/*     */   
/*     */   public int getTileGridYOffset() {
/* 229 */     return 0;
/*     */   }
/*     */   
/*     */   public Raster getTile(int tileX, int tileY) {
/* 233 */     Rectangle tileRect = new Rectangle(tileX * this.tileWidth, tileY * this.tileHeight, this.tileWidth, this.tileHeight);
/*     */ 
/*     */ 
/*     */     
/* 237 */     return getData(tileRect);
/*     */   }
/*     */   
/*     */   public Raster getData() {
/* 241 */     return read(new Rectangle(0, 0, getWidth(), getHeight()));
/*     */   }
/*     */   
/*     */   public Raster getData(Rectangle rect) {
/* 245 */     return read(rect);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized WritableRaster read(Rectangle rect) {
/* 253 */     this.tileParam.setSourceRegion(this.isSubsampling ? new Rectangle(this.subsampleX * rect.x, this.subsampleY * rect.y, this.subsampleX * rect.width, this.subsampleY * rect.height) : rect);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     try {
/* 261 */       BufferedImage bi = this.reader.read(this.imageIndex, this.tileParam);
/* 262 */       WritableRaster ras = bi.getRaster();
/* 263 */       return ras.createWritableChild(0, 0, ras
/* 264 */           .getWidth(), ras.getHeight(), rect.x, rect.y, (int[])null);
/*     */     
/*     */     }
/* 267 */     catch (IOException e) {
/* 268 */       throw new RuntimeException(e);
/*     */     } 
/*     */   }
/*     */   
/*     */   public WritableRaster copyData(WritableRaster raster) {
/* 273 */     if (raster == null) {
/* 274 */       return read(new Rectangle(0, 0, getWidth(), getHeight()));
/*     */     }
/* 276 */     Raster src = read(raster.getBounds());
/* 277 */     raster.setRect(src);
/* 278 */     return raster;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\github\jaiimageio\impl\plugins\tiff\TIFFRenderedImage.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */