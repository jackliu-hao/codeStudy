/*     */ package com.github.jaiimageio.impl.plugins.bmp;
/*     */ 
/*     */ import com.github.jaiimageio.impl.common.ImageUtil;
/*     */ import com.github.jaiimageio.impl.common.PackageUtil;
/*     */ import java.awt.image.SampleModel;
/*     */ import java.util.Locale;
/*     */ import javax.imageio.IIOException;
/*     */ import javax.imageio.ImageTypeSpecifier;
/*     */ import javax.imageio.ImageWriter;
/*     */ import javax.imageio.spi.ImageWriterSpi;
/*     */ import javax.imageio.spi.ServiceRegistry;
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
/*     */ public class BMPImageWriterSpi
/*     */   extends ImageWriterSpi
/*     */ {
/*  62 */   private static String[] readerSpiNames = new String[] { "com.github.jaiimageio.impl.plugins.bmp.BMPImageReaderSpi" };
/*     */   
/*  64 */   private static String[] formatNames = new String[] { "bmp", "BMP" };
/*  65 */   private static String[] extensions = new String[] { "bmp" };
/*  66 */   private static String[] mimeTypes = new String[] { "image/bmp", "image/x-bmp", "image/x-windows-bmp" };
/*     */   
/*     */   private boolean registered = false;
/*     */ 
/*     */   
/*     */   public BMPImageWriterSpi() {
/*  72 */     super(PackageUtil.getVendor(), 
/*  73 */         PackageUtil.getVersion(), formatNames, extensions, mimeTypes, "com.github.jaiimageio.impl.plugins.bmp.BMPImageWriter", STANDARD_OUTPUT_TYPE, readerSpiNames, false, null, null, null, null, true, "com_sun_media_imageio_plugins_bmp_image_1.0", "com.github.jaiimageio.impl.plugins.bmp.BMPMetadataFormat", null, null);
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
/*     */   public String getDescription(Locale locale) {
/*  89 */     String desc = PackageUtil.getSpecificationTitle() + " BMP Image Writer";
/*     */     
/*  91 */     return desc;
/*     */   }
/*     */ 
/*     */   
/*     */   public void onRegistration(ServiceRegistry registry, Class category) {
/*  96 */     if (this.registered) {
/*     */       return;
/*     */     }
/*     */     
/* 100 */     this.registered = true;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 105 */     ImageUtil.processOnRegistration(registry, category, "BMP", this, 8, 7);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean canEncodeImage(ImageTypeSpecifier type) {
/* 110 */     int dataType = type.getSampleModel().getDataType();
/* 111 */     if (dataType < 0 || dataType > 3) {
/* 112 */       return false;
/*     */     }
/* 114 */     SampleModel sm = type.getSampleModel();
/* 115 */     int numBands = sm.getNumBands();
/* 116 */     if (numBands != 1 && numBands != 3) {
/* 117 */       return false;
/*     */     }
/* 119 */     if (numBands == 1 && dataType != 0) {
/* 120 */       return false;
/*     */     }
/* 122 */     if (dataType > 0 && !(sm instanceof java.awt.image.SinglePixelPackedSampleModel))
/*     */     {
/* 124 */       return false;
/*     */     }
/* 126 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public ImageWriter createWriterInstance(Object extension) throws IIOException {
/* 131 */     return new BMPImageWriter(this);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\github\jaiimageio\impl\plugins\bmp\BMPImageWriterSpi.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */