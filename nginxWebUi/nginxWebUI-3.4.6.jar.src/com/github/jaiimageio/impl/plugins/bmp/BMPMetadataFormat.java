/*     */ package com.github.jaiimageio.impl.plugins.bmp;
/*     */ 
/*     */ import javax.imageio.ImageTypeSpecifier;
/*     */ import javax.imageio.metadata.IIOMetadataFormat;
/*     */ import javax.imageio.metadata.IIOMetadataFormatImpl;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class BMPMetadataFormat
/*     */   extends IIOMetadataFormatImpl
/*     */ {
/*  53 */   private static IIOMetadataFormat instance = null;
/*     */   
/*     */   private BMPMetadataFormat() {
/*  56 */     super("com_sun_media_imageio_plugins_bmp_image_1.0", 2);
/*     */ 
/*     */ 
/*     */     
/*  60 */     addElement("ImageDescriptor", "com_sun_media_imageio_plugins_bmp_image_1.0", 0);
/*     */ 
/*     */     
/*  63 */     addAttribute("ImageDescriptor", "bmpVersion", 0, true, null);
/*     */     
/*  65 */     addAttribute("ImageDescriptor", "width", 2, true, null, "0", "65535", true, true);
/*     */ 
/*     */     
/*  68 */     addAttribute("ImageDescriptor", "height", 2, true, null, "1", "65535", true, true);
/*     */ 
/*     */     
/*  71 */     addAttribute("ImageDescriptor", "bitsPerPixel", 2, true, null, "1", "65535", true, true);
/*     */ 
/*     */     
/*  74 */     addAttribute("ImageDescriptor", "compression", 2, false, null);
/*     */     
/*  76 */     addAttribute("ImageDescriptor", "imageSize", 2, true, null, "1", "65535", true, true);
/*     */ 
/*     */ 
/*     */     
/*  80 */     addElement("PixelsPerMeter", "com_sun_media_imageio_plugins_bmp_image_1.0", 0);
/*     */ 
/*     */     
/*  83 */     addAttribute("PixelsPerMeter", "X", 2, false, null, "1", "65535", true, true);
/*     */ 
/*     */     
/*  86 */     addAttribute("PixelsPerMeter", "Y", 2, false, null, "1", "65535", true, true);
/*     */ 
/*     */ 
/*     */     
/*  90 */     addElement("ColorsUsed", "com_sun_media_imageio_plugins_bmp_image_1.0", 0);
/*     */ 
/*     */     
/*  93 */     addAttribute("ColorsUsed", "value", 2, true, null, "0", "65535", true, true);
/*     */ 
/*     */ 
/*     */     
/*  97 */     addElement("ColorsImportant", "com_sun_media_imageio_plugins_bmp_image_1.0", 0);
/*     */ 
/*     */     
/* 100 */     addAttribute("ColorsImportant", "value", 2, false, null, "0", "65535", true, true);
/*     */ 
/*     */ 
/*     */     
/* 104 */     addElement("BI_BITFIELDS_Mask", "com_sun_media_imageio_plugins_bmp_image_1.0", 0);
/*     */ 
/*     */     
/* 107 */     addAttribute("BI_BITFIELDS_Mask", "red", 2, false, null, "0", "65535", true, true);
/*     */ 
/*     */     
/* 110 */     addAttribute("BI_BITFIELDS_Mask", "green", 2, false, null, "0", "65535", true, true);
/*     */ 
/*     */     
/* 113 */     addAttribute("BI_BITFIELDS_Mask", "blue", 2, false, null, "0", "65535", true, true);
/*     */ 
/*     */ 
/*     */     
/* 117 */     addElement("ColorSpace", "com_sun_media_imageio_plugins_bmp_image_1.0", 0);
/*     */ 
/*     */     
/* 120 */     addAttribute("ColorSpace", "value", 2, false, null, "0", "65535", true, true);
/*     */ 
/*     */ 
/*     */     
/* 124 */     addElement("LCS_CALIBRATED_RGB", "com_sun_media_imageio_plugins_bmp_image_1.0", 0);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 129 */     addAttribute("LCS_CALIBRATED_RGB", "redX", 4, false, null, "0", "65535", true, true);
/*     */ 
/*     */     
/* 132 */     addAttribute("LCS_CALIBRATED_RGB", "redY", 4, false, null, "0", "65535", true, true);
/*     */ 
/*     */     
/* 135 */     addAttribute("LCS_CALIBRATED_RGB", "redZ", 4, false, null, "0", "65535", true, true);
/*     */ 
/*     */     
/* 138 */     addAttribute("LCS_CALIBRATED_RGB", "greenX", 4, false, null, "0", "65535", true, true);
/*     */ 
/*     */     
/* 141 */     addAttribute("LCS_CALIBRATED_RGB", "greenY", 4, false, null, "0", "65535", true, true);
/*     */ 
/*     */     
/* 144 */     addAttribute("LCS_CALIBRATED_RGB", "greenZ", 4, false, null, "0", "65535", true, true);
/*     */ 
/*     */     
/* 147 */     addAttribute("LCS_CALIBRATED_RGB", "blueX", 4, false, null, "0", "65535", true, true);
/*     */ 
/*     */     
/* 150 */     addAttribute("LCS_CALIBRATED_RGB", "blueY", 4, false, null, "0", "65535", true, true);
/*     */ 
/*     */     
/* 153 */     addAttribute("LCS_CALIBRATED_RGB", "blueZ", 4, false, null, "0", "65535", true, true);
/*     */ 
/*     */ 
/*     */     
/* 157 */     addElement("LCS_CALIBRATED_RGB_GAMMA", "com_sun_media_imageio_plugins_bmp_image_1.0", 0);
/*     */ 
/*     */     
/* 160 */     addAttribute("LCS_CALIBRATED_RGB_GAMMA", "red", 2, false, null, "0", "65535", true, true);
/*     */ 
/*     */     
/* 163 */     addAttribute("LCS_CALIBRATED_RGB_GAMMA", "green", 2, false, null, "0", "65535", true, true);
/*     */ 
/*     */     
/* 166 */     addAttribute("LCS_CALIBRATED_RGB_GAMMA", "blue", 2, false, null, "0", "65535", true, true);
/*     */ 
/*     */ 
/*     */     
/* 170 */     addElement("Intent", "com_sun_media_imageio_plugins_bmp_image_1.0", 0);
/*     */ 
/*     */     
/* 173 */     addAttribute("Intent", "value", 2, false, null, "0", "65535", true, true);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 178 */     addElement("Palette", "com_sun_media_imageio_plugins_bmp_image_1.0", 2, 256);
/*     */ 
/*     */     
/* 181 */     addAttribute("Palette", "sizeOfPalette", 2, true, null);
/*     */     
/* 183 */     addBooleanAttribute("Palette", "sortFlag", false, false);
/*     */ 
/*     */ 
/*     */     
/* 187 */     addElement("PaletteEntry", "Palette", 0);
/*     */     
/* 189 */     addAttribute("PaletteEntry", "index", 2, true, null, "0", "255", true, true);
/*     */ 
/*     */     
/* 192 */     addAttribute("PaletteEntry", "red", 2, true, null, "0", "255", true, true);
/*     */ 
/*     */     
/* 195 */     addAttribute("PaletteEntry", "green", 2, true, null, "0", "255", true, true);
/*     */ 
/*     */     
/* 198 */     addAttribute("PaletteEntry", "blue", 2, true, null, "0", "255", true, true);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 204 */     addElement("CommentExtensions", "com_sun_media_imageio_plugins_bmp_image_1.0", 1, 2147483647);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 209 */     addElement("CommentExtension", "CommentExtensions", 0);
/*     */     
/* 211 */     addAttribute("CommentExtension", "value", 0, true, null);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean canNodeAppear(String elementName, ImageTypeSpecifier imageType) {
/* 217 */     return true;
/*     */   }
/*     */   
/*     */   public static synchronized IIOMetadataFormat getInstance() {
/* 221 */     if (instance == null) {
/* 222 */       instance = new BMPMetadataFormat();
/*     */     }
/* 224 */     return instance;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\github\jaiimageio\impl\plugins\bmp\BMPMetadataFormat.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */