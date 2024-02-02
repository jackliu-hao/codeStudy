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
/*     */ public class FaxTIFFTagSet
/*     */   extends TIFFTagSet
/*     */ {
/*  57 */   private static FaxTIFFTagSet theInstance = null;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final int TAG_BAD_FAX_LINES = 326;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final int TAG_CLEAN_FAX_DATA = 327;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final int CLEAN_FAX_DATA_NO_ERRORS = 0;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final int CLEAN_FAX_DATA_ERRORS_CORRECTED = 1;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final int CLEAN_FAX_DATA_ERRORS_UNCORRECTED = 2;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final int TAG_CONSECUTIVE_BAD_LINES = 328;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static List tags;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static class BadFaxLines
/*     */     extends TIFFTag
/*     */   {
/*     */     public BadFaxLines() {
/* 108 */       super("BadFaxLines", 326, 24);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   static class CleanFaxData
/*     */     extends TIFFTag
/*     */   {
/*     */     public CleanFaxData() {
/* 118 */       super("CleanFaxData", 327, 8);
/*     */ 
/*     */ 
/*     */       
/* 122 */       addValueName(0, "No errors");
/*     */       
/* 124 */       addValueName(1, "Errors corrected");
/*     */       
/* 126 */       addValueName(2, "Errors uncorrected");
/*     */     }
/*     */   }
/*     */   
/*     */   static class ConsecutiveBadFaxLines
/*     */     extends TIFFTag
/*     */   {
/*     */     public ConsecutiveBadFaxLines() {
/* 134 */       super("ConsecutiveBadFaxLines", 328, 24);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static void initTags() {
/* 144 */     tags = new ArrayList(42);
/*     */     
/* 146 */     tags.add(new BadFaxLines());
/* 147 */     tags.add(new CleanFaxData());
/* 148 */     tags.add(new ConsecutiveBadFaxLines());
/*     */   }
/*     */   
/*     */   private FaxTIFFTagSet() {
/* 152 */     super(tags);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static synchronized FaxTIFFTagSet getInstance() {
/* 161 */     if (theInstance == null) {
/* 162 */       initTags();
/* 163 */       theInstance = new FaxTIFFTagSet();
/* 164 */       tags = null;
/*     */     } 
/* 166 */     return theInstance;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\github\jaiimageio\plugins\tiff\FaxTIFFTagSet.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */