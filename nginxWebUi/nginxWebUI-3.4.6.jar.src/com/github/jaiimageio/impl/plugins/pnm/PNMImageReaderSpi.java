/*     */ package com.github.jaiimageio.impl.plugins.pnm;
/*     */ 
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
/*     */ public class PNMImageReaderSpi
/*     */   extends ImageReaderSpi
/*     */ {
/*  60 */   private static String[] writerSpiNames = new String[] { "com.github.jaiimageio.impl.plugins.pnm.PNMImageWriterSpi" };
/*     */   
/*  62 */   private static String[] formatNames = new String[] { "pnm", "PNM" };
/*  63 */   private static String[] entensions = new String[] { "pbm", "pgm", "ppm" };
/*  64 */   private static String[] mimeType = new String[] { "image/x-portable-anymap", "image/x-portable-bitmap", "image/x-portable-graymap", "image/x-portable-pixmap" };
/*     */ 
/*     */   
/*     */   private boolean registered = false;
/*     */ 
/*     */ 
/*     */   
/*     */   public PNMImageReaderSpi() {
/*  72 */     super(PackageUtil.getVendor(), 
/*  73 */         PackageUtil.getVersion(), formatNames, entensions, mimeType, "com.github.jaiimageio.impl.plugins.pnm.PNMImageReader", STANDARD_INPUT_TYPE, writerSpiNames, true, null, null, null, null, true, null, null, null, null);
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
/*     */   public void onRegistration(ServiceRegistry registry, Class category) {
/*  88 */     if (this.registered) {
/*     */       return;
/*     */     }
/*  91 */     this.registered = true;
/*     */   }
/*     */   
/*     */   public String getDescription(Locale locale) {
/*  95 */     String desc = PackageUtil.getSpecificationTitle() + " PNM Image Reader";
/*     */     
/*  97 */     return desc;
/*     */   }
/*     */   
/*     */   public boolean canDecodeInput(Object source) throws IOException {
/* 101 */     if (!(source instanceof ImageInputStream)) {
/* 102 */       return false;
/*     */     }
/*     */     
/* 105 */     ImageInputStream stream = (ImageInputStream)source;
/* 106 */     byte[] b = new byte[2];
/*     */     
/* 108 */     stream.mark();
/* 109 */     stream.readFully(b);
/* 110 */     stream.reset();
/*     */     
/* 112 */     return (b[0] == 80 && b[1] >= 49 && b[1] <= 54);
/*     */   }
/*     */ 
/*     */   
/*     */   public ImageReader createReaderInstance(Object extension) throws IIOException {
/* 117 */     return new PNMImageReader(this);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\github\jaiimageio\impl\plugins\pnm\PNMImageReaderSpi.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */