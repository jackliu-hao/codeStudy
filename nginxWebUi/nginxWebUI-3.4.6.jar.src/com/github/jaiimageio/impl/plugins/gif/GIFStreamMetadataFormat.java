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
/*     */ public class GIFStreamMetadataFormat
/*     */   extends IIOMetadataFormatImpl
/*     */ {
/*  56 */   private static IIOMetadataFormat instance = null;
/*     */   
/*     */   private GIFStreamMetadataFormat() {
/*  59 */     super("javax_imageio_gif_stream_1.0", 2);
/*     */ 
/*     */ 
/*     */     
/*  63 */     addElement("Version", "javax_imageio_gif_stream_1.0", 0);
/*     */     
/*  65 */     addAttribute("Version", "value", 0, true, (String)null, 
/*     */         
/*  67 */         Arrays.asList(GIFStreamMetadata.versionStrings));
/*     */ 
/*     */     
/*  70 */     addElement("LogicalScreenDescriptor", "javax_imageio_gif_stream_1.0", 0);
/*     */ 
/*     */     
/*  73 */     addAttribute("LogicalScreenDescriptor", "logicalScreenWidth", 2, true, null, "1", "65535", true, true);
/*     */ 
/*     */     
/*  76 */     addAttribute("LogicalScreenDescriptor", "logicalScreenHeight", 2, true, null, "1", "65535", true, true);
/*     */ 
/*     */     
/*  79 */     addAttribute("LogicalScreenDescriptor", "colorResolution", 2, true, null, "1", "8", true, true);
/*     */ 
/*     */     
/*  82 */     addAttribute("LogicalScreenDescriptor", "pixelAspectRatio", 2, true, null, "0", "255", true, true);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  87 */     addElement("GlobalColorTable", "javax_imageio_gif_stream_1.0", 2, 256);
/*     */ 
/*     */     
/*  90 */     addAttribute("GlobalColorTable", "sizeOfGlobalColorTable", 2, true, (String)null, 
/*     */         
/*  92 */         Arrays.asList(GIFStreamMetadata.colorTableSizes));
/*  93 */     addAttribute("GlobalColorTable", "backgroundColorIndex", 2, true, null, "0", "255", true, true);
/*     */ 
/*     */     
/*  96 */     addBooleanAttribute("GlobalColorTable", "sortFlag", false, false);
/*     */ 
/*     */ 
/*     */     
/* 100 */     addElement("ColorTableEntry", "GlobalColorTable", 0);
/*     */     
/* 102 */     addAttribute("ColorTableEntry", "index", 2, true, null, "0", "255", true, true);
/*     */ 
/*     */     
/* 105 */     addAttribute("ColorTableEntry", "red", 2, true, null, "0", "255", true, true);
/*     */ 
/*     */     
/* 108 */     addAttribute("ColorTableEntry", "green", 2, true, null, "0", "255", true, true);
/*     */ 
/*     */     
/* 111 */     addAttribute("ColorTableEntry", "blue", 2, true, null, "0", "255", true, true);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean canNodeAppear(String elementName, ImageTypeSpecifier imageType) {
/* 118 */     return true;
/*     */   }
/*     */   
/*     */   public static synchronized IIOMetadataFormat getInstance() {
/* 122 */     if (instance == null) {
/* 123 */       instance = new GIFStreamMetadataFormat();
/*     */     }
/* 125 */     return instance;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\github\jaiimageio\impl\plugins\gif\GIFStreamMetadataFormat.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */