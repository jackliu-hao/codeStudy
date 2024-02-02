/*    */ package com.github.jaiimageio.impl.plugins.wbmp;
/*    */ 
/*    */ import javax.imageio.ImageTypeSpecifier;
/*    */ import javax.imageio.metadata.IIOMetadataFormat;
/*    */ import javax.imageio.metadata.IIOMetadataFormatImpl;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ class WBMPMetadataFormat
/*    */   extends IIOMetadataFormatImpl
/*    */ {
/* 54 */   private static IIOMetadataFormat instance = null;
/*    */   
/*    */   private WBMPMetadataFormat() {
/* 57 */     super("com_sun_media_imageio_plugins_wbmp_image_1.0", 2);
/*    */ 
/*    */ 
/*    */     
/* 61 */     addElement("ImageDescriptor", "com_sun_media_imageio_plugins_wbmp_image_1.0", 0);
/*    */ 
/*    */ 
/*    */     
/* 65 */     addAttribute("ImageDescriptor", "WBMPType", 2, true, "0");
/*    */ 
/*    */     
/* 68 */     addAttribute("ImageDescriptor", "Width", 2, true, null, "0", "65535", true, true);
/*    */ 
/*    */     
/* 71 */     addAttribute("ImageDescriptor", "Height", 2, true, null, "1", "65535", true, true);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean canNodeAppear(String elementName, ImageTypeSpecifier imageType) {
/* 80 */     return true;
/*    */   }
/*    */   
/*    */   public static synchronized IIOMetadataFormat getInstance() {
/* 84 */     if (instance == null) {
/* 85 */       instance = new WBMPMetadataFormat();
/*    */     }
/* 87 */     return instance;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\github\jaiimageio\impl\plugins\wbmp\WBMPMetadataFormat.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */