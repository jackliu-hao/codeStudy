/*     */ package com.github.jaiimageio.plugins.tiff;
/*     */ 
/*     */ import com.github.jaiimageio.impl.plugins.tiff.TIFFIFD;
/*     */ import com.github.jaiimageio.impl.plugins.tiff.TIFFImageMetadata;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.TreeMap;
/*     */ import javax.imageio.metadata.IIOInvalidTreeException;
/*     */ import javax.imageio.metadata.IIOMetadata;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class TIFFDirectory
/*     */   implements Cloneable
/*     */ {
/*     */   private static final int MAX_LOW_FIELD_TAG_NUM = 532;
/*     */   private List tagSets;
/*     */   private TIFFTag parentTag;
/* 144 */   private TIFFField[] lowFields = new TIFFField[533];
/*     */ 
/*     */   
/* 147 */   private int numLowFields = 0;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 153 */   private Map highFields = new TreeMap<Object, Object>();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static TIFFDirectory createFromMetadata(IIOMetadata tiffImageMetadata) throws IIOInvalidTreeException {
/*     */     TIFFImageMetadata tim;
/* 180 */     if (tiffImageMetadata == null) {
/* 181 */       throw new IllegalArgumentException("tiffImageMetadata == null");
/*     */     }
/*     */ 
/*     */     
/* 185 */     if (tiffImageMetadata instanceof TIFFImageMetadata) {
/* 186 */       tim = (TIFFImageMetadata)tiffImageMetadata;
/*     */     } else {
/*     */       
/* 189 */       ArrayList<BaselineTIFFTagSet> l = new ArrayList(1);
/* 190 */       l.add(BaselineTIFFTagSet.getInstance());
/* 191 */       tim = new TIFFImageMetadata(l);
/*     */ 
/*     */       
/* 194 */       String formatName = null;
/* 195 */       if ("com_sun_media_imageio_plugins_tiff_image_1.0"
/* 196 */         .equals(tiffImageMetadata.getNativeMetadataFormatName())) {
/* 197 */         formatName = "com_sun_media_imageio_plugins_tiff_image_1.0";
/*     */       } else {
/*     */         
/* 200 */         String[] extraNames = tiffImageMetadata.getExtraMetadataFormatNames();
/* 201 */         if (extraNames != null) {
/* 202 */           for (int i = 0; i < extraNames.length; i++) {
/* 203 */             if ("com_sun_media_imageio_plugins_tiff_image_1.0"
/* 204 */               .equals(extraNames[i])) {
/* 205 */               formatName = extraNames[i];
/*     */               
/*     */               break;
/*     */             } 
/*     */           } 
/*     */         }
/* 211 */         if (formatName == null) {
/* 212 */           if (tiffImageMetadata.isStandardMetadataFormatSupported()) {
/* 213 */             formatName = "javax_imageio_1.0";
/*     */           } else {
/*     */             
/* 216 */             throw new IllegalArgumentException("Parameter does not support required metadata format!");
/*     */           } 
/*     */         }
/*     */       } 
/*     */ 
/*     */ 
/*     */       
/* 223 */       tim.setFromTree(formatName, tiffImageMetadata
/* 224 */           .getAsTree(formatName));
/*     */     } 
/*     */     
/* 227 */     return (TIFFDirectory)tim.getRootIFD();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static TIFFIFD getDirectoryAsIFD(TIFFDirectory dir) {
/* 234 */     if (dir instanceof TIFFIFD) {
/* 235 */       return (TIFFIFD)dir;
/*     */     }
/*     */ 
/*     */     
/* 239 */     TIFFIFD ifd = new TIFFIFD(Arrays.asList(dir.getTagSets()), dir.getParentTag());
/* 240 */     TIFFField[] fields = dir.getTIFFFields();
/* 241 */     int numFields = fields.length;
/* 242 */     for (int i = 0; i < numFields; i++) {
/* 243 */       TIFFField f = fields[i];
/* 244 */       TIFFTag tag = f.getTag();
/* 245 */       if (tag.isIFDPointer()) {
/*     */         
/* 247 */         TIFFIFD tIFFIFD = getDirectoryAsIFD((TIFFDirectory)f.getData());
/* 248 */         f = new TIFFField(tag, f.getType(), f.getCount(), tIFFIFD);
/*     */       } 
/* 250 */       ifd.addTIFFField(f);
/*     */     } 
/*     */     
/* 253 */     return ifd;
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
/*     */   public TIFFDirectory(TIFFTagSet[] tagSets, TIFFTag parentTag) {
/* 269 */     if (tagSets == null) {
/* 270 */       throw new IllegalArgumentException("tagSets == null!");
/*     */     }
/* 272 */     this.tagSets = new ArrayList(tagSets.length);
/* 273 */     int numTagSets = tagSets.length;
/* 274 */     for (int i = 0; i < numTagSets; i++) {
/* 275 */       this.tagSets.add(tagSets[i]);
/*     */     }
/* 277 */     this.parentTag = parentTag;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public TIFFTagSet[] getTagSets() {
/* 287 */     return (TIFFTagSet[])this.tagSets.toArray((Object[])new TIFFTagSet[this.tagSets.size()]);
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
/*     */   public void addTagSet(TIFFTagSet tagSet) {
/* 299 */     if (tagSet == null) {
/* 300 */       throw new IllegalArgumentException("tagSet == null");
/*     */     }
/*     */     
/* 303 */     if (!this.tagSets.contains(tagSet)) {
/* 304 */       this.tagSets.add(tagSet);
/*     */     }
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
/*     */   public void removeTagSet(TIFFTagSet tagSet) {
/* 317 */     if (tagSet == null) {
/* 318 */       throw new IllegalArgumentException("tagSet == null");
/*     */     }
/*     */     
/* 321 */     if (this.tagSets.contains(tagSet)) {
/* 322 */       this.tagSets.remove(tagSet);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public TIFFTag getParentTag() {
/* 334 */     return this.parentTag;
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
/*     */   public TIFFTag getTag(int tagNumber) {
/* 347 */     return TIFFIFD.getTag(tagNumber, this.tagSets);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getNumTIFFFields() {
/* 357 */     return this.numLowFields + this.highFields.size();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean containsTIFFField(int tagNumber) {
/* 368 */     return ((tagNumber >= 0 && tagNumber <= 532 && this.lowFields[tagNumber] != null) || this.highFields
/*     */       
/* 370 */       .containsKey(new Integer(tagNumber)));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void addTIFFField(TIFFField f) {
/* 380 */     if (f == null) {
/* 381 */       throw new IllegalArgumentException("f == null");
/*     */     }
/* 383 */     int tagNumber = f.getTagNumber();
/* 384 */     if (tagNumber >= 0 && tagNumber <= 532) {
/* 385 */       if (this.lowFields[tagNumber] == null) {
/* 386 */         this.numLowFields++;
/*     */       }
/* 388 */       this.lowFields[tagNumber] = f;
/*     */     } else {
/* 390 */       this.highFields.put(new Integer(tagNumber), f);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public TIFFField getTIFFField(int tagNumber) {
/*     */     TIFFField f;
/* 403 */     if (tagNumber >= 0 && tagNumber <= 532) {
/* 404 */       f = this.lowFields[tagNumber];
/*     */     } else {
/* 406 */       f = (TIFFField)this.highFields.get(new Integer(tagNumber));
/*     */     } 
/* 408 */     return f;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void removeTIFFField(int tagNumber) {
/* 417 */     if (tagNumber >= 0 && tagNumber <= 532) {
/* 418 */       if (this.lowFields[tagNumber] != null) {
/* 419 */         this.numLowFields--;
/* 420 */         this.lowFields[tagNumber] = null;
/*     */       } 
/*     */     } else {
/* 423 */       this.highFields.remove(new Integer(tagNumber));
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public TIFFField[] getTIFFFields() {
/* 435 */     TIFFField[] fields = new TIFFField[this.numLowFields + this.highFields.size()];
/*     */ 
/*     */     
/* 438 */     int nextIndex = 0;
/* 439 */     for (int i = 0; i <= 532; i++) {
/* 440 */       if (this.lowFields[i] != null) {
/* 441 */         fields[nextIndex++] = this.lowFields[i];
/* 442 */         if (nextIndex == this.numLowFields) {
/*     */           break;
/*     */         }
/*     */       } 
/*     */     } 
/* 447 */     if (!this.highFields.isEmpty()) {
/* 448 */       Iterator keys = this.highFields.keySet().iterator();
/* 449 */       while (keys.hasNext()) {
/* 450 */         fields[nextIndex++] = (TIFFField)this.highFields.get(keys.next());
/*     */       }
/*     */     } 
/*     */     
/* 454 */     return fields;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void removeTIFFFields() {
/* 461 */     Arrays.fill((Object[])this.lowFields, (Object)null);
/* 462 */     this.numLowFields = 0;
/* 463 */     this.highFields.clear();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public IIOMetadata getAsMetadata() {
/* 473 */     return (IIOMetadata)new TIFFImageMetadata(getDirectoryAsIFD(this));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object clone() {
/* 482 */     TIFFDirectory dir = new TIFFDirectory(getTagSets(), getParentTag());
/* 483 */     TIFFField[] fields = getTIFFFields();
/* 484 */     int numFields = fields.length;
/* 485 */     for (int i = 0; i < numFields; i++) {
/* 486 */       dir.addTIFFField(fields[i]);
/*     */     }
/*     */     
/* 489 */     return dir;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\github\jaiimageio\plugins\tiff\TIFFDirectory.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */