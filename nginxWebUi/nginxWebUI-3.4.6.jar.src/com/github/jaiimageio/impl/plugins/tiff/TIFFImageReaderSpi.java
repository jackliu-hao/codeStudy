/*     */ package com.github.jaiimageio.impl.plugins.tiff;
/*     */ 
/*     */ import com.github.jaiimageio.impl.common.PackageUtil;
/*     */ import java.io.IOException;
/*     */ import java.util.Locale;
/*     */ import javax.imageio.ImageReader;
/*     */ import javax.imageio.spi.ImageReaderSpi;
/*     */ import javax.imageio.spi.ServiceRegistry;
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
/*     */ public class TIFFImageReaderSpi
/*     */   extends ImageReaderSpi
/*     */ {
/*  59 */   private static final String[] names = new String[] { "tif", "TIF", "tiff", "TIFF" };
/*     */   
/*  61 */   private static final String[] suffixes = new String[] { "tif", "tiff" };
/*     */   
/*  63 */   private static final String[] MIMETypes = new String[] { "image/tiff" };
/*     */ 
/*     */   
/*     */   private static final String readerClassName = "com.github.jaiimageio.impl.plugins.tiff.TIFFImageReader";
/*     */   
/*  68 */   private static final String[] writerSpiNames = new String[] { "com.github.jaiimageio.impl.plugins.tiff.TIFFImageWriterSpi" };
/*     */ 
/*     */   
/*     */   private boolean registered = false;
/*     */ 
/*     */   
/*     */   public TIFFImageReaderSpi() {
/*  75 */     super(PackageUtil.getVendor(), 
/*  76 */         PackageUtil.getVersion(), names, suffixes, MIMETypes, "com.github.jaiimageio.impl.plugins.tiff.TIFFImageReader", STANDARD_INPUT_TYPE, writerSpiNames, false, "com_sun_media_imageio_plugins_tiff_stream_1.0", "com.github.jaiimageio.impl.plugins.tiff.TIFFStreamMetadataFormat", null, null, true, "com_sun_media_imageio_plugins_tiff_image_1.0", "com.github.jaiimageio.impl.plugins.tiff.TIFFImageMetadataFormat", null, null);
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
/*     */   public String getDescription(Locale locale) {
/*  95 */     String desc = PackageUtil.getSpecificationTitle() + " TIFF Image Reader";
/*     */     
/*  97 */     return desc;
/*     */   }
/*     */   
/*     */   public boolean canDecodeInput(Object input) throws IOException {
/* 101 */     if (!(input instanceof ImageInputStream)) {
/* 102 */       return false;
/*     */     }
/*     */     
/* 105 */     ImageInputStream stream = (ImageInputStream)input;
/* 106 */     byte[] b = new byte[4];
/* 107 */     stream.mark();
/* 108 */     stream.readFully(b);
/* 109 */     stream.reset();
/*     */     
/* 111 */     return ((b[0] == 73 && b[1] == 73 && b[2] == 42 && b[3] == 0) || (b[0] == 77 && b[1] == 77 && b[2] == 0 && b[3] == 42));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ImageReader createReaderInstance(Object extension) {
/* 118 */     return new TIFFImageReader(this);
/*     */   }
/*     */ 
/*     */   
/*     */   public void onRegistration(ServiceRegistry registry, Class category) {
/* 123 */     if (this.registered) {
/*     */       return;
/*     */     }
/*     */     
/* 127 */     this.registered = true;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\github\jaiimageio\impl\plugins\tiff\TIFFImageReaderSpi.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */