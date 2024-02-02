/*     */ package com.github.jaiimageio.impl.plugins.pcx;
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
/*     */ public class PCXImageReaderSpi
/*     */   extends ImageReaderSpi
/*     */ {
/*  60 */   private static String[] writerSpiNames = new String[] { "com.github.jaiimageio.impl.plugins.pcx.PCXImageWriterSpi" };
/*     */   
/*  62 */   private static String[] formatNames = new String[] { "pcx", "PCX" };
/*  63 */   private static String[] extensions = new String[] { "pcx" };
/*  64 */   private static String[] mimeTypes = new String[] { "image/pcx", "image/x-pcx", "image/x-windows-pcx", "image/x-pc-paintbrush" };
/*     */   
/*     */   private boolean registered = false;
/*     */ 
/*     */   
/*     */   public PCXImageReaderSpi() {
/*  70 */     super(PackageUtil.getVendor(), 
/*  71 */         PackageUtil.getVersion(), formatNames, extensions, mimeTypes, "com.github.jaiimageio.impl.plugins.pcx.PCXImageReader", STANDARD_INPUT_TYPE, writerSpiNames, false, null, null, null, null, true, null, null, null, null);
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
/*     */   public void onRegistration(ServiceRegistry registry, Class category) {
/*  87 */     if (this.registered) {
/*     */       return;
/*     */     }
/*  90 */     this.registered = true;
/*     */   }
/*     */   
/*     */   public String getDescription(Locale locale) {
/*  94 */     String desc = PackageUtil.getSpecificationTitle() + " PCX Image Reader";
/*     */     
/*  96 */     return desc;
/*     */   }
/*     */   
/*     */   public boolean canDecodeInput(Object source) throws IOException {
/* 100 */     if (!(source instanceof ImageInputStream)) {
/* 101 */       return false;
/*     */     }
/*     */     
/* 104 */     ImageInputStream stream = (ImageInputStream)source;
/* 105 */     stream.mark();
/* 106 */     byte b = stream.readByte();
/* 107 */     stream.reset();
/*     */     
/* 109 */     return (b == 10);
/*     */   }
/*     */ 
/*     */   
/*     */   public ImageReader createReaderInstance(Object extension) throws IIOException {
/* 114 */     return new PCXImageReader(this);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\github\jaiimageio\impl\plugins\pcx\PCXImageReaderSpi.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */