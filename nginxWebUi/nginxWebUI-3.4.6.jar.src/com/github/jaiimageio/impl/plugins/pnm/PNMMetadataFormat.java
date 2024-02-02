/*     */ package com.github.jaiimageio.impl.plugins.pnm;
/*     */ 
/*     */ import java.util.Hashtable;
/*     */ import javax.imageio.ImageTypeSpecifier;
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
/*     */ public class PNMMetadataFormat
/*     */   extends IIOMetadataFormatImpl
/*     */ {
/*  55 */   private static Hashtable parents = new Hashtable<Object, Object>();
/*     */   private static PNMMetadataFormat instance;
/*     */   
/*     */   static {
/*  59 */     parents.put("FormatName", "com_sun_media_imageio_plugins_pnm_image_1.0");
/*  60 */     parents.put("Variant", "com_sun_media_imageio_plugins_pnm_image_1.0");
/*  61 */     parents.put("Width", "com_sun_media_imageio_plugins_pnm_image_1.0");
/*  62 */     parents.put("Height", "com_sun_media_imageio_plugins_pnm_image_1.0");
/*  63 */     parents.put("MaximumSample", "com_sun_media_imageio_plugins_pnm_image_1.0");
/*  64 */     parents.put("Comment", "com_sun_media_imageio_plugins_pnm_image_1.0");
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static synchronized PNMMetadataFormat getInstance() {
/*  70 */     if (instance == null)
/*  71 */       instance = new PNMMetadataFormat(); 
/*  72 */     return instance;
/*     */   }
/*     */   
/*  75 */   String resourceBaseName = getClass().getName() + "Resources";
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   PNMMetadataFormat() {
/*  82 */     super("com_sun_media_imageio_plugins_pnm_image_1.0", 1);
/*  83 */     setResourceBaseName(this.resourceBaseName);
/*  84 */     addElements();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void addElements() {
/*  91 */     addElement("FormatName", 
/*  92 */         getParent("FormatName"), 0);
/*     */ 
/*     */     
/*  95 */     addElement("Variant", 
/*  96 */         getParent("Variant"), 0);
/*     */     
/*  98 */     addElement("Width", 
/*  99 */         getParent("Width"), 0);
/*     */     
/* 101 */     addElement("Height", 
/* 102 */         getParent("Height"), 0);
/*     */     
/* 104 */     addElement("MaximumSample", 
/* 105 */         getParent("MaximumSample"), 0);
/*     */     
/* 107 */     addElement("Comment", 
/* 108 */         getParent("Comment"), 0);
/*     */   }
/*     */ 
/*     */   
/*     */   public String getParent(String elementName) {
/* 113 */     return (String)parents.get(elementName);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean canNodeAppear(String elementName, ImageTypeSpecifier imageType) {
/* 118 */     if (getParent(elementName) != null)
/* 119 */       return true; 
/* 120 */     return false;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\github\jaiimageio\impl\plugins\pnm\PNMMetadataFormat.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */