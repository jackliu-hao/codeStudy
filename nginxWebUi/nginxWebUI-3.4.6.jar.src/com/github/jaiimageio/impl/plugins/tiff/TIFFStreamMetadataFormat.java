/*    */ package com.github.jaiimageio.impl.plugins.tiff;
/*    */ 
/*    */ import javax.imageio.ImageTypeSpecifier;
/*    */ import javax.imageio.metadata.IIOMetadataFormat;
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
/*    */ public class TIFFStreamMetadataFormat
/*    */   extends TIFFMetadataFormat
/*    */ {
/* 52 */   private static TIFFStreamMetadataFormat theInstance = null;
/*    */ 
/*    */   
/*    */   public boolean canNodeAppear(String elementName, ImageTypeSpecifier imageType) {
/* 56 */     return false;
/*    */   }
/*    */   
/*    */   private TIFFStreamMetadataFormat() {
/* 60 */     this.resourceBaseName = "com.github.jaiimageio.impl.plugins.tiff.TIFFStreamMetadataFormatResources";
/*    */     
/* 62 */     this.rootName = "com_sun_media_imageio_plugins_tiff_stream_1.0";
/*    */ 
/*    */ 
/*    */     
/* 66 */     String[] empty = new String[0];
/*    */ 
/*    */ 
/*    */     
/* 70 */     String[] childNames = { "ByteOrder" };
/* 71 */     TIFFElementInfo einfo = new TIFFElementInfo(childNames, empty, 1);
/*    */     
/* 73 */     this.elementInfoMap.put("com_sun_media_imageio_plugins_tiff_stream_1.0", einfo);
/*    */ 
/*    */     
/* 76 */     childNames = empty;
/* 77 */     String[] attrNames = { "value" };
/* 78 */     einfo = new TIFFElementInfo(childNames, attrNames, 0);
/* 79 */     this.elementInfoMap.put("ByteOrder", einfo);
/*    */     
/* 81 */     TIFFAttrInfo ainfo = new TIFFAttrInfo();
/* 82 */     ainfo.dataType = 0;
/* 83 */     ainfo.isRequired = true;
/* 84 */     this.attrInfoMap.put("ByteOrder/value", ainfo);
/*    */   }
/*    */   
/*    */   public static synchronized IIOMetadataFormat getInstance() {
/* 88 */     if (theInstance == null) {
/* 89 */       theInstance = new TIFFStreamMetadataFormat();
/*    */     }
/* 91 */     return theInstance;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\github\jaiimageio\impl\plugins\tiff\TIFFStreamMetadataFormat.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */