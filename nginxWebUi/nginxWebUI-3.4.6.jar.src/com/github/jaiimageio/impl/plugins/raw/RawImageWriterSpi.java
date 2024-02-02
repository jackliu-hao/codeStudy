/*     */ package com.github.jaiimageio.impl.plugins.raw;
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
/*     */ public class RawImageWriterSpi
/*     */   extends ImageWriterSpi
/*     */ {
/*  58 */   private static String[] readerSpiNames = new String[] { "com.github.jaiimageio.impl.plugins.raw.RawImageReaderSpi" };
/*     */   
/*  60 */   private static String[] formatNames = new String[] { "raw", "RAW" };
/*  61 */   private static String[] entensions = new String[] { "" };
/*  62 */   private static String[] mimeType = new String[] { "" };
/*     */   
/*     */   private boolean registered = false;
/*     */   
/*     */   public RawImageWriterSpi() {
/*  67 */     super(PackageUtil.getVendor(), 
/*  68 */         PackageUtil.getVersion(), formatNames, entensions, mimeType, "com.github.jaiimageio.impl.plugins.raw.RawImageWriter", STANDARD_OUTPUT_TYPE, readerSpiNames, true, null, null, null, null, true, null, null, null, null);
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
/*  82 */     String desc = PackageUtil.getSpecificationTitle() + " Raw Image Writer";
/*     */     
/*  84 */     return desc;
/*     */   }
/*     */ 
/*     */   
/*     */   public void onRegistration(ServiceRegistry registry, Class category) {
/*  89 */     if (this.registered) {
/*     */       return;
/*     */     }
/*     */     
/*  93 */     this.registered = true;
/*     */   }
/*     */   
/*     */   public boolean canEncodeImage(ImageTypeSpecifier type) {
/*  97 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public ImageWriter createWriterInstance(Object extension) throws IIOException {
/* 102 */     return new RawImageWriter(this);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\github\jaiimageio\impl\plugins\raw\RawImageWriterSpi.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */