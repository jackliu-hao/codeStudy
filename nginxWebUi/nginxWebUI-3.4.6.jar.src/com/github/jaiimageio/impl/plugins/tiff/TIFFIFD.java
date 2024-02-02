/*     */ package com.github.jaiimageio.impl.plugins.tiff;
/*     */ 
/*     */ import com.github.jaiimageio.plugins.tiff.BaselineTIFFTagSet;
/*     */ import com.github.jaiimageio.plugins.tiff.TIFFDirectory;
/*     */ import com.github.jaiimageio.plugins.tiff.TIFFField;
/*     */ import com.github.jaiimageio.plugins.tiff.TIFFTag;
/*     */ import com.github.jaiimageio.plugins.tiff.TIFFTagSet;
/*     */ import java.io.EOFException;
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ import java.util.Vector;
/*     */ import javax.imageio.stream.ImageInputStream;
/*     */ import javax.imageio.stream.ImageOutputStream;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class TIFFIFD
/*     */   extends TIFFDirectory
/*     */ {
/*  67 */   private long stripOrTileByteCountsPosition = -1L;
/*  68 */   private long stripOrTileOffsetsPosition = -1L;
/*  69 */   private long lastPosition = -1L;
/*     */   
/*     */   public static TIFFTag getTag(int tagNumber, List tagSets) {
/*  72 */     Iterator<TIFFTagSet> iter = tagSets.iterator();
/*  73 */     while (iter.hasNext()) {
/*  74 */       TIFFTagSet tagSet = iter.next();
/*  75 */       TIFFTag tag = tagSet.getTag(tagNumber);
/*  76 */       if (tag != null) {
/*  77 */         return tag;
/*     */       }
/*     */     } 
/*     */     
/*  81 */     return null;
/*     */   }
/*     */   
/*     */   public static TIFFTag getTag(String tagName, List tagSets) {
/*  85 */     Iterator<TIFFTagSet> iter = tagSets.iterator();
/*  86 */     while (iter.hasNext()) {
/*  87 */       TIFFTagSet tagSet = iter.next();
/*  88 */       TIFFTag tag = tagSet.getTag(tagName);
/*  89 */       if (tag != null) {
/*  90 */         return tag;
/*     */       }
/*     */     } 
/*     */     
/*  94 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static void writeTIFFFieldToStream(TIFFField field, ImageOutputStream stream) throws IOException {
/* 100 */     int i, count = field.getCount();
/* 101 */     Object data = field.getData();
/*     */     
/* 103 */     switch (field.getType()) {
/*     */       case 2:
/* 105 */         for (i = 0; i < count; i++) {
/* 106 */           String s = ((String[])data)[i];
/* 107 */           int length = s.length();
/* 108 */           for (int j = 0; j < length; j++) {
/* 109 */             stream.writeByte(s.charAt(j) & 0xFF);
/*     */           }
/* 111 */           stream.writeByte(0);
/*     */         } 
/*     */         break;
/*     */       case 1:
/*     */       case 6:
/*     */       case 7:
/* 117 */         stream.write((byte[])data);
/*     */         break;
/*     */       case 3:
/* 120 */         stream.writeChars((char[])data, 0, ((char[])data).length);
/*     */         break;
/*     */       case 8:
/* 123 */         stream.writeShorts((short[])data, 0, ((short[])data).length);
/*     */         break;
/*     */       case 9:
/* 126 */         stream.writeInts((int[])data, 0, ((int[])data).length);
/*     */         break;
/*     */       case 4:
/* 129 */         for (i = 0; i < count; i++) {
/* 130 */           stream.writeInt((int)((long[])data)[i]);
/*     */         }
/*     */         break;
/*     */       case 13:
/* 134 */         stream.writeInt(0);
/*     */         break;
/*     */       case 11:
/* 137 */         stream.writeFloats((float[])data, 0, ((float[])data).length);
/*     */         break;
/*     */       case 12:
/* 140 */         stream.writeDoubles((double[])data, 0, ((double[])data).length);
/*     */         break;
/*     */       case 10:
/* 143 */         for (i = 0; i < count; i++) {
/* 144 */           stream.writeInt(((int[][])data)[i][0]);
/* 145 */           stream.writeInt(((int[][])data)[i][1]);
/*     */         } 
/*     */         break;
/*     */       case 5:
/* 149 */         for (i = 0; i < count; i++) {
/* 150 */           long num = ((long[][])data)[i][0];
/* 151 */           long den = ((long[][])data)[i][1];
/* 152 */           stream.writeInt((int)num);
/* 153 */           stream.writeInt((int)den);
/*     */         } 
/*     */         break;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public TIFFIFD(List tagSets, TIFFTag parentTag) {
/* 162 */     super((TIFFTagSet[])tagSets.toArray((Object[])new TIFFTagSet[tagSets.size()]), parentTag);
/*     */   }
/*     */ 
/*     */   
/*     */   public TIFFIFD(List tagSets) {
/* 167 */     this(tagSets, (TIFFTag)null);
/*     */   }
/*     */   
/*     */   public List getTagSetList() {
/* 171 */     return Arrays.asList(getTagSets());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Iterator iterator() {
/* 182 */     return Arrays.<TIFFField>asList(getTIFFFields()).iterator();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void initialize(ImageInputStream stream, boolean ignoreUnknownFields) throws IOException {
/* 190 */     removeTIFFFields();
/*     */     
/* 192 */     List tagSetList = getTagSetList();
/*     */     
/* 194 */     int numEntries = stream.readUnsignedShort();
/* 195 */     for (int i = 0; i < numEntries; i++) {
/*     */       
/* 197 */       int tag = stream.readUnsignedShort();
/* 198 */       int type = stream.readUnsignedShort();
/* 199 */       int count = (int)stream.readUnsignedInt();
/*     */ 
/*     */       
/* 202 */       TIFFTag tiffTag = getTag(tag, tagSetList);
/*     */ 
/*     */       
/* 205 */       if (ignoreUnknownFields && tiffTag == null) {
/*     */ 
/*     */         
/* 208 */         stream.skipBytes(4);
/*     */ 
/*     */       
/*     */       }
/*     */       else {
/*     */ 
/*     */ 
/*     */         
/* 216 */         long nextTagOffset = stream.getStreamPosition() + 4L;
/*     */         
/* 218 */         int sizeOfType = TIFFTag.getSizeOfType(type);
/* 219 */         if (count * sizeOfType > 4) {
/* 220 */           long value = stream.readUnsignedInt();
/* 221 */           stream.seek(value);
/*     */         } 
/*     */         
/* 224 */         if (tag == 279 || tag == 325 || tag == 514) {
/*     */ 
/*     */           
/* 227 */           this
/* 228 */             .stripOrTileByteCountsPosition = stream.getStreamPosition();
/* 229 */         } else if (tag == 273 || tag == 324 || tag == 513) {
/*     */ 
/*     */           
/* 232 */           this
/* 233 */             .stripOrTileOffsetsPosition = stream.getStreamPosition();
/*     */         } 
/*     */         
/* 236 */         Object obj = null; try {
/*     */           byte[] bvalues; char[] cvalues; int j; long[] lvalues; int k; long[][] llvalues; int m; short[] svalues; int n; int[] ivalues; int i1; int[][] iivalues; int i2; float[] fvalues; int i3; double[] dvalues;
/*     */           int i4;
/* 239 */           switch (type) {
/*     */             case 1:
/*     */             case 2:
/*     */             case 6:
/*     */             case 7:
/* 244 */               bvalues = new byte[count];
/* 245 */               stream.readFully(bvalues, 0, count);
/*     */               
/* 247 */               if (type == 2) {
/*     */                 String[] strings;
/* 249 */                 Vector<String> v = new Vector();
/* 250 */                 boolean inString = false;
/* 251 */                 int prevIndex = 0;
/* 252 */                 for (int index = 0; index <= count; index++) {
/* 253 */                   if (index < count && bvalues[index] != 0) {
/* 254 */                     if (!inString)
/*     */                     {
/* 256 */                       prevIndex = index;
/* 257 */                       inString = true;
/*     */                     }
/*     */                   
/* 260 */                   } else if (inString) {
/*     */                     
/* 262 */                     String s = new String(bvalues, prevIndex, index - prevIndex);
/*     */                     
/* 264 */                     v.add(s);
/* 265 */                     inString = false;
/*     */                   } 
/*     */                 } 
/*     */ 
/*     */                 
/* 270 */                 count = v.size();
/*     */                 
/* 272 */                 if (count != 0) {
/* 273 */                   strings = new String[count];
/* 274 */                   for (int c = 0; c < count; c++) {
/* 275 */                     strings[c] = v.elementAt(c);
/*     */                   
/*     */                   }
/*     */                 }
/*     */                 else {
/*     */                   
/* 281 */                   count = 1;
/* 282 */                   strings = new String[] { "" };
/*     */                 } 
/*     */                 
/* 285 */                 obj = strings; break;
/*     */               } 
/* 287 */               obj = bvalues;
/*     */               break;
/*     */ 
/*     */             
/*     */             case 3:
/* 292 */               cvalues = new char[count];
/* 293 */               for (j = 0; j < count; j++) {
/* 294 */                 cvalues[j] = (char)stream.readUnsignedShort();
/*     */               }
/* 296 */               obj = cvalues;
/*     */               break;
/*     */             
/*     */             case 4:
/*     */             case 13:
/* 301 */               lvalues = new long[count];
/* 302 */               for (k = 0; k < count; k++) {
/* 303 */                 lvalues[k] = stream.readUnsignedInt();
/*     */               }
/* 305 */               obj = lvalues;
/*     */               break;
/*     */             
/*     */             case 5:
/* 309 */               llvalues = new long[count][2];
/* 310 */               for (m = 0; m < count; m++) {
/* 311 */                 llvalues[m][0] = stream.readUnsignedInt();
/* 312 */                 llvalues[m][1] = stream.readUnsignedInt();
/*     */               } 
/* 314 */               obj = llvalues;
/*     */               break;
/*     */             
/*     */             case 8:
/* 318 */               svalues = new short[count];
/* 319 */               for (n = 0; n < count; n++) {
/* 320 */                 svalues[n] = stream.readShort();
/*     */               }
/* 322 */               obj = svalues;
/*     */               break;
/*     */             
/*     */             case 9:
/* 326 */               ivalues = new int[count];
/* 327 */               for (i1 = 0; i1 < count; i1++) {
/* 328 */                 ivalues[i1] = stream.readInt();
/*     */               }
/* 330 */               obj = ivalues;
/*     */               break;
/*     */             
/*     */             case 10:
/* 334 */               iivalues = new int[count][2];
/* 335 */               for (i2 = 0; i2 < count; i2++) {
/* 336 */                 iivalues[i2][0] = stream.readInt();
/* 337 */                 iivalues[i2][1] = stream.readInt();
/*     */               } 
/* 339 */               obj = iivalues;
/*     */               break;
/*     */             
/*     */             case 11:
/* 343 */               fvalues = new float[count];
/* 344 */               for (i3 = 0; i3 < count; i3++) {
/* 345 */                 fvalues[i3] = stream.readFloat();
/*     */               }
/* 347 */               obj = fvalues;
/*     */               break;
/*     */             
/*     */             case 12:
/* 351 */               dvalues = new double[count];
/* 352 */               for (i4 = 0; i4 < count; i4++) {
/* 353 */                 dvalues[i4] = stream.readDouble();
/*     */               }
/* 355 */               obj = dvalues;
/*     */               break;
/*     */           } 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 362 */         } catch (EOFException eofe) {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */           
/* 368 */           if (BaselineTIFFTagSet.getInstance().getTag(tag) == null) {
/* 369 */             throw eofe;
/*     */           }
/*     */         } 
/*     */         
/* 373 */         if (tiffTag != null)
/*     */         {
/* 375 */           if (tiffTag.isDataTypeOK(type))
/*     */           {
/* 377 */             if (tiffTag.isIFDPointer() && obj != null) {
/* 378 */               stream.mark();
/* 379 */               stream.seek(((long[])obj)[0]);
/*     */               
/* 381 */               List<TIFFTagSet> tagSets = new ArrayList(1);
/* 382 */               tagSets.add(tiffTag.getTagSet());
/* 383 */               TIFFIFD subIFD = new TIFFIFD(tagSets);
/*     */ 
/*     */               
/* 386 */               subIFD.initialize(stream, ignoreUnknownFields);
/* 387 */               obj = subIFD;
/* 388 */               stream.reset();
/*     */             }  } 
/*     */         }
/* 391 */         if (tiffTag == null) {
/* 392 */           tiffTag = new TIFFTag(null, tag, 1 << type, null);
/*     */         }
/*     */ 
/*     */ 
/*     */         
/* 397 */         if (obj != null) {
/* 398 */           TIFFField f = new TIFFField(tiffTag, type, count, obj);
/* 399 */           addTIFFField(f);
/*     */         } 
/*     */         
/* 402 */         stream.seek(nextTagOffset);
/*     */       } 
/*     */     } 
/* 405 */     this.lastPosition = stream.getStreamPosition();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void writeToStream(ImageOutputStream stream) throws IOException {
/* 411 */     int numFields = getNumTIFFFields();
/* 412 */     stream.writeShort(numFields);
/*     */     
/* 414 */     long nextSpace = stream.getStreamPosition() + (12 * numFields) + 4L;
/*     */     
/* 416 */     Iterator<TIFFField> iter = iterator();
/* 417 */     while (iter.hasNext()) {
/* 418 */       long pos; TIFFField f = iter.next();
/*     */       
/* 420 */       TIFFTag tag = f.getTag();
/*     */       
/* 422 */       int type = f.getType();
/* 423 */       int count = f.getCount();
/*     */ 
/*     */       
/* 426 */       if (type == 0) {
/* 427 */         type = 7;
/*     */       }
/* 429 */       int size = count * TIFFTag.getSizeOfType(type);
/*     */       
/* 431 */       if (type == 2) {
/* 432 */         int chars = 0;
/* 433 */         for (int i = 0; i < count; i++) {
/* 434 */           chars += f.getAsString(i).length() + 1;
/*     */         }
/* 436 */         count = chars;
/* 437 */         size = count;
/*     */       } 
/*     */       
/* 440 */       int tagNumber = f.getTagNumber();
/* 441 */       stream.writeShort(tagNumber);
/* 442 */       stream.writeShort(type);
/* 443 */       stream.writeInt(count);
/*     */ 
/*     */       
/* 446 */       stream.writeInt(0);
/* 447 */       stream.mark();
/* 448 */       stream.skipBytes(-4);
/*     */ 
/*     */ 
/*     */       
/* 452 */       if (size > 4 || tag.isIFDPointer()) {
/*     */         
/* 454 */         nextSpace = nextSpace + 3L & 0xFFFFFFFFFFFFFFFCL;
/*     */         
/* 456 */         stream.writeInt((int)nextSpace);
/* 457 */         stream.seek(nextSpace);
/* 458 */         pos = nextSpace;
/*     */         
/* 460 */         if (tag.isIFDPointer()) {
/* 461 */           TIFFIFD subIFD = (TIFFIFD)f.getData();
/* 462 */           subIFD.writeToStream(stream);
/* 463 */           nextSpace = subIFD.lastPosition;
/*     */         } else {
/* 465 */           writeTIFFFieldToStream(f, stream);
/* 466 */           nextSpace = stream.getStreamPosition();
/*     */         } 
/*     */       } else {
/* 469 */         pos = stream.getStreamPosition();
/* 470 */         writeTIFFFieldToStream(f, stream);
/*     */       } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 478 */       if (tagNumber == 279 || tagNumber == 325 || tagNumber == 514) {
/*     */ 
/*     */ 
/*     */         
/* 482 */         this.stripOrTileByteCountsPosition = pos;
/* 483 */       } else if (tagNumber == 273 || tagNumber == 324 || tagNumber == 513) {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 489 */         this.stripOrTileOffsetsPosition = pos;
/*     */       } 
/*     */       
/* 492 */       stream.reset();
/*     */     } 
/*     */     
/* 495 */     this.lastPosition = nextSpace;
/*     */   }
/*     */   
/*     */   public long getStripOrTileByteCountsPosition() {
/* 499 */     return this.stripOrTileByteCountsPosition;
/*     */   }
/*     */   
/*     */   public long getStripOrTileOffsetsPosition() {
/* 503 */     return this.stripOrTileOffsetsPosition;
/*     */   }
/*     */   
/*     */   public long getLastPosition() {
/* 507 */     return this.lastPosition;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   void setPositions(long stripOrTileOffsetsPosition, long stripOrTileByteCountsPosition, long lastPosition) {
/* 513 */     this.stripOrTileOffsetsPosition = stripOrTileOffsetsPosition;
/* 514 */     this.stripOrTileByteCountsPosition = stripOrTileByteCountsPosition;
/* 515 */     this.lastPosition = lastPosition;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public TIFFIFD getShallowClone() {
/* 525 */     BaselineTIFFTagSet baselineTIFFTagSet = BaselineTIFFTagSet.getInstance();
/*     */ 
/*     */     
/* 528 */     List tagSetList = getTagSetList();
/* 529 */     if (!tagSetList.contains(baselineTIFFTagSet)) {
/* 530 */       return this;
/*     */     }
/*     */ 
/*     */     
/* 534 */     TIFFIFD shallowClone = new TIFFIFD(tagSetList, getParentTag());
/*     */ 
/*     */     
/* 537 */     Set baselineTagNumbers = baselineTIFFTagSet.getTagNumbers();
/*     */ 
/*     */     
/* 540 */     Iterator<TIFFField> fields = iterator();
/* 541 */     while (fields.hasNext()) {
/*     */       
/* 543 */       TIFFField fieldClone, field = fields.next();
/*     */ 
/*     */       
/* 546 */       Integer tagNumber = new Integer(field.getTagNumber());
/*     */ 
/*     */ 
/*     */       
/* 550 */       if (baselineTagNumbers.contains(tagNumber)) {
/*     */         
/* 552 */         Object fieldData = field.getData();
/*     */         
/* 554 */         int fieldType = field.getType();
/*     */         
/*     */         try {
/* 557 */           switch (fieldType) {
/*     */             case 1:
/*     */             case 6:
/*     */             case 7:
/* 561 */               fieldData = ((byte[])fieldData).clone();
/*     */               break;
/*     */             case 2:
/* 564 */               fieldData = ((String[])fieldData).clone();
/*     */               break;
/*     */             case 3:
/* 567 */               fieldData = ((char[])fieldData).clone();
/*     */               break;
/*     */             case 4:
/*     */             case 13:
/* 571 */               fieldData = ((long[])fieldData).clone();
/*     */               break;
/*     */             case 5:
/* 574 */               fieldData = ((long[][])fieldData).clone();
/*     */               break;
/*     */             case 8:
/* 577 */               fieldData = ((short[])fieldData).clone();
/*     */               break;
/*     */             case 9:
/* 580 */               fieldData = ((int[])fieldData).clone();
/*     */               break;
/*     */             case 10:
/* 583 */               fieldData = ((int[][])fieldData).clone();
/*     */               break;
/*     */             case 11:
/* 586 */               fieldData = ((float[])fieldData).clone();
/*     */               break;
/*     */             case 12:
/* 589 */               fieldData = ((double[])fieldData).clone();
/*     */               break;
/*     */           } 
/*     */ 
/*     */         
/* 594 */         } catch (Exception exception) {}
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 599 */         fieldClone = new TIFFField(field.getTag(), fieldType, field.getCount(), fieldData);
/*     */       } else {
/*     */         
/* 602 */         fieldClone = field;
/*     */       } 
/*     */ 
/*     */       
/* 606 */       shallowClone.addTIFFField(fieldClone);
/*     */     } 
/*     */ 
/*     */     
/* 610 */     shallowClone.setPositions(this.stripOrTileOffsetsPosition, this.stripOrTileByteCountsPosition, this.lastPosition);
/*     */ 
/*     */ 
/*     */     
/* 614 */     return shallowClone;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\github\jaiimageio\impl\plugins\tiff\TIFFIFD.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */