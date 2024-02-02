/*     */ package com.github.jaiimageio.impl.plugins.tiff;
/*     */ 
/*     */ import javax.imageio.ImageTypeSpecifier;
/*     */ import javax.imageio.metadata.IIOMetadataFormat;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class TIFFImageMetadataFormat
/*     */   extends TIFFMetadataFormat
/*     */ {
/*  52 */   private static TIFFImageMetadataFormat theInstance = null;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean canNodeAppear(String elementName, ImageTypeSpecifier imageType) {
/*  59 */     return false;
/*     */   }
/*     */   
/*     */   private TIFFImageMetadataFormat() {
/*  63 */     this.resourceBaseName = "com.github.jaiimageio.impl.plugins.tiff.TIFFImageMetadataFormatResources";
/*     */     
/*  65 */     this.rootName = "com_sun_media_imageio_plugins_tiff_image_1.0";
/*     */ 
/*     */ 
/*     */     
/*  69 */     String[] empty = new String[0];
/*     */ 
/*     */ 
/*     */     
/*  73 */     String[] childNames = { "TIFFIFD" };
/*  74 */     TIFFElementInfo einfo = new TIFFElementInfo(childNames, empty, 4);
/*     */     
/*  76 */     this.elementInfoMap.put("com_sun_media_imageio_plugins_tiff_image_1.0", einfo);
/*     */ 
/*     */     
/*  79 */     childNames = new String[] { "TIFFField", "TIFFIFD" };
/*  80 */     String[] attrNames = { "tagSets", "parentTagNumber", "parentTagName" };
/*     */     
/*  82 */     einfo = new TIFFElementInfo(childNames, attrNames, 4);
/*  83 */     this.elementInfoMap.put("TIFFIFD", einfo);
/*     */     
/*  85 */     TIFFAttrInfo ainfo = new TIFFAttrInfo();
/*  86 */     ainfo.dataType = 0;
/*  87 */     ainfo.isRequired = true;
/*  88 */     this.attrInfoMap.put("TIFFIFD/tagSets", ainfo);
/*     */     
/*  90 */     ainfo = new TIFFAttrInfo();
/*  91 */     ainfo.dataType = 2;
/*  92 */     ainfo.isRequired = false;
/*  93 */     this.attrInfoMap.put("TIFFIFD/parentTagNumber", ainfo);
/*     */     
/*  95 */     ainfo = new TIFFAttrInfo();
/*  96 */     ainfo.dataType = 0;
/*  97 */     ainfo.isRequired = false;
/*  98 */     this.attrInfoMap.put("TIFFIFD/parentTagName", ainfo);
/*     */     
/* 100 */     String[] types = { "TIFFByte", "TIFFAscii", "TIFFShort", "TIFFSShort", "TIFFLong", "TIFFSLong", "TIFFRational", "TIFFSRational", "TIFFFloat", "TIFFDouble", "TIFFUndefined" };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 114 */     attrNames = new String[] { "value", "description" };
/* 115 */     String[] attrNamesValueOnly = { "value" };
/* 116 */     TIFFAttrInfo ainfoValue = new TIFFAttrInfo();
/* 117 */     TIFFAttrInfo ainfoDescription = new TIFFAttrInfo();
/*     */     int i;
/* 119 */     for (i = 0; i < types.length; i++) {
/* 120 */       if (!types[i].equals("TIFFUndefined")) {
/* 121 */         childNames = new String[1];
/* 122 */         childNames[0] = types[i];
/* 123 */         einfo = new TIFFElementInfo(childNames, empty, 4);
/*     */         
/* 125 */         this.elementInfoMap.put(types[i] + "s", einfo);
/*     */       } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 134 */       boolean hasDescription = (!types[i].equals("TIFFUndefined") && !types[i].equals("TIFFAscii") && !types[i].equals("TIFFRational") && !types[i].equals("TIFFSRational") && !types[i].equals("TIFFFloat") && !types[i].equals("TIFFDouble"));
/*     */       
/* 136 */       String[] anames = hasDescription ? attrNames : attrNamesValueOnly;
/* 137 */       einfo = new TIFFElementInfo(empty, anames, 0);
/* 138 */       this.elementInfoMap.put(types[i], einfo);
/*     */       
/* 140 */       this.attrInfoMap.put(types[i] + "/value", ainfoValue);
/* 141 */       if (hasDescription) {
/* 142 */         this.attrInfoMap.put(types[i] + "/description", ainfoDescription);
/*     */       }
/*     */     } 
/*     */     
/* 146 */     childNames = new String[2 * types.length - 1];
/* 147 */     for (i = 0; i < types.length; i++) {
/* 148 */       childNames[2 * i] = types[i];
/* 149 */       if (!types[i].equals("TIFFUndefined")) {
/* 150 */         childNames[2 * i + 1] = types[i] + "s";
/*     */       }
/*     */     } 
/* 153 */     attrNames = new String[] { "number", "name" };
/* 154 */     einfo = new TIFFElementInfo(childNames, attrNames, 3);
/* 155 */     this.elementInfoMap.put("TIFFField", einfo);
/*     */     
/* 157 */     ainfo = new TIFFAttrInfo();
/* 158 */     ainfo.isRequired = true;
/* 159 */     this.attrInfoMap.put("TIFFField/number", ainfo);
/*     */     
/* 161 */     ainfo = new TIFFAttrInfo();
/* 162 */     this.attrInfoMap.put("TIFFField/name", ainfo);
/*     */   }
/*     */   
/*     */   public static synchronized IIOMetadataFormat getInstance() {
/* 166 */     if (theInstance == null) {
/* 167 */       theInstance = new TIFFImageMetadataFormat();
/*     */     }
/* 169 */     return theInstance;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\github\jaiimageio\impl\plugins\tiff\TIFFImageMetadataFormat.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */