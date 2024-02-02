/*     */ package com.github.jaiimageio.impl.plugins.gif;
/*     */ 
/*     */ import java.util.Arrays;
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
/*     */ 
/*     */ 
/*     */ public class GIFImageMetadataFormat
/*     */   extends IIOMetadataFormatImpl
/*     */ {
/*  56 */   private static IIOMetadataFormat instance = null;
/*     */   
/*     */   private GIFImageMetadataFormat() {
/*  59 */     super("javax_imageio_gif_image_1.0", 2);
/*     */ 
/*     */ 
/*     */     
/*  63 */     addElement("ImageDescriptor", "javax_imageio_gif_image_1.0", 0);
/*     */ 
/*     */     
/*  66 */     addAttribute("ImageDescriptor", "imageLeftPosition", 2, true, null, "0", "65535", true, true);
/*     */ 
/*     */     
/*  69 */     addAttribute("ImageDescriptor", "imageTopPosition", 2, true, null, "0", "65535", true, true);
/*     */ 
/*     */     
/*  72 */     addAttribute("ImageDescriptor", "imageWidth", 2, true, null, "1", "65535", true, true);
/*     */ 
/*     */     
/*  75 */     addAttribute("ImageDescriptor", "imageHeight", 2, true, null, "1", "65535", true, true);
/*     */ 
/*     */     
/*  78 */     addBooleanAttribute("ImageDescriptor", "interlaceFlag", false, false);
/*     */ 
/*     */ 
/*     */     
/*  82 */     addElement("LocalColorTable", "javax_imageio_gif_image_1.0", 2, 256);
/*     */ 
/*     */     
/*  85 */     addAttribute("LocalColorTable", "sizeOfLocalColorTable", 2, true, (String)null, 
/*     */         
/*  87 */         Arrays.asList(GIFStreamMetadata.colorTableSizes));
/*  88 */     addBooleanAttribute("LocalColorTable", "sortFlag", false, false);
/*     */ 
/*     */ 
/*     */     
/*  92 */     addElement("ColorTableEntry", "LocalColorTable", 0);
/*     */     
/*  94 */     addAttribute("ColorTableEntry", "index", 2, true, null, "0", "255", true, true);
/*     */ 
/*     */     
/*  97 */     addAttribute("ColorTableEntry", "red", 2, true, null, "0", "255", true, true);
/*     */ 
/*     */     
/* 100 */     addAttribute("ColorTableEntry", "green", 2, true, null, "0", "255", true, true);
/*     */ 
/*     */     
/* 103 */     addAttribute("ColorTableEntry", "blue", 2, true, null, "0", "255", true, true);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 108 */     addElement("GraphicControlExtension", "javax_imageio_gif_image_1.0", 0);
/*     */ 
/*     */     
/* 111 */     addAttribute("GraphicControlExtension", "disposalMethod", 0, true, (String)null, 
/*     */         
/* 113 */         Arrays.asList(GIFImageMetadata.disposalMethodNames));
/* 114 */     addBooleanAttribute("GraphicControlExtension", "userInputFlag", false, false);
/*     */     
/* 116 */     addBooleanAttribute("GraphicControlExtension", "transparentColorFlag", false, false);
/*     */     
/* 118 */     addAttribute("GraphicControlExtension", "delayTime", 2, true, null, "0", "65535", true, true);
/*     */ 
/*     */     
/* 121 */     addAttribute("GraphicControlExtension", "transparentColorIndex", 2, true, null, "0", "255", true, true);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 126 */     addElement("PlainTextExtension", "javax_imageio_gif_image_1.0", 0);
/*     */ 
/*     */     
/* 129 */     addAttribute("PlainTextExtension", "textGridLeft", 2, true, null, "0", "65535", true, true);
/*     */ 
/*     */     
/* 132 */     addAttribute("PlainTextExtension", "textGridTop", 2, true, null, "0", "65535", true, true);
/*     */ 
/*     */     
/* 135 */     addAttribute("PlainTextExtension", "textGridWidth", 2, true, null, "1", "65535", true, true);
/*     */ 
/*     */     
/* 138 */     addAttribute("PlainTextExtension", "textGridHeight", 2, true, null, "1", "65535", true, true);
/*     */ 
/*     */     
/* 141 */     addAttribute("PlainTextExtension", "characterCellWidth", 2, true, null, "1", "65535", true, true);
/*     */ 
/*     */     
/* 144 */     addAttribute("PlainTextExtension", "characterCellHeight", 2, true, null, "1", "65535", true, true);
/*     */ 
/*     */     
/* 147 */     addAttribute("PlainTextExtension", "textForegroundColor", 2, true, null, "0", "255", true, true);
/*     */ 
/*     */     
/* 150 */     addAttribute("PlainTextExtension", "textBackgroundColor", 2, true, null, "0", "255", true, true);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 155 */     addElement("ApplicationExtensions", "javax_imageio_gif_image_1.0", 1, 2147483647);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 160 */     addElement("ApplicationExtension", "ApplicationExtensions", 0);
/*     */     
/* 162 */     addAttribute("ApplicationExtension", "applicationID", 0, true, null);
/*     */     
/* 164 */     addAttribute("ApplicationExtension", "authenticationCode", 0, true, null);
/*     */     
/* 166 */     addObjectValue("ApplicationExtension", byte.class, 0, 2147483647);
/*     */ 
/*     */ 
/*     */     
/* 170 */     addElement("CommentExtensions", "javax_imageio_gif_image_1.0", 1, 2147483647);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 175 */     addElement("CommentExtension", "CommentExtensions", 0);
/*     */     
/* 177 */     addAttribute("CommentExtension", "value", 0, true, null);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean canNodeAppear(String elementName, ImageTypeSpecifier imageType) {
/* 183 */     return true;
/*     */   }
/*     */   
/*     */   public static synchronized IIOMetadataFormat getInstance() {
/* 187 */     if (instance == null) {
/* 188 */       instance = new GIFImageMetadataFormat();
/*     */     }
/* 190 */     return instance;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\github\jaiimageio\impl\plugins\gif\GIFImageMetadataFormat.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */