/*     */ package com.github.jaiimageio.impl.plugins.png;
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
/*     */ public class CLibPNGMetadataFormat
/*     */   extends IIOMetadataFormatImpl
/*     */ {
/*  56 */   private static IIOMetadataFormat instance = null;
/*     */   
/*  58 */   private static String VALUE_0 = "0";
/*  59 */   private static String VALUE_1 = "1";
/*  60 */   private static String VALUE_12 = "12";
/*  61 */   private static String VALUE_23 = "23";
/*  62 */   private static String VALUE_31 = "31";
/*  63 */   private static String VALUE_59 = "59";
/*  64 */   private static String VALUE_60 = "60";
/*  65 */   private static String VALUE_255 = "255";
/*  66 */   private static String VALUE_MAX_16 = "65535";
/*  67 */   private static String VALUE_MAX_32 = "2147483647";
/*     */   
/*     */   private CLibPNGMetadataFormat() {
/*  70 */     super("javax_imageio_png_1.0", 2);
/*     */ 
/*     */ 
/*     */     
/*  74 */     addElement("IHDR", "javax_imageio_png_1.0", 0);
/*     */ 
/*     */     
/*  77 */     addAttribute("IHDR", "width", 2, true, null, VALUE_1, VALUE_MAX_32, true, true);
/*     */ 
/*     */ 
/*     */     
/*  81 */     addAttribute("IHDR", "height", 2, true, null, VALUE_1, VALUE_MAX_32, true, true);
/*     */ 
/*     */ 
/*     */     
/*  85 */     addAttribute("IHDR", "bitDepth", 2, true, (String)null, 
/*     */         
/*  87 */         Arrays.asList(CLibPNGMetadata.IHDR_bitDepths));
/*     */     
/*  89 */     String[] colorTypes = { "Grayscale", "RGB", "Palette", "GrayAlpha", "RGBAlpha" };
/*     */ 
/*     */     
/*  92 */     addAttribute("IHDR", "colorType", 0, true, (String)null, 
/*     */         
/*  94 */         Arrays.asList(colorTypes));
/*     */     
/*  96 */     addAttribute("IHDR", "compressionMethod", 0, true, (String)null, 
/*     */         
/*  98 */         Arrays.asList(CLibPNGMetadata.IHDR_compressionMethodNames));
/*     */     
/* 100 */     addAttribute("IHDR", "filterMethod", 0, true, (String)null, 
/*     */         
/* 102 */         Arrays.asList(CLibPNGMetadata.IHDR_filterMethodNames));
/*     */     
/* 104 */     addAttribute("IHDR", "interlaceMethod", 0, true, (String)null, 
/*     */         
/* 106 */         Arrays.asList(CLibPNGMetadata.IHDR_interlaceMethodNames));
/*     */ 
/*     */     
/* 109 */     addElement("PLTE", "javax_imageio_png_1.0", 1, 256);
/*     */ 
/*     */ 
/*     */     
/* 113 */     addElement("PLTEEntry", "PLTE", 0);
/*     */ 
/*     */     
/* 116 */     addAttribute("PLTEEntry", "index", 2, true, null, VALUE_0, VALUE_255, true, true);
/*     */ 
/*     */ 
/*     */     
/* 120 */     addAttribute("PLTEEntry", "red", 2, true, null, VALUE_0, VALUE_255, true, true);
/*     */ 
/*     */ 
/*     */     
/* 124 */     addAttribute("PLTEEntry", "green", 2, true, null, VALUE_0, VALUE_255, true, true);
/*     */ 
/*     */ 
/*     */     
/* 128 */     addAttribute("PLTEEntry", "blue", 2, true, null, VALUE_0, VALUE_255, true, true);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 133 */     addElement("bKGD", "javax_imageio_png_1.0", 3);
/*     */ 
/*     */ 
/*     */     
/* 137 */     addElement("bKGD_Grayscale", "bKGD", 0);
/*     */ 
/*     */     
/* 140 */     addAttribute("bKGD_Grayscale", "gray", 2, true, null, VALUE_0, VALUE_MAX_16, true, true);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 145 */     addElement("bKGD_RGB", "bKGD", 0);
/*     */ 
/*     */     
/* 148 */     addAttribute("bKGD_RGB", "red", 2, true, null, VALUE_0, VALUE_MAX_16, true, true);
/*     */ 
/*     */ 
/*     */     
/* 152 */     addAttribute("bKGD_RGB", "green", 2, true, null, VALUE_0, VALUE_MAX_16, true, true);
/*     */ 
/*     */ 
/*     */     
/* 156 */     addAttribute("bKGD_RGB", "blue", 2, true, null, VALUE_0, VALUE_MAX_16, true, true);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 161 */     addElement("bKGD_Palette", "bKGD", 0);
/*     */ 
/*     */     
/* 164 */     addAttribute("bKGD_Palette", "index", 2, true, null, VALUE_0, VALUE_255, true, true);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 169 */     addElement("cHRM", "javax_imageio_png_1.0", 0);
/*     */ 
/*     */     
/* 172 */     addAttribute("cHRM", "whitePointX", 2, true, null, VALUE_0, VALUE_MAX_16, true, true);
/*     */ 
/*     */ 
/*     */     
/* 176 */     addAttribute("cHRM", "whitePointY", 2, true, null, VALUE_0, VALUE_MAX_16, true, true);
/*     */ 
/*     */ 
/*     */     
/* 180 */     addAttribute("cHRM", "redX", 2, true, null, VALUE_0, VALUE_MAX_16, true, true);
/*     */ 
/*     */ 
/*     */     
/* 184 */     addAttribute("cHRM", "redY", 2, true, null, VALUE_0, VALUE_MAX_16, true, true);
/*     */ 
/*     */ 
/*     */     
/* 188 */     addAttribute("cHRM", "greenX", 2, true, null, VALUE_0, VALUE_MAX_16, true, true);
/*     */ 
/*     */ 
/*     */     
/* 192 */     addAttribute("cHRM", "greenY", 2, true, null, VALUE_0, VALUE_MAX_16, true, true);
/*     */ 
/*     */ 
/*     */     
/* 196 */     addAttribute("cHRM", "blueX", 2, true, null, VALUE_0, VALUE_MAX_16, true, true);
/*     */ 
/*     */ 
/*     */     
/* 200 */     addAttribute("cHRM", "blueY", 2, true, null, VALUE_0, VALUE_MAX_16, true, true);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 205 */     addElement("gAMA", "javax_imageio_png_1.0", 0);
/*     */ 
/*     */     
/* 208 */     addAttribute("gAMA", "value", 2, true, null, VALUE_0, VALUE_MAX_32, true, true);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 213 */     addElement("hIST", "javax_imageio_png_1.0", 1, 256);
/*     */ 
/*     */ 
/*     */     
/* 217 */     addElement("hISTEntry", "hIST", 0);
/*     */ 
/*     */     
/* 220 */     addAttribute("hISTEntry", "index", 2, true, null, VALUE_0, VALUE_255, true, true);
/*     */ 
/*     */ 
/*     */     
/* 224 */     addAttribute("hISTEntry", "value", 2, true, null, VALUE_0, VALUE_MAX_16, true, true);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 229 */     addElement("iCCP", "javax_imageio_png_1.0", 0);
/*     */ 
/*     */     
/* 232 */     addAttribute("iCCP", "profileName", 0, true, null);
/*     */ 
/*     */     
/* 235 */     addAttribute("iCCP", "compressionMethod", 0, true, (String)null, 
/*     */         
/* 237 */         Arrays.asList(CLibPNGMetadata.iCCP_compressionMethodNames));
/*     */     
/* 239 */     addObjectValue("iCCP", byte.class, 0, 2147483647);
/*     */ 
/*     */     
/* 242 */     addElement("iTXt", "javax_imageio_png_1.0", 1, 2147483647);
/*     */ 
/*     */ 
/*     */     
/* 246 */     addElement("iTXtEntry", "iTXt", 0);
/*     */ 
/*     */     
/* 249 */     addAttribute("iTXtEntry", "keyword", 0, true, null);
/*     */ 
/*     */     
/* 252 */     addBooleanAttribute("iTXtEntry", "compressionFlag", false, false);
/*     */ 
/*     */     
/* 255 */     addAttribute("iTXtEntry", "compressionMethod", 0, true, null);
/*     */ 
/*     */     
/* 258 */     addAttribute("iTXtEntry", "languageTag", 0, true, null);
/*     */ 
/*     */     
/* 261 */     addAttribute("iTXtEntry", "translatedKeyword", 0, true, null);
/*     */ 
/*     */     
/* 264 */     addAttribute("iTXtEntry", "text", 0, true, null);
/*     */ 
/*     */ 
/*     */     
/* 268 */     addElement("pHYS", "javax_imageio_png_1.0", 0);
/*     */ 
/*     */     
/* 271 */     addAttribute("pHYS", "pixelsPerUnitXAxis", 2, true, null, VALUE_0, VALUE_MAX_32, true, true);
/*     */ 
/*     */     
/* 274 */     addAttribute("pHYS", "pixelsPerUnitYAxis", 2, true, null, VALUE_0, VALUE_MAX_32, true, true);
/*     */ 
/*     */     
/* 277 */     addAttribute("pHYS", "unitSpecifier", 0, true, (String)null, 
/*     */         
/* 279 */         Arrays.asList(CLibPNGMetadata.unitSpecifierNames));
/*     */ 
/*     */     
/* 282 */     addElement("sBIT", "javax_imageio_png_1.0", 3);
/*     */ 
/*     */ 
/*     */     
/* 286 */     addElement("sBIT_Grayscale", "sBIT", 0);
/*     */ 
/*     */     
/* 289 */     addAttribute("sBIT_Grayscale", "gray", 2, true, null, VALUE_0, VALUE_255, true, true);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 294 */     addElement("sBIT_GrayAlpha", "sBIT", 0);
/*     */ 
/*     */     
/* 297 */     addAttribute("sBIT_GrayAlpha", "gray", 2, true, null, VALUE_0, VALUE_255, true, true);
/*     */ 
/*     */ 
/*     */     
/* 301 */     addAttribute("sBIT_GrayAlpha", "alpha", 2, true, null, VALUE_0, VALUE_255, true, true);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 306 */     addElement("sBIT_RGB", "sBIT", 0);
/*     */ 
/*     */     
/* 309 */     addAttribute("sBIT_RGB", "red", 2, true, null, VALUE_0, VALUE_255, true, true);
/*     */ 
/*     */ 
/*     */     
/* 313 */     addAttribute("sBIT_RGB", "green", 2, true, null, VALUE_0, VALUE_255, true, true);
/*     */ 
/*     */ 
/*     */     
/* 317 */     addAttribute("sBIT_RGB", "blue", 2, true, null, VALUE_0, VALUE_255, true, true);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 322 */     addElement("sBIT_RGBAlpha", "sBIT", 0);
/*     */ 
/*     */     
/* 325 */     addAttribute("sBIT_RGBAlpha", "red", 2, true, null, VALUE_0, VALUE_255, true, true);
/*     */ 
/*     */ 
/*     */     
/* 329 */     addAttribute("sBIT_RGBAlpha", "green", 2, true, null, VALUE_0, VALUE_255, true, true);
/*     */ 
/*     */ 
/*     */     
/* 333 */     addAttribute("sBIT_RGBAlpha", "blue", 2, true, null, VALUE_0, VALUE_255, true, true);
/*     */ 
/*     */ 
/*     */     
/* 337 */     addAttribute("sBIT_RGBAlpha", "alpha", 2, true, null, VALUE_0, VALUE_255, true, true);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 342 */     addElement("sBIT_Palette", "sBIT", 0);
/*     */ 
/*     */     
/* 345 */     addAttribute("sBIT_Palette", "red", 2, true, null, VALUE_0, VALUE_255, true, true);
/*     */ 
/*     */ 
/*     */     
/* 349 */     addAttribute("sBIT_Palette", "green", 2, true, null, VALUE_0, VALUE_255, true, true);
/*     */ 
/*     */ 
/*     */     
/* 353 */     addAttribute("sBIT_Palette", "blue", 2, true, null, VALUE_0, VALUE_255, true, true);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 358 */     addElement("sPLT", "javax_imageio_png_1.0", 1, 256);
/*     */ 
/*     */ 
/*     */     
/* 362 */     addElement("sPLTEntry", "sPLT", 0);
/*     */ 
/*     */     
/* 365 */     addAttribute("sPLTEntry", "index", 2, true, null, VALUE_0, VALUE_255, true, true);
/*     */ 
/*     */ 
/*     */     
/* 369 */     addAttribute("sPLTEntry", "red", 2, true, null, VALUE_0, VALUE_255, true, true);
/*     */ 
/*     */ 
/*     */     
/* 373 */     addAttribute("sPLTEntry", "green", 2, true, null, VALUE_0, VALUE_255, true, true);
/*     */ 
/*     */ 
/*     */     
/* 377 */     addAttribute("sPLTEntry", "blue", 2, true, null, VALUE_0, VALUE_255, true, true);
/*     */ 
/*     */ 
/*     */     
/* 381 */     addAttribute("sPLTEntry", "alpha", 2, true, null, VALUE_0, VALUE_255, true, true);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 386 */     addElement("sRGB", "javax_imageio_png_1.0", 0);
/*     */ 
/*     */     
/* 389 */     addAttribute("sRGB", "renderingIntent", 0, true, (String)null, 
/*     */         
/* 391 */         Arrays.asList(CLibPNGMetadata.renderingIntentNames));
/*     */ 
/*     */     
/* 394 */     addElement("tEXt", "javax_imageio_png_1.0", 1, 2147483647);
/*     */ 
/*     */ 
/*     */     
/* 398 */     addElement("tEXtEntry", "tEXt", 0);
/*     */ 
/*     */     
/* 401 */     addAttribute("tEXtEntry", "keyword", 0, true, null);
/*     */ 
/*     */     
/* 404 */     addAttribute("tEXtEntry", "value", 0, true, null);
/*     */ 
/*     */ 
/*     */     
/* 408 */     addElement("tIME", "javax_imageio_png_1.0", 0);
/*     */ 
/*     */     
/* 411 */     addAttribute("tIME", "year", 2, true, null, VALUE_0, VALUE_MAX_16, true, true);
/*     */ 
/*     */ 
/*     */     
/* 415 */     addAttribute("tIME", "month", 2, true, null, VALUE_1, VALUE_12, true, true);
/*     */ 
/*     */ 
/*     */     
/* 419 */     addAttribute("tIME", "day", 2, true, null, VALUE_1, VALUE_31, true, true);
/*     */ 
/*     */ 
/*     */     
/* 423 */     addAttribute("tIME", "hour", 2, true, null, VALUE_0, VALUE_23, true, true);
/*     */ 
/*     */ 
/*     */     
/* 427 */     addAttribute("tIME", "minute", 2, true, null, VALUE_0, VALUE_59, true, true);
/*     */ 
/*     */ 
/*     */     
/* 431 */     addAttribute("tIME", "second", 2, true, null, VALUE_0, VALUE_60, true, true);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 436 */     addElement("tRNS", "javax_imageio_png_1.0", 3);
/*     */ 
/*     */ 
/*     */     
/* 440 */     addElement("tRNS_Grayscale", "tRNS", 0);
/*     */ 
/*     */     
/* 443 */     addAttribute("tRNS_Grayscale", "gray", 2, true, null, VALUE_0, VALUE_MAX_16, true, true);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 448 */     addElement("tRNS_RGB", "tRNS", 0);
/*     */ 
/*     */     
/* 451 */     addAttribute("tRNS_RGB", "red", 2, true, null, VALUE_0, VALUE_MAX_16, true, true);
/*     */ 
/*     */ 
/*     */     
/* 455 */     addAttribute("tRNS_RGB", "green", 2, true, null, VALUE_0, VALUE_MAX_16, true, true);
/*     */ 
/*     */ 
/*     */     
/* 459 */     addAttribute("tRNS_RGB", "blue", 2, true, null, VALUE_0, VALUE_MAX_16, true, true);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 464 */     addElement("tRNS_Palette", "tRNS", 0);
/*     */ 
/*     */     
/* 467 */     addAttribute("tRNS_Palette", "index", 2, true, null, VALUE_0, VALUE_255, true, true);
/*     */ 
/*     */ 
/*     */     
/* 471 */     addAttribute("tRNS_Palette", "alpha", 2, true, null, VALUE_0, VALUE_255, true, true);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 476 */     addElement("zTXt", "javax_imageio_png_1.0", 1, 2147483647);
/*     */ 
/*     */ 
/*     */     
/* 480 */     addElement("zTXtEntry", "zTXt", 0);
/*     */ 
/*     */     
/* 483 */     addAttribute("zTXtEntry", "keyword", 0, true, null);
/*     */ 
/*     */     
/* 486 */     addAttribute("zTXtEntry", "compressionMethod", 0, true, (String)null, 
/*     */         
/* 488 */         Arrays.asList(CLibPNGMetadata.zTXt_compressionMethodNames));
/*     */     
/* 490 */     addAttribute("zTXtEntry", "text", 0, true, null);
/*     */ 
/*     */ 
/*     */     
/* 494 */     addElement("UnknownChunks", "javax_imageio_png_1.0", 1, 2147483647);
/*     */ 
/*     */ 
/*     */     
/* 498 */     addElement("UnknownChunk", "UnknownChunks", 0);
/*     */ 
/*     */     
/* 501 */     addAttribute("UnknownChunk", "type", 0, true, null);
/*     */ 
/*     */     
/* 504 */     addObjectValue("UnknownChunk", byte.class, 0, 2147483647);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean canNodeAppear(String elementName, ImageTypeSpecifier imageType) {
/* 509 */     return true;
/*     */   }
/*     */   
/*     */   public static synchronized IIOMetadataFormat getInstance() {
/* 513 */     if (instance == null) {
/* 514 */       instance = new CLibPNGMetadataFormat();
/*     */     }
/* 516 */     return instance;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\github\jaiimageio\impl\plugins\png\CLibPNGMetadataFormat.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */