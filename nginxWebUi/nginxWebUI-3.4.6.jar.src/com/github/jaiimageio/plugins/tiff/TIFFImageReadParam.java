/*     */ package com.github.jaiimageio.plugins.tiff;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import javax.imageio.ImageReadParam;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class TIFFImageReadParam
/*     */   extends ImageReadParam
/*     */ {
/*  84 */   List allowedTagSets = new ArrayList(4);
/*     */   
/*  86 */   TIFFDecompressor decompressor = null;
/*     */   
/*  88 */   TIFFColorConverter colorConverter = null;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public TIFFImageReadParam() {
/* 102 */     addAllowedTagSet(BaselineTIFFTagSet.getInstance());
/* 103 */     addAllowedTagSet(FaxTIFFTagSet.getInstance());
/* 104 */     addAllowedTagSet(EXIFParentTIFFTagSet.getInstance());
/* 105 */     addAllowedTagSet(GeoTIFFTagSet.getInstance());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void addAllowedTagSet(TIFFTagSet tagSet) {
/* 118 */     if (tagSet == null) {
/* 119 */       throw new IllegalArgumentException("tagSet == null!");
/*     */     }
/* 121 */     this.allowedTagSets.add(tagSet);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void removeAllowedTagSet(TIFFTagSet tagSet) {
/* 136 */     if (tagSet == null) {
/* 137 */       throw new IllegalArgumentException("tagSet == null!");
/*     */     }
/* 139 */     this.allowedTagSets.remove(tagSet);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public List getAllowedTagSets() {
/* 149 */     return this.allowedTagSets;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setTIFFDecompressor(TIFFDecompressor decompressor) {
/* 165 */     this.decompressor = decompressor;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public TIFFDecompressor getTIFFDecompressor() {
/* 180 */     return this.decompressor;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setColorConverter(TIFFColorConverter colorConverter) {
/* 195 */     this.colorConverter = colorConverter;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public TIFFColorConverter getColorConverter() {
/* 207 */     return this.colorConverter;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\github\jaiimageio\plugins\tiff\TIFFImageReadParam.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */