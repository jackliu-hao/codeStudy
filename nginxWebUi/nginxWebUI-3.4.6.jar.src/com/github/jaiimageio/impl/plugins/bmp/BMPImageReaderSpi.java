/*     */ package com.github.jaiimageio.impl.plugins.bmp;
/*     */ 
/*     */ import com.github.jaiimageio.impl.common.ImageUtil;
/*     */ import com.github.jaiimageio.impl.common.PackageUtil;
/*     */ import java.io.IOException;
/*     */ import java.util.Locale;
/*     */ import javax.imageio.IIOException;
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
/*     */ public class BMPImageReaderSpi
/*     */   extends ImageReaderSpi
/*     */ {
/*  61 */   private static String[] writerSpiNames = new String[] { "com.github.jaiimageio.impl.plugins.bmp.BMPImageWriterSpi" };
/*     */   
/*  63 */   private static String[] formatNames = new String[] { "bmp", "BMP" };
/*  64 */   private static String[] extensions = new String[] { "bmp" };
/*  65 */   private static String[] mimeTypes = new String[] { "image/bmp", "image/x-bmp", "image/x-windows-bmp" };
/*     */   
/*     */   private boolean registered = false;
/*     */ 
/*     */   
/*     */   public BMPImageReaderSpi() {
/*  71 */     super(PackageUtil.getVendor(), 
/*  72 */         PackageUtil.getVersion(), formatNames, extensions, mimeTypes, "com.github.jaiimageio.impl.plugins.bmp.BMPImageReader", STANDARD_INPUT_TYPE, writerSpiNames, false, null, null, null, null, true, "com_sun_media_imageio_plugins_bmp_image_1.0", "com.github.jaiimageio.impl.plugins.bmp.BMPMetadataFormat", null, null);
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
/*     */   public void onRegistration(ServiceRegistry registry, Class category) {
/*  89 */     if (this.registered) {
/*     */       return;
/*     */     }
/*  92 */     this.registered = true;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  97 */     ImageUtil.processOnRegistration(registry, category, "BMP", this, 8, 7);
/*     */   }
/*     */ 
/*     */   
/*     */   public String getDescription(Locale locale) {
/* 102 */     String desc = PackageUtil.getSpecificationTitle() + " BMP Image Reader";
/*     */     
/* 104 */     return desc;
/*     */   }
/*     */   
/*     */   public boolean canDecodeInput(Object source) throws IOException {
/* 108 */     if (!(source instanceof ImageInputStream)) {
/* 109 */       return false;
/*     */     }
/*     */     
/* 112 */     ImageInputStream stream = (ImageInputStream)source;
/* 113 */     byte[] b = new byte[2];
/* 114 */     stream.mark();
/* 115 */     stream.readFully(b);
/* 116 */     stream.reset();
/*     */     
/* 118 */     return (b[0] == 66 && b[1] == 77);
/*     */   }
/*     */ 
/*     */   
/*     */   public ImageReader createReaderInstance(Object extension) throws IIOException {
/* 123 */     return new BMPImageReader(this);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\github\jaiimageio\impl\plugins\bmp\BMPImageReaderSpi.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */