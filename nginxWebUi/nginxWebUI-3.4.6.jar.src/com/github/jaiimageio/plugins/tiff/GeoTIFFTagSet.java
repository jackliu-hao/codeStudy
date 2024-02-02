/*     */ package com.github.jaiimageio.plugins.tiff;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class GeoTIFFTagSet
/*     */   extends TIFFTagSet
/*     */ {
/*  65 */   private static GeoTIFFTagSet theInstance = null;
/*     */ 
/*     */ 
/*     */   
/*     */   public static final int TAG_MODEL_PIXEL_SCALE = 33550;
/*     */ 
/*     */   
/*     */   public static final int TAG_MODEL_TRANSFORMATION = 34264;
/*     */ 
/*     */   
/*     */   public static final int TAG_MODEL_TIE_POINT = 33922;
/*     */ 
/*     */   
/*     */   public static final int TAG_GEO_KEY_DIRECTORY = 34735;
/*     */ 
/*     */   
/*     */   public static final int TAG_GEO_DOUBLE_PARAMS = 34736;
/*     */ 
/*     */   
/*     */   public static final int TAG_GEO_ASCII_PARAMS = 34737;
/*     */ 
/*     */   
/*     */   private static List tags;
/*     */ 
/*     */ 
/*     */   
/*     */   static class ModelPixelScale
/*     */     extends TIFFTag
/*     */   {
/*     */     public ModelPixelScale() {
/*  95 */       super("ModelPixelScaleTag", 33550, 4096);
/*     */     }
/*     */   }
/*     */   
/*     */   static class ModelTransformation
/*     */     extends TIFFTag
/*     */   {
/*     */     public ModelTransformation() {
/* 103 */       super("ModelTransformationTag", 34264, 4096);
/*     */     }
/*     */   }
/*     */   
/*     */   static class ModelTiePoint
/*     */     extends TIFFTag
/*     */   {
/*     */     public ModelTiePoint() {
/* 111 */       super("ModelTiePointTag", 33922, 4096);
/*     */     }
/*     */   }
/*     */   
/*     */   static class GeoKeyDirectory
/*     */     extends TIFFTag
/*     */   {
/*     */     public GeoKeyDirectory() {
/* 119 */       super("GeoKeyDirectory", 34735, 8);
/*     */     }
/*     */   }
/*     */   
/*     */   static class GeoDoubleParams
/*     */     extends TIFFTag
/*     */   {
/*     */     public GeoDoubleParams() {
/* 127 */       super("GeoDoubleParams", 34736, 4096);
/*     */     }
/*     */   }
/*     */   
/*     */   static class GeoAsciiParams
/*     */     extends TIFFTag
/*     */   {
/*     */     public GeoAsciiParams() {
/* 135 */       super("GeoAsciiParams", 34737, 4);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static void initTags() {
/* 144 */     tags = new ArrayList(42);
/*     */     
/* 146 */     tags.add(new ModelPixelScale());
/* 147 */     tags.add(new ModelTransformation());
/* 148 */     tags.add(new ModelTiePoint());
/* 149 */     tags.add(new GeoKeyDirectory());
/* 150 */     tags.add(new GeoDoubleParams());
/* 151 */     tags.add(new GeoAsciiParams());
/*     */   }
/*     */   
/*     */   private GeoTIFFTagSet() {
/* 155 */     super(tags);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static synchronized GeoTIFFTagSet getInstance() {
/* 164 */     if (theInstance == null) {
/* 165 */       initTags();
/* 166 */       theInstance = new GeoTIFFTagSet();
/* 167 */       tags = null;
/*     */     } 
/* 169 */     return theInstance;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\github\jaiimageio\plugins\tiff\GeoTIFFTagSet.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */