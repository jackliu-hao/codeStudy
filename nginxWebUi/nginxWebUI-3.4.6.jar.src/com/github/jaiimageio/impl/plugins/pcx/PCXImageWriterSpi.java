/*     */ package com.github.jaiimageio.impl.plugins.pcx;
/*     */ 
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
/*     */ public class PCXImageWriterSpi
/*     */   extends ImageWriterSpi
/*     */ {
/*  61 */   private static String[] readerSpiNames = new String[] { "com.github.jaiimageio.impl.plugins.pcx.PCXImageReaderSpi" };
/*     */   
/*  63 */   private static String[] formatNames = new String[] { "pcx", "PCX" };
/*  64 */   private static String[] extensions = new String[] { "pcx" };
/*  65 */   private static String[] mimeTypes = new String[] { "image/pcx", "image/x-pcx", "image/x-windows-pcx", "image/x-pc-paintbrush" };
/*     */   
/*     */   private boolean registered = false;
/*     */ 
/*     */   
/*     */   public PCXImageWriterSpi() {
/*  71 */     super(PackageUtil.getVendor(), 
/*  72 */         PackageUtil.getVersion(), formatNames, extensions, mimeTypes, "com.github.jaiimageio.impl.plugins.pcx.PCXImageWriter", STANDARD_OUTPUT_TYPE, readerSpiNames, false, null, null, null, null, true, null, null, null, null);
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
/*  88 */     String desc = PackageUtil.getSpecificationTitle() + " PCX Image Writer";
/*     */     
/*  90 */     return desc;
/*     */   }
/*     */   
/*     */   public void onRegistration(ServiceRegistry registry, Class category) {
/*  94 */     if (this.registered) {
/*     */       return;
/*     */     }
/*     */     
/*  98 */     this.registered = true;
/*     */   }
/*     */   
/*     */   public boolean canEncodeImage(ImageTypeSpecifier type) {
/* 102 */     int dataType = type.getSampleModel().getDataType();
/* 103 */     if (dataType < 0 || dataType > 3) {
/* 104 */       return false;
/*     */     }
/* 106 */     SampleModel sm = type.getSampleModel();
/* 107 */     int numBands = sm.getNumBands();
/* 108 */     if (numBands != 1 && numBands != 3) {
/* 109 */       return false;
/*     */     }
/* 111 */     if (numBands == 1 && dataType != 0) {
/* 112 */       return false;
/*     */     }
/* 114 */     if (dataType > 0 && !(sm instanceof java.awt.image.SinglePixelPackedSampleModel)) {
/* 115 */       return false;
/*     */     }
/* 117 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public ImageWriter createWriterInstance(Object extension) throws IIOException {
/* 122 */     return new PCXImageWriter(this);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\github\jaiimageio\impl\plugins\pcx\PCXImageWriterSpi.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */