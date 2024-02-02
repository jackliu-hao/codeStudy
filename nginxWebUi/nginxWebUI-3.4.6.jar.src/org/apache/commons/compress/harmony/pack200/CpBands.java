/*     */ package org.apache.commons.compress.harmony.pack200;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.TreeSet;
/*     */ import org.objectweb.asm.Type;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class CpBands
/*     */   extends BandSet
/*     */ {
/*  38 */   private final Set defaultAttributeNames = new HashSet();
/*     */   
/*  40 */   private final Set cp_Utf8 = new TreeSet();
/*  41 */   private final Set cp_Int = new TreeSet();
/*  42 */   private final Set cp_Float = new TreeSet();
/*  43 */   private final Set cp_Long = new TreeSet();
/*  44 */   private final Set cp_Double = new TreeSet();
/*  45 */   private final Set cp_String = new TreeSet();
/*  46 */   private final Set cp_Class = new TreeSet();
/*  47 */   private final Set cp_Signature = new TreeSet();
/*  48 */   private final Set cp_Descr = new TreeSet();
/*  49 */   private final Set cp_Field = new TreeSet();
/*  50 */   private final Set cp_Method = new TreeSet();
/*  51 */   private final Set cp_Imethod = new TreeSet();
/*     */   
/*  53 */   private final Map stringsToCpUtf8 = new HashMap<>();
/*  54 */   private final Map stringsToCpNameAndType = new HashMap<>();
/*  55 */   private final Map stringsToCpClass = new HashMap<>();
/*  56 */   private final Map stringsToCpSignature = new HashMap<>();
/*  57 */   private final Map stringsToCpMethod = new HashMap<>();
/*  58 */   private final Map stringsToCpField = new HashMap<>();
/*  59 */   private final Map stringsToCpIMethod = new HashMap<>();
/*     */   
/*  61 */   private final Map objectsToCPConstant = new HashMap<>();
/*     */   
/*     */   private final Segment segment;
/*     */   
/*     */   public CpBands(Segment segment, int effort) {
/*  66 */     super(effort, segment.getSegmentHeader());
/*  67 */     this.segment = segment;
/*  68 */     this.defaultAttributeNames.add("AnnotationDefault");
/*  69 */     this.defaultAttributeNames.add("RuntimeVisibleAnnotations");
/*  70 */     this.defaultAttributeNames.add("RuntimeInvisibleAnnotations");
/*  71 */     this.defaultAttributeNames.add("RuntimeVisibleParameterAnnotations");
/*  72 */     this.defaultAttributeNames.add("RuntimeInvisibleParameterAnnotations");
/*  73 */     this.defaultAttributeNames.add("Code");
/*  74 */     this.defaultAttributeNames.add("LineNumberTable");
/*  75 */     this.defaultAttributeNames.add("LocalVariableTable");
/*  76 */     this.defaultAttributeNames.add("LocalVariableTypeTable");
/*  77 */     this.defaultAttributeNames.add("ConstantValue");
/*  78 */     this.defaultAttributeNames.add("Deprecated");
/*  79 */     this.defaultAttributeNames.add("EnclosingMethod");
/*  80 */     this.defaultAttributeNames.add("Exceptions");
/*  81 */     this.defaultAttributeNames.add("InnerClasses");
/*  82 */     this.defaultAttributeNames.add("Signature");
/*  83 */     this.defaultAttributeNames.add("SourceFile");
/*     */   }
/*     */ 
/*     */   
/*     */   public void pack(OutputStream out) throws IOException, Pack200Exception {
/*  88 */     PackingUtils.log("Writing constant pool bands...");
/*  89 */     writeCpUtf8(out);
/*  90 */     writeCpInt(out);
/*  91 */     writeCpFloat(out);
/*  92 */     writeCpLong(out);
/*  93 */     writeCpDouble(out);
/*  94 */     writeCpString(out);
/*  95 */     writeCpClass(out);
/*  96 */     writeCpSignature(out);
/*  97 */     writeCpDescr(out);
/*  98 */     writeCpMethodOrField(this.cp_Field, out, "cp_Field");
/*  99 */     writeCpMethodOrField(this.cp_Method, out, "cp_Method");
/* 100 */     writeCpMethodOrField(this.cp_Imethod, out, "cp_Imethod");
/*     */   }
/*     */   
/*     */   private void writeCpUtf8(OutputStream out) throws IOException, Pack200Exception {
/* 104 */     PackingUtils.log("Writing " + this.cp_Utf8.size() + " UTF8 entries...");
/* 105 */     int[] cpUtf8Prefix = new int[this.cp_Utf8.size() - 2];
/* 106 */     int[] cpUtf8Suffix = new int[this.cp_Utf8.size() - 1];
/* 107 */     List chars = new ArrayList();
/* 108 */     List<Integer> bigSuffix = new ArrayList();
/* 109 */     List bigChars = new ArrayList();
/* 110 */     Object[] cpUtf8Array = this.cp_Utf8.toArray();
/* 111 */     String first = ((CPUTF8)cpUtf8Array[1]).getUnderlyingString();
/* 112 */     cpUtf8Suffix[0] = first.length();
/* 113 */     addCharacters(chars, first.toCharArray());
/* 114 */     for (int i = 2; i < cpUtf8Array.length; i++) {
/* 115 */       char[] previous = ((CPUTF8)cpUtf8Array[i - 1]).getUnderlyingString().toCharArray();
/* 116 */       String currentStr = ((CPUTF8)cpUtf8Array[i]).getUnderlyingString();
/* 117 */       char[] current = currentStr.toCharArray();
/* 118 */       int prefix = 0;
/* 119 */       for (int m = 0; m < previous.length && 
/* 120 */         previous[m] == current[m]; m++)
/*     */       {
/*     */         
/* 123 */         prefix++;
/*     */       }
/* 125 */       cpUtf8Prefix[i - 2] = prefix;
/* 126 */       currentStr = currentStr.substring(prefix);
/* 127 */       char[] suffix = currentStr.toCharArray();
/* 128 */       if (suffix.length > 1000) {
/*     */         
/* 130 */         cpUtf8Suffix[i - 1] = 0;
/* 131 */         bigSuffix.add(Integer.valueOf(suffix.length));
/* 132 */         addCharacters(bigChars, suffix);
/*     */       } else {
/* 134 */         cpUtf8Suffix[i - 1] = suffix.length;
/* 135 */         addCharacters(chars, suffix);
/*     */       } 
/*     */     } 
/* 138 */     int[] cpUtf8Chars = new int[chars.size()];
/* 139 */     int[] cpUtf8BigSuffix = new int[bigSuffix.size()];
/* 140 */     int[][] cpUtf8BigChars = new int[bigSuffix.size()][]; int j;
/* 141 */     for (j = 0; j < cpUtf8Chars.length; j++) {
/* 142 */       cpUtf8Chars[j] = ((Character)chars.get(j)).charValue();
/*     */     }
/* 144 */     for (j = 0; j < cpUtf8BigSuffix.length; j++) {
/* 145 */       int numBigChars = ((Integer)bigSuffix.get(j)).intValue();
/* 146 */       cpUtf8BigSuffix[j] = numBigChars;
/* 147 */       cpUtf8BigChars[j] = new int[numBigChars];
/* 148 */       for (int m = 0; m < numBigChars; m++) {
/* 149 */         cpUtf8BigChars[j][m] = ((Character)bigChars.remove(0)).charValue();
/*     */       }
/*     */     } 
/*     */     
/* 153 */     byte[] encodedBand = encodeBandInt("cpUtf8Prefix", cpUtf8Prefix, Codec.DELTA5);
/* 154 */     out.write(encodedBand);
/* 155 */     PackingUtils.log("Wrote " + encodedBand.length + " bytes from cpUtf8Prefix[" + cpUtf8Prefix.length + "]");
/*     */     
/* 157 */     encodedBand = encodeBandInt("cpUtf8Suffix", cpUtf8Suffix, Codec.UNSIGNED5);
/* 158 */     out.write(encodedBand);
/* 159 */     PackingUtils.log("Wrote " + encodedBand.length + " bytes from cpUtf8Suffix[" + cpUtf8Suffix.length + "]");
/*     */     
/* 161 */     encodedBand = encodeBandInt("cpUtf8Chars", cpUtf8Chars, Codec.CHAR3);
/* 162 */     out.write(encodedBand);
/* 163 */     PackingUtils.log("Wrote " + encodedBand.length + " bytes from cpUtf8Chars[" + cpUtf8Chars.length + "]");
/*     */     
/* 165 */     encodedBand = encodeBandInt("cpUtf8BigSuffix", cpUtf8BigSuffix, Codec.DELTA5);
/* 166 */     out.write(encodedBand);
/* 167 */     PackingUtils.log("Wrote " + encodedBand.length + " bytes from cpUtf8BigSuffix[" + cpUtf8BigSuffix.length + "]");
/*     */     
/* 169 */     for (int k = 0; k < cpUtf8BigChars.length; k++) {
/* 170 */       encodedBand = encodeBandInt("cpUtf8BigChars " + k, cpUtf8BigChars[k], Codec.DELTA5);
/* 171 */       out.write(encodedBand);
/* 172 */       PackingUtils.log("Wrote " + encodedBand.length + " bytes from cpUtf8BigChars" + k + "[" + (cpUtf8BigChars[k]).length + "]");
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private void addCharacters(List<Character> chars, char[] charArray) {
/* 178 */     for (int i = 0; i < charArray.length; i++) {
/* 179 */       chars.add(Character.valueOf(charArray[i]));
/*     */     }
/*     */   }
/*     */   
/*     */   private void writeCpInt(OutputStream out) throws IOException, Pack200Exception {
/* 184 */     PackingUtils.log("Writing " + this.cp_Int.size() + " Integer entries...");
/* 185 */     int[] cpInt = new int[this.cp_Int.size()];
/* 186 */     int i = 0;
/* 187 */     for (Iterator<CPInt> iterator = this.cp_Int.iterator(); iterator.hasNext(); ) {
/* 188 */       CPInt integer = iterator.next();
/* 189 */       cpInt[i] = integer.getInt();
/* 190 */       i++;
/*     */     } 
/* 192 */     byte[] encodedBand = encodeBandInt("cp_Int", cpInt, Codec.UDELTA5);
/* 193 */     out.write(encodedBand);
/* 194 */     PackingUtils.log("Wrote " + encodedBand.length + " bytes from cp_Int[" + cpInt.length + "]");
/*     */   }
/*     */   
/*     */   private void writeCpFloat(OutputStream out) throws IOException, Pack200Exception {
/* 198 */     PackingUtils.log("Writing " + this.cp_Float.size() + " Float entries...");
/* 199 */     int[] cpFloat = new int[this.cp_Float.size()];
/* 200 */     int i = 0;
/* 201 */     for (Iterator<CPFloat> iterator = this.cp_Float.iterator(); iterator.hasNext(); ) {
/* 202 */       CPFloat fl = iterator.next();
/* 203 */       cpFloat[i] = Float.floatToIntBits(fl.getFloat());
/* 204 */       i++;
/*     */     } 
/* 206 */     byte[] encodedBand = encodeBandInt("cp_Float", cpFloat, Codec.UDELTA5);
/* 207 */     out.write(encodedBand);
/* 208 */     PackingUtils.log("Wrote " + encodedBand.length + " bytes from cp_Float[" + cpFloat.length + "]");
/*     */   }
/*     */   
/*     */   private void writeCpLong(OutputStream out) throws IOException, Pack200Exception {
/* 212 */     PackingUtils.log("Writing " + this.cp_Long.size() + " Long entries...");
/* 213 */     int[] highBits = new int[this.cp_Long.size()];
/* 214 */     int[] loBits = new int[this.cp_Long.size()];
/* 215 */     int i = 0;
/* 216 */     for (Iterator<CPLong> iterator = this.cp_Long.iterator(); iterator.hasNext(); ) {
/* 217 */       CPLong lng = iterator.next();
/* 218 */       long l = lng.getLong();
/* 219 */       highBits[i] = (int)(l >> 32L);
/* 220 */       loBits[i] = (int)l;
/* 221 */       i++;
/*     */     } 
/* 223 */     byte[] encodedBand = encodeBandInt("cp_Long_hi", highBits, Codec.UDELTA5);
/* 224 */     out.write(encodedBand);
/* 225 */     PackingUtils.log("Wrote " + encodedBand.length + " bytes from cp_Long_hi[" + highBits.length + "]");
/*     */     
/* 227 */     encodedBand = encodeBandInt("cp_Long_lo", loBits, Codec.DELTA5);
/* 228 */     out.write(encodedBand);
/* 229 */     PackingUtils.log("Wrote " + encodedBand.length + " bytes from cp_Long_lo[" + loBits.length + "]");
/*     */   }
/*     */   
/*     */   private void writeCpDouble(OutputStream out) throws IOException, Pack200Exception {
/* 233 */     PackingUtils.log("Writing " + this.cp_Double.size() + " Double entries...");
/* 234 */     int[] highBits = new int[this.cp_Double.size()];
/* 235 */     int[] loBits = new int[this.cp_Double.size()];
/* 236 */     int i = 0;
/* 237 */     for (Iterator<CPDouble> iterator = this.cp_Double.iterator(); iterator.hasNext(); ) {
/* 238 */       CPDouble dbl = iterator.next();
/* 239 */       long l = Double.doubleToLongBits(dbl.getDouble());
/* 240 */       highBits[i] = (int)(l >> 32L);
/* 241 */       loBits[i] = (int)l;
/* 242 */       i++;
/*     */     } 
/* 244 */     byte[] encodedBand = encodeBandInt("cp_Double_hi", highBits, Codec.UDELTA5);
/* 245 */     out.write(encodedBand);
/* 246 */     PackingUtils.log("Wrote " + encodedBand.length + " bytes from cp_Double_hi[" + highBits.length + "]");
/*     */     
/* 248 */     encodedBand = encodeBandInt("cp_Double_lo", loBits, Codec.DELTA5);
/* 249 */     out.write(encodedBand);
/* 250 */     PackingUtils.log("Wrote " + encodedBand.length + " bytes from cp_Double_lo[" + loBits.length + "]");
/*     */   }
/*     */   
/*     */   private void writeCpString(OutputStream out) throws IOException, Pack200Exception {
/* 254 */     PackingUtils.log("Writing " + this.cp_String.size() + " String entries...");
/* 255 */     int[] cpString = new int[this.cp_String.size()];
/* 256 */     int i = 0;
/* 257 */     for (Iterator<CPString> iterator = this.cp_String.iterator(); iterator.hasNext(); ) {
/* 258 */       CPString cpStr = iterator.next();
/* 259 */       cpString[i] = cpStr.getIndexInCpUtf8();
/* 260 */       i++;
/*     */     } 
/* 262 */     byte[] encodedBand = encodeBandInt("cpString", cpString, Codec.UDELTA5);
/* 263 */     out.write(encodedBand);
/* 264 */     PackingUtils.log("Wrote " + encodedBand.length + " bytes from cpString[" + cpString.length + "]");
/*     */   }
/*     */   
/*     */   private void writeCpClass(OutputStream out) throws IOException, Pack200Exception {
/* 268 */     PackingUtils.log("Writing " + this.cp_Class.size() + " Class entries...");
/* 269 */     int[] cpClass = new int[this.cp_Class.size()];
/* 270 */     int i = 0;
/* 271 */     for (Iterator<CPClass> iterator = this.cp_Class.iterator(); iterator.hasNext(); ) {
/* 272 */       CPClass cpCl = iterator.next();
/* 273 */       cpClass[i] = cpCl.getIndexInCpUtf8();
/* 274 */       i++;
/*     */     } 
/* 276 */     byte[] encodedBand = encodeBandInt("cpClass", cpClass, Codec.UDELTA5);
/* 277 */     out.write(encodedBand);
/* 278 */     PackingUtils.log("Wrote " + encodedBand.length + " bytes from cpClass[" + cpClass.length + "]");
/*     */   }
/*     */   
/*     */   private void writeCpSignature(OutputStream out) throws IOException, Pack200Exception {
/* 282 */     PackingUtils.log("Writing " + this.cp_Signature.size() + " Signature entries...");
/* 283 */     int[] cpSignatureForm = new int[this.cp_Signature.size()];
/* 284 */     List classes = new ArrayList();
/* 285 */     int i = 0;
/* 286 */     for (Iterator<CPSignature> iterator = this.cp_Signature.iterator(); iterator.hasNext(); ) {
/* 287 */       CPSignature cpS = iterator.next();
/* 288 */       classes.addAll(cpS.getClasses());
/* 289 */       cpSignatureForm[i] = cpS.getIndexInCpUtf8();
/* 290 */       i++;
/*     */     } 
/* 292 */     int[] cpSignatureClasses = new int[classes.size()];
/* 293 */     for (int j = 0; j < cpSignatureClasses.length; j++) {
/* 294 */       cpSignatureClasses[j] = ((CPClass)classes.get(j)).getIndex();
/*     */     }
/*     */     
/* 297 */     byte[] encodedBand = encodeBandInt("cpSignatureForm", cpSignatureForm, Codec.DELTA5);
/* 298 */     out.write(encodedBand);
/* 299 */     PackingUtils.log("Wrote " + encodedBand.length + " bytes from cpSignatureForm[" + cpSignatureForm.length + "]");
/*     */     
/* 301 */     encodedBand = encodeBandInt("cpSignatureClasses", cpSignatureClasses, Codec.UDELTA5);
/* 302 */     out.write(encodedBand);
/*     */     
/* 304 */     PackingUtils.log("Wrote " + encodedBand.length + " bytes from cpSignatureClasses[" + cpSignatureClasses.length + "]");
/*     */   }
/*     */   
/*     */   private void writeCpDescr(OutputStream out) throws IOException, Pack200Exception {
/* 308 */     PackingUtils.log("Writing " + this.cp_Descr.size() + " Descriptor entries...");
/* 309 */     int[] cpDescrName = new int[this.cp_Descr.size()];
/* 310 */     int[] cpDescrType = new int[this.cp_Descr.size()];
/* 311 */     int i = 0;
/* 312 */     for (Iterator<CPNameAndType> iterator = this.cp_Descr.iterator(); iterator.hasNext(); ) {
/* 313 */       CPNameAndType nameAndType = iterator.next();
/* 314 */       cpDescrName[i] = nameAndType.getNameIndex();
/* 315 */       cpDescrType[i] = nameAndType.getTypeIndex();
/* 316 */       i++;
/*     */     } 
/*     */     
/* 319 */     byte[] encodedBand = encodeBandInt("cp_Descr_Name", cpDescrName, Codec.DELTA5);
/* 320 */     out.write(encodedBand);
/* 321 */     PackingUtils.log("Wrote " + encodedBand.length + " bytes from cp_Descr_Name[" + cpDescrName.length + "]");
/*     */     
/* 323 */     encodedBand = encodeBandInt("cp_Descr_Type", cpDescrType, Codec.UDELTA5);
/* 324 */     out.write(encodedBand);
/* 325 */     PackingUtils.log("Wrote " + encodedBand.length + " bytes from cp_Descr_Type[" + cpDescrType.length + "]");
/*     */   }
/*     */ 
/*     */   
/*     */   private void writeCpMethodOrField(Set cp, OutputStream out, String name) throws IOException, Pack200Exception {
/* 330 */     PackingUtils.log("Writing " + cp.size() + " Method and Field entries...");
/* 331 */     int[] cp_methodOrField_class = new int[cp.size()];
/* 332 */     int[] cp_methodOrField_desc = new int[cp.size()];
/* 333 */     int i = 0;
/* 334 */     for (Iterator<CPMethodOrField> iterator = cp.iterator(); iterator.hasNext(); ) {
/* 335 */       CPMethodOrField mOrF = iterator.next();
/* 336 */       cp_methodOrField_class[i] = mOrF.getClassIndex();
/* 337 */       cp_methodOrField_desc[i] = mOrF.getDescIndex();
/* 338 */       i++;
/*     */     } 
/* 340 */     byte[] encodedBand = encodeBandInt(name + "_class", cp_methodOrField_class, Codec.DELTA5);
/* 341 */     out.write(encodedBand);
/* 342 */     PackingUtils.log("Wrote " + encodedBand.length + " bytes from " + name + "_class[" + cp_methodOrField_class.length + "]");
/*     */ 
/*     */     
/* 345 */     encodedBand = encodeBandInt(name + "_desc", cp_methodOrField_desc, Codec.UDELTA5);
/* 346 */     out.write(encodedBand);
/*     */     
/* 348 */     PackingUtils.log("Wrote " + encodedBand.length + " bytes from " + name + "_desc[" + cp_methodOrField_desc.length + "]");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void finaliseBands() {
/* 356 */     addCPUtf8("");
/* 357 */     removeSignaturesFromCpUTF8();
/* 358 */     addIndices();
/* 359 */     this.segmentHeader.setCp_Utf8_count(this.cp_Utf8.size());
/* 360 */     this.segmentHeader.setCp_Int_count(this.cp_Int.size());
/* 361 */     this.segmentHeader.setCp_Float_count(this.cp_Float.size());
/* 362 */     this.segmentHeader.setCp_Long_count(this.cp_Long.size());
/* 363 */     this.segmentHeader.setCp_Double_count(this.cp_Double.size());
/* 364 */     this.segmentHeader.setCp_String_count(this.cp_String.size());
/* 365 */     this.segmentHeader.setCp_Class_count(this.cp_Class.size());
/* 366 */     this.segmentHeader.setCp_Signature_count(this.cp_Signature.size());
/* 367 */     this.segmentHeader.setCp_Descr_count(this.cp_Descr.size());
/* 368 */     this.segmentHeader.setCp_Field_count(this.cp_Field.size());
/* 369 */     this.segmentHeader.setCp_Method_count(this.cp_Method.size());
/* 370 */     this.segmentHeader.setCp_Imethod_count(this.cp_Imethod.size());
/*     */   }
/*     */   
/*     */   private void removeSignaturesFromCpUTF8() {
/* 374 */     for (Iterator<CPSignature> iterator = this.cp_Signature.iterator(); iterator.hasNext(); ) {
/* 375 */       CPSignature signature = iterator.next();
/* 376 */       String sigStr = signature.getUnderlyingString();
/* 377 */       CPUTF8 utf8 = signature.getSignatureForm();
/* 378 */       String form = utf8.getUnderlyingString();
/* 379 */       if (!sigStr.equals(form)) {
/* 380 */         removeCpUtf8(sigStr);
/*     */       }
/*     */     } 
/*     */   }
/*     */   
/*     */   private void addIndices() {
/* 386 */     Set[] sets = { this.cp_Utf8, this.cp_Int, this.cp_Float, this.cp_Long, this.cp_Double, this.cp_String, this.cp_Class, this.cp_Signature, this.cp_Descr, this.cp_Field, this.cp_Method, this.cp_Imethod };
/*     */     
/* 388 */     for (int i = 0; i < sets.length; i++) {
/* 389 */       int j = 0;
/* 390 */       for (Iterator<?> iterator2 = sets[i].iterator(); iterator2.hasNext(); ) {
/* 391 */         ConstantPoolEntry entry = (ConstantPoolEntry)iterator2.next();
/* 392 */         entry.setIndex(j);
/* 393 */         j++;
/*     */       } 
/*     */     } 
/* 396 */     Map<Object, Object> classNameToIndex = new HashMap<>();
/* 397 */     for (Iterator<CPMethodOrField> iterator = this.cp_Field.iterator(); iterator.hasNext(); ) {
/* 398 */       CPMethodOrField mOrF = iterator.next();
/* 399 */       CPClass className = mOrF.getClassName();
/* 400 */       Integer index = (Integer)classNameToIndex.get(className);
/* 401 */       if (index == null) {
/* 402 */         classNameToIndex.put(className, Integer.valueOf(1));
/* 403 */         mOrF.setIndexInClass(0); continue;
/*     */       } 
/* 405 */       int theIndex = index.intValue();
/* 406 */       mOrF.setIndexInClass(theIndex);
/* 407 */       classNameToIndex.put(className, Integer.valueOf(theIndex + 1));
/*     */     } 
/*     */     
/* 410 */     classNameToIndex.clear();
/* 411 */     Map<Object, Object> classNameToConstructorIndex = new HashMap<>();
/* 412 */     for (Iterator<CPMethodOrField> iterator1 = this.cp_Method.iterator(); iterator1.hasNext(); ) {
/* 413 */       CPMethodOrField mOrF = iterator1.next();
/* 414 */       CPClass className = mOrF.getClassName();
/* 415 */       Integer index = (Integer)classNameToIndex.get(className);
/* 416 */       if (index == null) {
/* 417 */         classNameToIndex.put(className, Integer.valueOf(1));
/* 418 */         mOrF.setIndexInClass(0);
/*     */       } else {
/* 420 */         int theIndex = index.intValue();
/* 421 */         mOrF.setIndexInClass(theIndex);
/* 422 */         classNameToIndex.put(className, Integer.valueOf(theIndex + 1));
/*     */       } 
/* 424 */       if (mOrF.getDesc().getName().equals("<init>")) {
/* 425 */         Integer constructorIndex = (Integer)classNameToConstructorIndex.get(className);
/* 426 */         if (constructorIndex == null) {
/* 427 */           classNameToConstructorIndex.put(className, Integer.valueOf(1));
/* 428 */           mOrF.setIndexInClassForConstructor(0); continue;
/*     */         } 
/* 430 */         int theIndex = constructorIndex.intValue();
/* 431 */         mOrF.setIndexInClassForConstructor(theIndex);
/* 432 */         classNameToConstructorIndex.put(className, Integer.valueOf(theIndex + 1));
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private void removeCpUtf8(String string) {
/* 439 */     CPUTF8 utf8 = (CPUTF8)this.stringsToCpUtf8.get(string);
/* 440 */     if (utf8 != null && this.stringsToCpClass.get(string) == null) {
/* 441 */       this.stringsToCpUtf8.remove(string);
/* 442 */       this.cp_Utf8.remove(utf8);
/*     */     } 
/*     */   }
/*     */   
/*     */   void addCPUtf8(String utf8) {
/* 447 */     getCPUtf8(utf8);
/*     */   }
/*     */   
/*     */   public CPUTF8 getCPUtf8(String utf8) {
/* 451 */     if (utf8 == null) {
/* 452 */       return null;
/*     */     }
/* 454 */     CPUTF8 cpUtf8 = (CPUTF8)this.stringsToCpUtf8.get(utf8);
/* 455 */     if (cpUtf8 == null) {
/* 456 */       cpUtf8 = new CPUTF8(utf8);
/* 457 */       this.cp_Utf8.add(cpUtf8);
/* 458 */       this.stringsToCpUtf8.put(utf8, cpUtf8);
/*     */     } 
/* 460 */     return cpUtf8;
/*     */   }
/*     */   
/*     */   public CPSignature getCPSignature(String signature) {
/* 464 */     if (signature == null) {
/* 465 */       return null;
/*     */     }
/* 467 */     CPSignature cpS = (CPSignature)this.stringsToCpSignature.get(signature);
/* 468 */     if (cpS == null) {
/* 469 */       CPUTF8 signatureUTF8; List<CPClass> cpClasses = new ArrayList();
/*     */       
/* 471 */       if (signature.length() > 1 && signature.indexOf('L') != -1) {
/* 472 */         List<String> classes = new ArrayList();
/* 473 */         char[] chars = signature.toCharArray();
/* 474 */         StringBuffer signatureString = new StringBuffer();
/* 475 */         for (int i = 0; i < chars.length; i++) {
/* 476 */           signatureString.append(chars[i]);
/* 477 */           if (chars[i] == 'L') {
/* 478 */             StringBuffer className = new StringBuffer();
/* 479 */             for (int j = i + 1; j < chars.length; j++) {
/* 480 */               char c = chars[j];
/* 481 */               if (!Character.isLetter(c) && !Character.isDigit(c) && c != '/' && c != '$' && c != '_') {
/*     */                 
/* 483 */                 classes.add(className.toString());
/* 484 */                 i = j - 1;
/*     */                 break;
/*     */               } 
/* 487 */               className.append(c);
/*     */             } 
/*     */           } 
/*     */         } 
/* 491 */         removeCpUtf8(signature);
/* 492 */         for (Iterator<String> iterator2 = classes.iterator(); iterator2.hasNext(); ) {
/* 493 */           String className = iterator2.next();
/* 494 */           CPClass cpClass = null;
/* 495 */           if (className != null) {
/* 496 */             className = className.replace('.', '/');
/* 497 */             cpClass = (CPClass)this.stringsToCpClass.get(className);
/* 498 */             if (cpClass == null) {
/* 499 */               CPUTF8 cpUtf8 = getCPUtf8(className);
/* 500 */               cpClass = new CPClass(cpUtf8);
/* 501 */               this.cp_Class.add(cpClass);
/* 502 */               this.stringsToCpClass.put(className, cpClass);
/*     */             } 
/*     */           } 
/* 505 */           cpClasses.add(cpClass);
/*     */         } 
/*     */         
/* 508 */         signatureUTF8 = getCPUtf8(signatureString.toString());
/*     */       } else {
/* 510 */         signatureUTF8 = getCPUtf8(signature);
/*     */       } 
/* 512 */       cpS = new CPSignature(signature, signatureUTF8, cpClasses);
/* 513 */       this.cp_Signature.add(cpS);
/* 514 */       this.stringsToCpSignature.put(signature, cpS);
/*     */     } 
/* 516 */     return cpS;
/*     */   }
/*     */   
/*     */   public CPClass getCPClass(String className) {
/* 520 */     if (className == null) {
/* 521 */       return null;
/*     */     }
/* 523 */     className = className.replace('.', '/');
/* 524 */     CPClass cpClass = (CPClass)this.stringsToCpClass.get(className);
/* 525 */     if (cpClass == null) {
/* 526 */       CPUTF8 cpUtf8 = getCPUtf8(className);
/* 527 */       cpClass = new CPClass(cpUtf8);
/* 528 */       this.cp_Class.add(cpClass);
/* 529 */       this.stringsToCpClass.put(className, cpClass);
/*     */     } 
/* 531 */     if (cpClass.isInnerClass()) {
/* 532 */       this.segment.getClassBands().currentClassReferencesInnerClass(cpClass);
/*     */     }
/* 534 */     return cpClass;
/*     */   }
/*     */   
/*     */   public void addCPClass(String className) {
/* 538 */     getCPClass(className);
/*     */   }
/*     */   
/*     */   public CPNameAndType getCPNameAndType(String name, String signature) {
/* 542 */     String descr = name + ":" + signature;
/* 543 */     CPNameAndType nameAndType = (CPNameAndType)this.stringsToCpNameAndType.get(descr);
/* 544 */     if (nameAndType == null) {
/* 545 */       nameAndType = new CPNameAndType(getCPUtf8(name), getCPSignature(signature));
/* 546 */       this.stringsToCpNameAndType.put(descr, nameAndType);
/* 547 */       this.cp_Descr.add(nameAndType);
/*     */     } 
/* 549 */     return nameAndType;
/*     */   }
/*     */   
/*     */   public CPMethodOrField getCPField(CPClass cpClass, String name, String desc) {
/* 553 */     String key = cpClass.toString() + ":" + name + ":" + desc;
/* 554 */     CPMethodOrField cpF = (CPMethodOrField)this.stringsToCpField.get(key);
/* 555 */     if (cpF == null) {
/* 556 */       CPNameAndType nAndT = getCPNameAndType(name, desc);
/* 557 */       cpF = new CPMethodOrField(cpClass, nAndT);
/* 558 */       this.cp_Field.add(cpF);
/* 559 */       this.stringsToCpField.put(key, cpF);
/*     */     } 
/* 561 */     return cpF;
/*     */   }
/*     */   
/*     */   public CPConstant getConstant(Object value) {
/* 565 */     CPConstant constant = (CPConstant)this.objectsToCPConstant.get(value);
/* 566 */     if (constant == null) {
/* 567 */       if (value instanceof Integer) {
/* 568 */         constant = new CPInt(((Integer)value).intValue());
/* 569 */         this.cp_Int.add(constant);
/* 570 */       } else if (value instanceof Long) {
/* 571 */         constant = new CPLong(((Long)value).longValue());
/* 572 */         this.cp_Long.add(constant);
/* 573 */       } else if (value instanceof Float) {
/* 574 */         constant = new CPFloat(((Float)value).floatValue());
/* 575 */         this.cp_Float.add(constant);
/* 576 */       } else if (value instanceof Double) {
/* 577 */         constant = new CPDouble(((Double)value).doubleValue());
/* 578 */         this.cp_Double.add(constant);
/* 579 */       } else if (value instanceof String) {
/* 580 */         constant = new CPString(getCPUtf8((String)value));
/* 581 */         this.cp_String.add(constant);
/* 582 */       } else if (value instanceof Type) {
/* 583 */         String className = ((Type)value).getClassName();
/* 584 */         if (className.endsWith("[]")) {
/* 585 */           className = "[L" + className.substring(0, className.length() - 2);
/* 586 */           while (className.endsWith("[]")) {
/* 587 */             className = "[" + className.substring(0, className.length() - 2);
/*     */           }
/* 589 */           className = className + ";";
/*     */         } 
/* 591 */         constant = getCPClass(className);
/*     */       } 
/* 593 */       this.objectsToCPConstant.put(value, constant);
/*     */     } 
/* 595 */     return constant;
/*     */   }
/*     */   
/*     */   public CPMethodOrField getCPMethod(CPClass cpClass, String name, String desc) {
/* 599 */     String key = cpClass.toString() + ":" + name + ":" + desc;
/* 600 */     CPMethodOrField cpM = (CPMethodOrField)this.stringsToCpMethod.get(key);
/* 601 */     if (cpM == null) {
/* 602 */       CPNameAndType nAndT = getCPNameAndType(name, desc);
/* 603 */       cpM = new CPMethodOrField(cpClass, nAndT);
/* 604 */       this.cp_Method.add(cpM);
/* 605 */       this.stringsToCpMethod.put(key, cpM);
/*     */     } 
/* 607 */     return cpM;
/*     */   }
/*     */   
/*     */   public CPMethodOrField getCPIMethod(CPClass cpClass, String name, String desc) {
/* 611 */     String key = cpClass.toString() + ":" + name + ":" + desc;
/* 612 */     CPMethodOrField cpIM = (CPMethodOrField)this.stringsToCpIMethod.get(key);
/* 613 */     if (cpIM == null) {
/* 614 */       CPNameAndType nAndT = getCPNameAndType(name, desc);
/* 615 */       cpIM = new CPMethodOrField(cpClass, nAndT);
/* 616 */       this.cp_Imethod.add(cpIM);
/* 617 */       this.stringsToCpIMethod.put(key, cpIM);
/*     */     } 
/* 619 */     return cpIM;
/*     */   }
/*     */   
/*     */   public CPMethodOrField getCPField(String owner, String name, String desc) {
/* 623 */     return getCPField(getCPClass(owner), name, desc);
/*     */   }
/*     */   
/*     */   public CPMethodOrField getCPMethod(String owner, String name, String desc) {
/* 627 */     return getCPMethod(getCPClass(owner), name, desc);
/*     */   }
/*     */   
/*     */   public CPMethodOrField getCPIMethod(String owner, String name, String desc) {
/* 631 */     return getCPIMethod(getCPClass(owner), name, desc);
/*     */   }
/*     */   
/*     */   public boolean existsCpClass(String className) {
/* 635 */     CPClass cpClass = (CPClass)this.stringsToCpClass.get(className);
/* 636 */     return (cpClass != null);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\harmony\pack200\CpBands.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */