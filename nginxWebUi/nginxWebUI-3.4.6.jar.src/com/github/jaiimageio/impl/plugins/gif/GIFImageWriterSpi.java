/*     */ package com.github.jaiimageio.impl.plugins.gif;
/*     */ 
/*     */ import com.github.jaiimageio.impl.common.ImageUtil;
/*     */ import com.github.jaiimageio.impl.common.PackageUtil;
/*     */ import com.github.jaiimageio.impl.common.PaletteBuilder;
/*     */ import java.awt.image.ColorModel;
/*     */ import java.awt.image.SampleModel;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ public class GIFImageWriterSpi
/*     */   extends ImageWriterSpi
/*     */ {
/*     */   private static final String vendorName = "Sun Microsystems, Inc.";
/*     */   private static final String version = "1.0";
/*  67 */   private static final String[] names = new String[] { "gif", "GIF" };
/*     */   
/*  69 */   private static final String[] suffixes = new String[] { "gif" };
/*     */   
/*  71 */   private static final String[] MIMETypes = new String[] { "image/gif" };
/*     */ 
/*     */   
/*     */   private static final String writerClassName = "com.github.jaiimageio.impl.plugins.gif.GIFImageWriter";
/*     */   
/*  76 */   private static final String[] readerSpiNames = new String[] { "com.sun.imageio.plugins.gif.GIFImageReaderSpi" };
/*     */ 
/*     */   
/*     */   private boolean registered = false;
/*     */ 
/*     */   
/*     */   public GIFImageWriterSpi() {
/*  83 */     super("Sun Microsystems, Inc.", "1.0", names, suffixes, MIMETypes, "com.github.jaiimageio.impl.plugins.gif.GIFImageWriter", STANDARD_OUTPUT_TYPE, readerSpiNames, true, "javax_imageio_gif_stream_1.0", "com.github.jaiimageio.impl.plugins.gif.GIFStreamMetadataFormat", null, null, true, "javax_imageio_gif_image_1.0", "com.github.jaiimageio.impl.plugins.gif.GIFStreamMetadataFormat", null, null);
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
/*     */   
/*     */   public boolean canEncodeImage(ImageTypeSpecifier type) {
/* 103 */     if (type == null) {
/* 104 */       throw new IllegalArgumentException("type == null!");
/*     */     }
/*     */     
/* 107 */     SampleModel sm = type.getSampleModel();
/* 108 */     ColorModel cm = type.getColorModel();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 114 */     boolean canEncode = (sm.getNumBands() == 1 && sm.getSampleSize(0) <= 8 && sm.getWidth() <= 65535 && sm.getHeight() <= 65535 && (cm == null || cm.getComponentSize()[0] <= 8));
/*     */     
/* 116 */     if (canEncode) {
/* 117 */       return true;
/*     */     }
/* 119 */     return PaletteBuilder.canCreatePalette(type);
/*     */   }
/*     */ 
/*     */   
/*     */   public String getDescription(Locale locale) {
/* 124 */     String desc = PackageUtil.getSpecificationTitle() + " GIF Image Writer";
/*     */     
/* 126 */     return desc;
/*     */   }
/*     */ 
/*     */   
/*     */   public void onRegistration(ServiceRegistry registry, Class category) {
/* 131 */     if (this.registered) {
/*     */       return;
/*     */     }
/*     */     
/* 135 */     this.registered = true;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 140 */     ImageUtil.processOnRegistration(registry, category, "GIF", this, 9, 8);
/*     */   }
/*     */ 
/*     */   
/*     */   public ImageWriter createWriterInstance(Object extension) {
/* 145 */     return new GIFImageWriter(this);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\github\jaiimageio\impl\plugins\gif\GIFImageWriterSpi.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */