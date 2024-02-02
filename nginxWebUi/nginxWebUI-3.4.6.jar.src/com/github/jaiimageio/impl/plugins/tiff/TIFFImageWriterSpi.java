/*     */ package com.github.jaiimageio.impl.plugins.tiff;
/*     */ 
/*     */ import com.github.jaiimageio.impl.common.PackageUtil;
/*     */ import java.util.Locale;
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
/*     */ public class TIFFImageWriterSpi
/*     */   extends ImageWriterSpi
/*     */ {
/*  58 */   private static final String[] names = new String[] { "tif", "TIF", "tiff", "TIFF" };
/*     */   
/*  60 */   private static final String[] suffixes = new String[] { "tif", "tiff" };
/*     */   
/*  62 */   private static final String[] MIMETypes = new String[] { "image/tiff" };
/*     */ 
/*     */   
/*     */   private static final String writerClassName = "com.github.jaiimageio.impl.plugins.tiff.TIFFImageWriter";
/*     */   
/*  67 */   private static final String[] readerSpiNames = new String[] { "com.github.jaiimageio.impl.plugins.tiff.TIFFImageReaderSpi" };
/*     */ 
/*     */   
/*     */   private boolean registered = false;
/*     */ 
/*     */   
/*     */   public TIFFImageWriterSpi() {
/*  74 */     super(PackageUtil.getVendor(), 
/*  75 */         PackageUtil.getVersion(), names, suffixes, MIMETypes, "com.github.jaiimageio.impl.plugins.tiff.TIFFImageWriter", STANDARD_OUTPUT_TYPE, readerSpiNames, false, "com_sun_media_imageio_plugins_tiff_stream_1.0", "com.github.jaiimageio.impl.plugins.tiff.TIFFStreamMetadataFormat", null, null, false, "com_sun_media_imageio_plugins_tiff_image_1.0", "com.github.jaiimageio.impl.plugins.tiff.TIFFImageMetadataFormat", null, null);
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
/*     */   public boolean canEncodeImage(ImageTypeSpecifier type) {
/*  94 */     return true;
/*     */   }
/*     */   
/*     */   public String getDescription(Locale locale) {
/*  98 */     String desc = PackageUtil.getSpecificationTitle() + " TIFF Image Writer";
/*     */     
/* 100 */     return desc;
/*     */   }
/*     */   
/*     */   public ImageWriter createWriterInstance(Object extension) {
/* 104 */     return new TIFFImageWriter(this);
/*     */   }
/*     */ 
/*     */   
/*     */   public void onRegistration(ServiceRegistry registry, Class category) {
/* 109 */     if (this.registered) {
/*     */       return;
/*     */     }
/*     */     
/* 113 */     this.registered = true;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\github\jaiimageio\impl\plugins\tiff\TIFFImageWriterSpi.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */