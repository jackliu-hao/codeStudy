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
/*     */ public class EXIFParentTIFFTagSet
/*     */   extends TIFFTagSet
/*     */ {
/*  59 */   private static EXIFParentTIFFTagSet theInstance = null;
/*     */   
/*     */   public static final int TAG_EXIF_IFD_POINTER = 34665;
/*     */   
/*     */   public static final int TAG_GPS_INFO_IFD_POINTER = 34853;
/*     */   
/*     */   private static List tags;
/*     */ 
/*     */   
/*     */   static class EXIFIFDPointer
/*     */     extends TIFFTag
/*     */   {
/*     */     public EXIFIFDPointer() {
/*  72 */       super("EXIFIFDPointer", 34665, 16, 
/*     */ 
/*     */           
/*  75 */           EXIFTIFFTagSet.getInstance());
/*     */     }
/*     */   }
/*     */   
/*     */   static class GPSInfoIFDPointer
/*     */     extends TIFFTag
/*     */   {
/*     */     public GPSInfoIFDPointer() {
/*  83 */       super("GPSInfoIFDPointer", 34853, 16, 
/*     */ 
/*     */           
/*  86 */           EXIFGPSTagSet.getInstance());
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static void initTags() {
/*  93 */     tags = new ArrayList(1);
/*  94 */     tags.add(new EXIFIFDPointer());
/*  95 */     tags.add(new GPSInfoIFDPointer());
/*     */   }
/*     */   
/*     */   private EXIFParentTIFFTagSet() {
/*  99 */     super(tags);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static synchronized EXIFParentTIFFTagSet getInstance() {
/* 108 */     if (theInstance == null) {
/* 109 */       initTags();
/* 110 */       theInstance = new EXIFParentTIFFTagSet();
/* 111 */       tags = null;
/*     */     } 
/* 113 */     return theInstance;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\github\jaiimageio\plugins\tiff\EXIFParentTIFFTagSet.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */