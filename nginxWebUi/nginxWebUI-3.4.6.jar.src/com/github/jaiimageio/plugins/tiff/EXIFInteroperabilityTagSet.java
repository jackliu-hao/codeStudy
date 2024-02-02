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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class EXIFInteroperabilityTagSet
/*     */   extends TIFFTagSet
/*     */ {
/*     */   public static final int TAG_INTEROPERABILITY_INDEX = 1;
/*     */   public static final String INTEROPERABILITY_INDEX_R98 = "R98";
/*     */   public static final String INTEROPERABILITY_INDEX_THM = "THM";
/*  85 */   private static EXIFInteroperabilityTagSet theInstance = null;
/*     */   private static List tags;
/*     */   
/*     */   static class InteroperabilityIndex extends TIFFTag {
/*     */     public InteroperabilityIndex() {
/*  90 */       super("InteroperabilityIndex", 1, 4);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static void initTags() {
/*  99 */     tags = new ArrayList(42);
/*     */     
/* 101 */     tags.add(new InteroperabilityIndex());
/*     */   }
/*     */   
/*     */   private EXIFInteroperabilityTagSet() {
/* 105 */     super(tags);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static synchronized EXIFInteroperabilityTagSet getInstance() {
/* 115 */     if (theInstance == null) {
/* 116 */       initTags();
/* 117 */       theInstance = new EXIFInteroperabilityTagSet();
/* 118 */       tags = null;
/*     */     } 
/* 120 */     return theInstance;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\github\jaiimageio\plugins\tiff\EXIFInteroperabilityTagSet.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */