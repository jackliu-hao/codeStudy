/*     */ package com.github.jaiimageio.plugins.tiff;
/*     */ 
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class TIFFTag
/*     */ {
/*     */   public static final int TIFF_BYTE = 1;
/*     */   public static final int TIFF_ASCII = 2;
/*     */   public static final int TIFF_SHORT = 3;
/*     */   public static final int TIFF_LONG = 4;
/*     */   public static final int TIFF_RATIONAL = 5;
/*     */   public static final int TIFF_SBYTE = 6;
/*     */   public static final int TIFF_UNDEFINED = 7;
/*     */   public static final int TIFF_SSHORT = 8;
/*     */   public static final int TIFF_SLONG = 9;
/*     */   public static final int TIFF_SRATIONAL = 10;
/*     */   public static final int TIFF_FLOAT = 11;
/*     */   public static final int TIFF_DOUBLE = 12;
/*     */   public static final int TIFF_IFD_POINTER = 13;
/*     */   public static final int MIN_DATATYPE = 1;
/*     */   public static final int MAX_DATATYPE = 13;
/* 121 */   private static final int[] sizeOfType = new int[] { 0, 1, 1, 2, 4, 8, 1, 1, 2, 4, 8, 4, 8, 4 };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   int number;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   String name;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   int dataTypes;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 195 */   TIFFTagSet tagSet = null;
/*     */ 
/*     */   
/* 198 */   Map valueNames = null;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public TIFFTag(String name, int number, int dataTypes, TIFFTagSet tagSet) {
/* 228 */     this.name = name;
/* 229 */     this.number = number;
/* 230 */     this.dataTypes = dataTypes;
/* 231 */     this.tagSet = tagSet;
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
/*     */   public TIFFTag(String name, int number, int dataTypes) {
/* 247 */     this(name, number, dataTypes, null);
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
/*     */   public static int getSizeOfType(int dataType) {
/* 263 */     if (dataType < 1 || dataType > 13) {
/* 264 */       throw new IllegalArgumentException("dataType out of range!");
/*     */     }
/*     */     
/* 267 */     return sizeOfType[dataType];
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getName() {
/* 276 */     return this.name;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getNumber() {
/* 285 */     return this.number;
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
/*     */   
/*     */   public int getDataTypes() {
/* 302 */     return this.dataTypes;
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
/*     */ 
/*     */   
/*     */   public boolean isDataTypeOK(int dataType) {
/* 320 */     if (dataType < 1 || dataType > 13) {
/* 321 */       throw new IllegalArgumentException("datatype not in range!");
/*     */     }
/* 323 */     return ((this.dataTypes & 1 << dataType) != 0);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public TIFFTagSet getTagSet() {
/* 332 */     return this.tagSet;
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
/*     */   public boolean isIFDPointer() {
/* 348 */     return (this.tagSet != null || (this.dataTypes & 0x2000) != 0);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean hasValueNames() {
/* 358 */     return (this.valueNames != null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void addValueName(int value, String name) {
/* 369 */     if (this.valueNames == null) {
/* 370 */       this.valueNames = new HashMap<Object, Object>();
/*     */     }
/* 372 */     this.valueNames.put(new Integer(value), name);
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
/*     */   public String getValueName(int value) {
/* 386 */     if (this.valueNames == null) {
/* 387 */       return null;
/*     */     }
/* 389 */     return (String)this.valueNames.get(new Integer(value));
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\github\jaiimageio\plugins\tiff\TIFFTag.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */