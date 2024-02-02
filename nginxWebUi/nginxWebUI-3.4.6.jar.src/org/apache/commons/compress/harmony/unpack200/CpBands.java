/*     */ package org.apache.commons.compress.harmony.unpack200;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import org.apache.commons.compress.harmony.pack200.Codec;
/*     */ import org.apache.commons.compress.harmony.pack200.Pack200Exception;
/*     */ import org.apache.commons.compress.harmony.unpack200.bytecode.CPClass;
/*     */ import org.apache.commons.compress.harmony.unpack200.bytecode.CPDouble;
/*     */ import org.apache.commons.compress.harmony.unpack200.bytecode.CPFieldRef;
/*     */ import org.apache.commons.compress.harmony.unpack200.bytecode.CPFloat;
/*     */ import org.apache.commons.compress.harmony.unpack200.bytecode.CPInteger;
/*     */ import org.apache.commons.compress.harmony.unpack200.bytecode.CPInterfaceMethodRef;
/*     */ import org.apache.commons.compress.harmony.unpack200.bytecode.CPLong;
/*     */ import org.apache.commons.compress.harmony.unpack200.bytecode.CPMethodRef;
/*     */ import org.apache.commons.compress.harmony.unpack200.bytecode.CPNameAndType;
/*     */ import org.apache.commons.compress.harmony.unpack200.bytecode.CPString;
/*     */ import org.apache.commons.compress.harmony.unpack200.bytecode.CPUTF8;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
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
/*     */   public SegmentConstantPool getConstantPool() {
/*  45 */     return this.pool;
/*     */   }
/*     */   
/*  48 */   private final SegmentConstantPool pool = new SegmentConstantPool(this);
/*     */   
/*     */   private String[] cpClass;
/*     */   
/*     */   private int[] cpClassInts;
/*     */   private int[] cpDescriptorNameInts;
/*     */   private int[] cpDescriptorTypeInts;
/*     */   private String[] cpDescriptor;
/*     */   private double[] cpDouble;
/*     */   private String[] cpFieldClass;
/*     */   private String[] cpFieldDescriptor;
/*     */   private int[] cpFieldClassInts;
/*     */   private int[] cpFieldDescriptorInts;
/*     */   private float[] cpFloat;
/*     */   private String[] cpIMethodClass;
/*     */   private String[] cpIMethodDescriptor;
/*     */   private int[] cpIMethodClassInts;
/*     */   private int[] cpIMethodDescriptorInts;
/*     */   private int[] cpInt;
/*     */   private long[] cpLong;
/*     */   private String[] cpMethodClass;
/*     */   private String[] cpMethodDescriptor;
/*     */   private int[] cpMethodClassInts;
/*     */   private int[] cpMethodDescriptorInts;
/*     */   private String[] cpSignature;
/*     */   private int[] cpSignatureInts;
/*     */   private String[] cpString;
/*     */   private int[] cpStringInts;
/*     */   private String[] cpUTF8;
/*  77 */   private final Map stringsToCPUTF8 = new HashMap<>();
/*  78 */   private final Map stringsToCPStrings = new HashMap<>();
/*  79 */   private final Map longsToCPLongs = new HashMap<>();
/*  80 */   private final Map integersToCPIntegers = new HashMap<>();
/*  81 */   private final Map floatsToCPFloats = new HashMap<>();
/*  82 */   private final Map stringsToCPClass = new HashMap<>();
/*  83 */   private final Map doublesToCPDoubles = new HashMap<>();
/*  84 */   private final Map descriptorsToCPNameAndTypes = new HashMap<>();
/*     */   
/*     */   private Map mapClass;
/*     */   
/*     */   private Map mapDescriptor;
/*     */   
/*     */   private Map mapUTF8;
/*     */   
/*     */   private Map mapSignature;
/*     */   private int intOffset;
/*     */   private int floatOffset;
/*     */   private int longOffset;
/*     */   private int doubleOffset;
/*     */   private int stringOffset;
/*     */   private int classOffset;
/*     */   private int signatureOffset;
/*     */   private int descrOffset;
/*     */   private int fieldOffset;
/*     */   private int methodOffset;
/*     */   private int imethodOffset;
/*     */   
/*     */   public CpBands(Segment segment) {
/* 106 */     super(segment);
/*     */   }
/*     */ 
/*     */   
/*     */   public void read(InputStream in) throws IOException, Pack200Exception {
/* 111 */     parseCpUtf8(in);
/* 112 */     parseCpInt(in);
/* 113 */     parseCpFloat(in);
/* 114 */     parseCpLong(in);
/* 115 */     parseCpDouble(in);
/* 116 */     parseCpString(in);
/* 117 */     parseCpClass(in);
/* 118 */     parseCpSignature(in);
/* 119 */     parseCpDescriptor(in);
/* 120 */     parseCpField(in);
/* 121 */     parseCpMethod(in);
/* 122 */     parseCpIMethod(in);
/*     */     
/* 124 */     this.intOffset = this.cpUTF8.length;
/* 125 */     this.floatOffset = this.intOffset + this.cpInt.length;
/* 126 */     this.longOffset = this.floatOffset + this.cpFloat.length;
/* 127 */     this.doubleOffset = this.longOffset + this.cpLong.length;
/* 128 */     this.stringOffset = this.doubleOffset + this.cpDouble.length;
/* 129 */     this.classOffset = this.stringOffset + this.cpString.length;
/* 130 */     this.signatureOffset = this.classOffset + this.cpClass.length;
/* 131 */     this.descrOffset = this.signatureOffset + this.cpSignature.length;
/* 132 */     this.fieldOffset = this.descrOffset + this.cpDescriptor.length;
/* 133 */     this.methodOffset = this.fieldOffset + this.cpFieldClass.length;
/* 134 */     this.imethodOffset = this.methodOffset + this.cpMethodClass.length;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void unpack() {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void parseCpClass(InputStream in) throws IOException, Pack200Exception {
/* 151 */     int cpClassCount = this.header.getCpClassCount();
/* 152 */     this.cpClassInts = decodeBandInt("cp_Class", in, Codec.UDELTA5, cpClassCount);
/* 153 */     this.cpClass = new String[cpClassCount];
/* 154 */     this.mapClass = new HashMap<>(cpClassCount);
/* 155 */     for (int i = 0; i < cpClassCount; i++) {
/* 156 */       this.cpClass[i] = this.cpUTF8[this.cpClassInts[i]];
/* 157 */       this.mapClass.put(this.cpClass[i], Integer.valueOf(i));
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
/*     */ 
/*     */   
/*     */   private void parseCpDescriptor(InputStream in) throws IOException, Pack200Exception {
/* 172 */     int cpDescriptorCount = this.header.getCpDescriptorCount();
/* 173 */     this.cpDescriptorNameInts = decodeBandInt("cp_Descr_name", in, Codec.DELTA5, cpDescriptorCount);
/* 174 */     this.cpDescriptorTypeInts = decodeBandInt("cp_Descr_type", in, Codec.UDELTA5, cpDescriptorCount);
/* 175 */     String[] cpDescriptorNames = getReferences(this.cpDescriptorNameInts, this.cpUTF8);
/* 176 */     String[] cpDescriptorTypes = getReferences(this.cpDescriptorTypeInts, this.cpSignature);
/* 177 */     this.cpDescriptor = new String[cpDescriptorCount];
/* 178 */     this.mapDescriptor = new HashMap<>(cpDescriptorCount);
/* 179 */     for (int i = 0; i < cpDescriptorCount; i++) {
/* 180 */       this.cpDescriptor[i] = cpDescriptorNames[i] + ":" + cpDescriptorTypes[i];
/* 181 */       this.mapDescriptor.put(this.cpDescriptor[i], Integer.valueOf(i));
/*     */     } 
/*     */   }
/*     */   
/*     */   private void parseCpDouble(InputStream in) throws IOException, Pack200Exception {
/* 186 */     int cpDoubleCount = this.header.getCpDoubleCount();
/* 187 */     long[] band = parseFlags("cp_Double", in, cpDoubleCount, Codec.UDELTA5, Codec.DELTA5);
/* 188 */     this.cpDouble = new double[band.length];
/* 189 */     for (int i = 0; i < band.length; i++) {
/* 190 */       this.cpDouble[i] = Double.longBitsToDouble(band[i]);
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
/*     */   private void parseCpField(InputStream in) throws IOException, Pack200Exception {
/* 203 */     int cpFieldCount = this.header.getCpFieldCount();
/* 204 */     this.cpFieldClassInts = decodeBandInt("cp_Field_class", in, Codec.DELTA5, cpFieldCount);
/* 205 */     this.cpFieldDescriptorInts = decodeBandInt("cp_Field_desc", in, Codec.UDELTA5, cpFieldCount);
/* 206 */     this.cpFieldClass = new String[cpFieldCount];
/* 207 */     this.cpFieldDescriptor = new String[cpFieldCount];
/* 208 */     for (int i = 0; i < cpFieldCount; i++) {
/* 209 */       this.cpFieldClass[i] = this.cpClass[this.cpFieldClassInts[i]];
/* 210 */       this.cpFieldDescriptor[i] = this.cpDescriptor[this.cpFieldDescriptorInts[i]];
/*     */     } 
/*     */   }
/*     */   
/*     */   private void parseCpFloat(InputStream in) throws IOException, Pack200Exception {
/* 215 */     int cpFloatCount = this.header.getCpFloatCount();
/* 216 */     this.cpFloat = new float[cpFloatCount];
/* 217 */     int[] floatBits = decodeBandInt("cp_Float", in, Codec.UDELTA5, cpFloatCount);
/* 218 */     for (int i = 0; i < cpFloatCount; i++) {
/* 219 */       this.cpFloat[i] = Float.intBitsToFloat(floatBits[i]);
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
/*     */   private void parseCpIMethod(InputStream in) throws IOException, Pack200Exception {
/* 232 */     int cpIMethodCount = this.header.getCpIMethodCount();
/* 233 */     this.cpIMethodClassInts = decodeBandInt("cp_Imethod_class", in, Codec.DELTA5, cpIMethodCount);
/* 234 */     this.cpIMethodDescriptorInts = decodeBandInt("cp_Imethod_desc", in, Codec.UDELTA5, cpIMethodCount);
/* 235 */     this.cpIMethodClass = new String[cpIMethodCount];
/* 236 */     this.cpIMethodDescriptor = new String[cpIMethodCount];
/* 237 */     for (int i = 0; i < cpIMethodCount; i++) {
/* 238 */       this.cpIMethodClass[i] = this.cpClass[this.cpIMethodClassInts[i]];
/* 239 */       this.cpIMethodDescriptor[i] = this.cpDescriptor[this.cpIMethodDescriptorInts[i]];
/*     */     } 
/*     */   }
/*     */   
/*     */   private void parseCpInt(InputStream in) throws IOException, Pack200Exception {
/* 244 */     int cpIntCount = this.header.getCpIntCount();
/* 245 */     this.cpInt = decodeBandInt("cpInt", in, Codec.UDELTA5, cpIntCount);
/*     */   }
/*     */   
/*     */   private void parseCpLong(InputStream in) throws IOException, Pack200Exception {
/* 249 */     int cpLongCount = this.header.getCpLongCount();
/* 250 */     this.cpLong = parseFlags("cp_Long", in, cpLongCount, Codec.UDELTA5, Codec.DELTA5);
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
/*     */   private void parseCpMethod(InputStream in) throws IOException, Pack200Exception {
/* 262 */     int cpMethodCount = this.header.getCpMethodCount();
/* 263 */     this.cpMethodClassInts = decodeBandInt("cp_Method_class", in, Codec.DELTA5, cpMethodCount);
/* 264 */     this.cpMethodDescriptorInts = decodeBandInt("cp_Method_desc", in, Codec.UDELTA5, cpMethodCount);
/* 265 */     this.cpMethodClass = new String[cpMethodCount];
/* 266 */     this.cpMethodDescriptor = new String[cpMethodCount];
/* 267 */     for (int i = 0; i < cpMethodCount; i++) {
/* 268 */       this.cpMethodClass[i] = this.cpClass[this.cpMethodClassInts[i]];
/* 269 */       this.cpMethodDescriptor[i] = this.cpDescriptor[this.cpMethodDescriptorInts[i]];
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void parseCpSignature(InputStream in) throws IOException, Pack200Exception {
/* 287 */     int cpSignatureCount = this.header.getCpSignatureCount();
/* 288 */     this.cpSignatureInts = decodeBandInt("cp_Signature_form", in, Codec.DELTA5, cpSignatureCount);
/* 289 */     String[] cpSignatureForm = getReferences(this.cpSignatureInts, this.cpUTF8);
/* 290 */     this.cpSignature = new String[cpSignatureCount];
/* 291 */     this.mapSignature = new HashMap<>();
/* 292 */     int lCount = 0;
/* 293 */     for (int i = 0; i < cpSignatureCount; i++) {
/* 294 */       String form = cpSignatureForm[i];
/* 295 */       char[] chars = form.toCharArray();
/* 296 */       for (int k = 0; k < chars.length; k++) {
/* 297 */         if (chars[k] == 'L') {
/* 298 */           this.cpSignatureInts[i] = -1;
/* 299 */           lCount++;
/*     */         } 
/*     */       } 
/*     */     } 
/* 303 */     String[] cpSignatureClasses = parseReferences("cp_Signature_classes", in, Codec.UDELTA5, lCount, this.cpClass);
/* 304 */     int index = 0;
/* 305 */     for (int j = 0; j < cpSignatureCount; j++) {
/* 306 */       String form = cpSignatureForm[j];
/* 307 */       int len = form.length();
/* 308 */       StringBuffer signature = new StringBuffer(64);
/* 309 */       ArrayList<String> list = new ArrayList();
/* 310 */       for (int k = 0; k < len; k++) {
/* 311 */         char c = form.charAt(k);
/* 312 */         signature.append(c);
/* 313 */         if (c == 'L') {
/* 314 */           String className = cpSignatureClasses[index];
/* 315 */           list.add(className);
/* 316 */           signature.append(className);
/* 317 */           index++;
/*     */         } 
/*     */       } 
/* 320 */       this.cpSignature[j] = signature.toString();
/* 321 */       this.mapSignature.put(signature.toString(), Integer.valueOf(j));
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void parseCpString(InputStream in) throws IOException, Pack200Exception {
/* 339 */     int cpStringCount = this.header.getCpStringCount();
/* 340 */     this.cpStringInts = decodeBandInt("cp_String", in, Codec.UDELTA5, cpStringCount);
/* 341 */     this.cpString = new String[cpStringCount];
/* 342 */     for (int i = 0; i < cpStringCount; i++) {
/* 343 */       this.cpString[i] = this.cpUTF8[this.cpStringInts[i]];
/*     */     }
/*     */   }
/*     */   
/*     */   private void parseCpUtf8(InputStream in) throws IOException, Pack200Exception {
/* 348 */     int cpUTF8Count = this.header.getCpUTF8Count();
/* 349 */     this.cpUTF8 = new String[cpUTF8Count];
/* 350 */     this.mapUTF8 = new HashMap<>(cpUTF8Count + 1);
/* 351 */     this.cpUTF8[0] = "";
/* 352 */     this.mapUTF8.put("", Integer.valueOf(0));
/* 353 */     int[] prefix = decodeBandInt("cpUTF8Prefix", in, Codec.DELTA5, cpUTF8Count - 2);
/* 354 */     int charCount = 0;
/* 355 */     int bigSuffixCount = 0;
/* 356 */     int[] suffix = decodeBandInt("cpUTF8Suffix", in, Codec.UNSIGNED5, cpUTF8Count - 1);
/*     */     
/* 358 */     for (int i = 0; i < suffix.length; i++) {
/* 359 */       if (suffix[i] == 0) {
/* 360 */         bigSuffixCount++;
/*     */       } else {
/* 362 */         charCount += suffix[i];
/*     */       } 
/*     */     } 
/* 365 */     char[] data = new char[charCount];
/* 366 */     int[] dataBand = decodeBandInt("cp_Utf8_chars", in, Codec.CHAR3, charCount);
/* 367 */     for (int j = 0; j < data.length; j++) {
/* 368 */       data[j] = (char)dataBand[j];
/*     */     }
/*     */ 
/*     */     
/* 372 */     int[] bigSuffixCounts = decodeBandInt("cp_Utf8_big_suffix", in, Codec.DELTA5, bigSuffixCount);
/* 373 */     int[][] bigSuffixDataBand = new int[bigSuffixCount][];
/* 374 */     for (int k = 0; k < bigSuffixDataBand.length; k++) {
/* 375 */       bigSuffixDataBand[k] = decodeBandInt("cp_Utf8_big_chars " + k, in, Codec.DELTA5, bigSuffixCounts[k]);
/*     */     }
/*     */ 
/*     */     
/* 379 */     char[][] bigSuffixData = new char[bigSuffixCount][]; int m;
/* 380 */     for (m = 0; m < bigSuffixDataBand.length; m++) {
/* 381 */       bigSuffixData[m] = new char[(bigSuffixDataBand[m]).length];
/* 382 */       for (int n = 0; n < (bigSuffixDataBand[m]).length; n++) {
/* 383 */         bigSuffixData[m][n] = (char)bigSuffixDataBand[m][n];
/*     */       }
/*     */     } 
/*     */     
/* 387 */     charCount = 0;
/* 388 */     bigSuffixCount = 0;
/* 389 */     for (m = 1; m < cpUTF8Count; m++) {
/* 390 */       String lastString = this.cpUTF8[m - 1];
/* 391 */       if (suffix[m - 1] == 0) {
/*     */ 
/*     */         
/* 394 */         this.cpUTF8[m] = lastString.substring(0, (m > 1) ? prefix[m - 2] : 0) + new String(bigSuffixData[bigSuffixCount++]);
/*     */         
/* 396 */         this.mapUTF8.put(this.cpUTF8[m], Integer.valueOf(m));
/*     */       } else {
/* 398 */         this.cpUTF8[m] = lastString.substring(0, (m > 1) ? prefix[m - 2] : 0) + new String(data, charCount, suffix[m - 1]);
/*     */         
/* 400 */         charCount += suffix[m - 1];
/* 401 */         this.mapUTF8.put(this.cpUTF8[m], Integer.valueOf(m));
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   public String[] getCpClass() {
/* 407 */     return this.cpClass;
/*     */   }
/*     */   
/*     */   public String[] getCpDescriptor() {
/* 411 */     return this.cpDescriptor;
/*     */   }
/*     */   
/*     */   public String[] getCpFieldClass() {
/* 415 */     return this.cpFieldClass;
/*     */   }
/*     */   
/*     */   public String[] getCpIMethodClass() {
/* 419 */     return this.cpIMethodClass;
/*     */   }
/*     */   
/*     */   public int[] getCpInt() {
/* 423 */     return this.cpInt;
/*     */   }
/*     */   
/*     */   public long[] getCpLong() {
/* 427 */     return this.cpLong;
/*     */   }
/*     */   
/*     */   public String[] getCpMethodClass() {
/* 431 */     return this.cpMethodClass;
/*     */   }
/*     */   
/*     */   public String[] getCpMethodDescriptor() {
/* 435 */     return this.cpMethodDescriptor;
/*     */   }
/*     */   
/*     */   public String[] getCpSignature() {
/* 439 */     return this.cpSignature;
/*     */   }
/*     */   
/*     */   public String[] getCpUTF8() {
/* 443 */     return this.cpUTF8;
/*     */   }
/*     */   
/*     */   public CPUTF8 cpUTF8Value(int index) {
/* 447 */     String string = this.cpUTF8[index];
/* 448 */     CPUTF8 cputf8 = (CPUTF8)this.stringsToCPUTF8.get(string);
/* 449 */     if (cputf8 == null) {
/* 450 */       cputf8 = new CPUTF8(string, index);
/* 451 */       this.stringsToCPUTF8.put(string, cputf8);
/* 452 */     } else if (cputf8.getGlobalIndex() > index) {
/* 453 */       cputf8.setGlobalIndex(index);
/*     */     } 
/* 455 */     return cputf8;
/*     */   }
/*     */   
/*     */   public CPUTF8 cpUTF8Value(String string) {
/* 459 */     return cpUTF8Value(string, true);
/*     */   }
/*     */   
/*     */   public CPUTF8 cpUTF8Value(String string, boolean searchForIndex) {
/* 463 */     CPUTF8 cputf8 = (CPUTF8)this.stringsToCPUTF8.get(string);
/* 464 */     if (cputf8 == null) {
/* 465 */       Integer index = null;
/* 466 */       if (searchForIndex) {
/* 467 */         index = (Integer)this.mapUTF8.get(string);
/*     */       }
/* 469 */       if (index != null) {
/* 470 */         return cpUTF8Value(index.intValue());
/*     */       }
/* 472 */       if (searchForIndex) {
/* 473 */         index = (Integer)this.mapSignature.get(string);
/*     */       }
/* 475 */       if (index != null) {
/* 476 */         return cpSignatureValue(index.intValue());
/*     */       }
/* 478 */       cputf8 = new CPUTF8(string, -1);
/* 479 */       this.stringsToCPUTF8.put(string, cputf8);
/*     */     } 
/* 481 */     return cputf8;
/*     */   }
/*     */   
/*     */   public CPString cpStringValue(int index) {
/* 485 */     String string = this.cpString[index];
/* 486 */     int utf8Index = this.cpStringInts[index];
/* 487 */     int globalIndex = this.stringOffset + index;
/* 488 */     CPString cpString = (CPString)this.stringsToCPStrings.get(string);
/* 489 */     if (cpString == null) {
/* 490 */       cpString = new CPString(cpUTF8Value(utf8Index), globalIndex);
/* 491 */       this.stringsToCPStrings.put(string, cpString);
/*     */     } 
/* 493 */     return cpString;
/*     */   }
/*     */   
/*     */   public CPLong cpLongValue(int index) {
/* 497 */     Long l = Long.valueOf(this.cpLong[index]);
/* 498 */     CPLong cpLong = (CPLong)this.longsToCPLongs.get(l);
/* 499 */     if (cpLong == null) {
/* 500 */       cpLong = new CPLong(l, index + this.longOffset);
/* 501 */       this.longsToCPLongs.put(l, cpLong);
/*     */     } 
/* 503 */     return cpLong;
/*     */   }
/*     */   
/*     */   public CPInteger cpIntegerValue(int index) {
/* 507 */     Integer i = Integer.valueOf(this.cpInt[index]);
/* 508 */     CPInteger cpInteger = (CPInteger)this.integersToCPIntegers.get(i);
/* 509 */     if (cpInteger == null) {
/* 510 */       cpInteger = new CPInteger(i, index + this.intOffset);
/* 511 */       this.integersToCPIntegers.put(i, cpInteger);
/*     */     } 
/* 513 */     return cpInteger;
/*     */   }
/*     */   
/*     */   public CPFloat cpFloatValue(int index) {
/* 517 */     Float f = Float.valueOf(this.cpFloat[index]);
/* 518 */     CPFloat cpFloat = (CPFloat)this.floatsToCPFloats.get(f);
/* 519 */     if (cpFloat == null) {
/* 520 */       cpFloat = new CPFloat(f, index + this.floatOffset);
/* 521 */       this.floatsToCPFloats.put(f, cpFloat);
/*     */     } 
/* 523 */     return cpFloat;
/*     */   }
/*     */   
/*     */   public CPClass cpClassValue(int index) {
/* 527 */     String string = this.cpClass[index];
/* 528 */     int utf8Index = this.cpClassInts[index];
/* 529 */     int globalIndex = this.classOffset + index;
/* 530 */     CPClass cpString = (CPClass)this.stringsToCPClass.get(string);
/* 531 */     if (cpString == null) {
/* 532 */       cpString = new CPClass(cpUTF8Value(utf8Index), globalIndex);
/* 533 */       this.stringsToCPClass.put(string, cpString);
/*     */     } 
/* 535 */     return cpString;
/*     */   }
/*     */   
/*     */   public CPClass cpClassValue(String string) {
/* 539 */     CPClass cpString = (CPClass)this.stringsToCPClass.get(string);
/* 540 */     if (cpString == null) {
/* 541 */       Integer index = (Integer)this.mapClass.get(string);
/* 542 */       if (index != null) {
/* 543 */         return cpClassValue(index.intValue());
/*     */       }
/* 545 */       cpString = new CPClass(cpUTF8Value(string, false), -1);
/* 546 */       this.stringsToCPClass.put(string, cpString);
/*     */     } 
/* 548 */     return cpString;
/*     */   }
/*     */   
/*     */   public CPDouble cpDoubleValue(int index) {
/* 552 */     Double dbl = Double.valueOf(this.cpDouble[index]);
/* 553 */     CPDouble cpDouble = (CPDouble)this.doublesToCPDoubles.get(dbl);
/* 554 */     if (cpDouble == null) {
/* 555 */       cpDouble = new CPDouble(dbl, index + this.doubleOffset);
/* 556 */       this.doublesToCPDoubles.put(dbl, cpDouble);
/*     */     } 
/* 558 */     return cpDouble;
/*     */   }
/*     */   
/*     */   public CPNameAndType cpNameAndTypeValue(int index) {
/* 562 */     String descriptor = this.cpDescriptor[index];
/* 563 */     CPNameAndType cpNameAndType = (CPNameAndType)this.descriptorsToCPNameAndTypes.get(descriptor);
/* 564 */     if (cpNameAndType == null) {
/* 565 */       int nameIndex = this.cpDescriptorNameInts[index];
/* 566 */       int descriptorIndex = this.cpDescriptorTypeInts[index];
/*     */       
/* 568 */       CPUTF8 name = cpUTF8Value(nameIndex);
/* 569 */       CPUTF8 descriptorU = cpSignatureValue(descriptorIndex);
/* 570 */       cpNameAndType = new CPNameAndType(name, descriptorU, index + this.descrOffset);
/* 571 */       this.descriptorsToCPNameAndTypes.put(descriptor, cpNameAndType);
/*     */     } 
/* 573 */     return cpNameAndType;
/*     */   }
/*     */   
/*     */   public CPInterfaceMethodRef cpIMethodValue(int index) {
/* 577 */     return new CPInterfaceMethodRef(cpClassValue(this.cpIMethodClassInts[index]), 
/* 578 */         cpNameAndTypeValue(this.cpIMethodDescriptorInts[index]), index + this.imethodOffset);
/*     */   }
/*     */   
/*     */   public CPMethodRef cpMethodValue(int index) {
/* 582 */     return new CPMethodRef(cpClassValue(this.cpMethodClassInts[index]), 
/* 583 */         cpNameAndTypeValue(this.cpMethodDescriptorInts[index]), index + this.methodOffset);
/*     */   }
/*     */   
/*     */   public CPFieldRef cpFieldValue(int index) {
/* 587 */     return new CPFieldRef(cpClassValue(this.cpFieldClassInts[index]), cpNameAndTypeValue(this.cpFieldDescriptorInts[index]), index + this.fieldOffset);
/*     */   }
/*     */ 
/*     */   
/*     */   public CPUTF8 cpSignatureValue(int index) {
/*     */     int globalIndex;
/* 593 */     if (this.cpSignatureInts[index] != -1) {
/* 594 */       globalIndex = this.cpSignatureInts[index];
/*     */     } else {
/* 596 */       globalIndex = index + this.signatureOffset;
/*     */     } 
/* 598 */     String string = this.cpSignature[index];
/* 599 */     CPUTF8 cpUTF8 = (CPUTF8)this.stringsToCPUTF8.get(string);
/* 600 */     if (cpUTF8 == null) {
/* 601 */       cpUTF8 = new CPUTF8(string, globalIndex);
/* 602 */       this.stringsToCPUTF8.put(string, cpUTF8);
/*     */     } 
/* 604 */     return cpUTF8;
/*     */   }
/*     */   
/*     */   public CPNameAndType cpNameAndTypeValue(String descriptor) {
/* 608 */     CPNameAndType cpNameAndType = (CPNameAndType)this.descriptorsToCPNameAndTypes.get(descriptor);
/* 609 */     if (cpNameAndType == null) {
/* 610 */       Integer index = (Integer)this.mapDescriptor.get(descriptor);
/* 611 */       if (index != null) {
/* 612 */         return cpNameAndTypeValue(index.intValue());
/*     */       }
/* 614 */       int colon = descriptor.indexOf(':');
/* 615 */       String nameString = descriptor.substring(0, colon);
/* 616 */       String descriptorString = descriptor.substring(colon + 1);
/*     */       
/* 618 */       CPUTF8 name = cpUTF8Value(nameString, true);
/* 619 */       CPUTF8 descriptorU = cpUTF8Value(descriptorString, true);
/* 620 */       cpNameAndType = new CPNameAndType(name, descriptorU, -1 + this.descrOffset);
/* 621 */       this.descriptorsToCPNameAndTypes.put(descriptor, cpNameAndType);
/*     */     } 
/* 623 */     return cpNameAndType;
/*     */   }
/*     */   
/*     */   public int[] getCpDescriptorNameInts() {
/* 627 */     return this.cpDescriptorNameInts;
/*     */   }
/*     */   
/*     */   public int[] getCpDescriptorTypeInts() {
/* 631 */     return this.cpDescriptorTypeInts;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\harmon\\unpack200\CpBands.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */