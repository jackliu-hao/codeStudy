/*     */ package com.github.jaiimageio.impl.plugins.pnm;
/*     */ 
/*     */ import com.github.jaiimageio.impl.common.PackageUtil;
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
/*     */ public class PNMImageWriterSpi
/*     */   extends ImageWriterSpi
/*     */ {
/*  59 */   private static String[] readerSpiNames = new String[] { "com.github.jaiimageio.impl.plugins.pnm.PNMImageReaderSpi" };
/*     */   
/*  61 */   private static String[] formatNames = new String[] { "pnm", "PNM" };
/*  62 */   private static String[] entensions = new String[] { "pbm", "pgm", "ppm" };
/*  63 */   private static String[] mimeType = new String[] { "image/x-portable-anymap", "image/x-portable-bitmap", "image/x-portable-graymap", "image/x-portable-pixmap" };
/*     */ 
/*     */   
/*     */   private boolean registered = false;
/*     */ 
/*     */ 
/*     */   
/*     */   public PNMImageWriterSpi() {
/*  71 */     super(PackageUtil.getVendor(), 
/*  72 */         PackageUtil.getVersion(), formatNames, entensions, mimeType, "com.github.jaiimageio.impl.plugins.pnm.PNMImageWriter", STANDARD_OUTPUT_TYPE, readerSpiNames, true, null, null, null, null, true, null, null, null, null);
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
/*     */   public String getDescription(Locale locale) {
/*  86 */     String desc = PackageUtil.getSpecificationTitle() + " PNM Image Writer";
/*     */     
/*  88 */     return desc;
/*     */   }
/*     */ 
/*     */   
/*     */   public void onRegistration(ServiceRegistry registry, Class category) {
/*  93 */     if (this.registered) {
/*     */       return;
/*     */     }
/*     */     
/*  97 */     this.registered = true;
/*     */   }
/*     */   
/*     */   public boolean canEncodeImage(ImageTypeSpecifier type) {
/* 101 */     int dataType = type.getSampleModel().getDataType();
/* 102 */     if (dataType < 0 || dataType > 3)
/*     */     {
/* 104 */       return false;
/*     */     }
/* 106 */     int numBands = type.getSampleModel().getNumBands();
/* 107 */     if (numBands != 1 && numBands != 3) {
/* 108 */       return false;
/*     */     }
/* 110 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public ImageWriter createWriterInstance(Object extension) throws IIOException {
/* 115 */     return new PNMImageWriter(this);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\github\jaiimageio\impl\plugins\pnm\PNMImageWriterSpi.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */