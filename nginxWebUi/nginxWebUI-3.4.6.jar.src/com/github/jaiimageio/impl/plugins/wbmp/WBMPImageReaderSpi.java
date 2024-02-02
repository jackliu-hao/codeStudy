/*     */ package com.github.jaiimageio.impl.plugins.wbmp;
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
/*     */ public class WBMPImageReaderSpi
/*     */   extends ImageReaderSpi
/*     */ {
/*  61 */   private static String[] writerSpiNames = new String[] { "com.github.jaiimageio.impl.plugins.wbmp.WBMPImageWriterSpi" };
/*     */   
/*  63 */   private static String[] formatNames = new String[] { "wbmp", "WBMP" };
/*  64 */   private static String[] entensions = new String[] { "wbmp" };
/*  65 */   private static String[] mimeType = new String[] { "image/vnd.wap.wbmp" };
/*     */   
/*     */   private boolean registered = false;
/*     */   
/*     */   public WBMPImageReaderSpi() {
/*  70 */     super(PackageUtil.getVendor(), 
/*  71 */         PackageUtil.getVersion(), formatNames, entensions, mimeType, "com.github.jaiimageio.impl.plugins.wbmp.WBMPImageReader", STANDARD_INPUT_TYPE, writerSpiNames, true, null, null, null, null, true, "com_sun_media_imageio_plugins_wbmp_image_1.0", "com.github.jaiimageio.impl.plugins.wbmp.WBMPMetadataFormat", null, null);
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
/*  88 */     if (this.registered) {
/*     */       return;
/*     */     }
/*  91 */     this.registered = true;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  96 */     ImageUtil.processOnRegistration(registry, category, "WBMP", this, 8, 7);
/*     */   }
/*     */ 
/*     */   
/*     */   public String getDescription(Locale locale) {
/* 101 */     String desc = PackageUtil.getSpecificationTitle() + " WBMP Image Reader";
/*     */     
/* 103 */     return desc;
/*     */   }
/*     */   
/*     */   public boolean canDecodeInput(Object source) throws IOException {
/* 107 */     if (!(source instanceof ImageInputStream)) {
/* 108 */       return false;
/*     */     }
/*     */     
/* 111 */     ImageInputStream stream = (ImageInputStream)source;
/*     */     
/* 113 */     stream.mark();
/* 114 */     int type = stream.readByte();
/* 115 */     byte fixHeaderField = stream.readByte();
/*     */     
/* 117 */     int width = ImageUtil.readMultiByteInteger(stream);
/* 118 */     int height = ImageUtil.readMultiByteInteger(stream);
/*     */     
/* 120 */     long remainingBytes = stream.length() - stream.getStreamPosition();
/* 121 */     stream.reset();
/*     */ 
/*     */     
/* 124 */     if (type != 0 || fixHeaderField != 0)
/*     */     {
/* 126 */       return false;
/*     */     }
/*     */ 
/*     */     
/* 130 */     if (width <= 0 || height <= 0) {
/* 131 */       return false;
/*     */     }
/*     */     
/* 134 */     long scanSize = (width / 8 + ((width % 8 == 0) ? 0 : 1));
/*     */     
/* 136 */     return (remainingBytes == scanSize * height);
/*     */   }
/*     */ 
/*     */   
/*     */   public ImageReader createReaderInstance(Object extension) throws IIOException {
/* 141 */     return new WBMPImageReader(this);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\github\jaiimageio\impl\plugins\wbmp\WBMPImageReaderSpi.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */